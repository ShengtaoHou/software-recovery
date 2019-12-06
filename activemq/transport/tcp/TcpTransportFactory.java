// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.InactivityMonitor;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import org.apache.activemq.transport.WireFormatNegotiator;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.TransportLoggerSupport;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.io.IOException;
import javax.net.ServerSocketFactory;
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

public class TcpTransportFactory extends TransportFactory
{
    private static final Logger LOG;
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            final ServerSocketFactory serverSocketFactory = this.createServerSocketFactory();
            final TcpTransportServer server = this.createTcpTransportServer(location, serverSocketFactory);
            server.setWireFormatFactory(this.createWireFormatFactory(options));
            IntrospectionSupport.setProperties(server, options);
            final Map<String, Object> transportOptions = IntrospectionSupport.extractProperties(options, "transport.");
            server.setTransportOption(transportOptions);
            server.bind();
            return server;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    protected TcpTransportServer createTcpTransportServer(final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new TcpTransportServer(this, location, serverSocketFactory);
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        final TcpTransport tcpTransport = transport.narrow(TcpTransport.class);
        IntrospectionSupport.setProperties(tcpTransport, options);
        final Map<String, Object> socketOptions = IntrospectionSupport.extractProperties(options, "socket.");
        tcpTransport.setSocketOptions(socketOptions);
        if (tcpTransport.isTrace()) {
            try {
                transport = TransportLoggerSupport.createTransportLogger(transport, tcpTransport.getLogWriterName(), tcpTransport.isDynamicManagement(), tcpTransport.isStartLogging(), tcpTransport.getJmxPort());
            }
            catch (Throwable e) {
                TcpTransportFactory.LOG.error("Could not create TransportLogger object for: " + tcpTransport.getLogWriterName() + ", reason: " + e, e);
            }
        }
        final boolean useInactivityMonitor = "true".equals(this.getOption(options, "useInactivityMonitor", "true"));
        if (useInactivityMonitor && this.isUseInactivityMonitor(transport)) {
            transport = this.createInactivityMonitor(transport, format);
            IntrospectionSupport.setProperties(transport, options);
        }
        if (format instanceof OpenWireFormat) {
            transport = new WireFormatNegotiator(transport, (OpenWireFormat)format, tcpTransport.getMinmumWireFormatVersion());
        }
        return super.compositeConfigure(transport, format, options);
    }
    
    protected boolean isUseInactivityMonitor(final Transport transport) {
        return true;
    }
    
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws UnknownHostException, IOException {
        URI localLocation = null;
        final String path = location.getPath();
        if (path != null && path.length() > 0) {
            final int localPortIndex = path.indexOf(58);
            try {
                Integer.parseInt(path.substring(localPortIndex + 1, path.length()));
                final String localString = location.getScheme() + ":/" + path;
                localLocation = new URI(localString);
            }
            catch (Exception e) {
                TcpTransportFactory.LOG.warn("path isn't a valid local location for TcpTransport to use", e.getMessage());
                if (TcpTransportFactory.LOG.isDebugEnabled()) {
                    TcpTransportFactory.LOG.debug("Failure detail", e);
                }
            }
        }
        final SocketFactory socketFactory = this.createSocketFactory();
        return this.createTcpTransport(wf, socketFactory, location, localLocation);
    }
    
    protected TcpTransport createTcpTransport(final WireFormat wf, final SocketFactory socketFactory, final URI location, final URI localLocation) throws UnknownHostException, IOException {
        return new TcpTransport(wf, socketFactory, location, localLocation);
    }
    
    protected ServerSocketFactory createServerSocketFactory() throws IOException {
        return ServerSocketFactory.getDefault();
    }
    
    protected SocketFactory createSocketFactory() throws IOException {
        return SocketFactory.getDefault();
    }
    
    protected Transport createInactivityMonitor(final Transport transport, final WireFormat format) {
        return new InactivityMonitor(transport, format);
    }
    
    static {
        LOG = LoggerFactory.getLogger(TcpTransportFactory.class);
    }
}
