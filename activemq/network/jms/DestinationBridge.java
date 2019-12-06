// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import org.slf4j.LoggerFactory;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Destination;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.MessageConsumer;
import org.slf4j.Logger;
import javax.jms.MessageListener;
import org.apache.activemq.Service;

public abstract class DestinationBridge implements Service, MessageListener
{
    private static final Logger LOG;
    protected MessageConsumer consumer;
    protected AtomicBoolean started;
    protected JmsMesageConvertor jmsMessageConvertor;
    protected boolean doHandleReplyTo;
    protected JmsConnector jmsConnector;
    
    public DestinationBridge() {
        this.started = new AtomicBoolean(false);
        this.doHandleReplyTo = true;
    }
    
    public MessageConsumer getConsumer() {
        return this.consumer;
    }
    
    public void setConsumer(final MessageConsumer consumer) {
        this.consumer = consumer;
    }
    
    public void setJmsConnector(final JmsConnector connector) {
        this.jmsConnector = connector;
    }
    
    public JmsMesageConvertor getJmsMessageConvertor() {
        return this.jmsMessageConvertor;
    }
    
    public void setJmsMessageConvertor(final JmsMesageConvertor jmsMessageConvertor) {
        this.jmsMessageConvertor = jmsMessageConvertor;
    }
    
    protected Destination processReplyToDestination(final Destination destination) {
        return this.jmsConnector.createReplyToBridge(destination, this.getConnnectionForConsumer(), this.getConnectionForProducer());
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.createConsumer();
            this.createProducer();
        }
    }
    
    @Override
    public void stop() throws Exception {
        this.started.set(false);
    }
    
    @Override
    public void onMessage(final Message message) {
        int attempt = 0;
        final int maxRetries = this.jmsConnector.getReconnectionPolicy().getMaxSendRetries();
        while (this.started.get() && message != null && attempt <= maxRetries) {
            try {
                if (attempt++ > 0) {
                    try {
                        Thread.sleep(this.jmsConnector.getReconnectionPolicy().getNextDelay(attempt));
                    }
                    catch (InterruptedException e2) {
                        break;
                    }
                }
                if (this.jmsMessageConvertor != null) {
                    Message converted;
                    if (this.doHandleReplyTo) {
                        final Destination replyTo = message.getJMSReplyTo();
                        if (replyTo != null) {
                            converted = this.jmsMessageConvertor.convert(message, this.processReplyToDestination(replyTo));
                        }
                        else {
                            converted = this.jmsMessageConvertor.convert(message);
                        }
                    }
                    else {
                        message.setJMSReplyTo(null);
                        converted = this.jmsMessageConvertor.convert(message);
                    }
                    try {
                        this.sendMessage(converted);
                    }
                    catch (Exception e3) {
                        this.jmsConnector.handleConnectionFailure(this.getConnectionForProducer());
                        continue;
                    }
                    try {
                        message.acknowledge();
                    }
                    catch (Exception e3) {
                        this.jmsConnector.handleConnectionFailure(this.getConnnectionForConsumer());
                        continue;
                    }
                    return;
                }
                continue;
            }
            catch (Exception e) {
                DestinationBridge.LOG.info("failed to forward message on attempt: {} reason: {} message: {}", (Object)new Object[] { attempt, e, message }, e);
                continue;
            }
            break;
        }
    }
    
    protected boolean isDoHandleReplyTo() {
        return this.doHandleReplyTo;
    }
    
    protected void setDoHandleReplyTo(final boolean doHandleReplyTo) {
        this.doHandleReplyTo = doHandleReplyTo;
    }
    
    protected abstract MessageConsumer createConsumer() throws JMSException;
    
    protected abstract MessageProducer createProducer() throws JMSException;
    
    protected abstract void sendMessage(final Message p0) throws JMSException;
    
    protected abstract Connection getConnnectionForConsumer();
    
    protected abstract Connection getConnectionForProducer();
    
    static {
        LOG = LoggerFactory.getLogger(DestinationBridge.class);
    }
}
