// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowFlushed$ implements UowState
{
    public static final UowFlushed$ MODULE$;
    
    static {
        new UowFlushed$();
    }
    
    @Override
    public int stage() {
        return 5;
    }
    
    @Override
    public String toString() {
        return "UowFlushed";
    }
    
    private UowFlushed$() {
        MODULE$ = this;
    }
}
