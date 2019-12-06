// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.Broker;

public interface PendingSubscriberMessageStoragePolicy
{
    PendingMessageCursor getSubscriberPendingMessageCursor(final Broker p0, final String p1, final int p2, final Subscription p3);
}
