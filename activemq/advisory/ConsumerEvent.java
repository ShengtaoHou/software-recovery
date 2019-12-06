// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.apache.activemq.command.ConsumerId;
import javax.jms.Destination;
import java.util.EventObject;

public abstract class ConsumerEvent extends EventObject
{
    private static final long serialVersionUID = 2442156576867593780L;
    private final Destination destination;
    private final ConsumerId consumerId;
    private final int consumerCount;
    
    public ConsumerEvent(final ConsumerEventSource source, final Destination destination, final ConsumerId consumerId, final int consumerCount) {
        super(source);
        this.destination = destination;
        this.consumerId = consumerId;
        this.consumerCount = consumerCount;
    }
    
    public ConsumerEventSource getAdvisor() {
        return (ConsumerEventSource)this.getSource();
    }
    
    public Destination getDestination() {
        return this.destination;
    }
    
    public int getConsumerCount() {
        return this.consumerCount;
    }
    
    public ConsumerId getConsumerId() {
        return this.consumerId;
    }
    
    public abstract boolean isStarted();
}
