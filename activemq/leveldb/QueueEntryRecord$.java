// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Some;
import scala.runtime.BoxesRunTime;
import scala.None$;
import scala.Tuple4;
import scala.Option;
import scala.Serializable;
import org.apache.activemq.command.MessageId;
import scala.runtime.AbstractFunction4;

public final class QueueEntryRecord$ extends AbstractFunction4<MessageId, Object, Object, Object, QueueEntryRecord> implements Serializable
{
    public static final QueueEntryRecord$ MODULE$;
    
    static {
        new QueueEntryRecord$();
    }
    
    public final String toString() {
        return "QueueEntryRecord";
    }
    
    public QueueEntryRecord apply(final MessageId id, final long queueKey, final long queueSeq, final int deliveries) {
        return new QueueEntryRecord(id, queueKey, queueSeq, deliveries);
    }
    
    public Option<Tuple4<MessageId, Object, Object, Object>> unapply(final QueueEntryRecord x$0) {
        return (Option<Tuple4<MessageId, Object, Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple4((Object)x$0.id(), (Object)BoxesRunTime.boxToLong(x$0.queueKey()), (Object)BoxesRunTime.boxToLong(x$0.queueSeq()), (Object)BoxesRunTime.boxToInteger(x$0.deliveries()))));
    }
    
    public int $lessinit$greater$default$4() {
        return 0;
    }
    
    public int apply$default$4() {
        return 0;
    }
    
    private Object readResolve() {
        return QueueEntryRecord$.MODULE$;
    }
    
    private QueueEntryRecord$() {
        MODULE$ = this;
    }
}
