// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.view;

import org.apache.activemq.broker.region.Destination;

public class BrokerDestinationView
{
    private final Destination destination;
    
    BrokerDestinationView(final Destination destination) {
        this.destination = destination;
    }
    
    public String getName() {
        return this.destination.getName();
    }
    
    public long getEnqueueCount() {
        return this.destination.getDestinationStatistics().getEnqueues().getCount();
    }
    
    public long getDequeueCount() {
        return this.destination.getDestinationStatistics().getDequeues().getCount();
    }
    
    public long getDispatchCount() {
        return this.destination.getDestinationStatistics().getDispatched().getCount();
    }
    
    public long getInFlightCount() {
        return this.destination.getDestinationStatistics().getInflight().getCount();
    }
    
    public long getExpiredCount() {
        return this.destination.getDestinationStatistics().getExpired().getCount();
    }
    
    public int getConsumerCount() {
        return (int)this.destination.getDestinationStatistics().getConsumers().getCount();
    }
    
    public int getProducerCount() {
        return (int)this.destination.getDestinationStatistics().getProducers().getCount();
    }
    
    public long getQueueSize() {
        return this.destination.getDestinationStatistics().getMessages().getCount();
    }
    
    public long getMessagesCached() {
        return this.destination.getDestinationStatistics().getMessagesCached().getCount();
    }
    
    public int getMemoryPercentUsage() {
        return this.destination.getMemoryUsage().getPercentUsage();
    }
    
    public long getMemoryUsageByteCount() {
        return this.destination.getMemoryUsage().getUsage();
    }
    
    public long getMemoryLimit() {
        return this.destination.getMemoryUsage().getLimit();
    }
    
    public double getAverageEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getAverageTime();
    }
    
    public long getMaxEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getMaxTime();
    }
    
    public long getMinEnqueueTime() {
        return this.destination.getDestinationStatistics().getProcessTime().getMinTime();
    }
    
    public double getAverageMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getAverageSize();
    }
    
    public long getMaxMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getMaxSize();
    }
    
    public long getMinMessageSize() {
        return this.destination.getDestinationStatistics().getMessageSize().getMinSize();
    }
    
    public boolean isDLQ() {
        return this.destination.isDLQ();
    }
    
    public long getBlockedSends() {
        return this.destination.getDestinationStatistics().getBlockedSends().getCount();
    }
    
    public double getAverageBlockedTime() {
        return this.destination.getDestinationStatistics().getBlockedTime().getAverageTime();
    }
    
    public long getTotalBlockedTime() {
        return this.destination.getDestinationStatistics().getBlockedTime().getTotalTime();
    }
}
