// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collections;
import java.util.Set;
import org.apache.activemq.filter.DestinationMapEntry;

public class AuthorizationEntry extends DestinationMapEntry
{
    private Set<Object> readACLs;
    private Set<Object> writeACLs;
    private Set<Object> adminACLs;
    protected String adminRoles;
    protected String readRoles;
    protected String writeRoles;
    private String groupClass;
    
    public AuthorizationEntry() {
        this.readACLs = this.emptySet();
        this.writeACLs = this.emptySet();
        this.adminACLs = this.emptySet();
    }
    
    public String getGroupClass() {
        return this.groupClass;
    }
    
    private Set<Object> emptySet() {
        return (Set<Object>)Collections.EMPTY_SET;
    }
    
    public void setGroupClass(final String groupClass) {
        this.groupClass = groupClass;
    }
    
    public Set<Object> getAdminACLs() {
        return this.adminACLs;
    }
    
    public void setAdminACLs(final Set<Object> adminACLs) {
        this.adminACLs = adminACLs;
    }
    
    public Set<Object> getReadACLs() {
        return this.readACLs;
    }
    
    public void setReadACLs(final Set<Object> readACLs) {
        this.readACLs = readACLs;
    }
    
    public Set<Object> getWriteACLs() {
        return this.writeACLs;
    }
    
    public void setWriteACLs(final Set<Object> writeACLs) {
        this.writeACLs = writeACLs;
    }
    
    public void setAdmin(final String roles) throws Exception {
        this.adminRoles = roles;
        this.setAdminACLs(this.parseACLs(this.adminRoles));
    }
    
    public void setRead(final String roles) throws Exception {
        this.readRoles = roles;
        this.setReadACLs(this.parseACLs(this.readRoles));
    }
    
    public void setWrite(final String roles) throws Exception {
        this.writeRoles = roles;
        this.setWriteACLs(this.parseACLs(this.writeRoles));
    }
    
    protected Set<Object> parseACLs(final String roles) throws Exception {
        final Set<Object> answer = new HashSet<Object>();
        final StringTokenizer iter = new StringTokenizer(roles, ",");
        while (iter.hasMoreTokens()) {
            final String name = iter.nextToken().trim();
            final String groupClass = (this.groupClass != null) ? this.groupClass : "org.apache.activemq.jaas.GroupPrincipal";
            answer.add(DefaultAuthorizationMap.createGroupPrincipal(name, groupClass));
        }
        return answer;
    }
}
