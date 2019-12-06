// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.journal;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ProducerId;
import org.apache.activeio.packet.ByteArrayPacket;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.command.JournalTrace;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.JournalTransaction;
import org.apache.activemq.command.JournalTopicAck;
import org.apache.activemq.command.JournalQueueAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.NonCachedMessageEvaluationContext;
import org.apache.activeio.packet.Packet;
import org.apache.activeio.journal.InvalidRecordLocationException;
import org.apache.activemq.command.DataStructure;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.ArrayList;
import org.apache.activeio.journal.RecordLocation;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.concurrent.ExecutorService;
import org.apache.activemq.util.ThreadPoolUtils;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.thread.Task;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.store.MessageStore;
import java.util.Collection;
import java.util.HashSet;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import java.io.IOException;
import org.apache.activemq.openwire.OpenWireFormat;
import java.io.File;
import org.apache.activemq.thread.TaskRunnerFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.thread.TaskRunner;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activeio.journal.Journal;
import org.slf4j.Logger;
import org.apache.activemq.thread.Scheduler;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.usage.UsageListener;
import org.apache.activeio.journal.JournalEventListener;
import org.apache.activemq.store.PersistenceAdapter;

public class JournalPersistenceAdapter implements PersistenceAdapter, JournalEventListener, UsageListener, BrokerServiceAware
{
    private BrokerService brokerService;
    protected Scheduler scheduler;
    private static final Logger LOG;
    private Journal journal;
    private PersistenceAdapter longTermPersistence;
    private final WireFormat wireFormat;
    private final ConcurrentHashMap<ActiveMQQueue, JournalMessageStore> queues;
    private final ConcurrentHashMap<ActiveMQTopic, JournalTopicMessageStore> topics;
    private SystemUsage usageManager;
    private long checkpointInterval;
    private long lastCheckpointRequest;
    private long lastCleanup;
    private int maxCheckpointWorkers;
    private int maxCheckpointMessageAddSize;
    private final JournalTransactionStore transactionStore;
    private ThreadPoolExecutor checkpointExecutor;
    private TaskRunner checkpointTask;
    private CountDownLatch nextCheckpointCountDownLatch;
    private boolean fullCheckPoint;
    private final AtomicBoolean started;
    private final Runnable periodicCheckpointTask;
    private TaskRunnerFactory taskRunnerFactory;
    private File directory;
    
    public JournalPersistenceAdapter() {
        this.wireFormat = new OpenWireFormat();
        this.queues = new ConcurrentHashMap<ActiveMQQueue, JournalMessageStore>();
        this.topics = new ConcurrentHashMap<ActiveMQTopic, JournalTopicMessageStore>();
        this.checkpointInterval = 300000L;
        this.lastCheckpointRequest = System.currentTimeMillis();
        this.lastCleanup = System.currentTimeMillis();
        this.maxCheckpointWorkers = 10;
        this.maxCheckpointMessageAddSize = 1048576;
        this.transactionStore = new JournalTransactionStore(this);
        this.nextCheckpointCountDownLatch = new CountDownLatch(1);
        this.started = new AtomicBoolean(false);
        this.periodicCheckpointTask = this.createPeriodicCheckpointTask();
    }
    
    public JournalPersistenceAdapter(final Journal journal, final PersistenceAdapter longTermPersistence, final TaskRunnerFactory taskRunnerFactory) throws IOException {
        this.wireFormat = new OpenWireFormat();
        this.queues = new ConcurrentHashMap<ActiveMQQueue, JournalMessageStore>();
        this.topics = new ConcurrentHashMap<ActiveMQTopic, JournalTopicMessageStore>();
        this.checkpointInterval = 300000L;
        this.lastCheckpointRequest = System.currentTimeMillis();
        this.lastCleanup = System.currentTimeMillis();
        this.maxCheckpointWorkers = 10;
        this.maxCheckpointMessageAddSize = 1048576;
        this.transactionStore = new JournalTransactionStore(this);
        this.nextCheckpointCountDownLatch = new CountDownLatch(1);
        this.started = new AtomicBoolean(false);
        this.periodicCheckpointTask = this.createPeriodicCheckpointTask();
        this.setJournal(journal);
        this.setTaskRunnerFactory(taskRunnerFactory);
        this.setPersistenceAdapter(longTermPersistence);
    }
    
    public void setTaskRunnerFactory(final TaskRunnerFactory taskRunnerFactory) {
        this.taskRunnerFactory = taskRunnerFactory;
    }
    
    public void setJournal(final Journal journal) {
        (this.journal = journal).setJournalEventListener((JournalEventListener)this);
    }
    
    public void setPersistenceAdapter(final PersistenceAdapter longTermPersistence) {
        this.longTermPersistence = longTermPersistence;
    }
    
    final Runnable createPeriodicCheckpointTask() {
        return new Runnable() {
            @Override
            public void run() {
                long lastTime = 0L;
                synchronized (this) {
                    lastTime = JournalPersistenceAdapter.this.lastCheckpointRequest;
                }
                if (System.currentTimeMillis() > lastTime + JournalPersistenceAdapter.this.checkpointInterval) {
                    JournalPersistenceAdapter.this.checkpoint(false, true);
                }
            }
        };
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        this.usageManager = usageManager;
        this.longTermPersistence.setUsageManager(usageManager);
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        final Set<ActiveMQDestination> destinations = new HashSet<ActiveMQDestination>(this.longTermPersistence.getDestinations());
        destinations.addAll((Collection<? extends ActiveMQDestination>)this.queues.keySet());
        destinations.addAll((Collection<? extends ActiveMQDestination>)this.topics.keySet());
        return destinations;
    }
    
    private MessageStore createMessageStore(final ActiveMQDestination destination) throws IOException {
        if (destination.isQueue()) {
            return this.createQueueMessageStore((ActiveMQQueue)destination);
        }
        return this.createTopicMessageStore((ActiveMQTopic)destination);
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        JournalMessageStore store = this.queues.get(destination);
        if (store == null) {
            final MessageStore checkpointStore = this.longTermPersistence.createQueueMessageStore(destination);
            store = new JournalMessageStore(this, checkpointStore, destination);
            this.queues.put(destination, store);
        }
        return store;
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destinationName) throws IOException {
        JournalTopicMessageStore store = this.topics.get(destinationName);
        if (store == null) {
            final TopicMessageStore checkpointStore = this.longTermPersistence.createTopicMessageStore(destinationName);
            store = new JournalTopicMessageStore(this, checkpointStore, destinationName);
            this.topics.put(destinationName, store);
        }
        return store;
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
        return this.transactionStore;
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return this.longTermPersistence.getLastMessageBrokerSequenceId();
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        this.longTermPersistence.beginTransaction(context);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        this.longTermPersistence.commitTransaction(context);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        this.longTermPersistence.rollbackTransaction(context);
    }
    
    public synchronized void start() throws Exception {
        if (!this.started.compareAndSet(false, true)) {
            return;
        }
        if (this.brokerService != null) {
            this.wireFormat.setVersion(this.brokerService.getStoreOpenWireVersion());
        }
        this.checkpointTask = this.taskRunnerFactory.createTaskRunner(new Task() {
            @Override
            public boolean iterate() {
                return JournalPersistenceAdapter.this.doCheckpoint();
            }
        }, "ActiveMQ Journal Checkpoint Worker");
        this.checkpointExecutor = new ThreadPoolExecutor(this.maxCheckpointWorkers, this.maxCheckpointWorkers, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runable) {
                final Thread t = new Thread(runable, "Journal checkpoint worker");
                t.setPriority(7);
                return t;
            }
        });
        this.usageManager.getMemoryUsage().addUsageListener(this);
        if (this.longTermPersistence instanceof JDBCPersistenceAdapter) {
            ((JDBCPersistenceAdapter)this.longTermPersistence).setCleanupPeriod(0);
        }
        this.longTermPersistence.start();
        this.createTransactionStore();
        this.recover();
        (this.scheduler = new Scheduler("Journal Scheduler")).start();
        this.scheduler.executePeriodically(this.periodicCheckpointTask, this.checkpointInterval / 10L);
    }
    
    public void stop() throws Exception {
        this.usageManager.getMemoryUsage().removeUsageListener(this);
        if (!this.started.compareAndSet(true, false)) {
            return;
        }
        this.scheduler.cancel(this.periodicCheckpointTask);
        this.scheduler.stop();
        this.checkpoint(true, true);
        this.checkpointTask.shutdown();
        ThreadPoolUtils.shutdown(this.checkpointExecutor);
        this.checkpointExecutor = null;
        this.queues.clear();
        this.topics.clear();
        IOException firstException = null;
        try {
            this.journal.close();
        }
        catch (Exception e) {
            firstException = IOExceptionSupport.create("Failed to close journals: " + e, e);
        }
        this.longTermPersistence.stop();
        if (firstException != null) {
            throw firstException;
        }
    }
    
    public PersistenceAdapter getLongTermPersistence() {
        return this.longTermPersistence;
    }
    
    public WireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public void overflowNotification(final RecordLocation safeLocation) {
        this.checkpoint(false, true);
    }
    
    public void checkpoint(final boolean sync, final boolean fullCheckpoint) {
        try {
            if (this.journal == null) {
                throw new IllegalStateException("Journal is closed.");
            }
            final long now = System.currentTimeMillis();
            CountDownLatch latch = null;
            synchronized (this) {
                latch = this.nextCheckpointCountDownLatch;
                this.lastCheckpointRequest = now;
                if (fullCheckpoint) {
                    this.fullCheckPoint = true;
                }
            }
            this.checkpointTask.wakeup();
            if (sync) {
                JournalPersistenceAdapter.LOG.debug("Waking for checkpoint to complete.");
                latch.await();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JournalPersistenceAdapter.LOG.warn("Request to start checkpoint failed: " + e, e);
        }
    }
    
    @Override
    public void checkpoint(final boolean sync) {
        this.checkpoint(sync, sync);
    }
    
    public boolean doCheckpoint() {
        CountDownLatch latch = null;
        final boolean fullCheckpoint;
        synchronized (this) {
            latch = this.nextCheckpointCountDownLatch;
            this.nextCheckpointCountDownLatch = new CountDownLatch(1);
            fullCheckpoint = this.fullCheckPoint;
            this.fullCheckPoint = false;
        }
        try {
            JournalPersistenceAdapter.LOG.debug("Checkpoint started.");
            RecordLocation newMark = null;
            final ArrayList<FutureTask<RecordLocation>> futureTasks = new ArrayList<FutureTask<RecordLocation>>(this.queues.size() + this.topics.size());
            if (fullCheckpoint) {
                final Iterator<JournalMessageStore> iterator = this.queues.values().iterator();
                while (iterator.hasNext()) {
                    try {
                        final JournalMessageStore ms = iterator.next();
                        final FutureTask<RecordLocation> task = new FutureTask<RecordLocation>(new Callable<RecordLocation>() {
                            @Override
                            public RecordLocation call() throws Exception {
                                return ms.checkpoint();
                            }
                        });
                        futureTasks.add(task);
                        this.checkpointExecutor.execute(task);
                    }
                    catch (Exception e) {
                        JournalPersistenceAdapter.LOG.error("Failed to checkpoint a message store: " + e, e);
                    }
                }
            }
            final Iterator<JournalTopicMessageStore> iterator2 = this.topics.values().iterator();
            while (iterator2.hasNext()) {
                try {
                    final JournalTopicMessageStore ms2 = iterator2.next();
                    final FutureTask<RecordLocation> task = new FutureTask<RecordLocation>(new Callable<RecordLocation>() {
                        @Override
                        public RecordLocation call() throws Exception {
                            return ms2.checkpoint();
                        }
                    });
                    futureTasks.add(task);
                    this.checkpointExecutor.execute(task);
                }
                catch (Exception e) {
                    JournalPersistenceAdapter.LOG.error("Failed to checkpoint a message store: " + e, e);
                }
            }
            try {
                for (final FutureTask<RecordLocation> ft : futureTasks) {
                    final RecordLocation mark = ft.get();
                    if (fullCheckpoint && mark != null && (newMark == null || newMark.compareTo((Object)mark) < 0)) {
                        newMark = mark;
                    }
                }
            }
            catch (Throwable e2) {
                JournalPersistenceAdapter.LOG.error("Failed to checkpoint a message store: " + e2, e2);
            }
            if (fullCheckpoint) {
                try {
                    if (newMark != null) {
                        JournalPersistenceAdapter.LOG.debug("Marking journal at: " + newMark);
                        this.journal.setMark(newMark, true);
                    }
                }
                catch (Exception e) {
                    JournalPersistenceAdapter.LOG.error("Failed to mark the Journal: " + e, e);
                }
                if (this.longTermPersistence instanceof JDBCPersistenceAdapter) {
                    final long now = System.currentTimeMillis();
                    if (now > this.lastCleanup + this.checkpointInterval) {
                        this.lastCleanup = now;
                        ((JDBCPersistenceAdapter)this.longTermPersistence).cleanup();
                    }
                }
            }
            JournalPersistenceAdapter.LOG.debug("Checkpoint done.");
        }
        finally {
            latch.countDown();
        }
        synchronized (this) {
            return this.fullCheckPoint;
        }
    }
    
    public DataStructure readCommand(final RecordLocation location) throws IOException {
        try {
            final Packet packet = this.journal.read(location);
            return (DataStructure)this.wireFormat.unmarshal(this.toByteSequence(packet));
        }
        catch (InvalidRecordLocationException e) {
            throw this.createReadException(location, (Exception)e);
        }
        catch (IOException e2) {
            throw this.createReadException(location, e2);
        }
    }
    
    private void recover() throws IllegalStateException, InvalidRecordLocationException, IOException {
        RecordLocation pos = null;
        int transactionCounter = 0;
        JournalPersistenceAdapter.LOG.info("Journal Recovery Started from: " + this.journal);
        final ConnectionContext context = new ConnectionContext(new NonCachedMessageEvaluationContext());
        while ((pos = this.journal.getNextRecordLocation(pos)) != null) {
            final Packet data = this.journal.read(pos);
            final DataStructure c = (DataStructure)this.wireFormat.unmarshal(this.toByteSequence(data));
            if (c instanceof Message) {
                final Message message = (Message)c;
                final JournalMessageStore store = (JournalMessageStore)this.createMessageStore(message.getDestination());
                if (message.isInTransaction()) {
                    this.transactionStore.addMessage(store, message, pos);
                }
                else {
                    store.replayAddMessage(context, message);
                    ++transactionCounter;
                }
            }
            else {
                switch (c.getDataStructureType()) {
                    case 52: {
                        final JournalQueueAck command = (JournalQueueAck)c;
                        final JournalMessageStore store = (JournalMessageStore)this.createMessageStore(command.getDestination());
                        if (command.getMessageAck().isInTransaction()) {
                            this.transactionStore.removeMessage(store, command.getMessageAck(), pos);
                        }
                        else {
                            store.replayRemoveMessage(context, command.getMessageAck());
                            ++transactionCounter;
                        }
                        continue;
                    }
                    case 50: {
                        final JournalTopicAck command2 = (JournalTopicAck)c;
                        final JournalTopicMessageStore store2 = (JournalTopicMessageStore)this.createMessageStore(command2.getDestination());
                        if (command2.getTransactionId() != null) {
                            this.transactionStore.acknowledge(store2, command2, pos);
                        }
                        else {
                            store2.replayAcknowledge(context, command2.getClientId(), command2.getSubscritionName(), command2.getMessageId());
                            ++transactionCounter;
                        }
                        continue;
                    }
                    case 54: {
                        final JournalTransaction command3 = (JournalTransaction)c;
                        try {
                            switch (command3.getType()) {
                                case 1: {
                                    this.transactionStore.replayPrepare(command3.getTransactionId());
                                    continue;
                                }
                                case 2:
                                case 4: {
                                    final JournalTransactionStore.Tx tx = this.transactionStore.replayCommit(command3.getTransactionId(), command3.getWasPrepared());
                                    if (tx == null) {
                                        continue;
                                    }
                                    tx.getOperations();
                                    for (final JournalTransactionStore.TxOperation op : tx.getOperations()) {
                                        if (op.operationType == 0) {
                                            op.store.replayAddMessage(context, (Message)op.data);
                                        }
                                        if (op.operationType == 1) {
                                            op.store.replayRemoveMessage(context, (MessageAck)op.data);
                                        }
                                        if (op.operationType == 3) {
                                            final JournalTopicAck ack = (JournalTopicAck)op.data;
                                            ((JournalTopicMessageStore)op.store).replayAcknowledge(context, ack.getClientId(), ack.getSubscritionName(), ack.getMessageId());
                                        }
                                    }
                                    ++transactionCounter;
                                    continue;
                                }
                                case 3:
                                case 5: {
                                    this.transactionStore.replayRollback(command3.getTransactionId());
                                    continue;
                                }
                                default: {
                                    throw new IOException("Invalid journal command type: " + command3.getType());
                                }
                            }
                        }
                        catch (IOException e) {
                            JournalPersistenceAdapter.LOG.error("Recovery Failure: Could not replay: " + c + ", reason: " + e, e);
                        }
                        continue;
                    }
                    case 53: {
                        final JournalTrace trace = (JournalTrace)c;
                        JournalPersistenceAdapter.LOG.debug("TRACE Entry: " + trace.getMessage());
                        continue;
                    }
                    default: {
                        JournalPersistenceAdapter.LOG.error("Unknown type of record in transaction log which will be discarded: " + c);
                        continue;
                    }
                }
            }
        }
        final RecordLocation location = this.writeTraceMessage("RECOVERED", true);
        this.journal.setMark(location, true);
        JournalPersistenceAdapter.LOG.info("Journal Recovered: " + transactionCounter + " message(s) in transactions recovered.");
    }
    
    private IOException createReadException(final RecordLocation location, final Exception e) {
        return IOExceptionSupport.create("Failed to read to journal for: " + location + ". Reason: " + e, e);
    }
    
    protected IOException createWriteException(final DataStructure packet, final Exception e) {
        return IOExceptionSupport.create("Failed to write to journal for: " + packet + ". Reason: " + e, e);
    }
    
    protected IOException createWriteException(final String command, final Exception e) {
        return IOExceptionSupport.create("Failed to write to journal for command: " + command + ". Reason: " + e, e);
    }
    
    protected IOException createRecoveryFailedException(final Exception e) {
        return IOExceptionSupport.create("Failed to recover from journal. Reason: " + e, e);
    }
    
    public RecordLocation writeCommand(final DataStructure command, final boolean sync) throws IOException {
        if (this.started.get()) {
            try {
                return this.journal.write(this.toPacket(this.wireFormat.marshal(command)), sync);
            }
            catch (IOException ioe) {
                JournalPersistenceAdapter.LOG.error("Cannot write to the journal", ioe);
                this.brokerService.handleIOException(ioe);
                throw ioe;
            }
        }
        throw new IOException("closed");
    }
    
    private RecordLocation writeTraceMessage(final String message, final boolean sync) throws IOException {
        final JournalTrace trace = new JournalTrace();
        trace.setMessage(message);
        return this.writeCommand(trace, sync);
    }
    
    public void onUsageChanged(final Usage usage, int oldPercentUsage, int newPercentUsage) {
        newPercentUsage = newPercentUsage / 10 * 10;
        oldPercentUsage = oldPercentUsage / 10 * 10;
        if (newPercentUsage >= 70 && oldPercentUsage < newPercentUsage) {
            final boolean sync = newPercentUsage >= 90;
            this.checkpoint(sync, true);
        }
    }
    
    public JournalTransactionStore getTransactionStore() {
        return this.transactionStore;
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        try {
            final JournalTrace trace = new JournalTrace();
            trace.setMessage("DELETED");
            final RecordLocation location = this.journal.write(this.toPacket(this.wireFormat.marshal(trace)), false);
            this.journal.setMark(location, true);
            JournalPersistenceAdapter.LOG.info("Journal deleted: ");
        }
        catch (IOException e) {
            throw e;
        }
        catch (Throwable e2) {
            throw IOExceptionSupport.create(e2);
        }
        this.longTermPersistence.deleteAllMessages();
    }
    
    public SystemUsage getUsageManager() {
        return this.usageManager;
    }
    
    public int getMaxCheckpointMessageAddSize() {
        return this.maxCheckpointMessageAddSize;
    }
    
    public void setMaxCheckpointMessageAddSize(final int maxCheckpointMessageAddSize) {
        this.maxCheckpointMessageAddSize = maxCheckpointMessageAddSize;
    }
    
    public int getMaxCheckpointWorkers() {
        return this.maxCheckpointWorkers;
    }
    
    public void setMaxCheckpointWorkers(final int maxCheckpointWorkers) {
        this.maxCheckpointWorkers = maxCheckpointWorkers;
    }
    
    public long getCheckpointInterval() {
        return this.checkpointInterval;
    }
    
    public void setCheckpointInterval(final long checkpointInterval) {
        this.checkpointInterval = checkpointInterval;
    }
    
    public boolean isUseExternalMessageReferences() {
        return false;
    }
    
    public void setUseExternalMessageReferences(final boolean enable) {
        if (enable) {
            throw new IllegalArgumentException("The journal does not support message references.");
        }
    }
    
    public Packet toPacket(final ByteSequence sequence) {
        return (Packet)new ByteArrayPacket(new org.apache.activeio.packet.ByteSequence(sequence.data, sequence.offset, sequence.length));
    }
    
    public ByteSequence toByteSequence(final Packet packet) {
        final org.apache.activeio.packet.ByteSequence sequence = packet.asByteSequence();
        return new ByteSequence(sequence.getData(), sequence.getOffset(), sequence.getLength());
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
        this.longTermPersistence.setBrokerName(brokerName);
    }
    
    @Override
    public String toString() {
        return "JournalPersistenceAdapter(" + this.longTermPersistence + ")";
    }
    
    @Override
    public void setDirectory(final File dir) {
        this.directory = dir;
    }
    
    @Override
    public File getDirectory() {
        return this.directory;
    }
    
    @Override
    public long size() {
        return 0L;
    }
    
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
        final PersistenceAdapter pa = this.getLongTermPersistence();
        if (pa instanceof BrokerServiceAware) {
            ((BrokerServiceAware)pa).setBrokerService(brokerService);
        }
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) {
        return -1L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JournalPersistenceAdapter.class);
    }
}
