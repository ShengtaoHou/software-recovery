// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;

public class XBeanAuthorizationEntry extends AuthorizationEntry implements InitializingBean
{
    @Override
    public void setAdmin(final String roles) throws Exception {
        this.adminRoles = roles;
    }
    
    @Override
    public void setRead(final String roles) throws Exception {
        this.readRoles = roles;
    }
    
    @Override
    public void setWrite(final String roles) throws Exception {
        this.writeRoles = roles;
    }
    
    @PostConstruct
    private void postConstruct() {
        try {
            this.afterPropertiesSet();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void afterPropertiesSet() throws Exception {
        if (this.adminRoles != null) {
            this.setAdminACLs(this.parseACLs(this.adminRoles));
        }
        if (this.writeRoles != null) {
            this.setWriteACLs(this.parseACLs(this.writeRoles));
        }
        if (this.readRoles != null) {
            this.setReadACLs(this.parseACLs(this.readRoles));
        }
    }
    
    public String toString() {
        return "XBeanAuthEntry:" + this.adminRoles + "," + this.writeRoles + "," + this.readRoles;
    }
}
