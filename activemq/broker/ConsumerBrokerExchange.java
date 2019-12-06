// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.Region;
import org.apache.activemq.broker.region.Destination;

public class ConsumerBrokerExchange
{
    private ConnectionContext connectionContext;
    private Destination regionDestination;
    private Region region;
    private Subscription subscription;
    private boolean wildcard;
    
    public ConnectionContext getConnectionContext() {
        return this.connectionContext;
    }
    
    public void setConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }
    
    public Region getRegion() {
        return this.region;
    }
    
    public void setRegion(final Region region) {
        this.region = region;
    }
    
    public Destination getRegionDestination() {
        return this.regionDestination;
    }
    
    public void setRegionDestination(final Destination regionDestination) {
        this.regionDestination = regionDestination;
    }
    
    public Subscription getSubscription() {
        return this.subscription;
    }
    
    public void setSubscription(final Subscription subscription) {
        this.subscription = subscription;
    }
    
    public boolean isWildcard() {
        return this.wildcard;
    }
    
    public void setWildcard(final boolean wildcard) {
        this.wildcard = wildcard;
    }
}
