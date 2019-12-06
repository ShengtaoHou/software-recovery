// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.Collections;
import java.security.Principal;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SecurityContext
{
    public static final SecurityContext BROKER_SECURITY_CONTEXT;
    final String userName;
    final ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination> authorizedReadDests;
    final ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination> authorizedWriteDests;
    
    public SecurityContext(final String userName) {
        this.authorizedReadDests = new ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination>();
        this.authorizedWriteDests = new ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination>();
        this.userName = userName;
    }
    
    public boolean isInOneOf(final Set<?> allowedPrincipals) {
        final Iterator<?> allowedIter = allowedPrincipals.iterator();
        final HashSet<?> userPrincipals = new HashSet<Object>(this.getPrincipals());
        while (allowedIter.hasNext()) {
            final Iterator<?> userIter = userPrincipals.iterator();
            final Object allowedPrincipal = allowedIter.next();
            while (userIter.hasNext()) {
                if (allowedPrincipal.equals(userIter.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public abstract Set<Principal> getPrincipals();
    
    public String getUserName() {
        return this.userName;
    }
    
    public ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination> getAuthorizedReadDests() {
        return this.authorizedReadDests;
    }
    
    public ConcurrentHashMap<ActiveMQDestination, ActiveMQDestination> getAuthorizedWriteDests() {
        return this.authorizedWriteDests;
    }
    
    public boolean isBrokerContext() {
        return false;
    }
    
    static {
        BROKER_SECURITY_CONTEXT = new SecurityContext("ActiveMQBroker") {
            @Override
            public boolean isBrokerContext() {
                return true;
            }
            
            @Override
            public Set<Principal> getPrincipals() {
                return Collections.emptySet();
            }
        };
    }
}
