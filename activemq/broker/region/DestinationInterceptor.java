// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;

public interface DestinationInterceptor
{
    Destination intercept(final Destination p0);
    
    void remove(final Destination p0);
    
    void create(final Broker p0, final ConnectionContext p1, final ActiveMQDestination p2) throws Exception;
}
