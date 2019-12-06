// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache<Key, Value> implements Map<Key, Value>
{
    private final Map<Key, CacheNode<Key, Value>> cache;
    private final LinkedHashSet[] frequencyList;
    private int lowestFrequency;
    private int maxFrequency;
    private final int maxCacheSize;
    private final float evictionFactor;
    
    public LFUCache(final int maxCacheSize, final float evictionFactor) {
        if (evictionFactor <= 0.0f || evictionFactor >= 1.0f) {
            throw new IllegalArgumentException("Eviction factor must be greater than 0 and lesser than or equal to 1");
        }
        this.cache = new HashMap<Key, CacheNode<Key, Value>>(maxCacheSize);
        this.frequencyList = new LinkedHashSet[maxCacheSize];
        this.lowestFrequency = 0;
        this.maxFrequency = maxCacheSize - 1;
        this.maxCacheSize = maxCacheSize;
        this.evictionFactor = evictionFactor;
        this.initFrequencyList();
    }
    
    @Override
    public Value put(final Key k, final Value v) {
        Value oldValue = null;
        CacheNode<Key, Value> currentNode = this.cache.get(k);
        if (currentNode == null) {
            if (this.cache.size() == this.maxCacheSize) {
                this.doEviction();
            }
            final LinkedHashSet<CacheNode<Key, Value>> nodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[0];
            currentNode = new CacheNode<Key, Value>(k, v, 0);
            nodes.add(currentNode);
            this.cache.put(k, currentNode);
            this.lowestFrequency = 0;
        }
        else {
            oldValue = currentNode.v;
            currentNode.v = v;
        }
        return oldValue;
    }
    
    @Override
    public void putAll(final Map<? extends Key, ? extends Value> map) {
        for (final Entry<? extends Key, ? extends Value> me : map.entrySet()) {
            this.put(me.getKey(), me.getValue());
        }
    }
    
    @Override
    public Value get(final Object k) {
        final CacheNode<Key, Value> currentNode = this.cache.get(k);
        if (currentNode != null) {
            final int currentFrequency = currentNode.frequency;
            if (currentFrequency < this.maxFrequency) {
                final int nextFrequency = currentFrequency + 1;
                final LinkedHashSet<CacheNode<Key, Value>> currentNodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[currentFrequency];
                final LinkedHashSet<CacheNode<Key, Value>> newNodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[nextFrequency];
                this.moveToNextFrequency(currentNode, nextFrequency, currentNodes, newNodes);
                this.cache.put((Key)k, currentNode);
                if (this.lowestFrequency == currentFrequency && currentNodes.isEmpty()) {
                    this.lowestFrequency = nextFrequency;
                }
            }
            else {
                final LinkedHashSet<CacheNode<Key, Value>> nodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[currentFrequency];
                nodes.remove(currentNode);
                nodes.add(currentNode);
            }
            return currentNode.v;
        }
        return null;
    }
    
    @Override
    public Value remove(final Object k) {
        final CacheNode<Key, Value> currentNode = this.cache.remove(k);
        if (currentNode != null) {
            final LinkedHashSet<CacheNode<Key, Value>> nodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[currentNode.frequency];
            nodes.remove(currentNode);
            if (this.lowestFrequency == currentNode.frequency) {
                this.findNextLowestFrequency();
            }
            return currentNode.v;
        }
        return null;
    }
    
    public int frequencyOf(final Key k) {
        final CacheNode<Key, Value> node = this.cache.get(k);
        if (node != null) {
            return node.frequency + 1;
        }
        return 0;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i <= this.maxFrequency; ++i) {
            this.frequencyList[i].clear();
        }
        this.cache.clear();
        this.lowestFrequency = 0;
    }
    
    @Override
    public Set<Key> keySet() {
        return this.cache.keySet();
    }
    
    @Override
    public Collection<Value> values() {
        return null;
    }
    
    @Override
    public Set<Entry<Key, Value>> entrySet() {
        return null;
    }
    
    @Override
    public int size() {
        return this.cache.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.cache.containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return false;
    }
    
    private void initFrequencyList() {
        for (int i = 0; i <= this.maxFrequency; ++i) {
            this.frequencyList[i] = new LinkedHashSet();
        }
    }
    
    private void doEviction() {
        int currentlyDeleted = 0;
        final float target = this.maxCacheSize * this.evictionFactor;
        while (currentlyDeleted < target) {
            final LinkedHashSet<CacheNode<Key, Value>> nodes = (LinkedHashSet<CacheNode<Key, Value>>)this.frequencyList[this.lowestFrequency];
            if (nodes.isEmpty()) {
                throw new IllegalStateException("Lowest frequency constraint violated!");
            }
            final Iterator<CacheNode<Key, Value>> it = nodes.iterator();
            while (it.hasNext() && currentlyDeleted++ < target) {
                final CacheNode<Key, Value> node = it.next();
                it.remove();
                this.cache.remove(node.k);
            }
            if (it.hasNext()) {
                continue;
            }
            this.findNextLowestFrequency();
        }
    }
    
    private void moveToNextFrequency(final CacheNode<Key, Value> currentNode, final int nextFrequency, final LinkedHashSet<CacheNode<Key, Value>> currentNodes, final LinkedHashSet<CacheNode<Key, Value>> newNodes) {
        currentNodes.remove(currentNode);
        newNodes.add(currentNode);
        currentNode.frequency = nextFrequency;
    }
    
    private void findNextLowestFrequency() {
        while (this.lowestFrequency <= this.maxFrequency && this.frequencyList[this.lowestFrequency].isEmpty()) {
            ++this.lowestFrequency;
        }
        if (this.lowestFrequency > this.maxFrequency) {
            this.lowestFrequency = 0;
        }
    }
    
    private static class CacheNode<Key, Value>
    {
        public final Key k;
        public Value v;
        public int frequency;
        
        public CacheNode(final Key k, final Value v, final int frequency) {
            this.k = k;
            this.v = v;
            this.frequency = frequency;
        }
    }
}
