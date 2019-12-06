// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.apache.activemq.command.MessageId;
import java.util.Collection;
import java.util.Iterator;
import org.apache.activemq.broker.region.MessageReference;

public interface PendingList extends Iterable<MessageReference>
{
    boolean isEmpty();
    
    void clear();
    
    PendingNode addMessageFirst(final MessageReference p0);
    
    PendingNode addMessageLast(final MessageReference p0);
    
    PendingNode remove(final MessageReference p0);
    
    int size();
    
    Iterator<MessageReference> iterator();
    
    boolean contains(final MessageReference p0);
    
    Collection<MessageReference> values();
    
    void addAll(final PendingList p0);
    
    MessageReference get(final MessageId p0);
}
