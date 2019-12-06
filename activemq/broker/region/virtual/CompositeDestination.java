// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.Destination;
import java.util.Collection;

public abstract class CompositeDestination implements VirtualDestination
{
    private String name;
    private Collection forwardTo;
    private boolean forwardOnly;
    private boolean copyMessage;
    private boolean concurrentSend;
    
    public CompositeDestination() {
        this.forwardOnly = true;
        this.copyMessage = true;
        this.concurrentSend = false;
    }
    
    @Override
    public Destination intercept(final Destination destination) {
        return new CompositeDestinationFilter(destination, this.getForwardTo(), this.isForwardOnly(), this.isCopyMessage(), this.isConcurrentSend());
    }
    
    @Override
    public void create(final Broker broker, final ConnectionContext context, final ActiveMQDestination destination) {
    }
    
    @Override
    public void remove(final Destination destination) {
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Collection getForwardTo() {
        return this.forwardTo;
    }
    
    public void setForwardTo(final Collection forwardDestinations) {
        this.forwardTo = forwardDestinations;
    }
    
    public boolean isForwardOnly() {
        return this.forwardOnly;
    }
    
    public void setForwardOnly(final boolean forwardOnly) {
        this.forwardOnly = forwardOnly;
    }
    
    public boolean isCopyMessage() {
        return this.copyMessage;
    }
    
    public void setCopyMessage(final boolean copyMessage) {
        this.copyMessage = copyMessage;
    }
    
    public void setConcurrentSend(final boolean concurrentSend) {
        this.concurrentSend = concurrentSend;
    }
    
    public boolean isConcurrentSend() {
        return this.concurrentSend;
    }
}
