// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import org.slf4j.Logger;
import scala.collection.immutable.StringOps;
import scala.Predef$;

public final class Log$
{
    public static final Log$ MODULE$;
    
    static {
        new Log$();
    }
    
    public Log apply(final Class<?> clazz) {
        return this.apply(new StringOps(Predef$.MODULE$.augmentString(clazz.getName())).stripSuffix("$"));
    }
    
    public Log apply(final String name) {
        return (Log)new Log$$anon.Log$$anon$1(name);
    }
    
    public Log apply(final Logger value) {
        return (Log)new Log$$anon.Log$$anon$2(value);
    }
    
    private Log$() {
        MODULE$ = this;
    }
}
