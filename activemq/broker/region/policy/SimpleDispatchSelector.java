// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;

public class SimpleDispatchSelector implements DispatchSelector
{
    private final ActiveMQDestination destination;
    
    public SimpleDispatchSelector(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    @Override
    public boolean canDispatch(final Subscription subscription, final MessageReference node) throws Exception {
        final MessageEvaluationContext msgContext = new NonCachedMessageEvaluationContext();
        msgContext.setDestination(this.destination);
        msgContext.setMessageReference(node);
        return subscription.matches(node, msgContext);
    }
}
