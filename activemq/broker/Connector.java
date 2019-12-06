// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.broker.region.ConnectorStatistics;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.Service;

public interface Connector extends Service
{
    BrokerInfo getBrokerInfo();
    
    ConnectorStatistics getStatistics();
    
    boolean isUpdateClusterClients();
    
    boolean isRebalanceClusterClients();
    
    void updateClientClusterInfo();
    
    boolean isUpdateClusterClientsOnRemove();
    
    int connectionCount();
    
    boolean isAllowLinkStealing();
}
