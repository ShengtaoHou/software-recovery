// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.JMSException;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import javax.jms.TopicSubscriber;

public class CamelTopicSubscriber extends CamelMessageConsumer implements TopicSubscriber
{
    public CamelTopicSubscriber(final CamelTopic destination, final Endpoint endpoint, final ActiveMQSession session, final String name, final String messageSelector, final boolean noLocal) {
        super(destination, endpoint, session, messageSelector, noLocal);
    }
    
    @Override
    public Topic getTopic() throws JMSException {
        this.checkClosed();
        return (Topic)super.getDestination();
    }
    
    @Override
    public boolean getNoLocal() throws JMSException {
        this.checkClosed();
        return super.isNoLocal();
    }
}
