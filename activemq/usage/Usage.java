// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.usage;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public abstract class Usage<T extends Usage> implements Service
{
    private static final Logger LOG;
    protected final ReentrantReadWriteLock usageLock;
    protected final Condition waitForSpaceCondition;
    protected int percentUsage;
    protected T parent;
    protected String name;
    private UsageCapacity limiter;
    private int percentUsageMinDelta;
    private final List<UsageListener> listeners;
    private final boolean debug;
    private float usagePortion;
    private final List<T> children;
    private final List<Runnable> callbacks;
    private int pollingTime;
    private final AtomicBoolean started;
    private ThreadPoolExecutor executor;
    
    public Usage(final T parent, String name, final float portion) {
        this.usageLock = new ReentrantReadWriteLock();
        this.waitForSpaceCondition = this.usageLock.writeLock().newCondition();
        this.limiter = new DefaultUsageCapacity();
        this.percentUsageMinDelta = 1;
        this.listeners = new CopyOnWriteArrayList<UsageListener>();
        this.debug = Usage.LOG.isDebugEnabled();
        this.usagePortion = 1.0f;
        this.children = new CopyOnWriteArrayList<T>();
        this.callbacks = new LinkedList<Runnable>();
        this.pollingTime = 100;
        this.started = new AtomicBoolean();
        this.parent = parent;
        this.usagePortion = portion;
        if (parent != null) {
            this.limiter.setLimit((long)(parent.getLimit() * (double)portion));
            name = parent.name + ":" + name;
        }
        this.name = name;
    }
    
    protected abstract long retrieveUsage();
    
    public void waitForSpace() throws InterruptedException {
        this.waitForSpace(0L);
    }
    
    public boolean waitForSpace(final long timeout) throws InterruptedException {
        return this.waitForSpace(timeout, 100);
    }
    
    public boolean waitForSpace(final long timeout, final int highWaterMark) throws InterruptedException {
        if (this.parent != null && !this.parent.waitForSpace(timeout, highWaterMark)) {
            return false;
        }
        this.usageLock.writeLock().lock();
        try {
            this.percentUsage = this.caclPercentUsage();
            if (this.percentUsage >= highWaterMark) {
                long timeleft;
                for (long deadline = timeleft = ((timeout > 0L) ? (System.currentTimeMillis() + timeout) : Long.MAX_VALUE); timeleft > 0L; timeleft = deadline - System.currentTimeMillis()) {
                    this.percentUsage = this.caclPercentUsage();
                    if (this.percentUsage < highWaterMark) {
                        break;
                    }
                    this.waitForSpaceCondition.await(this.pollingTime, TimeUnit.MILLISECONDS);
                }
            }
            return this.percentUsage < highWaterMark;
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    public boolean isFull() {
        return this.isFull(100);
    }
    
    public boolean isFull(final int highWaterMark) {
        if (this.parent != null && this.parent.isFull(highWaterMark)) {
            return true;
        }
        this.usageLock.writeLock().lock();
        try {
            this.percentUsage = this.caclPercentUsage();
            return this.percentUsage >= highWaterMark;
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    public void addUsageListener(final UsageListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeUsageListener(final UsageListener listener) {
        this.listeners.remove(listener);
    }
    
    public long getLimit() {
        this.usageLock.readLock().lock();
        try {
            return this.limiter.getLimit();
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    public void setLimit(final long limit) {
        if (this.percentUsageMinDelta < 0) {
            throw new IllegalArgumentException("percentUsageMinDelta must be greater or equal to 0");
        }
        this.usageLock.writeLock().lock();
        try {
            this.limiter.setLimit(limit);
            this.usagePortion = 0.0f;
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
        this.onLimitChange();
    }
    
    protected void onLimitChange() {
        if (this.usagePortion > 0.0f && this.parent != null) {
            this.usageLock.writeLock().lock();
            try {
                this.limiter.setLimit((long)(this.parent.getLimit() * (double)this.usagePortion));
            }
            finally {
                this.usageLock.writeLock().unlock();
            }
        }
        this.usageLock.writeLock().lock();
        try {
            this.setPercentUsage(this.caclPercentUsage());
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
        for (final T child : this.children) {
            child.onLimitChange();
        }
    }
    
    public float getUsagePortion() {
        this.usageLock.readLock().lock();
        try {
            return this.usagePortion;
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    public void setUsagePortion(final float usagePortion) {
        this.usageLock.writeLock().lock();
        try {
            this.usagePortion = usagePortion;
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
        this.onLimitChange();
    }
    
    public int getPercentUsage() {
        this.usageLock.readLock().lock();
        try {
            return this.percentUsage;
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    public int getPercentUsageMinDelta() {
        this.usageLock.readLock().lock();
        try {
            return this.percentUsageMinDelta;
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    public void setPercentUsageMinDelta(final int percentUsageMinDelta) {
        if (percentUsageMinDelta < 1) {
            throw new IllegalArgumentException("percentUsageMinDelta must be greater than 0");
        }
        this.usageLock.writeLock().lock();
        try {
            this.percentUsageMinDelta = percentUsageMinDelta;
            this.setPercentUsage(this.caclPercentUsage());
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    public long getUsage() {
        this.usageLock.readLock().lock();
        try {
            return this.retrieveUsage();
        }
        finally {
            this.usageLock.readLock().unlock();
        }
    }
    
    protected void setPercentUsage(final int value) {
        this.usageLock.writeLock().lock();
        try {
            final int oldValue = this.percentUsage;
            this.percentUsage = value;
            if (oldValue != value) {
                this.fireEvent(oldValue, value);
            }
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    protected int caclPercentUsage() {
        if (this.limiter.getLimit() == 0L) {
            return 0;
        }
        return (int)(this.retrieveUsage() * 100L / this.limiter.getLimit() / this.percentUsageMinDelta * this.percentUsageMinDelta);
    }
    
    private void fireEvent(final int oldPercentUsage, final int newPercentUsage) {
        if (this.debug) {
            Usage.LOG.debug(this.getName() + ": usage change from: " + oldPercentUsage + "% of available memory, to: " + newPercentUsage + "% of available memory");
        }
        if (this.started.get()) {
            if (oldPercentUsage >= 100 && newPercentUsage < 100) {
                this.waitForSpaceCondition.signalAll();
                if (!this.callbacks.isEmpty()) {
                    for (final Runnable callback : this.callbacks) {
                        this.getExecutor().execute(callback);
                    }
                    this.callbacks.clear();
                }
            }
            if (!this.listeners.isEmpty()) {
                final Runnable listenerNotifier = new Runnable() {
                    @Override
                    public void run() {
                        for (final UsageListener listener : Usage.this.listeners) {
                            listener.onUsageChanged(Usage.this, oldPercentUsage, newPercentUsage);
                        }
                    }
                };
                if (this.started.get()) {
                    this.getExecutor().execute(listenerNotifier);
                }
                else {
                    Usage.LOG.warn("Not notifying memory usage change to listeners on shutdown");
                }
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "Usage(" + this.getName() + ") percentUsage=" + this.percentUsage + "%, usage=" + this.retrieveUsage() + ", limit=" + this.limiter.getLimit() + ", percentUsageMinDelta=" + this.percentUsageMinDelta + "%" + ((this.parent != null) ? (";Parent:" + this.parent.toString()) : "");
    }
    
    @Override
    public void start() {
        if (this.started.compareAndSet(false, true)) {
            if (this.parent != null) {
                this.parent.addChild((Usage<Usage<Usage<Usage>>>)this);
                if (this.getLimit() > this.parent.getLimit()) {
                    Usage.LOG.info("Usage({}) limit={} should be smaller than its parent limit={}", this.getName(), this.getLimit(), this.parent.getLimit());
                }
            }
            for (final T t : this.children) {
                t.start();
            }
        }
    }
    
    @Override
    public void stop() {
        if (this.started.compareAndSet(true, false)) {
            if (this.parent != null) {
                this.parent.removeChild((Usage<Usage<Usage<Usage>>>)this);
            }
            this.usageLock.writeLock().lock();
            try {
                this.waitForSpaceCondition.signalAll();
                for (final Runnable callback : this.callbacks) {
                    callback.run();
                }
                this.callbacks.clear();
            }
            finally {
                this.usageLock.writeLock().unlock();
            }
            for (final T t : this.children) {
                t.stop();
            }
        }
    }
    
    protected void addChild(final T child) {
        this.children.add(child);
        if (this.started.get()) {
            child.start();
        }
    }
    
    protected void removeChild(final T child) {
        this.children.remove(child);
    }
    
    public boolean notifyCallbackWhenNotFull(final Runnable callback) {
        if (this.parent != null) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    Usage.this.usageLock.writeLock().lock();
                    try {
                        if (Usage.this.percentUsage >= 100) {
                            Usage.this.callbacks.add(callback);
                        }
                        else {
                            callback.run();
                        }
                    }
                    finally {
                        Usage.this.usageLock.writeLock().unlock();
                    }
                }
            };
            if (this.parent.notifyCallbackWhenNotFull(r)) {
                return true;
            }
        }
        this.usageLock.writeLock().lock();
        try {
            if (this.percentUsage >= 100) {
                this.callbacks.add(callback);
                return true;
            }
            return false;
        }
        finally {
            this.usageLock.writeLock().unlock();
        }
    }
    
    public UsageCapacity getLimiter() {
        return this.limiter;
    }
    
    public void setLimiter(final UsageCapacity limiter) {
        this.limiter = limiter;
    }
    
    public int getPollingTime() {
        return this.pollingTime;
    }
    
    public void setPollingTime(final int pollingTime) {
        this.pollingTime = pollingTime;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public T getParent() {
        return this.parent;
    }
    
    public void setParent(final T parent) {
        this.parent = parent;
    }
    
    public void setExecutor(final ThreadPoolExecutor executor) {
        this.executor = executor;
    }
    
    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }
    
    public boolean isStarted() {
        return this.started.get();
    }
    
    static {
        LOG = LoggerFactory.getLogger(Usage.class);
    }
}
