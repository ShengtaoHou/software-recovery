// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Subscription;

public interface DispatchSelector
{
    boolean canDispatch(final Subscription p0, final MessageReference p1) throws Exception;
}
