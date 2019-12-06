// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.reliable.ReplayBuffer;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.activemq.command.Endpoint;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.command.Command;
import java.net.SocketAddress;
import org.apache.activemq.openwire.OpenWireFormat;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import org.slf4j.Logger;

public class CommandDatagramChannel extends CommandChannelSupport
{
    private static final Logger LOG;
    private DatagramChannel channel;
    private ByteBufferPool bufferPool;
    private Object readLock;
    private ByteBuffer readBuffer;
    private Object writeLock;
    private int defaultMarshalBufferSize;
    private volatile int receiveCounter;
    
    public CommandDatagramChannel(final UdpTransport transport, final OpenWireFormat wireFormat, final int datagramSize, final SocketAddress targetAddress, final DatagramHeaderMarshaller headerMarshaller, final DatagramChannel channel, final ByteBufferPool bufferPool) {
        super(transport, wireFormat, datagramSize, targetAddress, headerMarshaller);
        this.readLock = new Object();
        this.writeLock = new Object();
        this.defaultMarshalBufferSize = 65536;
        this.channel = channel;
        this.bufferPool = bufferPool;
    }
    
    @Override
    public void start() throws Exception {
        this.bufferPool.setDefaultSize(this.datagramSize);
        this.bufferPool.start();
        this.readBuffer = this.bufferPool.borrowBuffer();
    }
    
    @Override
    public void stop() throws Exception {
        this.bufferPool.stop();
    }
    
    @Override
    public Command read() throws IOException {
        Command answer = null;
        Endpoint from = null;
        synchronized (this.readLock) {
            SocketAddress address;
            do {
                this.readBuffer.clear();
                address = this.channel.receive(this.readBuffer);
                this.readBuffer.flip();
            } while (this.readBuffer.limit() == 0);
            ++this.receiveCounter;
            from = this.headerMarshaller.createEndpoint(this.readBuffer, address);
            final int remaining = this.readBuffer.remaining();
            final byte[] data = new byte[remaining];
            this.readBuffer.get(data);
            final DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));
            answer = (Command)this.wireFormat.unmarshal(dataIn);
        }
        if (answer != null) {
            answer.setFrom(from);
            if (CommandDatagramChannel.LOG.isDebugEnabled()) {
                CommandDatagramChannel.LOG.debug("Channel: " + this.name + " received from: " + from + " about to process: " + answer);
            }
        }
        return answer;
    }
    
    @Override
    public void write(final Command command, final SocketAddress address) throws IOException {
        synchronized (this.writeLock) {
            final ByteArrayOutputStream largeBuffer = new ByteArrayOutputStream(this.defaultMarshalBufferSize);
            this.wireFormat.marshal(command, new DataOutputStream(largeBuffer));
            final byte[] data = largeBuffer.toByteArray();
            final int size = data.length;
            ByteBuffer writeBuffer = this.bufferPool.borrowBuffer();
            writeBuffer.clear();
            this.headerMarshaller.writeHeader(command, writeBuffer);
            if (size > writeBuffer.remaining()) {
                int offset = 0;
                boolean lastFragment = false;
                final int length = data.length;
                int fragment = 0;
                while (!lastFragment) {
                    if (fragment > 0) {
                        writeBuffer = this.bufferPool.borrowBuffer();
                        writeBuffer.clear();
                        this.headerMarshaller.writeHeader(command, writeBuffer);
                    }
                    int chunkSize = writeBuffer.remaining();
                    BooleanStream bs = null;
                    if (this.wireFormat.isTightEncodingEnabled()) {
                        bs = new BooleanStream();
                        bs.writeBoolean(true);
                    }
                    chunkSize -= 9;
                    if (bs != null) {
                        chunkSize -= bs.marshalledSize();
                    }
                    else {
                        --chunkSize;
                    }
                    if (!this.wireFormat.isSizePrefixDisabled()) {
                        writeBuffer.putInt(chunkSize);
                        chunkSize -= 4;
                    }
                    lastFragment = (offset + chunkSize >= length);
                    if (chunkSize + offset > length) {
                        chunkSize = length - offset;
                    }
                    if (lastFragment) {
                        writeBuffer.put((byte)61);
                    }
                    else {
                        writeBuffer.put((byte)60);
                    }
                    if (bs != null) {
                        bs.marshal(writeBuffer);
                    }
                    int commandId = command.getCommandId();
                    if (fragment > 0) {
                        commandId = this.sequenceGenerator.getNextSequenceId();
                    }
                    writeBuffer.putInt(commandId);
                    if (bs == null) {
                        writeBuffer.put((byte)1);
                    }
                    writeBuffer.putInt(chunkSize);
                    writeBuffer.put(data, offset, chunkSize);
                    offset += chunkSize;
                    this.sendWriteBuffer(commandId, address, writeBuffer, false);
                    ++fragment;
                }
            }
            else {
                writeBuffer.put(data);
                this.sendWriteBuffer(command.getCommandId(), address, writeBuffer, false);
            }
        }
    }
    
    public ByteBufferPool getBufferPool() {
        return this.bufferPool;
    }
    
    public void setBufferPool(final ByteBufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }
    
    protected void sendWriteBuffer(final int commandId, final SocketAddress address, final ByteBuffer writeBuffer, final boolean redelivery) throws IOException {
        final ReplayBuffer bufferCache = this.getReplayBuffer();
        if (bufferCache != null && !redelivery) {
            bufferCache.addBuffer(commandId, writeBuffer);
        }
        writeBuffer.flip();
        if (CommandDatagramChannel.LOG.isDebugEnabled()) {
            final String text = redelivery ? "REDELIVERING" : "sending";
            CommandDatagramChannel.LOG.debug("Channel: " + this.name + " " + text + " datagram: " + commandId + " to: " + address);
        }
        this.channel.send(writeBuffer, address);
    }
    
    @Override
    public void sendBuffer(final int commandId, final Object buffer) throws IOException {
        if (buffer != null) {
            final ByteBuffer writeBuffer = (ByteBuffer)buffer;
            this.sendWriteBuffer(commandId, this.getReplayAddress(), writeBuffer, true);
        }
        else if (CommandDatagramChannel.LOG.isWarnEnabled()) {
            CommandDatagramChannel.LOG.warn("Request for buffer: " + commandId + " is no longer present");
        }
    }
    
    @Override
    public int getReceiveCounter() {
        return this.receiveCounter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CommandDatagramChannel.class);
    }
}
