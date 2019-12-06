// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

public interface Cache
{
    Object get(final Object p0);
    
    Object put(final Object p0, final Object p1);
    
    Object remove(final Object p0);
    
    void close();
    
    int size();
}
