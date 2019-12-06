// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import java.util.concurrent.TimeUnit;

public class MemoryUsage extends Usage<MemoryUsage>
{
    private long usage;
    
    public MemoryUsage() {
        this(null, null);
    }
    
    public MemoryUsage(final MemoryUsage parent) {
        this(parent, "default");
    }
    
    public MemoryUsage(final String name) {
        this(null, name);
    }
    
    public MemoryUsage(final MemoryUsage parent, final String name) {
        this(parent, name, 1.0f);
    }
    
    public MemoryUsage(final MemoryUsage parent, final String name, final float portion) {
        super(parent, name, portion);
    }
    
    @Override
    public void waitForSpace() throws InterruptedException {
        if (this.parent != null) {
            ((MemoryUsage)this.parent).waitForSpace();
        }
        this.usageLock.readLock().lock();
        try {
            if (this.percentUsage >= 100 && this.isStarted()) {
                this.usageLock.readLock().unlock();
                this.usageLock.writeLock().lock();
                try {
                    while (this.percentUsage >= 100 && this.isStarted()) {
                        this.waitForSpaceCondition.await();
                    }
                    this.usageLock.readLock().lock();
                }
                finally {
                    this.usageLock.writeLock().unlock();
                }
            }
            if (this.percentUsage >= 100 && !this.isStarted()) {
                throw new InterruptedException("waitForSpace stopped during wait.");
            }
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean waitForSpace(final long timeout) throws InterruptedException {
        if (this.parent != null && !((MemoryUsage)this.parent).waitForSpace(timeout)) {
            return false;
        }
        this.usageLock.readLock().lock();
        try {
            if (this.percentUsage >= 100) {
                this.usageLock.readLock().unlock();
                this.usageLock.writeLock().lock();
                try {
                    while (this.percentUsage >= 100) {
                        this.waitForSpaceCondition.await(timeout, TimeUnit.MILLISECONDS);
                    }
                    this.usageLock.readLock().lock();
                }
                finally {
                    this.usageLock.writeLock().unlock();
                }
            }
            return this.percentUsage < 100;
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean isFull() {
        if (this.parent != null && ((MemoryUsage)this.parent).isFull()) {
            return true;
        }
        this.usageLock.readLock().lock();
        try {
            return this.percentUsage >= 100;
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    public void enqueueUsage(final long value) throws InterruptedException {
        this.waitForSpace();
        this.increaseUsage(value);
    }
    
    public void increaseUsage(final long value) {
        if (value == 0L) {
            return;
        }
        this.usageLock.writeLock().lock();
        try {
            this.usage += value;
            this.setPercentUsage(this.caclPercentUsage());
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
        if (this.parent != null) {
            ((MemoryUsage)this.parent).increaseUsage(value);
        }
    }
    
    public void decreaseUsage(final long value) {
        if (value == 0L) {
            return;
        }
        this.usageLock.writeLock().lock();
        try {
            this.usage -= value;
            this.setPercentUsage(this.caclPercentUsage());
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
        if (this.parent != null) {
            ((MemoryUsage)this.parent).decreaseUsage(value);
        }
    }
    
    @Override
    protected long retrieveUsage() {
        return this.usage;
    }
    
    @Override
    public long getUsage() {
        return this.usage;
    }
    
    public void setUsage(final long usage) {
        this.usage = usage;
    }
    
    public void setPercentOfJvmHeap(final int percentOfJvmHeap) {
        if (percentOfJvmHeap > 0) {
            this.setLimit(Math.round(Runtime.getRuntime().maxMemory() * percentOfJvmHeap / 100.0));
        }
    }
}
