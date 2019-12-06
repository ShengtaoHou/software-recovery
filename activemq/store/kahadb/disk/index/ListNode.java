// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import java.io.DataInput;
import java.io.DataOutput;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.util.NoSuchElementException;
import org.apache.activemq.store.kahadb.disk.util.LinkedNode;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.store.kahadb.disk.util.LinkedNodeList;
import org.apache.activemq.store.kahadb.disk.page.Page;

public final class ListNode<Key, Value>
{
    private static final boolean ADD_FIRST = true;
    private static final boolean ADD_LAST = false;
    private ListIndex<Key, Value> containingList;
    private Page<ListNode<Key, Value>> page;
    private LinkedNodeList<KeyValueEntry<Key, Value>> entries;
    private long next;
    
    public ListNode() {
        this.entries = new LinkedNodeList<KeyValueEntry<Key, Value>>() {
            @Override
            public String toString() {
                return "PageId:" + ListNode.this.page.getPageId() + ", index:" + ListNode.this.containingList + super.toString();
            }
        };
        this.next = -1L;
    }
    
    public Value put(final Transaction tx, final Key key, final Value value) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.entries.addLast(new KeyValueEntry<Key, Value>(key, value));
        this.store(tx, false);
        return null;
    }
    
    public Value addFirst(final Transaction tx, final Key key, final Value value) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.entries.addFirst(new KeyValueEntry<Key, Value>(key, value));
        this.store(tx, true);
        return null;
    }
    
    public void storeUpdate(final Transaction tx) throws IOException {
        this.store(tx, false);
    }
    
    private void store(final Transaction tx, final boolean addFirst) throws IOException {
        try {
            this.getContainingList().storeNode(tx, this, this.entries.size() == 1);
            if (this.next == -1L) {
                this.getContainingList().setTailPageId(this.getPageId());
            }
        }
        catch (Transaction.PageOverflowIOException e) {
            this.split(tx, addFirst);
        }
    }
    
    private void store(final Transaction tx) throws IOException {
        this.getContainingList().storeNode(tx, this, true);
    }
    
    private void split(final Transaction tx, final boolean isAddFirst) throws IOException {
        final ListNode<Key, Value> extension = this.getContainingList().createNode(tx);
        if (isAddFirst) {
            extension.setEntries((LinkedNodeList<KeyValueEntry<Key, Value>>)this.entries.getHead().splitAfter());
            extension.setNext(this.getNext());
            extension.store(tx, isAddFirst);
            this.setNext(extension.getPageId());
        }
        else {
            extension.setEntries(this.entries.getTail().getPrevious().splitAfter());
            extension.setNext(this.getNext());
            extension.store(tx, isAddFirst);
            this.getContainingList().setTailPageId(extension.getPageId());
            this.setNext(extension.getPageId());
        }
        this.store(tx, true);
    }
    
    private void setEntries(final LinkedNodeList<KeyValueEntry<Key, Value>> list) {
        this.entries = list;
    }
    
    public Value get(final Transaction tx, final Key key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        Value result = null;
        for (KeyValueEntry<Key, Value> nextEntry = this.entries.getTail(); nextEntry != null; nextEntry = (KeyValueEntry<Key, Value>)nextEntry.getPrevious()) {
            if (nextEntry.getKey().equals(key)) {
                result = nextEntry.getValue();
                break;
            }
        }
        return result;
    }
    
    public boolean isEmpty(final Transaction tx) {
        return this.entries.isEmpty();
    }
    
    public Map.Entry<Key, Value> getFirst(final Transaction tx) {
        return this.entries.getHead();
    }
    
    public Map.Entry<Key, Value> getLast(final Transaction tx) {
        return this.entries.getTail();
    }
    
    public Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx, final long pos) throws IOException {
        return new ListIterator(tx, this, pos);
    }
    
    public Iterator<Map.Entry<Key, Value>> iterator(final Transaction tx) throws IOException {
        return new ListIterator(tx, this, 0L);
    }
    
    Iterator<ListNode<Key, Value>> listNodeIterator(final Transaction tx) throws IOException {
        return new ListNodeIterator(tx, this);
    }
    
    public void clear(final Transaction tx) throws IOException {
        this.entries.clear();
        tx.free(this.getPageId());
    }
    
    public boolean contains(final Transaction tx, final Key key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        boolean found = false;
        for (KeyValueEntry<Key, Value> nextEntry = this.entries.getTail(); nextEntry != null; nextEntry = (KeyValueEntry<Key, Value>)nextEntry.getPrevious()) {
            if (nextEntry.getKey().equals(key)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    public long getPageId() {
        return this.page.getPageId();
    }
    
    public Page<ListNode<Key, Value>> getPage() {
        return this.page;
    }
    
    public void setPage(final Page<ListNode<Key, Value>> page) {
        this.page = page;
    }
    
    public long getNext() {
        return this.next;
    }
    
    public void setNext(final long next) {
        this.next = next;
    }
    
    public void setContainingList(final ListIndex<Key, Value> list) {
        this.containingList = list;
    }
    
    public ListIndex<Key, Value> getContainingList() {
        return this.containingList;
    }
    
    public boolean isHead() {
        return this.getPageId() == this.containingList.getHeadPageId();
    }
    
    public boolean isTail() {
        return this.getPageId() == this.containingList.getTailPageId();
    }
    
    public int size(final Transaction tx) {
        return this.entries.size();
    }
    
    @Override
    public String toString() {
        return "[ListNode(" + ((this.page != null) ? (this.page.getPageId() + "->" + this.next) : "null") + ")[" + this.entries.size() + "]]";
    }
    
    static final class KeyValueEntry<Key, Value> extends LinkedNode<KeyValueEntry<Key, Value>> implements Map.Entry<Key, Value>
    {
        private final Key key;
        private Value value;
        
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
            final Value oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        @Override
        public String toString() {
            return "{" + this.key + ":" + this.value + "}";
        }
    }
    
    private final class ListNodeIterator implements Iterator<ListNode<Key, Value>>
    {
        private final Transaction tx;
        private final ListIndex<Key, Value> index;
        ListNode<Key, Value> nextEntry;
        
        private ListNodeIterator(final Transaction tx, final ListNode<Key, Value> current) {
            this.tx = tx;
            this.nextEntry = current;
            this.index = current.getContainingList();
        }
        
        @Override
        public boolean hasNext() {
            return this.nextEntry != null;
        }
        
        @Override
        public ListNode<Key, Value> next() {
            final ListNode<Key, Value> current = this.nextEntry;
            if (current != null) {
                if (current.next != -1L) {
                    try {
                        this.nextEntry = this.index.loadNode(this.tx, current.next);
                        return current;
                    }
                    catch (IOException unexpected) {
                        final IllegalStateException e = new IllegalStateException("failed to load next: " + current.next + ", reason: " + unexpected.getLocalizedMessage());
                        e.initCause(unexpected);
                        throw e;
                    }
                }
                this.nextEntry = null;
            }
            return current;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    final class ListIterator implements Iterator<Map.Entry<Key, Value>>
    {
        private final Transaction tx;
        private final ListIndex<Key, Value> targetList;
        ListNode<Key, Value> currentNode;
        ListNode<Key, Value> previousNode;
        KeyValueEntry<Key, Value> nextEntry;
        KeyValueEntry<Key, Value> entryToRemove;
        
        private ListIterator(final Transaction tx, final ListNode<Key, Value> current, final long start) {
            this.tx = tx;
            this.currentNode = current;
            this.targetList = current.getContainingList();
            this.nextEntry = current.entries.getHead();
            if (start > 0L) {
                this.moveToRequestedStart(start);
            }
        }
        
        private void moveToRequestedStart(final long start) {
            long count;
            for (count = 0L; this.hasNext() && count < start; ++count) {
                this.next();
            }
            if (!this.hasNext()) {
                throw new NoSuchElementException("Index " + start + " out of current range: " + count);
            }
        }
        
        private KeyValueEntry<Key, Value> getFromNextNode() {
            KeyValueEntry<Key, Value> result = null;
            if (this.currentNode.getNext() != -1L) {
                try {
                    this.previousNode = this.currentNode;
                    this.currentNode = this.targetList.loadNode(this.tx, this.currentNode.getNext());
                }
                catch (IOException unexpected) {
                    final NoSuchElementException e = new NoSuchElementException(unexpected.getLocalizedMessage());
                    e.initCause(unexpected);
                    throw e;
                }
                result = this.currentNode.entries.getHead();
            }
            return result;
        }
        
        @Override
        public boolean hasNext() {
            if (this.nextEntry == null) {
                this.nextEntry = this.getFromNextNode();
            }
            return this.nextEntry != null;
        }
        
        @Override
        public Map.Entry<Key, Value> next() {
            if (this.nextEntry != null) {
                this.entryToRemove = this.nextEntry;
                this.nextEntry = (KeyValueEntry<Key, Value>)this.entryToRemove.getNext();
                return this.entryToRemove;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            if (this.entryToRemove == null) {
                throw new IllegalStateException("can only remove once, call hasNext();next() again");
            }
            try {
                this.entryToRemove.unlink();
                this.entryToRemove = null;
                ListNode<Key, Value> toRemoveNode = null;
                if (this.currentNode.entries.isEmpty()) {
                    if (!this.currentNode.isHead() || !this.currentNode.isTail()) {
                        if (this.currentNode.isHead()) {
                            final ListNode<Key, Value> headNode = this.currentNode;
                            this.nextEntry = this.getFromNextNode();
                            if (this.currentNode.isTail()) {
                                this.targetList.setTailPageId(headNode.getPageId());
                            }
                            headNode.setEntries(this.currentNode.entries);
                            headNode.setNext(this.currentNode.getNext());
                            headNode.store(this.tx);
                            toRemoveNode = this.currentNode;
                            this.currentNode = headNode;
                        }
                        else if (this.currentNode.isTail()) {
                            toRemoveNode = this.currentNode;
                            this.previousNode.setNext(-1L);
                            this.previousNode.store(this.tx);
                            this.targetList.setTailPageId(this.previousNode.getPageId());
                        }
                        else {
                            toRemoveNode = this.currentNode;
                            this.previousNode.setNext(toRemoveNode.getNext());
                            this.previousNode.store(this.tx);
                            this.currentNode = this.previousNode;
                        }
                    }
                }
                this.targetList.onRemove();
                if (toRemoveNode != null) {
                    this.tx.free(toRemoveNode.getPage());
                }
                else {
                    this.currentNode.store(this.tx);
                }
            }
            catch (IOException unexpected) {
                final IllegalStateException e = new IllegalStateException(unexpected.getLocalizedMessage());
                e.initCause(unexpected);
                throw e;
            }
        }
        
        ListNode<Key, Value> getCurrent() {
            return this.currentNode;
        }
    }
    
    public static final class NodeMarshaller<Key, Value> extends VariableMarshaller<ListNode<Key, Value>>
    {
        private final Marshaller<Key> keyMarshaller;
        private final Marshaller<Value> valueMarshaller;
        
        public NodeMarshaller(final Marshaller<Key> keyMarshaller, final Marshaller<Value> valueMarshaller) {
            this.keyMarshaller = keyMarshaller;
            this.valueMarshaller = valueMarshaller;
        }
        
        @Override
        public void writePayload(final ListNode<Key, Value> node, final DataOutput os) throws IOException {
            os.writeLong(((ListNode<Object, Object>)node).next);
            final short count = (short)((ListNode<Object, Object>)node).entries.size();
            if (count != ((ListNode<Object, Object>)node).entries.size()) {
                throw new IOException("short over flow, too many entries in list: " + ((ListNode<Object, Object>)node).entries.size());
            }
            os.writeShort(count);
            for (KeyValueEntry<Key, Value> entry = ((ListNode<Object, Object>)node).entries.getHead(); entry != null; entry = (KeyValueEntry<Key, Value>)entry.getNext()) {
                this.keyMarshaller.writePayload(entry.getKey(), os);
                this.valueMarshaller.writePayload(entry.getValue(), os);
            }
        }
        
        @Override
        public ListNode<Key, Value> readPayload(final DataInput is) throws IOException {
            final ListNode<Key, Value> node = new ListNode<Key, Value>();
            node.setNext(is.readLong());
            for (short size = is.readShort(), i = 0; i < size; ++i) {
                ((ListNode<Object, Object>)node).entries.addLast(new KeyValueEntry<Key, Value>(this.keyMarshaller.readPayload(is), this.valueMarshaller.readPayload(is)));
            }
            return node;
        }
    }
}
