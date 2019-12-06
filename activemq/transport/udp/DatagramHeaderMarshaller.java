// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import java.io.DataOutputStream;
import org.apache.activemq.command.Command;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.apache.activemq.command.Endpoint;
import java.net.SocketAddress;
import java.util.Map;

public class DatagramHeaderMarshaller
{
    private Map<SocketAddress, Endpoint> endpoints;
    
    public DatagramHeaderMarshaller() {
        this.endpoints = new HashMap<SocketAddress, Endpoint>();
    }
    
    public Endpoint createEndpoint(final ByteBuffer readBuffer, final SocketAddress address) {
        return this.getEndpoint(address);
    }
    
    public Endpoint createEndpoint(final DatagramPacket datagram, final DataInputStream dataIn) {
        return this.getEndpoint(datagram.getSocketAddress());
    }
    
    public void writeHeader(final Command command, final ByteBuffer writeBuffer) {
    }
    
    public void writeHeader(final Command command, final DataOutputStream dataOut) {
    }
    
    protected Endpoint getEndpoint(final SocketAddress address) {
        Endpoint endpoint = this.endpoints.get(address);
        if (endpoint == null) {
            endpoint = this.createEndpoint(address);
            this.endpoints.put(address, endpoint);
        }
        return endpoint;
    }
    
    protected Endpoint createEndpoint(final SocketAddress address) {
        return new DatagramEndpoint(address.toString(), address);
    }
}
