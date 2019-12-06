// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ProducerAck;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.InvalidDestinationException;
import javax.jms.Message;
import javax.jms.IllegalStateException;
import org.apache.activemq.management.StatsImpl;
import javax.jms.JMSException;
import org.apache.activemq.command.Command;
import javax.jms.Destination;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;
import java.util.HashMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.usage.MemoryUsage;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.management.JMSProducerStatsImpl;
import org.apache.activemq.command.ProducerInfo;
import org.slf4j.Logger;
import org.apache.activemq.management.StatsCapable;

public class ActiveMQMessageProducer extends ActiveMQMessageProducerSupport implements StatsCapable, Disposable
{
    private static final Logger LOG;
    protected ProducerInfo info;
    protected boolean closed;
    private final JMSProducerStatsImpl stats;
    private AtomicLong messageSequence;
    private final long startTime;
    private MessageTransformer transformer;
    private MemoryUsage producerWindow;
    
    protected ActiveMQMessageProducer(final ActiveMQSession session, final ProducerId producerId, final ActiveMQDestination destination, final int sendTimeout) throws JMSException {
        super(session);
        (this.info = new ProducerInfo(producerId)).setWindowSize(session.connection.getProducerWindowSize());
        if (destination != null && destination.getOptions() != null) {
            final Map<String, Object> options = IntrospectionSupport.extractProperties(new HashMap(destination.getOptions()), "producer.");
            IntrospectionSupport.setProperties(this.info, options);
            if (options.size() > 0) {
                final String msg = "There are " + options.size() + " producer options that couldn't be set on the producer. Check the options are spelled correctly. Unknown parameters=[" + options + "]. This producer cannot be started.";
                ActiveMQMessageProducer.LOG.warn(msg);
                throw new ConfigurationException(msg);
            }
        }
        this.info.setDestination(destination);
        if (session.connection.getProtocolVersion() >= 3 && this.info.getWindowSize() > 0) {
            (this.producerWindow = new MemoryUsage("Producer Window: " + producerId)).setExecutor(session.getConnectionExecutor());
            this.producerWindow.setLimit(this.info.getWindowSize());
            this.producerWindow.start();
        }
        this.defaultDeliveryMode = 2;
        this.defaultPriority = 4;
        this.defaultTimeToLive = 0L;
        this.startTime = System.currentTimeMillis();
        this.messageSequence = new AtomicLong(0L);
        this.stats = new JMSProducerStatsImpl(session.getSessionStats(), destination);
        try {
            this.session.addProducer(this);
            this.session.syncSendPacket(this.info);
        }
        catch (JMSException e) {
            this.session.removeProducer(this);
            throw e;
        }
        this.setSendTimeout(sendTimeout);
        this.setTransformer(session.getTransformer());
    }
    
    @Override
    public StatsImpl getStats() {
        return this.stats;
    }
    
    public JMSProducerStatsImpl getProducerStats() {
        return this.stats;
    }
    
    @Override
    public Destination getDestination() throws JMSException {
        this.checkClosed();
        return this.info.getDestination();
    }
    
    @Override
    public void close() throws JMSException {
        if (!this.closed) {
            this.dispose();
            this.session.asyncSendPacket(this.info.createRemoveCommand());
        }
    }
    
    @Override
    public void dispose() {
        if (!this.closed) {
            this.session.removeProducer(this);
            if (this.producerWindow != null) {
                this.producerWindow.stop();
            }
            this.closed = true;
        }
    }
    
    @Override
    protected void checkClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException("The producer is closed");
        }
    }
    
    @Override
    public void send(final Destination destination, final Message message, final int deliveryMode, final int priority, final long timeToLive) throws JMSException {
        this.send(destination, message, deliveryMode, priority, timeToLive, null);
    }
    
    public void send(final Message message, final AsyncCallback onComplete) throws JMSException {
        this.send(this.getDestination(), message, this.defaultDeliveryMode, this.defaultPriority, this.defaultTimeToLive, onComplete);
    }
    
    public void send(final Destination destination, final Message message, final AsyncCallback onComplete) throws JMSException {
        this.send(destination, message, this.defaultDeliveryMode, this.defaultPriority, this.defaultTimeToLive, onComplete);
    }
    
    public void send(final Message message, final int deliveryMode, final int priority, final long timeToLive, final AsyncCallback onComplete) throws JMSException {
        this.send(this.getDestination(), message, deliveryMode, priority, timeToLive, onComplete);
    }
    
    public void send(final Destination destination, Message message, final int deliveryMode, final int priority, final long timeToLive, final AsyncCallback onComplete) throws JMSException {
        this.checkClosed();
        if (destination == null) {
            if (this.info.getDestination() == null) {
                throw new UnsupportedOperationException("A destination must be specified.");
            }
            throw new InvalidDestinationException("Don't understand null destinations");
        }
        else {
            ActiveMQDestination dest;
            if (destination.equals(this.info.getDestination())) {
                dest = (ActiveMQDestination)destination;
            }
            else {
                if (this.info.getDestination() != null) {
                    throw new UnsupportedOperationException("This producer can only send messages to: " + this.info.getDestination().getPhysicalName());
                }
                dest = ActiveMQDestination.transform(destination);
            }
            if (dest == null) {
                throw new JMSException("No destination specified");
            }
            if (this.transformer != null) {
                final Message transformedMessage = this.transformer.producerTransform(this.session, this, message);
                if (transformedMessage != null) {
                    message = transformedMessage;
                }
            }
            if (this.producerWindow != null) {
                try {
                    this.producerWindow.waitForSpace();
                }
                catch (InterruptedException e) {
                    throw new JMSException("Send aborted due to thread interrupt.");
                }
            }
            this.session.send(this, dest, message, deliveryMode, priority, timeToLive, this.producerWindow, this.sendTimeout, onComplete);
            this.stats.onMessage();
        }
    }
    
    public MessageTransformer getTransformer() {
        return this.transformer;
    }
    
    public void setTransformer(final MessageTransformer transformer) {
        this.transformer = transformer;
    }
    
    protected long getStartTime() {
        return this.startTime;
    }
    
    protected long getMessageSequence() {
        return this.messageSequence.incrementAndGet();
    }
    
    protected void setMessageSequence(final AtomicLong messageSequence) {
        this.messageSequence = messageSequence;
    }
    
    protected ProducerInfo getProducerInfo() {
        return (this.info != null) ? this.info : null;
    }
    
    protected void setProducerInfo(final ProducerInfo info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        return "ActiveMQMessageProducer { value=" + this.info.getProducerId() + " }";
    }
    
    public void onProducerAck(final ProducerAck pa) {
        if (this.producerWindow != null) {
            this.producerWindow.decreaseUsage(pa.getSize());
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ActiveMQMessageProducer.class);
    }
}
