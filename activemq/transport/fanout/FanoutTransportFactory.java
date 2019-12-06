// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.fanout;

import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.transport.discovery.DiscoveryTransport;
import java.util.Map;
import org.apache.activemq.transport.CompositeTransport;
import org.apache.activemq.transport.discovery.DiscoveryTransportFactory;
import org.apache.activemq.util.URISupport;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.ResponseCorrelator;
import org.apache.activemq.transport.MutexTransport;
import org.apache.activemq.transport.Transport;
import java.net.URI;
import org.apache.activemq.transport.TransportFactory;

public class FanoutTransportFactory extends TransportFactory
{
    @Override
    public Transport doConnect(final URI location) throws IOException {
        try {
            Transport transport = this.createTransport(location);
            transport = new MutexTransport(transport);
            transport = new ResponseCorrelator(transport);
            return transport;
        }
        catch (URISyntaxException e) {
            throw new IOException("Invalid location: " + location);
        }
    }
    
    @Override
    public Transport doCompositeConnect(final URI location) throws IOException {
        try {
            return this.createTransport(location);
        }
        catch (URISyntaxException e) {
            throw new IOException("Invalid location: " + location);
        }
    }
    
    public Transport createTransport(final URI location) throws IOException, URISyntaxException {
        final URISupport.CompositeData compositeData = URISupport.parseComposite(location);
        final Map<String, String> parameters = compositeData.getParameters();
        final FanoutTransport fanoutTransport = this.createTransport(parameters);
        final DiscoveryTransport discoveryTransport = DiscoveryTransportFactory.createTransport(fanoutTransport, compositeData, parameters);
        return discoveryTransport;
    }
    
    public FanoutTransport createTransport(final Map<String, String> parameters) throws IOException {
        final FanoutTransport transport = new FanoutTransport();
        IntrospectionSupport.setProperties(transport, parameters);
        return transport;
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        throw new IOException("Invalid server URI: " + location);
    }
}
