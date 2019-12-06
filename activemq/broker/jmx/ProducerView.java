// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerInfo;

public class ProducerView implements ProducerViewMBean
{
    protected final ProducerInfo info;
    protected final String clientId;
    protected final String userName;
    protected final ManagedRegionBroker broker;
    protected ActiveMQDestination lastUsedDestination;
    
    public ProducerView(final ProducerInfo info, final String clientId, final String userName, final ManagedRegionBroker broker) {
        this.info = info;
        this.clientId = clientId;
        this.userName = userName;
        this.broker = broker;
    }
    
    @Override
    public String getClientId() {
        return this.clientId;
    }
    
    @Override
    public String getConnectionId() {
        if (this.info != null) {
            return this.info.getProducerId().getConnectionId();
        }
        return "NOTSET";
    }
    
    @Override
    public long getSessionId() {
        if (this.info != null) {
            return this.info.getProducerId().getSessionId();
        }
        return 0L;
    }
    
    @Override
    public String getProducerId() {
        if (this.info != null) {
            return this.info.getProducerId().toString();
        }
        return "NOTSET";
    }
    
    @Override
    public String getDestinationName() {
        if (this.info != null && this.info.getDestination() != null) {
            final ActiveMQDestination dest = this.info.getDestination();
            return dest.getPhysicalName();
        }
        if (this.lastUsedDestination != null) {
            return this.lastUsedDestination.getPhysicalName();
        }
        return "NOTSET";
    }
    
    @Override
    public boolean isDestinationQueue() {
        if (this.info != null) {
            if (this.info.getDestination() != null) {
                final ActiveMQDestination dest = this.info.getDestination();
                return dest.isQueue();
            }
            if (this.lastUsedDestination != null) {
                return this.lastUsedDestination.isQueue();
            }
        }
        return false;
    }
    
    @Override
    public boolean isDestinationTopic() {
        if (this.info != null) {
            if (this.info.getDestination() != null) {
                final ActiveMQDestination dest = this.info.getDestination();
                return dest.isTopic();
            }
            if (this.lastUsedDestination != null) {
                return this.lastUsedDestination.isTopic();
            }
        }
        return false;
    }
    
    @Override
    public boolean isDestinationTemporary() {
        if (this.info != null) {
            if (this.info.getDestination() != null) {
                final ActiveMQDestination dest = this.info.getDestination();
                return dest.isTemporary();
            }
            if (this.lastUsedDestination != null) {
                return this.lastUsedDestination.isTemporary();
            }
        }
        return false;
    }
    
    @Override
    public int getProducerWindowSize() {
        if (this.info != null) {
            return this.info.getWindowSize();
        }
        return 0;
    }
    
    @Override
    public boolean isDispatchAsync() {
        return this.info != null && this.info.isDispatchAsync();
    }
    
    @Override
    public String toString() {
        return "ProducerView: " + this.getClientId() + ":" + this.getConnectionId();
    }
    
    void setLastUsedDestinationName(final ActiveMQDestination destinationName) {
        this.lastUsedDestination = destinationName;
    }
    
    @Override
    public String getUserName() {
        return this.userName;
    }
    
    @Override
    public boolean isProducerBlocked() {
        final ProducerBrokerExchange producerBrokerExchange = this.broker.getBrokerService().getProducerBrokerExchange(this.info);
        return producerBrokerExchange != null && producerBrokerExchange.isBlockedForFlowControl();
    }
    
    @Override
    public long getTotalTimeBlocked() {
        final ProducerBrokerExchange producerBrokerExchange = this.broker.getBrokerService().getProducerBrokerExchange(this.info);
        if (producerBrokerExchange != null) {
            return producerBrokerExchange.getTotalTimeBlocked();
        }
        return 0L;
    }
    
    @Override
    public int getPercentageBlocked() {
        final ProducerBrokerExchange producerBrokerExchange = this.broker.getBrokerService().getProducerBrokerExchange(this.info);
        if (producerBrokerExchange != null) {
            return producerBrokerExchange.getPercentageBlocked();
        }
        return 0;
    }
    
    @Override
    public void resetFlowControlStats() {
        final ProducerBrokerExchange producerBrokerExchange = this.broker.getBrokerService().getProducerBrokerExchange(this.info);
        if (producerBrokerExchange != null) {
            producerBrokerExchange.resetFlowControl();
        }
    }
    
    @Override
    public void resetStatistics() {
        if (this.info != null) {
            this.info.resetSentCount();
        }
    }
    
    @Override
    public long getSentCount() {
        return (this.info != null) ? this.info.getSentCount() : 0L;
    }
}
