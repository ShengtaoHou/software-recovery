// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public class OldestMessageEvictionStrategy extends MessageEvictionStrategySupport
{
    @Override
    public MessageReference[] evictMessages(final LinkedList messages) {
        return new MessageReference[] { messages.removeFirst() };
    }
}
