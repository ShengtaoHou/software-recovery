// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.ws;

import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.Connector;
import org.apache.activemq.command.BrokerInfo;
import java.net.InetSocketAddress;
import org.apache.activemq.util.ServiceStopper;
import java.util.Iterator;
import javax.servlet.Servlet;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.apache.activemq.transport.SocketConnectorFactory;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.transport.WebTransportServerSupport;

public class WSTransportServer extends WebTransportServerSupport
{
    private static final Logger LOG;
    
    public WSTransportServer(final URI location) {
        super(location);
        this.bindAddress = location;
        this.socketConnectorFactory = new SocketConnectorFactory();
    }
    
    @Override
    protected void doStart() throws Exception {
        this.server = new Server();
        if (this.connector == null) {
            this.connector = this.socketConnectorFactory.createConnector();
        }
        final URI boundTo = this.bind();
        final ServletContextHandler contextHandler = new ServletContextHandler((HandlerContainer)this.server, "/", 0);
        final ServletHolder holder = new ServletHolder();
        final Map<String, Object> webSocketOptions = IntrospectionSupport.extractProperties(this.transportOptions, "websocket.");
        for (final Map.Entry<String, Object> webSocketEntry : webSocketOptions.entrySet()) {
            final Object value = webSocketEntry.getValue();
            if (value != null) {
                holder.setInitParameter((String)webSocketEntry.getKey(), value.toString());
            }
        }
        holder.setServlet((Servlet)new WSServlet());
        contextHandler.addServlet(holder, "/");
        contextHandler.setAttribute("acceptListener", (Object)this.getAcceptListener());
        this.server.start();
        int port = boundTo.getPort();
        if (this.connector.getLocalPort() != -1) {
            port = this.connector.getLocalPort();
        }
        this.setConnectURI(new URI(boundTo.getScheme(), boundTo.getUserInfo(), boundTo.getHost(), port, boundTo.getPath(), boundTo.getQuery(), boundTo.getFragment()));
        WSTransportServer.LOG.info("Listening for connections at {}", this.getConnectURI());
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        final Server temp = this.server;
        this.server = null;
        if (temp != null) {
            temp.stop();
        }
    }
    
    @Override
    public InetSocketAddress getSocketAddress() {
        return null;
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
    }
    
    protected void setConnector(final Connector connector) {
        this.connector = connector;
    }
    
    @Override
    public void setTransportOption(final Map<String, Object> transportOptions) {
        final Map<String, Object> socketOptions = IntrospectionSupport.extractProperties(transportOptions, "transport.");
        this.socketConnectorFactory.setTransportOptions(socketOptions);
        super.setTransportOption(transportOptions);
    }
    
    @Override
    public boolean isSslServer() {
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(WSTransportServer.class);
    }
}
