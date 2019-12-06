// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

public class CacheFilter implements Cache
{
    protected final Cache next;
    
    public CacheFilter(final Cache next) {
        this.next = next;
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        return this.next.put(key, value);
    }
    
    @Override
    public Object get(final Object key) {
        return this.next.get(key);
    }
    
    @Override
    public Object remove(final Object key) {
        return this.next.remove(key);
    }
    
    @Override
    public void close() {
        this.next.close();
    }
    
    @Override
    public int size() {
        return this.next.size();
    }
}
