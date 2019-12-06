// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

public interface AbortSlowAckConsumerStrategyViewMBean extends AbortSlowConsumerStrategyViewMBean
{
    @MBeanInfo("returns the current max time since last ack setting")
    long getMaxTimeSinceLastAck();
    
    @MBeanInfo("sets the duration (milliseconds) after which a consumer that doesn't ack a message will be marked as slow")
    void setMaxTimeSinceLastAck(final long p0);
    
    @MBeanInfo("returns the current value of the ignore idle consumers setting.")
    boolean isIgnoreIdleConsumers();
    
    @MBeanInfo("sets whether consumers that are idle (no dispatched messages) should be included when checking for slow acks.")
    void setIgnoreIdleConsumers(final boolean p0);
    
    @MBeanInfo("returns the current value of the ignore network connector consumers setting.")
    boolean isIgnoreNetworkConsumers();
    
    @MBeanInfo("sets whether consumers that are from network connector should be included when checking for slow acks.")
    void setIgnoreNetworkConsumers(final boolean p0);
}
