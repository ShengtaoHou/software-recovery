// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.store.kahadb.disk.page.Page;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.store.kahadb.disk.page.PageFile;
import org.slf4j.Logger;

public class BTreeIndex<Key, Value> implements Index<Key, Value>
{
    private static final Logger LOG;
    private PageFile pageFile;
    private long pageId;
    private AtomicBoolean loaded;
    private final BTreeNode.Marshaller<Key, Value> marshaller;
    private Marshaller<Key> keyMarshaller;
    private Marshaller<Value> valueMarshaller;
    private Prefixer<Key> prefixer;
    
    public BTreeIndex() {
        this.loaded = new AtomicBoolean();
        this.marshaller = new BTreeNode.Marshaller<Key, Value>(this);
    }
    
    public BTreeIndex(final long rootPageId) {
        this.loaded = new AtomicBoolean();
        this.marshaller = new BTreeNode.Marshaller<Key, Value>(this);
        this.pageId = rootPageId;
    }
    
    public BTreeIndex(final Page page) {
        this(page.getPageId());
    }
    
    public BTreeIndex(final PageFile pageFile, final long rootPageId) {
        this.loaded = new AtomicBoolean();
        this.marshaller = new BTreeNode.Marshaller<Key, Value>(this);
        this.pageFile = pageFile;
        this.pageId = rootPageId;
    }
    
    public BTreeIndex(final PageFile pageFile, final Page page) {
        this(pageFile, page.getPageId());
    }
    
    @Override
    public synchronized void load(final Transaction tx) throws IOException {
        if (this.loaded.compareAndSet(false, true)) {
            BTreeIndex.LOG.debug("loading");
            if (this.keyMarshaller == null) {
                throw new IllegalArgumentException("The key marshaller must be set before loading the BTreeIndex");
            }
            if (this.valueMarshaller == null) {
                throw new IllegalArgumentException("The value marshaller must be set before loading the BTreeIndex");
            }
            final Page<BTreeNode<Key, Value>> p = tx.load(this.pageId, (Marshaller<BTreeNode<Key, Value>>)null);
            if (p.getType() == 0) {
                final BTreeNode<Key, Value> root = this.createNode(p, null);
                this.storeNode(tx, root, true);
            }
        }
    }
    
    @Override
    public synchronized void unload(final Transaction tx) {
        if (this.loaded.compareAndSet(true, false)) {}
    }
    
    private BTreeNode<Key, Value> getRoot(final Transaction tx) throws IOException {
        return this.loadNode(tx, this.pageId, null);
    }
    
    @Override
    public synchronized boolean containsKey(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        return this.getRoot(tx).contains(tx, key);
    }
    
    @Override
    public synchronized Value get(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        return this.getRoot(tx).get(tx, key);
    }
    
    @Override
    public synchronized Value put(final Transaction tx, final Key key, final Value value) throws IOException {
        this.assertLoaded();
        return this.getRoot(tx).put(tx, key, value);
    }
    
    @Override
    public synchronized Value remove(final Transaction tx, final Key key) throws IOException {
        this.assertLoaded();
        return this.getRoot(tx).remove(tx, key);
    }
    
    @Override
    public boolean isTransient() {
        return false;
    }
    
    @Override
    public synchronized void clear(final Transaction tx) throws IOException {
        this.getRoot(tx).clear(tx);
    }
    
    public synchronized int getMinLeafDepth(final Transaction tx) throws IOException {
        return this.getRoot(tx).getMinLeafDepth(tx, 0);
    }
    
    public synchronized int getMaxLeafDepth(final Transaction tx) throws IOException {
        return this.getRoot(tx).getMaxLeafDepth(tx, 0);
    }
    
    public synchronized void printStructure(final Transaction tx, final PrintWriter out) throws IOException {
        this.getRoot(tx).printStructure(tx, out, "");
    }
    
    public synchronized void printStructure(final Transaction tx, final OutputStream out) throws IOException {
        final PrintWriter pw = new PrintWriter(out, false);
        this.getRoot(tx).printStructure(tx, pw, "");
        pw.flush();
    }
    
    public synchronized boolean isEmpty(final Transaction tx) throws IOException {
        return this.getRoot(tx).isEmpty(tx);
    }
    
    @Override
    public synchronized Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx) throws IOException {
        return this.getRoot(tx).iterator(tx);
    }
    
    public synchronized Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx, final Key initialKey) throws IOException {
        return this.getRoot(tx).iterator(tx, initialKey);
    }
    
    public synchronized void visit(final Transaction tx, final BTreeVisitor<Key, Value> visitor) throws IOException {
        this.getRoot(tx).visit(tx, visitor);
    }
    
    public synchronized Map.Entry<Key, Value> getFirst(final Transaction tx) throws IOException {
        return this.getRoot(tx).getFirst(tx);
    }
    
    public synchronized Map.Entry<Key, Value> getLast(final Transaction tx) throws IOException {
        return this.getRoot(tx).getLast(tx);
    }
    
    private void assertLoaded() throws IllegalStateException {
        if (!this.loaded.get()) {
            throw new IllegalStateException("The BTreeIndex is not loaded");
        }
    }
    
    BTreeNode<Key, Value> loadNode(final Transaction tx, final long pageId, final BTreeNode<Key, Value> parent) throws IOException {
        final Page<BTreeNode<Key, Value>> page = tx.load(pageId, (Marshaller<BTreeNode<Key, Value>>)this.marshaller);
        final BTreeNode<Key, Value> node = page.get();
        node.setPage(page);
        node.setParent(parent);
        return node;
    }
    
    BTreeNode<Key, Value> createNode(final Transaction tx, final BTreeNode<Key, Value> parent) throws IOException {
        final Page<BTreeNode<Key, Value>> p = tx.allocate();
        final BTreeNode<Key, Value> node = new BTreeNode<Key, Value>(this);
        node.setPage(p);
        node.setParent(parent);
        node.setEmpty();
        p.set(node);
        return node;
    }
    
    BTreeNode<Key, Value> createNode(final Page<BTreeNode<Key, Value>> p, final BTreeNode<Key, Value> parent) throws IOException {
        final BTreeNode<Key, Value> node = new BTreeNode<Key, Value>(this);
        node.setPage(p);
        node.setParent(parent);
        node.setEmpty();
        p.set(node);
        return node;
    }
    
    void storeNode(final Transaction tx, final BTreeNode<Key, Value> node, final boolean overflow) throws IOException {
        tx.store(node.getPage(), (Marshaller<BTreeNode<Key, Value>>)this.marshaller, overflow);
    }
    
    public PageFile getPageFile() {
        return this.pageFile;
    }
    
    public long getPageId() {
        return this.pageId;
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
    
    public Prefixer<Key> getPrefixer() {
        return this.prefixer;
    }
    
    public void setPrefixer(final Prefixer<Key> prefixer) {
        this.prefixer = prefixer;
    }
    
    public void setPageFile(final PageFile pageFile) {
        this.pageFile = pageFile;
    }
    
    public void setPageId(final long pageId) {
        this.pageId = pageId;
    }
    
    static {
        LOG = LoggerFactory.getLogger(BTreeIndex.class);
    }
    
    public static class StringPrefixer implements Prefixer<String>
    {
        @Override
        public String getSimplePrefix(final String value1, final String value2) {
            final char[] c1 = value1.toCharArray();
            final char[] c2 = value2.toCharArray();
            final int n = Math.min(c1.length, c2.length);
            for (int i = 0; i < n; ++i) {
                if (c1[i] != c2[i]) {
                    return value2.substring(0, i + 1);
                }
            }
            if (n == c2.length) {
                return value2;
            }
            return value2.substring(0, n);
        }
    }
    
    public interface Prefixer<Key>
    {
        Key getSimplePrefix(final Key p0, final Key p1);
    }
}
