// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import java.io.ByteArrayInputStream;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.DataInput;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.store.kahadb.disk.util.LongMarshaller;
import org.apache.activemq.store.kahadb.disk.util.LocationMarshaller;
import org.apache.activemq.store.kahadb.disk.index.ListIndex;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.activemq.store.kahadb.data.KahaDestination;
import org.apache.activemq.store.kahadb.data.KahaUpdateMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveDestinationCommand;
import org.apache.activemq.store.kahadb.data.KahaCommitCommand;
import org.apache.activemq.store.kahadb.data.KahaPrepareCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaSubscriptionCommand;
import org.apache.activemq.store.kahadb.data.KahaEntryType;
import org.apache.activemq.util.DataByteArrayInputStream;
import java.io.OutputStream;
import org.apache.activemq.util.DataByteArrayOutputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.store.kahadb.disk.util.Sequence;
import org.apache.activemq.store.kahadb.disk.journal.DataFile;
import java.util.Collection;
import org.apache.activemq.store.kahadb.disk.util.SequenceSet;
import org.apache.activemq.store.kahadb.disk.index.BTreeVisitor;
import org.apache.activemq.store.kahadb.data.KahaAckMessageFileMapCommand;
import org.apache.activemq.ActiveMQMessageAuditNoSync;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.activemq.store.kahadb.data.KahaProducerAuditCommand;
import org.apache.activemq.store.kahadb.data.KahaTransactionInfo;
import org.apache.activemq.store.kahadb.data.KahaRollbackCommand;
import java.util.ArrayList;
import java.util.Date;
import org.apache.activemq.store.kahadb.data.KahaTraceCommand;
import org.apache.activemq.util.IOHelper;
import java.util.Iterator;
import java.util.Map;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.util.StringMarshaller;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.store.kahadb.disk.index.BTreeIndex;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.util.ServiceStopper;
import java.util.Set;
import java.util.List;
import org.apache.activemq.command.TransactionId;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.slf4j.Logger;
import org.apache.activemq.protobuf.Buffer;
import java.io.File;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.util.ServiceSupport;

public abstract class MessageDatabase extends ServiceSupport implements BrokerServiceAware
{
    protected BrokerService brokerService;
    public static final String PROPERTY_LOG_SLOW_ACCESS_TIME = "org.apache.activemq.store.kahadb.LOG_SLOW_ACCESS_TIME";
    public static final int LOG_SLOW_ACCESS_TIME;
    public static final File DEFAULT_DIRECTORY;
    protected static final Buffer UNMATCHED;
    private static final Logger LOG;
    static final int CLOSED_STATE = 1;
    static final int OPEN_STATE = 2;
    static final long NOT_ACKED = -1L;
    static final int VERSION = 5;
    protected PageFile pageFile;
    protected Journal journal;
    protected Metadata metadata;
    protected MetadataMarshaller metadataMarshaller;
    protected boolean failIfDatabaseIsLocked;
    protected boolean deleteAllMessages;
    protected File directory;
    protected File indexDirectory;
    protected Thread checkpointThread;
    protected boolean enableJournalDiskSyncs;
    protected boolean archiveDataLogs;
    protected File directoryArchive;
    protected AtomicLong journalSize;
    long checkpointInterval;
    long cleanupInterval;
    int journalMaxFileLength;
    int journalMaxWriteBatchSize;
    boolean enableIndexWriteAsync;
    int setIndexWriteBatchSize;
    protected AtomicBoolean opened;
    private boolean ignoreMissingJournalfiles;
    private int indexCacheSize;
    private boolean checkForCorruptJournalFiles;
    private boolean checksumJournalFiles;
    protected boolean forceRecoverIndex;
    private final Object checkpointThreadLock;
    private boolean rewriteOnRedelivery;
    private boolean archiveCorruptedIndex;
    private boolean useIndexLFRUEviction;
    private float indexLFUEvictionFactor;
    private boolean enableIndexDiskSyncs;
    private boolean enableIndexRecoveryFile;
    private boolean enableIndexPageCaching;
    ReentrantReadWriteLock checkpointLock;
    private Location nextRecoveryPosition;
    private Location lastRecoveryPosition;
    protected final ReentrantReadWriteLock indexLock;
    private final HashSet<Integer> journalFilesBeingReplicated;
    final Runnable nullCompletionCallback;
    private final HashMap<String, StoredDestination> storedDestinations;
    private final LinkedHashMap<TransactionId, List<Operation>> inflightTransactions;
    protected final LinkedHashMap<TransactionId, List<Operation>> preparedTransactions;
    protected final Set<String> ackedAndPrepared;
    protected final Set<String> rolledBackAcks;
    
    public MessageDatabase() {
        this.metadata = new Metadata();
        this.metadataMarshaller = new MetadataMarshaller();
        this.directory = MessageDatabase.DEFAULT_DIRECTORY;
        this.indexDirectory = null;
        this.enableJournalDiskSyncs = true;
        this.journalSize = new AtomicLong(0L);
        this.checkpointInterval = 5000L;
        this.cleanupInterval = 30000L;
        this.journalMaxFileLength = 33554432;
        this.journalMaxWriteBatchSize = 4194304;
        this.enableIndexWriteAsync = false;
        this.setIndexWriteBatchSize = PageFile.DEFAULT_WRITE_BATCH_SIZE;
        this.opened = new AtomicBoolean();
        this.ignoreMissingJournalfiles = false;
        this.indexCacheSize = 10000;
        this.checkForCorruptJournalFiles = false;
        this.checksumJournalFiles = true;
        this.forceRecoverIndex = false;
        this.checkpointThreadLock = new Object();
        this.rewriteOnRedelivery = false;
        this.archiveCorruptedIndex = false;
        this.useIndexLFRUEviction = false;
        this.indexLFUEvictionFactor = 0.2f;
        this.enableIndexDiskSyncs = true;
        this.enableIndexRecoveryFile = true;
        this.enableIndexPageCaching = true;
        this.checkpointLock = new ReentrantReadWriteLock();
        this.indexLock = new ReentrantReadWriteLock();
        this.journalFilesBeingReplicated = new HashSet<Integer>();
        this.nullCompletionCallback = new Runnable() {
            @Override
            public void run() {
            }
        };
        this.storedDestinations = new HashMap<String, StoredDestination>();
        this.inflightTransactions = new LinkedHashMap<TransactionId, List<Operation>>();
        this.preparedTransactions = new LinkedHashMap<TransactionId, List<Operation>>();
        this.ackedAndPrepared = new HashSet<String>();
        this.rolledBackAcks = new HashSet<String>();
    }
    
    public void doStart() throws Exception {
        this.load();
    }
    
    public void doStop(final ServiceStopper stopper) throws Exception {
        this.unload();
    }
    
    private void loadPageFile() throws IOException {
        this.indexLock.writeLock().lock();
        try {
            final PageFile pageFile = this.getPageFile();
            pageFile.load();
            pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    if (pageFile.getPageCount() == 0L) {
                        final Page<Metadata> page = tx.allocate();
                        assert page.getPageId() == 0L;
                        page.set(MessageDatabase.this.metadata);
                        MessageDatabase.this.metadata.page = page;
                        MessageDatabase.this.metadata.state = 1;
                        MessageDatabase.this.metadata.destinations = new BTreeIndex<String, StoredDestination>(pageFile, tx.allocate().getPageId());
                        tx.store(MessageDatabase.this.metadata.page, MessageDatabase.this.metadataMarshaller, true);
                    }
                    else {
                        final Page<Metadata> page = tx.load(0L, (Marshaller<Metadata>)MessageDatabase.this.metadataMarshaller);
                        MessageDatabase.this.metadata = page.get();
                        MessageDatabase.this.metadata.page = page;
                    }
                    MessageDatabase.this.metadata.destinations.setKeyMarshaller(StringMarshaller.INSTANCE);
                    MessageDatabase.this.metadata.destinations.setValueMarshaller(new StoredDestinationMarshaller());
                    MessageDatabase.this.metadata.destinations.load(tx);
                }
            });
            this.storedDestinations.clear();
            pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    final Iterator<Map.Entry<String, StoredDestination>> iterator = MessageDatabase.this.metadata.destinations.iterator(tx);
                    while (iterator.hasNext()) {
                        final Map.Entry<String, StoredDestination> entry = iterator.next();
                        final StoredDestination sd = MessageDatabase.this.loadStoredDestination(tx, entry.getKey(), entry.getValue().subscriptions != null);
                        MessageDatabase.this.storedDestinations.put(entry.getKey(), sd);
                        if (MessageDatabase.this.checkForCorruptJournalFiles && !entry.getValue().locationIndex.isEmpty(tx) && entry.getValue().orderIndex.nextMessageId <= 0L) {
                            throw new IOException("Detected uninitialized orderIndex nextMessageId with pending messages for " + entry.getKey());
                        }
                    }
                }
            });
            pageFile.flush();
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    private void startCheckpoint() {
        if (this.checkpointInterval == 0L && this.cleanupInterval == 0L) {
            MessageDatabase.LOG.info("periodic checkpoint/cleanup disabled, will ocurr on clean shutdown/restart");
            return;
        }
        synchronized (this.checkpointThreadLock) {
            boolean start = false;
            if (this.checkpointThread == null) {
                start = true;
            }
            else if (!this.checkpointThread.isAlive()) {
                start = true;
                MessageDatabase.LOG.info("KahaDB: Recovering checkpoint thread after death");
            }
            if (start) {
                (this.checkpointThread = new Thread("ActiveMQ Journal Checkpoint Worker") {
                    @Override
                    public void run() {
                        try {
                            long lastCleanup = System.currentTimeMillis();
                            long lastCheckpoint = System.currentTimeMillis();
                            final long sleepTime = Math.min((MessageDatabase.this.checkpointInterval > 0L) ? MessageDatabase.this.checkpointInterval : MessageDatabase.this.cleanupInterval, 500L);
                            while (MessageDatabase.this.opened.get()) {
                                Thread.sleep(sleepTime);
                                final long now = System.currentTimeMillis();
                                if (MessageDatabase.this.cleanupInterval > 0L && now - lastCleanup >= MessageDatabase.this.cleanupInterval) {
                                    MessageDatabase.this.checkpointCleanup(true);
                                    lastCleanup = now;
                                    lastCheckpoint = now;
                                }
                                else {
                                    if (MessageDatabase.this.checkpointInterval <= 0L || now - lastCheckpoint < MessageDatabase.this.checkpointInterval) {
                                        continue;
                                    }
                                    MessageDatabase.this.checkpointCleanup(false);
                                    lastCheckpoint = now;
                                }
                            }
                        }
                        catch (InterruptedException ex) {}
                        catch (IOException ioe) {
                            MessageDatabase.LOG.error("Checkpoint failed", ioe);
                            MessageDatabase.this.brokerService.handleIOException(ioe);
                        }
                    }
                }).setDaemon(true);
                this.checkpointThread.start();
            }
        }
    }
    
    public void open() throws IOException {
        if (this.opened.compareAndSet(false, true)) {
            this.getJournal().start();
            try {
                this.loadPageFile();
            }
            catch (Throwable t) {
                MessageDatabase.LOG.warn("Index corrupted. Recovering the index through journal replay. Cause:" + t);
                if (MessageDatabase.LOG.isDebugEnabled()) {
                    MessageDatabase.LOG.debug("Index load failure", t);
                }
                try {
                    this.pageFile.unload();
                }
                catch (Exception ex) {}
                if (this.archiveCorruptedIndex) {
                    this.pageFile.archive();
                }
                else {
                    this.pageFile.delete();
                }
                this.metadata = this.createMetadata();
                this.pageFile = null;
                this.loadPageFile();
            }
            this.startCheckpoint();
            this.recover();
        }
    }
    
    public void load() throws IOException {
        this.indexLock.writeLock().lock();
        IOHelper.mkdirs(this.directory);
        try {
            if (this.deleteAllMessages) {
                this.getJournal().start();
                this.getJournal().delete();
                this.getJournal().close();
                this.journal = null;
                this.getPageFile().delete();
                MessageDatabase.LOG.info("Persistence store purged.");
                this.deleteAllMessages = false;
            }
            this.open();
            this.store(((KahaTraceCommandBase<JournalCommand<?>>)new KahaTraceCommand()).setMessage("LOADED " + new Date()));
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    public void close() throws IOException, InterruptedException {
        if (this.opened.compareAndSet(true, false)) {
            this.checkpointLock.writeLock().lock();
            try {
                if (this.metadata.page != null) {
                    this.checkpointUpdate(true);
                }
                this.pageFile.unload();
                this.metadata = this.createMetadata();
            }
            finally {
                this.checkpointLock.writeLock().unlock();
            }
            this.journal.close();
            synchronized (this.checkpointThreadLock) {
                if (this.checkpointThread != null) {
                    this.checkpointThread.join();
                }
            }
        }
    }
    
    public void unload() throws IOException, InterruptedException {
        this.indexLock.writeLock().lock();
        try {
            if (this.pageFile != null && this.pageFile.isLoaded()) {
                this.metadata.state = 1;
                this.metadata.firstInProgressTransactionLocation = this.getInProgressTxLocationRange()[0];
                if (this.metadata.page != null) {
                    this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            tx.store(MessageDatabase.this.metadata.page, MessageDatabase.this.metadataMarshaller, true);
                        }
                    });
                }
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
        this.close();
    }
    
    public Location[] getInProgressTxLocationRange() {
        final Location[] range = { null, null };
        synchronized (this.inflightTransactions) {
            if (!this.inflightTransactions.isEmpty()) {
                for (final List<Operation> ops : this.inflightTransactions.values()) {
                    if (!ops.isEmpty()) {
                        this.trackMaxAndMin(range, ops);
                    }
                }
            }
            if (!this.preparedTransactions.isEmpty()) {
                for (final List<Operation> ops : this.preparedTransactions.values()) {
                    if (!ops.isEmpty()) {
                        this.trackMaxAndMin(range, ops);
                    }
                }
            }
        }
        return range;
    }
    
    private void trackMaxAndMin(final Location[] range, final List<Operation> ops) {
        Location t = ops.get(0).getLocation();
        if (range[0] == null || t.compareTo(range[0]) <= 0) {
            range[0] = t;
        }
        t = ops.get(ops.size() - 1).getLocation();
        if (range[1] == null || t.compareTo(range[1]) >= 0) {
            range[1] = t;
        }
    }
    
    public String getTransactions() {
        final ArrayList<TranInfo> infos = new ArrayList<TranInfo>();
        synchronized (this.inflightTransactions) {
            if (!this.inflightTransactions.isEmpty()) {
                for (final Map.Entry<TransactionId, List<Operation>> entry : this.inflightTransactions.entrySet()) {
                    final TranInfo info = new TranInfo();
                    info.id = entry.getKey();
                    for (final Operation operation : entry.getValue()) {
                        info.track(operation);
                    }
                    infos.add(info);
                }
            }
        }
        synchronized (this.preparedTransactions) {
            if (!this.preparedTransactions.isEmpty()) {
                for (final Map.Entry<TransactionId, List<Operation>> entry : this.preparedTransactions.entrySet()) {
                    final TranInfo info = new TranInfo();
                    info.id = entry.getKey();
                    for (final Operation operation : entry.getValue()) {
                        info.track(operation);
                    }
                    infos.add(info);
                }
            }
        }
        return infos.toString();
    }
    
    private void recover() throws IllegalStateException, IOException {
        this.indexLock.writeLock().lock();
        try {
            final long start = System.currentTimeMillis();
            final Location producerAuditPosition = this.recoverProducerAudit();
            final Location ackMessageFileLocation = this.recoverAckMessageFileMap();
            final Location lastIndoubtPosition = this.getRecoveryPosition();
            Location recoveryPosition = this.minimum(producerAuditPosition, ackMessageFileLocation);
            recoveryPosition = this.minimum(recoveryPosition, lastIndoubtPosition);
            if (recoveryPosition != null) {
                int redoCounter = 0;
                MessageDatabase.LOG.info("Recovering from the journal ...");
                while (recoveryPosition != null) {
                    final JournalCommand<?> message = this.load(recoveryPosition);
                    this.process(message, this.metadata.lastUpdate = recoveryPosition, lastIndoubtPosition);
                    ++redoCounter;
                    recoveryPosition = this.journal.getNextLocation(recoveryPosition);
                    if (MessageDatabase.LOG.isInfoEnabled() && redoCounter % 100000 == 0) {
                        MessageDatabase.LOG.info("@" + recoveryPosition + ", " + redoCounter + " entries recovered ..");
                    }
                }
                if (MessageDatabase.LOG.isInfoEnabled()) {
                    final long end = System.currentTimeMillis();
                    MessageDatabase.LOG.info("Recovery replayed " + redoCounter + " operations from the journal in " + (end - start) / 1000.0f + " seconds.");
                }
            }
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    MessageDatabase.this.recoverIndex(tx);
                }
            });
            final Set<TransactionId> toRollback = new HashSet<TransactionId>();
            final Set<TransactionId> toDiscard = new HashSet<TransactionId>();
            synchronized (this.inflightTransactions) {
                for (final TransactionId id : this.inflightTransactions.keySet()) {
                    if (id.isLocalTransaction()) {
                        toRollback.add(id);
                    }
                    else {
                        toDiscard.add(id);
                    }
                }
                for (final TransactionId tx : toRollback) {
                    if (MessageDatabase.LOG.isDebugEnabled()) {
                        MessageDatabase.LOG.debug("rolling back recovered indoubt local transaction " + tx);
                    }
                    this.store(((KahaRollbackCommandBase<JournalCommand<?>>)new KahaRollbackCommand()).setTransactionInfo(TransactionIdConversion.convertToLocal(tx)), false, null, null);
                }
                for (final TransactionId tx : toDiscard) {
                    if (MessageDatabase.LOG.isDebugEnabled()) {
                        MessageDatabase.LOG.debug("discarding recovered in-flight XA transaction " + tx);
                    }
                    this.inflightTransactions.remove(tx);
                }
            }
            synchronized (this.preparedTransactions) {
                for (final TransactionId txId : this.preparedTransactions.keySet()) {
                    MessageDatabase.LOG.warn("Recovered prepared XA TX: [{}]", txId);
                }
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    private KahaTransactionInfo createLocalTransactionInfo(final TransactionId tx) {
        return TransactionIdConversion.convertToLocal(tx);
    }
    
    private Location minimum(final Location producerAuditPosition, final Location lastIndoubtPosition) {
        Location min = null;
        if (producerAuditPosition != null) {
            min = producerAuditPosition;
            if (lastIndoubtPosition != null && lastIndoubtPosition.compareTo(producerAuditPosition) < 0) {
                min = lastIndoubtPosition;
            }
        }
        else {
            min = lastIndoubtPosition;
        }
        return min;
    }
    
    private Location recoverProducerAudit() throws IOException {
        if (this.metadata.producerSequenceIdTrackerLocation != null) {
            final KahaProducerAuditCommand audit = (KahaProducerAuditCommand)this.load(this.metadata.producerSequenceIdTrackerLocation);
            try {
                final ObjectInputStream objectIn = new ObjectInputStream(audit.getAudit().newInput());
                final int maxNumProducers = this.getMaxFailoverProducersToTrack();
                final int maxAuditDepth = this.getFailoverProducersAuditDepth();
                (this.metadata.producerSequenceIdTracker = (ActiveMQMessageAuditNoSync)objectIn.readObject()).setAuditDepth(maxAuditDepth);
                this.metadata.producerSequenceIdTracker.setMaximumNumberOfProducersToTrack(maxNumProducers);
                return this.journal.getNextLocation(this.metadata.producerSequenceIdTrackerLocation);
            }
            catch (Exception e) {
                MessageDatabase.LOG.warn("Cannot recover message audit", e);
                return this.journal.getNextLocation(null);
            }
        }
        return this.journal.getNextLocation(null);
    }
    
    private Location recoverAckMessageFileMap() throws IOException {
        if (this.metadata.ackMessageFileMapLocation != null) {
            final KahaAckMessageFileMapCommand audit = (KahaAckMessageFileMapCommand)this.load(this.metadata.ackMessageFileMapLocation);
            try {
                final ObjectInputStream objectIn = new ObjectInputStream(audit.getAckMessageFileMap().newInput());
                this.metadata.ackMessageFileMap = (Map<Integer, Set<Integer>>)objectIn.readObject();
                return this.journal.getNextLocation(this.metadata.ackMessageFileMapLocation);
            }
            catch (Exception e) {
                MessageDatabase.LOG.warn("Cannot recover ackMessageFileMap", e);
                return this.journal.getNextLocation(null);
            }
        }
        return this.journal.getNextLocation(null);
    }
    
    protected void recoverIndex(final Transaction tx) throws IOException {
        long start = System.currentTimeMillis();
        final Location lastAppendLocation = this.journal.getLastAppendLocation();
        long undoCounter = 0L;
        for (final StoredDestination sd : this.storedDestinations.values()) {
            final ArrayList<Long> matches = new ArrayList<Long>();
            sd.locationIndex.visit(tx, new BTreeVisitor.GTEVisitor<Location, Long>(lastAppendLocation) {
                @Override
                protected void matched(final Location key, final Long value) {
                    matches.add(value);
                }
            });
            for (final Long sequenceId : matches) {
                final MessageKeys keys = sd.orderIndex.remove(tx, sequenceId);
                sd.locationIndex.remove(tx, keys.location);
                sd.messageIdIndex.remove(tx, keys.messageId);
                this.metadata.producerSequenceIdTracker.rollback(keys.messageId);
                ++undoCounter;
            }
        }
        if (undoCounter > 0L && MessageDatabase.LOG.isInfoEnabled()) {
            final long end = System.currentTimeMillis();
            MessageDatabase.LOG.info("Rolled back " + undoCounter + " messages from the index in " + (end - start) / 1000.0f + " seconds.");
        }
        undoCounter = 0L;
        start = System.currentTimeMillis();
        final SequenceSet ss = new SequenceSet();
        for (final StoredDestination sd2 : this.storedDestinations.values()) {
            sd2.locationIndex.visit(tx, new BTreeVisitor<Location, Long>() {
                int last = -1;
                
                @Override
                public boolean isInterestedInKeysBetween(final Location first, final Location second) {
                    if (first == null) {
                        return !ss.contains(0, second.getDataFileId());
                    }
                    return second == null || !ss.contains(first.getDataFileId(), second.getDataFileId());
                }
                
                @Override
                public void visit(final List<Location> keys, final List<Long> values) {
                    for (final Location l : keys) {
                        final int fileId = l.getDataFileId();
                        if (this.last != fileId) {
                            ss.add(fileId);
                            this.last = fileId;
                        }
                    }
                }
            });
        }
        final HashSet<Integer> missingJournalFiles = new HashSet<Integer>();
        while (!ss.isEmpty()) {
            missingJournalFiles.add((int)ss.removeFirst());
        }
        missingJournalFiles.removeAll(this.journal.getFileMap().keySet());
        if (!missingJournalFiles.isEmpty() && MessageDatabase.LOG.isInfoEnabled()) {
            MessageDatabase.LOG.info("Some journal files are missing: " + missingJournalFiles);
        }
        final ArrayList<BTreeVisitor.Predicate<Location>> missingPredicates = new ArrayList<BTreeVisitor.Predicate<Location>>();
        for (final Integer missing : missingJournalFiles) {
            missingPredicates.add((BTreeVisitor.Predicate<Comparable>)new BTreeVisitor.BetweenVisitor<Comparable, Object>(new Location(missing, 0), new Location(missing + 1, 0)));
        }
        if (this.checkForCorruptJournalFiles) {
            final Collection<DataFile> dataFiles = this.journal.getFileMap().values();
            for (final DataFile dataFile : dataFiles) {
                final int id = dataFile.getDataFileId();
                missingPredicates.add((BTreeVisitor.Predicate<Comparable>)new BTreeVisitor.BetweenVisitor<Comparable, Object>(new Location(id, dataFile.getLength()), new Location(id + 1, 0)));
                for (Sequence seq = dataFile.getCorruptedBlocks().getHead(); seq != null; seq = seq.getNext()) {
                    missingPredicates.add((BTreeVisitor.Predicate<Comparable>)new BTreeVisitor.BetweenVisitor<Comparable, Object>(new Location(id, (int)seq.getFirst()), new Location(id, (int)seq.getLast() + 1)));
                }
            }
        }
        if (!missingPredicates.isEmpty()) {
            for (final StoredDestination sd3 : this.storedDestinations.values()) {
                final ArrayList<Long> matches2 = new ArrayList<Long>();
                sd3.locationIndex.visit(tx, new BTreeVisitor.OrVisitor<Location, Long>(missingPredicates) {
                    @Override
                    protected void matched(final Location key, final Long value) {
                        matches2.add(value);
                    }
                });
                if (!matches2.isEmpty()) {
                    if (!this.ignoreMissingJournalfiles) {
                        throw new IOException("Detected missing/corrupt journal files. " + matches2.size() + " messages affected.");
                    }
                    for (final Long sequenceId2 : matches2) {
                        final MessageKeys keys2 = sd3.orderIndex.remove(tx, sequenceId2);
                        sd3.locationIndex.remove(tx, keys2.location);
                        sd3.messageIdIndex.remove(tx, keys2.messageId);
                        ++undoCounter;
                    }
                }
            }
        }
        if (undoCounter > 0L && MessageDatabase.LOG.isInfoEnabled()) {
            final long end2 = System.currentTimeMillis();
            MessageDatabase.LOG.info("Detected missing/corrupt journal files.  Dropped " + undoCounter + " messages from the index in " + (end2 - start) / 1000.0f + " seconds.");
        }
    }
    
    public void incrementalRecover() throws IOException {
        this.indexLock.writeLock().lock();
        try {
            if (this.nextRecoveryPosition == null) {
                if (this.lastRecoveryPosition == null) {
                    this.nextRecoveryPosition = this.getRecoveryPosition();
                }
                else {
                    this.nextRecoveryPosition = this.journal.getNextLocation(this.lastRecoveryPosition);
                }
            }
            while (this.nextRecoveryPosition != null) {
                this.lastRecoveryPosition = this.nextRecoveryPosition;
                this.metadata.lastUpdate = this.lastRecoveryPosition;
                final JournalCommand<?> message = this.load(this.lastRecoveryPosition);
                this.process(message, this.lastRecoveryPosition, null, null);
                this.nextRecoveryPosition = this.journal.getNextLocation(this.lastRecoveryPosition);
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    public Location getLastUpdatePosition() throws IOException {
        return this.metadata.lastUpdate;
    }
    
    private Location getRecoveryPosition() throws IOException {
        if (!this.forceRecoverIndex) {
            if (this.metadata.firstInProgressTransactionLocation != null) {
                return this.metadata.firstInProgressTransactionLocation;
            }
            if (this.metadata.lastUpdate != null) {
                return this.journal.getNextLocation(this.metadata.lastUpdate);
            }
        }
        return this.journal.getNextLocation(null);
    }
    
    protected void checkpointCleanup(final boolean cleanup) throws IOException {
        this.indexLock.writeLock().lock();
        long start;
        try {
            start = System.currentTimeMillis();
            if (!this.opened.get()) {
                return;
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
        this.checkpointUpdate(cleanup);
        final long end = System.currentTimeMillis();
        if (MessageDatabase.LOG_SLOW_ACCESS_TIME > 0 && end - start > MessageDatabase.LOG_SLOW_ACCESS_TIME && MessageDatabase.LOG.isInfoEnabled()) {
            MessageDatabase.LOG.info("Slow KahaDB access: cleanup took " + (end - start));
        }
    }
    
    public ByteSequence toByteSequence(final JournalCommand<?> data) throws IOException {
        final int size = data.serializedSizeFramed();
        final DataByteArrayOutputStream os = new DataByteArrayOutputStream(size + 1);
        os.writeByte(data.type().getNumber());
        data.writeFramed(os);
        return os.toByteSequence();
    }
    
    public Location store(final JournalCommand<?> data) throws IOException {
        return this.store(data, false, null, null);
    }
    
    public Location store(final JournalCommand<?> data, final Runnable onJournalStoreComplete) throws IOException {
        return this.store(data, false, null, null, onJournalStoreComplete);
    }
    
    public Location store(final JournalCommand<?> data, final boolean sync, final Runnable before, final Runnable after) throws IOException {
        return this.store(data, sync, before, after, null);
    }
    
    public Location store(final JournalCommand<?> data, final boolean sync, final Runnable before, final Runnable after, final Runnable onJournalStoreComplete) throws IOException {
        try {
            final ByteSequence sequence = this.toByteSequence(data);
            this.checkpointLock.readLock().lock();
            Location location;
            try {
                final long start = System.currentTimeMillis();
                location = ((onJournalStoreComplete == null) ? this.journal.write(sequence, sync) : this.journal.write(sequence, onJournalStoreComplete));
                final long start2 = System.currentTimeMillis();
                this.process(data, location, before, after);
                final long end = System.currentTimeMillis();
                if (MessageDatabase.LOG_SLOW_ACCESS_TIME > 0 && end - start > MessageDatabase.LOG_SLOW_ACCESS_TIME && MessageDatabase.LOG.isInfoEnabled()) {
                    MessageDatabase.LOG.info("Slow KahaDB access: Journal append took: " + (start2 - start) + " ms, Index update took " + (end - start2) + " ms");
                }
            }
            finally {
                this.checkpointLock.readLock().unlock();
            }
            if (after != null) {
                after.run();
            }
            if (this.checkpointThread != null && !this.checkpointThread.isAlive()) {
                this.startCheckpoint();
            }
            return location;
        }
        catch (IOException ioe) {
            MessageDatabase.LOG.error("KahaDB failed to store to Journal", ioe);
            this.brokerService.handleIOException(ioe);
            throw ioe;
        }
    }
    
    public JournalCommand<?> load(final Location location) throws IOException {
        final long start = System.currentTimeMillis();
        final ByteSequence data = this.journal.read(location);
        final long end = System.currentTimeMillis();
        if (MessageDatabase.LOG_SLOW_ACCESS_TIME > 0 && end - start > MessageDatabase.LOG_SLOW_ACCESS_TIME && MessageDatabase.LOG.isInfoEnabled()) {
            MessageDatabase.LOG.info("Slow KahaDB access: Journal read took: " + (end - start) + " ms");
        }
        final DataByteArrayInputStream is = new DataByteArrayInputStream(data);
        final byte readByte = is.readByte();
        final KahaEntryType type = KahaEntryType.valueOf(readByte);
        if (type == null) {
            try {
                is.close();
            }
            catch (IOException ex) {}
            throw new IOException("Could not load journal record. Invalid location: " + location);
        }
        final JournalCommand<?> message = (JournalCommand<?>)type.createMessage();
        message.mergeFramed(is);
        return message;
    }
    
    void process(final JournalCommand<?> data, final Location location, final Location inDoubtlocation) throws IOException {
        if (inDoubtlocation != null && location.compareTo(inDoubtlocation) >= 0) {
            if (data instanceof KahaSubscriptionCommand) {
                final KahaSubscriptionCommand kahaSubscriptionCommand = (KahaSubscriptionCommand)data;
                if (kahaSubscriptionCommand.hasSubscriptionInfo()) {
                    MessageDatabase.LOG.debug("ignoring add sub command during recovery replay:" + data);
                    return;
                }
            }
            this.process(data, location, null, null);
        }
        else {
            data.visit(new Visitor() {
                @Override
                public void visit(final KahaAddMessageCommand command) throws IOException {
                    MessageDatabase.this.metadata.producerSequenceIdTracker.isDuplicate(command.getMessageId());
                }
            });
        }
    }
    
    void process(final JournalCommand<?> data, final Location location, final Runnable before, final Runnable after) throws IOException {
        data.visit(new Visitor() {
            @Override
            public void visit(final KahaAddMessageCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaRemoveMessageCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaPrepareCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaCommitCommand command) throws IOException {
                MessageDatabase.this.process(command, location, before, after);
            }
            
            @Override
            public void visit(final KahaRollbackCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaRemoveDestinationCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaSubscriptionCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
            
            @Override
            public void visit(final KahaProducerAuditCommand command) throws IOException {
                MessageDatabase.this.processLocation(location);
            }
            
            @Override
            public void visit(final KahaAckMessageFileMapCommand command) throws IOException {
                MessageDatabase.this.processLocation(location);
            }
            
            @Override
            public void visit(final KahaTraceCommand command) {
                MessageDatabase.this.processLocation(location);
            }
            
            @Override
            public void visit(final KahaUpdateMessageCommand command) throws IOException {
                MessageDatabase.this.process(command, location);
            }
        });
    }
    
    protected void process(final KahaAddMessageCommand command, final Location location) throws IOException {
        if (command.hasTransactionInfo()) {
            final List<Operation> inflightTx = this.getInflightTx(command.getTransactionInfo(), location);
            inflightTx.add(new AddOpperation(command, location));
        }
        else {
            this.indexLock.writeLock().lock();
            try {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        MessageDatabase.this.updateIndex(tx, command, location);
                    }
                });
            }
            finally {
                this.indexLock.writeLock().unlock();
            }
        }
    }
    
    protected void process(final KahaUpdateMessageCommand command, final Location location) throws IOException {
        this.indexLock.writeLock().lock();
        try {
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    MessageDatabase.this.updateIndex(tx, command, location);
                }
            });
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    protected void process(final KahaRemoveMessageCommand command, final Location location) throws IOException {
        if (command.hasTransactionInfo()) {
            final List<Operation> inflightTx = this.getInflightTx(command.getTransactionInfo(), location);
            inflightTx.add(new RemoveOpperation(command, location));
        }
        else {
            this.indexLock.writeLock().lock();
            try {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        MessageDatabase.this.updateIndex(tx, command, location);
                    }
                });
            }
            finally {
                this.indexLock.writeLock().unlock();
            }
        }
    }
    
    protected void process(final KahaRemoveDestinationCommand command, final Location location) throws IOException {
        this.indexLock.writeLock().lock();
        try {
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    MessageDatabase.this.updateIndex(tx, command, location);
                }
            });
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    protected void process(final KahaSubscriptionCommand command, final Location location) throws IOException {
        this.indexLock.writeLock().lock();
        try {
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    MessageDatabase.this.updateIndex(tx, command, location);
                }
            });
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    protected void processLocation(final Location location) {
        this.indexLock.writeLock().lock();
        try {
            this.metadata.lastUpdate = location;
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    protected void process(final KahaCommitCommand command, final Location location, final Runnable before, final Runnable after) throws IOException {
        final TransactionId key = TransactionIdConversion.convert(command.getTransactionInfo());
        List<Operation> inflightTx;
        synchronized (this.inflightTransactions) {
            inflightTx = this.inflightTransactions.remove(key);
            if (inflightTx == null) {
                inflightTx = this.preparedTransactions.remove(key);
            }
        }
        if (inflightTx == null) {
            if (before != null) {
                before.run();
            }
            return;
        }
        final List<Operation> messagingTx = inflightTx;
        this.indexLock.writeLock().lock();
        try {
            if (before != null) {
                before.run();
            }
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    for (final Operation op : messagingTx) {
                        op.execute(tx);
                    }
                }
            });
            this.metadata.lastUpdate = location;
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    protected void process(final KahaPrepareCommand command, final Location location) {
        final TransactionId key = TransactionIdConversion.convert(command.getTransactionInfo());
        synchronized (this.inflightTransactions) {
            final List<Operation> tx = this.inflightTransactions.remove(key);
            if (tx != null) {
                this.preparedTransactions.put(key, tx);
            }
        }
    }
    
    protected void process(final KahaRollbackCommand command, final Location location) throws IOException {
        final TransactionId key = TransactionIdConversion.convert(command.getTransactionInfo());
        List<Operation> updates = null;
        synchronized (this.inflightTransactions) {
            updates = this.inflightTransactions.remove(key);
            if (updates == null) {
                updates = this.preparedTransactions.remove(key);
            }
        }
    }
    
    void updateIndex(final Transaction tx, final KahaAddMessageCommand command, final Location location) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        if (sd.subscriptions != null && sd.subscriptions.isEmpty(tx)) {
            return;
        }
        final int priority = command.getPrioritySupported() ? command.getPriority() : 4;
        final long id = sd.orderIndex.getNextMessageId(priority);
        Long previous = sd.locationIndex.put(tx, location, id);
        if (previous == null) {
            previous = sd.messageIdIndex.put(tx, command.getMessageId(), id);
            if (previous == null) {
                sd.orderIndex.put(tx, priority, id, new MessageKeys(command.getMessageId(), location));
                if (sd.subscriptions != null && !sd.subscriptions.isEmpty(tx)) {
                    this.addAckLocationForNewMessage(tx, sd, id);
                }
            }
            else {
                MessageDatabase.LOG.warn("Duplicate message add attempt rejected. Destination: {}://{}, Message id: {}", command.getDestination().getType(), command.getDestination().getName(), command.getMessageId());
                sd.messageIdIndex.put(tx, command.getMessageId(), previous);
                sd.locationIndex.remove(tx, location);
                this.rollbackStatsOnDuplicate(command.getDestination());
            }
        }
        else {
            sd.locationIndex.put(tx, location, previous);
        }
        this.metadata.producerSequenceIdTracker.isDuplicate(command.getMessageId());
        this.metadata.lastUpdate = location;
    }
    
    void updateIndex(final Transaction tx, final KahaUpdateMessageCommand updateMessageCommand, final Location location) throws IOException {
        final KahaAddMessageCommand command = updateMessageCommand.getMessage();
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        final Long id = sd.messageIdIndex.get(tx, command.getMessageId());
        if (id != null) {
            sd.orderIndex.put(tx, command.getPrioritySupported() ? command.getPriority() : 4, id, new MessageKeys(command.getMessageId(), location));
            sd.locationIndex.put(tx, location, id);
        }
        else {
            MessageDatabase.LOG.warn("Non existent message update attempt rejected. Destination: {}://{}, Message id: {}", command.getDestination().getType(), command.getDestination().getName(), command.getMessageId());
        }
        this.metadata.lastUpdate = location;
    }
    
    abstract void rollbackStatsOnDuplicate(final KahaDestination p0);
    
    void updateIndex(final Transaction tx, final KahaRemoveMessageCommand command, final Location ackLocation) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        if (!command.hasSubscriptionKey()) {
            final Long sequenceId = sd.messageIdIndex.remove(tx, command.getMessageId());
            if (sequenceId != null) {
                final MessageKeys keys = sd.orderIndex.remove(tx, sequenceId);
                if (keys != null) {
                    sd.locationIndex.remove(tx, keys.location);
                    this.recordAckMessageReferenceLocation(ackLocation, keys.location);
                }
                else if (MessageDatabase.LOG.isDebugEnabled()) {
                    MessageDatabase.LOG.debug("message not found in order index: " + sequenceId + " for: " + command.getMessageId());
                }
            }
            else if (MessageDatabase.LOG.isDebugEnabled()) {
                MessageDatabase.LOG.debug("message not found in sequence id index: " + command.getMessageId());
            }
        }
        else {
            final Long sequence = sd.messageIdIndex.get(tx, command.getMessageId());
            if (sequence != null) {
                final String subscriptionKey = command.getSubscriptionKey();
                if (command.getAck() != MessageDatabase.UNMATCHED) {
                    sd.orderIndex.get(tx, sequence);
                    final byte priority = sd.orderIndex.lastGetPriority();
                    sd.subscriptionAcks.put(tx, subscriptionKey, new LastAck(sequence, priority));
                }
                final MessageKeys keys2 = sd.orderIndex.get(tx, sequence);
                if (keys2 != null) {
                    this.recordAckMessageReferenceLocation(ackLocation, keys2.location);
                }
                this.removeAckLocation(tx, sd, subscriptionKey, sequence);
            }
            else if (MessageDatabase.LOG.isDebugEnabled()) {
                MessageDatabase.LOG.debug("no message sequence exists for id: " + command.getMessageId() + " and sub: " + command.getSubscriptionKey());
            }
        }
        this.metadata.lastUpdate = ackLocation;
    }
    
    private void recordAckMessageReferenceLocation(final Location ackLocation, final Location messageLocation) {
        Set<Integer> referenceFileIds = this.metadata.ackMessageFileMap.get(ackLocation.getDataFileId());
        if (referenceFileIds == null) {
            referenceFileIds = new HashSet<Integer>();
            referenceFileIds.add(messageLocation.getDataFileId());
            this.metadata.ackMessageFileMap.put(ackLocation.getDataFileId(), referenceFileIds);
        }
        else {
            final Integer id = messageLocation.getDataFileId();
            if (!referenceFileIds.contains(id)) {
                referenceFileIds.add(id);
            }
        }
    }
    
    void updateIndex(final Transaction tx, final KahaRemoveDestinationCommand command, final Location location) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        sd.orderIndex.remove(tx);
        sd.locationIndex.clear(tx);
        sd.locationIndex.unload(tx);
        tx.free(sd.locationIndex.getPageId());
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
            sd.ackPositions.clear(tx);
            sd.ackPositions.unload(tx);
            tx.free(sd.ackPositions.getHeadPageId());
            sd.subLocations.clear(tx);
            sd.subLocations.unload(tx);
            tx.free(sd.subLocations.getHeadPageId());
        }
        final String key = this.key(command.getDestination());
        this.storedDestinations.remove(key);
        this.metadata.destinations.remove(tx, key);
    }
    
    void updateIndex(final Transaction tx, final KahaSubscriptionCommand command, final Location location) throws IOException {
        final StoredDestination sd = this.getStoredDestination(command.getDestination(), tx);
        final String subscriptionKey = command.getSubscriptionKey();
        if (command.hasSubscriptionInfo()) {
            sd.subscriptions.put(tx, subscriptionKey, command);
            sd.subLocations.put(tx, subscriptionKey, location);
            long ackLocation = -1L;
            if (!command.getRetroactive()) {
                ackLocation = sd.orderIndex.nextMessageId - 1L;
            }
            else {
                this.addAckLocationForRetroactiveSub(tx, sd, subscriptionKey);
            }
            sd.subscriptionAcks.put(tx, subscriptionKey, new LastAck(ackLocation));
            sd.subscriptionCache.add(subscriptionKey);
        }
        else {
            sd.subscriptions.remove(tx, subscriptionKey);
            sd.subLocations.remove(tx, subscriptionKey);
            sd.subscriptionAcks.remove(tx, subscriptionKey);
            sd.subscriptionCache.remove(subscriptionKey);
            this.removeAckLocationsForSub(tx, sd, subscriptionKey);
            if (sd.subscriptions.isEmpty(tx)) {
                sd.messageIdIndex.clear(tx);
                sd.locationIndex.clear(tx);
                sd.orderIndex.clear(tx);
            }
        }
    }
    
    private void checkpointUpdate(final boolean cleanup) throws IOException {
        this.checkpointLock.writeLock().lock();
        try {
            this.indexLock.writeLock().lock();
            try {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        MessageDatabase.this.checkpointUpdate(tx, cleanup);
                    }
                });
            }
            finally {
                this.indexLock.writeLock().unlock();
            }
        }
        finally {
            this.checkpointLock.writeLock().unlock();
        }
    }
    
    void checkpointUpdate(final Transaction tx, final boolean cleanup) throws IOException {
        MessageDatabase.LOG.debug("Checkpoint started.");
        final Location lastUpdate = this.metadata.lastUpdate;
        this.metadata.state = 2;
        this.metadata.producerSequenceIdTrackerLocation = this.checkpointProducerAudit();
        this.metadata.ackMessageFileMapLocation = this.checkpointAckMessageFileMap();
        final Location[] inProgressTxRange = this.getInProgressTxLocationRange();
        this.metadata.firstInProgressTransactionLocation = inProgressTxRange[0];
        tx.store(this.metadata.page, this.metadataMarshaller, true);
        this.pageFile.flush();
        if (cleanup) {
            final TreeSet<Integer> completeFileSet = new TreeSet<Integer>(this.journal.getFileMap().keySet());
            final TreeSet<Integer> gcCandidateSet = new TreeSet<Integer>(completeFileSet);
            if (MessageDatabase.LOG.isTraceEnabled()) {
                MessageDatabase.LOG.trace("Last update: " + lastUpdate + ", full gc candidates set: " + gcCandidateSet);
            }
            if (lastUpdate != null) {
                gcCandidateSet.remove(lastUpdate.getDataFileId());
            }
            if (this.journalFilesBeingReplicated != null) {
                gcCandidateSet.removeAll(this.journalFilesBeingReplicated);
            }
            if (this.metadata.producerSequenceIdTrackerLocation != null) {
                final int dataFileId = this.metadata.producerSequenceIdTrackerLocation.getDataFileId();
                if (gcCandidateSet.contains(dataFileId) && gcCandidateSet.first() == dataFileId) {
                    this.metadata.producerSequenceIdTracker.setModified(true);
                    if (MessageDatabase.LOG.isTraceEnabled()) {
                        MessageDatabase.LOG.trace("rewriting producerSequenceIdTracker:" + this.metadata.producerSequenceIdTrackerLocation);
                    }
                }
                gcCandidateSet.remove(dataFileId);
                if (MessageDatabase.LOG.isTraceEnabled()) {
                    MessageDatabase.LOG.trace("gc candidates after producerSequenceIdTrackerLocation:" + dataFileId + ", " + gcCandidateSet);
                }
            }
            if (this.metadata.ackMessageFileMapLocation != null) {
                final int dataFileId = this.metadata.ackMessageFileMapLocation.getDataFileId();
                gcCandidateSet.remove(dataFileId);
                if (MessageDatabase.LOG.isTraceEnabled()) {
                    MessageDatabase.LOG.trace("gc candidates after ackMessageFileMapLocation:" + dataFileId + ", " + gcCandidateSet);
                }
            }
            if (inProgressTxRange[0] != null) {
                for (int pendingTx = inProgressTxRange[0].getDataFileId(); pendingTx <= inProgressTxRange[1].getDataFileId(); ++pendingTx) {
                    gcCandidateSet.remove(pendingTx);
                }
            }
            if (MessageDatabase.LOG.isTraceEnabled()) {
                MessageDatabase.LOG.trace("gc candidates after tx range:" + Arrays.asList(inProgressTxRange) + ", " + gcCandidateSet);
            }
            for (final Map.Entry<String, StoredDestination> entry : this.storedDestinations.entrySet()) {
                if (gcCandidateSet.isEmpty()) {
                    break;
                }
                entry.getValue().locationIndex.visit(tx, new BTreeVisitor<Location, Long>() {
                    int last = -1;
                    
                    @Override
                    public boolean isInterestedInKeysBetween(final Location first, final Location second) {
                        if (first == null) {
                            final SortedSet<Integer> subset = gcCandidateSet.headSet(second.getDataFileId() + 1);
                            if (!subset.isEmpty() && subset.last() == second.getDataFileId()) {
                                subset.remove(second.getDataFileId());
                            }
                            return !subset.isEmpty();
                        }
                        if (second == null) {
                            final SortedSet<Integer> subset = gcCandidateSet.tailSet(first.getDataFileId());
                            if (!subset.isEmpty() && subset.first() == first.getDataFileId()) {
                                subset.remove(first.getDataFileId());
                            }
                            return !subset.isEmpty();
                        }
                        final SortedSet<Integer> subset = gcCandidateSet.subSet(first.getDataFileId(), second.getDataFileId() + 1);
                        if (!subset.isEmpty() && subset.first() == first.getDataFileId()) {
                            subset.remove(first.getDataFileId());
                        }
                        if (!subset.isEmpty() && subset.last() == second.getDataFileId()) {
                            subset.remove(second.getDataFileId());
                        }
                        return !subset.isEmpty();
                    }
                    
                    @Override
                    public void visit(final List<Location> keys, final List<Long> values) {
                        for (final Location l : keys) {
                            final int fileId = l.getDataFileId();
                            if (this.last != fileId) {
                                gcCandidateSet.remove(fileId);
                                this.last = fileId;
                            }
                        }
                    }
                });
                if (entry.getValue().subLocations != null) {
                    final Iterator<Map.Entry<String, Location>> iter = entry.getValue().subLocations.iterator(tx);
                    while (iter.hasNext()) {
                        final Map.Entry<String, Location> subscription = iter.next();
                        final int dataFileId2 = subscription.getValue().getDataFileId();
                        if (!gcCandidateSet.isEmpty() && gcCandidateSet.first() == dataFileId2) {
                            final StoredDestination destination = entry.getValue();
                            final String subscriptionKey = subscription.getKey();
                            final SequenceSet pendingAcks = destination.ackPositions.get(tx, subscriptionKey);
                            if (pendingAcks == null || pendingAcks.size() <= 1) {
                                if (MessageDatabase.LOG.isTraceEnabled()) {
                                    MessageDatabase.LOG.trace("Found candidate for rewrite: {} from file {}", entry.getKey(), dataFileId2);
                                }
                                final KahaSubscriptionCommand kahaSub = destination.subscriptions.get(tx, subscriptionKey);
                                destination.subLocations.put(tx, subscriptionKey, this.checkpointSubscriptionCommand(kahaSub));
                                continue;
                            }
                        }
                        gcCandidateSet.remove(dataFileId2);
                    }
                }
                if (!MessageDatabase.LOG.isTraceEnabled()) {
                    continue;
                }
                MessageDatabase.LOG.trace("gc candidates after dest:" + entry.getKey() + ", " + gcCandidateSet);
            }
            if (MessageDatabase.LOG.isTraceEnabled()) {
                MessageDatabase.LOG.trace("gc candidates: " + gcCandidateSet);
            }
            final TreeSet<Integer> gcCandidates = new TreeSet<Integer>(gcCandidateSet);
            final Iterator<Integer> candidates = gcCandidateSet.iterator();
            while (candidates.hasNext()) {
                final Integer candidate = candidates.next();
                final Set<Integer> referencedFileIds = this.metadata.ackMessageFileMap.get(candidate);
                if (referencedFileIds != null) {
                    for (final Integer referencedFileId : referencedFileIds) {
                        if (completeFileSet.contains(referencedFileId) && !gcCandidates.contains(referencedFileId)) {
                            candidates.remove();
                            break;
                        }
                    }
                    if (gcCandidateSet.contains(candidate)) {
                        this.metadata.ackMessageFileMap.remove(candidate);
                    }
                    else {
                        if (!MessageDatabase.LOG.isTraceEnabled()) {
                            continue;
                        }
                        MessageDatabase.LOG.trace("not removing data file: " + candidate + " as contained ack(s) refer to referenced file: " + referencedFileIds);
                    }
                }
            }
            if (!gcCandidateSet.isEmpty()) {
                if (MessageDatabase.LOG.isDebugEnabled()) {
                    MessageDatabase.LOG.debug("Cleanup removing the data files: " + gcCandidateSet);
                }
                this.journal.removeDataFiles(gcCandidateSet);
            }
        }
        MessageDatabase.LOG.debug("Checkpoint done.");
    }
    
    private Location checkpointProducerAudit() throws IOException {
        if (this.metadata.producerSequenceIdTracker == null || this.metadata.producerSequenceIdTracker.modified()) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream(baos);
            oout.writeObject(this.metadata.producerSequenceIdTracker);
            oout.flush();
            oout.close();
            final Location location = this.store(((KahaProducerAuditCommandBase<JournalCommand<?>>)new KahaProducerAuditCommand()).setAudit(new Buffer(baos.toByteArray())), this.nullCompletionCallback);
            try {
                location.getLatch().await();
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException(e.toString());
            }
            return location;
        }
        return this.metadata.producerSequenceIdTrackerLocation;
    }
    
    private Location checkpointAckMessageFileMap() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oout = new ObjectOutputStream(baos);
        oout.writeObject(this.metadata.ackMessageFileMap);
        oout.flush();
        oout.close();
        final Location location = this.store(((KahaAckMessageFileMapCommandBase<JournalCommand<?>>)new KahaAckMessageFileMapCommand()).setAckMessageFileMap(new Buffer(baos.toByteArray())), this.nullCompletionCallback);
        try {
            location.getLatch().await();
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException(e.toString());
        }
        return location;
    }
    
    private Location checkpointSubscriptionCommand(final KahaSubscriptionCommand subscription) throws IOException {
        final ByteSequence sequence = this.toByteSequence(subscription);
        final Location location = this.journal.write(sequence, this.nullCompletionCallback);
        try {
            location.getLatch().await();
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException(e.toString());
        }
        return location;
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
    
    protected StoredDestination getExistingStoredDestination(final KahaDestination destination, final Transaction tx) throws IOException {
        final String key = this.key(destination);
        StoredDestination rc = this.storedDestinations.get(key);
        if (rc == null && this.metadata.destinations.containsKey(tx, key)) {
            rc = this.getStoredDestination(destination, tx);
        }
        return rc;
    }
    
    private StoredDestination loadStoredDestination(final Transaction tx, final String key, final boolean topic) throws IOException {
        StoredDestination rc = this.metadata.destinations.get(tx, key);
        if (rc == null) {
            rc = new StoredDestination();
            rc.orderIndex.allocate(tx);
            rc.locationIndex = new BTreeIndex<Location, Long>(this.pageFile, tx.allocate());
            rc.messageIdIndex = new BTreeIndex<String, Long>(this.pageFile, tx.allocate());
            if (topic) {
                rc.subscriptions = new BTreeIndex<String, KahaSubscriptionCommand>(this.pageFile, tx.allocate());
                rc.subscriptionAcks = new BTreeIndex<String, LastAck>(this.pageFile, tx.allocate());
                rc.ackPositions = new ListIndex<String, SequenceSet>(this.pageFile, tx.allocate());
                rc.subLocations = new ListIndex<String, Location>(this.pageFile, tx.allocate());
            }
            this.metadata.destinations.put(tx, key, rc);
        }
        rc.orderIndex.load(tx);
        rc.orderIndex.configureLast(tx);
        rc.locationIndex.setKeyMarshaller(LocationMarshaller.INSTANCE);
        rc.locationIndex.setValueMarshaller(LongMarshaller.INSTANCE);
        rc.locationIndex.load(tx);
        rc.messageIdIndex.setKeyMarshaller(StringMarshaller.INSTANCE);
        rc.messageIdIndex.setValueMarshaller(LongMarshaller.INSTANCE);
        rc.messageIdIndex.load(tx);
        if (topic) {
            rc.subscriptions.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.subscriptions.setValueMarshaller(KahaSubscriptionCommandMarshaller.INSTANCE);
            rc.subscriptions.load(tx);
            rc.subscriptionAcks.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.subscriptionAcks.setValueMarshaller(new LastAckMarshaller());
            rc.subscriptionAcks.load(tx);
            rc.ackPositions.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.ackPositions.setValueMarshaller(SequenceSet.Marshaller.INSTANCE);
            rc.ackPositions.load(tx);
            rc.subLocations.setKeyMarshaller(StringMarshaller.INSTANCE);
            rc.subLocations.setValueMarshaller(LocationMarshaller.INSTANCE);
            rc.subLocations.load(tx);
            rc.subscriptionCursors = new HashMap<String, MessageOrderCursor>();
            if (this.metadata.version < 3) {
                final Iterator<Map.Entry<String, LastAck>> iterator = rc.subscriptionAcks.iterator(tx);
                while (iterator.hasNext()) {
                    final Map.Entry<String, LastAck> entry = iterator.next();
                    final Iterator<Map.Entry<Long, MessageKeys>> orderIterator = rc.orderIndex.iterator(tx, new MessageOrderCursor(entry.getValue().lastAckedSequence));
                    while (orderIterator.hasNext()) {
                        final Long sequence = orderIterator.next().getKey();
                        this.addAckLocation(tx, rc, sequence, entry.getKey());
                    }
                    rc.subscriptionAcks.put(tx, entry.getKey(), entry.getValue());
                }
            }
            final Iterator<Map.Entry<String, SequenceSet>> subscriptions = rc.ackPositions.iterator(tx);
            while (subscriptions.hasNext()) {
                final Map.Entry<String, SequenceSet> subscription = subscriptions.next();
                final SequenceSet pendingAcks = subscription.getValue();
                if (pendingAcks != null && !pendingAcks.isEmpty()) {
                    final Long lastPendingAck = pendingAcks.getTail().getLast();
                    for (final Long sequenceId : pendingAcks) {
                        Long current = rc.messageReferences.get(sequenceId);
                        if (current == null) {
                            current = new Long(0L);
                        }
                        if (!sequenceId.equals(lastPendingAck)) {
                            ++current;
                        }
                        rc.messageReferences.put(sequenceId, current);
                    }
                }
            }
            Iterator<Map.Entry<String, LastAck>> iterator2 = rc.subscriptionAcks.iterator(tx);
            while (iterator2.hasNext()) {
                final Map.Entry<String, LastAck> entry2 = iterator2.next();
                rc.subscriptionCache.add(entry2.getKey());
            }
            if (rc.orderIndex.nextMessageId == 0L) {
                if (!rc.subscriptionAcks.isEmpty(tx)) {
                    iterator2 = rc.subscriptionAcks.iterator(tx);
                    while (iterator2.hasNext()) {
                        final Map.Entry<String, LastAck> entry2 = iterator2.next();
                        rc.orderIndex.nextMessageId = Math.max(rc.orderIndex.nextMessageId, entry2.getValue().lastAckedSequence + 1L);
                    }
                }
            }
            else if (!rc.messageReferences.isEmpty()) {
                final Long nextMessageId = (Long)rc.messageReferences.keySet().toArray()[rc.messageReferences.size() - 1];
                rc.orderIndex.nextMessageId = Math.max(rc.orderIndex.nextMessageId, nextMessageId);
            }
        }
        if (this.metadata.version < 5) {
            this.metadata.destinations.put(tx, key, rc);
        }
        return rc;
    }
    
    private void addAckLocation(final Transaction tx, final StoredDestination sd, final Long messageSequence, final String subscriptionKey) throws IOException {
        SequenceSet sequences = sd.ackPositions.get(tx, subscriptionKey);
        if (sequences == null) {
            sequences = new SequenceSet();
            sequences.add(messageSequence);
            sd.ackPositions.add(tx, subscriptionKey, sequences);
        }
        else {
            sequences.add(messageSequence);
            sd.ackPositions.put(tx, subscriptionKey, sequences);
        }
        Long count = sd.messageReferences.get(messageSequence);
        if (count == null) {
            count = 0L;
        }
        ++count;
        sd.messageReferences.put(messageSequence, count);
    }
    
    private void addAckLocationForRetroactiveSub(final Transaction tx, final StoredDestination sd, final String subscriptionKey) throws IOException {
        final SequenceSet allOutstanding = new SequenceSet();
        final Iterator<Map.Entry<String, SequenceSet>> iterator = sd.ackPositions.iterator(tx);
        while (iterator.hasNext()) {
            final SequenceSet set = iterator.next().getValue();
            for (final Long entry : set) {
                allOutstanding.add(entry);
            }
        }
        sd.ackPositions.put(tx, subscriptionKey, allOutstanding);
        for (final Long ackPosition : allOutstanding) {
            Long count = sd.messageReferences.get(ackPosition);
            ++count;
            sd.messageReferences.put(ackPosition, count);
        }
    }
    
    private void addAckLocationForNewMessage(final Transaction tx, final StoredDestination sd, final Long messageSequence) throws IOException {
        for (final String subscriptionKey : sd.subscriptionCache) {
            SequenceSet sequences = sd.ackPositions.get(tx, subscriptionKey);
            if (sequences == null) {
                sequences = new SequenceSet();
                sequences.add(new Sequence(messageSequence, messageSequence + 1L));
                sd.ackPositions.add(tx, subscriptionKey, sequences);
            }
            else {
                sequences.add(new Sequence(messageSequence, messageSequence + 1L));
                sd.ackPositions.put(tx, subscriptionKey, sequences);
            }
            Long count = sd.messageReferences.get(messageSequence);
            if (count == null) {
                count = 0L;
            }
            ++count;
            sd.messageReferences.put(messageSequence, count);
            sd.messageReferences.put(messageSequence + 1L, 0L);
        }
    }
    
    private void removeAckLocationsForSub(final Transaction tx, final StoredDestination sd, final String subscriptionKey) throws IOException {
        if (!sd.ackPositions.isEmpty(tx)) {
            final SequenceSet sequences = sd.ackPositions.remove(tx, subscriptionKey);
            if (sequences == null || sequences.isEmpty()) {
                return;
            }
            final ArrayList<Long> unreferenced = new ArrayList<Long>();
            for (final Long sequenceId : sequences) {
                Long references = sd.messageReferences.get(sequenceId);
                if (references != null) {
                    --references;
                    if (references > 0L) {
                        sd.messageReferences.put(sequenceId, references);
                    }
                    else {
                        sd.messageReferences.remove(sequenceId);
                        unreferenced.add(sequenceId);
                    }
                }
            }
            for (final Long sequenceId : unreferenced) {
                final ArrayList<Map.Entry<Long, MessageKeys>> deletes = new ArrayList<Map.Entry<Long, MessageKeys>>();
                sd.orderIndex.getDeleteList(tx, deletes, sequenceId);
                for (final Map.Entry<Long, MessageKeys> entry : deletes) {
                    sd.locationIndex.remove(tx, entry.getValue().location);
                    sd.messageIdIndex.remove(tx, entry.getValue().messageId);
                    sd.orderIndex.remove(tx, entry.getKey());
                }
            }
        }
    }
    
    private void removeAckLocation(final Transaction tx, final StoredDestination sd, final String subscriptionKey, final Long messageSequence) throws IOException {
        if (messageSequence != null) {
            final SequenceSet range = sd.ackPositions.get(tx, subscriptionKey);
            if (range != null && !range.isEmpty()) {
                range.remove(messageSequence);
                if (!range.isEmpty()) {
                    sd.ackPositions.put(tx, subscriptionKey, range);
                }
                else {
                    sd.ackPositions.remove(tx, subscriptionKey);
                }
                final Long count = sd.messageReferences.get(messageSequence);
                if (count != null) {
                    final long references = count - 1L;
                    if (references > 0L) {
                        sd.messageReferences.put(messageSequence, references);
                        return;
                    }
                    sd.messageReferences.remove(messageSequence);
                }
                final ArrayList<Map.Entry<Long, MessageKeys>> deletes = new ArrayList<Map.Entry<Long, MessageKeys>>();
                sd.orderIndex.getDeleteList(tx, deletes, messageSequence);
                for (final Map.Entry<Long, MessageKeys> entry : deletes) {
                    sd.locationIndex.remove(tx, entry.getValue().location);
                    sd.messageIdIndex.remove(tx, entry.getValue().messageId);
                    sd.orderIndex.remove(tx, entry.getKey());
                }
            }
        }
    }
    
    public LastAck getLastAck(final Transaction tx, final StoredDestination sd, final String subscriptionKey) throws IOException {
        return sd.subscriptionAcks.get(tx, subscriptionKey);
    }
    
    public long getStoredMessageCount(final Transaction tx, final StoredDestination sd, final String subscriptionKey) throws IOException {
        final SequenceSet messageSequences = sd.ackPositions.get(tx, subscriptionKey);
        if (messageSequences != null) {
            final long result = messageSequences.rangeSize();
            return (result > 0L) ? (result - 1L) : 0L;
        }
        return 0L;
    }
    
    private String key(final KahaDestination destination) {
        return destination.getType().getNumber() + ":" + destination.getName();
    }
    
    public void trackRecoveredAcks(final ArrayList<MessageAck> acks) {
        this.indexLock.writeLock().lock();
        try {
            for (final MessageAck ack : acks) {
                this.ackedAndPrepared.add(ack.getLastMessageId().toProducerKey());
            }
        }
        finally {
            this.indexLock.writeLock().unlock();
        }
    }
    
    public void forgetRecoveredAcks(final ArrayList<MessageAck> acks, final boolean rollback) throws IOException {
        if (acks != null) {
            this.indexLock.writeLock().lock();
            try {
                for (final MessageAck ack : acks) {
                    final String id = ack.getLastMessageId().toProducerKey();
                    this.ackedAndPrepared.remove(id);
                    if (rollback) {
                        this.rolledBackAcks.add(id);
                    }
                }
            }
            finally {
                this.indexLock.writeLock().unlock();
            }
        }
    }
    
    private List<Operation> getInflightTx(final KahaTransactionInfo info, final Location location) {
        final TransactionId key = TransactionIdConversion.convert(info);
        List<Operation> tx;
        synchronized (this.inflightTransactions) {
            tx = this.inflightTransactions.get(key);
            if (tx == null) {
                tx = (List<Operation>)Collections.synchronizedList(new ArrayList<Operation>());
                this.inflightTransactions.put(key, tx);
            }
        }
        return tx;
    }
    
    private TransactionId key(final KahaTransactionInfo transactionInfo) {
        return TransactionIdConversion.convert(transactionInfo);
    }
    
    private PageFile createPageFile() throws IOException {
        if (this.indexDirectory == null) {
            this.indexDirectory = this.directory;
        }
        IOHelper.mkdirs(this.indexDirectory);
        final PageFile index = new PageFile(this.indexDirectory, "db");
        index.setEnableWriteThread(this.isEnableIndexWriteAsync());
        index.setWriteBatchSize(this.getIndexWriteBatchSize());
        index.setPageCacheSize(this.indexCacheSize);
        index.setUseLFRUEviction(this.isUseIndexLFRUEviction());
        index.setLFUEvictionFactor(this.getIndexLFUEvictionFactor());
        index.setEnableDiskSyncs(this.isEnableIndexDiskSyncs());
        index.setEnableRecoveryFile(this.isEnableIndexRecoveryFile());
        index.setEnablePageCaching(this.isEnableIndexPageCaching());
        return index;
    }
    
    private Journal createJournal() throws IOException {
        final Journal manager = new Journal();
        manager.setDirectory(this.directory);
        manager.setMaxFileLength(this.getJournalMaxFileLength());
        manager.setCheckForCorruptionOnStartup(this.checkForCorruptJournalFiles);
        manager.setChecksum(this.checksumJournalFiles || this.checkForCorruptJournalFiles);
        manager.setWriteBatchSize(this.getJournalMaxWriteBatchSize());
        manager.setArchiveDataLogs(this.isArchiveDataLogs());
        manager.setSizeAccumulator(this.journalSize);
        manager.setEnableAsyncDiskSync(this.isEnableJournalDiskSyncs());
        if (this.getDirectoryArchive() != null) {
            IOHelper.mkdirs(this.getDirectoryArchive());
            manager.setDirectoryArchive(this.getDirectoryArchive());
        }
        return manager;
    }
    
    private Metadata createMetadata() {
        final Metadata md = new Metadata();
        md.producerSequenceIdTracker.setAuditDepth(this.getFailoverProducersAuditDepth());
        md.producerSequenceIdTracker.setMaximumNumberOfProducersToTrack(this.getMaxFailoverProducersToTrack());
        return md;
    }
    
    public int getJournalMaxWriteBatchSize() {
        return this.journalMaxWriteBatchSize;
    }
    
    public void setJournalMaxWriteBatchSize(final int journalMaxWriteBatchSize) {
        this.journalMaxWriteBatchSize = journalMaxWriteBatchSize;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public boolean isDeleteAllMessages() {
        return this.deleteAllMessages;
    }
    
    public void setDeleteAllMessages(final boolean deleteAllMessages) {
        this.deleteAllMessages = deleteAllMessages;
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
    
    public boolean isEnableJournalDiskSyncs() {
        return this.enableJournalDiskSyncs;
    }
    
    public void setEnableJournalDiskSyncs(final boolean syncWrites) {
        this.enableJournalDiskSyncs = syncWrites;
    }
    
    public long getCheckpointInterval() {
        return this.checkpointInterval;
    }
    
    public void setCheckpointInterval(final long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }
    
    public long getCleanupInterval() {
        return this.cleanupInterval;
    }
    
    public void setCleanupInterval(final long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
    
    public void setJournalMaxFileLength(final int journalMaxFileLength) {
        this.journalMaxFileLength = journalMaxFileLength;
    }
    
    public int getJournalMaxFileLength() {
        return this.journalMaxFileLength;
    }
    
    public void setMaxFailoverProducersToTrack(final int maxFailoverProducersToTrack) {
        this.metadata.producerSequenceIdTracker.setMaximumNumberOfProducersToTrack(maxFailoverProducersToTrack);
    }
    
    public int getMaxFailoverProducersToTrack() {
        return this.metadata.producerSequenceIdTracker.getMaximumNumberOfProducersToTrack();
    }
    
    public void setFailoverProducersAuditDepth(final int failoverProducersAuditDepth) {
        this.metadata.producerSequenceIdTracker.setAuditDepth(failoverProducersAuditDepth);
    }
    
    public int getFailoverProducersAuditDepth() {
        return this.metadata.producerSequenceIdTracker.getAuditDepth();
    }
    
    public PageFile getPageFile() throws IOException {
        if (this.pageFile == null) {
            this.pageFile = this.createPageFile();
        }
        return this.pageFile;
    }
    
    public Journal getJournal() throws IOException {
        if (this.journal == null) {
            this.journal = this.createJournal();
        }
        return this.journal;
    }
    
    public boolean isFailIfDatabaseIsLocked() {
        return this.failIfDatabaseIsLocked;
    }
    
    public void setFailIfDatabaseIsLocked(final boolean failIfDatabaseIsLocked) {
        this.failIfDatabaseIsLocked = failIfDatabaseIsLocked;
    }
    
    public boolean isIgnoreMissingJournalfiles() {
        return this.ignoreMissingJournalfiles;
    }
    
    public void setIgnoreMissingJournalfiles(final boolean ignoreMissingJournalfiles) {
        this.ignoreMissingJournalfiles = ignoreMissingJournalfiles;
    }
    
    public int getIndexCacheSize() {
        return this.indexCacheSize;
    }
    
    public void setIndexCacheSize(final int indexCacheSize) {
        this.indexCacheSize = indexCacheSize;
    }
    
    public boolean isCheckForCorruptJournalFiles() {
        return this.checkForCorruptJournalFiles;
    }
    
    public void setCheckForCorruptJournalFiles(final boolean checkForCorruptJournalFiles) {
        this.checkForCorruptJournalFiles = checkForCorruptJournalFiles;
    }
    
    public boolean isChecksumJournalFiles() {
        return this.checksumJournalFiles;
    }
    
    public void setChecksumJournalFiles(final boolean checksumJournalFiles) {
        this.checksumJournalFiles = checksumJournalFiles;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    public boolean isArchiveDataLogs() {
        return this.archiveDataLogs;
    }
    
    public void setArchiveDataLogs(final boolean archiveDataLogs) {
        this.archiveDataLogs = archiveDataLogs;
    }
    
    public File getDirectoryArchive() {
        return this.directoryArchive;
    }
    
    public void setDirectoryArchive(final File directoryArchive) {
        this.directoryArchive = directoryArchive;
    }
    
    public boolean isArchiveCorruptedIndex() {
        return this.archiveCorruptedIndex;
    }
    
    public void setArchiveCorruptedIndex(final boolean archiveCorruptedIndex) {
        this.archiveCorruptedIndex = archiveCorruptedIndex;
    }
    
    public float getIndexLFUEvictionFactor() {
        return this.indexLFUEvictionFactor;
    }
    
    public void setIndexLFUEvictionFactor(final float indexLFUEvictionFactor) {
        this.indexLFUEvictionFactor = indexLFUEvictionFactor;
    }
    
    public boolean isUseIndexLFRUEviction() {
        return this.useIndexLFRUEviction;
    }
    
    public void setUseIndexLFRUEviction(final boolean useIndexLFRUEviction) {
        this.useIndexLFRUEviction = useIndexLFRUEviction;
    }
    
    public void setEnableIndexDiskSyncs(final boolean enableIndexDiskSyncs) {
        this.enableIndexDiskSyncs = enableIndexDiskSyncs;
    }
    
    public void setEnableIndexRecoveryFile(final boolean enableIndexRecoveryFile) {
        this.enableIndexRecoveryFile = enableIndexRecoveryFile;
    }
    
    public void setEnableIndexPageCaching(final boolean enableIndexPageCaching) {
        this.enableIndexPageCaching = enableIndexPageCaching;
    }
    
    public boolean isEnableIndexDiskSyncs() {
        return this.enableIndexDiskSyncs;
    }
    
    public boolean isEnableIndexRecoveryFile() {
        return this.enableIndexRecoveryFile;
    }
    
    public boolean isEnableIndexPageCaching() {
        return this.enableIndexPageCaching;
    }
    
    public File getIndexDirectory() {
        return this.indexDirectory;
    }
    
    public void setIndexDirectory(final File indexDirectory) {
        this.indexDirectory = indexDirectory;
    }
    
    static {
        LOG_SLOW_ACCESS_TIME = Integer.getInteger("org.apache.activemq.store.kahadb.LOG_SLOW_ACCESS_TIME", 0);
        DEFAULT_DIRECTORY = new File("KahaDB");
        UNMATCHED = new Buffer(new byte[0]);
        LOG = LoggerFactory.getLogger(MessageDatabase.class);
    }
    
    protected class Metadata
    {
        protected Page<Metadata> page;
        protected int state;
        protected BTreeIndex<String, StoredDestination> destinations;
        protected Location lastUpdate;
        protected Location firstInProgressTransactionLocation;
        protected Location producerSequenceIdTrackerLocation;
        protected Location ackMessageFileMapLocation;
        protected transient ActiveMQMessageAuditNoSync producerSequenceIdTracker;
        protected transient Map<Integer, Set<Integer>> ackMessageFileMap;
        protected int version;
        protected int openwireVersion;
        
        protected Metadata() {
            this.producerSequenceIdTrackerLocation = null;
            this.ackMessageFileMapLocation = null;
            this.producerSequenceIdTracker = new ActiveMQMessageAuditNoSync();
            this.ackMessageFileMap = new HashMap<Integer, Set<Integer>>();
            this.version = 5;
            this.openwireVersion = 6;
        }
        
        public void read(final DataInput is) throws IOException {
            this.state = is.readInt();
            this.destinations = new BTreeIndex<String, StoredDestination>(MessageDatabase.this.pageFile, is.readLong());
            if (is.readBoolean()) {
                this.lastUpdate = LocationMarshaller.INSTANCE.readPayload(is);
            }
            else {
                this.lastUpdate = null;
            }
            if (is.readBoolean()) {
                this.firstInProgressTransactionLocation = LocationMarshaller.INSTANCE.readPayload(is);
            }
            else {
                this.firstInProgressTransactionLocation = null;
            }
            try {
                if (is.readBoolean()) {
                    this.producerSequenceIdTrackerLocation = LocationMarshaller.INSTANCE.readPayload(is);
                }
                else {
                    this.producerSequenceIdTrackerLocation = null;
                }
            }
            catch (EOFException ex) {}
            try {
                this.version = is.readInt();
            }
            catch (EOFException expectedOnUpgrade) {
                this.version = 1;
            }
            if (this.version >= 5 && is.readBoolean()) {
                this.ackMessageFileMapLocation = LocationMarshaller.INSTANCE.readPayload(is);
            }
            else {
                this.ackMessageFileMapLocation = null;
            }
            try {
                this.openwireVersion = is.readInt();
            }
            catch (EOFException expectedOnUpgrade) {
                this.openwireVersion = 6;
            }
            MessageDatabase.LOG.info("KahaDB is version " + this.version);
        }
        
        public void write(final DataOutput os) throws IOException {
            os.writeInt(this.state);
            os.writeLong(this.destinations.getPageId());
            if (this.lastUpdate != null) {
                os.writeBoolean(true);
                LocationMarshaller.INSTANCE.writePayload(this.lastUpdate, os);
            }
            else {
                os.writeBoolean(false);
            }
            if (this.firstInProgressTransactionLocation != null) {
                os.writeBoolean(true);
                LocationMarshaller.INSTANCE.writePayload(this.firstInProgressTransactionLocation, os);
            }
            else {
                os.writeBoolean(false);
            }
            if (this.producerSequenceIdTrackerLocation != null) {
                os.writeBoolean(true);
                LocationMarshaller.INSTANCE.writePayload(this.producerSequenceIdTrackerLocation, os);
            }
            else {
                os.writeBoolean(false);
            }
            os.writeInt(5);
            if (this.ackMessageFileMapLocation != null) {
                os.writeBoolean(true);
                LocationMarshaller.INSTANCE.writePayload(this.ackMessageFileMapLocation, os);
            }
            else {
                os.writeBoolean(false);
            }
            os.writeInt(this.openwireVersion);
        }
    }
    
    class MetadataMarshaller extends VariableMarshaller<Metadata>
    {
        @Override
        public Metadata readPayload(final DataInput dataIn) throws IOException {
            final Metadata rc = MessageDatabase.this.createMetadata();
            rc.read(dataIn);
            return rc;
        }
        
        @Override
        public void writePayload(final Metadata object, final DataOutput dataOut) throws IOException {
            object.write(dataOut);
        }
    }
    
    class TranInfo
    {
        TransactionId id;
        Location location;
        HashMap<KahaDestination, opCount> destinationOpCount;
        
        TranInfo() {
            this.destinationOpCount = new HashMap<KahaDestination, opCount>();
        }
        
        public void track(final Operation operation) {
            if (this.location == null) {
                this.location = operation.getLocation();
            }
            boolean isAdd = false;
            KahaDestination destination;
            if (operation instanceof AddOpperation) {
                final AddOpperation add = (AddOpperation)operation;
                destination = add.getCommand().getDestination();
                isAdd = true;
            }
            else {
                final RemoveOpperation removeOpperation = (RemoveOpperation)operation;
                destination = removeOpperation.getCommand().getDestination();
            }
            opCount opCount = this.destinationOpCount.get(destination);
            if (opCount == null) {
                opCount = new opCount();
                this.destinationOpCount.put(destination, opCount);
            }
            if (isAdd) {
                final opCount opCount2 = opCount;
                ++opCount2.add;
            }
            else {
                final opCount opCount3 = opCount;
                ++opCount3.remove;
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.location).append(";").append(this.id).append(";\n");
            for (final Map.Entry<KahaDestination, opCount> op : this.destinationOpCount.entrySet()) {
                buffer.append(op.getKey()).append('+').append(op.getValue().add).append(',').append('-').append(op.getValue().remove).append(';');
            }
            return buffer.toString();
        }
        
        class opCount
        {
            int add;
            int remove;
        }
    }
    
    static class MessageKeys
    {
        final String messageId;
        final Location location;
        
        public MessageKeys(final String messageId, final Location location) {
            this.messageId = messageId;
            this.location = location;
        }
        
        @Override
        public String toString() {
            return "[" + this.messageId + "," + this.location + "]";
        }
    }
    
    protected static class MessageKeysMarshaller extends VariableMarshaller<MessageKeys>
    {
        static final MessageKeysMarshaller INSTANCE;
        
        @Override
        public MessageKeys readPayload(final DataInput dataIn) throws IOException {
            return new MessageKeys(dataIn.readUTF(), LocationMarshaller.INSTANCE.readPayload(dataIn));
        }
        
        @Override
        public void writePayload(final MessageKeys object, final DataOutput dataOut) throws IOException {
            dataOut.writeUTF(object.messageId);
            LocationMarshaller.INSTANCE.writePayload(object.location, dataOut);
        }
        
        static {
            INSTANCE = new MessageKeysMarshaller();
        }
    }
    
    class LastAck
    {
        long lastAckedSequence;
        byte priority;
        
        public LastAck(final LastAck source) {
            this.lastAckedSequence = source.lastAckedSequence;
            this.priority = source.priority;
        }
        
        public LastAck() {
            this.priority = 9;
        }
        
        public LastAck(final long ackLocation) {
            this.lastAckedSequence = ackLocation;
            this.priority = 0;
        }
        
        public LastAck(final long ackLocation, final byte priority) {
            this.lastAckedSequence = ackLocation;
            this.priority = priority;
        }
        
        @Override
        public String toString() {
            return "[" + this.lastAckedSequence + ":" + this.priority + "]";
        }
    }
    
    protected class LastAckMarshaller implements Marshaller<LastAck>
    {
        @Override
        public void writePayload(final LastAck object, final DataOutput dataOut) throws IOException {
            dataOut.writeLong(object.lastAckedSequence);
            dataOut.writeByte(object.priority);
        }
        
        @Override
        public LastAck readPayload(final DataInput dataIn) throws IOException {
            final LastAck lastAcked = new LastAck();
            lastAcked.lastAckedSequence = dataIn.readLong();
            if (MessageDatabase.this.metadata.version >= 3) {
                lastAcked.priority = dataIn.readByte();
            }
            return lastAcked;
        }
        
        @Override
        public int getFixedSize() {
            return 9;
        }
        
        @Override
        public LastAck deepCopy(final LastAck source) {
            return new LastAck(source);
        }
        
        @Override
        public boolean isDeepCopySupported() {
            return true;
        }
    }
    
    class StoredDestination
    {
        MessageOrderIndex orderIndex;
        BTreeIndex<Location, Long> locationIndex;
        BTreeIndex<String, Long> messageIdIndex;
        BTreeIndex<String, KahaSubscriptionCommand> subscriptions;
        BTreeIndex<String, LastAck> subscriptionAcks;
        HashMap<String, MessageOrderCursor> subscriptionCursors;
        ListIndex<String, SequenceSet> ackPositions;
        ListIndex<String, Location> subLocations;
        final TreeMap<Long, Long> messageReferences;
        final HashSet<String> subscriptionCache;
        
        StoredDestination() {
            this.orderIndex = new MessageOrderIndex();
            this.messageReferences = new TreeMap<Long, Long>();
            this.subscriptionCache = new LinkedHashSet<String>();
        }
    }
    
    protected class StoredDestinationMarshaller extends VariableMarshaller<StoredDestination>
    {
        @Override
        public StoredDestination readPayload(final DataInput dataIn) throws IOException {
            final StoredDestination value = new StoredDestination();
            value.orderIndex.defaultPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, dataIn.readLong());
            value.locationIndex = new BTreeIndex<Location, Long>(MessageDatabase.this.pageFile, dataIn.readLong());
            value.messageIdIndex = new BTreeIndex<String, Long>(MessageDatabase.this.pageFile, dataIn.readLong());
            if (dataIn.readBoolean()) {
                value.subscriptions = new BTreeIndex<String, KahaSubscriptionCommand>(MessageDatabase.this.pageFile, dataIn.readLong());
                value.subscriptionAcks = new BTreeIndex<String, LastAck>(MessageDatabase.this.pageFile, dataIn.readLong());
                if (MessageDatabase.this.metadata.version >= 4) {
                    value.ackPositions = new ListIndex<String, SequenceSet>(MessageDatabase.this.pageFile, dataIn.readLong());
                }
                else {
                    MessageDatabase.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            final LinkedHashMap<String, SequenceSet> temp = new LinkedHashMap<String, SequenceSet>();
                            if (MessageDatabase.this.metadata.version >= 3) {
                                final BTreeIndex<Long, HashSet<String>> oldAckPositions = new BTreeIndex<Long, HashSet<String>>(MessageDatabase.this.pageFile, dataIn.readLong());
                                oldAckPositions.setKeyMarshaller(LongMarshaller.INSTANCE);
                                oldAckPositions.setValueMarshaller(HashSetStringMarshaller.INSTANCE);
                                oldAckPositions.load(tx);
                                final Iterator<Map.Entry<Long, HashSet<String>>> iterator = oldAckPositions.iterator(tx);
                                while (iterator.hasNext()) {
                                    final Map.Entry<Long, HashSet<String>> entry = iterator.next();
                                    for (final String subKey : entry.getValue()) {
                                        SequenceSet pendingAcks = temp.get(subKey);
                                        if (pendingAcks == null) {
                                            pendingAcks = new SequenceSet();
                                            temp.put(subKey, pendingAcks);
                                        }
                                        pendingAcks.add(entry.getKey());
                                    }
                                }
                            }
                            (value.ackPositions = new ListIndex<String, SequenceSet>(MessageDatabase.this.pageFile, tx.allocate())).setKeyMarshaller(StringMarshaller.INSTANCE);
                            value.ackPositions.setValueMarshaller(SequenceSet.Marshaller.INSTANCE);
                            value.ackPositions.load(tx);
                            for (final String subscriptionKey : temp.keySet()) {
                                value.ackPositions.put(tx, subscriptionKey, temp.get(subscriptionKey));
                            }
                        }
                    });
                }
                if (MessageDatabase.this.metadata.version >= 5) {
                    value.subLocations = new ListIndex<String, Location>(MessageDatabase.this.pageFile, dataIn.readLong());
                }
                else {
                    MessageDatabase.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            (value.subLocations = new ListIndex<String, Location>(MessageDatabase.this.pageFile, tx.allocate())).setKeyMarshaller(StringMarshaller.INSTANCE);
                            value.subLocations.setValueMarshaller(LocationMarshaller.INSTANCE);
                            value.subLocations.load(tx);
                        }
                    });
                }
            }
            if (MessageDatabase.this.metadata.version >= 2) {
                value.orderIndex.lowPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, dataIn.readLong());
                value.orderIndex.highPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, dataIn.readLong());
            }
            else {
                MessageDatabase.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        (value.orderIndex.lowPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, tx.allocate())).setKeyMarshaller(LongMarshaller.INSTANCE);
                        value.orderIndex.lowPriorityIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
                        value.orderIndex.lowPriorityIndex.load(tx);
                        (value.orderIndex.highPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, tx.allocate())).setKeyMarshaller(LongMarshaller.INSTANCE);
                        value.orderIndex.highPriorityIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
                        value.orderIndex.highPriorityIndex.load(tx);
                    }
                });
            }
            return value;
        }
        
        @Override
        public void writePayload(final StoredDestination value, final DataOutput dataOut) throws IOException {
            dataOut.writeLong(value.orderIndex.defaultPriorityIndex.getPageId());
            dataOut.writeLong(value.locationIndex.getPageId());
            dataOut.writeLong(value.messageIdIndex.getPageId());
            if (value.subscriptions != null) {
                dataOut.writeBoolean(true);
                dataOut.writeLong(value.subscriptions.getPageId());
                dataOut.writeLong(value.subscriptionAcks.getPageId());
                dataOut.writeLong(value.ackPositions.getHeadPageId());
                dataOut.writeLong(value.subLocations.getHeadPageId());
            }
            else {
                dataOut.writeBoolean(false);
            }
            dataOut.writeLong(value.orderIndex.lowPriorityIndex.getPageId());
            dataOut.writeLong(value.orderIndex.highPriorityIndex.getPageId());
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
    
    abstract class Operation<T extends JournalCommand<T>>
    {
        final T command;
        final Location location;
        
        public Operation(final T command, final Location location) {
            this.command = command;
            this.location = location;
        }
        
        public Location getLocation() {
            return this.location;
        }
        
        public T getCommand() {
            return this.command;
        }
        
        public abstract void execute(final Transaction p0) throws IOException;
    }
    
    class AddOpperation extends Operation<KahaAddMessageCommand>
    {
        public AddOpperation(final KahaAddMessageCommand command, final Location location) {
            super(command, location);
        }
        
        @Override
        public void execute(final Transaction tx) throws IOException {
            MessageDatabase.this.updateIndex(tx, (KahaAddMessageCommand)this.command, this.location);
        }
    }
    
    class RemoveOpperation extends Operation<KahaRemoveMessageCommand>
    {
        public RemoveOpperation(final KahaRemoveMessageCommand command, final Location location) {
            super(command, location);
        }
        
        @Override
        public void execute(final Transaction tx) throws IOException {
            MessageDatabase.this.updateIndex(tx, (KahaRemoveMessageCommand)this.command, this.location);
        }
    }
    
    class MessageOrderCursor
    {
        long defaultCursorPosition;
        long lowPriorityCursorPosition;
        long highPriorityCursorPosition;
        
        MessageOrderCursor() {
        }
        
        MessageOrderCursor(final long position) {
            this.defaultCursorPosition = position;
            this.lowPriorityCursorPosition = position;
            this.highPriorityCursorPosition = position;
        }
        
        MessageOrderCursor(final MessageOrderCursor other) {
            this.defaultCursorPosition = other.defaultCursorPosition;
            this.lowPriorityCursorPosition = other.lowPriorityCursorPosition;
            this.highPriorityCursorPosition = other.highPriorityCursorPosition;
        }
        
        MessageOrderCursor copy() {
            return new MessageOrderCursor(this);
        }
        
        void reset() {
            this.defaultCursorPosition = 0L;
            this.highPriorityCursorPosition = 0L;
            this.lowPriorityCursorPosition = 0L;
        }
        
        void increment() {
            if (this.defaultCursorPosition != 0L) {
                ++this.defaultCursorPosition;
            }
            if (this.highPriorityCursorPosition != 0L) {
                ++this.highPriorityCursorPosition;
            }
            if (this.lowPriorityCursorPosition != 0L) {
                ++this.lowPriorityCursorPosition;
            }
        }
        
        @Override
        public String toString() {
            return "MessageOrderCursor:[def:" + this.defaultCursorPosition + ", low:" + this.lowPriorityCursorPosition + ", high:" + this.highPriorityCursorPosition + "]";
        }
        
        public void sync(final MessageOrderCursor other) {
            this.defaultCursorPosition = other.defaultCursorPosition;
            this.lowPriorityCursorPosition = other.lowPriorityCursorPosition;
            this.highPriorityCursorPosition = other.highPriorityCursorPosition;
        }
    }
    
    class MessageOrderIndex
    {
        static final byte HI = 9;
        static final byte LO = 0;
        static final byte DEF = 4;
        long nextMessageId;
        BTreeIndex<Long, MessageKeys> defaultPriorityIndex;
        BTreeIndex<Long, MessageKeys> lowPriorityIndex;
        BTreeIndex<Long, MessageKeys> highPriorityIndex;
        MessageOrderCursor cursor;
        Long lastDefaultKey;
        Long lastHighKey;
        Long lastLowKey;
        byte lastGetPriority;
        
        MessageOrderIndex() {
            this.cursor = new MessageOrderCursor();
        }
        
        MessageKeys remove(final Transaction tx, final Long key) throws IOException {
            MessageKeys result = this.defaultPriorityIndex.remove(tx, key);
            if (result == null && this.highPriorityIndex != null) {
                result = this.highPriorityIndex.remove(tx, key);
                if (result == null && this.lowPriorityIndex != null) {
                    result = this.lowPriorityIndex.remove(tx, key);
                }
            }
            return result;
        }
        
        void load(final Transaction tx) throws IOException {
            this.defaultPriorityIndex.setKeyMarshaller(LongMarshaller.INSTANCE);
            this.defaultPriorityIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
            this.defaultPriorityIndex.load(tx);
            this.lowPriorityIndex.setKeyMarshaller(LongMarshaller.INSTANCE);
            this.lowPriorityIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
            this.lowPriorityIndex.load(tx);
            this.highPriorityIndex.setKeyMarshaller(LongMarshaller.INSTANCE);
            this.highPriorityIndex.setValueMarshaller(MessageKeysMarshaller.INSTANCE);
            this.highPriorityIndex.load(tx);
        }
        
        void allocate(final Transaction tx) throws IOException {
            this.defaultPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, tx.allocate());
            if (MessageDatabase.this.metadata.version >= 2) {
                this.lowPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, tx.allocate());
                this.highPriorityIndex = new BTreeIndex<Long, MessageKeys>(MessageDatabase.this.pageFile, tx.allocate());
            }
        }
        
        void configureLast(final Transaction tx) throws IOException {
            if (this.highPriorityIndex != null) {
                Map.Entry<Long, MessageKeys> lastEntry = this.highPriorityIndex.getLast(tx);
                if (lastEntry != null) {
                    this.nextMessageId = lastEntry.getKey() + 1L;
                }
                else {
                    lastEntry = this.defaultPriorityIndex.getLast(tx);
                    if (lastEntry != null) {
                        this.nextMessageId = lastEntry.getKey() + 1L;
                    }
                    else {
                        lastEntry = this.lowPriorityIndex.getLast(tx);
                        if (lastEntry != null) {
                            this.nextMessageId = lastEntry.getKey() + 1L;
                        }
                    }
                }
            }
            else {
                final Map.Entry<Long, MessageKeys> lastEntry = this.defaultPriorityIndex.getLast(tx);
                if (lastEntry != null) {
                    this.nextMessageId = lastEntry.getKey() + 1L;
                }
            }
        }
        
        void clear(final Transaction tx) throws IOException {
            this.remove(tx);
            this.resetCursorPosition();
            this.allocate(tx);
            this.load(tx);
            this.configureLast(tx);
        }
        
        void remove(final Transaction tx) throws IOException {
            this.defaultPriorityIndex.clear(tx);
            this.defaultPriorityIndex.unload(tx);
            tx.free(this.defaultPriorityIndex.getPageId());
            if (this.lowPriorityIndex != null) {
                this.lowPriorityIndex.clear(tx);
                this.lowPriorityIndex.unload(tx);
                tx.free(this.lowPriorityIndex.getPageId());
            }
            if (this.highPriorityIndex != null) {
                this.highPriorityIndex.clear(tx);
                this.highPriorityIndex.unload(tx);
                tx.free(this.highPriorityIndex.getPageId());
            }
        }
        
        void resetCursorPosition() {
            this.cursor.reset();
            this.lastDefaultKey = null;
            this.lastHighKey = null;
            this.lastLowKey = null;
        }
        
        void setBatch(final Transaction tx, final Long sequence) throws IOException {
            if (sequence != null) {
                final Long nextPosition = new Long(sequence + 1L);
                if (this.defaultPriorityIndex.containsKey(tx, sequence)) {
                    this.lastDefaultKey = sequence;
                    this.cursor.defaultCursorPosition = nextPosition;
                }
                else if (this.highPriorityIndex != null) {
                    if (this.highPriorityIndex.containsKey(tx, sequence)) {
                        this.lastHighKey = sequence;
                        this.cursor.highPriorityCursorPosition = nextPosition;
                    }
                    else if (this.lowPriorityIndex.containsKey(tx, sequence)) {
                        this.lastLowKey = sequence;
                        this.cursor.lowPriorityCursorPosition = nextPosition;
                    }
                }
                else {
                    MessageDatabase.LOG.warn("setBatch: sequence " + sequence + " not found in orderindex:" + this);
                    this.lastDefaultKey = sequence;
                    this.cursor.defaultCursorPosition = nextPosition;
                }
            }
        }
        
        void setBatch(final Transaction tx, final LastAck last) throws IOException {
            this.setBatch(tx, last.lastAckedSequence);
            if (this.cursor.defaultCursorPosition == 0L && this.cursor.highPriorityCursorPosition == 0L && this.cursor.lowPriorityCursorPosition == 0L) {
                final long next = last.lastAckedSequence + 1L;
                switch (last.priority) {
                    case 4: {
                        this.cursor.defaultCursorPosition = next;
                        this.cursor.highPriorityCursorPosition = next;
                        break;
                    }
                    case 9: {
                        this.cursor.highPriorityCursorPosition = next;
                        break;
                    }
                    case 0: {
                        this.cursor.lowPriorityCursorPosition = next;
                        this.cursor.defaultCursorPosition = next;
                        this.cursor.highPriorityCursorPosition = next;
                        break;
                    }
                }
            }
        }
        
        void stoppedIterating() {
            if (this.lastDefaultKey != null) {
                this.cursor.defaultCursorPosition = this.lastDefaultKey + 1L;
            }
            if (this.lastHighKey != null) {
                this.cursor.highPriorityCursorPosition = this.lastHighKey + 1L;
            }
            if (this.lastLowKey != null) {
                this.cursor.lowPriorityCursorPosition = this.lastLowKey + 1L;
            }
            this.lastDefaultKey = null;
            this.lastHighKey = null;
            this.lastLowKey = null;
        }
        
        void getDeleteList(final Transaction tx, final ArrayList<Map.Entry<Long, MessageKeys>> deletes, final Long sequenceId) throws IOException {
            if (this.defaultPriorityIndex.containsKey(tx, sequenceId)) {
                this.getDeleteList(tx, deletes, this.defaultPriorityIndex, sequenceId);
            }
            else if (this.highPriorityIndex != null && this.highPriorityIndex.containsKey(tx, sequenceId)) {
                this.getDeleteList(tx, deletes, this.highPriorityIndex, sequenceId);
            }
            else if (this.lowPriorityIndex != null && this.lowPriorityIndex.containsKey(tx, sequenceId)) {
                this.getDeleteList(tx, deletes, this.lowPriorityIndex, sequenceId);
            }
        }
        
        void getDeleteList(final Transaction tx, final ArrayList<Map.Entry<Long, MessageKeys>> deletes, final BTreeIndex<Long, MessageKeys> index, final Long sequenceId) throws IOException {
            final Iterator<Map.Entry<Long, MessageKeys>> iterator = index.iterator(tx, sequenceId);
            deletes.add(iterator.next());
        }
        
        long getNextMessageId(final int priority) {
            return this.nextMessageId++;
        }
        
        MessageKeys get(final Transaction tx, final Long key) throws IOException {
            MessageKeys result = this.defaultPriorityIndex.get(tx, key);
            if (result == null) {
                result = this.highPriorityIndex.get(tx, key);
                if (result == null) {
                    result = this.lowPriorityIndex.get(tx, key);
                    this.lastGetPriority = 0;
                }
                else {
                    this.lastGetPriority = 9;
                }
            }
            else {
                this.lastGetPriority = 4;
            }
            return result;
        }
        
        MessageKeys put(final Transaction tx, final int priority, final Long key, final MessageKeys value) throws IOException {
            if (priority == 4) {
                return this.defaultPriorityIndex.put(tx, key, value);
            }
            if (priority > 4) {
                return this.highPriorityIndex.put(tx, key, value);
            }
            return this.lowPriorityIndex.put(tx, key, value);
        }
        
        Iterator<Map.Entry<Long, MessageKeys>> iterator(final Transaction tx) throws IOException {
            return new MessageOrderIterator(tx, this.cursor);
        }
        
        Iterator<Map.Entry<Long, MessageKeys>> iterator(final Transaction tx, final MessageOrderCursor m) throws IOException {
            return new MessageOrderIterator(tx, m);
        }
        
        public byte lastGetPriority() {
            return this.lastGetPriority;
        }
        
        public boolean alreadyDispatched(final Long sequence) {
            return (this.cursor.highPriorityCursorPosition > 0L && this.cursor.highPriorityCursorPosition >= sequence) || (this.cursor.defaultCursorPosition > 0L && this.cursor.defaultCursorPosition >= sequence) || (this.cursor.lowPriorityCursorPosition > 0L && this.cursor.lowPriorityCursorPosition >= sequence);
        }
        
        class MessageOrderIterator implements Iterator<Map.Entry<Long, MessageKeys>>
        {
            Iterator<Map.Entry<Long, MessageKeys>> currentIterator;
            final Iterator<Map.Entry<Long, MessageKeys>> highIterator;
            final Iterator<Map.Entry<Long, MessageKeys>> defaultIterator;
            final Iterator<Map.Entry<Long, MessageKeys>> lowIterator;
            
            MessageOrderIterator(final Transaction tx, final MessageOrderCursor m) throws IOException {
                this.defaultIterator = MessageOrderIndex.this.defaultPriorityIndex.iterator(tx, m.defaultCursorPosition);
                if (MessageOrderIndex.this.highPriorityIndex != null) {
                    this.highIterator = MessageOrderIndex.this.highPriorityIndex.iterator(tx, m.highPriorityCursorPosition);
                }
                else {
                    this.highIterator = null;
                }
                if (MessageOrderIndex.this.lowPriorityIndex != null) {
                    this.lowIterator = MessageOrderIndex.this.lowPriorityIndex.iterator(tx, m.lowPriorityCursorPosition);
                }
                else {
                    this.lowIterator = null;
                }
            }
            
            @Override
            public boolean hasNext() {
                if (this.currentIterator != null) {
                    if (this.highIterator != null) {
                        if (this.currentIterator.hasNext()) {
                            return true;
                        }
                        if (this.currentIterator == this.highIterator) {
                            if (this.defaultIterator.hasNext()) {
                                this.currentIterator = this.defaultIterator;
                                return this.currentIterator.hasNext();
                            }
                            if (this.lowIterator.hasNext()) {
                                this.currentIterator = this.lowIterator;
                                return this.currentIterator.hasNext();
                            }
                            return false;
                        }
                        else if (this.currentIterator == this.defaultIterator) {
                            if (this.lowIterator.hasNext()) {
                                this.currentIterator = this.lowIterator;
                                return this.currentIterator.hasNext();
                            }
                            return false;
                        }
                    }
                    return this.currentIterator.hasNext();
                }
                if (this.highIterator == null) {
                    this.currentIterator = this.defaultIterator;
                    return this.currentIterator.hasNext();
                }
                if (this.highIterator.hasNext()) {
                    this.currentIterator = this.highIterator;
                    return this.currentIterator.hasNext();
                }
                if (this.defaultIterator.hasNext()) {
                    this.currentIterator = this.defaultIterator;
                    return this.currentIterator.hasNext();
                }
                if (this.lowIterator.hasNext()) {
                    this.currentIterator = this.lowIterator;
                    return this.currentIterator.hasNext();
                }
                return false;
            }
            
            @Override
            public Map.Entry<Long, MessageKeys> next() {
                final Map.Entry<Long, MessageKeys> result = this.currentIterator.next();
                if (result != null) {
                    final Long key = result.getKey();
                    if (this.highIterator != null) {
                        if (this.currentIterator == this.defaultIterator) {
                            MessageOrderIndex.this.lastDefaultKey = key;
                        }
                        else if (this.currentIterator == this.highIterator) {
                            MessageOrderIndex.this.lastHighKey = key;
                        }
                        else {
                            MessageOrderIndex.this.lastLowKey = key;
                        }
                    }
                    else {
                        MessageOrderIndex.this.lastDefaultKey = key;
                    }
                }
                return result;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
    
    private static class HashSetStringMarshaller extends VariableMarshaller<HashSet<String>>
    {
        static final HashSetStringMarshaller INSTANCE;
        
        @Override
        public void writePayload(final HashSet<String> object, final DataOutput dataOut) throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oout = new ObjectOutputStream(baos);
            oout.writeObject(object);
            oout.flush();
            oout.close();
            final byte[] data = baos.toByteArray();
            dataOut.writeInt(data.length);
            dataOut.write(data);
        }
        
        @Override
        public HashSet<String> readPayload(final DataInput dataIn) throws IOException {
            final int dataLen = dataIn.readInt();
            final byte[] data = new byte[dataLen];
            dataIn.readFully(data);
            final ByteArrayInputStream bais = new ByteArrayInputStream(data);
            final ObjectInputStream oin = new ObjectInputStream(bais);
            try {
                return (HashSet<String>)oin.readObject();
            }
            catch (ClassNotFoundException cfe) {
                final IOException ioe = new IOException("Failed to read HashSet<String>: " + cfe);
                ioe.initCause(cfe);
                throw ioe;
            }
        }
        
        static {
            INSTANCE = new HashSetStringMarshaller();
        }
    }
}
