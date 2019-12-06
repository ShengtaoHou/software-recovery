// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.MessageAck;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import javax.jms.ResourceAllocationException;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.Message;
import java.io.IOException;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.broker.region.policy.SlowConsumerStrategy;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQDestination;

public abstract class BaseDestination implements Destination
{
    public static final int MAX_PAGE_SIZE = 200;
    public static final int MAX_BROWSE_PAGE_SIZE = 400;
    public static final long EXPIRE_MESSAGE_PERIOD = 30000L;
    public static final long DEFAULT_INACTIVE_TIMEOUT_BEFORE_GC = 60000L;
    public static final int MAX_PRODUCERS_TO_AUDIT = 64;
    public static final int MAX_AUDIT_DEPTH = 2048;
    protected final ActiveMQDestination destination;
    protected final Broker broker;
    protected final MessageStore store;
    protected SystemUsage systemUsage;
    protected MemoryUsage memoryUsage;
    private boolean producerFlowControl;
    private boolean alwaysRetroactive;
    protected boolean warnOnProducerFlowControl;
    protected long blockedProducerWarningInterval;
    private int maxProducersToAudit;
    private int maxAuditDepth;
    private boolean enableAudit;
    private int maxPageSize;
    private int maxBrowsePageSize;
    private boolean useCache;
    private int minimumMessageSize;
    private boolean lazyDispatch;
    private boolean advisoryForSlowConsumers;
    private boolean advisoryForFastProducers;
    private boolean advisoryForDiscardingMessages;
    private boolean advisoryWhenFull;
    private boolean advisoryForDelivery;
    private boolean advisoryForConsumed;
    private boolean sendAdvisoryIfNoConsumers;
    protected final DestinationStatistics destinationStatistics;
    protected final BrokerService brokerService;
    protected final Broker regionBroker;
    protected DeadLetterStrategy deadLetterStrategy;
    protected long expireMessagesPeriod;
    private int maxExpirePageSize;
    protected int cursorMemoryHighWaterMark;
    protected int storeUsageHighWaterMark;
    private SlowConsumerStrategy slowConsumerStrategy;
    private boolean prioritizedMessages;
    private long inactiveTimoutBeforeGC;
    private boolean gcIfInactive;
    private boolean gcWithNetworkConsumers;
    private long lastActiveTime;
    private boolean reduceMemoryFootprint;
    protected final Scheduler scheduler;
    private boolean disposed;
    private boolean doOptimzeMessageStorage;
    private int optimizeMessageStoreInFlightLimit;
    private boolean persistJMSRedelivered;
    
    public BaseDestination(final BrokerService brokerService, final MessageStore store, final ActiveMQDestination destination, final DestinationStatistics parentStats) throws Exception {
        this.producerFlowControl = true;
        this.alwaysRetroactive = false;
        this.warnOnProducerFlowControl = true;
        this.blockedProducerWarningInterval = 30000L;
        this.maxProducersToAudit = 1024;
        this.maxAuditDepth = 2048;
        this.enableAudit = true;
        this.maxPageSize = 200;
        this.maxBrowsePageSize = 400;
        this.useCache = true;
        this.minimumMessageSize = 1024;
        this.lazyDispatch = false;
        this.destinationStatistics = new DestinationStatistics();
        this.deadLetterStrategy = BaseDestination.DEFAULT_DEAD_LETTER_STRATEGY;
        this.expireMessagesPeriod = 30000L;
        this.maxExpirePageSize = 400;
        this.cursorMemoryHighWaterMark = 70;
        this.storeUsageHighWaterMark = 100;
        this.inactiveTimoutBeforeGC = 60000L;
        this.lastActiveTime = 0L;
        this.reduceMemoryFootprint = false;
        this.disposed = false;
        this.doOptimzeMessageStorage = true;
        this.optimizeMessageStoreInFlightLimit = 10;
        this.brokerService = brokerService;
        this.broker = brokerService.getBroker();
        this.store = store;
        this.destination = destination;
        this.destinationStatistics.setEnabled(parentStats.isEnabled());
        this.destinationStatistics.setParent(parentStats);
        this.systemUsage = new SystemUsage(brokerService.getProducerSystemUsage(), destination.toString());
        (this.memoryUsage = this.systemUsage.getMemoryUsage()).setUsagePortion(1.0f);
        this.regionBroker = brokerService.getRegionBroker();
        this.scheduler = brokerService.getBroker().getScheduler();
    }
    
    public void initialize() throws Exception {
        if (this.store != null) {
            this.store.setMemoryUsage(this.memoryUsage);
        }
    }
    
    @Override
    public boolean isProducerFlowControl() {
        return this.producerFlowControl;
    }
    
    @Override
    public void setProducerFlowControl(final boolean producerFlowControl) {
        this.producerFlowControl = producerFlowControl;
    }
    
    @Override
    public boolean isAlwaysRetroactive() {
        return this.alwaysRetroactive;
    }
    
    @Override
    public void setAlwaysRetroactive(final boolean alwaysRetroactive) {
        this.alwaysRetroactive = alwaysRetroactive;
    }
    
    @Override
    public void setBlockedProducerWarningInterval(final long blockedProducerWarningInterval) {
        this.blockedProducerWarningInterval = blockedProducerWarningInterval;
    }
    
    @Override
    public long getBlockedProducerWarningInterval() {
        return this.blockedProducerWarningInterval;
    }
    
    @Override
    public int getMaxProducersToAudit() {
        return this.maxProducersToAudit;
    }
    
    @Override
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.maxProducersToAudit = maxProducersToAudit;
    }
    
    @Override
    public int getMaxAuditDepth() {
        return this.maxAuditDepth;
    }
    
    @Override
    public void setMaxAuditDepth(final int maxAuditDepth) {
        this.maxAuditDepth = maxAuditDepth;
    }
    
    @Override
    public boolean isEnableAudit() {
        return this.enableAudit;
    }
    
    @Override
    public void setEnableAudit(final boolean enableAudit) {
        this.enableAudit = enableAudit;
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.destinationStatistics.getProducers().increment();
        this.lastActiveTime = 0L;
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.destinationStatistics.getProducers().decrement();
    }
    
    @Override
    public void addSubscription(final ConnectionContext context, final Subscription sub) throws Exception {
        this.destinationStatistics.getConsumers().increment();
        this.lastActiveTime = 0L;
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final Subscription sub, final long lastDeliveredSequenceId) throws Exception {
        this.destinationStatistics.getConsumers().decrement();
    }
    
    @Override
    public final MemoryUsage getMemoryUsage() {
        return this.memoryUsage;
    }
    
    @Override
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    @Override
    public DestinationStatistics getDestinationStatistics() {
        return this.destinationStatistics;
    }
    
    @Override
    public ActiveMQDestination getActiveMQDestination() {
        return this.destination;
    }
    
    @Override
    public final String getName() {
        return this.getActiveMQDestination().getPhysicalName();
    }
    
    @Override
    public final MessageStore getMessageStore() {
        return this.store;
    }
    
    @Override
    public boolean isActive() {
        boolean isActive = this.destinationStatistics.getConsumers().getCount() != 0L || this.destinationStatistics.getProducers().getCount() != 0L;
        if (isActive && this.isGcWithNetworkConsumers() && this.destinationStatistics.getConsumers().getCount() != 0L) {
            isActive = this.hasRegularConsumers(this.getConsumers());
        }
        return isActive;
    }
    
    @Override
    public int getMaxPageSize() {
        return this.maxPageSize;
    }
    
    @Override
    public void setMaxPageSize(final int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }
    
    @Override
    public int getMaxBrowsePageSize() {
        return (this.maxBrowsePageSize > 0) ? this.maxBrowsePageSize : this.getMaxPageSize();
    }
    
    @Override
    public void setMaxBrowsePageSize(final int maxPageSize) {
        this.maxBrowsePageSize = maxPageSize;
    }
    
    public int getMaxExpirePageSize() {
        return this.maxExpirePageSize;
    }
    
    public void setMaxExpirePageSize(final int maxPageSize) {
        this.maxExpirePageSize = maxPageSize;
    }
    
    public void setExpireMessagesPeriod(final long expireMessagesPeriod) {
        this.expireMessagesPeriod = expireMessagesPeriod;
    }
    
    public long getExpireMessagesPeriod() {
        return this.expireMessagesPeriod;
    }
    
    @Override
    public boolean isUseCache() {
        return this.useCache;
    }
    
    @Override
    public void setUseCache(final boolean useCache) {
        this.useCache = useCache;
    }
    
    @Override
    public int getMinimumMessageSize() {
        return this.minimumMessageSize;
    }
    
    @Override
    public void setMinimumMessageSize(final int minimumMessageSize) {
        this.minimumMessageSize = minimumMessageSize;
    }
    
    @Override
    public boolean isLazyDispatch() {
        return this.lazyDispatch;
    }
    
    @Override
    public void setLazyDispatch(final boolean lazyDispatch) {
        this.lazyDispatch = lazyDispatch;
    }
    
    protected long getDestinationSequenceId() {
        return this.regionBroker.getBrokerSequenceId();
    }
    
    public boolean isAdvisoryForSlowConsumers() {
        return this.advisoryForSlowConsumers;
    }
    
    public void setAdvisoryForSlowConsumers(final boolean advisoryForSlowConsumers) {
        this.advisoryForSlowConsumers = advisoryForSlowConsumers;
    }
    
    public boolean isAdvisoryForDiscardingMessages() {
        return this.advisoryForDiscardingMessages;
    }
    
    public void setAdvisoryForDiscardingMessages(final boolean advisoryForDiscardingMessages) {
        this.advisoryForDiscardingMessages = advisoryForDiscardingMessages;
    }
    
    public boolean isAdvisoryWhenFull() {
        return this.advisoryWhenFull;
    }
    
    public void setAdvisoryWhenFull(final boolean advisoryWhenFull) {
        this.advisoryWhenFull = advisoryWhenFull;
    }
    
    public boolean isAdvisoryForDelivery() {
        return this.advisoryForDelivery;
    }
    
    public void setAdvisoryForDelivery(final boolean advisoryForDelivery) {
        this.advisoryForDelivery = advisoryForDelivery;
    }
    
    public boolean isAdvisoryForConsumed() {
        return this.advisoryForConsumed;
    }
    
    public void setAdvisoryForConsumed(final boolean advisoryForConsumed) {
        this.advisoryForConsumed = advisoryForConsumed;
    }
    
    public boolean isAdvisoryForFastProducers() {
        return this.advisoryForFastProducers;
    }
    
    public void setAdvisoryForFastProducers(final boolean advisoryForFastProducers) {
        this.advisoryForFastProducers = advisoryForFastProducers;
    }
    
    public boolean isSendAdvisoryIfNoConsumers() {
        return this.sendAdvisoryIfNoConsumers;
    }
    
    public void setSendAdvisoryIfNoConsumers(final boolean sendAdvisoryIfNoConsumers) {
        this.sendAdvisoryIfNoConsumers = sendAdvisoryIfNoConsumers;
    }
    
    @Override
    public DeadLetterStrategy getDeadLetterStrategy() {
        return this.deadLetterStrategy;
    }
    
    public void setDeadLetterStrategy(final DeadLetterStrategy deadLetterStrategy) {
        this.deadLetterStrategy = deadLetterStrategy;
    }
    
    @Override
    public int getCursorMemoryHighWaterMark() {
        return this.cursorMemoryHighWaterMark;
    }
    
    @Override
    public void setCursorMemoryHighWaterMark(final int cursorMemoryHighWaterMark) {
        this.cursorMemoryHighWaterMark = cursorMemoryHighWaterMark;
    }
    
    @Override
    public void messageConsumed(final ConnectionContext context, final MessageReference messageReference) {
        if (this.advisoryForConsumed) {
            this.broker.messageConsumed(context, messageReference);
        }
    }
    
    @Override
    public void messageDelivered(final ConnectionContext context, final MessageReference messageReference) {
        if (this.advisoryForDelivery) {
            this.broker.messageDelivered(context, messageReference);
        }
    }
    
    @Override
    public void messageDiscarded(final ConnectionContext context, final Subscription sub, final MessageReference messageReference) {
        if (this.advisoryForDiscardingMessages) {
            this.broker.messageDiscarded(context, sub, messageReference);
        }
    }
    
    @Override
    public void slowConsumer(final ConnectionContext context, final Subscription subs) {
        if (this.advisoryForSlowConsumers) {
            this.broker.slowConsumer(context, this, subs);
        }
        if (this.slowConsumerStrategy != null) {
            this.slowConsumerStrategy.slowConsumer(context, subs);
        }
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo) {
        if (this.advisoryForFastProducers) {
            this.broker.fastProducer(context, producerInfo, this.getActiveMQDestination());
        }
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Usage<?> usage) {
        if (this.advisoryWhenFull) {
            this.broker.isFull(context, this, usage);
        }
    }
    
    @Override
    public void dispose(final ConnectionContext context) throws IOException {
        if (this.store != null) {
            this.store.removeAllMessages(context);
            this.store.dispose(context);
        }
        this.destinationStatistics.setParent(null);
        this.memoryUsage.stop();
        this.disposed = true;
    }
    
    @Override
    public boolean isDisposed() {
        return this.disposed;
    }
    
    protected void onMessageWithNoConsumers(final ConnectionContext context, final Message msg) throws Exception {
        if (!msg.isPersistent() && this.isSendAdvisoryIfNoConsumers() && (this.destination.isQueue() || !AdvisorySupport.isAdvisoryTopic(this.destination))) {
            final Message message = msg.copy();
            if (message.getOriginalDestination() != null) {
                message.setOriginalDestination(message.getDestination());
            }
            if (message.getOriginalTransactionId() != null) {
                message.setOriginalTransactionId(message.getTransactionId());
            }
            ActiveMQTopic advisoryTopic;
            if (this.destination.isQueue()) {
                advisoryTopic = AdvisorySupport.getNoQueueConsumersAdvisoryTopic(this.destination);
            }
            else {
                advisoryTopic = AdvisorySupport.getNoTopicConsumersAdvisoryTopic(this.destination);
            }
            message.setDestination(advisoryTopic);
            message.setTransactionId(null);
            final boolean originalFlowControl = context.isProducerFlowControl();
            try {
                context.setProducerFlowControl(false);
                final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
                producerExchange.setMutable(false);
                producerExchange.setConnectionContext(context);
                producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
                context.getBroker().send(producerExchange, message);
            }
            finally {
                context.setProducerFlowControl(originalFlowControl);
            }
        }
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
    }
    
    public final int getStoreUsageHighWaterMark() {
        return this.storeUsageHighWaterMark;
    }
    
    public void setStoreUsageHighWaterMark(final int storeUsageHighWaterMark) {
        this.storeUsageHighWaterMark = storeUsageHighWaterMark;
    }
    
    protected final void waitForSpace(final ConnectionContext context, final ProducerBrokerExchange producerBrokerExchange, final Usage<?> usage, final String warning) throws IOException, InterruptedException, ResourceAllocationException {
        this.waitForSpace(context, producerBrokerExchange, usage, 100, warning);
    }
    
    protected final void waitForSpace(final ConnectionContext context, final ProducerBrokerExchange producerBrokerExchange, final Usage<?> usage, final int highWaterMark, final String warning) throws IOException, InterruptedException, ResourceAllocationException {
        if (!context.isNetworkConnection() && this.systemUsage.isSendFailIfNoSpace()) {
            this.getLog().debug("sendFailIfNoSpace, forcing exception on send, usage: {}: {}", usage, warning);
            throw new ResourceAllocationException(warning);
        }
        if (!context.isNetworkConnection() && this.systemUsage.getSendFailIfNoSpaceAfterTimeout() != 0L) {
            if (!usage.waitForSpace(this.systemUsage.getSendFailIfNoSpaceAfterTimeout(), highWaterMark)) {
                this.getLog().debug("sendFailIfNoSpaceAfterTimeout expired, forcing exception on send, usage: {}: {}", usage, warning);
                throw new ResourceAllocationException(warning);
            }
        }
        else {
            long nextWarn;
            final long start = nextWarn = System.currentTimeMillis();
            producerBrokerExchange.blockingOnFlowControl(true);
            this.destinationStatistics.getBlockedSends().increment();
            while (!usage.waitForSpace(1000L, highWaterMark)) {
                if (context.getStopping().get()) {
                    throw new IOException("Connection closed, send aborted.");
                }
                final long now = System.currentTimeMillis();
                if (now < nextWarn) {
                    continue;
                }
                this.getLog().info("{}: {} (blocking for: {}s)", usage, warning, new Long((now - start) / 1000L));
                nextWarn = now + this.blockedProducerWarningInterval;
            }
            final long finish = System.currentTimeMillis();
            final long totalTimeBlocked = finish - start;
            this.destinationStatistics.getBlockedTime().addTime(totalTimeBlocked);
            producerBrokerExchange.incrementTimeBlocked(this, totalTimeBlocked);
            producerBrokerExchange.blockingOnFlowControl(false);
        }
    }
    
    protected abstract Logger getLog();
    
    public void setSlowConsumerStrategy(final SlowConsumerStrategy slowConsumerStrategy) {
        this.slowConsumerStrategy = slowConsumerStrategy;
    }
    
    @Override
    public SlowConsumerStrategy getSlowConsumerStrategy() {
        return this.slowConsumerStrategy;
    }
    
    @Override
    public boolean isPrioritizedMessages() {
        return this.prioritizedMessages;
    }
    
    public void setPrioritizedMessages(final boolean prioritizedMessages) {
        this.prioritizedMessages = prioritizedMessages;
        if (this.store != null) {
            this.store.setPrioritizedMessages(prioritizedMessages);
        }
    }
    
    @Override
    public long getInactiveTimoutBeforeGC() {
        return this.inactiveTimoutBeforeGC;
    }
    
    public void setInactiveTimoutBeforeGC(final long inactiveTimoutBeforeGC) {
        this.inactiveTimoutBeforeGC = inactiveTimoutBeforeGC;
    }
    
    public boolean isGcIfInactive() {
        return this.gcIfInactive;
    }
    
    public void setGcIfInactive(final boolean gcIfInactive) {
        this.gcIfInactive = gcIfInactive;
    }
    
    public void setGcWithNetworkConsumers(final boolean gcWithNetworkConsumers) {
        this.gcWithNetworkConsumers = gcWithNetworkConsumers;
    }
    
    public boolean isGcWithNetworkConsumers() {
        return this.gcWithNetworkConsumers;
    }
    
    @Override
    public void markForGC(final long timeStamp) {
        if (this.isGcIfInactive() && this.lastActiveTime == 0L && !this.isActive() && this.destinationStatistics.messages.getCount() == 0L && this.getInactiveTimoutBeforeGC() > 0L) {
            this.lastActiveTime = timeStamp;
        }
    }
    
    @Override
    public boolean canGC() {
        boolean result = false;
        if (this.isGcIfInactive() && this.lastActiveTime != 0L && System.currentTimeMillis() - this.lastActiveTime >= this.getInactiveTimoutBeforeGC()) {
            result = true;
        }
        return result;
    }
    
    public void setReduceMemoryFootprint(final boolean reduceMemoryFootprint) {
        this.reduceMemoryFootprint = reduceMemoryFootprint;
    }
    
    protected boolean isReduceMemoryFootprint() {
        return this.reduceMemoryFootprint;
    }
    
    @Override
    public boolean isDoOptimzeMessageStorage() {
        return this.doOptimzeMessageStorage;
    }
    
    @Override
    public void setDoOptimzeMessageStorage(final boolean doOptimzeMessageStorage) {
        this.doOptimzeMessageStorage = doOptimzeMessageStorage;
    }
    
    public int getOptimizeMessageStoreInFlightLimit() {
        return this.optimizeMessageStoreInFlightLimit;
    }
    
    public void setOptimizeMessageStoreInFlightLimit(final int optimizeMessageStoreInFlightLimit) {
        this.optimizeMessageStoreInFlightLimit = optimizeMessageStoreInFlightLimit;
    }
    
    @Override
    public abstract List<Subscription> getConsumers();
    
    protected boolean hasRegularConsumers(final List<Subscription> consumers) {
        boolean hasRegularConsumers = false;
        for (final Subscription subscription : consumers) {
            if (!subscription.getConsumerInfo().isNetworkSubscription()) {
                hasRegularConsumers = true;
                break;
            }
        }
        return hasRegularConsumers;
    }
    
    public ConnectionContext createConnectionContext() {
        final ConnectionContext answer = new ConnectionContext(new NonCachedMessageEvaluationContext());
        answer.setBroker(this.broker);
        answer.getMessageEvaluationContext().setDestination(this.getActiveMQDestination());
        answer.setSecurityContext(SecurityContext.BROKER_SECURITY_CONTEXT);
        return answer;
    }
    
    protected MessageAck convertToNonRangedAck(MessageAck ack, final MessageReference node) {
        if (ack.getMessageCount() > 0) {
            final MessageAck a = new MessageAck();
            ack.copy(a);
            ack = a;
            ack.setFirstMessageId(node.getMessageId());
            ack.setLastMessageId(node.getMessageId());
            ack.setMessageCount(1);
        }
        return ack;
    }
    
    @Override
    public boolean isDLQ() {
        return this.getDeadLetterStrategy().isDLQ(this.getActiveMQDestination());
    }
    
    @Override
    public void duplicateFromStore(final Message message, final Subscription durableSub) {
        final ConnectionContext connectionContext = this.createConnectionContext();
        this.getLog().warn("duplicate message from store {}, redirecting for dlq processing", message.getMessageId());
        final Throwable cause = new Throwable("duplicate from store for " + this.destination);
        message.setRegionDestination(this);
        this.broker.getRoot().sendToDeadLetterQueue(connectionContext, message, null, cause);
        final MessageAck messageAck = new MessageAck(message, (byte)1, 1);
        messageAck.setPoisonCause(cause);
        try {
            this.acknowledge(connectionContext, durableSub, messageAck, message);
        }
        catch (IOException e) {
            this.getLog().error("Failed to acknowledge duplicate message {} from {} with {}", message.getMessageId(), this.destination, messageAck);
        }
    }
    
    public void setPersistJMSRedelivered(final boolean persistJMSRedelivered) {
        this.persistJMSRedelivered = persistJMSRedelivered;
    }
    
    public boolean isPersistJMSRedelivered() {
        return this.persistJMSRedelivered;
    }
}
