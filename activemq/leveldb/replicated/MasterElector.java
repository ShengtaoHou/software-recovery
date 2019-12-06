// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.Option;
import scala.collection.generic.TraversableForwarder;
import scala.Tuple2;
import scala.Function2;
import scala.Function1;
import scala.collection.SeqLike;
import scala.collection.immutable.Map;
import scala.collection.mutable.ListBuffer;
import scala.MatchError;
import scala.Some;
import scala.None$;
import scala.Predef$;
import org.apache.activemq.leveldb.replicated.groups.ChangeListener;
import scala.runtime.BoxedUnit;
import org.slf4j.Logger;
import scala.collection.Seq;
import scala.Function0;
import scala.reflect.ScalaSignature;
import org.apache.activemq.leveldb.replicated.groups.ClusteredSingleton;

@ScalaSignature(bytes = "\u0006\u0001\u00055s!B\u0001\u0003\u0011\u0003i\u0011!D'bgR,'/\u00127fGR|'O\u0003\u0002\u0004\t\u0005Q!/\u001a9mS\u000e\fG/\u001a3\u000b\u0005\u00151\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000f!\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0011\u00059yQ\"\u0001\u0002\u0007\u000bA\u0011\u0001\u0012A\t\u0003\u001b5\u000b7\u000f^3s\u000b2,7\r^8s'\ry!\u0003\u0007\t\u0003'Yi\u0011\u0001\u0006\u0006\u0002+\u0005)1oY1mC&\u0011q\u0003\u0006\u0002\u0007\u0003:L(+\u001a4\u0011\u0005eaR\"\u0001\u000e\u000b\u0005m!\u0011\u0001B;uS2L!!\b\u000e\u0003\u00071{w\rC\u0003 \u001f\u0011\u0005\u0001%\u0001\u0004=S:LGO\u0010\u000b\u0002\u001b\u0019!\u0001C\u0001\u0001#'\t\t3\u0005E\u0002%O%j\u0011!\n\u0006\u0003M\t\taa\u001a:pkB\u001c\u0018B\u0001\u0015&\u0005I\u0019E.^:uKJ,GmU5oO2,Go\u001c8\u0011\u00059Q\u0013BA\u0016\u0003\u0005AaUM^3m\t\nsu\u000eZ3Ti\u0006$X\r\u0003\u0005.C\t\u0005\t\u0015!\u0003/\u0003\u0015\u0019Ho\u001c:f!\tqq&\u0003\u00021\u0005\t!R\t\\3di&tw\rT3wK2$%i\u0015;pe\u0016DQaH\u0011\u0005\u0002I\"\"a\r\u001b\u0011\u00059\t\u0003\"B\u00172\u0001\u0004q\u0003\"\u0003\u001c\"\u0001\u0004\u0005\r\u0011\"\u00018\u0003)a\u0017m\u001d;`gR\fG/Z\u000b\u0002S!I\u0011(\ta\u0001\u0002\u0004%\tAO\u0001\u000fY\u0006\u001cHoX:uCR,w\fJ3r)\tYd\b\u0005\u0002\u0014y%\u0011Q\b\u0006\u0002\u0005+:LG\u000fC\u0004@q\u0005\u0005\t\u0019A\u0015\u0002\u0007a$\u0013\u0007\u0003\u0004BC\u0001\u0006K!K\u0001\fY\u0006\u001cHoX:uCR,\u0007\u0005C\u0005DC\u0001\u0007\t\u0019!C\u0001\t\u00069Q\r\\3di\u0016$W#A#\u0011\u0005\u0019KeBA\nH\u0013\tAE#\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0015.\u0013aa\u0015;sS:<'B\u0001%\u0015\u0011%i\u0015\u00051AA\u0002\u0013\u0005a*A\u0006fY\u0016\u001cG/\u001a3`I\u0015\fHCA\u001eP\u0011\u001dyD*!AA\u0002\u0015Ca!U\u0011!B\u0013)\u0015\u0001C3mK\u000e$X\r\u001a\u0011\t\u000fM\u000b\u0003\u0019!C\u0001)\u0006A\u0001o\\:ji&|g.F\u0001V!\t\u0019b+\u0003\u0002X)\t!Aj\u001c8h\u0011\u001dI\u0016\u00051A\u0005\u0002i\u000bA\u0002]8tSRLwN\\0%KF$\"aO.\t\u000f}B\u0016\u0011!a\u0001+\"1Q,\tQ!\nU\u000b\u0011\u0002]8tSRLwN\u001c\u0011\t\u0013}\u000b\u0003\u0019!a\u0001\n\u0003!\u0015aB1eIJ,7o\u001d\u0005\nC\u0006\u0002\r\u00111A\u0005\u0002\t\f1\"\u00193ee\u0016\u001c8o\u0018\u0013fcR\u00111h\u0019\u0005\b\u007f\u0001\f\t\u00111\u0001F\u0011\u0019)\u0017\u0005)Q\u0005\u000b\u0006A\u0011\r\u001a3sKN\u001c\b\u0005C\u0004hC\u0001\u0007I\u0011\u00015\u0002\u001dU\u0004H-\u0019;j]\u001e|6\u000f^8sKV\t\u0011\u000e\u0005\u0002\u0014U&\u00111\u000e\u0006\u0002\b\u0005>|G.Z1o\u0011\u001di\u0017\u00051A\u0005\u00029\f!#\u001e9eCRLgnZ0ti>\u0014Xm\u0018\u0013fcR\u00111h\u001c\u0005\b\u007f1\f\t\u00111\u0001j\u0011\u0019\t\u0018\u0005)Q\u0005S\u0006yQ\u000f\u001d3bi&twmX:u_J,\u0007\u0005C\u0005tC\u0001\u0007\t\u0019!C\u0001\t\u0006aa.\u001a=u?\u000e|gN\\3di\"IQ/\ta\u0001\u0002\u0004%\tA^\u0001\u0011]\u0016DHoX2p]:,7\r^0%KF$\"aO<\t\u000f}\"\u0018\u0011!a\u0001\u000b\"1\u00110\tQ!\n\u0015\u000bQB\\3yi~\u001bwN\u001c8fGR\u0004\u0003\"C>\"\u0001\u0004\u0005\r\u0011\"\u0001E\u0003E\u0019wN\u001c8fGR,GmX1eIJ,7o\u001d\u0005\n{\u0006\u0002\r\u00111A\u0005\u0002y\fQcY8o]\u0016\u001cG/\u001a3`C\u0012$'/Z:t?\u0012*\u0017\u000f\u0006\u0002<\u007f\"9q\b`A\u0001\u0002\u0004)\u0005bBA\u0002C\u0001\u0006K!R\u0001\u0013G>tg.Z2uK\u0012|\u0016\r\u001a3sKN\u001c\b\u0005C\u0004\u0002\b\u0005\"\t!!\u0003\u0002\t)|\u0017N\\\u000b\u0002w!9\u0011QB\u0011\u0005\u0002\u0005=\u0011aB3mK\u000e$xN]\u000b\u0002g!9\u00111C\u0011\u0005\u0002\u0005%\u0011AB;qI\u0006$X\r\u0003\u0004\u0002\u0018\u0005\"\taN\u0001\rGJ,\u0017\r^3`gR\fG/Z\u0004\b\u00037\t\u0003\u0012AA\u000f\u0003=\u0019\u0007.\u00198hK~c\u0017n\u001d;f]\u0016\u0014\b\u0003BA\u0010\u0003Ci\u0011!\t\u0004\b\u0003G\t\u0003\u0012AA\u0013\u0005=\u0019\u0007.\u00198hK~c\u0017n\u001d;f]\u0016\u00148#BA\u0011%\u0005\u001d\u0002c\u0001\u0013\u0002*%\u0019\u00111F\u0013\u0003\u001d\rC\u0017M\\4f\u0019&\u001cH/\u001a8fe\"9q$!\t\u0005\u0002\u0005=BCAA\u000f\u0011!\t\u0019$!\t\u0005\u0002\u0005%\u0011!C2p]:,7\r^3e\u0011!\t9$!\t\u0005\u0002\u0005%\u0011\u0001\u00043jg\u000e|gN\\3di\u0016$\u0007\"CA\u001e\u0003C\u0001\r\u0011\"\u0001i\u0003\u001d\u0019Ho\u001c9qK\u0012D!\"a\u0010\u0002\"\u0001\u0007I\u0011AA!\u0003-\u0019Ho\u001c9qK\u0012|F%Z9\u0015\u0007m\n\u0019\u0005\u0003\u0005@\u0003{\t\t\u00111\u0001j\u0011!\t9%!\t!B\u0013I\u0017\u0001C:u_B\u0004X\r\u001a\u0011\t\u0011\u0005-\u0013\u0011\u0005C\u0001\u0003\u0013\tqa\u00195b]\u001e,G\r")
public class MasterElector extends ClusteredSingleton<LevelDBNodeState>
{
    public final ElectingLevelDBStore org$apache$activemq$leveldb$replicated$MasterElector$$store;
    private LevelDBNodeState last_state;
    private String elected;
    private long position;
    private String address;
    private boolean updating_store;
    private String next_connect;
    private String connected_address;
    private volatile change_listener$ change_listener$module;
    
    public static void trace(final Throwable e) {
        MasterElector$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        MasterElector$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        MasterElector$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        MasterElector$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        MasterElector$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        MasterElector$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        MasterElector$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static Logger log() {
        return MasterElector$.MODULE$.log();
    }
    
    private change_listener$ change_listener$lzycompute() {
        synchronized (this) {
            if (this.change_listener$module == null) {
                this.change_listener$module = new change_listener$();
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.change_listener$module;
        }
    }
    
    public LevelDBNodeState last_state() {
        return this.last_state;
    }
    
    public void last_state_$eq(final LevelDBNodeState x$1) {
        this.last_state = x$1;
    }
    
    public String elected() {
        return this.elected;
    }
    
    public void elected_$eq(final String x$1) {
        this.elected = x$1;
    }
    
    public long position() {
        return this.position;
    }
    
    public void position_$eq(final long x$1) {
        this.position = x$1;
    }
    
    public String address() {
        return this.address;
    }
    
    public void address_$eq(final String x$1) {
        this.address = x$1;
    }
    
    public boolean updating_store() {
        return this.updating_store;
    }
    
    public void updating_store_$eq(final boolean x$1) {
        this.updating_store = x$1;
    }
    
    public String next_connect() {
        return this.next_connect;
    }
    
    public void next_connect_$eq(final String x$1) {
        this.next_connect = x$1;
    }
    
    public String connected_address() {
        return this.connected_address;
    }
    
    public void connected_address_$eq(final String x$1) {
        this.connected_address = x$1;
    }
    
    public synchronized void join() {
        this.last_state_$eq(this.create_state());
        this.join(this.last_state());
        this.add(this.change_listener());
    }
    
    public MasterElector elector() {
        return this;
    }
    
    public void update() {
        synchronized (this.elector()) {
            final LevelDBNodeState create_state;
            final LevelDBNodeState next = create_state = this.create_state();
            final LevelDBNodeState last_state = this.last_state();
            Label_0056: {
                Label_0043: {
                    if (create_state == null) {
                        if (last_state != null) {
                            break Label_0043;
                        }
                    }
                    else if (!create_state.equals(last_state)) {
                        break Label_0043;
                    }
                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                    break Label_0056;
                }
                this.last_state_$eq(next);
                this.join(next);
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
        }
        // monitorexit(this.elector())
    }
    
    public LevelDBNodeState create_state() {
        final LevelDBNodeState rc = new LevelDBNodeState();
        rc.id_$eq(this.org$apache$activemq$leveldb$replicated$MasterElector$$store.brokerName());
        rc.elected_$eq(this.elected());
        rc.position_$eq(this.position());
        rc.weight_$eq(this.org$apache$activemq$leveldb$replicated$MasterElector$$store.weight());
        rc.address_$eq(this.address());
        rc.container_$eq(this.org$apache$activemq$leveldb$replicated$MasterElector$$store.container());
        rc.address_$eq(this.address());
        return rc;
    }
    
    public change_listener$ change_listener() {
        return (this.change_listener$module == null) ? this.change_listener$lzycompute() : this.change_listener$module;
    }
    
    public MasterElector(final ElectingLevelDBStore store) {
        this.org$apache$activemq$leveldb$replicated$MasterElector$$store = store;
        super(LevelDBNodeState.class);
        this.position = -1L;
        this.updating_store = false;
    }
    
    public class change_listener$ implements ChangeListener
    {
        private boolean stopped;
        
        @Override
        public void connected() {
            this.changed();
        }
        
        @Override
        public void disconnected() {
            this.changed();
        }
        
        public boolean stopped() {
            return this.stopped;
        }
        
        public void stopped_$eq(final boolean x$1) {
            this.stopped = x$1;
        }
        
        @Override
        public void changed() {
            synchronized (MasterElector.this.elector()) {
                MasterElector$.MODULE$.debug((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$5(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[] { MasterElector.this.members() }));
                if (MasterElector.this.isMaster()) {
                    final Option value = MasterElector.this.members().get((Object)MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.brokerName());
                    if (None$.MODULE$.equals(value)) {
                        MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$6(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                        final BoxedUnit unit = BoxedUnit.UNIT;
                    }
                    else {
                        if (!(value instanceof Some)) {
                            throw new MatchError((Object)value);
                        }
                        final ListBuffer members = (ListBuffer)((Some)value).x();
                        if (members.size() > MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.replicas()) {
                            MasterElector$.MODULE$.warn((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$7(this, members), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                        }
                        if (members.size() < MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.clusterSizeQuorum()) {
                            MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$8(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                            MasterElector.this.elected_$eq(null);
                            final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                        }
                        else {
                            if (MasterElector.this.elected() != null) {
                                final Map by_eid = (Map)Predef$.MODULE$.Map().apply((Seq)members);
                                if (by_eid.get((Object)MasterElector.this.elected()).isEmpty()) {
                                    MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$9(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                                    MasterElector.this.elected_$eq(null);
                                }
                            }
                            if (MasterElector.this.elected() == null) {
                                final ListBuffer sortedMembers = (ListBuffer)((SeqLike)members.filter((Function1)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$1(this))).sortWith((Function2)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$2(this));
                                if (sortedMembers.size() != members.size()) {
                                    MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$10(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                                }
                                else {
                                    MasterElector.this.elected_$eq((String)((Tuple2)sortedMembers.head())._1());
                                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                                }
                            }
                            else {
                                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                            }
                        }
                    }
                }
                else {
                    MasterElector.this.elected_$eq(null);
                }
                final String master_elected = (MasterElector.this.eid() == null) ? null : ((String)MasterElector.this.master().map((Function1)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$3(this)).getOrElse((Function0)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$4(this)));
                String s;
                if (master_elected == null) {
                    if (MasterElector.this.connected_address() == null && MasterElector.this.address() == null && !MasterElector.this.updating_store()) {
                        MasterElector.this.position_$eq(MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.position());
                    }
                    s = null;
                }
                else {
                    MasterElector.this.position_$eq(-1L);
                    s = (String)((TraversableForwarder)MasterElector.this.members().get((Object)MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.brokerName()).get()).find((Function1)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$5(this, master_elected)).map((Function1)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$6(this)).getOrElse((Function0)new MasterElector$change_listener$$anonfun.MasterElector$change_listener$$anonfun$7(this));
                }
                final String connect_target = s;
                Label_0749: {
                    if (MasterElector.this.eid() != null) {
                        final String s2 = master_elected;
                        final String eid = MasterElector.this.eid();
                        if (s2 == null) {
                            if (eid == null) {
                                break Label_0749;
                            }
                        }
                        else if (s2.equals(eid)) {
                            break Label_0749;
                        }
                    }
                    if (MasterElector.this.address() != null && !MasterElector.this.updating_store()) {
                        MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$11(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                        MasterElector.this.updating_store_$eq(true);
                        MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.stop_master((Function0<BoxedUnit>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$1(this));
                    }
                }
                Label_0861: {
                    if (MasterElector.this.eid() != null) {
                        final String s3 = master_elected;
                        final String eid2 = MasterElector.this.eid();
                        if (s3 == null) {
                            if (eid2 != null) {
                                break Label_0861;
                            }
                        }
                        else if (!s3.equals(eid2)) {
                            break Label_0861;
                        }
                        if (MasterElector.this.address() == null && !MasterElector.this.updating_store()) {
                            MasterElector$.MODULE$.info((Function0<String>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$12(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                            MasterElector.this.updating_store_$eq(true);
                            MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.start_master((Function1<Object, BoxedUnit>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$2(this));
                        }
                    }
                }
                Label_1031: {
                    if (MasterElector.this.eid() != null) {
                        final String s4 = master_elected;
                        final String eid3 = MasterElector.this.eid();
                        if (s4 == null) {
                            if (eid3 == null) {
                                break Label_1031;
                            }
                        }
                        else if (s4.equals(eid3)) {
                            break Label_1031;
                        }
                    }
                    if (MasterElector.this.address() == null) {
                        final String s5 = connect_target;
                        final String connected_address = MasterElector.this.connected_address();
                        if (s5 == null) {
                            if (connected_address == null) {
                                break Label_1031;
                            }
                        }
                        else if (s5.equals(connected_address)) {
                            break Label_1031;
                        }
                        if (connect_target != null && !MasterElector.this.updating_store()) {
                            MasterElector.this.updating_store_$eq(true);
                            MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.start_slave(connect_target, (Function0<BoxedUnit>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$3(this, connect_target));
                        }
                        if (connect_target == null && !MasterElector.this.updating_store()) {
                            MasterElector.this.updating_store_$eq(true);
                            MasterElector.this.org$apache$activemq$leveldb$replicated$MasterElector$$store.stop_slave((Function0<BoxedUnit>)new MasterElector$change_listener$$anonfun$changed.MasterElector$change_listener$$anonfun$changed$4(this));
                        }
                    }
                }
                if (MasterElector.this.group().zk().isConnected()) {
                    MasterElector.this.update();
                    final BoxedUnit boxedUnit2 = BoxedUnit.UNIT;
                }
                else {
                    final BoxedUnit boxedUnit2 = BoxedUnit.UNIT;
                }
            }
            // monitorexit(this.$outer.elector())
        }
        
        public change_listener$() {
            if (MasterElector.this == null) {
                throw null;
            }
            this.stopped = false;
        }
    }
}
