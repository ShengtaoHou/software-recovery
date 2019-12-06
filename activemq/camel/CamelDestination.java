// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import org.apache.activemq.ActiveMQConnection;
import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;
import javax.jms.MessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.CamelContextAware;
import org.apache.activemq.CustomDestination;

public class CamelDestination implements CustomDestination, CamelContextAware
{
    private String uri;
    private Endpoint endpoint;
    private CamelContext camelContext;
    private JmsBinding binding;
    
    public CamelDestination() {
        this.binding = new JmsBinding(new JmsEndpoint());
    }
    
    public CamelDestination(final String uri) {
        this.binding = new JmsBinding(new JmsEndpoint());
        this.uri = uri;
    }
    
    @Override
    public String toString() {
        return this.uri.toString();
    }
    
    @Override
    public MessageConsumer createConsumer(final ActiveMQSession session, final String messageSelector) {
        return this.createConsumer(session, messageSelector, false);
    }
    
    @Override
    public MessageConsumer createConsumer(final ActiveMQSession session, final String messageSelector, final boolean noLocal) {
        return new CamelMessageConsumer(this, this.resolveEndpoint(session), session, messageSelector, noLocal);
    }
    
    @Override
    public TopicSubscriber createSubscriber(final ActiveMQSession session, final String messageSelector, final boolean noLocal) {
        return this.createDurableSubscriber(session, null, messageSelector, noLocal);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final ActiveMQSession session, final String name, final String messageSelector, final boolean noLocal) {
        throw new UnsupportedOperationException("This destination is not a Topic: " + this);
    }
    
    @Override
    public QueueReceiver createReceiver(final ActiveMQSession session, final String messageSelector) {
        throw new UnsupportedOperationException("This destination is not a Queue: " + this);
    }
    
    @Override
    public MessageProducer createProducer(final ActiveMQSession session) throws JMSException {
        return new CamelMessageProducer(this, this.resolveEndpoint(session), session);
    }
    
    @Override
    public TopicPublisher createPublisher(final ActiveMQSession session) throws JMSException {
        throw new UnsupportedOperationException("This destination is not a Topic: " + this);
    }
    
    @Override
    public QueueSender createSender(final ActiveMQSession session) throws JMSException {
        throw new UnsupportedOperationException("This destination is not a Queue: " + this);
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void setUri(final String uri) {
        this.uri = uri;
    }
    
    public Endpoint getEndpoint() {
        return this.endpoint;
    }
    
    public void setEndpoint(final Endpoint endpoint) {
        this.endpoint = endpoint;
    }
    
    public CamelContext getCamelContext() {
        return this.camelContext;
    }
    
    public void setCamelContext(final CamelContext camelContext) {
        this.camelContext = camelContext;
    }
    
    public JmsBinding getBinding() {
        return this.binding;
    }
    
    public void setBinding(final JmsBinding binding) {
        this.binding = binding;
    }
    
    protected Endpoint resolveEndpoint(final ActiveMQSession session) {
        Endpoint answer = this.getEndpoint();
        if (answer == null) {
            answer = this.resolveCamelContext(session).getEndpoint(this.getUri());
            if (answer == null) {
                throw new IllegalArgumentException("No endpoint could be found for URI: " + this.getUri());
            }
        }
        return answer;
    }
    
    protected CamelContext resolveCamelContext(final ActiveMQSession session) {
        CamelContext answer = this.getCamelContext();
        if (answer == null) {
            final ActiveMQConnection connection = session.getConnection();
            if (connection instanceof CamelConnection) {
                final CamelConnection camelConnection = (CamelConnection)connection;
                answer = camelConnection.getCamelContext();
            }
        }
        if (answer == null) {
            throw new IllegalArgumentException("No CamelContext has been configured");
        }
        return answer;
    }
}
