// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationInterceptor;

public class AuthorizationDestinationInterceptor implements DestinationInterceptor
{
    private final AuthorizationBroker broker;
    
    public AuthorizationDestinationInterceptor(final AuthorizationBroker broker) {
        this.broker = broker;
    }
    
    @Override
    public Destination intercept(final Destination destination) {
        return new AuthorizationDestinationFilter(destination, this.broker);
    }
    
    @Override
    public void remove(final Destination destination) {
    }
    
    @Override
    public void create(final Broker broker, final ConnectionContext context, final ActiveMQDestination destination) throws Exception {
    }
}
