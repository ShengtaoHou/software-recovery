// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import org.apache.activemq.store.PersistenceAdapter;

public class StoreUsage extends Usage<StoreUsage>
{
    private PersistenceAdapter store;
    
    public StoreUsage() {
        super(null, null, 1.0f);
    }
    
    public StoreUsage(final String name, final PersistenceAdapter store) {
        super(null, name, 1.0f);
        this.store = store;
    }
    
    public StoreUsage(final StoreUsage parent, final String name) {
        super(parent, name, 1.0f);
        this.store = parent.store;
    }
    
    @Override
    protected long retrieveUsage() {
        if (this.store == null) {
            return 0L;
        }
        return this.store.size();
    }
    
    public PersistenceAdapter getStore() {
        return this.store;
    }
    
    public void setStore(final PersistenceAdapter store) {
        this.store = store;
        this.onLimitChange();
    }
    
    @Override
    public int getPercentUsage() {
        this.usageLock.writeLock().lock();
        try {
            this.percentUsage = this.caclPercentUsage();
            return super.getPercentUsage();
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean waitForSpace(final long timeout, final int highWaterMark) throws InterruptedException {
        return (this.parent != null && ((StoreUsage)this.parent).waitForSpace(timeout, highWaterMark)) || super.waitForSpace(timeout, highWaterMark);
    }
}
