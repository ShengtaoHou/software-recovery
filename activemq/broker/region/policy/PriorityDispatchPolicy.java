// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;
import java.util.Comparator;

public class PriorityDispatchPolicy extends SimpleDispatchPolicy
{
    private final Comparator<? super Subscription> orderedCompare;
    
    public PriorityDispatchPolicy() {
        this.orderedCompare = new Comparator<Subscription>() {
            @Override
            public int compare(final Subscription o1, final Subscription o2) {
                return o2.getConsumerInfo().getPriority() - o1.getConsumerInfo().getPriority();
            }
        };
    }
    
    @Override
    public boolean dispatch(final MessageReference node, final MessageEvaluationContext msgContext, final List<Subscription> consumers) throws Exception {
        final ArrayList<Subscription> ordered = new ArrayList<Subscription>(consumers);
        Collections.sort(ordered, this.orderedCompare);
        final StringBuffer stringBuffer = new StringBuffer();
        for (final Subscription sub : ordered) {
            stringBuffer.append(sub.getConsumerInfo().getPriority());
            stringBuffer.append(',');
        }
        return super.dispatch(node, msgContext, ordered);
    }
}
