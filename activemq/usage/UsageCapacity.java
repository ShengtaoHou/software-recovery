// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

public interface UsageCapacity
{
    boolean isLimit(final long p0);
    
    long getLimit();
    
    void setLimit(final long p0);
}
