// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.activemq.command.MessageAck;
import java.io.IOException;
import org.apache.activemq.filter.MessageEvaluationContext;
import javax.jms.JMSException;
import java.util.HashMap;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.MessageId;
import java.util.Map;
import org.slf4j.Logger;

public class QueueBrowserSubscription extends QueueSubscription
{
    protected static final Logger LOG;
    int queueRefs;
    boolean browseDone;
    boolean destinationsAdded;
    private final Map<MessageId, Object> audit;
    private long maxMessages;
    
    public QueueBrowserSubscription(final Broker broker, final SystemUsage usageManager, final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        super(broker, usageManager, context, info);
        this.audit = new HashMap<MessageId, Object>();
    }
    
    @Override
    protected boolean canDispatch(final MessageReference node) {
        return !((QueueMessageReference)node).isAcked();
    }
    
    @Override
    public synchronized String toString() {
        return "QueueBrowserSubscription: consumer=" + this.info.getConsumerId() + ", destinations=" + this.destinations.size() + ", dispatched=" + this.dispatched.size() + ", delivered=" + this.prefetchExtension + ", pending=" + this.getPendingQueueSize();
    }
    
    public synchronized void destinationsAdded() throws Exception {
        this.destinationsAdded = true;
        this.checkDone();
    }
    
    public boolean isDuplicate(final MessageId messageId) {
        if (!this.audit.containsKey(messageId)) {
            this.audit.put(messageId, Boolean.TRUE);
            return false;
        }
        return true;
    }
    
    private void checkDone() throws Exception {
        if (!this.browseDone && this.queueRefs == 0 && this.destinationsAdded) {
            this.browseDone = true;
            this.add(QueueMessageReference.NULL_MESSAGE);
        }
    }
    
    @Override
    public boolean matches(final MessageReference node, final MessageEvaluationContext context) throws IOException {
        return !this.browseDone && super.matches(node, context);
    }
    
    @Override
    protected void acknowledge(final ConnectionContext context, final MessageAck ack, final MessageReference n) throws IOException {
        if (this.info.isNetworkSubscription()) {
            super.acknowledge(context, ack, n);
        }
    }
    
    public synchronized void incrementQueueRef() {
        ++this.queueRefs;
    }
    
    public synchronized void decrementQueueRef() throws Exception {
        if (this.queueRefs > 0) {
            --this.queueRefs;
        }
        this.checkDone();
    }
    
    @Override
    public List<MessageReference> remove(final ConnectionContext context, final Destination destination) throws Exception {
        super.remove(context, destination);
        return new ArrayList<MessageReference>();
    }
    
    public boolean atMax() {
        return this.maxMessages > 0L && this.getEnqueueCounter() >= this.maxMessages;
    }
    
    public void setMaxMessages(final long max) {
        this.maxMessages = max;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueueBrowserSubscription.class);
    }
}
