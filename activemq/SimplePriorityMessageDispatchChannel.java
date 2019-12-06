// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.MessageDispatch;
import java.util.LinkedList;

public class SimplePriorityMessageDispatchChannel implements MessageDispatchChannel
{
    private static final Integer MAX_PRIORITY;
    private final Object mutex;
    private final LinkedList<MessageDispatch>[] lists;
    private boolean closed;
    private boolean running;
    private int size;
    
    public SimplePriorityMessageDispatchChannel() {
        this.mutex = new Object();
        this.size = 0;
        this.lists = (LinkedList<MessageDispatch>[])new LinkedList[(int)SimplePriorityMessageDispatchChannel.MAX_PRIORITY];
        for (int i = 0; i < SimplePriorityMessageDispatchChannel.MAX_PRIORITY; ++i) {
            this.lists[i] = new LinkedList<MessageDispatch>();
        }
    }
    
    @Override
    public void enqueue(final MessageDispatch message) {
        synchronized (this.mutex) {
            this.getList(message).addLast(message);
            ++this.size;
            this.mutex.notify();
        }
    }
    
    @Override
    public void enqueueFirst(final MessageDispatch message) {
        synchronized (this.mutex) {
            this.getList(message).addFirst(message);
            ++this.size;
            this.mutex.notify();
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public MessageDispatch dequeue(final long timeout) throws InterruptedException {
        synchronized (this.mutex) {
            while (timeout != 0L && !this.closed && (this.isEmpty() || !this.running)) {
                if (timeout != -1L) {
                    this.mutex.wait(timeout);
                    break;
                }
                this.mutex.wait();
            }
            if (this.closed || !this.running || this.isEmpty()) {
                return null;
            }
            return this.removeFirst();
        }
    }
    
    @Override
    public MessageDispatch dequeueNoWait() {
        synchronized (this.mutex) {
            if (this.closed || !this.running || this.isEmpty()) {
                return null;
            }
            return this.removeFirst();
        }
    }
    
    @Override
    public MessageDispatch peek() {
        synchronized (this.mutex) {
            if (this.closed || !this.running || this.isEmpty()) {
                return null;
            }
            return this.getFirst();
        }
    }
    
    @Override
    public void start() {
        synchronized (this.mutex) {
            this.running = true;
            this.mutex.notifyAll();
        }
    }
    
    @Override
    public void stop() {
        synchronized (this.mutex) {
            this.running = false;
            this.mutex.notifyAll();
        }
    }
    
    @Override
    public void close() {
        synchronized (this.mutex) {
            if (!this.closed) {
                this.running = false;
                this.closed = true;
            }
            this.mutex.notifyAll();
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.mutex) {
            for (int i = 0; i < SimplePriorityMessageDispatchChannel.MAX_PRIORITY; ++i) {
                this.lists[i].clear();
            }
            this.size = 0;
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public int size() {
        synchronized (this.mutex) {
            return this.size;
        }
    }
    
    @Override
    public Object getMutex() {
        return this.mutex;
    }
    
    @Override
    public boolean isRunning() {
        return this.running;
    }
    
    @Override
    public List<MessageDispatch> removeAll() {
        synchronized (this.mutex) {
            final ArrayList<MessageDispatch> result = new ArrayList<MessageDispatch>(this.size());
            for (int i = SimplePriorityMessageDispatchChannel.MAX_PRIORITY - 1; i >= 0; --i) {
                final List<MessageDispatch> list = this.lists[i];
                result.addAll(list);
                this.size -= list.size();
                list.clear();
            }
            return result;
        }
    }
    
    @Override
    public String toString() {
        String result = "";
        for (int i = SimplePriorityMessageDispatchChannel.MAX_PRIORITY - 1; i >= 0; --i) {
            result = result + i + ":{" + this.lists[i].toString() + "}";
        }
        return result;
    }
    
    protected int getPriority(final MessageDispatch message) {
        int priority = 4;
        if (message.getMessage() != null) {
            priority = Math.max(message.getMessage().getPriority(), 0);
            priority = Math.min(priority, 9);
        }
        return priority;
    }
    
    protected LinkedList<MessageDispatch> getList(final MessageDispatch md) {
        return this.lists[this.getPriority(md)];
    }
    
    private final MessageDispatch removeFirst() {
        if (this.size > 0) {
            for (int i = SimplePriorityMessageDispatchChannel.MAX_PRIORITY - 1; i >= 0; --i) {
                final LinkedList<MessageDispatch> list = this.lists[i];
                if (!list.isEmpty()) {
                    --this.size;
                    return list.removeFirst();
                }
            }
        }
        return null;
    }
    
    private final MessageDispatch getFirst() {
        if (this.size > 0) {
            for (int i = SimplePriorityMessageDispatchChannel.MAX_PRIORITY - 1; i >= 0; --i) {
                final LinkedList<MessageDispatch> list = this.lists[i];
                if (!list.isEmpty()) {
                    return list.getFirst();
                }
            }
        }
        return null;
    }
    
    static {
        MAX_PRIORITY = 10;
    }
}
