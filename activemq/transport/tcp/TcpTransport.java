// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.tcp;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.thread.TaskRunnerFactory;
import org.apache.activemq.util.ServiceStopper;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.HashMap;
import org.apache.activemq.util.InetAddressUtil;
import java.io.DataInput;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.io.DataOutput;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketException;
import org.apache.activemq.TransportLoggerSupport;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.SocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.apache.activemq.wireformat.WireFormat;
import java.net.URI;
import org.slf4j.Logger;
import org.apache.activemq.Service;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportThreadSupport;

public class TcpTransport extends TransportThreadSupport implements Transport, Service, Runnable
{
    private static final Logger LOG;
    protected final URI remoteLocation;
    protected final URI localLocation;
    protected final WireFormat wireFormat;
    protected int connectionTimeout;
    protected int soTimeout;
    protected int socketBufferSize;
    protected int ioBufferSize;
    protected boolean closeAsync;
    protected Socket socket;
    protected DataOutputStream dataOut;
    protected DataInputStream dataIn;
    protected TimeStampStream buffOut;
    protected int trafficClass;
    private boolean trafficClassSet;
    protected boolean diffServChosen;
    protected boolean typeOfServiceChosen;
    protected boolean trace;
    protected String logWriterName;
    protected boolean dynamicManagement;
    protected boolean startLogging;
    protected int jmxPort;
    protected boolean useLocalHost;
    protected int minmumWireFormatVersion;
    protected SocketFactory socketFactory;
    protected final AtomicReference<CountDownLatch> stoppedLatch;
    protected volatile int receiveCounter;
    private Map<String, Object> socketOptions;
    private int soLinger;
    private Boolean keepAlive;
    private Boolean tcpNoDelay;
    private Thread runnerThread;
    
    public TcpTransport(final WireFormat wireFormat, final SocketFactory socketFactory, final URI remoteLocation, final URI localLocation) throws UnknownHostException, IOException {
        this.connectionTimeout = 30000;
        this.socketBufferSize = 65536;
        this.ioBufferSize = 8192;
        this.closeAsync = true;
        this.buffOut = null;
        this.trafficClass = 0;
        this.trafficClassSet = false;
        this.diffServChosen = false;
        this.typeOfServiceChosen = false;
        this.trace = false;
        this.logWriterName = TransportLoggerSupport.defaultLogWriterName;
        this.dynamicManagement = false;
        this.startLogging = true;
        this.jmxPort = 1099;
        this.useLocalHost = false;
        this.stoppedLatch = new AtomicReference<CountDownLatch>();
        this.soLinger = Integer.MIN_VALUE;
        this.wireFormat = wireFormat;
        this.socketFactory = socketFactory;
        try {
            this.socket = socketFactory.createSocket();
        }
        catch (SocketException e) {
            this.socket = null;
        }
        this.remoteLocation = remoteLocation;
        this.localLocation = localLocation;
        this.setDaemon(false);
    }
    
    public TcpTransport(final WireFormat wireFormat, final Socket socket) throws IOException {
        this.connectionTimeout = 30000;
        this.socketBufferSize = 65536;
        this.ioBufferSize = 8192;
        this.closeAsync = true;
        this.buffOut = null;
        this.trafficClass = 0;
        this.trafficClassSet = false;
        this.diffServChosen = false;
        this.typeOfServiceChosen = false;
        this.trace = false;
        this.logWriterName = TransportLoggerSupport.defaultLogWriterName;
        this.dynamicManagement = false;
        this.startLogging = true;
        this.jmxPort = 1099;
        this.useLocalHost = false;
        this.stoppedLatch = new AtomicReference<CountDownLatch>();
        this.soLinger = Integer.MIN_VALUE;
        this.wireFormat = wireFormat;
        this.socket = socket;
        this.remoteLocation = null;
        this.localLocation = null;
        this.setDaemon(true);
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.checkStarted();
        this.wireFormat.marshal(command, this.dataOut);
        this.dataOut.flush();
    }
    
    @Override
    public String toString() {
        return "" + (this.socket.isConnected() ? ("tcp://" + this.socket.getInetAddress() + ":" + this.socket.getPort() + "@" + this.socket.getLocalPort()) : ((this.localLocation != null) ? this.localLocation : this.remoteLocation));
    }
    
    @Override
    public void run() {
        TcpTransport.LOG.trace("TCP consumer thread for " + this + " starting");
        this.runnerThread = Thread.currentThread();
        try {
            while (!this.isStopped()) {
                this.doRun();
            }
        }
        catch (IOException e) {
            this.stoppedLatch.get().countDown();
            this.onException(e);
        }
        catch (Throwable e2) {
            this.stoppedLatch.get().countDown();
            final IOException ioe = new IOException("Unexpected error occured: " + e2);
            ioe.initCause(e2);
            this.onException(ioe);
        }
        finally {
            this.stoppedLatch.get().countDown();
        }
    }
    
    protected void doRun() throws IOException {
        try {
            final Object command = this.readCommand();
            this.doConsume(command);
        }
        catch (SocketTimeoutException ex) {}
        catch (InterruptedIOException ex2) {}
    }
    
    protected Object readCommand() throws IOException {
        return this.wireFormat.unmarshal(this.dataIn);
    }
    
    public String getDiffServ() {
        return Integer.toString(this.trafficClass);
    }
    
    public void setDiffServ(final String diffServ) throws IllegalArgumentException {
        this.trafficClass = QualityOfServiceUtils.getDSCP(diffServ);
        this.diffServChosen = true;
    }
    
    public int getTypeOfService() {
        return this.trafficClass;
    }
    
    public void setTypeOfService(final int typeOfService) {
        this.trafficClass = QualityOfServiceUtils.getToS(typeOfService);
        this.typeOfServiceChosen = true;
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
    
    public int getJmxPort() {
        return this.jmxPort;
    }
    
    public void setJmxPort(final int jmxPort) {
        this.jmxPort = jmxPort;
    }
    
    public int getMinmumWireFormatVersion() {
        return this.minmumWireFormatVersion;
    }
    
    public void setMinmumWireFormatVersion(final int minmumWireFormatVersion) {
        this.minmumWireFormatVersion = minmumWireFormatVersion;
    }
    
    public boolean isUseLocalHost() {
        return this.useLocalHost;
    }
    
    public void setUseLocalHost(final boolean useLocalHost) {
        this.useLocalHost = useLocalHost;
    }
    
    public int getSocketBufferSize() {
        return this.socketBufferSize;
    }
    
    public void setSocketBufferSize(final int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }
    
    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }
    
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }
    
    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public Boolean getKeepAlive() {
        return this.keepAlive;
    }
    
    public void setKeepAlive(final Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
    
    public void setSoLinger(final int soLinger) {
        this.soLinger = soLinger;
    }
    
    public int getSoLinger() {
        return this.soLinger;
    }
    
    public Boolean getTcpNoDelay() {
        return this.tcpNoDelay;
    }
    
    public void setTcpNoDelay(final Boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }
    
    public int getIoBufferSize() {
        return this.ioBufferSize;
    }
    
    public void setIoBufferSize(final int ioBufferSize) {
        this.ioBufferSize = ioBufferSize;
    }
    
    public boolean isCloseAsync() {
        return this.closeAsync;
    }
    
    public void setCloseAsync(final boolean closeAsync) {
        this.closeAsync = closeAsync;
    }
    
    protected String resolveHostName(final String host) throws UnknownHostException {
        if (this.isUseLocalHost()) {
            final String localName = InetAddressUtil.getLocalHostName();
            if (localName != null && localName.equals(host)) {
                return "localhost";
            }
        }
        return host;
    }
    
    protected void initialiseSocket(final Socket sock) throws SocketException, IllegalArgumentException {
        if (this.socketOptions != null) {
            final Map<String, Object> copy = new HashMap<String, Object>(this.socketOptions);
            IntrospectionSupport.setProperties(this.socket, copy);
            if (!copy.isEmpty()) {
                throw new IllegalArgumentException("Invalid socket parameters: " + copy);
            }
        }
        try {
            sock.setReceiveBufferSize(this.socketBufferSize);
            sock.setSendBufferSize(this.socketBufferSize);
        }
        catch (SocketException se) {
            TcpTransport.LOG.warn("Cannot set socket buffer size = " + this.socketBufferSize);
            TcpTransport.LOG.debug("Cannot set socket buffer size. Reason: " + se.getMessage() + ". This exception is ignored.", se);
        }
        sock.setSoTimeout(this.soTimeout);
        if (this.keepAlive != null) {
            sock.setKeepAlive(this.keepAlive);
        }
        if (this.soLinger > -1) {
            sock.setSoLinger(true, this.soLinger);
        }
        else if (this.soLinger == -1) {
            sock.setSoLinger(false, 0);
        }
        if (this.tcpNoDelay != null) {
            sock.setTcpNoDelay(this.tcpNoDelay);
        }
        if (!this.trafficClassSet) {
            this.trafficClassSet = this.setTrafficClass(sock);
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this.connect();
        this.stoppedLatch.set(new CountDownLatch(1));
        super.doStart();
    }
    
    protected void connect() throws Exception {
        if (this.socket == null && this.socketFactory == null) {
            throw new IllegalStateException("Cannot connect if the socket or socketFactory have not been set");
        }
        InetSocketAddress localAddress = null;
        InetSocketAddress remoteAddress = null;
        if (this.localLocation != null) {
            localAddress = new InetSocketAddress(InetAddress.getByName(this.localLocation.getHost()), this.localLocation.getPort());
        }
        if (this.remoteLocation != null) {
            final String host = this.resolveHostName(this.remoteLocation.getHost());
            remoteAddress = new InetSocketAddress(host, this.remoteLocation.getPort());
        }
        this.trafficClassSet = this.setTrafficClass(this.socket);
        if (this.socket != null) {
            if (localAddress != null) {
                this.socket.bind(localAddress);
            }
            if (remoteAddress != null) {
                if (this.connectionTimeout >= 0) {
                    this.socket.connect(remoteAddress, this.connectionTimeout);
                }
                else {
                    this.socket.connect(remoteAddress);
                }
            }
        }
        else if (localAddress != null) {
            this.socket = this.socketFactory.createSocket(remoteAddress.getAddress(), remoteAddress.getPort(), localAddress.getAddress(), localAddress.getPort());
        }
        else {
            this.socket = this.socketFactory.createSocket(remoteAddress.getAddress(), remoteAddress.getPort());
        }
        this.initialiseSocket(this.socket);
        this.initializeStreams();
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (TcpTransport.LOG.isDebugEnabled()) {
            TcpTransport.LOG.debug("Stopping transport " + this);
        }
        if (this.socket != null) {
            if (this.closeAsync) {
                final CountDownLatch latch = new CountDownLatch(1);
                final TaskRunnerFactory taskRunnerFactory = new TaskRunnerFactory();
                taskRunnerFactory.execute(new Runnable() {
                    @Override
                    public void run() {
                        TcpTransport.LOG.trace("Closing socket {}", TcpTransport.this.socket);
                        try {
                            TcpTransport.this.socket.close();
                            TcpTransport.LOG.debug("Closed socket {}", TcpTransport.this.socket);
                        }
                        catch (IOException e) {
                            if (TcpTransport.LOG.isDebugEnabled()) {
                                TcpTransport.LOG.debug("Caught exception closing socket " + TcpTransport.this.socket + ". This exception will be ignored.", e);
                            }
                        }
                        finally {
                            latch.countDown();
                        }
                    }
                });
                try {
                    latch.await(1L, TimeUnit.SECONDS);
                }
                catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                }
                finally {
                    taskRunnerFactory.shutdownNow();
                }
            }
            else {
                TcpTransport.LOG.trace("Closing socket {}", this.socket);
                try {
                    this.socket.close();
                    TcpTransport.LOG.debug("Closed socket {}", this.socket);
                }
                catch (IOException e) {
                    if (TcpTransport.LOG.isDebugEnabled()) {
                        TcpTransport.LOG.debug("Caught exception closing socket " + this.socket + ". This exception will be ignored.", e);
                    }
                }
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        final CountDownLatch countDownLatch = this.stoppedLatch.get();
        if (countDownLatch != null && Thread.currentThread() != this.runnerThread) {
            countDownLatch.await(1L, TimeUnit.SECONDS);
        }
    }
    
    protected void initializeStreams() throws Exception {
        final TcpBufferedInputStream buffIn = new TcpBufferedInputStream(this.socket.getInputStream(), this.ioBufferSize) {
            @Override
            public int read() throws IOException {
                final TcpTransport this$0 = TcpTransport.this;
                ++this$0.receiveCounter;
                return super.read();
            }
            
            @Override
            public int read(final byte[] b, final int off, final int len) throws IOException {
                final TcpTransport this$0 = TcpTransport.this;
                ++this$0.receiveCounter;
                return super.read(b, off, len);
            }
            
            @Override
            public long skip(final long n) throws IOException {
                final TcpTransport this$0 = TcpTransport.this;
                ++this$0.receiveCounter;
                return super.skip(n);
            }
            
            @Override
            protected void fill() throws IOException {
                final TcpTransport this$0 = TcpTransport.this;
                ++this$0.receiveCounter;
                super.fill();
            }
        };
        this.dataIn = new DataInputStream(buffIn);
        final TcpBufferedOutputStream outputStream = new TcpBufferedOutputStream(this.socket.getOutputStream(), this.ioBufferSize);
        this.dataOut = new DataOutputStream(outputStream);
        this.buffOut = outputStream;
    }
    
    protected void closeStreams() throws IOException {
        if (this.dataOut != null) {
            this.dataOut.close();
        }
        if (this.dataIn != null) {
            this.dataIn.close();
        }
    }
    
    public void setSocketOptions(final Map<String, Object> socketOptions) {
        this.socketOptions = new HashMap<String, Object>(socketOptions);
    }
    
    @Override
    public String getRemoteAddress() {
        if (this.socket == null) {
            return null;
        }
        final SocketAddress address = this.socket.getRemoteSocketAddress();
        if (address instanceof InetSocketAddress) {
            return "tcp://" + ((InetSocketAddress)address).getAddress().getHostAddress() + ":" + ((InetSocketAddress)address).getPort();
        }
        return "" + this.socket.getRemoteSocketAddress();
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        if (target == Socket.class) {
            return target.cast(this.socket);
        }
        if (target == TimeStampStream.class) {
            return target.cast(this.buffOut);
        }
        return super.narrow(target);
    }
    
    @Override
    public int getReceiveCounter() {
        return this.receiveCounter;
    }
    
    private boolean setTrafficClass(final Socket sock) throws SocketException, IllegalArgumentException {
        if (sock == null || (!this.diffServChosen && !this.typeOfServiceChosen)) {
            return false;
        }
        if (this.diffServChosen && this.typeOfServiceChosen) {
            throw new IllegalArgumentException("Cannot set both the  Differentiated Services and Type of Services transport  options on the same connection.");
        }
        sock.setTrafficClass(this.trafficClass);
        final int resultTrafficClass = sock.getTrafficClass();
        if (this.trafficClass != resultTrafficClass) {
            if (this.trafficClass >> 2 == resultTrafficClass >> 2 && (this.trafficClass & 0x3) != (resultTrafficClass & 0x3)) {
                TcpTransport.LOG.warn("Attempted to set the Traffic Class to " + this.trafficClass + " but the result Traffic Class was " + resultTrafficClass + ". Please check that your system allows you to set the ECN bits (the first two bits).");
            }
            else {
                TcpTransport.LOG.warn("Attempted to set the Traffic Class to " + this.trafficClass + " but the result Traffic Class was " + resultTrafficClass + ". Please check that your system supports java.net.setTrafficClass.");
            }
            return false;
        }
        this.diffServChosen = false;
        this.typeOfServiceChosen = false;
        return true;
    }
    
    public WireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    static {
        LOG = LoggerFactory.getLogger(TcpTransport.class);
    }
}
