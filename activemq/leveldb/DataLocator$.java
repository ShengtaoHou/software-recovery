// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Some;
import scala.runtime.BoxesRunTime;
import scala.None$;
import scala.Tuple3;
import scala.Option;
import scala.Serializable;
import scala.runtime.AbstractFunction3;

public final class DataLocator$ extends AbstractFunction3<LevelDBStore, Object, Object, DataLocator> implements Serializable
{
    public static final DataLocator$ MODULE$;
    
    static {
        new DataLocator$();
    }
    
    public final String toString() {
        return "DataLocator";
    }
    
    public DataLocator apply(final LevelDBStore store, final long pos, final int len) {
        return new DataLocator(store, pos, len);
    }
    
    public Option<Tuple3<LevelDBStore, Object, Object>> unapply(final DataLocator x$0) {
        return (Option<Tuple3<LevelDBStore, Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple3((Object)x$0.store(), (Object)BoxesRunTime.boxToLong(x$0.pos()), (Object)BoxesRunTime.boxToInteger(x$0.len()))));
    }
    
    private Object readResolve() {
        return DataLocator$.MODULE$;
    }
    
    private DataLocator$() {
        MODULE$ = this;
    }
}
