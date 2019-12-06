// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import java.io.IOException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;

public class ProxyTopicMessageStore implements TopicMessageStore
{
    final TopicMessageStore delegate;
    
    public ProxyTopicMessageStore(final TopicMessageStore delegate) {
        this.delegate = delegate;
    }
    
    public MessageStore getDelegate() {
        return this.delegate;
    }
    
    @Override
    public void addMessage(final ConnectionContext context, final Message message) throws IOException {
        this.delegate.addMessage(context, message);
    }
    
    @Override
    public void addMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        this.delegate.addMessage(context, message, canOptimizeHint);
    }
    
    @Override
    public Message getMessage(final MessageId identity) throws IOException {
        return this.delegate.getMessage(identity);
    }
    
    @Override
    public void recover(final MessageRecoveryListener listener) throws Exception {
        this.delegate.recover(listener);
    }
    
    @Override
    public void removeAllMessages(final ConnectionContext context) throws IOException {
        this.delegate.removeAllMessages(context);
    }
    
    @Override
    public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
        this.delegate.removeMessage(context, ack);
    }
    
    @Override
    public void start() throws Exception {
        this.delegate.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.delegate.stop();
    }
    
    @Override
    public SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
        return this.delegate.lookupSubscription(clientId, subscriptionName);
    }
    
    @Override
    public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        this.delegate.acknowledge(context, clientId, subscriptionName, messageId, ack);
    }
    
    @Override
    public void addSubscription(final SubscriptionInfo subscriptionInfo, final boolean retroactive) throws IOException {
        this.delegate.addSubscription(subscriptionInfo, retroactive);
    }
    
    @Override
    public void deleteSubscription(final String clientId, final String subscriptionName) throws IOException {
        this.delegate.deleteSubscription(clientId, subscriptionName);
    }
    
    @Override
    public void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
        this.delegate.recoverSubscription(clientId, subscriptionName, listener);
    }
    
    @Override
    public void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        this.delegate.recoverNextMessages(clientId, subscriptionName, maxReturned, listener);
    }
    
    @Override
    public void resetBatching(final String clientId, final String subscriptionName) {
        this.delegate.resetBatching(clientId, subscriptionName);
    }
    
    @Override
    public ActiveMQDestination getDestination() {
        return this.delegate.getDestination();
    }
    
    @Override
    public SubscriptionInfo[] getAllSubscriptions() throws IOException {
        return this.delegate.getAllSubscriptions();
    }
    
    @Override
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
        this.delegate.setMemoryUsage(memoryUsage);
    }
    
    @Override
    public int getMessageCount(final String clientId, final String subscriberName) throws IOException {
        return this.delegate.getMessageCount(clientId, subscriberName);
    }
    
    @Override
    public int getMessageCount() throws IOException {
        return this.delegate.getMessageCount();
    }
    
    @Override
    public void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        this.delegate.recoverNextMessages(maxReturned, listener);
    }
    
    @Override
    public void dispose(final ConnectionContext context) {
        this.delegate.dispose(context);
    }
    
    @Override
    public void resetBatching() {
        this.delegate.resetBatching();
    }
    
    @Override
    public void setBatch(final MessageId messageId) throws Exception {
        this.delegate.setBatch(messageId);
    }
    
    @Override
    public boolean isEmpty() throws Exception {
        return this.delegate.isEmpty();
    }
    
    @Override
    public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
        return this.delegate.asyncAddTopicMessage(context, message);
    }
    
    @Override
    public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        return this.delegate.asyncAddTopicMessage(context, message, canOptimizeHint);
    }
    
    @Override
    public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
        return this.delegate.asyncAddQueueMessage(context, message);
    }
    
    @Override
    public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
        return this.delegate.asyncAddQueueMessage(context, message, canOptimizeHint);
    }
    
    @Override
    public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
        this.delegate.removeAsyncMessage(context, ack);
    }
    
    @Override
    public void setPrioritizedMessages(final boolean prioritizedMessages) {
        this.delegate.setPrioritizedMessages(prioritizedMessages);
    }
    
    @Override
    public boolean isPrioritizedMessages() {
        return this.delegate.isPrioritizedMessages();
    }
    
    @Override
    public void updateMessage(final Message message) throws IOException {
        this.delegate.updateMessage(message);
    }
}
