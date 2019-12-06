// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.activemq.command.MessageAck;
import javax.jms.JMSException;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.Broker;
import org.slf4j.Logger;

public class QueueSubscription extends PrefetchSubscription implements LockOwner
{
    private static final Logger LOG;
    
    public QueueSubscription(final Broker broker, final SystemUsage usageManager, final ConnectionContext context, final ConsumerInfo info) throws JMSException {
        super(broker, usageManager, context, info);
    }
    
    @Override
    protected void acknowledge(final ConnectionContext context, final MessageAck ack, final MessageReference n) throws IOException {
        this.setTimeOfLastMessageAck(System.currentTimeMillis());
        final Destination q = (Destination)n.getRegionDestination();
        final QueueMessageReference node = (QueueMessageReference)n;
        final Queue queue = (Queue)q;
        if (n.isExpired() && !this.broker.isExpired(n)) {
            QueueSubscription.LOG.debug("ignoring ack {}, for already expired message: {}", ack, n);
            return;
        }
        queue.removeMessage(context, this, node, ack);
    }
    
    @Override
    protected boolean canDispatch(final MessageReference n) throws IOException {
        boolean result = true;
        final QueueMessageReference node = (QueueMessageReference)n;
        if (node.isAcked() || node.isDropped()) {
            result = false;
        }
        result = (result && (this.isBrowser() || node.lock(this)));
        return result;
    }
    
    @Override
    public synchronized String toString() {
        return "QueueSubscription: consumer=" + this.info.getConsumerId() + ", destinations=" + this.destinations.size() + ", dispatched=" + this.dispatched.size() + ", delivered=" + this.prefetchExtension + ", pending=" + this.getPendingQueueSize();
    }
    
    @Override
    public int getLockPriority() {
        return this.info.getPriority();
    }
    
    @Override
    public boolean isLockExclusive() {
        return this.info.isExclusive();
    }
    
    @Override
    public void destroy() {
        this.setSlowConsumer(false);
    }
    
    @Override
    protected boolean isDropped(final MessageReference node) {
        boolean result = false;
        if (node instanceof IndirectMessageReference) {
            final QueueMessageReference qmr = (QueueMessageReference)node;
            result = qmr.isDropped();
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueueSubscription.class);
    }
}
