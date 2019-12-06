// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import javax.management.ObjectName;
import javax.jms.InvalidSelectorException;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.ConsumerInfo;
import java.util.List;
import org.apache.activemq.command.ActiveMQDestination;
import java.io.IOException;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConnectionContext;

public interface Subscription extends SubscriptionRecovery
{
    void add(final MessageReference p0) throws Exception;
    
    void acknowledge(final ConnectionContext p0, final MessageAck p1) throws Exception;
    
    Response pullMessage(final ConnectionContext p0, final MessagePull p1) throws Exception;
    
    boolean isWildcard();
    
    boolean matches(final MessageReference p0, final MessageEvaluationContext p1) throws IOException;
    
    boolean matches(final ActiveMQDestination p0);
    
    void add(final ConnectionContext p0, final Destination p1) throws Exception;
    
    List<MessageReference> remove(final ConnectionContext p0, final Destination p1) throws Exception;
    
    ConsumerInfo getConsumerInfo();
    
    void gc();
    
    void processMessageDispatchNotification(final MessageDispatchNotification p0) throws Exception;
    
    int getPendingQueueSize();
    
    int getDispatchedQueueSize();
    
    long getDispatchedCounter();
    
    long getEnqueueCounter();
    
    long getDequeueCounter();
    
    String getSelector();
    
    void setSelector(final String p0) throws InvalidSelectorException, UnsupportedOperationException;
    
    ObjectName getObjectName();
    
    void setObjectName(final ObjectName p0);
    
    boolean isLowWaterMark();
    
    boolean isHighWaterMark();
    
    boolean isFull();
    
    void updateConsumerPrefetch(final int p0);
    
    void destroy();
    
    int getPrefetchSize();
    
    int getInFlightSize();
    
    int getInFlightUsage();
    
    boolean isRecoveryRequired();
    
    boolean isBrowser();
    
    int countBeforeFull();
    
    ConnectionContext getContext();
    
    int getCursorMemoryHighWaterMark();
    
    void setCursorMemoryHighWaterMark(final int p0);
    
    boolean isSlowConsumer();
    
    void unmatched(final MessageReference p0) throws IOException;
    
    long getTimeOfLastMessageAck();
    
    long getConsumedCount();
    
    void incrementConsumedCount();
    
    void resetConsumedCount();
}
