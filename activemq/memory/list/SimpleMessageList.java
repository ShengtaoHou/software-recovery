// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory.list;

import java.util.Collection;
import java.util.Iterator;
import org.apache.activemq.filter.DestinationFilter;
import java.util.ArrayList;
import org.apache.activemq.command.Message;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public class SimpleMessageList implements MessageList
{
    private final LinkedList<MessageReference> list;
    private int maximumSize;
    private int size;
    private final Object lock;
    
    public SimpleMessageList() {
        this.list = new LinkedList<MessageReference>();
        this.maximumSize = 6553600;
        this.lock = new Object();
    }
    
    public SimpleMessageList(final int maximumSize) {
        this.list = new LinkedList<MessageReference>();
        this.maximumSize = 6553600;
        this.lock = new Object();
        this.maximumSize = maximumSize;
    }
    
    @Override
    public void add(final MessageReference node) {
        final int delta = node.getMessageHardRef().getSize();
        synchronized (this.lock) {
            this.list.add(node);
            this.size += delta;
            while (this.size > this.maximumSize) {
                final MessageReference evicted = this.list.removeFirst();
                this.size -= evicted.getMessageHardRef().getSize();
            }
        }
    }
    
    @Override
    public List<MessageReference> getMessages(final ActiveMQDestination destination) {
        return this.getList();
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) {
        final List<Message> result = new ArrayList<Message>();
        final DestinationFilter filter = DestinationFilter.parseFilter(destination);
        synchronized (this.lock) {
            for (final MessageReference ref : this.list) {
                final Message msg = ref.getMessage();
                if (filter.matches(msg.getDestination())) {
                    result.add(msg);
                }
            }
        }
        return result.toArray(new Message[result.size()]);
    }
    
    public List<MessageReference> getList() {
        synchronized (this.lock) {
            return new ArrayList<MessageReference>(this.list);
        }
    }
    
    public int getSize() {
        synchronized (this.lock) {
            return this.size;
        }
    }
    
    @Override
    public void clear() {
        synchronized (this.lock) {
            this.list.clear();
            this.size = 0;
        }
    }
}
