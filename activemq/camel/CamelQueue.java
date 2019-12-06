// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import org.apache.activemq.ActiveMQSession;
import javax.jms.JMSException;
import javax.jms.Queue;

public class CamelQueue extends CamelDestination implements Queue
{
    public CamelQueue(final String uri) {
        super(uri);
    }
    
    @Override
    public String getQueueName() throws JMSException {
        return this.getUri();
    }
    
    @Override
    public QueueSender createSender(final ActiveMQSession session) throws JMSException {
        return new CamelQueueSender(this, this.resolveEndpoint(session), session);
    }
    
    @Override
    public QueueReceiver createReceiver(final ActiveMQSession session, final String messageSelector) {
        return new CamelQueueReceiver(this, this.resolveEndpoint(session), session, messageSelector);
    }
}
