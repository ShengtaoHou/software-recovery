// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import java.util.List;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;

public class inListFunction implements FilterFunction
{
    @Override
    public boolean isValid(final FunctionCallExpression expr) {
        return expr.getNumArguments() == 2;
    }
    
    @Override
    public boolean returnsBoolean(final FunctionCallExpression expr) {
        return true;
    }
    
    @Override
    public Object evaluate(final FunctionCallExpression expr, final MessageEvaluationContext message_ctx) throws JMSException {
        List arr;
        Object cand;
        int cur;
        boolean found_f;
        for (arr = (List)expr.getArgument(0).evaluate(message_ctx), cand = expr.getArgument(1).evaluate(message_ctx), cur = 0, found_f = false; cur < arr.size() && !found_f; found_f = arr.get(cur).equals(cand), ++cur) {}
        return found_f;
    }
}
