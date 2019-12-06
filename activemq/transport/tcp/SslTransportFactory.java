// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.SslContext;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.io.IOException;
import javax.net.ServerSocketFactory;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.slf4j.Logger;

public class SslTransportFactory extends TcpTransportFactory
{
    private static final Logger LOG;
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final ServerSocketFactory serverSocketFactory = this.createServerSocketFactory();
            final SslTransportServer server = this.createSslTransportServer(location, (SSLServerSocketFactory)serverSocketFactory);
            server.setWireFormatFactory(this.createWireFormatFactory(options));
            IntrospectionSupport.setProperties(server, options);
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "transport.");
            server.setTransportOption(transportOptions);
            server.bind();
            return server;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    protected SslTransportServer createSslTransportServer(final URI location, final SSLServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new SslTransportServer(this, location, serverSocketFactory);
    }
    
    @Override
    public Transport compositeConfigure(final Transport transport, final WireFormat format, final Map options) {
        final SslTransport sslTransport = transport.narrow(SslTransport.class);
        IntrospectionSupport.setProperties(sslTransport, options);
        return super.compositeConfigure(transport, format, options);
    }
    
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws UnknownHostException, IOException {
        URI localLocation = null;
        final String path = location.getPath();
        if (path != null && path.length() > 0) {
            final int localPortIndex = path.indexOf(58);
            try {
                Integer.parseInt(path.substring(localPortIndex + 1, path.length()));
                final String localString = location.getScheme() + ":/" + path;
                localLocation = new URI(localString);
            }
            catch (Exception e) {
                SslTransportFactory.LOG.warn("path isn't a valid local location for SslTransport to use", e);
            }
        }
        final SocketFactory socketFactory = this.createSocketFactory();
        return new SslTransport(wf, (SSLSocketFactory)socketFactory, location, localLocation, false);
    }
    
    @Override
    protected ServerSocketFactory createServerSocketFactory() throws IOException {
        if (SslContext.getCurrentSslContext() != null) {
            final SslContext ctx = SslContext.getCurrentSslContext();
            try {
                return ctx.getSSLContext().getServerSocketFactory();
            }
            catch (Exception e) {
                throw IOExceptionSupport.create(e);
            }
        }
        return SSLServerSocketFactory.getDefault();
    }
    
    @Override
    protected SocketFactory createSocketFactory() throws IOException {
        if (SslContext.getCurrentSslContext() != null) {
            final SslContext ctx = SslContext.getCurrentSslContext();
            try {
                return ctx.getSSLContext().getSocketFactory();
            }
            catch (Exception e) {
                throw IOExceptionSupport.create(e);
            }
        }
        return SSLSocketFactory.getDefault();
    }
    
    static {
        LOG = LoggerFactory.getLogger(SslTransportFactory.class);
    }
}
