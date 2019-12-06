// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceSupport;
import java.util.HashMap;
import java.net.InetSocketAddress;
import org.apache.activemq.util.ServiceStopper;
import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import org.apache.activemq.util.InetAddressUtil;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.SocketTimeoutException;
import org.apache.activemq.command.BrokerInfo;
import java.util.Map;
import java.net.SocketException;
import org.apache.activemq.util.IntrospectionSupport;
import javax.net.ssl.SSLServerSocket;
import org.apache.activemq.util.IOExceptionSupport;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.activemq.TransportLoggerSupport;
import org.apache.activemq.openwire.OpenWireFormatFactory;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import javax.net.ServerSocketFactory;
import org.apache.activemq.wireformat.WireFormatFactory;
import java.net.ServerSocket;
import org.slf4j.Logger;
import org.apache.activemq.util.ServiceListener;
import org.apache.activemq.transport.TransportServerThreadSupport;

public class TcpTransportServer extends TransportServerThreadSupport implements ServiceListener
{
    private static final Logger LOG;
    protected ServerSocket serverSocket;
    protected int backlog;
    protected WireFormatFactory wireFormatFactory;
    protected final TcpTransportFactory transportFactory;
    protected long maxInactivityDuration;
    protected long maxInactivityDurationInitalDelay;
    protected int minmumWireFormatVersion;
    protected boolean useQueueForAccept;
    protected boolean allowLinkStealing;
    protected boolean trace;
    protected int soTimeout;
    protected int socketBufferSize;
    protected int connectionTimeout;
    protected String logWriterName;
    protected boolean dynamicManagement;
    protected boolean startLogging;
    protected final ServerSocketFactory serverSocketFactory;
    protected BlockingQueue<Socket> socketQueue;
    protected Thread socketHandlerThread;
    protected int maximumConnections;
    protected AtomicInteger currentTransportCount;
    
    public TcpTransportServer(final TcpTransportFactory transportFactory, final URI location, final ServerSocketFactory serverSocketFactory) throws IOException, URISyntaxException {
        super(location);
        this.backlog = 5000;
        this.wireFormatFactory = new OpenWireFormatFactory();
        this.maxInactivityDuration = 30000L;
        this.maxInactivityDurationInitalDelay = 10000L;
        this.useQueueForAccept = true;
        this.trace = false;
        this.soTimeout = 0;
        this.socketBufferSize = 65536;
        this.connectionTimeout = 30000;
        this.logWriterName = TransportLoggerSupport.defaultLogWriterName;
        this.dynamicManagement = false;
        this.startLogging = true;
        this.socketQueue = new LinkedBlockingQueue<Socket>();
        this.maximumConnections = Integer.MAX_VALUE;
        this.currentTransportCount = new AtomicInteger();
        this.transportFactory = transportFactory;
        this.serverSocketFactory = serverSocketFactory;
    }
    
    public void bind() throws IOException {
        final URI bind = this.getBindLocation();
        String host = bind.getHost();
        host = ((host == null || host.length() == 0) ? "localhost" : host);
        final InetAddress addr = InetAddress.getByName(host);
        try {
            this.configureServerSocket(this.serverSocket = this.serverSocketFactory.createServerSocket(bind.getPort(), this.backlog, addr));
        }
        catch (IOException e) {
            throw IOExceptionSupport.create("Failed to bind to server socket: " + bind + " due to: " + e, e);
        }
        try {
            this.setConnectURI(new URI(bind.getScheme(), bind.getUserInfo(), this.resolveHostName(this.serverSocket, addr), this.serverSocket.getLocalPort(), bind.getPath(), bind.getQuery(), bind.getFragment()));
        }
        catch (URISyntaxException e3) {
            try {
                this.setConnectURI(new URI(bind.getScheme(), bind.getUserInfo(), addr.getHostAddress(), this.serverSocket.getLocalPort(), bind.getPath(), bind.getQuery(), bind.getFragment()));
            }
            catch (URISyntaxException e2) {
                throw IOExceptionSupport.create(e2);
            }
        }
    }
    
    private void configureServerSocket(final ServerSocket socket) throws SocketException {
        socket.setSoTimeout(2000);
        if (this.transportOptions != null) {
            if (socket instanceof SSLServerSocket && this.transportOptions.containsKey("enabledCipherSuites")) {
                final Object cipherSuites = this.transportOptions.remove("enabledCipherSuites");
                if (!IntrospectionSupport.setProperty(socket, "enabledCipherSuites", cipherSuites)) {
                    throw new SocketException(String.format("Invalid transport options {enabledCipherSuites=%s}", cipherSuites));
                }
            }
            IntrospectionSupport.setProperties(socket, this.transportOptions);
        }
    }
    
    public WireFormatFactory getWireFormatFactory() {
        return this.wireFormatFactory;
    }
    
    public void setWireFormatFactory(final WireFormatFactory wireFormatFactory) {
        this.wireFormatFactory = wireFormatFactory;
    }
    
    @Override
    public void setBrokerInfo(final BrokerInfo brokerInfo) {
    }
    
    public long getMaxInactivityDuration() {
        return this.maxInactivityDuration;
    }
    
    public void setMaxInactivityDuration(final long maxInactivityDuration) {
        this.maxInactivityDuration = maxInactivityDuration;
    }
    
    public long getMaxInactivityDurationInitalDelay() {
        return this.maxInactivityDurationInitalDelay;
    }
    
    public void setMaxInactivityDurationInitalDelay(final long maxInactivityDurationInitalDelay) {
        this.maxInactivityDurationInitalDelay = maxInactivityDurationInitalDelay;
    }
    
    public int getMinmumWireFormatVersion() {
        return this.minmumWireFormatVersion;
    }
    
    public void setMinmumWireFormatVersion(final int minmumWireFormatVersion) {
        this.minmumWireFormatVersion = minmumWireFormatVersion;
    }
    
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    public String getLogWriterName() {
        return this.logWriterName;
    }
    
    public void setLogWriterName(final String logFormat) {
        this.logWriterName = logFormat;
    }
    
    public boolean isDynamicManagement() {
        return this.dynamicManagement;
    }
    
    public void setDynamicManagement(final boolean useJmx) {
        this.dynamicManagement = useJmx;
    }
    
    public boolean isStartLogging() {
        return this.startLogging;
    }
    
    public void setStartLogging(final boolean startLogging) {
        this.startLogging = startLogging;
    }
    
    public int getBacklog() {
        return this.backlog;
    }
    
    public void setBacklog(final int backlog) {
        this.backlog = backlog;
    }
    
    public boolean isUseQueueForAccept() {
        return this.useQueueForAccept;
    }
    
    public void setUseQueueForAccept(final boolean useQueueForAccept) {
        this.useQueueForAccept = useQueueForAccept;
    }
    
    @Override
    public void run() {
        while (!this.isStopped()) {
            Socket socket = null;
            try {
                socket = this.serverSocket.accept();
                if (socket == null) {
                    continue;
                }
                if (this.isStopped() || this.getAcceptListener() == null) {
                    socket.close();
                }
                else if (this.useQueueForAccept) {
                    this.socketQueue.put(socket);
                }
                else {
                    this.handleSocket(socket);
                }
            }
            catch (SocketTimeoutException ex) {}
            catch (Exception e) {
                if (!this.isStopping()) {
                    this.onAcceptError(e);
                }
                else {
                    if (this.isStopped()) {
                        continue;
                    }
                    TcpTransportServer.LOG.warn("run()", e);
                    this.onAcceptError(e);
                }
            }
        }
    }
    
    protected Transport createTransport(final Socket socket, final WireFormat format) throws IOException {
        return new TcpTransport(format, socket);
    }
    
    @Override
    public String toString() {
        return "" + this.getBindLocation();
    }
    
    protected String resolveHostName(final ServerSocket socket, final InetAddress bindAddress) throws UnknownHostException {
        String result = null;
        if (socket.isBound()) {
            if (socket.getInetAddress().isAnyLocalAddress()) {
                result = InetAddressUtil.getLocalHostName();
            }
            else {
                result = socket.getInetAddress().getCanonicalHostName();
            }
        }
        else {
            result = bindAddress.getCanonicalHostName();
        }
        return result;
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this.useQueueForAccept) {
            final Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!TcpTransportServer.this.isStopped() && !TcpTransportServer.this.isStopping()) {
                            final Socket sock = TcpTransportServer.this.socketQueue.poll(1L, TimeUnit.SECONDS);
                            if (sock != null) {
                                try {
                                    TcpTransportServer.this.handleSocket(sock);
                                }
                                catch (Throwable thrown) {
                                    if (!TcpTransportServer.this.isStopping()) {
                                        TransportServerSupport.this.onAcceptError(new Exception(thrown));
                                    }
                                    else {
                                        if (TcpTransportServer.this.isStopped()) {
                                            continue;
                                        }
                                        TcpTransportServer.LOG.warn("Unexpected error thrown during accept handling: ", thrown);
                                        TransportServerSupport.this.onAcceptError(new Exception(thrown));
                                    }
                                }
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        TcpTransportServer.LOG.info("socketQueue interuppted - stopping");
                        if (!TcpTransportServer.this.isStopping()) {
                            TransportServerSupport.this.onAcceptError(e);
                        }
                    }
                }
            };
            (this.socketHandlerThread = new Thread(null, run, "ActiveMQ Transport Server Thread Handler: " + this.toString(), this.getStackSize())).setDaemon(true);
            this.socketHandlerThread.setPriority(8);
            this.socketHandlerThread.start();
        }
        super.doStart();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (this.serverSocket != null) {
            this.serverSocket.close();
            this.serverSocket = null;
        }
        super.doStop(stopper);
    }
    
    @Override
    public InetSocketAddress getSocketAddress() {
        return (InetSocketAddress)this.serverSocket.getLocalSocketAddress();
    }
    
    protected final void handleSocket(final Socket socket) {
        boolean closeSocket = true;
        try {
            if (this.currentTransportCount.get() >= this.maximumConnections) {
                throw new ExceededMaximumConnectionsException("Exceeded the maximum number of allowed client connections. See the 'maximumConnections' property on the TCP transport configuration URI in the ActiveMQ configuration file (e.g., activemq.xml)");
            }
            final HashMap<String, Object> options = new HashMap<String, Object>();
            options.put("maxInactivityDuration", this.maxInactivityDuration);
            options.put("maxInactivityDurationInitalDelay", this.maxInactivityDurationInitalDelay);
            options.put("minmumWireFormatVersion", this.minmumWireFormatVersion);
            options.put("trace", this.trace);
            options.put("soTimeout", this.soTimeout);
            options.put("socketBufferSize", this.socketBufferSize);
            options.put("connectionTimeout", this.connectionTimeout);
            options.put("logWriterName", this.logWriterName);
            options.put("dynamicManagement", this.dynamicManagement);
            options.put("startLogging", this.startLogging);
            options.putAll(this.transportOptions);
            final WireFormat format = this.wireFormatFactory.createWireFormat();
            final Transport transport = this.createTransport(socket, format);
            closeSocket = false;
            if (transport instanceof ServiceSupport) {
                ((ServiceSupport)transport).addServiceListener(this);
            }
            final Transport configuredTransport = this.transportFactory.serverConfigure(transport, format, options);
            this.getAcceptListener().onAccept(configuredTransport);
            this.currentTransportCount.incrementAndGet();
        }
        catch (SocketTimeoutException ex) {}
        catch (Exception e) {
            if (closeSocket) {
                try {
                    socket.close();
                }
                catch (Exception ex2) {}
            }
            if (!this.isStopping()) {
                this.onAcceptError(e);
            }
            else if (!this.isStopped()) {
                TcpTransportServer.LOG.warn("run()", e);
                this.onAcceptError(e);
            }
        }
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }
    
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    public int getSocketBufferSize() {
        return this.socketBufferSize;
    }
    
    public void setSocketBufferSize(final int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }
    
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getMaximumConnections() {
        return this.maximumConnections;
    }
    
    public void setMaximumConnections(final int maximumConnections) {
        this.maximumConnections = maximumConnections;
    }
    
    @Override
    public void started(final Service service) {
    }
    
    @Override
    public void stopped(final Service service) {
        this.currentTransportCount.decrementAndGet();
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
        LOG = LoggerFactory.getLogger(TcpTransportServer.class);
    }
}
