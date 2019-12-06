// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.fusesource.mqtt.codec.PINGRESP;
import org.slf4j.LoggerFactory;
import java.util.zip.DataFormatException;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.ByteArrayOutputStream;
import java.util.zip.Inflater;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.RemoveInfo;
import org.fusesource.mqtt.codec.UNSUBACK;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.PrefetchSubscription;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.TopicRegion;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerInfo;
import org.fusesource.mqtt.codec.SUBACK;
import org.apache.activemq.command.ShutdownInfo;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.QoS;
import java.util.Iterator;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.List;
import org.apache.activemq.store.PersistenceAdapterSupport;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.util.IOExceptionSupport;
import org.fusesource.mqtt.codec.CONNACK;
import javax.jms.JMSException;
import org.fusesource.mqtt.codec.PUBCOMP;
import org.fusesource.mqtt.codec.PUBREL;
import org.fusesource.mqtt.codec.PUBACK;
import org.fusesource.mqtt.codec.PUBLISH;
import org.fusesource.mqtt.codec.UNSUBSCRIBE;
import org.fusesource.mqtt.codec.SUBSCRIBE;
import java.io.IOException;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.Command;
import java.util.Collections;
import java.util.HashSet;
import org.apache.activemq.util.LRUCache;
import org.fusesource.mqtt.codec.CONNECT;
import org.apache.activemq.command.ConnectionInfo;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.fusesource.mqtt.codec.PUBREC;
import org.apache.activemq.command.MessageAck;
import java.util.Set;
import javax.jms.Destination;
import org.apache.activemq.command.ActiveMQTopic;
import java.util.Map;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.apache.activemq.command.ConsumerId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ConnectionId;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;

public class MQTTProtocolConverter
{
    private static final Logger LOG;
    private static final IdGenerator CONNECTION_ID_GENERATOR;
    private static final MQTTFrame PING_RESP_FRAME;
    private static final double MQTT_KEEP_ALIVE_GRACE_PERIOD = 0.5;
    static final int DEFAULT_CACHE_SIZE = 5000;
    private static final byte SUBSCRIBE_ERROR = Byte.MIN_VALUE;
    private final ConnectionId connectionId;
    private final SessionId sessionId;
    private final ProducerId producerId;
    private final LongSequenceGenerator publisherIdGenerator;
    private final LongSequenceGenerator consumerIdGenerator;
    private final ConcurrentHashMap<Integer, ResponseHandler> resposeHandlers;
    private final ConcurrentHashMap<ConsumerId, MQTTSubscription> subscriptionsByConsumerId;
    private final ConcurrentHashMap<UTF8Buffer, MQTTSubscription> mqttSubscriptionByTopic;
    private final Map<UTF8Buffer, ActiveMQTopic> activeMQTopicMap;
    private final Map<Destination, UTF8Buffer> mqttTopicMap;
    private final Set<String> restoredSubs;
    private final Map<Short, MessageAck> consumerAcks;
    private final Map<Short, PUBREC> publisherRecs;
    private final MQTTTransport mqttTransport;
    private final BrokerService brokerService;
    private final Object commnadIdMutex;
    private int lastCommandId;
    private final AtomicBoolean connected;
    private final ConnectionInfo connectionInfo;
    private CONNECT connect;
    private String clientId;
    private long defaultKeepAlive;
    private int activeMQSubscriptionPrefetch;
    protected static final String QOS_PROPERTY_NAME = "ActiveMQ.MQTT.QoS";
    private final MQTTPacketIdGenerator packetIdGenerator;
    boolean willSent;
    
    public MQTTProtocolConverter(final MQTTTransport mqttTransport, final BrokerService brokerService) {
        this.connectionId = new ConnectionId(MQTTProtocolConverter.CONNECTION_ID_GENERATOR.generateId());
        this.sessionId = new SessionId(this.connectionId, -1L);
        this.producerId = new ProducerId(this.sessionId, 1L);
        this.publisherIdGenerator = new LongSequenceGenerator();
        this.consumerIdGenerator = new LongSequenceGenerator();
        this.resposeHandlers = new ConcurrentHashMap<Integer, ResponseHandler>();
        this.subscriptionsByConsumerId = new ConcurrentHashMap<ConsumerId, MQTTSubscription>();
        this.mqttSubscriptionByTopic = new ConcurrentHashMap<UTF8Buffer, MQTTSubscription>();
        this.activeMQTopicMap = new LRUCache<UTF8Buffer, ActiveMQTopic>(5000);
        this.mqttTopicMap = new LRUCache<Destination, UTF8Buffer>(5000);
        this.restoredSubs = Collections.synchronizedSet(new HashSet<String>());
        this.consumerAcks = new LRUCache<Short, MessageAck>(5000);
        this.publisherRecs = new LRUCache<Short, PUBREC>(5000);
        this.commnadIdMutex = new Object();
        this.connected = new AtomicBoolean(false);
        this.connectionInfo = new ConnectionInfo();
        this.activeMQSubscriptionPrefetch = 1;
        this.willSent = false;
        this.mqttTransport = mqttTransport;
        this.brokerService = brokerService;
        this.packetIdGenerator = MQTTPacketIdGenerator.getMQTTPacketIdGenerator(brokerService);
        this.defaultKeepAlive = 0L;
    }
    
    int generateCommandId() {
        synchronized (this.commnadIdMutex) {
            return this.lastCommandId++;
        }
    }
    
    void sendToActiveMQ(final Command command, final ResponseHandler handler) {
        if (command instanceof ActiveMQMessage) {
            final ActiveMQMessage msg = (ActiveMQMessage)command;
            if (msg.getDestination().getPhysicalName().startsWith("$")) {
                if (handler != null) {
                    try {
                        handler.onResponse(this, new Response());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
        command.setCommandId(this.generateCommandId());
        if (handler != null) {
            command.setResponseRequired(true);
            this.resposeHandlers.put(command.getCommandId(), handler);
        }
        this.getMQTTTransport().sendToActiveMQ(command);
    }
    
    void sendToMQTT(final MQTTFrame frame) {
        try {
            this.mqttTransport.sendToMQTT(frame);
        }
        catch (IOException e) {
            MQTTProtocolConverter.LOG.warn("Failed to send frame " + frame, e);
        }
    }
    
    public void onMQTTCommand(final MQTTFrame frame) throws IOException, JMSException {
        switch (frame.messageType()) {
            case 12: {
                MQTTProtocolConverter.LOG.debug("Received a ping from client: " + this.getClientId());
                this.sendToMQTT(MQTTProtocolConverter.PING_RESP_FRAME);
                MQTTProtocolConverter.LOG.debug("Sent Ping Response to " + this.getClientId());
                break;
            }
            case 1: {
                final CONNECT connect = new CONNECT().decode(frame);
                this.onMQTTConnect(connect);
                MQTTProtocolConverter.LOG.debug("MQTT Client {} connected. (version: {})", this.getClientId(), connect.version());
                break;
            }
            case 14: {
                MQTTProtocolConverter.LOG.debug("MQTT Client {} disconnecting", this.getClientId());
                this.onMQTTDisconnect();
                break;
            }
            case 8: {
                this.onSubscribe(new SUBSCRIBE().decode(frame));
                break;
            }
            case 10: {
                this.onUnSubscribe(new UNSUBSCRIBE().decode(frame));
                break;
            }
            case 3: {
                this.onMQTTPublish(new PUBLISH().decode(frame));
                break;
            }
            case 4: {
                this.onMQTTPubAck(new PUBACK().decode(frame));
                break;
            }
            case 5: {
                this.onMQTTPubRec(new PUBREC().decode(frame));
                break;
            }
            case 6: {
                this.onMQTTPubRel(new PUBREL().decode(frame));
                break;
            }
            case 7: {
                this.onMQTTPubComp(new PUBCOMP().decode(frame));
                break;
            }
            default: {
                this.handleException(new MQTTProtocolException("Unknown MQTTFrame type: " + frame.messageType(), true), frame);
                break;
            }
        }
    }
    
    void onMQTTConnect(final CONNECT connect) throws MQTTProtocolException {
        if (this.connected.get()) {
            throw new MQTTProtocolException("Already connected.");
        }
        this.connect = connect;
        String clientId = "";
        if (connect.clientId() != null) {
            clientId = connect.clientId().toString();
        }
        String userName = null;
        if (connect.userName() != null) {
            userName = connect.userName().toString();
        }
        String passswd = null;
        if (connect.password() != null) {
            passswd = connect.password().toString();
        }
        this.configureInactivityMonitor(connect.keepAlive());
        this.connectionInfo.setConnectionId(this.connectionId);
        if (clientId != null && !clientId.isEmpty()) {
            this.connectionInfo.setClientId(clientId);
        }
        else {
            if (!connect.cleanSession()) {
                final CONNACK ack = new CONNACK();
                ack.code(CONNACK.Code.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
                try {
                    this.getMQTTTransport().sendToMQTT(ack.encode());
                    this.getMQTTTransport().onException(IOExceptionSupport.create("Invalid Client ID", null));
                }
                catch (IOException e) {
                    this.getMQTTTransport().onException(IOExceptionSupport.create(e));
                }
                return;
            }
            this.connectionInfo.setClientId("" + this.connectionInfo.getConnectionId().toString());
        }
        this.connectionInfo.setResponseRequired(true);
        this.connectionInfo.setUserName(userName);
        this.connectionInfo.setPassword(passswd);
        this.connectionInfo.setTransportContext(this.mqttTransport.getPeerCertificates());
        this.sendToActiveMQ(this.connectionInfo, new ResponseHandler() {
            @Override
            public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                if (response.isException()) {
                    final Throwable exception = ((ExceptionResponse)response).getException();
                    final CONNACK ack = new CONNACK();
                    ack.code(CONNACK.Code.CONNECTION_REFUSED_SERVER_UNAVAILABLE);
                    MQTTProtocolConverter.this.getMQTTTransport().sendToMQTT(ack.encode());
                    MQTTProtocolConverter.this.getMQTTTransport().onException(IOExceptionSupport.create(exception));
                    return;
                }
                final SessionInfo sessionInfo = new SessionInfo(MQTTProtocolConverter.this.sessionId);
                MQTTProtocolConverter.this.sendToActiveMQ(sessionInfo, null);
                final ProducerInfo producerInfo = new ProducerInfo(MQTTProtocolConverter.this.producerId);
                MQTTProtocolConverter.this.sendToActiveMQ(producerInfo, new ResponseHandler() {
                    @Override
                    public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                        if (response.isException()) {
                            final Throwable exception = ((ExceptionResponse)response).getException();
                            final CONNACK ack = new CONNACK();
                            ack.code(CONNACK.Code.CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD);
                            MQTTProtocolConverter.this.getMQTTTransport().sendToMQTT(ack.encode());
                            MQTTProtocolConverter.this.getMQTTTransport().onException(IOExceptionSupport.create(exception));
                            return;
                        }
                        final CONNACK ack2 = new CONNACK();
                        ack2.code(CONNACK.Code.CONNECTION_ACCEPTED);
                        MQTTProtocolConverter.this.connected.set(true);
                        MQTTProtocolConverter.this.getMQTTTransport().sendToMQTT(ack2.encode());
                        final List<SubscriptionInfo> subs = PersistenceAdapterSupport.listSubscriptions(MQTTProtocolConverter.this.brokerService.getPersistenceAdapter(), MQTTProtocolConverter.this.connectionInfo.getClientId());
                        if (connect.cleanSession()) {
                            MQTTProtocolConverter.this.packetIdGenerator.stopClientSession(MQTTProtocolConverter.this.getClientId());
                            MQTTProtocolConverter.this.deleteDurableSubs(subs);
                        }
                        else {
                            MQTTProtocolConverter.this.packetIdGenerator.startClientSession(MQTTProtocolConverter.this.getClientId());
                            MQTTProtocolConverter.this.restoreDurableSubs(subs);
                        }
                    }
                });
            }
        });
    }
    
    public void deleteDurableSubs(final List<SubscriptionInfo> subs) {
        try {
            for (final SubscriptionInfo sub : subs) {
                final RemoveSubscriptionInfo rsi = new RemoveSubscriptionInfo();
                rsi.setConnectionId(this.connectionId);
                rsi.setSubscriptionName(sub.getSubcriptionName());
                rsi.setClientId(sub.getClientId());
                this.sendToActiveMQ(rsi, new ResponseHandler() {
                    @Override
                    public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                    }
                });
            }
        }
        catch (Throwable e) {
            MQTTProtocolConverter.LOG.warn("Could not delete the MQTT durable subs.", e);
        }
    }
    
    public void restoreDurableSubs(final List<SubscriptionInfo> subs) {
        try {
            for (final SubscriptionInfo sub : subs) {
                final String name = sub.getSubcriptionName();
                final String[] split = name.split(":", 2);
                final QoS qoS = QoS.valueOf(split[0]);
                this.onSubscribe(new Topic(split[1], qoS));
                this.restoredSubs.add(split[1]);
            }
        }
        catch (IOException e) {
            MQTTProtocolConverter.LOG.warn("Could not restore the MQTT durable subs.", e);
        }
    }
    
    void onMQTTDisconnect() throws MQTTProtocolException {
        if (this.connected.get()) {
            this.connected.set(false);
            this.sendToActiveMQ(this.connectionInfo.createRemoveCommand(), null);
            this.sendToActiveMQ(new ShutdownInfo(), null);
        }
        this.stopTransport();
    }
    
    void onSubscribe(final SUBSCRIBE command) throws MQTTProtocolException {
        this.checkConnected();
        final Topic[] topics = command.topics();
        if (topics != null) {
            final byte[] qos = new byte[topics.length];
            for (int i = 0; i < topics.length; ++i) {
                qos[i] = this.onSubscribe(topics[i]);
            }
            final SUBACK ack = new SUBACK();
            ack.messageId(command.messageId());
            ack.grantedQos(qos);
            try {
                this.getMQTTTransport().sendToMQTT(ack.encode());
            }
            catch (IOException e) {
                MQTTProtocolConverter.LOG.warn("Couldn't send SUBACK for " + command, e);
            }
        }
        else {
            MQTTProtocolConverter.LOG.warn("No topics defined for Subscription " + command);
        }
    }
    
    byte onSubscribe(final Topic topic) throws MQTTProtocolException {
        final UTF8Buffer topicName = topic.name();
        final QoS topicQoS = topic.qos();
        final ActiveMQDestination destination = new ActiveMQTopic(this.convertMQTTToActiveMQ(topicName.toString()));
        if (this.mqttSubscriptionByTopic.containsKey(topicName)) {
            final MQTTSubscription mqttSubscription = this.mqttSubscriptionByTopic.get(topicName);
            if (topicQoS == mqttSubscription.qos()) {
                this.resendRetainedMessages(topicName, destination, mqttSubscription);
                return (byte)topicQoS.ordinal();
            }
            this.onUnSubscribe(topicName);
            this.onUnSubscribe(topicName);
        }
        final ConsumerId id = new ConsumerId(this.sessionId, this.consumerIdGenerator.getNextSequenceId());
        final ConsumerInfo consumerInfo = new ConsumerInfo(id);
        consumerInfo.setDestination(destination);
        consumerInfo.setPrefetchSize(this.getActiveMQSubscriptionPrefetch());
        consumerInfo.setRetroactive(true);
        consumerInfo.setDispatchAsync(true);
        if (!this.connect.cleanSession() && this.connect.clientId() != null && topicQoS.ordinal() >= QoS.AT_LEAST_ONCE.ordinal()) {
            consumerInfo.setSubscriptionName(topicQoS + ":" + topicName.toString());
        }
        final MQTTSubscription mqttSubscription2 = new MQTTSubscription(this, topicQoS, consumerInfo);
        this.subscriptionsByConsumerId.put(id, mqttSubscription2);
        this.mqttSubscriptionByTopic.put(topicName, mqttSubscription2);
        final byte[] qos = { -1 };
        this.sendToActiveMQ(consumerInfo, new ResponseHandler() {
            @Override
            public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                if (response.isException()) {
                    final Throwable throwable = ((ExceptionResponse)response).getException();
                    MQTTProtocolConverter.LOG.warn("Error subscribing to " + topicName, throwable);
                    qos[0] = -128;
                }
                else {
                    qos[0] = (byte)topicQoS.ordinal();
                }
            }
        });
        if (qos[0] == -128) {
            this.subscriptionsByConsumerId.remove(id);
            this.mqttSubscriptionByTopic.remove(topicName);
        }
        return qos[0];
    }
    
    private void resendRetainedMessages(final UTF8Buffer topicName, final ActiveMQDestination destination, final MQTTSubscription mqttSubscription) throws MQTTProtocolException {
        if (this.restoredSubs.remove(destination.getPhysicalName())) {
            return;
        }
        RegionBroker regionBroker;
        try {
            regionBroker = (RegionBroker)this.brokerService.getBroker().getAdaptor(RegionBroker.class);
        }
        catch (Exception e) {
            throw new MQTTProtocolException("Error subscribing to " + topicName + ": " + e.getMessage(), false, e);
        }
        final TopicRegion topicRegion = (TopicRegion)regionBroker.getTopicRegion();
        final ConsumerInfo consumerInfo = mqttSubscription.getConsumerInfo();
        final ConsumerId consumerId = consumerInfo.getConsumerId();
        final String connectionInfoClientId = this.connectionInfo.getClientId();
        final ConnectionContext connectionContext = regionBroker.getConnectionContext(connectionInfoClientId);
        final Set<org.apache.activemq.broker.region.Destination> matchingDestinations = topicRegion.getDestinations(destination);
        for (final org.apache.activemq.broker.region.Destination dest : matchingDestinations) {
            for (final Subscription subscription : dest.getConsumers()) {
                if (subscription.getConsumerInfo().getConsumerId().equals(consumerId)) {
                    try {
                        ((org.apache.activemq.broker.region.Topic)dest).recoverRetroactiveMessages(connectionContext, subscription);
                        if (subscription instanceof PrefetchSubscription) {
                            final PrefetchSubscription prefetchSubscription = (PrefetchSubscription)subscription;
                            prefetchSubscription.dispatchPending();
                        }
                        break;
                    }
                    catch (Exception e2) {
                        throw new MQTTProtocolException("Error recovering retained messages for " + dest.getName() + ": " + e2.getMessage(), false, e2);
                    }
                }
            }
        }
    }
    
    void onUnSubscribe(final UNSUBSCRIBE command) throws MQTTProtocolException {
        this.checkConnected();
        final UTF8Buffer[] topics = command.topics();
        if (topics != null) {
            for (final UTF8Buffer topic : topics) {
                this.onUnSubscribe(topic);
            }
        }
        final UNSUBACK ack = new UNSUBACK();
        ack.messageId(command.messageId());
        this.sendToMQTT(ack.encode());
    }
    
    void onUnSubscribe(final UTF8Buffer topicName) {
        final MQTTSubscription subs = this.mqttSubscriptionByTopic.remove(topicName);
        if (subs != null) {
            final ConsumerInfo info = subs.getConsumerInfo();
            if (info != null) {
                this.subscriptionsByConsumerId.remove(info.getConsumerId());
            }
            RemoveInfo removeInfo = null;
            if (info != null) {
                removeInfo = info.createRemoveCommand();
            }
            this.sendToActiveMQ(removeInfo, null);
            if (subs.getConsumerInfo().getSubscriptionName() != null) {
                this.restoredSubs.remove(this.convertMQTTToActiveMQ(topicName.toString()));
                final RemoveSubscriptionInfo rsi = new RemoveSubscriptionInfo();
                rsi.setConnectionId(this.connectionId);
                rsi.setSubscriptionName(subs.getConsumerInfo().getSubscriptionName());
                rsi.setClientId(this.connectionInfo.getClientId());
                this.sendToActiveMQ(rsi, null);
            }
        }
    }
    
    public void onActiveMQCommand(final Command command) throws Exception {
        if (command.isResponse()) {
            final Response response = (Response)command;
            final ResponseHandler rh = this.resposeHandlers.remove(response.getCorrelationId());
            if (rh != null) {
                rh.onResponse(this, response);
            }
            else if (response.isException()) {
                final Throwable exception = ((ExceptionResponse)response).getException();
                this.handleException(exception, null);
            }
        }
        else if (command.isMessageDispatch()) {
            final MessageDispatch md = (MessageDispatch)command;
            final MQTTSubscription sub = this.subscriptionsByConsumerId.get(md.getConsumerId());
            if (sub != null) {
                final MessageAck ack = sub.createMessageAck(md);
                final PUBLISH publish = sub.createPublish((ActiveMQMessage)md.getMessage());
                switch (publish.qos()) {
                    case AT_LEAST_ONCE:
                    case EXACTLY_ONCE: {
                        publish.dup(publish.dup() || md.getMessage().isRedelivered());
                        break;
                    }
                }
                if (ack != null && sub.expectAck(publish)) {
                    synchronized (this.consumerAcks) {
                        this.consumerAcks.put(publish.messageId(), ack);
                    }
                }
                this.getMQTTTransport().sendToMQTT(publish.encode());
                if (ack != null && !sub.expectAck(publish)) {
                    this.getMQTTTransport().sendToActiveMQ(ack);
                }
            }
        }
        else if (command.getDataStructureType() == 16) {
            final Throwable exception2 = ((ConnectionError)command).getException();
            this.handleException(exception2, null);
        }
        else if (!command.isBrokerInfo()) {
            MQTTProtocolConverter.LOG.debug("Do not know how to process ActiveMQ Command " + command);
        }
    }
    
    void onMQTTPublish(final PUBLISH command) throws IOException, JMSException {
        this.checkConnected();
        final ActiveMQMessage message = this.convertMessage(command);
        message.setProducerId(this.producerId);
        message.onSend();
        this.sendToActiveMQ(message, this.createResponseHandler(command));
    }
    
    void onMQTTPubAck(final PUBACK command) {
        final short messageId = command.messageId();
        this.packetIdGenerator.ackPacketId(this.getClientId(), messageId);
        final MessageAck ack;
        synchronized (this.consumerAcks) {
            ack = this.consumerAcks.remove(messageId);
        }
        if (ack != null) {
            this.getMQTTTransport().sendToActiveMQ(ack);
        }
    }
    
    void onMQTTPubRec(final PUBREC commnand) {
        final PUBREL pubrel = new PUBREL();
        pubrel.messageId(commnand.messageId());
        this.sendToMQTT(pubrel.encode());
    }
    
    void onMQTTPubRel(final PUBREL command) {
        final PUBREC ack;
        synchronized (this.publisherRecs) {
            ack = this.publisherRecs.remove(command.messageId());
        }
        if (ack == null) {
            MQTTProtocolConverter.LOG.warn("Unknown PUBREL: " + command.messageId() + " received");
        }
        final PUBCOMP pubcomp = new PUBCOMP();
        pubcomp.messageId(command.messageId());
        this.sendToMQTT(pubcomp.encode());
    }
    
    void onMQTTPubComp(final PUBCOMP command) {
        final short messageId = command.messageId();
        this.packetIdGenerator.ackPacketId(this.getClientId(), messageId);
        final MessageAck ack;
        synchronized (this.consumerAcks) {
            ack = this.consumerAcks.remove(messageId);
        }
        if (ack != null) {
            this.getMQTTTransport().sendToActiveMQ(ack);
        }
    }
    
    ActiveMQMessage convertMessage(final PUBLISH command) throws JMSException {
        final ActiveMQBytesMessage msg = new ActiveMQBytesMessage();
        msg.setProducerId(this.producerId);
        final MessageId id = new MessageId(this.producerId, this.publisherIdGenerator.getNextSequenceId());
        msg.setMessageId(id);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setPriority((byte)4);
        msg.setPersistent(command.qos() != QoS.AT_MOST_ONCE && !command.retain());
        msg.setIntProperty("ActiveMQ.MQTT.QoS", command.qos().ordinal());
        if (command.retain()) {
            msg.setBooleanProperty("ActiveMQ.Retain", true);
        }
        ActiveMQTopic topic;
        synchronized (this.activeMQTopicMap) {
            topic = this.activeMQTopicMap.get(command.topicName());
            if (topic == null) {
                final String topicName = this.convertMQTTToActiveMQ(command.topicName().toString());
                topic = new ActiveMQTopic(topicName);
                this.activeMQTopicMap.put(command.topicName(), topic);
            }
        }
        msg.setJMSDestination(topic);
        msg.writeBytes(command.payload().data, command.payload().offset, command.payload().length);
        return msg;
    }
    
    public PUBLISH convertMessage(final ActiveMQMessage message) throws IOException, JMSException, DataFormatException {
        final PUBLISH result = new PUBLISH();
        QoS qoS;
        if (message.propertyExists("ActiveMQ.MQTT.QoS")) {
            final int ordinal = message.getIntProperty("ActiveMQ.MQTT.QoS");
            qoS = QoS.values()[ordinal];
        }
        else {
            qoS = (message.isPersistent() ? QoS.AT_MOST_ONCE : QoS.AT_LEAST_ONCE);
        }
        result.qos(qoS);
        if (message.getBooleanProperty("ActiveMQ.Retained")) {
            result.retain(true);
        }
        UTF8Buffer topicName;
        synchronized (this.mqttTopicMap) {
            topicName = this.mqttTopicMap.get(message.getJMSDestination());
            if (topicName == null) {
                topicName = new UTF8Buffer(this.convertActiveMQToMQTT(message.getDestination().getPhysicalName()));
                this.mqttTopicMap.put(message.getJMSDestination(), topicName);
            }
        }
        result.topicName(topicName);
        if (message.getDataStructureType() == 28) {
            final ActiveMQTextMessage msg = (ActiveMQTextMessage)message.copy();
            msg.setReadOnlyBody(true);
            final String messageText = msg.getText();
            if (messageText != null) {
                result.payload(new Buffer(messageText.getBytes("UTF-8")));
            }
        }
        else if (message.getDataStructureType() == 24) {
            final ActiveMQBytesMessage msg2 = (ActiveMQBytesMessage)message.copy();
            msg2.setReadOnlyBody(true);
            final byte[] data = new byte[(int)msg2.getBodyLength()];
            msg2.readBytes(data);
            result.payload(new Buffer(data));
        }
        else if (message.getDataStructureType() == 25) {
            final ActiveMQMapMessage msg3 = (ActiveMQMapMessage)message.copy();
            msg3.setReadOnlyBody(true);
            final Map<String, Object> map = msg3.getContentMap();
            if (map != null) {
                result.payload(new Buffer(map.toString().getBytes("UTF-8")));
            }
        }
        else {
            ByteSequence byteSequence = message.getContent();
            if (byteSequence != null && byteSequence.getLength() > 0) {
                if (message.isCompressed()) {
                    final Inflater inflater = new Inflater();
                    inflater.setInput(byteSequence.data, byteSequence.offset, byteSequence.length);
                    final byte[] data2 = new byte[4096];
                    final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                    int read;
                    while ((read = inflater.inflate(data2)) != 0) {
                        bytesOut.write(data2, 0, read);
                    }
                    byteSequence = bytesOut.toByteSequence();
                    bytesOut.close();
                }
                result.payload(new Buffer(byteSequence.data, byteSequence.offset, byteSequence.length));
            }
        }
        return result;
    }
    
    private String convertActiveMQToMQTT(final String physicalName) {
        return physicalName.replace('.', '/');
    }
    
    public MQTTTransport getMQTTTransport() {
        return this.mqttTransport;
    }
    
    public void onTransportError() {
        if (this.connect != null && this.connected.get()) {
            if (this.connect.willTopic() != null && this.connect.willMessage() != null && !this.willSent) {
                this.willSent = true;
                try {
                    final PUBLISH publish = new PUBLISH();
                    publish.topicName(this.connect.willTopic());
                    publish.qos(this.connect.willQos());
                    publish.messageId(this.packetIdGenerator.getNextSequenceId(this.getClientId()));
                    publish.payload((Buffer)this.connect.willMessage());
                    final ActiveMQMessage message = this.convertMessage(publish);
                    message.setProducerId(this.producerId);
                    message.onSend();
                    this.sendToActiveMQ(message, null);
                }
                catch (Exception e) {
                    MQTTProtocolConverter.LOG.warn("Failed to publish Will Message " + this.connect.willMessage());
                }
            }
            this.sendToActiveMQ(this.connectionInfo.createRemoveCommand(), null);
        }
    }
    
    void configureInactivityMonitor(final short keepAliveSeconds) {
        final MQTTInactivityMonitor monitor = this.getMQTTTransport().getInactivityMonitor();
        if (monitor == null) {
            return;
        }
        long keepAliveMS = keepAliveSeconds * 1000;
        if (MQTTProtocolConverter.LOG.isDebugEnabled()) {
            MQTTProtocolConverter.LOG.debug("MQTT Client " + this.getClientId() + " requests heart beat of  " + keepAliveMS + " ms");
        }
        try {
            if (keepAliveMS == 0L && this.defaultKeepAlive > 0L) {
                keepAliveMS = this.defaultKeepAlive;
            }
            final long readGracePeriod = (long)(keepAliveMS * 0.5);
            monitor.setProtocolConverter(this);
            monitor.setReadKeepAliveTime(keepAliveMS);
            monitor.setReadGraceTime(readGracePeriod);
            monitor.startMonitorThread();
            if (MQTTProtocolConverter.LOG.isDebugEnabled()) {
                MQTTProtocolConverter.LOG.debug("MQTT Client " + this.getClientId() + " established heart beat of  " + keepAliveMS + " ms (" + keepAliveMS + "ms + " + readGracePeriod + "ms grace period)");
            }
        }
        catch (Exception ex) {
            MQTTProtocolConverter.LOG.warn("Failed to start MQTT InactivityMonitor ", ex);
        }
    }
    
    void handleException(final Throwable exception, final MQTTFrame command) {
        MQTTProtocolConverter.LOG.warn("Exception occurred processing: \n" + command + ": " + exception.toString());
        if (MQTTProtocolConverter.LOG.isDebugEnabled()) {
            MQTTProtocolConverter.LOG.debug("Exception detail", exception);
        }
        if (this.connected.get() && this.connectionInfo != null) {
            this.connected.set(false);
            this.sendToActiveMQ(this.connectionInfo.createRemoveCommand(), null);
        }
        this.stopTransport();
    }
    
    void checkConnected() throws MQTTProtocolException {
        if (!this.connected.get()) {
            throw new MQTTProtocolException("Not connected.");
        }
    }
    
    String getClientId() {
        if (this.clientId == null) {
            if (this.connect != null && this.connect.clientId() != null) {
                this.clientId = this.connect.clientId().toString();
            }
            else {
                this.clientId = "";
            }
        }
        return this.clientId;
    }
    
    private void stopTransport() {
        try {
            this.getMQTTTransport().stop();
        }
        catch (Throwable e) {
            MQTTProtocolConverter.LOG.debug("Failed to stop MQTT transport ", e);
        }
    }
    
    ResponseHandler createResponseHandler(final PUBLISH command) {
        if (command != null) {
            switch (command.qos()) {
                case AT_LEAST_ONCE: {
                    return new ResponseHandler() {
                        @Override
                        public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                            if (response.isException()) {
                                MQTTProtocolConverter.LOG.warn("Failed to send MQTT Publish: ", command, ((ExceptionResponse)response).getException());
                            }
                            else {
                                final PUBACK ack = new PUBACK();
                                ack.messageId(command.messageId());
                                converter.getMQTTTransport().sendToMQTT(ack.encode());
                            }
                        }
                    };
                }
                case EXACTLY_ONCE: {
                    return new ResponseHandler() {
                        @Override
                        public void onResponse(final MQTTProtocolConverter converter, final Response response) throws IOException {
                            if (response.isException()) {
                                MQTTProtocolConverter.LOG.warn("Failed to send MQTT Publish: ", command, ((ExceptionResponse)response).getException());
                            }
                            else {
                                final PUBREC ack = new PUBREC();
                                ack.messageId(command.messageId());
                                synchronized (MQTTProtocolConverter.this.publisherRecs) {
                                    MQTTProtocolConverter.this.publisherRecs.put(command.messageId(), ack);
                                }
                                converter.getMQTTTransport().sendToMQTT(ack.encode());
                            }
                        }
                    };
                }
            }
        }
        return null;
    }
    
    private String convertMQTTToActiveMQ(final String name) {
        final char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '#': {
                    chars[i] = '>';
                    break;
                }
                case '>': {
                    chars[i] = '#';
                    break;
                }
                case '+': {
                    chars[i] = '*';
                    break;
                }
                case '*': {
                    chars[i] = '+';
                    break;
                }
                case '/': {
                    chars[i] = '.';
                    break;
                }
                case '.': {
                    chars[i] = '/';
                    break;
                }
            }
        }
        final String rc = new String(chars);
        return rc;
    }
    
    public long getDefaultKeepAlive() {
        return this.defaultKeepAlive;
    }
    
    public void setDefaultKeepAlive(final long keepAlive) {
        this.defaultKeepAlive = keepAlive;
    }
    
    public int getActiveMQSubscriptionPrefetch() {
        return this.activeMQSubscriptionPrefetch;
    }
    
    public void setActiveMQSubscriptionPrefetch(final int activeMQSubscriptionPrefetch) {
        this.activeMQSubscriptionPrefetch = activeMQSubscriptionPrefetch;
    }
    
    public MQTTPacketIdGenerator getPacketIdGenerator() {
        return this.packetIdGenerator;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MQTTProtocolConverter.class);
        CONNECTION_ID_GENERATOR = new IdGenerator();
        PING_RESP_FRAME = new PINGRESP().encode();
    }
}
