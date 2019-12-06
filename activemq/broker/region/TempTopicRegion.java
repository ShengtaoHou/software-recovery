// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Broker;
import javax.jms.JMSException;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.SystemUsage;
import org.slf4j.Logger;

public class TempTopicRegion extends AbstractTempRegion
{
    private static final Logger LOG;
    
    public TempTopicRegion(final RegionBroker broker, final DestinationStatistics destinationStatistics, final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        super(broker, destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    @Override
    protected Subscription createSubscription(final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        if (info.isDurable()) {
            throw new JMSException("A durable subscription cannot be created for a temporary topic.");
        }
        try {
            final TopicSubscription answer = new TopicSubscription(this.broker, context, info, this.usageManager);
            final ActiveMQDestination destination = info.getDestination();
            if (destination != null && this.broker.getDestinationPolicy() != null) {
                final PolicyEntry entry = this.broker.getDestinationPolicy().getEntryFor(destination);
                if (entry != null) {
                    entry.configure(this.broker, this.usageManager, answer);
                }
            }
            answer.init();
            return answer;
        }
        catch (Exception e) {
            TempTopicRegion.LOG.error("Failed to create TopicSubscription ", e);
            final JMSException jmsEx = new JMSException("Couldn't create TopicSubscription");
            jmsEx.setLinkedException(e);
            throw jmsEx;
        }
    }
    
    @Override
    public String toString() {
        return "TempTopicRegion: destinations=" + this.destinations.size() + ", subscriptions=" + this.subscriptions.size() + ", memory=" + this.usageManager.getMemoryUsage().getPercentUsage() + "%";
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, long timeout) throws Exception {
        if (timeout == 0L) {
            timeout = 1L;
        }
        super.removeDestination(context, destination, timeout);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TempTopicRegion.class);
    }
}
