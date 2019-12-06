// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.discovery.http;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import javax.servlet.Servlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.net.URI;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.Server;
import org.apache.activemq.Service;

public class EmbeddedJettyServer implements Service
{
    private HTTPDiscoveryAgent agent;
    private Server server;
    private SelectChannelConnector connector;
    private DiscoveryRegistryServlet camelServlet;
    
    public EmbeddedJettyServer() {
        this.camelServlet = new DiscoveryRegistryServlet();
    }
    
    @Override
    public void start() throws Exception {
        final URI uri = new URI(this.agent.getRegistryURL());
        this.server = new Server();
        final ServletContextHandler context = new ServletContextHandler(0);
        context.setContextPath("/");
        final ServletHolder holder = new ServletHolder();
        holder.setServlet((Servlet)this.camelServlet);
        context.addServlet(holder, "/*");
        this.server.setHandler((Handler)context);
        this.server.start();
        int port = 80;
        if (uri.getPort() >= 0) {
            port = uri.getPort();
        }
        (this.connector = new SelectChannelConnector()).setPort(port);
        this.server.addConnector((Connector)this.connector);
        this.connector.start();
    }
    
    @Override
    public void stop() throws Exception {
        if (this.connector != null) {
            this.connector.stop();
            this.connector = null;
        }
        if (this.server != null) {
            this.server.stop();
            this.server = null;
        }
    }
    
    public HTTPDiscoveryAgent getAgent() {
        return this.agent;
    }
    
    public void setAgent(final HTTPDiscoveryAgent agent) {
        this.agent = agent;
    }
}
