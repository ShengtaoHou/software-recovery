// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.IOExceptionSupport;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.transport.tcp.SslTransport;
import java.util.Map;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.broker.SslContext;
import org.apache.activemq.transport.TransportServer;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import javax.net.ServerSocketFactory;
import java.net.URI;
import javax.net.ssl.SSLContext;
import org.slf4j.Logger;

public class NIOSSLTransportFactory extends NIOTransportFactory
{
    private static final Logger LOG;
    protected SSLContext context;
    
    @Override
    protected TcpTransportServer createTcpTransportServer(final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new NIOSSLTransportServer(this.context, this, location, serverSocketFactory);
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        if (SslContext.getCurrentSslContext() != null) {
            try {
                this.context = SslContext.getCurrentSslContext().getSSLContext();
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        return super.doBind(location);
    }
    
    @Override
    public Transport compositeConfigure(final Transport transport, final WireFormat format, final Map options) {
        if (transport instanceof SslTransport) {
            final SslTransport sslTransport = transport.narrow(SslTransport.class);
            IntrospectionSupport.setProperties(sslTransport, options);
        }
        else if (transport instanceof NIOSSLTransport) {
            final NIOSSLTransport sslTransport2 = transport.narrow(NIOSSLTransport.class);
            IntrospectionSupport.setProperties(sslTransport2, options);
        }
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
                NIOSSLTransportFactory.LOG.warn("path isn't a valid local location for SslTransport to use", e);
            }
        }
        final SocketFactory socketFactory = this.createSocketFactory();
        return new SslTransport(wf, (SSLSocketFactory)socketFactory, location, localLocation, false);
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
        LOG = LoggerFactory.getLogger(NIOSSLTransportFactory.class);
    }
}
