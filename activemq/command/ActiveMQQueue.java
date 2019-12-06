// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import javax.jms.JMSException;
import javax.jms.Queue;

public class ActiveMQQueue extends ActiveMQDestination implements Queue
{
    public static final byte DATA_STRUCTURE_TYPE = 100;
    private static final long serialVersionUID = -3885260014960795889L;
    
    public ActiveMQQueue() {
    }
    
    public ActiveMQQueue(final String name) {
        super(name);
    }
    
    @Override
    public byte getDataStructureType() {
        return 100;
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
        return 1;
    }
    
    @Override
    protected String getQualifiedPrefix() {
        return "queue://";
    }
}
