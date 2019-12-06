// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.multicast;

import org.slf4j.LoggerFactory;
import org.apache.activemq.transport.udp.DatagramHeaderMarshaller;
import java.net.InetSocketAddress;
import org.apache.activemq.transport.udp.CommandDatagramSocket;
import org.apache.activemq.transport.udp.CommandChannel;
import org.apache.activemq.util.ServiceStopper;
import java.net.SocketException;
import java.net.SocketAddress;
import java.net.DatagramSocket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;
import org.apache.activemq.openwire.OpenWireFormat;
import java.net.InetAddress;
import java.net.MulticastSocket;
import org.slf4j.Logger;
import org.apache.activemq.transport.udp.UdpTransport;

public class MulticastTransport extends UdpTransport
{
    private static final Logger LOG;
    private static final int DEFAULT_IDLE_TIME = 5000;
    private MulticastSocket socket;
    private InetAddress mcastAddress;
    private int mcastPort;
    private int timeToLive;
    private boolean loopBackMode;
    private long keepAliveInterval;
    
    public MulticastTransport(final OpenWireFormat wireFormat, final URI remoteLocation) throws UnknownHostException, IOException {
        super(wireFormat, remoteLocation);
        this.timeToLive = 1;
        this.keepAliveInterval = 5000L;
    }
    
    public long getKeepAliveInterval() {
        return this.keepAliveInterval;
    }
    
    public void setKeepAliveInterval(final long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public boolean isLoopBackMode() {
        return this.loopBackMode;
    }
    
    public void setLoopBackMode(final boolean loopBackMode) {
        this.loopBackMode = loopBackMode;
    }
    
    public int getTimeToLive() {
        return this.timeToLive;
    }
    
    public void setTimeToLive(final int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    @Override
    protected String getProtocolName() {
        return "Multicast";
    }
    
    @Override
    protected String getProtocolUriScheme() {
        return "multicast://";
    }
    
    @Override
    protected void bind(final DatagramSocket socket, final SocketAddress localAddress) throws SocketException {
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        super.doStop(stopper);
        if (this.socket != null) {
            try {
                this.socket.leaveGroup(this.getMulticastAddress());
            }
            catch (IOException e) {
                stopper.onException(this, e);
            }
            this.socket.close();
        }
    }
    
    @Override
    protected CommandChannel createCommandChannel() throws IOException {
        (this.socket = new MulticastSocket(this.mcastPort)).setLoopbackMode(this.loopBackMode);
        this.socket.setTimeToLive(this.timeToLive);
        MulticastTransport.LOG.debug("Joining multicast address: " + this.getMulticastAddress());
        this.socket.joinGroup(this.getMulticastAddress());
        this.socket.setSoTimeout((int)this.keepAliveInterval);
        return new CommandDatagramSocket(this, this.getWireFormat(), this.getDatagramSize(), this.getTargetAddress(), this.createDatagramHeaderMarshaller(), this.getSocket());
    }
    
    protected InetAddress getMulticastAddress() {
        return this.mcastAddress;
    }
    
    protected MulticastSocket getSocket() {
        return this.socket;
    }
    
    protected void setSocket(final MulticastSocket socket) {
        this.socket = socket;
    }
    
    @Override
    protected InetSocketAddress createAddress(final URI remoteLocation) throws UnknownHostException, IOException {
        this.mcastAddress = InetAddress.getByName(remoteLocation.getHost());
        this.mcastPort = remoteLocation.getPort();
        return new InetSocketAddress(this.mcastAddress, this.mcastPort);
    }
    
    @Override
    protected DatagramHeaderMarshaller createDatagramHeaderMarshaller() {
        return new MulticastDatagramHeaderMarshaller("udp://dummyHostName:" + this.getPort());
    }
    
    static {
        LOG = LoggerFactory.getLogger(MulticastTransport.class);
    }
}
