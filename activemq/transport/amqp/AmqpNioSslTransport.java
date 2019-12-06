// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.activemq.transport.TransportSupport;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.nio.NIOSSLTransport;

public class AmqpNioSslTransport extends NIOSSLTransport
{
    private final AmqpNioTransportHelper amqpNioTransportHelper;
    
    public AmqpNioSslTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
        this.amqpNioTransportHelper = new AmqpNioTransportHelper(this);
    }
    
    public AmqpNioSslTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
        this.amqpNioTransportHelper = new AmqpNioTransportHelper(this);
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        super.initializeStreams();
        if (this.inputBuffer.position() != 0 && this.inputBuffer.hasRemaining()) {
            this.serviceRead();
        }
    }
    
    @Override
    protected void processCommand(final ByteBuffer plain) throws Exception {
        this.amqpNioTransportHelper.processCommand(plain);
    }
}
