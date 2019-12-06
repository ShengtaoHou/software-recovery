// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import java.util.Collection;
import java.util.Set;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ProducerInfo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ProducerId;
import java.util.Map;
import org.apache.activemq.command.SessionInfo;

public class SessionState
{
    final SessionInfo info;
    private final Map<ProducerId, ProducerState> producers;
    private final Map<ConsumerId, ConsumerState> consumers;
    private final AtomicBoolean shutdown;
    
    public SessionState(final SessionInfo info) {
        this.producers = new ConcurrentHashMap<ProducerId, ProducerState>();
        this.consumers = new ConcurrentHashMap<ConsumerId, ConsumerState>();
        this.shutdown = new AtomicBoolean(false);
        this.info = info;
    }
    
    @Override
    public String toString() {
        return this.info.toString();
    }
    
    public void addProducer(final ProducerInfo info) {
        this.checkShutdown();
        this.producers.put(info.getProducerId(), new ProducerState(info));
    }
    
    public ProducerState removeProducer(final ProducerId id) {
        final ProducerState producerState = this.producers.remove(id);
        if (producerState != null && producerState.getTransactionState() != null) {
            producerState.getTransactionState().addProducerState(producerState);
        }
        return producerState;
    }
    
    public void addConsumer(final ConsumerInfo info) {
        this.checkShutdown();
        this.consumers.put(info.getConsumerId(), new ConsumerState(info));
    }
    
    public ConsumerState removeConsumer(final ConsumerId id) {
        return this.consumers.remove(id);
    }
    
    public SessionInfo getInfo() {
        return this.info;
    }
    
    public Set<ConsumerId> getConsumerIds() {
        return this.consumers.keySet();
    }
    
    public Set<ProducerId> getProducerIds() {
        return this.producers.keySet();
    }
    
    public Collection<ProducerState> getProducerStates() {
        return this.producers.values();
    }
    
    public ProducerState getProducerState(final ProducerId producerId) {
        return this.producers.get(producerId);
    }
    
    public Collection<ConsumerState> getConsumerStates() {
        return this.consumers.values();
    }
    
    public ConsumerState getConsumerState(final ConsumerId consumerId) {
        return this.consumers.get(consumerId);
    }
    
    private void checkShutdown() {
        if (this.shutdown.get()) {
            throw new IllegalStateException("Disposed");
        }
    }
    
    public void shutdown() {
        this.shutdown.set(false);
    }
}
