// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;

public abstract class DestinationMapEntry<T> implements Comparable<T>
{
    protected ActiveMQDestination destination;
    
    @Override
    public int compareTo(final Object that) {
        if (that instanceof DestinationMapEntry) {
            final DestinationMapEntry<?> thatEntry = (DestinationMapEntry<?>)that;
            return ActiveMQDestination.compare(this.destination, thatEntry.destination);
        }
        if (that == null) {
            return 1;
        }
        return this.getClass().getName().compareTo(that.getClass().getName());
    }
    
    public void setQueue(final String name) {
        this.setDestination(new ActiveMQQueue(name));
    }
    
    public void setTopic(final String name) {
        this.setDestination(new ActiveMQTopic(name));
    }
    
    public void setTempTopic(final boolean flag) {
        this.setDestination(new ActiveMQTempTopic(">"));
    }
    
    public void setTempQueue(final boolean flag) {
        this.setDestination(new ActiveMQTempQueue(">"));
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public Comparable<T> getValue() {
        return this;
    }
}
