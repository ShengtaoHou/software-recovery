// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import javax.jms.StreamMessage;
import javax.jms.TopicPublisher;
import javax.jms.MessageProducer;
import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Message;
import javax.jms.MapMessage;
import javax.jms.TopicSubscriber;
import javax.jms.Topic;
import javax.jms.InvalidDestinationException;
import javax.jms.MessageConsumer;
import javax.jms.Destination;
import javax.jms.BytesMessage;
import javax.jms.IllegalStateException;
import javax.jms.QueueBrowser;
import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.TopicSession;

public class ActiveMQTopicSession implements TopicSession
{
    private final TopicSession next;
    
    public ActiveMQTopicSession(final TopicSession next) {
        this.next = next;
    }
    
    @Override
    public void close() throws JMSException {
        this.next.close();
    }
    
    @Override
    public void commit() throws JMSException {
        this.next.commit();
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }
    
    @Override
    public QueueBrowser createBrowser(final Queue queue, final String messageSelector) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }
    
    @Override
    public BytesMessage createBytesMessage() throws JMSException {
        return this.next.createBytesMessage();
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            throw new InvalidDestinationException("Queues are not supported by a TopicSession");
        }
        return this.next.createConsumer(destination);
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector) throws JMSException {
        if (destination instanceof Queue) {
            throw new InvalidDestinationException("Queues are not supported by a TopicSession");
        }
        return this.next.createConsumer(destination, messageSelector);
    }
    
    @Override
    public MessageConsumer createConsumer(final Destination destination, final String messageSelector, final boolean noLocal) throws JMSException {
        if (destination instanceof Queue) {
            throw new InvalidDestinationException("Queues are not supported by a TopicSession");
        }
        return this.next.createConsumer(destination, messageSelector, noLocal);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String name) throws JMSException {
        return this.next.createDurableSubscriber(topic, name);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final Topic topic, final String name, final String messageSelector, final boolean noLocal) throws JMSException {
        return this.next.createDurableSubscriber(topic, name, messageSelector, noLocal);
    }
    
    @Override
    public MapMessage createMapMessage() throws JMSException {
        return this.next.createMapMessage();
    }
    
    @Override
    public Message createMessage() throws JMSException {
        return this.next.createMessage();
    }
    
    @Override
    public ObjectMessage createObjectMessage() throws JMSException {
        return this.next.createObjectMessage();
    }
    
    @Override
    public ObjectMessage createObjectMessage(final Serializable object) throws JMSException {
        return this.next.createObjectMessage(object);
    }
    
    @Override
    public MessageProducer createProducer(final Destination destination) throws JMSException {
        if (destination instanceof Queue) {
            throw new InvalidDestinationException("Queues are not supported by a TopicSession");
        }
        return this.next.createProducer(destination);
    }
    
    @Override
    public TopicPublisher createPublisher(final Topic topic) throws JMSException {
        return this.next.createPublisher(topic);
    }
    
    @Override
    public Queue createQueue(final String queueName) throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }
    
    @Override
    public StreamMessage createStreamMessage() throws JMSException {
        return this.next.createStreamMessage();
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic) throws JMSException {
        return this.next.createSubscriber(topic);
    }
    
    @Override
    public TopicSubscriber createSubscriber(final Topic topic, final String messageSelector, final boolean noLocal) throws JMSException {
        return this.next.createSubscriber(topic, messageSelector, noLocal);
    }
    
    @Override
    public TemporaryQueue createTemporaryQueue() throws JMSException {
        throw new IllegalStateException("Operation not supported by a TopicSession");
    }
    
    @Override
    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return this.next.createTemporaryTopic();
    }
    
    @Override
    public TextMessage createTextMessage() throws JMSException {
        return this.next.createTextMessage();
    }
    
    @Override
    public TextMessage createTextMessage(final String text) throws JMSException {
        return this.next.createTextMessage(text);
    }
    
    @Override
    public Topic createTopic(final String topicName) throws JMSException {
        return this.next.createTopic(topicName);
    }
    
    @Override
    public boolean equals(final Object arg0) {
        return this == arg0 || this.next.equals(arg0);
    }
    
    @Override
    public int getAcknowledgeMode() throws JMSException {
        return this.next.getAcknowledgeMode();
    }
    
    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.next.getMessageListener();
    }
    
    @Override
    public boolean getTransacted() throws JMSException {
        return this.next.getTransacted();
    }
    
    @Override
    public int hashCode() {
        return this.next.hashCode();
    }
    
    @Override
    public void recover() throws JMSException {
        this.next.recover();
    }
    
    @Override
    public void rollback() throws JMSException {
        this.next.rollback();
    }
    
    @Override
    public void run() {
        this.next.run();
    }
    
    @Override
    public void setMessageListener(final MessageListener listener) throws JMSException {
        this.next.setMessageListener(listener);
    }
    
    @Override
    public String toString() {
        return this.next.toString();
    }
    
    @Override
    public void unsubscribe(final String name) throws JMSException {
        this.next.unsubscribe(name);
    }
    
    public TopicSession getNext() {
        return this.next;
    }
}
