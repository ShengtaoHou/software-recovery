// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.MessageId;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.usage.SystemUsage;

public abstract class AbstractPendingMessageCursor implements PendingMessageCursor
{
    protected int memoryUsageHighWaterMark;
    protected int maxBatchSize;
    protected SystemUsage systemUsage;
    protected int maxProducersToAudit;
    protected int maxAuditDepth;
    protected boolean enableAudit;
    protected ActiveMQMessageAudit audit;
    protected boolean useCache;
    private boolean cacheEnabled;
    private boolean started;
    protected MessageReference last;
    protected final boolean prioritizedMessages;
    
    public AbstractPendingMessageCursor(final boolean prioritizedMessages) {
        this.memoryUsageHighWaterMark = 70;
        this.maxBatchSize = 200;
        this.maxProducersToAudit = 64;
        this.maxAuditDepth = 2048;
        this.enableAudit = true;
        this.useCache = true;
        this.cacheEnabled = true;
        this.started = false;
        this.last = null;
        this.prioritizedMessages = prioritizedMessages;
    }
    
    @Override
    public synchronized void start() throws Exception {
        if (!this.started && this.enableAudit && this.audit == null) {
            this.audit = new ActiveMQMessageAudit(this.maxAuditDepth, this.maxProducersToAudit);
        }
        this.started = true;
    }
    
    @Override
    public synchronized void stop() throws Exception {
        this.started = false;
        this.gc();
    }
    
    @Override
    public void add(final ConnectionContext context, final Destination destination) throws Exception {
    }
    
    @Override
    public List<MessageReference> remove(final ConnectionContext context, final Destination destination) throws Exception {
        return (List<MessageReference>)Collections.EMPTY_LIST;
    }
    
    @Override
    public boolean isRecoveryRequired() {
        return true;
    }
    
    @Override
    public void addMessageFirst(final MessageReference node) throws Exception {
    }
    
    @Override
    public void addMessageLast(final MessageReference node) throws Exception {
    }
    
    @Override
    public boolean tryAddMessageLast(final MessageReference node, final long maxWaitTime) throws Exception {
        this.addMessageLast(node);
        return true;
    }
    
    @Override
    public void addRecoveredMessage(final MessageReference node) throws Exception {
        this.addMessageLast(node);
    }
    
    @Override
    public void clear() {
    }
    
    @Override
    public boolean hasNext() {
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean isEmpty(final Destination destination) {
        return this.isEmpty();
    }
    
    @Override
    public MessageReference next() {
        return null;
    }
    
    @Override
    public void remove() {
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public int getMaxBatchSize() {
        return this.maxBatchSize;
    }
    
    @Override
    public void setMaxBatchSize(final int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }
    
    protected void fillBatch() throws Exception {
    }
    
    @Override
    public void resetForGC() {
        this.reset();
    }
    
    @Override
    public void remove(final MessageReference node) {
    }
    
    @Override
    public void gc() {
    }
    
    @Override
    public void setSystemUsage(final SystemUsage usageManager) {
        this.systemUsage = usageManager;
    }
    
    @Override
    public boolean hasSpace() {
        return this.systemUsage == null || !this.systemUsage.getMemoryUsage().isFull(this.memoryUsageHighWaterMark);
    }
    
    @Override
    public boolean isFull() {
        return this.systemUsage != null && this.systemUsage.getMemoryUsage().isFull();
    }
    
    @Override
    public void release() {
    }
    
    @Override
    public boolean hasMessagesBufferedToDeliver() {
        return false;
    }
    
    @Override
    public int getMemoryUsageHighWaterMark() {
        return this.memoryUsageHighWaterMark;
    }
    
    @Override
    public void setMemoryUsageHighWaterMark(final int memoryUsageHighWaterMark) {
        this.memoryUsageHighWaterMark = memoryUsageHighWaterMark;
    }
    
    @Override
    public SystemUsage getSystemUsage() {
        return this.systemUsage;
    }
    
    @Override
    public void destroy() throws Exception {
        this.stop();
    }
    
    @Override
    public LinkedList<MessageReference> pageInList(final int maxItems) {
        throw new RuntimeException("Not supported");
    }
    
    @Override
    public int getMaxProducersToAudit() {
        return this.maxProducersToAudit;
    }
    
    @Override
    public synchronized void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.maxProducersToAudit = maxProducersToAudit;
        if (this.audit != null) {
            this.audit.setMaximumNumberOfProducersToTrack(maxProducersToAudit);
        }
    }
    
    @Override
    public int getMaxAuditDepth() {
        return this.maxAuditDepth;
    }
    
    @Override
    public synchronized void setMaxAuditDepth(final int maxAuditDepth) {
        this.maxAuditDepth = maxAuditDepth;
        if (this.audit != null) {
            this.audit.setAuditDepth(maxAuditDepth);
        }
    }
    
    @Override
    public boolean isEnableAudit() {
        return this.enableAudit;
    }
    
    @Override
    public synchronized void setEnableAudit(final boolean enableAudit) {
        this.enableAudit = enableAudit;
        if (enableAudit && this.started && this.audit == null) {
            this.audit = new ActiveMQMessageAudit(this.maxAuditDepth, this.maxProducersToAudit);
        }
    }
    
    @Override
    public boolean isTransient() {
        return false;
    }
    
    @Override
    public void setMessageAudit(final ActiveMQMessageAudit audit) {
        this.audit = audit;
    }
    
    @Override
    public ActiveMQMessageAudit getMessageAudit() {
        return this.audit;
    }
    
    @Override
    public boolean isUseCache() {
        return this.useCache;
    }
    
    @Override
    public void setUseCache(final boolean useCache) {
        this.useCache = useCache;
    }
    
    public synchronized boolean isDuplicate(final MessageId messageId) {
        final boolean unique = this.recordUniqueId(messageId);
        this.rollback(messageId);
        return !unique;
    }
    
    public synchronized boolean recordUniqueId(final MessageId messageId) {
        return !this.enableAudit || this.audit == null || !this.audit.isDuplicate(messageId);
    }
    
    @Override
    public synchronized void rollback(final MessageId id) {
        if (this.audit != null) {
            this.audit.rollback(id);
        }
    }
    
    public synchronized boolean isStarted() {
        return this.started;
    }
    
    public static boolean isPrioritizedMessageSubscriber(final Broker broker, final Subscription sub) {
        boolean result = false;
        final Set<Destination> destinations = broker.getDestinations(sub.getActiveMQDestination());
        if (destinations != null) {
            for (final Destination dest : destinations) {
                if (dest.isPrioritizedMessages()) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    @Override
    public synchronized boolean isCacheEnabled() {
        return this.cacheEnabled;
    }
    
    public synchronized void setCacheEnabled(final boolean val) {
        this.cacheEnabled = val;
    }
    
    @Override
    public void rebase() {
    }
}
