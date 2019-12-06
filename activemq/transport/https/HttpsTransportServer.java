// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.https;

import org.eclipse.jetty.server.Connector;
import org.apache.activemq.transport.SecureSocketConnectorFactory;
import org.apache.activemq.transport.http.HttpTransportFactory;
import java.net.URI;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.transport.http.HttpTransportServer;

public class HttpsTransportServer extends HttpTransportServer
{
    private SslContext context;
    
    public HttpsTransportServer(final URI uri, final HttpsTransportFactory factory, final SslContext context) {
        super(uri, factory);
        this.context = context;
        this.socketConnectorFactory = new SecureSocketConnectorFactory(context);
    }
    
    public void doStart() throws Exception {
        final Connector sslConnector = this.socketConnectorFactory.createConnector();
        this.setConnector(sslConnector);
        super.doStart();
    }
}
