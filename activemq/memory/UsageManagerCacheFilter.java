// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

import org.apache.activemq.usage.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;

public class UsageManagerCacheFilter extends CacheFilter
{
    private final AtomicLong totalUsage;
    private final MemoryUsage usage;
    
    public UsageManagerCacheFilter(final Cache next, final MemoryUsage um) {
        super(next);
        this.totalUsage = new AtomicLong(0L);
        this.usage = um;
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        long usageValue = this.getUsageOfAddedObject(value);
        final Object rc = super.put(key, value);
        if (rc != null) {
            usageValue -= this.getUsageOfRemovedObject(rc);
        }
        this.totalUsage.addAndGet(usageValue);
        this.usage.increaseUsage(usageValue);
        return rc;
    }
    
    @Override
    public Object remove(final Object key) {
        final Object rc = super.remove(key);
        if (rc != null) {
            final long usageValue = this.getUsageOfRemovedObject(rc);
            this.totalUsage.addAndGet(-usageValue);
            this.usage.decreaseUsage(usageValue);
        }
        return rc;
    }
    
    protected long getUsageOfAddedObject(final Object value) {
        return 1L;
    }
    
    protected long getUsageOfRemovedObject(final Object value) {
        return 1L;
    }
    
    @Override
    public void close() {
        this.usage.decreaseUsage(this.totalUsage.get());
    }
}
