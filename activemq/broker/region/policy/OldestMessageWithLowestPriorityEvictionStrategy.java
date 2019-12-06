// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.broker.region.MessageReference;
import java.util.LinkedList;

public class OldestMessageWithLowestPriorityEvictionStrategy extends MessageEvictionStrategySupport
{
    @Override
    public MessageReference[] evictMessages(final LinkedList messages) throws IOException {
        byte lowestPriority = 127;
        int pivot = 0;
        final Iterator iter = messages.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final MessageReference reference = iter.next();
            final byte priority = reference.getMessage().getPriority();
            if (priority < lowestPriority) {
                lowestPriority = priority;
                pivot = i;
            }
            ++i;
        }
        return new MessageReference[] { messages.remove(pivot) };
    }
}
