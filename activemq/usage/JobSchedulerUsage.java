// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import org.apache.activemq.broker.scheduler.JobSchedulerStore;

public class JobSchedulerUsage extends Usage<JobSchedulerUsage>
{
    private JobSchedulerStore store;
    
    public JobSchedulerUsage() {
        super(null, null, 1.0f);
    }
    
    public JobSchedulerUsage(final String name, final JobSchedulerStore store) {
        super(null, name, 1.0f);
        this.store = store;
    }
    
    public JobSchedulerUsage(final JobSchedulerUsage parent, final String name) {
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
    
    public JobSchedulerStore getStore() {
        return this.store;
    }
    
    public void setStore(final JobSchedulerStore store) {
        this.store = store;
        this.onLimitChange();
    }
}
