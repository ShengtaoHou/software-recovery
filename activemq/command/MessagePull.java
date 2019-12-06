// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class MessagePull extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 20;
    protected ConsumerId consumerId;
    protected ActiveMQDestination destination;
    protected long timeout;
    private MessageId messageId;
    private String correlationId;
    private transient boolean tracked;
    
    public MessagePull() {
        this.tracked = false;
    }
    
    @Override
    public byte getDataStructureType() {
        return 20;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processMessagePull(this);
    }
    
    public void configure(final ConsumerInfo info) {
        this.setConsumerId(info.getConsumerId());
        this.setDestination(info.getDestination());
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
    
    public long getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    public String getCorrelationId() {
        return this.correlationId;
    }
    
    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }
    
    public MessageId getMessageId() {
        return this.messageId;
    }
    
    public void setMessageId(final MessageId messageId) {
        this.messageId = messageId;
    }
    
    public void setTracked(final boolean tracked) {
        this.tracked = tracked;
    }
    
    public boolean isTracked() {
        return this.tracked;
    }
}
