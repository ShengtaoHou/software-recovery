// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.Some;
import scala.runtime.BoxesRunTime;
import scala.None$;
import scala.Tuple4;
import scala.Option;
import scala.Serializable;
import scala.runtime.AbstractFunction4;

public final class SlaveStatus$ extends AbstractFunction4<String, String, Object, Object, SlaveStatus> implements Serializable
{
    public static final SlaveStatus$ MODULE$;
    
    static {
        new SlaveStatus$();
    }
    
    public final String toString() {
        return "SlaveStatus";
    }
    
    public SlaveStatus apply(final String nodeId, final String remoteAddress, final boolean attached, final long position) {
        return new SlaveStatus(nodeId, remoteAddress, attached, position);
    }
    
    public Option<Tuple4<String, String, Object, Object>> unapply(final SlaveStatus x$0) {
        return (Option<Tuple4<String, String, Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple4((Object)x$0.nodeId(), (Object)x$0.remoteAddress(), (Object)BoxesRunTime.boxToBoolean(x$0.attached()), (Object)BoxesRunTime.boxToLong(x$0.position()))));
    }
    
    private Object readResolve() {
        return SlaveStatus$.MODULE$;
    }
    
    private SlaveStatus$() {
        MODULE$ = this;
    }
}
