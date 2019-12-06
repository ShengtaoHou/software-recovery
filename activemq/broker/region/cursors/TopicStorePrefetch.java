// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.slf4j.LoggerFactory;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.store.TopicMessageStore;
import org.slf4j.Logger;

class TopicStorePrefetch extends AbstractStoreCursor
{
    private static final Logger LOG;
    private final TopicMessageStore store;
    private final String clientId;
    private final String subscriberName;
    private final Subscription subscription;
    private byte lastRecoveredPriority;
    
    public TopicStorePrefetch(final Subscription subscription, final Topic topic, final String clientId, final String subscriberName) {
        super(topic);
        this.lastRecoveredPriority = 9;
        this.subscription = subscription;
        this.store = (TopicMessageStore)topic.getMessageStore();
        this.clientId = clientId;
        this.subscriberName = subscriberName;
        this.maxProducersToAudit = 32;
        this.maxAuditDepth = 10000;
        this.resetSize();
    }
    
    @Override
    public boolean recoverMessageReference(final MessageId messageReference) throws Exception {
        throw new RuntimeException("Not supported");
    }
    
    @Override
    public synchronized void addMessageFirst(final MessageReference node) throws Exception {
        this.batchList.addMessageFirst(node);
        ++this.size;
    }
    
    @Override
    public synchronized boolean recoverMessage(final Message message, final boolean cached) throws Exception {
        TopicStorePrefetch.LOG.trace("{} recover: {}, priority: {}", this, message.getMessageId(), message.getPriority());
        boolean recovered = false;
        final MessageEvaluationContext messageEvaluationContext = new NonCachedMessageEvaluationContext();
        messageEvaluationContext.setMessageReference(message);
        if (this.subscription.matches(message, messageEvaluationContext)) {
            recovered = super.recoverMessage(message, cached);
            if (recovered && !cached) {
                this.lastRecoveredPriority = message.getPriority();
            }
        }
        return recovered;
    }
    
    @Override
    protected synchronized int getStoreSize() {
        try {
            return this.store.getMessageCount(this.clientId, this.subscriberName);
        }
        catch (Exception e) {
            TopicStorePrefetch.LOG.error("{} Failed to get the outstanding message count from the store", this, e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected synchronized boolean isStoreEmpty() {
        try {
            return this.store.isEmpty();
        }
        catch (Exception e) {
            TopicStorePrefetch.LOG.error("Failed to determine if store is empty", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void resetBatch() {
        this.store.resetBatching(this.clientId, this.subscriberName);
    }
    
    @Override
    protected void doFillBatch() throws Exception {
        this.store.recoverNextMessages(this.clientId, this.subscriberName, this.maxBatchSize, this);
    }
    
    public byte getLastRecoveredPriority() {
        return this.lastRecoveredPriority;
    }
    
    public final boolean isPaging() {
        return !this.isCacheEnabled() && !this.batchList.isEmpty();
    }
    
    @Override
    public Subscription getSubscription() {
        return this.subscription;
    }
    
    @Override
    public String toString() {
        return "TopicStorePrefetch(" + this.clientId + "," + this.subscriberName + ") " + this.subscription.getConsumerInfo().getConsumerId() + " - " + super.toString();
    }
    
    static {
        LOG = LoggerFactory.getLogger(TopicStorePrefetch.class);
    }
}
