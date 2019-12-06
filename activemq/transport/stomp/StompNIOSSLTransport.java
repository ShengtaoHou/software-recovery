// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import org.apache.activemq.transport.tcp.TcpTransport;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import java.security.cert.X509Certificate;
import org.apache.activemq.transport.nio.NIOSSLTransport;

public class StompNIOSSLTransport extends NIOSSLTransport
{
    StompCodec codec;
    private X509Certificate[] cachedPeerCerts;
    
    public StompNIOSSLTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
    }
    
    public StompNIOSSLTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        this.codec = new StompCodec(this);
        super.initializeStreams();
        if (this.inputBuffer.position() != 0 && this.inputBuffer.hasRemaining()) {
            this.serviceRead();
        }
    }
    
    @Override
    protected void processCommand(final ByteBuffer plain) throws Exception {
        final byte[] fill = new byte[plain.remaining()];
        plain.get(fill);
        final ByteArrayInputStream input = new ByteArrayInputStream(fill);
        this.codec.parse(input, fill.length);
    }
    
    @Override
    public void doConsume(final Object command) {
        final StompFrame frame = (StompFrame)command;
        if (this.cachedPeerCerts == null) {
            this.cachedPeerCerts = this.getPeerCertificates();
        }
        frame.setTransportContext(this.cachedPeerCerts);
        super.doConsume(command);
    }
}
