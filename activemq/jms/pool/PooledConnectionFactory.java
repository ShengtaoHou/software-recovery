// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import javax.jms.JMSException;
import javax.jms.XAConnectionFactory;
import javax.jms.Connection;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import javax.jms.ConnectionFactory;

public class PooledConnectionFactory implements ConnectionFactory
{
    private static final transient Logger LOG;
    protected final AtomicBoolean stopped;
    private GenericKeyedObjectPool<ConnectionKey, ConnectionPool> connectionsPool;
    private ConnectionFactory connectionFactory;
    private int maximumActiveSessionPerConnection;
    private int idleTimeout;
    private boolean blockIfSessionPoolIsFull;
    private long blockIfSessionPoolIsFullTimeout;
    private long expiryTimeout;
    private boolean createConnectionOnStartup;
    private boolean useAnonymousProducers;
    
    public PooledConnectionFactory() {
        this.stopped = new AtomicBoolean(false);
        this.maximumActiveSessionPerConnection = 500;
        this.idleTimeout = 30000;
        this.blockIfSessionPoolIsFull = true;
        this.blockIfSessionPoolIsFullTimeout = -1L;
        this.expiryTimeout = 0L;
        this.createConnectionOnStartup = true;
        this.useAnonymousProducers = true;
    }
    
    public void initConnectionsPool() {
        if (this.connectionsPool == null) {
            (this.connectionsPool = (GenericKeyedObjectPool<ConnectionKey, ConnectionPool>)new GenericKeyedObjectPool((KeyedPoolableObjectFactory)new KeyedPoolableObjectFactory<ConnectionKey, ConnectionPool>() {
                public void activateObject(final ConnectionKey key, final ConnectionPool connection) throws Exception {
                }
                
                public void destroyObject(final ConnectionKey key, final ConnectionPool connection) throws Exception {
                    try {
                        if (PooledConnectionFactory.LOG.isTraceEnabled()) {
                            PooledConnectionFactory.LOG.trace("Destroying connection: {}", connection);
                        }
                        connection.close();
                    }
                    catch (Exception e) {
                        PooledConnectionFactory.LOG.warn("Close connection failed for connection: " + connection + ". This exception will be ignored.", e);
                    }
                }
                
                public ConnectionPool makeObject(final ConnectionKey key) throws Exception {
                    final Connection delegate = PooledConnectionFactory.this.createConnection(key);
                    final ConnectionPool connection = PooledConnectionFactory.this.createConnectionPool(delegate);
                    connection.setIdleTimeout(PooledConnectionFactory.this.getIdleTimeout());
                    connection.setExpiryTimeout(PooledConnectionFactory.this.getExpiryTimeout());
                    connection.setMaximumActiveSessionPerConnection(PooledConnectionFactory.this.getMaximumActiveSessionPerConnection());
                    connection.setBlockIfSessionPoolIsFull(PooledConnectionFactory.this.isBlockIfSessionPoolIsFull());
                    if (PooledConnectionFactory.this.isBlockIfSessionPoolIsFull() && PooledConnectionFactory.this.getBlockIfSessionPoolIsFullTimeout() > 0L) {
                        connection.setBlockIfSessionPoolIsFullTimeout(PooledConnectionFactory.this.getBlockIfSessionPoolIsFullTimeout());
                    }
                    connection.setUseAnonymousProducers(PooledConnectionFactory.this.isUseAnonymousProducers());
                    if (PooledConnectionFactory.LOG.isTraceEnabled()) {
                        PooledConnectionFactory.LOG.trace("Created new connection: {}", connection);
                    }
                    return connection;
                }
                
                public void passivateObject(final ConnectionKey key, final ConnectionPool connection) throws Exception {
                }
                
                public boolean validateObject(final ConnectionKey key, final ConnectionPool connection) {
                    if (connection != null && connection.expiredCheck()) {
                        if (PooledConnectionFactory.LOG.isTraceEnabled()) {
                            PooledConnectionFactory.LOG.trace("Connection has expired: {} and will be destroyed", connection);
                        }
                        return false;
                    }
                    return true;
                }
            })).setMaxIdle(1);
            this.connectionsPool.setTestOnBorrow(true);
            this.connectionsPool.setTestWhileIdle(true);
        }
    }
    
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
    
    public void setConnectionFactory(final ConnectionFactory toUse) {
        if (toUse instanceof XAConnectionFactory) {
            this.connectionFactory = new ConnectionFactory() {
                @Override
                public Connection createConnection() throws JMSException {
                    return ((XAConnectionFactory)toUse).createXAConnection();
                }
                
                @Override
                public Connection createConnection(final String userName, final String password) throws JMSException {
                    return ((XAConnectionFactory)toUse).createXAConnection(userName, password);
                }
            };
        }
        else {
            this.connectionFactory = toUse;
        }
    }
    
    @Override
    public Connection createConnection() throws JMSException {
        return this.createConnection(null, null);
    }
    
    @Override
    public synchronized Connection createConnection(final String userName, final String password) throws JMSException {
        if (this.stopped.get()) {
            PooledConnectionFactory.LOG.debug("PooledConnectionFactory is stopped, skip create new connection.");
            return null;
        }
        ConnectionPool connection = null;
        final ConnectionKey key = new ConnectionKey(userName, password);
        Label_0090: {
            if (this.getConnectionsPool().getNumIdle((Object)key) < this.getMaxConnections()) {
                try {
                    this.connectionsPool.setLifo(true);
                    this.connectionsPool.addObject((Object)key);
                    break Label_0090;
                }
                catch (Exception e) {
                    throw this.createJmsException("Error while attempting to add new Connection to the pool", e);
                }
            }
            this.connectionsPool.setLifo(false);
            try {
                while (connection == null) {
                    connection = (ConnectionPool)this.connectionsPool.borrowObject((Object)key);
                    synchronized (connection) {
                        if (connection.getConnection() != null) {
                            connection.incrementReferenceCount();
                            break;
                        }
                        this.connectionsPool.returnObject((Object)key, (Object)connection);
                        connection = null;
                    }
                }
            }
            catch (Exception e) {
                throw this.createJmsException("Error while attempting to retrieve a connection from the pool", e);
            }
        }
        try {
            this.connectionsPool.returnObject((Object)key, (Object)connection);
        }
        catch (Exception e) {
            throw this.createJmsException("Error when returning connection to the pool", e);
        }
        return this.newPooledConnection(connection);
    }
    
    protected Connection newPooledConnection(final ConnectionPool connection) {
        return new PooledConnection(connection);
    }
    
    private JMSException createJmsException(final String msg, final Exception cause) {
        final JMSException exception = new JMSException(msg);
        exception.setLinkedException(cause);
        exception.initCause(cause);
        return exception;
    }
    
    protected Connection createConnection(final ConnectionKey key) throws JMSException {
        if (key.getUserName() == null && key.getPassword() == null) {
            return this.connectionFactory.createConnection();
        }
        return this.connectionFactory.createConnection(key.getUserName(), key.getPassword());
    }
    
    public void start() {
        PooledConnectionFactory.LOG.debug("Staring the PooledConnectionFactory: create on start = {}", (Object)this.isCreateConnectionOnStartup());
        this.stopped.set(false);
        if (this.isCreateConnectionOnStartup()) {
            try {
                this.createConnection();
            }
            catch (JMSException e) {
                PooledConnectionFactory.LOG.warn("Create pooled connection during start failed. This exception will be ignored.", e);
            }
        }
    }
    
    public void stop() {
        if (this.stopped.compareAndSet(false, true)) {
            PooledConnectionFactory.LOG.debug("Stopping the PooledConnectionFactory, number of connections in cache: {}", (Object)((this.connectionsPool != null) ? this.connectionsPool.getNumActive() : 0));
            try {
                if (this.connectionsPool != null) {
                    this.connectionsPool.close();
                }
            }
            catch (Exception ex) {}
        }
    }
    
    public void clear() {
        if (this.stopped.get()) {
            return;
        }
        this.getConnectionsPool().clear();
    }
    
    public int getMaximumActiveSessionPerConnection() {
        return this.maximumActiveSessionPerConnection;
    }
    
    public void setMaximumActiveSessionPerConnection(final int maximumActiveSessionPerConnection) {
        this.maximumActiveSessionPerConnection = maximumActiveSessionPerConnection;
    }
    
    public void setBlockIfSessionPoolIsFull(final boolean block) {
        this.blockIfSessionPoolIsFull = block;
    }
    
    public boolean isBlockIfSessionPoolIsFull() {
        return this.blockIfSessionPoolIsFull;
    }
    
    public int getMaxConnections() {
        return this.getConnectionsPool().getMaxIdle();
    }
    
    public void setMaxConnections(final int maxConnections) {
        this.getConnectionsPool().setMaxIdle(maxConnections);
    }
    
    public int getIdleTimeout() {
        return this.idleTimeout;
    }
    
    public void setIdleTimeout(final int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
    
    public void setExpiryTimeout(final long expiryTimeout) {
        this.expiryTimeout = expiryTimeout;
    }
    
    public long getExpiryTimeout() {
        return this.expiryTimeout;
    }
    
    public boolean isCreateConnectionOnStartup() {
        return this.createConnectionOnStartup;
    }
    
    public void setCreateConnectionOnStartup(final boolean createConnectionOnStartup) {
        this.createConnectionOnStartup = createConnectionOnStartup;
    }
    
    public boolean isUseAnonymousProducers() {
        return this.useAnonymousProducers;
    }
    
    public void setUseAnonymousProducers(final boolean value) {
        this.useAnonymousProducers = value;
    }
    
    protected GenericKeyedObjectPool<ConnectionKey, ConnectionPool> getConnectionsPool() {
        this.initConnectionsPool();
        return this.connectionsPool;
    }
    
    public void setTimeBetweenExpirationCheckMillis(final long timeBetweenExpirationCheckMillis) {
        this.getConnectionsPool().setTimeBetweenEvictionRunsMillis(timeBetweenExpirationCheckMillis);
    }
    
    public long getTimeBetweenExpirationCheckMillis() {
        return this.getConnectionsPool().getTimeBetweenEvictionRunsMillis();
    }
    
    public int getNumConnections() {
        return this.getConnectionsPool().getNumIdle();
    }
    
    protected ConnectionPool createConnectionPool(final Connection connection) {
        return new ConnectionPool(connection);
    }
    
    public long getBlockIfSessionPoolIsFullTimeout() {
        return this.blockIfSessionPoolIsFullTimeout;
    }
    
    public void setBlockIfSessionPoolIsFullTimeout(final long blockIfSessionPoolIsFullTimeout) {
        this.blockIfSessionPoolIsFullTimeout = blockIfSessionPoolIsFullTimeout;
    }
    
    static {
        LOG = LoggerFactory.getLogger(PooledConnectionFactory.class);
    }
}
