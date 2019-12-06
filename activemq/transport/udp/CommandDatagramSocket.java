// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.reliable.ReplayBuffer;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import org.apache.activemq.command.Endpoint;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.command.Command;
import java.net.SocketAddress;
import org.apache.activemq.openwire.OpenWireFormat;
import java.net.DatagramSocket;
import org.slf4j.Logger;

public class CommandDatagramSocket extends CommandChannelSupport
{
    private static final Logger LOG;
    private DatagramSocket channel;
    private Object readLock;
    private Object writeLock;
    private volatile int receiveCounter;
    
    public CommandDatagramSocket(final UdpTransport transport, final OpenWireFormat wireFormat, final int datagramSize, final SocketAddress targetAddress, final DatagramHeaderMarshaller headerMarshaller, final DatagramSocket channel) {
        super(transport, wireFormat, datagramSize, targetAddress, headerMarshaller);
        this.readLock = new Object();
        this.writeLock = new Object();
        this.channel = channel;
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public Command read() throws IOException {
        Command answer = null;
        Endpoint from = null;
        synchronized (this.readLock) {
            final DatagramPacket datagram = this.createDatagramPacket();
            this.channel.receive(datagram);
            ++this.receiveCounter;
            final DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(datagram.getData(), 0, datagram.getLength()));
            from = this.headerMarshaller.createEndpoint(datagram, dataIn);
            answer = (Command)this.wireFormat.unmarshal(dataIn);
        }
        if (answer != null) {
            answer.setFrom(from);
            if (CommandDatagramSocket.LOG.isDebugEnabled()) {
                CommandDatagramSocket.LOG.debug("Channel: " + this.name + " about to process: " + answer);
            }
        }
        return answer;
    }
    
    @Override
    public void write(final Command command, final SocketAddress address) throws IOException {
        synchronized (this.writeLock) {
            ByteArrayOutputStream writeBuffer = this.createByteArrayOutputStream();
            final DataOutputStream dataOut = new DataOutputStream(writeBuffer);
            this.headerMarshaller.writeHeader(command, dataOut);
            int offset = writeBuffer.size();
            this.wireFormat.marshal(command, dataOut);
            if (this.remaining(writeBuffer) >= 0) {
                this.sendWriteBuffer(address, writeBuffer, command.getCommandId());
            }
            else {
                final byte[] data = writeBuffer.toByteArray();
                boolean lastFragment = false;
                final int length = data.length;
                int fragment = 0;
                while (!lastFragment) {
                    writeBuffer = this.createByteArrayOutputStream();
                    this.headerMarshaller.writeHeader(command, dataOut);
                    int chunkSize = this.remaining(writeBuffer);
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
                        dataOut.writeInt(chunkSize);
                        chunkSize -= 4;
                    }
                    lastFragment = (offset + chunkSize >= length);
                    if (chunkSize + offset > length) {
                        chunkSize = length - offset;
                    }
                    if (lastFragment) {
                        dataOut.write(61);
                    }
                    else {
                        dataOut.write(60);
                    }
                    if (bs != null) {
                        bs.marshal(dataOut);
                    }
                    int commandId = command.getCommandId();
                    if (fragment > 0) {
                        commandId = this.sequenceGenerator.getNextSequenceId();
                    }
                    dataOut.writeInt(commandId);
                    if (bs == null) {
                        dataOut.write(1);
                    }
                    dataOut.writeInt(chunkSize);
                    dataOut.write(data, offset, chunkSize);
                    offset += chunkSize;
                    this.sendWriteBuffer(address, writeBuffer, commandId);
                    ++fragment;
                }
            }
        }
    }
    
    @Override
    public int getDatagramSize() {
        return this.datagramSize;
    }
    
    @Override
    public void setDatagramSize(final int datagramSize) {
        this.datagramSize = datagramSize;
    }
    
    protected void sendWriteBuffer(final SocketAddress address, final ByteArrayOutputStream writeBuffer, final int commandId) throws IOException {
        final byte[] data = writeBuffer.toByteArray();
        this.sendWriteBuffer(commandId, address, data, false);
    }
    
    protected void sendWriteBuffer(final int commandId, final SocketAddress address, final byte[] data, final boolean redelivery) throws IOException {
        final ReplayBuffer bufferCache = this.getReplayBuffer();
        if (bufferCache != null && !redelivery) {
            bufferCache.addBuffer(commandId, data);
        }
        if (CommandDatagramSocket.LOG.isDebugEnabled()) {
            final String text = redelivery ? "REDELIVERING" : "sending";
            CommandDatagramSocket.LOG.debug("Channel: " + this.name + " " + text + " datagram: " + commandId + " to: " + address);
        }
        final DatagramPacket packet = new DatagramPacket(data, 0, data.length, address);
        this.channel.send(packet);
    }
    
    @Override
    public void sendBuffer(final int commandId, final Object buffer) throws IOException {
        if (buffer != null) {
            final byte[] data = (byte[])buffer;
            this.sendWriteBuffer(commandId, this.replayAddress, data, true);
        }
        else if (CommandDatagramSocket.LOG.isWarnEnabled()) {
            CommandDatagramSocket.LOG.warn("Request for buffer: " + commandId + " is no longer present");
        }
    }
    
    protected DatagramPacket createDatagramPacket() {
        return new DatagramPacket(new byte[this.datagramSize], this.datagramSize);
    }
    
    protected int remaining(final ByteArrayOutputStream buffer) {
        return this.datagramSize - buffer.size();
    }
    
    protected ByteArrayOutputStream createByteArrayOutputStream() {
        return new ByteArrayOutputStream(this.datagramSize);
    }
    
    @Override
    public int getReceiveCounter() {
        return this.receiveCounter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(CommandDatagramSocket.class);
    }
}
