// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.MessageConsumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public interface MessageTransformer
{
    Message producerTransform(final Session p0, final MessageProducer p1, final Message p2) throws JMSException;
    
    Message consumerTransform(final Session p0, final MessageConsumer p1, final Message p2) throws JMSException;
}
