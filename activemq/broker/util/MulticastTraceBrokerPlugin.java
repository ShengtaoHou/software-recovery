// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.DatagramSocket;
import java.net.URISyntaxException;
import java.net.URI;

public class MulticastTraceBrokerPlugin extends UDPTraceBrokerPlugin
{
    private int timeToLive;
    
    public MulticastTraceBrokerPlugin() {
        this.timeToLive = 1;
        try {
            this.destination = new URI("multicast://224.1.2.3:61616");
        }
        catch (URISyntaxException ex) {}
    }
    
    @Override
    protected DatagramSocket createSocket() throws IOException {
        final MulticastSocket s = new MulticastSocket();
        s.setSendBufferSize(this.maxTraceDatagramSize);
        s.setBroadcast(this.broadcast);
        s.setLoopbackMode(true);
        s.setTimeToLive(this.timeToLive);
        return s;
    }
    
    public int getTimeToLive() {
        return this.timeToLive;
    }
    
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }
}
