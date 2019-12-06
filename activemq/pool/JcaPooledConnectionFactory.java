// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.pool;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.ActiveMQConnection;
import javax.transaction.TransactionManager;
import org.apache.activemq.jms.pool.JcaConnectionPool;
import org.apache.activemq.jms.pool.ConnectionPool;
import javax.jms.Connection;
import org.slf4j.Logger;

public class JcaPooledConnectionFactory extends XaPooledConnectionFactory
{
    private static final transient Logger LOG;
    private String name;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new JcaConnectionPool(connection, this.getTransactionManager(), this.getName()) {
            @Override
            protected Connection wrap(final Connection connection) {
                ((ActiveMQConnection)connection).addTransportListener(new TransportListener() {
                    @Override
                    public void onCommand(final Object command) {
                    }
                    
                    @Override
                    public void onException(final IOException error) {
                        synchronized (this) {
                            JcaConnectionPool.this.setHasExpired(true);
                            JcaPooledConnectionFactory.LOG.info("Expiring connection " + connection + " on IOException: " + error);
                            JcaPooledConnectionFactory.LOG.debug("Expiring connection on IOException", error);
                        }
                    }
                    
                    @Override
                    public void transportInterupted() {
                    }
                    
                    @Override
                    public void transportResumed() {
                    }
                });
                this.setHasExpired(((ActiveMQConnection)connection).isTransportFailed());
                return connection;
            }
            
            @Override
            protected void unWrap(final Connection connection) {
                if (connection != null) {
                    ((ActiveMQConnection)connection).cleanUpTempDestinations();
                }
            }
        };
    }
    
    static {
        LOG = LoggerFactory.getLogger(JcaPooledConnectionFactory.class);
    }
}
