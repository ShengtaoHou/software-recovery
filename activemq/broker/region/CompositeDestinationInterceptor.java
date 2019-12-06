// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;

public class CompositeDestinationInterceptor implements DestinationInterceptor
{
    private volatile DestinationInterceptor[] interceptors;
    
    public CompositeDestinationInterceptor(final DestinationInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }
    
    @Override
    public Destination intercept(Destination destination) {
        for (int i = 0; i < this.interceptors.length; ++i) {
            destination = this.interceptors[i].intercept(destination);
        }
        return destination;
    }
    
    @Override
    public void remove(final Destination destination) {
        for (int i = 0; i < this.interceptors.length; ++i) {
            this.interceptors[i].remove(destination);
        }
    }
    
    @Override
    public void create(final Broker broker, final ConnectionContext context, final ActiveMQDestination destination) throws Exception {
        for (int i = 0; i < this.interceptors.length; ++i) {
            this.interceptors[i].create(broker, context, destination);
        }
    }
    
    public void setInterceptors(final DestinationInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }
    
    public DestinationInterceptor[] getInterceptors() {
        return this.interceptors;
    }
}
