// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import java.net.SocketAddress;
import org.apache.activemq.command.BaseEndpoint;

public class DatagramEndpoint extends BaseEndpoint
{
    private final SocketAddress address;
    
    public DatagramEndpoint(final String name, final SocketAddress address) {
        super(name);
        this.address = address;
    }
    
    public SocketAddress getAddress() {
        return this.address;
    }
}
