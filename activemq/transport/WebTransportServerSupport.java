// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.apache.activemq.util.InetAddressUtil;
import java.net.InetAddress;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import java.net.URI;

public abstract class WebTransportServerSupport extends TransportServerSupport
{
    protected URI bindAddress;
    protected Server server;
    protected Connector connector;
    protected SocketConnectorFactory socketConnectorFactory;
    protected String host;
    
    public WebTransportServerSupport(final URI location) {
        super(location);
    }
    
    public URI bind() throws Exception {
        final URI bind = this.getBindLocation();
        String bindHost = bind.getHost();
        bindHost = ((bindHost == null || bindHost.length() == 0) ? "localhost" : bindHost);
        final InetAddress addr = InetAddress.getByName(bindHost);
        this.host = addr.getCanonicalHostName();
        this.connector.setHost(this.host);
        this.connector.setPort(this.bindAddress.getPort());
        this.connector.setServer(this.server);
        this.server.addConnector(this.connector);
        if (addr.isAnyLocalAddress()) {
            this.host = InetAddressUtil.getLocalHostName();
        }
        final URI boundUri = new URI(bind.getScheme(), bind.getUserInfo(), this.host, this.bindAddress.getPort(), bind.getPath(), bind.getQuery(), bind.getFragment());
        this.setConnectURI(boundUri);
        return boundUri;
    }
}
