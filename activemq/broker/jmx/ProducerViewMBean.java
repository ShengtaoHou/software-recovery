// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

public interface ProducerViewMBean
{
    @MBeanInfo("JMS Client id of the Connection the Producer is on.")
    String getClientId();
    
    @MBeanInfo("ID of the Connection the Producer is on.")
    String getConnectionId();
    
    @MBeanInfo("ID of the Session the Producer is on.")
    long getSessionId();
    
    @MBeanInfo("ID of the Producer.")
    String getProducerId();
    
    @MBeanInfo("The name of the destionation the Producer is on.")
    String getDestinationName();
    
    @MBeanInfo("Producer is on a Queue")
    boolean isDestinationQueue();
    
    @MBeanInfo("Producer is on a Topic")
    boolean isDestinationTopic();
    
    @MBeanInfo("Producer is on a temporary Queue/Topic")
    boolean isDestinationTemporary();
    
    @MBeanInfo("Configured Window Size for the Producer")
    int getProducerWindowSize();
    
    @MBeanInfo("Is the producer configured for Async Dispatch")
    boolean isDispatchAsync();
    
    @MBeanInfo("User Name used to authorize creation of this Producer")
    String getUserName();
    
    @MBeanInfo("is the producer blocked for Flow Control")
    boolean isProducerBlocked();
    
    @MBeanInfo("total time (ms) Producer Blocked For Flow Control")
    long getTotalTimeBlocked();
    
    @MBeanInfo("percentage of sends Producer Blocked for Flow Control")
    int getPercentageBlocked();
    
    @MBeanInfo("reset flow control state")
    void resetFlowControlStats();
    
    @MBeanInfo("Resets statistics.")
    void resetStatistics();
    
    @MBeanInfo("Messages dispatched by Producer")
    long getSentCount();
}
