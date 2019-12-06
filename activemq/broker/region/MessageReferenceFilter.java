// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import javax.jms.JMSException;
import org.apache.activemq.broker.ConnectionContext;

public interface MessageReferenceFilter
{
    boolean evaluate(final ConnectionContext p0, final MessageReference p1) throws JMSException;
}
