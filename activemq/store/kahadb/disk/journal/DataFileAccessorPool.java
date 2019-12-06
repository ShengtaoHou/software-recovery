// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class DataFileAccessorPool
{
    private final Journal journal;
    private final Map<Integer, Pool> pools;
    private boolean closed;
    private int maxOpenReadersPerFile;
    
    public DataFileAccessorPool(final Journal dataManager) {
        this.pools = new HashMap<Integer, Pool>();
        this.maxOpenReadersPerFile = 5;
        this.journal = dataManager;
    }
    
    synchronized void clearUsedMark() {
        for (final Pool pool : this.pools.values()) {
            pool.clearUsedMark();
        }
    }
    
    synchronized void disposeUnused() {
        final Iterator<Pool> iter = this.pools.values().iterator();
        while (iter.hasNext()) {
            final Pool pool = iter.next();
            if (!pool.isUsed()) {
                pool.dispose();
                iter.remove();
            }
        }
    }
    
    synchronized void disposeDataFileAccessors(final DataFile dataFile) {
        if (this.closed) {
            throw new IllegalStateException("Closed.");
        }
        final Pool pool = this.pools.get(dataFile.getDataFileId());
        if (pool != null) {
            if (pool.getOpenCounter() != 0) {
                throw new IllegalStateException("The data file is still in use: " + dataFile + ", use count: " + pool.getOpenCounter());
            }
            pool.dispose();
            this.pools.remove(dataFile.getDataFileId());
        }
    }
    
    synchronized DataFileAccessor openDataFileAccessor(final DataFile dataFile) throws IOException {
        if (this.closed) {
            throw new IOException("Closed.");
        }
        Pool pool = this.pools.get(dataFile.getDataFileId());
        if (pool == null) {
            pool = new Pool(dataFile);
            this.pools.put(dataFile.getDataFileId(), pool);
        }
        return pool.openDataFileReader();
    }
    
    synchronized void closeDataFileAccessor(final DataFileAccessor reader) {
        final Pool pool = this.pools.get(reader.getDataFile().getDataFileId());
        if (pool == null || this.closed) {
            reader.dispose();
        }
        else {
            pool.closeDataFileReader(reader);
        }
    }
    
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        for (final Pool pool : this.pools.values()) {
            pool.dispose();
        }
        this.pools.clear();
    }
    
    class Pool
    {
        private final DataFile file;
        private final List<DataFileAccessor> pool;
        private boolean used;
        private int openCounter;
        private boolean disposed;
        
        public Pool(final DataFile file) {
            this.pool = new ArrayList<DataFileAccessor>();
            this.file = file;
        }
        
        public DataFileAccessor openDataFileReader() throws IOException {
            DataFileAccessor rc = null;
            if (this.pool.isEmpty()) {
                rc = new DataFileAccessor(DataFileAccessorPool.this.journal, this.file);
            }
            else {
                rc = this.pool.remove(this.pool.size() - 1);
            }
            this.used = true;
            ++this.openCounter;
            return rc;
        }
        
        public synchronized void closeDataFileReader(final DataFileAccessor reader) {
            --this.openCounter;
            if (this.pool.size() >= DataFileAccessorPool.this.maxOpenReadersPerFile || this.disposed) {
                reader.dispose();
            }
            else {
                this.pool.add(reader);
            }
        }
        
        public synchronized void clearUsedMark() {
            this.used = false;
        }
        
        public synchronized boolean isUsed() {
            return this.used;
        }
        
        public synchronized void dispose() {
            for (final DataFileAccessor reader : this.pool) {
                reader.dispose();
            }
            this.pool.clear();
            this.disposed = true;
        }
        
        public synchronized int getOpenCounter() {
            return this.openCounter;
        }
    }
}
