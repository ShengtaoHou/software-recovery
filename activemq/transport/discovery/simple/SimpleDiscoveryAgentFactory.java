// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.simple;

import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import java.net.URI;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;

public class SimpleDiscoveryAgentFactory extends DiscoveryAgentFactory
{
    @Override
    protected DiscoveryAgent doCreateDiscoveryAgent(final URI uri) throws IOException {
        try {
            final URISupport.CompositeData data = URISupport.parseComposite(uri);
            final Map options = data.getParameters();
            final SimpleDiscoveryAgent rc = new SimpleDiscoveryAgent();
            IntrospectionSupport.setProperties(rc, options);
            rc.setServices(data.getComponents());
            return rc;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create discovery agent: " + uri, e);
        }
    }
}
