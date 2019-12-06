// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.virtual;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.DestinationInterceptor;

public interface VirtualDestination extends DestinationInterceptor
{
    ActiveMQDestination getVirtualDestination();
    
    Destination intercept(final Destination p0);
}
