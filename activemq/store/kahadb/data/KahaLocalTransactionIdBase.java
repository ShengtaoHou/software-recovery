// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaLocalTransactionIdBase<T> extends BaseMessage<T>
{
    private String f_connectionId;
    private boolean b_connectionId;
    private long f_transactionId;
    private boolean b_transactionId;
    
    KahaLocalTransactionIdBase() {
        this.f_connectionId = null;
        this.f_transactionId = 0L;
    }
    
    public boolean hasConnectionId() {
        return this.b_connectionId;
    }
    
    public String getConnectionId() {
        return this.f_connectionId;
    }
    
    public T setConnectionId(final String connectionId) {
        this.loadAndClear();
        this.b_connectionId = true;
        this.f_connectionId = connectionId;
        return (T)this;
    }
    
    public void clearConnectionId() {
        this.loadAndClear();
        this.b_connectionId = false;
        this.f_connectionId = null;
    }
    
    public boolean hasTransactionId() {
        return this.b_transactionId;
    }
    
    public long getTransactionId() {
        return this.f_transactionId;
    }
    
    public T setTransactionId(final long transactionId) {
        this.loadAndClear();
        this.b_transactionId = true;
        this.f_transactionId = transactionId;
        return (T)this;
    }
    
    public void clearTransactionId() {
        this.loadAndClear();
        this.b_transactionId = false;
        this.f_transactionId = 0L;
    }
}
