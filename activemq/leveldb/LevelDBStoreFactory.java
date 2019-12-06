// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import java.io.IOException;
import org.apache.activemq.store.PersistenceAdapter;
import java.io.File;
import org.apache.activemq.store.PersistenceAdapterFactory;

public class LevelDBStoreFactory implements PersistenceAdapterFactory
{
    private int asyncBufferSize;
    private File directory;
    private int flushDelay;
    private int indexBlockRestartInterval;
    private int indexBlockSize;
    private long indexCacheSize;
    private String indexCompression;
    private String indexFactory;
    private int indexMaxOpenFiles;
    private int indexWriteBufferSize;
    private String logCompression;
    private File logDirectory;
    private long logSize;
    private boolean monitorStats;
    private boolean paranoidChecks;
    private boolean sync;
    private boolean verifyChecksums;
    
    public LevelDBStoreFactory() {
        this.asyncBufferSize = 4194304;
        this.directory = new File("LevelDB");
        this.flushDelay = 5000;
        this.indexBlockRestartInterval = 16;
        this.indexBlockSize = 4096;
        this.indexCacheSize = 268435456L;
        this.indexCompression = "snappy";
        this.indexFactory = "org.fusesource.leveldbjni.JniDBFactory, org.iq80.leveldb.impl.Iq80DBFactory";
        this.indexMaxOpenFiles = 1000;
        this.indexWriteBufferSize = 6291456;
        this.logCompression = "none";
        this.logSize = 104857600L;
        this.sync = true;
    }
    
    @Override
    public PersistenceAdapter createPersistenceAdapter() throws IOException {
        final LevelDBStore store = new LevelDBStore();
        store.setVerifyChecksums(this.verifyChecksums);
        store.setAsyncBufferSize(this.asyncBufferSize);
        store.setDirectory(this.directory);
        store.setFlushDelay(this.flushDelay);
        store.setIndexBlockRestartInterval(this.indexBlockRestartInterval);
        store.setIndexBlockSize(this.indexBlockSize);
        store.setIndexCacheSize(this.indexCacheSize);
        store.setIndexCompression(this.indexCompression);
        store.setIndexFactory(this.indexFactory);
        store.setIndexMaxOpenFiles(this.indexMaxOpenFiles);
        store.setIndexWriteBufferSize(this.indexWriteBufferSize);
        store.setLogCompression(this.logCompression);
        store.setLogDirectory(this.logDirectory);
        store.setLogSize(this.logSize);
        store.setMonitorStats(this.monitorStats);
        store.setParanoidChecks(this.paranoidChecks);
        store.setSync(this.sync);
        return store;
    }
    
    public int getAsyncBufferSize() {
        return this.asyncBufferSize;
    }
    
    public void setAsyncBufferSize(final int asyncBufferSize) {
        this.asyncBufferSize = asyncBufferSize;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public int getFlushDelay() {
        return this.flushDelay;
    }
    
    public void setFlushDelay(final int flushDelay) {
        this.flushDelay = flushDelay;
    }
    
    public int getIndexBlockRestartInterval() {
        return this.indexBlockRestartInterval;
    }
    
    public void setIndexBlockRestartInterval(final int indexBlockRestartInterval) {
        this.indexBlockRestartInterval = indexBlockRestartInterval;
    }
    
    public int getIndexBlockSize() {
        return this.indexBlockSize;
    }
    
    public void setIndexBlockSize(final int indexBlockSize) {
        this.indexBlockSize = indexBlockSize;
    }
    
    public long getIndexCacheSize() {
        return this.indexCacheSize;
    }
    
    public void setIndexCacheSize(final long indexCacheSize) {
        this.indexCacheSize = indexCacheSize;
    }
    
    public String getIndexCompression() {
        return this.indexCompression;
    }
    
    public void setIndexCompression(final String indexCompression) {
        this.indexCompression = indexCompression;
    }
    
    public String getIndexFactory() {
        return this.indexFactory;
    }
    
    public void setIndexFactory(final String indexFactory) {
        this.indexFactory = indexFactory;
    }
    
    public int getIndexMaxOpenFiles() {
        return this.indexMaxOpenFiles;
    }
    
    public void setIndexMaxOpenFiles(final int indexMaxOpenFiles) {
        this.indexMaxOpenFiles = indexMaxOpenFiles;
    }
    
    public int getIndexWriteBufferSize() {
        return this.indexWriteBufferSize;
    }
    
    public void setIndexWriteBufferSize(final int indexWriteBufferSize) {
        this.indexWriteBufferSize = indexWriteBufferSize;
    }
    
    public String getLogCompression() {
        return this.logCompression;
    }
    
    public void setLogCompression(final String logCompression) {
        this.logCompression = logCompression;
    }
    
    public File getLogDirectory() {
        return this.logDirectory;
    }
    
    public void setLogDirectory(final File logDirectory) {
        this.logDirectory = logDirectory;
    }
    
    public long getLogSize() {
        return this.logSize;
    }
    
    public void setLogSize(final long logSize) {
        this.logSize = logSize;
    }
    
    public boolean isMonitorStats() {
        return this.monitorStats;
    }
    
    public void setMonitorStats(final boolean monitorStats) {
        this.monitorStats = monitorStats;
    }
    
    public boolean isParanoidChecks() {
        return this.paranoidChecks;
    }
    
    public void setParanoidChecks(final boolean paranoidChecks) {
        this.paranoidChecks = paranoidChecks;
    }
    
    public boolean isSync() {
        return this.sync;
    }
    
    public void setSync(final boolean sync) {
        this.sync = sync;
    }
    
    public boolean isVerifyChecksums() {
        return this.verifyChecksums;
    }
    
    public void setVerifyChecksums(final boolean verifyChecksums) {
        this.verifyChecksums = verifyChecksums;
    }
}
