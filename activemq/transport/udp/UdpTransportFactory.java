// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import org.apache.activemq.transport.reliable.ExceptionIfDroppedReplayStrategy;
import org.apache.activemq.transport.reliable.DefaultReplayStrategy;
import org.apache.activemq.transport.reliable.ReplayStrategy;
import org.apache.activemq.transport.reliable.Replayer;
import org.apache.activemq.transport.reliable.ReliableTransport;
import java.net.UnknownHostException;
import org.apache.activemq.transport.InactivityMonitor;
import org.apache.activemq.TransportLoggerSupport;
import org.apache.activemq.transport.CommandJoiner;
import org.apache.activemq.util.IntrospectionSupport;
import java.io.IOException;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.wireformat.WireFormat;
import java.net.URISyntaxException;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.transport.Transport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportFactory;

@Deprecated
public class UdpTransportFactory extends TransportFactory
{
    private static final Logger log;
    
    @Override
    public TransportServer doBind(final URI location) throws IOException {
        try {
            final Map<String, String> options = new HashMap<String, String>(URISupport.parseParameters(location));
            if (options.containsKey("port")) {
                throw new IllegalArgumentException("The port property cannot be specified on a UDP server transport - please use the port in the URI syntax");
            }
            final WireFormat wf = this.createWireFormat(options);
            final int port = location.getPort();
            final OpenWireFormat openWireFormat = this.asOpenWireFormat(wf);
            final UdpTransport transport = (UdpTransport)this.createTransport(location.getPort(), wf);
            final Transport configuredTransport = this.configure(transport, wf, options, true);
            final UdpTransportServer server = new UdpTransportServer(location, transport, configuredTransport, this.createReplayStrategy());
            return server;
        }
        catch (URISyntaxException e) {
            throw IOExceptionSupport.create(e);
        }
        catch (Exception e2) {
            throw IOExceptionSupport.create(e2);
        }
    }
    
    @Override
    public Transport configure(final Transport transport, final WireFormat format, final Map options) throws Exception {
        return this.configure(transport, format, options, false);
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        IntrospectionSupport.setProperties(transport, options);
        final UdpTransport udpTransport = (UdpTransport)transport;
        transport = new CommandJoiner(transport, this.asOpenWireFormat(format));
        if (udpTransport.isTrace()) {
            try {
                transport = TransportLoggerSupport.createTransportLogger(transport);
            }
            catch (Throwable e) {
                UdpTransportFactory.log.error("Could not create TransportLogger, reason: " + e, e);
            }
        }
        transport = new InactivityMonitor(transport, format);
        if (format instanceof OpenWireFormat) {
            transport = this.configureClientSideNegotiator(transport, format, udpTransport);
        }
        return transport;
    }
    
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws UnknownHostException, IOException {
        final OpenWireFormat wireFormat = this.asOpenWireFormat(wf);
        return new UdpTransport(wireFormat, location);
    }
    
    protected Transport createTransport(final int port, final WireFormat wf) throws UnknownHostException, IOException {
        final OpenWireFormat wireFormat = this.asOpenWireFormat(wf);
        return new UdpTransport(wireFormat, port);
    }
    
    protected Transport configure(Transport transport, final WireFormat format, final Map options, final boolean acceptServer) throws Exception {
        IntrospectionSupport.setProperties(transport, options);
        final UdpTransport udpTransport = (UdpTransport)transport;
        final OpenWireFormat openWireFormat = this.asOpenWireFormat(format);
        if (udpTransport.isTrace()) {
            transport = TransportLoggerSupport.createTransportLogger(transport);
        }
        transport = new InactivityMonitor(transport, format);
        if (!acceptServer && format instanceof OpenWireFormat) {
            transport = this.configureClientSideNegotiator(transport, format, udpTransport);
        }
        if (acceptServer) {
            udpTransport.setReplayEnabled(false);
            transport = new CommandJoiner(transport, openWireFormat);
            return transport;
        }
        final ReliableTransport reliableTransport = new ReliableTransport(transport, udpTransport);
        final Replayer replayer = reliableTransport.getReplayer();
        reliableTransport.setReplayStrategy(this.createReplayStrategy(replayer));
        return new CommandJoiner(reliableTransport, openWireFormat);
    }
    
    protected ReplayStrategy createReplayStrategy(final Replayer replayer) {
        if (replayer != null) {
            return new DefaultReplayStrategy(5);
        }
        return new ExceptionIfDroppedReplayStrategy(1);
    }
    
    protected ReplayStrategy createReplayStrategy() {
        return new DefaultReplayStrategy(5);
    }
    
    protected Transport configureClientSideNegotiator(final Transport transport, final WireFormat format, final UdpTransport udpTransport) {
        return new ResponseRedirectInterceptor(transport, udpTransport);
    }
    
    protected OpenWireFormat asOpenWireFormat(final WireFormat wf) {
        final OpenWireFormat answer = (OpenWireFormat)wf;
        return answer;
    }
    
    static {
        log = LoggerFactory.getLogger(TcpTransportFactory.class);
    }
}
