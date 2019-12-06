// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel.component.broker;

import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import java.util.Iterator;
import org.apache.camel.Endpoint;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.camel.Component;
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.apache.activemq.broker.inteceptor.MessageInterceptor;
import java.util.List;
import org.apache.camel.spi.UriPath;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.inteceptor.MessageInterceptorRegistry;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.Service;
import org.apache.camel.MultipleConsumersSupport;
import org.apache.camel.impl.DefaultEndpoint;

@ManagedResource(description = "Managed Camel Broker Endpoint")
@UriEndpoint(scheme = "broker", consumerClass = BrokerConsumer.class)
public class BrokerEndpoint extends DefaultEndpoint implements MultipleConsumersSupport, Service
{
    static final String PRODUCER_BROKER_EXCHANGE = "producerBrokerExchange";
    @UriParam
    private final BrokerConfiguration configuration;
    private MessageInterceptorRegistry messageInterceptorRegistry;
    @UriPath
    private final ActiveMQDestination destination;
    private List<MessageInterceptor> messageInterceptorList;
    
    public BrokerEndpoint(final String uri, final BrokerComponent component, final ActiveMQDestination destination, final BrokerConfiguration configuration) {
        super(UnsafeUriCharactersEncoder.encode(uri), (Component)component);
        this.messageInterceptorList = new CopyOnWriteArrayList<MessageInterceptor>();
        this.destination = destination;
        this.configuration = configuration;
    }
    
    public Producer createProducer() throws Exception {
        final BrokerProducer producer = new BrokerProducer(this);
        return (Producer)producer;
    }
    
    public Consumer createConsumer(final Processor processor) throws Exception {
        final BrokerConsumer consumer = new BrokerConsumer((Endpoint)this, processor);
        this.configureConsumer((Consumer)consumer);
        return (Consumer)consumer;
    }
    
    public boolean isSingleton() {
        return false;
    }
    
    public boolean isMultipleConsumersSupported() {
        return true;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    protected void doStart() throws Exception {
        super.doStart();
        this.messageInterceptorRegistry = MessageInterceptorRegistry.getInstance().get(this.configuration.getBrokerName());
        for (final MessageInterceptor messageInterceptor : this.messageInterceptorList) {
            this.addMessageInterceptor(messageInterceptor);
        }
        this.messageInterceptorList.clear();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    protected void addMessageInterceptor(final MessageInterceptor messageInterceptor) {
        if (this.isStarted()) {
            this.messageInterceptorRegistry.addMessageInterceptor(this.destination, messageInterceptor);
        }
        else {
            this.messageInterceptorList.add(messageInterceptor);
        }
    }
    
    protected void removeMessageInterceptor(final MessageInterceptor messageInterceptor) {
        this.messageInterceptorRegistry.removeMessageInterceptor(this.destination, messageInterceptor);
    }
    
    protected void inject(final ProducerBrokerExchange producerBrokerExchange, final Message message) throws Exception {
        ProducerBrokerExchange pbe = producerBrokerExchange;
        if (message != null) {
            message.setDestination(this.destination);
            if (producerBrokerExchange != null && producerBrokerExchange.getRegionDestination() != null && !producerBrokerExchange.getRegionDestination().getActiveMQDestination().equals(this.destination)) {
                pbe = null;
            }
            this.messageInterceptorRegistry.injectMessage(pbe, message);
        }
    }
}
