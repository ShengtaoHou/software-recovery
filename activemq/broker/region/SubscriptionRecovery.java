// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;

public interface SubscriptionRecovery
{
    boolean addRecoveredMessage(final ConnectionContext p0, final MessageReference p1) throws Exception;
    
    ActiveMQDestination getActiveMQDestination();
}
