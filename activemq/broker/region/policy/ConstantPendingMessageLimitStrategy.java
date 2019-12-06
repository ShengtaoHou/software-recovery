// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.TopicSubscription;

public class ConstantPendingMessageLimitStrategy implements PendingMessageLimitStrategy
{
    private int limit;
    
    public ConstantPendingMessageLimitStrategy() {
        this.limit = -1;
    }
    
    @Override
    public int getMaximumPendingMessageLimit(final TopicSubscription subscription) {
        return this.limit;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
}
