// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.naming.NamingException;
import org.springframework.jndi.JndiTemplate;

public class JndiTemplateLookupFactory extends JndiLookupFactory
{
    private final JndiTemplate template;
    
    public JndiTemplateLookupFactory(final JndiTemplate template) {
        this.template = template;
    }
    
    @Override
    public <T> T lookup(final String name, final Class<T> clazz) throws NamingException {
        return (T)this.template.lookup(name, (Class)clazz);
    }
}
