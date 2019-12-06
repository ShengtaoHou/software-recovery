// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.jms.pool;

import javax.jms.InvalidDestinationException;
import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.Destination;
import javax.jms.MessageProducer;

public class PooledProducer implements MessageProducer
{
    private final MessageProducer messageProducer;
    private final Destination destination;
    private int deliveryMode;
    private boolean disableMessageID;
    private boolean disableMessageTimestamp;
    private int priority;
    private long timeToLive;
    private boolean anonymous;
    
    public PooledProducer(final MessageProducer messageProducer, final Destination destination) throws JMSException {
        this.anonymous = true;
        this.messageProducer = messageProducer;
        this.destination = destination;
        this.anonymous = (messageProducer.getDestination() == null);
        this.deliveryMode = messageProducer.getDeliveryMode();
        this.disableMessageID = messageProducer.getDisableMessageID();
        this.disableMessageTimestamp = messageProducer.getDisableMessageTimestamp();
        this.priority = messageProducer.getPriority();
        this.timeToLive = messageProducer.getTimeToLive();
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.anonymous) {
            this.messageProducer.close();
        }
    }
    
    @Override
    public void send(final Destination destination, final Message message) throws JMSException {
        this.send(destination, message, this.getDeliveryMode(), this.getPriority(), this.getTimeToLive());
    }
    
    @Override
    public void send(final Message message) throws JMSException {
        this.send(this.destination, message, this.getDeliveryMode(), this.getPriority(), this.getTimeToLive());
    }
    
    @Override
    public void send(final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        this.send(this.destination, message, deliveryMode, priority, timeToLive);
    }
    
    @Override
    public void send(final Destination destination, final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        if (destination != null) {
            final MessageProducer messageProducer = this.getMessageProducer();
            synchronized (messageProducer) {
                if (this.anonymous && this.destination != null && !this.destination.equals(destination)) {
                    throw new UnsupportedOperationException("This producer can only send messages to: " + this.destination);
                }
                messageProducer.send(destination, message, deliveryMode, priority, timeToLive);
            }
            return;
        }
        if (this.messageProducer.getDestination() == null) {
            throw new UnsupportedOperationException("A destination must be specified.");
        }
        throw new InvalidDestinationException("Don't understand null destinations");
    }
    
    @Override
    public Destination getDestination() {
        return this.destination;
    }
    
    @Override
    public int getDeliveryMode() {
        return this.deliveryMode;
    }
    
    @Override
    public void setDeliveryMode(final int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }
    
    @Override
    public boolean getDisableMessageID() {
        return this.disableMessageID;
    }
    
    @Override
    public void setDisableMessageID(final boolean disableMessageID) {
        this.disableMessageID = disableMessageID;
    }
    
    @Override
    public boolean getDisableMessageTimestamp() {
        return this.disableMessageTimestamp;
    }
    
    @Override
    public void setDisableMessageTimestamp(final boolean disableMessageTimestamp) {
        this.disableMessageTimestamp = disableMessageTimestamp;
    }
    
    @Override
    public int getPriority() {
        return this.priority;
    }
    
    @Override
    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    @Override
    public long getTimeToLive() {
        return this.timeToLive;
    }
    
    @Override
    public void setTimeToLive(final long timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    protected MessageProducer getMessageProducer() {
        return this.messageProducer;
    }
    
    protected boolean isAnonymous() {
        return this.anonymous;
    }
    
    @Override
    public String toString() {
        return "PooledProducer { " + this.messageProducer + " }";
    }
}
