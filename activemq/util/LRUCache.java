// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.Map;
import java.util.LinkedHashMap;

public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    private static final long serialVersionUID = -342098639681884413L;
    protected int maxCacheSize;
    
    public LRUCache() {
        this(0, 10000, 0.75f, true);
    }
    
    public LRUCache(final int maximumCacheSize) {
        this(0, maximumCacheSize, 0.75f, true);
    }
    
    public LRUCache(final int initialCapacity, final int maximumCacheSize, final float loadFactor, final boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxCacheSize = 10000;
        this.maxCacheSize = maximumCacheSize;
    }
    
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }
    
    public void setMaxCacheSize(final int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        if (this.size() > this.maxCacheSize) {
            this.onCacheEviction(eldest);
            return true;
        }
        return false;
    }
    
    protected void onCacheEviction(final Map.Entry<K, V> eldest) {
    }
}
