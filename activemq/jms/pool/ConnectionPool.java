// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import org.slf4j.Logger;

public class ConnectionPool
{
    private static final transient Logger LOG;
    protected Connection connection;
    private int referenceCount;
    private long lastUsed;
    private final long firstUsed;
    private boolean hasExpired;
    private int idleTimeout;
    private long expiryTimeout;
    private boolean useAnonymousProducers;
    private final AtomicBoolean started;
    private final GenericKeyedObjectPool<SessionKey, PooledSession> sessionPool;
    private final List<PooledSession> loanedSessions;
    
    public ConnectionPool(final Connection connection) {
        this.lastUsed = System.currentTimeMillis();
        this.firstUsed = this.lastUsed;
        this.idleTimeout = 30000;
        this.expiryTimeout = 0L;
        this.useAnonymousProducers = true;
        this.started = new AtomicBoolean(false);
        this.loanedSessions = new CopyOnWriteArrayList<PooledSession>();
        this.connection = this.wrap(connection);
        this.sessionPool = (GenericKeyedObjectPool<SessionKey, PooledSession>)new GenericKeyedObjectPool((KeyedPoolableObjectFactory)new KeyedPoolableObjectFactory<SessionKey, PooledSession>() {
            public void activateObject(final SessionKey key, final PooledSession session) throws Exception {
                ConnectionPool.this.loanedSessions.add(session);
            }
            
            public void destroyObject(final SessionKey key, final PooledSession session) throws Exception {
                ConnectionPool.this.loanedSessions.remove(session);
                session.getInternalSession().close();
            }
            
            public PooledSession makeObject(final SessionKey key) throws Exception {
                final Session session = ConnectionPool.this.makeSession(key);
                return new PooledSession(key, session, (KeyedObjectPool<SessionKey, PooledSession>)ConnectionPool.this.sessionPool, key.isTransacted(), ConnectionPool.this.useAnonymousProducers);
            }
            
            public void passivateObject(final SessionKey key, final PooledSession session) throws Exception {
                ConnectionPool.this.loanedSessions.remove(session);
            }
            
            public boolean validateObject(final SessionKey key, final PooledSession session) {
                return true;
            }
        });
    }
    
    public void setHasExpired(final boolean val) {
        this.hasExpired = val;
    }
    
    protected Session makeSession(final SessionKey key) throws JMSException {
        return this.connection.createSession(key.isTransacted(), key.getAckMode());
    }
    
    protected Connection wrap(final Connection connection) {
        return connection;
    }
    
    protected void unWrap(final Connection connection) {
    }
    
    public void start() throws JMSException {
        if (this.started.compareAndSet(false, true)) {
            try {
                this.connection.start();
            }
            catch (JMSException e) {
                this.started.set(false);
                throw e;
            }
        }
    }
    
    public synchronized Connection getConnection() {
        return this.connection;
    }
    
    public Session createSession(final boolean transacted, final int ackMode) throws JMSException {
        final SessionKey key = new SessionKey(transacted, ackMode);
        PooledSession session;
        try {
            session = (PooledSession)this.sessionPool.borrowObject((Object)key);
        }
        catch (Exception e) {
            final IllegalStateException illegalStateException = new IllegalStateException(e.toString());
            illegalStateException.initCause(e);
            throw illegalStateException;
        }
        return session;
    }
    
    public synchronized void close() {
        if (this.connection != null) {
            try {
                this.sessionPool.close();
            }
            catch (Exception ex) {
                try {
                    this.connection.close();
                }
                catch (Exception ex2) {}
                finally {
                    this.connection = null;
                }
            }
            finally {
                try {
                    this.connection.close();
                }
                catch (Exception ex3) {
                    this.connection = null;
                }
                finally {
                    this.connection = null;
                }
            }
        }
    }
    
    public synchronized void incrementReferenceCount() {
        ++this.referenceCount;
        this.lastUsed = System.currentTimeMillis();
    }
    
    public synchronized void decrementReferenceCount() {
        --this.referenceCount;
        this.lastUsed = System.currentTimeMillis();
        if (this.referenceCount == 0) {
            for (final PooledSession session : this.loanedSessions) {
                try {
                    session.close();
                }
                catch (Exception ex) {}
            }
            this.loanedSessions.clear();
            this.unWrap(this.getConnection());
            this.expiredCheck();
        }
    }
    
    public synchronized boolean expiredCheck() {
        boolean expired = false;
        if (this.connection == null) {
            return true;
        }
        if (this.hasExpired && this.referenceCount == 0) {
            this.close();
            expired = true;
        }
        if (this.expiryTimeout > 0L && System.currentTimeMillis() > this.firstUsed + this.expiryTimeout) {
            this.hasExpired = true;
            if (this.referenceCount == 0) {
                this.close();
                expired = true;
            }
        }
        if (this.referenceCount == 0 && this.idleTimeout > 0 && System.currentTimeMillis() > this.lastUsed + this.idleTimeout) {
            this.hasExpired = true;
            this.close();
            expired = true;
        }
        return expired;
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
    
    public int getMaximumActiveSessionPerConnection() {
        return this.sessionPool.getMaxActive();
    }
    
    public void setMaximumActiveSessionPerConnection(final int maximumActiveSessionPerConnection) {
        this.sessionPool.setMaxActive(maximumActiveSessionPerConnection);
    }
    
    public boolean isUseAnonymousProducers() {
        return this.useAnonymousProducers;
    }
    
    public void setUseAnonymousProducers(final boolean value) {
        this.useAnonymousProducers = value;
    }
    
    public int getNumSessions() {
        return this.sessionPool.getNumIdle() + this.sessionPool.getNumActive();
    }
    
    public int getNumIdleSessions() {
        return this.sessionPool.getNumIdle();
    }
    
    public int getNumActiveSessions() {
        return this.sessionPool.getNumActive();
    }
    
    public void setBlockIfSessionPoolIsFull(final boolean block) {
        this.sessionPool.setWhenExhaustedAction((byte)(byte)(block ? 1 : 0));
    }
    
    public boolean isBlockIfSessionPoolIsFull() {
        return this.sessionPool.getWhenExhaustedAction() == 1;
    }
    
    public long getBlockIfSessionPoolIsFullTimeout() {
        return this.sessionPool.getMaxWait();
    }
    
    public void setBlockIfSessionPoolIsFullTimeout(final long blockIfSessionPoolIsFullTimeout) {
        this.sessionPool.setMaxWait(blockIfSessionPoolIsFullTimeout);
    }
    
    @Override
    public String toString() {
        return "ConnectionPool[" + this.connection + "]";
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConnectionPool.class);
    }
}
