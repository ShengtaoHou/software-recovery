// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Session;
import javax.jms.XAQueueSession;
import javax.jms.XATopicSession;
import javax.jms.JMSException;
import javax.jms.XASession;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.transport.Transport;
import javax.jms.XAConnection;
import javax.jms.XAQueueConnection;
import javax.jms.XATopicConnection;

public class ActiveMQXAConnection extends ActiveMQConnection implements XATopicConnection, XAQueueConnection, XAConnection
{
    private int xaAckMode;
    
    protected ActiveMQXAConnection(final Transport transport, final IdGenerator clientIdGenerator, final IdGenerator connectionIdGenerator, final JMSStatsImpl factoryStats) throws Exception {
        super(transport, clientIdGenerator, connectionIdGenerator, factoryStats);
    }
    
    @Override
    public XASession createXASession() throws JMSException {
        return (XASession)this.createSession(true, 0);
    }
    
    @Override
    public XATopicSession createXATopicSession() throws JMSException {
        return (XATopicSession)this.createSession(true, 0);
    }
    
    @Override
    public XAQueueSession createXAQueueSession() throws JMSException {
        return (XAQueueSession)this.createSession(true, 0);
    }
    
    @Override
    public Session createSession(final boolean transacted, final int acknowledgeMode) throws JMSException {
        this.checkClosedOrFailed();
        this.ensureConnectionInfoSent();
        return new ActiveMQXASession(this, this.getNextSessionId(), this.getAckMode(), this.isDispatchAsync());
    }
    
    private int getAckMode() {
        return (this.xaAckMode > 0) ? this.xaAckMode : 0;
    }
    
    public void setXaAckMode(final int xaAckMode) {
        this.xaAckMode = xaAckMode;
    }
    
    public int getXaAckMode() {
        return this.xaAckMode;
    }
}
