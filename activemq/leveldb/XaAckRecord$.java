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
import org.apache.activemq.command.MessageAck;
import scala.runtime.AbstractFunction4;

public final class XaAckRecord$ extends AbstractFunction4<Object, Object, MessageAck, Object, XaAckRecord> implements Serializable
{
    public static final XaAckRecord$ MODULE$;
    
    static {
        new XaAckRecord$();
    }
    
    public final String toString() {
        return "XaAckRecord";
    }
    
    public XaAckRecord apply(final long container, final long seq, final MessageAck ack, final long sub) {
        return new XaAckRecord(container, seq, ack, sub);
    }
    
    public Option<Tuple4<Object, Object, MessageAck, Object>> unapply(final XaAckRecord x$0) {
        return (Option<Tuple4<Object, Object, MessageAck, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple4((Object)BoxesRunTime.boxToLong(x$0.container()), (Object)BoxesRunTime.boxToLong(x$0.seq()), (Object)x$0.ack(), (Object)BoxesRunTime.boxToLong(x$0.sub()))));
    }
    
    public long $lessinit$greater$default$4() {
        return -1L;
    }
    
    public long apply$default$4() {
        return -1L;
    }
    
    private Object readResolve() {
        return XaAckRecord$.MODULE$;
    }
    
    private XaAckRecord$() {
        MODULE$ = this;
    }
}
