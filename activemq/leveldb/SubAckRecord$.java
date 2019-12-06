// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.runtime.BoxesRunTime;
import scala.Some;
import scala.Tuple2$mcJJ$sp;
import scala.None$;
import scala.Tuple2;
import scala.Option;
import scala.Serializable;
import scala.runtime.AbstractFunction2;

public final class SubAckRecord$ extends AbstractFunction2<Object, Object, SubAckRecord> implements Serializable
{
    public static final SubAckRecord$ MODULE$;
    
    static {
        new SubAckRecord$();
    }
    
    public final String toString() {
        return "SubAckRecord";
    }
    
    public SubAckRecord apply(final long subKey, final long ackPosition) {
        return new SubAckRecord(subKey, ackPosition);
    }
    
    public Option<Tuple2<Object, Object>> unapply(final SubAckRecord x$0) {
        return (Option<Tuple2<Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2$mcJJ$sp(x$0.subKey(), x$0.ackPosition())));
    }
    
    private Object readResolve() {
        return SubAckRecord$.MODULE$;
    }
    
    private SubAckRecord$() {
        MODULE$ = this;
    }
}
