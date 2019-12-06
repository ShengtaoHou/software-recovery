// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import javax.jms.IllegalStateException;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.TopicSession;
import javax.jms.QueueSession;
import javax.jms.Queue;
import javax.jms.ConnectionMetaData;
import javax.jms.ExceptionListener;
import javax.jms.Topic;
import javax.jms.ConnectionConsumer;
import javax.jms.ServerSessionPool;
import javax.jms.Destination;
import javax.jms.JMSException;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryQueue;
import java.util.List;
import org.slf4j.Logger;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

public class PooledConnection implements TopicConnection, QueueConnection, PooledSessionEventListener
{
    private static final transient Logger LOG;
    protected ConnectionPool pool;
    private volatile boolean stopped;
    private final List<TemporaryQueue> connTempQueues;
    private final List<TemporaryTopic> connTempTopics;
    private final List<PooledSession> loanedSessions;
    
    public PooledConnection(final ConnectionPool pool) {
        this.connTempQueues = new CopyOnWriteArrayList<TemporaryQueue>();
        this.connTempTopics = new CopyOnWriteArrayList<TemporaryTopic>();
        this.loanedSessions = new CopyOnWriteArrayList<PooledSession>();
        this.pool = pool;
    }
    
    public PooledConnection newInstance() {
        return new PooledConnection(this.pool);
    }
    
    @Override
    public void close() throws JMSException {
        this.cleanupConnectionTemporaryDestinations();
        this.cleanupAllLoanedSessions();
        if (this.pool != null) {
            this.pool.decrementReferenceCount();
            this.pool = null;
        }
    }
    
    @Override
    public void start() throws JMSException {
        this.assertNotClosed();
        this.pool.start();
    }
    
    @Override
    public void stop() throws JMSException {
        this.stopped = true;
    }
    
    @Override
    public ConnectionConsumer createConnectionConsumer(final Destination destination, final String selector, final ServerSessionPool serverSessionPool, final int maxMessages) throws JMSException {
        return this.getConnection().createConnectionConsumer(destination, selector, serverSessionPool, maxMessages);
    }
    
    @Override
    public ConnectionConsumer createConnectionConsumer(final Topic topic, final String s, final ServerSessionPool serverSessionPool, final int maxMessages) throws JMSException {
        return this.getConnection().createConnectionConsumer(topic, s, serverSessionPool, maxMessages);
    }
    
    @Override
    public ConnectionConsumer createDurableConnectionConsumer(final Topic topic, final String selector, final String s1, final ServerSessionPool serverSessionPool, final int i) throws JMSException {
        return this.getConnection().createDurableConnectionConsumer(topic, selector, s1, serverSessionPool, i);
    }
    
    @Override
    public String getClientID() throws JMSException {
        return this.getConnection().getClientID();
    }
    
    @Override
    public ExceptionListener getExceptionListener() throws JMSException {
        return this.getConnection().getExceptionListener();
    }
    
    @Override
    public ConnectionMetaData getMetaData() throws JMSException {
        return this.getConnection().getMetaData();
    }
    
    @Override
    public void setExceptionListener(final ExceptionListener exceptionListener) throws JMSException {
        this.getConnection().setExceptionListener(exceptionListener);
    }
    
    @Override
    public void setClientID(final String clientID) throws JMSException {
        if (this.getConnection().getClientID() == null || !this.getClientID().equals(clientID)) {
            this.getConnection().setClientID(clientID);
        }
    }
    
    @Override
    public ConnectionConsumer createConnectionConsumer(final Queue queue, final String selector, final ServerSessionPool serverSessionPool, final int maxMessages) throws JMSException {
        return this.getConnection().createConnectionConsumer(queue, selector, serverSessionPool, maxMessages);
    }
    
    @Override
    public QueueSession createQueueSession(final boolean transacted, final int ackMode) throws JMSException {
        return (QueueSession)this.createSession(transacted, ackMode);
    }
    
    @Override
    public TopicSession createTopicSession(final boolean transacted, final int ackMode) throws JMSException {
        return (TopicSession)this.createSession(transacted, ackMode);
    }
    
    @Override
    public Session createSession(final boolean transacted, final int ackMode) throws JMSException {
        final PooledSession result = (PooledSession)this.pool.createSession(transacted, ackMode);
        this.loanedSessions.add(result);
        result.addSessionEventListener(this);
        return result;
    }
    
    @Override
    public void onTemporaryQueueCreate(final TemporaryQueue tempQueue) {
        this.connTempQueues.add(tempQueue);
    }
    
    @Override
    public void onTemporaryTopicCreate(final TemporaryTopic tempTopic) {
        this.connTempTopics.add(tempTopic);
    }
    
    @Override
    public void onSessionClosed(final PooledSession session) {
        if (session != null) {
            this.loanedSessions.remove(session);
        }
    }
    
    public Connection getConnection() throws JMSException {
        this.assertNotClosed();
        return this.pool.getConnection();
    }
    
    protected void assertNotClosed() throws IllegalStateException {
        if (this.stopped || this.pool == null) {
            throw new IllegalStateException("Connection closed");
        }
    }
    
    protected Session createSession(final SessionKey key) throws JMSException {
        return this.getConnection().createSession(key.isTransacted(), key.getAckMode());
    }
    
    @Override
    public String toString() {
        return "PooledConnection { " + this.pool + " }";
    }
    
    protected void cleanupConnectionTemporaryDestinations() {
        for (final TemporaryQueue tempQueue : this.connTempQueues) {
            try {
                tempQueue.delete();
            }
            catch (JMSException ex) {
                PooledConnection.LOG.info("failed to delete Temporary Queue \"" + tempQueue.toString() + "\" on closing pooled connection: " + ex.getMessage());
            }
        }
        this.connTempQueues.clear();
        for (final TemporaryTopic tempTopic : this.connTempTopics) {
            try {
                tempTopic.delete();
            }
            catch (JMSException ex) {
                PooledConnection.LOG.info("failed to delete Temporary Topic \"" + tempTopic.toString() + "\" on closing pooled connection: " + ex.getMessage());
            }
        }
        this.connTempTopics.clear();
    }
    
    protected void cleanupAllLoanedSessions() {
        for (final PooledSession session : this.loanedSessions) {
            try {
                session.close();
            }
            catch (JMSException ex) {
                PooledConnection.LOG.info("failed to close laoned Session \"" + session + "\" on closing pooled connection: " + ex.getMessage());
            }
        }
        this.loanedSessions.clear();
    }
    
    public int getNumSessions() {
        return this.pool.getNumSessions();
    }
    
    public int getNumActiveSessions() {
        return this.pool.getNumActiveSessions();
    }
    
    public int getNumtIdleSessions() {
        return this.pool.getNumIdleSessions();
    }
    
    static {
        LOG = LoggerFactory.getLogger(PooledConnection.class);
    }
}
