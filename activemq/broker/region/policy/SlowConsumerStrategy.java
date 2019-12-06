// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.ConnectionContext;

public interface SlowConsumerStrategy
{
    void slowConsumer(final ConnectionContext p0, final Subscription p1);
    
    void setBrokerService(final Broker p0);
    
    void addDestination(final Destination p0);
}
