// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.apache.activemq.command.ProducerId;
import javax.jms.Destination;
import java.util.EventObject;

public abstract class ProducerEvent extends EventObject
{
    private static final long serialVersionUID = 2442156576867593780L;
    private final Destination destination;
    private final ProducerId producerId;
    private final int producerCount;
    
    public ProducerEvent(final ProducerEventSource source, final Destination destination, final ProducerId producerId, final int producerCount) {
        super(source);
        this.destination = destination;
        this.producerId = producerId;
        this.producerCount = producerCount;
    }
    
    public ProducerEventSource getAdvisor() {
        return (ProducerEventSource)this.getSource();
    }
    
    public Destination getDestination() {
        return this.destination;
    }
    
    public int getProducerCount() {
        return this.producerCount;
    }
    
    public ProducerId getProducerId() {
        return this.producerId;
    }
    
    public abstract boolean isStarted();
}
