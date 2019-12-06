// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.JMSException;
import org.apache.activemq.advisory.DestinationSource;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

public interface EnhancedConnection extends TopicConnection, QueueConnection, Closeable
{
    DestinationSource getDestinationSource() throws JMSException;
}
