// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.apache.activemq.broker.SslContext;
import org.apache.activemq.transport.TransportServer;
import java.net.UnknownHostException;
import org.apache.activemq.transport.tcp.TcpTransport;
import javax.net.SocketFactory;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import org.apache.activemq.transport.nio.NIOSSLTransportServer;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import javax.net.ServerSocketFactory;
import java.net.URI;
import javax.net.ssl.SSLContext;

public class StompNIOSSLTransportFactory extends StompNIOTransportFactory
{
    protected SSLContext context;
    
    @Override
    protected TcpTransportServer createTcpTransportServer(final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new NIOSSLTransportServer(this.context, this, location, serverSocketFactory) {
            @Override
            protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
                final StompNIOSSLTransport transport = new StompNIOSSLTransport(format, socket);
                if (StompNIOSSLTransportFactory.this.context != null) {
                    transport.setSslContext(StompNIOSSLTransportFactory.this.context);
                }
                transport.setNeedClientAuth(this.isNeedClientAuth());
                transport.setWantClientAuth(this.isWantClientAuth());
                return transport;
            }
        };
    }
    
    @Override
    protected TcpTransport createTcpTransport(final WireFormat wf, final SocketFactory socketFactory, final URI location, final URI localLocation) throws UnknownHostException, IOException {
        return new StompNIOSSLTransport(wf, socketFactory, location, localLocation);
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
}
