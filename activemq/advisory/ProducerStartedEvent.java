// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;

public class ProducerStartedEvent extends ProducerEvent
{
    private static final long serialVersionUID = 5088138839609391074L;
    private final transient ProducerInfo consumerInfo;
    
    public ProducerStartedEvent(final ProducerEventSource source, final ActiveMQDestination destination, final ProducerInfo consumerInfo, final int count) {
        super(source, destination, consumerInfo.getProducerId(), count);
        this.consumerInfo = consumerInfo;
    }
    
    @Override
    public boolean isStarted() {
        return true;
    }
    
    public ProducerInfo getProducerInfo() {
        return this.consumerInfo;
    }
}
