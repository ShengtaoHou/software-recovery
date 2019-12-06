// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import org.linkedin.zookeeper.tracker.NodeEventsListener;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap$;
import org.linkedin.zookeeper.tracker.ZKDataReader;
import org.linkedin.zookeeper.client.IZKClient;
import org.linkedin.zookeeper.tracker.ZKByteArrayDataReader;
import org.apache.zookeeper.data.Stat;
import scala.runtime.BoxedUnit;
import scala.Function2;
import scala.collection.JavaConversions$;
import scala.Option;
import scala.MatchError;
import org.apache.zookeeper.KeeperException;
import scala.None$;
import scala.Some;
import scala.runtime.BoxesRunTime;
import scala.collection.immutable.StringOps;
import org.apache.zookeeper.CreateMode;
import scala.Predef$;
import scala.Function1;
import scala.collection.mutable.StringBuilder;
import scala.Function0;
import scala.runtime.TraitSetter;
import scala.collection.immutable.List;
import java.util.LinkedHashMap;
import scala.collection.mutable.HashMap;
import org.linkedin.zookeeper.tracker.ZooKeeperTreeTracker;
import scala.reflect.ScalaSignature;
import org.linkedin.zookeeper.client.LifecycleListener;

@ScalaSignature(bytes = "\u0006\u0001\u00055v!B\u0001\u0003\u0011\u0003y\u0011A\u0004.p_.+W\r]3s\u000fJ|W\u000f\u001d\u0006\u0003\u0007\u0011\taa\u001a:pkB\u001c(BA\u0003\u0007\u0003)\u0011X\r\u001d7jG\u0006$X\r\u001a\u0006\u0003\u000f!\tq\u0001\\3wK2$'M\u0003\u0002\n\u0015\u0005A\u0011m\u0019;jm\u0016l\u0017O\u0003\u0002\f\u0019\u00051\u0011\r]1dQ\u0016T\u0011!D\u0001\u0004_J<7\u0001\u0001\t\u0003!Ei\u0011A\u0001\u0004\u0006%\tA\ta\u0005\u0002\u000f5>|7*Z3qKJ<%o\\;q'\t\tB\u0003\u0005\u0002\u001615\taCC\u0001\u0018\u0003\u0015\u00198-\u00197b\u0013\tIbC\u0001\u0004B]f\u0014VM\u001a\u0005\u00067E!\t\u0001H\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003=AQAH\t\u0005\u0002}\tq!\\3nE\u0016\u00148\u000fF\u0002!u}\u0002B!\t\u0014)i5\t!E\u0003\u0002$I\u0005!Q\u000f^5m\u0015\u0005)\u0013\u0001\u00026bm\u0006L!a\n\u0012\u0003\u001b1Kgn[3e\u0011\u0006\u001c\b.T1q!\tI\u0013G\u0004\u0002+_9\u00111FL\u0007\u0002Y)\u0011QFD\u0001\u0007yI|w\u000e\u001e \n\u0003]I!\u0001\r\f\u0002\rA\u0013X\rZ3g\u0013\t\u00114G\u0001\u0004TiJLgn\u001a\u0006\u0003aY\u00012!F\u001b8\u0013\t1dCA\u0003BeJ\f\u0017\u0010\u0005\u0002\u0016q%\u0011\u0011H\u0006\u0002\u0005\u0005f$X\rC\u0003<;\u0001\u0007A(\u0001\u0002{WB\u0011\u0001#P\u0005\u0003}\t\u0011\u0001BW&DY&,g\u000e\u001e\u0005\u0006\u0001v\u0001\r\u0001K\u0001\u0005a\u0006$\bN\u0002\u0003\u0013\u0005\u0001\u00115\u0003B!D\u0013N\u0003\"\u0001R$\u000e\u0003\u0015S!A\u0012\u0013\u0002\t1\fgnZ\u0005\u0003\u0011\u0016\u0013aa\u00142kK\u000e$\bC\u0001&R\u001b\u0005Y%B\u0001'N\u0003\u0019\u0019G.[3oi*\u0011ajT\u0001\nu>|7.Z3qKJT!\u0001\u0015\u0007\u0002\u00111Lgn[3eS:L!AU&\u0003#1Kg-Z2zG2,G*[:uK:,'\u000f\u0005\u0002\u0011)&\u0011QK\u0001\u0002\u0016\u0007\"\fgnZ3MSN$XM\\3s'V\u0004\bo\u001c:u\u0011!Y\u0014I!b\u0001\n\u00039V#\u0001\u001f\t\u0011e\u000b%\u0011!Q\u0001\nq\n1A_6!\u0011!Y\u0016I!b\u0001\n\u0003a\u0016\u0001\u0002:p_R,\u0012\u0001\u000b\u0005\t=\u0006\u0013\t\u0011)A\u0005Q\u0005)!o\\8uA!)1$\u0011C\u0001AR\u0019\u0011MY2\u0011\u0005A\t\u0005\"B\u001e`\u0001\u0004a\u0004\"B.`\u0001\u0004A\u0003bB3B\u0005\u0004%\tAZ\u0001\u0005iJ,W-F\u0001h!\rA7\u000eN\u0007\u0002S*\u0011!.T\u0001\biJ\f7m[3s\u0013\ta\u0017N\u0001\u000b[_>\\U-\u001a9feR\u0013X-\u001a+sC\u000e\\WM\u001d\u0005\u0007]\u0006\u0003\u000b\u0011B4\u0002\u000bQ\u0014X-\u001a\u0011\t\u000fA\f%\u0019!C\u0001c\u0006)!n\\5ogV\t!\u000f\u0005\u0003tq\"RX\"\u0001;\u000b\u0005U4\u0018aB7vi\u0006\u0014G.\u001a\u0006\u0003oZ\t!bY8mY\u0016\u001cG/[8o\u0013\tIHOA\u0004ICNDW*\u00199\u0011\u0005UY\u0018B\u0001?\u0017\u0005\rIe\u000e\u001e\u0005\u0007}\u0006\u0003\u000b\u0011\u0002:\u0002\r)|\u0017N\\:!\u0011!q\u0012\t1A\u0005\u0002\u0005\u0005Q#\u0001\u0011\t\u0013\u0005\u0015\u0011\t1A\u0005\u0002\u0005\u001d\u0011aC7f[\n,'o]0%KF$B!!\u0003\u0002\u0010A\u0019Q#a\u0003\n\u0007\u00055aC\u0001\u0003V]&$\b\"CA\t\u0003\u0007\t\t\u00111\u0001!\u0003\rAH%\r\u0005\b\u0003+\t\u0005\u0015)\u0003!\u0003!iW-\u001c2feN\u0004\u0003bBA\r\u0003\u0012%\u00111D\u0001\u0013[\u0016l'-\u001a:`a\u0006$\bn\u00189sK\u001aL\u00070\u0006\u0002\u0002\u001eA\u0019A)a\b\n\u0005I*\u0005\"CA\u0012\u0003\u0002\u0007I\u0011AA\u0013\u0003\u0019\u0019Gn\\:fIV\u0011\u0011q\u0005\t\u0004+\u0005%\u0012bAA\u0016-\t9!i\\8mK\u0006t\u0007\"CA\u0018\u0003\u0002\u0007I\u0011AA\u0019\u0003)\u0019Gn\\:fI~#S-\u001d\u000b\u0005\u0003\u0013\t\u0019\u0004\u0003\u0006\u0002\u0012\u00055\u0012\u0011!a\u0001\u0003OA\u0001\"a\u000eBA\u0003&\u0011qE\u0001\bG2|7/\u001a3!Q\u0011\t)$a\u000f\u0011\u0007U\ti$C\u0002\u0002@Y\u0011\u0001B^8mCRLG.\u001a\u0005\b\u0003\u0007\nE\u0011AA#\u0003\u0015\u0019Gn\\:f+\t\tI\u0001C\u0004\u0002J\u0005#\t!!\n\u0002\u0013\r|gN\\3di\u0016$\u0007bBA'\u0003\u0012\u0005\u0011qJ\u0001\f_:\u001cuN\u001c8fGR,G\r\u0006\u0002\u0002\n!9\u00111K!\u0005\u0002\u0005=\u0013AD8o\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u0005\b\u0003/\nE\u0011AA-\u0003\u0011Qw.\u001b8\u0015\u0007!\nY\u0006C\u0005\u0002^\u0005U\u0003\u0013!a\u0001i\u0005!A-\u0019;b\u0011\u001d\t\t'\u0011C\u0001\u0003G\na!\u001e9eCR,GCBA\u0005\u0003K\n9\u0007\u0003\u0004A\u0003?\u0002\r\u0001\u000b\u0005\n\u0003;\ny\u0006%AA\u0002QBq!a\u001bB\t\u0003\ti'A\u0003mK\u00064X\r\u0006\u0003\u0002\n\u0005=\u0004B\u0002!\u0002j\u0001\u0007\u0001\u0006C\u0004\u0002t\u0005#I!!\u0012\u0002'\u0019L'/Z0dYV\u001cH/\u001a:`G\"\fgnZ3\t\u000f\u0005]\u0014\t\"\u0003\u0002z\u000511M]3bi\u0016$b!!\u0003\u0002|\u0005u\u0004B\u0002!\u0002v\u0001\u0007\u0001\u0006\u0003\u0006\u0002\u0000\u0005U\u0004\u0013!a\u0001\u0003\u0003\u000bQaY8v]R\u00042\u0001RAB\u0013\r\t))\u0012\u0002\b\u0013:$XmZ3s\u0011%\tI)QI\u0001\n\u0003\tY)\u0001\bk_&tG\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u00055%f\u0001\u001b\u0002\u0010.\u0012\u0011\u0011\u0013\t\u0005\u0003'\u000bi*\u0004\u0002\u0002\u0016*!\u0011qSAM\u0003%)hn\u00195fG.,GMC\u0002\u0002\u001cZ\t!\"\u00198o_R\fG/[8o\u0013\u0011\ty*!&\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0005\u0002$\u0006\u000b\n\u0011\"\u0001\u0002\f\u0006\u0001R\u000f\u001d3bi\u0016$C-\u001a4bk2$HE\r\u0005\n\u0003O\u000b\u0015\u0013!C\u0005\u0003S\u000b\u0001c\u0019:fCR,G\u0005Z3gCVdG\u000f\n\u001a\u0016\u0005\u0005-&\u0006BAA\u0003\u001f\u0003")
public class ZooKeeperGroup implements LifecycleListener, ChangeListenerSupport
{
    private final ZKClient zk;
    private final String root;
    private final ZooKeeperTreeTracker<byte[]> tree;
    private final HashMap<String, Object> joins;
    private LinkedHashMap<String, byte[]> members;
    private volatile boolean closed;
    private List<ChangeListener> listeners;
    
    public List<ChangeListener> listeners() {
        return this.listeners;
    }
    
    @TraitSetter
    public void listeners_$eq(final List<ChangeListener> x$1) {
        this.listeners = x$1;
    }
    
    public void add(final ChangeListener listener) {
        ChangeListenerSupport$class.add(this, listener);
    }
    
    public void remove(final ChangeListener listener) {
        ChangeListenerSupport$class.remove(this, listener);
    }
    
    public void fireConnected() {
        ChangeListenerSupport$class.fireConnected(this);
    }
    
    public void fireDisconnected() {
        ChangeListenerSupport$class.fireDisconnected(this);
    }
    
    public void fireChanged() {
        ChangeListenerSupport$class.fireChanged(this);
    }
    
    public <T> T check_elapsed_time(final Function0<T> func) {
        return (T)ChangeListenerSupport$class.check_elapsed_time(this, func);
    }
    
    public ZKClient zk() {
        return this.zk;
    }
    
    public String root() {
        return this.root;
    }
    
    public ZooKeeperTreeTracker<byte[]> tree() {
        return this.tree;
    }
    
    public HashMap<String, Object> joins() {
        return this.joins;
    }
    
    public LinkedHashMap<String, byte[]> members() {
        return this.members;
    }
    
    public void members_$eq(final LinkedHashMap<String, byte[]> x$1) {
        this.members = x$1;
    }
    
    public String org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$member_path_prefix() {
        return new StringBuilder().append((Object)this.root()).append((Object)"/0").toString();
    }
    
    public boolean closed() {
        return this.closed;
    }
    
    public void closed_$eq(final boolean x$1) {
        this.closed = x$1;
    }
    
    public synchronized void close() {
        this.closed_$eq(true);
        this.joins().foreach((Function1)new ZooKeeperGroup$$anonfun$close.ZooKeeperGroup$$anonfun$close$1(this));
        this.joins().clear();
        this.tree().destroy();
        this.zk().removeListener((LifecycleListener)this);
    }
    
    public boolean connected() {
        return this.zk().isConnected();
    }
    
    public void onConnected() {
        this.fireConnected();
    }
    
    public void onDisconnected() {
        this.members_$eq(new LinkedHashMap<String, byte[]>());
        this.fireDisconnected();
    }
    
    public synchronized String join(final byte[] data) {
        final String id = new StringOps(Predef$.MODULE$.augmentString(this.zk().createWithParents(this.org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$member_path_prefix(), data, CreateMode.EPHEMERAL_SEQUENTIAL))).stripPrefix(this.org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$member_path_prefix());
        this.joins().put((Object)id, (Object)BoxesRunTime.boxToInteger(0));
        return id;
    }
    
    public byte[] join$default$1() {
        return null;
    }
    
    public void update(final String path, final byte[] data) {
        synchronized (this) {
            final Option value = this.joins().get((Object)path);
            if (value instanceof Some) {
                final int ver = BoxesRunTime.unboxToInt(((Some)value).x());
                this.liftedTree1$1(path, data, ver);
                // monitorexit(this)
                return;
            }
            if (None$.MODULE$.equals(value)) {
                throw new KeeperException.NoNodeException(new StringBuilder().append((Object)"Has not joined locally: ").append((Object)path).toString());
            }
            throw new MatchError((Object)value);
        }
    }
    
    public byte[] update$default$2() {
        return null;
    }
    
    public synchronized void leave(final String path) {
        this.joins().remove((Object)path).foreach((Function1)new ZooKeeperGroup$$anonfun$leave.ZooKeeperGroup$$anonfun$leave$1(this, path));
    }
    
    public void org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change() {
        synchronized (this) {
            final List t = (List)JavaConversions$.MODULE$.mapAsScalaMap(this.tree().getTree()).toList().filterNot((Function1)new ZooKeeperGroup$$anonfun.ZooKeeperGroup$$anonfun$1(this));
            this.members_$eq(new LinkedHashMap<String, byte[]>());
            ((List)t.sortWith((Function2)new ZooKeeperGroup$$anonfun$org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change.ZooKeeperGroup$$anonfun$org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change$1(this))).foreach((Function1)new ZooKeeperGroup$$anonfun$org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change.ZooKeeperGroup$$anonfun$org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change$2(this));
            final BoxedUnit unit = BoxedUnit.UNIT;
            // monitorexit(this)
            this.fireChanged();
        }
    }
    
    private void create(String path, Integer count) {
        Label_0088: {
            try {
                while (true) {
                    if (this.zk().exists(path, false) != null) {
                        return;
                    }
                    break Label_0088;
                    final String s = path;
                    count = Predef$.MODULE$.int2Integer(Predef$.MODULE$.Integer2int(count) + 1);
                    path = s;
                    continue;
                }
            }
            catch (KeeperException.SessionExpiredException ex) {}
        }
        this.zk().createOrSetWithParents(path, "", CreateMode.PERSISTENT);
        final BoxedUnit unit = BoxedUnit.UNIT;
    }
    
    private Integer create$default$2() {
        return Predef$.MODULE$.int2Integer(0);
    }
    
    private final Option liftedTree1$1(final String path$1, final byte[] data$1, final int ver$1) {
        try {
            final Stat stat = this.zk().setData(new StringBuilder().append((Object)this.org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$member_path_prefix()).append((Object)path$1).toString(), data$1, ver$1);
            return this.joins().put((Object)path$1, (Object)BoxesRunTime.boxToInteger(stat.getVersion()));
        }
        catch (KeeperException.NoNodeException ex) {
            this.joins().remove((Object)path$1);
            throw ex;
        }
    }
    
    public ZooKeeperGroup(final ZKClient zk, final String root) {
        this.zk = zk;
        this.root = root;
        ChangeListenerSupport$class.$init$(this);
        this.tree = (ZooKeeperTreeTracker<byte[]>)new ZooKeeperTreeTracker((IZKClient)zk, (ZKDataReader)new ZKByteArrayDataReader(), root, 1);
        this.joins = (HashMap<String, Object>)HashMap$.MODULE$.apply((Seq)Nil$.MODULE$);
        this.members = new LinkedHashMap<String, byte[]>();
        zk.registerListener((LifecycleListener)this);
        this.create(root, this.create$default$2());
        this.tree().track((NodeEventsListener)new ZooKeeperGroup$$anon.ZooKeeperGroup$$anon$1(this));
        this.org$apache$activemq$leveldb$replicated$groups$ZooKeeperGroup$$fire_cluster_change();
        this.closed = false;
    }
}
