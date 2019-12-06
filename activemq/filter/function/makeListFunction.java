// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import java.util.ArrayList;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;

public class makeListFunction implements FilterFunction
{
    @Override
    public boolean isValid(final FunctionCallExpression expr) {
        return true;
    }
    
    @Override
    public boolean returnsBoolean(final FunctionCallExpression expr) {
        return false;
    }
    
    @Override
    public Object evaluate(final FunctionCallExpression expr, final MessageEvaluationContext message) throws JMSException {
        final int num_arg = expr.getNumArguments();
        final ArrayList ele_arr = new ArrayList(num_arg);
        for (int cur = 0; cur < num_arg; ++cur) {
            ele_arr.add(expr.getArgument(cur).evaluate(message));
        }
        return ele_arr;
    }
}
