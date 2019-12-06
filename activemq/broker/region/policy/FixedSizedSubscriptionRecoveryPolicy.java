// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.memory.list.DestinationBasedMessageList;
import org.apache.activemq.memory.list.SimpleMessageList;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.memory.list.MessageList;

public class FixedSizedSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    private MessageList buffer;
    private int maximumSize;
    private boolean useSharedBuffer;
    
    public FixedSizedSubscriptionRecoveryPolicy() {
        this.maximumSize = 65536;
        this.useSharedBuffer = true;
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        final FixedSizedSubscriptionRecoveryPolicy rc = new FixedSizedSubscriptionRecoveryPolicy();
        rc.setMaximumSize(this.maximumSize);
        rc.setUseSharedBuffer(this.useSharedBuffer);
        return rc;
    }
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference message) throws Exception {
        this.buffer.add(message);
        return true;
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        final List copy = this.buffer.getMessages(sub.getActiveMQDestination());
        if (!copy.isEmpty()) {
            for (final MessageReference node : copy) {
                sub.addRecoveredMessage(context, node);
            }
        }
    }
    
    @Override
    public void start() throws Exception {
        this.buffer = this.createMessageList();
    }
    
    @Override
    public void stop() throws Exception {
        this.buffer.clear();
    }
    
    public MessageList getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final MessageList buffer) {
        this.buffer = buffer;
    }
    
    public int getMaximumSize() {
        return this.maximumSize;
    }
    
    public void setMaximumSize(final int maximumSize) {
        this.maximumSize = maximumSize;
    }
    
    public boolean isUseSharedBuffer() {
        return this.useSharedBuffer;
    }
    
    public void setUseSharedBuffer(final boolean useSharedBuffer) {
        this.useSharedBuffer = useSharedBuffer;
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) throws Exception {
        return this.buffer.browse(destination);
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
    
    protected MessageList createMessageList() {
        if (this.useSharedBuffer) {
            return new SimpleMessageList(this.maximumSize);
        }
        return new DestinationBasedMessageList(this.maximumSize);
    }
}
