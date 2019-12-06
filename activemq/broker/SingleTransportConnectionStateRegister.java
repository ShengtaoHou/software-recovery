// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ConsumerId;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.ConnectionId;

public class SingleTransportConnectionStateRegister implements TransportConnectionStateRegister
{
    private TransportConnectionState connectionState;
    private ConnectionId connectionId;
    
    @Override
    public TransportConnectionState registerConnectionState(final ConnectionId connectionId, final TransportConnectionState state) {
        final TransportConnectionState rc = this.connectionState;
        this.connectionState = state;
        this.connectionId = connectionId;
        return rc;
    }
    
    @Override
    public synchronized TransportConnectionState unregisterConnectionState(ConnectionId connectionId) {
        TransportConnectionState rc = null;
        if (connectionId != null && this.connectionState != null && this.connectionId != null && this.connectionId.equals(connectionId)) {
            rc = this.connectionState;
            this.connectionState = null;
            connectionId = null;
        }
        return rc;
    }
    
    @Override
    public synchronized List<TransportConnectionState> listConnectionStates() {
        final List<TransportConnectionState> rc = new ArrayList<TransportConnectionState>();
        if (this.connectionState != null) {
            rc.add(this.connectionState);
        }
        return rc;
    }
    
    @Override
    public synchronized TransportConnectionState lookupConnectionState(final String connectionId) {
        final TransportConnectionState cs = this.connectionState;
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a connectionId for a connection that had not been registered: " + connectionId);
        }
        return cs;
    }
    
    @Override
    public synchronized TransportConnectionState lookupConnectionState(final ConsumerId id) {
        final TransportConnectionState cs = this.connectionState;
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a consumer from a connection that had not been registered: " + id.getParentId().getParentId());
        }
        return cs;
    }
    
    @Override
    public synchronized TransportConnectionState lookupConnectionState(final ProducerId id) {
        final TransportConnectionState cs = this.connectionState;
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a producer from a connection that had not been registered: " + id.getParentId().getParentId());
        }
        return cs;
    }
    
    @Override
    public synchronized TransportConnectionState lookupConnectionState(final SessionId id) {
        final TransportConnectionState cs = this.connectionState;
        if (cs == null) {
            throw new IllegalStateException("Cannot lookup a session from a connection that had not been registered: " + id.getParentId());
        }
        return cs;
    }
    
    @Override
    public synchronized TransportConnectionState lookupConnectionState(final ConnectionId connectionId) {
        final TransportConnectionState cs = this.connectionState;
        return cs;
    }
    
    @Override
    public synchronized boolean doesHandleMultipleConnectionStates() {
        return false;
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.connectionState == null;
    }
    
    @Override
    public void intialize(final TransportConnectionStateRegister other) {
        if (other.isEmpty()) {
            this.clear();
        }
        else {
            final Map map = other.mapStates();
            final Iterator i = map.entrySet().iterator();
            final Map.Entry<ConnectionId, TransportConnectionState> entry = i.next();
            this.connectionId = entry.getKey();
            this.connectionState = entry.getValue();
        }
    }
    
    @Override
    public Map<ConnectionId, TransportConnectionState> mapStates() {
        final Map<ConnectionId, TransportConnectionState> map = new HashMap<ConnectionId, TransportConnectionState>();
        if (!this.isEmpty()) {
            map.put(this.connectionId, this.connectionState);
        }
        return map;
    }
    
    @Override
    public void clear() {
        this.connectionState = null;
        this.connectionId = null;
    }
}
