// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.Logger;
import org.apache.activemq.broker.region.policy.SimpleDispatchSelector;

public class QueueDispatchSelector extends SimpleDispatchSelector
{
    private static final Logger LOG;
    private Subscription exclusiveConsumer;
    
    public QueueDispatchSelector(final ActiveMQDestination destination) {
        super(destination);
    }
    
    public Subscription getExclusiveConsumer() {
        return this.exclusiveConsumer;
    }
    
    public void setExclusiveConsumer(final Subscription exclusiveConsumer) {
        this.exclusiveConsumer = exclusiveConsumer;
    }
    
    public boolean isExclusiveConsumer(final Subscription s) {
        return s == this.exclusiveConsumer;
    }
    
    public boolean canSelect(final Subscription subscription, final MessageReference m) throws Exception {
        boolean result = super.canDispatch(subscription, m);
        if (result && !subscription.isBrowser()) {
            result = (this.exclusiveConsumer == null || this.exclusiveConsumer == subscription);
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueueDispatchSelector.class);
    }
}
