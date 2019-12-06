// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ConsumerControl extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 17;
    protected ConsumerId consumerId;
    protected boolean close;
    protected boolean stop;
    protected boolean start;
    protected boolean flush;
    protected int prefetch;
    protected ActiveMQDestination destination;
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    @Override
    public byte getDataStructureType() {
        return 17;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processConsumerControl(this);
    }
    
    public boolean isClose() {
        return this.close;
    }
    
    public void setClose(final boolean close) {
        this.close = close;
    }
    
    public ConsumerId getConsumerId() {
        return this.consumerId;
    }
    
    public void setConsumerId(final ConsumerId consumerId) {
        this.consumerId = consumerId;
    }
    
    public int getPrefetch() {
        return this.prefetch;
    }
    
    public void setPrefetch(final int prefetch) {
        this.prefetch = prefetch;
    }
    
    public boolean isFlush() {
        return this.flush;
    }
    
    public void setFlush(final boolean flush) {
        this.flush = flush;
    }
    
    public boolean isStart() {
        return this.start;
    }
    
    public void setStart(final boolean start) {
        this.start = start;
    }
    
    public boolean isStop() {
        return this.stop;
    }
    
    public void setStop(final boolean stop) {
        this.stop = stop;
    }
}
