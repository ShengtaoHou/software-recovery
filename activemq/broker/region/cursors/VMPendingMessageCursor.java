// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import java.util.LinkedList;
import org.apache.activemq.broker.region.QueueMessageReference;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;
import java.util.Iterator;

public class VMPendingMessageCursor extends AbstractPendingMessageCursor
{
    private final PendingList list;
    private Iterator<MessageReference> iter;
    
    public VMPendingMessageCursor(final boolean prioritizedMessages) {
        super(prioritizedMessages);
        if (this.prioritizedMessages) {
            this.list = new PrioritizedPendingList();
        }
        else {
            this.list = new OrderedPendingList();
        }
    }
    
    @Override
    public synchronized List<MessageReference> remove(final ConnectionContext context, final Destination destination) throws Exception {
        final List<MessageReference> rc = new ArrayList<MessageReference>();
        final Iterator<MessageReference> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            final MessageReference r = iterator.next();
            if (r.getRegionDestination() == destination) {
                r.decrementReferenceCount();
                rc.add(r);
                iterator.remove();
            }
        }
        return rc;
    }
    
    @Override
    public synchronized boolean isEmpty() {
        if (this.list.isEmpty()) {
            return true;
        }
        final Iterator<MessageReference> iterator = this.list.iterator();
        while (iterator.hasNext()) {
            final MessageReference node = iterator.next();
            if (node == QueueMessageReference.NULL_MESSAGE) {
                continue;
            }
            if (!node.isDropped()) {
                return false;
            }
            iterator.remove();
        }
        return true;
    }
    
    @Override
    public synchronized void reset() {
        this.iter = this.list.iterator();
        this.last = null;
    }
    
    @Override
    public synchronized void addMessageLast(final MessageReference node) {
        node.incrementReferenceCount();
        this.list.addMessageLast(node);
    }
    
    @Override
    public synchronized void addMessageFirst(final MessageReference node) {
        node.incrementReferenceCount();
        this.list.addMessageFirst(node);
    }
    
    @Override
    public synchronized boolean hasNext() {
        return this.iter.hasNext();
    }
    
    @Override
    public synchronized MessageReference next() {
        this.last = this.iter.next();
        if (this.last != null) {
            this.last.incrementReferenceCount();
        }
        return this.last;
    }
    
    @Override
    public synchronized void remove() {
        if (this.last != null) {
            this.last.decrementReferenceCount();
        }
        this.iter.remove();
    }
    
    @Override
    public synchronized int size() {
        return this.list.size();
    }
    
    @Override
    public synchronized void clear() {
        for (final MessageReference ref : this.list) {
            ref.decrementReferenceCount();
        }
        this.list.clear();
    }
    
    @Override
    public synchronized void remove(final MessageReference node) {
        this.list.remove(node);
        node.decrementReferenceCount();
    }
    
    @Override
    public LinkedList<MessageReference> pageInList(final int maxItems) {
        final LinkedList<MessageReference> result = new LinkedList<MessageReference>();
        for (final MessageReference ref : this.list) {
            ref.incrementReferenceCount();
            result.add(ref);
            if (result.size() >= maxItems) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public boolean isTransient() {
        return true;
    }
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
        this.clear();
    }
}
