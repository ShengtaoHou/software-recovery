// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.slf4j.LoggerFactory;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.Message;
import java.util.Collections;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import java.util.List;
import org.apache.activemq.broker.region.Destination;
import java.util.Map;
import org.slf4j.Logger;

public class StoreDurableSubscriberCursor extends AbstractPendingMessageCursor
{
    private static final Logger LOG;
    private final String clientId;
    private final String subscriberName;
    private final Map<Destination, TopicStorePrefetch> topics;
    private final List<PendingMessageCursor> storePrefetches;
    private final PendingMessageCursor nonPersistent;
    private PendingMessageCursor currentCursor;
    private final DurableTopicSubscription subscription;
    private boolean immediatePriorityDispatch;
    
    public StoreDurableSubscriberCursor(final Broker broker, final String clientId, final String subscriberName, final int maxBatchSize, final DurableTopicSubscription subscription) {
        super(AbstractPendingMessageCursor.isPrioritizedMessageSubscriber(broker, subscription));
        this.topics = new HashMap<Destination, TopicStorePrefetch>();
        this.storePrefetches = new CopyOnWriteArrayList<PendingMessageCursor>();
        this.immediatePriorityDispatch = true;
        this.subscription = subscription;
        this.clientId = clientId;
        this.subscriberName = subscriberName;
        if (broker.getBrokerService().isPersistent()) {
            this.nonPersistent = new FilePendingMessageCursor(broker, clientId + subscriberName, this.prioritizedMessages);
        }
        else {
            this.nonPersistent = new VMPendingMessageCursor(this.prioritizedMessages);
        }
        this.nonPersistent.setMaxBatchSize(maxBatchSize);
        this.nonPersistent.setSystemUsage(this.systemUsage);
        this.storePrefetches.add(this.nonPersistent);
        if (this.prioritizedMessages) {
            this.setMaxAuditDepth(10 * this.getMaxAuditDepth());
        }
    }
    
    @Override
    public synchronized void start() throws Exception {
        if (!this.isStarted()) {
            super.start();
            for (final PendingMessageCursor tsp : this.storePrefetches) {
                tsp.setMessageAudit(this.getMessageAudit());
                tsp.start();
            }
        }
    }
    
    @Override
    public synchronized void stop() throws Exception {
        if (this.isStarted()) {
            if (this.subscription.isKeepDurableSubsActive()) {
                super.gc();
                for (final PendingMessageCursor tsp : this.storePrefetches) {
                    tsp.gc();
                }
            }
            else {
                super.stop();
                for (final PendingMessageCursor tsp : this.storePrefetches) {
                    tsp.stop();
                }
                this.getMessageAudit().clear();
            }
        }
    }
    
    @Override
    public synchronized void add(final ConnectionContext context, final Destination destination) throws Exception {
        if (destination != null && !AdvisorySupport.isAdvisoryTopic(destination.getActiveMQDestination())) {
            final TopicStorePrefetch tsp = new TopicStorePrefetch(this.subscription, (Topic)destination, this.clientId, this.subscriberName);
            tsp.setMaxBatchSize(destination.getMaxPageSize());
            tsp.setSystemUsage(this.systemUsage);
            tsp.setMessageAudit(this.getMessageAudit());
            tsp.setEnableAudit(this.isEnableAudit());
            tsp.setMemoryUsageHighWaterMark(this.getMemoryUsageHighWaterMark());
            tsp.setUseCache(this.isUseCache());
            tsp.setCacheEnabled(this.isUseCache() && tsp.isEmpty());
            this.topics.put(destination, tsp);
            this.storePrefetches.add(tsp);
            if (this.isStarted()) {
                tsp.start();
            }
        }
    }
    
    @Override
    public synchronized List<MessageReference> remove(final ConnectionContext context, final Destination destination) throws Exception {
        final PendingMessageCursor tsp = this.topics.remove(destination);
        if (tsp != null) {
            this.storePrefetches.remove(tsp);
        }
        return (List<MessageReference>)Collections.EMPTY_LIST;
    }
    
    @Override
    public synchronized boolean isEmpty() {
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            if (!tsp.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public synchronized boolean isEmpty(final Destination destination) {
        boolean result = true;
        final TopicStorePrefetch tsp = this.topics.get(destination);
        if (tsp != null) {
            result = tsp.isEmpty();
        }
        return result;
    }
    
    @Override
    public boolean isRecoveryRequired() {
        return false;
    }
    
    @Override
    public synchronized void addMessageLast(final MessageReference node) throws Exception {
        if (node != null) {
            final Message msg = node.getMessage();
            if (this.isStarted() && !msg.isPersistent()) {
                this.nonPersistent.addMessageLast(node);
            }
            if (msg.isPersistent()) {
                final Destination dest = (Destination)msg.getRegionDestination();
                final TopicStorePrefetch tsp = this.topics.get(dest);
                if (tsp != null) {
                    tsp.addMessageLast(node);
                    if (this.prioritizedMessages && this.immediatePriorityDispatch && tsp.isPaging() && msg.getPriority() > tsp.getLastRecoveredPriority()) {
                        tsp.recoverMessage(node.getMessage(), true);
                        StoreDurableSubscriberCursor.LOG.trace("cached high priority ({} message: {}, current paged batch priority: {}, cache size: {}", msg.getPriority(), msg.getMessageId(), tsp.getLastRecoveredPriority(), tsp.batchList.size());
                    }
                }
            }
        }
    }
    
    @Override
    public boolean isTransient() {
        return this.subscription.isKeepDurableSubsActive();
    }
    
    @Override
    public void addMessageFirst(final MessageReference node) throws Exception {
        if (node != null) {
            final Message msg = node.getMessage();
            if (!msg.isPersistent()) {
                this.nonPersistent.addMessageFirst(node);
            }
            else {
                final Destination dest = (Destination)msg.getRegionDestination();
                final TopicStorePrefetch tsp = this.topics.get(dest);
                if (tsp != null) {
                    tsp.addMessageFirst(node);
                }
            }
        }
    }
    
    @Override
    public synchronized void addRecoveredMessage(final MessageReference node) throws Exception {
        this.nonPersistent.addMessageLast(node);
    }
    
    @Override
    public synchronized void clear() {
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            tsp.clear();
        }
    }
    
    @Override
    public synchronized boolean hasNext() {
        boolean result = true;
        if (result) {
            try {
                this.currentCursor = this.getNextCursor();
            }
            catch (Exception e) {
                StoreDurableSubscriberCursor.LOG.error("Failed to get current cursor ", e);
                throw new RuntimeException(e);
            }
            result = (this.currentCursor != null && this.currentCursor.hasNext());
        }
        return result;
    }
    
    @Override
    public synchronized MessageReference next() {
        final MessageReference result = (this.currentCursor != null) ? this.currentCursor.next() : null;
        return result;
    }
    
    @Override
    public synchronized void remove() {
        if (this.currentCursor != null) {
            this.currentCursor.remove();
        }
    }
    
    @Override
    public synchronized void remove(final MessageReference node) {
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            tsp.remove(node);
        }
    }
    
    @Override
    public synchronized void reset() {
        for (final PendingMessageCursor storePrefetch : this.storePrefetches) {
            storePrefetch.reset();
        }
    }
    
    @Override
    public synchronized void release() {
        this.currentCursor = null;
        for (final PendingMessageCursor storePrefetch : this.storePrefetches) {
            storePrefetch.release();
        }
    }
    
    @Override
    public synchronized int size() {
        int pendingCount = 0;
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            pendingCount += tsp.size();
        }
        return pendingCount;
    }
    
    @Override
    public void setMaxBatchSize(final int newMaxBatchSize) {
        for (final PendingMessageCursor storePrefetch : this.storePrefetches) {
            storePrefetch.setMaxBatchSize(newMaxBatchSize);
        }
        super.setMaxBatchSize(newMaxBatchSize);
    }
    
    @Override
    public synchronized void gc() {
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            tsp.gc();
        }
    }
    
    @Override
    public void setSystemUsage(final SystemUsage usageManager) {
        super.setSystemUsage(usageManager);
        for (final PendingMessageCursor tsp : this.storePrefetches) {
            tsp.setSystemUsage(usageManager);
        }
    }
    
    @Override
    public void setMemoryUsageHighWaterMark(final int memoryUsageHighWaterMark) {
        super.setMemoryUsageHighWaterMark(memoryUsageHighWaterMark);
        for (final PendingMessageCursor cursor : this.storePrefetches) {
            cursor.setMemoryUsageHighWaterMark(memoryUsageHighWaterMark);
        }
    }
    
    @Override
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        super.setMaxProducersToAudit(maxProducersToAudit);
        for (final PendingMessageCursor cursor : this.storePrefetches) {
            cursor.setMaxAuditDepth(this.maxAuditDepth);
        }
    }
    
    @Override
    public void setMaxAuditDepth(final int maxAuditDepth) {
        super.setMaxAuditDepth(maxAuditDepth);
        for (final PendingMessageCursor cursor : this.storePrefetches) {
            cursor.setMaxAuditDepth(maxAuditDepth);
        }
    }
    
    @Override
    public void setEnableAudit(final boolean enableAudit) {
        super.setEnableAudit(enableAudit);
        for (final PendingMessageCursor cursor : this.storePrefetches) {
            cursor.setEnableAudit(enableAudit);
        }
    }
    
    @Override
    public void setUseCache(final boolean useCache) {
        super.setUseCache(useCache);
        for (final PendingMessageCursor cursor : this.storePrefetches) {
            cursor.setUseCache(useCache);
        }
    }
    
    protected synchronized PendingMessageCursor getNextCursor() throws Exception {
        if (this.currentCursor == null || this.currentCursor.isEmpty()) {
            this.currentCursor = null;
            for (final PendingMessageCursor tsp : this.storePrefetches) {
                if (tsp.hasNext()) {
                    this.currentCursor = tsp;
                    break;
                }
            }
            if (this.storePrefetches.size() > 1) {
                final PendingMessageCursor first = this.storePrefetches.remove(0);
                this.storePrefetches.add(first);
            }
        }
        return this.currentCursor;
    }
    
    @Override
    public String toString() {
        return "StoreDurableSubscriber(" + this.clientId + ":" + this.subscriberName + ")";
    }
    
    public boolean isImmediatePriorityDispatch() {
        return this.immediatePriorityDispatch;
    }
    
    public void setImmediatePriorityDispatch(final boolean immediatePriorityDispatch) {
        this.immediatePriorityDispatch = immediatePriorityDispatch;
    }
    
    static {
        LOG = LoggerFactory.getLogger(StoreDurableSubscriberCursor.class);
    }
}
