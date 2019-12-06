// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.Iterator;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.apache.activemq.jaas.UserPrincipal;
import java.security.Principal;
import javax.security.auth.login.LoginContext;
import org.apache.activemq.jaas.JaasCertificateCallbackHandler;
import java.security.cert.X509Certificate;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;

public class JaasCertificateAuthenticationBroker extends BrokerFilter
{
    private final String jaasConfiguration;
    
    public JaasCertificateAuthenticationBroker(final Broker next, final String jaasConfiguration) {
        super(next);
        this.jaasConfiguration = jaasConfiguration;
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        if (context.getSecurityContext() == null) {
            if (!(info.getTransportContext() instanceof X509Certificate[])) {
                throw new SecurityException("Unable to authenticate transport without SSL certificate.");
            }
            final ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(JaasAuthenticationBroker.class.getClassLoader());
            try {
                final CallbackHandler callback = (CallbackHandler)new JaasCertificateCallbackHandler((X509Certificate[])info.getTransportContext());
                final LoginContext lc = new LoginContext(this.jaasConfiguration, callback);
                lc.login();
                final Subject subject = lc.getSubject();
                String dnName = "";
                for (final Principal principal : subject.getPrincipals()) {
                    if (principal instanceof UserPrincipal) {
                        dnName = ((UserPrincipal)principal).getName();
                        break;
                    }
                }
                final SecurityContext s = new JaasCertificateSecurityContext(dnName, subject, (X509Certificate[])info.getTransportContext());
                context.setSecurityContext(s);
            }
            catch (Exception e) {
                throw new SecurityException("User name [" + info.getUserName() + "] or password is invalid. " + e.getMessage(), e);
            }
            finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
        super.addConnection(context, info);
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        super.removeConnection(context, info, error);
        context.setSecurityContext(null);
    }
}
