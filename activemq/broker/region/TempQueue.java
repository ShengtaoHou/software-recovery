// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.Task;
import org.apache.activemq.broker.region.cursors.VMPendingMessageCursor;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.slf4j.Logger;

public class TempQueue extends Queue
{
    private static final Logger LOG;
    private final ActiveMQTempDestination tempDest;
    
    public TempQueue(final BrokerService brokerService, final ActiveMQDestination destination, final MessageStore store, final DestinationStatistics parentStats, final TaskRunnerFactory taskFactory) throws Exception {
        super(brokerService, destination, store, parentStats, taskFactory);
        this.tempDest = (ActiveMQTempDestination)destination;
    }
    
    @Override
    public void initialize() throws Exception {
        (this.messages = new VMPendingMessageCursor(false)).setMemoryUsageHighWaterMark(this.getCursorMemoryHighWaterMark());
        this.systemUsage = this.brokerService.getSystemUsage();
        this.memoryUsage.setParent(this.systemUsage.getMemoryUsage());
        this.taskRunner = this.taskFactory.createTaskRunner(this, "TempQueue:  " + this.destination.getPhysicalName());
    }
    
    @Override
    public void addSubscription(final ConnectionContext context, final Subscription sub) throws Exception {
        if (!context.isFaultTolerant() && !context.isNetworkConnection() && !this.tempDest.getConnectionId().equals(sub.getConsumerInfo().getConsumerId().getConnectionId())) {
            this.tempDest.setConnectionId(sub.getConsumerInfo().getConsumerId().getConnectionId());
            TempQueue.LOG.debug("changed ownership of {} to {}", this, this.tempDest.getConnectionId());
        }
        super.addSubscription(context, sub);
    }
    
    @Override
    public void dispose(final ConnectionContext context) throws IOException {
        if (this.destinationStatistics.getMessages().getCount() > 0L) {
            TempQueue.LOG.info("{} on dispose, purge of {} pending messages: {}", this.getActiveMQDestination().getQualifiedName(), this.destinationStatistics.getMessages().getCount(), this.messages);
        }
        try {
            this.purge();
        }
        catch (Exception e) {
            TempQueue.LOG.warn("Caught an exception purging Queue: {}", this.destination, e);
        }
        super.dispose(context);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TempQueue.class);
    }
}
