// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.security;

public interface SecurityAdminMBean
{
    public static final String OPERATION_READ = "read";
    public static final String OPERATION_WRITE = "write";
    public static final String OPERATION_ADMIN = "admin";
    
    void addRole(final String p0);
    
    void removeRole(final String p0);
    
    void addUserRole(final String p0, final String p1);
    
    void removeUserRole(final String p0, final String p1);
    
    void addTopicRole(final String p0, final String p1, final String p2);
    
    void removeTopicRole(final String p0, final String p1, final String p2);
    
    void addQueueRole(final String p0, final String p1, final String p2);
    
    void removeQueueRole(final String p0, final String p1, final String p2);
}
