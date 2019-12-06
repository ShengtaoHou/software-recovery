// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.Serializable;

public final class LongCounter$ implements Serializable
{
    public static final LongCounter$ MODULE$;
    
    static {
        new LongCounter$();
    }
    
    public long $lessinit$greater$default$1() {
        return 0L;
    }
    
    private Object readResolve() {
        return LongCounter$.MODULE$;
    }
    
    private LongCounter$() {
        MODULE$ = this;
    }
}
