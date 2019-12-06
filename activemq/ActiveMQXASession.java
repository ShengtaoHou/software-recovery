// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.transaction.xa.XAResource;
import javax.jms.Session;
import javax.jms.TransactionInProgressException;
import javax.jms.JMSException;
import org.apache.activemq.command.SessionId;
import javax.jms.XATopicSession;
import javax.jms.XAQueueSession;
import javax.jms.TopicSession;
import javax.jms.QueueSession;

public class ActiveMQXASession extends ActiveMQSession implements QueueSession, TopicSession, XAQueueSession, XATopicSession
{
    public ActiveMQXASession(final ActiveMQXAConnection connection, final SessionId sessionId, final int theAcknowlegeMode, final boolean dispatchAsync) throws JMSException {
        super(connection, sessionId, theAcknowlegeMode, dispatchAsync);
    }
    
    @Override
    public boolean getTransacted() throws JMSException {
        this.checkClosed();
        return this.getTransactionContext().isInXATransaction();
    }
    
    @Override
    public void rollback() throws JMSException {
        this.checkClosed();
        throw new TransactionInProgressException("Cannot rollback() inside an XASession");
    }
    
    @Override
    public void commit() throws JMSException {
        this.checkClosed();
        throw new TransactionInProgressException("Cannot commit() inside an XASession");
    }
    
    @Override
    public Session getSession() throws JMSException {
        return this;
    }
    
    @Override
    public XAResource getXAResource() {
        return this.getTransactionContext();
    }
    
    @Override
    public QueueSession getQueueSession() throws JMSException {
        return new ActiveMQQueueSession(this);
    }
    
    @Override
    public TopicSession getTopicSession() throws JMSException {
        return new ActiveMQTopicSession(this);
    }
    
    @Override
    public boolean isAutoAcknowledge() {
        return true;
    }
    
    @Override
    protected void doStartTransaction() throws JMSException {
    }
}
