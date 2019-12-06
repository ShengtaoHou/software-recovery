// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.group;

import java.io.IOException;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.util.FactoryFinder;

public class GroupFactoryFinder
{
    private static final FactoryFinder GROUP_FACTORY_FINDER;
    
    private GroupFactoryFinder() {
    }
    
    public static MessageGroupMapFactory createMessageGroupMapFactory(final String type) throws IOException {
        try {
            Map<String, String> properties = null;
            String factoryType = type.trim();
            final int p = factoryType.indexOf(63);
            if (p >= 0) {
                final String propertiesString = factoryType.substring(p + 1);
                factoryType = factoryType.substring(0, p);
                properties = URISupport.parseQuery(propertiesString);
            }
            final MessageGroupMapFactory result = (MessageGroupMapFactory)GroupFactoryFinder.GROUP_FACTORY_FINDER.newInstance(factoryType);
            if (properties != null && result != null) {
                IntrospectionSupport.setProperties(result, properties);
            }
            return result;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not load " + type + " factory:" + e, e);
        }
    }
    
    static {
        GROUP_FACTORY_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/groups/");
    }
}
