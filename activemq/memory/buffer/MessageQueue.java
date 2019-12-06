// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.buffer;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public class MessageQueue
{
    private MessageBuffer buffer;
    private LinkedList<MessageReference> list;
    private int size;
    private Object lock;
    private int position;
    
    public MessageQueue(final MessageBuffer buffer) {
        this.list = new LinkedList<MessageReference>();
        this.lock = new Object();
        this.buffer = buffer;
    }
    
    public void add(final MessageReference messageRef) {
        final Message message = messageRef.getMessageHardRef();
        final int delta = message.getSize();
        int newSize = 0;
        synchronized (this.lock) {
            this.list.add(messageRef);
            this.size += delta;
            newSize = this.size;
        }
        this.buffer.onSizeChanged(this, delta, newSize);
    }
    
    public void add(final ActiveMQMessage message) {
        final int delta = message.getSize();
        int newSize = 0;
        synchronized (this.lock) {
            this.list.add(message);
            this.size += delta;
            newSize = this.size;
        }
        this.buffer.onSizeChanged(this, delta, newSize);
    }
    
    public int evictMessage() {
        synchronized (this.lock) {
            if (!this.list.isEmpty()) {
                final ActiveMQMessage message = this.list.removeFirst();
                final int messageSize = message.getSize();
                this.size -= messageSize;
                return messageSize;
            }
        }
        return 0;
    }
    
    public List<MessageReference> getList() {
        synchronized (this.lock) {
            return new ArrayList<MessageReference>(this.list);
        }
    }
    
    public void appendMessages(final List<MessageReference> answer) {
        synchronized (this.lock) {
            final Iterator<MessageReference> iter = this.list.iterator();
            while (iter.hasNext()) {
                answer.add(iter.next());
            }
        }
    }
    
    public int getSize() {
        synchronized (this.lock) {
            return this.size;
        }
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(final int position) {
        this.position = position;
    }
    
    public void clear() {
        synchronized (this.lock) {
            this.list.clear();
            this.size = 0;
        }
    }
}
