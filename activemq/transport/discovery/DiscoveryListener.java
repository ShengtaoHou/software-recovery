// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery;

import org.apache.activemq.command.DiscoveryEvent;

public interface DiscoveryListener
{
    public static final String DISCOVERED_OPTION_PREFIX = "discovered.";
    
    void onServiceAdd(final DiscoveryEvent p0);
    
    void onServiceRemove(final DiscoveryEvent p0);
}
