// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;

public interface BooleanExpression extends Expression
{
    boolean matches(final MessageEvaluationContext p0) throws JMSException;
}
