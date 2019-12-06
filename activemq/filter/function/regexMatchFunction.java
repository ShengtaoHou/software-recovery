// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter.function;

import javax.jms.JMSException;
import java.util.regex.Matcher;
import org.apache.activemq.filter.MessageEvaluationContext;
import org.apache.activemq.filter.FunctionCallExpression;
import java.util.regex.Pattern;
import org.apache.activemq.util.LRUCache;

public class regexMatchFunction implements FilterFunction
{
    protected static final LRUCache<String, Pattern> compiledExprCache;
    
    @Override
    public boolean isValid(final FunctionCallExpression expr) {
        return expr.getNumArguments() == 2;
    }
    
    @Override
    public boolean returnsBoolean(final FunctionCallExpression expr) {
        return true;
    }
    
    @Override
    public Object evaluate(final FunctionCallExpression expr, final MessageEvaluationContext message) throws JMSException {
        final Object reg = expr.getArgument(0).evaluate(message);
        if (reg != null) {
            String reg_str;
            if (reg instanceof String) {
                reg_str = (String)reg;
            }
            else {
                reg_str = reg.toString();
            }
            final Object cand = expr.getArgument(1).evaluate(message);
            if (cand != null) {
                String cand_str;
                if (cand instanceof String) {
                    cand_str = (String)cand;
                }
                else {
                    cand_str = cand.toString();
                }
                final Pattern pat = this.getCompiledPattern(reg_str);
                final Matcher match_eng = pat.matcher(cand_str);
                return match_eng.find();
            }
        }
        return Boolean.FALSE;
    }
    
    protected Pattern getCompiledPattern(final String reg_ex_str) {
        Pattern result;
        synchronized (regexMatchFunction.compiledExprCache) {
            result = regexMatchFunction.compiledExprCache.get(reg_ex_str);
        }
        if (result == null) {
            result = Pattern.compile(reg_ex_str);
            synchronized (regexMatchFunction.compiledExprCache) {
                regexMatchFunction.compiledExprCache.put(reg_ex_str, result);
            }
        }
        return result;
    }
    
    static {
        compiledExprCache = new LRUCache<String, Pattern>(100);
    }
}
