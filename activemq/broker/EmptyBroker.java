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
import java.util.Set;
import java.util.Collections;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Map;
import org.apache.activemq.command.BrokerId;

public class EmptyBroker implements Broker
{
    @Override
    public BrokerId getBrokerId() {
        return null;
    }
    
    @Override
    public String getBrokerName() {
        return null;
    }
    
    @Override
    public Broker getAdaptor(final Class type) {
        if (type.isInstance(this)) {
            return this;
        }
        return null;
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
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
    }
    
    @Override
    public void addSession(final ConnectionContext context, final SessionInfo info) throws Exception {
    }
    
    @Override
    public void removeSession(final ConnectionContext context, final SessionInfo info) throws Exception {
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        return null;
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        return null;
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        return null;
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        return 0;
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean flag) throws Exception {
        return null;
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        return null;
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message message) throws Exception {
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
    }
    
    @Override
    public void gc() {
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public void addBroker(final Connection connection, final BrokerInfo info) {
    }
    
    @Override
    public void removeBroker(final Connection connection, final BrokerInfo info) {
    }
    
    @Override
    public BrokerInfo[] getPeerBrokerInfos() {
        return null;
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
    }
    
    @Override
    public boolean isStopped() {
        return false;
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        return null;
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
    }
    
    @Override
    public boolean isFaultTolerantConfiguration() {
        return false;
    }
    
    @Override
    public ConnectionContext getAdminConnectionContext() {
        return null;
    }
    
    @Override
    public void setAdminConnectionContext(final ConnectionContext adminConnectionContext) {
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        return null;
    }
    
    @Override
    public PListStore getTempDataStore() {
        return null;
    }
    
    @Override
    public URI getVmConnectorURI() {
        return null;
    }
    
    @Override
    public void brokerServiceStarted() {
    }
    
    @Override
    public BrokerService getBrokerService() {
        return null;
    }
    
    @Override
    public boolean isExpired(final MessageReference messageReference) {
        return false;
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference message, final Subscription subscription) {
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference messageReference, final Subscription subscription, final Throwable poisonCause) {
        return false;
    }
    
    @Override
    public Broker getRoot() {
        return null;
    }
    
    @Override
    public long getBrokerSequenceId() {
        return -1L;
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo, final ActiveMQDestination destination) {
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Destination destination, final Usage usage) {
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Destination destination, final Subscription subs) {
    }
    
    @Override
    public void nowMasterBroker() {
    }
    
    @Override
    public void networkBridgeStarted(final BrokerInfo brokerInfo, final boolean createdByDuplex, final String remoteIp) {
    }
    
    @Override
    public void networkBridgeStopped(final BrokerInfo brokerInfo) {
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
    }
    
    @Override
    public void reapplyInterceptor() {
    }
    
    @Override
    public Scheduler getScheduler() {
        return null;
    }
    
    @Override
    public ThreadPoolExecutor getExecutor() {
        return null;
    }
}
