// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.command.ConnectionInfo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.activemq.state.ConnectionState;

public class TransportConnectionState extends ConnectionState
{
    private ConnectionContext context;
    private TransportConnection connection;
    private AtomicInteger referenceCounter;
    private final Object connectionMutex;
    
    public TransportConnectionState(final ConnectionInfo info, final TransportConnection transportConnection) {
        super(info);
        this.referenceCounter = new AtomicInteger();
        this.connectionMutex = new Object();
        this.connection = transportConnection;
    }
    
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public TransportConnection getConnection() {
        return this.connection;
    }
    
    public void setContext(final ConnectionContext context) {
        this.context = context;
    }
    
    public void setConnection(final TransportConnection connection) {
        this.connection = connection;
    }
    
    public int incrementReference() {
        return this.referenceCounter.incrementAndGet();
    }
    
    public int decrementReference() {
        return this.referenceCounter.decrementAndGet();
    }
    
    public AtomicInteger getReferenceCounter() {
        return this.referenceCounter;
    }
    
    public void setReferenceCounter(final AtomicInteger referenceCounter) {
        this.referenceCounter = referenceCounter;
    }
    
    public Object getConnectionMutex() {
        return this.connectionMutex;
    }
}
