// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.vm;

import java.net.InetSocketAddress;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.transport.ResponseCorrelator;
import org.apache.activemq.transport.MutexTransport;
import org.apache.activemq.transport.Transport;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URI;
import org.apache.activemq.transport.TransportAcceptListener;
import org.apache.activemq.transport.TransportServer;

public class VMTransportServer implements TransportServer
{
    private TransportAcceptListener acceptListener;
    private final URI location;
    private boolean disposed;
    private final AtomicInteger connectionCount;
    private final boolean disposeOnDisconnect;
    private boolean allowLinkStealing;
    
    public VMTransportServer(final URI location, final boolean disposeOnDisconnect) {
        this.connectionCount = new AtomicInteger(0);
        this.location = location;
        this.disposeOnDisconnect = disposeOnDisconnect;
    }
    
    @Override
    public String toString() {
        return "VMTransportServer(" + this.location + ")";
    }
    
    public VMTransport connect() throws IOException {
        final TransportAcceptListener al;
        synchronized (this) {
            if (this.disposed) {
                throw new IOException("Server has been disposed.");
            }
            al = this.acceptListener;
        }
        if (al == null) {
            throw new IOException("Server TransportAcceptListener is null.");
        }
        this.connectionCount.incrementAndGet();
        final VMTransport client = new VMTransport(this.location) {
            @Override
            public void stop() throws Exception {
                if (!this.disposed.get()) {
                    super.stop();
                    if (VMTransportServer.this.connectionCount.decrementAndGet() == 0 && VMTransportServer.this.disposeOnDisconnect) {
                        VMTransportServer.this.stop();
                    }
                }
            }
        };
        final VMTransport server = new VMTransport(this.location);
        client.setPeer(server);
        server.setPeer(client);
        al.onAccept(configure(server));
        return client;
    }
    
    public static Transport configure(Transport transport) {
        transport = new MutexTransport(transport);
        transport = new ResponseCorrelator(transport);
        return transport;
    }
    
    @Override
    public synchronized void setAcceptListener(final TransportAcceptListener acceptListener) {
        this.acceptListener = acceptListener;
    }
    
    @Override
    public void start() throws IOException {
    }
    
    @Override
    public void stop() throws IOException {
        VMTransportFactory.stopped(this);
    }
    
    @Override
    public URI getConnectURI() {
        return this.location;
    }
    
    public URI getBindURI() {
        return this.location;
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
    }
    
    @Override
    public InetSocketAddress getSocketAddress() {
        return null;
    }
    
    public int getConnectionCount() {
        return this.connectionCount.intValue();
    }
    
    @Override
    public boolean isSslServer() {
        return false;
    }
    
    @Override
    public boolean isAllowLinkStealing() {
        return this.allowLinkStealing;
    }
    
    public void setAllowLinkStealing(final boolean allowLinkStealing) {
        this.allowLinkStealing = allowLinkStealing;
    }
}
