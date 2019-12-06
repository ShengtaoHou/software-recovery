// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaUpdateMessageCommandBase<T> extends BaseMessage<T>
{
    private KahaAddMessageCommand f_message;
    
    KahaUpdateMessageCommandBase() {
        this.f_message = null;
    }
    
    public boolean hasMessage() {
        return this.f_message != null;
    }
    
    public KahaAddMessageCommand getMessage() {
        if (this.f_message == null) {
            this.f_message = new KahaAddMessageCommand();
        }
        return this.f_message;
    }
    
    public T setMessage(final KahaAddMessageCommand message) {
        this.loadAndClear();
        this.f_message = message;
        return (T)this;
    }
    
    public void clearMessage() {
        this.loadAndClear();
        this.f_message = null;
    }
}
