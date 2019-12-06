// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.inteceptor;

import java.util.Iterator;
import java.util.Set;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.broker.BrokerFilter;

class MessageInterceptorFilter extends BrokerFilter
{
    private DestinationMap interceptorMap;
    
    MessageInterceptorFilter(final Broker next) {
        super(next);
        this.interceptorMap = new DestinationMap();
    }
    
    MessageInterceptor addMessageInterceptor(final String destinationName, final MessageInterceptor messageInterceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        this.interceptorMap.put(activeMQDestination, messageInterceptor);
        return messageInterceptor;
    }
    
    void removeMessageInterceptor(final String destinationName, final MessageInterceptor interceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        this.interceptorMap.remove(activeMQDestination, interceptor);
    }
    
    MessageInterceptor addMessageInterceptorForQueue(final String destinationName, final MessageInterceptor messageInterceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        this.interceptorMap.put(activeMQDestination, messageInterceptor);
        return messageInterceptor;
    }
    
    void removeMessageInterceptorForQueue(final String destinationName, final MessageInterceptor interceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)1);
        this.interceptorMap.remove(activeMQDestination, interceptor);
    }
    
    MessageInterceptor addMessageInterceptorForTopic(final String destinationName, final MessageInterceptor messageInterceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)2);
        this.interceptorMap.put(activeMQDestination, messageInterceptor);
        return messageInterceptor;
    }
    
    void removeMessageInterceptorForTopic(final String destinationName, final MessageInterceptor interceptor) {
        final ActiveMQDestination activeMQDestination = ActiveMQDestination.createDestination(destinationName, (byte)2);
        this.interceptorMap.remove(activeMQDestination, interceptor);
    }
    
    MessageInterceptor addMessageInterceptor(final ActiveMQDestination activeMQDestination, final MessageInterceptor messageInterceptor) {
        this.interceptorMap.put(activeMQDestination, messageInterceptor);
        return messageInterceptor;
    }
    
    void removeMessageInterceptor(final ActiveMQDestination activeMQDestination, final MessageInterceptor interceptor) {
        this.interceptorMap.remove(activeMQDestination, interceptor);
    }
    
    void injectMessage(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        ProducerBrokerExchange pe = producerExchange;
        if (pe == null) {
            pe = new ProducerBrokerExchange();
            final ConnectionContext cc = new ConnectionContext();
            cc.setBroker(this.getRoot());
            pe.setConnectionContext(cc);
            pe.setMutable(true);
            pe.setProducerState(new ProducerState(new ProducerInfo()));
        }
        super.send(pe, messageSend);
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message messageSend) throws Exception {
        final ActiveMQDestination activeMQDestination = messageSend.getDestination();
        if (!this.interceptorMap.isEmpty() && activeMQDestination != null) {
            final Set<MessageInterceptor> set = (Set<MessageInterceptor>)this.interceptorMap.get(activeMQDestination);
            if (set != null && !set.isEmpty()) {
                for (final MessageInterceptor mi : set) {
                    mi.intercept(producerExchange, messageSend);
                }
            }
            else {
                super.send(producerExchange, messageSend);
            }
        }
        else {
            super.send(producerExchange, messageSend);
        }
    }
}
