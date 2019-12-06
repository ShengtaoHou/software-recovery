// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.slf4j.LoggerFactory;
import java.util.Map;
import java.net.InetSocketAddress;
import org.apache.activemq.util.ServiceStopper;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.GzipHandler;
import javax.servlet.Servlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.apache.activemq.transport.xstream.XStreamWireFormat;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.transport.SocketConnectorFactory;
import java.net.URI;
import org.apache.activemq.transport.util.TextWireFormat;
import org.slf4j.Logger;
import org.apache.activemq.transport.WebTransportServerSupport;

public class HttpTransportServer extends WebTransportServerSupport
{
    private static final Logger LOG;
    private TextWireFormat wireFormat;
    private final HttpTransportFactory transportFactory;
    
    public HttpTransportServer(final URI uri, final HttpTransportFactory factory) {
        super(uri);
        this.bindAddress = uri;
        this.transportFactory = factory;
        this.socketConnectorFactory = new SocketConnectorFactory();
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
    }
    
    public TextWireFormat getWireFormat() {
        if (this.wireFormat == null) {
            this.wireFormat = this.createWireFormat();
        }
        return this.wireFormat;
    }
    
    public void setWireFormat(final TextWireFormat wireFormat) {
        this.wireFormat = wireFormat;
    }
    
    protected TextWireFormat createWireFormat() {
        return new XStreamWireFormat();
    }
    
    protected void setConnector(final Connector connector) {
        this.connector = connector;
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
        holder.setServlet((Servlet)new HttpTunnelServlet());
        contextHandler.addServlet(holder, "/");
        contextHandler.setAttribute("acceptListener", (Object)this.getAcceptListener());
        contextHandler.setAttribute("wireFormat", (Object)this.getWireFormat());
        contextHandler.setAttribute("transportFactory", (Object)this.transportFactory);
        contextHandler.setAttribute("transportOptions", (Object)this.transportOptions);
        final GzipHandler gzipHandler = new GzipHandler();
        contextHandler.setHandler((Handler)gzipHandler);
        this.server.start();
        int port = boundTo.getPort();
        if (this.connector.getLocalPort() != -1) {
            port = this.connector.getLocalPort();
        }
        this.setConnectURI(new URI(boundTo.getScheme(), boundTo.getUserInfo(), boundTo.getHost(), port, boundTo.getPath(), boundTo.getQuery(), boundTo.getFragment()));
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
    public void setTransportOption(final Map<String, Object> transportOptions) {
        this.socketConnectorFactory.setTransportOptions(transportOptions);
        super.setTransportOption(transportOptions);
    }
    
    @Override
    public boolean isSslServer() {
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpTransportServer.class);
    }
}
