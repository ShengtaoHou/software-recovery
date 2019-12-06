// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.pool;

import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.jms.pool.ConnectionPool;
import org.apache.activemq.EnhancedConnection;

public class PooledConnection extends org.apache.activemq.jms.pool.PooledConnection implements EnhancedConnection
{
    public PooledConnection(final ConnectionPool connection) {
        super(connection);
    }
    
    @Override
    public DestinationSource getDestinationSource() throws JMSException {
        return ((ActiveMQConnection)this.getConnection()).getDestinationSource();
    }
}
