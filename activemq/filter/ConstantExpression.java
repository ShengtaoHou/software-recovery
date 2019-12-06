// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;
import java.math.BigDecimal;

public class ConstantExpression implements Expression
{
    public static final BooleanConstantExpression NULL;
    public static final BooleanConstantExpression TRUE;
    public static final BooleanConstantExpression FALSE;
    private Object value;
    
    public ConstantExpression(final Object value) {
        this.value = value;
    }
    
    public static ConstantExpression createFromDecimal(String text) {
        if (text.endsWith("l") || text.endsWith("L")) {
            text = text.substring(0, text.length() - 1);
        }
        Number value;
        try {
            value = new Long(text);
        }
        catch (NumberFormatException e) {
            value = new BigDecimal(text);
        }
        final long l = value.longValue();
        if (-2147483648L <= l && l <= 2147483647L) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }
    
    public static ConstantExpression createFromHex(final String text) {
        Number value = Long.parseLong(text.substring(2), 16);
        final long l = value.longValue();
        if (-2147483648L <= l && l <= 2147483647L) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }
    
    public static ConstantExpression createFromOctal(final String text) {
        Number value = Long.parseLong(text, 8);
        final long l = value.longValue();
        if (-2147483648L <= l && l <= 2147483647L) {
            value = value.intValue();
        }
        return new ConstantExpression(value);
    }
    
    public static ConstantExpression createFloat(final String text) {
        final Number value = new Double(text);
        return new ConstantExpression(value);
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        return this.value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            return "NULL";
        }
        if (this.value instanceof Boolean) {
            return this.value ? "TRUE" : "FALSE";
        }
        if (this.value instanceof String) {
            return encodeString((String)this.value);
        }
        return this.value.toString();
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && this.getClass().equals(o.getClass()) && this.toString().equals(o.toString());
    }
    
    public static String encodeString(final String s) {
        final StringBuffer b = new StringBuffer();
        b.append('\'');
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c == '\'') {
                b.append(c);
            }
            b.append(c);
        }
        b.append('\'');
        return b.toString();
    }
    
    static {
        NULL = new BooleanConstantExpression(null);
        TRUE = new BooleanConstantExpression(Boolean.TRUE);
        FALSE = new BooleanConstantExpression(Boolean.FALSE);
    }
    
    static class BooleanConstantExpression extends ConstantExpression implements BooleanExpression
    {
        public BooleanConstantExpression(final Object value) {
            super(value);
        }
        
        @Override
        public boolean matches(final MessageEvaluationContext message) throws JMSException {
            final Object object = this.evaluate(message);
            return object != null && object == Boolean.TRUE;
        }
    }
}
