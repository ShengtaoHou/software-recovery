// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;
import java.util.concurrent.atomic.AtomicLong;

public class ProducerInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 6;
    protected ProducerId producerId;
    protected ActiveMQDestination destination;
    protected BrokerId[] brokerPath;
    protected boolean dispatchAsync;
    protected int windowSize;
    protected AtomicLong sentCount;
    
    public ProducerInfo() {
        this.sentCount = new AtomicLong();
    }
    
    public ProducerInfo(final ProducerId producerId) {
        this.sentCount = new AtomicLong();
        this.producerId = producerId;
    }
    
    public ProducerInfo(final SessionInfo sessionInfo, final long producerId) {
        this.sentCount = new AtomicLong();
        this.producerId = new ProducerId(sessionInfo.getSessionId(), producerId);
    }
    
    public ProducerInfo copy() {
        final ProducerInfo info = new ProducerInfo();
        this.copy(info);
        return info;
    }
    
    public void copy(final ProducerInfo info) {
        super.copy(info);
        info.producerId = this.producerId;
        info.destination = this.destination;
    }
    
    @Override
    public byte getDataStructureType() {
        return 6;
    }
    
    public ProducerId getProducerId() {
        return this.producerId;
    }
    
    public void setProducerId(final ProducerId producerId) {
        this.producerId = producerId;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public RemoveInfo createRemoveCommand() {
        final RemoveInfo command = new RemoveInfo(this.getProducerId());
        command.setResponseRequired(this.isResponseRequired());
        return command;
    }
    
    public BrokerId[] getBrokerPath() {
        return this.brokerPath;
    }
    
    public void setBrokerPath(final BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processAddProducer(this);
    }
    
    public boolean isDispatchAsync() {
        return this.dispatchAsync;
    }
    
    public void setDispatchAsync(final boolean dispatchAsync) {
        this.dispatchAsync = dispatchAsync;
    }
    
    public int getWindowSize() {
        return this.windowSize;
    }
    
    public void setWindowSize(final int windowSize) {
        this.windowSize = windowSize;
    }
    
    public long getSentCount() {
        return this.sentCount.get();
    }
    
    public void incrementSentCount() {
        this.sentCount.incrementAndGet();
    }
    
    public void resetSentCount() {
        this.sentCount.set(0L);
    }
}
