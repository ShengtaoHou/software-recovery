// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.activemq.broker.region.MessageReference;
import java.util.HashMap;
import org.apache.activemq.command.MessageId;
import java.util.Map;

public class PrioritizedPendingList implements PendingList
{
    private static final Integer MAX_PRIORITY;
    private final OrderedPendingList[] lists;
    private final Map<MessageId, PendingNode> map;
    
    public PrioritizedPendingList() {
        this.lists = new OrderedPendingList[(int)PrioritizedPendingList.MAX_PRIORITY];
        this.map = new HashMap<MessageId, PendingNode>();
        for (int i = 0; i < PrioritizedPendingList.MAX_PRIORITY; ++i) {
            this.lists[i] = new OrderedPendingList();
        }
    }
    
    @Override
    public PendingNode addMessageFirst(final MessageReference message) {
        final PendingNode node = this.getList(message).addMessageFirst(message);
        this.map.put(message.getMessageId(), node);
        return node;
    }
    
    @Override
    public PendingNode addMessageLast(final MessageReference message) {
        final PendingNode node = this.getList(message).addMessageLast(message);
        this.map.put(message.getMessageId(), node);
        return node;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < PrioritizedPendingList.MAX_PRIORITY; ++i) {
            this.lists[i].clear();
        }
        this.map.clear();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public Iterator<MessageReference> iterator() {
        return new PrioritizedPendingListIterator();
    }
    
    @Override
    public PendingNode remove(final MessageReference message) {
        PendingNode node = null;
        if (message != null) {
            node = this.map.remove(message.getMessageId());
            if (node != null) {
                node.getList().removeNode(node);
            }
        }
        return node;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public String toString() {
        return "PrioritizedPendingList(" + System.identityHashCode(this) + ")";
    }
    
    protected int getPriority(final MessageReference message) {
        int priority = 4;
        if (message.getMessageId() != null) {
            priority = Math.max(message.getMessage().getPriority(), 0);
            priority = Math.min(priority, 9);
        }
        return priority;
    }
    
    protected OrderedPendingList getList(final MessageReference msg) {
        return this.lists[this.getPriority(msg)];
    }
    
    @Override
    public boolean contains(final MessageReference message) {
        return this.map.values().contains(message);
    }
    
    @Override
    public Collection<MessageReference> values() {
        final List<MessageReference> messageReferences = new ArrayList<MessageReference>();
        for (final PendingNode pendingNode : this.map.values()) {
            messageReferences.add(pendingNode.getMessage());
        }
        return messageReferences;
    }
    
    @Override
    public void addAll(final PendingList pendingList) {
        for (final MessageReference messageReference : pendingList) {
            this.addMessageLast(messageReference);
        }
    }
    
    @Override
    public MessageReference get(final MessageId messageId) {
        final PendingNode node = this.map.get(messageId);
        if (node != null) {
            return node.getMessage();
        }
        return null;
    }
    
    static {
        MAX_PRIORITY = 10;
    }
    
    private class PrioritizedPendingListIterator implements Iterator<MessageReference>
    {
        private int index;
        private int currentIndex;
        List<PendingNode> list;
        
        PrioritizedPendingListIterator() {
            this.index = 0;
            this.currentIndex = 0;
            this.list = new ArrayList<PendingNode>(PrioritizedPendingList.this.size());
            for (int i = PrioritizedPendingList.MAX_PRIORITY - 1; i >= 0; --i) {
                final OrderedPendingList orderedPendingList = PrioritizedPendingList.this.lists[i];
                if (!orderedPendingList.isEmpty()) {
                    this.list.addAll(orderedPendingList.getAsList());
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.list.size() > this.index;
        }
        
        @Override
        public MessageReference next() {
            final PendingNode node = this.list.get(this.index);
            this.currentIndex = this.index;
            ++this.index;
            return node.getMessage();
        }
        
        @Override
        public void remove() {
            final PendingNode node = this.list.get(this.currentIndex);
            if (node != null) {
                PrioritizedPendingList.this.map.remove(node.getMessage().getMessageId());
                node.getList().removeNode(node);
            }
        }
    }
}
