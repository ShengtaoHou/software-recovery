// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;

public class SubscriptionInfo implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 55;
    protected ActiveMQDestination subscribedDestination;
    protected ActiveMQDestination destination;
    protected String clientId;
    protected String subscriptionName;
    protected String selector;
    
    public SubscriptionInfo() {
    }
    
    public SubscriptionInfo(final String clientId, final String subscriptionName) {
        this.clientId = clientId;
        this.subscriptionName = subscriptionName;
    }
    
    @Override
    public byte getDataStructureType() {
        return 55;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public ActiveMQDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ActiveMQDestination destination) {
        this.destination = destination;
    }
    
    public String getSelector() {
        return this.selector;
    }
    
    public void setSelector(final String selector) {
        this.selector = selector;
    }
    
    public String getSubcriptionName() {
        return this.subscriptionName;
    }
    
    public void setSubcriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }
    
    public String getSubscriptionName() {
        return this.subscriptionName;
    }
    
    public void setSubscriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this);
    }
    
    @Override
    public int hashCode() {
        final int h1 = (this.clientId != null) ? this.clientId.hashCode() : -1;
        final int h2 = (this.subscriptionName != null) ? this.subscriptionName.hashCode() : -1;
        return h1 ^ h2;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean result = false;
        if (obj instanceof SubscriptionInfo) {
            final SubscriptionInfo other = (SubscriptionInfo)obj;
            result = (((this.clientId == null && other.clientId == null) || (this.clientId != null && other.clientId != null && this.clientId.equals(other.clientId))) && ((this.subscriptionName == null && other.subscriptionName == null) || (this.subscriptionName != null && other.subscriptionName != null && this.subscriptionName.equals(other.subscriptionName))));
        }
        return result;
    }
    
    public ActiveMQDestination getSubscribedDestination() {
        if (this.subscribedDestination == null) {
            return this.getDestination();
        }
        return this.subscribedDestination;
    }
    
    public void setSubscribedDestination(final ActiveMQDestination subscribedDestination) {
        this.subscribedDestination = subscribedDestination;
    }
}
