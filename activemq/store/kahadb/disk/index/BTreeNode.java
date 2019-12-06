// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import java.io.DataInput;
import java.io.DataOutput;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.store.kahadb.disk.page.Page;

public final class BTreeNode<Key, Value>
{
    private final BTreeIndex<Key, Value> index;
    private BTreeNode<Key, Value> parent;
    private Page<BTreeNode<Key, Value>> page;
    private Key[] keys;
    private Value[] values;
    private long[] children;
    private long next;
    
    public BTreeNode(final BTreeIndex<Key, Value> index) {
        this.next = -1L;
        this.index = index;
    }
    
    public void setEmpty() {
        this.setLeafData(this.createKeyArray(0), this.createValueArray(0));
    }
    
    private BTreeNode<Key, Value> getChild(final Transaction tx, final int idx) throws IOException {
        if (this.isBranch() && idx >= 0 && idx < this.children.length) {
            final BTreeNode<Key, Value> result = this.index.loadNode(tx, this.children[idx], this);
            return result;
        }
        return null;
    }
    
    private BTreeNode<Key, Value> getRightLeaf(final Transaction tx) throws IOException {
        BTreeNode<Key, Value> cur;
        for (cur = this; cur.isBranch(); cur = cur.getChild(tx, cur.keys.length)) {}
        return cur;
    }
    
    private BTreeNode<Key, Value> getLeftLeaf(final Transaction tx) throws IOException {
        BTreeNode<Key, Value> cur;
        for (cur = this; cur.isBranch(); cur = cur.getChild(tx, 0)) {}
        return cur;
    }
    
    private BTreeNode<Key, Value> getLeftPeer(final Transaction tx, final BTreeNode<Key, Value> x) throws IOException {
        for (BTreeNode<Key, Value> cur = x; cur.parent != null; cur = cur.parent) {
            if (cur.parent.children[0] != cur.getPageId()) {
                for (int i = 0; i < cur.parent.children.length; ++i) {
                    if (cur.parent.children[i] == cur.getPageId()) {
                        return cur.parent.getChild(tx, i - 1);
                    }
                }
                throw new AssertionError((Object)("page " + x + " was decendent of " + cur.getPageId()));
            }
        }
        return null;
    }
    
    public Value remove(final Transaction tx, final Key key) throws IOException {
        if (this.isBranch()) {
            int idx = Arrays.binarySearch(this.keys, key);
            idx = ((idx < 0) ? (-(idx + 1)) : (idx + 1));
            BTreeNode<Key, Value> child = this.getChild(tx, idx);
            if (child.getPageId() == this.index.getPageId()) {
                throw new IOException("BTree corrupted: Cycle detected.");
            }
            final Value rc = child.remove(tx, key);
            if (child.keys.length == 0) {
                if (child.isBranch()) {
                    this.children[idx] = child.children[0];
                    tx.free(child.getPage());
                }
                else {
                    BTreeNode<Key, Value> previousLeaf = null;
                    if (idx > 0) {
                        previousLeaf = this.getChild(tx, idx - 1).getRightLeaf(tx);
                    }
                    else {
                        final BTreeNode<Key, Value> lp = this.getLeftPeer(tx, this);
                        if (lp != null) {
                            previousLeaf = lp.getRightLeaf(tx);
                        }
                    }
                    if (previousLeaf != null) {
                        previousLeaf.next = child.next;
                        this.index.storeNode(tx, previousLeaf, true);
                    }
                    if (idx < this.children.length - 1) {
                        this.setBranchData(arrayDelete(this.keys, idx), arrayDelete(this.children, idx));
                    }
                    else {
                        this.setBranchData(arrayDelete(this.keys, idx - 1), arrayDelete(this.children, idx));
                    }
                    if (this.children.length == 1 && this.parent == null) {
                        child = this.getChild(tx, 0);
                        this.keys = child.keys;
                        this.children = child.children;
                        this.values = child.values;
                        tx.free(child.getPage());
                    }
                }
                this.index.storeNode(tx, this, true);
            }
            return rc;
        }
        else {
            final int idx = Arrays.binarySearch(this.keys, key);
            if (idx < 0) {
                return null;
            }
            final Value oldValue = this.values[idx];
            this.setLeafData(arrayDelete(this.keys, idx), arrayDelete(this.values, idx));
            if (this.keys.length == 0 && this.parent != null) {
                tx.free(this.getPage());
            }
            else {
                this.index.storeNode(tx, this, true);
            }
            return oldValue;
        }
    }
    
    public Value put(final Transaction tx, final Key key, final Value value) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (this.isBranch()) {
            return (Value)getLeafNode(tx, (BTreeNode<Key, Object>)this, key).put(tx, key, value);
        }
        int idx = Arrays.binarySearch(this.keys, key);
        Value oldValue = null;
        if (idx >= 0) {
            oldValue = this.values[idx];
            this.values[idx] = value;
            this.setLeafData(this.keys, this.values);
        }
        else {
            idx = -(idx + 1);
            this.setLeafData(arrayInsert(this.keys, key, idx), arrayInsert(this.values, value, idx));
        }
        try {
            this.index.storeNode(tx, this, this.allowOverflow());
        }
        catch (Transaction.PageOverflowIOException e) {
            this.split(tx);
        }
        return oldValue;
    }
    
    private void promoteValue(final Transaction tx, final Key key, final long nodeId) throws IOException {
        int idx = Arrays.binarySearch(this.keys, key);
        idx = ((idx < 0) ? (-(idx + 1)) : (idx + 1));
        this.setBranchData(arrayInsert(this.keys, key, idx), arrayInsert(this.children, nodeId, idx + 1));
        try {
            this.index.storeNode(tx, this, this.allowOverflow());
        }
        catch (Transaction.PageOverflowIOException e) {
            this.split(tx);
        }
    }
    
    private void split(final Transaction tx) throws IOException {
        Value[] leftValues = null;
        Value[] rightValues = null;
        long[] leftChildren = null;
        long[] rightChildren = null;
        final int vc = this.keys.length;
        final int pivot = vc / 2;
        Key[] leftKeys;
        Key[] rightKeys;
        Key separator;
        if (this.isBranch()) {
            leftKeys = this.createKeyArray(pivot);
            leftChildren = new long[leftKeys.length + 1];
            rightKeys = this.createKeyArray(vc - (pivot + 1));
            rightChildren = new long[rightKeys.length + 1];
            System.arraycopy(this.keys, 0, leftKeys, 0, leftKeys.length);
            System.arraycopy(this.children, 0, leftChildren, 0, leftChildren.length);
            System.arraycopy(this.keys, leftKeys.length + 1, rightKeys, 0, rightKeys.length);
            System.arraycopy(this.children, leftChildren.length, rightChildren, 0, rightChildren.length);
            final BTreeIndex.Prefixer<Key> prefixer = this.index.getPrefixer();
            if (prefixer != null) {
                separator = prefixer.getSimplePrefix(leftKeys[leftKeys.length - 1], rightKeys[0]);
            }
            else {
                separator = this.keys[leftKeys.length];
            }
        }
        else {
            leftKeys = this.createKeyArray(pivot);
            leftValues = this.createValueArray(leftKeys.length);
            rightKeys = this.createKeyArray(vc - pivot);
            rightValues = this.createValueArray(rightKeys.length);
            System.arraycopy(this.keys, 0, leftKeys, 0, leftKeys.length);
            System.arraycopy(this.values, 0, leftValues, 0, leftValues.length);
            System.arraycopy(this.keys, leftKeys.length, rightKeys, 0, rightKeys.length);
            System.arraycopy(this.values, leftValues.length, rightValues, 0, rightValues.length);
            separator = rightKeys[0];
        }
        if (this.parent == null) {
            final BTreeNode<Key, Value> rNode = this.index.createNode(tx, this);
            final BTreeNode<Key, Value> lNode = this.index.createNode(tx, this);
            if (this.isBranch()) {
                rNode.setBranchData(rightKeys, rightChildren);
                lNode.setBranchData(leftKeys, leftChildren);
            }
            else {
                rNode.setLeafData(rightKeys, rightValues);
                lNode.setLeafData(leftKeys, leftValues);
                lNode.setNext(rNode.getPageId());
            }
            final Key[] v = this.createKeyArray(1);
            v[0] = separator;
            this.setBranchData(v, new long[] { lNode.getPageId(), rNode.getPageId() });
            this.index.storeNode(tx, this, true);
            this.index.storeNode(tx, rNode, true);
            this.index.storeNode(tx, lNode, true);
        }
        else {
            final BTreeNode<Key, Value> rNode = this.index.createNode(tx, this.parent);
            if (this.isBranch()) {
                this.setBranchData(leftKeys, leftChildren);
                rNode.setBranchData(rightKeys, rightChildren);
            }
            else {
                rNode.setNext(this.next);
                this.next = rNode.getPageId();
                this.setLeafData(leftKeys, leftValues);
                rNode.setLeafData(rightKeys, rightValues);
            }
            this.index.storeNode(tx, this, true);
            this.index.storeNode(tx, rNode, true);
            this.parent.promoteValue(tx, separator, rNode.getPageId());
        }
    }
    
    public void printStructure(final Transaction tx, final PrintWriter out, String prefix) throws IOException {
        if (prefix.length() > 0 && this.parent == null) {
            throw new IllegalStateException("Cycle back to root node detected.");
        }
        if (this.parent == null) {
            prefix += "|";
            out.println(prefix + this.getPageId());
        }
        if (this.isBranch()) {
            for (int i = 0; i < this.children.length; ++i) {
                final BTreeNode<Key, Value> child = this.getChild(tx, i);
                if (i == this.children.length - 1) {
                    out.println(prefix + "\\- " + child.getPageId() + (child.isBranch() ? (" (" + child.children.length + ")") : ""));
                    child.printStructure(tx, out, prefix + "   ");
                }
                else {
                    out.println(prefix + "|- " + child.getPageId() + (child.isBranch() ? (" (" + child.children.length + ")") : "") + " : " + this.keys[i]);
                    child.printStructure(tx, out, prefix + "   ");
                }
            }
        }
    }
    
    public int getMinLeafDepth(final Transaction tx, int depth) throws IOException {
        ++depth;
        if (this.isBranch()) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < this.children.length; ++i) {
                min = Math.min(min, this.getChild(tx, i).getMinLeafDepth(tx, depth));
            }
            return min;
        }
        return depth;
    }
    
    public int getMaxLeafDepth(final Transaction tx, int depth) throws IOException {
        ++depth;
        if (this.isBranch()) {
            int v = 0;
            for (int i = 0; i < this.children.length; ++i) {
                v = Math.max(v, this.getChild(tx, i).getMaxLeafDepth(tx, depth));
            }
            depth = v;
        }
        return depth;
    }
    
    public Value get(final Transaction tx, final Key key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (this.isBranch()) {
            return (Value)getLeafNode(tx, (BTreeNode<Key, Object>)this, key).get(tx, key);
        }
        final int idx = Arrays.binarySearch(this.keys, key);
        if (idx < 0) {
            return null;
        }
        return this.values[idx];
    }
    
    public boolean isEmpty(final Transaction tx) throws IOException {
        return this.keys.length == 0;
    }
    
    public void visit(final Transaction tx, final BTreeVisitor<Key, Value> visitor) throws IOException {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        if (this.isBranch()) {
            for (int i = 0; i < this.children.length; ++i) {
                Key key1 = null;
                if (i != 0) {
                    key1 = this.keys[i - 1];
                }
                Key key2 = null;
                if (i != this.children.length - 1) {
                    key2 = this.keys[i];
                }
                if (visitor.isInterestedInKeysBetween(key1, key2)) {
                    final BTreeNode<Key, Value> child = this.getChild(tx, i);
                    child.visit(tx, visitor);
                }
            }
        }
        else {
            visitor.visit(Arrays.asList(this.keys), Arrays.asList(this.values));
        }
    }
    
    public Map.Entry<Key, Value> getFirst(final Transaction tx) throws IOException {
        BTreeNode<Key, Value> node;
        for (node = this; node.isBranch(); node = node.getChild(tx, 0)) {}
        if (node.values.length > 0) {
            return new KeyValueEntry(node.keys[0], node.values[0]);
        }
        return null;
    }
    
    public Map.Entry<Key, Value> getLast(final Transaction tx) throws IOException {
        BTreeNode<Key, Value> node;
        for (node = this; node.isBranch(); node = node.getChild(tx, node.children.length - 1)) {}
        if (node.values.length > 0) {
            final int idx = node.values.length - 1;
            return new KeyValueEntry(node.keys[idx], node.values[idx]);
        }
        return null;
    }
    
    public BTreeNode<Key, Value> getFirstLeafNode(final Transaction tx) throws IOException {
        BTreeNode<Key, Value> node;
        for (node = this; node.isBranch(); node = node.getChild(tx, 0)) {}
        return node;
    }
    
    public Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx, final Key startKey) throws IOException {
        if (startKey == null) {
            return this.iterator(tx);
        }
        if (this.isBranch()) {
            return (Iterator<Map.Entry<Key, Value>>)getLeafNode(tx, (BTreeNode<Key, Object>)this, startKey).iterator(tx, startKey);
        }
        int idx = Arrays.binarySearch(this.keys, startKey);
        if (idx < 0) {
            idx = -(idx + 1);
        }
        return new BTreeIterator(tx, this, idx);
    }
    
    public Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx) throws IOException {
        return new BTreeIterator(tx, (BTreeNode)this.getFirstLeafNode(tx), 0);
    }
    
    public void clear(final Transaction tx) throws IOException {
        if (this.isBranch()) {
            for (int i = 0; i < this.children.length; ++i) {
                final BTreeNode<Key, Value> node = this.index.loadNode(tx, this.children[i], this);
                node.clear(tx);
                tx.free(node.getPage());
            }
        }
        if (this.parent == null) {
            this.setLeafData(this.createKeyArray(0), this.createValueArray(0));
            this.next = -1L;
            this.index.storeNode(tx, this, true);
        }
    }
    
    private static <Key, Value> BTreeNode<Key, Value> getLeafNode(final Transaction tx, final BTreeNode<Key, Value> node, final Key key) throws IOException {
        BTreeNode<Key, Value> current;
        BTreeNode<Key, Value> child;
        for (current = node; current.isBranch(); current = child) {
            int idx = Arrays.binarySearch(current.keys, key);
            idx = ((idx < 0) ? (-(idx + 1)) : (idx + 1));
            child = current.getChild(tx, idx);
            if (child == node) {
                throw new IOException("BTree corrupted: Cylce detected.");
            }
        }
        return current;
    }
    
    public boolean contains(final Transaction tx, final Key key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (this.isBranch()) {
            return getLeafNode(tx, (BTreeNode<Key, Object>)this, key).contains(tx, key);
        }
        final int idx = Arrays.binarySearch(this.keys, key);
        return idx >= 0;
    }
    
    private boolean allowOverflow() {
        return this.keys.length <= 3;
    }
    
    private void setLeafData(final Key[] keys, final Value[] values) {
        this.keys = keys;
        this.values = values;
        this.children = null;
    }
    
    private void setBranchData(final Key[] keys, final long[] nodeIds) {
        this.keys = keys;
        this.children = nodeIds;
        this.values = null;
    }
    
    private Key[] createKeyArray(final int size) {
        return (Key[])new Object[size];
    }
    
    private Value[] createValueArray(final int size) {
        return (Value[])new Object[size];
    }
    
    private static <T> T[] arrayDelete(final T[] vals, final int idx) {
        final T[] newVals = (T[])new Object[vals.length - 1];
        if (idx > 0) {
            System.arraycopy(vals, 0, newVals, 0, idx);
        }
        if (idx < newVals.length) {
            System.arraycopy(vals, idx + 1, newVals, idx, newVals.length - idx);
        }
        return newVals;
    }
    
    private static long[] arrayDelete(final long[] vals, final int idx) {
        final long[] newVals = new long[vals.length - 1];
        if (idx > 0) {
            System.arraycopy(vals, 0, newVals, 0, idx);
        }
        if (idx < newVals.length) {
            System.arraycopy(vals, idx + 1, newVals, idx, newVals.length - idx);
        }
        return newVals;
    }
    
    private static <T> T[] arrayInsert(final T[] vals, final T val, final int idx) {
        final T[] newVals = (T[])new Object[vals.length + 1];
        if (idx > 0) {
            System.arraycopy(vals, 0, newVals, 0, idx);
        }
        newVals[idx] = val;
        if (idx < vals.length) {
            System.arraycopy(vals, idx, newVals, idx + 1, vals.length - idx);
        }
        return newVals;
    }
    
    private static long[] arrayInsert(final long[] vals, final long val, final int idx) {
        final long[] newVals = new long[vals.length + 1];
        if (idx > 0) {
            System.arraycopy(vals, 0, newVals, 0, idx);
        }
        newVals[idx] = val;
        if (idx < vals.length) {
            System.arraycopy(vals, idx, newVals, idx + 1, vals.length - idx);
        }
        return newVals;
    }
    
    private boolean isBranch() {
        return this.children != null;
    }
    
    public long getPageId() {
        return this.page.getPageId();
    }
    
    public BTreeNode<Key, Value> getParent() {
        return this.parent;
    }
    
    public void setParent(final BTreeNode<Key, Value> parent) {
        this.parent = parent;
    }
    
    public Page<BTreeNode<Key, Value>> getPage() {
        return this.page;
    }
    
    public void setPage(final Page<BTreeNode<Key, Value>> page) {
        this.page = page;
    }
    
    public long getNext() {
        return this.next;
    }
    
    public void setNext(final long next) {
        this.next = next;
    }
    
    @Override
    public String toString() {
        return "[BTreeNode " + (this.isBranch() ? "branch" : "leaf") + ": " + Arrays.asList(this.keys) + "]";
    }
    
    private final class KeyValueEntry implements Map.Entry<Key, Value>
    {
        private final Key key;
        private final Value value;
        
        public KeyValueEntry(final Key key, final Value value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public Key getKey() {
            return this.key;
        }
        
        @Override
        public Value getValue() {
            return this.value;
        }
        
        @Override
        public Value setValue(final Value value) {
            throw new UnsupportedOperationException();
        }
    }
    
    private final class BTreeIterator implements Iterator<Map.Entry<Key, Value>>
    {
        private final Transaction tx;
        BTreeNode<Key, Value> current;
        int nextIndex;
        Map.Entry<Key, Value> nextEntry;
        
        private BTreeIterator(final Transaction tx, final BTreeNode<Key, Value> current, final int nextIndex) {
            this.tx = tx;
            this.current = current;
            this.nextIndex = nextIndex;
        }
        
        private synchronized void findNextPage() {
            if (this.nextEntry != null) {
                return;
            }
            try {
                while (this.current != null) {
                    if (this.nextIndex < this.current.keys.length) {
                        this.nextEntry = new KeyValueEntry(this.current.keys[this.nextIndex], this.current.values[this.nextIndex]);
                        ++this.nextIndex;
                        break;
                    }
                    if (this.current.next < 0L) {
                        break;
                    }
                    this.current = BTreeNode.this.index.loadNode(this.tx, this.current.next, null);
                    assert !this.current.isBranch() : "Should have linked to the next leaf node.";
                    this.nextIndex = 0;
                }
            }
            catch (IOException ex) {}
        }
        
        @Override
        public boolean hasNext() {
            this.findNextPage();
            return this.nextEntry != null;
        }
        
        @Override
        public Map.Entry<Key, Value> next() {
            this.findNextPage();
            if (this.nextEntry != null) {
                final Map.Entry<Key, Value> lastEntry = this.nextEntry;
                this.nextEntry = null;
                return lastEntry;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public static class Marshaller<Key, Value> extends VariableMarshaller<BTreeNode<Key, Value>>
    {
        private final BTreeIndex<Key, Value> index;
        
        public Marshaller(final BTreeIndex<Key, Value> index) {
            this.index = index;
        }
        
        @Override
        public void writePayload(final BTreeNode<Key, Value> node, final DataOutput os) throws IOException {
            final short count = (short)((BTreeNode<Object, Object>)node).keys.length;
            if (count != ((BTreeNode<Object, Object>)node).keys.length) {
                throw new IOException("Too many keys");
            }
            os.writeShort(count);
            for (int i = 0; i < ((BTreeNode<Object, Object>)node).keys.length; ++i) {
                this.index.getKeyMarshaller().writePayload((Key)((BTreeNode<Object, Object>)node).keys[i], os);
            }
            if (((BTreeNode<Object, Object>)node).isBranch()) {
                os.writeBoolean(true);
                for (int i = 0; i < count + 1; ++i) {
                    os.writeLong(((BTreeNode<Object, Object>)node).children[i]);
                }
            }
            else {
                os.writeBoolean(false);
                for (int i = 0; i < count; ++i) {
                    this.index.getValueMarshaller().writePayload((Value)((BTreeNode<Object, Object>)node).values[i], os);
                }
                os.writeLong(((BTreeNode<Object, Object>)node).next);
            }
        }
        
        @Override
        public BTreeNode<Key, Value> readPayload(final DataInput is) throws IOException {
            final BTreeNode<Key, Value> node = new BTreeNode<Key, Value>(this.index);
            final int count = is.readShort();
            ((BTreeNode<Object, Object>)node).keys = new Object[count];
            for (int i = 0; i < count; ++i) {
                ((BTreeNode<Object, Object>)node).keys[i] = this.index.getKeyMarshaller().readPayload(is);
            }
            if (is.readBoolean()) {
                ((BTreeNode<Object, Object>)node).children = new long[count + 1];
                for (int i = 0; i < count + 1; ++i) {
                    ((BTreeNode<Object, Object>)node).children[i] = is.readLong();
                }
            }
            else {
                ((BTreeNode<Object, Object>)node).values = new Object[count];
                for (int i = 0; i < count; ++i) {
                    ((BTreeNode<Object, Object>)node).values[i] = this.index.getValueMarshaller().readPayload(is);
                }
                ((BTreeNode<Object, Object>)node).next = is.readLong();
            }
            return node;
        }
    }
}
