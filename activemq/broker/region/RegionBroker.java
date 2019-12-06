// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.util.BrokerSupport;
import java.net.URI;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.Service;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.MessageDispatch;
import java.util.Locale;
import org.apache.activemq.util.InetAddressUtil;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.DestinationInfo;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.activemq.broker.TransportConnection;
import javax.jms.InvalidClientIDException;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.broker.Broker;
import javax.jms.JMSException;
import java.util.Set;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.HashMap;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.thread.TaskRunnerFactory;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Connection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.state.ConnectionState;
import org.apache.activemq.command.ConnectionId;
import java.util.Map;
import org.apache.activemq.util.IdGenerator;
import org.slf4j.Logger;
import org.apache.activemq.broker.EmptyBroker;

public class RegionBroker extends EmptyBroker
{
    public static final String ORIGINAL_EXPIRATION = "originalExpiration";
    private static final Logger LOG;
    private static final IdGenerator BROKER_ID_GENERATOR;
    protected final DestinationStatistics destinationStatistics;
    protected DestinationFactory destinationFactory;
    protected final Map<ConnectionId, ConnectionState> connectionStates;
    private final Region queueRegion;
    private final Region topicRegion;
    private final Region tempQueueRegion;
    private final Region tempTopicRegion;
    protected final BrokerService brokerService;
    private boolean started;
    private boolean keepDurableSubsActive;
    private final CopyOnWriteArrayList<Connection> connections;
    private final Map<ActiveMQDestination, ActiveMQDestination> destinationGate;
    private final Map<ActiveMQDestination, Destination> destinations;
    private final Map<BrokerId, BrokerInfo> brokerInfos;
    private final LongSequenceGenerator sequenceGenerator;
    private BrokerId brokerId;
    private String brokerName;
    private final Map<String, ConnectionContext> clientIdSet;
    private final DestinationInterceptor destinationInterceptor;
    private ConnectionContext adminConnectionContext;
    private final Scheduler scheduler;
    private final ThreadPoolExecutor executor;
    private boolean allowTempAutoCreationOnSend;
    private final ReentrantReadWriteLock inactiveDestinationsPurgeLock;
    private final Runnable purgeInactiveDestinationsTask;
    
    public RegionBroker(final BrokerService brokerService, final TaskRunnerFactory taskRunnerFactory, final SystemUsage memoryManager, final DestinationFactory destinationFactory, final DestinationInterceptor destinationInterceptor, final Scheduler scheduler, final ThreadPoolExecutor executor) throws IOException {
        this.destinationStatistics = new DestinationStatistics();
        this.connectionStates = Collections.synchronizedMap(new HashMap<ConnectionId, ConnectionState>());
        this.connections = new CopyOnWriteArrayList<Connection>();
        this.destinationGate = new HashMap<ActiveMQDestination, ActiveMQDestination>();
        this.destinations = new ConcurrentHashMap<ActiveMQDestination, Destination>();
        this.brokerInfos = new HashMap<BrokerId, BrokerInfo>();
        this.sequenceGenerator = new LongSequenceGenerator();
        this.clientIdSet = new HashMap<String, ConnectionContext>();
        this.inactiveDestinationsPurgeLock = new ReentrantReadWriteLock();
        this.purgeInactiveDestinationsTask = new Runnable() {
            @Override
            public void run() {
                RegionBroker.this.purgeInactiveDestinations();
            }
        };
        this.brokerService = brokerService;
        this.executor = executor;
        this.scheduler = scheduler;
        if (destinationFactory == null) {
            throw new IllegalArgumentException("null destinationFactory");
        }
        this.sequenceGenerator.setLastSequenceId(destinationFactory.getLastMessageBrokerSequenceId());
        this.destinationFactory = destinationFactory;
        this.queueRegion = this.createQueueRegion(memoryManager, taskRunnerFactory, destinationFactory);
        this.topicRegion = this.createTopicRegion(memoryManager, taskRunnerFactory, destinationFactory);
        this.destinationInterceptor = destinationInterceptor;
        this.tempQueueRegion = this.createTempQueueRegion(memoryManager, taskRunnerFactory, destinationFactory);
        this.tempTopicRegion = this.createTempTopicRegion(memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    @Override
    public Map<ActiveMQDestination, Destination> getDestinationMap() {
        final Map<ActiveMQDestination, Destination> answer = new HashMap<ActiveMQDestination, Destination>(this.getQueueRegion().getDestinationMap());
        answer.putAll(this.getTopicRegion().getDestinationMap());
        return answer;
    }
    
    @Override
    public Set<Destination> getDestinations(final ActiveMQDestination destination) {
        try {
            return this.getRegion(destination).getDestinations(destination);
        }
        catch (JMSException jmse) {
            return Collections.emptySet();
        }
    }
    
    @Override
    public Broker getAdaptor(final Class type) {
        if (type.isInstance(this)) {
            return this;
        }
        return null;
    }
    
    public Region getQueueRegion() {
        return this.queueRegion;
    }
    
    public Region getTempQueueRegion() {
        return this.tempQueueRegion;
    }
    
    public Region getTempTopicRegion() {
        return this.tempTopicRegion;
    }
    
    public Region getTopicRegion() {
        return this.topicRegion;
    }
    
    protected Region createTempTopicRegion(final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        return new TempTopicRegion(this, this.destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    protected Region createTempQueueRegion(final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        return new TempQueueRegion(this, this.destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    protected Region createTopicRegion(final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        return new TopicRegion(this, this.destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    protected Region createQueueRegion(final SystemUsage memoryManager, final TaskRunnerFactory taskRunnerFactory, final DestinationFactory destinationFactory) {
        return new QueueRegion(this, this.destinationStatistics, memoryManager, taskRunnerFactory, destinationFactory);
    }
    
    @Override
    public void start() throws Exception {
        this.started = true;
        this.queueRegion.start();
        this.topicRegion.start();
        this.tempQueueRegion.start();
        this.tempTopicRegion.start();
        final int period = this.brokerService.getSchedulePeriodForDestinationPurge();
        if (period > 0) {
            this.scheduler.executePeriodically(this.purgeInactiveDestinationsTask, period);
        }
    }
    
    @Override
    public void stop() throws Exception {
        this.started = false;
        this.scheduler.cancel(this.purgeInactiveDestinationsTask);
        final ServiceStopper ss = new ServiceStopper();
        this.doStop(ss);
        ss.throwFirstException();
        this.clientIdSet.clear();
        this.connections.clear();
        this.destinations.clear();
        this.brokerInfos.clear();
    }
    
    public PolicyMap getDestinationPolicy() {
        return (this.brokerService != null) ? this.brokerService.getDestinationPolicy() : null;
    }
    
    public ConnectionContext getConnectionContext(final String clientId) {
        return this.clientIdSet.get(clientId);
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        final String clientId = info.getClientId();
        if (clientId == null) {
            throw new InvalidClientIDException("No clientID specified for connection request");
        }
        synchronized (this.clientIdSet) {
            final ConnectionContext oldContext = this.clientIdSet.get(clientId);
            if (oldContext != null) {
                if (!context.isAllowLinkStealing()) {
                    throw new InvalidClientIDException("Broker: " + this.getBrokerName() + " - Client: " + clientId + " already connected from " + oldContext.getConnection().getRemoteAddress());
                }
                this.clientIdSet.remove(clientId);
                if (oldContext.getConnection() != null) {
                    final Connection connection = oldContext.getConnection();
                    RegionBroker.LOG.warn("Stealing link for clientId {} From Connection {}", clientId, oldContext.getConnection());
                    if (connection instanceof TransportConnection) {
                        final TransportConnection transportConnection = (TransportConnection)connection;
                        transportConnection.stopAsync();
                    }
                    else {
                        connection.stop();
                    }
                }
                else {
                    RegionBroker.LOG.error("Not Connection for {}", oldContext);
                }
            }
            else {
                this.clientIdSet.put(clientId, context);
            }
        }
        this.connections.add(context.getConnection());
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        final String clientId = info.getClientId();
        if (clientId == null) {
            throw new InvalidClientIDException("No clientID specified for connection disconnect request");
        }
        synchronized (this.clientIdSet) {
            final ConnectionContext oldValue = this.clientIdSet.get(clientId);
            if (oldValue == context && this.isEqual(oldValue.getConnectionId(), info.getConnectionId())) {
                this.clientIdSet.remove(clientId);
            }
        }
        this.connections.remove(context.getConnection());
    }
    
    protected boolean isEqual(final ConnectionId connectionId, final ConnectionId connectionId2) {
        return connectionId == connectionId2 || (connectionId != null && connectionId.equals(connectionId2));
    }
    
    @Override
    public Connection[] getClients() throws Exception {
        final ArrayList<Connection> l = new ArrayList<Connection>(this.connections);
        final Connection[] rc = new Connection[l.size()];
        l.toArray(rc);
        return rc;
    }
    
    @Override
    public Destination addDestination(final ConnectionContext context, final ActiveMQDestination destination, final boolean createIfTemp) throws Exception {
        Destination answer = this.destinations.get(destination);
        if (answer != null) {
            return answer;
        }
        synchronized (this.destinationGate) {
            answer = this.destinations.get(destination);
            if (answer != null) {
                return answer;
            }
            if (this.destinationGate.get(destination) != null) {
                while (this.destinationGate.containsKey(destination)) {
                    this.destinationGate.wait();
                }
                answer = this.destinations.get(destination);
                if (answer != null) {
                    return answer;
                }
                this.destinationGate.put(destination, destination);
            }
        }
        try {
            boolean create = true;
            if (destination.isTemporary()) {
                create = createIfTemp;
            }
            answer = this.getRegion(destination).addDestination(context, destination, create);
            this.destinations.put(destination, answer);
        }
        finally {
            synchronized (this.destinationGate) {
                this.destinationGate.remove(destination);
                this.destinationGate.notifyAll();
            }
        }
        return answer;
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        if (this.destinations.containsKey(destination)) {
            this.getRegion(destination).removeDestination(context, destination, timeout);
            this.destinations.remove(destination);
        }
    }
    
    @Override
    public void addDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.addDestination(context, info.getDestination(), true);
    }
    
    @Override
    public void removeDestinationInfo(final ConnectionContext context, final DestinationInfo info) throws Exception {
        this.removeDestination(context, info.getDestination(), info.getTimeout());
    }
    
    @Override
    public ActiveMQDestination[] getDestinations() throws Exception {
        final ArrayList<ActiveMQDestination> l = new ArrayList<ActiveMQDestination>(this.getDestinationMap().keySet());
        final ActiveMQDestination[] rc = new ActiveMQDestination[l.size()];
        l.toArray(rc);
        return rc;
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        if (destination != null) {
            this.inactiveDestinationsPurgeLock.readLock().lock();
            try {
                context.getBroker().addDestination(context, destination, this.isAllowTempAutoCreationOnSend());
                this.getRegion(destination).addProducer(context, info);
            }
            finally {
                this.inactiveDestinationsPurgeLock.readLock().unlock();
            }
        }
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        if (destination != null) {
            this.inactiveDestinationsPurgeLock.readLock().lock();
            try {
                this.getRegion(destination).removeProducer(context, info);
            }
            finally {
                this.inactiveDestinationsPurgeLock.readLock().unlock();
            }
        }
    }
    
    @Override
    public Subscription addConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        if (this.destinationInterceptor != null) {
            this.destinationInterceptor.create(this, context, destination);
        }
        this.inactiveDestinationsPurgeLock.readLock().lock();
        try {
            return this.getRegion(destination).addConsumer(context, info);
        }
        finally {
            this.inactiveDestinationsPurgeLock.readLock().unlock();
        }
    }
    
    @Override
    public void removeConsumer(final ConnectionContext context, final ConsumerInfo info) throws Exception {
        final ActiveMQDestination destination = info.getDestination();
        this.inactiveDestinationsPurgeLock.readLock().lock();
        try {
            this.getRegion(destination).removeConsumer(context, info);
        }
        finally {
            this.inactiveDestinationsPurgeLock.readLock().unlock();
        }
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final RemoveSubscriptionInfo info) throws Exception {
        this.inactiveDestinationsPurgeLock.readLock().lock();
        try {
            this.topicRegion.removeSubscription(context, info);
        }
        finally {
            this.inactiveDestinationsPurgeLock.readLock().unlock();
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message message) throws Exception {
        final ActiveMQDestination destination = message.getDestination();
        message.setBrokerInTime(System.currentTimeMillis());
        if (producerExchange.isMutable() || producerExchange.getRegion() == null || (producerExchange.getRegionDestination() != null && producerExchange.getRegionDestination().isDisposed())) {
            producerExchange.getConnectionContext().getBroker().addDestination(producerExchange.getConnectionContext(), destination, this.isAllowTempAutoCreationOnSend());
            producerExchange.setRegion(this.getRegion(destination));
            producerExchange.setRegionDestination(null);
        }
        producerExchange.getRegion().send(producerExchange, message);
        if (producerExchange.isMutable()) {
            producerExchange.setRegionDestination(null);
            producerExchange.setRegion(null);
        }
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        if (consumerExchange.isWildcard() || consumerExchange.getRegion() == null) {
            final ActiveMQDestination destination = ack.getDestination();
            consumerExchange.setRegion(this.getRegion(destination));
        }
        consumerExchange.getRegion().acknowledge(consumerExchange, ack);
    }
    
    protected Region getRegion(final ActiveMQDestination destination) throws JMSException {
        switch (destination.getDestinationType()) {
            case 1: {
                return this.queueRegion;
            }
            case 2: {
                return this.topicRegion;
            }
            case 5: {
                return this.tempQueueRegion;
            }
            case 6: {
                return this.tempTopicRegion;
            }
            default: {
                throw this.createUnknownDestinationTypeException(destination);
            }
        }
    }
    
    @Override
    public Response messagePull(final ConnectionContext context, final MessagePull pull) throws Exception {
        final ActiveMQDestination destination = pull.getDestination();
        return this.getRegion(destination).messagePull(context, pull);
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId transactionId) throws Exception {
        throw new IllegalAccessException("Transaction operation not implemented by this broker.");
    }
    
    @Override
    public void gc() {
        this.queueRegion.gc();
        this.topicRegion.gc();
    }
    
    @Override
    public BrokerId getBrokerId() {
        if (this.brokerId == null) {
            this.brokerId = new BrokerId(RegionBroker.BROKER_ID_GENERATOR.generateId());
        }
        return this.brokerId;
    }
    
    public void setBrokerId(final BrokerId brokerId) {
        this.brokerId = brokerId;
    }
    
    @Override
    public String getBrokerName() {
        if (this.brokerName == null) {
            try {
                this.brokerName = InetAddressUtil.getLocalHostName().toLowerCase(Locale.ENGLISH);
            }
            catch (Exception e) {
                this.brokerName = "localhost";
            }
        }
        return this.brokerName;
    }
    
    public void setBrokerName(final String brokerName) {
        this.brokerName = brokerName;
    }
    
    public DestinationStatistics getDestinationStatistics() {
        return this.destinationStatistics;
    }
    
    protected JMSException createUnknownDestinationTypeException(final ActiveMQDestination destination) {
        return new JMSException("Unknown destination type: " + destination.getDestinationType());
    }
    
    @Override
    public synchronized void addBroker(final Connection connection, final BrokerInfo info) {
        BrokerInfo existing = this.brokerInfos.get(info.getBrokerId());
        if (existing == null) {
            existing = info.copy();
            existing.setPeerBrokerInfos(null);
            this.brokerInfos.put(info.getBrokerId(), existing);
        }
        existing.incrementRefCount();
        RegionBroker.LOG.debug("{} addBroker: {} brokerInfo size: {}", this.getBrokerName(), info.getBrokerName(), this.brokerInfos.size());
        this.addBrokerInClusterUpdate(info);
    }
    
    @Override
    public synchronized void removeBroker(final Connection connection, final BrokerInfo info) {
        if (info != null) {
            final BrokerInfo existing = this.brokerInfos.get(info.getBrokerId());
            if (existing != null && existing.decrementRefCount() == 0) {
                this.brokerInfos.remove(info.getBrokerId());
            }
            RegionBroker.LOG.debug("{} removeBroker: {} brokerInfo size: {}", this.getBrokerName(), info.getBrokerName(), this.brokerInfos.size());
            if (!this.brokerService.isStopping()) {
                this.removeBrokerInClusterUpdate(info);
            }
        }
    }
    
    @Override
    public synchronized BrokerInfo[] getPeerBrokerInfos() {
        BrokerInfo[] result = new BrokerInfo[this.brokerInfos.size()];
        result = this.brokerInfos.values().toArray(result);
        return result;
    }
    
    @Override
    public void preProcessDispatch(final MessageDispatch messageDispatch) {
        final Message message = messageDispatch.getMessage();
        if (message != null) {
            final long endTime = System.currentTimeMillis();
            message.setBrokerOutTime(endTime);
            if (this.getBrokerService().isEnableStatistics()) {
                final long totalTime = endTime - message.getBrokerInTime();
                ((Destination)message.getRegionDestination()).getDestinationStatistics().getProcessTime().addTime(totalTime);
            }
            if (((BaseDestination)message.getRegionDestination()).isPersistJMSRedelivered() && !message.isRedelivered() && message.isPersistent()) {
                final int originalValue = message.getRedeliveryCounter();
                message.incrementRedeliveryCounter();
                try {
                    ((BaseDestination)message.getRegionDestination()).getMessageStore().updateMessage(message);
                }
                catch (IOException error) {
                    RegionBroker.LOG.error("Failed to persist JMSRedeliveryFlag on {} in {}", message.getMessageId(), message.getDestination(), error);
                }
                finally {
                    message.setRedeliveryCounter(originalValue);
                }
            }
        }
    }
    
    @Override
    public void postProcessDispatch(final MessageDispatch messageDispatch) {
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        final ActiveMQDestination destination = messageDispatchNotification.getDestination();
        this.getRegion(destination).processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public boolean isStopped() {
        return !this.started;
    }
    
    @Override
    public Set<ActiveMQDestination> getDurableDestinations() {
        return this.destinationFactory.getDestinations();
    }
    
    protected void doStop(final ServiceStopper ss) {
        ss.stop(this.queueRegion);
        ss.stop(this.topicRegion);
        ss.stop(this.tempQueueRegion);
        ss.stop(this.tempTopicRegion);
    }
    
    public boolean isKeepDurableSubsActive() {
        return this.keepDurableSubsActive;
    }
    
    public void setKeepDurableSubsActive(final boolean keepDurableSubsActive) {
        this.keepDurableSubsActive = keepDurableSubsActive;
        ((TopicRegion)this.topicRegion).setKeepDurableSubsActive(keepDurableSubsActive);
    }
    
    public DestinationInterceptor getDestinationInterceptor() {
        return this.destinationInterceptor;
    }
    
    @Override
    public ConnectionContext getAdminConnectionContext() {
        return this.adminConnectionContext;
    }
    
    @Override
    public void setAdminConnectionContext(final ConnectionContext adminConnectionContext) {
        this.adminConnectionContext = adminConnectionContext;
    }
    
    public Map<ConnectionId, ConnectionState> getConnectionStates() {
        return this.connectionStates;
    }
    
    @Override
    public PListStore getTempDataStore() {
        return this.brokerService.getTempDataStore();
    }
    
    @Override
    public URI getVmConnectorURI() {
        return this.brokerService.getVmConnectorURI();
    }
    
    @Override
    public void brokerServiceStarted() {
    }
    
    @Override
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    @Override
    public boolean isExpired(final MessageReference messageReference) {
        boolean expired = false;
        if (messageReference.isExpired()) {
            try {
                final Message message = messageReference.getMessage();
                synchronized (message) {
                    expired = this.stampAsExpired(message);
                }
            }
            catch (IOException e) {
                RegionBroker.LOG.warn("unexpected exception on message expiry determination for: {}", messageReference, e);
            }
        }
        return expired;
    }
    
    private boolean stampAsExpired(final Message message) throws IOException {
        boolean stamped = false;
        if (message.getProperty("originalExpiration") == null) {
            final long expiration = message.getExpiration();
            message.setProperty("originalExpiration", new Long(expiration));
            stamped = true;
        }
        return stamped;
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final MessageReference node, final Subscription subscription) {
        RegionBroker.LOG.debug("Message expired {}", node);
        this.getRoot().sendToDeadLetterQueue(context, node, subscription, new Throwable("Message Expired. Expiration:" + node.getExpiration()));
    }
    
    @Override
    public boolean sendToDeadLetterQueue(final ConnectionContext context, final MessageReference node, final Subscription subscription, final Throwable poisonCause) {
        try {
            if (node != null) {
                Message message = node.getMessage();
                if (message != null && node.getRegionDestination() != null) {
                    final DeadLetterStrategy deadLetterStrategy = ((Destination)node.getRegionDestination()).getDeadLetterStrategy();
                    if (deadLetterStrategy != null) {
                        if (deadLetterStrategy.isSendToDeadLetterQueue(message)) {
                            message = message.copy();
                            this.stampAsExpired(message);
                            message.setExpiration(0L);
                            if (!message.isPersistent()) {
                                message.setPersistent(true);
                                message.setProperty("originalDeliveryMode", "NON_PERSISTENT");
                            }
                            if (poisonCause != null) {
                                message.setProperty("dlqDeliveryFailureCause", poisonCause.toString());
                            }
                            final ActiveMQDestination deadLetterDestination = deadLetterStrategy.getDeadLetterQueueFor(message, subscription);
                            ConnectionContext adminContext = context;
                            if (context.getSecurityContext() == null || !context.getSecurityContext().isBrokerContext()) {
                                adminContext = BrokerSupport.getConnectionContext(this);
                            }
                            BrokerSupport.resendNoCopy(adminContext, message, deadLetterDestination);
                            return true;
                        }
                    }
                    else {
                        RegionBroker.LOG.debug("Dead Letter message with no DLQ strategy in place, message id: {}, destination: {}", message.getMessageId(), message.getDestination());
                    }
                }
            }
        }
        catch (Exception e) {
            RegionBroker.LOG.warn("Caught an exception sending to DLQ: {}", node, e);
        }
        return false;
    }
    
    @Override
    public Broker getRoot() {
        try {
            return this.getBrokerService().getBroker();
        }
        catch (Exception e) {
            RegionBroker.LOG.error("Trying to get Root Broker", e);
            throw new RuntimeException("The broker from the BrokerService should not throw an exception");
        }
    }
    
    @Override
    public long getBrokerSequenceId() {
        synchronized (this.sequenceGenerator) {
            return this.sequenceGenerator.getNextSequenceId();
        }
    }
    
    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }
    
    @Override
    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }
    
    @Override
    public void processConsumerControl(final ConsumerBrokerExchange consumerExchange, final ConsumerControl control) {
        final ActiveMQDestination destination = control.getDestination();
        try {
            this.getRegion(destination).processConsumerControl(consumerExchange, control);
        }
        catch (JMSException jmse) {
            RegionBroker.LOG.warn("unmatched destination: {}, in consumerControl: {}", destination, control);
        }
    }
    
    protected void addBrokerInClusterUpdate(final BrokerInfo info) {
        final List<TransportConnector> connectors = this.brokerService.getTransportConnectors();
        for (final TransportConnector connector : connectors) {
            if (connector.isUpdateClusterClients()) {
                connector.addPeerBroker(info);
                connector.updateClientClusterInfo();
            }
        }
    }
    
    protected void removeBrokerInClusterUpdate(final BrokerInfo info) {
        final List<TransportConnector> connectors = this.brokerService.getTransportConnectors();
        for (final TransportConnector connector : connectors) {
            if (connector.isUpdateClusterClients() && connector.isUpdateClusterClientsOnRemove()) {
                connector.removePeerBroker(info);
                connector.updateClientClusterInfo();
            }
        }
    }
    
    protected void purgeInactiveDestinations() {
        this.inactiveDestinationsPurgeLock.writeLock().lock();
        try {
            final List<Destination> list = new ArrayList<Destination>();
            final Map<ActiveMQDestination, Destination> map = this.getDestinationMap();
            if (this.isAllowTempAutoCreationOnSend()) {
                map.putAll(this.tempQueueRegion.getDestinationMap());
                map.putAll(this.tempTopicRegion.getDestinationMap());
            }
            final long maxPurgedDests = this.brokerService.getMaxPurgedDestinationsPerSweep();
            final long timeStamp = System.currentTimeMillis();
            for (final Destination d : map.values()) {
                d.markForGC(timeStamp);
                if (d.canGC()) {
                    list.add(d);
                    if (maxPurgedDests > 0L && list.size() == maxPurgedDests) {
                        break;
                    }
                    continue;
                }
            }
            if (!list.isEmpty()) {
                final ConnectionContext context = BrokerSupport.getConnectionContext(this);
                context.setBroker(this);
                for (final Destination dest : list) {
                    Logger log = RegionBroker.LOG;
                    if (dest instanceof BaseDestination) {
                        log = ((BaseDestination)dest).getLog();
                    }
                    log.info("{} Inactive for longer than {} ms - removing ...", dest.getName(), dest.getInactiveTimoutBeforeGC());
                    try {
                        this.getRoot().removeDestination(context, dest.getActiveMQDestination(), this.isAllowTempAutoCreationOnSend() ? 1 : 0);
                    }
                    catch (Exception e) {
                        RegionBroker.LOG.error("Failed to remove inactive destination {}", dest, e);
                    }
                }
            }
        }
        finally {
            this.inactiveDestinationsPurgeLock.writeLock().unlock();
        }
    }
    
    public boolean isAllowTempAutoCreationOnSend() {
        return this.allowTempAutoCreationOnSend;
    }
    
    public void setAllowTempAutoCreationOnSend(final boolean allowTempAutoCreationOnSend) {
        this.allowTempAutoCreationOnSend = allowTempAutoCreationOnSend;
    }
    
    @Override
    public void reapplyInterceptor() {
        this.queueRegion.reapplyInterceptor();
        this.topicRegion.reapplyInterceptor();
        this.tempQueueRegion.reapplyInterceptor();
        this.tempTopicRegion.reapplyInterceptor();
    }
    
    static {
        LOG = LoggerFactory.getLogger(RegionBroker.class);
        BROKER_ID_GENERATOR = new IdGenerator();
    }
}
