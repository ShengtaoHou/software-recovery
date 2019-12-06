// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

public class InsertableMutableBrokerFilter extends MutableBrokerFilter
{
    MutableBrokerFilter parent;
    
    public InsertableMutableBrokerFilter(final MutableBrokerFilter parent) {
        super(parent.getNext());
        (this.parent = parent).setNext(this);
    }
    
    public void remove() {
        this.parent.setNext(this.getNext());
    }
}
