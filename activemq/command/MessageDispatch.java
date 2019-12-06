// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;
import org.apache.activemq.transport.TransmitCallback;

public class MessageDispatch extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 21;
    protected ConsumerId consumerId;
    protected ActiveMQDestination destination;
    protected Message message;
    protected int redeliveryCounter;
    protected transient long deliverySequenceId;
    protected transient Object consumer;
    protected transient TransmitCallback transmitCallback;
    protected transient Throwable rollbackCause;
    
    @Override
    public byte getDataStructureType() {
        return 21;
    }
    
    @Override
    public boolean isMessageDispatch() {
        return true;
    }
    
    public ConsumerId getConsumerId() {
        return this.consumerId;
    }
    
    public void setConsumerId(final ConsumerId consumerId) {
        this.consumerId = consumerId;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public Message getMessage() {
        return this.message;
    }
    
    public void setMessage(final Message message) {
        this.message = message;
    }
    
    public long getDeliverySequenceId() {
        return this.deliverySequenceId;
    }
    
    public void setDeliverySequenceId(final long deliverySequenceId) {
        this.deliverySequenceId = deliverySequenceId;
    }
    
    public int getRedeliveryCounter() {
        return this.redeliveryCounter;
    }
    
    public void setRedeliveryCounter(final int deliveryCounter) {
        this.redeliveryCounter = deliveryCounter;
    }
    
    public Object getConsumer() {
        return this.consumer;
    }
    
    public void setConsumer(final Object consumer) {
        this.consumer = consumer;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processMessageDispatch(this);
    }
    
    public TransmitCallback getTransmitCallback() {
        return this.transmitCallback;
    }
    
    public void setTransmitCallback(final TransmitCallback transmitCallback) {
        this.transmitCallback = transmitCallback;
    }
    
    public Throwable getRollbackCause() {
        return this.rollbackCause;
    }
    
    public void setRollbackCause(final Throwable rollbackCause) {
        this.rollbackCause = rollbackCause;
    }
}
