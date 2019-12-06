// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import java.util.Iterator;
import java.io.DataOutput;
import java.io.DataInput;
import org.apache.activemq.store.kahadb.disk.util.VariableMarshaller;
import java.util.Map;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.io.IOException;
import java.util.TreeMap;
import org.apache.activemq.store.kahadb.disk.page.Page;

class HashBin<Key, Value>
{
    private Page<HashBin<Key, Value>> page;
    private TreeMap<Key, Value> data;
    
    HashBin() {
        this.data = new TreeMap<Key, Value>();
    }
    
    public int size() {
        return this.data.size();
    }
    
    public Value put(final Key key, final Value value) throws IOException {
        return this.data.put(key, value);
    }
    
    public Value get(final Key key) throws IOException {
        return this.data.get(key);
    }
    
    public boolean containsKey(final Key key) throws IOException {
        return this.data.containsKey(key);
    }
    
    public Map<Key, Value> getAll(final Transaction tx) throws IOException {
        return this.data;
    }
    
    public Value remove(final Key key) throws IOException {
        return this.data.remove(key);
    }
    
    public Page<HashBin<Key, Value>> getPage() {
        return this.page;
    }
    
    public void setPage(final Page<HashBin<Key, Value>> page) {
        (this.page = page).set(this);
    }
    
    public static class Marshaller<Key, Value> extends VariableMarshaller<HashBin<Key, Value>>
    {
        private final HashIndex<Key, Value> hashIndex;
        
        public Marshaller(final HashIndex<Key, Value> index) {
            this.hashIndex = index;
        }
        
        @Override
        public HashBin<Key, Value> readPayload(final DataInput is) throws IOException {
            final HashBin<Key, Value> bin = new HashBin<Key, Value>();
            for (int size = is.readInt(), i = 0; i < size; ++i) {
                final Key key = this.hashIndex.getKeyMarshaller().readPayload(is);
                final Value value = this.hashIndex.getValueMarshaller().readPayload(is);
                ((HashBin<Object, Object>)bin).data.put(key, value);
            }
            return bin;
        }
        
        @Override
        public void writePayload(final HashBin<Key, Value> bin, final DataOutput os) throws IOException {
            os.writeInt(((HashBin<Object, Object>)bin).data.size());
            for (final Map.Entry<Key, Value> entry : ((HashBin<Object, Object>)bin).data.entrySet()) {
                this.hashIndex.getKeyMarshaller().writePayload(entry.getKey(), os);
                this.hashIndex.getValueMarshaller().writePayload(entry.getValue(), os);
            }
        }
    }
}
