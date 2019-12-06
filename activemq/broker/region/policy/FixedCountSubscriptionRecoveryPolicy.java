// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.Broker;
import java.util.List;
import org.apache.activemq.filter.DestinationFilter;
import java.util.ArrayList;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;

public class FixedCountSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    private volatile MessageReference[] messages;
    private int maximumSize;
    private int tail;
    
    public FixedCountSubscriptionRecoveryPolicy() {
        this.maximumSize = 100;
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        final FixedCountSubscriptionRecoveryPolicy rc = new FixedCountSubscriptionRecoveryPolicy();
        rc.setMaximumSize(this.maximumSize);
        return rc;
    }
    
    @Override
    public synchronized boolean add(final ConnectionContext context, final MessageReference node) throws Exception {
        this.messages[this.tail++] = node;
        if (this.tail >= this.messages.length) {
            this.tail = 0;
        }
        return true;
    }
    
    @Override
    public synchronized void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        int t = this.tail;
        if (this.messages[t] == null) {
            t = 0;
        }
        if (this.messages[t] == null) {
            return;
        }
        do {
            final MessageReference node = this.messages[t];
            sub.addRecoveredMessage(context, node);
            if (++t >= this.messages.length) {
                t = 0;
            }
        } while (t != this.tail);
    }
    
    @Override
    public void start() throws Exception {
        this.messages = new MessageReference[this.maximumSize];
    }
    
    @Override
    public void stop() throws Exception {
        this.messages = null;
    }
    
    public int getMaximumSize() {
        return this.maximumSize;
    }
    
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }
    
    @Override
    public synchronized Message[] browse(final ActiveMQDestination destination) throws Exception {
        final List<Message> result = new ArrayList<Message>();
        final DestinationFilter filter = DestinationFilter.parseFilter(destination);
        int t = this.tail;
        if (this.messages[t] == null) {
            t = 0;
        }
        if (this.messages[t] != null) {
            do {
                final MessageReference ref = this.messages[t];
                final Message message = ref.getMessage();
                if (filter.matches(message.getDestination())) {
                    result.add(message);
                }
                if (++t >= this.messages.length) {
                    t = 0;
                }
            } while (t != this.tail);
        }
        return result.toArray(new Message[result.size()]);
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
}
