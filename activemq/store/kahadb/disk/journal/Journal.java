// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import org.apache.activemq.store.kahadb.disk.util.LinkedNode;
import org.slf4j.LoggerFactory;
import java.util.TreeMap;
import java.util.Set;
import java.util.zip.Checksum;
import java.util.zip.Adler32;
import org.apache.activemq.util.DataByteArrayInputStream;
import org.apache.activemq.store.kahadb.disk.util.Sequence;
import java.io.UnsupportedEncodingException;
import java.util.TimerTask;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.store.kahadb.disk.util.SchedulerTimerTask;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.io.FilenameFilter;
import org.apache.activemq.util.ByteSequence;
import java.io.IOException;
import org.apache.activemq.util.DataByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.activemq.store.kahadb.disk.util.LinkedNodeList;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;

public class Journal
{
    public static final String CALLER_BUFFER_APPENDER = "org.apache.kahadb.journal.CALLER_BUFFER_APPENDER";
    public static final boolean callerBufferAppender;
    private static final int MAX_BATCH_SIZE = 33554432;
    public static final int RECORD_HEAD_SPACE = 5;
    public static final byte USER_RECORD_TYPE = 1;
    public static final byte BATCH_CONTROL_RECORD_TYPE = 2;
    public static final byte[] BATCH_CONTROL_RECORD_MAGIC;
    public static final int BATCH_CONTROL_RECORD_SIZE;
    public static final byte[] BATCH_CONTROL_RECORD_HEADER;
    public static final String DEFAULT_DIRECTORY = ".";
    public static final String DEFAULT_ARCHIVE_DIRECTORY = "data-archive";
    public static final String DEFAULT_FILE_PREFIX = "db-";
    public static final String DEFAULT_FILE_SUFFIX = ".log";
    public static final int DEFAULT_MAX_FILE_LENGTH = 33554432;
    public static final int DEFAULT_CLEANUP_INTERVAL = 30000;
    public static final int PREFERED_DIFF = 524288;
    public static final int DEFAULT_MAX_WRITE_BATCH_SIZE = 4194304;
    private static final Logger LOG;
    protected final Map<WriteKey, WriteCommand> inflightWrites;
    protected File directory;
    protected File directoryArchive;
    protected String filePrefix;
    protected String fileSuffix;
    protected boolean started;
    protected int maxFileLength;
    protected int preferedFileLength;
    protected int writeBatchSize;
    protected FileAppender appender;
    protected DataFileAccessorPool accessorPool;
    protected Map<Integer, DataFile> fileMap;
    protected Map<File, DataFile> fileByFileMap;
    protected LinkedNodeList<DataFile> dataFiles;
    protected final AtomicReference<Location> lastAppendLocation;
    protected Runnable cleanupTask;
    protected AtomicLong totalLength;
    protected boolean archiveDataLogs;
    private ReplicationTarget replicationTarget;
    protected boolean checksum;
    protected boolean checkForCorruptionOnStartup;
    protected boolean enableAsyncDiskSync;
    private Timer timer;
    
    public Journal() {
        this.inflightWrites = new ConcurrentHashMap<WriteKey, WriteCommand>();
        this.directory = new File(".");
        this.directoryArchive = new File("data-archive");
        this.filePrefix = "db-";
        this.fileSuffix = ".log";
        this.maxFileLength = 33554432;
        this.preferedFileLength = 33030144;
        this.writeBatchSize = 4194304;
        this.fileMap = new HashMap<Integer, DataFile>();
        this.fileByFileMap = new LinkedHashMap<File, DataFile>();
        this.dataFiles = new LinkedNodeList<DataFile>();
        this.lastAppendLocation = new AtomicReference<Location>();
        this.totalLength = new AtomicLong();
        this.enableAsyncDiskSync = true;
    }
    
    private static byte[] createBatchControlRecordHeader() {
        try {
            final DataByteArrayOutputStream os = new DataByteArrayOutputStream();
            os.writeInt(Journal.BATCH_CONTROL_RECORD_SIZE);
            os.writeByte(2);
            os.write(Journal.BATCH_CONTROL_RECORD_MAGIC);
            final ByteSequence sequence = os.toByteSequence();
            sequence.compact();
            return sequence.getData();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not create batch control record header.", e);
        }
    }
    
    public synchronized void start() throws IOException {
        if (this.started) {
            return;
        }
        final long start = System.currentTimeMillis();
        this.accessorPool = new DataFileAccessorPool(this);
        this.started = true;
        this.preferedFileLength = Math.max(524288, this.getMaxFileLength() - 524288);
        this.appender = (Journal.callerBufferAppender ? new CallerBufferingDataFileAppender(this) : new DataFileAppender(this));
        final File[] files = this.directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String n) {
                return dir.equals(Journal.this.directory) && n.startsWith(Journal.this.filePrefix) && n.endsWith(Journal.this.fileSuffix);
            }
        });
        if (files != null) {
            for (final File file : files) {
                try {
                    final String n = file.getName();
                    final String numStr = n.substring(this.filePrefix.length(), n.length() - this.fileSuffix.length());
                    final int num = Integer.parseInt(numStr);
                    final DataFile dataFile = new DataFile(file, num, this.preferedFileLength);
                    this.fileMap.put(dataFile.getDataFileId(), dataFile);
                    this.totalLength.addAndGet(dataFile.getLength());
                }
                catch (NumberFormatException ex) {}
            }
            final List<DataFile> l = new ArrayList<DataFile>(this.fileMap.values());
            Collections.sort(l);
            for (final DataFile df : l) {
                if (df.getLength() == 0) {
                    Journal.LOG.info("ignoring zero length, partially initialised journal data file: " + df);
                }
                else {
                    this.dataFiles.addLast(df);
                    this.fileByFileMap.put(df.getFile(), df);
                    if (!this.isCheckForCorruptionOnStartup()) {
                        continue;
                    }
                    this.lastAppendLocation.set(this.recoveryCheck(df));
                }
            }
        }
        this.getCurrentWriteFile();
        if (this.lastAppendLocation.get() == null) {
            final DataFile df2 = this.dataFiles.getTail();
            this.lastAppendLocation.set(this.recoveryCheck(df2));
        }
        this.cleanupTask = new Runnable() {
            @Override
            public void run() {
                Journal.this.cleanup();
            }
        };
        this.timer = new Timer("KahaDB Scheduler", true);
        final TimerTask task = new SchedulerTimerTask(this.cleanupTask);
        this.timer.scheduleAtFixedRate(task, 30000L, 30000L);
        final long end = System.currentTimeMillis();
        Journal.LOG.trace("Startup took: " + (end - start) + " ms");
    }
    
    private static byte[] bytes(final String string) {
        try {
            return string.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected Location recoveryCheck(final DataFile dataFile) throws IOException {
        final Location location = new Location();
        location.setDataFileId(dataFile.getDataFileId());
        location.setOffset(0);
        final DataFileAccessor reader = this.accessorPool.openDataFileAccessor(dataFile);
        try {
            while (true) {
                final int size = this.checkBatchRecord(reader, location.getOffset());
                if (size >= 0) {
                    location.setOffset(location.getOffset() + Journal.BATCH_CONTROL_RECORD_SIZE + size);
                }
                else {
                    final int nextOffset = this.findNextBatchRecord(reader, location.getOffset() + 1);
                    if (nextOffset < 0) {
                        break;
                    }
                    final Sequence sequence = new Sequence(location.getOffset(), nextOffset - 1);
                    Journal.LOG.info("Corrupt journal records found in '" + dataFile.getFile() + "' between offsets: " + sequence);
                    dataFile.corruptedBlocks.add(sequence);
                    location.setOffset(nextOffset);
                }
            }
        }
        catch (IOException ex) {}
        finally {
            this.accessorPool.closeDataFileAccessor(reader);
        }
        final int existingLen = dataFile.getLength();
        dataFile.setLength(location.getOffset());
        if (existingLen > dataFile.getLength()) {
            this.totalLength.addAndGet(dataFile.getLength() - existingLen);
        }
        if (!dataFile.corruptedBlocks.isEmpty() && dataFile.corruptedBlocks.getTail().getLast() + 1L == location.getOffset()) {
            dataFile.setLength((int)dataFile.corruptedBlocks.removeLastSequence().getFirst());
        }
        return location;
    }
    
    private int findNextBatchRecord(final DataFileAccessor reader, int offset) throws IOException {
        final ByteSequence header = new ByteSequence(Journal.BATCH_CONTROL_RECORD_HEADER);
        final byte[] data = new byte[4096];
        ByteSequence bs = new ByteSequence(data, 0, reader.read(offset, data));
        int pos = 0;
        while (true) {
            pos = bs.indexOf(header, pos);
            if (pos >= 0) {
                return offset + pos;
            }
            if (bs.length != data.length) {
                return -1;
            }
            offset += bs.length - Journal.BATCH_CONTROL_RECORD_HEADER.length;
            bs = new ByteSequence(data, 0, reader.read(offset, data));
            pos = 0;
        }
    }
    
    public int checkBatchRecord(final DataFileAccessor reader, final int offset) throws IOException {
        final byte[] controlRecord = new byte[Journal.BATCH_CONTROL_RECORD_SIZE];
        final DataByteArrayInputStream controlIs = new DataByteArrayInputStream(controlRecord);
        reader.readFully(offset, controlRecord);
        for (int i = 0; i < Journal.BATCH_CONTROL_RECORD_HEADER.length; ++i) {
            if (controlIs.readByte() != Journal.BATCH_CONTROL_RECORD_HEADER[i]) {
                return -1;
            }
        }
        final int size = controlIs.readInt();
        if (size > 33554432) {
            return -1;
        }
        if (this.isChecksum()) {
            final long expectedChecksum = controlIs.readLong();
            if (expectedChecksum == 0L) {
                return size;
            }
            final byte[] data = new byte[size];
            reader.readFully(offset + Journal.BATCH_CONTROL_RECORD_SIZE, data);
            final Checksum checksum = new Adler32();
            checksum.update(data, 0, data.length);
            if (expectedChecksum != checksum.getValue()) {
                return -1;
            }
        }
        return size;
    }
    
    void addToTotalLength(final int size) {
        this.totalLength.addAndGet(size);
    }
    
    public long length() {
        return this.totalLength.get();
    }
    
    synchronized DataFile getCurrentWriteFile() throws IOException {
        if (this.dataFiles.isEmpty()) {
            this.rotateWriteFile();
        }
        return this.dataFiles.getTail();
    }
    
    synchronized DataFile rotateWriteFile() {
        final int nextNum = this.dataFiles.isEmpty() ? 1 : (this.dataFiles.getTail().getDataFileId() + 1);
        final File file = this.getFile(nextNum);
        final DataFile nextWriteFile = new DataFile(file, nextNum, this.preferedFileLength);
        this.fileMap.put(nextWriteFile.getDataFileId(), nextWriteFile);
        this.fileByFileMap.put(file, nextWriteFile);
        this.dataFiles.addLast(nextWriteFile);
        return nextWriteFile;
    }
    
    public File getFile(final int nextNum) {
        final String fileName = this.filePrefix + nextNum + this.fileSuffix;
        final File file = new File(this.directory, fileName);
        return file;
    }
    
    synchronized DataFile getDataFile(final Location item) throws IOException {
        final Integer key = item.getDataFileId();
        final DataFile dataFile = this.fileMap.get(key);
        if (dataFile == null) {
            Journal.LOG.error("Looking for key " + key + " but not found in fileMap: " + this.fileMap);
            throw new IOException("Could not locate data file " + this.getFile(item.getDataFileId()));
        }
        return dataFile;
    }
    
    synchronized File getFile(final Location item) throws IOException {
        final Integer key = item.getDataFileId();
        final DataFile dataFile = this.fileMap.get(key);
        if (dataFile == null) {
            Journal.LOG.error("Looking for key " + key + " but not found in fileMap: " + this.fileMap);
            throw new IOException("Could not locate data file " + this.getFile(item.getDataFileId()));
        }
        return dataFile.getFile();
    }
    
    private DataFile getNextDataFile(final DataFile dataFile) {
        return dataFile.getNext();
    }
    
    public synchronized void close() throws IOException {
        if (!this.started) {
            return;
        }
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.accessorPool.close();
        this.appender.close();
        this.fileMap.clear();
        this.fileByFileMap.clear();
        this.dataFiles.clear();
        this.lastAppendLocation.set(null);
        this.started = false;
    }
    
    protected synchronized void cleanup() {
        if (this.accessorPool != null) {
            this.accessorPool.disposeUnused();
        }
    }
    
    public synchronized boolean delete() throws IOException {
        this.appender.close();
        this.accessorPool.close();
        boolean result = true;
        for (final DataFile dataFile : this.fileMap.values()) {
            this.totalLength.addAndGet(-dataFile.getLength());
            result &= dataFile.delete();
        }
        this.fileMap.clear();
        this.fileByFileMap.clear();
        this.lastAppendLocation.set(null);
        this.dataFiles = new LinkedNodeList<DataFile>();
        this.accessorPool = new DataFileAccessorPool(this);
        this.appender = new DataFileAppender(this);
        return result;
    }
    
    public synchronized void removeDataFiles(final Set<Integer> files) throws IOException {
        for (final Integer key : files) {
            if (key >= this.lastAppendLocation.get().getDataFileId()) {
                continue;
            }
            final DataFile dataFile = this.fileMap.get(key);
            if (dataFile == null) {
                continue;
            }
            this.forceRemoveDataFile(dataFile);
        }
    }
    
    private synchronized void forceRemoveDataFile(final DataFile dataFile) throws IOException {
        this.accessorPool.disposeDataFileAccessors(dataFile);
        this.fileByFileMap.remove(dataFile.getFile());
        this.fileMap.remove(dataFile.getDataFileId());
        this.totalLength.addAndGet(-dataFile.getLength());
        dataFile.unlink();
        if (this.archiveDataLogs) {
            dataFile.move(this.getDirectoryArchive());
            Journal.LOG.debug("moved data file " + dataFile + " to " + this.getDirectoryArchive());
        }
        else if (dataFile.delete()) {
            Journal.LOG.debug("Discarded data file " + dataFile);
        }
        else {
            Journal.LOG.warn("Failed to discard data file " + dataFile.getFile());
        }
    }
    
    public int getMaxFileLength() {
        return this.maxFileLength;
    }
    
    public void setMaxFileLength(final int maxFileLength) {
        this.maxFileLength = maxFileLength;
    }
    
    @Override
    public String toString() {
        return this.directory.toString();
    }
    
    public synchronized void appendedExternally(final Location loc, final int length) throws IOException {
        DataFile dataFile = null;
        if (this.dataFiles.getTail().getDataFileId() == loc.getDataFileId()) {
            dataFile = this.dataFiles.getTail();
            dataFile.incrementLength(length);
        }
        else {
            if (this.dataFiles.getTail().getDataFileId() + 1 != loc.getDataFileId()) {
                throw new IOException("Invalid external append.");
            }
            final int nextNum = loc.getDataFileId();
            final File file = this.getFile(nextNum);
            dataFile = new DataFile(file, nextNum, this.preferedFileLength);
            this.fileMap.put(dataFile.getDataFileId(), dataFile);
            this.fileByFileMap.put(file, dataFile);
            this.dataFiles.addLast(dataFile);
        }
    }
    
    public synchronized Location getNextLocation(final Location location) throws IOException, IllegalStateException {
        Location cur = null;
        while (true) {
            if (cur == null) {
                if (location == null) {
                    final DataFile head = this.dataFiles.getHead();
                    if (head == null) {
                        return null;
                    }
                    cur = new Location();
                    cur.setDataFileId(head.getDataFileId());
                    cur.setOffset(0);
                }
                else if (location.getSize() == -1) {
                    cur = new Location(location);
                }
                else {
                    cur = new Location(location);
                    cur.setOffset(location.getOffset() + location.getSize());
                }
            }
            else {
                cur.setOffset(cur.getOffset() + cur.getSize());
            }
            DataFile dataFile = this.getDataFile(cur);
            if (dataFile.getLength() <= cur.getOffset()) {
                dataFile = this.getNextDataFile(dataFile);
                if (dataFile == null) {
                    return null;
                }
                cur.setDataFileId(dataFile.getDataFileId());
                cur.setOffset(0);
            }
            final DataFileAccessor reader = this.accessorPool.openDataFileAccessor(dataFile);
            try {
                reader.readLocationDetails(cur);
            }
            finally {
                this.accessorPool.closeDataFileAccessor(reader);
            }
            if (cur.getType() == 0) {
                return null;
            }
            if (cur.getType() == 1) {
                return cur;
            }
        }
    }
    
    public synchronized Location getNextLocation(final File file, final Location lastLocation, final boolean thisFileOnly) throws IllegalStateException, IOException {
        final DataFile df = this.fileByFileMap.get(file);
        return this.getNextLocation(df, lastLocation, thisFileOnly);
    }
    
    public synchronized Location getNextLocation(DataFile dataFile, final Location lastLocation, final boolean thisFileOnly) throws IOException, IllegalStateException {
        Location cur = null;
        while (true) {
            if (cur == null) {
                if (lastLocation == null) {
                    final DataFile head = dataFile.getHeadNode();
                    cur = new Location();
                    cur.setDataFileId(head.getDataFileId());
                    cur.setOffset(0);
                }
                else {
                    cur = new Location(lastLocation);
                    cur.setOffset(cur.getOffset() + cur.getSize());
                }
            }
            else {
                cur.setOffset(cur.getOffset() + cur.getSize());
            }
            if (dataFile.getLength() <= cur.getOffset()) {
                if (thisFileOnly) {
                    return null;
                }
                dataFile = this.getNextDataFile(dataFile);
                if (dataFile == null) {
                    return null;
                }
                cur.setDataFileId(dataFile.getDataFileId());
                cur.setOffset(0);
            }
            final DataFileAccessor reader = this.accessorPool.openDataFileAccessor(dataFile);
            try {
                reader.readLocationDetails(cur);
            }
            finally {
                this.accessorPool.closeDataFileAccessor(reader);
            }
            if (cur.getType() == 0) {
                return null;
            }
            if (cur.getType() > 0) {
                return cur;
            }
        }
    }
    
    public synchronized ByteSequence read(final Location location) throws IOException, IllegalStateException {
        final DataFile dataFile = this.getDataFile(location);
        final DataFileAccessor reader = this.accessorPool.openDataFileAccessor(dataFile);
        ByteSequence rc = null;
        try {
            rc = reader.readRecord(location);
        }
        finally {
            this.accessorPool.closeDataFileAccessor(reader);
        }
        return rc;
    }
    
    public Location write(final ByteSequence data, final boolean sync) throws IOException, IllegalStateException {
        final Location loc = this.appender.storeItem(data, (byte)1, sync);
        return loc;
    }
    
    public Location write(final ByteSequence data, final Runnable onComplete) throws IOException, IllegalStateException {
        final Location loc = this.appender.storeItem(data, (byte)1, onComplete);
        return loc;
    }
    
    public void update(final Location location, final ByteSequence data, final boolean sync) throws IOException {
        final DataFile dataFile = this.getDataFile(location);
        final DataFileAccessor updater = this.accessorPool.openDataFileAccessor(dataFile);
        try {
            updater.updateRecord(location, data, sync);
        }
        finally {
            this.accessorPool.closeDataFileAccessor(updater);
        }
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public String getFilePrefix() {
        return this.filePrefix;
    }
    
    public void setFilePrefix(final String filePrefix) {
        this.filePrefix = filePrefix;
    }
    
    public Map<WriteKey, WriteCommand> getInflightWrites() {
        return this.inflightWrites;
    }
    
    public Location getLastAppendLocation() {
        return this.lastAppendLocation.get();
    }
    
    public void setLastAppendLocation(final Location lastSyncedLocation) {
        this.lastAppendLocation.set(lastSyncedLocation);
    }
    
    public File getDirectoryArchive() {
        return this.directoryArchive;
    }
    
    public void setDirectoryArchive(final File directoryArchive) {
        this.directoryArchive = directoryArchive;
    }
    
    public boolean isArchiveDataLogs() {
        return this.archiveDataLogs;
    }
    
    public void setArchiveDataLogs(final boolean archiveDataLogs) {
        this.archiveDataLogs = archiveDataLogs;
    }
    
    public synchronized Integer getCurrentDataFileId() {
        if (this.dataFiles.isEmpty()) {
            return null;
        }
        return this.dataFiles.getTail().getDataFileId();
    }
    
    public Set<File> getFiles() {
        return this.fileByFileMap.keySet();
    }
    
    public synchronized Map<Integer, DataFile> getFileMap() {
        return new TreeMap<Integer, DataFile>(this.fileMap);
    }
    
    public long getDiskSize() {
        long tailLength = 0L;
        synchronized (this) {
            if (!this.dataFiles.isEmpty()) {
                tailLength = this.dataFiles.getTail().getLength();
            }
        }
        long rc = this.totalLength.get();
        if (tailLength < this.preferedFileLength) {
            rc -= tailLength;
            rc += this.preferedFileLength;
        }
        return rc;
    }
    
    public void setReplicationTarget(final ReplicationTarget replicationTarget) {
        this.replicationTarget = replicationTarget;
    }
    
    public ReplicationTarget getReplicationTarget() {
        return this.replicationTarget;
    }
    
    public String getFileSuffix() {
        return this.fileSuffix;
    }
    
    public void setFileSuffix(final String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }
    
    public boolean isChecksum() {
        return this.checksum;
    }
    
    public void setChecksum(final boolean checksumWrites) {
        this.checksum = checksumWrites;
    }
    
    public boolean isCheckForCorruptionOnStartup() {
        return this.checkForCorruptionOnStartup;
    }
    
    public void setCheckForCorruptionOnStartup(final boolean checkForCorruptionOnStartup) {
        this.checkForCorruptionOnStartup = checkForCorruptionOnStartup;
    }
    
    public void setWriteBatchSize(final int writeBatchSize) {
        this.writeBatchSize = writeBatchSize;
    }
    
    public int getWriteBatchSize() {
        return this.writeBatchSize;
    }
    
    public void setSizeAccumulator(final AtomicLong storeSizeAccumulator) {
        this.totalLength = storeSizeAccumulator;
    }
    
    public void setEnableAsyncDiskSync(final boolean val) {
        this.enableAsyncDiskSync = val;
    }
    
    public boolean isEnableAsyncDiskSync() {
        return this.enableAsyncDiskSync;
    }
    
    static {
        callerBufferAppender = Boolean.parseBoolean(System.getProperty("org.apache.kahadb.journal.CALLER_BUFFER_APPENDER", "false"));
        BATCH_CONTROL_RECORD_MAGIC = bytes("WRITE BATCH");
        BATCH_CONTROL_RECORD_SIZE = 5 + Journal.BATCH_CONTROL_RECORD_MAGIC.length + 4 + 8;
        BATCH_CONTROL_RECORD_HEADER = createBatchControlRecordHeader();
        LOG = LoggerFactory.getLogger(Journal.class);
    }
    
    public static class WriteCommand extends LinkedNode<WriteCommand>
    {
        public final Location location;
        public final ByteSequence data;
        final boolean sync;
        public final Runnable onComplete;
        
        public WriteCommand(final Location location, final ByteSequence data, final boolean sync) {
            this.location = location;
            this.data = data;
            this.sync = sync;
            this.onComplete = null;
        }
        
        public WriteCommand(final Location location, final ByteSequence data, final Runnable onComplete) {
            this.location = location;
            this.data = data;
            this.onComplete = onComplete;
            this.sync = false;
        }
    }
    
    public static class WriteKey
    {
        private final int file;
        private final long offset;
        private final int hash;
        
        public WriteKey(final Location item) {
            this.file = item.getDataFileId();
            this.offset = item.getOffset();
            this.hash = (int)((long)this.file ^ this.offset);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof WriteKey) {
                final WriteKey di = (WriteKey)obj;
                return di.file == this.file && di.offset == this.offset;
            }
            return false;
        }
    }
}
