// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.Broker;

public interface PendingQueueMessageStoragePolicy
{
    PendingMessageCursor getQueuePendingMessageCursor(final Broker p0, final Queue p1);
}
