// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import java.net.URISyntaxException;
import java.io.IOException;
import javax.net.ServerSocketFactory;
import java.net.URI;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import javax.net.ssl.SSLContext;
import org.apache.activemq.transport.tcp.TcpTransportServer;

public class NIOSSLTransportServer extends TcpTransportServer
{
    private SSLContext context;
    private boolean needClientAuth;
    private boolean wantClientAuth;
    
    public NIOSSLTransportServer(final SSLContext context, final TcpTransportFactory transportFactory, final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        super(transportFactory, location, serverSocketFactory);
        this.context = context;
    }
    
    @Override
    protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
        final NIOSSLTransport transport = new NIOSSLTransport(format, socket);
        if (this.context != null) {
            transport.setSslContext(this.context);
        }
        transport.setNeedClientAuth(this.needClientAuth);
        transport.setWantClientAuth(this.wantClientAuth);
        return transport;
    }
    
    @Override
    public boolean isSslServer() {
        return true;
    }
    
    public boolean isNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public void setNeedClientAuth(final boolean value) {
        this.needClientAuth = value;
    }
    
    public boolean isWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean value) {
        this.wantClientAuth = value;
    }
}
