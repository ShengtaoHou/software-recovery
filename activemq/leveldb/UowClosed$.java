// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowClosed$ implements UowState
{
    public static final UowClosed$ MODULE$;
    
    static {
        new UowClosed$();
    }
    
    @Override
    public int stage() {
        return 1;
    }
    
    @Override
    public String toString() {
        return "UowClosed";
    }
    
    private UowClosed$() {
        MODULE$ = this;
    }
}
