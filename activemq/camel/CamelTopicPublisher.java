// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import javax.jms.TopicPublisher;

public class CamelTopicPublisher extends CamelMessageProducer implements TopicPublisher
{
    public CamelTopicPublisher(final CamelTopic destination, final Endpoint endpoint, final ActiveMQSession session) throws JMSException {
        super(destination, endpoint, session);
    }
    
    @Override
    public Topic getTopic() throws JMSException {
        return (Topic)super.getDestination();
    }
    
    @Override
    public void publish(final Message message) throws JMSException {
        super.send(message);
    }
    
    @Override
    public void publish(final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        super.send(message, deliveryMode, priority, timeToLive);
    }
    
    @Override
    public void publish(final Topic topic, final Message message) throws JMSException {
        super.send(topic, message);
    }
    
    @Override
    public void publish(final Topic topic, final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        super.send(topic, message, deliveryMode, priority, timeToLive);
    }
}
