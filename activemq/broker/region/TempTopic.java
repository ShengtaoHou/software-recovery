// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.activemq.thread.Task;

public class TempTopic extends Topic implements Task
{
    private final ActiveMQTempDestination tempDest;
    
    public TempTopic(final BrokerService brokerService, final ActiveMQDestination destination, final TopicMessageStore store, final DestinationStatistics parentStats, final TaskRunnerFactory taskFactory) throws Exception {
        super(brokerService, destination, store, parentStats, taskFactory);
        this.tempDest = (ActiveMQTempDestination)destination;
    }
    
    @Override
    public void addSubscription(final ConnectionContext context, final Subscription sub) throws Exception {
        if (!context.isFaultTolerant() && !context.isNetworkConnection() && !this.tempDest.getConnectionId().equals(sub.getConsumerInfo().getConsumerId().getConnectionId())) {
            this.tempDest.setConnectionId(sub.getConsumerInfo().getConsumerId().getConnectionId());
            if (TempTopic.LOG.isDebugEnabled()) {
                TempTopic.LOG.debug(" changed ownership of " + this + " to " + this.tempDest.getConnectionId());
            }
        }
        super.addSubscription(context, sub);
    }
    
    @Override
    public void initialize() {
    }
}
