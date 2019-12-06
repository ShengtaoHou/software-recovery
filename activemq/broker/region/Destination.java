// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.broker.region.policy.SharedDeadLetterStrategy;
import org.apache.activemq.broker.region.policy.SlowConsumerStrategy;
import org.apache.activemq.command.MessageDispatchNotification;
import java.util.List;
import org.apache.activemq.usage.Usage;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.command.ActiveMQDestination;
import java.io.IOException;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.policy.DeadLetterStrategy;
import org.apache.activemq.command.Message;
import org.apache.activemq.thread.Task;
import org.apache.activemq.Service;

public interface Destination extends Service, Task, Message.MessageDestination
{
    public static final DeadLetterStrategy DEFAULT_DEAD_LETTER_STRATEGY = new SharedDeadLetterStrategy();
    public static final long DEFAULT_BLOCKED_PRODUCER_WARNING_INTERVAL = 30000L;
    
    void addSubscription(final ConnectionContext p0, final Subscription p1) throws Exception;
    
    void removeSubscription(final ConnectionContext p0, final Subscription p1, final long p2) throws Exception;
    
    void addProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    void removeProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    void send(final ProducerBrokerExchange p0, final Message p1) throws Exception;
    
    void acknowledge(final ConnectionContext p0, final Subscription p1, final MessageAck p2, final MessageReference p3) throws IOException;
    
    long getInactiveTimoutBeforeGC();
    
    void markForGC(final long p0);
    
    boolean canGC();
    
    void gc();
    
    ActiveMQDestination getActiveMQDestination();
    
    MemoryUsage getMemoryUsage();
    
    void setMemoryUsage(final MemoryUsage p0);
    
    void dispose(final ConnectionContext p0) throws IOException;
    
    boolean isDisposed();
    
    DestinationStatistics getDestinationStatistics();
    
    DeadLetterStrategy getDeadLetterStrategy();
    
    Message[] browse();
    
    String getName();
    
    MessageStore getMessageStore();
    
    boolean isProducerFlowControl();
    
    void setProducerFlowControl(final boolean p0);
    
    boolean isAlwaysRetroactive();
    
    void setAlwaysRetroactive(final boolean p0);
    
    void setBlockedProducerWarningInterval(final long p0);
    
    long getBlockedProducerWarningInterval();
    
    int getMaxProducersToAudit();
    
    void setMaxProducersToAudit(final int p0);
    
    int getMaxAuditDepth();
    
    void setMaxAuditDepth(final int p0);
    
    boolean isEnableAudit();
    
    void setEnableAudit(final boolean p0);
    
    boolean isActive();
    
    int getMaxPageSize();
    
    void setMaxPageSize(final int p0);
    
    int getMaxBrowsePageSize();
    
    void setMaxBrowsePageSize(final int p0);
    
    boolean isUseCache();
    
    void setUseCache(final boolean p0);
    
    int getMinimumMessageSize();
    
    void setMinimumMessageSize(final int p0);
    
    int getCursorMemoryHighWaterMark();
    
    void setCursorMemoryHighWaterMark(final int p0);
    
    void wakeup();
    
    boolean isLazyDispatch();
    
    void setLazyDispatch(final boolean p0);
    
    void messageExpired(final ConnectionContext p0, final Subscription p1, final MessageReference p2);
    
    void messageConsumed(final ConnectionContext p0, final MessageReference p1);
    
    void messageDelivered(final ConnectionContext p0, final MessageReference p1);
    
    void messageDiscarded(final ConnectionContext p0, final Subscription p1, final MessageReference p2);
    
    void slowConsumer(final ConnectionContext p0, final Subscription p1);
    
    void fastProducer(final ConnectionContext p0, final ProducerInfo p1);
    
    void isFull(final ConnectionContext p0, final Usage<?> p1);
    
    List<Subscription> getConsumers();
    
    void processDispatchNotification(final MessageDispatchNotification p0) throws Exception;
    
    boolean isPrioritizedMessages();
    
    SlowConsumerStrategy getSlowConsumerStrategy();
    
    boolean isDoOptimzeMessageStorage();
    
    void setDoOptimzeMessageStorage(final boolean p0);
    
    void clearPendingMessages();
    
    boolean isDLQ();
    
    void duplicateFromStore(final Message p0, final Subscription p1);
}
