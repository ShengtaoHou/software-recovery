// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.StoreDurableSubscriberCursor;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.Broker;

public class StorePendingDurableSubscriberMessageStoragePolicy implements PendingDurableSubscriberMessageStoragePolicy
{
    boolean immediatePriorityDispatch;
    boolean useCache;
    
    public StorePendingDurableSubscriberMessageStoragePolicy() {
        this.immediatePriorityDispatch = true;
        this.useCache = true;
    }
    
    public boolean isImmediatePriorityDispatch() {
        return this.immediatePriorityDispatch;
    }
    
    public void setImmediatePriorityDispatch(final boolean immediatePriorityDispatch) {
        this.immediatePriorityDispatch = immediatePriorityDispatch;
    }
    
    public boolean isUseCache() {
        return this.useCache;
    }
    
    public void setUseCache(final boolean useCache) {
        this.useCache = useCache;
    }
    
    @Override
    public PendingMessageCursor getSubscriberPendingMessageCursor(final Broker broker, final String clientId, final String name, final int maxBatchSize, final DurableTopicSubscription sub) {
        final StoreDurableSubscriberCursor cursor = new StoreDurableSubscriberCursor(broker, clientId, name, maxBatchSize, sub);
        cursor.setUseCache(this.isUseCache());
        cursor.setImmediatePriorityDispatch(this.isImmediatePriorityDispatch());
        return cursor;
    }
}
