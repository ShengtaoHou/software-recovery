// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.QueueSender;
import javax.jms.TopicPublisher;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueReceiver;
import javax.jms.TopicSubscriber;
import javax.jms.MessageConsumer;
import javax.jms.Destination;

public interface CustomDestination extends Destination
{
    MessageConsumer createConsumer(final ActiveMQSession p0, final String p1);
    
    MessageConsumer createConsumer(final ActiveMQSession p0, final String p1, final boolean p2);
    
    TopicSubscriber createSubscriber(final ActiveMQSession p0, final String p1, final boolean p2);
    
    TopicSubscriber createDurableSubscriber(final ActiveMQSession p0, final String p1, final String p2, final boolean p3);
    
    QueueReceiver createReceiver(final ActiveMQSession p0, final String p1);
    
    MessageProducer createProducer(final ActiveMQSession p0) throws JMSException;
    
    TopicPublisher createPublisher(final ActiveMQSession p0) throws JMSException;
    
    QueueSender createSender(final ActiveMQSession p0) throws JMSException;
}
