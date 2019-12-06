// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Serializable;
import scala.runtime.AbstractFunction0;

public final class QueueEntryRange$ extends AbstractFunction0<QueueEntryRange> implements Serializable
{
    public static final QueueEntryRange$ MODULE$;
    
    static {
        new QueueEntryRange$();
    }
    
    public final String toString() {
        return "QueueEntryRange";
    }
    
    public QueueEntryRange apply() {
        return new QueueEntryRange();
    }
    
    public boolean unapply(final QueueEntryRange x$0) {
        return x$0 != null;
    }
    
    private Object readResolve() {
        return QueueEntryRange$.MODULE$;
    }
    
    private QueueEntryRange$() {
        MODULE$ = this;
    }
}
