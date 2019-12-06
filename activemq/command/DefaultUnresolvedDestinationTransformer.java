// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.lang.reflect.Method;
import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.Queue;
import javax.jms.Destination;

public class DefaultUnresolvedDestinationTransformer implements UnresolvedDestinationTransformer
{
    @Override
    public ActiveMQDestination transform(final Destination dest) throws JMSException {
        final String queueName = ((Queue)dest).getQueueName();
        final String topicName = ((Topic)dest).getTopicName();
        if (queueName == null && topicName == null) {
            throw new JMSException("Unresolvable destination: Both queue and topic names are null: " + dest);
        }
        try {
            final Method isQueueMethod = dest.getClass().getMethod("isQueue", (Class<?>[])new Class[0]);
            final Method isTopicMethod = dest.getClass().getMethod("isTopic", (Class<?>[])new Class[0]);
            final Boolean isQueue = (Boolean)isQueueMethod.invoke(dest, new Object[0]);
            final Boolean isTopic = (Boolean)isTopicMethod.invoke(dest, new Object[0]);
            if (isQueue) {
                return new ActiveMQQueue(queueName);
            }
            if (isTopic) {
                return new ActiveMQTopic(topicName);
            }
            throw new JMSException("Unresolvable destination: Neither Queue nor Topic: " + dest);
        }
        catch (Exception e) {
            throw new JMSException("Unresolvable destination: " + e.getMessage() + ": " + dest);
        }
    }
    
    @Override
    public ActiveMQDestination transform(final String dest) throws JMSException {
        return new ActiveMQQueue(dest);
    }
}
