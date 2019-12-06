// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.failover;

import org.apache.activemq.transport.TransportServer;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.ResponseCorrelator;
import org.apache.activemq.transport.MutexTransport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.Transport;
import java.net.URI;
import org.apache.activemq.transport.TransportFactory;

public class FailoverTransportFactory extends TransportFactory
{
    @Override
    public Transport doConnect(final URI location) throws IOException {
        try {
            Transport transport = this.createTransport(URISupport.parseComposite(location));
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
            return this.createTransport(URISupport.parseComposite(location));
        }
        catch (URISyntaxException e) {
            throw new IOException("Invalid location: " + location);
        }
    }
    
    public Transport createTransport(final URISupport.CompositeData compositData) throws IOException {
        final Map<String, String> options = compositData.getParameters();
        final FailoverTransport transport = this.createTransport(options);
        if (!options.isEmpty()) {
            throw new IllegalArgumentException("Invalid connect parameters: " + options);
        }
        transport.add(false, compositData.getComponents());
        return transport;
    }
    
    public FailoverTransport createTransport(final Map<String, String> parameters) throws IOException {
        final FailoverTransport transport = new FailoverTransport();
        final Map<String, Object> nestedExtraQueryOptions = IntrospectionSupport.extractProperties(parameters, "nested.");
        IntrospectionSupport.setProperties(transport, parameters);
        try {
            transport.setNestedExtraQueryOptions(URISupport.createQueryString(nestedExtraQueryOptions));
        }
        catch (URISyntaxException ex) {}
        return transport;
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        throw new IOException("Invalid server URI: " + location);
    }
}
