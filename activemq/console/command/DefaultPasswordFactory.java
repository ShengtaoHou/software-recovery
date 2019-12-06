// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command;

public class DefaultPasswordFactory implements PasswordFactory
{
    public static PasswordFactory factory;
    
    @Override
    public String getPassword(final String password) {
        return password;
    }
    
    static {
        DefaultPasswordFactory.factory = new DefaultPasswordFactory();
    }
}
