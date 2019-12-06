// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.regex.Pattern;
import java.util.HashSet;
import javax.jms.JMSException;
import java.util.List;
import java.util.Set;

public abstract class ComparisonExpression extends BinaryExpression implements BooleanExpression
{
    public static final ThreadLocal<Boolean> CONVERT_STRING_EXPRESSIONS;
    boolean convertStringExpressions;
    private static final Set<Character> REGEXP_CONTROL_CHARS;
    
    public ComparisonExpression(final Expression left, final Expression right) {
        super(left, right);
        this.convertStringExpressions = false;
        this.convertStringExpressions = (ComparisonExpression.CONVERT_STRING_EXPRESSIONS.get() != null);
    }
    
    public static BooleanExpression createBetween(final Expression value, final Expression left, final Expression right) {
        return LogicExpression.createAND(createGreaterThanEqual(value, left), createLessThanEqual(value, right));
    }
    
    public static BooleanExpression createNotBetween(final Expression value, final Expression left, final Expression right) {
        return LogicExpression.createOR(createLessThan(value, left), createGreaterThan(value, right));
    }
    
    public static BooleanExpression createLike(final Expression left, final String right, final String escape) {
        if (escape != null && escape.length() != 1) {
            throw new RuntimeException("The ESCAPE string litteral is invalid.  It can only be one character.  Litteral used: " + escape);
        }
        int c = -1;
        if (escape != null) {
            c = ('\uffff' & escape.charAt(0));
        }
        return new LikeExpression(left, right, c);
    }
    
    public static BooleanExpression createNotLike(final Expression left, final String right, final String escape) {
        return UnaryExpression.createNOT(createLike(left, right, escape));
    }
    
    public static BooleanExpression createInFilter(final Expression left, final List elements) {
        if (!(left instanceof PropertyExpression)) {
            throw new RuntimeException("Expected a property for In expression, got: " + left);
        }
        return UnaryExpression.createInExpression((PropertyExpression)left, elements, false);
    }
    
    public static BooleanExpression createNotInFilter(final Expression left, final List elements) {
        if (!(left instanceof PropertyExpression)) {
            throw new RuntimeException("Expected a property for In expression, got: " + left);
        }
        return UnaryExpression.createInExpression((PropertyExpression)left, elements, true);
    }
    
    public static BooleanExpression createIsNull(final Expression left) {
        return doCreateEqual(left, ConstantExpression.NULL);
    }
    
    public static BooleanExpression createIsNotNull(final Expression left) {
        return UnaryExpression.createNOT(doCreateEqual(left, ConstantExpression.NULL));
    }
    
    public static BooleanExpression createNotEqual(final Expression left, final Expression right) {
        return UnaryExpression.createNOT(createEqual(left, right));
    }
    
    public static BooleanExpression createEqual(final Expression left, final Expression right) {
        checkEqualOperand(left);
        checkEqualOperand(right);
        checkEqualOperandCompatability(left, right);
        return doCreateEqual(left, right);
    }
    
    private static BooleanExpression doCreateEqual(final Expression left, final Expression right) {
        return new ComparisonExpression(left, right) {
            @Override
            public Object evaluate(final MessageEvaluationContext message) throws JMSException {
                final Object lv = this.left.evaluate(message);
                final Object rv = this.right.evaluate(message);
                if (lv == null ^ rv == null) {
                    return Boolean.FALSE;
                }
                if (lv == rv || lv.equals(rv)) {
                    return Boolean.TRUE;
                }
                if (lv instanceof Comparable && rv instanceof Comparable) {
                    return this.compare((Comparable)lv, (Comparable)rv);
                }
                return Boolean.FALSE;
            }
            
            @Override
            protected boolean asBoolean(final int answer) {
                return answer == 0;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "=";
            }
        };
    }
    
    public static BooleanExpression createGreaterThan(final Expression left, final Expression right) {
        checkLessThanOperand(left);
        checkLessThanOperand(right);
        return new ComparisonExpression(left, right) {
            @Override
            protected boolean asBoolean(final int answer) {
                return answer > 0;
            }
            
            @Override
            public String getExpressionSymbol() {
                return ">";
            }
        };
    }
    
    public static BooleanExpression createGreaterThanEqual(final Expression left, final Expression right) {
        checkLessThanOperand(left);
        checkLessThanOperand(right);
        return new ComparisonExpression(left, right) {
            @Override
            protected boolean asBoolean(final int answer) {
                return answer >= 0;
            }
            
            @Override
            public String getExpressionSymbol() {
                return ">=";
            }
        };
    }
    
    public static BooleanExpression createLessThan(final Expression left, final Expression right) {
        checkLessThanOperand(left);
        checkLessThanOperand(right);
        return new ComparisonExpression(left, right) {
            @Override
            protected boolean asBoolean(final int answer) {
                return answer < 0;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "<";
            }
        };
    }
    
    public static BooleanExpression createLessThanEqual(final Expression left, final Expression right) {
        checkLessThanOperand(left);
        checkLessThanOperand(right);
        return new ComparisonExpression(left, right) {
            @Override
            protected boolean asBoolean(final int answer) {
                return answer <= 0;
            }
            
            @Override
            public String getExpressionSymbol() {
                return "<=";
            }
        };
    }
    
    public static void checkLessThanOperand(final Expression expr) {
        if (expr instanceof ConstantExpression) {
            final Object value = ((ConstantExpression)expr).getValue();
            if (value instanceof Number) {
                return;
            }
            throw new RuntimeException("Value '" + expr + "' cannot be compared.");
        }
        else if (expr instanceof BooleanExpression) {
            throw new RuntimeException("Value '" + expr + "' cannot be compared.");
        }
    }
    
    public static void checkEqualOperand(final Expression expr) {
        if (expr instanceof ConstantExpression) {
            final Object value = ((ConstantExpression)expr).getValue();
            if (value == null) {
                throw new RuntimeException("'" + expr + "' cannot be compared.");
            }
        }
    }
    
    private static void checkEqualOperandCompatability(final Expression left, final Expression right) {
        if (left instanceof ConstantExpression && right instanceof ConstantExpression && left instanceof BooleanExpression && !(right instanceof BooleanExpression)) {
            throw new RuntimeException("'" + left + "' cannot be compared with '" + right + "'");
        }
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        final Comparable<Comparable> lv = (Comparable<Comparable>)this.left.evaluate(message);
        if (lv == null) {
            return null;
        }
        final Comparable rv = (Comparable)this.right.evaluate(message);
        if (rv == null) {
            return null;
        }
        return this.compare(lv, rv);
    }
    
    protected Boolean compare(Comparable lv, Comparable rv) {
        final Class<? extends Comparable> lc = lv.getClass();
        final Class<? extends Comparable> rc = rv.getClass();
        if (lc != rc) {
            try {
                if (lc == Boolean.class) {
                    if (!this.convertStringExpressions || rc != String.class) {
                        return Boolean.FALSE;
                    }
                    lv = Boolean.valueOf((String)lv);
                }
                else if (lc == Byte.class) {
                    if (rc == Short.class) {
                        lv = ((Number)lv).shortValue();
                    }
                    else if (rc == Integer.class) {
                        lv = ((Number)lv).intValue();
                    }
                    else if (rc == Long.class) {
                        lv = ((Number)lv).longValue();
                    }
                    else if (rc == Float.class) {
                        lv = new Float(((Number)lv).floatValue());
                    }
                    else if (rc == Double.class) {
                        lv = new Double(((Number)lv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Byte.valueOf((String)rv);
                    }
                }
                else if (lc == Short.class) {
                    if (rc == Integer.class) {
                        lv = ((Number)lv).intValue();
                    }
                    else if (rc == Long.class) {
                        lv = ((Number)lv).longValue();
                    }
                    else if (rc == Float.class) {
                        lv = new Float(((Number)lv).floatValue());
                    }
                    else if (rc == Double.class) {
                        lv = new Double(((Number)lv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Short.valueOf((String)rv);
                    }
                }
                else if (lc == Integer.class) {
                    if (rc == Long.class) {
                        lv = ((Number)lv).longValue();
                    }
                    else if (rc == Float.class) {
                        lv = new Float(((Number)lv).floatValue());
                    }
                    else if (rc == Double.class) {
                        lv = new Double(((Number)lv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Integer.valueOf((String)rv);
                    }
                }
                else if (lc == Long.class) {
                    if (rc == Integer.class) {
                        rv = ((Number)rv).longValue();
                    }
                    else if (rc == Float.class) {
                        lv = new Float(((Number)lv).floatValue());
                    }
                    else if (rc == Double.class) {
                        lv = new Double(((Number)lv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Long.valueOf((String)rv);
                    }
                }
                else if (lc == Float.class) {
                    if (rc == Integer.class) {
                        rv = new Float(((Number)rv).floatValue());
                    }
                    else if (rc == Long.class) {
                        rv = new Float(((Number)rv).floatValue());
                    }
                    else if (rc == Double.class) {
                        lv = new Double(((Number)lv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Float.valueOf((String)rv);
                    }
                }
                else if (lc == Double.class) {
                    if (rc == Integer.class) {
                        rv = new Double(((Number)rv).doubleValue());
                    }
                    else if (rc == Long.class) {
                        rv = new Double(((Number)rv).doubleValue());
                    }
                    else if (rc == Float.class) {
                        rv = new Float(((Number)rv).doubleValue());
                    }
                    else {
                        if (!this.convertStringExpressions || rc != String.class) {
                            return Boolean.FALSE;
                        }
                        rv = Double.valueOf((String)rv);
                    }
                }
                else {
                    if (!this.convertStringExpressions || lc != String.class) {
                        return Boolean.FALSE;
                    }
                    if (rc == Boolean.class) {
                        lv = Boolean.valueOf((String)lv);
                    }
                    else if (rc == Byte.class) {
                        lv = Byte.valueOf((String)lv);
                    }
                    else if (rc == Short.class) {
                        lv = Short.valueOf((String)lv);
                    }
                    else if (rc == Integer.class) {
                        lv = Integer.valueOf((String)lv);
                    }
                    else if (rc == Long.class) {
                        lv = Long.valueOf((String)lv);
                    }
                    else if (rc == Float.class) {
                        lv = Float.valueOf((String)lv);
                    }
                    else {
                        if (rc != Double.class) {
                            return Boolean.FALSE;
                        }
                        lv = Double.valueOf((String)lv);
                    }
                }
            }
            catch (NumberFormatException e) {
                return Boolean.FALSE;
            }
        }
        return this.asBoolean(lv.compareTo(rv)) ? Boolean.TRUE : Boolean.FALSE;
    }
    
    protected abstract boolean asBoolean(final int p0);
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        final Object object = this.evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
    
    static {
        CONVERT_STRING_EXPRESSIONS = new ThreadLocal<Boolean>();
        (REGEXP_CONTROL_CHARS = new HashSet<Character>()).add('.');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('\\');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('[');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add(']');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('^');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('$');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('?');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('*');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('+');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('{');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('}');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('|');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('(');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add(')');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add(':');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('&');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('<');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('>');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('=');
        ComparisonExpression.REGEXP_CONTROL_CHARS.add('!');
    }
    
    static class LikeExpression extends UnaryExpression implements BooleanExpression
    {
        Pattern likePattern;
        
        public LikeExpression(final Expression right, final String like, final int escape) {
            super(right);
            final StringBuffer regexp = new StringBuffer(like.length() * 2);
            regexp.append("\\A");
            for (int i = 0; i < like.length(); ++i) {
                final char c = like.charAt(i);
                if (escape == ('\uffff' & c)) {
                    if (++i >= like.length()) {
                        break;
                    }
                    final char t = like.charAt(i);
                    regexp.append("\\x");
                    regexp.append(Integer.toHexString('\uffff' & t));
                }
                else if (c == '%') {
                    regexp.append(".*?");
                }
                else if (c == '_') {
                    regexp.append(".");
                }
                else if (ComparisonExpression.REGEXP_CONTROL_CHARS.contains(new Character(c))) {
                    regexp.append("\\x");
                    regexp.append(Integer.toHexString('\uffff' & c));
                }
                else {
                    regexp.append(c);
                }
            }
            regexp.append("\\z");
            this.likePattern = Pattern.compile(regexp.toString(), 32);
        }
        
        @Override
        public String getExpressionSymbol() {
            return "LIKE";
        }
        
        @Override
        public Object evaluate(final MessageEvaluationContext message) throws JMSException {
            final Object rv = this.getRight().evaluate(message);
            if (rv == null) {
                return null;
            }
            if (!(rv instanceof String)) {
                return Boolean.FALSE;
            }
            return this.likePattern.matcher((CharSequence)rv).matches() ? Boolean.TRUE : Boolean.FALSE;
        }
        
        @Override
        public boolean matches(final MessageEvaluationContext message) throws JMSException {
            final Object object = this.evaluate(message);
            return object != null && object == Boolean.TRUE;
        }
    }
}
