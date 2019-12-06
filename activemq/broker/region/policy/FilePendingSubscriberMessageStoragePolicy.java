// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.FilePendingMessageCursor;
import org.apache.activemq.broker.region.cursors.AbstractPendingMessageCursor;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.Broker;

public class FilePendingSubscriberMessageStoragePolicy implements PendingSubscriberMessageStoragePolicy
{
    @Override
    public PendingMessageCursor getSubscriberPendingMessageCursor(final Broker broker, final String name, final int maxBatchSize, final Subscription subs) {
        return new FilePendingMessageCursor(broker, "PendingCursor:" + name, AbstractPendingMessageCursor.isPrioritizedMessageSubscriber(broker, subs));
    }
}
