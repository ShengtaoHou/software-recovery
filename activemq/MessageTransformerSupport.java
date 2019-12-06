// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.JMSException;
import javax.jms.Message;

public abstract class MessageTransformerSupport implements MessageTransformer
{
    protected void copyProperties(final Message fromMessage, final Message toMesage) throws JMSException {
        ActiveMQMessageTransformation.copyProperties(fromMessage, toMesage);
    }
}
