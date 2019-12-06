// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

public final class UowOpen$ implements UowState
{
    public static final UowOpen$ MODULE$;
    
    static {
        new UowOpen$();
    }
    
    @Override
    public int stage() {
        return 0;
    }
    
    @Override
    public String toString() {
        return "UowOpen";
    }
    
    private UowOpen$() {
        MODULE$ = this;
    }
}
