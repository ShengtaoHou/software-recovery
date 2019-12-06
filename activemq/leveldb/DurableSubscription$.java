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
import org.apache.activemq.command.SubscriptionInfo;
import scala.runtime.AbstractFunction3;

public final class DurableSubscription$ extends AbstractFunction3<Object, Object, SubscriptionInfo, DurableSubscription> implements Serializable
{
    public static final DurableSubscription$ MODULE$;
    
    static {
        new DurableSubscription$();
    }
    
    public final String toString() {
        return "DurableSubscription";
    }
    
    public DurableSubscription apply(final long subKey, final long topicKey, final SubscriptionInfo info) {
        return new DurableSubscription(subKey, topicKey, info);
    }
    
    public Option<Tuple3<Object, Object, SubscriptionInfo>> unapply(final DurableSubscription x$0) {
        return (Option<Tuple3<Object, Object, SubscriptionInfo>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple3((Object)BoxesRunTime.boxToLong(x$0.subKey()), (Object)BoxesRunTime.boxToLong(x$0.topicKey()), (Object)x$0.info())));
    }
    
    private Object readResolve() {
        return DurableSubscription$.MODULE$;
    }
    
    private DurableSubscription$() {
        MODULE$ = this;
    }
}
