// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.command.Command;
import org.apache.activemq.transport.Transport;

public class CompositeDemandForwardingBridge extends DemandForwardingBridgeSupport
{
    public CompositeDemandForwardingBridge(final NetworkBridgeConfiguration configuration, final Transport localBroker, final Transport remoteBroker) {
        super(configuration, localBroker, remoteBroker);
        this.remoteBrokerName = remoteBroker.toString();
    }
    
    protected void serviceLocalBrokerInfo(final Command command) throws InterruptedException {
    }
}
