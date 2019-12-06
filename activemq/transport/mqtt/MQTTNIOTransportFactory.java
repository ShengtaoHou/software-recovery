// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import org.apache.activemq.transport.MutexTransport;
import java.util.HashMap;
import java.net.UnknownHostException;
import org.apache.activemq.transport.tcp.TcpTransport;
import javax.net.SocketFactory;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import org.apache.activemq.transport.tcp.TcpTransportFactory;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import javax.net.ServerSocketFactory;
import java.net.URI;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.transport.nio.NIOTransportFactory;

public class MQTTNIOTransportFactory extends NIOTransportFactory implements BrokerServiceAware
{
    private BrokerService brokerService;
    
    public MQTTNIOTransportFactory() {
        this.brokerService = null;
    }
    
    @Override
    protected String getDefaultWireFormatType() {
        return "mqtt";
    }
    
    @Override
    protected TcpTransportServer createTcpTransportServer(final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        final TcpTransportServer result = new TcpTransportServer(this, location, serverSocketFactory) {
            @Override
            protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
                return new MQTTNIOTransport(format, socket);
            }
        };
        result.setAllowLinkStealing(true);
        return result;
    }
    
    @Override
    protected TcpTransport createTcpTransport(final WireFormat wf, final SocketFactory socketFactory, final URI location, final URI localLocation) throws UnknownHostException, IOException {
        return new MQTTNIOTransport(wf, socketFactory, location, localLocation);
    }
    
    @Override
    public Transport serverConfigure(Transport transport, final WireFormat format, final HashMap options) throws Exception {
        transport = super.serverConfigure(transport, format, options);
        final MutexTransport mutex = transport.narrow(MutexTransport.class);
        if (mutex != null) {
            mutex.setSyncOnCommand(true);
        }
        return transport;
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        transport = new MQTTTransportFilter(transport, format, this.brokerService);
        IntrospectionSupport.setProperties(transport, options);
        return super.compositeConfigure(transport, format, options);
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    @Override
    protected Transport createInactivityMonitor(final Transport transport, final WireFormat format) {
        final MQTTInactivityMonitor monitor = new MQTTInactivityMonitor(transport, format);
        final MQTTTransportFilter filter = transport.narrow(MQTTTransportFilter.class);
        filter.setInactivityMonitor(monitor);
        return monitor;
    }
}
