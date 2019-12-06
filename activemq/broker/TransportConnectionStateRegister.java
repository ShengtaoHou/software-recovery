// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerId;
import java.util.Map;
import java.util.List;
import org.apache.activemq.command.ConnectionId;

public interface TransportConnectionStateRegister
{
    TransportConnectionState registerConnectionState(final ConnectionId p0, final TransportConnectionState p1);
    
    TransportConnectionState unregisterConnectionState(final ConnectionId p0);
    
    List<TransportConnectionState> listConnectionStates();
    
    Map<ConnectionId, TransportConnectionState> mapStates();
    
    TransportConnectionState lookupConnectionState(final String p0);
    
    TransportConnectionState lookupConnectionState(final ConsumerId p0);
    
    TransportConnectionState lookupConnectionState(final ProducerId p0);
    
    TransportConnectionState lookupConnectionState(final SessionId p0);
    
    TransportConnectionState lookupConnectionState(final ConnectionId p0);
    
    boolean isEmpty();
    
    boolean doesHandleMultipleConnectionStates();
    
    void intialize(final TransportConnectionStateRegister p0);
    
    void clear();
}
