// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import javax.jms.Destination;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ActiveMQDestination;

public class ConsumerStoppedEvent extends ConsumerEvent
{
    private static final long serialVersionUID = 5378835541037193206L;
    
    public ConsumerStoppedEvent(final ConsumerEventSource source, final ActiveMQDestination destination, final ConsumerId consumerId, final int count) {
        super(source, destination, consumerId, count);
    }
    
    @Override
    public boolean isStarted() {
        return false;
    }
}
