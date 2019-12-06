// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.Serializable;
import scala.runtime.AbstractFunction0;

public final class TimeMetric$ extends AbstractFunction0<TimeMetric> implements Serializable
{
    public static final TimeMetric$ MODULE$;
    
    static {
        new TimeMetric$();
    }
    
    public final String toString() {
        return "TimeMetric";
    }
    
    public TimeMetric apply() {
        return new TimeMetric();
    }
    
    public boolean unapply(final TimeMetric x$0) {
        return x$0 != null;
    }
    
    private Object readResolve() {
        return TimeMetric$.MODULE$;
    }
    
    private TimeMetric$() {
        MODULE$ = this;
    }
}
