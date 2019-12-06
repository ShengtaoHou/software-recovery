// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.ObjectName;
import javax.jms.InvalidSelectorException;

public interface SubscriptionViewMBean
{
    @MBeanInfo("JMS Client id of the Connection the Subscription is on.")
    String getClientId();
    
    @MBeanInfo("ID of the Connection the Subscription is on.")
    String getConnectionId();
    
    @MBeanInfo("ID of the Session the Subscription is on.")
    long getSessionId();
    
    @Deprecated
    @MBeanInfo("ID of the Subscription.")
    long getSubcriptionId();
    
    @MBeanInfo("ID of the Subscription.")
    long getSubscriptionId();
    
    @MBeanInfo("The name of the destionation the subscription is on.")
    String getDestinationName();
    
    @MBeanInfo("The SQL-92 message header selector or XPATH body selector of the subscription.")
    String getSelector();
    
    void setSelector(@MBeanInfo("selector") final String p0) throws InvalidSelectorException, UnsupportedOperationException;
    
    @MBeanInfo("Subscription is on a Queue")
    boolean isDestinationQueue();
    
    @MBeanInfo("Subscription is on a Topic")
    boolean isDestinationTopic();
    
    @MBeanInfo("Subscription is on a temporary Queue/Topic")
    boolean isDestinationTemporary();
    
    @MBeanInfo("Subscription is active (connected and receiving messages).")
    boolean isActive();
    
    @MBeanInfo("Subscription was created by a demand-forwarding network bridge")
    boolean isNetwork();
    
    @MBeanInfo("Number of messages pending delivery.")
    int getPendingQueueSize();
    
    @MBeanInfo("Number of messages dispatched awaiting acknowledgement.")
    int getDispatchedQueueSize();
    
    @MBeanInfo("Number of messages dispatched awaiting acknowledgement.")
    int getMessageCountAwaitingAcknowledge();
    
    @MBeanInfo("Number of messages that sent to the client.")
    long getDispatchedCounter();
    
    @MBeanInfo("Number of messages that matched the subscription.")
    long getEnqueueCounter();
    
    @MBeanInfo("Number of messages were sent to and acknowledge by the client.")
    long getDequeueCounter();
    
    @MBeanInfo("Number of messages to pre-fetch and dispatch to the client.")
    int getPrefetchSize();
    
    @MBeanInfo("The subscriber is retroactive (tries to receive broadcasted topic messages sent prior to connecting)")
    boolean isRetroactive();
    
    @MBeanInfo("The subscriber is exclusive (no other subscribers may receive messages from the destination as long as this one is)")
    boolean isExclusive();
    
    @MBeanInfo("The subsription is persistent.")
    boolean isDurable();
    
    @MBeanInfo("The subsription ignores local messages.")
    boolean isNoLocal();
    
    @MBeanInfo("The maximum number of pending messages allowed (in addition to the prefetch size).")
    int getMaximumPendingMessageLimit();
    
    @MBeanInfo("The subscription priority")
    byte getPriority();
    
    @Deprecated
    @MBeanInfo("The name of the subscription (durable subscriptions only).")
    String getSubcriptionName();
    
    @MBeanInfo("The name of the subscription (durable subscriptions only).")
    String getSubscriptionName();
    
    @MBeanInfo("Returns true if the subscription (which may be using wildcards) matches the given queue name")
    boolean isMatchingQueue(final String p0);
    
    @MBeanInfo("Returns true if the subscription (which may be using wildcards) matches the given topic name")
    boolean isMatchingTopic(final String p0);
    
    @MBeanInfo("Returns true if the subscription is slow")
    boolean isSlowConsumer();
    
    @MBeanInfo("User Name used to authorize creation of this Subscription")
    String getUserName();
    
    @MBeanInfo("ObjectName of the Connection that created this Subscription")
    ObjectName getConnection();
    
    @MBeanInfo("Resets statistics.")
    void resetStatistics();
    
    @MBeanInfo("Messages consumed")
    long getConsumedCount();
}
