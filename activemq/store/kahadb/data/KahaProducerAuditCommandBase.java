// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaProducerAuditCommandBase<T> extends BaseMessage<T>
{
    private Buffer f_audit;
    private boolean b_audit;
    
    KahaProducerAuditCommandBase() {
        this.f_audit = null;
    }
    
    public boolean hasAudit() {
        return this.b_audit;
    }
    
    public Buffer getAudit() {
        return this.f_audit;
    }
    
    public T setAudit(final Buffer audit) {
        this.loadAndClear();
        this.b_audit = true;
        this.f_audit = audit;
        return (T)this;
    }
    
    public void clearAudit() {
        this.loadAndClear();
        this.b_audit = false;
        this.f_audit = null;
    }
}
