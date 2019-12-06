// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region;

import org.apache.activemq.management.StatisticImpl;
import org.apache.activemq.management.SizeStatisticImpl;
import org.apache.activemq.management.TimeStatisticImpl;
import org.apache.activemq.management.PollCountStatisticImpl;
import org.apache.activemq.management.CountStatisticImpl;
import org.apache.activemq.management.StatsImpl;

public class DestinationStatistics extends StatsImpl
{
    protected CountStatisticImpl enqueues;
    protected CountStatisticImpl dequeues;
    protected CountStatisticImpl consumers;
    protected CountStatisticImpl producers;
    protected CountStatisticImpl messages;
    protected PollCountStatisticImpl messagesCached;
    protected CountStatisticImpl dispatched;
    protected CountStatisticImpl inflight;
    protected CountStatisticImpl expired;
    protected TimeStatisticImpl processTime;
    protected CountStatisticImpl blockedSends;
    protected TimeStatisticImpl blockedTime;
    protected SizeStatisticImpl messageSize;
    
    public DestinationStatistics() {
        this.enqueues = new CountStatisticImpl("enqueues", "The number of messages that have been sent to the destination");
        this.dispatched = new CountStatisticImpl("dispatched", "The number of messages that have been dispatched from the destination");
        this.dequeues = new CountStatisticImpl("dequeues", "The number of messages that have been acknowledged from the destination");
        this.inflight = new CountStatisticImpl("inflight", "The number of messages dispatched but awaiting acknowledgement");
        this.expired = new CountStatisticImpl("expired", "The number of messages that have expired");
        (this.consumers = new CountStatisticImpl("consumers", "The number of consumers that that are subscribing to messages from the destination")).setDoReset(false);
        (this.producers = new CountStatisticImpl("producers", "The number of producers that that are publishing messages to the destination")).setDoReset(false);
        (this.messages = new CountStatisticImpl("messages", "The number of messages that that are being held by the destination")).setDoReset(false);
        this.messagesCached = new PollCountStatisticImpl("messagesCached", "The number of messages that are held in the destination's memory cache");
        this.processTime = new TimeStatisticImpl("processTime", "information around length of time messages are held by a destination");
        this.blockedSends = new CountStatisticImpl("blockedSends", "number of messages that have to wait for flow control");
        this.blockedTime = new TimeStatisticImpl("blockedTime", "amount of time messages are blocked for flow control");
        this.messageSize = new SizeStatisticImpl("messageSize", "Size of messages passing through the destination");
        this.addStatistic("enqueues", this.enqueues);
        this.addStatistic("dispatched", this.dispatched);
        this.addStatistic("dequeues", this.dequeues);
        this.addStatistic("inflight", this.inflight);
        this.addStatistic("expired", this.expired);
        this.addStatistic("consumers", this.consumers);
        this.addStatistic("producers", this.producers);
        this.addStatistic("messages", this.messages);
        this.addStatistic("messagesCached", this.messagesCached);
        this.addStatistic("processTime", this.processTime);
        this.addStatistic("blockedSends", this.blockedSends);
        this.addStatistic("blockedTime", this.blockedTime);
        this.addStatistic("messageSize", this.messageSize);
    }
    
    public CountStatisticImpl getEnqueues() {
        return this.enqueues;
    }
    
    public CountStatisticImpl getDequeues() {
        return this.dequeues;
    }
    
    public CountStatisticImpl getInflight() {
        return this.inflight;
    }
    
    public CountStatisticImpl getExpired() {
        return this.expired;
    }
    
    public CountStatisticImpl getConsumers() {
        return this.consumers;
    }
    
    public CountStatisticImpl getProducers() {
        return this.producers;
    }
    
    public PollCountStatisticImpl getMessagesCached() {
        return this.messagesCached;
    }
    
    public CountStatisticImpl getMessages() {
        return this.messages;
    }
    
    public void setMessagesCached(final PollCountStatisticImpl messagesCached) {
        this.messagesCached = messagesCached;
    }
    
    public CountStatisticImpl getDispatched() {
        return this.dispatched;
    }
    
    public TimeStatisticImpl getProcessTime() {
        return this.processTime;
    }
    
    public CountStatisticImpl getBlockedSends() {
        return this.blockedSends;
    }
    
    public TimeStatisticImpl getBlockedTime() {
        return this.blockedTime;
    }
    
    public SizeStatisticImpl getMessageSize() {
        return this.messageSize;
    }
    
    @Override
    public void reset() {
        if (this.isDoReset()) {
            super.reset();
            this.enqueues.reset();
            this.dequeues.reset();
            this.dispatched.reset();
            this.inflight.reset();
            this.expired.reset();
            this.blockedSends.reset();
            this.blockedTime.reset();
            this.messageSize.reset();
        }
    }
    
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        this.enqueues.setEnabled(enabled);
        this.dispatched.setEnabled(enabled);
        this.dequeues.setEnabled(enabled);
        this.inflight.setEnabled(enabled);
        this.expired.setEnabled(true);
        this.consumers.setEnabled(enabled);
        this.producers.setEnabled(enabled);
        this.messages.setEnabled(enabled);
        this.messagesCached.setEnabled(enabled);
        this.processTime.setEnabled(enabled);
        this.blockedSends.setEnabled(enabled);
        this.blockedTime.setEnabled(enabled);
        this.messageSize.setEnabled(enabled);
    }
    
    public void setParent(final DestinationStatistics parent) {
        if (parent != null) {
            this.enqueues.setParent(parent.enqueues);
            this.dispatched.setParent(parent.dispatched);
            this.dequeues.setParent(parent.dequeues);
            this.inflight.setParent(parent.inflight);
            this.expired.setParent(parent.expired);
            this.consumers.setParent(parent.consumers);
            this.producers.setParent(parent.producers);
            this.messagesCached.setParent(parent.messagesCached);
            this.messages.setParent(parent.messages);
            this.processTime.setParent(parent.processTime);
            this.blockedSends.setParent(parent.blockedSends);
            this.blockedTime.setParent(parent.blockedTime);
            this.messageSize.setParent(parent.messageSize);
        }
        else {
            this.enqueues.setParent(null);
            this.dispatched.setParent(null);
            this.dequeues.setParent(null);
            this.inflight.setParent(null);
            this.expired.setParent(null);
            this.consumers.setParent(null);
            this.producers.setParent(null);
            this.messagesCached.setParent(null);
            this.messages.setParent(null);
            this.processTime.setParent(null);
            this.blockedSends.setParent(null);
            this.blockedTime.setParent(null);
            this.messageSize.setParent(null);
        }
    }
}
