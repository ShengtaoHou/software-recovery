// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaXATransactionIdBase<T> extends BaseMessage<T>
{
    private int f_formatId;
    private boolean b_formatId;
    private Buffer f_branchQualifier;
    private boolean b_branchQualifier;
    private Buffer f_globalTransactionId;
    private boolean b_globalTransactionId;
    
    KahaXATransactionIdBase() {
        this.f_formatId = 0;
        this.f_branchQualifier = null;
        this.f_globalTransactionId = null;
    }
    
    public boolean hasFormatId() {
        return this.b_formatId;
    }
    
    public int getFormatId() {
        return this.f_formatId;
    }
    
    public T setFormatId(final int formatId) {
        this.loadAndClear();
        this.b_formatId = true;
        this.f_formatId = formatId;
        return (T)this;
    }
    
    public void clearFormatId() {
        this.loadAndClear();
        this.b_formatId = false;
        this.f_formatId = 0;
    }
    
    public boolean hasBranchQualifier() {
        return this.b_branchQualifier;
    }
    
    public Buffer getBranchQualifier() {
        return this.f_branchQualifier;
    }
    
    public T setBranchQualifier(final Buffer branchQualifier) {
        this.loadAndClear();
        this.b_branchQualifier = true;
        this.f_branchQualifier = branchQualifier;
        return (T)this;
    }
    
    public void clearBranchQualifier() {
        this.loadAndClear();
        this.b_branchQualifier = false;
        this.f_branchQualifier = null;
    }
    
    public boolean hasGlobalTransactionId() {
        return this.b_globalTransactionId;
    }
    
    public Buffer getGlobalTransactionId() {
        return this.f_globalTransactionId;
    }
    
    public T setGlobalTransactionId(final Buffer globalTransactionId) {
        this.loadAndClear();
        this.b_globalTransactionId = true;
        this.f_globalTransactionId = globalTransactionId;
        return (T)this;
    }
    
    public void clearGlobalTransactionId() {
        this.loadAndClear();
        this.b_globalTransactionId = false;
        this.f_globalTransactionId = null;
    }
}
