// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.proxy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.TransportFilter;
import org.apache.activemq.transport.CompositeTransport;
import org.apache.activemq.transport.TransportFactory;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportAcceptListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.URI;
import org.apache.activemq.transport.TransportServer;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public class ProxyConnector implements Service
{
    private static final Logger LOG;
    private TransportServer server;
    private URI bind;
    private URI remote;
    private URI localUri;
    private String name;
    private boolean proxyToLocalBroker;
    private final CopyOnWriteArrayList<ProxyConnection> connections;
    
    public ProxyConnector() {
        this.proxyToLocalBroker = true;
        this.connections = new CopyOnWriteArrayList<ProxyConnection>();
    }
    
    @Override
    public void start() throws Exception {
        this.getServer().setAcceptListener(new TransportAcceptListener() {
            @Override
            public void onAccept(final Transport localTransport) {
                ProxyConnection connection = null;
                try {
                    final Transport remoteTransport = ProxyConnector.this.createRemoteTransport(localTransport);
                    connection = new ProxyConnection(localTransport, remoteTransport);
                    connection.start();
                    ProxyConnector.this.connections.add(connection);
                }
                catch (Exception e) {
                    this.onAcceptError(e);
                    try {
                        if (connection != null) {
                            connection.stop();
                        }
                    }
                    catch (Exception eoc) {
                        ProxyConnector.LOG.error("Could not close broken connection: ", eoc);
                    }
                }
            }
            
            @Override
            public void onAcceptError(final Exception error) {
                ProxyConnector.LOG.error("Could not accept connection: ", error);
            }
        });
        this.getServer().start();
        ProxyConnector.LOG.info("Proxy Connector {} started", this.getName());
    }
    
    @Override
    public void stop() throws Exception {
        final ServiceStopper ss = new ServiceStopper();
        if (this.server != null) {
            ss.stop(this.server);
        }
        final Iterator<ProxyConnection> iter = this.connections.iterator();
        while (iter.hasNext()) {
            ProxyConnector.LOG.info("Connector stopped: Stopping proxy.");
            ss.stop(iter.next());
        }
        this.connections.clear();
        ss.throwFirstException();
        ProxyConnector.LOG.info("Proxy Connector {} stopped", this.getName());
    }
    
    public URI getLocalUri() {
        return this.localUri;
    }
    
    public void setLocalUri(final URI localURI) {
        this.localUri = localURI;
    }
    
    public URI getBind() {
        return this.bind;
    }
    
    public void setBind(final URI bind) {
        this.bind = bind;
    }
    
    public URI getRemote() {
        return this.remote;
    }
    
    public void setRemote(final URI remote) {
        this.remote = remote;
    }
    
    public TransportServer getServer() throws IOException, URISyntaxException {
        if (this.server == null) {
            this.server = this.createServer();
        }
        return this.server;
    }
    
    public void setServer(final TransportServer server) {
        this.server = server;
    }
    
    protected TransportServer createServer() throws IOException, URISyntaxException {
        if (this.bind == null) {
            throw new IllegalArgumentException("You must specify either a server or the bind property");
        }
        return TransportFactory.bind(this.bind);
    }
    
    private Transport createRemoteTransport(final Transport local) throws Exception {
        Transport transport = TransportFactory.compositeConnect(this.remote);
        final CompositeTransport ct = transport.narrow(CompositeTransport.class);
        if (ct != null && this.localUri != null && this.proxyToLocalBroker) {
            ct.add(false, new URI[] { this.localUri });
        }
        transport = new TransportFilter(transport) {
            @Override
            public void stop() throws Exception {
                ProxyConnector.LOG.info("Stopping proxy.");
                super.stop();
                final ProxyConnection dummy = new ProxyConnection(local, this);
                ProxyConnector.LOG.debug("Removing proxyConnection {}", dummy.toString());
                ProxyConnector.this.connections.remove(dummy);
            }
        };
        return transport;
    }
    
    public String getName() {
        if (this.name == null) {
            if (this.server != null) {
                this.name = this.server.getConnectURI().toString();
            }
            else {
                this.name = "proxy";
            }
        }
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isProxyToLocalBroker() {
        return this.proxyToLocalBroker;
    }
    
    public void setProxyToLocalBroker(final boolean proxyToLocalBroker) {
        this.proxyToLocalBroker = proxyToLocalBroker;
    }
    
    protected Integer getConnectionCount() {
        return this.connections.size();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProxyConnector.class);
    }
}
