// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.apache.activemq.util.ServiceStopper;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.nio.channels.WritableByteChannel;
import org.apache.activemq.transport.nio.NIOOutputStream;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.transport.nio.SelectorManager;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import java.nio.ByteBuffer;
import org.apache.activemq.transport.nio.SelectorSelection;
import java.nio.channels.SocketChannel;
import org.apache.activemq.transport.tcp.TcpTransport;

public class MQTTNIOTransport extends TcpTransport
{
    private SocketChannel channel;
    private SelectorSelection selection;
    private ByteBuffer inputBuffer;
    MQTTCodec codec;
    
    public MQTTNIOTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
    }
    
    public MQTTNIOTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        (this.channel = this.socket.getChannel()).configureBlocking(false);
        this.selection = SelectorManager.getInstance().register(this.channel, new SelectorManager.Listener() {
            @Override
            public void onSelect(final SelectorSelection selection) {
                if (!MQTTNIOTransport.this.isStopped()) {
                    MQTTNIOTransport.this.serviceRead();
                }
            }
            
            @Override
            public void onError(final SelectorSelection selection, final Throwable error) {
                if (error instanceof IOException) {
                    MQTTNIOTransport.this.onException((IOException)error);
                }
                else {
                    MQTTNIOTransport.this.onException(IOExceptionSupport.create(error));
                }
            }
        });
        this.inputBuffer = ByteBuffer.allocate(8192);
        final NIOOutputStream outPutStream = new NIOOutputStream(this.channel, 8192);
        this.dataOut = new DataOutputStream(outPutStream);
        this.buffOut = outPutStream;
        this.codec = new MQTTCodec(this);
    }
    
    private void serviceRead() {
        try {
            while (this.isStarted()) {
                final int readSize = this.channel.read(this.inputBuffer);
                if (readSize == -1) {
                    this.onException(new EOFException());
                    this.selection.close();
                    break;
                }
                if (readSize == 0) {
                    break;
                }
                this.inputBuffer.flip();
                final DataByteArrayInputStream dis = new DataByteArrayInputStream(this.inputBuffer.array());
                this.codec.parse(dis, readSize);
                this.receiveCounter += readSize;
                this.inputBuffer.clear();
            }
        }
        catch (IOException e) {
            this.onException(e);
        }
        catch (Throwable e2) {
            this.onException(IOExceptionSupport.create(e2));
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this.connect();
        this.selection.setInterestOps(1);
        this.selection.enable();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        try {
            if (this.selection != null) {
                this.selection.close();
            }
        }
        finally {
            super.doStop(stopper);
        }
    }
}
