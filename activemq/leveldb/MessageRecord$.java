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
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.command.MessageId;
import scala.runtime.AbstractFunction4;

public final class MessageRecord$ extends AbstractFunction4<LevelDBStore, MessageId, Buffer, Object, MessageRecord> implements Serializable
{
    public static final MessageRecord$ MODULE$;
    
    static {
        new MessageRecord$();
    }
    
    public final String toString() {
        return "MessageRecord";
    }
    
    public MessageRecord apply(final LevelDBStore store, final MessageId id, final Buffer data, final boolean syncNeeded) {
        return new MessageRecord(store, id, data, syncNeeded);
    }
    
    public Option<Tuple4<LevelDBStore, MessageId, Buffer, Object>> unapply(final MessageRecord x$0) {
        return (Option<Tuple4<LevelDBStore, MessageId, Buffer, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple4((Object)x$0.store(), (Object)x$0.id(), (Object)x$0.data(), (Object)BoxesRunTime.boxToBoolean(x$0.syncNeeded()))));
    }
    
    private Object readResolve() {
        return MessageRecord$.MODULE$;
    }
    
    private MessageRecord$() {
        MODULE$ = this;
    }
}
