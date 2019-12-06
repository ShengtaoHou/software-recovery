// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import org.apache.activemq.store.kahadb.data.KahaSubscriptionCommand;
import org.apache.activemq.command.SubscriptionInfo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.store.kahadb.data.KahaRemoveDestinationCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.protobuf.Buffer;
import java.io.InterruptedIOException;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.store.ListenableFuture;
import java.util.HashMap;
import org.apache.activemq.store.AbstractMessageStore;
import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.store.kahadb.data.KahaLocation;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaUpdateMessageCommand;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ProducerId;
import java.util.HashSet;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.command.MessageId;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import java.util.Set;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.RegionBroker;
import org.apache.activemq.store.kahadb.data.KahaDestination;
import java.util.Iterator;
import org.apache.activemq.util.ThreadPoolUtils;
import org.apache.activemq.util.ServiceStopper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.openwire.OpenWireFormat;
import java.util.LinkedList;
import org.apache.activemq.store.TransactionIdTransformer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.wireformat.WireFormat;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.apache.activemq.store.PersistenceAdapter;

public class KahaDBStore extends MessageDatabase implements PersistenceAdapter
{
    static final Logger LOG;
    private static final int MAX_ASYNC_JOBS = 10000;
    public static final String PROPERTY_CANCELED_TASK_MOD_METRIC = "org.apache.activemq.store.kahadb.CANCELED_TASK_MOD_METRIC";
    public static final int cancelledTaskModMetric;
    public static final String PROPERTY_ASYNC_EXECUTOR_MAX_THREADS = "org.apache.activemq.store.kahadb.ASYNC_EXECUTOR_MAX_THREADS";
    private static final int asyncExecutorMaxThreads;
    protected ExecutorService queueExecutor;
    protected ExecutorService topicExecutor;
    protected final List<Map<AsyncJobKey, StoreTask>> asyncQueueMaps;
    protected final List<Map<AsyncJobKey, StoreTask>> asyncTopicMaps;
    final WireFormat wireFormat;
    private SystemUsage usageManager;
    private LinkedBlockingQueue<Runnable> asyncQueueJobQueue;
    private LinkedBlockingQueue<Runnable> asyncTopicJobQueue;
    Semaphore globalQueueSemaphore;
    Semaphore globalTopicSemaphore;
    private boolean concurrentStoreAndDispatchQueues;
    private boolean concurrentStoreAndDispatchTopics;
    private final boolean concurrentStoreAndDispatchTransactions = false;
    private int maxAsyncJobs;
    private final KahaDBTransactionStore transactionStore;
    private TransactionIdTransformer transactionIdTransformer;
    
    public KahaDBStore() {
        this.asyncQueueMaps = new LinkedList<Map<AsyncJobKey, StoreTask>>();
        this.asyncTopicMaps = new LinkedList<Map<AsyncJobKey, StoreTask>>();
        this.wireFormat = new OpenWireFormat();
        this.concurrentStoreAndDispatchQueues = true;
        this.concurrentStoreAndDispatchTopics = false;
        this.maxAsyncJobs = 10000;
        this.transactionStore = new KahaDBTransactionStore(this);
        this.transactionIdTransformer = new TransactionIdTransformer() {
            @Override
            public TransactionId transform(final TransactionId txid) {
                return txid;
            }
        };
    }
    
    @Override
    public String toString() {
        return "KahaDB:[" + this.directory.getAbsolutePath() + "]";
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        this.usageManager = usageManager;
    }
    
    public SystemUsage getUsageManager() {
        return this.usageManager;
    }
    
    public boolean isConcurrentStoreAndDispatchQueues() {
        return this.concurrentStoreAndDispatchQueues;
    }
    
    public void setConcurrentStoreAndDispatchQueues(final boolean concurrentStoreAndDispatch) {
        this.concurrentStoreAndDispatchQueues = concurrentStoreAndDispatch;
    }
    
    public boolean isConcurrentStoreAndDispatchTopics() {
        return this.concurrentStoreAndDispatchTopics;
    }
    
    public void setConcurrentStoreAndDispatchTopics(final boolean concurrentStoreAndDispatch) {
        this.concurrentStoreAndDispatchTopics = concurrentStoreAndDispatch;
    }
    
    public boolean isConcurrentStoreAndDispatchTransactions() {
        this.getClass();
        return false;
    }
    
    public int getMaxAsyncJobs() {
        return this.maxAsyncJobs;
    }
    
    public void setMaxAsyncJobs(final int maxAsyncJobs) {
        this.maxAsyncJobs = maxAsyncJobs;
    }
    
    @Override
    public void doStart() throws Exception {
        if (this.brokerService != null) {
            this.metadata.openwireVersion = this.brokerService.getStoreOpenWireVersion();
            this.wireFormat.setVersion(this.metadata.openwireVersion);
            if (KahaDBStore.LOG.isDebugEnabled()) {
                KahaDBStore.LOG.debug("Store OpenWire version configured as: {}", (Object)this.metadata.openwireVersion);
            }
        }
        super.doStart();
        if (this.brokerService != null && this.metadata.openwireVersion != this.brokerService.getStoreOpenWireVersion()) {
            KahaDBStore.LOG.warn("Receovered Store uses a different OpenWire version[{}] than the version configured[{}].", (Object)this.metadata.openwireVersion, this.brokerService.getStoreOpenWireVersion());
        }
        this.globalQueueSemaphore = new Semaphore(this.getMaxAsyncJobs());
        this.globalTopicSemaphore = new Semaphore(this.getMaxAsyncJobs());
        this.asyncQueueJobQueue = new LinkedBlockingQueue<Runnable>(this.getMaxAsyncJobs());
        this.asyncTopicJobQueue = new LinkedBlockingQueue<Runnable>(this.getMaxAsyncJobs());
        this.queueExecutor = new StoreTaskExecutor(1, KahaDBStore.asyncExecutorMaxThreads, 0L, TimeUnit.MILLISECONDS, this.asyncQueueJobQueue, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable, "ConcurrentQueueStoreAndDispatch");
                thread.setDaemon(true);
                return thread;
            }
        });
        this.topicExecutor = new StoreTaskExecutor(1, KahaDBStore.asyncExecutorMaxThreads, 0L, TimeUnit.MILLISECONDS, this.asyncTopicJobQueue, new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable, "ConcurrentTopicStoreAndDispatch");
                thread.setDaemon(true);
                return thread;
            }
        });
    }
    
    @Override
    public void doStop(final ServiceStopper stopper) throws Exception {
        KahaDBStore.LOG.info("Stopping async queue tasks");
        if (this.globalQueueSemaphore != null) {
            this.globalQueueSemaphore.tryAcquire(this.maxAsyncJobs, 60L, TimeUnit.SECONDS);
        }
        synchronized (this.asyncQueueMaps) {
            for (final Map<AsyncJobKey, StoreTask> m : this.asyncQueueMaps) {
                synchronized (m) {
                    for (final StoreTask task : m.values()) {
                        task.cancel();
                    }
                }
            }
            this.asyncQueueMaps.clear();
        }
        KahaDBStore.LOG.info("Stopping async topic tasks");
        if (this.globalTopicSemaphore != null) {
            this.globalTopicSemaphore.tryAcquire(this.maxAsyncJobs, 60L, TimeUnit.SECONDS);
        }
        synchronized (this.asyncTopicMaps) {
            for (final Map<AsyncJobKey, StoreTask> m : this.asyncTopicMaps) {
                synchronized (m) {
                    for (final StoreTask task : m.values()) {
                        task.cancel();
                    }
                }
            }
            this.asyncTopicMaps.clear();
        }
        if (this.globalQueueSemaphore != null) {
            this.globalQueueSemaphore.drainPermits();
        }
        if (this.globalTopicSemaphore != null) {
            this.globalTopicSemaphore.drainPermits();
        }
        if (this.queueExecutor != null) {
            ThreadPoolUtils.shutdownNow(this.queueExecutor);
            this.queueExecutor = null;
        }
        if (this.topicExecutor != null) {
            ThreadPoolUtils.shutdownNow(this.topicExecutor);
            this.topicExecutor = null;
        }
        KahaDBStore.LOG.info("Stopped KahaDB");
        super.doStop(stopper);
    }
    
    @Override
    void rollbackStatsOnDuplicate(final KahaDestination commandDestination) {
        if (this.brokerService != null) {
            final RegionBroker regionBroker = (RegionBroker)this.brokerService.getRegionBroker();
            if (regionBroker != null) {
                final Set<Destination> destinationSet = regionBroker.getDestinations(this.convert(commandDestination));
                for (final Destination destination : destinationSet) {
                    destination.getDestinationStatistics().getMessages().decrement();
                    destination.getDestinationStatistics().getEnqueues().decrement();
                }
            }
        }
    }
    
    private Location findMessageLocation(final String key, final KahaDestination destination) throws IOException {
        return this.pageFile.tx().execute((Transaction.CallableClosure<Location, Throwable>)new Transaction.CallableClosure<Location, IOException>() {
            @Override
            public Location execute(final Transaction tx) throws IOException {
                final StoredDestination sd = KahaDBStore.this.getStoredDestination(destination, tx);
                final Long sequence = sd.messageIdIndex.get(tx, key);
                if (sequence == null) {
                    return null;
                }
                return sd.orderIndex.get(tx, sequence).location;
            }
        });
    }
    
    protected StoreQueueTask removeQueueTask(final KahaDBMessageStore store, final MessageId id) {
        StoreQueueTask task = null;
        synchronized (store.asyncTaskMap) {
            task = store.asyncTaskMap.remove(new AsyncJobKey(id, store.getDestination()));
        }
        return task;
    }
    
    protected void addQueueTask(final KahaDBMessageStore store, final StoreQueueTask task) throws IOException {
        synchronized (store.asyncTaskMap) {
            store.asyncTaskMap.put(new AsyncJobKey(task.getMessage().getMessageId(), store.getDestination()), task);
        }
        this.queueExecutor.execute(task);
    }
    
    protected StoreTopicTask removeTopicTask(final KahaDBTopicMessageStore store, final MessageId id) {
        StoreTopicTask task = null;
        synchronized (store.asyncTaskMap) {
            task = store.asyncTaskMap.remove(new AsyncJobKey(id, store.getDestination()));
        }
        return task;
    }
    
    protected void addTopicTask(final KahaDBTopicMessageStore store, final StoreTopicTask task) throws IOException {
        synchronized (store.asyncTaskMap) {
            store.asyncTaskMap.put(new AsyncJobKey(task.getMessage().getMessageId(), store.getDestination()), task);
        }
        this.topicExecutor.execute(task);
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        return this.transactionStore;
    }
    
    public boolean getForceRecoverIndex() {
        return this.forceRecoverIndex;
    }
    
    public void setForceRecoverIndex(final boolean forceRecoverIndex) {
        this.forceRecoverIndex = forceRecoverIndex;
    }
    
    String subscriptionKey(final String clientId, final String subscriptionName) {
        return clientId + ":" + subscriptionName;
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        return this.transactionStore.proxy(new KahaDBMessageStore(destination));
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        return this.transactionStore.proxy(new KahaDBTopicMessageStore(destination));
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        this.deleteAllMessages = true;
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        try {
            final HashSet<ActiveMQDestination> rc = new HashSet<ActiveMQDestination>();
            this.indexLock.writeLock().lock();
            try {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        final Iterator<Map.Entry<String, StoredDestination>> iterator = KahaDBStore.this.metadata.destinations.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<String, StoredDestination> entry = iterator.next();
                            if (!this.isEmptyTopic(entry, tx)) {
                                rc.add(KahaDBStore.this.convert(entry.getKey()));
                            }
                        }
                    }
                    
                    private boolean isEmptyTopic(final Map.Entry<String, StoredDestination> entry, final Transaction tx) throws IOException {
                        boolean isEmptyTopic = false;
                        final ActiveMQDestination dest = KahaDBStore.this.convert(entry.getKey());
                        if (dest.isTopic()) {
                            final StoredDestination loadedStore = KahaDBStore.this.getStoredDestination(KahaDBStore.this.convert(dest), tx);
                            if (loadedStore.subscriptionAcks.isEmpty(tx)) {
                                isEmptyTopic = true;
                            }
                        }
                        return isEmptyTopic;
                    }
                });
            }
            finally {
                this.indexLock.writeLock().unlock();
            }
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return 0L;
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) {
        this.indexLock.readLock().lock();
        try {
            return this.metadata.producerSequenceIdTracker.getLastSeqId(id);
        }
        finally {
            this.indexLock.readLock().unlock();
        }
    }
    
    @Override
    public long size() {
        try {
            return this.journalSize.get() + this.getPageFile().getDiskSize();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
        super.checkpointCleanup(sync);
    }
    
    Message loadMessage(final Location location) throws IOException {
        final JournalCommand<?> command = this.load(location);
        KahaAddMessageCommand addMessage = null;
        switch (command.type()) {
            case KAHA_UPDATE_MESSAGE_COMMAND: {
                addMessage = ((KahaUpdateMessageCommand)command).getMessage();
                break;
            }
            default: {
                addMessage = (KahaAddMessageCommand)command;
                break;
            }
        }
        final Message msg = (Message)this.wireFormat.unmarshal(new DataInputStream(addMessage.getMessage().newInput()));
        return msg;
    }
    
    KahaLocation convert(final Location location) {
        final KahaLocation rc = new KahaLocation();
        rc.setLogId(location.getDataFileId());
        rc.setOffset(location.getOffset());
        return rc;
    }
    
    KahaDestination convert(final ActiveMQDestination dest) {
        final KahaDestination rc = new KahaDestination();
        rc.setName(dest.getPhysicalName());
        switch (dest.getDestinationType()) {
            case 1: {
                rc.setType(KahaDestination.DestinationType.QUEUE);
                return rc;
            }
            case 2: {
                rc.setType(KahaDestination.DestinationType.TOPIC);
                return rc;
            }
            case 5: {
                rc.setType(KahaDestination.DestinationType.TEMP_QUEUE);
                return rc;
            }
            case 6: {
                rc.setType(KahaDestination.DestinationType.TEMP_TOPIC);
                return rc;
            }
            default: {
                return null;
            }
        }
    }
    
    ActiveMQDestination convert(final String dest) {
        final int p = dest.indexOf(":");
        if (p < 0) {
            throw new IllegalArgumentException("Not in the valid destination format");
        }
        final int type = Integer.parseInt(dest.substring(0, p));
        final String name = dest.substring(p + 1);
        return this.convert(type, name);
    }
    
    private ActiveMQDestination convert(final KahaDestination commandDestination) {
        return this.convert(commandDestination.getType().getNumber(), commandDestination.getName());
    }
    
    private ActiveMQDestination convert(final int type, final String name) {
        switch (KahaDestination.DestinationType.valueOf(type)) {
            case QUEUE: {
                return new ActiveMQQueue(name);
            }
            case TOPIC: {
                return new ActiveMQTopic(name);
            }
            case TEMP_QUEUE: {
                return new ActiveMQTempQueue(name);
            }
            case TEMP_TOPIC: {
                return new ActiveMQTempTopic(name);
            }
            default: {
                throw new IllegalArgumentException("Not in the valid destination format");
            }
        }
    }
    
    public TransactionIdTransformer getTransactionIdTransformer() {
        return this.transactionIdTransformer;
    }
    
    public void setTransactionIdTransformer(final TransactionIdTransformer transactionIdTransformer) {
        this.transactionIdTransformer = transactionIdTransformer;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KahaDBStore.class);
        cancelledTaskModMetric = Integer.parseInt(System.getProperty("org.apache.activemq.store.kahadb.CANCELED_TASK_MOD_METRIC", "0"), 10);
        asyncExecutorMaxThreads = Integer.parseInt(System.getProperty("org.apache.activemq.store.kahadb.ASYNC_EXECUTOR_MAX_THREADS", "1"), 10);
    }
    
    public class KahaDBMessageStore extends AbstractMessageStore
    {
        protected final Map<AsyncJobKey, StoreTask> asyncTaskMap;
        protected KahaDestination dest;
        private final int maxAsyncJobs;
        private final Semaphore localDestinationSemaphore;
        double doneTasks;
        double canceledTasks;
        
        public KahaDBMessageStore(final ActiveMQDestination destination) {
            super(destination);
            this.asyncTaskMap = new HashMap<AsyncJobKey, StoreTask>();
            this.canceledTasks = 0.0;
            this.dest = KahaDBStore.this.convert(destination);
            this.maxAsyncJobs = KahaDBStore.this.getMaxAsyncJobs();
            this.localDestinationSemaphore = new Semaphore(this.maxAsyncJobs);
        }
        
        @Override
        public ActiveMQDestination getDestination() {
            return this.destination;
        }
        
        @Override
        public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
            if (KahaDBStore.this.isConcurrentStoreAndDispatchQueues()) {
                final StoreQueueTask result = new StoreQueueTask(this, context, message);
                result.aquireLocks();
                KahaDBStore.this.addQueueTask(this, result);
                return result.getFuture();
            }
            return super.asyncAddQueueMessage(context, message);
        }
        
        @Override
        public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
            if (KahaDBStore.this.isConcurrentStoreAndDispatchQueues()) {
                final AsyncJobKey key = new AsyncJobKey(ack.getLastMessageId(), this.getDestination());
                StoreQueueTask task = null;
                synchronized (this.asyncTaskMap) {
                    task = this.asyncTaskMap.get(key);
                }
                if (task != null) {
                    if (!ack.isInTransaction()) {
                        if (task.cancel()) {
                            synchronized (this.asyncTaskMap) {
                                this.asyncTaskMap.remove(key);
                            }
                            return;
                        }
                    }
                    try {
                        task.future.get();
                    }
                    catch (InterruptedException e) {
                        throw new InterruptedIOException(e.toString());
                    }
                    catch (Exception ignored) {
                        KahaDBStore.LOG.debug("removeAsync: cannot cancel, waiting for add resulted in ex", ignored);
                    }
                    this.removeMessage(context, ack);
                }
                else {
                    this.removeMessage(context, ack);
                }
            }
            else {
                this.removeMessage(context, ack);
            }
        }
        
        @Override
        public void addMessage(final ConnectionContext context, final Message message) throws IOException {
            final KahaAddMessageCommand command = new KahaAddMessageCommand();
            command.setDestination(this.dest);
            command.setMessageId(message.getMessageId().toProducerKey());
            command.setTransactionInfo(TransactionIdConversion.convert(KahaDBStore.this.transactionIdTransformer.transform(message.getTransactionId())));
            command.setPriority(message.getPriority());
            command.setPrioritySupported(this.isPrioritizedMessages());
            final ByteSequence packet = KahaDBStore.this.wireFormat.marshal(message);
            command.setMessage(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            KahaDBStore.this.store(command, KahaDBStore.this.isEnableJournalDiskSyncs() && message.isResponseRequired(), null, null);
        }
        
        @Override
        public void updateMessage(final Message message) throws IOException {
            if (KahaDBStore.LOG.isTraceEnabled()) {
                KahaDBStore.LOG.trace("updating: " + message.getMessageId() + " with deliveryCount: " + message.getRedeliveryCounter());
            }
            final KahaUpdateMessageCommand updateMessageCommand = new KahaUpdateMessageCommand();
            final KahaAddMessageCommand command = new KahaAddMessageCommand();
            command.setDestination(this.dest);
            command.setMessageId(message.getMessageId().toProducerKey());
            command.setPriority(message.getPriority());
            command.setPrioritySupported(this.prioritizedMessages);
            final ByteSequence packet = KahaDBStore.this.wireFormat.marshal(message);
            command.setMessage(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            updateMessageCommand.setMessage(command);
            KahaDBStore.this.store(updateMessageCommand, KahaDBStore.this.isEnableJournalDiskSyncs(), null, null);
        }
        
        @Override
        public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
            final KahaRemoveMessageCommand command = new KahaRemoveMessageCommand();
            command.setDestination(this.dest);
            command.setMessageId(ack.getLastMessageId().toProducerKey());
            command.setTransactionInfo(TransactionIdConversion.convert(KahaDBStore.this.transactionIdTransformer.transform(ack.getTransactionId())));
            final ByteSequence packet = KahaDBStore.this.wireFormat.marshal(ack);
            command.setAck(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            KahaDBStore.this.store(command, KahaDBStore.this.isEnableJournalDiskSyncs() && ack.isResponseRequired(), null, null);
        }
        
        @Override
        public void removeAllMessages(final ConnectionContext context) throws IOException {
            final KahaRemoveDestinationCommand command = new KahaRemoveDestinationCommand();
            command.setDestination(this.dest);
            KahaDBStore.this.store(command, true, null, null);
        }
        
        @Override
        public Message getMessage(final MessageId identity) throws IOException {
            final String key = identity.toProducerKey();
            KahaDBStore.this.indexLock.writeLock().lock();
            Location location;
            try {
                location = KahaDBStore.this.findMessageLocation(key, this.dest);
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
            if (location == null) {
                return null;
            }
            return KahaDBStore.this.loadMessage(location);
        }
        
        @Override
        public int getMessageCount() throws IOException {
            try {
                this.lockAsyncJobQueue();
                KahaDBStore.this.indexLock.writeLock().lock();
                try {
                    return KahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Integer, Throwable>)new Transaction.CallableClosure<Integer, IOException>() {
                        @Override
                        public Integer execute(final Transaction tx) throws IOException {
                            final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                            int rc = 0;
                            final Iterator<Map.Entry<Location, Long>> iterator = sd.locationIndex.iterator(tx);
                            while (iterator.hasNext()) {
                                iterator.next();
                                ++rc;
                            }
                            return rc;
                        }
                    });
                }
                finally {
                    KahaDBStore.this.indexLock.writeLock().unlock();
                }
            }
            finally {
                this.unlockAsyncJobQueue();
            }
        }
        
        @Override
        public boolean isEmpty() throws IOException {
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                return KahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Boolean, Throwable>)new Transaction.CallableClosure<Boolean, IOException>() {
                    @Override
                    public Boolean execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        return sd.locationIndex.isEmpty(tx);
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public void recover(final MessageRecoveryListener listener) throws Exception {
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        KahaDBMessageStore.this.recoverRolledBackAcks(sd, tx, Integer.MAX_VALUE, listener);
                        sd.orderIndex.resetCursorPosition();
                        final Iterator<Map.Entry<Long, MessageKeys>> iterator = sd.orderIndex.iterator(tx);
                        while (listener.hasSpace() && iterator.hasNext()) {
                            final Map.Entry<Long, MessageKeys> entry = iterator.next();
                            if (KahaDBStore.this.ackedAndPrepared.contains(entry.getValue().messageId)) {
                                continue;
                            }
                            final Message msg = KahaDBStore.this.loadMessage(entry.getValue().location);
                            listener.recoverMessage(msg);
                        }
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        Map.Entry<Long, MessageKeys> entry = null;
                        int counter = KahaDBMessageStore.this.recoverRolledBackAcks(sd, tx, maxReturned, listener);
                        final Iterator<Map.Entry<Long, MessageKeys>> iterator = sd.orderIndex.iterator(tx);
                        while (iterator.hasNext()) {
                            entry = iterator.next();
                            if (KahaDBStore.this.ackedAndPrepared.contains(entry.getValue().messageId)) {
                                continue;
                            }
                            final Message msg = KahaDBStore.this.loadMessage(entry.getValue().location);
                            listener.recoverMessage(msg);
                            if (++counter >= maxReturned) {
                                break;
                            }
                        }
                        sd.orderIndex.stoppedIterating();
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        protected int recoverRolledBackAcks(final StoredDestination sd, final Transaction tx, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
            int counter = 0;
            final Iterator<String> iterator = KahaDBStore.this.rolledBackAcks.iterator();
            while (iterator.hasNext()) {
                final String id = iterator.next();
                iterator.remove();
                final Long sequence = sd.messageIdIndex.get(tx, id);
                if (sequence != null) {
                    if (sd.orderIndex.alreadyDispatched(sequence)) {
                        listener.recoverMessage(KahaDBStore.this.loadMessage(sd.orderIndex.get(tx, sequence).location));
                        if (++counter >= maxReturned) {
                            break;
                        }
                        continue;
                    }
                    else {
                        KahaDBStore.LOG.info("rolledback ack message {} with seq {} will be picked up in future batch {}", id, sequence, sd.orderIndex.cursor);
                    }
                }
                else {
                    KahaDBStore.LOG.warn("Failed to locate rolled back ack message {} in {}", id, sd);
                }
            }
            return counter;
        }
        
        @Override
        public void resetBatching() {
            if (KahaDBStore.this.pageFile.isLoaded()) {
                KahaDBStore.this.indexLock.writeLock().lock();
                try {
                    KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                        @Override
                        public void execute(final Transaction tx) throws Exception {
                            final StoredDestination sd = KahaDBStore.this.getExistingStoredDestination(KahaDBMessageStore.this.dest, tx);
                            if (sd != null) {
                                sd.orderIndex.resetCursorPosition();
                            }
                        }
                    });
                }
                catch (Exception e) {
                    KahaDBStore.LOG.error("Failed to reset batching", e);
                }
                finally {
                    KahaDBStore.this.indexLock.writeLock().unlock();
                }
            }
        }
        
        @Override
        public void setBatch(final MessageId identity) throws IOException {
            try {
                final String key = identity.toProducerKey();
                this.lockAsyncJobQueue();
                KahaDBStore.this.indexLock.writeLock().lock();
                try {
                    KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                            final Long location = sd.messageIdIndex.get(tx, key);
                            if (location != null) {
                                sd.orderIndex.setBatch(tx, location);
                            }
                        }
                    });
                }
                finally {
                    KahaDBStore.this.indexLock.writeLock().unlock();
                }
            }
            finally {
                this.unlockAsyncJobQueue();
            }
        }
        
        @Override
        public void setMemoryUsage(final MemoryUsage memoryUsage) {
        }
        
        @Override
        public void start() throws Exception {
            super.start();
        }
        
        @Override
        public void stop() throws Exception {
            super.stop();
        }
        
        protected void lockAsyncJobQueue() {
            try {
                this.localDestinationSemaphore.tryAcquire(this.maxAsyncJobs, 60L, TimeUnit.SECONDS);
            }
            catch (Exception e) {
                KahaDBStore.LOG.error("Failed to lock async jobs for " + this.destination, e);
            }
        }
        
        protected void unlockAsyncJobQueue() {
            this.localDestinationSemaphore.release(this.maxAsyncJobs);
        }
        
        protected void acquireLocalAsyncLock() {
            try {
                this.localDestinationSemaphore.acquire();
            }
            catch (InterruptedException e) {
                KahaDBStore.LOG.error("Failed to aquire async lock for " + this.destination, e);
            }
        }
        
        protected void releaseLocalAsyncLock() {
            this.localDestinationSemaphore.release();
        }
    }
    
    class KahaDBTopicMessageStore extends KahaDBMessageStore implements TopicMessageStore
    {
        private final AtomicInteger subscriptionCount;
        
        public KahaDBTopicMessageStore(final ActiveMQTopic destination) throws IOException {
            super(destination);
            (this.subscriptionCount = new AtomicInteger()).set(this.getAllSubscriptions().length);
            KahaDBStore.this.asyncTopicMaps.add(this.asyncTaskMap);
        }
        
        @Override
        public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
            if (KahaDBStore.this.isConcurrentStoreAndDispatchTopics()) {
                final StoreTopicTask result = new StoreTopicTask(this, context, message, this.subscriptionCount.get());
                result.aquireLocks();
                KahaDBStore.this.addTopicTask(this, result);
                return result.getFuture();
            }
            return super.asyncAddTopicMessage(context, message);
        }
        
        @Override
        public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName).toString();
            if (KahaDBStore.this.isConcurrentStoreAndDispatchTopics()) {
                final AsyncJobKey key = new AsyncJobKey(messageId, this.getDestination());
                StoreTopicTask task = null;
                synchronized (this.asyncTaskMap) {
                    task = this.asyncTaskMap.get(key);
                }
                if (task != null) {
                    if (task.addSubscriptionKey(subscriptionKey)) {
                        KahaDBStore.this.removeTopicTask(this, messageId);
                        if (task.cancel()) {
                            synchronized (this.asyncTaskMap) {
                                this.asyncTaskMap.remove(key);
                            }
                        }
                    }
                }
                else {
                    this.doAcknowledge(context, subscriptionKey, messageId, ack);
                }
            }
            else {
                this.doAcknowledge(context, subscriptionKey, messageId, ack);
            }
        }
        
        protected void doAcknowledge(final ConnectionContext context, final String subscriptionKey, final MessageId messageId, final MessageAck ack) throws IOException {
            final KahaRemoveMessageCommand command = new KahaRemoveMessageCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(subscriptionKey);
            command.setMessageId(messageId.toProducerKey());
            command.setTransactionInfo((ack != null) ? TransactionIdConversion.convert(KahaDBStore.this.transactionIdTransformer.transform(ack.getTransactionId())) : null);
            if (ack != null && ack.isUnmatchedAck()) {
                command.setAck(MessageDatabase.UNMATCHED);
            }
            else {
                final ByteSequence packet = KahaDBStore.this.wireFormat.marshal(ack);
                command.setAck(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            }
            KahaDBStore.this.store(command, false, null, null);
        }
        
        @Override
        public void addSubscription(final SubscriptionInfo subscriptionInfo, final boolean retroactive) throws IOException {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(subscriptionInfo.getClientId(), subscriptionInfo.getSubscriptionName());
            final KahaSubscriptionCommand command = new KahaSubscriptionCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(subscriptionKey.toString());
            command.setRetroactive(retroactive);
            final ByteSequence packet = KahaDBStore.this.wireFormat.marshal(subscriptionInfo);
            command.setSubscriptionInfo(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            KahaDBStore.this.store(command, KahaDBStore.this.isEnableJournalDiskSyncs(), null, null);
            this.subscriptionCount.incrementAndGet();
        }
        
        @Override
        public void deleteSubscription(final String clientId, final String subscriptionName) throws IOException {
            final KahaSubscriptionCommand command = new KahaSubscriptionCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(KahaDBStore.this.subscriptionKey(clientId, subscriptionName).toString());
            KahaDBStore.this.store(command, KahaDBStore.this.isEnableJournalDiskSyncs(), null, null);
            this.subscriptionCount.decrementAndGet();
        }
        
        @Override
        public SubscriptionInfo[] getAllSubscriptions() throws IOException {
            final ArrayList<SubscriptionInfo> subscriptions = new ArrayList<SubscriptionInfo>();
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final Iterator<Map.Entry<String, KahaSubscriptionCommand>> iterator = sd.subscriptions.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<String, KahaSubscriptionCommand> entry = iterator.next();
                            final SubscriptionInfo info = (SubscriptionInfo)KahaDBStore.this.wireFormat.unmarshal(new DataInputStream(entry.getValue().getSubscriptionInfo().newInput()));
                            subscriptions.add(info);
                        }
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
            final SubscriptionInfo[] rc = new SubscriptionInfo[subscriptions.size()];
            subscriptions.toArray(rc);
            return rc;
        }
        
        @Override
        public SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                return KahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<SubscriptionInfo, Throwable>)new Transaction.CallableClosure<SubscriptionInfo, IOException>() {
                    @Override
                    public SubscriptionInfo execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final KahaSubscriptionCommand command = sd.subscriptions.get(tx, subscriptionKey);
                        if (command == null) {
                            return null;
                        }
                        return (SubscriptionInfo)KahaDBStore.this.wireFormat.unmarshal(new DataInputStream(command.getSubscriptionInfo().newInput()));
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public int getMessageCount(final String clientId, final String subscriptionName) throws IOException {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                return KahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Integer, Throwable>)new Transaction.CallableClosure<Integer, IOException>() {
                    @Override
                    public Integer execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final LastAck cursorPos = KahaDBStore.this.getLastAck(tx, sd, subscriptionKey);
                        if (cursorPos == null) {
                            return 0;
                        }
                        return (int)KahaDBStore.this.getStoredMessageCount(tx, sd, subscriptionKey);
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            final SubscriptionInfo info = this.lookupSubscription(clientId, subscriptionName);
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final LastAck cursorPos = KahaDBStore.this.getLastAck(tx, sd, subscriptionKey);
                        sd.orderIndex.setBatch(tx, cursorPos);
                        KahaDBTopicMessageStore.this.recoverRolledBackAcks(sd, tx, Integer.MAX_VALUE, listener);
                        final Iterator<Map.Entry<Long, MessageKeys>> iterator = sd.orderIndex.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<Long, MessageKeys> entry = iterator.next();
                            if (KahaDBStore.this.ackedAndPrepared.contains(entry.getValue().messageId)) {
                                continue;
                            }
                            listener.recoverMessage(KahaDBStore.this.loadMessage(entry.getValue().location));
                        }
                        sd.orderIndex.resetCursorPosition();
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
            final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            final SubscriptionInfo info = this.lookupSubscription(clientId, subscriptionName);
            KahaDBStore.this.indexLock.writeLock().lock();
            try {
                KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        sd.orderIndex.resetCursorPosition();
                        MessageOrderCursor moc = sd.subscriptionCursors.get(subscriptionKey);
                        if (moc == null) {
                            final LastAck pos = KahaDBStore.this.getLastAck(tx, sd, subscriptionKey);
                            if (pos == null) {
                                return;
                            }
                            sd.orderIndex.setBatch(tx, pos);
                            moc = sd.orderIndex.cursor;
                        }
                        else {
                            sd.orderIndex.cursor.sync(moc);
                        }
                        Map.Entry<Long, MessageKeys> entry = null;
                        int counter = KahaDBTopicMessageStore.this.recoverRolledBackAcks(sd, tx, maxReturned, listener);
                        final Iterator<Map.Entry<Long, MessageKeys>> iterator = sd.orderIndex.iterator(tx, moc);
                        while (iterator.hasNext()) {
                            entry = iterator.next();
                            if (KahaDBStore.this.ackedAndPrepared.contains(entry.getValue().messageId)) {
                                continue;
                            }
                            if (listener.recoverMessage(KahaDBStore.this.loadMessage(entry.getValue().location))) {
                                ++counter;
                            }
                            if (counter >= maxReturned || !listener.hasSpace()) {
                                break;
                            }
                        }
                        sd.orderIndex.stoppedIterating();
                        if (entry != null) {
                            final MessageOrderCursor copy = sd.orderIndex.cursor.copy();
                            sd.subscriptionCursors.put(subscriptionKey, copy);
                        }
                    }
                });
            }
            finally {
                KahaDBStore.this.indexLock.writeLock().unlock();
            }
        }
        
        @Override
        public void resetBatching(final String clientId, final String subscriptionName) {
            try {
                final String subscriptionKey = KahaDBStore.this.subscriptionKey(clientId, subscriptionName);
                KahaDBStore.this.indexLock.writeLock().lock();
                try {
                    KahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            final StoredDestination sd = KahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                            sd.subscriptionCursors.remove(subscriptionKey);
                        }
                    });
                }
                finally {
                    KahaDBStore.this.indexLock.writeLock().unlock();
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    static class AsyncJobKey
    {
        MessageId id;
        ActiveMQDestination destination;
        
        AsyncJobKey(final MessageId id, final ActiveMQDestination destination) {
            this.id = id;
            this.destination = destination;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof AsyncJobKey && this.id.equals(((AsyncJobKey)obj).id) && this.destination.equals(((AsyncJobKey)obj).destination));
        }
        
        @Override
        public int hashCode() {
            return this.id.hashCode() + this.destination.hashCode();
        }
        
        @Override
        public String toString() {
            return this.destination.getPhysicalName() + "-" + this.id;
        }
    }
    
    class StoreQueueTask implements Runnable, StoreTask
    {
        protected final Message message;
        protected final ConnectionContext context;
        protected final KahaDBMessageStore store;
        protected final InnerFutureTask future;
        protected final AtomicBoolean done;
        protected final AtomicBoolean locked;
        
        public StoreQueueTask(final KahaDBMessageStore store, final ConnectionContext context, final Message message) {
            this.done = new AtomicBoolean();
            this.locked = new AtomicBoolean();
            this.store = store;
            this.context = context;
            this.message = message;
            this.future = new InnerFutureTask(this);
        }
        
        public ListenableFuture<Object> getFuture() {
            return this.future;
        }
        
        @Override
        public boolean cancel() {
            return this.done.compareAndSet(false, true) && this.future.cancel(false);
        }
        
        @Override
        public void aquireLocks() {
            if (this.locked.compareAndSet(false, true)) {
                try {
                    KahaDBStore.this.globalQueueSemaphore.acquire();
                    this.store.acquireLocalAsyncLock();
                    this.message.incrementReferenceCount();
                }
                catch (InterruptedException e) {
                    KahaDBStore.LOG.warn("Failed to aquire lock", e);
                }
            }
        }
        
        @Override
        public void releaseLocks() {
            if (this.locked.compareAndSet(true, false)) {
                this.store.releaseLocalAsyncLock();
                KahaDBStore.this.globalQueueSemaphore.release();
                this.message.decrementReferenceCount();
            }
        }
        
        @Override
        public void run() {
            final KahaDBMessageStore store = this.store;
            ++store.doneTasks;
            try {
                if (this.done.compareAndSet(false, true)) {
                    this.store.addMessage(this.context, this.message);
                    KahaDBStore.this.removeQueueTask(this.store, this.message.getMessageId());
                    this.future.complete();
                }
                else if (KahaDBStore.cancelledTaskModMetric > 0) {
                    final KahaDBMessageStore store2 = this.store;
                    final double canceledTasks = store2.canceledTasks;
                    store2.canceledTasks = canceledTasks + 1.0;
                    if (canceledTasks % KahaDBStore.cancelledTaskModMetric == 0.0) {
                        System.err.println(this.store.dest.getName() + " cancelled: " + this.store.canceledTasks / this.store.doneTasks * 100.0);
                        final KahaDBMessageStore store3 = this.store;
                        final KahaDBMessageStore store4 = this.store;
                        final double n = 0.0;
                        store4.doneTasks = n;
                        store3.canceledTasks = n;
                    }
                }
            }
            catch (Exception e) {
                this.future.setException(e);
            }
        }
        
        protected Message getMessage() {
            return this.message;
        }
        
        private class InnerFutureTask extends FutureTask<Object> implements ListenableFuture<Object>
        {
            private Runnable listener;
            
            public InnerFutureTask(final Runnable runnable) {
                super(runnable, null);
            }
            
            public void setException(final Exception e) {
                super.setException(e);
            }
            
            public void complete() {
                super.set(null);
            }
            
            public void done() {
                this.fireListener();
            }
            
            @Override
            public void addListener(final Runnable listener) {
                this.listener = listener;
                if (this.isDone()) {
                    this.fireListener();
                }
            }
            
            private void fireListener() {
                if (this.listener != null) {
                    try {
                        this.listener.run();
                    }
                    catch (Exception ignored) {
                        KahaDBStore.LOG.warn("Unexpected exception from future {} listener callback {}", this, this.listener, ignored);
                    }
                }
            }
        }
    }
    
    class StoreTopicTask extends StoreQueueTask
    {
        private final int subscriptionCount;
        private final List<String> subscriptionKeys;
        private final KahaDBTopicMessageStore topicStore;
        
        public StoreTopicTask(final KahaDBTopicMessageStore store, final ConnectionContext context, final Message message, final int subscriptionCount) {
            super(store, context, message);
            this.subscriptionKeys = new ArrayList<String>(1);
            this.topicStore = store;
            this.subscriptionCount = subscriptionCount;
        }
        
        @Override
        public void aquireLocks() {
            if (this.locked.compareAndSet(false, true)) {
                try {
                    KahaDBStore.this.globalTopicSemaphore.acquire();
                    this.store.acquireLocalAsyncLock();
                    this.message.incrementReferenceCount();
                }
                catch (InterruptedException e) {
                    KahaDBStore.LOG.warn("Failed to aquire lock", e);
                }
            }
        }
        
        @Override
        public void releaseLocks() {
            if (this.locked.compareAndSet(true, false)) {
                this.message.decrementReferenceCount();
                this.store.releaseLocalAsyncLock();
                KahaDBStore.this.globalTopicSemaphore.release();
            }
        }
        
        public boolean addSubscriptionKey(final String key) {
            synchronized (this.subscriptionKeys) {
                this.subscriptionKeys.add(key);
            }
            return this.subscriptionKeys.size() >= this.subscriptionCount;
        }
        
        @Override
        public void run() {
            final KahaDBMessageStore store = this.store;
            ++store.doneTasks;
            try {
                if (this.done.compareAndSet(false, true)) {
                    this.topicStore.addMessage(this.context, this.message);
                    synchronized (this.subscriptionKeys) {
                        for (final String key : this.subscriptionKeys) {
                            this.topicStore.doAcknowledge(this.context, key, this.message.getMessageId(), null);
                        }
                    }
                    KahaDBStore.this.removeTopicTask(this.topicStore, this.message.getMessageId());
                    this.future.complete();
                }
                else if (KahaDBStore.cancelledTaskModMetric > 0) {
                    final KahaDBMessageStore store2 = this.store;
                    final double canceledTasks = store2.canceledTasks;
                    store2.canceledTasks = canceledTasks + 1.0;
                    if (canceledTasks % KahaDBStore.cancelledTaskModMetric == 0.0) {
                        System.err.println(this.store.dest.getName() + " cancelled: " + this.store.canceledTasks / this.store.doneTasks * 100.0);
                        final KahaDBMessageStore store3 = this.store;
                        final KahaDBMessageStore store4 = this.store;
                        final double n = 0.0;
                        store4.doneTasks = n;
                        store3.canceledTasks = n;
                    }
                }
            }
            catch (Exception e) {
                this.future.setException(e);
            }
        }
    }
    
    public class StoreTaskExecutor extends ThreadPoolExecutor
    {
        public StoreTaskExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit timeUnit, final BlockingQueue<Runnable> queue, final ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, queue, threadFactory);
        }
        
        @Override
        protected void afterExecute(final Runnable runnable, final Throwable throwable) {
            super.afterExecute(runnable, throwable);
            if (runnable instanceof StoreTask) {
                ((StoreTask)runnable).releaseLocks();
            }
        }
    }
    
    public interface StoreTask
    {
        boolean cancel();
        
        void aquireLocks();
        
        void releaseLocks();
    }
}
