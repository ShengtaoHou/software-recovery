// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConsumerInfo;

public class DefaultNetworkBridgeFilterFactory implements NetworkBridgeFilterFactory
{
    @Override
    public NetworkBridgeFilter create(final ConsumerInfo info, final BrokerId[] remoteBrokerPath, final int messageTTL, final int consumerTTL) {
        return new NetworkBridgeFilter(info, remoteBrokerPath[0], messageTTL, consumerTTL);
    }
}
