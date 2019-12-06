// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.advisory;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.command.ActiveMQMessage;
import javax.jms.Message;
import javax.jms.Destination;
import javax.jms.JMSException;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import java.util.Set;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Connection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import javax.jms.MessageListener;

public class DestinationSource implements MessageListener
{
    private static final Logger LOG;
    private AtomicBoolean started;
    private final Connection connection;
    private Session session;
    private MessageConsumer queueConsumer;
    private MessageConsumer topicConsumer;
    private MessageConsumer tempTopicConsumer;
    private MessageConsumer tempQueueConsumer;
    private Set<ActiveMQQueue> queues;
    private Set<ActiveMQTopic> topics;
    private Set<ActiveMQTempQueue> temporaryQueues;
    private Set<ActiveMQTempTopic> temporaryTopics;
    private DestinationListener listener;
    
    public DestinationSource(final Connection connection) throws JMSException {
        this.started = new AtomicBoolean(false);
        this.queues = new CopyOnWriteArraySet<ActiveMQQueue>();
        this.topics = new CopyOnWriteArraySet<ActiveMQTopic>();
        this.temporaryQueues = new CopyOnWriteArraySet<ActiveMQTempQueue>();
        this.temporaryTopics = new CopyOnWriteArraySet<ActiveMQTempTopic>();
        this.connection = connection;
    }
    
    public DestinationListener getListener() {
        return this.listener;
    }
    
    public void setDestinationListener(final DestinationListener listener) {
        this.listener = listener;
    }
    
    public Set<ActiveMQQueue> getQueues() {
        return this.queues;
    }
    
    public Set<ActiveMQTopic> getTopics() {
        return this.topics;
    }
    
    public Set<ActiveMQTempQueue> getTemporaryQueues() {
        return this.temporaryQueues;
    }
    
    public Set<ActiveMQTempTopic> getTemporaryTopics() {
        return this.temporaryTopics;
    }
    
    public void start() throws JMSException {
        if (this.started.compareAndSet(false, true)) {
            this.session = this.connection.createSession(false, 1);
            (this.queueConsumer = this.session.createConsumer(AdvisorySupport.QUEUE_ADVISORY_TOPIC)).setMessageListener(this);
            (this.topicConsumer = this.session.createConsumer(AdvisorySupport.TOPIC_ADVISORY_TOPIC)).setMessageListener(this);
            (this.tempQueueConsumer = this.session.createConsumer(AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC)).setMessageListener(this);
            (this.tempTopicConsumer = this.session.createConsumer(AdvisorySupport.TEMP_TOPIC_ADVISORY_TOPIC)).setMessageListener(this);
        }
    }
    
    public void stop() throws JMSException {
        if (this.started.compareAndSet(true, false) && this.session != null) {
            this.session.close();
        }
    }
    
    @Override
    public void onMessage(final Message message) {
        if (message instanceof ActiveMQMessage) {
            final ActiveMQMessage activeMessage = (ActiveMQMessage)message;
            final Object command = activeMessage.getDataStructure();
            if (command instanceof DestinationInfo) {
                final DestinationInfo destinationInfo = (DestinationInfo)command;
                final DestinationEvent event = new DestinationEvent(this, destinationInfo);
                this.fireDestinationEvent(event);
            }
            else {
                DestinationSource.LOG.warn("Unknown dataStructure: " + command);
            }
        }
        else {
            DestinationSource.LOG.warn("Unknown message type: " + message + ". Message ignored");
        }
    }
    
    protected void fireDestinationEvent(final DestinationEvent event) {
        final ActiveMQDestination destination = event.getDestination();
        final boolean add = event.isAddOperation();
        if (destination instanceof ActiveMQQueue) {
            final ActiveMQQueue queue = (ActiveMQQueue)destination;
            if (add) {
                this.queues.add(queue);
            }
            else {
                this.queues.remove(queue);
            }
        }
        else if (destination instanceof ActiveMQTopic) {
            final ActiveMQTopic topic = (ActiveMQTopic)destination;
            if (add) {
                this.topics.add(topic);
            }
            else {
                this.topics.remove(topic);
            }
        }
        else if (destination instanceof ActiveMQTempQueue) {
            final ActiveMQTempQueue queue2 = (ActiveMQTempQueue)destination;
            if (add) {
                this.temporaryQueues.add(queue2);
            }
            else {
                this.temporaryQueues.remove(queue2);
            }
        }
        else if (destination instanceof ActiveMQTempTopic) {
            final ActiveMQTempTopic topic2 = (ActiveMQTempTopic)destination;
            if (add) {
                this.temporaryTopics.add(topic2);
            }
            else {
                this.temporaryTopics.remove(topic2);
            }
        }
        else {
            DestinationSource.LOG.warn("Unknown destination type: " + destination);
        }
        if (this.listener != null) {
            this.listener.onDestinationEvent(event);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ConsumerEventSource.class);
    }
}
