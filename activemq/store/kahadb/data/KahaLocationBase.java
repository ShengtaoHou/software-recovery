// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaLocationBase<T> extends BaseMessage<T>
{
    private int f_logId;
    private boolean b_logId;
    private int f_offset;
    private boolean b_offset;
    
    KahaLocationBase() {
        this.f_logId = 0;
        this.f_offset = 0;
    }
    
    public boolean hasLogId() {
        return this.b_logId;
    }
    
    public int getLogId() {
        return this.f_logId;
    }
    
    public T setLogId(final int logId) {
        this.loadAndClear();
        this.b_logId = true;
        this.f_logId = logId;
        return (T)this;
    }
    
    public void clearLogId() {
        this.loadAndClear();
        this.b_logId = false;
        this.f_logId = 0;
    }
    
    public boolean hasOffset() {
        return this.b_offset;
    }
    
    public int getOffset() {
        return this.f_offset;
    }
    
    public T setOffset(final int offset) {
        this.loadAndClear();
        this.b_offset = true;
        this.f_offset = offset;
        return (T)this;
    }
    
    public void clearOffset() {
        this.loadAndClear();
        this.b_offset = false;
        this.f_offset = 0;
    }
}
