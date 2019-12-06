// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.wss;

import org.eclipse.jetty.server.Connector;
import org.apache.activemq.transport.SecureSocketConnectorFactory;
import java.net.URI;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.transport.ws.WSTransportServer;

public class WSSTransportServer extends WSTransportServer
{
    private SslContext context;
    
    public WSSTransportServer(final URI location, final SslContext context) {
        super(location);
        this.context = context;
        this.socketConnectorFactory = new SecureSocketConnectorFactory(context);
    }
    
    @Override
    protected void doStart() throws Exception {
        final Connector sslConnector = this.socketConnectorFactory.createConnector();
        this.setConnector(sslConnector);
        super.doStart();
    }
}
