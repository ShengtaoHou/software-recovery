// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowFlushQueued$ implements UowState
{
    public static final UowFlushQueued$ MODULE$;
    
    static {
        new UowFlushQueued$();
    }
    
    @Override
    public int stage() {
        return 3;
    }
    
    @Override
    public String toString() {
        return "UowFlushQueued";
    }
    
    private UowFlushQueued$() {
        MODULE$ = this;
    }
}
