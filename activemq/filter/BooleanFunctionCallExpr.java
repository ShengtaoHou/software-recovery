// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;
import java.util.List;

public class BooleanFunctionCallExpr extends FunctionCallExpression implements BooleanExpression
{
    public BooleanFunctionCallExpr(final String func_name, final List<Expression> args) throws invalidFunctionExpressionException {
        super(func_name, args);
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext message_ctx) throws JMSException {
        final Boolean result = (Boolean)this.evaluate(message_ctx);
        return result != null && result;
    }
}
