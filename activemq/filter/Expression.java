// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.filter;

import javax.jms.JMSException;

public interface Expression
{
    Object evaluate(final MessageEvaluationContext p0) throws JMSException;
}
