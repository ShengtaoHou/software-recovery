// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;

public final class XQueryExpression implements BooleanExpression
{
    private final String xpath;
    
    XQueryExpression(final String xpath) {
        this.xpath = xpath;
    }
    
    @Override
    public Object evaluate(final MessageEvaluationContext message) throws JMSException {
        return Boolean.FALSE;
    }
    
    @Override
    public String toString() {
        return "XQUERY " + ConstantExpression.encodeString(this.xpath);
    }
    
    @Override
    public boolean matches(final MessageEvaluationContext message) throws JMSException {
        final Object object = this.evaluate(message);
        return object != null && object == Boolean.TRUE;
    }
}
