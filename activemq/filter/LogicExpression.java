// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;

public abstract class LogicExpression extends BinaryExpression implements BooleanExpression
{
    public LogicExpression(final BooleanExpression left, final BooleanExpression right) {
        super(left, right);
    }
    
    public static BooleanExpression createOR(final BooleanExpression lvalue, final BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Boolean lv = (Boolean)this.left.evaluate(message);
                if (lv != null && lv) {
                    return Boolean.TRUE;
                }
                final Boolean rv = (Boolean)this.right.evaluate(message);
                return (rv == null) ? null : rv;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "OR";
            }
        };
    }
    
    public static BooleanExpression createAND(final BooleanExpression lvalue, final BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Boolean lv = (Boolean)this.left.evaluate(message);
                if (lv == null) {
                    return null;
                }
                if (!lv) {
                    return Boolean.FALSE;
                }
                final Boolean rv = (Boolean)this.right.evaluate(message);
                return (rv == null) ? null : rv;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "AND";
            }
        };
    }
    
    @Override
    public abstract Object evaluate(final MessageEvaluationContext p0) throws JMSException;
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        final Object object = this.evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
}
