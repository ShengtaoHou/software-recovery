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

public final class EntryLocator$ extends AbstractFunction2<Object, Object, EntryLocator> implements Serializable
{
    public static final EntryLocator$ MODULE$;
    
    static {
        new EntryLocator$();
    }
    
    public final String toString() {
        return "EntryLocator";
    }
    
    public EntryLocator apply(final long qid, final long seq) {
        return new EntryLocator(qid, seq);
    }
    
    public Option<Tuple2<Object, Object>> unapply(final EntryLocator x$0) {
        return (Option<Tuple2<Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2$mcJJ$sp(x$0.qid(), x$0.seq())));
    }
    
    private Object readResolve() {
        return EntryLocator$.MODULE$;
    }
    
    private EntryLocator$() {
        MODULE$ = this;
    }
}
