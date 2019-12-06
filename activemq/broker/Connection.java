// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.command.ConnectionControl;
import java.io.IOException;
import org.apache.activemq.broker.region.ConnectionStatistics;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.Command;
import org.apache.activemq.Service;

public interface Connection extends Service
{
    Connector getConnector();
    
    void dispatchSync(final Command p0);
    
    void dispatchAsync(final Command p0);
    
    Response service(final Command p0);
    
    void serviceException(final Throwable p0);
    
    boolean isSlow();
    
    boolean isBlocked();
    
    boolean isConnected();
    
    boolean isActive();
    
    int getDispatchQueueSize();
    
    ConnectionStatistics getStatistics();
    
    boolean isManageable();
    
    String getRemoteAddress();
    
    void serviceExceptionAsync(final IOException p0);
    
    String getConnectionId();
    
    boolean isNetworkConnection();
    
    boolean isFaultTolerantConnection();
    
    void updateClient(final ConnectionControl p0);
    
    int getActiveTransactionCount();
    
    Long getOldestActiveTransactionDuration();
}
