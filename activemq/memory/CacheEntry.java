// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

public class CacheEntry
{
    public final Object key;
    public final Object value;
    public CacheEntry next;
    public CacheEntry previous;
    public CacheEntryList owner;
    
    public CacheEntry(final Object key, final Object value) {
        this.key = key;
        this.value = value;
    }
    
    public boolean remove() {
        if (this.owner == null || this.key == null || this.next == null) {
            return false;
        }
        synchronized (this.owner.tail) {
            this.next.previous = this.previous;
            this.previous.next = this.next;
            this.owner = null;
            this.next = null;
            this.previous = null;
        }
        return true;
    }
}
