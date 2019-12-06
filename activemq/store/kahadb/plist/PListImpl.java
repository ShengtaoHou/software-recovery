// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.plist;

import java.util.NoSuchElementException;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.activemq.store.PListEntry;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.util.LocationMarshaller;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.store.kahadb.disk.util.StringMarshaller;
import org.slf4j.Logger;
import org.apache.activemq.store.PList;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.store.kahadb.disk.index.ListIndex;

public class PListImpl extends ListIndex<String, Location> implements PList
{
    static final Logger LOG;
    final PListStoreImpl store;
    private String name;
    Object indexLock;
    
    PListImpl(final PListStoreImpl store) {
        this.store = store;
        this.indexLock = store.getIndexLock();
        this.setPageFile(store.getPageFile());
        ((ListIndex<String, Value>)this).setKeyMarshaller(StringMarshaller.INSTANCE);
        ((ListIndex<Key, Location>)this).setValueMarshaller(LocationMarshaller.INSTANCE);
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    void read(final DataInput in) throws IOException {
        this.setHeadPageId(in.readLong());
    }
    
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.getHeadPageId());
    }
    
    @Override
    public synchronized void destroy() throws IOException {
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    PListImpl.this.clear(tx);
                    PListImpl.this.unload(tx);
                }
            });
        }
    }
    
    @Override
    public Object addLast(final String id, final ByteSequence bs) throws IOException {
        final Location location = this.store.write(bs, false);
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    PListImpl.this.add(tx, id, location);
                }
            });
        }
        return new Locator(id);
    }
    
    @Override
    public Object addFirst(final String id, final ByteSequence bs) throws IOException {
        final Location location = this.store.write(bs, false);
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    PListImpl.this.addFirst(tx, id, location);
                }
            });
        }
        return new Locator(id);
    }
    
    @Override
    public boolean remove(final Object l) throws IOException {
        final Locator locator = (Locator)l;
        assert locator != null;
        assert locator.plist() == this;
        return this.remove(locator.id);
    }
    
    public boolean remove(final String id) throws IOException {
        final AtomicBoolean result = new AtomicBoolean();
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    result.set(((ListIndex<String, Object>)PListImpl.this).remove(tx, id) != null);
                }
            });
        }
        return result.get();
    }
    
    public boolean remove(final long position) throws IOException {
        final AtomicBoolean result = new AtomicBoolean();
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    final Iterator<Map.Entry<String, Location>> iterator = PListImpl.this.iterator(tx, position);
                    if (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                        result.set(true);
                    }
                    else {
                        result.set(false);
                    }
                }
            });
        }
        return result.get();
    }
    
    public PListEntry get(final long position) throws IOException {
        PListEntry result = null;
        final AtomicReference<Map.Entry<String, Location>> ref = new AtomicReference<Map.Entry<String, Location>>();
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    final Iterator<Map.Entry<String, Location>> iterator = PListImpl.this.iterator(tx, position);
                    ref.set(iterator.next());
                }
            });
        }
        if (ref.get() != null) {
            final ByteSequence bs = this.store.getPayload(ref.get().getValue());
            result = new PListEntry(ref.get().getKey(), bs, new Locator(ref.get().getKey()));
        }
        return result;
    }
    
    public PListEntry getFirst() throws IOException {
        PListEntry result = null;
        final AtomicReference<Map.Entry<String, Location>> ref = new AtomicReference<Map.Entry<String, Location>>();
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    ref.set(PListImpl.this.getFirst(tx));
                }
            });
        }
        if (ref.get() != null) {
            final ByteSequence bs = this.store.getPayload(ref.get().getValue());
            result = new PListEntry(ref.get().getKey(), bs, new Locator(ref.get().getKey()));
        }
        return result;
    }
    
    public PListEntry getLast() throws IOException {
        PListEntry result = null;
        final AtomicReference<Map.Entry<String, Location>> ref = new AtomicReference<Map.Entry<String, Location>>();
        synchronized (this.indexLock) {
            this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                @Override
                public void execute(final Transaction tx) throws IOException {
                    ref.set(PListImpl.this.getLast(tx));
                }
            });
        }
        if (ref.get() != null) {
            final ByteSequence bs = this.store.getPayload(ref.get().getValue());
            result = new PListEntry(ref.get().getKey(), bs, new Locator(ref.get().getKey()));
        }
        return result;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0L;
    }
    
    @Override
    public PListIterator iterator() throws IOException {
        return new PListIteratorImpl();
    }
    
    public void claimFileLocations(final Set<Integer> candidates) throws IOException {
        synchronized (this.indexLock) {
            if (this.loaded.get()) {
                this.store.getPageFile().tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        final Iterator<Map.Entry<String, Location>> iterator = PListImpl.this.iterator(tx);
                        while (iterator.hasNext()) {
                            final Location location = iterator.next().getValue();
                            candidates.remove(location.getDataFileId());
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name + "[headPageId=" + this.getHeadPageId() + ",tailPageId=" + this.getTailPageId() + ", size=" + this.size() + "]";
    }
    
    static {
        LOG = LoggerFactory.getLogger(PListImpl.class);
    }
    
    class Locator
    {
        final String id;
        
        Locator(final String id) {
            this.id = id;
        }
        
        PListImpl plist() {
            return PListImpl.this;
        }
    }
    
    final class PListIteratorImpl implements PListIterator
    {
        final Iterator<Map.Entry<String, Location>> iterator;
        final Transaction tx;
        
        PListIteratorImpl() throws IOException {
            this.tx = PListImpl.this.store.pageFile.tx();
            synchronized (PListImpl.this.indexLock) {
                this.iterator = PListImpl.this.iterator(this.tx);
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public PListEntry next() {
            final Map.Entry<String, Location> entry = this.iterator.next();
            ByteSequence bs = null;
            try {
                bs = PListImpl.this.store.getPayload(entry.getValue());
            }
            catch (IOException unexpected) {
                final NoSuchElementException e = new NoSuchElementException(unexpected.getLocalizedMessage());
                e.initCause(unexpected);
                throw e;
            }
            return new PListEntry(entry.getKey(), bs, new Locator(entry.getKey()));
        }
        
        @Override
        public void remove() {
            try {
                synchronized (PListImpl.this.indexLock) {
                    this.tx.execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            PListIteratorImpl.this.iterator.remove();
                        }
                    });
                }
            }
            catch (IOException unexpected) {
                final IllegalStateException e = new IllegalStateException(unexpected);
                e.initCause(unexpected);
                throw e;
            }
        }
        
        @Override
        public void release() {
            try {
                this.tx.rollback();
            }
            catch (IOException unexpected) {
                final IllegalStateException e = new IllegalStateException(unexpected);
                e.initCause(unexpected);
                throw e;
            }
        }
    }
}
