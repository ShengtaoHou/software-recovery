// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.memory;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.command.ActiveMQTopic;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQQueue;
import java.io.File;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.store.PersistenceAdapter;

public class MemoryPersistenceAdapter implements PersistenceAdapter
{
    private static final Logger LOG;
    MemoryTransactionStore transactionStore;
    ConcurrentHashMap<ActiveMQDestination, TopicMessageStore> topics;
    ConcurrentHashMap<ActiveMQDestination, MessageStore> queues;
    private boolean useExternalMessageReferences;
    
    public MemoryPersistenceAdapter() {
        this.topics = new ConcurrentHashMap<ActiveMQDestination, TopicMessageStore>();
        this.queues = new ConcurrentHashMap<ActiveMQDestination, MessageStore>();
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        final Set<ActiveMQDestination> rc = new HashSet<ActiveMQDestination>(this.queues.size() + this.topics.size());
        Iterator<ActiveMQDestination> iter = this.queues.keySet().iterator();
        while (iter.hasNext()) {
            rc.add(iter.next());
        }
        iter = this.topics.keySet().iterator();
        while (iter.hasNext()) {
            rc.add(iter.next());
        }
        return rc;
    }
    
    public static MemoryPersistenceAdapter newInstance(final File file) {
        return new MemoryPersistenceAdapter();
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        MessageStore rc = this.queues.get(destination);
        if (rc == null) {
            rc = new MemoryMessageStore(destination);
            if (this.transactionStore != null) {
                rc = this.transactionStore.proxy(rc);
            }
            this.queues.put(destination, rc);
        }
        return rc;
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        TopicMessageStore rc = this.topics.get(destination);
        if (rc == null) {
            rc = new MemoryTopicMessageStore(destination);
            if (this.transactionStore != null) {
                rc = this.transactionStore.proxy(rc);
            }
            this.topics.put(destination, rc);
        }
        return rc;
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
        this.queues.remove(destination);
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
        this.topics.remove(destination);
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        if (this.transactionStore == null) {
            this.transactionStore = new MemoryTransactionStore(this);
        }
        return this.transactionStore;
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) {
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) {
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) {
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return 0L;
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        final Iterator<TopicMessageStore> iter = this.topics.values().iterator();
        while (iter.hasNext()) {
            final MemoryMessageStore store = this.asMemoryMessageStore(iter.next());
            if (store != null) {
                store.delete();
            }
        }
        final Iterator<MessageStore> iter2 = this.queues.values().iterator();
        while (iter2.hasNext()) {
            final MemoryMessageStore store = this.asMemoryMessageStore(iter2.next());
            if (store != null) {
                store.delete();
            }
        }
        if (this.transactionStore != null) {
            this.transactionStore.delete();
        }
    }
    
    public boolean isUseExternalMessageReferences() {
        return this.useExternalMessageReferences;
    }
    
    public void setUseExternalMessageReferences(final boolean useExternalMessageReferences) {
        this.useExternalMessageReferences = useExternalMessageReferences;
    }
    
    protected MemoryMessageStore asMemoryMessageStore(final Object value) {
        if (value instanceof MemoryMessageStore) {
            return (MemoryMessageStore)value;
        }
        if (value instanceof ProxyMessageStore) {
            final MessageStore delegate = ((ProxyMessageStore)value).getDelegate();
            if (delegate instanceof MemoryMessageStore) {
                return (MemoryMessageStore)delegate;
            }
        }
        MemoryPersistenceAdapter.LOG.warn("Expected an instance of MemoryMessageStore but was: " + value);
        return null;
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
    }
    
    @Override
    public String toString() {
        return "MemoryPersistenceAdapter";
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
    }
    
    @Override
    public void setDirectory(final File dir) {
    }
    
    @Override
    public File getDirectory() {
        return null;
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
    }
    
    @Override
    public long size() {
        return 0L;
    }
    
    public void setCreateTransactionStore(final boolean create) throws IOException {
        if (create) {
            this.createTransactionStore();
        }
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) {
        return -1L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MemoryPersistenceAdapter.class);
    }
}
