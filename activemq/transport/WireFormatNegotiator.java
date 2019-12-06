// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import org.apache.activemq.util.IOExceptionSupport;
import java.net.Socket;
import org.apache.activemq.command.Command;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.WireFormatInfo;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.openwire.OpenWireFormat;
import org.slf4j.Logger;

public class WireFormatNegotiator extends TransportFilter
{
    private static final Logger LOG;
    private OpenWireFormat wireFormat;
    private final int minimumVersion;
    private long negotiateTimeout;
    private final AtomicBoolean firstStart;
    private final CountDownLatch readyCountDownLatch;
    private final CountDownLatch wireInfoSentDownLatch;
    
    public WireFormatNegotiator(final Transport next, final OpenWireFormat wireFormat, int minimumVersion) {
        super(next);
        this.negotiateTimeout = 15000L;
        this.firstStart = new AtomicBoolean(true);
        this.readyCountDownLatch = new CountDownLatch(1);
        this.wireInfoSentDownLatch = new CountDownLatch(1);
        this.wireFormat = wireFormat;
        if (minimumVersion <= 0) {
            minimumVersion = 1;
        }
        this.minimumVersion = minimumVersion;
        try {
            if (wireFormat.getPreferedWireFormatInfo() != null) {
                this.setNegotiateTimeout(wireFormat.getPreferedWireFormatInfo().getMaxInactivityDurationInitalDelay());
            }
        }
        catch (IOException ex) {}
    }
    
    @Override
    public void start() throws Exception {
        super.start();
        if (this.firstStart.compareAndSet(true, false)) {
            this.sendWireFormat();
        }
    }
    
    public void sendWireFormat() throws IOException {
        try {
            final WireFormatInfo info = this.wireFormat.getPreferedWireFormatInfo();
            if (WireFormatNegotiator.LOG.isDebugEnabled()) {
                WireFormatNegotiator.LOG.debug("Sending: " + info);
            }
            this.sendWireFormat(info);
        }
        finally {
            this.wireInfoSentDownLatch.countDown();
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        this.readyCountDownLatch.countDown();
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        try {
            if (!this.readyCountDownLatch.await(this.negotiateTimeout, TimeUnit.MILLISECONDS)) {
                throw new IOException("Wire format negotiation timeout: peer did not send his wire format.");
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedIOException();
        }
        super.oneway(command);
    }
    
    @Override
    public void onCommand(final Object o) {
        final Command command = (Command)o;
        if (command.isWireFormatInfo()) {
            final WireFormatInfo info = (WireFormatInfo)command;
            this.negociate(info);
        }
        this.getTransportListener().onCommand(command);
    }
    
    public void negociate(final WireFormatInfo info) {
        if (WireFormatNegotiator.LOG.isDebugEnabled()) {
            WireFormatNegotiator.LOG.debug("Received WireFormat: " + info);
        }
        try {
            this.wireInfoSentDownLatch.await();
            if (WireFormatNegotiator.LOG.isDebugEnabled()) {
                WireFormatNegotiator.LOG.debug(this + " before negotiation: " + this.wireFormat);
            }
            if (!info.isValid()) {
                this.onException(new IOException("Remote wire format magic is invalid"));
            }
            else if (info.getVersion() < this.minimumVersion) {
                this.onException(new IOException("Remote wire format (" + info.getVersion() + ") is lower the minimum version required (" + this.minimumVersion + ")"));
            }
            this.wireFormat.renegotiateWireFormat(info);
            final Socket socket = this.next.narrow(Socket.class);
            if (socket != null) {
                socket.setTcpNoDelay(this.wireFormat.isTcpNoDelayEnabled());
            }
            if (WireFormatNegotiator.LOG.isDebugEnabled()) {
                WireFormatNegotiator.LOG.debug(this + " after negotiation: " + this.wireFormat);
            }
        }
        catch (IOException e) {
            this.onException(e);
        }
        catch (InterruptedException e2) {
            this.onException((IOException)new InterruptedIOException().initCause(e2));
        }
        catch (Exception e3) {
            this.onException(IOExceptionSupport.create(e3));
        }
        this.readyCountDownLatch.countDown();
        this.onWireFormatNegotiated(info);
    }
    
    @Override
    public void onException(final IOException error) {
        this.readyCountDownLatch.countDown();
        super.onException(error);
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    protected void sendWireFormat(final WireFormatInfo info) throws IOException {
        this.next.oneway(info);
    }
    
    protected void onWireFormatNegotiated(final WireFormatInfo info) {
    }
    
    public long getNegotiateTimeout() {
        return this.negotiateTimeout;
    }
    
    public void setNegotiateTimeout(final long negotiateTimeout) {
        this.negotiateTimeout = negotiateTimeout;
    }
    
    static {
        LOG = LoggerFactory.getLogger(WireFormatNegotiator.class);
    }
}
