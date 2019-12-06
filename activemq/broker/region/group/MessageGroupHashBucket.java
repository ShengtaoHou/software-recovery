// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

import java.util.Map;
import org.apache.activemq.memory.LRUMap;
import org.apache.activemq.command.ConsumerId;

public class MessageGroupHashBucket implements MessageGroupMap
{
    private final int bucketCount;
    private final ConsumerId[] consumers;
    private final LRUMap<String, String> cache;
    
    public MessageGroupHashBucket(final int bucketCount, final int cachedSize) {
        this.bucketCount = bucketCount;
        this.consumers = new ConsumerId[bucketCount];
        this.cache = new LRUMap<String, String>(cachedSize);
    }
    
    @Override
    public synchronized void put(final String groupId, final ConsumerId consumerId) {
        final int bucket = this.getBucketNumber(groupId);
        this.consumers[bucket] = consumerId;
        if (consumerId != null) {
            this.cache.put(groupId, consumerId.toString());
        }
    }
    
    @Override
    public synchronized ConsumerId get(final String groupId) {
        final int bucket = this.getBucketNumber(groupId);
        this.cache.get(groupId);
        return this.consumers[bucket];
    }
    
    @Override
    public synchronized ConsumerId removeGroup(final String groupId) {
        final int bucket = this.getBucketNumber(groupId);
        final ConsumerId answer = this.consumers[bucket];
        this.consumers[bucket] = null;
        this.cache.remove(groupId);
        return answer;
    }
    
    @Override
    public synchronized MessageGroupSet removeConsumer(final ConsumerId consumerId) {
        MessageGroupSet answer = null;
        for (int i = 0; i < this.consumers.length; ++i) {
            final ConsumerId owner = this.consumers[i];
            if (owner != null && owner.equals(consumerId)) {
                answer = this.createMessageGroupSet(i, answer);
                this.consumers[i] = null;
            }
        }
        if (answer == null) {
            answer = EmptyMessageGroupSet.INSTANCE;
        }
        return answer;
    }
    
    @Override
    public synchronized void removeAll() {
        for (int i = 0; i < this.consumers.length; ++i) {
            this.consumers[i] = null;
        }
    }
    
    @Override
    public Map<String, String> getGroups() {
        return this.cache;
    }
    
    @Override
    public String getType() {
        return "bucket";
    }
    
    public int getBucketCount() {
        return this.bucketCount;
    }
    
    @Override
    public String toString() {
        int count = 0;
        for (int i = 0; i < this.consumers.length; ++i) {
            if (this.consumers[i] != null) {
                ++count;
            }
        }
        return "active message group buckets: " + count;
    }
    
    protected MessageGroupSet createMessageGroupSet(final int bucketNumber, final MessageGroupSet parent) {
        final MessageGroupSet answer = this.createMessageGroupSet(bucketNumber);
        if (parent == null) {
            return answer;
        }
        return new MessageGroupSet() {
            @Override
            public boolean contains(final String groupID) {
                return parent.contains(groupID) || answer.contains(groupID);
            }
        };
    }
    
    protected MessageGroupSet createMessageGroupSet(final int bucketNumber) {
        return new MessageGroupSet() {
            @Override
            public boolean contains(final String groupID) {
                final int bucket = MessageGroupHashBucket.this.getBucketNumber(groupID);
                return bucket == bucketNumber;
            }
        };
    }
    
    protected int getBucketNumber(final String groupId) {
        int bucket = groupId.hashCode() % this.bucketCount;
        if (bucket < 0) {
            bucket *= -1;
        }
        return bucket;
    }
}
