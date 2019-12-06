// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.slf4j.LoggerFactory;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;

public class StoreQueueCursor extends AbstractPendingMessageCursor
{
    private static final Logger LOG;
    private final Broker broker;
    private int pendingCount;
    private final Queue queue;
    private PendingMessageCursor nonPersistent;
    private final QueueStorePrefetch persistent;
    private boolean started;
    private PendingMessageCursor currentCursor;
    
    public StoreQueueCursor(final Broker broker, final Queue queue) {
        super(queue != null && queue.isPrioritizedMessages());
        this.broker = broker;
        this.queue = queue;
        this.persistent = new QueueStorePrefetch(queue, broker);
        this.currentCursor = this.persistent;
    }
    
    @Override
    public synchronized void start() throws Exception {
        this.started = true;
        super.start();
        if (this.nonPersistent == null) {
            if (this.broker.getBrokerService().isPersistent()) {
                this.nonPersistent = new FilePendingMessageCursor(this.broker, this.queue.getName(), this.prioritizedMessages);
            }
            else {
                this.nonPersistent = new VMPendingMessageCursor(this.prioritizedMessages);
            }
            this.nonPersistent.setMaxBatchSize(this.getMaxBatchSize());
            this.nonPersistent.setSystemUsage(this.systemUsage);
            this.nonPersistent.setEnableAudit(this.isEnableAudit());
            this.nonPersistent.setMaxAuditDepth(this.getMaxAuditDepth());
            this.nonPersistent.setMaxProducersToAudit(this.getMaxProducersToAudit());
        }
        this.nonPersistent.setMessageAudit(this.getMessageAudit());
        this.nonPersistent.start();
        this.persistent.setMessageAudit(this.getMessageAudit());
        this.persistent.start();
        this.pendingCount = this.persistent.size() + this.nonPersistent.size();
    }
    
    @Override
    public synchronized void stop() throws Exception {
        this.started = false;
        if (this.nonPersistent != null) {
            this.nonPersistent.destroy();
        }
        this.persistent.stop();
        this.persistent.gc();
        super.stop();
        this.pendingCount = 0;
    }
    
    @Override
    public synchronized void addMessageLast(final MessageReference node) throws Exception {
        if (node != null) {
            final Message msg = node.getMessage();
            if (this.started) {
                ++this.pendingCount;
                if (!msg.isPersistent()) {
                    this.nonPersistent.addMessageLast(node);
                }
            }
            if (msg.isPersistent()) {
                this.persistent.addMessageLast(node);
            }
        }
    }
    
    @Override
    public synchronized void addMessageFirst(final MessageReference node) throws Exception {
        if (node != null) {
            final Message msg = node.getMessage();
            if (this.started) {
                ++this.pendingCount;
                if (!msg.isPersistent()) {
                    this.nonPersistent.addMessageFirst(node);
                }
            }
            if (msg.isPersistent()) {
                this.persistent.addMessageFirst(node);
            }
        }
    }
    
    @Override
    public synchronized void clear() {
        this.pendingCount = 0;
    }
    
    @Override
    public synchronized boolean hasNext() {
        try {
            this.getNextCursor();
        }
        catch (Exception e) {
            StoreQueueCursor.LOG.error("Failed to get current cursor ", e);
            throw new RuntimeException(e);
        }
        return this.currentCursor != null && this.currentCursor.hasNext();
    }
    
    @Override
    public synchronized MessageReference next() {
        final MessageReference result = (this.currentCursor != null) ? this.currentCursor.next() : null;
        return result;
    }
    
    @Override
    public synchronized void remove() {
        if (this.currentCursor != null) {
            this.currentCursor.remove();
        }
        --this.pendingCount;
    }
    
    @Override
    public synchronized void remove(final MessageReference node) {
        if (!node.isPersistent()) {
            this.nonPersistent.remove(node);
        }
        else {
            this.persistent.remove(node);
        }
        --this.pendingCount;
    }
    
    @Override
    public synchronized void reset() {
        this.nonPersistent.reset();
        this.persistent.reset();
        this.pendingCount = this.persistent.size() + this.nonPersistent.size();
    }
    
    @Override
    public void release() {
        this.nonPersistent.release();
        this.persistent.release();
    }
    
    @Override
    public synchronized int size() {
        if (this.pendingCount < 0) {
            this.pendingCount = this.persistent.size() + this.nonPersistent.size();
        }
        return this.pendingCount;
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.pendingCount == 0;
    }
    
    @Override
    public boolean isRecoveryRequired() {
        return false;
    }
    
    public PendingMessageCursor getNonPersistent() {
        return this.nonPersistent;
    }
    
    public void setNonPersistent(final PendingMessageCursor nonPersistent) {
        this.nonPersistent = nonPersistent;
    }
    
    @Override
    public void setMaxBatchSize(final int maxBatchSize) {
        this.persistent.setMaxBatchSize(maxBatchSize);
        if (this.nonPersistent != null) {
            this.nonPersistent.setMaxBatchSize(maxBatchSize);
        }
        super.setMaxBatchSize(maxBatchSize);
    }
    
    @Override
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        super.setMaxProducersToAudit(maxProducersToAudit);
        if (this.persistent != null) {
            this.persistent.setMaxProducersToAudit(maxProducersToAudit);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setMaxProducersToAudit(maxProducersToAudit);
        }
    }
    
    @Override
    public void setMaxAuditDepth(final int maxAuditDepth) {
        super.setMaxAuditDepth(maxAuditDepth);
        if (this.persistent != null) {
            this.persistent.setMaxAuditDepth(maxAuditDepth);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setMaxAuditDepth(maxAuditDepth);
        }
    }
    
    @Override
    public void setEnableAudit(final boolean enableAudit) {
        super.setEnableAudit(enableAudit);
        if (this.persistent != null) {
            this.persistent.setEnableAudit(enableAudit);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setEnableAudit(enableAudit);
        }
    }
    
    @Override
    public void setUseCache(final boolean useCache) {
        super.setUseCache(useCache);
        if (this.persistent != null) {
            this.persistent.setUseCache(useCache);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setUseCache(useCache);
        }
    }
    
    @Override
    public void setMemoryUsageHighWaterMark(final int memoryUsageHighWaterMark) {
        super.setMemoryUsageHighWaterMark(memoryUsageHighWaterMark);
        if (this.persistent != null) {
            this.persistent.setMemoryUsageHighWaterMark(memoryUsageHighWaterMark);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setMemoryUsageHighWaterMark(memoryUsageHighWaterMark);
        }
    }
    
    @Override
    public synchronized void gc() {
        if (this.persistent != null) {
            this.persistent.gc();
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.gc();
        }
        this.pendingCount = this.persistent.size() + this.nonPersistent.size();
    }
    
    @Override
    public void setSystemUsage(final SystemUsage usageManager) {
        super.setSystemUsage(usageManager);
        if (this.persistent != null) {
            this.persistent.setSystemUsage(usageManager);
        }
        if (this.nonPersistent != null) {
            this.nonPersistent.setSystemUsage(usageManager);
        }
    }
    
    protected synchronized PendingMessageCursor getNextCursor() throws Exception {
        if (this.currentCursor == null || !this.currentCursor.hasMessagesBufferedToDeliver()) {
            this.currentCursor = ((this.currentCursor == this.persistent) ? this.nonPersistent : this.persistent);
            if (this.currentCursor.isEmpty()) {
                this.currentCursor = ((this.currentCursor == this.persistent) ? this.nonPersistent : this.persistent);
            }
        }
        return this.currentCursor;
    }
    
    @Override
    public boolean isCacheEnabled() {
        boolean cacheEnabled = this.isUseCache();
        if (cacheEnabled) {
            if (this.persistent != null) {
                cacheEnabled &= this.persistent.isCacheEnabled();
            }
            if (this.nonPersistent != null) {
                cacheEnabled &= this.nonPersistent.isCacheEnabled();
            }
            this.setCacheEnabled(cacheEnabled);
        }
        return cacheEnabled;
    }
    
    @Override
    public void rebase() {
        this.persistent.rebase();
        this.reset();
    }
    
    static {
        LOG = LoggerFactory.getLogger(StoreQueueCursor.class);
    }
}
