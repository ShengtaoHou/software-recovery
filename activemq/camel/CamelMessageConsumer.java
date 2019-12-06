// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.IllegalStateException;
import org.apache.camel.Processor;
import javax.jms.Session;
import org.apache.camel.Exchange;
import javax.jms.Message;
import org.apache.activemq.util.JMSExceptionSupport;
import javax.jms.JMSException;
import org.apache.camel.PollingConsumer;
import org.apache.camel.Consumer;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import javax.jms.MessageConsumer;

public class CamelMessageConsumer implements MessageConsumer
{
    private final CamelDestination destination;
    private final Endpoint endpoint;
    private final ActiveMQSession session;
    private final String messageSelector;
    private final boolean noLocal;
    private MessageListener messageListener;
    private Consumer consumer;
    private PollingConsumer pollingConsumer;
    private boolean closed;
    
    public CamelMessageConsumer(final CamelDestination destination, final Endpoint endpoint, final ActiveMQSession session, final String messageSelector, final boolean noLocal) {
        this.destination = destination;
        this.endpoint = endpoint;
        this.session = session;
        this.messageSelector = messageSelector;
        this.noLocal = noLocal;
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.closed) {
            this.closed = true;
            try {
                if (this.consumer != null) {
                    this.consumer.stop();
                }
                if (this.pollingConsumer != null) {
                    this.pollingConsumer.stop();
                }
            }
            catch (JMSException e) {
                throw e;
            }
            catch (Exception e2) {
                throw JMSExceptionSupport.create(e2);
            }
        }
    }
    
    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.messageListener;
    }
    
    @Override
    public void setMessageListener(final MessageListener messageListener) throws JMSException {
        this.messageListener = messageListener;
        if (messageListener != null && this.consumer == null) {
            this.consumer = this.createConsumer();
        }
    }
    
    @Override
    public Message receive() throws JMSException {
        final Exchange exchange = this.getPollingConsumer().receive();
        return this.createMessage(exchange);
    }
    
    @Override
    public Message receive(final long timeoutMillis) throws JMSException {
        final Exchange exchange = this.getPollingConsumer().receive(timeoutMillis);
        return this.createMessage(exchange);
    }
    
    @Override
    public Message receiveNoWait() throws JMSException {
        final Exchange exchange = this.getPollingConsumer().receiveNoWait();
        return this.createMessage(exchange);
    }
    
    public CamelDestination getDestination() {
        return this.destination;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    @Override
    public String getMessageSelector() {
        return this.messageSelector;
    }
    
    public boolean isNoLocal() {
        return this.noLocal;
    }
    
    public ActiveMQSession getSession() {
        return this.session;
    }
    
    protected PollingConsumer getPollingConsumer() throws JMSException {
        try {
            if (this.pollingConsumer == null) {
                (this.pollingConsumer = this.endpoint.createPollingConsumer()).start();
            }
            return this.pollingConsumer;
        }
        catch (JMSException e) {
            throw e;
        }
        catch (Exception e2) {
            throw JMSExceptionSupport.create(e2);
        }
    }
    
    protected Message createMessage(final Exchange exchange) throws JMSException {
        if (exchange != null) {
            final Message message = this.destination.getBinding().makeJmsMessage(exchange, (Session)this.session);
            return message;
        }
        return null;
    }
    
    protected Consumer createConsumer() throws JMSException {
        try {
            final Consumer answer = this.endpoint.createConsumer((Processor)new Processor() {
                public void process(final Exchange exchange) throws Exception {
                    final Message message = CamelMessageConsumer.this.createMessage(exchange);
                    CamelMessageConsumer.this.getMessageListener().onMessage(message);
                }
            });
            answer.start();
            return answer;
        }
        catch (JMSException e) {
            throw e;
        }
        catch (Exception e2) {
            throw JMSExceptionSupport.create(e2);
        }
    }
    
    protected void checkClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException("The producer is closed");
        }
    }
}
