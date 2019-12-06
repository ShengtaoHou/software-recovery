// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

public class CachedMessageGroupMapFactory implements MessageGroupMapFactory
{
    private int cacheSize;
    
    public CachedMessageGroupMapFactory() {
        this.cacheSize = 1024;
    }
    
    public int getCacheSize() {
        return this.cacheSize;
    }
    
    public void setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    @Override
    public MessageGroupMap createMessageGroupMap() {
        return new CachedMessageGroupMap(this.getCacheSize());
    }
}
