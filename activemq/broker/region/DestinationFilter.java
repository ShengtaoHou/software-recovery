// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.util.SubscriptionKey;
import org.apache.activemq.broker.region.policy.SlowConsumerStrategy;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.store.MessageStore;
import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.broker.Broker;
import java.util.List;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import java.io.IOException;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConnectionContext;

public class DestinationFilter implements Destination
{
    protected final Destination next;
    
    public DestinationFilter(final Destination next) {
        this.next = next;
    }
    
    @Override
    public void acknowledge(final ConnectionContext context, final Subscription sub, final MessageAck ack, final MessageReference node) throws IOException {
        this.next.acknowledge(context, sub, ack, node);
    }
    
    @Override
    public void addSubscription(final ConnectionContext context, final Subscription sub) throws Exception {
        this.next.addSubscription(context, sub);
    }
    
    @Override
    public Message[] browse() {
        return this.next.browse();
    }
    
    @Override
    public void dispose(final ConnectionContext context) throws IOException {
        this.next.dispose(context);
    }
    
    @Override
    public boolean isDisposed() {
        return this.next.isDisposed();
    }
    
    @Override
    public void gc() {
        this.next.gc();
    }
    
    @Override
    public void markForGC(final long timeStamp) {
        this.next.markForGC(timeStamp);
    }
    
    @Override
    public boolean canGC() {
        return this.next.canGC();
    }
    
    @Override
    public long getInactiveTimoutBeforeGC() {
        return this.next.getInactiveTimoutBeforeGC();
    }
    
    @Override
    public ActiveMQDestination getActiveMQDestination() {
        return this.next.getActiveMQDestination();
    }
    
    @Override
    public DeadLetterStrategy getDeadLetterStrategy() {
        return this.next.getDeadLetterStrategy();
    }
    
    @Override
    public DestinationStatistics getDestinationStatistics() {
        return this.next.getDestinationStatistics();
    }
    
    @Override
    public String getName() {
        return this.next.getName();
    }
    
    @Override
    public MemoryUsage getMemoryUsage() {
        return this.next.getMemoryUsage();
    }
    
    @Override
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
        this.next.setMemoryUsage(memoryUsage);
    }
    
    @Override
    public void removeSubscription(final ConnectionContext context, final Subscription sub, final long lastDeliveredSequenceId) throws Exception {
        this.next.removeSubscription(context, sub, lastDeliveredSequenceId);
    }
    
    @Override
    public void send(final ProducerBrokerExchange context, final Message messageSend) throws Exception {
        this.next.send(context, messageSend);
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
    public List<Subscription> getConsumers() {
        return this.next.getConsumers();
    }
    
    protected void send(final ProducerBrokerExchange context, final Message message, final ActiveMQDestination destination) throws Exception {
        final Broker broker = context.getConnectionContext().getBroker();
        final Set<Destination> destinations = broker.getDestinations(destination);
        for (final Destination dest : destinations) {
            dest.send(context, message.copy());
        }
    }
    
    @Override
    public MessageStore getMessageStore() {
        return this.next.getMessageStore();
    }
    
    @Override
    public boolean isProducerFlowControl() {
        return this.next.isProducerFlowControl();
    }
    
    @Override
    public void setProducerFlowControl(final boolean value) {
        this.next.setProducerFlowControl(value);
    }
    
    @Override
    public boolean isAlwaysRetroactive() {
        return this.next.isAlwaysRetroactive();
    }
    
    @Override
    public void setAlwaysRetroactive(final boolean value) {
        this.next.setAlwaysRetroactive(value);
    }
    
    @Override
    public void setBlockedProducerWarningInterval(final long blockedProducerWarningInterval) {
        this.next.setBlockedProducerWarningInterval(blockedProducerWarningInterval);
    }
    
    @Override
    public long getBlockedProducerWarningInterval() {
        return this.next.getBlockedProducerWarningInterval();
    }
    
    @Override
    public void addProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.addProducer(context, info);
    }
    
    @Override
    public void removeProducer(final ConnectionContext context, final ProducerInfo info) throws Exception {
        this.next.removeProducer(context, info);
    }
    
    @Override
    public int getMaxAuditDepth() {
        return this.next.getMaxAuditDepth();
    }
    
    @Override
    public int getMaxProducersToAudit() {
        return this.next.getMaxProducersToAudit();
    }
    
    @Override
    public boolean isEnableAudit() {
        return this.next.isEnableAudit();
    }
    
    @Override
    public void setEnableAudit(final boolean enableAudit) {
        this.next.setEnableAudit(enableAudit);
    }
    
    @Override
    public void setMaxAuditDepth(final int maxAuditDepth) {
        this.next.setMaxAuditDepth(maxAuditDepth);
    }
    
    @Override
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.next.setMaxProducersToAudit(maxProducersToAudit);
    }
    
    @Override
    public boolean isActive() {
        return this.next.isActive();
    }
    
    @Override
    public int getMaxPageSize() {
        return this.next.getMaxPageSize();
    }
    
    @Override
    public void setMaxPageSize(final int maxPageSize) {
        this.next.setMaxPageSize(maxPageSize);
    }
    
    @Override
    public boolean isUseCache() {
        return this.next.isUseCache();
    }
    
    @Override
    public void setUseCache(final boolean useCache) {
        this.next.setUseCache(useCache);
    }
    
    @Override
    public int getMinimumMessageSize() {
        return this.next.getMinimumMessageSize();
    }
    
    @Override
    public void setMinimumMessageSize(final int minimumMessageSize) {
        this.next.setMinimumMessageSize(minimumMessageSize);
    }
    
    @Override
    public void wakeup() {
        this.next.wakeup();
    }
    
    @Override
    public boolean isLazyDispatch() {
        return this.next.isLazyDispatch();
    }
    
    @Override
    public void setLazyDispatch(final boolean value) {
        this.next.setLazyDispatch(value);
    }
    
    public void messageExpired(final ConnectionContext context, final PrefetchSubscription prefetchSubscription, final MessageReference node) {
        this.next.messageExpired(context, prefetchSubscription, node);
    }
    
    @Override
    public boolean iterate() {
        return this.next.iterate();
    }
    
    @Override
    public void fastProducer(final ConnectionContext context, final ProducerInfo producerInfo) {
        this.next.fastProducer(context, producerInfo);
    }
    
    @Override
    public void isFull(final ConnectionContext context, final Usage<?> usage) {
        this.next.isFull(context, usage);
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
    public void slowConsumer(final ConnectionContext context, final Subscription subs) {
        this.next.slowConsumer(context, subs);
    }
    
    @Override
    public void messageExpired(final ConnectionContext context, final Subscription subs, final MessageReference node) {
        this.next.messageExpired(context, subs, node);
    }
    
    @Override
    public int getMaxBrowsePageSize() {
        return this.next.getMaxBrowsePageSize();
    }
    
    @Override
    public void setMaxBrowsePageSize(final int maxPageSize) {
        this.next.setMaxBrowsePageSize(maxPageSize);
    }
    
    @Override
    public void processDispatchNotification(final MessageDispatchNotification messageDispatchNotification) throws Exception {
        this.next.processDispatchNotification(messageDispatchNotification);
    }
    
    @Override
    public int getCursorMemoryHighWaterMark() {
        return this.next.getCursorMemoryHighWaterMark();
    }
    
    @Override
    public void setCursorMemoryHighWaterMark(final int cursorMemoryHighWaterMark) {
        this.next.setCursorMemoryHighWaterMark(cursorMemoryHighWaterMark);
    }
    
    @Override
    public boolean isPrioritizedMessages() {
        return this.next.isPrioritizedMessages();
    }
    
    @Override
    public SlowConsumerStrategy getSlowConsumerStrategy() {
        return this.next.getSlowConsumerStrategy();
    }
    
    @Override
    public boolean isDoOptimzeMessageStorage() {
        return this.next.isDoOptimzeMessageStorage();
    }
    
    @Override
    public void setDoOptimzeMessageStorage(final boolean doOptimzeMessageStorage) {
        this.next.setDoOptimzeMessageStorage(doOptimzeMessageStorage);
    }
    
    @Override
    public void clearPendingMessages() {
        this.next.clearPendingMessages();
    }
    
    @Override
    public boolean isDLQ() {
        return this.next.isDLQ();
    }
    
    @Override
    public void duplicateFromStore(final Message message, final Subscription subscription) {
        this.next.duplicateFromStore(message, subscription);
    }
    
    public void deleteSubscription(final ConnectionContext context, final SubscriptionKey key) throws Exception {
        if (this.next instanceof DestinationFilter) {
            final DestinationFilter filter = (DestinationFilter)this.next;
            filter.deleteSubscription(context, key);
        }
        else if (this.next instanceof Topic) {
            final Topic topic = (Topic)this.next;
            topic.deleteSubscription(context, key);
        }
    }
    
    public Destination getNext() {
        return this.next;
    }
}
