// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.JMSException;
import javax.jms.Queue;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import javax.jms.QueueReceiver;

public class CamelQueueReceiver extends CamelMessageConsumer implements QueueReceiver
{
    public CamelQueueReceiver(final CamelQueue destination, final Endpoint endpoint, final ActiveMQSession session, final String name) {
        super(destination, endpoint, session, null, false);
    }
    
    @Override
    public Queue getQueue() throws JMSException {
        this.checkClosed();
        return (Queue)super.getDestination();
    }
}
