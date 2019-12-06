// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.region.policy;

import org.apache.activemq.broker.ConnectionContext;

public class SlowConsumerEntry
{
    final ConnectionContext context;
    Object subscription;
    int slowCount;
    int markCount;
    
    SlowConsumerEntry(final ConnectionContext context) {
        this.slowCount = 1;
        this.markCount = 0;
        this.context = context;
    }
    
    public void slow() {
        ++this.slowCount;
    }
    
    public void mark() {
        ++this.markCount;
    }
    
    public void setSubscription(final Object subscriptionObjectName) {
        this.subscription = subscriptionObjectName;
    }
    
    public Object getSubscription() {
        return this.subscription;
    }
    
    public int getSlowCount() {
        return this.slowCount;
    }
    
    public int getMarkCount() {
        return this.markCount;
    }
}
