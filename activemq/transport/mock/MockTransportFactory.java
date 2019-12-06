// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mock;

import java.io.IOException;
import org.apache.activemq.transport.TransportServer;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import java.net.URISyntaxException;
import org.apache.activemq.transport.ResponseCorrelator;
import org.apache.activemq.transport.MutexTransport;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.Transport;
import java.net.URI;
import org.apache.activemq.transport.TransportFactory;

public class MockTransportFactory extends TransportFactory
{
    @Override
    public Transport doConnect(final URI location) throws URISyntaxException, Exception {
        Transport transport = this.createTransport(URISupport.parseComposite(location));
        transport = new MutexTransport(transport);
        transport = new ResponseCorrelator(transport);
        return transport;
    }
    
    @Override
    public Transport doCompositeConnect(final URI location) throws URISyntaxException, Exception {
        return this.createTransport(URISupport.parseComposite(location));
    }
    
    public Transport createTransport(final URISupport.CompositeData compositData) throws Exception {
        final MockTransport transport = new MockTransport(TransportFactory.compositeConnect(compositData.getComponents()[0]));
        IntrospectionSupport.setProperties(transport, compositData.getParameters());
        return transport;
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        throw new IOException("This protocol does not support being bound.");
    }
}
