// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQTopic;
import javax.jms.JMSException;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Connection;
import org.slf4j.Logger;
import javax.jms.MessageListener;
import org.apache.activemq.Service;

public class ProducerEventSource implements Service, MessageListener
{
    private static final Logger LOG;
    private final Connection connection;
    private final ActiveMQDestination destination;
    private ProducerListener listener;
    private AtomicBoolean started;
    private AtomicInteger producerCount;
    private Session session;
    private MessageConsumer consumer;
    
    public ProducerEventSource(final Connection connection, final Destination destination) throws JMSException {
        this.started = new AtomicBoolean(false);
        this.producerCount = new AtomicInteger();
        this.connection = connection;
        this.destination = ActiveMQDestination.transform(destination);
    }
    
    public void setProducerListener(final ProducerListener listener) {
        this.listener = listener;
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.session = this.connection.createSession(false, 1);
            final ActiveMQTopic advisoryTopic = AdvisorySupport.getProducerAdvisoryTopic(this.destination);
            (this.consumer = this.session.createConsumer(advisoryTopic)).setMessageListener(this);
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false) && this.session != null) {
            this.session.close();
        }
    }
    
    @Override
    public void onMessage(final Message message) {
        if (message instanceof ActiveMQMessage) {
            final ActiveMQMessage activeMessage = (ActiveMQMessage)message;
            final Object command = activeMessage.getDataStructure();
            int count = 0;
            if (command instanceof ProducerInfo) {
                count = this.producerCount.incrementAndGet();
                count = this.extractProducerCountFromMessage(message, count);
                this.fireProducerEvent(new ProducerStartedEvent(this, this.destination, (ProducerInfo)command, count));
            }
            else if (command instanceof RemoveInfo) {
                final RemoveInfo removeInfo = (RemoveInfo)command;
                if (removeInfo.isProducerRemove()) {
                    count = this.producerCount.decrementAndGet();
                    count = this.extractProducerCountFromMessage(message, count);
                    this.fireProducerEvent(new ProducerStoppedEvent(this, this.destination, (ProducerId)removeInfo.getObjectId(), count));
                }
            }
            else {
                ProducerEventSource.LOG.warn("Unknown command: " + command);
            }
        }
        else {
            ProducerEventSource.LOG.warn("Unknown message type: " + message + ". Message ignored");
        }
    }
    
    protected int extractProducerCountFromMessage(final Message message, final int count) {
        try {
            final Object value = message.getObjectProperty("producerCount");
            if (value instanceof Number) {
                final Number n = (Number)value;
                return n.intValue();
            }
            ProducerEventSource.LOG.warn("No producerCount header available on the message: " + message);
        }
        catch (Exception e) {
            ProducerEventSource.LOG.warn("Failed to extract producerCount from message: " + message + ".Reason: " + e, e);
        }
        return count;
    }
    
    protected void fireProducerEvent(final ProducerEvent event) {
        if (this.listener != null) {
            this.listener.onProducerEvent(event);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ProducerEventSource.class);
    }
}
