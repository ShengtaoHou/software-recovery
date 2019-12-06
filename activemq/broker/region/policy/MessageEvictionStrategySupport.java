// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

public abstract class MessageEvictionStrategySupport implements MessageEvictionStrategy
{
    private int evictExpiredMessagesHighWatermark;
    
    public MessageEvictionStrategySupport() {
        this.evictExpiredMessagesHighWatermark = 1000;
    }
    
    @Override
    public int getEvictExpiredMessagesHighWatermark() {
        return this.evictExpiredMessagesHighWatermark;
    }
    
    public void setEvictExpiredMessagesHighWatermark(final int evictExpiredMessagesHighWaterMark) {
        this.evictExpiredMessagesHighWatermark = evictExpiredMessagesHighWaterMark;
    }
}
