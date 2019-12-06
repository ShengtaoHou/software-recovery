// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;

public class ActiveMQTempTopic extends ActiveMQTempDestination implements TemporaryTopic
{
    public static final byte DATA_STRUCTURE_TYPE = 103;
    private static final long serialVersionUID = -4325596784597300253L;
    
    public ActiveMQTempTopic() {
    }
    
    public ActiveMQTempTopic(final String name) {
        super(name);
    }
    
    public ActiveMQTempTopic(final ConnectionId connectionId, final long sequenceId) {
        super(connectionId.getValue(), sequenceId);
    }
    
    @Override
    public byte getDataStructureType() {
        return 103;
    }
    
    @Override
    public boolean isTopic() {
        return true;
    }
    
    @Override
    public String getTopicName() throws JMSException {
        return this.getPhysicalName();
    }
    
    @Override
    public byte getDestinationType() {
        return 6;
    }
    
    @Override
    protected String getQualifiedPrefix() {
        return "temp-topic://";
    }
}
