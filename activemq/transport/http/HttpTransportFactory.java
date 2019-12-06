// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.InactivityMonitor;
import org.apache.activemq.transport.TransportLoggerFactory;
import java.net.MalformedURLException;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.xstream.XStreamWireFormat;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.wireformat.WireFormat;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFactory;

public class HttpTransportFactory extends TransportFactory
{
    private static final Logger LOG;
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final HttpTransportServer result = new HttpTransportServer(location, this);
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "transport.");
            result.setTransportOption(transportOptions);
            return result;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    protected TextWireFormat asTextWireFormat(final WireFormat wireFormat) {
        if (wireFormat instanceof TextWireFormat) {
            return (TextWireFormat)wireFormat;
        }
        HttpTransportFactory.LOG.trace("Not created with a TextWireFormat: " + wireFormat);
        return new XStreamWireFormat();
    }
    
    @Override
    protected String getDefaultWireFormatType() {
        return "xstream";
    }
    
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws IOException {
        final TextWireFormat textWireFormat = this.asTextWireFormat(wf);
        URI uri;
        try {
            uri = URISupport.removeQuery(location);
        }
        catch (URISyntaxException e) {
            final MalformedURLException cause = new MalformedURLException("Error removing query on " + location);
            cause.initCause(e);
            throw cause;
        }
        return new HttpClientTransport(textWireFormat, uri);
    }
    
    @Override
    public Transport serverConfigure(final Transport transport, final WireFormat format, final HashMap options) throws Exception {
        return this.compositeConfigure(transport, format, options);
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        transport = super.compositeConfigure(transport, format, options);
        final HttpClientTransport httpTransport = transport.narrow(HttpClientTransport.class);
        if (httpTransport != null && httpTransport.isTrace()) {
            try {
                transport = TransportLoggerFactory.getInstance().createTransportLogger(transport);
            }
            catch (Throwable e) {
                HttpTransportFactory.LOG.error("Could not create TransportLogger object for: " + TransportLoggerFactory.defaultLogWriterName + ", reason: " + e, e);
            }
        }
        final boolean useInactivityMonitor = "true".equals(this.getOption(options, "useInactivityMonitor", "true"));
        if (useInactivityMonitor) {
            transport = new InactivityMonitor(transport, null);
            IntrospectionSupport.setProperties(transport, options);
        }
        return transport;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpTransportFactory.class);
    }
}
