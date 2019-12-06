// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;

public class replaceFunction implements FilterFunction
{
    @Override
    public boolean isValid(final FunctionCallExpression expr) {
        return expr.getNumArguments() == 3;
    }
    
    @Override
    public boolean returnsBoolean(final FunctionCallExpression expr) {
        return false;
    }
    
    @Override
    public Object evaluate(final FunctionCallExpression expr, final MessageEvaluationContext message_ctx) throws JMSException {
        final String src = (String)expr.getArgument(0).evaluate(message_ctx);
        final String match_regex = (String)expr.getArgument(1).evaluate(message_ctx);
        final String repl_lit = (String)expr.getArgument(2).evaluate(message_ctx);
        final String result = src.replaceAll(match_regex, repl_lit);
        return result;
    }
}
