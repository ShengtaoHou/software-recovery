// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import java.util.Arrays;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;

public class splitFunction implements FilterFunction
{
    @Override
    public boolean isValid(final FunctionCallExpression expr) {
        return expr.getNumArguments() >= 2 && expr.getNumArguments() <= 3;
    }
    
    @Override
    public boolean returnsBoolean(final FunctionCallExpression expr) {
        return false;
    }
    
    @Override
    public Object evaluate(final FunctionCallExpression expr, final MessageEvaluationContext message_ctx) throws JMSException {
        final String src = (String)expr.getArgument(0).evaluate(message_ctx);
        final String split_pat = (String)expr.getArgument(1).evaluate(message_ctx);
        String[] result;
        if (expr.getNumArguments() > 2) {
            final Integer limit = (Integer)expr.getArgument(2).evaluate(message_ctx);
            result = src.split(split_pat, limit);
        }
        else {
            result = src.split(split_pat);
        }
        return Arrays.asList(result);
    }
}
