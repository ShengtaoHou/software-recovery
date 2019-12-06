// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import org.apache.activemq.jaas.GroupPrincipal;
import java.util.HashSet;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Broker;
import java.security.Principal;
import java.util.Set;
import java.util.Map;

public class SimpleAuthenticationBroker extends AbstractAuthenticationBroker
{
    private boolean anonymousAccessAllowed;
    private String anonymousUser;
    private String anonymousGroup;
    private Map<String, String> userPasswords;
    private Map<String, Set<Principal>> userGroups;
    
    public SimpleAuthenticationBroker(final Broker next, final Map<String, String> userPasswords, final Map<String, Set<Principal>> userGroups) {
        super(next);
        this.anonymousAccessAllowed = false;
        this.userPasswords = userPasswords;
        this.userGroups = userGroups;
    }
    
    public void setAnonymousAccessAllowed(final boolean anonymousAccessAllowed) {
        this.anonymousAccessAllowed = anonymousAccessAllowed;
    }
    
    public void setAnonymousUser(final String anonymousUser) {
        this.anonymousUser = anonymousUser;
    }
    
    public void setAnonymousGroup(final String anonymousGroup) {
        this.anonymousGroup = anonymousGroup;
    }
    
    public void setUserPasswords(final Map<String, String> value) {
        this.userPasswords = value;
    }
    
    public void setUserGroups(final Map<String, Set<Principal>> value) {
        this.userGroups = value;
    }
    
    @Override
    public void addConnection(final ConnectionContext context, final ConnectionInfo info) throws Exception {
        SecurityContext s = context.getSecurityContext();
        if (s == null) {
            if (this.anonymousAccessAllowed && info.getUserName() == null && info.getPassword() == null) {
                info.setUserName(this.anonymousUser);
                s = new SecurityContext(info.getUserName()) {
                    @Override
                    public Set<Principal> getPrincipals() {
                        final Set<Principal> groups = new HashSet<Principal>();
                        groups.add((Principal)new GroupPrincipal(SimpleAuthenticationBroker.this.anonymousGroup));
                        return groups;
                    }
                };
            }
            else {
                final String pw = this.userPasswords.get(info.getUserName());
                if (pw == null || !pw.equals(info.getPassword())) {
                    throw new SecurityException("User name [" + info.getUserName() + "] or password is invalid.");
                }
                final Set<Principal> groups = this.userGroups.get(info.getUserName());
                s = new SecurityContext(info.getUserName()) {
                    @Override
                    public Set<Principal> getPrincipals() {
                        return groups;
                    }
                };
            }
            context.setSecurityContext(s);
            this.securityContexts.add(s);
        }
        try {
            super.addConnection(context, info);
        }
        catch (Exception e) {
            this.securityContexts.remove(s);
            context.setSecurityContext(null);
            throw e;
        }
    }
}
