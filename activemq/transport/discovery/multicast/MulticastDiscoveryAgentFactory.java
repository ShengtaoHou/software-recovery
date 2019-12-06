// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.multicast;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Map;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.discovery.DiscoveryAgent;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.transport.discovery.DiscoveryAgentFactory;

public class MulticastDiscoveryAgentFactory extends DiscoveryAgentFactory
{
    private static final Logger LOG;
    
    @Override
    protected DiscoveryAgent doCreateDiscoveryAgent(final URI uri) throws IOException {
        try {
            if (MulticastDiscoveryAgentFactory.LOG.isTraceEnabled()) {
                MulticastDiscoveryAgentFactory.LOG.trace("doCreateDiscoveryAgent: uri = " + uri.toString());
            }
            final MulticastDiscoveryAgent mda = new MulticastDiscoveryAgent();
            mda.setDiscoveryURI(uri);
            final Map options = URISupport.parseParameters(uri);
            IntrospectionSupport.setProperties(mda, options);
            return mda;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create discovery agent: " + uri, e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MulticastDiscoveryAgentFactory.class);
    }
}
