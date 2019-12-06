// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

import org.apache.activemq.command.SubscriptionInfo;

public class SubscriptionKey
{
    public final String clientId;
    public final String subscriptionName;
    private final int hashValue;
    
    public SubscriptionKey(final SubscriptionInfo info) {
        this(info.getClientId(), info.getSubscriptionName());
    }
    
    public SubscriptionKey(final String clientId, final String subscriptionName) {
        this.clientId = clientId;
        this.subscriptionName = ((subscriptionName != null) ? subscriptionName : "NOT_SET");
        this.hashValue = (clientId.hashCode() ^ this.subscriptionName.hashCode());
    }
    
    @Override
    public int hashCode() {
        return this.hashValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final SubscriptionKey key = (SubscriptionKey)o;
            return key.clientId.equals(this.clientId) && key.subscriptionName.equals(this.subscriptionName);
        }
        catch (Throwable e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return this.clientId + ":" + this.subscriptionName;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public String getSubscriptionName() {
        return this.subscriptionName;
    }
}
