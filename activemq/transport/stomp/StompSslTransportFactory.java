// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.MutexTransport;
import java.util.HashMap;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.net.URISyntaxException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import org.apache.activemq.transport.tcp.SslTransport;
import javax.net.ssl.SSLSocket;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.Socket;
import org.apache.activemq.transport.tcp.SslTransportServer;
import javax.net.ssl.SSLServerSocketFactory;
import java.net.URI;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.transport.tcp.SslTransportFactory;

public class StompSslTransportFactory extends SslTransportFactory implements BrokerServiceAware
{
    private BrokerContext brokerContext;
    
    public StompSslTransportFactory() {
        this.brokerContext = null;
    }
    
    @Override
    protected String getDefaultWireFormatType() {
        return "stomp";
    }
    
    @Override
    protected SslTransportServer createSslTransportServer(final URI location, final SSLServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        return new SslTransportServer(this, location, serverSocketFactory) {
            @Override
            protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
                return new SslTransport(format, (SSLSocket)socket) {
                    private X509Certificate[] cachedPeerCerts;
                    
                    @Override
                    public void doConsume(final Object command) {
                        final StompFrame frame = (StompFrame)command;
                        if (this.cachedPeerCerts == null) {
                            this.cachedPeerCerts = this.getPeerCertificates();
                        }
                        frame.setTransportContext(this.cachedPeerCerts);
                        super.doConsume(command);
                    }
                };
            }
        };
    }
    
    @Override
    public Transport compositeConfigure(Transport transport, final WireFormat format, final Map options) {
        transport = new StompTransportFilter(transport, format, this.brokerContext);
        IntrospectionSupport.setProperties(transport, options);
        return super.compositeConfigure(transport, format, options);
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
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerContext = brokerService.getBrokerContext();
    }
    
    @Override
    protected Transport createInactivityMonitor(final Transport transport, final WireFormat format) {
        final StompInactivityMonitor monitor = new StompInactivityMonitor(transport, format);
        final StompTransportFilter filter = transport.narrow(StompTransportFilter.class);
        filter.setInactivityMonitor(monitor);
        return monitor;
    }
}
