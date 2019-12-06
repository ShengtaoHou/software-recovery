// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.camel;

import javax.jms.TopicSubscriber;
import javax.jms.TopicPublisher;
import org.apache.activemq.ActiveMQSession;
import javax.jms.JMSException;
import javax.jms.Topic;

public class CamelTopic extends CamelDestination implements Topic
{
    public CamelTopic(final String uri) {
        super(uri);
    }
    
    @Override
    public String getTopicName() throws JMSException {
        return this.getUri();
    }
    
    @Override
    public TopicPublisher createPublisher(final ActiveMQSession session) throws JMSException {
        return new CamelTopicPublisher(this, this.resolveEndpoint(session), session);
    }
    
    @Override
    public TopicSubscriber createDurableSubscriber(final ActiveMQSession session, final String name, final String messageSelector, final boolean noLocal) {
        return new CamelTopicSubscriber(this, this.resolveEndpoint(session), session, name, messageSelector, noLocal);
    }
}
