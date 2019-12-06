// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.JMSException;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;

public class TempQueueRegion extends AbstractTempRegion
{
    public TempQueueRegion(final RegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    @Override
    protected Subscription createSubscription(final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        if (info.isBrowser()) {
            return new QueueBrowserSubscription(this.broker, this.usageManager, context, info);
        }
        return new QueueSubscription(this.broker, this.usageManager, context, info);
    }
    
    @Override
    public String toString() {
        return "TempQueueRegion: destinations=" + this.destinations.size() + ", subscriptions=" + this.subscriptions.size() + ", memory=" + this.usageManager.getMemoryUsage().getPercentUsage() + "%";
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, long timeout) throws Exception {
        if (timeout == 0L) {
            timeout = 1L;
        }
        super.removeDestination(context, destination, timeout);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.processDispatchNotificationViaDestination(messageDispatchNotification);
    }
}
