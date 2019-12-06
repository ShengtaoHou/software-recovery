// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.JMSException;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.TopicPublisher;

public class ActiveMQTopicPublisher extends ActiveMQMessageProducer implements TopicPublisher
{
    protected ActiveMQTopicPublisher(final ActiveMQSession session, final ActiveMQDestination destination, final int sendTimeout) throws JMSException {
        super(session, session.getNextProducerId(), destination, sendTimeout);
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
