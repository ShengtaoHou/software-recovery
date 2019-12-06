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
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Queue;

class QueueBridge extends DestinationBridge
{
    protected Queue consumerQueue;
    protected Queue producerQueue;
    protected QueueSession consumerSession;
    protected QueueSession producerSession;
    protected String selector;
    protected QueueSender producer;
    protected QueueConnection consumerConnection;
    protected QueueConnection producerConnection;
    
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
        this.consumerSession = this.consumerConnection.createQueueSession(false, 2);
        MessageConsumer consumer = null;
        if (this.selector != null && this.selector.length() > 0) {
            consumer = this.consumerSession.createReceiver(this.consumerQueue, this.selector);
        }
        else {
            consumer = this.consumerSession.createReceiver(this.consumerQueue);
        }
        consumer.setMessageListener(this);
        return consumer;
    }
    
    @Override
    protected synchronized MessageProducer createProducer() throws JMSException {
        if (this.producerConnection == null) {
            return null;
        }
        this.producerSession = this.producerConnection.createQueueSession(false, 1);
        return this.producer = this.producerSession.createSender(null);
    }
    
    @Override
    protected synchronized void sendMessage(final Message message) throws JMSException {
        if (this.producer == null && this.createProducer() == null) {
            throw new JMSException("Producer for remote queue not available.");
        }
        try {
            this.producer.send(this.producerQueue, message);
        }
        catch (JMSException e) {
            this.producer = null;
            throw e;
        }
    }
    
    public QueueConnection getConsumerConnection() {
        return this.consumerConnection;
    }
    
    public void setConsumerConnection(final QueueConnection consumerConnection) {
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
    
    public Queue getConsumerQueue() {
        return this.consumerQueue;
    }
    
    public void setConsumerQueue(final Queue consumerQueue) {
        this.consumerQueue = consumerQueue;
    }
    
    public QueueConnection getProducerConnection() {
        return this.producerConnection;
    }
    
    public void setProducerConnection(final QueueConnection producerConnection) {
        this.producerConnection = producerConnection;
    }
    
    public Queue getProducerQueue() {
        return this.producerQueue;
    }
    
    public void setProducerQueue(final Queue producerQueue) {
        this.producerQueue = producerQueue;
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
