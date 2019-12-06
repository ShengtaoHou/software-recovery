// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.broker.scheduler.JobSchedulerStore;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.PersistenceAdapter;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.Service;

public class SystemUsage implements Service
{
    private SystemUsage parent;
    private String name;
    private MemoryUsage memoryUsage;
    private StoreUsage storeUsage;
    private TempUsage tempUsage;
    private ThreadPoolExecutor executor;
    private JobSchedulerUsage jobSchedulerUsage;
    private String checkLimitsLogLevel;
    private boolean sendFailIfNoSpaceExplicitySet;
    private boolean sendFailIfNoSpace;
    private boolean sendFailIfNoSpaceAfterTimeoutExplicitySet;
    private long sendFailIfNoSpaceAfterTimeout;
    private final List<SystemUsage> children;
    
    public SystemUsage() {
        this("default", null, null, null);
    }
    
    public SystemUsage(final String name, final PersistenceAdapter adapter, final PListStore tempStore, final JobSchedulerStore jobSchedulerStore) {
        this.checkLimitsLogLevel = "warn";
        this.sendFailIfNoSpaceAfterTimeout = 0L;
        this.children = new CopyOnWriteArrayList<SystemUsage>();
        this.parent = null;
        this.name = name;
        this.memoryUsage = new MemoryUsage(name + ":memory");
        this.storeUsage = new StoreUsage(name + ":store", adapter);
        this.tempUsage = new TempUsage(name + ":temp", tempStore);
        this.jobSchedulerUsage = new JobSchedulerUsage(name + ":jobScheduler", jobSchedulerStore);
        this.memoryUsage.setExecutor(this.getExecutor());
        this.storeUsage.setExecutor(this.getExecutor());
        this.tempUsage.setExecutor(this.getExecutor());
    }
    
    public SystemUsage(final SystemUsage parent, final String name) {
        this.checkLimitsLogLevel = "warn";
        this.sendFailIfNoSpaceAfterTimeout = 0L;
        this.children = new CopyOnWriteArrayList<SystemUsage>();
        this.parent = parent;
        this.executor = parent.getExecutor();
        this.name = name;
        this.memoryUsage = new MemoryUsage(parent.memoryUsage, name + ":memory");
        this.storeUsage = new StoreUsage(parent.storeUsage, name + ":store");
        this.tempUsage = new TempUsage(parent.tempUsage, name + ":temp");
        this.jobSchedulerUsage = new JobSchedulerUsage(parent.jobSchedulerUsage, name + ":jobScheduler");
        this.memoryUsage.setExecutor(this.getExecutor());
        this.storeUsage.setExecutor(this.getExecutor());
        this.tempUsage.setExecutor(this.getExecutor());
    }
    
    public String getName() {
        return this.name;
    }
    
    public MemoryUsage getMemoryUsage() {
        return this.memoryUsage;
    }
    
    public StoreUsage getStoreUsage() {
        return this.storeUsage;
    }
    
    public TempUsage getTempUsage() {
        return this.tempUsage;
    }
    
    public JobSchedulerUsage getJobSchedulerUsage() {
        return this.jobSchedulerUsage;
    }
    
    @Override
    public String toString() {
        return "UsageManager(" + this.getName() + ")";
    }
    
    @Override
    public void start() {
        if (this.parent != null) {
            this.parent.addChild(this);
        }
        this.memoryUsage.start();
        this.storeUsage.start();
        this.tempUsage.start();
        this.jobSchedulerUsage.start();
    }
    
    @Override
    public void stop() {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.memoryUsage.stop();
        this.storeUsage.stop();
        this.tempUsage.stop();
        this.jobSchedulerUsage.stop();
    }
    
    public void setSendFailIfNoSpace(final boolean failProducerIfNoSpace) {
        this.sendFailIfNoSpaceExplicitySet = true;
        this.sendFailIfNoSpace = failProducerIfNoSpace;
    }
    
    public boolean isSendFailIfNoSpace() {
        if (this.sendFailIfNoSpaceExplicitySet || this.parent == null) {
            return this.sendFailIfNoSpace;
        }
        return this.parent.isSendFailIfNoSpace();
    }
    
    private void addChild(final SystemUsage child) {
        this.children.add(child);
    }
    
    private void removeChild(final SystemUsage child) {
        this.children.remove(child);
    }
    
    public SystemUsage getParent() {
        return this.parent;
    }
    
    public void setParent(final SystemUsage parent) {
        this.parent = parent;
    }
    
    public boolean isSendFailIfNoSpaceExplicitySet() {
        return this.sendFailIfNoSpaceExplicitySet;
    }
    
    public void setSendFailIfNoSpaceExplicitySet(final boolean sendFailIfNoSpaceExplicitySet) {
        this.sendFailIfNoSpaceExplicitySet = sendFailIfNoSpaceExplicitySet;
    }
    
    public long getSendFailIfNoSpaceAfterTimeout() {
        if (this.sendFailIfNoSpaceAfterTimeoutExplicitySet || this.parent == null) {
            return this.sendFailIfNoSpaceAfterTimeout;
        }
        return this.parent.getSendFailIfNoSpaceAfterTimeout();
    }
    
    public void setSendFailIfNoSpaceAfterTimeout(final long sendFailIfNoSpaceAfterTimeout) {
        this.sendFailIfNoSpaceAfterTimeoutExplicitySet = true;
        this.sendFailIfNoSpaceAfterTimeout = sendFailIfNoSpaceAfterTimeout;
    }
    
    public void setName(final String name) {
        this.name = name;
        this.memoryUsage.setName(name + ":memory");
        this.storeUsage.setName(name + ":store");
        this.tempUsage.setName(name + ":temp");
        this.jobSchedulerUsage.setName(name + ":jobScheduler");
    }
    
    public void setMemoryUsage(final MemoryUsage memoryUsage) {
        if (memoryUsage.getName() == null) {
            memoryUsage.setName(this.memoryUsage.getName());
        }
        if (this.parent != null) {
            memoryUsage.setParent(this.parent.memoryUsage);
        }
        (this.memoryUsage = memoryUsage).setExecutor(this.getExecutor());
    }
    
    public void setStoreUsage(final StoreUsage storeUsage) {
        if (storeUsage.getStore() == null) {
            storeUsage.setStore(this.storeUsage.getStore());
        }
        if (storeUsage.getName() == null) {
            storeUsage.setName(this.storeUsage.getName());
        }
        if (this.parent != null) {
            storeUsage.setParent(this.parent.storeUsage);
        }
        (this.storeUsage = storeUsage).setExecutor(this.executor);
    }
    
    public void setTempUsage(final TempUsage tempDiskUsage) {
        if (tempDiskUsage.getStore() == null) {
            tempDiskUsage.setStore(this.tempUsage.getStore());
        }
        if (tempDiskUsage.getName() == null) {
            tempDiskUsage.setName(this.tempUsage.getName());
        }
        if (this.parent != null) {
            tempDiskUsage.setParent(this.parent.tempUsage);
        }
        (this.tempUsage = tempDiskUsage).setExecutor(this.getExecutor());
    }
    
    public void setJobSchedulerUsage(final JobSchedulerUsage jobSchedulerUsage) {
        if (jobSchedulerUsage.getStore() == null) {
            jobSchedulerUsage.setStore(this.jobSchedulerUsage.getStore());
        }
        if (jobSchedulerUsage.getName() == null) {
            jobSchedulerUsage.setName(this.jobSchedulerUsage.getName());
        }
        if (this.parent != null) {
            jobSchedulerUsage.setParent(this.parent.jobSchedulerUsage);
        }
        (this.jobSchedulerUsage = jobSchedulerUsage).setExecutor(this.getExecutor());
    }
    
    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }
    
    public void setExecutor(final ThreadPoolExecutor executor) {
        this.executor = executor;
        if (this.memoryUsage != null) {
            this.memoryUsage.setExecutor(this.executor);
        }
        if (this.storeUsage != null) {
            this.storeUsage.setExecutor(this.executor);
        }
        if (this.tempUsage != null) {
            this.tempUsage.setExecutor(this.executor);
        }
        if (this.jobSchedulerUsage != null) {
            this.jobSchedulerUsage.setExecutor(this.executor);
        }
    }
    
    public String getCheckLimitsLogLevel() {
        return this.checkLimitsLogLevel;
    }
    
    public void setCheckLimitsLogLevel(final String checkLimitsLogLevel) {
        this.checkLimitsLogLevel = checkLimitsLogLevel;
    }
}
