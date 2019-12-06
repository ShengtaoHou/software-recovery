// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.region.group.GroupFactoryFinder;
import org.apache.activemq.broker.region.QueueSubscription;
import org.apache.activemq.broker.region.QueueBrowserSubscription;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.TopicSubscription;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import org.apache.activemq.broker.region.BaseDestination;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.network.NetworkBridgeFilterFactory;
import org.apache.activemq.broker.region.group.MessageGroupMapFactory;
import org.slf4j.Logger;
import org.apache.activemq.filter.DestinationMapEntry;

public class PolicyEntry extends DestinationMapEntry
{
    private static final Logger LOG;
    private DispatchPolicy dispatchPolicy;
    private SubscriptionRecoveryPolicy subscriptionRecoveryPolicy;
    private boolean sendAdvisoryIfNoConsumers;
    private DeadLetterStrategy deadLetterStrategy;
    private PendingMessageLimitStrategy pendingMessageLimitStrategy;
    private MessageEvictionStrategy messageEvictionStrategy;
    private long memoryLimit;
    private String messageGroupMapFactoryType;
    private MessageGroupMapFactory messageGroupMapFactory;
    private PendingQueueMessageStoragePolicy pendingQueuePolicy;
    private PendingDurableSubscriberMessageStoragePolicy pendingDurableSubscriberPolicy;
    private PendingSubscriberMessageStoragePolicy pendingSubscriberPolicy;
    private int maxProducersToAudit;
    private int maxAuditDepth;
    private int maxQueueAuditDepth;
    private boolean enableAudit;
    private boolean producerFlowControl;
    private boolean alwaysRetroactive;
    private long blockedProducerWarningInterval;
    private boolean optimizedDispatch;
    private int maxPageSize;
    private int maxBrowsePageSize;
    private boolean useCache;
    private long minimumMessageSize;
    private boolean useConsumerPriority;
    private boolean strictOrderDispatch;
    private boolean lazyDispatch;
    private int timeBeforeDispatchStarts;
    private int consumersBeforeDispatchStarts;
    private boolean advisoryForSlowConsumers;
    private boolean advisoryForFastProducers;
    private boolean advisoryForDiscardingMessages;
    private boolean advisoryWhenFull;
    private boolean advisoryForDelivery;
    private boolean advisoryForConsumed;
    private long expireMessagesPeriod;
    private int maxExpirePageSize;
    private int queuePrefetch;
    private int queueBrowserPrefetch;
    private int topicPrefetch;
    private int durableTopicPrefetch;
    private boolean usePrefetchExtension;
    private int cursorMemoryHighWaterMark;
    private int storeUsageHighWaterMark;
    private SlowConsumerStrategy slowConsumerStrategy;
    private boolean prioritizedMessages;
    private boolean allConsumersExclusiveByDefault;
    private boolean gcInactiveDestinations;
    private boolean gcWithNetworkConsumers;
    private long inactiveTimoutBeforeGC;
    private boolean reduceMemoryFootprint;
    private NetworkBridgeFilterFactory networkBridgeFilterFactory;
    private boolean doOptimzeMessageStorage;
    private int optimizeMessageStoreInFlightLimit;
    private boolean persistJMSRedelivered;
    
    public PolicyEntry() {
        this.deadLetterStrategy = Destination.DEFAULT_DEAD_LETTER_STRATEGY;
        this.messageGroupMapFactoryType = "cached";
        this.maxProducersToAudit = 64;
        this.maxAuditDepth = 2048;
        this.maxQueueAuditDepth = 2048;
        this.enableAudit = true;
        this.producerFlowControl = true;
        this.alwaysRetroactive = false;
        this.blockedProducerWarningInterval = 30000L;
        this.optimizedDispatch = false;
        this.maxPageSize = 200;
        this.maxBrowsePageSize = 400;
        this.useCache = true;
        this.minimumMessageSize = 1024L;
        this.useConsumerPriority = true;
        this.strictOrderDispatch = false;
        this.lazyDispatch = false;
        this.timeBeforeDispatchStarts = 0;
        this.consumersBeforeDispatchStarts = 0;
        this.expireMessagesPeriod = 30000L;
        this.maxExpirePageSize = 400;
        this.queuePrefetch = 1000;
        this.queueBrowserPrefetch = 500;
        this.topicPrefetch = 32767;
        this.durableTopicPrefetch = 100;
        this.usePrefetchExtension = true;
        this.cursorMemoryHighWaterMark = 70;
        this.storeUsageHighWaterMark = 100;
        this.inactiveTimoutBeforeGC = 60000L;
        this.doOptimzeMessageStorage = true;
        this.optimizeMessageStoreInFlightLimit = 10;
        this.persistJMSRedelivered = false;
    }
    
    public void configure(final Broker broker, final Queue queue) {
        this.baseConfiguration(broker, queue);
        if (this.dispatchPolicy != null) {
            queue.setDispatchPolicy(this.dispatchPolicy);
        }
        queue.setDeadLetterStrategy(this.getDeadLetterStrategy());
        queue.setMessageGroupMapFactory(this.getMessageGroupMapFactory());
        if (this.memoryLimit > 0L) {
            queue.getMemoryUsage().setLimit(this.memoryLimit);
        }
        if (this.pendingQueuePolicy != null) {
            final PendingMessageCursor messages = this.pendingQueuePolicy.getQueuePendingMessageCursor(broker, queue);
            queue.setMessages(messages);
        }
        queue.setUseConsumerPriority(this.isUseConsumerPriority());
        queue.setStrictOrderDispatch(this.isStrictOrderDispatch());
        queue.setOptimizedDispatch(this.isOptimizedDispatch());
        queue.setLazyDispatch(this.isLazyDispatch());
        queue.setTimeBeforeDispatchStarts(this.getTimeBeforeDispatchStarts());
        queue.setConsumersBeforeDispatchStarts(this.getConsumersBeforeDispatchStarts());
        queue.setAllConsumersExclusiveByDefault(this.isAllConsumersExclusiveByDefault());
        queue.setPersistJMSRedelivered(this.isPersistJMSRedelivered());
    }
    
    public void update(final Queue queue) {
        this.baseUpdate(queue);
        if (this.memoryLimit > 0L) {
            queue.getMemoryUsage().setLimit(this.memoryLimit);
        }
        queue.setUseConsumerPriority(this.isUseConsumerPriority());
        queue.setStrictOrderDispatch(this.isStrictOrderDispatch());
        queue.setOptimizedDispatch(this.isOptimizedDispatch());
        queue.setLazyDispatch(this.isLazyDispatch());
        queue.setTimeBeforeDispatchStarts(this.getTimeBeforeDispatchStarts());
        queue.setConsumersBeforeDispatchStarts(this.getConsumersBeforeDispatchStarts());
        queue.setAllConsumersExclusiveByDefault(this.isAllConsumersExclusiveByDefault());
        queue.setPersistJMSRedelivered(this.isPersistJMSRedelivered());
    }
    
    public void configure(final Broker broker, final Topic topic) {
        this.baseConfiguration(broker, topic);
        if (this.dispatchPolicy != null) {
            topic.setDispatchPolicy(this.dispatchPolicy);
        }
        topic.setDeadLetterStrategy(this.getDeadLetterStrategy());
        if (this.subscriptionRecoveryPolicy != null) {
            final SubscriptionRecoveryPolicy srp = this.subscriptionRecoveryPolicy.copy();
            srp.setBroker(broker);
            topic.setSubscriptionRecoveryPolicy(srp);
        }
        if (this.memoryLimit > 0L) {
            topic.getMemoryUsage().setLimit(this.memoryLimit);
        }
        topic.setLazyDispatch(this.isLazyDispatch());
    }
    
    public void update(final Topic topic) {
        this.baseUpdate(topic);
        if (this.memoryLimit > 0L) {
            topic.getMemoryUsage().setLimit(this.memoryLimit);
        }
        topic.setLazyDispatch(this.isLazyDispatch());
    }
    
    public void baseUpdate(final BaseDestination destination) {
        destination.setProducerFlowControl(this.isProducerFlowControl());
        destination.setAlwaysRetroactive(this.isAlwaysRetroactive());
        destination.setBlockedProducerWarningInterval(this.getBlockedProducerWarningInterval());
        destination.setMaxPageSize(this.getMaxPageSize());
        destination.setMaxBrowsePageSize(this.getMaxBrowsePageSize());
        destination.setMinimumMessageSize((int)this.getMinimumMessageSize());
        destination.setMaxExpirePageSize(this.getMaxExpirePageSize());
        destination.setCursorMemoryHighWaterMark(this.getCursorMemoryHighWaterMark());
        destination.setStoreUsageHighWaterMark(this.getStoreUsageHighWaterMark());
        destination.setGcIfInactive(this.isGcInactiveDestinations());
        destination.setGcWithNetworkConsumers(this.isGcWithNetworkConsumers());
        destination.setInactiveTimoutBeforeGC(this.getInactiveTimoutBeforeGC());
        destination.setReduceMemoryFootprint(this.isReduceMemoryFootprint());
        destination.setDoOptimzeMessageStorage(this.isDoOptimzeMessageStorage());
        destination.setOptimizeMessageStoreInFlightLimit(this.getOptimizeMessageStoreInFlightLimit());
        destination.setAdvisoryForConsumed(this.isAdvisoryForConsumed());
        destination.setAdvisoryForDelivery(this.isAdvisoryForDelivery());
        destination.setAdvisoryForDiscardingMessages(this.isAdvisoryForDiscardingMessages());
        destination.setAdvisoryForSlowConsumers(this.isAdvisoryForSlowConsumers());
        destination.setAdvisoryForFastProducers(this.isAdvisoryForFastProducers());
        destination.setAdvisoryWhenFull(this.isAdvisoryWhenFull());
        destination.setSendAdvisoryIfNoConsumers(this.isSendAdvisoryIfNoConsumers());
    }
    
    public void baseConfiguration(final Broker broker, final BaseDestination destination) {
        this.baseUpdate(destination);
        destination.setEnableAudit(this.isEnableAudit());
        destination.setMaxAuditDepth(this.getMaxQueueAuditDepth());
        destination.setMaxProducersToAudit(this.getMaxProducersToAudit());
        destination.setUseCache(this.isUseCache());
        destination.setExpireMessagesPeriod(this.getExpireMessagesPeriod());
        final SlowConsumerStrategy scs = this.getSlowConsumerStrategy();
        if (scs != null) {
            scs.setBrokerService(broker);
            scs.addDestination(destination);
        }
        destination.setSlowConsumerStrategy(scs);
        destination.setPrioritizedMessages(this.isPrioritizedMessages());
    }
    
    public void configure(final Broker broker, final SystemUsage memoryManager, final TopicSubscription subscription) {
        this.configurePrefetch(subscription);
        if (this.pendingMessageLimitStrategy != null) {
            int value = this.pendingMessageLimitStrategy.getMaximumPendingMessageLimit(subscription);
            final int consumerLimit = subscription.getInfo().getMaximumPendingMessageLimit();
            if (consumerLimit > 0 && (value < 0 || consumerLimit < value)) {
                value = consumerLimit;
            }
            if (value >= 0) {
                PolicyEntry.LOG.debug("Setting the maximumPendingMessages size to: {} for consumer: {}", (Object)value, subscription.getInfo().getConsumerId());
                subscription.setMaximumPendingMessages(value);
            }
        }
        if (this.messageEvictionStrategy != null) {
            subscription.setMessageEvictionStrategy(this.messageEvictionStrategy);
        }
        if (this.pendingSubscriberPolicy != null) {
            final String name = subscription.getContext().getClientId() + "_" + subscription.getConsumerInfo().getConsumerId();
            final int maxBatchSize = subscription.getConsumerInfo().getPrefetchSize();
            subscription.setMatched(this.pendingSubscriberPolicy.getSubscriberPendingMessageCursor(broker, name, maxBatchSize, subscription));
        }
        if (this.enableAudit) {
            subscription.setEnableAudit(this.enableAudit);
            subscription.setMaxProducersToAudit(this.maxProducersToAudit);
            subscription.setMaxAuditDepth(this.maxAuditDepth);
        }
    }
    
    public void configure(final Broker broker, final SystemUsage memoryManager, final DurableTopicSubscription sub) {
        final String clientId = sub.getSubscriptionKey().getClientId();
        final String subName = sub.getSubscriptionKey().getSubscriptionName();
        sub.setCursorMemoryHighWaterMark(this.getCursorMemoryHighWaterMark());
        this.configurePrefetch(sub);
        if (this.pendingDurableSubscriberPolicy != null) {
            final PendingMessageCursor cursor = this.pendingDurableSubscriberPolicy.getSubscriberPendingMessageCursor(broker, clientId, subName, sub.getPrefetchSize(), sub);
            cursor.setSystemUsage(memoryManager);
            sub.setPending(cursor);
        }
        final int auditDepth = this.getMaxAuditDepth();
        if (auditDepth == 2048 && this.isPrioritizedMessages()) {
            sub.setMaxAuditDepth(auditDepth * 10);
        }
        else {
            sub.setMaxAuditDepth(auditDepth);
        }
        sub.setMaxProducersToAudit(this.getMaxProducersToAudit());
        sub.setUsePrefetchExtension(this.isUsePrefetchExtension());
    }
    
    public void configure(final Broker broker, final SystemUsage memoryManager, final QueueBrowserSubscription sub) {
        this.configurePrefetch(sub);
        sub.setCursorMemoryHighWaterMark(this.getCursorMemoryHighWaterMark());
        sub.setUsePrefetchExtension(this.isUsePrefetchExtension());
        sub.setMaxProducersToAudit(Integer.MAX_VALUE);
        sub.setMaxAuditDepth(32767);
        sub.setMaxMessages(this.getMaxBrowsePageSize());
    }
    
    public void configure(final Broker broker, final SystemUsage memoryManager, final QueueSubscription sub) {
        this.configurePrefetch(sub);
        sub.setCursorMemoryHighWaterMark(this.getCursorMemoryHighWaterMark());
        sub.setUsePrefetchExtension(this.isUsePrefetchExtension());
        sub.setMaxProducersToAudit(this.getMaxProducersToAudit());
    }
    
    public void configurePrefetch(final Subscription subscription) {
        final int currentPrefetch = subscription.getConsumerInfo().getPrefetchSize();
        if (subscription instanceof QueueBrowserSubscription) {
            if (currentPrefetch == 500) {
                ((QueueBrowserSubscription)subscription).setPrefetchSize(this.getQueueBrowserPrefetch());
            }
        }
        else if (subscription instanceof QueueSubscription) {
            if (currentPrefetch == 1000) {
                ((QueueSubscription)subscription).setPrefetchSize(this.getQueuePrefetch());
            }
        }
        else if (subscription instanceof DurableTopicSubscription) {
            if (currentPrefetch == 100 || subscription.getConsumerInfo().getPrefetchSize() == 1000) {
                ((DurableTopicSubscription)subscription).setPrefetchSize(this.getDurableTopicPrefetch());
            }
        }
        else if (subscription instanceof TopicSubscription && currentPrefetch == 32767) {
            ((TopicSubscription)subscription).setPrefetchSize(this.getTopicPrefetch());
        }
        if (currentPrefetch != 0 && subscription.getPrefetchSize() == 0) {
            subscription.updateConsumerPrefetch(0);
        }
    }
    
    public DispatchPolicy getDispatchPolicy() {
        return this.dispatchPolicy;
    }
    
    public void setDispatchPolicy(final DispatchPolicy policy) {
        this.dispatchPolicy = policy;
    }
    
    public SubscriptionRecoveryPolicy getSubscriptionRecoveryPolicy() {
        return this.subscriptionRecoveryPolicy;
    }
    
    public void setSubscriptionRecoveryPolicy(final SubscriptionRecoveryPolicy subscriptionRecoveryPolicy) {
        this.subscriptionRecoveryPolicy = subscriptionRecoveryPolicy;
    }
    
    public boolean isSendAdvisoryIfNoConsumers() {
        return this.sendAdvisoryIfNoConsumers;
    }
    
    public void setSendAdvisoryIfNoConsumers(final boolean sendAdvisoryIfNoConsumers) {
        this.sendAdvisoryIfNoConsumers = sendAdvisoryIfNoConsumers;
    }
    
    public DeadLetterStrategy getDeadLetterStrategy() {
        return this.deadLetterStrategy;
    }
    
    public void setDeadLetterStrategy(final DeadLetterStrategy deadLetterStrategy) {
        this.deadLetterStrategy = deadLetterStrategy;
    }
    
    public PendingMessageLimitStrategy getPendingMessageLimitStrategy() {
        return this.pendingMessageLimitStrategy;
    }
    
    public void setPendingMessageLimitStrategy(final PendingMessageLimitStrategy pendingMessageLimitStrategy) {
        this.pendingMessageLimitStrategy = pendingMessageLimitStrategy;
    }
    
    public MessageEvictionStrategy getMessageEvictionStrategy() {
        return this.messageEvictionStrategy;
    }
    
    public void setMessageEvictionStrategy(final MessageEvictionStrategy messageEvictionStrategy) {
        this.messageEvictionStrategy = messageEvictionStrategy;
    }
    
    public long getMemoryLimit() {
        return this.memoryLimit;
    }
    
    public void setMemoryLimit(final long memoryLimit) {
        this.memoryLimit = memoryLimit;
    }
    
    public MessageGroupMapFactory getMessageGroupMapFactory() {
        if (this.messageGroupMapFactory == null) {
            try {
                this.messageGroupMapFactory = GroupFactoryFinder.createMessageGroupMapFactory(this.getMessageGroupMapFactoryType());
            }
            catch (Exception e) {
                PolicyEntry.LOG.error("Failed to create message group Factory ", e);
            }
        }
        return this.messageGroupMapFactory;
    }
    
    public void setMessageGroupMapFactory(final MessageGroupMapFactory messageGroupMapFactory) {
        this.messageGroupMapFactory = messageGroupMapFactory;
    }
    
    public String getMessageGroupMapFactoryType() {
        return this.messageGroupMapFactoryType;
    }
    
    public void setMessageGroupMapFactoryType(final String messageGroupMapFactoryType) {
        this.messageGroupMapFactoryType = messageGroupMapFactoryType;
    }
    
    public PendingDurableSubscriberMessageStoragePolicy getPendingDurableSubscriberPolicy() {
        return this.pendingDurableSubscriberPolicy;
    }
    
    public void setPendingDurableSubscriberPolicy(final PendingDurableSubscriberMessageStoragePolicy pendingDurableSubscriberPolicy) {
        this.pendingDurableSubscriberPolicy = pendingDurableSubscriberPolicy;
    }
    
    public PendingQueueMessageStoragePolicy getPendingQueuePolicy() {
        return this.pendingQueuePolicy;
    }
    
    public void setPendingQueuePolicy(final PendingQueueMessageStoragePolicy pendingQueuePolicy) {
        this.pendingQueuePolicy = pendingQueuePolicy;
    }
    
    public PendingSubscriberMessageStoragePolicy getPendingSubscriberPolicy() {
        return this.pendingSubscriberPolicy;
    }
    
    public void setPendingSubscriberPolicy(final PendingSubscriberMessageStoragePolicy pendingSubscriberPolicy) {
        this.pendingSubscriberPolicy = pendingSubscriberPolicy;
    }
    
    public boolean isProducerFlowControl() {
        return this.producerFlowControl;
    }
    
    public void setProducerFlowControl(final boolean producerFlowControl) {
        this.producerFlowControl = producerFlowControl;
    }
    
    public boolean isAlwaysRetroactive() {
        return this.alwaysRetroactive;
    }
    
    public void setAlwaysRetroactive(final boolean alwaysRetroactive) {
        this.alwaysRetroactive = alwaysRetroactive;
    }
    
    public void setBlockedProducerWarningInterval(final long blockedProducerWarningInterval) {
        this.blockedProducerWarningInterval = blockedProducerWarningInterval;
    }
    
    public long getBlockedProducerWarningInterval() {
        return this.blockedProducerWarningInterval;
    }
    
    public int getMaxProducersToAudit() {
        return this.maxProducersToAudit;
    }
    
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.maxProducersToAudit = maxProducersToAudit;
    }
    
    public int getMaxAuditDepth() {
        return this.maxAuditDepth;
    }
    
    public void setMaxAuditDepth(final int maxAuditDepth) {
        this.maxAuditDepth = maxAuditDepth;
    }
    
    public boolean isEnableAudit() {
        return this.enableAudit;
    }
    
    public void setEnableAudit(final boolean enableAudit) {
        this.enableAudit = enableAudit;
    }
    
    public int getMaxQueueAuditDepth() {
        return this.maxQueueAuditDepth;
    }
    
    public void setMaxQueueAuditDepth(final int maxQueueAuditDepth) {
        this.maxQueueAuditDepth = maxQueueAuditDepth;
    }
    
    public boolean isOptimizedDispatch() {
        return this.optimizedDispatch;
    }
    
    public void setOptimizedDispatch(final boolean optimizedDispatch) {
        this.optimizedDispatch = optimizedDispatch;
    }
    
    public int getMaxPageSize() {
        return this.maxPageSize;
    }
    
    public void setMaxPageSize(final int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }
    
    public int getMaxBrowsePageSize() {
        return this.maxBrowsePageSize;
    }
    
    public void setMaxBrowsePageSize(final int maxPageSize) {
        this.maxBrowsePageSize = maxPageSize;
    }
    
    public boolean isUseCache() {
        return this.useCache;
    }
    
    public void setUseCache(final boolean useCache) {
        this.useCache = useCache;
    }
    
    public long getMinimumMessageSize() {
        return this.minimumMessageSize;
    }
    
    public void setMinimumMessageSize(final long minimumMessageSize) {
        this.minimumMessageSize = minimumMessageSize;
    }
    
    public boolean isUseConsumerPriority() {
        return this.useConsumerPriority;
    }
    
    public void setUseConsumerPriority(final boolean useConsumerPriority) {
        this.useConsumerPriority = useConsumerPriority;
    }
    
    public boolean isStrictOrderDispatch() {
        return this.strictOrderDispatch;
    }
    
    public void setStrictOrderDispatch(final boolean strictOrderDispatch) {
        this.strictOrderDispatch = strictOrderDispatch;
    }
    
    public boolean isLazyDispatch() {
        return this.lazyDispatch;
    }
    
    public void setLazyDispatch(final boolean lazyDispatch) {
        this.lazyDispatch = lazyDispatch;
    }
    
    public int getTimeBeforeDispatchStarts() {
        return this.timeBeforeDispatchStarts;
    }
    
    public void setTimeBeforeDispatchStarts(final int timeBeforeDispatchStarts) {
        this.timeBeforeDispatchStarts = timeBeforeDispatchStarts;
    }
    
    public int getConsumersBeforeDispatchStarts() {
        return this.consumersBeforeDispatchStarts;
    }
    
    public void setConsumersBeforeDispatchStarts(final int consumersBeforeDispatchStarts) {
        this.consumersBeforeDispatchStarts = consumersBeforeDispatchStarts;
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
    
    public void setMaxExpirePageSize(final int maxExpirePageSize) {
        this.maxExpirePageSize = maxExpirePageSize;
    }
    
    public int getMaxExpirePageSize() {
        return this.maxExpirePageSize;
    }
    
    public void setExpireMessagesPeriod(final long expireMessagesPeriod) {
        this.expireMessagesPeriod = expireMessagesPeriod;
    }
    
    public long getExpireMessagesPeriod() {
        return this.expireMessagesPeriod;
    }
    
    public int getQueuePrefetch() {
        return this.queuePrefetch;
    }
    
    public void setQueuePrefetch(final int queuePrefetch) {
        this.queuePrefetch = queuePrefetch;
    }
    
    public int getQueueBrowserPrefetch() {
        return this.queueBrowserPrefetch;
    }
    
    public void setQueueBrowserPrefetch(final int queueBrowserPrefetch) {
        this.queueBrowserPrefetch = queueBrowserPrefetch;
    }
    
    public int getTopicPrefetch() {
        return this.topicPrefetch;
    }
    
    public void setTopicPrefetch(final int topicPrefetch) {
        this.topicPrefetch = topicPrefetch;
    }
    
    public int getDurableTopicPrefetch() {
        return this.durableTopicPrefetch;
    }
    
    public void setDurableTopicPrefetch(final int durableTopicPrefetch) {
        this.durableTopicPrefetch = durableTopicPrefetch;
    }
    
    public boolean isUsePrefetchExtension() {
        return this.usePrefetchExtension;
    }
    
    public void setUsePrefetchExtension(final boolean usePrefetchExtension) {
        this.usePrefetchExtension = usePrefetchExtension;
    }
    
    public int getCursorMemoryHighWaterMark() {
        return this.cursorMemoryHighWaterMark;
    }
    
    public void setCursorMemoryHighWaterMark(final int cursorMemoryHighWaterMark) {
        this.cursorMemoryHighWaterMark = cursorMemoryHighWaterMark;
    }
    
    public void setStoreUsageHighWaterMark(final int storeUsageHighWaterMark) {
        this.storeUsageHighWaterMark = storeUsageHighWaterMark;
    }
    
    public int getStoreUsageHighWaterMark() {
        return this.storeUsageHighWaterMark;
    }
    
    public void setSlowConsumerStrategy(final SlowConsumerStrategy slowConsumerStrategy) {
        this.slowConsumerStrategy = slowConsumerStrategy;
    }
    
    public SlowConsumerStrategy getSlowConsumerStrategy() {
        return this.slowConsumerStrategy;
    }
    
    public boolean isPrioritizedMessages() {
        return this.prioritizedMessages;
    }
    
    public void setPrioritizedMessages(final boolean prioritizedMessages) {
        this.prioritizedMessages = prioritizedMessages;
    }
    
    public void setAllConsumersExclusiveByDefault(final boolean allConsumersExclusiveByDefault) {
        this.allConsumersExclusiveByDefault = allConsumersExclusiveByDefault;
    }
    
    public boolean isAllConsumersExclusiveByDefault() {
        return this.allConsumersExclusiveByDefault;
    }
    
    public boolean isGcInactiveDestinations() {
        return this.gcInactiveDestinations;
    }
    
    public void setGcInactiveDestinations(final boolean gcInactiveDestinations) {
        this.gcInactiveDestinations = gcInactiveDestinations;
    }
    
    public long getInactiveTimoutBeforeGC() {
        return this.inactiveTimoutBeforeGC;
    }
    
    public void setInactiveTimoutBeforeGC(final long inactiveTimoutBeforeGC) {
        this.inactiveTimoutBeforeGC = inactiveTimoutBeforeGC;
    }
    
    public void setGcWithNetworkConsumers(final boolean gcWithNetworkConsumers) {
        this.gcWithNetworkConsumers = gcWithNetworkConsumers;
    }
    
    public boolean isGcWithNetworkConsumers() {
        return this.gcWithNetworkConsumers;
    }
    
    public boolean isReduceMemoryFootprint() {
        return this.reduceMemoryFootprint;
    }
    
    public void setReduceMemoryFootprint(final boolean reduceMemoryFootprint) {
        this.reduceMemoryFootprint = reduceMemoryFootprint;
    }
    
    public void setNetworkBridgeFilterFactory(final NetworkBridgeFilterFactory networkBridgeFilterFactory) {
        this.networkBridgeFilterFactory = networkBridgeFilterFactory;
    }
    
    public NetworkBridgeFilterFactory getNetworkBridgeFilterFactory() {
        return this.networkBridgeFilterFactory;
    }
    
    public boolean isDoOptimzeMessageStorage() {
        return this.doOptimzeMessageStorage;
    }
    
    public void setDoOptimzeMessageStorage(final boolean doOptimzeMessageStorage) {
        this.doOptimzeMessageStorage = doOptimzeMessageStorage;
    }
    
    public int getOptimizeMessageStoreInFlightLimit() {
        return this.optimizeMessageStoreInFlightLimit;
    }
    
    public void setOptimizeMessageStoreInFlightLimit(final int optimizeMessageStoreInFlightLimit) {
        this.optimizeMessageStoreInFlightLimit = optimizeMessageStoreInFlightLimit;
    }
    
    public void setPersistJMSRedelivered(final boolean val) {
        this.persistJMSRedelivered = val;
    }
    
    public boolean isPersistJMSRedelivered() {
        return this.persistJMSRedelivered;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PolicyEntry.class);
    }
}
