// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

public interface NetworkDestinationViewMBean
{
    @MBeanInfo("Name of this destination.")
    String getName();
    
    @MBeanInfo("Resets statistics.")
    void resetStats();
    
    @MBeanInfo("Number of messages that have been sent to the destination.")
    long getCount();
    
    @MBeanInfo("rate of messages sent across the network destination.")
    double getRate();
}
