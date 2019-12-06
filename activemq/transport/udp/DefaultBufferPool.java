// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.List;

public class DefaultBufferPool extends SimpleBufferPool implements ByteBufferPool
{
    private List<ByteBuffer> buffers;
    private Object lock;
    
    public DefaultBufferPool() {
        super(true);
        this.buffers = new ArrayList<ByteBuffer>();
        this.lock = new Object();
    }
    
    public DefaultBufferPool(final boolean useDirect) {
        super(useDirect);
        this.buffers = new ArrayList<ByteBuffer>();
        this.lock = new Object();
    }
    
    @Override
    public synchronized ByteBuffer borrowBuffer() {
        synchronized (this.lock) {
            final int size = this.buffers.size();
            if (size > 0) {
                return this.buffers.remove(size - 1);
            }
        }
        return this.createBuffer();
    }
    
    @Override
    public void returnBuffer(final ByteBuffer buffer) {
        synchronized (this.lock) {
            this.buffers.add(buffer);
        }
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
        synchronized (this.lock) {
            this.buffers.clear();
        }
    }
}
