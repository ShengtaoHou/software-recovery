// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.apache.activemq.transport.reliable.ReplayBuffer;
import java.net.SocketAddress;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.Service;
import org.apache.activemq.transport.reliable.Replayer;

public interface CommandChannel extends Replayer, Service
{
    Command read() throws IOException;
    
    void write(final Command p0, final SocketAddress p1) throws IOException;
    
    int getDatagramSize();
    
    void setDatagramSize(final int p0);
    
    DatagramHeaderMarshaller getHeaderMarshaller();
    
    void setHeaderMarshaller(final DatagramHeaderMarshaller p0);
    
    void setTargetAddress(final SocketAddress p0);
    
    void setReplayAddress(final SocketAddress p0);
    
    void setReplayBuffer(final ReplayBuffer p0);
    
    int getReceiveCounter();
}
