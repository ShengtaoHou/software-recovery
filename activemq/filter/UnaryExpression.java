// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.jms.JMSException;
import java.math.BigDecimal;

public abstract class UnaryExpression implements Expression
{
    private static final BigDecimal BD_LONG_MIN_VALUE;
    protected Expression right;
    
    public UnaryExpression(final Expression left) {
        this.right = left;
    }
    
    public static Expression createNegate(final Expression left) {
        return new UnaryExpression(left) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Object rvalue = this.right.evaluate(message);
                if (rvalue == null) {
                    return null;
                }
                if (rvalue instanceof Number) {
                    return negate((Number)rvalue);
                }
                return null;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "-";
            }
        };
    }
    
    public static BooleanExpression createInExpression(final PropertyExpression right, final List<Object> elements, final boolean not) {
        Collection<Object> t;
        if (elements.size() == 0) {
            t = null;
        }
        else if (elements.size() < 5) {
            t = elements;
        }
        else {
            t = new HashSet<Object>(elements);
        }
        final Collection inList = t;
        return new BooleanUnaryExpression(right) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Object rvalue = this.right.evaluate(message);
                if (rvalue == null) {
                    return null;
                }
                if (rvalue.getClass() != String.class) {
                    return null;
                }
                if ((inList != null && inList.contains(rvalue)) ^ not) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            
            @Override
            public String toString() {
                final StringBuffer answer = new StringBuffer();
                answer.append(this.right);
                answer.append(" ");
                answer.append(this.getExpressionSymbol());
                answer.append(" ( ");
                int count = 0;
                for (final Object o : inList) {
                    if (count != 0) {
                        answer.append(", ");
                    }
                    answer.append(o);
                    ++count;
                }
                answer.append(" )");
                return answer.toString();
            }
            
            @Override
            public String getExpressionSymbol() {
                if (not) {
                    return "NOT IN";
                }
                return "IN";
            }
        };
    }
    
    public static BooleanExpression createNOT(final BooleanExpression left) {
        return new BooleanUnaryExpression(left) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Boolean lvalue = (Boolean)this.right.evaluate(message);
                if (lvalue == null) {
                    return null;
                }
                return ((boolean)lvalue) ? Boolean.FALSE : Boolean.TRUE;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "NOT";
            }
        };
    }
    
    public static BooleanExpression createXPath(final String xpath) {
        return new XPathExpression(xpath);
    }
    
    public static BooleanExpression createXQuery(final String xpath) {
        return new XQueryExpression(xpath);
    }
    
    public static BooleanExpression createBooleanCast(final Expression left) {
        return new BooleanUnaryExpression(left) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Object rvalue = this.right.evaluate(message);
                if (rvalue == null) {
                    return null;
                }
                if (!rvalue.getClass().equals(Boolean.class)) {
                    return Boolean.FALSE;
                }
                return rvalue ? Boolean.TRUE : Boolean.FALSE;
            }
            
            @Override
            public String toString() {
                return this.right.toString();
            }
            
            @Override
            public String getExpressionSymbol() {
                return "";
            }
        };
    }
    
    private static Number negate(final Number left) {
        final Class clazz = left.getClass();
        if (clazz == Integer.class) {
            return new Integer(-left.intValue());
        }
        if (clazz == Long.class) {
            return new Long(-left.longValue());
        }
        if (clazz == Float.class) {
            return new Float(-left.floatValue());
        }
        if (clazz == Double.class) {
            return new Double(-left.doubleValue());
        }
        if (clazz != BigDecimal.class) {
            throw new RuntimeException("Don't know how to negate: " + left);
        }
        BigDecimal bd = (BigDecimal)left;
        bd = bd.negate();
        if (UnaryExpression.BD_LONG_MIN_VALUE.compareTo(bd) == 0) {
            return Long.MIN_VALUE;
        }
        return bd;
    }
    
    public Expression getRight() {
        return this.right;
    }
    
    public void setRight(final Expression expression) {
        this.right = expression;
    }
    
    @Override
    public String toString() {
        return "(" + this.getExpressionSymbol() + " " + this.right.toString() + ")";
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && this.getClass().equals(o.getClass()) && this.toString().equals(o.toString());
    }
    
    public abstract String getExpressionSymbol();
    
    static {
        BD_LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
    }
    
    abstract static class BooleanUnaryExpression extends UnaryExpression implements BooleanExpression
    {
        public BooleanUnaryExpression(final Expression left) {
            super(left);
        }
        
        @Override
        public boolean matches(final MessageEvaluationContext message) throws JMSException {
            final Object object = this.evaluate(message);
            return object != null && object == Boolean.TRUE;
        }
    }
}
