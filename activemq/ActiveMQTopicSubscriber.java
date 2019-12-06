// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Topic;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import javax.jms.TopicSubscriber;

public class ActiveMQTopicSubscriber extends ActiveMQMessageConsumer implements TopicSubscriber
{
    protected ActiveMQTopicSubscriber(final ActiveMQSession theSession, final ConsumerId consumerId, final ActiveMQDestination dest, final String name, final String selector, final int prefetch, final int maximumPendingMessageCount, final boolean noLocalValue, final boolean browserValue, final boolean asyncDispatch) throws JMSException {
        super(theSession, consumerId, dest, name, selector, prefetch, maximumPendingMessageCount, noLocalValue, browserValue, asyncDispatch, null);
    }
    
    @Override
    public Topic getTopic() throws JMSException {
        this.checkClosed();
        return (Topic)super.getDestination();
    }
    
    @Override
    public boolean getNoLocal() throws JMSException {
        this.checkClosed();
        return super.isNoLocal();
    }
}
