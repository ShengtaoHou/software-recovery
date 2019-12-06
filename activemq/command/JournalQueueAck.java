// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;

public class JournalQueueAck implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 52;
    ActiveMQDestination destination;
    MessageAck messageAck;
    
    @Override
    public byte getDataStructureType() {
        return 52;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public MessageAck getMessageAck() {
        return this.messageAck;
    }
    
    public void setMessageAck(final MessageAck messageAck) {
        this.messageAck = messageAck;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this, JournalQueueAck.class);
    }
}
