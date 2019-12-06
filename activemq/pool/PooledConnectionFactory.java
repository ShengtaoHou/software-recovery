// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.pool;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.ActiveMQConnection;
import javax.jms.Connection;
import org.apache.activemq.jms.pool.ConnectionPool;
import javax.naming.NamingException;
import org.apache.activemq.jndi.JNDIReferenceFactory;
import javax.naming.Reference;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.apache.activemq.Service;
import org.apache.activemq.jndi.JNDIStorableInterface;

public class PooledConnectionFactory extends org.apache.activemq.jms.pool.PooledConnectionFactory implements JNDIStorableInterface, Service
{
    public static final String POOL_PROPS_PREFIX = "pool";
    private static final transient Logger LOG;
    
    public PooledConnectionFactory() {
    }
    
    public PooledConnectionFactory(final ActiveMQConnectionFactory activeMQConnectionFactory) {
        this.setConnectionFactory(activeMQConnectionFactory);
    }
    
    public PooledConnectionFactory(final String brokerURL) {
        this.setConnectionFactory(new ActiveMQConnectionFactory(brokerURL));
    }
    
    protected void buildFromProperties(final Properties props) {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.buildFromProperties(props);
        this.setConnectionFactory(activeMQConnectionFactory);
        IntrospectionSupport.setProperties(this, new HashMap<String, Object>((Map<? extends String, ?>)props), "pool");
    }
    
    protected void populateProperties(final Properties props) {
        ((ActiveMQConnectionFactory)this.getConnectionFactory()).populateProperties(props);
        IntrospectionSupport.getProperties(this, props, "pool");
    }
    
    @Override
    public void setProperties(final Properties properties) {
        this.buildFromProperties(properties);
    }
    
    @Override
    public Properties getProperties() {
        final Properties properties = new Properties();
        this.populateProperties(properties);
        return properties;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        return JNDIReferenceFactory.createReference(this.getClass().getName(), this);
    }
    
    @Override
    protected Connection newPooledConnection(final ConnectionPool connection) {
        return new PooledConnection(connection);
    }
    
    @Override
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new ConnectionPool(connection) {
            @Override
            protected Connection wrap(final Connection connection) {
                ((ActiveMQConnection)connection).addTransportListener(new TransportListener() {
                    @Override
                    public void onCommand(final Object command) {
                    }
                    
                    @Override
                    public void onException(final IOException error) {
                        synchronized (this) {
                            ConnectionPool.this.setHasExpired(true);
                            PooledConnectionFactory.LOG.info("Expiring connection {} on IOException: {}", connection, error);
                            PooledConnectionFactory.LOG.debug("Expiring connection on IOException", error);
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
        LOG = LoggerFactory.getLogger(org.apache.activemq.jms.pool.PooledConnectionFactory.class);
    }
}
