// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import org.apache.activemq.usage.Usage;
import javax.jms.InvalidSelectorException;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.Message;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import java.util.Iterator;
import org.apache.activemq.broker.region.cursors.AbstractPendingMessageCursor;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.broker.region.cursors.PendingMessageCursor;
import java.io.IOException;
import org.apache.activemq.command.MessageAck;
import javax.jms.JMSException;
import org.apache.activemq.broker.region.cursors.StoreDurableSubscriberCursor;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.Broker;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.SubscriptionKey;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageId;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.usage.UsageListener;

public class DurableTopicSubscription extends PrefetchSubscription implements UsageListener
{
    private static final Logger LOG;
    private final ConcurrentHashMap<MessageId, Integer> redeliveredMessages;
    private final ConcurrentHashMap<ActiveMQDestination, Destination> durableDestinations;
    private final SubscriptionKey subscriptionKey;
    private final boolean keepDurableSubsActive;
    private final AtomicBoolean active;
    private final AtomicLong offlineTimestamp;
    
    public DurableTopicSubscription(final Broker broker, final SystemUsage usageManager, final ConnectionContext context, final ConsumerInfo info, final boolean keepDurableSubsActive) throws JMSException {
        super(broker, usageManager, context, info);
        this.redeliveredMessages = new ConcurrentHashMap<MessageId, Integer>();
        this.durableDestinations = new ConcurrentHashMap<ActiveMQDestination, Destination>();
        this.active = new AtomicBoolean();
        this.offlineTimestamp = new AtomicLong(-1L);
        (this.pending = new StoreDurableSubscriberCursor(broker, context.getClientId(), info.getSubscriptionName(), info.getPrefetchSize(), this)).setSystemUsage(usageManager);
        this.pending.setMemoryUsageHighWaterMark(this.getCursorMemoryHighWaterMark());
        this.keepDurableSubsActive = keepDurableSubsActive;
        this.subscriptionKey = new SubscriptionKey(context.getClientId(), info.getSubscriptionName());
    }
    
    public final boolean isActive() {
        return this.active.get();
    }
    
    public final long getOfflineTimestamp() {
        return this.offlineTimestamp.get();
    }
    
    public void setOfflineTimestamp(final long timestamp) {
        this.offlineTimestamp.set(timestamp);
    }
    
    @Override
    public boolean isFull() {
        return !this.active.get() || super.isFull();
    }
    
    @Override
    public void gc() {
    }
    
    @Override
    public void unmatched(final MessageReference node) throws IOException {
        final MessageAck ack = new MessageAck();
        ack.setAckType((byte)5);
        ack.setMessageID(node.getMessageId());
        final Destination regionDestination = (Destination)node.getRegionDestination();
        regionDestination.acknowledge(this.getContext(), this, ack, node);
    }
    
    @Override
    protected void setPendingBatchSize(final PendingMessageCursor pending, final int numberToDispatch) {
    }
    
    @Override
    public void add(final ConnectionContext context, final Destination destination) throws Exception {
        if (!this.destinations.contains(destination)) {
            super.add(context, destination);
        }
        if (this.durableDestinations.containsKey(destination.getActiveMQDestination())) {
            return;
        }
        this.durableDestinations.put(destination.getActiveMQDestination(), destination);
        if (this.active.get() || this.keepDurableSubsActive) {
            final Topic topic = (Topic)destination;
            topic.activate(context, this);
            this.enqueueCounter += this.pending.size();
        }
        else if (destination.getMessageStore() != null) {
            final TopicMessageStore store = (TopicMessageStore)destination.getMessageStore();
            try {
                this.enqueueCounter += store.getMessageCount(this.subscriptionKey.getClientId(), this.subscriptionKey.getSubscriptionName());
            }
            catch (IOException e) {
                final JMSException jmsEx = new JMSException("Failed to retrieve enqueueCount from store " + e);
                jmsEx.setLinkedException(e);
                throw jmsEx;
            }
        }
        this.dispatchPending();
    }
    
    public boolean isEmpty(final Topic topic) {
        return this.pending.isEmpty(topic);
    }
    
    public void activate(final SystemUsage memoryManager, final ConnectionContext context, final ConsumerInfo info, final RegionBroker regionBroker) throws Exception {
        if (!this.active.get()) {
            this.context = context;
            this.info = info;
            DurableTopicSubscription.LOG.debug("Activating {}", this);
            if (!this.keepDurableSubsActive) {
                for (final Destination destination : this.durableDestinations.values()) {
                    final Topic topic = (Topic)destination;
                    this.add(context, topic);
                    topic.activate(context, this);
                }
                final ActiveMQDestination dest = this.info.getDestination();
                if (dest != null && regionBroker.getDestinationPolicy() != null) {
                    final PolicyEntry entry = regionBroker.getDestinationPolicy().getEntryFor(dest);
                    if (entry != null) {
                        entry.configure(this.broker, this.usageManager, this);
                    }
                }
            }
            synchronized (this.pendingLock) {
                if (!((AbstractPendingMessageCursor)this.pending).isStarted() || !this.keepDurableSubsActive) {
                    this.pending.setSystemUsage(memoryManager);
                    this.pending.setMemoryUsageHighWaterMark(this.getCursorMemoryHighWaterMark());
                    this.pending.setMaxAuditDepth(this.getMaxAuditDepth());
                    this.pending.setMaxProducersToAudit(this.getMaxProducersToAudit());
                    this.pending.start();
                }
                for (final Destination destination2 : this.durableDestinations.values()) {
                    final Topic topic2 = (Topic)destination2;
                    if (topic2.isAlwaysRetroactive() || info.isRetroactive()) {
                        topic2.recoverRetroactiveMessages(context, this);
                    }
                }
            }
            this.active.set(true);
            this.offlineTimestamp.set(-1L);
            this.dispatchPending();
            this.usageManager.getMemoryUsage().addUsageListener(this);
        }
    }
    
    public void deactivate(final boolean keepDurableSubsActive) throws Exception {
        DurableTopicSubscription.LOG.debug("Deactivating keepActive={}, {}", (Object)keepDurableSubsActive, this);
        this.active.set(false);
        this.offlineTimestamp.set(System.currentTimeMillis());
        this.usageManager.getMemoryUsage().removeUsageListener(this);
        final ArrayList<Topic> topicsToDeactivate = new ArrayList<Topic>();
        List<MessageReference> savedDispateched = null;
        synchronized (this.pendingLock) {
            if (!keepDurableSubsActive) {
                this.pending.stop();
            }
            synchronized (this.dispatchLock) {
                for (final Destination destination : this.durableDestinations.values()) {
                    final Topic topic = (Topic)destination;
                    if (!keepDurableSubsActive) {
                        topicsToDeactivate.add(topic);
                    }
                    else {
                        topic.getDestinationStatistics().getInflight().subtract(this.dispatched.size());
                    }
                }
                Collections.reverse(this.dispatched);
                for (final MessageReference node : this.dispatched) {
                    final Integer count = this.redeliveredMessages.get(node.getMessageId());
                    if (count != null) {
                        this.redeliveredMessages.put(node.getMessageId(), count + 1);
                    }
                    else {
                        this.redeliveredMessages.put(node.getMessageId(), 1);
                    }
                    if (keepDurableSubsActive && this.pending.isTransient()) {
                        this.pending.addMessageFirst(node);
                        this.pending.rollback(node.getMessageId());
                    }
                    else {
                        node.decrementReferenceCount();
                    }
                }
                if (!topicsToDeactivate.isEmpty()) {
                    savedDispateched = new ArrayList<MessageReference>(this.dispatched);
                }
                this.dispatched.clear();
            }
            if (!keepDurableSubsActive && this.pending.isTransient()) {
                try {
                    this.pending.reset();
                    while (this.pending.hasNext()) {
                        final MessageReference node2 = this.pending.next();
                        node2.decrementReferenceCount();
                        this.pending.remove();
                    }
                }
                finally {
                    this.pending.release();
                }
            }
        }
        for (final Topic topic2 : topicsToDeactivate) {
            topic2.deactivate(this.context, this, savedDispateched);
        }
        this.prefetchExtension.set(0);
    }
    
    @Override
    protected MessageDispatch createMessageDispatch(final MessageReference node, final Message message) {
        final MessageDispatch md = super.createMessageDispatch(node, message);
        if (node != QueueMessageReference.NULL_MESSAGE) {
            final Integer count = this.redeliveredMessages.get(node.getMessageId());
            if (count != null) {
                md.setRedeliveryCounter(count);
            }
        }
        return md;
    }
    
    @Override
    public void add(final MessageReference node) throws Exception {
        if (!this.active.get() && !this.keepDurableSubsActive) {
            return;
        }
        super.add(node);
    }
    
    @Override
    public void dispatchPending() throws IOException {
        if (this.isActive()) {
            super.dispatchPending();
        }
    }
    
    public void removePending(final MessageReference node) throws IOException {
        this.pending.remove(node);
    }
    
    @Override
    protected void doAddRecoveredMessage(final MessageReference message) throws Exception {
        synchronized (this.pending) {
            this.pending.addRecoveredMessage(message);
        }
    }
    
    @Override
    public int getPendingQueueSize() {
        if (this.active.get() || this.keepDurableSubsActive) {
            return super.getPendingQueueSize();
        }
        return 0;
    }
    
    @Override
    public void setSelector(final String selector) throws InvalidSelectorException {
        throw new UnsupportedOperationException("You cannot dynamically change the selector for durable topic subscriptions");
    }
    
    @Override
    protected boolean canDispatch(final MessageReference node) {
        return true;
    }
    
    @Override
    protected void acknowledge(final ConnectionContext context, final MessageAck ack, final MessageReference node) throws IOException {
        this.setTimeOfLastMessageAck(System.currentTimeMillis());
        final Destination regionDestination = (Destination)node.getRegionDestination();
        regionDestination.acknowledge(context, this, ack, node);
        this.redeliveredMessages.remove(node.getMessageId());
        node.decrementReferenceCount();
    }
    
    @Override
    public synchronized String toString() {
        return "DurableTopicSubscription-" + this.getSubscriptionKey() + ", id=" + this.info.getConsumerId() + ", active=" + this.isActive() + ", destinations=" + this.durableDestinations.size() + ", total=" + this.enqueueCounter + ", pending=" + this.getPendingQueueSize() + ", dispatched=" + this.dispatchCounter + ", inflight=" + this.dispatched.size() + ", prefetchExtension=" + this.getPrefetchExtension();
    }
    
    public SubscriptionKey getSubscriptionKey() {
        return this.subscriptionKey;
    }
    
    @Override
    public void destroy() {
        synchronized (this.pendingLock) {
            try {
                this.pending.reset();
                while (this.pending.hasNext()) {
                    final MessageReference node = this.pending.next();
                    node.decrementReferenceCount();
                }
            }
            finally {
                this.pending.release();
                this.pending.clear();
            }
        }
        synchronized (this.dispatchLock) {
            for (final MessageReference node2 : this.dispatched) {
                node2.decrementReferenceCount();
            }
            this.dispatched.clear();
        }
        this.setSlowConsumer(false);
    }
    
    @Override
    public void onUsageChanged(final Usage usage, final int oldPercentUsage, final int newPercentUsage) {
        if (oldPercentUsage > newPercentUsage && oldPercentUsage >= 90) {
            try {
                this.dispatchPending();
            }
            catch (IOException e) {
                DurableTopicSubscription.LOG.warn("problem calling dispatchMatched", e);
            }
        }
    }
    
    @Override
    protected boolean isDropped(final MessageReference node) {
        return false;
    }
    
    public boolean isKeepDurableSubsActive() {
        return this.keepDurableSubsActive;
    }
    
    static {
        LOG = LoggerFactory.getLogger(DurableTopicSubscription.class);
    }
}
