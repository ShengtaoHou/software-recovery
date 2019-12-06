// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.slf4j.Logger;

public class ListIndex<Key, Value> implements Index<Key, Value>
{
    private static final Logger LOG;
    public static final long NOT_SET = -1L;
    protected PageFile pageFile;
    protected long headPageId;
    protected long tailPageId;
    private AtomicLong size;
    protected AtomicBoolean loaded;
    private ListNode.NodeMarshaller<Key, Value> marshaller;
    private Marshaller<Key> keyMarshaller;
    private Marshaller<Value> valueMarshaller;
    private ListNode<Key, Value> lastGetNodeCache;
    private Map.Entry<Key, Value> lastGetEntryCache;
    private WeakReference<Transaction> lastCacheTxSrc;
    
    public ListIndex() {
        this.size = new AtomicLong(0L);
        this.loaded = new AtomicBoolean();
        this.lastGetNodeCache = null;
        this.lastGetEntryCache = null;
        this.lastCacheTxSrc = new WeakReference<Transaction>(null);
    }
    
    public ListIndex(final PageFile pageFile, final long headPageId) {
        this.size = new AtomicLong(0L);
        this.loaded = new AtomicBoolean();
        this.lastGetNodeCache = null;
        this.lastGetEntryCache = null;
        this.lastCacheTxSrc = new WeakReference<Transaction>(null);
        this.pageFile = pageFile;
        this.setHeadPageId(headPageId);
    }
    
    public ListIndex(final PageFile pageFile, final Page page) {
        this(pageFile, page.getPageId());
    }
    
    @Override
    public synchronized void load(final Transaction tx) throws IOException {
        if (this.loaded.compareAndSet(false, true)) {
            ListIndex.LOG.debug("loading");
            if (this.keyMarshaller == null) {
                throw new IllegalArgumentException("The key marshaller must be set before loading the ListIndex");
            }
            if (this.valueMarshaller == null) {
                throw new IllegalArgumentException("The value marshaller must be set before loading the ListIndex");
            }
            this.marshaller = new ListNode.NodeMarshaller<Key, Value>(this.keyMarshaller, this.valueMarshaller);
            final Page<ListNode<Key, Value>> p = tx.load(this.getHeadPageId(), (Marshaller<ListNode<Key, Value>>)null);
            if (p.getType() == 0) {
                final ListNode<Key, Value> root = this.createNode(p);
                this.storeNode(tx, root, true);
                this.setHeadPageId(p.getPageId());
                this.setTailPageId(this.getHeadPageId());
            }
            else {
                ListNode<Key, Value> node = this.loadNode(tx, this.getHeadPageId());
                this.setTailPageId(this.getHeadPageId());
                this.size.addAndGet(node.size(tx));
                while (node.getNext() != -1L) {
                    node = this.loadNode(tx, node.getNext());
                    this.size.addAndGet(node.size(tx));
                    this.setTailPageId(node.getPageId());
                }
            }
        }
    }
    
    @Override
    public synchronized void unload(final Transaction tx) {
        if (this.loaded.compareAndSet(true, false)) {}
    }
    
    protected ListNode<Key, Value> getHead(final Transaction tx) throws IOException {
        return this.loadNode(tx, this.getHeadPageId());
    }
    
    protected ListNode<Key, Value> getTail(final Transaction tx) throws IOException {
        return this.loadNode(tx, this.getTailPageId());
    }
    
    @Override
    public synchronized boolean containsKey(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        if (this.size.get() == 0L) {
            return false;
        }
        final Iterator<Map.Entry<Key, Value>> iterator = this.iterator(tx);
        while (iterator.hasNext()) {
            final Map.Entry<Key, Value> candidate = iterator.next();
            if (key.equals(candidate.getKey())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public synchronized Value get(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        final Iterator<Map.Entry<Key, Value>> iterator = this.iterator(tx);
        while (iterator.hasNext()) {
            final Map.Entry<Key, Value> candidate = iterator.next();
            if (key.equals(candidate.getKey())) {
                this.lastGetNodeCache = ((ListNode.ListIterator)iterator).getCurrent();
                this.lastGetEntryCache = candidate;
                this.lastCacheTxSrc = new WeakReference<Transaction>(tx);
                return candidate.getValue();
            }
        }
        return null;
    }
    
    @Override
    public synchronized Value put(final Transaction tx, final Key key, final Value value) throws IOException {
        Value oldValue = null;
        if (this.lastGetNodeCache != null && tx.equals(this.lastCacheTxSrc.get())) {
            if (this.lastGetEntryCache.getKey().equals(key)) {
                oldValue = this.lastGetEntryCache.setValue(value);
                this.lastGetEntryCache.setValue(value);
                this.lastGetNodeCache.storeUpdate(tx);
                this.flushCache();
                return oldValue;
            }
            final Iterator<Map.Entry<Key, Value>> iterator = this.lastGetNodeCache.iterator(tx);
            while (iterator.hasNext()) {
                final Map.Entry<Key, Value> entry = iterator.next();
                if (entry.getKey().equals(key)) {
                    oldValue = entry.setValue(value);
                    ((ListNode.ListIterator)iterator).getCurrent().storeUpdate(tx);
                    this.flushCache();
                    return oldValue;
                }
            }
        }
        else {
            this.flushCache();
        }
        final Iterator<Map.Entry<Key, Value>> iterator = this.iterator(tx);
        while (iterator.hasNext() && ((ListNode.ListIterator)iterator).getCurrent() != this.lastGetNodeCache) {
            final Map.Entry<Key, Value> entry = iterator.next();
            if (entry.getKey().equals(key)) {
                oldValue = entry.setValue(value);
                ((ListNode.ListIterator)iterator).getCurrent().storeUpdate(tx);
                this.flushCache();
                return oldValue;
            }
        }
        this.flushCache();
        return this.add(tx, key, value);
    }
    
    public synchronized Value add(final Transaction tx, final Key key, final Value value) throws IOException {
        this.assertLoaded();
        this.getTail(tx).put(tx, key, value);
        this.size.incrementAndGet();
        this.flushCache();
        return null;
    }
    
    public synchronized Value addFirst(final Transaction tx, final Key key, final Value value) throws IOException {
        this.assertLoaded();
        this.getHead(tx).addFirst(tx, key, value);
        this.size.incrementAndGet();
        this.flushCache();
        return null;
    }
    
    @Override
    public synchronized Value remove(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        if (this.size.get() == 0L) {
            return null;
        }
        if (this.lastGetNodeCache != null && tx.equals(this.lastCacheTxSrc.get())) {
            final Iterator<Map.Entry<Key, Value>> iterator = this.lastGetNodeCache.iterator(tx);
            while (iterator.hasNext()) {
                final Map.Entry<Key, Value> entry = iterator.next();
                if (entry.getKey().equals(key)) {
                    iterator.remove();
                    this.flushCache();
                    return entry.getValue();
                }
            }
        }
        else {
            this.flushCache();
        }
        final Iterator<Map.Entry<Key, Value>> iterator = this.iterator(tx);
        while (iterator.hasNext() && ((ListNode.ListIterator)iterator).getCurrent() != this.lastGetNodeCache) {
            final Map.Entry<Key, Value> entry = iterator.next();
            if (entry.getKey().equals(key)) {
                iterator.remove();
                this.flushCache();
                return entry.getValue();
            }
        }
        return null;
    }
    
    public void onRemove() {
        this.size.decrementAndGet();
        this.flushCache();
    }
    
    @Override
    public boolean isTransient() {
        return false;
    }
    
    @Override
    public synchronized void clear(final Transaction tx) throws IOException {
        final Iterator<ListNode<Key, Value>> iterator = this.listNodeIterator(tx);
        while (iterator.hasNext()) {
            final ListNode<Key, Value> candidate = iterator.next();
            candidate.clear(tx);
            tx.commit();
        }
        this.flushCache();
        this.size.set(0L);
    }
    
    public synchronized Iterator<ListNode<Key, Value>> listNodeIterator(final Transaction tx) throws IOException {
        return this.getHead(tx).listNodeIterator(tx);
    }
    
    public synchronized boolean isEmpty(final Transaction tx) throws IOException {
        return this.getHead(tx).isEmpty(tx);
    }
    
    @Override
    public synchronized Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx) throws IOException {
        return this.getHead(tx).iterator(tx);
    }
    
    public synchronized Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx, final long initialPosition) throws IOException {
        return this.getHead(tx).iterator(tx, initialPosition);
    }
    
    public synchronized Map.Entry<Key, Value> getFirst(final Transaction tx) throws IOException {
        return this.getHead(tx).getFirst(tx);
    }
    
    public synchronized Map.Entry<Key, Value> getLast(final Transaction tx) throws IOException {
        return this.getTail(tx).getLast(tx);
    }
    
    private void assertLoaded() throws IllegalStateException {
        if (!this.loaded.get()) {
            throw new IllegalStateException("TheListIndex is not loaded");
        }
    }
    
    ListNode<Key, Value> loadNode(final Transaction tx, final long pageId) throws IOException {
        final Page<ListNode<Key, Value>> page = tx.load(pageId, (Marshaller<ListNode<Key, Value>>)this.marshaller);
        final ListNode<Key, Value> node = page.get();
        node.setPage(page);
        node.setContainingList(this);
        return node;
    }
    
    ListNode<Key, Value> createNode(final Page<ListNode<Key, Value>> page) throws IOException {
        final ListNode<Key, Value> node = new ListNode<Key, Value>();
        node.setPage(page);
        page.set(node);
        node.setContainingList(this);
        return node;
    }
    
    public ListNode<Key, Value> createNode(final Transaction tx) throws IOException {
        return this.createNode(tx.load(tx.allocate().getPageId(), (Marshaller<ListNode<Key, Value>>)null));
    }
    
    public void storeNode(final Transaction tx, final ListNode<Key, Value> node, final boolean overflow) throws IOException {
        tx.store(node.getPage(), (Marshaller<ListNode<Key, Value>>)this.marshaller, overflow);
        this.flushCache();
    }
    
    public PageFile getPageFile() {
        return this.pageFile;
    }
    
    public void setPageFile(final PageFile pageFile) {
        this.pageFile = pageFile;
    }
    
    public long getHeadPageId() {
        return this.headPageId;
    }
    
    public void setHeadPageId(final long headPageId) {
        this.headPageId = headPageId;
    }
    
    public Marshaller<Key> getKeyMarshaller() {
        return this.keyMarshaller;
    }
    
    @Override
    public void setKeyMarshaller(final Marshaller<Key> keyMarshaller) {
        this.keyMarshaller = keyMarshaller;
    }
    
    public Marshaller<Value> getValueMarshaller() {
        return this.valueMarshaller;
    }
    
    @Override
    public void setValueMarshaller(final Marshaller<Value> valueMarshaller) {
        this.valueMarshaller = valueMarshaller;
    }
    
    public void setTailPageId(final long tailPageId) {
        this.tailPageId = tailPageId;
    }
    
    public long getTailPageId() {
        return this.tailPageId;
    }
    
    public long size() {
        return this.size.get();
    }
    
    private void flushCache() {
        this.lastGetEntryCache = null;
        this.lastGetNodeCache = null;
        this.lastCacheTxSrc.clear();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ListIndex.class);
    }
}
