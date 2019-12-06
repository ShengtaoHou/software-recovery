// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import java.util.Iterator;
import org.apache.camel.converter.ObjectConverter;
import java.util.Map;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultAsyncProducer;

public class BrokerProducer extends DefaultAsyncProducer
{
    private final BrokerEndpoint brokerEndpoint;
    
    public BrokerProducer(final BrokerEndpoint endpoint) {
        super((Endpoint)endpoint);
        this.brokerEndpoint = endpoint;
    }
    
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        try {
            return this.processInOnly(exchange, callback);
        }
        catch (Throwable e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }
    
    protected boolean processInOnly(final Exchange exchange, final AsyncCallback callback) {
        try {
            final ActiveMQMessage message = this.getMessage(exchange);
            if (message != null) {
                message.setDestination(this.brokerEndpoint.getDestination());
                final ProducerBrokerExchange producerBrokerExchange = (ProducerBrokerExchange)exchange.getProperty("producerBrokerExchange");
                this.brokerEndpoint.inject(producerBrokerExchange, message);
            }
        }
        catch (Exception e) {
            exchange.setException((Throwable)e);
        }
        callback.done(true);
        return true;
    }
    
    private ActiveMQMessage getMessage(final Exchange exchange) throws Exception {
        org.apache.camel.Message camelMessage;
        if (exchange.hasOut()) {
            camelMessage = exchange.getOut();
        }
        else {
            camelMessage = exchange.getIn();
        }
        final Map<String, Object> headers = (Map<String, Object>)camelMessage.getHeaders();
        if (!(camelMessage instanceof JmsMessage)) {
            throw new IllegalStateException("Not the original message from the broker " + camelMessage);
        }
        final JmsMessage jmsMessage = (JmsMessage)camelMessage;
        if (jmsMessage.getJmsMessage() instanceof ActiveMQMessage) {
            final ActiveMQMessage result = (ActiveMQMessage)jmsMessage.getJmsMessage();
            this.setJmsHeaders(result, headers);
            return result;
        }
        throw new IllegalStateException("Not the original message from the broker " + jmsMessage.getJmsMessage());
    }
    
    private void setJmsHeaders(final ActiveMQMessage message, final Map<String, Object> headers) {
        message.setReadOnlyProperties(false);
        for (final Map.Entry<String, Object> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("JMSDeliveryMode")) {
                final Object value = entry.getValue();
                if (value instanceof Number) {
                    final Number number = (Number)value;
                    message.setJMSDeliveryMode(number.intValue());
                }
            }
            if (entry.getKey().equalsIgnoreCase("JmsPriority")) {
                final Integer value2 = ObjectConverter.toInteger(entry.getValue());
                if (value2 != null) {
                    message.setJMSPriority(value2);
                }
            }
            if (entry.getKey().equalsIgnoreCase("JMSTimestamp")) {
                final Long value3 = ObjectConverter.toLong(entry.getValue());
                if (value3 != null) {
                    message.setJMSTimestamp(value3);
                }
            }
            if (entry.getKey().equalsIgnoreCase("JMSExpiration")) {
                final Long value3 = ObjectConverter.toLong(entry.getValue());
                if (value3 != null) {
                    message.setJMSExpiration(value3);
                }
            }
            if (entry.getKey().equalsIgnoreCase("JMSRedelivered")) {
                message.setJMSRedelivered(ObjectConverter.toBool(entry.getValue()));
            }
            if (entry.getKey().equalsIgnoreCase("JMSType")) {
                final Object value = entry.getValue();
                if (value == null) {
                    continue;
                }
                message.setJMSType(value.toString());
            }
        }
    }
}
