// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.slf4j.LoggerFactory;
import org.apache.activemq.store.AbstractMessageStore;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.TransactionRecoveryListener;
import java.io.InputStream;
import org.apache.activemq.store.kahadb.data.KahaEntryType;
import org.apache.activemq.util.DataByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Date;
import org.apache.activemq.store.kahadb.data.KahaTraceCommand;
import java.io.OutputStream;
import org.apache.activemq.util.DataByteArrayOutputStream;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.store.kahadb.data.KahaCommitCommand;
import org.apache.activemq.store.kahadb.data.KahaPrepareCommand;
import java.util.Iterator;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.ProxyTopicMessageStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.store.ListenableFuture;
import java.io.IOException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.store.MessageStore;
import java.util.HashSet;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import java.util.Set;
import org.apache.activemq.command.TransactionId;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.store.TransactionStore;

public class MultiKahaDBTransactionStore implements TransactionStore
{
    static final Logger LOG;
    final MultiKahaDBPersistenceAdapter multiKahaDBPersistenceAdapter;
    final ConcurrentHashMap<TransactionId, Tx> inflightTransactions;
    final Set<TransactionId> recoveredPendingCommit;
    private Journal journal;
    private int journalMaxFileLength;
    private int journalWriteBatchSize;
    
    public MultiKahaDBTransactionStore(final MultiKahaDBPersistenceAdapter multiKahaDBPersistenceAdapter) {
        this.inflightTransactions = new ConcurrentHashMap<TransactionId, Tx>();
        this.recoveredPendingCommit = new HashSet<TransactionId>();
        this.journalMaxFileLength = 33554432;
        this.journalWriteBatchSize = 4194304;
        this.multiKahaDBPersistenceAdapter = multiKahaDBPersistenceAdapter;
    }
    
    public MessageStore proxy(final TransactionStore transactionStore, final MessageStore messageStore) {
        return new ProxyMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                MultiKahaDBTransactionStore.this.addMessage(transactionStore, context, this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimizeHint) throws IOException {
                MultiKahaDBTransactionStore.this.addMessage(transactionStore, context, this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
                return MultiKahaDBTransactionStore.this.asyncAddQueueMessage(transactionStore, context, this.getDelegate(), message);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
                return MultiKahaDBTransactionStore.this.asyncAddQueueMessage(transactionStore, context, this.getDelegate(), message);
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MultiKahaDBTransactionStore.this.removeMessage(transactionStore, context, this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MultiKahaDBTransactionStore.this.removeAsyncMessage(transactionStore, context, this.getDelegate(), ack);
            }
        };
    }
    
    public TopicMessageStore proxy(final TransactionStore transactionStore, final TopicMessageStore messageStore) {
        return new ProxyTopicMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimizeHint) throws IOException {
                MultiKahaDBTransactionStore.this.addMessage(transactionStore, context, this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                MultiKahaDBTransactionStore.this.addMessage(transactionStore, context, this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message, final boolean canOptimizeHint) throws IOException {
                return MultiKahaDBTransactionStore.this.asyncAddTopicMessage(transactionStore, context, this.getDelegate(), message);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
                return MultiKahaDBTransactionStore.this.asyncAddTopicMessage(transactionStore, context, this.getDelegate(), message);
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MultiKahaDBTransactionStore.this.removeMessage(transactionStore, context, this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MultiKahaDBTransactionStore.this.removeAsyncMessage(transactionStore, context, this.getDelegate(), ack);
            }
            
            @Override
            public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
                MultiKahaDBTransactionStore.this.acknowledge(transactionStore, context, (TopicMessageStore)this.getDelegate(), clientId, subscriptionName, messageId, ack);
            }
        };
    }
    
    public void deleteAllMessages() {
        IOHelper.deleteChildren(this.getDirectory());
    }
    
    public int getJournalMaxFileLength() {
        return this.journalMaxFileLength;
    }
    
    public void setJournalMaxFileLength(final int journalMaxFileLength) {
        this.journalMaxFileLength = journalMaxFileLength;
    }
    
    public int getJournalMaxWriteBatchSize() {
        return this.journalWriteBatchSize;
    }
    
    public void setJournalMaxWriteBatchSize(final int journalWriteBatchSize) {
        this.journalWriteBatchSize = journalWriteBatchSize;
    }
    
    public Tx getTx(final TransactionId txid) {
        Tx tx = this.inflightTransactions.get(txid);
        if (tx == null) {
            tx = new Tx();
            this.inflightTransactions.put(txid, tx);
        }
        return tx;
    }
    
    public Tx removeTx(final TransactionId txid) {
        return this.inflightTransactions.remove(txid);
    }
    
    @Override
    public void prepare(final TransactionId txid) throws IOException {
        final Tx tx = this.getTx(txid);
        for (final TransactionStore store : tx.getStores()) {
            store.prepare(txid);
        }
    }
    
    @Override
    public void commit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) throws IOException {
        if (preCommit != null) {
            preCommit.run();
        }
        final Tx tx = this.getTx(txid);
        if (wasPrepared) {
            for (final TransactionStore store : tx.getStores()) {
                store.commit(txid, true, null, null);
            }
        }
        else if (tx.getStores().size() == 1) {
            for (final TransactionStore store : tx.getStores()) {
                store.commit(txid, false, null, null);
            }
        }
        else {
            for (final TransactionStore store : tx.getStores()) {
                store.prepare(txid);
            }
            this.persistOutcome(tx, txid);
            for (final TransactionStore store : tx.getStores()) {
                store.commit(txid, true, null, null);
            }
            this.persistCompletion(txid);
        }
        this.removeTx(txid);
        if (postCommit != null) {
            postCommit.run();
        }
    }
    
    public void persistOutcome(final Tx tx, final TransactionId txid) throws IOException {
        tx.trackPrepareLocation(this.store(((KahaPrepareCommandBase<JournalCommand<?>>)new KahaPrepareCommand()).setTransactionInfo(TransactionIdConversion.convert(this.multiKahaDBPersistenceAdapter.transactionIdTransformer.transform(txid)))));
    }
    
    public void persistCompletion(final TransactionId txid) throws IOException {
        this.store(((KahaCommitCommandBase<JournalCommand<?>>)new KahaCommitCommand()).setTransactionInfo(TransactionIdConversion.convert(this.multiKahaDBPersistenceAdapter.transactionIdTransformer.transform(txid))));
    }
    
    private Location store(final JournalCommand<?> data) throws IOException {
        final int size = data.serializedSizeFramed();
        final DataByteArrayOutputStream os = new DataByteArrayOutputStream(size + 1);
        os.writeByte(data.type().getNumber());
        data.writeFramed(os);
        final Location location = this.journal.write(os.toByteSequence(), true);
        this.journal.setLastAppendLocation(location);
        return location;
    }
    
    @Override
    public void rollback(final TransactionId txid) throws IOException {
        final Tx tx = this.removeTx(txid);
        if (tx != null) {
            for (final TransactionStore store : tx.getStores()) {
                store.rollback(txid);
            }
        }
    }
    
    @Override
    public void start() throws Exception {
        (this.journal = new Journal() {
            @Override
            protected void cleanup() {
                super.cleanup();
                MultiKahaDBTransactionStore.this.txStoreCleanup();
            }
        }).setDirectory(this.getDirectory());
        this.journal.setMaxFileLength(this.journalMaxFileLength);
        this.journal.setWriteBatchSize(this.journalWriteBatchSize);
        IOHelper.mkdirs(this.journal.getDirectory());
        this.journal.start();
        this.recoverPendingLocalTransactions();
        this.store(((KahaTraceCommandBase<JournalCommand<?>>)new KahaTraceCommand()).setMessage("LOADED " + new Date()));
    }
    
    private void txStoreCleanup() {
        final Set<Integer> knownDataFileIds = new TreeSet<Integer>(this.journal.getFileMap().keySet());
        for (final Tx tx : this.inflightTransactions.values()) {
            knownDataFileIds.remove(tx.getPreparedLocationId());
        }
        try {
            this.journal.removeDataFiles(knownDataFileIds);
        }
        catch (Exception e) {
            MultiKahaDBTransactionStore.LOG.error(this + ", Failed to remove tx journal datafiles " + knownDataFileIds);
        }
    }
    
    private File getDirectory() {
        return new File(this.multiKahaDBPersistenceAdapter.getDirectory(), "txStore");
    }
    
    @Override
    public void stop() throws Exception {
        this.journal.close();
        this.journal = null;
    }
    
    private void recoverPendingLocalTransactions() throws IOException {
        for (Location location = this.journal.getNextLocation(null); location != null; location = this.journal.getNextLocation(location)) {
            this.process(this.load(location));
        }
        this.recoveredPendingCommit.addAll((Collection<? extends TransactionId>)this.inflightTransactions.keySet());
        MultiKahaDBTransactionStore.LOG.info("pending local transactions: " + this.recoveredPendingCommit);
    }
    
    public JournalCommand<?> load(final Location location) throws IOException {
        final DataByteArrayInputStream is = new DataByteArrayInputStream(this.journal.read(location));
        final byte readByte = is.readByte();
        final KahaEntryType type = KahaEntryType.valueOf(readByte);
        if (type == null) {
            throw new IOException("Could not load journal record. Invalid location: " + location);
        }
        final JournalCommand<?> message = (JournalCommand<?>)type.createMessage();
        message.mergeFramed(is);
        return message;
    }
    
    public void process(final JournalCommand<?> command) throws IOException {
        switch (command.type()) {
            case KAHA_PREPARE_COMMAND: {
                final KahaPrepareCommand prepareCommand = (KahaPrepareCommand)command;
                this.getTx(TransactionIdConversion.convert(prepareCommand.getTransactionInfo()));
                break;
            }
            case KAHA_COMMIT_COMMAND: {
                final KahaCommitCommand commitCommand = (KahaCommitCommand)command;
                this.removeTx(TransactionIdConversion.convert(commitCommand.getTransactionInfo()));
                break;
            }
            case KAHA_TRACE_COMMAND: {
                break;
            }
            default: {
                throw new IOException("Unexpected command in transaction journal: " + command);
            }
        }
    }
    
    @Override
    public synchronized void recover(final TransactionRecoveryListener listener) throws IOException {
        for (final PersistenceAdapter adapter : this.multiKahaDBPersistenceAdapter.adapters) {
            adapter.createTransactionStore().recover(new TransactionRecoveryListener() {
                @Override
                public void recover(final XATransactionId xid, final Message[] addedMessages, final MessageAck[] acks) {
                    try {
                        MultiKahaDBTransactionStore.this.getTx(xid).trackStore(adapter.createTransactionStore());
                    }
                    catch (IOException e) {
                        MultiKahaDBTransactionStore.LOG.error("Failed to access transaction store: " + adapter + " for prepared xa tid: " + xid, e);
                    }
                    listener.recover(xid, addedMessages, acks);
                }
            });
        }
        try {
            final Broker broker = this.multiKahaDBPersistenceAdapter.getBrokerService().getBroker();
            for (final TransactionId txid : broker.getPreparedTransactions(null)) {
                if (this.multiKahaDBPersistenceAdapter.isLocalXid(txid)) {
                    try {
                        if (this.recoveredPendingCommit.contains(txid)) {
                            MultiKahaDBTransactionStore.LOG.info("delivering pending commit outcome for tid: " + txid);
                            broker.commitTransaction(null, txid, false);
                        }
                        else {
                            MultiKahaDBTransactionStore.LOG.info("delivering rollback outcome to store for tid: " + txid);
                            broker.forgetTransaction(null, txid);
                        }
                        this.persistCompletion(txid);
                    }
                    catch (Exception ex) {
                        MultiKahaDBTransactionStore.LOG.error("failed to deliver pending outcome for tid: " + txid, ex);
                    }
                }
            }
        }
        catch (Exception e) {
            MultiKahaDBTransactionStore.LOG.error("failed to resolve pending local transactions", e);
        }
    }
    
    void addMessage(final TransactionStore transactionStore, final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() != null) {
            this.getTx(message.getTransactionId()).trackStore(transactionStore);
        }
        destination.addMessage(context, message);
    }
    
    ListenableFuture<Object> asyncAddQueueMessage(final TransactionStore transactionStore, final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() != null) {
            this.getTx(message.getTransactionId()).trackStore(transactionStore);
            destination.addMessage(context, message);
            return AbstractMessageStore.FUTURE;
        }
        return destination.asyncAddQueueMessage(context, message);
    }
    
    ListenableFuture<Object> asyncAddTopicMessage(final TransactionStore transactionStore, final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() != null) {
            this.getTx(message.getTransactionId()).trackStore(transactionStore);
            destination.addMessage(context, message);
            return AbstractMessageStore.FUTURE;
        }
        return destination.asyncAddTopicMessage(context, message);
    }
    
    final void removeMessage(final TransactionStore transactionStore, final ConnectionContext context, final MessageStore destination, final MessageAck ack) throws IOException {
        if (ack.getTransactionId() != null) {
            this.getTx(ack.getTransactionId()).trackStore(transactionStore);
        }
        destination.removeMessage(context, ack);
    }
    
    final void removeAsyncMessage(final TransactionStore transactionStore, final ConnectionContext context, final MessageStore destination, final MessageAck ack) throws IOException {
        if (ack.getTransactionId() != null) {
            this.getTx(ack.getTransactionId()).trackStore(transactionStore);
        }
        destination.removeAsyncMessage(context, ack);
    }
    
    final void acknowledge(final TransactionStore transactionStore, final ConnectionContext context, final TopicMessageStore destination, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        if (ack.getTransactionId() != null) {
            this.getTx(ack.getTransactionId()).trackStore(transactionStore);
        }
        destination.acknowledge(context, clientId, subscriptionName, messageId, ack);
    }
    
    static {
        LOG = LoggerFactory.getLogger(MultiKahaDBTransactionStore.class);
    }
    
    public class Tx
    {
        private final Set<TransactionStore> stores;
        private int prepareLocationId;
        
        public Tx() {
            this.stores = new HashSet<TransactionStore>();
            this.prepareLocationId = 0;
        }
        
        public void trackStore(final TransactionStore store) {
            this.stores.add(store);
        }
        
        public Set<TransactionStore> getStores() {
            return this.stores;
        }
        
        public void trackPrepareLocation(final Location location) {
            this.prepareLocationId = location.getDataFileId();
        }
        
        public int getPreparedLocationId() {
            return this.prepareLocationId;
        }
    }
}
