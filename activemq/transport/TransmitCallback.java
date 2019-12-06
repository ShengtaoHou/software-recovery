// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

public interface TransmitCallback
{
    void onSuccess();
    
    void onFailure();
}
