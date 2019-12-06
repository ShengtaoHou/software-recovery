// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.http;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.xstream.XStreamWireFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.transport.Transport;
import java.util.Map;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceListener;
import java.io.BufferedReader;
import java.io.InputStream;
import org.apache.activemq.command.WireFormatInfo;
import java.io.Reader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.Command;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.transport.TransportAcceptListener;
import org.slf4j.Logger;
import javax.servlet.http.HttpServlet;

public class HttpTunnelServlet extends HttpServlet
{
    private static final long serialVersionUID = -3826714430767484333L;
    private static final Logger LOG;
    private TransportAcceptListener listener;
    private HttpTransportFactory transportFactory;
    private TextWireFormat wireFormat;
    private ConcurrentMap<String, BlockingQueueTransport> clients;
    private final long requestTimeout = 30000L;
    private HashMap<String, Object> transportOptions;
    
    public HttpTunnelServlet() {
        this.clients = new ConcurrentHashMap<String, BlockingQueueTransport>();
    }
    
    public void init() throws ServletException {
        super.init();
        this.listener = (TransportAcceptListener)this.getServletContext().getAttribute("acceptListener");
        if (this.listener == null) {
            throw new ServletException("No such attribute 'acceptListener' available in the ServletContext");
        }
        this.transportFactory = (HttpTransportFactory)this.getServletContext().getAttribute("transportFactory");
        if (this.transportFactory == null) {
            throw new ServletException("No such attribute 'transportFactory' available in the ServletContext");
        }
        this.transportOptions = (HashMap<String, Object>)this.getServletContext().getAttribute("transportOptions");
        this.wireFormat = (TextWireFormat)this.getServletContext().getAttribute("wireFormat");
        if (this.wireFormat == null) {
            this.wireFormat = this.createWireFormat();
        }
    }
    
    protected void doOptions(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.addHeader("Accepts-Encoding", "gzip");
        super.doOptions(request, response);
    }
    
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.createTransportChannel(request, response);
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Command packet = null;
        int count = 0;
        try {
            final BlockingQueueTransport transportChannel = this.getTransportChannel(request, response);
            if (transportChannel == null) {
                return;
            }
            packet = transportChannel.getQueue().poll(30000L, TimeUnit.MILLISECONDS);
            final DataOutputStream stream = new DataOutputStream((OutputStream)response.getOutputStream());
            this.wireFormat.marshal(packet, stream);
            ++count;
        }
        catch (InterruptedException ex) {}
        if (count == 0) {
            response.setStatus(408);
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        InputStream stream = (InputStream)request.getInputStream();
        final String contentType = request.getContentType();
        if (contentType != null && contentType.equals("application/x-gzip")) {
            stream = new GZIPInputStream(stream);
        }
        final Command command = (Command)this.wireFormat.unmarshalText(new InputStreamReader(stream, "UTF-8"));
        if (command instanceof WireFormatInfo) {
            final WireFormatInfo info = (WireFormatInfo)command;
            if (!this.canProcessWireFormatVersion(info.getVersion())) {
                response.sendError(404, "Cannot process wire format of version: " + info.getVersion());
            }
        }
        else {
            final BlockingQueueTransport transport = this.getTransportChannel(request, response);
            if (transport == null) {
                return;
            }
            transport.doConsume(command);
        }
    }
    
    private boolean canProcessWireFormatVersion(final int version) {
        return true;
    }
    
    protected String readRequestBody(final HttpServletRequest request) throws IOException {
        final StringBuffer buffer = new StringBuffer();
        final BufferedReader reader = request.getReader();
        while (true) {
            final String line = reader.readLine();
            if (line == null) {
                break;
            }
            buffer.append(line);
            buffer.append("\n");
        }
        return buffer.toString();
    }
    
    protected BlockingQueueTransport getTransportChannel(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String clientID = request.getHeader("clientID");
        if (clientID == null) {
            response.sendError(400, "No clientID header specified");
            HttpTunnelServlet.LOG.warn("No clientID header specified");
            return null;
        }
        final BlockingQueueTransport answer = this.clients.get(clientID);
        if (answer == null) {
            HttpTunnelServlet.LOG.warn("The clientID header specified is invalid. Client sesion has not yet been established for it: " + clientID);
            return null;
        }
        return answer;
    }
    
    protected BlockingQueueTransport createTransportChannel(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String clientID = request.getHeader("clientID");
        if (clientID == null) {
            response.sendError(400, "No clientID header specified");
            HttpTunnelServlet.LOG.warn("No clientID header specified");
            return null;
        }
        final BlockingQueueTransport answer = this.createTransportChannel();
        if (this.clients.putIfAbsent(clientID, answer) != null) {
            response.sendError(400, "A session for clientID '" + clientID + "' has already been established");
            HttpTunnelServlet.LOG.warn("A session for clientID '" + clientID + "' has already been established");
            return null;
        }
        answer.addServiceListener(new ServiceListener() {
            @Override
            public void started(final Service service) {
            }
            
            @Override
            public void stopped(final Service service) {
                HttpTunnelServlet.this.clients.remove(clientID);
            }
        });
        Transport transport = answer;
        try {
            final HashMap<String, Object> options = new HashMap<String, Object>(this.transportOptions);
            transport = this.transportFactory.serverConfigure(answer, null, options);
        }
        catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
        this.listener.onAccept(transport);
        while (!transport.isConnected() && !transport.isDisposed()) {
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {}
        }
        if (transport.isDisposed()) {
            response.sendError(400, "The session for clientID '" + clientID + "' was prematurely disposed");
            HttpTunnelServlet.LOG.warn("The session for clientID '" + clientID + "' was prematurely disposed");
            return null;
        }
        return answer;
    }
    
    protected BlockingQueueTransport createTransportChannel() {
        return new BlockingQueueTransport(new LinkedBlockingQueue<Object>());
    }
    
    protected TextWireFormat createWireFormat() {
        return new XStreamWireFormat();
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpTunnelServlet.class);
    }
}
