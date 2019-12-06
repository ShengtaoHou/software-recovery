// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.Subscription;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.region.MessageReference;

public interface DispatchPolicy
{
    boolean dispatch(final MessageReference p0, final MessageEvaluationContext p1, final List<Subscription> p2) throws Exception;
}
