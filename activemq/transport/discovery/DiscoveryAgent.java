// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery;

import org.apache.activemq.command.DiscoveryEvent;
import java.io.IOException;
import org.apache.activemq.Service;

public interface DiscoveryAgent extends Service
{
    void setDiscoveryListener(final DiscoveryListener p0);
    
    void registerService(final String p0) throws IOException;
    
    void serviceFailed(final DiscoveryEvent p0) throws IOException;
}
