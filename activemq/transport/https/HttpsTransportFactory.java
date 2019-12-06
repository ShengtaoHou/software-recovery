// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.https;

import java.net.MalformedURLException;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.broker.SslContext;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import java.io.IOException;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.apache.activemq.transport.http.HttpTransportFactory;

public class HttpsTransportFactory extends HttpTransportFactory
{
    public TransportServer doBind(final String brokerId, final URI location) throws IOException {
        return this.doBind(location);
    }
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final HttpsTransportServer result = new HttpsTransportServer(location, this, SslContext.getCurrentSslContext());
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "transport.");
            result.setTransportOption(transportOptions);
            return result;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws MalformedURLException {
        URI uri;
        try {
            uri = URISupport.removeQuery(location);
        }
        catch (URISyntaxException e) {
            final MalformedURLException cause = new MalformedURLException("Error removing query on " + location);
            cause.initCause(e);
            throw cause;
        }
        return new HttpsClientTransport(this.asTextWireFormat(wf), uri);
    }
}
