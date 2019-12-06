// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.transport.Transport;

public class DemandForwardingBridge extends DemandForwardingBridgeSupport
{
    public DemandForwardingBridge(final NetworkBridgeConfiguration configuration, final Transport localBroker, final Transport remoteBroker) {
        super(configuration, localBroker, remoteBroker);
    }
}
