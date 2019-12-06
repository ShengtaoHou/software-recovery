// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.wss;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.broker.SslContext;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.apache.activemq.transport.TransportFactory;

public class WSSTransportFactory extends TransportFactory
{
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final WSSTransportServer result = new WSSTransportServer(location, SslContext.getCurrentSslContext());
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "");
            result.setTransportOption(transportOptions);
            return result;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
}
