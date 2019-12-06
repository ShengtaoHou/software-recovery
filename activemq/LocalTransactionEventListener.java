// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

public interface LocalTransactionEventListener
{
    void beginEvent();
    
    void commitEvent();
    
    void rollbackEvent();
}
