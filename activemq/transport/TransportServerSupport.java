// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.util.Map;
import java.net.URI;
import org.apache.activemq.util.ServiceSupport;

public abstract class TransportServerSupport extends ServiceSupport implements TransportServer
{
    private URI connectURI;
    private URI bindLocation;
    private TransportAcceptListener acceptListener;
    protected Map<String, Object> transportOptions;
    protected boolean allowLinkStealing;
    
    public TransportServerSupport() {
    }
    
    public TransportServerSupport(final URI location) {
        this.connectURI = location;
        this.bindLocation = location;
    }
    
    public TransportAcceptListener getAcceptListener() {
        return this.acceptListener;
    }
    
    @Override
    public void setAcceptListener(final TransportAcceptListener acceptListener) {
        this.acceptListener = acceptListener;
    }
    
    @Override
    public URI getConnectURI() {
        return this.connectURI;
    }
    
    public void setConnectURI(final URI location) {
        this.connectURI = location;
    }
    
    protected void onAcceptError(final Exception e) {
        if (this.acceptListener != null) {
            this.acceptListener.onAcceptError(e);
        }
    }
    
    public URI getBindLocation() {
        return this.bindLocation;
    }
    
    public void setBindLocation(final URI bindLocation) {
        this.bindLocation = bindLocation;
    }
    
    public void setTransportOption(final Map<String, Object> transportOptions) {
        this.transportOptions = transportOptions;
    }
    
    @Override
    public boolean isAllowLinkStealing() {
        return this.allowLinkStealing;
    }
    
    public void setAllowLinkStealing(final boolean allowLinkStealing) {
        this.allowLinkStealing = allowLinkStealing;
    }
}
