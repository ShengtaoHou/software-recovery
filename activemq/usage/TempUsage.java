// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import org.apache.activemq.store.PListStore;

public class TempUsage extends Usage<TempUsage>
{
    private PListStore store;
    
    public TempUsage() {
        super(null, null, 1.0f);
    }
    
    public TempUsage(final String name, final PListStore store) {
        super(null, name, 1.0f);
        this.store = store;
    }
    
    public TempUsage(final TempUsage parent, final String name) {
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
    
    public PListStore getStore() {
        return this.store;
    }
    
    public void setStore(final PListStore store) {
        this.store = store;
        this.onLimitChange();
    }
}
