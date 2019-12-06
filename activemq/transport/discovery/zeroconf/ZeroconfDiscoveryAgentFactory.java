// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.zeroconf;

import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import java.net.URI;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;

public class ZeroconfDiscoveryAgentFactory extends DiscoveryAgentFactory
{
    @Override
    protected DiscoveryAgent doCreateDiscoveryAgent(final URI uri) throws IOException {
        try {
            final Map options = URISupport.parseParameters(uri);
            final ZeroconfDiscoveryAgent rc = new ZeroconfDiscoveryAgent();
            rc.setGroup(uri.getHost());
            IntrospectionSupport.setProperties(rc, options);
            return rc;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create discovery agent: " + uri, e);
        }
    }
}
