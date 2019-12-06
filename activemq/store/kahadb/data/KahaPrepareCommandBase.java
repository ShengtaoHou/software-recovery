// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaPrepareCommandBase<T> extends BaseMessage<T>
{
    private KahaTransactionInfo f_transactionInfo;
    
    KahaPrepareCommandBase() {
        this.f_transactionInfo = null;
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
}
