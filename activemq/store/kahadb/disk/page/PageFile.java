// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.page;

import java.io.RandomAccessFile;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.util.zip.Checksum;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.zip.Adler32;
import java.util.Collection;
import org.apache.activemq.store.kahadb.disk.util.Sequence;
import org.apache.activemq.util.DataByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.activemq.util.IntrospectionSupport;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.io.InterruptedIOException;
import java.util.Iterator;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.util.LRUCache;
import java.util.Collections;
import org.apache.activemq.util.LFUCache;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.activemq.store.kahadb.disk.util.SequenceSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.RecoverableRandomAccessFile;
import java.io.File;
import org.slf4j.Logger;

public class PageFile
{
    private static final String PAGEFILE_SUFFIX = ".data";
    private static final String RECOVERY_FILE_SUFFIX = ".redo";
    private static final String FREE_FILE_SUFFIX = ".free";
    public static final int DEFAULT_PAGE_SIZE;
    public static final int DEFAULT_WRITE_BATCH_SIZE;
    public static final int DEFAULT_PAGE_CACHE_SIZE;
    private static final int RECOVERY_FILE_HEADER_SIZE = 4096;
    private static final int PAGE_FILE_HEADER_SIZE = 4096;
    private static final Logger LOG;
    private final File directory;
    private final String name;
    private RecoverableRandomAccessFile readFile;
    private RecoverableRandomAccessFile writeFile;
    private RecoverableRandomAccessFile recoveryFile;
    private int pageSize;
    private int recoveryFileMinPageCount;
    private int recoveryFileMaxPageCount;
    private int recoveryPageCount;
    private final AtomicBoolean loaded;
    int writeBatchSize;
    private Map<Long, Page> pageCache;
    private boolean enablePageCaching;
    private int pageCacheSize;
    private boolean enableRecoveryFile;
    private boolean enableDiskSyncs;
    private boolean enabledWriteThread;
    private final AtomicBoolean stopWriter;
    private Thread writerThread;
    private CountDownLatch checkpointLatch;
    private final TreeMap<Long, PageWrite> writes;
    private final AtomicLong nextFreePageId;
    private SequenceSet freeList;
    private final AtomicLong nextTxid;
    private MetaData metaData;
    private final ArrayList<File> tmpFilesForRemoval;
    private boolean useLFRUEviction;
    private float LFUEvictionFactor;
    
    public Transaction tx() {
        this.assertLoaded();
        return new Transaction(this);
    }
    
    public PageFile(final File directory, final String name) {
        this.pageSize = PageFile.DEFAULT_PAGE_SIZE;
        this.recoveryFileMinPageCount = 1000;
        this.recoveryFileMaxPageCount = 10000;
        this.loaded = new AtomicBoolean();
        this.writeBatchSize = PageFile.DEFAULT_WRITE_BATCH_SIZE;
        this.enablePageCaching = true;
        this.pageCacheSize = PageFile.DEFAULT_PAGE_CACHE_SIZE;
        this.enableRecoveryFile = true;
        this.enableDiskSyncs = true;
        this.enabledWriteThread = false;
        this.stopWriter = new AtomicBoolean();
        this.writes = new TreeMap<Long, PageWrite>();
        this.nextFreePageId = new AtomicLong();
        this.freeList = new SequenceSet();
        this.nextTxid = new AtomicLong();
        this.tmpFilesForRemoval = new ArrayList<File>();
        this.useLFRUEviction = false;
        this.LFUEvictionFactor = 0.2f;
        this.directory = directory;
        this.name = name;
    }
    
    public void delete() throws IOException {
        if (this.loaded.get()) {
            throw new IllegalStateException("Cannot delete page file data when the page file is loaded");
        }
        this.delete(this.getMainPageFile());
        this.delete(this.getFreeFile());
        this.delete(this.getRecoveryFile());
    }
    
    public void archive() throws IOException {
        if (this.loaded.get()) {
            throw new IllegalStateException("Cannot delete page file data when the page file is loaded");
        }
        final long timestamp = System.currentTimeMillis();
        this.archive(this.getMainPageFile(), String.valueOf(timestamp));
        this.archive(this.getFreeFile(), String.valueOf(timestamp));
        this.archive(this.getRecoveryFile(), String.valueOf(timestamp));
    }
    
    private void delete(final File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("Could not delete: " + file.getPath());
        }
    }
    
    private void archive(final File file, final String suffix) throws IOException {
        if (file.exists()) {
            final File archive = new File(file.getPath() + "-" + suffix);
            if (!file.renameTo(archive)) {
                throw new IOException("Could not archive: " + file.getPath() + " to " + file.getPath());
            }
        }
    }
    
    public void load() throws IOException, IllegalStateException {
        if (this.loaded.compareAndSet(false, true)) {
            if (this.enablePageCaching) {
                if (this.isUseLFRUEviction()) {
                    this.pageCache = (Map<Long, Page>)Collections.synchronizedMap(new LFUCache<Long, Page>(this.pageCacheSize, this.getLFUEvictionFactor()));
                }
                else {
                    this.pageCache = (Map<Long, Page>)Collections.synchronizedMap(new LRUCache<Long, Page>(this.pageCacheSize, this.pageCacheSize, 0.75f, true));
                }
            }
            final File file = this.getMainPageFile();
            IOHelper.mkdirs(file.getParentFile());
            this.writeFile = new RecoverableRandomAccessFile(file, "rw");
            this.readFile = new RecoverableRandomAccessFile(file, "r");
            if (this.readFile.length() > 0L) {
                this.loadMetaData();
                this.pageSize = this.metaData.getPageSize();
            }
            else {
                (this.metaData = new MetaData()).setFileType(PageFile.class.getName());
                this.metaData.setFileTypeVersion("1");
                this.metaData.setPageSize(this.getPageSize());
                this.metaData.setCleanShutdown(true);
                this.metaData.setFreePages(-1L);
                this.metaData.setLastTxId(0L);
                this.storeMetaData();
            }
            if (this.enableRecoveryFile) {
                this.recoveryFile = new RecoverableRandomAccessFile(this.getRecoveryFile(), "rw");
            }
            if (this.metaData.isCleanShutdown()) {
                this.nextTxid.set(this.metaData.getLastTxId() + 1L);
                if (this.metaData.getFreePages() > 0L) {
                    this.loadFreeList();
                }
            }
            else {
                PageFile.LOG.debug(this.toString() + ", Recovering page file...");
                this.nextTxid.set(this.redoRecoveryUpdates());
                this.freeList = new SequenceSet();
                final Iterator<Page> i = this.tx().iterator(true);
                while (i.hasNext()) {
                    final Page page = i.next();
                    if (page.getType() == 0) {
                        this.freeList.add(page.getPageId());
                    }
                }
            }
            this.metaData.setCleanShutdown(false);
            this.storeMetaData();
            this.getFreeFile().delete();
            if (this.writeFile.length() < 4096L) {
                this.writeFile.setLength(4096L);
            }
            this.nextFreePageId.set((this.writeFile.length() - 4096L) / this.pageSize);
            this.startWriter();
            return;
        }
        throw new IllegalStateException("Cannot load the page file when it is already loaded.");
    }
    
    public void unload() throws IOException {
        if (this.loaded.compareAndSet(true, false)) {
            this.flush();
            try {
                this.stopWriter();
            }
            catch (InterruptedException e) {
                throw new InterruptedIOException();
            }
            if (this.freeList.isEmpty()) {
                this.metaData.setFreePages(0L);
            }
            else {
                this.storeFreeList();
                this.metaData.setFreePages(this.freeList.size());
            }
            this.metaData.setLastTxId(this.nextTxid.get() - 1L);
            this.metaData.setCleanShutdown(true);
            this.storeMetaData();
            if (this.readFile != null) {
                this.readFile.close();
                this.readFile = null;
                this.writeFile.close();
                this.writeFile = null;
                if (this.enableRecoveryFile) {
                    this.recoveryFile.close();
                    this.recoveryFile = null;
                }
                this.freeList.clear();
                if (this.pageCache != null) {
                    this.pageCache = null;
                }
                synchronized (this.writes) {
                    this.writes.clear();
                }
            }
            return;
        }
        throw new IllegalStateException("Cannot unload the page file when it is not loaded");
    }
    
    public boolean isLoaded() {
        return this.loaded.get();
    }
    
    public void flush() throws IOException {
        if (this.enabledWriteThread && this.stopWriter.get()) {
            throw new IOException("Page file already stopped: checkpointing is not allowed");
        }
        final CountDownLatch checkpointLatch;
        synchronized (this.writes) {
            if (this.writes.isEmpty()) {
                return;
            }
            if (!this.enabledWriteThread) {
                this.writeBatch();
                return;
            }
            if (this.checkpointLatch == null) {
                this.checkpointLatch = new CountDownLatch(1);
            }
            checkpointLatch = this.checkpointLatch;
            this.writes.notify();
        }
        try {
            checkpointLatch.await();
        }
        catch (InterruptedException e) {
            final InterruptedIOException ioe = new InterruptedIOException();
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    @Override
    public String toString() {
        return "Page File: " + this.getMainPageFile();
    }
    
    private File getMainPageFile() {
        return new File(this.directory, IOHelper.toFileSystemSafeName(this.name) + ".data");
    }
    
    public File getFreeFile() {
        return new File(this.directory, IOHelper.toFileSystemSafeName(this.name) + ".free");
    }
    
    public File getRecoveryFile() {
        return new File(this.directory, IOHelper.toFileSystemSafeName(this.name) + ".redo");
    }
    
    public long toOffset(final long pageId) {
        return 4096L + pageId * this.pageSize;
    }
    
    private void loadMetaData() throws IOException {
        MetaData v1 = new MetaData();
        MetaData v2 = new MetaData();
        try {
            final Properties p = new Properties();
            final byte[] d = new byte[2048];
            this.readFile.seek(0L);
            this.readFile.readFully(d);
            final ByteArrayInputStream is = new ByteArrayInputStream(d);
            p.load(is);
            IntrospectionSupport.setProperties(v1, p);
        }
        catch (IOException e) {
            v1 = null;
        }
        try {
            final Properties p = new Properties();
            final byte[] d = new byte[2048];
            this.readFile.seek(2048L);
            this.readFile.readFully(d);
            final ByteArrayInputStream is = new ByteArrayInputStream(d);
            p.load(is);
            IntrospectionSupport.setProperties(v2, p);
        }
        catch (IOException e) {
            v2 = null;
        }
        if (v1 == null && v2 == null) {
            throw new IOException("Could not load page file meta data");
        }
        if (v1 == null || v1.metaDataTxId < 0L) {
            this.metaData = v2;
        }
        else if (v2 == null || v1.metaDataTxId < 0L) {
            this.metaData = v1;
        }
        else if (v1.metaDataTxId == v2.metaDataTxId) {
            this.metaData = v1;
        }
        else {
            this.metaData = v2;
        }
    }
    
    private void storeMetaData() throws IOException {
        final MetaData metaData = this.metaData;
        ++metaData.metaDataTxId;
        final Properties p = new Properties();
        IntrospectionSupport.getProperties(this.metaData, p, null);
        final ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
        p.store(os, "");
        if (os.size() > 2048) {
            throw new IOException("Configuation is larger than: 2048");
        }
        final byte[] filler = new byte[2048 - os.size()];
        Arrays.fill(filler, (byte)32);
        os.write(filler);
        os.flush();
        final byte[] d = os.toByteArray();
        this.writeFile.seek(0L);
        this.writeFile.write(d);
        this.writeFile.sync();
        this.writeFile.seek(2048L);
        this.writeFile.write(d);
        this.writeFile.sync();
    }
    
    private void storeFreeList() throws IOException {
        final FileOutputStream os = new FileOutputStream(this.getFreeFile());
        final DataOutputStream dos = new DataOutputStream(os);
        SequenceSet.Marshaller.INSTANCE.writePayload(this.freeList, (DataOutput)dos);
        dos.close();
    }
    
    private void loadFreeList() throws IOException {
        this.freeList.clear();
        final FileInputStream is = new FileInputStream(this.getFreeFile());
        final DataInputStream dis = new DataInputStream(is);
        this.freeList = SequenceSet.Marshaller.INSTANCE.readPayload((DataInput)dis);
        dis.close();
    }
    
    public boolean isEnableRecoveryFile() {
        return this.enableRecoveryFile;
    }
    
    public void setEnableRecoveryFile(final boolean doubleBuffer) {
        this.assertNotLoaded();
        this.enableRecoveryFile = doubleBuffer;
    }
    
    public boolean isEnableDiskSyncs() {
        return this.enableDiskSyncs;
    }
    
    public void setEnableDiskSyncs(final boolean syncWrites) {
        this.assertNotLoaded();
        this.enableDiskSyncs = syncWrites;
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public int getPageContentSize() {
        return this.pageSize - 21;
    }
    
    public void setPageSize(final int pageSize) throws IllegalStateException {
        this.assertNotLoaded();
        this.pageSize = pageSize;
    }
    
    public boolean isEnablePageCaching() {
        return this.enablePageCaching;
    }
    
    public void setEnablePageCaching(final boolean enablePageCaching) {
        this.assertNotLoaded();
        this.enablePageCaching = enablePageCaching;
    }
    
    public int getPageCacheSize() {
        return this.pageCacheSize;
    }
    
    public void setPageCacheSize(final int pageCacheSize) {
        this.assertNotLoaded();
        this.pageCacheSize = pageCacheSize;
    }
    
    public boolean isEnabledWriteThread() {
        return this.enabledWriteThread;
    }
    
    public void setEnableWriteThread(final boolean enableAsyncWrites) {
        this.assertNotLoaded();
        this.enabledWriteThread = enableAsyncWrites;
    }
    
    public long getDiskSize() throws IOException {
        return this.toOffset(this.nextFreePageId.get());
    }
    
    public long getPageCount() {
        return this.nextFreePageId.get();
    }
    
    public int getRecoveryFileMinPageCount() {
        return this.recoveryFileMinPageCount;
    }
    
    public long getFreePageCount() {
        this.assertLoaded();
        return this.freeList.rangeSize();
    }
    
    public void setRecoveryFileMinPageCount(final int recoveryFileMinPageCount) {
        this.assertNotLoaded();
        this.recoveryFileMinPageCount = recoveryFileMinPageCount;
    }
    
    public int getRecoveryFileMaxPageCount() {
        return this.recoveryFileMaxPageCount;
    }
    
    public void setRecoveryFileMaxPageCount(final int recoveryFileMaxPageCount) {
        this.assertNotLoaded();
        this.recoveryFileMaxPageCount = recoveryFileMaxPageCount;
    }
    
    public int getWriteBatchSize() {
        return this.writeBatchSize;
    }
    
    public void setWriteBatchSize(final int writeBatchSize) {
        this.writeBatchSize = writeBatchSize;
    }
    
    public float getLFUEvictionFactor() {
        return this.LFUEvictionFactor;
    }
    
    public void setLFUEvictionFactor(final float LFUEvictionFactor) {
        this.LFUEvictionFactor = LFUEvictionFactor;
    }
    
    public boolean isUseLFRUEviction() {
        return this.useLFRUEviction;
    }
    
    public void setUseLFRUEviction(final boolean useLFRUEviction) {
        this.useLFRUEviction = useLFRUEviction;
    }
    
    void assertLoaded() throws IllegalStateException {
        if (!this.loaded.get()) {
            throw new IllegalStateException("PageFile is not loaded");
        }
    }
    
    void assertNotLoaded() throws IllegalStateException {
        if (this.loaded.get()) {
            throw new IllegalStateException("PageFile is loaded");
        }
    }
    
     <T> Page<T> allocate(final int count) throws IOException {
        this.assertLoaded();
        if (count <= 0) {
            throw new IllegalArgumentException("The allocation count must be larger than zero");
        }
        final Sequence seq = this.freeList.removeFirstSequence(count);
        if (seq == null) {
            Page<T> first = null;
            int c = count;
            long pageId = this.nextFreePageId.getAndAdd(count);
            long writeTxnId = this.nextTxid.getAndAdd(count);
            while (c-- > 0) {
                final Page<T> page = new Page<T>(pageId++);
                page.makeFree(writeTxnId++);
                if (first == null) {
                    first = page;
                }
                this.addToCache(page);
                final DataByteArrayOutputStream out = new DataByteArrayOutputStream(this.pageSize);
                page.write(out);
                this.write(page, out.getData());
            }
            return first;
        }
        final Page<T> page2 = new Page<T>(seq.getFirst());
        page2.makeFree(0L);
        return page2;
    }
    
    long getNextWriteTransactionId() {
        return this.nextTxid.incrementAndGet();
    }
    
    synchronized void readPage(final long pageId, final byte[] data) throws IOException {
        this.readFile.seek(this.toOffset(pageId));
        this.readFile.readFully(data);
    }
    
    public void freePage(final long pageId) {
        this.freeList.add(pageId);
        this.removeFromCache(pageId);
    }
    
    private <T> void write(final Page<T> page, final byte[] data) throws IOException {
        final PageWrite write = new PageWrite(page, data);
        final Map.Entry<Long, PageWrite> entry = new Map.Entry<Long, PageWrite>() {
            @Override
            public Long getKey() {
                return write.getPage().getPageId();
            }
            
            @Override
            public PageWrite getValue() {
                return write;
            }
            
            @Override
            public PageWrite setValue(final PageWrite value) {
                return null;
            }
        };
        final Map.Entry<Long, PageWrite>[] entries = (Map.Entry<Long, PageWrite>[])new Map.Entry[] { entry };
        this.write(Arrays.asList(entries));
    }
    
    void write(final Collection<Map.Entry<Long, PageWrite>> updates) throws IOException {
        synchronized (this.writes) {
            if (this.enabledWriteThread) {
                while (this.writes.size() >= this.writeBatchSize && !this.stopWriter.get()) {
                    try {
                        this.writes.wait();
                        continue;
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new InterruptedIOException();
                    }
                    break;
                }
            }
            boolean longTx = false;
            for (final Map.Entry<Long, PageWrite> entry : updates) {
                final Long key = entry.getKey();
                final PageWrite value = entry.getValue();
                final PageWrite write = this.writes.get(key);
                if (write == null) {
                    this.writes.put(key, value);
                }
                else if (value.currentLocation != -1L) {
                    write.setCurrentLocation(value.page, value.currentLocation, value.length);
                    write.tmpFile = value.tmpFile;
                    longTx = true;
                }
                else {
                    write.setCurrent(value.page, value.current);
                }
            }
            if (longTx || this.canStartWriteBatch()) {
                if (this.enabledWriteThread) {
                    this.writes.notify();
                }
                else {
                    this.writeBatch();
                }
            }
        }
    }
    
    private boolean canStartWriteBatch() {
        final int capacityUsed = this.writes.size() * 100 / this.writeBatchSize;
        if (this.enabledWriteThread) {
            return capacityUsed >= 10 || this.checkpointLatch != null;
        }
        return capacityUsed >= 80 || this.checkpointLatch != null;
    }
    
     <T> Page<T> getFromCache(final long pageId) {
        synchronized (this.writes) {
            final PageWrite pageWrite = this.writes.get(pageId);
            if (pageWrite != null) {
                return (Page<T>)pageWrite.page;
            }
        }
        Page<T> result = null;
        if (this.enablePageCaching) {
            result = this.pageCache.get(pageId);
        }
        return result;
    }
    
    void addToCache(final Page page) {
        if (this.enablePageCaching) {
            this.pageCache.put(page.getPageId(), page);
        }
    }
    
    void removeFromCache(final long pageId) {
        if (this.enablePageCaching) {
            this.pageCache.remove(pageId);
        }
    }
    
    private void pollWrites() {
        try {
            while (!this.stopWriter.get()) {
                synchronized (this.writes) {
                    this.writes.notifyAll();
                    while (this.writes.isEmpty() && this.checkpointLatch == null && !this.stopWriter.get()) {
                        this.writes.wait(100L);
                    }
                    if (this.writes.isEmpty()) {
                        this.releaseCheckpointWaiter();
                    }
                }
                this.writeBatch();
            }
        }
        catch (Throwable e) {
            PageFile.LOG.info("An exception was raised while performing poll writes", e);
        }
        finally {
            this.releaseCheckpointWaiter();
        }
    }
    
    private void writeBatch() throws IOException {
        final ArrayList<PageWrite> batch;
        final CountDownLatch checkpointLatch;
        synchronized (this.writes) {
            batch = new ArrayList<PageWrite>(this.writes.size());
            for (final PageWrite write : this.writes.values()) {
                batch.add(write);
                write.begin();
                if (write.diskBound == null && write.diskBoundLocation == -1L) {
                    batch.remove(write);
                }
            }
            checkpointLatch = this.checkpointLatch;
            this.checkpointLatch = null;
        }
        final Checksum checksum = new Adler32();
        if (this.enableRecoveryFile) {
            this.recoveryFile.seek(4096L);
        }
        for (final PageWrite w : batch) {
            if (this.enableRecoveryFile) {
                try {
                    checksum.update(w.getDiskBound(), 0, this.pageSize);
                }
                catch (Throwable t) {
                    throw IOExceptionSupport.create("Cannot create recovery file. Reason: " + t, t);
                }
                this.recoveryFile.writeLong(w.page.getPageId());
                this.recoveryFile.write(w.getDiskBound(), 0, this.pageSize);
            }
            this.writeFile.seek(this.toOffset(w.page.getPageId()));
            this.writeFile.write(w.getDiskBound(), 0, this.pageSize);
            w.done();
        }
        try {
            if (this.enableRecoveryFile) {
                if (this.recoveryPageCount > this.recoveryFileMaxPageCount) {
                    final int t2 = Math.max(this.recoveryFileMinPageCount, batch.size());
                    this.recoveryFile.setLength(this.recoveryFileSizeForPages(t2));
                }
                this.recoveryFile.seek(0L);
                this.recoveryFile.writeLong(this.nextTxid.get());
                this.recoveryFile.writeLong(checksum.getValue());
                this.recoveryFile.writeInt(batch.size());
            }
            if (this.enableDiskSyncs) {
                if (this.enableRecoveryFile) {
                    this.writeFile.sync();
                }
                this.writeFile.sync();
            }
        }
        finally {
            synchronized (this.writes) {
                for (final PageWrite w2 : batch) {
                    if (w2.isDone()) {
                        this.writes.remove(w2.page.getPageId());
                        if (w2.tmpFile == null || !this.tmpFilesForRemoval.contains(w2.tmpFile)) {
                            continue;
                        }
                        if (!w2.tmpFile.delete()) {
                            throw new IOException("Can't delete temporary KahaDB transaction file:" + w2.tmpFile);
                        }
                        this.tmpFilesForRemoval.remove(w2.tmpFile);
                    }
                }
            }
            if (checkpointLatch != null) {
                checkpointLatch.countDown();
            }
        }
    }
    
    public void removeTmpFile(final File file) {
        this.tmpFilesForRemoval.add(file);
    }
    
    private long recoveryFileSizeForPages(final int pageCount) {
        return 4096 + (this.pageSize + 8) * pageCount;
    }
    
    private void releaseCheckpointWaiter() {
        if (this.checkpointLatch != null) {
            this.checkpointLatch.countDown();
            this.checkpointLatch = null;
        }
    }
    
    private long redoRecoveryUpdates() throws IOException {
        if (!this.enableRecoveryFile) {
            return 0L;
        }
        this.recoveryPageCount = 0;
        if (this.recoveryFile.length() == 0L) {
            this.recoveryFile.write(new byte[4096]);
            this.recoveryFile.setLength(this.recoveryFileSizeForPages(this.recoveryFileMinPageCount));
            return 0L;
        }
        this.recoveryFile.seek(0L);
        final long nextTxId = this.recoveryFile.readLong();
        final long expectedChecksum = this.recoveryFile.readLong();
        final int pageCounter = this.recoveryFile.readInt();
        this.recoveryFile.seek(4096L);
        final Checksum checksum = new Adler32();
        final LinkedHashMap<Long, byte[]> batch = new LinkedHashMap<Long, byte[]>();
        try {
            for (int i = 0; i < pageCounter; ++i) {
                final long offset = this.recoveryFile.readLong();
                final byte[] data = new byte[this.pageSize];
                if (this.recoveryFile.read(data, 0, this.pageSize) != this.pageSize) {
                    return nextTxId;
                }
                checksum.update(data, 0, this.pageSize);
                batch.put(offset, data);
            }
        }
        catch (Exception e) {
            PageFile.LOG.debug("Redo buffer was not fully intact: ", e);
            return nextTxId;
        }
        this.recoveryPageCount = pageCounter;
        if (checksum.getValue() != expectedChecksum) {
            return nextTxId;
        }
        for (final Map.Entry<Long, byte[]> e2 : batch.entrySet()) {
            this.writeFile.seek(this.toOffset(e2.getKey()));
            this.writeFile.write(e2.getValue());
        }
        this.writeFile.sync();
        return nextTxId;
    }
    
    private void startWriter() {
        synchronized (this.writes) {
            if (this.enabledWriteThread) {
                this.stopWriter.set(false);
                (this.writerThread = new Thread("KahaDB Page Writer") {
                    @Override
                    public void run() {
                        PageFile.this.pollWrites();
                    }
                }).setPriority(10);
                this.writerThread.setDaemon(true);
                this.writerThread.start();
            }
        }
    }
    
    private void stopWriter() throws InterruptedException {
        if (this.enabledWriteThread) {
            this.stopWriter.set(true);
            this.writerThread.join();
        }
    }
    
    public File getFile() {
        return this.getMainPageFile();
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    static {
        DEFAULT_PAGE_SIZE = Integer.getInteger("defaultPageSize", 4096);
        DEFAULT_WRITE_BATCH_SIZE = Integer.getInteger("defaultWriteBatchSize", 1000);
        DEFAULT_PAGE_CACHE_SIZE = Integer.getInteger("defaultPageCacheSize", 100);
        LOG = LoggerFactory.getLogger(PageFile.class);
    }
    
    static class PageWrite
    {
        Page page;
        byte[] current;
        byte[] diskBound;
        long currentLocation;
        long diskBoundLocation;
        File tmpFile;
        int length;
        
        public PageWrite(final Page page, final byte[] data) {
            this.currentLocation = -1L;
            this.diskBoundLocation = -1L;
            this.page = page;
            this.current = data;
        }
        
        public PageWrite(final Page page, final long currentLocation, final int length, final File tmpFile) {
            this.currentLocation = -1L;
            this.diskBoundLocation = -1L;
            this.page = page;
            this.currentLocation = currentLocation;
            this.tmpFile = tmpFile;
            this.length = length;
        }
        
        public void setCurrent(final Page page, final byte[] data) {
            this.page = page;
            this.current = data;
            this.currentLocation = -1L;
            this.diskBoundLocation = -1L;
        }
        
        public void setCurrentLocation(final Page page, final long location, final int length) {
            this.page = page;
            this.currentLocation = location;
            this.length = length;
            this.current = null;
        }
        
        @Override
        public String toString() {
            return "[PageWrite:" + this.page.getPageId() + "-" + this.page.getType() + "]";
        }
        
        public Page getPage() {
            return this.page;
        }
        
        public byte[] getDiskBound() throws IOException {
            if (this.diskBound == null && this.diskBoundLocation != -1L) {
                this.diskBound = new byte[this.length];
                final RandomAccessFile file = new RandomAccessFile(this.tmpFile, "r");
                file.seek(this.diskBoundLocation);
                file.read(this.diskBound);
                file.close();
                this.diskBoundLocation = -1L;
            }
            return this.diskBound;
        }
        
        void begin() {
            if (this.currentLocation != -1L) {
                this.diskBoundLocation = this.currentLocation;
            }
            else {
                this.diskBound = this.current;
            }
            this.current = null;
            this.currentLocation = -1L;
        }
        
        boolean done() {
            this.diskBoundLocation = -1L;
            this.diskBound = null;
            return this.current == null || this.currentLocation == -1L;
        }
        
        boolean isDone() {
            return this.diskBound == null && this.diskBoundLocation == -1L && this.current == null && this.currentLocation == -1L;
        }
    }
    
    public static class MetaData
    {
        String fileType;
        String fileTypeVersion;
        long metaDataTxId;
        int pageSize;
        boolean cleanShutdown;
        long lastTxId;
        long freePages;
        
        public MetaData() {
            this.metaDataTxId = -1L;
        }
        
        public String getFileType() {
            return this.fileType;
        }
        
        public void setFileType(final String fileType) {
            this.fileType = fileType;
        }
        
        public String getFileTypeVersion() {
            return this.fileTypeVersion;
        }
        
        public void setFileTypeVersion(final String version) {
            this.fileTypeVersion = version;
        }
        
        public long getMetaDataTxId() {
            return this.metaDataTxId;
        }
        
        public void setMetaDataTxId(final long metaDataTxId) {
            this.metaDataTxId = metaDataTxId;
        }
        
        public int getPageSize() {
            return this.pageSize;
        }
        
        public void setPageSize(final int pageSize) {
            this.pageSize = pageSize;
        }
        
        public boolean isCleanShutdown() {
            return this.cleanShutdown;
        }
        
        public void setCleanShutdown(final boolean cleanShutdown) {
            this.cleanShutdown = cleanShutdown;
        }
        
        public long getLastTxId() {
            return this.lastTxId;
        }
        
        public void setLastTxId(final long lastTxId) {
            this.lastTxId = lastTxId;
        }
        
        public long getFreePages() {
            return this.freePages;
        }
        
        public void setFreePages(final long value) {
            this.freePages = value;
        }
    }
}
