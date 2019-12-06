// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.transport.reliable.ReplayBuffer;
import org.apache.activemq.util.IntSequenceGenerator;
import java.net.SocketAddress;
import org.apache.activemq.openwire.OpenWireFormat;

public abstract class CommandChannelSupport implements CommandChannel
{
    protected OpenWireFormat wireFormat;
    protected int datagramSize;
    protected SocketAddress targetAddress;
    protected SocketAddress replayAddress;
    protected final String name;
    protected final IntSequenceGenerator sequenceGenerator;
    protected DatagramHeaderMarshaller headerMarshaller;
    private ReplayBuffer replayBuffer;
    
    public CommandChannelSupport(final UdpTransport transport, final OpenWireFormat wireFormat, final int datagramSize, final SocketAddress targetAddress, final DatagramHeaderMarshaller headerMarshaller) {
        this.datagramSize = 4096;
        this.wireFormat = wireFormat;
        this.datagramSize = datagramSize;
        this.targetAddress = targetAddress;
        this.headerMarshaller = headerMarshaller;
        this.name = transport.toString();
        this.sequenceGenerator = transport.getSequenceGenerator();
        this.replayAddress = targetAddress;
        if (this.sequenceGenerator == null) {
            throw new IllegalArgumentException("No sequenceGenerator on the given transport: " + transport);
        }
    }
    
    public void write(final Command command) throws IOException {
        this.write(command, this.targetAddress);
    }
    
    @Override
    public int getDatagramSize() {
        return this.datagramSize;
    }
    
    @Override
    public void setDatagramSize(final int datagramSize) {
        this.datagramSize = datagramSize;
    }
    
    public SocketAddress getTargetAddress() {
        return this.targetAddress;
    }
    
    @Override
    public void setTargetAddress(final SocketAddress targetAddress) {
        this.targetAddress = targetAddress;
    }
    
    public SocketAddress getReplayAddress() {
        return this.replayAddress;
    }
    
    @Override
    public void setReplayAddress(final SocketAddress replayAddress) {
        this.replayAddress = replayAddress;
    }
    
    @Override
    public String toString() {
        return "CommandChannel#" + this.name;
    }
    
    @Override
    public DatagramHeaderMarshaller getHeaderMarshaller() {
        return this.headerMarshaller;
    }
    
    @Override
    public void setHeaderMarshaller(final DatagramHeaderMarshaller headerMarshaller) {
        this.headerMarshaller = headerMarshaller;
    }
    
    public ReplayBuffer getReplayBuffer() {
        return this.replayBuffer;
    }
    
    @Override
    public void setReplayBuffer(final ReplayBuffer replayBuffer) {
        this.replayBuffer = replayBuffer;
    }
}
