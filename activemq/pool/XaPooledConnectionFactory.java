// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.pool;

import org.slf4j.LoggerFactory;
import javax.naming.NamingException;
import org.apache.activemq.jndi.JNDIReferenceFactory;
import javax.naming.Reference;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.util.Properties;
import java.io.IOException;
import org.apache.activemq.transport.TransportListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;
import org.apache.activemq.jms.pool.PooledSession;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.Session;
import org.apache.activemq.jms.pool.SessionKey;
import javax.transaction.TransactionManager;
import org.apache.activemq.jms.pool.XaConnectionPool;
import org.apache.activemq.jms.pool.ConnectionPool;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.slf4j.Logger;
import org.apache.activemq.Service;
import org.apache.activemq.jndi.JNDIStorableInterface;

public class XaPooledConnectionFactory extends org.apache.activemq.jms.pool.XaPooledConnectionFactory implements JNDIStorableInterface, Service
{
    public static final String POOL_PROPS_PREFIX = "pool";
    private static final transient Logger LOG;
    private String brokerUrl;
    
    public XaPooledConnectionFactory() {
    }
    
    public XaPooledConnectionFactory(final ActiveMQXAConnectionFactory connectionFactory) {
        this.setConnectionFactory(connectionFactory);
    }
    
    @Override
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new XaConnectionPool(connection, this.getTransactionManager()) {
            @Override
            protected Session makeSession(final SessionKey key) throws JMSException {
                if (this.connection instanceof XAConnection) {
                    return ((XAConnection)this.connection).createXASession();
                }
                return this.connection.createSession(key.isTransacted(), key.getAckMode());
            }
            
            @Override
            protected XAResource createXaResource(final PooledSession session) throws JMSException {
                if (session.getInternalSession() instanceof XASession) {
                    return ((XASession)session.getInternalSession()).getXAResource();
                }
                return ((ActiveMQSession)session.getInternalSession()).getTransactionContext();
            }
            
            @Override
            protected Connection wrap(final Connection connection) {
                ((ActiveMQConnection)connection).addTransportListener(new TransportListener() {
                    @Override
                    public void onCommand(final Object command) {
                    }
                    
                    @Override
                    public void onException(final IOException error) {
                        synchronized (this) {
                            XaConnectionPool.this.setHasExpired(true);
                            XaPooledConnectionFactory.LOG.info("Expiring connection " + connection + " on IOException: " + error);
                            XaPooledConnectionFactory.LOG.debug("Expiring connection on IOException", error);
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
    
    protected void buildFromProperties(final Properties props) {
        final ActiveMQConnectionFactory activeMQConnectionFactory = props.containsKey("xaAckMode") ? new ActiveMQXAConnectionFactory() : new ActiveMQConnectionFactory();
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
    
    public void setBrokerUrl(final String url) {
        if (this.brokerUrl == null || !this.brokerUrl.equals(url)) {
            this.brokerUrl = url;
            this.setConnectionFactory(new ActiveMQXAConnectionFactory(this.brokerUrl));
        }
    }
    
    public String getBrokerUrl() {
        return this.brokerUrl;
    }
    
    static {
        LOG = LoggerFactory.getLogger(org.apache.activemq.jms.pool.XaPooledConnectionFactory.class);
    }
}
