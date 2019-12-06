// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

public abstract class ActiveMQMessageProducerSupport implements MessageProducer, Closeable
{
    protected ActiveMQSession session;
    protected boolean disableMessageID;
    protected boolean disableMessageTimestamp;
    protected int defaultDeliveryMode;
    protected int defaultPriority;
    protected long defaultTimeToLive;
    protected int sendTimeout;
    
    public ActiveMQMessageProducerSupport(final ActiveMQSession session) {
        this.sendTimeout = 0;
        this.session = session;
        this.disableMessageTimestamp = session.connection.isDisableTimeStampsByDefault();
    }
    
    @Override
    public void setDisableMessageID(final boolean value) throws JMSException {
        this.checkClosed();
        this.disableMessageID = value;
    }
    
    @Override
    public boolean getDisableMessageID() throws JMSException {
        this.checkClosed();
        return this.disableMessageID;
    }
    
    @Override
    public void setDisableMessageTimestamp(final boolean value) throws JMSException {
        this.checkClosed();
        this.disableMessageTimestamp = value;
    }
    
    @Override
    public boolean getDisableMessageTimestamp() throws JMSException {
        this.checkClosed();
        return this.disableMessageTimestamp;
    }
    
    @Override
    public void setDeliveryMode(final int newDeliveryMode) throws JMSException {
        if (newDeliveryMode != 2 && newDeliveryMode != 1) {
            throw new IllegalStateException("unknown delivery mode: " + newDeliveryMode);
        }
        this.checkClosed();
        this.defaultDeliveryMode = newDeliveryMode;
    }
    
    @Override
    public int getDeliveryMode() throws JMSException {
        this.checkClosed();
        return this.defaultDeliveryMode;
    }
    
    @Override
    public void setPriority(final int newDefaultPriority) throws JMSException {
        if (newDefaultPriority < 0 || newDefaultPriority > 9) {
            throw new IllegalStateException("default priority must be a value between 0 and 9");
        }
        this.checkClosed();
        this.defaultPriority = newDefaultPriority;
    }
    
    @Override
    public int getPriority() throws JMSException {
        this.checkClosed();
        return this.defaultPriority;
    }
    
    @Override
    public void setTimeToLive(final long timeToLive) throws JMSException {
        if (timeToLive < 0L) {
            throw new IllegalStateException("cannot set a negative timeToLive");
        }
        this.checkClosed();
        this.defaultTimeToLive = timeToLive;
    }
    
    @Override
    public long getTimeToLive() throws JMSException {
        this.checkClosed();
        return this.defaultTimeToLive;
    }
    
    @Override
    public void send(final Message message) throws JMSException {
        this.send(this.getDestination(), message, this.defaultDeliveryMode, this.defaultPriority, this.defaultTimeToLive);
    }
    
    @Override
    public void send(final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        this.send(this.getDestination(), message, deliveryMode, priority, timeToLive);
    }
    
    @Override
    public void send(final Destination destination, final Message message) throws JMSException {
        this.send(destination, message, this.defaultDeliveryMode, this.defaultPriority, this.defaultTimeToLive);
    }
    
    protected abstract void checkClosed() throws IllegalStateException;
    
    public int getSendTimeout() {
        return this.sendTimeout;
    }
    
    public void setSendTimeout(final int sendTimeout) {
        this.sendTimeout = sendTimeout;
    }
}
