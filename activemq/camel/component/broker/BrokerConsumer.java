// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.camel.Processor;
import org.apache.camel.Endpoint;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.activemq.broker.inteceptor.MessageInterceptor;
import org.apache.camel.impl.DefaultConsumer;

public class BrokerConsumer extends DefaultConsumer implements MessageInterceptor
{
    private final JmsBinding jmsBinding;
    
    public BrokerConsumer(final Endpoint endpoint, final Processor processor) {
        super(endpoint, processor);
        this.jmsBinding = new JmsBinding();
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        ((BrokerEndpoint)this.getEndpoint()).addMessageInterceptor(this);
    }
    
    protected void doStop() throws Exception {
        ((BrokerEndpoint)this.getEndpoint()).removeMessageInterceptor(this);
        super.doStop();
    }
    
    public void intercept(final ProducerBrokerExchange producerExchange, final Message message) {
        final Exchange exchange = this.getEndpoint().createExchange(ExchangePattern.InOnly);
        exchange.setIn((org.apache.camel.Message)new BrokerJmsMessage((javax.jms.Message)message, this.jmsBinding));
        exchange.setProperty("CamelBinding", (Object)this.jmsBinding);
        exchange.setProperty("producerBrokerExchange", (Object)producerExchange);
        try {
            this.getProcessor().process(exchange);
        }
        catch (Exception e) {
            exchange.setException((Throwable)e);
        }
        if (exchange.getException() != null) {
            this.getExceptionHandler().handleException("Error processing intercepted message: " + message, exchange, (Throwable)exchange.getException());
        }
    }
}
