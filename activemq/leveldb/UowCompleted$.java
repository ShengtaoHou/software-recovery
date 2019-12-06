// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowCompleted$ implements UowState
{
    public static final UowCompleted$ MODULE$;
    
    static {
        new UowCompleted$();
    }
    
    @Override
    public int stage() {
        return 6;
    }
    
    @Override
    public String toString() {
        return "UowCompleted";
    }
    
    private UowCompleted$() {
        MODULE$ = this;
    }
}
