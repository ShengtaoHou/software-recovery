// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.Set;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.DestinationFilter;

public class AuthorizationDestinationFilter extends DestinationFilter
{
    private final AuthorizationBroker broker;
    
    public AuthorizationDestinationFilter(final Destination destination, final AuthorizationBroker broker) {
        super(destination);
        this.broker = broker;
    }
    
    @Override
    public void addSubscription(final ConnectionContext context, final Subscription sub) throws Exception {
        final SecurityContext securityContext = this.broker.checkSecurityContext(context);
        final AuthorizationMap authorizationMap = this.broker.getAuthorizationMap();
        final ActiveMQDestination destination = this.next.getActiveMQDestination();
        Set<?> allowedACLs;
        if (!destination.isTemporary()) {
            allowedACLs = authorizationMap.getReadACLs(destination);
        }
        else {
            allowedACLs = authorizationMap.getTempDestinationReadACLs();
        }
        if (!securityContext.isBrokerContext() && allowedACLs != null && !securityContext.isInOneOf(allowedACLs)) {
            throw new SecurityException("User " + securityContext.getUserName() + " is not authorized to read from: " + destination);
        }
        securityContext.getAuthorizedReadDests().put(destination, destination);
        super.addSubscription(context, sub);
    }
}
