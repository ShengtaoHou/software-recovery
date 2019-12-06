// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.command.Command;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.openwire.OpenWireFormat;
import java.io.EOFException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.nio.channels.WritableByteChannel;
import org.apache.activemq.util.IOExceptionSupport;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import javax.net.SocketFactory;
import org.apache.activemq.wireformat.WireFormat;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.apache.activemq.transport.tcp.TcpTransport;

public class NIOTransport extends TcpTransport
{
    protected SocketChannel channel;
    protected SelectorSelection selection;
    protected ByteBuffer inputBuffer;
    protected ByteBuffer currentBuffer;
    protected int nextFrameSize;
    
    public NIOTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        super(wireFormat, socketFactory, remoteLocation, localLocation);
    }
    
    public NIOTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        super(wireFormat, socket);
    }
    
    @Override
    protected void initializeStreams() throws IOException {
        (this.channel = this.socket.getChannel()).configureBlocking(false);
        this.selection = SelectorManager.getInstance().register(this.channel, new SelectorManager.Listener() {
            @Override
            public void onSelect(final SelectorSelection selection) {
                NIOTransport.this.serviceRead();
            }
            
            @Override
            public void onError(final SelectorSelection selection, final Throwable error) {
                if (error instanceof IOException) {
                    NIOTransport.this.onException((IOException)error);
                }
                else {
                    NIOTransport.this.onException(IOExceptionSupport.create(error));
                }
            }
        });
        this.inputBuffer = ByteBuffer.allocate(8192);
        this.currentBuffer = this.inputBuffer;
        this.nextFrameSize = -1;
        this.currentBuffer.limit(4);
        final NIOOutputStream outPutStream = new NIOOutputStream(this.channel, 16384);
        this.dataOut = new DataOutputStream(outPutStream);
        this.buffOut = outPutStream;
    }
    
    protected void serviceRead() {
        try {
            while (true) {
                final int readSize = this.channel.read(this.currentBuffer);
                if (readSize == -1) {
                    this.onException(new EOFException());
                    this.selection.close();
                    break;
                }
                if (readSize == 0) {
                    break;
                }
                if (this.currentBuffer.hasRemaining()) {
                    continue;
                }
                if (this.nextFrameSize == -1) {
                    assert this.inputBuffer == this.currentBuffer;
                    this.inputBuffer.flip();
                    this.nextFrameSize = this.inputBuffer.getInt() + 4;
                    if (this.wireFormat instanceof OpenWireFormat) {
                        final long maxFrameSize = ((OpenWireFormat)this.wireFormat).getMaxFrameSize();
                        if (this.nextFrameSize > maxFrameSize) {
                            throw new IOException("Frame size of " + this.nextFrameSize / 1048576 + " MB larger than max allowed " + maxFrameSize / 1048576L + " MB");
                        }
                    }
                    if (this.nextFrameSize > this.inputBuffer.capacity()) {
                        (this.currentBuffer = ByteBuffer.allocate(this.nextFrameSize)).putInt(this.nextFrameSize);
                    }
                    else {
                        this.inputBuffer.limit(this.nextFrameSize);
                    }
                }
                else {
                    this.currentBuffer.flip();
                    final Object command = this.wireFormat.unmarshal(new DataInputStream(new NIOInputStream(this.currentBuffer)));
                    this.doConsume(command);
                    this.nextFrameSize = -1;
                    this.inputBuffer.clear();
                    this.inputBuffer.limit(4);
                    this.currentBuffer = this.inputBuffer;
                }
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
        if (this.selection != null) {
            this.selection.close();
            this.selection = null;
        }
        super.doStop(stopper);
    }
}
