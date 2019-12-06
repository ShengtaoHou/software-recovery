// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.plist;

import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.io.DataOutput;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.util.StringMarshaller;
import org.apache.activemq.store.kahadb.disk.index.BTreeIndex;
import org.slf4j.LoggerFactory;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.store.PList;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.io.IOException;
import org.apache.activemq.broker.BrokerService;
import java.util.HashMap;
import org.apache.activemq.thread.Scheduler;
import java.util.Map;
import org.apache.activemq.util.LockFile;
import org.apache.activemq.store.kahadb.disk.journal.Journal;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import java.io.File;
import org.slf4j.Logger;
import org.apache.activemq.store.JournaledStore;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.util.ServiceSupport;

public class PListStoreImpl extends ServiceSupport implements BrokerServiceAware, Runnable, PListStore, JournaledStore
{
    static final Logger LOG;
    private static final int DATABASE_LOCKED_WAIT_DELAY = 10000;
    static final int CLOSED_STATE = 1;
    static final int OPEN_STATE = 2;
    private File directory;
    PageFile pageFile;
    private Journal journal;
    private LockFile lockFile;
    private boolean failIfDatabaseIsLocked;
    private int journalMaxFileLength;
    private int journalMaxWriteBatchSize;
    private boolean enableIndexWriteAsync;
    private boolean initialized;
    private boolean lazyInit;
    MetaData metaData;
    final MetaDataMarshaller metaDataMarshaller;
    Map<String, PListImpl> persistentLists;
    final Object indexLock;
    private Scheduler scheduler;
    private long cleanupInterval;
    private int indexPageSize;
    private int indexCacheSize;
    private int indexWriteBatchSize;
    private boolean indexEnablePageCaching;
    
    public PListStoreImpl() {
        this.journalMaxFileLength = 33554432;
        this.journalMaxWriteBatchSize = 4194304;
        this.enableIndexWriteAsync = false;
        this.initialized = false;
        this.lazyInit = true;
        this.metaData = new MetaData(this);
        this.metaDataMarshaller = new MetaDataMarshaller(this);
        this.persistentLists = new HashMap<String, PListImpl>();
        this.indexLock = new Object();
        this.cleanupInterval = 30000L;
        this.indexPageSize = PageFile.DEFAULT_PAGE_SIZE;
        this.indexCacheSize = PageFile.DEFAULT_PAGE_CACHE_SIZE;
        this.indexWriteBatchSize = PageFile.DEFAULT_WRITE_BATCH_SIZE;
        this.indexEnablePageCaching = true;
    }
    
    public Object getIndexLock() {
        return this.indexLock;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.scheduler = brokerService.getScheduler();
    }
    
    public int getIndexPageSize() {
        return this.indexPageSize;
    }
    
    public int getIndexCacheSize() {
        return this.indexCacheSize;
    }
    
    public int getIndexWriteBatchSize() {
        return this.indexWriteBatchSize;
    }
    
    public void setIndexPageSize(final int indexPageSize) {
        this.indexPageSize = indexPageSize;
    }
    
    public void setIndexCacheSize(final int indexCacheSize) {
        this.indexCacheSize = indexCacheSize;
    }
    
    public void setIndexWriteBatchSize(final int indexWriteBatchSize) {
        this.indexWriteBatchSize = indexWriteBatchSize;
    }
    
    public boolean getIndexEnablePageCaching() {
        return this.indexEnablePageCaching;
    }
    
    public void setIndexEnablePageCaching(final boolean indexEnablePageCaching) {
        this.indexEnablePageCaching = indexEnablePageCaching;
    }
    
    public Journal getJournal() {
        return this.journal;
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
        synchronized (this) {
            if (!this.initialized) {
                return 0L;
            }
        }
        try {
            return this.journal.getDiskSize() + this.pageFile.getDiskSize();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public PListImpl getPList(final String name) throws Exception {
        if (!this.isStarted()) {
            throw new IllegalStateException("Not started");
        }
        this.intialize();
        synchronized (this.indexLock) {
            synchronized (this) {
                PListImpl result = this.persistentLists.get(name);
                if (result == null) {
                    final PListImpl pl = new PListImpl(this);
                    pl.setName(name);
                    this.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            pl.setHeadPageId(tx.allocate().getPageId());
                            pl.load(tx);
                            PListStoreImpl.this.metaData.lists.put(tx, name, pl);
                        }
                    });
                    result = pl;
                    this.persistentLists.put(name, pl);
                }
                final PListImpl toLoad = result;
                this.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        toLoad.load(tx);
                    }
                });
                return result;
            }
        }
    }
    
    @Override
    public boolean removePList(final String name) throws Exception {
        boolean result = false;
        synchronized (this.indexLock) {
            synchronized (this) {
                final PList pl = this.persistentLists.remove(name);
                result = (pl != null);
                if (result) {
                    this.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            PListStoreImpl.this.metaData.lists.remove(tx, name);
                            pl.destroy();
                        }
                    });
                }
            }
        }
        return result;
    }
    
    protected synchronized void intialize() throws Exception {
        if (this.isStarted() && !this.initialized) {
            if (this.directory == null) {
                this.directory = new File(IOHelper.getDefaultDataDirectory() + File.pathSeparator + "delayedDB");
            }
            IOHelper.mkdirs(this.directory);
            this.lock();
            (this.journal = new Journal()).setDirectory(this.directory);
            this.journal.setMaxFileLength(this.getJournalMaxFileLength());
            this.journal.setWriteBatchSize(this.getJournalMaxWriteBatchSize());
            this.journal.start();
            (this.pageFile = new PageFile(this.directory, "tmpDB")).setEnablePageCaching(this.getIndexEnablePageCaching());
            this.pageFile.setPageSize(this.getIndexPageSize());
            this.pageFile.setWriteBatchSize(this.getIndexWriteBatchSize());
            this.pageFile.setPageCacheSize(this.getIndexCacheSize());
            this.pageFile.load();
            this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    if (PListStoreImpl.this.pageFile.getPageCount() == 0L) {
                        final Page<MetaData> page = tx.allocate();
                        assert page.getPageId() == 0L;
                        page.set(PListStoreImpl.this.metaData);
                        PListStoreImpl.this.metaData.page = page;
                        PListStoreImpl.this.metaData.createIndexes(tx);
                        tx.store(PListStoreImpl.this.metaData.page, PListStoreImpl.this.metaDataMarshaller, true);
                    }
                    else {
                        final Page<MetaData> page = tx.load(0L, (Marshaller<MetaData>)PListStoreImpl.this.metaDataMarshaller);
                        PListStoreImpl.this.metaData = page.get();
                        PListStoreImpl.this.metaData.page = page;
                    }
                    PListStoreImpl.this.metaData.load(tx);
                    PListStoreImpl.this.metaData.loadLists(tx, PListStoreImpl.this.persistentLists);
                }
            });
            this.pageFile.flush();
            if (this.cleanupInterval > 0L) {
                if (this.scheduler == null) {
                    (this.scheduler = new Scheduler(PListStoreImpl.class.getSimpleName())).start();
                }
                this.scheduler.executePeriodically(this, this.cleanupInterval);
            }
            this.initialized = true;
            PListStoreImpl.LOG.info(this + " initialized");
        }
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        if (!this.lazyInit) {
            this.intialize();
        }
        PListStoreImpl.LOG.info(this + " started");
    }
    
    @Override
    protected synchronized void doStop(final ServiceStopper stopper) throws Exception {
        if (this.scheduler != null && PListStoreImpl.class.getSimpleName().equals(this.scheduler.getName())) {
            this.scheduler.stop();
            this.scheduler = null;
        }
        for (final PListImpl pl : this.persistentLists.values()) {
            pl.unload(null);
        }
        if (this.pageFile != null) {
            this.pageFile.unload();
        }
        if (this.journal != null) {
            this.journal.close();
        }
        if (this.lockFile != null) {
            this.lockFile.unlock();
        }
        this.lockFile = null;
        this.initialized = false;
        PListStoreImpl.LOG.info(this + " stopped");
    }
    
    @Override
    public void run() {
        try {
            if (this.isStopping()) {
                return;
            }
            final int lastJournalFileId = this.journal.getLastAppendLocation().getDataFileId();
            final Set<Integer> candidates = this.journal.getFileMap().keySet();
            PListStoreImpl.LOG.trace("Full gc candidate set:" + candidates);
            if (candidates.size() > 1) {
                final Iterator<Integer> iterator = candidates.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next() >= lastJournalFileId) {
                        iterator.remove();
                    }
                }
                List<PListImpl> plists = null;
                synchronized (this.indexLock) {
                    synchronized (this) {
                        plists = new ArrayList<PListImpl>(this.persistentLists.values());
                    }
                }
                for (final PListImpl list : plists) {
                    list.claimFileLocations(candidates);
                    if (this.isStopping()) {
                        return;
                    }
                    PListStoreImpl.LOG.trace("Remaining gc candidate set after refs from: " + list.getName() + ":" + candidates);
                }
                PListStoreImpl.LOG.trace("GC Candidate set:" + candidates);
                this.journal.removeDataFiles(candidates);
            }
        }
        catch (IOException e) {
            PListStoreImpl.LOG.error("Exception on periodic cleanup: " + e, e);
        }
    }
    
    ByteSequence getPayload(final Location location) throws IllegalStateException, IOException {
        ByteSequence result = null;
        result = this.journal.read(location);
        return result;
    }
    
    Location write(final ByteSequence payload, final boolean sync) throws IllegalStateException, IOException {
        return this.journal.write(payload, sync);
    }
    
    private void lock() throws IOException {
        if (this.lockFile == null) {
            final File lockFileName = new File(this.directory, "lock");
            this.lockFile = new LockFile(lockFileName, true);
            if (this.failIfDatabaseIsLocked) {
                this.lockFile.lock();
            }
            else {
                while (true) {
                    try {
                        this.lockFile.lock();
                    }
                    catch (IOException e) {
                        PListStoreImpl.LOG.info("Database " + lockFileName + " is locked... waiting " + 10 + " seconds for the database to be unlocked. Reason: " + e);
                        try {
                            Thread.sleep(10000L);
                        }
                        catch (InterruptedException ex) {}
                        continue;
                    }
                    break;
                }
            }
        }
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
    
    @Override
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
    
    public long getCleanupInterval() {
        return this.cleanupInterval;
    }
    
    public void setCleanupInterval(final long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }
    
    public boolean isLazyInit() {
        return this.lazyInit;
    }
    
    public void setLazyInit(final boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    
    @Override
    public String toString() {
        final String path = (this.getDirectory() != null) ? this.getDirectory().getAbsolutePath() : "DIRECTORY_NOT_SET";
        return "PListStore:[" + path + "]";
    }
    
    static {
        LOG = LoggerFactory.getLogger(PListStoreImpl.class);
    }
    
    protected class MetaData
    {
        private final PListStoreImpl store;
        Page<MetaData> page;
        BTreeIndex<String, PListImpl> lists;
        
        protected MetaData(final PListStoreImpl store) {
            this.store = store;
        }
        
        void createIndexes(final Transaction tx) throws IOException {
            this.lists = new BTreeIndex<String, PListImpl>(PListStoreImpl.this.pageFile, tx.allocate().getPageId());
        }
        
        void load(final Transaction tx) throws IOException {
            this.lists.setKeyMarshaller(StringMarshaller.INSTANCE);
            this.lists.setValueMarshaller(new PListMarshaller(this.store));
            this.lists.load(tx);
        }
        
        void loadLists(final Transaction tx, final Map<String, PListImpl> lists) throws IOException {
            final Iterator<Map.Entry<String, PListImpl>> i = this.lists.iterator(tx);
            while (i.hasNext()) {
                final Map.Entry<String, PListImpl> entry = i.next();
                entry.getValue().load(tx);
                lists.put(entry.getKey(), entry.getValue());
            }
        }
        
        public void read(final DataInput is) throws IOException {
            (this.lists = new BTreeIndex<String, PListImpl>(PListStoreImpl.this.pageFile, is.readLong())).setKeyMarshaller(StringMarshaller.INSTANCE);
            this.lists.setValueMarshaller(new PListMarshaller(this.store));
        }
        
        public void write(final DataOutput os) throws IOException {
            os.writeLong(this.lists.getPageId());
        }
    }
    
    class MetaDataMarshaller extends VariableMarshaller<MetaData>
    {
        private final PListStoreImpl store;
        
        MetaDataMarshaller(final PListStoreImpl store) {
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
    
    class PListMarshaller extends VariableMarshaller<PListImpl>
    {
        private final PListStoreImpl store;
        
        PListMarshaller(final PListStoreImpl store) {
            this.store = store;
        }
        
        @Override
        public PListImpl readPayload(final DataInput dataIn) throws IOException {
            final PListImpl result = new PListImpl(this.store);
            result.read(dataIn);
            return result;
        }
        
        @Override
        public void writePayload(final PListImpl list, final DataOutput dataOut) throws IOException {
            list.write(dataOut);
        }
    }
}
