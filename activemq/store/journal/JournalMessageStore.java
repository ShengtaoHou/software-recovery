// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.journal;

import org.slf4j.LoggerFactory;
import org.apache.activemq.usage.UsageListener;
import org.apache.activemq.store.MessageRecoveryListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import org.apache.activemq.util.Callback;
import org.apache.activemq.command.JournalQueueAck;
import java.io.IOException;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.MessageAck;
import java.util.List;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageId;
import java.util.Map;
import java.util.Set;
import org.apache.activeio.journal.RecordLocation;
import org.apache.activemq.util.TransactionTemplate;
import org.apache.activemq.store.MessageStore;
import org.slf4j.Logger;
import org.apache.activemq.store.AbstractMessageStore;

public class JournalMessageStore extends AbstractMessageStore
{
    private static final Logger LOG;
    protected final JournalPersistenceAdapter peristenceAdapter;
    protected final JournalTransactionStore transactionStore;
    protected final MessageStore longTermStore;
    protected final TransactionTemplate transactionTemplate;
    protected RecordLocation lastLocation;
    protected Set<RecordLocation> inFlightTxLocations;
    private Map<MessageId, Message> messages;
    private List<MessageAck> messageAcks;
    private Map<MessageId, Message> cpAddedMessageIds;
    private MemoryUsage memoryUsage;
    
    public JournalMessageStore(final JournalPersistenceAdapter adapter, final MessageStore checkpointStore, final ActiveMQDestination destination) {
        super(destination);
        this.inFlightTxLocations = new HashSet<RecordLocation>();
        this.messages = new LinkedHashMap<MessageId, Message>();
        this.messageAcks = new ArrayList<MessageAck>();
        this.peristenceAdapter = adapter;
        this.transactionStore = adapter.getTransactionStore();
        this.longTermStore = checkpointStore;
        this.transactionTemplate = new TransactionTemplate(adapter, new ConnectionContext(new NonCachedMessageEvaluationContext()));
    }
    
    @Override
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
        this.memoryUsage = memoryUsage;
        this.longTermStore.setMemoryUsage(memoryUsage);
    }
    
    @Override
    public void addMessage(final ConnectionContext context, final Message message) throws IOException {
        final MessageId id = message.getMessageId();
        final boolean debug = JournalMessageStore.LOG.isDebugEnabled();
        message.incrementReferenceCount();
        final RecordLocation location = this.peristenceAdapter.writeCommand(message, message.isResponseRequired());
        if (!context.isInTransaction()) {
            if (debug) {
                JournalMessageStore.LOG.debug("Journalled message add for: " + id + ", at: " + location);
            }
            this.addMessage(message, location);
        }
        else {
            if (debug) {
                JournalMessageStore.LOG.debug("Journalled transacted message add for: " + id + ", at: " + location);
            }
            synchronized (this) {
                this.inFlightTxLocations.add(location);
            }
            this.transactionStore.addMessage(this, message, location);
            context.getTransaction().addSynchronization(new Synchronization() {
                @Override
                public void afterCommit() throws Exception {
                    if (debug) {
                        JournalMessageStore.LOG.debug("Transacted message add commit for: " + id + ", at: " + location);
                    }
                    synchronized (JournalMessageStore.this) {
                        JournalMessageStore.this.inFlightTxLocations.remove(location);
                        JournalMessageStore.this.addMessage(message, location);
                    }
                }
                
                @Override
                public void afterRollback() throws Exception {
                    if (debug) {
                        JournalMessageStore.LOG.debug("Transacted message add rollback for: " + id + ", at: " + location);
                    }
                    synchronized (JournalMessageStore.this) {
                        JournalMessageStore.this.inFlightTxLocations.remove(location);
                    }
                    message.decrementReferenceCount();
                }
            });
        }
    }
    
    void addMessage(final Message message, final RecordLocation location) {
        synchronized (this) {
            this.lastLocation = location;
            final MessageId id = message.getMessageId();
            this.messages.put(id, message);
        }
    }
    
    public void replayAddMessage(final ConnectionContext context, final Message message) {
        try {
            final Message t = this.longTermStore.getMessage(message.getMessageId());
            if (t == null) {
                this.longTermStore.addMessage(context, message);
            }
        }
        catch (Throwable e) {
            JournalMessageStore.LOG.warn("Could not replay add for message '" + message.getMessageId() + "'.  Message may have already been added. reason: " + e);
        }
    }
    
    @Override
    public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
        final boolean debug = JournalMessageStore.LOG.isDebugEnabled();
        final JournalQueueAck remove = new JournalQueueAck();
        remove.setDestination(this.destination);
        remove.setMessageAck(ack);
        final RecordLocation location = this.peristenceAdapter.writeCommand(remove, ack.isResponseRequired());
        if (!context.isInTransaction()) {
            if (debug) {
                JournalMessageStore.LOG.debug("Journalled message remove for: " + ack.getLastMessageId() + ", at: " + location);
            }
            this.removeMessage(ack, location);
        }
        else {
            if (debug) {
                JournalMessageStore.LOG.debug("Journalled transacted message remove for: " + ack.getLastMessageId() + ", at: " + location);
            }
            synchronized (this) {
                this.inFlightTxLocations.add(location);
            }
            this.transactionStore.removeMessage(this, ack, location);
            context.getTransaction().addSynchronization(new Synchronization() {
                @Override
                public void afterCommit() throws Exception {
                    if (debug) {
                        JournalMessageStore.LOG.debug("Transacted message remove commit for: " + ack.getLastMessageId() + ", at: " + location);
                    }
                    synchronized (JournalMessageStore.this) {
                        JournalMessageStore.this.inFlightTxLocations.remove(location);
                        JournalMessageStore.this.removeMessage(ack, location);
                    }
                }
                
                @Override
                public void afterRollback() throws Exception {
                    if (debug) {
                        JournalMessageStore.LOG.debug("Transacted message remove rollback for: " + ack.getLastMessageId() + ", at: " + location);
                    }
                    synchronized (JournalMessageStore.this) {
                        JournalMessageStore.this.inFlightTxLocations.remove(location);
                    }
                }
            });
        }
    }
    
    final void removeMessage(final MessageAck ack, final RecordLocation location) {
        synchronized (this) {
            this.lastLocation = location;
            final MessageId id = ack.getLastMessageId();
            final Message message = this.messages.remove(id);
            if (message == null) {
                this.messageAcks.add(ack);
            }
            else {
                message.decrementReferenceCount();
            }
        }
    }
    
    public void replayRemoveMessage(final ConnectionContext context, final MessageAck messageAck) {
        try {
            final Message t = this.longTermStore.getMessage(messageAck.getLastMessageId());
            if (t != null) {
                this.longTermStore.removeMessage(context, messageAck);
            }
        }
        catch (Throwable e) {
            JournalMessageStore.LOG.warn("Could not replay acknowledge for message '" + messageAck.getLastMessageId() + "'.  Message may have already been acknowledged. reason: " + e);
        }
    }
    
    public RecordLocation checkpoint() throws IOException {
        return this.checkpoint(null);
    }
    
    public RecordLocation checkpoint(final Callback postCheckpointTest) throws IOException {
        final int maxCheckpointMessageAddSize = this.peristenceAdapter.getMaxCheckpointMessageAddSize();
        final List<MessageAck> cpRemovedMessageLocations;
        final List<RecordLocation> cpActiveJournalLocations;
        synchronized (this) {
            this.cpAddedMessageIds = this.messages;
            cpRemovedMessageLocations = this.messageAcks;
            cpActiveJournalLocations = new ArrayList<RecordLocation>(this.inFlightTxLocations);
            this.messages = new LinkedHashMap<MessageId, Message>();
            this.messageAcks = new ArrayList<MessageAck>();
        }
        this.transactionTemplate.run(new Callback() {
            @Override
            public void execute() throws Exception {
                int size = 0;
                final PersistenceAdapter persitanceAdapter = JournalMessageStore.this.transactionTemplate.getPersistenceAdapter();
                final ConnectionContext context = JournalMessageStore.this.transactionTemplate.getContext();
                synchronized (JournalMessageStore.this) {
                    for (final Message message : JournalMessageStore.this.cpAddedMessageIds.values()) {
                        try {
                            JournalMessageStore.this.longTermStore.addMessage(context, message);
                        }
                        catch (Throwable e) {
                            JournalMessageStore.LOG.warn("Message could not be added to long term store: " + e.getMessage(), e);
                        }
                        size += message.getSize();
                        message.decrementReferenceCount();
                        if (size >= maxCheckpointMessageAddSize) {
                            persitanceAdapter.commitTransaction(context);
                            persitanceAdapter.beginTransaction(context);
                            size = 0;
                        }
                    }
                }
                persitanceAdapter.commitTransaction(context);
                persitanceAdapter.beginTransaction(context);
                final Iterator<MessageAck> iterator2 = cpRemovedMessageLocations.iterator();
                while (iterator2.hasNext()) {
                    try {
                        final MessageAck ack = iterator2.next();
                        JournalMessageStore.this.longTermStore.removeMessage(JournalMessageStore.this.transactionTemplate.getContext(), ack);
                    }
                    catch (Throwable e2) {
                        JournalMessageStore.LOG.debug("Message could not be removed from long term store: " + e2.getMessage(), e2);
                    }
                }
                if (postCheckpointTest != null) {
                    postCheckpointTest.execute();
                }
            }
        });
        synchronized (this) {
            this.cpAddedMessageIds = null;
        }
        if (cpActiveJournalLocations.size() > 0) {
            Collections.sort(cpActiveJournalLocations);
            return cpActiveJournalLocations.get(0);
        }
        synchronized (this) {
            return this.lastLocation;
        }
    }
    
    @Override
    public Message getMessage(final MessageId identity) throws IOException {
        Message answer = null;
        synchronized (this) {
            answer = this.messages.get(identity);
            if (answer == null && this.cpAddedMessageIds != null) {
                answer = this.cpAddedMessageIds.get(identity);
            }
        }
        if (answer != null) {
            return answer;
        }
        return this.longTermStore.getMessage(identity);
    }
    
    @Override
    public void recover(final MessageRecoveryListener listener) throws Exception {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.recover(listener);
    }
    
    @Override
    public void start() throws Exception {
        if (this.memoryUsage != null) {
            this.memoryUsage.addUsageListener(this.peristenceAdapter);
        }
        this.longTermStore.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.longTermStore.stop();
        if (this.memoryUsage != null) {
            this.memoryUsage.removeUsageListener(this.peristenceAdapter);
        }
    }
    
    public MessageStore getLongTermMessageStore() {
        return this.longTermStore;
    }
    
    @Override
    public void removeAllMessages(final ConnectionContext context) throws IOException {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.removeAllMessages(context);
    }
    
    public void addMessageReference(final ConnectionContext context, final MessageId messageId, final long expirationTime, final String messageRef) throws IOException {
        throw new IOException("The journal does not support message references.");
    }
    
    public String getMessageReference(final MessageId identity) throws IOException {
        throw new IOException("The journal does not support message references.");
    }
    
    @Override
    public int getMessageCount() throws IOException {
        this.peristenceAdapter.checkpoint(true, true);
        return this.longTermStore.getMessageCount();
    }
    
    @Override
    public void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.recoverNextMessages(maxReturned, listener);
    }
    
    @Override
    public void resetBatching() {
        this.longTermStore.resetBatching();
    }
    
    @Override
    public void setBatch(final MessageId messageId) throws Exception {
        this.peristenceAdapter.checkpoint(true, true);
        this.longTermStore.setBatch(messageId);
    }
    
    static {
        LOG = LoggerFactory.getLogger(JournalMessageStore.class);
    }
}
