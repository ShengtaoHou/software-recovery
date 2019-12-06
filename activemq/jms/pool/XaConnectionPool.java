// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.transaction.xa.XAResource;
import javax.transaction.SystemException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.Session;
import javax.jms.Connection;
import javax.transaction.TransactionManager;

public class XaConnectionPool extends ConnectionPool
{
    private final TransactionManager transactionManager;
    
    public XaConnectionPool(final Connection connection, final TransactionManager transactionManager) {
        super(connection);
        this.transactionManager = transactionManager;
    }
    
    @Override
    protected Session makeSession(final SessionKey key) throws JMSException {
        return ((XAConnection)this.connection).createXASession();
    }
    
    @Override
    public Session createSession(boolean transacted, int ackMode) throws JMSException {
        try {
            final boolean isXa = this.transactionManager != null && this.transactionManager.getStatus() != 6;
            if (isXa) {
                transacted = false;
                ackMode = 2;
            }
            else if (this.transactionManager != null) {
                transacted = false;
                if (ackMode == 0) {
                    ackMode = 1;
                }
            }
            final PooledSession session = (PooledSession)super.createSession(transacted, ackMode);
            if (isXa) {
                session.addSessionEventListener(new PooledSessionEventListener() {
                    @Override
                    public void onTemporaryQueueCreate(final TemporaryQueue tempQueue) {
                    }
                    
                    @Override
                    public void onTemporaryTopicCreate(final TemporaryTopic tempTopic) {
                    }
                    
                    @Override
                    public void onSessionClosed(final PooledSession session) {
                        session.setIgnoreClose(true);
                        session.setIsXa(false);
                    }
                });
                session.setIgnoreClose(true);
                session.setIsXa(true);
                this.transactionManager.getTransaction().registerSynchronization(new Synchronization(session));
                this.incrementReferenceCount();
                this.transactionManager.getTransaction().enlistResource(this.createXaResource(session));
            }
            else {
                session.setIgnoreClose(false);
            }
            return session;
        }
        catch (RollbackException e) {
            final JMSException jmsException = new JMSException("Rollback Exception");
            jmsException.initCause(e);
            throw jmsException;
        }
        catch (SystemException e2) {
            final JMSException jmsException = new JMSException("System Exception");
            jmsException.initCause(e2);
            throw jmsException;
        }
    }
    
    protected XAResource createXaResource(final PooledSession session) throws JMSException {
        return session.getXAResource();
    }
    
    protected class Synchronization implements javax.transaction.Synchronization
    {
        private final PooledSession session;
        
        private Synchronization(final PooledSession session) {
            this.session = session;
        }
        
        @Override
        public void beforeCompletion() {
        }
        
        @Override
        public void afterCompletion(final int status) {
            try {
                this.session.setIgnoreClose(false);
                this.session.close();
                XaConnectionPool.this.decrementReferenceCount();
            }
            catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
