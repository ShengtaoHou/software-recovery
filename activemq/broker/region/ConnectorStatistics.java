// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.management.StatisticImpl;
import org.apache.activemq.management.PollCountStatisticImpl;
import org.apache.activemq.management.CountStatisticImpl;
import org.apache.activemq.management.StatsImpl;

public class ConnectorStatistics extends StatsImpl
{
    protected CountStatisticImpl enqueues;
    protected CountStatisticImpl dequeues;
    protected CountStatisticImpl consumers;
    protected CountStatisticImpl messages;
    protected PollCountStatisticImpl messagesCached;
    
    public ConnectorStatistics() {
        this.enqueues = new CountStatisticImpl("enqueues", "The number of messages that have been sent to the destination");
        this.dequeues = new CountStatisticImpl("dequeues", "The number of messages that have been dispatched from the destination");
        this.consumers = new CountStatisticImpl("consumers", "The number of consumers that that are subscribing to messages from the destination");
        this.messages = new CountStatisticImpl("messages", "The number of messages that that are being held by the destination");
        this.messagesCached = new PollCountStatisticImpl("messagesCached", "The number of messages that are held in the destination's memory cache");
        this.addStatistic("enqueues", this.enqueues);
        this.addStatistic("dequeues", this.dequeues);
        this.addStatistic("consumers", this.consumers);
        this.addStatistic("messages", this.messages);
        this.addStatistic("messagesCached", this.messagesCached);
    }
    
    public CountStatisticImpl getEnqueues() {
        return this.enqueues;
    }
    
    public CountStatisticImpl getDequeues() {
        return this.dequeues;
    }
    
    public CountStatisticImpl getConsumers() {
        return this.consumers;
    }
    
    public PollCountStatisticImpl getMessagesCached() {
        return this.messagesCached;
    }
    
    public CountStatisticImpl getMessages() {
        return this.messages;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.enqueues.reset();
        this.dequeues.reset();
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        this.enqueues.setEnabled(enabled);
        this.dequeues.setEnabled(enabled);
        this.consumers.setEnabled(enabled);
        this.messages.setEnabled(enabled);
        this.messagesCached.setEnabled(enabled);
    }
    
    public void setParent(final ConnectorStatistics parent) {
        if (parent != null) {
            this.enqueues.setParent(parent.enqueues);
            this.dequeues.setParent(parent.dequeues);
            this.consumers.setParent(parent.consumers);
            this.messagesCached.setParent(parent.messagesCached);
            this.messages.setParent(parent.messages);
        }
        else {
            this.enqueues.setParent(null);
            this.dequeues.setParent(null);
            this.consumers.setParent(null);
            this.messagesCached.setParent(null);
            this.messages.setParent(null);
        }
    }
    
    public void setMessagesCached(final PollCountStatisticImpl messagesCached) {
        this.messagesCached = messagesCached;
    }
}
