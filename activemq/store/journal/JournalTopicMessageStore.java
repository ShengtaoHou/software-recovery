// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.journal;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.activemq.util.Callback;
import org.apache.activeio.journal.RecordLocation;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.JournalTopicAck;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import java.io.IOException;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.util.SubscriptionKey;
import java.util.HashMap;
import org.slf4j.Logger;
import org.apache.activemq.store.TopicMessageStore;

public class JournalTopicMessageStore extends JournalMessageStore implements TopicMessageStore
{
    private static final Logger LOG;
    private TopicMessageStore longTermStore;
    private HashMap<SubscriptionKey, MessageId> ackedLastAckLocations;
    
    public JournalTopicMessageStore(final JournalPersistenceAdapter adapter, final TopicMessageStore checkpointStore, final ActiveMQTopic destinationName) {
        super(adapter, checkpointStore, destinationName);
        this.ackedLastAckLocations = new HashMap<SubscriptionKey, MessageId>();
        this.longTermStore = checkpointStore;
    }
    
    @Override
    public void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.recoverSubscription(clientId, subscriptionName, listener);
    }
    
    @Override
    public void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.recoverNextMessages(clientId, subscriptionName, maxReturned, listener);
    }
    
    @Override
    public SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
        return this.longTermStore.lookupSubscription(clientId, subscriptionName);
    }
    
    @Override
    public void addSubscription(final SubscriptionInfo subscriptionInfo, final boolean retroactive) throws IOException {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.addSubscription(subscriptionInfo, retroactive);
    }
    
    @Override
    public void addMessage(final ConnectionContext context, final Message message) throws IOException {
        super.addMessage(context, message);
    }
    
    @Override
    public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck originalAck) throws IOException {
        final boolean debug = JournalTopicMessageStore.LOG.isDebugEnabled();
        final JournalTopicAck ack = new JournalTopicAck();
        ack.setDestination(this.destination);
        ack.setMessageId(messageId);
        ack.setMessageSequenceId(messageId.getBrokerSequenceId());
        ack.setSubscritionName(subscriptionName);
        ack.setClientId(clientId);
        ack.setTransactionId((context.getTransaction() != null) ? context.getTransaction().getTransactionId() : null);
        final RecordLocation location = this.peristenceAdapter.writeCommand(ack, false);
        final SubscriptionKey key = new SubscriptionKey(clientId, subscriptionName);
        if (!context.isInTransaction()) {
            if (debug) {
                JournalTopicMessageStore.LOG.debug("Journalled acknowledge for: " + messageId + ", at: " + location);
            }
            this.acknowledge(messageId, location, key);
        }
        else {
            if (debug) {
                JournalTopicMessageStore.LOG.debug("Journalled transacted acknowledge for: " + messageId + ", at: " + location);
            }
            synchronized (this) {
                this.inFlightTxLocations.add(location);
            }
            this.transactionStore.acknowledge(this, ack, location);
            context.getTransaction().addSynchronization(new Synchronization() {
                @Override
                public void afterCommit() throws Exception {
                    if (debug) {
                        JournalTopicMessageStore.LOG.debug("Transacted acknowledge commit for: " + messageId + ", at: " + location);
                    }
                    synchronized (JournalTopicMessageStore.this) {
                        JournalTopicMessageStore.this.inFlightTxLocations.remove(location);
                        JournalTopicMessageStore.this.acknowledge(messageId, location, key);
                    }
                }
                
                @Override
                public void afterRollback() throws Exception {
                    if (debug) {
                        JournalTopicMessageStore.LOG.debug("Transacted acknowledge rollback for: " + messageId + ", at: " + location);
                    }
                    synchronized (JournalTopicMessageStore.this) {
                        JournalTopicMessageStore.this.inFlightTxLocations.remove(location);
                    }
                }
            });
        }
    }
    
    public void replayAcknowledge(final ConnectionContext context, final String clientId, final String subscritionName, final MessageId messageId) {
        try {
            final SubscriptionInfo sub = this.longTermStore.lookupSubscription(clientId, subscritionName);
            if (sub != null) {
                this.longTermStore.acknowledge(context, clientId, subscritionName, messageId, null);
            }
        }
        catch (Throwable e) {
            JournalTopicMessageStore.LOG.debug("Could not replay acknowledge for message '" + messageId + "'.  Message may have already been acknowledged. reason: " + e);
        }
    }
    
    protected void acknowledge(final MessageId messageId, final RecordLocation location, final SubscriptionKey key) {
        synchronized (this) {
            this.lastLocation = location;
            this.ackedLastAckLocations.put(key, messageId);
        }
    }
    
    @Override
    public RecordLocation checkpoint() throws IOException {
        final HashMap<SubscriptionKey, MessageId> cpAckedLastAckLocations;
        synchronized (this) {
            cpAckedLastAckLocations = this.ackedLastAckLocations;
            this.ackedLastAckLocations = new HashMap<SubscriptionKey, MessageId>();
        }
        return super.checkpoint(new Callback() {
            @Override
            public void execute() throws Exception {
                for (final SubscriptionKey subscriptionKey : cpAckedLastAckLocations.keySet()) {
                    final MessageId identity = cpAckedLastAckLocations.get(subscriptionKey);
                    JournalTopicMessageStore.this.longTermStore.acknowledge(JournalTopicMessageStore.this.transactionTemplate.getContext(), subscriptionKey.clientId, subscriptionKey.subscriptionName, identity, null);
                }
            }
        });
    }
    
    public TopicMessageStore getLongTermTopicMessageStore() {
        return this.longTermStore;
    }
    
    @Override
    public void deleteSubscription(final String clientId, final String subscriptionName) throws IOException {
        this.longTermStore.deleteSubscription(clientId, subscriptionName);
    }
    
    @Override
    public SubscriptionInfo[] getAllSubscriptions() throws IOException {
        return this.longTermStore.getAllSubscriptions();
    }
    
    @Override
    public int getMessageCount(final String clientId, final String subscriberName) throws IOException {
        this.peristenceAdapter.checkpoint(true, true);
        return this.longTermStore.getMessageCount(clientId, subscriberName);
    }
    
    @Override
    public void resetBatching(final String clientId, final String subscriptionName) {
        this.longTermStore.resetBatching(clientId, subscriptionName);
    }
    
    static {
        LOG = LoggerFactory.getLogger(JournalTopicMessageStore.class);
    }
}
