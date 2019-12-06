// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery;

import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.apache.activemq.util.IntrospectionSupport;
import java.io.IOException;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.apache.activemq.transport.CompositeTransport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.failover.FailoverTransportFactory;

public class DiscoveryTransportFactory extends FailoverTransportFactory
{
    @Override
    public Transport createTransport(final URISupport.CompositeData compositeData) throws IOException {
        final Map<String, String> parameters = new HashMap<String, String>(compositeData.getParameters());
        final FailoverTransport failoverTransport = this.createTransport(parameters);
        return createTransport(failoverTransport, compositeData, parameters);
    }
    
    public static DiscoveryTransport createTransport(final CompositeTransport compositeTransport, final URISupport.CompositeData compositeData, final Map<String, String> parameters) throws IOException {
        final DiscoveryTransport transport = new DiscoveryTransport(compositeTransport);
        IntrospectionSupport.setProperties(transport, parameters);
        transport.setParameters(parameters);
        final URI discoveryAgentURI = compositeData.getComponents()[0];
        final DiscoveryAgent discoveryAgent = DiscoveryAgentFactory.createDiscoveryAgent(discoveryAgentURI);
        transport.setDiscoveryAgent(discoveryAgent);
        return transport;
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        throw new IOException("Invalid server URI: " + location);
    }
}
