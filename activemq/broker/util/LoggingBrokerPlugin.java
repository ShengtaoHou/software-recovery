// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.util;

import org.apache.activemq.usage.Usage;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.DestinationInfo;
import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.Connection;
import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerPluginSupport;

public class LoggingBrokerPlugin extends BrokerPluginSupport
{
    private static final Logger LOG;
    private boolean logAll;
    private boolean logMessageEvents;
    private boolean logConnectionEvents;
    private boolean logSessionEvents;
    private boolean logTransactionEvents;
    private boolean logConsumerEvents;
    private boolean logProducerEvents;
    private boolean logInternalEvents;
    private boolean perDestinationLogger;
    
    public LoggingBrokerPlugin() {
        this.logAll = false;
        this.logMessageEvents = false;
        this.logConnectionEvents = true;
        this.logSessionEvents = true;
        this.logTransactionEvents = false;
        this.logConsumerEvents = false;
        this.logProducerEvents = false;
        this.logInternalEvents = false;
        this.perDestinationLogger = false;
    }
    
    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        LoggingBrokerPlugin.LOG.info("Created LoggingBrokerPlugin: {}", this.toString());
    }
    
    public boolean isLogAll() {
        return this.logAll;
    }
    
    public void setLogAll(final boolean logAll) {
        this.logAll = logAll;
    }
    
    public boolean isLogMessageEvents() {
        return this.logMessageEvents;
    }
    
    public void setLogMessageEvents(final boolean logMessageEvents) {
        this.logMessageEvents = logMessageEvents;
    }
    
    public boolean isLogConnectionEvents() {
        return this.logConnectionEvents;
    }
    
    public void setLogConnectionEvents(final boolean logConnectionEvents) {
        this.logConnectionEvents = logConnectionEvents;
    }
    
    public boolean isLogSessionEvents() {
        return this.logSessionEvents;
    }
    
    public void setLogSessionEvents(final boolean logSessionEvents) {
        this.logSessionEvents = logSessionEvents;
    }
    
    public boolean isLogTransactionEvents() {
        return this.logTransactionEvents;
    }
    
    public void setLogTransactionEvents(final boolean logTransactionEvents) {
        this.logTransactionEvents = logTransactionEvents;
    }
    
    public boolean isLogConsumerEvents() {
        return this.logConsumerEvents;
    }
    
    public void setLogConsumerEvents(final boolean logConsumerEvents) {
        this.logConsumerEvents = logConsumerEvents;
    }
    
    public boolean isLogProducerEvents() {
        return this.logProducerEvents;
    }
    
    public void setLogProducerEvents(final boolean logProducerEvents) {
        this.logProducerEvents = logProducerEvents;
    }
    
    public boolean isLogInternalEvents() {
        return this.logInternalEvents;
    }
    
    public void setLogInternalEvents(final boolean logInternalEvents) {
        this.logInternalEvents = logInternalEvents;
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("Acknowledging message for client ID: {}{}", consumerExchange.getConnectionContext().getClientId(), (ack.getMessageCount() == 1) ? (", " + ack.getLastMessageId()) : "");
            if (ack.getMessageCount() > 1) {
                LoggingBrokerPlugin.LOG.trace("Message count: {}, First Message Id: {}, Last Message Id: {}", ack.getMessageCount(), ack.getFirstMessageId(), ack.getLastMessageId());
            }
        }
        super.acknowledge(consumerExchange, ack);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("Message Pull from: {} on {}", context.getClientId(), pull.getDestination().getPhysicalName());
        }
        return super.messagePull(context, pull);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConnectionEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding Connection: {}", info);
        }
        super.addConnection(context, info);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding Consumer: {}", info);
        }
        return super.addConsumer(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding Producer: {}", info);
        }
        super.addProducer(context, info);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LoggingBrokerPlugin.LOG.info("Committing transaction: {}", xid.getTransactionKey());
        }
        super.commitTransaction(context, xid, onePhase);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing subscription: {}", info);
        }
        super.removeSubscription(context, info);
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        final TransactionId[] result = super.getPreparedTransactions(context);
        if ((this.isLogAll() || this.isLogTransactionEvents()) && result != null) {
            final StringBuffer tids = new StringBuffer();
            for (final TransactionId tid : result) {
                if (tids.length() > 0) {
                    tids.append(", ");
                }
                tids.append(tid.getTransactionKey());
            }
            LoggingBrokerPlugin.LOG.info("Prepared transactions: {}", tids);
        }
        return result;
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LoggingBrokerPlugin.LOG.info("Preparing transaction: {}", xid.getTransactionKey());
        }
        return super.prepareTransaction(context, xid);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        if (this.isLogAll() || this.isLogConnectionEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing Connection: {}", info);
        }
        super.removeConnection(context, info, error);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing Consumer: {}", info);
        }
        super.removeConsumer(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing Producer: {}", info);
        }
        super.removeProducer(context, info);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LoggingBrokerPlugin.LOG.info("Rolling back Transaction: {}", xid.getTransactionKey());
        }
        super.rollbackTransaction(context, xid);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        if (this.isLogAll() || this.isLogProducerEvents()) {
            this.logSend(messageSend.copy());
        }
        super.send(producerExchange, messageSend);
    }
    
    private void logSend(final Message copy) {
        Logger perDestinationsLogger = LoggingBrokerPlugin.LOG;
        if (this.isPerDestinationLogger()) {
            final ActiveMQDestination destination = copy.getDestination();
            perDestinationsLogger = LoggerFactory.getLogger(LoggingBrokerPlugin.LOG.getName() + "." + destination.getDestinationTypeAsString() + "." + destination.getPhysicalName());
        }
        perDestinationsLogger.info("Sending message: {}", copy);
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LoggingBrokerPlugin.LOG.info("Beginning transaction: {}", xid.getTransactionKey());
        }
        super.beginTransaction(context, xid);
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        if (this.isLogAll() || this.isLogTransactionEvents()) {
            LoggingBrokerPlugin.LOG.info("Forgetting transaction: {}", transactionId.getTransactionKey());
        }
        super.forgetTransaction(context, transactionId);
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        final Connection[] result = super.getClients();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LoggingBrokerPlugin.LOG.info("Get Clients returned empty list.");
            }
            else {
                final StringBuffer cids = new StringBuffer();
                for (final Connection c : result) {
                    cids.append((cids.length() > 0) ? ", " : "");
                    cids.append(c.getConnectionId());
                }
                LoggingBrokerPlugin.LOG.info("Connected clients: {}", cids);
            }
        }
        return super.getClients();
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean create) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding destination: {}:{}", destination.getDestinationTypeAsString(), destination.getPhysicalName());
        }
        return super.addDestination(context, destination, create);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing destination: {}:{}", destination.getDestinationTypeAsString(), destination.getPhysicalName());
        }
        super.removeDestination(context, destination, timeout);
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        final ActiveMQDestination[] result = super.getDestinations();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LoggingBrokerPlugin.LOG.info("Get Destinations returned empty list.");
            }
            else {
                final StringBuffer destinations = new StringBuffer();
                for (final ActiveMQDestination dest : result) {
                    destinations.append((destinations.length() > 0) ? ", " : "");
                    destinations.append(dest.getPhysicalName());
                }
                LoggingBrokerPlugin.LOG.info("Get Destinations: {}", destinations);
            }
        }
        return result;
    }
    
    @Override
    public void start() throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Starting {}", this.getBrokerName());
        }
        super.start();
    }
    
    @Override
    public void stop() throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Stopping {}", this.getBrokerName());
        }
        super.stop();
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogSessionEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding Session: {}", info);
        }
        super.addSession(context, info);
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        if (this.isLogAll() || this.isLogSessionEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing Session: {}", info);
        }
        super.removeSession(context, info);
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding Broker {}", info.getBrokerName());
        }
        super.addBroker(connection, info);
    }
    
    @Override
    public void removeBroker(final Connection connection, final BrokerInfo info) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing Broker {}", info.getBrokerName());
        }
        super.removeBroker(connection, info);
    }
    
    @Override
    public BrokerInfo[] getPeerBrokerInfos() {
        final BrokerInfo[] result = super.getPeerBrokerInfos();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LoggingBrokerPlugin.LOG.info("Get Peer Broker Infos returned empty list.");
            }
            else {
                final StringBuffer peers = new StringBuffer();
                for (final BrokerInfo bi : result) {
                    peers.append((peers.length() > 0) ? ", " : "");
                    peers.append(bi.getBrokerName());
                }
                LoggingBrokerPlugin.LOG.info("Get Peer Broker Infos: {}", peers);
            }
        }
        return result;
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("preProcessDispatch: {}", messageDispatch);
        }
        super.preProcessDispatch(messageDispatch);
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("postProcessDispatch: {}", messageDispatch);
        }
        super.postProcessDispatch(messageDispatch);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents() || this.isLogConsumerEvents()) {
            LoggingBrokerPlugin.LOG.info("ProcessDispatchNotification: {}", messageDispatchNotification);
        }
        super.processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        final Set<ActiveMQDestination> result = super.getDurableDestinations();
        if (this.isLogAll() || this.isLogInternalEvents()) {
            if (result == null) {
                LoggingBrokerPlugin.LOG.info("Get Durable Destinations returned empty list.");
            }
            else {
                final StringBuffer destinations = new StringBuffer();
                for (final ActiveMQDestination dest : result) {
                    destinations.append((destinations.length() > 0) ? ", " : "");
                    destinations.append(dest.getPhysicalName());
                }
                LoggingBrokerPlugin.LOG.info("Get Durable Destinations: {}", destinations);
            }
        }
        return result;
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Adding destination info: {}", info);
        }
        super.addDestinationInfo(context, info);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Removing destination info: {}", info);
        }
        super.removeDestinationInfo(context, info);
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference message, final Subscription subscription) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = message.getMessage().toString();
            LoggingBrokerPlugin.LOG.info("Message has expired: {}", msg);
        }
        super.messageExpired(context, message, subscription);
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LoggingBrokerPlugin.LOG.info("Sending to DLQ: {}", msg);
        }
        return super.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
        if (this.isLogAll() || this.isLogProducerEvents() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Fast Producer: {}", producerInfo);
        }
        super.fastProducer(context, producerInfo, destination);
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
        if (this.isLogAll() || this.isLogProducerEvents() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Destination is full: {}", destination.getName());
        }
        super.isFull(context, destination, usage);
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LoggingBrokerPlugin.LOG.info("Message consumed: {}", msg);
        }
        super.messageConsumed(context, messageReference);
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LoggingBrokerPlugin.LOG.info("Message delivered: {}", msg);
        }
        super.messageDelivered(context, messageReference);
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            String msg = "Unable to display message.";
            msg = messageReference.getMessage().toString();
            LoggingBrokerPlugin.LOG.info("Message discarded: {}", msg);
        }
        super.messageDiscarded(context, sub, messageReference);
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination destination, final Subscription subs) {
        if (this.isLogAll() || this.isLogConsumerEvents() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Detected slow consumer on {}", destination.getName());
            final StringBuffer buf = new StringBuffer("Connection(");
            buf.append(subs.getConsumerInfo().getConsumerId().getConnectionId());
            buf.append(") Session(");
            buf.append(subs.getConsumerInfo().getConsumerId().getSessionId());
            buf.append(")");
            LoggingBrokerPlugin.LOG.info(buf.toString());
        }
        super.slowConsumer(context, destination, subs);
    }
    
    @Override
    public void nowMasterBroker() {
        if (this.isLogAll() || this.isLogInternalEvents()) {
            LoggingBrokerPlugin.LOG.info("Is now the master broker: {}", this.getBrokerName());
        }
        super.nowMasterBroker();
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("LoggingBrokerPlugin(");
        buf.append("logAll=");
        buf.append(this.isLogAll());
        buf.append(", logConnectionEvents=");
        buf.append(this.isLogConnectionEvents());
        buf.append(", logSessionEvents=");
        buf.append(this.isLogSessionEvents());
        buf.append(", logConsumerEvents=");
        buf.append(this.isLogConsumerEvents());
        buf.append(", logProducerEvents=");
        buf.append(this.isLogProducerEvents());
        buf.append(", logMessageEvents=");
        buf.append(this.isLogMessageEvents());
        buf.append(", logTransactionEvents=");
        buf.append(this.isLogTransactionEvents());
        buf.append(", logInternalEvents=");
        buf.append(this.isLogInternalEvents());
        buf.append(")");
        return buf.toString();
    }
    
    public void setPerDestinationLogger(final boolean perDestinationLogger) {
        this.perDestinationLogger = perDestinationLogger;
    }
    
    public boolean isPerDestinationLogger() {
        return this.perDestinationLogger;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LoggingBrokerPlugin.class);
    }
}
