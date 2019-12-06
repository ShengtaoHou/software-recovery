// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.IllegalStateException;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.ExchangePattern;
import org.apache.camel.util.ObjectHelper;
import javax.jms.Message;
import javax.jms.Destination;
import org.apache.activemq.util.JMSExceptionSupport;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.Endpoint;
import org.apache.camel.Producer;
import org.apache.activemq.ActiveMQMessageProducerSupport;

public class CamelMessageProducer extends ActiveMQMessageProducerSupport
{
    protected Producer producer;
    private final CamelDestination destination;
    private final Endpoint endpoint;
    private boolean closed;
    
    public CamelMessageProducer(final CamelDestination destination, final Endpoint endpoint, final ActiveMQSession session) throws JMSException {
        super(session);
        this.destination = destination;
        this.endpoint = endpoint;
        try {
            this.producer = endpoint.createProducer();
        }
        catch (JMSException e) {
            throw e;
        }
        catch (Exception e2) {
            throw JMSExceptionSupport.create(e2);
        }
    }
    
    @Override
    public CamelDestination getDestination() throws JMSException {
        return this.destination;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.closed) {
            this.closed = true;
            try {
                this.producer.stop();
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
    public void send(final Destination destination, final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        CamelDestination camelDestination = null;
        if (ObjectHelper.equal((Object)destination, (Object)this.destination)) {
            camelDestination = this.destination;
            try {
                final Exchange exchange = this.endpoint.createExchange(ExchangePattern.InOnly);
                exchange.setIn((org.apache.camel.Message)new JmsMessage(message, camelDestination.getBinding()));
                this.producer.process(exchange);
            }
            catch (JMSException e) {
                throw e;
            }
            catch (Exception e2) {
                throw JMSExceptionSupport.create(e2);
            }
            return;
        }
        throw new IllegalArgumentException("Invalid destination setting: " + destination + " when expected: " + this.destination);
    }
    
    @Override
    protected void checkClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException("The producer is closed");
        }
    }
}
