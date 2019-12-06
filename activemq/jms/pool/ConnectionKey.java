// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

public class ConnectionKey
{
    private final String userName;
    private final String password;
    private int hash;
    
    public ConnectionKey(final String userName, final String password) {
        this.password = password;
        this.userName = userName;
        this.hash = 31;
        if (userName != null) {
            this.hash += userName.hashCode();
        }
        this.hash *= 31;
        if (password != null) {
            this.hash += password.hashCode();
        }
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that instanceof ConnectionKey && this.equals((ConnectionKey)that));
    }
    
    public boolean equals(final ConnectionKey that) {
        return isEqual(this.userName, that.userName) && isEqual(this.password, that.password);
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public static boolean isEqual(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
}
