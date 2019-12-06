// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowFlushing$ implements UowState
{
    public static final UowFlushing$ MODULE$;
    
    static {
        new UowFlushing$();
    }
    
    @Override
    public int stage() {
        return 4;
    }
    
    @Override
    public String toString() {
        return "UowFlushing";
    }
    
    private UowFlushing$() {
        MODULE$ = this;
    }
}
