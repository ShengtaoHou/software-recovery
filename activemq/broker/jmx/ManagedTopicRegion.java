// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.JMSException;
import javax.management.ObjectName;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.broker.region.DestinationFactory;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.region.DestinationStatistics;
import org.apache.activemq.broker.region.TopicRegion;

public class ManagedTopicRegion extends TopicRegion
{
    private final ManagedRegionBroker regionBroker;
    
    public ManagedTopicRegion(final ManagedRegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
        this.regionBroker = broker;
    }
    
    @Override
    protected Subscription createSubscription(final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        final Subscription sub = super.createSubscription(context, info);
        final ObjectName name = this.regionBroker.registerSubscription(context, sub);
        sub.setObjectName(name);
        return sub;
    }
    
    @Override
    protected void destroySubscription(final Subscription sub) {
        this.regionBroker.unregisterSubscription(sub);
        super.destroySubscription(sub);
    }
    
    @Override
    protected Destination createDestination(final ConnectionContext context, final ActiveMQDestination destination) throws Exception {
        final Destination rc = super.createDestination(context, destination);
        this.regionBroker.register(destination, rc);
        return rc;
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        super.removeDestination(context, destination, timeout);
        this.regionBroker.unregister(destination);
    }
}
