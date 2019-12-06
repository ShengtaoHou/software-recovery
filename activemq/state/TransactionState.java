// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import org.apache.activemq.command.ProducerId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.Command;
import java.util.List;

public class TransactionState
{
    private final List<Command> commands;
    private final TransactionId id;
    private final AtomicBoolean shutdown;
    private boolean prepared;
    private int preparedResult;
    private final Map<ProducerId, ProducerState> producers;
    private final long createdAt;
    
    public TransactionState(final TransactionId id) {
        this.commands = new ArrayList<Command>();
        this.shutdown = new AtomicBoolean(false);
        this.producers = new ConcurrentHashMap<ProducerId, ProducerState>();
        this.createdAt = System.currentTimeMillis();
        this.id = id;
    }
    
    @Override
    public String toString() {
        return this.id.toString();
    }
    
    public void addCommand(final Command operation) {
        this.checkShutdown();
        this.commands.add(operation);
    }
    
    public List<Command> getCommands() {
        return this.commands;
    }
    
    private void checkShutdown() {
        if (this.shutdown.get()) {
            throw new IllegalStateException("Disposed");
        }
    }
    
    public void shutdown() {
        this.shutdown.set(false);
    }
    
    public TransactionId getId() {
        return this.id;
    }
    
    public void setPrepared(final boolean prepared) {
        this.prepared = prepared;
    }
    
    public boolean isPrepared() {
        return this.prepared;
    }
    
    public void setPreparedResult(final int preparedResult) {
        this.preparedResult = preparedResult;
    }
    
    public int getPreparedResult() {
        return this.preparedResult;
    }
    
    public void addProducerState(final ProducerState producerState) {
        if (producerState != null) {
            this.producers.put(producerState.getInfo().getProducerId(), producerState);
        }
    }
    
    public Map<ProducerId, ProducerState> getProducerStates() {
        return this.producers;
    }
    
    public long getCreatedAt() {
        return this.createdAt;
    }
}
