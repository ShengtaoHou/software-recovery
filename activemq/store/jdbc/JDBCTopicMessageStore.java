// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import java.util.Arrays;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.Iterator;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.Message;
import org.apache.activemq.store.MessageRecoveryListener;
import java.sql.SQLException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConnectionContext;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.command.MessageId;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.activemq.store.TopicMessageStore;

public class JDBCTopicMessageStore extends JDBCMessageStore implements TopicMessageStore
{
    private static final Logger LOG;
    private Map<String, LastRecovered> subscriberLastRecoveredMap;
    private Set<String> pendingCompletion;
    public static final String PROPERTY_SEQUENCE_ID_CACHE_SIZE = "org.apache.activemq.store.jdbc.SEQUENCE_ID_CACHE_SIZE";
    private static final int SEQUENCE_ID_CACHE_SIZE;
    private final ReentrantReadWriteLock sequenceIdCacheSizeLock;
    private Map<MessageId, long[]> sequenceIdCache;
    
    public JDBCTopicMessageStore(final JDBCPersistenceAdapter persistenceAdapter, final JDBCAdapter adapter, final WireFormat wireFormat, final ActiveMQTopic topic, final ActiveMQMessageAudit audit) throws IOException {
        super(persistenceAdapter, adapter, wireFormat, topic, audit);
        this.subscriberLastRecoveredMap = new ConcurrentHashMap<String, LastRecovered>();
        this.pendingCompletion = new HashSet<String>();
        this.sequenceIdCacheSizeLock = new ReentrantReadWriteLock();
        this.sequenceIdCache = new LinkedHashMap<MessageId, long[]>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<MessageId, long[]> eldest) {
                return this.size() > JDBCTopicMessageStore.SEQUENCE_ID_CACHE_SIZE;
            }
        };
    }
    
    @Override
    public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        if (ack != null && ack.isUnmatchedAck()) {
            if (JDBCTopicMessageStore.LOG.isTraceEnabled()) {
                JDBCTopicMessageStore.LOG.trace("ignoring unmatched selector ack for: " + messageId + ", cleanup will get to this message after subsequent acks.");
            }
            return;
        }
        final TransactionContext c = this.persistenceAdapter.getTransactionContext(context);
        try {
            final long[] res = this.getCachedStoreSequenceId(c, this.destination, messageId);
            if (this.isPrioritizedMessages()) {
                this.adapter.doSetLastAckWithPriority(c, this.destination, (context != null) ? context.getXid() : null, clientId, subscriptionName, res[0], res[1]);
            }
            else {
                this.adapter.doSetLastAck(c, this.destination, (context != null) ? context.getXid() : null, clientId, subscriptionName, res[0], res[1]);
            }
            if (JDBCTopicMessageStore.LOG.isTraceEnabled()) {
                JDBCTopicMessageStore.LOG.trace(clientId + ":" + subscriptionName + " ack, seq: " + res[0] + ", priority: " + res[1] + " mid:" + messageId);
            }
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to store acknowledgment for: " + clientId + " on message " + messageId + " in container: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public long[] getCachedStoreSequenceId(final TransactionContext transactionContext, final ActiveMQDestination destination, final MessageId messageId) throws SQLException, IOException {
        long[] val = null;
        this.sequenceIdCacheSizeLock.readLock().lock();
        try {
            val = this.sequenceIdCache.get(messageId);
        }
        finally {
            this.sequenceIdCacheSizeLock.readLock().unlock();
        }
        if (val == null) {
            val = this.adapter.getStoreSequenceId(transactionContext, destination, messageId);
        }
        return val;
    }
    
    @Override
    public void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            this.adapter.doRecoverSubscription(c, this.destination, clientId, subscriptionName, new JDBCMessageRecoveryListener() {
                @Override
                public boolean recoverMessage(final long sequenceId, final byte[] data) throws Exception {
                    final Message msg = (Message)JDBCTopicMessageStore.this.wireFormat.unmarshal(new ByteSequence(data));
                    msg.getMessageId().setBrokerSequenceId(sequenceId);
                    return listener.recoverMessage(msg);
                }
                
                @Override
                public boolean recoverMessageReference(final String reference) throws Exception {
                    return listener.recoverMessageReference(new MessageId(reference));
                }
            });
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to recover subscription: " + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public synchronized void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        final String key = this.getSubscriptionKey(clientId, subscriptionName);
        if (!this.subscriberLastRecoveredMap.containsKey(key)) {
            this.subscriberLastRecoveredMap.put(key, new LastRecovered());
        }
        final LastRecovered lastRecovered = this.subscriberLastRecoveredMap.get(key);
        final LastRecoveredAwareListener recoveredAwareListener = new LastRecoveredAwareListener(listener, maxReturned);
        try {
            if (JDBCTopicMessageStore.LOG.isTraceEnabled()) {
                JDBCTopicMessageStore.LOG.trace(this + ", " + key + " existing last recovered: " + lastRecovered);
            }
            if (this.isPrioritizedMessages()) {
                final Iterator<LastRecoveredEntry> it = lastRecovered.iterator();
                while (it.hasNext() && !recoveredAwareListener.complete()) {
                    final LastRecoveredEntry entry = it.next();
                    recoveredAwareListener.setLastRecovered(entry);
                    this.adapter.doRecoverNextMessagesWithPriority(c, this.destination, clientId, subscriptionName, entry.recovered, entry.priority, maxReturned, recoveredAwareListener);
                    if (recoveredAwareListener.stalled()) {
                        if (recoveredAwareListener.complete()) {
                            break;
                        }
                        entry.exhausted();
                    }
                }
            }
            else {
                final LastRecoveredEntry last = lastRecovered.defaultPriority();
                recoveredAwareListener.setLastRecovered(last);
                this.adapter.doRecoverNextMessages(c, this.destination, clientId, subscriptionName, last.recovered, 0L, maxReturned, recoveredAwareListener);
            }
            if (JDBCTopicMessageStore.LOG.isTraceEnabled()) {
                JDBCTopicMessageStore.LOG.trace(key + " last recovered: " + lastRecovered);
            }
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public void resetBatching(final String clientId, final String subscriptionName) {
        final String key = this.getSubscriptionKey(clientId, subscriptionName);
        if (!this.pendingCompletion.contains(key)) {
            this.subscriberLastRecoveredMap.remove(key);
        }
        else {
            JDBCTopicMessageStore.LOG.trace(this + ", skip resetBatch during pending completion for: " + key);
        }
    }
    
    public void pendingCompletion(final String clientId, final String subscriptionName, final long sequenceId, final byte priority) {
        final String key = this.getSubscriptionKey(clientId, subscriptionName);
        final LastRecovered recovered = new LastRecovered();
        recovered.perPriority[this.isPrioritizedMessages() ? priority : 4].recovered = sequenceId;
        this.subscriberLastRecoveredMap.put(key, recovered);
        this.pendingCompletion.add(key);
        JDBCTopicMessageStore.LOG.trace(this + ", pending completion: " + key + ", last: " + recovered);
    }
    
    public void complete(final String clientId, final String subscriptionName) {
        this.pendingCompletion.remove(this.getSubscriptionKey(clientId, subscriptionName));
        JDBCTopicMessageStore.LOG.trace(this + ", completion for: " + this.getSubscriptionKey(clientId, subscriptionName));
    }
    
    @Override
    protected void onAdd(final MessageId messageId, final long sequenceId, final byte priority) {
        for (final LastRecovered last : this.subscriberLastRecoveredMap.values()) {
            last.updateStored(sequenceId, priority);
        }
        this.sequenceIdCacheSizeLock.writeLock().lock();
        try {
            this.sequenceIdCache.put(messageId, new long[] { sequenceId, priority });
        }
        finally {
            this.sequenceIdCacheSizeLock.writeLock().unlock();
        }
    }
    
    @Override
    public void addSubscription(final SubscriptionInfo subscriptionInfo, final boolean retroactive) throws IOException {
        TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            c = this.persistenceAdapter.getTransactionContext();
            this.adapter.doSetSubscriberEntry(c, subscriptionInfo, retroactive, this.isPrioritizedMessages());
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to lookup subscription for info: " + subscriptionInfo.getClientId() + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            return this.adapter.doGetSubscriberEntry(c, this.destination, clientId, subscriptionName);
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to lookup subscription for: " + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public void deleteSubscription(final String clientId, final String subscriptionName) throws IOException {
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            this.adapter.doDeleteSubscription(c, this.destination, clientId, subscriptionName);
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to remove subscription for: " + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
            this.resetBatching(clientId, subscriptionName);
        }
    }
    
    @Override
    public SubscriptionInfo[] getAllSubscriptions() throws IOException {
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            return this.adapter.doGetAllSubscriptions(c, this.destination);
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to lookup subscriptions. Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public int getMessageCount(final String clientId, final String subscriberName) throws IOException {
        int result = 0;
        final TransactionContext c = this.persistenceAdapter.getTransactionContext();
        try {
            result = this.adapter.doGetDurableSubscriberMessageCount(c, this.destination, clientId, subscriberName, this.isPrioritizedMessages());
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to get Message Count: " + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
        if (JDBCTopicMessageStore.LOG.isTraceEnabled()) {
            JDBCTopicMessageStore.LOG.trace(clientId + ":" + subscriberName + ", messageCount: " + result);
        }
        return result;
    }
    
    protected String getSubscriptionKey(final String clientId, final String subscriberName) {
        String result = clientId + ":";
        result += ((subscriberName != null) ? subscriberName : "NOT_SET");
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JDBCTopicMessageStore.class);
        SEQUENCE_ID_CACHE_SIZE = Integer.parseInt(System.getProperty("org.apache.activemq.store.jdbc.SEQUENCE_ID_CACHE_SIZE", "1000"), 10);
    }
    
    private class LastRecovered implements Iterable<LastRecoveredEntry>
    {
        LastRecoveredEntry[] perPriority;
        
        LastRecovered() {
            this.perPriority = new LastRecoveredEntry[10];
            for (int i = 0; i < this.perPriority.length; ++i) {
                this.perPriority[i] = new LastRecoveredEntry(i);
            }
        }
        
        public void updateStored(final long sequence, final int priority) {
            this.perPriority[priority].stored = sequence;
        }
        
        public LastRecoveredEntry defaultPriority() {
            return this.perPriority[4];
        }
        
        @Override
        public String toString() {
            return Arrays.deepToString(this.perPriority);
        }
        
        @Override
        public Iterator<LastRecoveredEntry> iterator() {
            return new PriorityIterator();
        }
        
        class PriorityIterator implements Iterator<LastRecoveredEntry>
        {
            int current;
            
            PriorityIterator() {
                this.current = 9;
            }
            
            @Override
            public boolean hasNext() {
                for (int i = this.current; i >= 0; --i) {
                    if (LastRecovered.this.perPriority[i].hasMessages()) {
                        this.current = i;
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public LastRecoveredEntry next() {
                return LastRecovered.this.perPriority[this.current];
            }
            
            @Override
            public void remove() {
                throw new RuntimeException("not implemented");
            }
        }
    }
    
    private class LastRecoveredEntry
    {
        final int priority;
        long recovered;
        long stored;
        
        public LastRecoveredEntry(final int priority) {
            this.recovered = 0L;
            this.stored = 2147483647L;
            this.priority = priority;
        }
        
        @Override
        public String toString() {
            return this.priority + "-" + this.stored + ":" + this.recovered;
        }
        
        public void exhausted() {
            this.stored = this.recovered;
        }
        
        public boolean hasMessages() {
            return this.stored > this.recovered;
        }
    }
    
    class LastRecoveredAwareListener implements JDBCMessageRecoveryListener
    {
        final MessageRecoveryListener delegate;
        final int maxMessages;
        LastRecoveredEntry lastRecovered;
        int recoveredCount;
        int recoveredMarker;
        
        public LastRecoveredAwareListener(final MessageRecoveryListener delegate, final int maxMessages) {
            this.delegate = delegate;
            this.maxMessages = maxMessages;
        }
        
        @Override
        public boolean recoverMessage(final long sequenceId, final byte[] data) throws Exception {
            if (this.delegate.hasSpace() && this.recoveredCount < this.maxMessages) {
                final Message msg = (Message)JDBCTopicMessageStore.this.wireFormat.unmarshal(new ByteSequence(data));
                msg.getMessageId().setBrokerSequenceId(sequenceId);
                this.lastRecovered.recovered = sequenceId;
                if (this.delegate.recoverMessage(msg)) {
                    ++this.recoveredCount;
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean recoverMessageReference(final String reference) throws Exception {
            return this.delegate.recoverMessageReference(new MessageId(reference));
        }
        
        public void setLastRecovered(final LastRecoveredEntry lastRecovered) {
            this.lastRecovered = lastRecovered;
            this.recoveredMarker = this.recoveredCount;
        }
        
        public boolean complete() {
            return !this.delegate.hasSpace() || this.recoveredCount == this.maxMessages;
        }
        
        public boolean stalled() {
            return this.recoveredMarker == this.recoveredCount;
        }
    }
}
