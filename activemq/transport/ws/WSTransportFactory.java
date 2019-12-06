// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.ws;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.apache.activemq.transport.TransportFactory;

public class WSTransportFactory extends TransportFactory
{
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final WSTransportServer result = new WSTransportServer(location);
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "");
            result.setTransportOption(transportOptions);
            return result;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
}
