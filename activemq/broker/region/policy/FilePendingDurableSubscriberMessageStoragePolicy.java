// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.FilePendingMessageCursor;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.cursors.AbstractPendingMessageCursor;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.Broker;

public class FilePendingDurableSubscriberMessageStoragePolicy implements PendingDurableSubscriberMessageStoragePolicy
{
    @Override
    public PendingMessageCursor getSubscriberPendingMessageCursor(final Broker broker, final String clientId, final String name, final int maxBatchSize, final DurableTopicSubscription sub) {
        return new FilePendingMessageCursor(broker, name, AbstractPendingMessageCursor.isPrioritizedMessageSubscriber(broker, sub));
    }
}
