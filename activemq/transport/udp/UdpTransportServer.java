// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.transport.CommandJoiner;
import org.apache.activemq.transport.reliable.ReliableTransport;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.InactivityMonitor;
import org.apache.activemq.util.ServiceStopper;
import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.command.BrokerInfo;
import java.util.HashMap;
import java.net.URI;
import java.util.Map;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.reliable.ReplayStrategy;
import org.slf4j.Logger;
import org.apache.activemq.transport.TransportServerSupport;

@Deprecated
public class UdpTransportServer extends TransportServerSupport
{
    private static final Logger LOG;
    private final UdpTransport serverTransport;
    private final ReplayStrategy replayStrategy;
    private final Transport configuredTransport;
    private boolean usingWireFormatNegotiation;
    private final Map<DatagramEndpoint, Transport> transports;
    private boolean allowLinkStealing;
    
    public UdpTransportServer(final URI connectURI, final UdpTransport serverTransport, final Transport configuredTransport, final ReplayStrategy replayStrategy) {
        super(connectURI);
        this.transports = new HashMap<DatagramEndpoint, Transport>();
        this.serverTransport = serverTransport;
        this.configuredTransport = configuredTransport;
        this.replayStrategy = replayStrategy;
    }
    
    @Override
    public String toString() {
        return "UdpTransportServer@" + this.serverTransport;
    }
    
    public void run() {
    }
    
    public UdpTransport getServerTransport() {
        return this.serverTransport;
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
    }
    
    @Override
    protected void doStart() throws Exception {
        UdpTransportServer.LOG.info("Starting " + this);
        this.configuredTransport.setTransportListener(new TransportListener() {
            @Override
            public void onCommand(final Object o) {
                final Command command = (Command)o;
                UdpTransportServer.this.processInboundConnection(command);
            }
            
            @Override
            public void onException(final IOException error) {
                UdpTransportServer.LOG.error("Caught: " + error, error);
            }
            
            @Override
            public void transportInterupted() {
            }
            
            @Override
            public void transportResumed() {
            }
        });
        this.configuredTransport.start();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        this.configuredTransport.stop();
    }
    
    protected void processInboundConnection(final Command command) {
        final DatagramEndpoint endpoint = (DatagramEndpoint)command.getFrom();
        if (UdpTransportServer.LOG.isDebugEnabled()) {
            UdpTransportServer.LOG.debug("Received command on: " + this + " from address: " + endpoint + " command: " + command);
        }
        Transport transport = null;
        synchronized (this.transports) {
            transport = this.transports.get(endpoint);
            if (transport == null) {
                if (this.usingWireFormatNegotiation && !command.isWireFormatInfo()) {
                    UdpTransportServer.LOG.error("Received inbound server communication from: " + command.getFrom() + " expecting WireFormatInfo but was command: " + command);
                }
                else {
                    if (UdpTransportServer.LOG.isDebugEnabled()) {
                        UdpTransportServer.LOG.debug("Creating a new UDP server connection");
                    }
                    try {
                        transport = this.createTransport(command, endpoint);
                        transport = this.configureTransport(transport);
                        this.transports.put(endpoint, transport);
                    }
                    catch (IOException e) {
                        UdpTransportServer.LOG.error("Caught: " + e, e);
                        this.getAcceptListener().onAcceptError(e);
                    }
                }
            }
            else {
                UdpTransportServer.LOG.warn("Discarding duplicate command to server from: " + endpoint + " command: " + command);
            }
        }
    }
    
    protected Transport configureTransport(Transport transport) {
        transport = new InactivityMonitor(transport, this.serverTransport.getWireFormat());
        this.getAcceptListener().onAccept(transport);
        return transport;
    }
    
    protected Transport createTransport(final Command command, final DatagramEndpoint endpoint) throws IOException {
        if (endpoint == null) {
            throw new IOException("No endpoint available for command: " + command);
        }
        final SocketAddress address = endpoint.getAddress();
        final OpenWireFormat connectionWireFormat = this.serverTransport.getWireFormat().copy();
        final UdpTransport transport = new UdpTransport(connectionWireFormat, address);
        final ReliableTransport reliableTransport = new ReliableTransport(transport, transport);
        reliableTransport.getReplayer();
        reliableTransport.setReplayStrategy(this.replayStrategy);
        return new CommandJoiner(reliableTransport, connectionWireFormat) {
            @Override
            public void start() throws Exception {
                super.start();
                reliableTransport.onCommand(command);
            }
        };
    }
    
    @Override
    public InetSocketAddress getSocketAddress() {
        return this.serverTransport.getLocalSocketAddress();
    }
    
    @Override
    public boolean isSslServer() {
        return false;
    }
    
    @Override
    public boolean isAllowLinkStealing() {
        return this.allowLinkStealing;
    }
    
    @Override
    public void setAllowLinkStealing(final boolean allowLinkStealing) {
        this.allowLinkStealing = allowLinkStealing;
    }
    
    static {
        LOG = LoggerFactory.getLogger(UdpTransportServer.class);
    }
}
