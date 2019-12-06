// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.security.Principal;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import org.apache.activemq.jaas.JassCredentialCallbackHandler;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;

public class JaasAuthenticationBroker extends AbstractAuthenticationBroker
{
    private final String jassConfiguration;
    
    public JaasAuthenticationBroker(final Broker next, final String jassConfiguration) {
        super(next);
        this.jassConfiguration = jassConfiguration;
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        if (context.getSecurityContext() == null) {
            final ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(JaasAuthenticationBroker.class.getClassLoader());
            try {
                final JassCredentialCallbackHandler callback = new JassCredentialCallbackHandler(info.getUserName(), info.getPassword());
                final LoginContext lc = new LoginContext(this.jassConfiguration, (CallbackHandler)callback);
                lc.login();
                final Subject subject = lc.getSubject();
                final SecurityContext s = new JaasSecurityContext(info.getUserName(), subject);
                context.setSecurityContext(s);
                this.securityContexts.add(s);
            }
            catch (Exception e) {
                throw (SecurityException)new SecurityException("User name [" + info.getUserName() + "] or password is invalid.").initCause(e);
            }
            finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
        super.addConnection(context, info);
    }
    
    static class JaasSecurityContext extends SecurityContext
    {
        private final Subject subject;
        
        public JaasSecurityContext(final String userName, final Subject subject) {
            super(userName);
            this.subject = subject;
        }
        
        @Override
        public Set<Principal> getPrincipals() {
            return this.subject.getPrincipals();
        }
    }
}
