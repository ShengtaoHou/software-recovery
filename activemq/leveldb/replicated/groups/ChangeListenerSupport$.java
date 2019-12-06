// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class ChangeListenerSupport$
{
    public static final ChangeListenerSupport$ MODULE$;
    private final Logger LOG;
    
    static {
        new ChangeListenerSupport$();
    }
    
    public Logger LOG() {
        return this.LOG;
    }
    
    private ChangeListenerSupport$() {
        MODULE$ = this;
        this.LOG = LoggerFactory.getLogger(ChangeListenerSupport.class);
    }
}
