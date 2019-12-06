// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConsumerInfo;

public interface NetworkBridgeFilterFactory
{
    NetworkBridgeFilter create(final ConsumerInfo p0, final BrokerId[] p1, final int p2, final int p3);
}
