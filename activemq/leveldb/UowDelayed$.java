// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowDelayed$ implements UowState
{
    public static final UowDelayed$ MODULE$;
    
    static {
        new UowDelayed$();
    }
    
    @Override
    public int stage() {
        return 2;
    }
    
    @Override
    public String toString() {
        return "UowDelayed";
    }
    
    private UowDelayed$() {
        MODULE$ = this;
    }
}
