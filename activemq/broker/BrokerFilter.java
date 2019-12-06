// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.broker.region.MessageReference;
import java.net.URI;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageAck;
import java.util.Set;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;

public class BrokerFilter implements Broker
{
    protected final Broker next;
    
    public BrokerFilter(final Broker next) {
        this.next = next;
    }
    
    @Override
    public Broker getAdaptor(final Class type) {
        if (type.isInstance(this)) {
            return this;
        }
        return this.next.getAdaptor(type);
    }
    
    @Override
    public Map<ActiveMQDestination, Destination> getDestinationMap() {
        return this.next.getDestinationMap();
    }
    
    @Override
    public Set<Destination> getDestinations(final ActiveMQDestination destination) {
        return this.next.getDestinations(destination);
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        this.next.acknowledge(consumerExchange, ack);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        return this.next.messagePull(context, pull);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        this.next.addConnection(context, info);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        return this.next.addConsumer(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.addProducer(context, info);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        this.next.commitTransaction(context, xid, onePhase);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        this.next.removeSubscription(context, info);
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        return this.next.getPreparedTransactions(context);
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        return this.next.prepareTransaction(context, xid);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        this.next.removeConnection(context, info, error);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        this.next.removeConsumer(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.removeProducer(context, info);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.next.rollbackTransaction(context, xid);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        this.next.send(producerExchange, messageSend);
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.next.beginTransaction(context, xid);
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        this.next.forgetTransaction(context, transactionId);
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        return this.next.getClients();
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean createIfTemporary) throws Exception {
        return this.next.addDestination(context, destination, createIfTemporary);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        this.next.removeDestination(context, destination, timeout);
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        return this.next.getDestinations();
    }
    
    @Override
    public void start() throws Exception {
        this.next.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.next.stop();
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.next.addSession(context, info);
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.next.removeSession(context, info);
    }
    
    @Override
    public BrokerId getBrokerId() {
        return this.next.getBrokerId();
    }
    
    @Override
    public String getBrokerName() {
        return this.next.getBrokerName();
    }
    
    @Override
    public void gc() {
        this.next.gc();
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
        this.next.addBroker(connection, info);
    }
    
    @Override
    public void removeBroker(final Connection connection, final BrokerInfo info) {
        this.next.removeBroker(connection, info);
    }
    
    @Override
    public BrokerInfo[] getPeerBrokerInfos() {
        return this.next.getPeerBrokerInfos();
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        this.next.preProcessDispatch(messageDispatch);
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
        this.next.postProcessDispatch(messageDispatch);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.next.processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public boolean isStopped() {
        return this.next.isStopped();
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        return this.next.getDurableDestinations();
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.next.addDestinationInfo(context, info);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.next.removeDestinationInfo(context, info);
    }
    
    @Override
    public boolean isFaultTolerantConfiguration() {
        return this.next.isFaultTolerantConfiguration();
    }
    
    @Override
    public ConnectionContext getAdminConnectionContext() {
        return this.next.getAdminConnectionContext();
    }
    
    @Override
    public void setAdminConnectionContext(final ConnectionContext adminConnectionContext) {
        this.next.setAdminConnectionContext(adminConnectionContext);
    }
    
    @Override
    public PListStore getTempDataStore() {
        return this.next.getTempDataStore();
    }
    
    @Override
    public URI getVmConnectorURI() {
        return this.next.getVmConnectorURI();
    }
    
    @Override
    public void brokerServiceStarted() {
        this.next.brokerServiceStarted();
    }
    
    @Override
    public BrokerService getBrokerService() {
        return this.next.getBrokerService();
    }
    
    @Override
    public boolean isExpired(final MessageReference messageReference) {
        return this.next.isExpired(messageReference);
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference message, final Subscription subscription) {
        this.next.messageExpired(context, message, subscription);
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        return this.next.sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
    }
    
    @Override
    public Broker getRoot() {
        return this.next.getRoot();
    }
    
    @Override
    public long getBrokerSequenceId() {
        return this.next.getBrokerSequenceId();
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
        this.next.fastProducer(context, producerInfo, destination);
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
        this.next.isFull(context, destination, usage);
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        this.next.messageConsumed(context, messageReference);
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        this.next.messageDelivered(context, messageReference);
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        this.next.messageDiscarded(context, sub, messageReference);
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination destination, final Subscription subs) {
        this.next.slowConsumer(context, destination, subs);
    }
    
    @Override
    public void nowMasterBroker() {
        this.next.nowMasterBroker();
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
        this.next.processConsumerControl(consumerExchange, control);
    }
    
    @Override
    public void reapplyInterceptor() {
        this.next.reapplyInterceptor();
    }
    
    @Override
    public Scheduler getScheduler() {
        return this.next.getScheduler();
    }
    
    @Override
    public ThreadPoolExecutor getExecutor() {
        return this.next.getExecutor();
    }
    
    @Override
    public void networkBridgeStarted(final BrokerInfo brokerInfo, final boolean createdByDuplex, final String remoteIp) {
        this.next.networkBridgeStarted(brokerInfo, createdByDuplex, remoteIp);
    }
    
    @Override
    public void networkBridgeStopped(final BrokerInfo brokerInfo) {
        this.next.networkBridgeStopped(brokerInfo);
    }
}
