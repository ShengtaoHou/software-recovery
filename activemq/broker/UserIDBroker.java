// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.security.SecurityContext;
import org.apache.activemq.jaas.UserPrincipal;
import org.apache.activemq.command.Message;

public class UserIDBroker extends BrokerFilter
{
    boolean useAuthenticatePrincipal;
    
    public UserIDBroker(final Broker next) {
        super(next);
        this.useAuthenticatePrincipal = false;
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        final ConnectionContext context = producerExchange.getConnectionContext();
        String userID = context.getUserName();
        if (this.isUseAuthenticatePrincipal()) {
            final SecurityContext securityContext = context.getSecurityContext();
            if (securityContext != null) {
                final Set<?> principals = securityContext.getPrincipals();
                if (principals != null) {
                    for (final Object candidate : principals) {
                        if (candidate instanceof UserPrincipal) {
                            userID = ((UserPrincipal)candidate).getName();
                            break;
                        }
                    }
                }
            }
        }
        messageSend.setUserID(userID);
        super.send(producerExchange, messageSend);
    }
    
    public boolean isUseAuthenticatePrincipal() {
        return this.useAuthenticatePrincipal;
    }
    
    public void setUseAuthenticatePrincipal(final boolean useAuthenticatePrincipal) {
        this.useAuthenticatePrincipal = useAuthenticatePrincipal;
    }
}
