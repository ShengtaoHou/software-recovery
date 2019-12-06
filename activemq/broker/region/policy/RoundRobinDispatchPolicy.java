// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import java.util.Iterator;
import org.apache.activemq.broker.region.Subscription;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.region.MessageReference;

public class RoundRobinDispatchPolicy implements DispatchPolicy
{
    @Override
    public boolean dispatch(final MessageReference node, final MessageEvaluationContext msgContext, final List<Subscription> consumers) throws Exception {
        int count = 0;
        Subscription firstMatchingConsumer = null;
        synchronized (consumers) {
            for (final Subscription sub : consumers) {
                if (!sub.matches(node, msgContext)) {
                    sub.unmatched(node);
                }
                else {
                    if (firstMatchingConsumer == null) {
                        firstMatchingConsumer = sub;
                    }
                    sub.add(node);
                    ++count;
                }
            }
            if (firstMatchingConsumer != null) {
                try {
                    consumers.remove(firstMatchingConsumer);
                    consumers.add(firstMatchingConsumer);
                }
                catch (Throwable t) {}
            }
        }
        return count > 0;
    }
}
