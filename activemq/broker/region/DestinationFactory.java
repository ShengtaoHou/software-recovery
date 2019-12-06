// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import java.io.IOException;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.command.ActiveMQTopic;
import java.util.Set;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;

public abstract class DestinationFactory
{
    public abstract Destination createDestination(final ConnectionContext p0, final ActiveMQDestination p1, final DestinationStatistics p2) throws Exception;
    
    public abstract void removeDestination(final Destination p0);
    
    public abstract Set<ActiveMQDestination> getDestinations();
    
    public abstract SubscriptionInfo[] getAllDurableSubscriptions(final ActiveMQTopic p0) throws IOException;
    
    public abstract long getLastMessageBrokerSequenceId() throws IOException;
    
    public abstract void setRegionBroker(final RegionBroker p0);
}
