// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MultiExpressionEvaluator
{
    Map<String, ExpressionListenerSet> rootExpressions;
    Map<Expression, CacheExpression> cachedExpressions;
    int view;
    
    public MultiExpressionEvaluator() {
        this.rootExpressions = new HashMap<String, ExpressionListenerSet>();
        this.cachedExpressions = new HashMap<Expression, CacheExpression>();
    }
    
    public void addExpressionListner(final Expression selector, final ExpressionListener c) {
        ExpressionListenerSet data = this.rootExpressions.get(selector.toString());
        if (data == null) {
            data = new ExpressionListenerSet();
            data.expression = this.addToCache(selector);
            this.rootExpressions.put(selector.toString(), data);
        }
        data.listeners.add(c);
    }
    
    public boolean removeEventListner(final String selector, final ExpressionListener c) {
        final String expKey = selector;
        final ExpressionListenerSet d = this.rootExpressions.get(expKey);
        if (d == null) {
            return false;
        }
        if (!d.listeners.remove(c)) {
            return false;
        }
        if (d.listeners.size() == 0) {
            this.removeFromCache((CacheExpression)d.expression);
            this.rootExpressions.remove(expKey);
        }
        return true;
    }
    
    private CacheExpression addToCache(final Expression expr) {
        CacheExpression n = this.cachedExpressions.get(expr);
        if (n == null) {
            n = new CacheExpression(expr);
            this.cachedExpressions.put(expr, n);
            if (expr instanceof UnaryExpression) {
                final UnaryExpression un = (UnaryExpression)expr;
                un.setRight(this.addToCache(un.getRight()));
            }
            else if (expr instanceof BinaryExpression) {
                final BinaryExpression bn = (BinaryExpression)expr;
                bn.setRight(this.addToCache(bn.getRight()));
                bn.setLeft(this.addToCache(bn.getLeft()));
            }
        }
        final CacheExpression cacheExpression = n;
        ++cacheExpression.refCount;
        return n;
    }
    
    private void removeFromCache(final CacheExpression cn) {
        --cn.refCount;
        final Expression realExpr = cn.getRight();
        if (cn.refCount == 0) {
            this.cachedExpressions.remove(realExpr);
        }
        if (realExpr instanceof UnaryExpression) {
            final UnaryExpression un = (UnaryExpression)realExpr;
            this.removeFromCache((CacheExpression)un.getRight());
        }
        if (realExpr instanceof BinaryExpression) {
            final BinaryExpression bn = (BinaryExpression)realExpr;
            this.removeFromCache((CacheExpression)bn.getRight());
        }
    }
    
    public void evaluate(final MessageEvaluationContext message) {
        final Collection<ExpressionListenerSet> expressionListeners = this.rootExpressions.values();
        for (final ExpressionListenerSet els : expressionListeners) {
            try {
                final Object result = els.expression.evaluate(message);
                for (final ExpressionListener l : els.listeners) {
                    l.evaluateResultEvent(els.expression, message, result);
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    
    public class CacheExpression extends UnaryExpression
    {
        short refCount;
        int cview;
        Object cachedValue;
        int cachedHashCode;
        
        public CacheExpression(final Expression realExpression) {
            super(realExpression);
            this.cview = MultiExpressionEvaluator.this.view - 1;
            this.cachedHashCode = realExpression.hashCode();
        }
        
        @Override
        public Object evaluate(final MessageEvaluationContext message) throws JMSException {
            if (MultiExpressionEvaluator.this.view == this.cview) {
                return this.cachedValue;
            }
            this.cachedValue = this.right.evaluate(message);
            this.cview = MultiExpressionEvaluator.this.view;
            return this.cachedValue;
        }
        
        @Override
        public int hashCode() {
            return this.cachedHashCode;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && ((CacheExpression)o).right.equals(this.right);
        }
        
        @Override
        public String getExpressionSymbol() {
            return null;
        }
        
        @Override
        public String toString() {
            return this.right.toString();
        }
    }
    
    static class ExpressionListenerSet
    {
        Expression expression;
        List<ExpressionListener> listeners;
        
        ExpressionListenerSet() {
            this.listeners = new ArrayList<ExpressionListener>();
        }
    }
    
    interface ExpressionListener
    {
        void evaluateResultEvent(final Expression p0, final MessageEvaluationContext p1, final Object p2);
    }
}
