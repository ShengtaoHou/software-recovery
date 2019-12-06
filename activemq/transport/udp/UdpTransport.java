// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.udp;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.ServiceStopper;
import java.net.BindException;
import java.net.DatagramSocket;
import org.apache.activemq.util.InetAddressUtil;
import java.net.InetSocketAddress;
import org.apache.activemq.command.Endpoint;
import java.io.EOFException;
import java.net.SocketException;
import java.nio.channels.AsynchronousCloseException;
import org.apache.activemq.command.Command;
import org.apache.activemq.transport.reliable.Replayer;
import java.net.UnknownHostException;
import java.net.URI;
import java.io.IOException;
import org.apache.activemq.transport.reliable.ExceptionIfDroppedReplayStrategy;
import org.apache.activemq.util.IntSequenceGenerator;
import java.nio.channels.DatagramChannel;
import java.net.SocketAddress;
import org.apache.activemq.transport.reliable.ReplayBuffer;
import org.apache.activemq.transport.reliable.ReplayStrategy;
import org.apache.activemq.openwire.OpenWireFormat;
import org.slf4j.Logger;
import org.apache.activemq.Service;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportThreadSupport;

public class UdpTransport extends TransportThreadSupport implements Transport, Service, Runnable
{
    private static final Logger LOG;
    private static final int MAX_BIND_ATTEMPTS = 50;
    private static final long BIND_ATTEMPT_DELAY = 100L;
    private CommandChannel commandChannel;
    private OpenWireFormat wireFormat;
    private ByteBufferPool bufferPool;
    private ReplayStrategy replayStrategy;
    private ReplayBuffer replayBuffer;
    private int datagramSize;
    private SocketAddress targetAddress;
    private SocketAddress originalTargetAddress;
    private DatagramChannel channel;
    private boolean trace;
    private boolean useLocalHost;
    private int port;
    private int minmumWireFormatVersion;
    private String description;
    private IntSequenceGenerator sequenceGenerator;
    private boolean replayEnabled;
    
    protected UdpTransport(final OpenWireFormat wireFormat) throws IOException {
        this.replayStrategy = new ExceptionIfDroppedReplayStrategy();
        this.datagramSize = 4096;
        this.useLocalHost = false;
        this.replayEnabled = true;
        this.wireFormat = wireFormat;
    }
    
    public UdpTransport(final OpenWireFormat wireFormat, final URI remoteLocation) throws UnknownHostException, IOException {
        this(wireFormat);
        this.targetAddress = this.createAddress(remoteLocation);
        this.description = remoteLocation.toString() + "@";
    }
    
    public UdpTransport(final OpenWireFormat wireFormat, final SocketAddress socketAddress) throws IOException {
        this(wireFormat);
        this.targetAddress = socketAddress;
        this.description = this.getProtocolName() + "ServerConnection@";
    }
    
    public UdpTransport(final OpenWireFormat wireFormat, final int port) throws UnknownHostException, IOException {
        this(wireFormat);
        this.port = port;
        this.targetAddress = null;
        this.description = this.getProtocolName() + "Server@";
    }
    
    public Replayer createReplayer() throws IOException {
        if (this.replayEnabled) {
            return this.getCommandChannel();
        }
        return null;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.oneway(command, this.targetAddress);
    }
    
    public void oneway(final Object command, final SocketAddress address) throws IOException {
        if (UdpTransport.LOG.isDebugEnabled()) {
            UdpTransport.LOG.debug("Sending oneway from: " + this + " to target: " + this.targetAddress + " command: " + command);
        }
        this.checkStarted();
        this.commandChannel.write((Command)command, address);
    }
    
    @Override
    public String toString() {
        if (this.description != null) {
            return this.description + this.port;
        }
        return this.getProtocolUriScheme() + this.targetAddress + "@" + this.port;
    }
    
    @Override
    public void run() {
        UdpTransport.LOG.trace("Consumer thread starting for: " + this.toString());
        while (!this.isStopped()) {
            try {
                final Command command = this.commandChannel.read();
                this.doConsume(command);
            }
            catch (AsynchronousCloseException e6) {
                try {
                    this.stop();
                }
                catch (Exception e2) {
                    UdpTransport.LOG.warn("Caught in: " + this + " while closing: " + e2 + ". Now Closed", e2);
                }
            }
            catch (SocketException e3) {
                UdpTransport.LOG.debug("Socket closed: " + e3, e3);
                try {
                    this.stop();
                }
                catch (Exception e2) {
                    UdpTransport.LOG.warn("Caught in: " + this + " while closing: " + e2 + ". Now Closed", e2);
                }
            }
            catch (EOFException e4) {
                UdpTransport.LOG.debug("Socket closed: " + e4, e4);
                try {
                    this.stop();
                }
                catch (Exception e2) {
                    UdpTransport.LOG.warn("Caught in: " + this + " while closing: " + e2 + ". Now Closed", e2);
                }
            }
            catch (Exception e5) {
                try {
                    this.stop();
                }
                catch (Exception e2) {
                    UdpTransport.LOG.warn("Caught in: " + this + " while closing: " + e2 + ". Now Closed", e2);
                }
                if (e5 instanceof IOException) {
                    this.onException((IOException)e5);
                }
                else {
                    UdpTransport.LOG.error("Caught: " + e5, e5);
                    e5.printStackTrace();
                }
            }
        }
    }
    
    public void setTargetEndpoint(final Endpoint newTarget) {
        if (newTarget instanceof DatagramEndpoint) {
            final DatagramEndpoint endpoint = (DatagramEndpoint)newTarget;
            final SocketAddress address = endpoint.getAddress();
            if (address != null) {
                if (this.originalTargetAddress == null) {
                    this.originalTargetAddress = this.targetAddress;
                }
                this.targetAddress = address;
                this.commandChannel.setTargetAddress(address);
            }
        }
    }
    
    public boolean isTrace() {
        return this.trace;
    }
    
    public void setTrace(final boolean trace) {
        this.trace = trace;
    }
    
    public int getDatagramSize() {
        return this.datagramSize;
    }
    
    public void setDatagramSize(final int datagramSize) {
        this.datagramSize = datagramSize;
    }
    
    public boolean isUseLocalHost() {
        return this.useLocalHost;
    }
    
    public void setUseLocalHost(final boolean useLocalHost) {
        this.useLocalHost = useLocalHost;
    }
    
    public CommandChannel getCommandChannel() throws IOException {
        if (this.commandChannel == null) {
            this.commandChannel = this.createCommandChannel();
        }
        return this.commandChannel;
    }
    
    public void setCommandChannel(final CommandDatagramChannel commandChannel) {
        this.commandChannel = commandChannel;
    }
    
    public ReplayStrategy getReplayStrategy() {
        return this.replayStrategy;
    }
    
    public void setReplayStrategy(final ReplayStrategy replayStrategy) {
        this.replayStrategy = replayStrategy;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getMinmumWireFormatVersion() {
        return this.minmumWireFormatVersion;
    }
    
    public void setMinmumWireFormatVersion(final int minmumWireFormatVersion) {
        this.minmumWireFormatVersion = minmumWireFormatVersion;
    }
    
    public OpenWireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public IntSequenceGenerator getSequenceGenerator() {
        if (this.sequenceGenerator == null) {
            this.sequenceGenerator = new IntSequenceGenerator();
        }
        return this.sequenceGenerator;
    }
    
    public void setSequenceGenerator(final IntSequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }
    
    public boolean isReplayEnabled() {
        return this.replayEnabled;
    }
    
    public void setReplayEnabled(final boolean replayEnabled) {
        this.replayEnabled = replayEnabled;
    }
    
    public ByteBufferPool getBufferPool() {
        if (this.bufferPool == null) {
            this.bufferPool = new DefaultBufferPool();
        }
        return this.bufferPool;
    }
    
    public void setBufferPool(final ByteBufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }
    
    public ReplayBuffer getReplayBuffer() {
        return this.replayBuffer;
    }
    
    public void setReplayBuffer(final ReplayBuffer replayBuffer) throws IOException {
        this.replayBuffer = replayBuffer;
        this.getCommandChannel().setReplayBuffer(replayBuffer);
    }
    
    protected InetSocketAddress createAddress(final URI remoteLocation) throws UnknownHostException, IOException {
        final String host = this.resolveHostName(remoteLocation.getHost());
        return new InetSocketAddress(host, remoteLocation.getPort());
    }
    
    protected String resolveHostName(final String host) throws UnknownHostException {
        final String localName = InetAddressUtil.getLocalHostName();
        if (localName != null && this.isUseLocalHost() && localName.equals(host)) {
            return "localhost";
        }
        return host;
    }
    
    @Override
    protected void doStart() throws Exception {
        this.getCommandChannel().start();
        super.doStart();
    }
    
    protected CommandChannel createCommandChannel() throws IOException {
        final SocketAddress localAddress = this.createLocalAddress();
        this.channel = DatagramChannel.open();
        this.channel = this.connect(this.channel, this.targetAddress);
        final DatagramSocket socket = this.channel.socket();
        this.bind(socket, localAddress);
        if (this.port == 0) {
            this.port = socket.getLocalPort();
        }
        return this.createCommandDatagramChannel();
    }
    
    protected CommandChannel createCommandDatagramChannel() {
        return new CommandDatagramChannel(this, this.getWireFormat(), this.getDatagramSize(), this.getTargetAddress(), this.createDatagramHeaderMarshaller(), this.getChannel(), this.getBufferPool());
    }
    
    protected void bind(final DatagramSocket socket, final SocketAddress localAddress) throws IOException {
        this.channel.configureBlocking(true);
        if (UdpTransport.LOG.isDebugEnabled()) {
            UdpTransport.LOG.debug("Binding to address: " + localAddress);
        }
        int i = 0;
        while (i < 50) {
            try {
                socket.bind(localAddress);
                return;
            }
            catch (BindException e) {
                if (i + 1 == 50) {
                    throw e;
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException e2) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                ++i;
                continue;
            }
            break;
        }
    }
    
    protected DatagramChannel connect(final DatagramChannel channel, final SocketAddress targetAddress2) throws IOException {
        return channel;
    }
    
    protected SocketAddress createLocalAddress() {
        return new InetSocketAddress(this.port);
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        if (this.channel != null) {
            this.channel.close();
        }
    }
    
    protected DatagramHeaderMarshaller createDatagramHeaderMarshaller() {
        return new DatagramHeaderMarshaller();
    }
    
    protected String getProtocolName() {
        return "Udp";
    }
    
    protected String getProtocolUriScheme() {
        return "udp://";
    }
    
    protected SocketAddress getTargetAddress() {
        return this.targetAddress;
    }
    
    protected DatagramChannel getChannel() {
        return this.channel;
    }
    
    protected void setChannel(final DatagramChannel channel) {
        this.channel = channel;
    }
    
    public InetSocketAddress getLocalSocketAddress() {
        if (this.channel == null) {
            return null;
        }
        return (InetSocketAddress)this.channel.socket().getLocalSocketAddress();
    }
    
    @Override
    public String getRemoteAddress() {
        if (this.targetAddress != null) {
            return "" + this.targetAddress;
        }
        return null;
    }
    
    @Override
    public int getReceiveCounter() {
        if (this.commandChannel == null) {
            return 0;
        }
        return this.commandChannel.getReceiveCounter();
    }
    
    static {
        LOG = LoggerFactory.getLogger(UdpTransport.class);
    }
}
