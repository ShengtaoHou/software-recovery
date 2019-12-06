// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.util;

public interface Suspendable
{
    void suspend() throws Exception;
    
    void resume() throws Exception;
}
