// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.Service;

public interface NetworkBridgeViewMBean extends Service
{
    String getLocalAddress();
    
    String getRemoteAddress();
    
    String getRemoteBrokerName();
    
    String getRemoteBrokerId();
    
    String getLocalBrokerName();
    
    long getEnqueueCounter();
    
    long getDequeueCounter();
    
    boolean isCreatedByDuplex();
    
    void resetStats();
}
