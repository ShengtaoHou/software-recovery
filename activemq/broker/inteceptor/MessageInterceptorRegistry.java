// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.inteceptor;

import org.slf4j.LoggerFactory;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.MutableBrokerFilter;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.HashMap;
import org.apache.activemq.broker.BrokerRegistry;
import java.util.Map;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;

public class MessageInterceptorRegistry
{
    private static final Logger LOG;
    private static final MessageInterceptorRegistry INSTANCE;
    private final BrokerService brokerService;
    private MessageInterceptorFilter filter;
    private final Map<BrokerService, MessageInterceptorRegistry> messageInterceptorRegistryMap;
    
    public static MessageInterceptorRegistry getInstance() {
        return MessageInterceptorRegistry.INSTANCE;
    }
    
    public MessageInterceptorRegistry get(final String brokerName) {
        final BrokerService brokerService = BrokerRegistry.getInstance().lookup(brokerName);
        return this.get(brokerService);
    }
    
    public synchronized MessageInterceptorRegistry get(final BrokerService brokerService) {
        MessageInterceptorRegistry result = this.messageInterceptorRegistryMap.get(brokerService);
        if (result == null) {
            result = new MessageInterceptorRegistry(brokerService);
            this.messageInterceptorRegistryMap.put(brokerService, result);
        }
        return result;
    }
    
    private MessageInterceptorRegistry() {
        this.messageInterceptorRegistryMap = new HashMap<BrokerService, MessageInterceptorRegistry>();
        this.brokerService = BrokerRegistry.getInstance().findFirst();
        this.messageInterceptorRegistryMap.put(this.brokerService, this);
    }
    
    private MessageInterceptorRegistry(final BrokerService brokerService) {
        this.messageInterceptorRegistryMap = new HashMap<BrokerService, MessageInterceptorRegistry>();
        this.brokerService = brokerService;
    }
    
    public MessageInterceptor addMessageInterceptor(final String destinationName, final MessageInterceptor messageInterceptor) {
        return this.getFilter().addMessageInterceptor(destinationName, messageInterceptor);
    }
    
    public void removeMessageInterceptor(final String destinationName, final MessageInterceptor messageInterceptor) {
        this.getFilter().removeMessageInterceptor(destinationName, messageInterceptor);
    }
    
    public MessageInterceptor addMessageInterceptorForQueue(final String destinationName, final MessageInterceptor messageInterceptor) {
        return this.getFilter().addMessageInterceptorForQueue(destinationName, messageInterceptor);
    }
    
    public void removeMessageInterceptorForQueue(final String destinationName, final MessageInterceptor messageInterceptor) {
        this.getFilter().addMessageInterceptorForQueue(destinationName, messageInterceptor);
    }
    
    public MessageInterceptor addMessageInterceptorForTopic(final String destinationName, final MessageInterceptor messageInterceptor) {
        return this.getFilter().addMessageInterceptorForTopic(destinationName, messageInterceptor);
    }
    
    public void removeMessageInterceptorForTopic(final String destinationName, final MessageInterceptor messageInterceptor) {
        this.getFilter().removeMessageInterceptorForTopic(destinationName, messageInterceptor);
    }
    
    public MessageInterceptor addMessageInterceptor(final ActiveMQDestination activeMQDestination, final MessageInterceptor messageInterceptor) {
        return this.getFilter().addMessageInterceptor(activeMQDestination, messageInterceptor);
    }
    
    public void removeMessageInterceptor(final ActiveMQDestination activeMQDestination, final MessageInterceptor interceptor) {
        this.getFilter().removeMessageInterceptor(activeMQDestination, interceptor);
    }
    
    public void injectMessage(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        this.getFilter().injectMessage(producerExchange, messageSend);
    }
    
    private synchronized MessageInterceptorFilter getFilter() {
        if (this.filter == null) {
            try {
                final MutableBrokerFilter mutableBrokerFilter = (MutableBrokerFilter)this.brokerService.getBroker().getAdaptor(MutableBrokerFilter.class);
                final Broker next = mutableBrokerFilter.getNext();
                mutableBrokerFilter.setNext(this.filter = new MessageInterceptorFilter(next));
            }
            catch (Exception e) {
                MessageInterceptorRegistry.LOG.error("Failed to create MessageInterceptorFilter", e);
            }
        }
        return this.filter;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MessageInterceptorRegistry.class);
        INSTANCE = new MessageInterceptorRegistry();
    }
}
