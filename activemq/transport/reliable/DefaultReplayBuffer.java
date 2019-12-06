// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;

public class DefaultReplayBuffer implements ReplayBuffer
{
    private static final Logger LOG;
    private final int size;
    private ReplayBufferListener listener;
    private Map<Integer, Object> map;
    private int lowestCommandId;
    private Object lock;
    
    public DefaultReplayBuffer(final int size) {
        this.lowestCommandId = 1;
        this.lock = new Object();
        this.size = size;
        this.map = this.createMap(size);
    }
    
    @Override
    public void addBuffer(final int commandId, final Object buffer) {
        if (DefaultReplayBuffer.LOG.isDebugEnabled()) {
            DefaultReplayBuffer.LOG.debug("Adding command ID: " + commandId + " to replay buffer: " + this + " object: " + buffer);
        }
        synchronized (this.lock) {
            final int max = this.size - 1;
            while (this.map.size() >= max) {
                final Object evictedBuffer = this.map.remove(++this.lowestCommandId);
                this.onEvictedBuffer(this.lowestCommandId, evictedBuffer);
            }
            this.map.put(commandId, buffer);
        }
    }
    
    @Override
    public void setReplayBufferListener(final ReplayBufferListener bufferPoolAdapter) {
        this.listener = bufferPoolAdapter;
    }
    
    @Override
    public void replayMessages(final int fromCommandId, final int toCommandId, final Replayer replayer) throws IOException {
        if (replayer == null) {
            throw new IllegalArgumentException("No Replayer parameter specified");
        }
        if (DefaultReplayBuffer.LOG.isDebugEnabled()) {
            DefaultReplayBuffer.LOG.debug("Buffer: " + this + " replaying messages from: " + fromCommandId + " to: " + toCommandId);
        }
        for (int i = fromCommandId; i <= toCommandId; ++i) {
            Object buffer = null;
            synchronized (this.lock) {
                buffer = this.map.get(i);
            }
            replayer.sendBuffer(i, buffer);
        }
    }
    
    protected Map<Integer, Object> createMap(final int maximumSize) {
        return new HashMap<Integer, Object>(maximumSize);
    }
    
    protected void onEvictedBuffer(final int commandId, final Object buffer) {
        if (this.listener != null) {
            this.listener.onBufferDiscarded(commandId, buffer);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultReplayBuffer.class);
    }
}
