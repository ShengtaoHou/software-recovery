// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import javax.jms.JMSException;
import javax.jms.Destination;

public interface UnresolvedDestinationTransformer
{
    ActiveMQDestination transform(final Destination p0) throws JMSException;
    
    ActiveMQDestination transform(final String p0) throws JMSException;
}
