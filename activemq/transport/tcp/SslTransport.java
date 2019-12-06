// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.X509Certificate;
import org.apache.activemq.command.ConnectionInfo;
import java.net.Socket;
import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.HashMap;
import javax.net.ssl.SSLSocket;
import javax.net.SocketFactory;
import java.net.URI;
import javax.net.ssl.SSLSocketFactory;
import org.apache.activemq.wireformat.WireFormat;

public class SslTransport extends TcpTransport
{
    public SslTransport(final WireFormat wireFormat, final SSLSocketFactory socketFactory, final URI remoteLocation, final URI localLocation, final boolean needClientAuth) throws IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
        if (this.socket != null) {
            ((SSLSocket)this.socket).setNeedClientAuth(needClientAuth);
            final HashMap props = new HashMap();
            props.put("host", remoteLocation.getHost());
            IntrospectionSupport.setProperties(this.socket, props);
        }
    }
    
    public SslTransport(final WireFormat wireFormat, final SSLSocket socket) throws IOException {
        super(wireFormat, socket);
    }
    
    @Override
    public void doConsume(final Object command) {
        if (command instanceof ConnectionInfo) {
            final ConnectionInfo connectionInfo = (ConnectionInfo)command;
            connectionInfo.setTransportContext(this.getPeerCertificates());
        }
        super.doConsume(command);
    }
    
    public X509Certificate[] getPeerCertificates() {
        final SSLSocket sslSocket = (SSLSocket)this.socket;
        final SSLSession sslSession = sslSocket.getSession();
        X509Certificate[] clientCertChain;
        try {
            clientCertChain = (X509Certificate[])sslSession.getPeerCertificates();
        }
        catch (SSLPeerUnverifiedException e) {
            clientCertChain = null;
        }
        return clientCertChain;
    }
    
    @Override
    public String toString() {
        return "ssl://" + this.socket.getInetAddress() + ":" + this.socket.getPort();
    }
}
