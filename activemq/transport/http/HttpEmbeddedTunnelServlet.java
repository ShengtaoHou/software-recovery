// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.apache.activemq.transport.TransportAcceptListener;
import javax.servlet.ServletException;
import org.apache.activemq.transport.TransportServer;
import java.net.URI;
import org.apache.activemq.broker.BrokerService;

public class HttpEmbeddedTunnelServlet extends HttpTunnelServlet
{
    private static final long serialVersionUID = -3705734740251302361L;
    protected BrokerService broker;
    protected HttpTransportServer transportConnector;
    
    @Override
    public synchronized void init() throws ServletException {
        try {
            if (this.broker == null) {
                this.broker = this.createBroker();
                final String url = this.getConnectorURL();
                final HttpTransportFactory factory = new HttpTransportFactory();
                this.transportConnector = (HttpTransportServer)factory.doBind(new URI(url));
                this.broker.addConnector(this.transportConnector);
                final String brokerURL = this.getServletContext().getInitParameter("org.apache.activemq.brokerURL");
                if (brokerURL != null) {
                    this.log("Listening for internal communication on: " + brokerURL);
                }
            }
            this.broker.start();
        }
        catch (Exception e) {
            throw new ServletException("Failed to start embedded broker: " + e, (Throwable)e);
        }
        final TransportAcceptListener listener = this.transportConnector.getAcceptListener();
        this.getServletContext().setAttribute("transportChannelListener", (Object)listener);
        super.init();
    }
    
    protected BrokerService createBroker() throws Exception {
        final BrokerService answer = new BrokerService();
        return answer;
    }
    
    protected String getConnectorURL() {
        return "http://localhost/" + this.getServletContext().getServletContextName();
    }
}
