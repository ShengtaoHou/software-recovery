// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.ws;

import org.apache.activemq.transport.Transport;
import org.eclipse.jetty.websocket.WebSocket;
import java.io.IOException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import org.apache.activemq.transport.TransportAcceptListener;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class WSServlet extends WebSocketServlet
{
    private static final long serialVersionUID = -4716657876092884139L;
    private TransportAcceptListener listener;
    
    public void init() throws ServletException {
        super.init();
        this.listener = (TransportAcceptListener)this.getServletContext().getAttribute("acceptListener");
        if (this.listener == null) {
            throw new ServletException("No such attribute 'acceptListener' available in the ServletContext");
        }
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getNamedDispatcher("default").forward((ServletRequest)request, (ServletResponse)response);
    }
    
    public WebSocket doWebSocketConnect(final HttpServletRequest request, final String protocol) {
        WebSocket socket;
        if (protocol != null && protocol.startsWith("mqtt")) {
            socket = (WebSocket)new MQTTSocket();
        }
        else {
            socket = (WebSocket)new StompSocket();
        }
        this.listener.onAccept((Transport)socket);
        return socket;
    }
}
