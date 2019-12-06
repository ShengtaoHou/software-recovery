// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.apache.activemq.util.ServiceStopper;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import org.apache.activemq.transport.TransportSupport;

public class BlockingQueueTransport extends TransportSupport
{
    public static final long MAX_TIMEOUT = 30000L;
    private BlockingQueue<Object> queue;
    
    public BlockingQueueTransport(final BlockingQueue<Object> channel) {
        this.queue = channel;
    }
    
    public BlockingQueue<Object> getQueue() {
        return this.queue;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        try {
            final boolean success = this.queue.offer(command, 30000L, TimeUnit.MILLISECONDS);
            if (!success) {
                throw new IOException("Fail to add to BlockingQueue. Add timed out after 30000ms: size=" + this.queue.size());
            }
        }
        catch (InterruptedException e) {
            throw new IOException("Fail to add to BlockingQueue. Interrupted while waiting for space: size=" + this.queue.size());
        }
    }
    
    @Override
    public String getRemoteAddress() {
        return "blockingQueue_" + this.queue.hashCode();
    }
    
    @Override
    protected void doStart() throws Exception {
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
    }
    
    @Override
    public int getReceiveCounter() {
        return 0;
    }
}
