// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.net.URI;
import java.io.IOException;

public class TransportFilter implements TransportListener, Transport
{
    protected final Transport next;
    protected TransportListener transportListener;
    
    public TransportFilter(final Transport next) {
        this.next = next;
    }
    
    @Override
    public TransportListener getTransportListener() {
        return this.transportListener;
    }
    
    @Override
    public void setTransportListener(final TransportListener channelListener) {
        this.transportListener = channelListener;
        if (channelListener == null) {
            this.next.setTransportListener(null);
        }
        else {
            this.next.setTransportListener(this);
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.next == null) {
            throw new IOException("The next channel has not been set.");
        }
        if (this.transportListener == null) {
            throw new IOException("The command listener has not been set.");
        }
        this.next.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.next.stop();
    }
    
    @Override
    public void onCommand(final Object command) {
        this.transportListener.onCommand(command);
    }
    
    public Transport getNext() {
        return this.next;
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.next.oneway(command);
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        return this.next.asyncRequest(command, null);
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        return this.next.request(command);
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        return this.next.request(command, timeout);
    }
    
    @Override
    public void onException(final IOException error) {
        this.transportListener.onException(error);
    }
    
    @Override
    public void transportInterupted() {
        this.transportListener.transportInterupted();
    }
    
    @Override
    public void transportResumed() {
        this.transportListener.transportResumed();
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return target.cast(this);
        }
        return this.next.narrow(target);
    }
    
    @Override
    public String getRemoteAddress() {
        return this.next.getRemoteAddress();
    }
    
    @Override
    public boolean isFaultTolerant() {
        return this.next.isFaultTolerant();
    }
    
    @Override
    public boolean isDisposed() {
        return this.next.isDisposed();
    }
    
    @Override
    public boolean isConnected() {
        return this.next.isConnected();
    }
    
    @Override
    public void reconnect(final URI uri) throws IOException {
        this.next.reconnect(uri);
    }
    
    @Override
    public int getReceiveCounter() {
        return this.next.getReceiveCounter();
    }
    
    @Override
    public boolean isReconnectSupported() {
        return this.next.isReconnectSupported();
    }
    
    @Override
    public boolean isUpdateURIsSupported() {
        return this.next.isUpdateURIsSupported();
    }
    
    @Override
    public void updateURIs(final boolean rebalance, final URI[] uris) throws IOException {
        this.next.updateURIs(rebalance, uris);
    }
}
