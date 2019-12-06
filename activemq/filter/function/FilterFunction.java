// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;

public interface FilterFunction
{
    boolean isValid(final FunctionCallExpression p0);
    
    boolean returnsBoolean(final FunctionCallExpression p0);
    
    Object evaluate(final FunctionCallExpression p0, final MessageEvaluationContext p1) throws JMSException;
}
