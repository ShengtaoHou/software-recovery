// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.FilePendingMessageCursor;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.Broker;

public class FilePendingQueueMessageStoragePolicy implements PendingQueueMessageStoragePolicy
{
    @Override
    public PendingMessageCursor getQueuePendingMessageCursor(final Broker broker, final Queue queue) {
        return new FilePendingMessageCursor(broker, "PendingCursor:" + queue.getName(), queue.isPrioritizedMessages());
    }
}
