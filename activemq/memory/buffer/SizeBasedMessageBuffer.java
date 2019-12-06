// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.buffer;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class SizeBasedMessageBuffer implements MessageBuffer
{
    private int limit;
    private List<MessageQueue> bubbleList;
    private int size;
    private Object lock;
    
    public SizeBasedMessageBuffer() {
        this.limit = 6553600;
        this.bubbleList = new ArrayList<MessageQueue>();
        this.lock = new Object();
    }
    
    public SizeBasedMessageBuffer(final int limit) {
        this.limit = 6553600;
        this.bubbleList = new ArrayList<MessageQueue>();
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
        final MessageQueue queue = new MessageQueue(this);
        synchronized (this.lock) {
            queue.setPosition(this.bubbleList.size());
            this.bubbleList.add(queue);
        }
        return queue;
    }
    
    @Override
    public void onSizeChanged(final MessageQueue queue, final int delta, final int queueSize) {
        synchronized (this.lock) {
            this.bubbleUp(queue, queueSize);
            this.size += delta;
            while (this.size > this.limit) {
                final MessageQueue biggest = this.bubbleList.get(0);
                this.size -= biggest.evictMessage();
                this.bubbleDown(biggest, 0);
            }
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.lock) {
            for (final MessageQueue queue : this.bubbleList) {
                queue.clear();
            }
            this.size = 0;
        }
    }
    
    protected void bubbleUp(final MessageQueue queue, final int queueSize) {
        int position = queue.getPosition();
        while (--position >= 0) {
            final MessageQueue pivot = this.bubbleList.get(position);
            if (pivot.getSize() >= queueSize) {
                break;
            }
            this.swap(position, pivot, position + 1, queue);
        }
    }
    
    protected void bubbleDown(final MessageQueue biggest, int position) {
        final int queueSize = biggest.getSize();
        for (int end = this.bubbleList.size(), second = position + 1; second < end; ++second) {
            final MessageQueue pivot = this.bubbleList.get(second);
            if (pivot.getSize() <= queueSize) {
                break;
            }
            this.swap(position, biggest, second, pivot);
            position = second;
        }
    }
    
    protected void swap(final int firstPosition, final MessageQueue first, final int secondPosition, final MessageQueue second) {
        this.bubbleList.set(firstPosition, second);
        this.bubbleList.set(secondPosition, first);
        first.setPosition(secondPosition);
        second.setPosition(firstPosition);
    }
}
