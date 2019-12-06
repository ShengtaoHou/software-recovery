// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.cursors;

import org.apache.activemq.command.MessageId;
import org.apache.activemq.ActiveMQMessageAudit;
import java.util.LinkedList;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.region.MessageReference;
import java.util.List;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.Service;

public interface PendingMessageCursor extends Service
{
    void add(final ConnectionContext p0, final Destination p1) throws Exception;
    
    List<MessageReference> remove(final ConnectionContext p0, final Destination p1) throws Exception;
    
    boolean isEmpty();
    
    boolean isEmpty(final Destination p0);
    
    void reset();
    
    void release();
    
    void addMessageLast(final MessageReference p0) throws Exception;
    
    boolean tryAddMessageLast(final MessageReference p0, final long p1) throws Exception;
    
    void addMessageFirst(final MessageReference p0) throws Exception;
    
    void addRecoveredMessage(final MessageReference p0) throws Exception;
    
    boolean hasNext();
    
    MessageReference next();
    
    void remove();
    
    int size();
    
    void clear();
    
    boolean isRecoveryRequired();
    
    int getMaxBatchSize();
    
    void setMaxBatchSize(final int p0);
    
    void resetForGC();
    
    void remove(final MessageReference p0);
    
    void gc();
    
    void setSystemUsage(final SystemUsage p0);
    
    SystemUsage getSystemUsage();
    
    int getMemoryUsageHighWaterMark();
    
    void setMemoryUsageHighWaterMark(final int p0);
    
    boolean isFull();
    
    boolean hasSpace();
    
    boolean hasMessagesBufferedToDeliver();
    
    void destroy() throws Exception;
    
    LinkedList<MessageReference> pageInList(final int p0);
    
    void setMaxProducersToAudit(final int p0);
    
    int getMaxProducersToAudit();
    
    void setMaxAuditDepth(final int p0);
    
    int getMaxAuditDepth();
    
    boolean isEnableAudit();
    
    void setEnableAudit(final boolean p0);
    
    boolean isTransient();
    
    void setMessageAudit(final ActiveMQMessageAudit p0);
    
    ActiveMQMessageAudit getMessageAudit();
    
    void setUseCache(final boolean p0);
    
    boolean isUseCache();
    
    void rollback(final MessageId p0);
    
    boolean isCacheEnabled();
    
    void rebase();
}
