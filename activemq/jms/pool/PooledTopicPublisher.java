// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Destination;
import javax.jms.TopicPublisher;

public class PooledTopicPublisher extends PooledProducer implements TopicPublisher
{
    public PooledTopicPublisher(final TopicPublisher messageProducer, final Destination destination) throws JMSException {
        super(messageProducer, destination);
    }
    
    @Override
    public Topic getTopic() throws JMSException {
        return (Topic)this.getDestination();
    }
    
    @Override
    public void publish(final Message message) throws JMSException {
        this.getTopicPublisher().publish((Topic)this.getDestination(), message);
    }
    
    @Override
    public void publish(final Message message, final int i, final int i1, final long l) throws JMSException {
        this.getTopicPublisher().publish((Topic)this.getDestination(), message, i, i1, l);
    }
    
    @Override
    public void publish(final Topic topic, final Message message) throws JMSException {
        this.getTopicPublisher().publish(topic, message);
    }
    
    @Override
    public void publish(final Topic topic, final Message message, final int i, final int i1, final long l) throws JMSException {
        this.getTopicPublisher().publish(topic, message, i, i1, l);
    }
    
    protected TopicPublisher getTopicPublisher() {
        return (TopicPublisher)this.getMessageProducer();
    }
}
