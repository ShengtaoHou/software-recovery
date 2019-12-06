// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Session;
import javax.jms.ServerSession;
import org.apache.activemq.command.MessageDispatch;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.ConsumerInfo;
import javax.jms.ServerSessionPool;
import javax.jms.ConnectionConsumer;

public class ActiveMQConnectionConsumer implements ConnectionConsumer, ActiveMQDispatcher
{
    private ActiveMQConnection connection;
    private ServerSessionPool sessionPool;
    private ConsumerInfo consumerInfo;
    private boolean closed;
    
    protected ActiveMQConnectionConsumer(final ActiveMQConnection theConnection, final ServerSessionPool theSessionPool, final ConsumerInfo theConsumerInfo) throws JMSException {
        this.connection = theConnection;
        this.sessionPool = theSessionPool;
        this.consumerInfo = theConsumerInfo;
        this.connection.addConnectionConsumer(this);
        this.connection.addDispatcher(this.consumerInfo.getConsumerId(), this);
        this.connection.syncSendPacket(this.consumerInfo);
    }
    
    @Override
    public ServerSessionPool getServerSessionPool() throws JMSException {
        if (this.closed) {
            throw new IllegalStateException("The Connection Consumer is closed");
        }
        return this.sessionPool;
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.closed) {
            this.dispose();
            this.connection.asyncSendPacket(this.consumerInfo.createRemoveCommand());
        }
    }
    
    public void dispose() {
        if (!this.closed) {
            this.connection.removeDispatcher(this.consumerInfo.getConsumerId());
            this.connection.removeConnectionConsumer(this);
            this.closed = true;
        }
    }
    
    @Override
    public void dispatch(final MessageDispatch messageDispatch) {
        try {
            messageDispatch.setConsumer(this);
            final ServerSession serverSession = this.sessionPool.getServerSession();
            final Session s = serverSession.getSession();
            ActiveMQSession session = null;
            if (s instanceof ActiveMQSession) {
                session = (ActiveMQSession)s;
            }
            else if (s instanceof ActiveMQTopicSession) {
                final ActiveMQTopicSession topicSession = (ActiveMQTopicSession)s;
                session = (ActiveMQSession)topicSession.getNext();
            }
            else {
                if (!(s instanceof ActiveMQQueueSession)) {
                    this.connection.onClientInternalException(new JMSException("Session pool provided an invalid session type: " + s.getClass()));
                    return;
                }
                final ActiveMQQueueSession queueSession = (ActiveMQQueueSession)s;
                session = (ActiveMQSession)queueSession.getNext();
            }
            session.dispatch(messageDispatch);
            serverSession.start();
        }
        catch (JMSException e) {
            this.connection.onAsyncException(e);
        }
    }
    
    @Override
    public String toString() {
        return "ActiveMQConnectionConsumer { value=" + this.consumerInfo.getConsumerId() + " }";
    }
    
    public void clearMessagesInProgress(final AtomicInteger transportInterruptionProcessingComplete) {
    }
    
    public ConsumerInfo getConsumerInfo() {
        return this.consumerInfo;
    }
}
