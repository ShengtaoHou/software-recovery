// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.SessionInfo;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ConsumerId;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.DestinationInfo;
import java.util.List;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.TransactionId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.command.ConnectionInfo;

public class ConnectionState
{
    ConnectionInfo info;
    private final ConcurrentHashMap<TransactionId, TransactionState> transactions;
    private final ConcurrentHashMap<SessionId, SessionState> sessions;
    private final List<DestinationInfo> tempDestinations;
    private final AtomicBoolean shutdown;
    private boolean connectionInterruptProcessingComplete;
    private HashMap<ConsumerId, ConsumerInfo> recoveringPullConsumers;
    
    public ConnectionState(final ConnectionInfo info) {
        this.transactions = new ConcurrentHashMap<TransactionId, TransactionState>();
        this.sessions = new ConcurrentHashMap<SessionId, SessionState>();
        this.tempDestinations = Collections.synchronizedList(new ArrayList<DestinationInfo>());
        this.shutdown = new AtomicBoolean(false);
        this.connectionInterruptProcessingComplete = true;
        this.info = info;
        this.addSession(new SessionInfo(info, -1L));
    }
    
    @Override
    public String toString() {
        return this.info.toString();
    }
    
    public void reset(final ConnectionInfo info) {
        this.info = info;
        this.transactions.clear();
        this.sessions.clear();
        this.tempDestinations.clear();
        this.shutdown.set(false);
        this.addSession(new SessionInfo(info, -1L));
    }
    
    public void addTempDestination(final DestinationInfo info) {
        this.checkShutdown();
        this.tempDestinations.add(info);
    }
    
    public void removeTempDestination(final ActiveMQDestination destination) {
        final Iterator<DestinationInfo> iter = this.tempDestinations.iterator();
        while (iter.hasNext()) {
            final DestinationInfo di = iter.next();
            if (di.getDestination().equals(destination)) {
                iter.remove();
            }
        }
    }
    
    public void addTransactionState(final TransactionId id) {
        this.checkShutdown();
        this.transactions.put(id, new TransactionState(id));
    }
    
    public TransactionState getTransactionState(final TransactionId id) {
        return this.transactions.get(id);
    }
    
    public Collection<TransactionState> getTransactionStates() {
        return this.transactions.values();
    }
    
    public TransactionState removeTransactionState(final TransactionId id) {
        return this.transactions.remove(id);
    }
    
    public void addSession(final SessionInfo info) {
        this.checkShutdown();
        this.sessions.put(info.getSessionId(), new SessionState(info));
    }
    
    public SessionState removeSession(final SessionId id) {
        return this.sessions.remove(id);
    }
    
    public SessionState getSessionState(final SessionId id) {
        return this.sessions.get(id);
    }
    
    public ConnectionInfo getInfo() {
        return this.info;
    }
    
    public Set<SessionId> getSessionIds() {
        return this.sessions.keySet();
    }
    
    public List<DestinationInfo> getTempDestinations() {
        return this.tempDestinations;
    }
    
    public Collection<SessionState> getSessionStates() {
        return this.sessions.values();
    }
    
    private void checkShutdown() {
        if (this.shutdown.get()) {
            throw new IllegalStateException("Disposed");
        }
    }
    
    public void shutdown() {
        if (this.shutdown.compareAndSet(false, true)) {
            for (final SessionState ss : this.sessions.values()) {
                ss.shutdown();
            }
        }
    }
    
    public Map<ConsumerId, ConsumerInfo> getRecoveringPullConsumers() {
        if (this.recoveringPullConsumers == null) {
            this.recoveringPullConsumers = new HashMap<ConsumerId, ConsumerInfo>();
        }
        return this.recoveringPullConsumers;
    }
    
    public void setConnectionInterruptProcessingComplete(final boolean connectionInterruptProcessingComplete) {
        this.connectionInterruptProcessingComplete = connectionInterruptProcessingComplete;
    }
    
    public boolean isConnectionInterruptProcessingComplete() {
        return this.connectionInterruptProcessingComplete;
    }
}
