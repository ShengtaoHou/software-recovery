// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaSubscriptionCommandBase<T> extends BaseMessage<T>
{
    private KahaDestination f_destination;
    private String f_subscriptionKey;
    private boolean b_subscriptionKey;
    private boolean f_retroactive;
    private boolean b_retroactive;
    private Buffer f_subscriptionInfo;
    private boolean b_subscriptionInfo;
    
    KahaSubscriptionCommandBase() {
        this.f_destination = null;
        this.f_subscriptionKey = null;
        this.f_retroactive = false;
        this.f_subscriptionInfo = null;
    }
    
    public boolean hasDestination() {
        return this.f_destination != null;
    }
    
    public KahaDestination getDestination() {
        if (this.f_destination == null) {
            this.f_destination = new KahaDestination();
        }
        return this.f_destination;
    }
    
    public T setDestination(final KahaDestination destination) {
        this.loadAndClear();
        this.f_destination = destination;
        return (T)this;
    }
    
    public void clearDestination() {
        this.loadAndClear();
        this.f_destination = null;
    }
    
    public boolean hasSubscriptionKey() {
        return this.b_subscriptionKey;
    }
    
    public String getSubscriptionKey() {
        return this.f_subscriptionKey;
    }
    
    public T setSubscriptionKey(final String subscriptionKey) {
        this.loadAndClear();
        this.b_subscriptionKey = true;
        this.f_subscriptionKey = subscriptionKey;
        return (T)this;
    }
    
    public void clearSubscriptionKey() {
        this.loadAndClear();
        this.b_subscriptionKey = false;
        this.f_subscriptionKey = null;
    }
    
    public boolean hasRetroactive() {
        return this.b_retroactive;
    }
    
    public boolean getRetroactive() {
        return this.f_retroactive;
    }
    
    public T setRetroactive(final boolean retroactive) {
        this.loadAndClear();
        this.b_retroactive = true;
        this.f_retroactive = retroactive;
        return (T)this;
    }
    
    public void clearRetroactive() {
        this.loadAndClear();
        this.b_retroactive = false;
        this.f_retroactive = false;
    }
    
    public boolean hasSubscriptionInfo() {
        return this.b_subscriptionInfo;
    }
    
    public Buffer getSubscriptionInfo() {
        return this.f_subscriptionInfo;
    }
    
    public T setSubscriptionInfo(final Buffer subscriptionInfo) {
        this.loadAndClear();
        this.b_subscriptionInfo = true;
        this.f_subscriptionInfo = subscriptionInfo;
        return (T)this;
    }
    
    public void clearSubscriptionInfo() {
        this.loadAndClear();
        this.b_subscriptionInfo = false;
        this.f_subscriptionInfo = null;
    }
}
