// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

import java.util.Map;
import java.util.LinkedHashMap;

public class LRUMap<K, V> extends LinkedHashMap<K, V>
{
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    protected static final int DEFAULT_INITIAL_CAPACITY = 5000;
    private static final long serialVersionUID = -9179676638408888162L;
    private int maximumSize;
    
    public LRUMap(final int maximumSize) {
        this(5000, 0.75f, true, maximumSize);
    }
    
    public LRUMap(final int maximumSize, final boolean accessOrder) {
        this(5000, 0.75f, accessOrder, maximumSize);
    }
    
    public LRUMap(final int initialCapacity, final float loadFactor, final boolean accessOrder, final int maximumSize) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maximumSize = maximumSize;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this.maximumSize;
    }
}
