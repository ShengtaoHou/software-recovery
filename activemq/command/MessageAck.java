// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class MessageAck extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 22;
    public static final byte DELIVERED_ACK_TYPE = 0;
    public static final byte STANDARD_ACK_TYPE = 2;
    public static final byte POSION_ACK_TYPE = 1;
    public static final byte REDELIVERED_ACK_TYPE = 3;
    public static final byte INDIVIDUAL_ACK_TYPE = 4;
    public static final byte UNMATCHED_ACK_TYPE = 5;
    public static final byte EXPIRED_ACK_TYPE = 6;
    protected byte ackType;
    protected ConsumerId consumerId;
    protected MessageId firstMessageId;
    protected MessageId lastMessageId;
    protected ActiveMQDestination destination;
    protected TransactionId transactionId;
    protected int messageCount;
    protected Throwable poisonCause;
    protected transient String consumerKey;
    
    public MessageAck() {
    }
    
    public MessageAck(final MessageDispatch md, final byte ackType, final int messageCount) {
        this.ackType = ackType;
        this.consumerId = md.getConsumerId();
        this.destination = md.getDestination();
        this.lastMessageId = md.getMessage().getMessageId();
        this.messageCount = messageCount;
    }
    
    public MessageAck(final Message message, final byte ackType, final int messageCount) {
        this.ackType = ackType;
        this.destination = message.getDestination();
        this.lastMessageId = message.getMessageId();
        this.messageCount = messageCount;
    }
    
    public void copy(final MessageAck copy) {
        super.copy(copy);
        copy.firstMessageId = this.firstMessageId;
        copy.lastMessageId = this.lastMessageId;
        copy.destination = this.destination;
        copy.transactionId = this.transactionId;
        copy.ackType = this.ackType;
        copy.consumerId = this.consumerId;
    }
    
    @Override
    public byte getDataStructureType() {
        return 22;
    }
    
    @Override
    public boolean isMessageAck() {
        return true;
    }
    
    public boolean isPoisonAck() {
        return this.ackType == 1;
    }
    
    public boolean isStandardAck() {
        return this.ackType == 2;
    }
    
    public boolean isDeliveredAck() {
        return this.ackType == 0;
    }
    
    public boolean isRedeliveredAck() {
        return this.ackType == 3;
    }
    
    public boolean isIndividualAck() {
        return this.ackType == 4;
    }
    
    public boolean isUnmatchedAck() {
        return this.ackType == 5;
    }
    
    public boolean isExpiredAck() {
        return this.ackType == 6;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public void setTransactionId(final TransactionId transactionId) {
        this.transactionId = transactionId;
    }
    
    public boolean isInTransaction() {
        return this.transactionId != null;
    }
    
    public ConsumerId getConsumerId() {
        return this.consumerId;
    }
    
    public void setConsumerId(final ConsumerId consumerId) {
        this.consumerId = consumerId;
    }
    
    public byte getAckType() {
        return this.ackType;
    }
    
    public void setAckType(final byte ackType) {
        this.ackType = ackType;
    }
    
    public MessageId getFirstMessageId() {
        return this.firstMessageId;
    }
    
    public void setFirstMessageId(final MessageId firstMessageId) {
        this.firstMessageId = firstMessageId;
    }
    
    public MessageId getLastMessageId() {
        return this.lastMessageId;
    }
    
    public void setLastMessageId(final MessageId lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
    
    public int getMessageCount() {
        return this.messageCount;
    }
    
    public void setMessageCount(final int messageCount) {
        this.messageCount = messageCount;
    }
    
    public Throwable getPoisonCause() {
        return this.poisonCause;
    }
    
    public void setPoisonCause(final Throwable poisonCause) {
        this.poisonCause = poisonCause;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processMessageAck(this);
    }
    
    public void setMessageID(final MessageId messageID) {
        this.setFirstMessageId(messageID);
        this.setLastMessageId(messageID);
        this.setMessageCount(1);
    }
}
