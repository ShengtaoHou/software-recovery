// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import javax.management.ObjectName;
import org.apache.activemq.Service;

public interface NetworkBridge extends Service
{
    void serviceRemoteException(final Throwable p0);
    
    void serviceLocalException(final Throwable p0);
    
    void setNetworkBridgeListener(final NetworkBridgeListener p0);
    
    String getRemoteAddress();
    
    String getRemoteBrokerName();
    
    String getRemoteBrokerId();
    
    String getLocalAddress();
    
    String getLocalBrokerName();
    
    long getEnqueueCounter();
    
    long getDequeueCounter();
    
    void setMbeanObjectName(final ObjectName p0);
    
    ObjectName getMbeanObjectName();
    
    void resetStats();
}
