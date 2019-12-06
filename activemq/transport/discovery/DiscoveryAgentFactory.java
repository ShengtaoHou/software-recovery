// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery;

import org.apache.activemq.util.IOExceptionSupport;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.util.FactoryFinder;

public abstract class DiscoveryAgentFactory
{
    private static final FactoryFinder DISCOVERY_AGENT_FINDER;
    private static final ConcurrentHashMap<String, DiscoveryAgentFactory> DISCOVERY_AGENT_FACTORYS;
    
    private static DiscoveryAgentFactory findDiscoveryAgentFactory(final URI uri) throws IOException {
        final String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IOException("DiscoveryAgent scheme not specified: [" + uri + "]");
        }
        DiscoveryAgentFactory daf = DiscoveryAgentFactory.DISCOVERY_AGENT_FACTORYS.get(scheme);
        if (daf == null) {
            try {
                daf = (DiscoveryAgentFactory)DiscoveryAgentFactory.DISCOVERY_AGENT_FINDER.newInstance(scheme);
                DiscoveryAgentFactory.DISCOVERY_AGENT_FACTORYS.put(scheme, daf);
            }
            catch (Throwable e) {
                throw IOExceptionSupport.create("DiscoveryAgent scheme NOT recognized: [" + scheme + "]", e);
            }
        }
        return daf;
    }
    
    public static DiscoveryAgent createDiscoveryAgent(final URI uri) throws IOException {
        final DiscoveryAgentFactory tf = findDiscoveryAgentFactory(uri);
        return tf.doCreateDiscoveryAgent(uri);
    }
    
    protected abstract DiscoveryAgent doCreateDiscoveryAgent(final URI p0) throws IOException;
    
    static {
        DISCOVERY_AGENT_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/transport/discoveryagent/");
        DISCOVERY_AGENT_FACTORYS = new ConcurrentHashMap<String, DiscoveryAgentFactory>();
    }
}
