// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

public class MessageGroupHashBucketFactory implements MessageGroupMapFactory
{
    private int bucketCount;
    private int cacheSize;
    
    public MessageGroupHashBucketFactory() {
        this.bucketCount = 1024;
        this.cacheSize = 64;
    }
    
    @Override
    public MessageGroupMap createMessageGroupMap() {
        return new MessageGroupHashBucket(this.getBucketCount(), this.getCacheSize());
    }
    
    public int getBucketCount() {
        return this.bucketCount;
    }
    
    public void setBucketCount(final int bucketCount) {
        this.bucketCount = bucketCount;
    }
    
    public int getCacheSize() {
        return this.cacheSize;
    }
    
    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }
}
