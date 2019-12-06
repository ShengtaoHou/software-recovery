// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Some;
import scala.runtime.BoxesRunTime;
import scala.None$;
import scala.Tuple2;
import scala.Option;
import scala.Serializable;
import org.apache.activemq.command.ActiveMQDestination;
import scala.runtime.AbstractFunction2;

public final class QueueRecord$ extends AbstractFunction2<ActiveMQDestination, Object, QueueRecord> implements Serializable
{
    public static final QueueRecord$ MODULE$;
    
    static {
        new QueueRecord$();
    }
    
    public final String toString() {
        return "QueueRecord";
    }
    
    public QueueRecord apply(final ActiveMQDestination id, final long queue_key) {
        return new QueueRecord(id, queue_key);
    }
    
    public Option<Tuple2<ActiveMQDestination, Object>> unapply(final QueueRecord x$0) {
        return (Option<Tuple2<ActiveMQDestination, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2((Object)x$0.id(), (Object)BoxesRunTime.boxToLong(x$0.queue_key()))));
    }
    
    private Object readResolve() {
        return QueueRecord$.MODULE$;
    }
    
    private QueueRecord$() {
        MODULE$ = this;
    }
}
