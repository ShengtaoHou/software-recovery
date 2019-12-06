// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.plugin;

import org.slf4j.LoggerFactory;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.MessageId;
import javax.jms.JMSException;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.broker.ConnectionContext;
import javax.management.ObjectName;
import java.io.File;
import java.net.URI;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.region.DestinationStatistics;
import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class StatisticsBroker extends BrokerFilter
{
    private static Logger LOG;
    static final String STATS_DESTINATION_PREFIX = "ActiveMQ.Statistics.Destination";
    static final String STATS_BROKER_PREFIX = "ActiveMQ.Statistics.Broker";
    static final String STATS_BROKER_RESET_HEADER = "ActiveMQ.Statistics.Broker.Reset";
    static final String STATS_SUBSCRIPTION_PREFIX = "ActiveMQ.Statistics.Subscription";
    static final String STATS_DENOTE_END_LIST = "ActiveMQ.Statistics.Destination.List.End.With.Null";
    private static final IdGenerator ID_GENERATOR;
    private final LongSequenceGenerator messageIdGenerator;
    protected final ProducerId advisoryProducerId;
    protected BrokerViewMBean brokerView;
    
    public StatisticsBroker(final Broker next) {
        super(next);
        this.messageIdGenerator = new LongSequenceGenerator();
        (this.advisoryProducerId = new ProducerId()).setConnectionId(StatisticsBroker.ID_GENERATOR.generateId());
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        final ActiveMQDestination msgDest = messageSend.getDestination();
        final ActiveMQDestination replyTo = messageSend.getReplyTo();
        if (replyTo != null) {
            final String physicalName = msgDest.getPhysicalName();
            final boolean destStats = physicalName.regionMatches(true, 0, "ActiveMQ.Statistics.Destination", 0, "ActiveMQ.Statistics.Destination".length());
            final boolean brokerStats = physicalName.regionMatches(true, 0, "ActiveMQ.Statistics.Broker", 0, "ActiveMQ.Statistics.Broker".length());
            final boolean subStats = physicalName.regionMatches(true, 0, "ActiveMQ.Statistics.Subscription", 0, "ActiveMQ.Statistics.Subscription".length());
            final BrokerService brokerService = this.getBrokerService();
            final RegionBroker regionBroker = (RegionBroker)brokerService.getRegionBroker();
            if (destStats) {
                String destinationName = physicalName.substring("ActiveMQ.Statistics.Destination".length(), physicalName.length());
                if (destinationName.startsWith(".")) {
                    destinationName = destinationName.substring(1);
                }
                final String destinationQuery = destinationName.replace("ActiveMQ.Statistics.Destination.List.End.With.Null", "");
                final boolean endListMessage = !destinationName.equals(destinationQuery);
                final ActiveMQDestination queryDestination = ActiveMQDestination.createDestination(destinationQuery, msgDest.getDestinationType());
                final Set<Destination> destinations = this.getDestinations(queryDestination);
                for (final Destination dest : destinations) {
                    final DestinationStatistics stats = dest.getDestinationStatistics();
                    if (stats != null) {
                        final ActiveMQMapMessage statsMessage = new ActiveMQMapMessage();
                        statsMessage.setString("brokerName", regionBroker.getBrokerName());
                        statsMessage.setString("brokerId", regionBroker.getBrokerId().toString());
                        statsMessage.setString("destinationName", dest.getActiveMQDestination().toString());
                        statsMessage.setLong("size", stats.getMessages().getCount());
                        statsMessage.setLong("enqueueCount", stats.getEnqueues().getCount());
                        statsMessage.setLong("dequeueCount", stats.getDequeues().getCount());
                        statsMessage.setLong("dispatchCount", stats.getDispatched().getCount());
                        statsMessage.setLong("expiredCount", stats.getExpired().getCount());
                        statsMessage.setLong("inflightCount", stats.getInflight().getCount());
                        statsMessage.setLong("messagesCached", stats.getMessagesCached().getCount());
                        statsMessage.setDouble("averageMessageSize", stats.getMessageSize().getAveragePerSecond());
                        statsMessage.setInt("memoryPercentUsage", dest.getMemoryUsage().getPercentUsage());
                        statsMessage.setLong("memoryUsage", dest.getMemoryUsage().getUsage());
                        statsMessage.setLong("memoryLimit", dest.getMemoryUsage().getLimit());
                        statsMessage.setDouble("averageEnqueueTime", stats.getProcessTime().getAverageTime());
                        statsMessage.setDouble("maxEnqueueTime", (double)stats.getProcessTime().getMaxTime());
                        statsMessage.setDouble("minEnqueueTime", (double)stats.getProcessTime().getMinTime());
                        statsMessage.setLong("consumerCount", stats.getConsumers().getCount());
                        statsMessage.setLong("producerCount", stats.getProducers().getCount());
                        statsMessage.setJMSCorrelationID(messageSend.getCorrelationId());
                        this.sendStats(producerExchange.getConnectionContext(), statsMessage, replyTo);
                    }
                }
                if (endListMessage) {
                    final ActiveMQMapMessage statsMessage2 = new ActiveMQMapMessage();
                    statsMessage2.setJMSCorrelationID(messageSend.getCorrelationId());
                    this.sendStats(producerExchange.getConnectionContext(), statsMessage2, replyTo);
                }
            }
            else if (subStats) {
                this.sendSubStats(producerExchange.getConnectionContext(), this.getBrokerView().getQueueSubscribers(), replyTo);
                this.sendSubStats(producerExchange.getConnectionContext(), this.getBrokerView().getTopicSubscribers(), replyTo);
            }
            else if (brokerStats) {
                if (messageSend.getProperties().containsKey("ActiveMQ.Statistics.Broker.Reset")) {
                    this.getBrokerView().resetStatistics();
                }
                final ActiveMQMapMessage statsMessage3 = new ActiveMQMapMessage();
                final SystemUsage systemUsage = brokerService.getSystemUsage();
                final DestinationStatistics stats2 = regionBroker.getDestinationStatistics();
                statsMessage3.setString("brokerName", regionBroker.getBrokerName());
                statsMessage3.setString("brokerId", regionBroker.getBrokerId().toString());
                statsMessage3.setLong("size", stats2.getMessages().getCount());
                statsMessage3.setLong("enqueueCount", stats2.getEnqueues().getCount());
                statsMessage3.setLong("dequeueCount", stats2.getDequeues().getCount());
                statsMessage3.setLong("dispatchCount", stats2.getDispatched().getCount());
                statsMessage3.setLong("expiredCount", stats2.getExpired().getCount());
                statsMessage3.setLong("inflightCount", stats2.getInflight().getCount());
                statsMessage3.setDouble("averageMessageSize", stats2.getMessageSize().getAverageSize());
                statsMessage3.setLong("messagesCached", stats2.getMessagesCached().getCount());
                statsMessage3.setInt("memoryPercentUsage", systemUsage.getMemoryUsage().getPercentUsage());
                statsMessage3.setLong("memoryUsage", systemUsage.getMemoryUsage().getUsage());
                statsMessage3.setLong("memoryLimit", systemUsage.getMemoryUsage().getLimit());
                statsMessage3.setInt("storePercentUsage", systemUsage.getStoreUsage().getPercentUsage());
                statsMessage3.setLong("storeUsage", systemUsage.getStoreUsage().getUsage());
                statsMessage3.setLong("storeLimit", systemUsage.getStoreUsage().getLimit());
                statsMessage3.setInt("tempPercentUsage", systemUsage.getTempUsage().getPercentUsage());
                statsMessage3.setLong("tempUsage", systemUsage.getTempUsage().getUsage());
                statsMessage3.setLong("tempLimit", systemUsage.getTempUsage().getLimit());
                statsMessage3.setDouble("averageEnqueueTime", stats2.getProcessTime().getAverageTime());
                statsMessage3.setDouble("maxEnqueueTime", (double)stats2.getProcessTime().getMaxTime());
                statsMessage3.setDouble("minEnqueueTime", (double)stats2.getProcessTime().getMinTime());
                statsMessage3.setLong("consumerCount", stats2.getConsumers().getCount());
                statsMessage3.setLong("producerCount", stats2.getProducers().getCount());
                String answer = brokerService.getTransportConnectorURIsAsMap().get("tcp");
                answer = ((answer != null) ? answer : "");
                statsMessage3.setString("openwire", answer);
                answer = brokerService.getTransportConnectorURIsAsMap().get("stomp");
                answer = ((answer != null) ? answer : "");
                statsMessage3.setString("stomp", answer);
                answer = brokerService.getTransportConnectorURIsAsMap().get("ssl");
                answer = ((answer != null) ? answer : "");
                statsMessage3.setString("ssl", answer);
                answer = brokerService.getTransportConnectorURIsAsMap().get("stomp+ssl");
                answer = ((answer != null) ? answer : "");
                statsMessage3.setString("stomp+ssl", answer);
                final URI uri = brokerService.getVmConnectorURI();
                answer = ((uri != null) ? uri.toString() : "");
                statsMessage3.setString("vm", answer);
                final File file = brokerService.getDataDirectoryFile();
                answer = ((file != null) ? file.getCanonicalPath() : "");
                statsMessage3.setString("dataDirectory", answer);
                statsMessage3.setJMSCorrelationID(messageSend.getCorrelationId());
                this.sendStats(producerExchange.getConnectionContext(), statsMessage3, replyTo);
            }
            else {
                super.send(producerExchange, messageSend);
            }
        }
        else {
            super.send(producerExchange, messageSend);
        }
    }
    
    BrokerViewMBean getBrokerView() throws Exception {
        if (this.brokerView == null) {
            final ObjectName brokerName = this.getBrokerService().getBrokerObjectName();
            this.brokerView = (BrokerViewMBean)this.getBrokerService().getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true);
        }
        return this.brokerView;
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        StatisticsBroker.LOG.info("Starting StatisticsBroker");
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
    }
    
    protected void sendSubStats(final ConnectionContext context, final ObjectName[] subscribers, final ActiveMQDestination replyTo) throws Exception {
        for (int i = 0; i < subscribers.length; ++i) {
            final ObjectName name = subscribers[i];
            final SubscriptionViewMBean subscriber = (SubscriptionViewMBean)this.getBrokerService().getManagementContext().newProxyInstance(name, SubscriptionViewMBean.class, true);
            final ActiveMQMapMessage statsMessage = this.prepareSubscriptionMessage(subscriber);
            this.sendStats(context, statsMessage, replyTo);
        }
    }
    
    protected ActiveMQMapMessage prepareSubscriptionMessage(final SubscriptionViewMBean subscriber) throws JMSException {
        final Broker regionBroker = this.getBrokerService().getRegionBroker();
        final ActiveMQMapMessage statsMessage = new ActiveMQMapMessage();
        statsMessage.setString("brokerName", regionBroker.getBrokerName());
        statsMessage.setString("brokerId", regionBroker.getBrokerId().toString());
        statsMessage.setString("destinationName", subscriber.getDestinationName());
        statsMessage.setString("clientId", subscriber.getClientId());
        statsMessage.setString("connectionId", subscriber.getConnectionId());
        statsMessage.setLong("sessionId", subscriber.getSessionId());
        statsMessage.setString("selector", subscriber.getSelector());
        statsMessage.setLong("enqueueCounter", subscriber.getEnqueueCounter());
        statsMessage.setLong("dequeueCounter", subscriber.getDequeueCounter());
        statsMessage.setLong("dispatchedCounter", subscriber.getDispatchedCounter());
        statsMessage.setLong("dispatchedQueueSize", subscriber.getDispatchedQueueSize());
        statsMessage.setInt("prefetchSize", subscriber.getPrefetchSize());
        statsMessage.setInt("maximumPendingMessageLimit", subscriber.getMaximumPendingMessageLimit());
        statsMessage.setBoolean("exclusive", subscriber.isExclusive());
        statsMessage.setBoolean("retroactive", subscriber.isRetroactive());
        statsMessage.setBoolean("slowConsumer", subscriber.isSlowConsumer());
        return statsMessage;
    }
    
    protected void sendStats(final ConnectionContext context, final ActiveMQMapMessage msg, final ActiveMQDestination replyTo) throws Exception {
        msg.setPersistent(false);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPriority((byte)4);
        msg.setType("Advisory");
        msg.setMessageId(new MessageId(this.advisoryProducerId, this.messageIdGenerator.getNextSequenceId()));
        msg.setDestination(replyTo);
        msg.setResponseRequired(false);
        msg.setProducerId(this.advisoryProducerId);
        final boolean originalFlowControl = context.isProducerFlowControl();
        final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
        producerExchange.setConnectionContext(context);
        producerExchange.setMutable(true);
        producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
        try {
            context.setProducerFlowControl(false);
            this.next.send(producerExchange, msg);
        }
        finally {
            context.setProducerFlowControl(originalFlowControl);
        }
    }
    
    static {
        StatisticsBroker.LOG = LoggerFactory.getLogger(StatisticsBroker.class);
        ID_GENERATOR = new IdGenerator();
    }
}
