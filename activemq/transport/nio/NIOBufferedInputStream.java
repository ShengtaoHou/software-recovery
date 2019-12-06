// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.nio;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.InputStream;

public class NIOBufferedInputStream extends InputStream
{
    private static final int BUFFER_SIZE = 8192;
    private SocketChannel sc;
    private ByteBuffer bb;
    private Selector rs;
    
    public NIOBufferedInputStream(final ReadableByteChannel channel, final int size) throws ClosedChannelException, IOException {
        this.sc = null;
        this.bb = null;
        this.rs = null;
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.bb = ByteBuffer.allocateDirect(size);
        (this.sc = (SocketChannel)channel).configureBlocking(false);
        this.rs = Selector.open();
        this.sc.register(this.rs, 1);
        this.bb.position(0);
        this.bb.limit(0);
    }
    
    public NIOBufferedInputStream(final ReadableByteChannel channel) throws ClosedChannelException, IOException {
        this(channel, 8192);
    }
    
    @Override
    public int available() throws IOException {
        if (!this.rs.isOpen()) {
            throw new IOException("Input Stream Closed");
        }
        return this.bb.remaining();
    }
    
    @Override
    public void close() throws IOException {
        if (this.rs.isOpen()) {
            this.rs.close();
            if (this.sc.isOpen()) {
                this.sc.socket().shutdownInput();
                this.sc.socket().close();
            }
            this.bb = null;
            this.sc = null;
        }
    }
    
    @Override
    public int read() throws IOException {
        if (!this.rs.isOpen()) {
            throw new IOException("Input Stream Closed");
        }
        if (!this.bb.hasRemaining()) {
            try {
                this.fill(1);
            }
            catch (ClosedChannelException e) {
                this.close();
                return -1;
            }
        }
        return this.bb.get() & 0xFF;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int bytesCopied = -1;
        if (!this.rs.isOpen()) {
            throw new IOException("Input Stream Closed");
        }
        while (bytesCopied == -1) {
            if (!this.bb.hasRemaining()) {
                try {
                    this.fill(1);
                    continue;
                }
                catch (ClosedChannelException e) {
                    this.close();
                    return -1;
                }
                break;
            }
            bytesCopied = ((len < this.bb.remaining()) ? len : this.bb.remaining());
            this.bb.get(b, off, bytesCopied);
        }
        return bytesCopied;
    }
    
    @Override
    public long skip(long n) throws IOException {
        long skiped = 0L;
        if (!this.rs.isOpen()) {
            throw new IOException("Input Stream Closed");
        }
        while (n > 0L) {
            if (n > this.bb.remaining()) {
                skiped += this.bb.remaining();
                n -= this.bb.remaining();
                this.bb.position(this.bb.limit());
                try {
                    this.fill((int)n);
                    continue;
                }
                catch (ClosedChannelException e) {
                    this.close();
                    return skiped;
                }
                break;
            }
            skiped += n;
            this.bb.position(this.bb.position() + (int)n);
            n = 0L;
        }
        return skiped;
    }
    
    private void fill(int n) throws IOException, ClosedChannelException {
        int bytesRead = -1;
        if (n <= 0 || n <= this.bb.remaining()) {
            return;
        }
        this.bb.compact();
        n = ((this.bb.remaining() < n) ? this.bb.remaining() : n);
        while (true) {
            bytesRead = this.sc.read(this.bb);
            if (bytesRead == -1) {
                throw new ClosedChannelException();
            }
            n -= bytesRead;
            if (n <= 0) {
                this.bb.flip();
                return;
            }
            this.rs.select(0L);
            this.rs.selectedKeys().clear();
        }
    }
}
