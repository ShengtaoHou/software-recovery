// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.index;

import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import org.apache.activemq.store.kahadb.disk.util.Marshaller;

public interface Index<Key, Value>
{
    void setKeyMarshaller(final Marshaller<Key> p0);
    
    void setValueMarshaller(final Marshaller<Value> p0);
    
    void load(final Transaction p0) throws IOException;
    
    void unload(final Transaction p0) throws IOException;
    
    void clear(final Transaction p0) throws IOException;
    
    boolean containsKey(final Transaction p0, final Key p1) throws IOException;
    
    Value remove(final Transaction p0, final Key p1) throws IOException;
    
    Value put(final Transaction p0, final Key p1, final Value p2) throws IOException;
    
    Value get(final Transaction p0, final Key p1) throws IOException;
    
    boolean isTransient();
    
    Iterator<Map.Entry<Key, Value>> iterator(final Transaction p0) throws IOException, UnsupportedOperationException;
}
