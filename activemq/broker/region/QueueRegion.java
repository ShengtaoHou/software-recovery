// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.MessageDispatchNotification;
import java.util.Iterator;
import java.util.Set;
import javax.jms.JMSException;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;

public class QueueRegion extends AbstractRegion
{
    public QueueRegion(final RegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    @Override
    public String toString() {
        return "QueueRegion: destinations=" + this.destinations.size() + ", subscriptions=" + this.subscriptions.size() + ", memory=" + this.usageManager.getMemoryUsage().getPercentUsage() + "%";
    }
    
    @Override
    protected Subscription createSubscription(final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        final ActiveMQDestination destination = info.getDestination();
        PolicyEntry entry = null;
        if (destination != null && this.broker.getDestinationPolicy() != null) {
            entry = this.broker.getDestinationPolicy().getEntryFor(destination);
        }
        if (info.isBrowser()) {
            final QueueBrowserSubscription sub = new QueueBrowserSubscription(this.broker, this.usageManager, context, info);
            if (entry != null) {
                entry.configure(this.broker, this.usageManager, sub);
            }
            return sub;
        }
        final QueueSubscription sub2 = new QueueSubscription(this.broker, this.usageManager, context, info);
        if (entry != null) {
            entry.configure(this.broker, this.usageManager, sub2);
        }
        return sub2;
    }
    
    @Override
    protected Set<ActiveMQDestination> getInactiveDestinations() {
        final Set<ActiveMQDestination> inactiveDestinations = super.getInactiveDestinations();
        final Iterator<ActiveMQDestination> iter = inactiveDestinations.iterator();
        while (iter.hasNext()) {
            final ActiveMQDestination dest = iter.next();
            if (!dest.isQueue()) {
                iter.remove();
            }
        }
        return inactiveDestinations;
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.processDispatchNotificationViaDestination(messageDispatchNotification);
    }
}
