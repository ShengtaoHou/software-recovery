// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class TcpBufferedInputStream extends FilterInputStream
{
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    protected byte[] internalBuffer;
    protected int count;
    protected int position;
    
    public TcpBufferedInputStream(final InputStream in) {
        this(in, 8192);
    }
    
    public TcpBufferedInputStream(final InputStream in, final int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.internalBuffer = new byte[size];
    }
    
    protected void fill() throws IOException {
        final byte[] buffer = this.internalBuffer;
        this.count = 0;
        this.position = 0;
        final int n = this.in.read(buffer, this.position, buffer.length - this.position);
        if (n > 0) {
            this.count = n + this.position;
        }
    }
    
    @Override
    public int read() throws IOException {
        if (this.position >= this.count) {
            this.fill();
            if (this.position >= this.count) {
                return -1;
            }
        }
        return this.internalBuffer[this.position++] & 0xFF;
    }
    
    private int readStream(final byte[] b, final int off, final int len) throws IOException {
        int avail = this.count - this.position;
        if (avail <= 0) {
            if (len >= this.internalBuffer.length) {
                return this.in.read(b, off, len);
            }
            this.fill();
            avail = this.count - this.position;
            if (avail <= 0) {
                return -1;
            }
        }
        final int cnt = (avail < len) ? avail : len;
        System.arraycopy(this.internalBuffer, this.position, b, off, cnt);
        this.position += cnt;
        return cnt;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if ((off | len | off + len | b.length - (off + len)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int n = 0;
        while (true) {
            final int nread = this.readStream(b, off + n, len - n);
            if (nread <= 0) {
                return (n == 0) ? nread : n;
            }
            n += nread;
            if (n >= len) {
                return n;
            }
            final InputStream input = this.in;
            if (input != null && input.available() <= 0) {
                return n;
            }
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        final long avail = this.count - this.position;
        if (avail <= 0L) {
            return this.in.skip(n);
        }
        final long skipped = (avail < n) ? avail : n;
        this.position += (int)skipped;
        return skipped;
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available() + (this.count - this.position);
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
        }
    }
}
