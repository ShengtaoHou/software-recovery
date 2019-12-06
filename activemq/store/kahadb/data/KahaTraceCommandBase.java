// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaTraceCommandBase<T> extends BaseMessage<T>
{
    private String f_message;
    private boolean b_message;
    
    KahaTraceCommandBase() {
        this.f_message = null;
    }
    
    public boolean hasMessage() {
        return this.b_message;
    }
    
    public String getMessage() {
        return this.f_message;
    }
    
    public T setMessage(final String message) {
        this.loadAndClear();
        this.b_message = true;
        this.f_message = message;
        return (T)this;
    }
    
    public void clearMessage() {
        this.loadAndClear();
        this.b_message = false;
        this.f_message = null;
    }
}
