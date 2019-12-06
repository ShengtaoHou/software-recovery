// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;

public abstract class ArithmeticExpression extends BinaryExpression
{
    protected static final int INTEGER = 1;
    protected static final int LONG = 2;
    protected static final int DOUBLE = 3;
    
    public ArithmeticExpression(final Expression left, final Expression right) {
        super(left, right);
    }
    
    public static Expression createPlus(final Expression left, final Expression right) {
        return new ArithmeticExpression(left, right) {
            @Override
            protected Object evaluate(final Object lvalue, final Object rvalue) {
                if (lvalue instanceof String) {
                    final String text = (String)lvalue;
                    final String answer = text + rvalue;
                    return answer;
                }
                if (lvalue instanceof Number) {
                    return this.plus((Number)lvalue, this.asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call plus operation on: " + lvalue + " and: " + rvalue);
            }
            
            @Override
            public String getExpressionSymbol() {
                return "+";
            }
        };
    }
    
    public static Expression createMinus(final Expression left, final Expression right) {
        return new ArithmeticExpression(left, right) {
            @Override
            protected Object evaluate(final Object lvalue, final Object rvalue) {
                if (lvalue instanceof Number) {
                    return this.minus((Number)lvalue, this.asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call minus operation on: " + lvalue + " and: " + rvalue);
            }
            
            @Override
            public String getExpressionSymbol() {
                return "-";
            }
        };
    }
    
    public static Expression createMultiply(final Expression left, final Expression right) {
        return new ArithmeticExpression(left, right) {
            @Override
            protected Object evaluate(final Object lvalue, final Object rvalue) {
                if (lvalue instanceof Number) {
                    return this.multiply((Number)lvalue, this.asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call multiply operation on: " + lvalue + " and: " + rvalue);
            }
            
            @Override
            public String getExpressionSymbol() {
                return "*";
            }
        };
    }
    
    public static Expression createDivide(final Expression left, final Expression right) {
        return new ArithmeticExpression(left, right) {
            @Override
            protected Object evaluate(final Object lvalue, final Object rvalue) {
                if (lvalue instanceof Number) {
                    return this.divide((Number)lvalue, this.asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call divide operation on: " + lvalue + " and: " + rvalue);
            }
            
            @Override
            public String getExpressionSymbol() {
                return "/";
            }
        };
    }
    
    public static Expression createMod(final Expression left, final Expression right) {
        return new ArithmeticExpression(left, right) {
            @Override
            protected Object evaluate(final Object lvalue, final Object rvalue) {
                if (lvalue instanceof Number) {
                    return this.mod((Number)lvalue, this.asNumber(rvalue));
                }
                throw new RuntimeException("Cannot call mod operation on: " + lvalue + " and: " + rvalue);
            }
            
            @Override
            public String getExpressionSymbol() {
                return "%";
            }
        };
    }
    
    protected Number plus(final Number left, final Number right) {
        switch (this.numberType(left, right)) {
            case 1: {
                return new Integer(left.intValue() + right.intValue());
            }
            case 2: {
                return new Long(left.longValue() + right.longValue());
            }
            default: {
                return new Double(left.doubleValue() + right.doubleValue());
            }
        }
    }
    
    protected Number minus(final Number left, final Number right) {
        switch (this.numberType(left, right)) {
            case 1: {
                return new Integer(left.intValue() - right.intValue());
            }
            case 2: {
                return new Long(left.longValue() - right.longValue());
            }
            default: {
                return new Double(left.doubleValue() - right.doubleValue());
            }
        }
    }
    
    protected Number multiply(final Number left, final Number right) {
        switch (this.numberType(left, right)) {
            case 1: {
                return new Integer(left.intValue() * right.intValue());
            }
            case 2: {
                return new Long(left.longValue() * right.longValue());
            }
            default: {
                return new Double(left.doubleValue() * right.doubleValue());
            }
        }
    }
    
    protected Number divide(final Number left, final Number right) {
        return new Double(left.doubleValue() / right.doubleValue());
    }
    
    protected Number mod(final Number left, final Number right) {
        return new Double(left.doubleValue() % right.doubleValue());
    }
    
    private int numberType(final Number left, final Number right) {
        if (this.isDouble(left) || this.isDouble(right)) {
            return 3;
        }
        if (left instanceof Long || right instanceof Long) {
            return 2;
        }
        return 1;
    }
    
    private boolean isDouble(final Number n) {
        return n instanceof Float || n instanceof Double;
    }
    
    protected Number asNumber(final Object value) {
        if (value instanceof Number) {
            return (Number)value;
        }
        throw new RuntimeException("Cannot convert value: " + value + " into a number");
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        final Object lvalue = this.left.evaluate(message);
        if (lvalue == null) {
            return null;
        }
        final Object rvalue = this.right.evaluate(message);
        if (rvalue == null) {
            return null;
        }
        return this.evaluate(lvalue, rvalue);
    }
    
    protected abstract Object evaluate(final Object p0, final Object p1);
}
