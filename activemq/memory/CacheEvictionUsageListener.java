// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.memory;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.activemq.thread.Task;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.thread.TaskRunner;
import java.util.List;
import org.slf4j.Logger;
import org.apache.activemq.usage.UsageListener;

public class CacheEvictionUsageListener implements UsageListener
{
    private static final Logger LOG;
    private final List<CacheEvictor> evictors;
    private final int usageHighMark;
    private final int usageLowMark;
    private final TaskRunner evictionTask;
    private final Usage usage;
    
    public CacheEvictionUsageListener(final Usage usage, final int usageHighMark, final int usageLowMark, final TaskRunnerFactory taskRunnerFactory) {
        this.evictors = new CopyOnWriteArrayList<CacheEvictor>();
        this.usage = usage;
        this.usageHighMark = usageHighMark;
        this.usageLowMark = usageLowMark;
        this.evictionTask = taskRunnerFactory.createTaskRunner(new Task() {
            @Override
            public boolean iterate() {
                return CacheEvictionUsageListener.this.evictMessages();
            }
        }, "Cache Evictor: " + System.identityHashCode(this));
    }
    
    boolean evictMessages() {
        CacheEvictionUsageListener.LOG.debug("Evicting cache memory usage: {}", (Object)this.usage.getPercentUsage());
        final List<CacheEvictor> list = new LinkedList<CacheEvictor>(this.evictors);
        while (list.size() > 0 && this.usage.getPercentUsage() > this.usageLowMark) {
            final Iterator<CacheEvictor> iter = list.iterator();
            while (iter.hasNext()) {
                final CacheEvictor evictor = iter.next();
                if (evictor.evictCacheEntry() == null) {
                    iter.remove();
                }
            }
        }
        return false;
    }
    
    @Override
    public void onUsageChanged(final Usage usage, final int oldPercentUsage, final int newPercentUsage) {
        if (oldPercentUsage < newPercentUsage && usage.getPercentUsage() >= this.usageHighMark) {
            try {
                this.evictionTask.wakeup();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public void add(final CacheEvictor evictor) {
        this.evictors.add(evictor);
    }
    
    public void remove(final CacheEvictor evictor) {
        this.evictors.remove(evictor);
    }
    
    static {
        LOG = LoggerFactory.getLogger(CacheEvictionUsageListener.class);
    }
}
