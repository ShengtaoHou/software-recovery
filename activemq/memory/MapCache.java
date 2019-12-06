// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MapCache implements Cache
{
    protected final Map<Object, Object> map;
    
    public MapCache() {
        this(new ConcurrentHashMap<Object, Object>());
    }
    
    public MapCache(final Map<Object, Object> map) {
        this.map = map;
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        return this.map.put(key, value);
    }
    
    @Override
    public Object get(final Object key) {
        return this.map.get(key);
    }
    
    @Override
    public Object remove(final Object key) {
        return this.map.remove(key);
    }
    
    @Override
    public void close() {
        this.map.clear();
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
}
