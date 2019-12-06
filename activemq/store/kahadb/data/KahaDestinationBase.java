// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.data;

import org.apache.activemq.protobuf.BaseMessage;

abstract class KahaDestinationBase<T> extends BaseMessage<T>
{
    private KahaDestination.DestinationType f_type;
    private boolean b_type;
    private String f_name;
    private boolean b_name;
    
    KahaDestinationBase() {
        this.f_type = KahaDestination.DestinationType.QUEUE;
        this.f_name = null;
    }
    
    public boolean hasType() {
        return this.b_type;
    }
    
    public KahaDestination.DestinationType getType() {
        return this.f_type;
    }
    
    public T setType(final KahaDestination.DestinationType type) {
        this.loadAndClear();
        this.b_type = true;
        this.f_type = type;
        return (T)this;
    }
    
    public void clearType() {
        this.loadAndClear();
        this.b_type = false;
        this.f_type = KahaDestination.DestinationType.QUEUE;
    }
    
    public boolean hasName() {
        return this.b_name;
    }
    
    public String getName() {
        return this.f_name;
    }
    
    public T setName(final String name) {
        this.loadAndClear();
        this.b_name = true;
        this.f_name = name;
        return (T)this;
    }
    
    public void clearName() {
        this.loadAndClear();
        this.b_name = false;
        this.f_name = null;
    }
}
