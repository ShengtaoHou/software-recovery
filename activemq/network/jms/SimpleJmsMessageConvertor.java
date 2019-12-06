// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class SimpleJmsMessageConvertor implements JmsMesageConvertor
{
    @Override
    public Message convert(final Message message) throws JMSException {
        return message;
    }
    
    @Override
    public Message convert(final Message message, final Destination replyTo) throws JMSException {
        final Message msg = this.convert(message);
        if (replyTo != null) {
            msg.setJMSReplyTo(replyTo);
        }
        else {
            msg.setJMSReplyTo(null);
        }
        return msg;
    }
    
    @Override
    public void setConnection(final Connection connection) {
    }
}
