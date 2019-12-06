// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageConsumer;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Topic;

class TopicBridge extends DestinationBridge
{
    protected Topic consumerTopic;
    protected Topic producerTopic;
    protected TopicSession consumerSession;
    protected TopicSession producerSession;
    protected String consumerName;
    protected String selector;
    protected TopicPublisher producer;
    protected TopicConnection consumerConnection;
    protected TopicConnection producerConnection;
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.consumerSession != null) {
            this.consumerSession.close();
        }
        if (this.producerSession != null) {
            this.producerSession.close();
        }
    }
    
    @Override
    protected MessageConsumer createConsumer() throws JMSException {
        if (this.consumerConnection == null) {
            return null;
        }
        this.consumerSession = this.consumerConnection.createTopicSession(false, 2);
        MessageConsumer consumer = null;
        if (this.consumerName != null && this.consumerName.length() > 0) {
            if (this.selector != null && this.selector.length() > 0) {
                consumer = this.consumerSession.createDurableSubscriber(this.consumerTopic, this.consumerName, this.selector, false);
            }
            else {
                consumer = this.consumerSession.createDurableSubscriber(this.consumerTopic, this.consumerName);
            }
        }
        else if (this.selector != null && this.selector.length() > 0) {
            consumer = this.consumerSession.createSubscriber(this.consumerTopic, this.selector, false);
        }
        else {
            consumer = this.consumerSession.createSubscriber(this.consumerTopic);
        }
        consumer.setMessageListener(this);
        return consumer;
    }
    
    @Override
    protected synchronized MessageProducer createProducer() throws JMSException {
        if (this.producerConnection == null) {
            return null;
        }
        this.producerSession = this.producerConnection.createTopicSession(false, 1);
        return this.producer = this.producerSession.createPublisher(null);
    }
    
    @Override
    protected synchronized void sendMessage(final Message message) throws JMSException {
        if (this.producer == null && this.createProducer() == null) {
            throw new JMSException("Producer for remote queue not available.");
        }
        try {
            this.producer.publish(this.producerTopic, message);
        }
        catch (JMSException e) {
            this.producer = null;
            throw e;
        }
    }
    
    public TopicConnection getConsumerConnection() {
        return this.consumerConnection;
    }
    
    public void setConsumerConnection(final TopicConnection consumerConnection) {
        this.consumerConnection = consumerConnection;
        if (this.started.get()) {
            try {
                this.createConsumer();
            }
            catch (Exception e) {
                this.jmsConnector.handleConnectionFailure(this.getConnnectionForConsumer());
            }
        }
    }
    
    public String getConsumerName() {
        return this.consumerName;
    }
    
    public void setConsumerName(final String consumerName) {
        this.consumerName = consumerName;
    }
    
    public Topic getConsumerTopic() {
        return this.consumerTopic;
    }
    
    public void setConsumerTopic(final Topic consumerTopic) {
        this.consumerTopic = consumerTopic;
    }
    
    public TopicConnection getProducerConnection() {
        return this.producerConnection;
    }
    
    public void setProducerConnection(final TopicConnection producerConnection) {
        this.producerConnection = producerConnection;
    }
    
    public Topic getProducerTopic() {
        return this.producerTopic;
    }
    
    public void setProducerTopic(final Topic producerTopic) {
        this.producerTopic = producerTopic;
    }
    
    public String getSelector() {
        return this.selector;
    }
    
    public void setSelector(final String selector) {
        this.selector = selector;
    }
    
    @Override
    protected Connection getConnnectionForConsumer() {
        return this.getConsumerConnection();
    }
    
    @Override
    protected Connection getConnectionForProducer() {
        return this.getProducerConnection();
    }
}
