// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.fusesource.hawtbuf.DataByteArrayInputStream;
import java.nio.ByteBuffer;
import org.apache.activemq.transport.tcp.TcpTransport;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.nio.NIOSSLTransport;

public class MQTTNIOSSLTransport extends NIOSSLTransport
{
    MQTTCodec codec;
    
    public MQTTNIOSSLTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
    }
    
    public MQTTNIOSSLTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        this.codec = new MQTTCodec(this);
        super.initializeStreams();
        if (this.inputBuffer.position() != 0 && this.inputBuffer.hasRemaining()) {
            this.serviceRead();
        }
    }
    
    @Override
    protected void processCommand(final ByteBuffer plain) throws Exception {
        final byte[] fill = new byte[plain.remaining()];
        plain.get(fill);
        final DataByteArrayInputStream dis = new DataByteArrayInputStream(fill);
        this.codec.parse(dis, fill.length);
    }
}
