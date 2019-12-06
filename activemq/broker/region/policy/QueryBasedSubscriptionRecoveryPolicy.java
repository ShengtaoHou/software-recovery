// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.ConnectionId;
import javax.jms.JMSException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageTransformation;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.util.IdGenerator;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

public class QueryBasedSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    private static final Logger LOG;
    private MessageQuery query;
    private final AtomicLong messageSequence;
    private final IdGenerator idGenerator;
    private final ProducerId producerId;
    
    public QueryBasedSubscriptionRecoveryPolicy() {
        this.messageSequence = new AtomicLong(0L);
        this.idGenerator = new IdGenerator();
        this.producerId = this.createProducerId();
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        final QueryBasedSubscriptionRecoveryPolicy rc = new QueryBasedSubscriptionRecoveryPolicy();
        rc.setQuery(this.query);
        return rc;
    }
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference message) throws Exception {
        return this.query.validateUpdate(message.getMessage());
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        if (this.query != null) {
            final ActiveMQDestination destination = sub.getActiveMQDestination();
            this.query.execute(destination, new MessageListener() {
                @Override
                public void onMessage(final Message message) {
                    QueryBasedSubscriptionRecoveryPolicy.this.dispatchInitialMessage(message, topic, context, sub);
                }
            });
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.query == null) {
            throw new IllegalArgumentException("No query property configured");
        }
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    public MessageQuery getQuery() {
        return this.query;
    }
    
    public void setQuery(final MessageQuery query) {
        this.query = query;
    }
    
    @Override
    public org.apache.activemq.command.Message[] browse(final ActiveMQDestination dest) throws Exception {
        return new org.apache.activemq.command.Message[0];
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
    
    protected void dispatchInitialMessage(final Message message, final Destination regionDestination, final ConnectionContext context, final SubscriptionRecovery sub) {
        try {
            final ActiveMQMessage activeMessage = ActiveMQMessageTransformation.transformMessage(message, null);
            ActiveMQDestination destination = activeMessage.getDestination();
            if (destination == null) {
                destination = sub.getActiveMQDestination();
                activeMessage.setDestination(destination);
            }
            activeMessage.setRegionDestination(regionDestination);
            this.configure(activeMessage);
            sub.addRecoveredMessage(context, activeMessage);
        }
        catch (Throwable e) {
            QueryBasedSubscriptionRecoveryPolicy.LOG.warn("Failed to dispatch initial message: {} into subscription. Reason: ", message, e);
        }
    }
    
    protected void configure(final ActiveMQMessage msg) throws JMSException {
        final long sequenceNumber = this.messageSequence.incrementAndGet();
        msg.setMessageId(new MessageId(this.producerId, sequenceNumber));
        msg.onSend();
        msg.setProducerId(this.producerId);
    }
    
    protected ProducerId createProducerId() {
        final String id = this.idGenerator.generateId();
        final ConnectionId connectionId = new ConnectionId(id);
        final SessionId sessionId = new SessionId(connectionId, 1L);
        return new ProducerId(sessionId, 1L);
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueryBasedSubscriptionRecoveryPolicy.class);
    }
}
