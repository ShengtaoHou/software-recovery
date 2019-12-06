// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.io.DataOutput;
import java.io.DataInput;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Iterator;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;

public class HashIndex<Key, Value> implements Index<Key, Value>
{
    public static final int CLOSED_STATE = 1;
    public static final int OPEN_STATE = 2;
    private static final Logger LOG;
    public static final int DEFAULT_BIN_CAPACITY;
    public static final int DEFAULT_MAXIMUM_BIN_CAPACITY;
    public static final int DEFAULT_MINIMUM_BIN_CAPACITY;
    public static final int DEFAULT_LOAD_FACTOR;
    private AtomicBoolean loaded;
    private int increaseThreshold;
    private int decreaseThreshold;
    private int maximumBinCapacity;
    private int minimumBinCapacity;
    private int loadFactor;
    private PageFile pageFile;
    private long pageId;
    private Metadata metadata;
    private Metadata.Marshaller metadataMarshaller;
    private HashBin.Marshaller<Key, Value> hashBinMarshaller;
    private Marshaller<Key> keyMarshaller;
    private Marshaller<Value> valueMarshaller;
    
    public HashIndex(final PageFile pageFile, final long pageId) throws IOException {
        this.loaded = new AtomicBoolean();
        this.maximumBinCapacity = HashIndex.DEFAULT_MAXIMUM_BIN_CAPACITY;
        this.minimumBinCapacity = HashIndex.DEFAULT_MINIMUM_BIN_CAPACITY;
        this.loadFactor = HashIndex.DEFAULT_LOAD_FACTOR;
        this.metadata = new Metadata();
        this.metadataMarshaller = new Metadata.Marshaller();
        this.hashBinMarshaller = new HashBin.Marshaller<Key, Value>(this);
        this.pageFile = pageFile;
        this.pageId = pageId;
    }
    
    @Override
    public synchronized void load(final Transaction tx) throws IOException {
        if (this.loaded.compareAndSet(false, true)) {
            final Page<Metadata> metadataPage = tx.load(this.pageId, (Marshaller<Metadata>)this.metadataMarshaller);
            if (metadataPage.getType() == 0) {
                final Page binPage = tx.allocate(this.metadata.binCapacity);
                this.metadata.binPageId = binPage.getPageId();
                this.metadata.page = metadataPage;
                metadataPage.set(this.metadata);
                this.clear(tx);
            }
            else {
                (this.metadata = metadataPage.get()).page = metadataPage;
                if (this.metadata.state == 2) {
                    this.metadata.size = 0;
                    for (int i = 0; i < this.metadata.binCapacity; ++i) {
                        final int t = this.sizeOfBin(tx, i);
                        if (t > 0) {
                            this.metadata.binsActive++;
                        }
                        final Metadata metadata = this.metadata;
                        metadata.size += t;
                    }
                }
            }
            this.calcThresholds();
            this.metadata.state = 2;
            tx.store(metadataPage, this.metadataMarshaller, true);
            HashIndex.LOG.debug("HashIndex loaded. Using " + this.metadata.binCapacity + " bins starting at page " + this.metadata.binPageId);
        }
    }
    
    @Override
    public synchronized void unload(final Transaction tx) throws IOException {
        if (this.loaded.compareAndSet(true, false)) {
            this.metadata.state = 1;
            tx.store(this.metadata.page, (Marshaller<Object>)this.metadataMarshaller, true);
        }
    }
    
    private int sizeOfBin(final Transaction tx, final int index) throws IOException {
        return this.getBin(tx, index).size();
    }
    
    @Override
    public synchronized Value get(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        return this.getBin(tx, key).get(key);
    }
    
    @Override
    public synchronized boolean containsKey(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        return this.getBin(tx, key).containsKey(key);
    }
    
    @Override
    public synchronized Value put(final Transaction tx, final Key key, final Value value) throws IOException {
        this.assertLoaded();
        final HashBin<Key, Value> bin = this.getBin(tx, key);
        final int originalSize = bin.size();
        final Value result = bin.put(key, value);
        this.store(tx, bin);
        int newSize = bin.size();
        if (newSize != originalSize) {
            this.metadata.size++;
            if (newSize == 1) {
                this.metadata.binsActive++;
            }
        }
        if (this.metadata.binsActive >= this.increaseThreshold) {
            newSize = Math.min(this.maximumBinCapacity, this.metadata.binCapacity * 2);
            if (this.metadata.binCapacity != newSize) {
                this.resize(tx, newSize);
            }
        }
        return result;
    }
    
    @Override
    public synchronized Value remove(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        final HashBin<Key, Value> bin = this.getBin(tx, key);
        final int originalSize = bin.size();
        final Value result = bin.remove(key);
        int newSize = bin.size();
        if (newSize != originalSize) {
            this.store(tx, bin);
            this.metadata.size--;
            if (newSize == 0) {
                this.metadata.binsActive--;
            }
        }
        if (this.metadata.binsActive <= this.decreaseThreshold) {
            newSize = Math.max(this.minimumBinCapacity, this.metadata.binCapacity / 2);
            if (this.metadata.binCapacity != newSize) {
                this.resize(tx, newSize);
            }
        }
        return result;
    }
    
    @Override
    public synchronized void clear(final Transaction tx) throws IOException {
        this.assertLoaded();
        for (int i = 0; i < this.metadata.binCapacity; ++i) {
            final long pageId = this.metadata.binPageId + i;
            this.clearBinAtPage(tx, pageId);
        }
        this.metadata.size = 0;
        this.metadata.binsActive = 0;
    }
    
    @Override
    public Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    private void clearBinAtPage(final Transaction tx, final long pageId) throws IOException {
        final Page<HashBin<Key, Value>> page = tx.load(pageId, (Marshaller<HashBin<Key, Value>>)null);
        final HashBin<Key, Value> bin = new HashBin<Key, Value>();
        bin.setPage(page);
        page.set(bin);
        this.store(tx, bin);
    }
    
    @Override
    public String toString() {
        final String str = "HashIndex" + System.identityHashCode(this) + ": " + this.pageFile;
        return str;
    }
    
    private void assertLoaded() throws IllegalStateException {
        if (!this.loaded.get()) {
            throw new IllegalStateException("The HashIndex is not loaded");
        }
    }
    
    public synchronized void store(final Transaction tx, final HashBin<Key, Value> bin) throws IOException {
        tx.store(bin.getPage(), (Marshaller<HashBin<Key, Value>>)this.hashBinMarshaller, true);
    }
    
    private void resize(final Transaction tx, final int newSize) throws IOException {
        HashIndex.LOG.debug("Resizing to: " + newSize);
        int resizeCapacity = newSize;
        long resizePageId = tx.allocate(resizeCapacity).getPageId();
        for (int i = 0; i < resizeCapacity; ++i) {
            final long pageId = resizePageId + i;
            this.clearBinAtPage(tx, pageId);
        }
        this.metadata.binsActive = 0;
        for (int i = 0; i < this.metadata.binCapacity; ++i) {
            final HashBin<Key, Value> bin = this.getBin(tx, i);
            for (final Map.Entry<Key, Value> entry : bin.getAll(tx).entrySet()) {
                final HashBin<Key, Value> resizeBin = this.getBin(tx, entry.getKey(), resizePageId, resizeCapacity);
                resizeBin.put(entry.getKey(), entry.getValue());
                this.store(tx, resizeBin);
                if (resizeBin.size() == 1) {
                    this.metadata.binsActive++;
                }
            }
        }
        tx.free(this.metadata.binPageId, this.metadata.binCapacity);
        this.metadata.binCapacity = resizeCapacity;
        this.metadata.binPageId = resizePageId;
        this.metadata.state = 2;
        tx.store(this.metadata.page, (Marshaller<Object>)this.metadataMarshaller, true);
        this.calcThresholds();
        HashIndex.LOG.debug("Resizing done.  New bins start at: " + this.metadata.binPageId);
        resizeCapacity = 0;
        resizePageId = 0L;
    }
    
    private void calcThresholds() {
        this.increaseThreshold = this.metadata.binCapacity * this.loadFactor / 100;
        this.decreaseThreshold = this.metadata.binCapacity * this.loadFactor * this.loadFactor / 20000;
    }
    
    private HashBin<Key, Value> getBin(final Transaction tx, final Key key) throws IOException {
        return this.getBin(tx, key, this.metadata.binPageId, this.metadata.binCapacity);
    }
    
    private HashBin<Key, Value> getBin(final Transaction tx, final int i) throws IOException {
        return this.getBin(tx, i, this.metadata.binPageId);
    }
    
    private HashBin<Key, Value> getBin(final Transaction tx, final Key key, final long basePage, final int capacity) throws IOException {
        final int i = this.indexFor(key, capacity);
        return this.getBin(tx, i, basePage);
    }
    
    private HashBin<Key, Value> getBin(final Transaction tx, final int i, final long basePage) throws IOException {
        final Page<HashBin<Key, Value>> page = tx.load(basePage + i, (Marshaller<HashBin<Key, Value>>)this.hashBinMarshaller);
        final HashBin<Key, Value> rc = page.get();
        rc.setPage(page);
        return rc;
    }
    
    int indexFor(final Key x, final int length) {
        return Math.abs(x.hashCode() % length);
    }
    
    public Marshaller<Key> getKeyMarshaller() {
        return this.keyMarshaller;
    }
    
    @Override
    public synchronized void setKeyMarshaller(final Marshaller<Key> marshaller) {
        this.keyMarshaller = marshaller;
    }
    
    public Marshaller<Value> getValueMarshaller() {
        return this.valueMarshaller;
    }
    
    @Override
    public void setValueMarshaller(final Marshaller<Value> valueMarshaller) {
        this.valueMarshaller = valueMarshaller;
    }
    
    public int getBinCapacity() {
        return this.metadata.binCapacity;
    }
    
    public void setBinCapacity(final int binCapacity) {
        if (this.loaded.get() && binCapacity != this.metadata.binCapacity) {
            throw new RuntimeException("Pages already loaded - can't reset bin capacity");
        }
        this.metadata.binCapacity = binCapacity;
    }
    
    @Override
    public boolean isTransient() {
        return false;
    }
    
    public int getLoadFactor() {
        return this.loadFactor;
    }
    
    public void setLoadFactor(final int loadFactor) {
        this.loadFactor = loadFactor;
    }
    
    public int setMaximumBinCapacity() {
        return this.maximumBinCapacity;
    }
    
    public void setMaximumBinCapacity(final int maximumCapacity) {
        this.maximumBinCapacity = maximumCapacity;
    }
    
    public synchronized int size(final Transaction tx) {
        return this.metadata.size;
    }
    
    public synchronized int getActiveBins() {
        return this.metadata.binsActive;
    }
    
    public long getBinPageId() {
        return this.metadata.binPageId;
    }
    
    public PageFile getPageFile() {
        return this.pageFile;
    }
    
    public int getBinsActive() {
        return this.metadata.binsActive;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HashIndex.class);
        DEFAULT_BIN_CAPACITY = Integer.parseInt(System.getProperty("defaultBinSize", "1024"));
        DEFAULT_MAXIMUM_BIN_CAPACITY = Integer.parseInt(System.getProperty("maximumCapacity", "16384"));
        DEFAULT_MINIMUM_BIN_CAPACITY = Integer.parseInt(System.getProperty("minimumCapacity", "16"));
        DEFAULT_LOAD_FACTOR = Integer.parseInt(System.getProperty("defaultLoadFactor", "75"));
    }
    
    static class Metadata
    {
        private Page<Metadata> page;
        private int state;
        private long binPageId;
        private int binCapacity;
        private int binsActive;
        private int size;
        
        Metadata() {
            this.binCapacity = HashIndex.DEFAULT_BIN_CAPACITY;
        }
        
        public void read(final DataInput is) throws IOException {
            this.state = is.readInt();
            this.binPageId = is.readLong();
            this.binCapacity = is.readInt();
            this.size = is.readInt();
            this.binsActive = is.readInt();
        }
        
        public void write(final DataOutput os) throws IOException {
            os.writeInt(this.state);
            os.writeLong(this.binPageId);
            os.writeInt(this.binCapacity);
            os.writeInt(this.size);
            os.writeInt(this.binsActive);
        }
        
        static class Marshaller extends VariableMarshaller<Metadata>
        {
            @Override
            public Metadata readPayload(final DataInput dataIn) throws IOException {
                final Metadata rc = new Metadata();
                rc.read(dataIn);
                return rc;
            }
            
            @Override
            public void writePayload(final Metadata object, final DataOutput dataOut) throws IOException {
                object.write(dataOut);
            }
        }
    }
}
