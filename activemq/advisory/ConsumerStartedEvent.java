// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerInfo;

public class ConsumerStartedEvent extends ConsumerEvent
{
    private static final long serialVersionUID = 5088138839609391074L;
    private final transient ConsumerInfo consumerInfo;
    
    public ConsumerStartedEvent(final ConsumerEventSource source, final ActiveMQDestination destination, final ConsumerInfo consumerInfo, final int count) {
        super(source, destination, consumerInfo.getConsumerId(), count);
        this.consumerInfo = consumerInfo;
    }
    
    @Override
    public boolean isStarted() {
        return true;
    }
    
    public ConsumerInfo getConsumerInfo() {
        return this.consumerInfo;
    }
}
