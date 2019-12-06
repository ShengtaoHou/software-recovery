// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.apache.activemq.util.IntrospectionSupport;
import org.apache.activemq.wireformat.WireFormatFactory;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import org.apache.activemq.wireformat.WireFormat;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import java.util.concurrent.Executor;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.util.FactoryFinder;

public abstract class TransportFactory
{
    private static final FactoryFinder TRANSPORT_FACTORY_FINDER;
    private static final FactoryFinder WIREFORMAT_FACTORY_FINDER;
    private static final ConcurrentHashMap<String, TransportFactory> TRANSPORT_FACTORYS;
    private static final String WRITE_TIMEOUT_FILTER = "soWriteTimeout";
    private static final String THREAD_NAME_FILTER = "threadName";
    
    public abstract TransportServer doBind(final URI p0) throws IOException;
    
    public Transport doConnect(final URI location, final Executor ex) throws Exception {
        return this.doConnect(location);
    }
    
    public Transport doCompositeConnect(final URI location, final Executor ex) throws Exception {
        return this.doCompositeConnect(location);
    }
    
    public static Transport connect(final URI location) throws Exception {
        final TransportFactory tf = findTransportFactory(location);
        return tf.doConnect(location);
    }
    
    public static Transport connect(final URI location, final Executor ex) throws Exception {
        final TransportFactory tf = findTransportFactory(location);
        return tf.doConnect(location, ex);
    }
    
    public static Transport compositeConnect(final URI location) throws Exception {
        final TransportFactory tf = findTransportFactory(location);
        return tf.doCompositeConnect(location);
    }
    
    public static Transport compositeConnect(final URI location, final Executor ex) throws Exception {
        final TransportFactory tf = findTransportFactory(location);
        return tf.doCompositeConnect(location, ex);
    }
    
    public static TransportServer bind(final URI location) throws IOException {
        final TransportFactory tf = findTransportFactory(location);
        return tf.doBind(location);
    }
    
    public Transport doConnect(final URI location) throws Exception {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            if (!options.containsKey("wireFormat.host")) {
                options.put("wireFormat.host", location.getHost());
            }
            final WireFormat wf = this.createWireFormat(options);
            final Transport transport = this.createTransport(location, wf);
            final Transport rc = this.configure(transport, wf, options);
            if (!options.isEmpty()) {
                throw new IllegalArgumentException("Invalid connect parameters: " + options);
            }
            return rc;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    public Transport doCompositeConnect(final URI location) throws Exception {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final WireFormat wf = this.createWireFormat(options);
            final Transport transport = this.createTransport(location, wf);
            final Transport rc = this.compositeConfigure(transport, wf, options);
            if (!options.isEmpty()) {
                throw new IllegalArgumentException("Invalid connect parameters: " + options);
            }
            return rc;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    public static void registerTransportFactory(final String scheme, final TransportFactory tf) {
        TransportFactory.TRANSPORT_FACTORYS.put(scheme, tf);
    }
    
    protected Transport createTransport(final URI location, final WireFormat wf) throws MalformedURLException, UnknownHostException, IOException {
        throw new IOException("createTransport() method not implemented!");
    }
    
    public static TransportFactory findTransportFactory(final URI location) throws IOException {
        final String scheme = location.getScheme();
        if (scheme == null) {
            throw new IOException("Transport not scheme specified: [" + location + "]");
        }
        TransportFactory tf = TransportFactory.TRANSPORT_FACTORYS.get(scheme);
        if (tf == null) {
            try {
                tf = (TransportFactory)TransportFactory.TRANSPORT_FACTORY_FINDER.newInstance(scheme);
                TransportFactory.TRANSPORT_FACTORYS.put(scheme, tf);
            }
            catch (Throwable e) {
                throw IOExceptionSupport.create("Transport scheme NOT recognized: [" + scheme + "]", e);
            }
        }
        return tf;
    }
    
    protected WireFormat createWireFormat(final Map<String, String> options) throws IOException {
        final WireFormatFactory factory = this.createWireFormatFactory(options);
        final WireFormat format = factory.createWireFormat();
        return format;
    }
    
    protected WireFormatFactory createWireFormatFactory(final Map<String, String> options) throws IOException {
        String wireFormat = options.remove("wireFormat");
        if (wireFormat == null) {
            wireFormat = this.getDefaultWireFormatType();
        }
        try {
            final WireFormatFactory wff = (WireFormatFactory)TransportFactory.WIREFORMAT_FACTORY_FINDER.newInstance(wireFormat);
            IntrospectionSupport.setProperties(wff, options, "wireFormat.");
            return wff;
        }
        catch (Throwable e) {
            throw IOExceptionSupport.create("Could not create wire format factory for: " + wireFormat + ", reason: " + e, e);
        }
    }
    
    protected String getDefaultWireFormatType() {
        return "default";
    }
    
    public Transport configure(Transport transport, final WireFormat wf, final Map options) throws Exception {
        transport = this.compositeConfigure(transport, wf, options);
        transport = new MutexTransport(transport);
        transport = new ResponseCorrelator(transport);
        return transport;
    }
    
    public Transport serverConfigure(Transport transport, final WireFormat format, final HashMap options) throws Exception {
        if (options.containsKey("threadName")) {
            transport = new ThreadNameFilter(transport);
        }
        transport = this.compositeConfigure(transport, format, options);
        transport = new MutexTransport(transport);
        return transport;
    }
    
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        if (options.containsKey("soWriteTimeout")) {
            transport = new WriteTimeoutFilter(transport);
            final String soWriteTimeout = options.remove("soWriteTimeout");
            if (soWriteTimeout != null) {
                ((WriteTimeoutFilter)transport).setWriteTimeout(Long.parseLong(soWriteTimeout));
            }
        }
        IntrospectionSupport.setProperties(transport, options);
        return transport;
    }
    
    protected String getOption(final Map options, final String key, final String def) {
        String rc = options.remove(key);
        if (rc == null) {
            rc = def;
        }
        return rc;
    }
    
    static {
        TRANSPORT_FACTORY_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/transport/");
        WIREFORMAT_FACTORY_FINDER = new FactoryFinder("META-INF/services/org/apache/activemq/wireformat/");
        TRANSPORT_FACTORYS = new ConcurrentHashMap<String, TransportFactory>();
    }
}
