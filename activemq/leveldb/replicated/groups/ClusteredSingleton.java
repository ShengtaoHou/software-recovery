// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.collection.immutable.List$;
import scala.collection.immutable.List;
import scala.Option;
import scala.MatchError;
import scala.None$;
import scala.runtime.BoxesRunTime;
import scala.Function0;
import scala.Function1;
import scala.collection.mutable.ListBuffer;
import scala.Some;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001]4A!\u0001\u0002\u0001\u001f\t\u00112\t\\;ti\u0016\u0014X\rZ*j]\u001edW\r^8o\u0015\t\u0019A!\u0001\u0004he>,\bo\u001d\u0006\u0003\u000b\u0019\t!B]3qY&\u001c\u0017\r^3e\u0015\t9\u0001\"A\u0004mKZ,G\u000e\u001a2\u000b\u0005%Q\u0011\u0001C1di&4X-\\9\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\u0002\u0001+\t\u0001rc\u0005\u0002\u0001#A\u0019!cE\u000b\u000e\u0003\tI!\u0001\u0006\u0002\u00033\rcWo\u001d;fe\u0016$7+\u001b8hY\u0016$xN\\,bi\u000eDWM\u001d\t\u0003-]a\u0001\u0001B\u0003\u0019\u0001\t\u0007\u0011DA\u0001U#\tQ\u0002\u0005\u0005\u0002\u001c=5\tADC\u0001\u001e\u0003\u0015\u00198-\u00197b\u0013\tyBDA\u0004O_RD\u0017N\\4\u0011\u0005I\t\u0013B\u0001\u0012\u0003\u0005%qu\u000eZ3Ti\u0006$X\rC\u0005%\u0001\t\u0005\t\u0015!\u0003&Y\u0005Q1\u000f^1uK\u000ec\u0017m]:\u0011\u0007\u0019JSC\u0004\u0002\u001cO%\u0011\u0001\u0006H\u0001\u0007!J,G-\u001a4\n\u0005)Z#!B\"mCN\u001c(B\u0001\u0015\u001d\u0013\t!3\u0003C\u0003/\u0001\u0011\u0005q&\u0001\u0004=S:LGO\u0010\u000b\u0003aE\u00022A\u0005\u0001\u0016\u0011\u0015!S\u00061\u0001&\u0011%\u0019\u0004\u00011AA\u0002\u0013%A'\u0001\u0003`K&$W#A\u001b\u0011\u0005YZT\"A\u001c\u000b\u0005aJ\u0014\u0001\u00027b]\u001eT\u0011AO\u0001\u0005U\u00064\u0018-\u0003\u0002=o\t11\u000b\u001e:j]\u001eD\u0011B\u0010\u0001A\u0002\u0003\u0007I\u0011B \u0002\u0011}+\u0017\u000eZ0%KF$\"\u0001Q\"\u0011\u0005m\t\u0015B\u0001\"\u001d\u0005\u0011)f.\u001b;\t\u000f\u0011k\u0014\u0011!a\u0001k\u0005\u0019\u0001\u0010J\u0019\t\r\u0019\u0003\u0001\u0015)\u00036\u0003\u0015yV-\u001b3!\u0011\u0015A\u0005\u0001\"\u00015\u0003\r)\u0017\u000e\u001a\u0005\n\u0015\u0002\u0001\r\u00111A\u0005\n-\u000baaX:uCR,W#A\u000b\t\u00135\u0003\u0001\u0019!a\u0001\n\u0013q\u0015AC0ti\u0006$Xm\u0018\u0013fcR\u0011\u0001i\u0014\u0005\b\t2\u000b\t\u00111\u0001\u0016\u0011\u0019\t\u0006\u0001)Q\u0005+\u00059ql\u001d;bi\u0016\u0004\u0003\"B*\u0001\t\u0003\"\u0016\u0001B:u_B,\u0012\u0001\u0011\u0005\u0006-\u0002!\taV\u0001\u0005U>Lg\u000e\u0006\u0002A1\")\u0011,\u0016a\u0001+\u0005)1\u000f^1uK\")1\f\u0001C\u0001)\u0006)A.Z1wK\")Q\f\u0001C))\u0006qqN\u001c#jg\u000e|gN\\3di\u0016$\u0007\"B0\u0001\t#\"\u0016aC8o\u0007>tg.Z2uK\u0012DQ!\u0019\u0001\u0005\u0002\t\f\u0001\"[:NCN$XM]\u000b\u0002GB\u00111\u0004Z\u0005\u0003Kr\u0011qAQ8pY\u0016\fg\u000eC\u0003h\u0001\u0011\u0005\u0001.\u0001\u0004nCN$XM]\u000b\u0002SB\u00191D[\u000b\n\u0005-d\"AB(qi&|g\u000eC\u0003n\u0001\u0011\u0005a.\u0001\u0004tY\u00064Xm]\u000b\u0002_B\u0019\u0001/^\u000b\u000e\u0003ET!A]:\u0002\u0013%lW.\u001e;bE2,'B\u0001;\u001d\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003mF\u0014A\u0001T5ti\u0002")
public class ClusteredSingleton<T extends NodeState> extends ClusteredSingletonWatcher<T>
{
    private String org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid;
    private T _state;
    
    public String org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid() {
        return this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid;
    }
    
    private void org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid_$eq(final String x$1) {
        this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid = x$1;
    }
    
    public String eid() {
        return this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid();
    }
    
    private T _state() {
        return this._state;
    }
    
    private void _state_$eq(final T x$1) {
        this._state = x$1;
    }
    
    @Override
    public synchronized void stop() {
        if (this._state() != null) {
            this.leave();
        }
        super.stop();
    }
    
    public synchronized void join(final T state) {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
        if (state.id() == null) {
            throw new IllegalArgumentException("The state id cannot be null");
        }
        if (this._group() == null) {
            throw new IllegalStateException("Not started.");
        }
        this._state_$eq(state);
        if (!this.connected()) {
            return;
        }
        if (this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid() == null) {
            this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid_$eq(this.group().join(ClusteredSupport$.MODULE$.encode(state, this.mapper())));
            return;
        }
        this._group().update(this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid(), ClusteredSupport$.MODULE$.encode(state, this.mapper()));
    }
    
    public synchronized void leave() {
        if (this._state() == null) {
            throw new IllegalStateException("Not joined");
        }
        if (this._group() == null) {
            throw new IllegalStateException("Not started.");
        }
        this._state_$eq(null);
        if (this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid() != null && this.connected()) {
            this._group().leave(this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid());
            this.org$apache$activemq$leveldb$replicated$groups$ClusteredSingleton$$_eid_$eq(null);
        }
    }
    
    @Override
    public void onDisconnected() {
    }
    
    @Override
    public void onConnected() {
        if (this._state() != null) {
            this.join(this._state());
        }
    }
    
    public synchronized boolean isMaster() {
        if (this._state() == null) {
            return false;
        }
        final Option value = this._members().get((Object)this._state().id());
        boolean unboxToBoolean;
        if (value instanceof Some) {
            final ListBuffer nodes = (ListBuffer)((Some)value).x();
            unboxToBoolean = BoxesRunTime.unboxToBoolean(nodes.headOption().map((Function1)new ClusteredSingleton$$anonfun$isMaster.ClusteredSingleton$$anonfun$isMaster$2(this)).getOrElse((Function0)new ClusteredSingleton$$anonfun$isMaster.ClusteredSingleton$$anonfun$isMaster$1(this)));
        }
        else {
            if (!None$.MODULE$.equals(value)) {
                throw new MatchError((Object)value);
            }
            unboxToBoolean = false;
        }
        return unboxToBoolean;
    }
    
    public synchronized Option<T> master() {
        if (this._state() == null) {
            throw new IllegalStateException("Not joined");
        }
        return (Option<T>)this._members().get((Object)this._state().id()).map((Function1)new ClusteredSingleton$$anonfun$master.ClusteredSingleton$$anonfun$master$1(this));
    }
    
    public synchronized List<T> slaves() {
        if (this._state() == null) {
            throw new IllegalStateException("Not joined");
        }
        final List rc = (List)this._members().get((Object)this._state().id()).map((Function1)new ClusteredSingleton$$anonfun.ClusteredSingleton$$anonfun$1(this)).getOrElse((Function0)new ClusteredSingleton$$anonfun.ClusteredSingleton$$anonfun$2(this));
        return (List<T>)rc.drop(1).map((Function1)new ClusteredSingleton$$anonfun$slaves.ClusteredSingleton$$anonfun$slaves$1(this), List$.MODULE$.canBuildFrom());
    }
    
    public ClusteredSingleton(final Class<T> stateClass) {
        super(stateClass);
    }
}
