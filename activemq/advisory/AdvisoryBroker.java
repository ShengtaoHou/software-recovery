// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.slf4j.LoggerFactory;
import java.util.Map;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.DataStructure;
import java.util.Set;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.broker.region.TopicSubscription;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.TopicRegion;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.util.SubscriptionKey;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.broker.region.Destination;
import java.util.Iterator;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.Command;
import org.apache.activemq.broker.ConnectionContext;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerInfo;
import java.util.Queue;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConnectionId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerFilter;

public class AdvisoryBroker extends BrokerFilter
{
    private static final Logger LOG;
    private static final IdGenerator ID_GENERATOR;
    protected final ConcurrentHashMap<ConnectionId, ConnectionInfo> connections;
    protected final Queue<ConsumerInfo> consumers;
    protected final ConcurrentHashMap<ProducerId, ProducerInfo> producers;
    protected final ConcurrentHashMap<ActiveMQDestination, DestinationInfo> destinations;
    protected final ConcurrentHashMap<BrokerInfo, ActiveMQMessage> networkBridges;
    protected final ProducerId advisoryProducerId;
    private final LongSequenceGenerator messageIdGenerator;
    
    public AdvisoryBroker(final Broker next) {
        super(next);
        this.connections = new ConcurrentHashMap<ConnectionId, ConnectionInfo>();
        this.consumers = new ConcurrentLinkedQueue<ConsumerInfo>();
        this.producers = new ConcurrentHashMap<ProducerId, ProducerInfo>();
        this.destinations = new ConcurrentHashMap<ActiveMQDestination, DestinationInfo>();
        this.networkBridges = new ConcurrentHashMap<BrokerInfo, ActiveMQMessage>();
        this.advisoryProducerId = new ProducerId();
        this.messageIdGenerator = new LongSequenceGenerator();
        this.advisoryProducerId.setConnectionId(AdvisoryBroker.ID_GENERATOR.generateId());
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        super.addConnection(context, info);
        final ActiveMQTopic topic = AdvisorySupport.getConnectionAdvisoryTopic();
        final ConnectionInfo copy = info.copy();
        copy.setPassword("");
        this.fireAdvisory(context, topic, copy);
        this.connections.put(copy.getConnectionId(), copy);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final Subscription answer = super.addConsumer(context, info);
        if (!AdvisorySupport.isAdvisoryTopic(info.getDestination())) {
            final ActiveMQTopic topic = AdvisorySupport.getConsumerAdvisoryTopic(info.getDestination());
            this.consumers.offer(info);
            this.fireConsumerAdvisory(context, info.getDestination(), topic, info);
        }
        else {
            if (AdvisorySupport.isConnectionAdvisoryTopic(info.getDestination())) {
                for (final ConnectionInfo value : this.connections.values()) {
                    final ActiveMQTopic topic2 = AdvisorySupport.getConnectionAdvisoryTopic();
                    this.fireAdvisory(context, topic2, value, info.getConsumerId());
                }
            }
            if (AdvisorySupport.isTempDestinationAdvisoryTopic(info.getDestination())) {
                for (final DestinationInfo destination : this.destinations.values()) {
                    if (destination.getDestination().isTemporary()) {
                        final ActiveMQTopic topic2 = AdvisorySupport.getDestinationAdvisoryTopic(destination.getDestination());
                        this.fireAdvisory(context, topic2, destination, info.getConsumerId());
                    }
                }
            }
            else if (AdvisorySupport.isDestinationAdvisoryTopic(info.getDestination())) {
                for (final DestinationInfo destination : this.destinations.values()) {
                    final ActiveMQTopic topic2 = AdvisorySupport.getDestinationAdvisoryTopic(destination.getDestination());
                    this.fireAdvisory(context, topic2, destination, info.getConsumerId());
                }
            }
            if (AdvisorySupport.isProducerAdvisoryTopic(info.getDestination())) {
                for (final ProducerInfo value2 : this.producers.values()) {
                    final ActiveMQTopic topic2 = AdvisorySupport.getProducerAdvisoryTopic(value2.getDestination());
                    this.fireProducerAdvisory(context, value2.getDestination(), topic2, value2, info.getConsumerId());
                }
            }
            if (AdvisorySupport.isConsumerAdvisoryTopic(info.getDestination())) {
                for (final ConsumerInfo value3 : this.consumers) {
                    final ActiveMQTopic topic2 = AdvisorySupport.getConsumerAdvisoryTopic(value3.getDestination());
                    this.fireConsumerAdvisory(context, value3.getDestination(), topic2, value3, info.getConsumerId());
                }
            }
            if (AdvisorySupport.isNetworkBridgeAdvisoryTopic(info.getDestination())) {
                for (final BrokerInfo key : this.networkBridges.keySet()) {
                    final ActiveMQTopic topic2 = AdvisorySupport.getNetworkBridgeAdvisoryTopic();
                    this.fireAdvisory(context, topic2, key, null, this.networkBridges.get(key));
                }
            }
        }
        return answer;
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        super.addProducer(context, info);
        if (info.getDestination() != null && !AdvisorySupport.isAdvisoryTopic(info.getDestination())) {
            final ActiveMQTopic topic = AdvisorySupport.getProducerAdvisoryTopic(info.getDestination());
            this.fireProducerAdvisory(context, info.getDestination(), topic, info);
            this.producers.put(info.getProducerId(), info);
        }
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean create) throws Exception {
        final Destination answer = super.addDestination(context, destination, create);
        if (!AdvisorySupport.isAdvisoryTopic(destination)) {
            final DestinationInfo info = new DestinationInfo(context.getConnectionId(), (byte)0, destination);
            final DestinationInfo previous = this.destinations.putIfAbsent(destination, info);
            if (previous == null) {
                final ActiveMQTopic topic = AdvisorySupport.getDestinationAdvisoryTopic(destination);
                this.fireAdvisory(context, topic, info);
            }
        }
        return answer;
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        this.next.addDestinationInfo(context, info);
        if (!AdvisorySupport.isAdvisoryTopic(destination)) {
            final DestinationInfo previous = this.destinations.putIfAbsent(destination, info);
            if (previous == null) {
                final ActiveMQTopic topic = AdvisorySupport.getDestinationAdvisoryTopic(destination);
                this.fireAdvisory(context, topic, info);
            }
        }
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        super.removeDestination(context, destination, timeout);
        DestinationInfo info = this.destinations.remove(destination);
        if (info != null) {
            info = info.copy();
            info.setDestination(destination);
            info.setOperationType((byte)1);
            final ActiveMQTopic topic = AdvisorySupport.getDestinationAdvisoryTopic(destination);
            this.fireAdvisory(context, topic, info);
            final ActiveMQTopic[] allDestinationAdvisoryTopics;
            final ActiveMQTopic[] advisoryDestinations = allDestinationAdvisoryTopics = AdvisorySupport.getAllDestinationAdvisoryTopics(destination);
            for (final ActiveMQTopic advisoryDestination : allDestinationAdvisoryTopics) {
                try {
                    this.next.removeDestination(context, advisoryDestination, -1L);
                }
                catch (Exception ex) {}
            }
        }
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo destInfo) throws Exception {
        super.removeDestinationInfo(context, destInfo);
        DestinationInfo info = this.destinations.remove(destInfo.getDestination());
        if (info != null) {
            info = info.copy();
            info.setDestination(destInfo.getDestination());
            info.setOperationType((byte)1);
            final ActiveMQTopic topic = AdvisorySupport.getDestinationAdvisoryTopic(destInfo.getDestination());
            this.fireAdvisory(context, topic, info);
            final ActiveMQTopic[] allDestinationAdvisoryTopics;
            final ActiveMQTopic[] advisoryDestinations = allDestinationAdvisoryTopics = AdvisorySupport.getAllDestinationAdvisoryTopics(destInfo.getDestination());
            for (final ActiveMQTopic advisoryDestination : allDestinationAdvisoryTopics) {
                try {
                    this.next.removeDestination(context, advisoryDestination, -1L);
                }
                catch (Exception ex) {}
            }
        }
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        super.removeConnection(context, info, error);
        final ActiveMQTopic topic = AdvisorySupport.getConnectionAdvisoryTopic();
        this.fireAdvisory(context, topic, info.createRemoveCommand());
        this.connections.remove(info.getConnectionId());
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        super.removeConsumer(context, info);
        final ActiveMQDestination dest = info.getDestination();
        if (!AdvisorySupport.isAdvisoryTopic(dest)) {
            final ActiveMQTopic topic = AdvisorySupport.getConsumerAdvisoryTopic(dest);
            this.consumers.remove(info);
            if (!dest.isTemporary() || this.destinations.containsKey(dest)) {
                this.fireConsumerAdvisory(context, dest, topic, info.createRemoveCommand());
            }
        }
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        final SubscriptionKey key = new SubscriptionKey(context.getClientId(), info.getSubscriptionName());
        RegionBroker regionBroker = null;
        if (this.next instanceof RegionBroker) {
            regionBroker = (RegionBroker)this.next;
        }
        else {
            final BrokerService service = this.next.getBrokerService();
            regionBroker = (RegionBroker)service.getRegionBroker();
        }
        if (regionBroker == null) {
            AdvisoryBroker.LOG.warn("Cannot locate a RegionBroker instance to pass along the removeSubscription call");
            throw new IllegalStateException("No RegionBroker found.");
        }
        final DurableTopicSubscription sub = ((TopicRegion)regionBroker.getTopicRegion()).getDurableSubscription(key);
        super.removeSubscription(context, info);
        if (sub == null) {
            AdvisoryBroker.LOG.warn("We cannot send an advisory message for a durable sub removal when we don't know about the durable sub");
            return;
        }
        final ActiveMQDestination dest = sub.getConsumerInfo().getDestination();
        if (!AdvisorySupport.isAdvisoryTopic(dest)) {
            final ActiveMQTopic topic = AdvisorySupport.getConsumerAdvisoryTopic(dest);
            this.fireConsumerAdvisory(context, dest, topic, info);
        }
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        super.removeProducer(context, info);
        final ActiveMQDestination dest = info.getDestination();
        if (info.getDestination() != null && !AdvisorySupport.isAdvisoryTopic(dest)) {
            final ActiveMQTopic topic = AdvisorySupport.getProducerAdvisoryTopic(dest);
            this.producers.remove(info.getProducerId());
            if (!dest.isTemporary() || this.destinations.containsKey(dest)) {
                this.fireProducerAdvisory(context, dest, topic, info.createRemoveCommand());
            }
        }
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription) {
        super.messageExpired(context, messageReference, subscription);
        try {
            if (!messageReference.isAdvisory()) {
                final ActiveMQTopic topic = AdvisorySupport.getExpiredMessageTopic(messageReference.getMessage().getDestination());
                final Message payload = messageReference.getMessage().copy();
                payload.clearBody();
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("orignalMessageId", payload.getMessageId().toString());
                this.fireAdvisory(context, topic, payload, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("expired", e);
        }
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        super.messageConsumed(context, messageReference);
        try {
            if (!messageReference.isAdvisory()) {
                final ActiveMQTopic topic = AdvisorySupport.getMessageConsumedAdvisoryTopic(messageReference.getMessage().getDestination());
                final Message payload = messageReference.getMessage().copy();
                payload.clearBody();
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("orignalMessageId", payload.getMessageId().toString());
                final ActiveMQDestination destination = payload.getDestination();
                if (destination != null) {
                    advisoryMessage.setStringProperty("orignalDestination", payload.getMessageId().toString());
                }
                this.fireAdvisory(context, topic, payload, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("consumed", e);
        }
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        super.messageDelivered(context, messageReference);
        try {
            if (!messageReference.isAdvisory()) {
                final ActiveMQTopic topic = AdvisorySupport.getMessageDeliveredAdvisoryTopic(messageReference.getMessage().getDestination());
                final Message payload = messageReference.getMessage().copy();
                payload.clearBody();
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("orignalMessageId", payload.getMessageId().toString());
                final ActiveMQDestination destination = payload.getDestination();
                if (destination != null) {
                    advisoryMessage.setStringProperty("orignalDestination", payload.getMessageId().toString());
                }
                this.fireAdvisory(context, topic, payload, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("delivered", e);
        }
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        super.messageDiscarded(context, sub, messageReference);
        try {
            if (!messageReference.isAdvisory()) {
                final ActiveMQTopic topic = AdvisorySupport.getMessageDiscardedAdvisoryTopic(messageReference.getMessage().getDestination());
                final Message payload = messageReference.getMessage().copy();
                payload.clearBody();
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                if (sub instanceof TopicSubscription) {
                    advisoryMessage.setIntProperty("discardedCount", ((TopicSubscription)sub).discarded());
                }
                advisoryMessage.setStringProperty("orignalMessageId", payload.getMessageId().toString());
                advisoryMessage.setStringProperty("consumerId", sub.getConsumerInfo().getConsumerId().toString());
                final ActiveMQDestination destination = payload.getDestination();
                if (destination != null) {
                    advisoryMessage.setStringProperty("orignalDestination", payload.getMessageId().toString());
                }
                this.fireAdvisory(context, topic, payload, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("discarded", e);
        }
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination destination, final Subscription subs) {
        super.slowConsumer(context, destination, subs);
        try {
            if (!AdvisorySupport.isAdvisoryTopic(destination.getActiveMQDestination())) {
                final ActiveMQTopic topic = AdvisorySupport.getSlowConsumerAdvisoryTopic(destination.getActiveMQDestination());
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("consumerId", subs.getConsumerInfo().getConsumerId().toString());
                this.fireAdvisory(context, topic, subs.getConsumerInfo(), null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("slow consumer", e);
        }
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
        super.fastProducer(context, producerInfo, destination);
        try {
            if (!AdvisorySupport.isAdvisoryTopic(destination)) {
                final ActiveMQTopic topic = AdvisorySupport.getFastProducerAdvisoryTopic(destination);
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("producerId", producerInfo.getProducerId().toString());
                this.fireAdvisory(context, topic, producerInfo, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("fast producer", e);
        }
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
        super.isFull(context, destination, usage);
        if (!AdvisorySupport.isAdvisoryTopic(destination.getActiveMQDestination())) {
            try {
                final ActiveMQTopic topic = AdvisorySupport.getFullAdvisoryTopic(destination.getActiveMQDestination());
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setStringProperty("usageName", usage.getName());
                this.fireAdvisory(context, topic, null, null, advisoryMessage);
            }
            catch (Exception e) {
                this.handleFireFailure("is full", e);
            }
        }
    }
    
    @Override
    public void nowMasterBroker() {
        super.nowMasterBroker();
        try {
            final ActiveMQTopic topic = AdvisorySupport.getMasterBrokerAdvisoryTopic();
            final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
            final ConnectionContext context = new ConnectionContext();
            context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
            context.setBroker(this.getBrokerService().getBroker());
            this.fireAdvisory(context, topic, null, null, advisoryMessage);
        }
        catch (Exception e) {
            this.handleFireFailure("now master broker", e);
        }
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        final boolean wasDLQd = super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
        if (wasDLQd) {
            try {
                if (!messageReference.isAdvisory()) {
                    final ActiveMQTopic topic = AdvisorySupport.getMessageDLQdAdvisoryTopic(messageReference.getMessage().getDestination());
                    final Message payload = messageReference.getMessage().copy();
                    payload.clearBody();
                    this.fireAdvisory(context, topic, payload);
                }
            }
            catch (Exception e) {
                this.handleFireFailure("add to DLQ", e);
            }
        }
        return wasDLQd;
    }
    
    @Override
    public void networkBridgeStarted(final BrokerInfo brokerInfo, final boolean createdByDuplex, final String remoteIp) {
        try {
            if (brokerInfo != null) {
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setBooleanProperty("started", true);
                advisoryMessage.setBooleanProperty("createdByDuplex", createdByDuplex);
                advisoryMessage.setStringProperty("remoteIp", remoteIp);
                this.networkBridges.putIfAbsent(brokerInfo, advisoryMessage);
                final ActiveMQTopic topic = AdvisorySupport.getNetworkBridgeAdvisoryTopic();
                final ConnectionContext context = new ConnectionContext();
                context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
                context.setBroker(this.getBrokerService().getBroker());
                this.fireAdvisory(context, topic, brokerInfo, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("network bridge started", e);
        }
    }
    
    @Override
    public void networkBridgeStopped(final BrokerInfo brokerInfo) {
        try {
            if (brokerInfo != null) {
                final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
                advisoryMessage.setBooleanProperty("started", false);
                this.networkBridges.remove(brokerInfo);
                final ActiveMQTopic topic = AdvisorySupport.getNetworkBridgeAdvisoryTopic();
                final ConnectionContext context = new ConnectionContext();
                context.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
                context.setBroker(this.getBrokerService().getBroker());
                this.fireAdvisory(context, topic, brokerInfo, null, advisoryMessage);
            }
        }
        catch (Exception e) {
            this.handleFireFailure("network bridge stopped", e);
        }
    }
    
    private void handleFireFailure(final String message, final Throwable cause) {
        AdvisoryBroker.LOG.warn("Failed to fire {} advisory, reason: {}", message, cause);
        AdvisoryBroker.LOG.debug("{} detail: {}", message, cause);
    }
    
    protected void fireAdvisory(final ConnectionContext context, final ActiveMQTopic topic, final Command command) throws Exception {
        this.fireAdvisory(context, topic, command, null);
    }
    
    protected void fireAdvisory(final ConnectionContext context, final ActiveMQTopic topic, final Command command, final ConsumerId targetConsumerId) throws Exception {
        final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
        this.fireAdvisory(context, topic, command, targetConsumerId, advisoryMessage);
    }
    
    protected void fireConsumerAdvisory(final ConnectionContext context, final ActiveMQDestination consumerDestination, final ActiveMQTopic topic, final Command command) throws Exception {
        this.fireConsumerAdvisory(context, consumerDestination, topic, command, null);
    }
    
    protected void fireConsumerAdvisory(final ConnectionContext context, final ActiveMQDestination consumerDestination, final ActiveMQTopic topic, final Command command, final ConsumerId targetConsumerId) throws Exception {
        final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
        int count = 0;
        final Set<Destination> set = this.getDestinations(consumerDestination);
        if (set != null) {
            for (final Destination dest : set) {
                count += (int)dest.getDestinationStatistics().getConsumers().getCount();
            }
        }
        advisoryMessage.setIntProperty("consumerCount", count);
        this.fireAdvisory(context, topic, command, targetConsumerId, advisoryMessage);
    }
    
    protected void fireProducerAdvisory(final ConnectionContext context, final ActiveMQDestination producerDestination, final ActiveMQTopic topic, final Command command) throws Exception {
        this.fireProducerAdvisory(context, producerDestination, topic, command, null);
    }
    
    protected void fireProducerAdvisory(final ConnectionContext context, final ActiveMQDestination producerDestination, final ActiveMQTopic topic, final Command command, final ConsumerId targetConsumerId) throws Exception {
        final ActiveMQMessage advisoryMessage = new ActiveMQMessage();
        int count = 0;
        if (producerDestination != null) {
            final Set<Destination> set = this.getDestinations(producerDestination);
            if (set != null) {
                for (final Destination dest : set) {
                    count += (int)dest.getDestinationStatistics().getProducers().getCount();
                }
            }
        }
        advisoryMessage.setIntProperty("producerCount", count);
        this.fireAdvisory(context, topic, command, targetConsumerId, advisoryMessage);
    }
    
    public void fireAdvisory(final ConnectionContext context, final ActiveMQTopic topic, final Command command, final ConsumerId targetConsumerId, final ActiveMQMessage advisoryMessage) throws Exception {
        if (this.getBrokerService().isStarted()) {
            advisoryMessage.setStringProperty("originBrokerName", this.getBrokerName());
            final String id = (this.getBrokerId() != null) ? this.getBrokerId().getValue() : "NOT_SET";
            advisoryMessage.setStringProperty("originBrokerId", id);
            String url = this.getBrokerService().getVmConnectorURI().toString();
            if (this.getBrokerService().getDefaultSocketURIString() != null) {
                url = this.getBrokerService().getDefaultSocketURIString();
            }
            advisoryMessage.setStringProperty("originBrokerURL", url);
            advisoryMessage.setDataStructure(command);
            advisoryMessage.setPersistent(false);
            advisoryMessage.setType("Advisory");
            advisoryMessage.setMessageId(new MessageId(this.advisoryProducerId, this.messageIdGenerator.getNextSequenceId()));
            advisoryMessage.setTargetConsumerId(targetConsumerId);
            advisoryMessage.setDestination(topic);
            advisoryMessage.setResponseRequired(false);
            advisoryMessage.setProducerId(this.advisoryProducerId);
            final boolean originalFlowControl = context.isProducerFlowControl();
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setConnectionContext(context);
            producerExchange.setMutable(true);
            producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
            try {
                context.setProducerFlowControl(false);
                this.next.send(producerExchange, advisoryMessage);
            }
            finally {
                context.setProducerFlowControl(originalFlowControl);
            }
        }
    }
    
    public Map<ConnectionId, ConnectionInfo> getAdvisoryConnections() {
        return this.connections;
    }
    
    public Queue<ConsumerInfo> getAdvisoryConsumers() {
        return this.consumers;
    }
    
    public Map<ProducerId, ProducerInfo> getAdvisoryProducers() {
        return this.producers;
    }
    
    public Map<ActiveMQDestination, DestinationInfo> getAdvisoryDestinations() {
        return this.destinations;
    }
    
    static {
        LOG = LoggerFactory.getLogger(AdvisoryBroker.class);
        ID_GENERATOR = new IdGenerator();
    }
}
