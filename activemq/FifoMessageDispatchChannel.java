// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.MessageDispatch;
import java.util.LinkedList;

public class FifoMessageDispatchChannel implements MessageDispatchChannel
{
    private final Object mutex;
    private final LinkedList<MessageDispatch> list;
    private boolean closed;
    private boolean running;
    
    public FifoMessageDispatchChannel() {
        this.mutex = new Object();
        this.list = new LinkedList<MessageDispatch>();
    }
    
    @Override
    public void enqueue(final MessageDispatch message) {
        synchronized (this.mutex) {
            this.list.addLast(message);
            this.mutex.notify();
        }
    }
    
    @Override
    public void enqueueFirst(final MessageDispatch message) {
        synchronized (this.mutex) {
            this.list.addFirst(message);
            this.mutex.notify();
        }
    }
    
    @Override
    public boolean isEmpty() {
        synchronized (this.mutex) {
            return this.list.isEmpty();
        }
    }
    
    @Override
    public MessageDispatch dequeue(final long timeout) throws InterruptedException {
        synchronized (this.mutex) {
            while (timeout != 0L && !this.closed && (this.list.isEmpty() || !this.running)) {
                if (timeout != -1L) {
                    this.mutex.wait(timeout);
                    break;
                }
                this.mutex.wait();
            }
            if (this.closed || !this.running || this.list.isEmpty()) {
                return null;
            }
            return this.list.removeFirst();
        }
    }
    
    @Override
    public MessageDispatch dequeueNoWait() {
        synchronized (this.mutex) {
            if (this.closed || !this.running || this.list.isEmpty()) {
                return null;
            }
            return this.list.removeFirst();
        }
    }
    
    @Override
    public MessageDispatch peek() {
        synchronized (this.mutex) {
            if (this.closed || !this.running || this.list.isEmpty()) {
                return null;
            }
            return this.list.getFirst();
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
            this.list.clear();
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public int size() {
        synchronized (this.mutex) {
            return this.list.size();
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
            final ArrayList<MessageDispatch> rc = new ArrayList<MessageDispatch>(this.list);
            this.list.clear();
            return rc;
        }
    }
    
    @Override
    public String toString() {
        synchronized (this.mutex) {
            return this.list.toString();
        }
    }
}
