// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.memory;

import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.command.MessageAck;
import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Collections;
import java.util.HashMap;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.LRUCache;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.util.SubscriptionKey;
import java.util.Map;
import org.apache.activemq.store.TopicMessageStore;

public class MemoryTopicMessageStore extends MemoryMessageStore implements TopicMessageStore
{
    private Map<SubscriptionKey, SubscriptionInfo> subscriberDatabase;
    private Map<SubscriptionKey, MemoryTopicSub> topicSubMap;
    
    public MemoryTopicMessageStore(final ActiveMQDestination destination) {
        this(destination, new LRUCache<MessageId, Message>(100, 100, 0.75f, false), makeSubscriptionInfoMap());
    }
    
    public MemoryTopicMessageStore(final ActiveMQDestination destination, final Map<MessageId, Message> messageTable, final Map<SubscriptionKey, SubscriptionInfo> subscriberDatabase) {
        super(destination, messageTable);
        this.subscriberDatabase = subscriberDatabase;
        this.topicSubMap = makeSubMap();
    }
    
    protected static Map<SubscriptionKey, SubscriptionInfo> makeSubscriptionInfoMap() {
        return Collections.synchronizedMap(new HashMap<SubscriptionKey, SubscriptionInfo>());
    }
    
    protected static Map<SubscriptionKey, MemoryTopicSub> makeSubMap() {
        return Collections.synchronizedMap(new HashMap<SubscriptionKey, MemoryTopicSub>());
    }
    
    @Override
    public synchronized void addMessage(final ConnectionContext context, final Message message) throws IOException {
        super.addMessage(context, message);
        for (final MemoryTopicSub sub : this.topicSubMap.values()) {
            sub.addMessage(message.getMessageId(), message);
        }
    }
    
    @Override
    public synchronized void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        final SubscriptionKey key = new SubscriptionKey(clientId, subscriptionName);
        final MemoryTopicSub sub = this.topicSubMap.get(key);
        if (sub != null) {
            sub.removeMessage(messageId);
        }
    }
    
    @Override
    public synchronized SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
        return this.subscriberDatabase.get(new SubscriptionKey(clientId, subscriptionName));
    }
    
    @Override
    public synchronized void addSubscription(final SubscriptionInfo info, final boolean retroactive) throws IOException {
        final SubscriptionKey key = new SubscriptionKey(info);
        final MemoryTopicSub sub = new MemoryTopicSub();
        this.topicSubMap.put(key, sub);
        if (retroactive) {
            for (final Map.Entry entry : this.messageTable.entrySet()) {
                sub.addMessage(entry.getKey(), entry.getValue());
            }
        }
        this.subscriberDatabase.put(key, info);
    }
    
    @Override
    public synchronized void deleteSubscription(final String clientId, final String subscriptionName) {
        final SubscriptionKey key = new SubscriptionKey(clientId, subscriptionName);
        this.subscriberDatabase.remove(key);
        this.topicSubMap.remove(key);
    }
    
    @Override
    public synchronized void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
        final MemoryTopicSub sub = this.topicSubMap.get(new SubscriptionKey(clientId, subscriptionName));
        if (sub != null) {
            sub.recoverSubscription(listener);
        }
    }
    
    @Override
    public synchronized void delete() {
        super.delete();
        this.subscriberDatabase.clear();
        this.topicSubMap.clear();
    }
    
    @Override
    public SubscriptionInfo[] getAllSubscriptions() throws IOException {
        return this.subscriberDatabase.values().toArray(new SubscriptionInfo[this.subscriberDatabase.size()]);
    }
    
    @Override
    public synchronized int getMessageCount(final String clientId, final String subscriberName) throws IOException {
        int result = 0;
        final MemoryTopicSub sub = this.topicSubMap.get(new SubscriptionKey(clientId, subscriberName));
        if (sub != null) {
            result = sub.size();
        }
        return result;
    }
    
    @Override
    public synchronized void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        final MemoryTopicSub sub = this.topicSubMap.get(new SubscriptionKey(clientId, subscriptionName));
        if (sub != null) {
            sub.recoverNextMessages(maxReturned, listener);
        }
    }
    
    @Override
    public void resetBatching(final String clientId, final String subscriptionName) {
        final MemoryTopicSub sub = this.topicSubMap.get(new SubscriptionKey(clientId, subscriptionName));
        if (sub != null) {
            sub.resetBatching();
        }
    }
}
