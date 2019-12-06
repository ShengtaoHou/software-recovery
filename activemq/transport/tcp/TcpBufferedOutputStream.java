// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class TcpBufferedOutputStream extends FilterOutputStream implements TimeStampStream
{
    private static final int BUFFER_SIZE = 8192;
    private byte[] buffer;
    private int bufferlen;
    private int count;
    private volatile long writeTimestamp;
    
    public TcpBufferedOutputStream(final OutputStream out) {
        this(out, 8192);
    }
    
    public TcpBufferedOutputStream(final OutputStream out, final int size) {
        super(out);
        this.writeTimestamp = -1L;
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.buffer = new byte[size];
        this.bufferlen = size;
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (this.bufferlen - this.count < 1) {
            this.flush();
        }
        this.buffer[this.count++] = (byte)b;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (b != null) {
            if (this.bufferlen - this.count < len) {
                this.flush();
            }
            if (this.buffer.length >= len) {
                System.arraycopy(b, off, this.buffer, this.count, len);
                this.count += len;
            }
            else {
                try {
                    this.writeTimestamp = System.currentTimeMillis();
                    this.out.write(b, off, len);
                }
                finally {
                    this.writeTimestamp = -1L;
                }
            }
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.count > 0 && this.out != null) {
            try {
                this.writeTimestamp = System.currentTimeMillis();
                this.out.write(this.buffer, 0, this.count);
            }
            finally {
                this.writeTimestamp = -1L;
            }
            this.count = 0;
        }
    }
    
    @Override
    public void close() throws IOException {
        super.close();
    }
    
    @Override
    public boolean isWriting() {
        return this.writeTimestamp > 0L;
    }
    
    @Override
    public long getWriteTimestamp() {
        return this.writeTimestamp;
    }
}
