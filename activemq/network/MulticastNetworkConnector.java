// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.transport.TransportFactory;
import java.net.URI;
import org.apache.activemq.transport.Transport;

public class MulticastNetworkConnector extends NetworkConnector
{
    private Transport localTransport;
    private Transport remoteTransport;
    private URI remoteURI;
    private DemandForwardingBridgeSupport bridge;
    
    public MulticastNetworkConnector() {
    }
    
    public MulticastNetworkConnector(final URI remoteURI) {
        this.remoteURI = remoteURI;
    }
    
    public DemandForwardingBridgeSupport getBridge() {
        return this.bridge;
    }
    
    public void setBridge(final DemandForwardingBridgeSupport bridge) {
        this.bridge = bridge;
    }
    
    public Transport getLocalTransport() {
        return this.localTransport;
    }
    
    public void setLocalTransport(final Transport localTransport) {
        this.localTransport = localTransport;
    }
    
    public Transport getRemoteTransport() {
        return this.remoteTransport;
    }
    
    public void setRemoteTransport(final Transport remoteTransport) {
        this.remoteTransport = remoteTransport;
    }
    
    public URI getRemoteURI() {
        return this.remoteURI;
    }
    
    public void setRemoteURI(final URI remoteURI) {
        this.remoteURI = remoteURI;
    }
    
    @Override
    protected void handleStart() throws Exception {
        if (this.remoteTransport == null) {
            if (this.remoteURI == null) {
                throw new IllegalArgumentException("You must specify the remoteURI property");
            }
            this.remoteTransport = TransportFactory.connect(this.remoteURI);
        }
        if (this.localTransport == null) {
            this.localTransport = this.createLocalTransport();
        }
        this.configureBridge(this.bridge = this.createBridge(this.localTransport, this.remoteTransport));
        this.bridge.start();
        this.remoteTransport.start();
        this.localTransport.start();
        super.handleStart();
    }
    
    @Override
    protected void handleStop(final ServiceStopper stopper) throws Exception {
        super.handleStop(stopper);
        if (this.bridge != null) {
            try {
                this.bridge.stop();
            }
            catch (Exception e) {
                stopper.onException(this, e);
            }
        }
        if (this.remoteTransport != null) {
            try {
                this.remoteTransport.stop();
            }
            catch (Exception e) {
                stopper.onException(this, e);
            }
        }
        if (this.localTransport != null) {
            try {
                this.localTransport.stop();
            }
            catch (Exception e) {
                stopper.onException(this, e);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + this.getName() + "[" + this.remoteTransport.toString() + "]";
    }
    
    protected DemandForwardingBridgeSupport createBridge(final Transport local, final Transport remote) {
        final CompositeDemandForwardingBridge bridge = new CompositeDemandForwardingBridge(this, local, remote);
        bridge.setBrokerService(this.getBrokerService());
        return bridge;
    }
}
