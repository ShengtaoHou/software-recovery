// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import javax.jms.Destination;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ActiveMQDestination;

public class ProducerStoppedEvent extends ProducerEvent
{
    private static final long serialVersionUID = 5378835541037193206L;
    
    public ProducerStoppedEvent(final ProducerEventSource source, final ActiveMQDestination destination, final ProducerId consumerId, final int count) {
        super(source, destination, consumerId, count);
    }
    
    @Override
    public boolean isStarted() {
        return false;
    }
}
