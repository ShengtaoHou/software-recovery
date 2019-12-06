// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.apache.activemq.network.NetworkBridge;

public class NetworkBridgeView implements NetworkBridgeViewMBean
{
    private final NetworkBridge bridge;
    private boolean createByDuplex;
    private List<NetworkDestinationView> networkDestinationViewList;
    
    public NetworkBridgeView(final NetworkBridge bridge) {
        this.createByDuplex = false;
        this.networkDestinationViewList = new CopyOnWriteArrayList<NetworkDestinationView>();
        this.bridge = bridge;
    }
    
    @Override
    public void start() throws Exception {
        this.bridge.start();
    }
    
    @Override
    public void stop() throws Exception {
        this.bridge.stop();
    }
    
    @Override
    public String getLocalAddress() {
        return this.bridge.getLocalAddress();
    }
    
    @Override
    public String getRemoteAddress() {
        return this.bridge.getRemoteAddress();
    }
    
    @Override
    public String getRemoteBrokerName() {
        return this.bridge.getRemoteBrokerName();
    }
    
    @Override
    public String getRemoteBrokerId() {
        return this.bridge.getRemoteBrokerId();
    }
    
    @Override
    public String getLocalBrokerName() {
        return this.bridge.getLocalBrokerName();
    }
    
    @Override
    public long getEnqueueCounter() {
        return this.bridge.getEnqueueCounter();
    }
    
    @Override
    public long getDequeueCounter() {
        return this.bridge.getDequeueCounter();
    }
    
    @Override
    public boolean isCreatedByDuplex() {
        return this.createByDuplex;
    }
    
    public void setCreateByDuplex(final boolean createByDuplex) {
        this.createByDuplex = createByDuplex;
    }
    
    @Override
    public void resetStats() {
        this.bridge.resetStats();
        for (final NetworkDestinationView networkDestinationView : this.networkDestinationViewList) {
            networkDestinationView.resetStats();
        }
    }
    
    public void addNetworkDestinationView(final NetworkDestinationView networkDestinationView) {
        this.networkDestinationViewList.add(networkDestinationView);
    }
    
    public void removeNetworkDestinationView(final NetworkDestinationView networkDestinationView) {
        this.networkDestinationViewList.remove(networkDestinationView);
    }
}
