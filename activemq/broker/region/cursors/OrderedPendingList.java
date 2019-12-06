// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.activemq.util.LinkedNode;
import org.apache.activemq.broker.region.MessageReference;
import java.util.HashMap;
import org.apache.activemq.command.MessageId;
import java.util.Map;

public class OrderedPendingList implements PendingList
{
    private PendingNode root;
    private PendingNode tail;
    private final Map<MessageId, PendingNode> map;
    
    public OrderedPendingList() {
        this.root = null;
        this.tail = null;
        this.map = new HashMap<MessageId, PendingNode>();
    }
    
    @Override
    public PendingNode addMessageFirst(final MessageReference message) {
        final PendingNode node = new PendingNode(this, message);
        if (this.root == null) {
            this.root = node;
            this.tail = node;
        }
        else {
            this.root.linkBefore(node);
            this.root = node;
        }
        this.map.put(message.getMessageId(), node);
        return node;
    }
    
    @Override
    public PendingNode addMessageLast(final MessageReference message) {
        final PendingNode node = new PendingNode(this, message);
        if (this.root == null) {
            this.root = node;
        }
        else {
            this.tail.linkAfter(node);
        }
        this.tail = node;
        this.map.put(message.getMessageId(), node);
        return node;
    }
    
    @Override
    public void clear() {
        this.root = null;
        this.tail = null;
        this.map.clear();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public Iterator<MessageReference> iterator() {
        return new Iterator<MessageReference>() {
            private PendingNode current = null;
            private PendingNode next = OrderedPendingList.this.root;
            
            @Override
            public boolean hasNext() {
                return this.next != null;
            }
            
            @Override
            public MessageReference next() {
                MessageReference result = null;
                this.current = this.next;
                result = this.current.getMessage();
                this.next = (PendingNode)this.next.getNext();
                return result;
            }
            
            @Override
            public void remove() {
                if (this.current != null && this.current.getMessage() != null) {
                    OrderedPendingList.this.map.remove(this.current.getMessage().getMessageId());
                }
                OrderedPendingList.this.removeNode(this.current);
            }
        };
    }
    
    @Override
    public PendingNode remove(final MessageReference message) {
        PendingNode node = null;
        if (message != null) {
            node = this.map.remove(message.getMessageId());
            this.removeNode(node);
        }
        return node;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    void removeNode(final PendingNode node) {
        if (node != null) {
            this.map.remove(node.getMessage().getMessageId());
            if (this.root == node) {
                this.root = (PendingNode)node.getNext();
            }
            if (this.tail == node) {
                this.tail = (PendingNode)node.getPrevious();
            }
            node.unlink();
        }
    }
    
    List<PendingNode> getAsList() {
        final List<PendingNode> result = new ArrayList<PendingNode>(this.size());
        for (PendingNode node = this.root; node != null; node = (PendingNode)node.getNext()) {
            result.add(node);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "OrderedPendingList(" + System.identityHashCode(this) + ")";
    }
    
    @Override
    public boolean contains(final MessageReference message) {
        if (message != null) {
            for (final PendingNode value : this.map.values()) {
                if (value.getMessage().equals(message)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Collection<MessageReference> values() {
        final List<MessageReference> messageReferences = new ArrayList<MessageReference>();
        final Iterator<MessageReference> iterator = this.iterator();
        while (iterator.hasNext()) {
            messageReferences.add(iterator.next());
        }
        return messageReferences;
    }
    
    @Override
    public void addAll(final PendingList pendingList) {
        if (pendingList != null) {
            for (final MessageReference messageReference : pendingList) {
                this.addMessageLast(messageReference);
            }
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
}
