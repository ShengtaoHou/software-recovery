// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class MessageDispatchNotification extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 90;
    protected ConsumerId consumerId;
    protected ActiveMQDestination destination;
    protected MessageId messageId;
    protected long deliverySequenceId;
    
    @Override
    public byte getDataStructureType() {
        return 90;
    }
    
    @Override
    public boolean isMessageDispatchNotification() {
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
    
    public long getDeliverySequenceId() {
        return this.deliverySequenceId;
    }
    
    public void setDeliverySequenceId(final long deliverySequenceId) {
        this.deliverySequenceId = deliverySequenceId;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processMessageDispatchNotification(this);
    }
    
    public MessageId getMessageId() {
        return this.messageId;
    }
    
    public void setMessageId(final MessageId messageId) {
        this.messageId = messageId;
    }
}
