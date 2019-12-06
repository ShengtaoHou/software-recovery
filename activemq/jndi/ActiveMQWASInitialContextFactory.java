// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jndi;

import java.util.Iterator;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;

public class ActiveMQWASInitialContextFactory extends ActiveMQInitialContextFactory
{
    @Override
    public Context getInitialContext(final Hashtable environment) throws NamingException {
        return super.getInitialContext(this.transformEnvironment(environment));
    }
    
    protected Hashtable transformEnvironment(final Hashtable environment) {
        final Hashtable environment2 = new Hashtable();
        for (final Map.Entry entry : environment.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                final String key = entry.getKey();
                String value = entry.getValue();
                if (key.startsWith("java.naming.queue.")) {
                    String key2 = key.substring("java.naming.queue.".length());
                    key2 = key2.replace('.', '/');
                    environment2.put("queue." + key2, value);
                }
                else if (key.startsWith("java.naming.topic.")) {
                    String key2 = key.substring("java.naming.topic.".length());
                    key2 = key2.replace('.', '/');
                    environment2.put("topic." + key2, value);
                }
                else if (key.startsWith("java.naming.connectionFactoryNames")) {
                    final String key2 = key.substring("java.naming.".length());
                    environment2.put(key2, value);
                }
                else if (key.startsWith("java.naming.connection")) {
                    final String key2 = key.substring("java.naming.".length());
                    environment2.put(key2, value);
                }
                else if (key.startsWith("java.naming.provider.url")) {
                    value = value.replace(';', ',');
                    environment2.put("java.naming.provider.url", value);
                }
                else {
                    environment2.put(key, value);
                }
            }
        }
        return environment2;
    }
}
