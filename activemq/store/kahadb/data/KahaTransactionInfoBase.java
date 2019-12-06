// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaTransactionInfoBase<T> extends BaseMessage<T>
{
    private KahaLocalTransactionId f_localTransactionId;
    private KahaXATransactionId f_xaTransactionId;
    private KahaLocation f_previousEntry;
    
    KahaTransactionInfoBase() {
        this.f_localTransactionId = null;
        this.f_xaTransactionId = null;
        this.f_previousEntry = null;
    }
    
    public boolean hasLocalTransactionId() {
        return this.f_localTransactionId != null;
    }
    
    public KahaLocalTransactionId getLocalTransactionId() {
        if (this.f_localTransactionId == null) {
            this.f_localTransactionId = new KahaLocalTransactionId();
        }
        return this.f_localTransactionId;
    }
    
    public T setLocalTransactionId(final KahaLocalTransactionId localTransactionId) {
        this.loadAndClear();
        this.f_localTransactionId = localTransactionId;
        return (T)this;
    }
    
    public void clearLocalTransactionId() {
        this.loadAndClear();
        this.f_localTransactionId = null;
    }
    
    public boolean hasXaTransactionId() {
        return this.f_xaTransactionId != null;
    }
    
    public KahaXATransactionId getXaTransactionId() {
        if (this.f_xaTransactionId == null) {
            this.f_xaTransactionId = new KahaXATransactionId();
        }
        return this.f_xaTransactionId;
    }
    
    public T setXaTransactionId(final KahaXATransactionId xaTransactionId) {
        this.loadAndClear();
        this.f_xaTransactionId = xaTransactionId;
        return (T)this;
    }
    
    public void clearXaTransactionId() {
        this.loadAndClear();
        this.f_xaTransactionId = null;
    }
    
    public boolean hasPreviousEntry() {
        return this.f_previousEntry != null;
    }
    
    public KahaLocation getPreviousEntry() {
        if (this.f_previousEntry == null) {
            this.f_previousEntry = new KahaLocation();
        }
        return this.f_previousEntry;
    }
    
    public T setPreviousEntry(final KahaLocation previousEntry) {
        this.loadAndClear();
        this.f_previousEntry = previousEntry;
        return (T)this;
    }
    
    public void clearPreviousEntry() {
        this.loadAndClear();
        this.f_previousEntry = null;
    }
}
