// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import javax.jms.JMSException;
import javax.jms.TemporaryQueue;

public class ActiveMQTempQueue extends ActiveMQTempDestination implements TemporaryQueue
{
    public static final byte DATA_STRUCTURE_TYPE = 102;
    private static final long serialVersionUID = 6683049467527633867L;
    
    public ActiveMQTempQueue() {
    }
    
    public ActiveMQTempQueue(final String name) {
        super(name);
    }
    
    public ActiveMQTempQueue(final ConnectionId connectionId, final long sequenceId) {
        super(connectionId.getValue(), sequenceId);
    }
    
    @Override
    public byte getDataStructureType() {
        return 102;
    }
    
    @Override
    public boolean isQueue() {
        return true;
    }
    
    @Override
    public String getQueueName() throws JMSException {
        return this.getPhysicalName();
    }
    
    @Override
    public byte getDestinationType() {
        return 5;
    }
    
    @Override
    protected String getQualifiedPrefix() {
        return "temp-queue://";
    }
}
