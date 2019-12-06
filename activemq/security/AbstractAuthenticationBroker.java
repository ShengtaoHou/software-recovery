// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.command.ConnectionInfo;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.broker.BrokerFilter;

public class AbstractAuthenticationBroker extends BrokerFilter
{
    protected final CopyOnWriteArrayList<SecurityContext> securityContexts;
    
    public AbstractAuthenticationBroker(final Broker next) {
        super(next);
        this.securityContexts = new CopyOnWriteArrayList<SecurityContext>();
    }
    
    @Override
    public void removeDestination(final ConnectionContext context, final ActiveMQDestination destination, final long timeout) throws Exception {
        this.next.removeDestination(context, destination, timeout);
        for (final SecurityContext sc : this.securityContexts) {
            sc.getAuthorizedReadDests().remove(destination);
            sc.getAuthorizedWriteDests().remove(destination);
        }
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        super.removeConnection(context, info, error);
        if (this.securityContexts.remove(context.getSecurityContext())) {
            context.setSecurityContext(null);
        }
    }
    
    public void refresh() {
        for (final SecurityContext sc : this.securityContexts) {
            sc.getAuthorizedReadDests().clear();
            sc.getAuthorizedWriteDests().clear();
        }
    }
}
