// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import org.slf4j.LoggerFactory;
import java.net.URI;
import java.io.IOException;
import org.slf4j.Logger;
import org.apache.activemq.util.ServiceSupport;

public abstract class TransportSupport extends ServiceSupport implements Transport
{
    private static final Logger LOG;
    TransportListener transportListener;
    
    @Override
    public TransportListener getTransportListener() {
        return this.transportListener;
    }
    
    @Override
    public void setTransportListener(final TransportListener commandListener) {
        this.transportListener = commandListener;
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        final boolean assignableFrom = target.isAssignableFrom(this.getClass());
        if (assignableFrom) {
            return target.cast(this);
        }
        return null;
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        throw new AssertionError((Object)"Unsupported Method");
    }
    
    public void doConsume(final Object command) {
        if (command != null) {
            if (this.transportListener != null) {
                this.transportListener.onCommand(command);
            }
            else {
                TransportSupport.LOG.error("No transportListener available to process inbound command: " + command);
            }
        }
    }
    
    public void onException(final IOException e) {
        if (this.transportListener != null) {
            try {
                this.transportListener.onException(e);
            }
            catch (RuntimeException e2) {
                TransportSupport.LOG.debug("Unexpected runtime exception: " + e2, e2);
            }
        }
    }
    
    protected void checkStarted() throws IOException {
        if (!this.isStarted()) {
            throw new IOException("The transport is not running.");
        }
    }
    
    @Override
    public boolean isFaultTolerant() {
        return false;
    }
    
    @Override
    public void reconnect(final URI uri) throws IOException {
        throw new IOException("Not supported");
    }
    
    @Override
    public boolean isReconnectSupported() {
        return false;
    }
    
    @Override
    public boolean isUpdateURIsSupported() {
        return false;
    }
    
    @Override
    public void updateURIs(final boolean reblance, final URI[] uris) throws IOException {
        throw new IOException("Not supported");
    }
    
    @Override
    public boolean isDisposed() {
        return this.isStopped();
    }
    
    @Override
    public boolean isConnected() {
        return this.isStarted();
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransportSupport.class);
    }
}
