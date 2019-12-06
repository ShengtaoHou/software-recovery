// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.LevelDBStore;
import org.apache.activemq.leveldb.replicated.dto.FileInfo;
import org.apache.activemq.leveldb.RecordLog;
import org.apache.activemq.leveldb.LevelDBClient$;
import java.io.File;
import scala.Predef$;
import org.apache.activemq.leveldb.replicated.dto.SyncResponse;
import java.util.Set;
import scala.Tuple2;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import java.util.Collection;
import scala.collection.JavaConversions$;
import java.util.HashSet;
import org.slf4j.Logger;
import scala.collection.Seq;
import scala.Function0;
import java.util.TreeSet;
import scala.reflect.ScalaSignature;
import org.apache.activemq.leveldb.LevelDBClient;

@ScalaSignature(bytes = "\u0006\u0001\u0005\u0005r!B\u0001\u0003\u0011\u0003i\u0011aE'bgR,'\u000fT3wK2$%i\u00117jK:$(BA\u0002\u0005\u0003)\u0011X\r\u001d7jG\u0006$X\r\u001a\u0006\u0003\u000b\u0019\tq\u0001\\3wK2$'M\u0003\u0002\b\u0011\u0005A\u0011m\u0019;jm\u0016l\u0017O\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001\u0001\t\u0003\u001d=i\u0011A\u0001\u0004\u0006!\tA\t!\u0005\u0002\u0014\u001b\u0006\u001cH/\u001a:MKZ,G\u000e\u0012\"DY&,g\u000e^\n\u0004\u001fIA\u0002CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"AB!osJ+g\r\u0005\u0002\u001a95\t!D\u0003\u0002\u001c\t\u0005!Q\u000f^5m\u0013\ti\"DA\u0002M_\u001eDQaH\b\u0005\u0002\u0001\na\u0001P5oSRtD#A\u0007\t\u000f\tz!\u0019!C\u0001G\u0005yQ*\u0011(J\r\u0016\u001bFkX*V\r\u001aK\u0005,F\u0001%!\t)#&D\u0001'\u0015\t9\u0003&\u0001\u0003mC:<'\"A\u0015\u0002\t)\fg/Y\u0005\u0003W\u0019\u0012aa\u0015;sS:<\u0007BB\u0017\u0010A\u0003%A%\u0001\tN\u0003:Ke)R*U?N+fIR%YA!9qf\u0004b\u0001\n\u0003\u0019\u0013A\u0003'P\u000f~\u001bVK\u0012$J1\"1\u0011g\u0004Q\u0001\n\u0011\n1\u0002T(H?N+fIR%YA!91g\u0004b\u0001\n\u0003\u0019\u0013\u0001D%O\t\u0016CvlU+G\r&C\u0006BB\u001b\u0010A\u0003%A%A\u0007J\u001d\u0012+\u0005lX*V\r\u001aK\u0005\f\t\u0004\u0005!\t\u0001qg\u0005\u00027qA\u0011\u0011HO\u0007\u0002\t%\u00111\b\u0002\u0002\u000e\u0019\u00164X\r\u001c#C\u00072LWM\u001c;\t\u0011u2$Q1A\u0005\u0002y\nQa\u001d;pe\u0016,\u0012a\u0010\t\u0003\u001d\u0001K!!\u0011\u0002\u0003%5\u000b7\u000f^3s\u0019\u00164X\r\u001c#C'R|'/\u001a\u0005\t\u0007Z\u0012\t\u0011)A\u0005\u007f\u000511\u000f^8sK\u0002BQa\b\u001c\u0005\u0002\u0015#\"AR$\u0011\u000591\u0004\"B\u001fE\u0001\u0004y\u0004bB%7\u0001\u0004%\tAS\u0001\u0019g:\f\u0007o\u001d5piN|\u0006/\u001a8eS:<w\fZ3mKR,W#A&\u0011\u00071s\u0005+D\u0001N\u0015\tY\u0002&\u0003\u0002P\u001b\n9AK]3f'\u0016$\bCA\nR\u0013\t\u0011FC\u0001\u0003M_:<\u0007b\u0002+7\u0001\u0004%\t!V\u0001\u001dg:\f\u0007o\u001d5piN|\u0006/\u001a8eS:<w\fZ3mKR,w\fJ3r)\t1\u0016\f\u0005\u0002\u0014/&\u0011\u0001\f\u0006\u0002\u0005+:LG\u000fC\u0004['\u0006\u0005\t\u0019A&\u0002\u0007a$\u0013\u0007\u0003\u0004]m\u0001\u0006KaS\u0001\u001ag:\f\u0007o\u001d5piN|\u0006/\u001a8eS:<w\fZ3mKR,\u0007\u0005C\u0003_m\u0011\u0005q,\u0001\u000btY\u00064Xm\u00185fY\u0012|6O\\1qg\"|Go]\u000b\u0002AB\u0019A*\u0019)\n\u0005\tl%a\u0002%bg\"\u001cV\r\u001e\u0005\u0006IZ\"\t%Z\u0001\u001fe\u0016\u0004H.Y2f\u0019\u0006$Xm\u001d;T]\u0006\u00048\u000f[8u\t&\u0014Xm\u0019;pef$\"A\u00164\t\u000b\u001d\u001c\u0007\u0019\u0001)\u0002'9,wo\u00158baNDw\u000e^%oI\u0016D\bk\\:\t\u000b%4D\u0011\t6\u0002\u0005\u001d\u001cGC\u0001,l\u0011\u0015a\u0007\u000e1\u0001n\u00039!x\u000e]5d!>\u001c\u0018\u000e^5p]N\u00042A\u001c<z\u001d\tyGO\u0004\u0002qg6\t\u0011O\u0003\u0002s\u0019\u00051AH]8pizJ\u0011!F\u0005\u0003kR\tq\u0001]1dW\u0006<W-\u0003\u0002xq\n\u00191+Z9\u000b\u0005U$\u0002\u0003B\n{!BK!a\u001f\u000b\u0003\rQ+\b\u000f\\33\u0011\u0015ih\u0007\"\u0011\u007f\u0003ayG\u000eZ3ti~\u0013X\r^1j]\u0016$wl\u001d8baNDw\u000e^\u000b\u0002!\"9\u0011\u0011\u0001\u001c\u0005\u0002\u0005\r\u0011AD:oCB\u001c\bn\u001c;`gR\fG/\u001a\u000b\u0005\u0003\u000b\t\t\u0002\u0005\u0003\u0002\b\u00055QBAA\u0005\u0015\r\tYAA\u0001\u0004IR|\u0017\u0002BA\b\u0003\u0013\u0011AbU=oGJ+7\u000f]8og\u0016Da!a\u0005\u0000\u0001\u0004\u0001\u0016aC:oCB\u001c\bn\u001c;`S\u0012Dq!a\u00067\t\u0003\nI\"A\u0005de\u0016\fG/\u001a'pOV\u0011\u00111\u0004\t\u0004s\u0005u\u0011bAA\u0010\t\tI!+Z2pe\u0012dun\u001a")
public class MasterLevelDBClient extends LevelDBClient
{
    private final MasterLevelDBStore store;
    private TreeSet<Object> snapshots_pending_delete;
    
    public static void trace(final Throwable e) {
        MasterLevelDBClient$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        MasterLevelDBClient$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        MasterLevelDBClient$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        MasterLevelDBClient$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        MasterLevelDBClient$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBClient$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        MasterLevelDBClient$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static String INDEX_SUFFIX() {
        return MasterLevelDBClient$.MODULE$.INDEX_SUFFIX();
    }
    
    public static String LOG_SUFFIX() {
        return MasterLevelDBClient$.MODULE$.LOG_SUFFIX();
    }
    
    public static String MANIFEST_SUFFIX() {
        return MasterLevelDBClient$.MODULE$.MANIFEST_SUFFIX();
    }
    
    public MasterLevelDBStore store() {
        return this.store;
    }
    
    public TreeSet<Object> snapshots_pending_delete() {
        return this.snapshots_pending_delete;
    }
    
    public void snapshots_pending_delete_$eq(final TreeSet<Object> x$1) {
        this.snapshots_pending_delete = x$1;
    }
    
    public HashSet<Object> slave_held_snapshots() {
        final HashSet rc = new HashSet();
        JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.store().slaves().values()).foreach((Function1)new MasterLevelDBClient$$anonfun$slave_held_snapshots.MasterLevelDBClient$$anonfun$slave_held_snapshots$1(this, rc));
        return (HashSet<Object>)rc;
    }
    
    @Override
    public void replaceLatestSnapshotDirectory(final long newSnapshotIndexPos) {
        if (this.slave_held_snapshots().contains(BoxesRunTime.boxToLong(this.lastIndexSnapshotPos()))) {
            this.snapshots_pending_delete().add(BoxesRunTime.boxToLong(newSnapshotIndexPos));
            this.lastIndexSnapshotPos_$eq(newSnapshotIndexPos);
        }
        else {
            super.replaceLatestSnapshotDirectory(newSnapshotIndexPos);
        }
    }
    
    @Override
    public void gc(final Seq<Tuple2<Object, Object>> topicPositions) {
        final HashSet snapshots_to_rm = new HashSet((Collection<? extends E>)this.snapshots_pending_delete());
        snapshots_to_rm.removeAll(this.slave_held_snapshots());
        JavaConversions$.MODULE$.asScalaSet((Set)snapshots_to_rm).foreach((Function1)new MasterLevelDBClient$$anonfun$gc.MasterLevelDBClient$$anonfun$gc$1(this));
        super.gc(topicPositions);
    }
    
    @Override
    public long oldest_retained_snapshot() {
        return this.snapshots_pending_delete().isEmpty() ? super.oldest_retained_snapshot() : BoxesRunTime.unboxToLong(this.snapshots_pending_delete().first());
    }
    
    public SyncResponse snapshot_state(final long snapshot_id) {
        final SyncResponse rc = new SyncResponse();
        rc.snapshot_position = snapshot_id;
        rc.wal_append_position = this.log().current_appender().append_position();
        Predef$.MODULE$.refArrayOps((Object[])this.logDirectory().listFiles()).withFilter((Function1)new MasterLevelDBClient$$anonfun$snapshot_state.MasterLevelDBClient$$anonfun$snapshot_state$1(this)).foreach((Function1)new MasterLevelDBClient$$anonfun$snapshot_state.MasterLevelDBClient$$anonfun$snapshot_state$2(this, rc));
        final File index_dir = LevelDBClient$.MODULE$.create_sequence_file(this.directory(), snapshot_id, MasterLevelDBClient$.MODULE$.INDEX_SUFFIX());
        if (index_dir.exists()) {
            Predef$.MODULE$.refArrayOps((Object[])index_dir.listFiles()).foreach((Function1)new MasterLevelDBClient$$anonfun$snapshot_state.MasterLevelDBClient$$anonfun$snapshot_state$3(this, rc));
        }
        return rc;
    }
    
    @Override
    public RecordLog createLog() {
        return (RecordLog)new MasterLevelDBClient$$anon.MasterLevelDBClient$$anon$2(this);
    }
    
    public final FileInfo org$apache$activemq$leveldb$replicated$MasterLevelDBClient$$info$1(final File file) {
        final FileInfo rc = new FileInfo();
        rc.file = file.getName();
        rc.length = file.length();
        return rc;
    }
    
    public MasterLevelDBClient(final MasterLevelDBStore store) {
        super(this.store = store);
        this.snapshots_pending_delete = new TreeSet<Object>();
    }
}
