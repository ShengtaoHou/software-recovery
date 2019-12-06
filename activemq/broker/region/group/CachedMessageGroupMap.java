// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.memory.LRUMap;

public class CachedMessageGroupMap implements MessageGroupMap
{
    private final LRUMap<String, ConsumerId> cache;
    private final int maximumCacheSize;
    
    CachedMessageGroupMap(final int size) {
        this.cache = new LRUMap<String, ConsumerId>(size);
        this.maximumCacheSize = size;
    }
    
    @Override
    public synchronized void put(final String groupId, final ConsumerId consumerId) {
        this.cache.put(groupId, consumerId);
    }
    
    @Override
    public synchronized ConsumerId get(final String groupId) {
        return this.cache.get(groupId);
    }
    
    @Override
    public synchronized ConsumerId removeGroup(final String groupId) {
        return this.cache.remove(groupId);
    }
    
    @Override
    public synchronized MessageGroupSet removeConsumer(final ConsumerId consumerId) {
        final SimpleMessageGroupSet ownedGroups = new SimpleMessageGroupSet();
        final Map<String, ConsumerId> map = new HashMap<String, ConsumerId>();
        map.putAll(this.cache);
        for (final String group : map.keySet()) {
            final ConsumerId owner = map.get(group);
            if (owner.equals(consumerId)) {
                ownedGroups.add(group);
            }
        }
        for (final String group : ownedGroups.getUnderlyingSet()) {
            this.cache.remove(group);
        }
        return ownedGroups;
    }
    
    @Override
    public synchronized void removeAll() {
        this.cache.clear();
    }
    
    @Override
    public synchronized Map<String, String> getGroups() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final Map.Entry<String, ConsumerId> entry : this.cache.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }
        return result;
    }
    
    @Override
    public String getType() {
        return "cached";
    }
    
    public int getMaximumCacheSize() {
        return this.maximumCacheSize;
    }
    
    @Override
    public String toString() {
        return "message groups: " + this.cache.size();
    }
}
