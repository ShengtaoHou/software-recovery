// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.util.LinkedNode;

public class PendingNode extends LinkedNode
{
    private final MessageReference message;
    private final OrderedPendingList list;
    
    public PendingNode(final OrderedPendingList list, final MessageReference message) {
        this.list = list;
        this.message = message;
    }
    
    MessageReference getMessage() {
        return this.message;
    }
    
    OrderedPendingList getList() {
        return this.list;
    }
    
    @Override
    public String toString() {
        final PendingNode n = (PendingNode)this.getNext();
        String str = "PendingNode(";
        str = str + System.identityHashCode(this) + "),root=" + this.isHeadNode() + ",next=" + ((n != null) ? Integer.valueOf(System.identityHashCode(n)) : "NULL");
        return str;
    }
}
