// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.region.policy.AbortSlowConsumerStrategy;
import org.apache.activemq.broker.region.policy.AbortSlowAckConsumerStrategy;

public class AbortSlowAckConsumerStrategyView extends AbortSlowConsumerStrategyView implements AbortSlowAckConsumerStrategyViewMBean
{
    private final AbortSlowAckConsumerStrategy strategy;
    
    public AbortSlowAckConsumerStrategyView(final ManagedRegionBroker managedRegionBroker, final AbortSlowAckConsumerStrategy slowConsumerStrategy) {
        super(managedRegionBroker, slowConsumerStrategy);
        this.strategy = slowConsumerStrategy;
    }
    
    @Override
    public long getMaxTimeSinceLastAck() {
        return this.strategy.getMaxTimeSinceLastAck();
    }
    
    @Override
    public void setMaxTimeSinceLastAck(final long maxTimeSinceLastAck) {
        this.strategy.setMaxTimeSinceLastAck(maxTimeSinceLastAck);
    }
    
    @Override
    public boolean isIgnoreIdleConsumers() {
        return this.strategy.isIgnoreIdleConsumers();
    }
    
    @Override
    public void setIgnoreIdleConsumers(final boolean ignoreIdleConsumers) {
        this.strategy.setIgnoreIdleConsumers(ignoreIdleConsumers);
    }
    
    @Override
    public boolean isIgnoreNetworkConsumers() {
        return this.strategy.isIgnoreNetworkSubscriptions();
    }
    
    @Override
    public void setIgnoreNetworkConsumers(final boolean ignoreNetworkConsumers) {
        this.strategy.setIgnoreNetworkConsumers(ignoreNetworkConsumers);
    }
}
