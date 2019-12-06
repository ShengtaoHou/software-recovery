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
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
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
import org.apache.activemq.command.MessageAck;
import java.util.Set;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MutableBrokerFilter implements Broker
{
    protected AtomicReference<Broker> next;
    
    public MutableBrokerFilter(final Broker next) {
        (this.next = new AtomicReference<Broker>()).set(next);
    }
    
    @Override
    public Broker getAdaptor(final Class type) {
        if (type.isInstance(this)) {
            return this;
        }
        return this.next.get().getAdaptor(type);
    }
    
    public Broker getNext() {
        return this.next.get();
    }
    
    public void setNext(final Broker next) {
        this.next.set(next);
    }
    
    @Override
    public Map<ActiveMQDestination, Destination> getDestinationMap() {
        return this.getNext().getDestinationMap();
    }
    
    @Override
    public Set getDestinations(final ActiveMQDestination destination) {
        return this.getNext().getDestinations(destination);
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        this.getNext().acknowledge(consumerExchange, ack);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        this.getNext().addConnection(context, info);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        return this.getNext().addConsumer(context, info);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.getNext().addProducer(context, info);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        this.getNext().commitTransaction(context, xid, onePhase);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        this.getNext().removeSubscription(context, info);
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        return this.getNext().getPreparedTransactions(context);
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        return this.getNext().prepareTransaction(context, xid);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        this.getNext().removeConnection(context, info, error);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        this.getNext().removeConsumer(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.getNext().removeProducer(context, info);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.getNext().rollbackTransaction(context, xid);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        this.getNext().send(producerExchange, messageSend);
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        this.getNext().beginTransaction(context, xid);
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        this.getNext().forgetTransaction(context, transactionId);
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        return this.getNext().getClients();
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean createIfTemporary) throws Exception {
        return this.getNext().addDestination(context, destination, createIfTemporary);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        this.getNext().removeDestination(context, destination, timeout);
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        return this.getNext().getDestinations();
    }
    
    @Override
    public void start() throws Exception {
        this.getNext().start();
    }
    
    @Override
    public void stop() throws Exception {
        this.getNext().stop();
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.getNext().addSession(context, info);
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        this.getNext().removeSession(context, info);
    }
    
    @Override
    public BrokerId getBrokerId() {
        return this.getNext().getBrokerId();
    }
    
    @Override
    public String getBrokerName() {
        return this.getNext().getBrokerName();
    }
    
    @Override
    public void gc() {
        this.getNext().gc();
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
        this.getNext().addBroker(connection, info);
    }
    
    @Override
    public void removeBroker(final Connection connection, final BrokerInfo info) {
        this.getNext().removeBroker(connection, info);
    }
    
    @Override
    public BrokerInfo[] getPeerBrokerInfos() {
        return this.getNext().getPeerBrokerInfos();
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        this.getNext().preProcessDispatch(messageDispatch);
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
        this.getNext().postProcessDispatch(messageDispatch);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.getNext().processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public boolean isStopped() {
        return this.getNext().isStopped();
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        return this.getNext().getDurableDestinations();
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.getNext().addDestinationInfo(context, info);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.getNext().removeDestinationInfo(context, info);
    }
    
    @Override
    public boolean isFaultTolerantConfiguration() {
        return this.getNext().isFaultTolerantConfiguration();
    }
    
    @Override
    public ConnectionContext getAdminConnectionContext() {
        return this.getNext().getAdminConnectionContext();
    }
    
    @Override
    public void setAdminConnectionContext(final ConnectionContext adminConnectionContext) {
        this.getNext().setAdminConnectionContext(adminConnectionContext);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        return this.getNext().messagePull(context, pull);
    }
    
    @Override
    public PListStore getTempDataStore() {
        return this.getNext().getTempDataStore();
    }
    
    @Override
    public URI getVmConnectorURI() {
        return this.getNext().getVmConnectorURI();
    }
    
    @Override
    public void brokerServiceStarted() {
        this.getNext().brokerServiceStarted();
    }
    
    @Override
    public BrokerService getBrokerService() {
        return this.getNext().getBrokerService();
    }
    
    @Override
    public boolean isExpired(final MessageReference messageReference) {
        return this.getNext().isExpired(messageReference);
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference message, final Subscription subscription) {
        this.getNext().messageExpired(context, message, subscription);
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        return this.getNext().sendToDeadLetterQueue(context, messageReference, subscription, poisonCause);
    }
    
    @Override
    public Broker getRoot() {
        return this.getNext().getRoot();
    }
    
    @Override
    public long getBrokerSequenceId() {
        return this.getNext().getBrokerSequenceId();
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
        this.getNext().fastProducer(context, producerInfo, destination);
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
        this.getNext().isFull(context, destination, usage);
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        this.getNext().messageConsumed(context, messageReference);
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        this.getNext().messageDelivered(context, messageReference);
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        this.getNext().messageDiscarded(context, sub, messageReference);
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination dest, final Subscription subs) {
        this.getNext().slowConsumer(context, dest, subs);
    }
    
    @Override
    public void nowMasterBroker() {
        this.getNext().nowMasterBroker();
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
        this.getNext().processConsumerControl(consumerExchange, control);
    }
    
    @Override
    public void reapplyInterceptor() {
        this.getNext().reapplyInterceptor();
    }
    
    @Override
    public Scheduler getScheduler() {
        return this.getNext().getScheduler();
    }
    
    @Override
    public ThreadPoolExecutor getExecutor() {
        return this.getNext().getExecutor();
    }
    
    @Override
    public void networkBridgeStarted(final BrokerInfo brokerInfo, final boolean createdByDuplex, final String remoteIp) {
        this.getNext().networkBridgeStarted(brokerInfo, createdByDuplex, remoteIp);
    }
    
    @Override
    public void networkBridgeStopped(final BrokerInfo brokerInfo) {
        this.getNext().networkBridgeStopped(brokerInfo);
    }
}
