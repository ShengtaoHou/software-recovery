// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.converter;

import org.apache.activemq.command.ActiveMQObjectMessage;
import java.io.Serializable;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.Processor;
import javax.jms.MessageListener;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.Converter;

@Converter
public class ActiveMQMessageConverter
{
    private JmsBinding binding;
    
    public ActiveMQMessageConverter() {
        this.binding = new JmsBinding();
    }
    
    @Converter
    public ActiveMQMessage toMessage(final Exchange exchange) throws JMSException {
        final ActiveMQMessage message = createActiveMQMessage(exchange);
        this.getBinding().appendJmsProperties((Message)message, exchange);
        return message;
    }
    
    @Converter
    public Processor toProcessor(final MessageListener listener) {
        return (Processor)new Processor() {
            public void process(final Exchange exchange) throws Exception {
                final Message message = ActiveMQMessageConverter.this.toMessage(exchange);
                listener.onMessage(message);
            }
            
            @Override
            public String toString() {
                return "Processor of MessageListener: " + listener;
            }
        };
    }
    
    private static ActiveMQMessage createActiveMQMessage(final Exchange exchange) throws JMSException {
        final Object body = exchange.getIn().getBody();
        if (body instanceof String) {
            final ActiveMQTextMessage answer = new ActiveMQTextMessage();
            answer.setText((String)body);
            return answer;
        }
        if (body instanceof Serializable) {
            final ActiveMQObjectMessage answer2 = new ActiveMQObjectMessage();
            answer2.setObject((Serializable)body);
            return answer2;
        }
        return new ActiveMQMessage();
    }
    
    public JmsBinding getBinding() {
        return this.binding;
    }
    
    public void setBinding(final JmsBinding binding) {
        this.binding = binding;
    }
}
