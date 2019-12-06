// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.Message;

public class IndividualDeadLetterStrategy extends AbstractDeadLetterStrategy
{
    private String topicPrefix;
    private String queuePrefix;
    private String topicSuffix;
    private String queueSuffix;
    private boolean useQueueForQueueMessages;
    private boolean useQueueForTopicMessages;
    private boolean destinationPerDurableSubscriber;
    
    public IndividualDeadLetterStrategy() {
        this.topicPrefix = "ActiveMQ.DLQ.Topic.";
        this.queuePrefix = "ActiveMQ.DLQ.Queue.";
        this.useQueueForQueueMessages = true;
        this.useQueueForTopicMessages = true;
    }
    
    @Override
    public ActiveMQDestination getDeadLetterQueueFor(final Message message, final Subscription subscription) {
        if (message.getDestination().isQueue()) {
            return this.createDestination(message, this.queuePrefix, this.queueSuffix, this.useQueueForQueueMessages, subscription);
        }
        return this.createDestination(message, this.topicPrefix, this.topicSuffix, this.useQueueForTopicMessages, subscription);
    }
    
    public String getQueuePrefix() {
        return this.queuePrefix;
    }
    
    public void setQueuePrefix(final String queuePrefix) {
        this.queuePrefix = queuePrefix;
    }
    
    public String getTopicPrefix() {
        return this.topicPrefix;
    }
    
    public void setTopicPrefix(final String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
    
    public String getQueueSuffix() {
        return this.queueSuffix;
    }
    
    public void setQueueSuffix(final String queueSuffix) {
        this.queueSuffix = queueSuffix;
    }
    
    public String getTopicSuffix() {
        return this.topicSuffix;
    }
    
    public void setTopicSuffix(final String topicSuffix) {
        this.topicSuffix = topicSuffix;
    }
    
    public boolean isUseQueueForQueueMessages() {
        return this.useQueueForQueueMessages;
    }
    
    public void setUseQueueForQueueMessages(final boolean useQueueForQueueMessages) {
        this.useQueueForQueueMessages = useQueueForQueueMessages;
    }
    
    public boolean isUseQueueForTopicMessages() {
        return this.useQueueForTopicMessages;
    }
    
    public void setUseQueueForTopicMessages(final boolean useQueueForTopicMessages) {
        this.useQueueForTopicMessages = useQueueForTopicMessages;
    }
    
    public boolean isDestinationPerDurableSubscriber() {
        return this.destinationPerDurableSubscriber;
    }
    
    public void setDestinationPerDurableSubscriber(final boolean destinationPerDurableSubscriber) {
        this.destinationPerDurableSubscriber = destinationPerDurableSubscriber;
    }
    
    protected ActiveMQDestination createDestination(final Message message, final String prefix, final String suffix, final boolean useQueue, final Subscription subscription) {
        String name = null;
        final Destination regionDestination = (Destination)message.getRegionDestination();
        if (regionDestination != null && regionDestination.getActiveMQDestination() != null && regionDestination.getActiveMQDestination().getPhysicalName() != null && !regionDestination.getActiveMQDestination().getPhysicalName().isEmpty()) {
            name = prefix + regionDestination.getActiveMQDestination().getPhysicalName();
        }
        else {
            name = prefix + message.getDestination().getPhysicalName();
        }
        if (this.destinationPerDurableSubscriber && subscription instanceof DurableTopicSubscription) {
            name = name + "." + ((DurableTopicSubscription)subscription).getSubscriptionKey();
        }
        if (suffix != null && !suffix.isEmpty()) {
            name += suffix;
        }
        if (useQueue) {
            return new ActiveMQQueue(name);
        }
        return new ActiveMQTopic(name);
    }
    
    @Override
    public boolean isDLQ(final ActiveMQDestination destination) {
        final String name = destination.getPhysicalName();
        if (destination.isQueue()) {
            if ((this.queuePrefix != null && name.startsWith(this.queuePrefix)) || (this.queueSuffix != null && name.endsWith(this.queueSuffix))) {
                return true;
            }
        }
        else if ((this.topicPrefix != null && name.startsWith(this.topicPrefix)) || (this.topicSuffix != null && name.endsWith(this.topicSuffix))) {
            return true;
        }
        return false;
    }
}
