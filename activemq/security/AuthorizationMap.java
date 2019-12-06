// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;

public interface AuthorizationMap
{
    Set<?> getTempDestinationAdminACLs();
    
    Set<?> getTempDestinationReadACLs();
    
    Set<?> getTempDestinationWriteACLs();
    
    Set<?> getAdminACLs(final ActiveMQDestination p0);
    
    Set<?> getReadACLs(final ActiveMQDestination p0);
    
    Set<?> getWriteACLs(final ActiveMQDestination p0);
}
