// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Message;
import org.slf4j.Logger;

public class DiscardingDeadLetterStrategy extends SharedDeadLetterStrategy
{
    private static final Logger LOG;
    
    @Override
    public boolean isSendToDeadLetterQueue(final Message message) {
        final boolean result = false;
        DiscardingDeadLetterStrategy.LOG.debug("Discarding message sent to DLQ: {}, dest: {}", message.getMessageId(), message.getDestination());
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DiscardingDeadLetterStrategy.class);
    }
}
