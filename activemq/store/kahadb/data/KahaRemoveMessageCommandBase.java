// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaRemoveMessageCommandBase<T> extends BaseMessage<T>
{
    private KahaTransactionInfo f_transactionInfo;
    private KahaDestination f_destination;
    private String f_messageId;
    private boolean b_messageId;
    private Buffer f_ack;
    private boolean b_ack;
    private String f_subscriptionKey;
    private boolean b_subscriptionKey;
    
    KahaRemoveMessageCommandBase() {
        this.f_transactionInfo = null;
        this.f_destination = null;
        this.f_messageId = null;
        this.f_ack = null;
        this.f_subscriptionKey = null;
    }
    
    public boolean hasTransactionInfo() {
        return this.f_transactionInfo != null;
    }
    
    public KahaTransactionInfo getTransactionInfo() {
        if (this.f_transactionInfo == null) {
            this.f_transactionInfo = new KahaTransactionInfo();
        }
        return this.f_transactionInfo;
    }
    
    public T setTransactionInfo(final KahaTransactionInfo transactionInfo) {
        this.loadAndClear();
        this.f_transactionInfo = transactionInfo;
        return (T)this;
    }
    
    public void clearTransactionInfo() {
        this.loadAndClear();
        this.f_transactionInfo = null;
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
    
    public boolean hasMessageId() {
        return this.b_messageId;
    }
    
    public String getMessageId() {
        return this.f_messageId;
    }
    
    public T setMessageId(final String messageId) {
        this.loadAndClear();
        this.b_messageId = true;
        this.f_messageId = messageId;
        return (T)this;
    }
    
    public void clearMessageId() {
        this.loadAndClear();
        this.b_messageId = false;
        this.f_messageId = null;
    }
    
    public boolean hasAck() {
        return this.b_ack;
    }
    
    public Buffer getAck() {
        return this.f_ack;
    }
    
    public T setAck(final Buffer ack) {
        this.loadAndClear();
        this.b_ack = true;
        this.f_ack = ack;
        return (T)this;
    }
    
    public void clearAck() {
        this.loadAndClear();
        this.b_ack = false;
        this.f_ack = null;
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
}
