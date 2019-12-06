// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.apache.activemq.command.Endpoint;
import org.apache.activemq.command.Command;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportFilter;

public class ResponseRedirectInterceptor extends TransportFilter
{
    private final UdpTransport transport;
    
    public ResponseRedirectInterceptor(final Transport next, final UdpTransport transport) {
        super(next);
        this.transport = transport;
    }
    
    @Override
    public void onCommand(final Object o) {
        final Command command = (Command)o;
        final Endpoint from = command.getFrom();
        this.transport.setTargetEndpoint(from);
        super.onCommand(command);
    }
}
