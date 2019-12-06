// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import javax.net.ssl.SSLSocket;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import javax.net.ssl.SSLServerSocket;
import java.net.URISyntaxException;
import java.io.IOException;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.URI;

public class SslTransportServer extends TcpTransportServer
{
    private boolean needClientAuth;
    private boolean wantClientAuth;
    
    public SslTransportServer(final SslTransportFactory transportFactory, final URI location, final SSLServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        super(transportFactory, location, serverSocketFactory);
    }
    
    public void setNeedClientAuth(final boolean needAuth) {
        this.needClientAuth = needAuth;
    }
    
    public boolean getNeedClientAuth() {
        return this.needClientAuth;
    }
    
    public boolean getWantClientAuth() {
        return this.wantClientAuth;
    }
    
    public void setWantClientAuth(final boolean wantAuth) {
        this.wantClientAuth = wantAuth;
    }
    
    @Override
    public void bind() throws IOException {
        super.bind();
        if (this.needClientAuth) {
            ((SSLServerSocket)this.serverSocket).setNeedClientAuth(true);
        }
        else if (this.wantClientAuth) {
            ((SSLServerSocket)this.serverSocket).setWantClientAuth(true);
        }
    }
    
    @Override
    protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
        return new SslTransport(format, (SSLSocket)socket);
    }
    
    @Override
    public boolean isSslServer() {
        return true;
    }
}
