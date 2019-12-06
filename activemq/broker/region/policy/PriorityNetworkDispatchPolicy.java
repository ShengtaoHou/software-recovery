// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.activemq.broker.region.Subscription;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.region.MessageReference;
import org.slf4j.Logger;

public class PriorityNetworkDispatchPolicy extends SimpleDispatchPolicy
{
    private static final Logger LOG;
    
    @Override
    public boolean dispatch(final MessageReference node, final MessageEvaluationContext msgContext, final List<Subscription> consumers) throws Exception {
        final List<Subscription> duplicateFreeSubs = new ArrayList<Subscription>();
        synchronized (consumers) {
            for (final Subscription sub : consumers) {
                final ConsumerInfo info = sub.getConsumerInfo();
                if (info.isNetworkSubscription()) {
                    boolean highestPrioritySub = true;
                    final Iterator<Subscription> it = duplicateFreeSubs.iterator();
                    while (it.hasNext()) {
                        final Subscription candidate = it.next();
                        if (this.matches(candidate, info)) {
                            if (this.hasLowerPriority(candidate, info)) {
                                it.remove();
                            }
                            else {
                                highestPrioritySub = false;
                                PriorityNetworkDispatchPolicy.LOG.debug("ignoring lower priority: {} [{}, {}] in favour of: {} [{}, {}]", candidate, candidate.getConsumerInfo().getNetworkConsumerIds(), candidate.getConsumerInfo().getNetworkConsumerIds(), sub, sub.getConsumerInfo().getNetworkConsumerIds(), sub.getConsumerInfo().getNetworkConsumerIds());
                            }
                        }
                    }
                    if (!highestPrioritySub) {
                        continue;
                    }
                    duplicateFreeSubs.add(sub);
                }
                else {
                    duplicateFreeSubs.add(sub);
                }
            }
        }
        return super.dispatch(node, msgContext, duplicateFreeSubs);
    }
    
    private boolean hasLowerPriority(final Subscription candidate, final ConsumerInfo info) {
        return candidate.getConsumerInfo().getPriority() < info.getPriority();
    }
    
    private boolean matches(final Subscription candidate, final ConsumerInfo info) {
        boolean matched = false;
        for (final ConsumerId candidateId : candidate.getConsumerInfo().getNetworkConsumerIds()) {
            for (final ConsumerId subId : info.getNetworkConsumerIds()) {
                if (candidateId.equals(subId)) {
                    matched = true;
                    break;
                }
            }
        }
        return matched;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PriorityNetworkDispatchPolicy.class);
    }
}
