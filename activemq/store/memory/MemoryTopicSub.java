// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.memory;

import java.util.Iterator;
import org.apache.activemq.store.MessageRecoveryListener;
import java.util.LinkedHashMap;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import java.util.Map;

class MemoryTopicSub
{
    private Map<MessageId, Message> map;
    private MessageId lastBatch;
    
    MemoryTopicSub() {
        this.map = new LinkedHashMap<MessageId, Message>();
    }
    
    void addMessage(final MessageId id, final Message message) {
        synchronized (this) {
            this.map.put(id, message);
        }
        message.incrementReferenceCount();
    }
    
    void removeMessage(final MessageId id) {
        final Message removed;
        synchronized (this) {
            removed = this.map.remove(id);
            if ((this.lastBatch != null && this.lastBatch.equals(id)) || this.map.isEmpty()) {
                this.resetBatching();
            }
        }
        if (removed != null) {
            removed.decrementReferenceCount();
        }
    }
    
    synchronized int size() {
        return this.map.size();
    }
    
    synchronized void recoverSubscription(final MessageRecoveryListener listener) throws Exception {
        for (final Map.Entry entry : this.map.entrySet()) {
            final Object msg = entry.getValue();
            if (msg.getClass() == MessageId.class) {
                listener.recoverMessageReference((MessageId)msg);
            }
            else {
                listener.recoverMessage((Message)msg);
            }
        }
    }
    
    synchronized void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        boolean pastLackBatch = this.lastBatch == null;
        MessageId lastId = null;
        int count = 0;
        final Iterator iter = this.map.entrySet().iterator();
        while (iter.hasNext() && count < maxReturned) {
            final Map.Entry entry = iter.next();
            if (pastLackBatch) {
                ++count;
                final Object msg = entry.getValue();
                lastId = entry.getKey();
                if (msg.getClass() == MessageId.class) {
                    listener.recoverMessageReference((MessageId)msg);
                }
                else {
                    listener.recoverMessage((Message)msg);
                }
            }
            else {
                pastLackBatch = entry.getKey().equals(this.lastBatch);
            }
        }
        if (lastId != null) {
            this.lastBatch = lastId;
        }
    }
    
    synchronized void resetBatching() {
        this.lastBatch = null;
    }
}
