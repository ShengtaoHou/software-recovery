// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

public class CacheEntryList
{
    public final CacheEntry tail;
    
    public CacheEntryList() {
        this.tail = new CacheEntry(null, null);
        this.tail.next = this.tail;
        this.tail.previous = this.tail;
    }
    
    public void add(final CacheEntry ce) {
        this.addEntryBefore(this.tail, ce);
    }
    
    private void addEntryBefore(final CacheEntry position, final CacheEntry ce) {
        assert ce.key != null && ce.next == null && ce.owner == null;
        synchronized (this.tail) {
            ce.owner = this;
            ce.next = position;
            ce.previous = position.previous;
            ce.previous.next = ce;
            ce.next.previous = ce;
        }
    }
    
    public void clear() {
        synchronized (this.tail) {
            this.tail.next = this.tail;
            this.tail.previous = this.tail;
        }
    }
    
    public CacheEvictor createFIFOCacheEvictor() {
        return new CacheEvictor() {
            @Override
            public CacheEntry evictCacheEntry() {
                final CacheEntry rc;
                synchronized (CacheEntryList.this.tail) {
                    rc = CacheEntryList.this.tail.next;
                }
                return rc.remove() ? rc : null;
            }
        };
    }
    
    public CacheEvictor createLIFOCacheEvictor() {
        return new CacheEvictor() {
            @Override
            public CacheEntry evictCacheEntry() {
                final CacheEntry rc;
                synchronized (CacheEntryList.this.tail) {
                    rc = CacheEntryList.this.tail.previous;
                }
                return rc.remove() ? rc : null;
            }
        };
    }
}
