// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mock;

import java.net.URI;
import org.apache.activemq.transport.TransportFilter;
import org.apache.activemq.transport.FutureResponse;
import org.apache.activemq.transport.ResponseCallback;
import java.io.IOException;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.DefaultTransportListener;

public class MockTransport extends DefaultTransportListener implements Transport
{
    protected Transport next;
    protected TransportListener transportListener;
    
    public MockTransport(final Transport next) {
        this.next = next;
    }
    
    @Override
    public synchronized void setTransportListener(final TransportListener channelListener) {
        this.transportListener = channelListener;
        if (channelListener == null) {
            this.getNext().setTransportListener(null);
        }
        else {
            this.getNext().setTransportListener(this);
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.getNext() == null) {
            throw new IOException("The next channel has not been set.");
        }
        if (this.transportListener == null) {
            throw new IOException("The command listener has not been set.");
        }
        this.getNext().start();
    }
    
    @Override
    public void stop() throws Exception {
        this.getNext().stop();
    }
    
    @Override
    public void onCommand(final Object command) {
        this.getTransportListener().onCommand(command);
    }
    
    public synchronized Transport getNext() {
        return this.next;
    }
    
    @Override
    public synchronized TransportListener getTransportListener() {
        return this.transportListener;
    }
    
    @Override
    public String toString() {
        return this.getNext().toString();
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.getNext().oneway(command);
    }
    
    @Override
    public FutureResponse asyncRequest(final Object command, final ResponseCallback responseCallback) throws IOException {
        return this.getNext().asyncRequest(command, null);
    }
    
    @Override
    public Object request(final Object command) throws IOException {
        return this.getNext().request(command);
    }
    
    @Override
    public Object request(final Object command, final int timeout) throws IOException {
        return this.getNext().request(command, timeout);
    }
    
    @Override
    public void onException(final IOException error) {
        this.getTransportListener().onException(error);
    }
    
    @Override
    public <T> T narrow(final Class<T> target) {
        if (target.isAssignableFrom(this.getClass())) {
            return target.cast(this);
        }
        return this.getNext().narrow(target);
    }
    
    public synchronized void setNext(final Transport next) {
        this.next = next;
    }
    
    public void install(final TransportFilter filter) {
        filter.setTransportListener(this);
        this.getNext().setTransportListener(filter);
        this.setNext(filter);
    }
    
    @Override
    public String getRemoteAddress() {
        return this.getNext().getRemoteAddress();
    }
    
    @Override
    public boolean isFaultTolerant() {
        return this.getNext().isFaultTolerant();
    }
    
    @Override
    public boolean isDisposed() {
        return this.getNext().isDisposed();
    }
    
    @Override
    public boolean isConnected() {
        return this.getNext().isConnected();
    }
    
    @Override
    public void reconnect(final URI uri) throws IOException {
        this.getNext().reconnect(uri);
    }
    
    @Override
    public int getReceiveCounter() {
        return this.getNext().getReceiveCounter();
    }
    
    @Override
    public boolean isReconnectSupported() {
        return this.getNext().isReconnectSupported();
    }
    
    @Override
    public boolean isUpdateURIsSupported() {
        return this.getNext().isUpdateURIsSupported();
    }
    
    @Override
    public void updateURIs(final boolean reblance, final URI[] uris) throws IOException {
        this.getNext().updateURIs(reblance, uris);
    }
}
