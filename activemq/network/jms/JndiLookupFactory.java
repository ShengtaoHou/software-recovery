// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.naming.NamingException;
import javax.naming.InitialContext;

public class JndiLookupFactory
{
    public <T> T lookup(final String name, final Class<T> clazz) throws NamingException {
        final InitialContext ctx = new InitialContext();
        try {
            return clazz.cast(ctx.lookup(name));
        }
        finally {
            ctx.close();
        }
    }
}
