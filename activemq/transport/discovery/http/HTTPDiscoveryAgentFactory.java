// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.http;

import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import java.net.URI;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;

public class HTTPDiscoveryAgentFactory extends DiscoveryAgentFactory
{
    @Override
    protected DiscoveryAgent doCreateDiscoveryAgent(URI uri) throws IOException {
        try {
            final Map options = URISupport.parseParameters(uri);
            uri = URISupport.removeQuery(uri);
            final HTTPDiscoveryAgent rc = new HTTPDiscoveryAgent();
            rc.setRegistryURL(uri.toString());
            IntrospectionSupport.setProperties(rc, options);
            return rc;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create discovery agent: " + uri, e);
        }
    }
}
