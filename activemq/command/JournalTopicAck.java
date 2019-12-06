// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;

public class JournalTopicAck implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 50;
    ActiveMQDestination destination;
    String clientId;
    String subscritionName;
    MessageId messageId;
    long messageSequenceId;
    TransactionId transactionId;
    
    @Override
    public byte getDataStructureType() {
        return 50;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public MessageId getMessageId() {
        return this.messageId;
    }
    
    public void setMessageId(final MessageId messageId) {
        this.messageId = messageId;
    }
    
    public long getMessageSequenceId() {
        return this.messageSequenceId;
    }
    
    public void setMessageSequenceId(final long messageSequenceId) {
        this.messageSequenceId = messageSequenceId;
    }
    
    public String getSubscritionName() {
        return this.subscritionName;
    }
    
    public void setSubscritionName(final String subscritionName) {
        this.subscritionName = subscritionName;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public void setTransactionId(final TransactionId transaction) {
        this.transactionId = transaction;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this, JournalTopicAck.class);
    }
}
