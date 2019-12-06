// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;

public interface MessageAuthorizationPolicy
{
    boolean isAllowedToConsume(final ConnectionContext p0, final Message p1);
}
