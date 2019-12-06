// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.net.InetSocketAddress;
import java.net.URI;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.Service;

public interface TransportServer extends Service
{
    void setAcceptListener(final TransportAcceptListener p0);
    
    void setBrokerInfo(final BrokerInfo p0);
    
    URI getConnectURI();
    
    InetSocketAddress getSocketAddress();
    
    boolean isSslServer();
    
    boolean isAllowLinkStealing();
}
