// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.scheduler;

import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.io.DataOutput;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.util.IntegerMarshaller;
import org.apache.activemq.store.kahadb.disk.util.StringMarshaller;
import org.apache.activemq.store.kahadb.disk.index.BTreeIndex;
import org.slf4j.LoggerFactory;
import org.apache.activemq.store.SharedFileLocker;
import org.apache.activemq.broker.Locker;
import org.apache.activemq.util.ByteSequence;
import java.util.Set;
import java.util.HashSet;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.util.ServiceStopper;
import java.util.Iterator;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.broker.scheduler.JobScheduler;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import java.io.File;
import org.slf4j.Logger;
import org.apache.activemq.broker.scheduler.JobSchedulerStore;
import org.apache.activemq.broker.LockableServiceSupport;

public class JobSchedulerStoreImpl extends LockableServiceSupport implements JobSchedulerStore
{
    static final Logger LOG;
    private static final int DATABASE_LOCKED_WAIT_DELAY = 10000;
    public static final int CLOSED_STATE = 1;
    public static final int OPEN_STATE = 2;
    private File directory;
    PageFile pageFile;
    private Journal journal;
    protected AtomicLong journalSize;
    private boolean failIfDatabaseIsLocked;
    private int journalMaxFileLength;
    private int journalMaxWriteBatchSize;
    private boolean enableIndexWriteAsync;
    MetaData metaData;
    final MetaDataMarshaller metaDataMarshaller;
    Map<String, JobSchedulerImpl> schedulers;
    
    public JobSchedulerStoreImpl() {
        this.journalSize = new AtomicLong(0L);
        this.journalMaxFileLength = 33554432;
        this.journalMaxWriteBatchSize = 4194304;
        this.enableIndexWriteAsync = false;
        this.metaData = new MetaData(this);
        this.metaDataMarshaller = new MetaDataMarshaller(this);
        this.schedulers = new HashMap<String, JobSchedulerImpl>();
    }
    
    @Override
    public File getDirectory() {
        return this.directory;
    }
    
    @Override
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    @Override
    public long size() {
        if (!this.isStarted()) {
            return 0L;
        }
        try {
            return this.journalSize.get() + this.pageFile.getDiskSize();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public JobScheduler getJobScheduler(final String name) throws Exception {
        JobSchedulerImpl result = this.schedulers.get(name);
        if (result == null) {
            final JobSchedulerImpl js = new JobSchedulerImpl(this);
            js.setName(name);
            this.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    js.createIndexes(tx);
                    js.load(tx);
                    JobSchedulerStoreImpl.this.metaData.storedSchedulers.put(tx, name, js);
                }
            });
            result = js;
            this.schedulers.put(name, js);
            if (this.isStarted()) {
                result.start();
            }
            this.pageFile.flush();
        }
        return result;
    }
    
    @Override
    public synchronized boolean removeJobScheduler(final String name) throws Exception {
        boolean result = false;
        final JobSchedulerImpl js = this.schedulers.remove(name);
        result = (js != null);
        if (result) {
            js.stop();
            this.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    JobSchedulerStoreImpl.this.metaData.storedSchedulers.remove(tx, name);
                    js.destroy(tx);
                }
            });
        }
        return result;
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        if (this.directory == null) {
            this.directory = new File(IOHelper.getDefaultDataDirectory() + File.pathSeparator + "delayedDB");
        }
        IOHelper.mkdirs(this.directory);
        (this.journal = new Journal()).setDirectory(this.directory);
        this.journal.setMaxFileLength(this.getJournalMaxFileLength());
        this.journal.setWriteBatchSize(this.getJournalMaxWriteBatchSize());
        this.journal.setSizeAccumulator(this.journalSize);
        this.journal.start();
        (this.pageFile = new PageFile(this.directory, "scheduleDB")).setWriteBatchSize(1);
        this.pageFile.load();
        this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
            @Override
            public void execute(final Transaction tx) throws IOException {
                if (JobSchedulerStoreImpl.this.pageFile.getPageCount() == 0L) {
                    final Page<MetaData> page = tx.allocate();
                    assert page.getPageId() == 0L;
                    page.set(JobSchedulerStoreImpl.this.metaData);
                    JobSchedulerStoreImpl.this.metaData.page = page;
                    JobSchedulerStoreImpl.this.metaData.createIndexes(tx);
                    tx.store(JobSchedulerStoreImpl.this.metaData.page, JobSchedulerStoreImpl.this.metaDataMarshaller, true);
                }
                else {
                    final Page<MetaData> page = tx.load(0L, (Marshaller<MetaData>)JobSchedulerStoreImpl.this.metaDataMarshaller);
                    JobSchedulerStoreImpl.this.metaData = page.get();
                    JobSchedulerStoreImpl.this.metaData.page = page;
                }
                JobSchedulerStoreImpl.this.metaData.load(tx);
                JobSchedulerStoreImpl.this.metaData.loadScheduler(tx, JobSchedulerStoreImpl.this.schedulers);
                for (final JobSchedulerImpl js : JobSchedulerStoreImpl.this.schedulers.values()) {
                    try {
                        js.start();
                    }
                    catch (Exception e) {
                        JobSchedulerStoreImpl.LOG.error("Failed to load " + js.getName(), e);
                    }
                }
            }
        });
        this.pageFile.flush();
        JobSchedulerStoreImpl.LOG.info(this + " started");
    }
    
    @Override
    protected synchronized void doStop(final ServiceStopper stopper) throws Exception {
        for (final JobSchedulerImpl js : this.schedulers.values()) {
            js.stop();
        }
        if (this.pageFile != null) {
            this.pageFile.unload();
        }
        if (this.journal != null) {
            this.journal.close();
        }
        JobSchedulerStoreImpl.LOG.info(this + " stopped");
    }
    
    synchronized void incrementJournalCount(final Transaction tx, final Location location) throws IOException {
        final int logId = location.getDataFileId();
        final Integer val = this.metaData.journalRC.get(tx, logId);
        final int refCount = (val != null) ? (val + 1) : 1;
        this.metaData.journalRC.put(tx, logId, refCount);
    }
    
    synchronized void decrementJournalCount(final Transaction tx, final Location location) throws IOException {
        final int logId = location.getDataFileId();
        int refCount = this.metaData.journalRC.get(tx, logId);
        if (--refCount <= 0) {
            this.metaData.journalRC.remove(tx, logId);
            final Set<Integer> set = new HashSet<Integer>();
            set.add(logId);
            this.journal.removeDataFiles(set);
        }
        else {
            this.metaData.journalRC.put(tx, logId, refCount);
        }
    }
    
    synchronized ByteSequence getPayload(final Location location) throws IllegalStateException, IOException {
        ByteSequence result = null;
        result = this.journal.read(location);
        return result;
    }
    
    synchronized Location write(final ByteSequence payload, final boolean sync) throws IllegalStateException, IOException {
        return this.journal.write(payload, sync);
    }
    
    PageFile getPageFile() {
        this.pageFile.isLoaded();
        return this.pageFile;
    }
    
    public boolean isFailIfDatabaseIsLocked() {
        return this.failIfDatabaseIsLocked;
    }
    
    public void setFailIfDatabaseIsLocked(final boolean failIfDatabaseIsLocked) {
        this.failIfDatabaseIsLocked = failIfDatabaseIsLocked;
    }
    
    public int getJournalMaxFileLength() {
        return this.journalMaxFileLength;
    }
    
    public void setJournalMaxFileLength(final int journalMaxFileLength) {
        this.journalMaxFileLength = journalMaxFileLength;
    }
    
    public int getJournalMaxWriteBatchSize() {
        return this.journalMaxWriteBatchSize;
    }
    
    public void setJournalMaxWriteBatchSize(final int journalMaxWriteBatchSize) {
        this.journalMaxWriteBatchSize = journalMaxWriteBatchSize;
    }
    
    public boolean isEnableIndexWriteAsync() {
        return this.enableIndexWriteAsync;
    }
    
    public void setEnableIndexWriteAsync(final boolean enableIndexWriteAsync) {
        this.enableIndexWriteAsync = enableIndexWriteAsync;
    }
    
    @Override
    public String toString() {
        return "JobSchedulerStore:" + this.directory;
    }
    
    @Override
    public Locker createDefaultLocker() throws IOException {
        final SharedFileLocker locker = new SharedFileLocker();
        locker.setDirectory(this.getDirectory());
        return locker;
    }
    
    @Override
    public void init() throws Exception {
    }
    
    static {
        LOG = LoggerFactory.getLogger(JobSchedulerStoreImpl.class);
    }
    
    protected class MetaData
    {
        private final JobSchedulerStoreImpl store;
        Page<MetaData> page;
        BTreeIndex<Integer, Integer> journalRC;
        BTreeIndex<String, JobSchedulerImpl> storedSchedulers;
        
        protected MetaData(final JobSchedulerStoreImpl store) {
            this.store = store;
        }
        
        void createIndexes(final Transaction tx) throws IOException {
            this.storedSchedulers = new BTreeIndex<String, JobSchedulerImpl>(JobSchedulerStoreImpl.this.pageFile, tx.allocate().getPageId());
            this.journalRC = new BTreeIndex<Integer, Integer>(JobSchedulerStoreImpl.this.pageFile, tx.allocate().getPageId());
        }
        
        void load(final Transaction tx) throws IOException {
            this.storedSchedulers.setKeyMarshaller(StringMarshaller.INSTANCE);
            this.storedSchedulers.setValueMarshaller(new JobSchedulerMarshaller(this.store));
            this.storedSchedulers.load(tx);
            this.journalRC.setKeyMarshaller(IntegerMarshaller.INSTANCE);
            this.journalRC.setValueMarshaller(IntegerMarshaller.INSTANCE);
            this.journalRC.load(tx);
        }
        
        void loadScheduler(final Transaction tx, final Map<String, JobSchedulerImpl> schedulers) throws IOException {
            final Iterator<Map.Entry<String, JobSchedulerImpl>> i = this.storedSchedulers.iterator(tx);
            while (i.hasNext()) {
                final Map.Entry<String, JobSchedulerImpl> entry = i.next();
                entry.getValue().load(tx);
                schedulers.put(entry.getKey(), entry.getValue());
            }
        }
        
        public void read(final DataInput is) throws IOException {
            (this.storedSchedulers = new BTreeIndex<String, JobSchedulerImpl>(JobSchedulerStoreImpl.this.pageFile, is.readLong())).setKeyMarshaller(StringMarshaller.INSTANCE);
            this.storedSchedulers.setValueMarshaller(new JobSchedulerMarshaller(this.store));
            (this.journalRC = new BTreeIndex<Integer, Integer>(JobSchedulerStoreImpl.this.pageFile, is.readLong())).setKeyMarshaller(IntegerMarshaller.INSTANCE);
            this.journalRC.setValueMarshaller(IntegerMarshaller.INSTANCE);
        }
        
        public void write(final DataOutput os) throws IOException {
            os.writeLong(this.storedSchedulers.getPageId());
            os.writeLong(this.journalRC.getPageId());
        }
    }
    
    class MetaDataMarshaller extends VariableMarshaller<MetaData>
    {
        private final JobSchedulerStoreImpl store;
        
        MetaDataMarshaller(final JobSchedulerStoreImpl store) {
            this.store = store;
        }
        
        @Override
        public MetaData readPayload(final DataInput dataIn) throws IOException {
            final MetaData rc = new MetaData(this.store);
            rc.read(dataIn);
            return rc;
        }
        
        @Override
        public void writePayload(final MetaData object, final DataOutput dataOut) throws IOException {
            object.write(dataOut);
        }
    }
    
    class ValueMarshaller extends VariableMarshaller<List<JobLocation>>
    {
        @Override
        public List<JobLocation> readPayload(final DataInput dataIn) throws IOException {
            final List<JobLocation> result = new ArrayList<JobLocation>();
            for (int size = dataIn.readInt(), i = 0; i < size; ++i) {
                final JobLocation jobLocation = new JobLocation();
                jobLocation.readExternal(dataIn);
                result.add(jobLocation);
            }
            return result;
        }
        
        @Override
        public void writePayload(final List<JobLocation> value, final DataOutput dataOut) throws IOException {
            dataOut.writeInt(value.size());
            for (final JobLocation jobLocation : value) {
                jobLocation.writeExternal(dataOut);
            }
        }
    }
    
    class JobSchedulerMarshaller extends VariableMarshaller<JobSchedulerImpl>
    {
        private final JobSchedulerStoreImpl store;
        
        JobSchedulerMarshaller(final JobSchedulerStoreImpl store) {
            this.store = store;
        }
        
        @Override
        public JobSchedulerImpl readPayload(final DataInput dataIn) throws IOException {
            final JobSchedulerImpl result = new JobSchedulerImpl(this.store);
            result.read(dataIn);
            return result;
        }
        
        @Override
        public void writePayload(final JobSchedulerImpl js, final DataOutput dataOut) throws IOException {
            js.write(dataOut);
        }
    }
}
