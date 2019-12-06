// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaAckMessageFileMapCommandBase<T> extends BaseMessage<T>
{
    private Buffer f_ackMessageFileMap;
    private boolean b_ackMessageFileMap;
    
    KahaAckMessageFileMapCommandBase() {
        this.f_ackMessageFileMap = null;
    }
    
    public boolean hasAckMessageFileMap() {
        return this.b_ackMessageFileMap;
    }
    
    public Buffer getAckMessageFileMap() {
        return this.f_ackMessageFileMap;
    }
    
    public T setAckMessageFileMap(final Buffer ackMessageFileMap) {
        this.loadAndClear();
        this.b_ackMessageFileMap = true;
        this.f_ackMessageFileMap = ackMessageFileMap;
        return (T)this;
    }
    
    public void clearAckMessageFileMap() {
        this.loadAndClear();
        this.b_ackMessageFileMap = false;
        this.f_ackMessageFileMap = null;
    }
}
