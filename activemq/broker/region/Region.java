// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.command.ConsumerControl;
import java.util.Set;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.MessagePull;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.broker.ConsumerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ConsumerInfo;
import java.util.Map;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.Service;

public interface Region extends Service
{
    Destination addDestination(final ConnectionContext p0, final ActiveMQDestination p1, final boolean p2) throws Exception;
    
    void removeDestination(final ConnectionContext p0, final ActiveMQDestination p1, final long p2) throws Exception;
    
    Map<ActiveMQDestination, Destination> getDestinationMap();
    
    Subscription addConsumer(final ConnectionContext p0, final ConsumerInfo p1) throws Exception;
    
    void removeConsumer(final ConnectionContext p0, final ConsumerInfo p1) throws Exception;
    
    void addProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    void removeProducer(final ConnectionContext p0, final ProducerInfo p1) throws Exception;
    
    void removeSubscription(final ConnectionContext p0, final RemoveSubscriptionInfo p1) throws Exception;
    
    void send(final ProducerBrokerExchange p0, final Message p1) throws Exception;
    
    void acknowledge(final ConsumerBrokerExchange p0, final MessageAck p1) throws Exception;
    
    Response messagePull(final ConnectionContext p0, final MessagePull p1) throws Exception;
    
    void processDispatchNotification(final MessageDispatchNotification p0) throws Exception;
    
    void gc();
    
    Set<Destination> getDestinations(final ActiveMQDestination p0);
    
    void processConsumerControl(final ConsumerBrokerExchange p0, final ConsumerControl p1);
    
    void reapplyInterceptor();
}
