// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.Broker;

public interface PendingDurableSubscriberMessageStoragePolicy
{
    PendingMessageCursor getSubscriberPendingMessageCursor(final Broker p0, final String p1, final String p2, final int p3, final DurableTopicSubscription p4);
}
