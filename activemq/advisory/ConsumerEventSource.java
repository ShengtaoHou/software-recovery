// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.RemoveInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQTopic;
import javax.jms.JMSException;
import javax.jms.Destination;
import org.apache.activemq.ActiveMQMessageConsumer;
import javax.jms.Session;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.command.ActiveMQDestination;
import javax.jms.Connection;
import org.slf4j.Logger;
import javax.jms.MessageListener;
import org.apache.activemq.Service;

public class ConsumerEventSource implements Service, MessageListener
{
    private static final Logger LOG;
    private final Connection connection;
    private final ActiveMQDestination destination;
    private ConsumerListener listener;
    private AtomicBoolean started;
    private AtomicInteger consumerCount;
    private Session session;
    private ActiveMQMessageConsumer consumer;
    
    public ConsumerEventSource(final Connection connection, final Destination destination) throws JMSException {
        this.started = new AtomicBoolean(false);
        this.consumerCount = new AtomicInteger();
        this.connection = connection;
        this.destination = ActiveMQDestination.transform(destination);
    }
    
    public void setConsumerListener(final ConsumerListener listener) {
        this.listener = listener;
    }
    
    public String getConsumerId() {
        return (this.consumer != null) ? this.consumer.getConsumerId().toString() : "NOT_SET";
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.session = this.connection.createSession(false, 1);
            final ActiveMQTopic advisoryTopic = AdvisorySupport.getConsumerAdvisoryTopic(this.destination);
            (this.consumer = (ActiveMQMessageConsumer)this.session.createConsumer(advisoryTopic)).setMessageListener(this);
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
            if (command instanceof ConsumerInfo) {
                count = this.consumerCount.incrementAndGet();
                count = this.extractConsumerCountFromMessage(message, count);
                this.fireConsumerEvent(new ConsumerStartedEvent(this, this.destination, (ConsumerInfo)command, count));
            }
            else if (command instanceof RemoveInfo) {
                final RemoveInfo removeInfo = (RemoveInfo)command;
                if (removeInfo.isConsumerRemove()) {
                    count = this.consumerCount.decrementAndGet();
                    count = this.extractConsumerCountFromMessage(message, count);
                    this.fireConsumerEvent(new ConsumerStoppedEvent(this, this.destination, (ConsumerId)removeInfo.getObjectId(), count));
                }
            }
            else {
                ConsumerEventSource.LOG.warn("Unknown command: " + command);
            }
        }
        else {
            ConsumerEventSource.LOG.warn("Unknown message type: " + message + ". Message ignored");
        }
    }
    
    protected int extractConsumerCountFromMessage(final Message message, final int count) {
        try {
            final Object value = message.getObjectProperty("consumerCount");
            if (value instanceof Number) {
                final Number n = (Number)value;
                return n.intValue();
            }
            ConsumerEventSource.LOG.warn("No consumerCount header available on the message: " + message);
        }
        catch (Exception e) {
            ConsumerEventSource.LOG.warn("Failed to extract consumerCount from message: " + message + ".Reason: " + e, e);
        }
        return count;
    }
    
    protected void fireConsumerEvent(final ConsumerEvent event) {
        if (this.listener != null) {
            this.listener.onConsumerEvent(event);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConsumerEventSource.class);
    }
}
