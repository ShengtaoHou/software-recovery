// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;
import java.io.IOException;
import org.apache.activemq.util.JMSExceptionSupport;

public class NoLocalExpression implements BooleanExpression
{
    private final String connectionId;
    
    public NoLocalExpression(final String connectionId) {
        this.connectionId = connectionId;
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        try {
            if (message.isDropped()) {
                return false;
            }
            final String messageConnectionId = message.getMessage().getProducerId().getConnectionId();
            return !this.connectionId.equals(messageConnectionId);
        }
        catch (IOException e) {
            throw JMSExceptionSupport.create(e);
        }
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        return this.matches(message) ? Boolean.TRUE : Boolean.FALSE;
    }
}
