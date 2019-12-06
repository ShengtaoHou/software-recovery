// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

public class DefaultUsageCapacity implements UsageCapacity
{
    private long limit;
    
    @Override
    public boolean isLimit(final long size) {
        return size >= this.limit;
    }
    
    @Override
    public final long getLimit() {
        return this.limit;
    }
    
    @Override
    public final void setLimit(final long limit) {
        this.limit = limit;
    }
}
