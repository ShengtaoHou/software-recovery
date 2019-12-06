// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.SubscriptionInfo;
import java.io.IOException;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQTempDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.thread.TaskRunnerFactory;

public class DestinationFactoryImpl extends DestinationFactory
{
    protected final TaskRunnerFactory taskRunnerFactory;
    protected final PersistenceAdapter persistenceAdapter;
    protected RegionBroker broker;
    private final BrokerService brokerService;
    
    public DestinationFactoryImpl(final BrokerService brokerService, final TaskRunnerFactory taskRunnerFactory, final PersistenceAdapter persistenceAdapter) {
        this.brokerService = brokerService;
        this.taskRunnerFactory = taskRunnerFactory;
        if (persistenceAdapter == null) {
            throw new IllegalArgumentException("null persistenceAdapter");
        }
        this.persistenceAdapter = persistenceAdapter;
    }
    
    @Override
    public void setRegionBroker(final RegionBroker broker) {
        if (broker == null) {
            throw new IllegalArgumentException("null broker");
        }
        this.broker = broker;
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        return this.persistenceAdapter.getDestinations();
    }
    
    @Override
    public Destination createDestination(final ConnectionContext context, final ActiveMQDestination destination, final DestinationStatistics destinationStatistics) throws Exception {
        if (destination.isQueue()) {
            if (destination.isTemporary()) {
                final ActiveMQTempDestination tempDest = (ActiveMQTempDestination)destination;
                final Queue queue = new TempQueue(this.brokerService, destination, null, destinationStatistics, this.taskRunnerFactory);
                this.configureQueue(queue, destination);
                queue.initialize();
                return queue;
            }
            final MessageStore store = this.persistenceAdapter.createQueueMessageStore((ActiveMQQueue)destination);
            final Queue queue = new Queue(this.brokerService, destination, store, destinationStatistics, this.taskRunnerFactory);
            this.configureQueue(queue, destination);
            queue.initialize();
            return queue;
        }
        else {
            if (destination.isTemporary()) {
                final Topic topic = new Topic(this.brokerService, destination, null, destinationStatistics, this.taskRunnerFactory);
                this.configureTopic(topic, destination);
                topic.initialize();
                return topic;
            }
            TopicMessageStore store2 = null;
            if (!AdvisorySupport.isAdvisoryTopic(destination)) {
                store2 = this.persistenceAdapter.createTopicMessageStore((ActiveMQTopic)destination);
            }
            final Topic topic2 = new Topic(this.brokerService, destination, store2, destinationStatistics, this.taskRunnerFactory);
            this.configureTopic(topic2, destination);
            topic2.initialize();
            return topic2;
        }
    }
    
    @Override
    public void removeDestination(final Destination dest) {
        final ActiveMQDestination destination = dest.getActiveMQDestination();
        if (!destination.isTemporary()) {
            if (destination.isQueue()) {
                this.persistenceAdapter.removeQueueMessageStore((ActiveMQQueue)destination);
            }
            else if (!AdvisorySupport.isAdvisoryTopic(destination)) {
                this.persistenceAdapter.removeTopicMessageStore((ActiveMQTopic)destination);
            }
        }
    }
    
    protected void configureQueue(final Queue queue, final ActiveMQDestination destination) {
        if (this.broker == null) {
            throw new IllegalStateException("broker property is not set");
        }
        if (this.broker.getDestinationPolicy() != null) {
            final PolicyEntry entry = this.broker.getDestinationPolicy().getEntryFor(destination);
            if (entry != null) {
                entry.configure(this.broker, queue);
            }
        }
    }
    
    protected void configureTopic(final Topic topic, final ActiveMQDestination destination) {
        if (this.broker == null) {
            throw new IllegalStateException("broker property is not set");
        }
        if (this.broker.getDestinationPolicy() != null) {
            final PolicyEntry entry = this.broker.getDestinationPolicy().getEntryFor(destination);
            if (entry != null) {
                entry.configure(this.broker, topic);
            }
        }
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return this.persistenceAdapter.getLastMessageBrokerSequenceId();
    }
    
    public PersistenceAdapter getPersistenceAdapter() {
        return this.persistenceAdapter;
    }
    
    @Override
    public SubscriptionInfo[] getAllDurableSubscriptions(final ActiveMQTopic topic) throws IOException {
        return this.persistenceAdapter.createTopicMessageStore(topic).getAllSubscriptions();
    }
}
