// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import javax.naming.NamingException;
import javax.naming.NameNotFoundException;

public abstract class LazyCreateContext extends ReadOnlyContext
{
    @Override
    public Object lookup(final String name) throws NamingException {
        try {
            return super.lookup(name);
        }
        catch (NameNotFoundException e) {
            final Object answer = this.createEntry(name);
            if (answer == null) {
                throw e;
            }
            this.internalBind(name, answer);
            return answer;
        }
    }
    
    protected abstract Object createEntry(final String p0);
}
