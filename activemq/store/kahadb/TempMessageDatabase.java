// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.DataOutput;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import org.apache.activemq.command.SubscriptionInfo;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.TreeMap;
import org.apache.activemq.store.kahadb.disk.util.LongMarshaller;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.data.KahaDestination;
import java.util.Iterator;
import org.apache.activemq.store.kahadb.data.KahaSubscriptionCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveDestinationCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.store.kahadb.disk.util.StringMarshaller;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.util.ArrayList;
import org.apache.activemq.command.TransactionId;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.File;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.apache.activemq.store.kahadb.disk.index.BTreeIndex;
import org.slf4j.Logger;

public class TempMessageDatabase
{
    private static final Logger LOG;
    public static final int CLOSED_STATE = 1;
    public static final int OPEN_STATE = 2;
    protected BTreeIndex<String, StoredDestination> destinations;
    protected PageFile pageFile;
    protected File directory;
    boolean enableIndexWriteAsync;
    int setIndexWriteBatchSize;
    protected AtomicBoolean started;
    protected AtomicBoolean opened;
    protected final Object indexMutex;
    private final HashSet<Integer> journalFilesBeingReplicated;
    private final HashMap<String, StoredDestination> storedDestinations;
    protected final LinkedHashMap<TransactionId, ArrayList<Operation>> inflightTransactions;
    protected final LinkedHashMap<TransactionId, ArrayList<Operation>> preparedTransactions;
    
    public TempMessageDatabase() {
        this.enableIndexWriteAsync = true;
        this.setIndexWriteBatchSize = PageFile.DEFAULT_WRITE_BATCH_SIZE;
        this.started = new AtomicBoolean();
        this.opened = new AtomicBoolean();
        this.indexMutex = new Object();
        this.journalFilesBeingReplicated = new HashSet<Integer>();
        this.storedDestinations = new HashMap<String, StoredDestination>();
        this.inflightTransactions = new LinkedHashMap<TransactionId, ArrayList<Operation>>();
        this.preparedTransactions = new LinkedHashMap<TransactionId, ArrayList<Operation>>();
    }
    
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.load();
        }
    }
    
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            this.unload();
        }
    }
    
    private void loadPageFile() throws IOException {
        synchronized (this.indexMutex) {
            final PageFile pageFile = this.getPageFile();
            pageFile.load();
            pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    (TempMessageDatabase.this.destinations = new BTreeIndex<String, StoredDestination>(pageFile, tx.allocate().getPageId())).setKeyMarshaller(StringMarshaller.INSTANCE);
                    TempMessageDatabase.this.destinations.setValueMarshaller(new StoredDestinationMarshaller());
                    TempMessageDatabase.this.destinations.load(tx);
                }
            });
            pageFile.flush();
            this.storedDestinations.clear();
        }
    }
    
    public void open() throws IOException {
        if (this.opened.compareAndSet(false, true)) {
            this.loadPageFile();
        }
    }
    
    public void load() throws IOException {
        synchronized (this.indexMutex) {
            this.open();
            this.pageFile.unload();
            this.pageFile.delete();
            this.loadPageFile();
        }
    }
    
    public void close() throws IOException, InterruptedException {
        if (this.opened.compareAndSet(true, false)) {
            synchronized (this.indexMutex) {
                this.pageFile.unload();
            }
        }
    }
    
    public void unload() throws IOException, InterruptedException {
        synchronized (this.indexMutex) {
            if (this.pageFile.isLoaded()) {
                this.close();
            }
        }
    }
    
    public void processAdd(final KahaAddMessageCommand command, final TransactionId txid, final ByteSequence data) throws IOException {
        if (txid != null) {
            synchronized (this.indexMutex) {
                final ArrayList<Operation> inflightTx = this.getInflightTx(txid);
                inflightTx.add(new AddOpperation(command, data));
            }
        }
        else {
            synchronized (this.indexMutex) {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        TempMessageDatabase.this.upadateIndex(tx, command, data);
                    }
                });
            }
        }
    }
    
    public void processRemove(final KahaRemoveMessageCommand command, final TransactionId txid) throws IOException {
        if (txid != null) {
            synchronized (this.indexMutex) {
                final ArrayList<Operation> inflightTx = this.getInflightTx(txid);
                inflightTx.add(new RemoveOpperation(command));
            }
        }
        else {
            synchronized (this.indexMutex) {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        TempMessageDatabase.this.updateIndex(tx, command);
                    }
                });
            }
        }
    }
    
    public void process(final KahaRemoveDestinationCommand command) throws IOException {
        synchronized (this.indexMutex) {
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    TempMessageDatabase.this.updateIndex(tx, command);
                }
            });
        }
    }
    
    public void process(final KahaSubscriptionCommand command) throws IOException {
        synchronized (this.indexMutex) {
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    TempMessageDatabase.this.updateIndex(tx, command);
                }
            });
        }
    }
    
    public void processCommit(final TransactionId key) throws IOException {
        synchronized (this.indexMutex) {
            ArrayList<Operation> inflightTx = this.inflightTransactions.remove(key);
            if (inflightTx == null) {
                inflightTx = this.preparedTransactions.remove(key);
            }
            if (inflightTx == null) {
                return;
            }
            final ArrayList<Operation> messagingTx = inflightTx;
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    for (final Operation op : messagingTx) {
                        op.execute(tx);
                    }
                }
            });
        }
    }
    
    public void processPrepare(final TransactionId key) {
        synchronized (this.indexMutex) {
            final ArrayList<Operation> tx = this.inflightTransactions.remove(key);
            if (tx != null) {
                this.preparedTransactions.put(key, tx);
            }
        }
    }
    
    public void processRollback(final TransactionId key) {
        synchronized (this.indexMutex) {
            final ArrayList<Operation> tx = this.inflightTransactions.remove(key);
            if (tx == null) {
                this.preparedTransactions.remove(key);
            }
        }
    }
    
    private void upadateIndex(final Transaction tx, final KahaAddMessageCommand command, final ByteSequence data) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        if (sd.subscriptions != null && sd.ackPositions.isEmpty()) {
            return;
        }
        final long id = sd.nextMessageId++;
        final Long previous = sd.messageIdIndex.put(tx, command.getMessageId(), id);
        if (previous == null) {
            sd.orderIndex.put(tx, id, new MessageRecord(command.getMessageId(), data));
        }
        else {
            sd.messageIdIndex.put(tx, command.getMessageId(), previous);
        }
    }
    
    private void updateIndex(final Transaction tx, final KahaRemoveMessageCommand command) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        if (!command.hasSubscriptionKey()) {
            final Long sequenceId = sd.messageIdIndex.remove(tx, command.getMessageId());
            if (sequenceId != null) {
                sd.orderIndex.remove(tx, sequenceId);
            }
        }
        else {
            final Long sequence = sd.messageIdIndex.get(tx, command.getMessageId());
            if (sequence != null) {
                final String subscriptionKey = command.getSubscriptionKey();
                final Long prev = sd.subscriptionAcks.put(tx, subscriptionKey, sequence);
                this.removeAckByteSequence(tx, sd, subscriptionKey, prev);
                this.addAckByteSequence(sd, sequence, subscriptionKey);
            }
        }
    }
    
    private void updateIndex(final Transaction tx, final KahaRemoveDestinationCommand command) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        sd.orderIndex.clear(tx);
        sd.orderIndex.unload(tx);
        tx.free(sd.orderIndex.getPageId());
        sd.messageIdIndex.clear(tx);
        sd.messageIdIndex.unload(tx);
        tx.free(sd.messageIdIndex.getPageId());
        if (sd.subscriptions != null) {
            sd.subscriptions.clear(tx);
            sd.subscriptions.unload(tx);
            tx.free(sd.subscriptions.getPageId());
            sd.subscriptionAcks.clear(tx);
            sd.subscriptionAcks.unload(tx);
            tx.free(sd.subscriptionAcks.getPageId());
        }
        final String key = this.key(command.getDestination());
        this.storedDestinations.remove(key);
        this.destinations.remove(tx, key);
    }
    
    private void updateIndex(final Transaction tx, final KahaSubscriptionCommand command) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        if (command.hasSubscriptionInfo()) {
            final String subscriptionKey = command.getSubscriptionKey();
            sd.subscriptions.put(tx, subscriptionKey, command);
            long ackByteSequence = -1L;
            if (!command.getRetroactive()) {
                ackByteSequence = sd.nextMessageId - 1L;
            }
            sd.subscriptionAcks.put(tx, subscriptionKey, ackByteSequence);
            this.addAckByteSequence(sd, ackByteSequence, subscriptionKey);
        }
        else {
            final String subscriptionKey = command.getSubscriptionKey();
            sd.subscriptions.remove(tx, subscriptionKey);
            final Long prev = sd.subscriptionAcks.remove(tx, subscriptionKey);
            if (prev != null) {
                this.removeAckByteSequence(tx, sd, subscriptionKey, prev);
            }
        }
    }
    
    public HashSet<Integer> getJournalFilesBeingReplicated() {
        return this.journalFilesBeingReplicated;
    }
    
    protected StoredDestination getStoredDestination(final KahaDestination destination, final Transaction tx) throws IOException {
        final String key = this.key(destination);
        StoredDestination rc = this.storedDestinations.get(key);
        if (rc == null) {
            final boolean topic = destination.getType() == KahaDestination.DestinationType.TOPIC || destination.getType() == KahaDestination.DestinationType.TEMP_TOPIC;
            rc = this.loadStoredDestination(tx, key, topic);
            this.storedDestinations.put(key, rc);
        }
        return rc;
    }
    
    private StoredDestination loadStoredDestination(final Transaction tx, final String key, final boolean topic) throws IOException {
        StoredDestination rc = this.destinations.get(tx, key);
        if (rc == null) {
            rc = new StoredDestination();
            rc.orderIndex = new BTreeIndex<Long, MessageRecord>(this.pageFile, tx.allocate());
            rc.messageIdIndex = new BTreeIndex<String, Long>(this.pageFile, tx.allocate());
            if (topic) {
                rc.subscriptions = new BTreeIndex<String, KahaSubscriptionCommand>(this.pageFile, tx.allocate());
                rc.subscriptionAcks = new BTreeIndex<String, Long>(this.pageFile, tx.allocate());
            }
            this.destinations.put(tx, key, rc);
        }
        rc.orderIndex.setKeyMarshaller(LongMarshaller.INSTANCE);
        rc.orderIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
        rc.orderIndex.load(tx);
        final Map.Entry<Long, MessageRecord> lastEntry = rc.orderIndex.getLast(tx);
        if (lastEntry != null) {
            rc.nextMessageId = lastEntry.getKey() + 1L;
        }
        rc.messageIdIndex.setKeyMarshaller(StringMarshaller.INSTANCE);
        rc.messageIdIndex.setValueMarshaller(LongMarshaller.INSTANCE);
        rc.messageIdIndex.load(tx);
        if (topic) {
            rc.subscriptions.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.subscriptions.setValueMarshaller(KahaSubscriptionCommandMarshaller.INSTANCE);
            rc.subscriptions.load(tx);
            rc.subscriptionAcks.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.subscriptionAcks.setValueMarshaller(LongMarshaller.INSTANCE);
            rc.subscriptionAcks.load(tx);
            rc.ackPositions = new TreeMap<Long, HashSet<String>>();
            rc.subscriptionCursors = new HashMap<String, Long>();
            final Iterator<Map.Entry<String, Long>> iterator = rc.subscriptionAcks.iterator(tx);
            while (iterator.hasNext()) {
                final Map.Entry<String, Long> entry = iterator.next();
                this.addAckByteSequence(rc, entry.getValue(), entry.getKey());
            }
        }
        return rc;
    }
    
    private void addAckByteSequence(final StoredDestination sd, final Long messageSequence, final String subscriptionKey) {
        HashSet<String> hs = sd.ackPositions.get(messageSequence);
        if (hs == null) {
            hs = new HashSet<String>();
            sd.ackPositions.put(messageSequence, hs);
        }
        hs.add(subscriptionKey);
    }
    
    private void removeAckByteSequence(final Transaction tx, final StoredDestination sd, final String subscriptionKey, final Long sequenceId) throws IOException {
        if (sequenceId != null) {
            final HashSet<String> hs = sd.ackPositions.get(sequenceId);
            if (hs != null) {
                hs.remove(subscriptionKey);
                if (hs.isEmpty()) {
                    final HashSet<String> firstSet = sd.ackPositions.values().iterator().next();
                    sd.ackPositions.remove(sequenceId);
                    if (hs == firstSet) {
                        final ArrayList<Map.Entry<Long, MessageRecord>> deletes = new ArrayList<Map.Entry<Long, MessageRecord>>();
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<Long, MessageRecord> entry = iterator.next();
                            if (entry.getKey().compareTo(sequenceId) <= 0) {
                                deletes.add(entry);
                            }
                        }
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator2 = deletes.iterator();
                        while (iterator2.hasNext()) {
                            final Map.Entry<Long, MessageRecord> entry = iterator2.next();
                            sd.messageIdIndex.remove(tx, entry.getValue().messageId);
                            sd.orderIndex.remove(tx, entry.getKey());
                        }
                    }
                }
            }
        }
    }
    
    private String key(final KahaDestination destination) {
        return destination.getType().getNumber() + ":" + destination.getName();
    }
    
    private ArrayList<Operation> getInflightTx(final TransactionId key) {
        ArrayList<Operation> tx = this.inflightTransactions.get(key);
        if (tx == null) {
            tx = new ArrayList<Operation>();
            this.inflightTransactions.put(key, tx);
        }
        return tx;
    }
    
    private PageFile createPageFile() {
        final PageFile index = new PageFile(this.directory, "temp-db");
        index.setEnableWriteThread(this.isEnableIndexWriteAsync());
        index.setWriteBatchSize(this.getIndexWriteBatchSize());
        index.setEnableDiskSyncs(false);
        index.setEnableRecoveryFile(false);
        return index;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public void setIndexWriteBatchSize(final int setIndexWriteBatchSize) {
        this.setIndexWriteBatchSize = setIndexWriteBatchSize;
    }
    
    public int getIndexWriteBatchSize() {
        return this.setIndexWriteBatchSize;
    }
    
    public void setEnableIndexWriteAsync(final boolean enableIndexWriteAsync) {
        this.enableIndexWriteAsync = enableIndexWriteAsync;
    }
    
    boolean isEnableIndexWriteAsync() {
        return this.enableIndexWriteAsync;
    }
    
    public PageFile getPageFile() {
        if (this.pageFile == null) {
            this.pageFile = this.createPageFile();
        }
        return this.pageFile;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TempMessageDatabase.class);
    }
    
    class StoredSubscription
    {
        SubscriptionInfo subscriptionInfo;
        String lastAckId;
        ByteSequence lastAckByteSequence;
        ByteSequence cursor;
    }
    
    static class MessageRecord
    {
        final String messageId;
        final ByteSequence data;
        
        public MessageRecord(final String messageId, final ByteSequence location) {
            this.messageId = messageId;
            this.data = location;
        }
        
        @Override
        public String toString() {
            return "[" + this.messageId + "," + this.data + "]";
        }
    }
    
    protected static class MessageKeysMarshaller extends VariableMarshaller<MessageRecord>
    {
        static final MessageKeysMarshaller INSTANCE;
        
        @Override
        public MessageRecord readPayload(final DataInput dataIn) throws IOException {
            return new MessageRecord(dataIn.readUTF(), ByteSequenceMarshaller.INSTANCE.readPayload(dataIn));
        }
        
        @Override
        public void writePayload(final MessageRecord object, final DataOutput dataOut) throws IOException {
            dataOut.writeUTF(object.messageId);
            ByteSequenceMarshaller.INSTANCE.writePayload(object.data, dataOut);
        }
        
        static {
            INSTANCE = new MessageKeysMarshaller();
        }
    }
    
    static class StoredDestination
    {
        long nextMessageId;
        BTreeIndex<Long, MessageRecord> orderIndex;
        BTreeIndex<String, Long> messageIdIndex;
        BTreeIndex<String, KahaSubscriptionCommand> subscriptions;
        BTreeIndex<String, Long> subscriptionAcks;
        HashMap<String, Long> subscriptionCursors;
        TreeMap<Long, HashSet<String>> ackPositions;
    }
    
    protected class StoredDestinationMarshaller extends VariableMarshaller<StoredDestination>
    {
        public Class<StoredDestination> getType() {
            return StoredDestination.class;
        }
        
        @Override
        public StoredDestination readPayload(final DataInput dataIn) throws IOException {
            final StoredDestination value = new StoredDestination();
            value.orderIndex = new BTreeIndex<Long, MessageRecord>(TempMessageDatabase.this.pageFile, dataIn.readLong());
            value.messageIdIndex = new BTreeIndex<String, Long>(TempMessageDatabase.this.pageFile, dataIn.readLong());
            if (dataIn.readBoolean()) {
                value.subscriptions = new BTreeIndex<String, KahaSubscriptionCommand>(TempMessageDatabase.this.pageFile, dataIn.readLong());
                value.subscriptionAcks = new BTreeIndex<String, Long>(TempMessageDatabase.this.pageFile, dataIn.readLong());
            }
            return value;
        }
        
        @Override
        public void writePayload(final StoredDestination value, final DataOutput dataOut) throws IOException {
            dataOut.writeLong(value.orderIndex.getPageId());
            dataOut.writeLong(value.messageIdIndex.getPageId());
            if (value.subscriptions != null) {
                dataOut.writeBoolean(true);
                dataOut.writeLong(value.subscriptions.getPageId());
                dataOut.writeLong(value.subscriptionAcks.getPageId());
            }
            else {
                dataOut.writeBoolean(false);
            }
        }
    }
    
    static class ByteSequenceMarshaller extends VariableMarshaller<ByteSequence>
    {
        static final ByteSequenceMarshaller INSTANCE;
        
        @Override
        public ByteSequence readPayload(final DataInput dataIn) throws IOException {
            final byte[] data = new byte[dataIn.readInt()];
            dataIn.readFully(data);
            return new ByteSequence(data);
        }
        
        @Override
        public void writePayload(final ByteSequence object, final DataOutput dataOut) throws IOException {
            dataOut.writeInt(object.getLength());
            dataOut.write(object.getData(), object.getOffset(), object.getLength());
        }
        
        static {
            INSTANCE = new ByteSequenceMarshaller();
        }
    }
    
    static class KahaSubscriptionCommandMarshaller extends VariableMarshaller<KahaSubscriptionCommand>
    {
        static final KahaSubscriptionCommandMarshaller INSTANCE;
        
        @Override
        public KahaSubscriptionCommand readPayload(final DataInput dataIn) throws IOException {
            final KahaSubscriptionCommand rc = new KahaSubscriptionCommand();
            rc.mergeFramed((InputStream)dataIn);
            return rc;
        }
        
        @Override
        public void writePayload(final KahaSubscriptionCommand object, final DataOutput dataOut) throws IOException {
            object.writeFramed((OutputStream)dataOut);
        }
        
        static {
            INSTANCE = new KahaSubscriptionCommandMarshaller();
        }
    }
    
    abstract class Operation
    {
        public abstract void execute(final Transaction p0) throws IOException;
    }
    
    class AddOpperation extends Operation
    {
        final KahaAddMessageCommand command;
        private final ByteSequence data;
        
        public AddOpperation(final KahaAddMessageCommand command, final ByteSequence location) {
            this.command = command;
            this.data = location;
        }
        
        @Override
        public void execute(final Transaction tx) throws IOException {
            TempMessageDatabase.this.upadateIndex(tx, this.command, this.data);
        }
        
        public KahaAddMessageCommand getCommand() {
            return this.command;
        }
    }
    
    class RemoveOpperation extends Operation
    {
        final KahaRemoveMessageCommand command;
        
        public RemoveOpperation(final KahaRemoveMessageCommand command) {
            this.command = command;
        }
        
        @Override
        public void execute(final Transaction tx) throws IOException {
            TempMessageDatabase.this.updateIndex(tx, this.command);
        }
        
        public KahaRemoveMessageCommand getCommand() {
            return this.command;
        }
    }
}
