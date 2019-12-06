// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.HashMap;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerId;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.ConnectionId;
import java.util.Map;

public class MapTransportConnectionStateRegister implements TransportConnectionStateRegister
{
    private Map<ConnectionId, TransportConnectionState> connectionStates;
    
    public MapTransportConnectionStateRegister() {
        this.connectionStates = new ConcurrentHashMap<ConnectionId, TransportConnectionState>();
    }
    
    @Override
    public TransportConnectionState registerConnectionState(final ConnectionId connectionId, final TransportConnectionState state) {
        final TransportConnectionState rc = this.connectionStates.put(connectionId, state);
        return rc;
    }
    
    @Override
    public TransportConnectionState unregisterConnectionState(final ConnectionId connectionId) {
        final TransportConnectionState rc = this.connectionStates.remove(connectionId);
        if (rc.getReferenceCounter().get() > 1) {
            rc.decrementReference();
            this.connectionStates.put(connectionId, rc);
        }
        return rc;
    }
    
    @Override
    public List<TransportConnectionState> listConnectionStates() {
        final List<TransportConnectionState> rc = new ArrayList<TransportConnectionState>();
        rc.addAll(this.connectionStates.values());
        return rc;
    }
    
    @Override
    public TransportConnectionState lookupConnectionState(final String connectionId) {
        return this.connectionStates.get(new ConnectionId(connectionId));
    }
    
    @Override
    public TransportConnectionState lookupConnectionState(final ConsumerId id) {
        final TransportConnectionState cs = this.lookupConnectionState(id.getConnectionId());
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a consumer from a connection that had not been registered: " + id.getParentId().getParentId());
        }
        return cs;
    }
    
    @Override
    public TransportConnectionState lookupConnectionState(final ProducerId id) {
        final TransportConnectionState cs = this.lookupConnectionState(id.getConnectionId());
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a producer from a connection that had not been registered: " + id.getParentId().getParentId());
        }
        return cs;
    }
    
    @Override
    public TransportConnectionState lookupConnectionState(final SessionId id) {
        final TransportConnectionState cs = this.lookupConnectionState(id.getConnectionId());
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a session from a connection that had not been registered: " + id.getParentId());
        }
        return cs;
    }
    
    @Override
    public TransportConnectionState lookupConnectionState(final ConnectionId connectionId) {
        final TransportConnectionState cs = this.connectionStates.get(connectionId);
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a connection that had not been registered: " + connectionId);
        }
        return cs;
    }
    
    @Override
    public boolean doesHandleMultipleConnectionStates() {
        return true;
    }
    
    @Override
    public boolean isEmpty() {
        return this.connectionStates.isEmpty();
    }
    
    @Override
    public void clear() {
        this.connectionStates.clear();
    }
    
    @Override
    public void intialize(final TransportConnectionStateRegister other) {
        this.connectionStates.clear();
        this.connectionStates.putAll(other.mapStates());
    }
    
    @Override
    public Map<ConnectionId, TransportConnectionState> mapStates() {
        final HashMap<ConnectionId, TransportConnectionState> map = new HashMap<ConnectionId, TransportConnectionState>(this.connectionStates);
        return map;
    }
}
