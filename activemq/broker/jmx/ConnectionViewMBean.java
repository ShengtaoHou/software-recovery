// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import javax.management.ObjectName;
import org.apache.activemq.Service;

public interface ConnectionViewMBean extends Service
{
    @MBeanInfo("Connection is slow.")
    boolean isSlow();
    
    @MBeanInfo("Connection is blocked.")
    boolean isBlocked();
    
    @MBeanInfo("Connection is connected to the broker.")
    boolean isConnected();
    
    @MBeanInfo("Connection is active (both connected and receiving messages).")
    boolean isActive();
    
    @MBeanInfo("Resets the statistics")
    void resetStatistics();
    
    @MBeanInfo("source address for this connection")
    String getRemoteAddress();
    
    @MBeanInfo("client id for this connection")
    String getClientId();
    
    @MBeanInfo("The number of messages pending dispatch")
    int getDispatchQueueSize();
    
    @MBeanInfo("User Name used to authorize creation of this connection")
    String getUserName();
    
    @MBeanInfo("The ObjectNames of all Consumers created by this Connection")
    ObjectName[] getConsumers();
    
    @MBeanInfo("The ObjectNames of all Producers created by this Connection")
    ObjectName[] getProducers();
    
    @MBeanInfo("The number of active transactions established on this Connection.")
    int getActiveTransactionCount();
    
    @MBeanInfo("The age in ms of the oldest active transaction established on this Connection.")
    Long getOldestActiveTransactionDuration();
}
