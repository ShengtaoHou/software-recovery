// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

public class PooledMessageConsumer implements MessageConsumer
{
    private final PooledSession session;
    private final MessageConsumer delegate;
    
    public PooledMessageConsumer(final PooledSession session, final MessageConsumer delegate) {
        this.session = session;
        this.delegate = delegate;
    }
    
    @Override
    public void close() throws JMSException {
        this.session.onConsumerClose(this.delegate);
        this.delegate.close();
    }
    
    @Override
    public MessageListener getMessageListener() throws JMSException {
        return this.delegate.getMessageListener();
    }
    
    @Override
    public String getMessageSelector() throws JMSException {
        return this.delegate.getMessageSelector();
    }
    
    @Override
    public Message receive() throws JMSException {
        return this.delegate.receive();
    }
    
    @Override
    public Message receive(final long timeout) throws JMSException {
        return this.delegate.receive(timeout);
    }
    
    @Override
    public Message receiveNoWait() throws JMSException {
        return this.delegate.receiveNoWait();
    }
    
    @Override
    public void setMessageListener(final MessageListener listener) throws JMSException {
        this.delegate.setMessageListener(listener);
    }
    
    @Override
    public String toString() {
        return "PooledMessageConsumer { " + this.delegate + " }";
    }
}
