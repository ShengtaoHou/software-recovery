// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.TopicSubscription;

public class PrefetchRatePendingMessageLimitStrategy implements PendingMessageLimitStrategy
{
    private double multiplier;
    
    public PrefetchRatePendingMessageLimitStrategy() {
        this.multiplier = 0.5;
    }
    
    @Override
    public int getMaximumPendingMessageLimit(final TopicSubscription subscription) {
        final int prefetchSize = subscription.getConsumerInfo().getPrefetchSize();
        return (int)(prefetchSize * this.multiplier);
    }
    
    public double getMultiplier() {
        return this.multiplier;
    }
    
    public void setMultiplier(final double rate) {
        this.multiplier = rate;
    }
}
