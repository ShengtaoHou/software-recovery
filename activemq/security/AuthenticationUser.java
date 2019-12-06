// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

public class AuthenticationUser
{
    String username;
    String password;
    String groups;
    
    public AuthenticationUser() {
    }
    
    public AuthenticationUser(final String username, final String password, final String groups) {
        this.username = username;
        this.password = password;
        this.groups = groups;
    }
    
    public String getGroups() {
        return this.groups;
    }
    
    public void setGroups(final String groups) {
        this.groups = groups;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
}
