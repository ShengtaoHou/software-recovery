// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network;

import org.apache.activemq.command.Message;

public interface NetworkBridgeListener
{
    void bridgeFailed();
    
    void onStart(final NetworkBridge p0);
    
    void onStop(final NetworkBridge p0);
    
    void onOutboundMessage(final NetworkBridge p0, final Message p1);
    
    void onInboundMessage(final NetworkBridge p0, final Message p1);
}
