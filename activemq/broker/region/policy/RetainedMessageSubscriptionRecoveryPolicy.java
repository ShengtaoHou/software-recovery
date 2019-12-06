// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.Broker;
import java.util.List;
import java.util.Arrays;
import org.apache.activemq.filter.DestinationFilter;
import java.util.ArrayList;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.region.SubscriptionRecovery;
import org.apache.activemq.broker.region.Topic;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;

public class RetainedMessageSubscriptionRecoveryPolicy implements SubscriptionRecoveryPolicy
{
    public static final String RETAIN_PROPERTY = "ActiveMQ.Retain";
    public static final String RETAINED_PROPERTY = "ActiveMQ.Retained";
    private volatile MessageReference retainedMessage;
    private SubscriptionRecoveryPolicy wrapped;
    
    public RetainedMessageSubscriptionRecoveryPolicy(final SubscriptionRecoveryPolicy wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public boolean add(final ConnectionContext context, final MessageReference node) throws Exception {
        final Message message = node.getMessage();
        final Object retainValue = message.getProperty("ActiveMQ.Retain");
        final boolean retain = retainValue != null && Boolean.parseBoolean(retainValue.toString());
        if (retain) {
            if (message.getContent().getLength() > 0) {
                this.retainedMessage = message.copy();
                this.retainedMessage.getMessage().removeProperty("ActiveMQ.Retain");
                this.retainedMessage.getMessage().setProperty("ActiveMQ.Retained", true);
            }
            else {
                this.retainedMessage = null;
            }
            node.getMessage().removeProperty("ActiveMQ.Retain");
        }
        return this.wrapped == null || this.wrapped.add(context, node);
    }
    
    @Override
    public void recover(final ConnectionContext context, final Topic topic, final SubscriptionRecovery sub) throws Exception {
        if (this.retainedMessage != null) {
            sub.addRecoveredMessage(context, this.retainedMessage);
        }
        if (this.wrapped != null) {
            boolean recover = true;
            if (sub instanceof DurableTopicSubscription && !((DurableTopicSubscription)sub).isEmpty(topic)) {
                recover = false;
            }
            if (recover) {
                this.wrapped.recover(context, topic, sub);
            }
        }
    }
    
    @Override
    public void start() throws Exception {
        if (this.wrapped != null) {
            this.wrapped.start();
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.wrapped != null) {
            this.wrapped.stop();
        }
    }
    
    @Override
    public Message[] browse(final ActiveMQDestination destination) throws Exception {
        final List<Message> result = new ArrayList<Message>();
        if (this.retainedMessage != null) {
            final DestinationFilter filter = DestinationFilter.parseFilter(destination);
            if (filter.matches(this.retainedMessage.getMessage().getDestination())) {
                result.add(this.retainedMessage.getMessage());
            }
        }
        Message[] messages = result.toArray(new Message[result.size()]);
        if (this.wrapped != null) {
            final Message[] wrappedMessages = this.wrapped.browse(destination);
            if (wrappedMessages != null && wrappedMessages.length > 0) {
                final int origLen = messages.length;
                messages = Arrays.copyOf(messages, origLen + wrappedMessages.length);
                System.arraycopy(wrappedMessages, 0, messages, origLen, wrappedMessages.length);
            }
        }
        return messages;
    }
    
    @Override
    public SubscriptionRecoveryPolicy copy() {
        return new RetainedMessageSubscriptionRecoveryPolicy(this.wrapped);
    }
    
    @Override
    public void setBroker(final Broker broker) {
    }
    
    public void setWrapped(final SubscriptionRecoveryPolicy wrapped) {
        this.wrapped = wrapped;
    }
    
    public SubscriptionRecoveryPolicy getWrapped() {
        return this.wrapped;
    }
}
