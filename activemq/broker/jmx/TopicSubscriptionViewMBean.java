// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

public interface TopicSubscriptionViewMBean extends SubscriptionViewMBean
{
    @MBeanInfo("Number of messages discared due to being a slow consumer")
    int getDiscardedCount();
    
    @MBeanInfo("Maximum number of messages that can be pending")
    int getMaximumPendingQueueSize();
    
    void setMaximumPendingQueueSize(final int p0);
}
