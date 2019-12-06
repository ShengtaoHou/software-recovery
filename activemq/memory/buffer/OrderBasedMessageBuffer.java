// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.buffer;

import java.util.Iterator;
import java.util.LinkedList;

public class OrderBasedMessageBuffer implements MessageBuffer
{
    private int limit;
    private LinkedList<MessageQueue> list;
    private int size;
    private Object lock;
    
    public OrderBasedMessageBuffer() {
        this.limit = 6553600;
        this.list = new LinkedList<MessageQueue>();
        this.lock = new Object();
    }
    
    public OrderBasedMessageBuffer(final int limit) {
        this.limit = 6553600;
        this.list = new LinkedList<MessageQueue>();
        this.lock = new Object();
        this.limit = limit;
    }
    
    @Override
    public int getSize() {
        synchronized (this.lock) {
            return this.size;
        }
    }
    
    @Override
    public MessageQueue createMessageQueue() {
        return new MessageQueue(this);
    }
    
    @Override
    public void onSizeChanged(final MessageQueue queue, final int delta, final int queueSize) {
        synchronized (this.lock) {
            this.list.addLast(queue);
            this.size += delta;
            while (this.size > this.limit) {
                final MessageQueue biggest = this.list.removeFirst();
                this.size -= biggest.evictMessage();
            }
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.lock) {
            for (final MessageQueue queue : this.list) {
                queue.clear();
            }
            this.size = 0;
        }
    }
}
