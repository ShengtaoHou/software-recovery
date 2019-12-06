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
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.BrokerId;
import java.util.Set;
import java.util.Collections;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;

public class ErrorBroker implements Broker
{
    private final String message;
    
    public ErrorBroker(final String message) {
        this.message = message;
    }
    
    @Override
    public Map<ActiveMQDestination, Destination> getDestinationMap() {
        return (Map<ActiveMQDestination, Destination>)Collections.EMPTY_MAP;
    }
    
    @Override
    public Set getDestinations(final ActiveMQDestination destination) {
        return Collections.EMPTY_SET;
    }
    
    @Override
    public Broker getAdaptor(final Class type) {
        if (type.isInstance(this)) {
            return this;
        }
        return null;
    }
    
    @Override
    public BrokerId getBrokerId() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public String getBrokerName() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean flag) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message message) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void gc() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void start() throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void stop() throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeBroker(final Connection connection, final BrokerInfo info) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public BrokerInfo[] getPeerBrokerInfos() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public boolean isStopped() {
        return true;
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public boolean isFaultTolerantConfiguration() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public ConnectionContext getAdminConnectionContext() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void setAdminConnectionContext(final ConnectionContext adminConnectionContext) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public PListStore getTempDataStore() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public URI getVmConnectorURI() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void brokerServiceStarted() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public BrokerService getBrokerService() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public boolean isExpired(final MessageReference messageReference) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference message, final Subscription subscription) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Broker getRoot() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public long getBrokerSequenceId() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination destination, final Subscription subs) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void nowMasterBroker() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void reapplyInterceptor() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public Scheduler getScheduler() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public ThreadPoolExecutor getExecutor() {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void networkBridgeStarted(final BrokerInfo brokerInfo, final boolean createdByDuplex, final String remoteIp) {
        throw new BrokerStoppedException(this.message);
    }
    
    @Override
    public void networkBridgeStopped(final BrokerInfo brokerInfo) {
        throw new BrokerStoppedException(this.message);
    }
}
