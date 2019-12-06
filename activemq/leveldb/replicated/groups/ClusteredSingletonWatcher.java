// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.reflect.ClassTag;
import scala.Predef;
import scala.Array$;
import scala.reflect.ClassTag$;
import scala.Predef$;
import scala.collection.TraversableOnce;
import scala.Function1;
import java.util.Map;
import scala.collection.JavaConversions$;
import scala.runtime.BoxedUnit;
import java.util.LinkedHashMap;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap$;
import org.codehaus.jackson.map.ObjectMapper;
import scala.Function0;
import scala.collection.immutable.List;
import scala.Tuple2;
import scala.collection.mutable.ListBuffer;
import scala.collection.mutable.HashMap;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005=c\u0001B\u0001\u0003\u0001=\u0011\u0011d\u00117vgR,'/\u001a3TS:<G.\u001a;p]^\u000bGo\u00195fe*\u00111\u0001B\u0001\u0007OJ|W\u000f]:\u000b\u0005\u00151\u0011A\u0003:fa2L7-\u0019;fI*\u0011q\u0001C\u0001\bY\u00164X\r\u001c3c\u0015\tI!\"\u0001\u0005bGRLg/Z7r\u0015\tYA\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u001b\u0005\u0019qN]4\u0004\u0001U\u0011\u0001cJ\n\u0004\u0001E9\u0002C\u0001\n\u0016\u001b\u0005\u0019\"\"\u0001\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Y\u0019\"AB!osJ+g\r\u0005\u0002\u001935\t!!\u0003\u0002\u001b\u0005\t)2\t[1oO\u0016d\u0015n\u001d;f]\u0016\u00148+\u001e9q_J$\b\u0002\u0003\u000f\u0001\u0005\u000b\u0007I\u0011A\u000f\u0002\u0015M$\u0018\r^3DY\u0006\u001c8/F\u0001\u001f!\ry\"%\n\b\u0003%\u0001J!!I\n\u0002\rA\u0013X\rZ3g\u0013\t\u0019CEA\u0003DY\u0006\u001c8O\u0003\u0002\"'A\u0011ae\n\u0007\u0001\t\u0015A\u0003A1\u0001*\u0005\u0005!\u0016C\u0001\u0016.!\t\u00112&\u0003\u0002-'\t9aj\u001c;iS:<\u0007C\u0001\r/\u0013\ty#AA\u0005O_\u0012,7\u000b^1uK\"A\u0011\u0007\u0001B\u0001B\u0003%a$A\u0006ti\u0006$Xm\u00117bgN\u0004\u0003\"B\u001a\u0001\t\u0003!\u0014A\u0002\u001fj]&$h\b\u0006\u00026mA\u0019\u0001\u0004A\u0013\t\u000bq\u0011\u0004\u0019\u0001\u0010\t\u0013a\u0002\u0001\u0019!a\u0001\n#I\u0014AB0he>,\b/F\u0001;!\tA2(\u0003\u0002=\u0005\tq!l\\8LK\u0016\u0004XM]$s_V\u0004\b\"\u0003 \u0001\u0001\u0004\u0005\r\u0011\"\u0005@\u0003)yvM]8va~#S-\u001d\u000b\u0003\u0001\u000e\u0003\"AE!\n\u0005\t\u001b\"\u0001B+oSRDq\u0001R\u001f\u0002\u0002\u0003\u0007!(A\u0002yIEBaA\u0012\u0001!B\u0013Q\u0014aB0he>,\b\u000f\t\u0005\u0006\u0011\u0002!\t!O\u0001\u0006OJ|W\u000f\u001d\u0005\u0006\u0015\u0002!\taS\u0001\u0007[\u0006\u0004\b/\u001a:\u0016\u00031\u0003\"!\u0014+\u000e\u00039S!a\u0014)\u0002\u00075\f\u0007O\u0003\u0002R%\u00069!.Y2lg>t'BA*\r\u0003!\u0019w\u000eZ3iCV\u001c\u0018BA+O\u00051y%M[3di6\u000b\u0007\u000f]3s\u0011\u001d9\u0006A1A\u0005\na\u000b\u0001\u0002\\5ti\u0016tWM]\u000b\u00023J\u0019!,\u00050\u0007\tmc\u0006!\u0017\u0002\ryI,g-\u001b8f[\u0016tGO\u0010\u0005\u0007;\u0002\u0001\u000b\u0011B-\u0002\u00131L7\u000f^3oKJ\u0004\u0003C\u0001\r`\u0013\t\u0001'A\u0001\bDQ\u0006tw-\u001a'jgR,g.\u001a:\t\u000b\tTF\u0011A2\u0002\u000f\rD\u0017M\\4fIR\t\u0001\tC\u0003f\u0001\u0011Ea-A\u0006p]\u000e{gN\\3di\u0016$W#\u0001!\t\u000b!\u0004A\u0011\u00034\u0002\u001d=tG)[:d_:tWm\u0019;fI\")!\u000e\u0001C\u0001W\u0006)1\u000f^1siR\u0011\u0001\t\u001c\u0005\u0006\u0011&\u0004\rA\u000f\u0005\u0006]\u0002!\tAZ\u0001\u0005gR|\u0007\u000fC\u0003q\u0001\u0011\u0005\u0011/A\u0005d_:tWm\u0019;fIV\t!\u000f\u0005\u0002\u0013g&\u0011Ao\u0005\u0002\b\u0005>|G.Z1o\u0011\u001d1\b\u00011A\u0005\u0012]\f\u0001bX7f[\n,'o]\u000b\u0002qB1\u0011P`A\u0001\u0003#i\u0011A\u001f\u0006\u0003wr\fq!\\;uC\ndWM\u0003\u0002~'\u0005Q1m\u001c7mK\u000e$\u0018n\u001c8\n\u0005}T(a\u0002%bg\"l\u0015\r\u001d\t\u0005\u0003\u0007\ti!\u0004\u0002\u0002\u0006)!\u0011qAA\u0005\u0003\u0011a\u0017M\\4\u000b\u0005\u0005-\u0011\u0001\u00026bm\u0006LA!a\u0004\u0002\u0006\t11\u000b\u001e:j]\u001e\u0004R!_A\n\u0003/I1!!\u0006{\u0005)a\u0015n\u001d;Ck\u001a4WM\u001d\t\u0007%\u0005e\u0011\u0011A\u0013\n\u0007\u0005m1C\u0001\u0004UkBdWM\r\u0005\n\u0003?\u0001\u0001\u0019!C\t\u0003C\tAbX7f[\n,'o]0%KF$2\u0001QA\u0012\u0011!!\u0015QDA\u0001\u0002\u0004A\bbBA\u0014\u0001\u0001\u0006K\u0001_\u0001\n?6,WNY3sg\u0002Ba!a\u000b\u0001\t\u00039\u0018aB7f[\n,'o\u001d\u0005\b\u0003_\u0001A\u0011AA\u0019\u0003=\u0019\u0007.\u00198hK\u0012|F-Z2pI\u0016$Gc\u0001!\u00024!A\u0011QGA\u0017\u0001\u0004\t9$A\u0001n!\u001d\tI$a\u0010\u0002\u0002\u0015j!!a\u000f\u000b\t\u0005u\u0012\u0011B\u0001\u0005kRLG.\u0003\u0003\u0002B\u0005m\"!\u0004'j].,G\rS1tQ6\u000b\u0007\u000fC\u0004\u0002F\u0001!\t!a\u0012\u0002\u000f5\f7\u000f^3sgV\u0011\u0011\u0011\n\t\u0005%\u0005-S%C\u0002\u0002NM\u0011Q!\u0011:sCf\u0004")
public class ClusteredSingletonWatcher<T extends NodeState> implements ChangeListenerSupport
{
    private final Class<T> stateClass;
    private ZooKeeperGroup _group;
    private final ChangeListener listener;
    private HashMap<String, ListBuffer<Tuple2<String, T>>> _members;
    private List<ChangeListener> listeners;
    
    @Override
    public List<ChangeListener> listeners() {
        return this.listeners;
    }
    
    @Override
    public void listeners_$eq(final List<ChangeListener> x$1) {
        this.listeners = x$1;
    }
    
    @Override
    public void add(final ChangeListener listener) {
        ChangeListenerSupport$class.add(this, listener);
    }
    
    @Override
    public void remove(final ChangeListener listener) {
        ChangeListenerSupport$class.remove(this, listener);
    }
    
    @Override
    public void fireConnected() {
        ChangeListenerSupport$class.fireConnected(this);
    }
    
    @Override
    public void fireDisconnected() {
        ChangeListenerSupport$class.fireDisconnected(this);
    }
    
    @Override
    public void fireChanged() {
        ChangeListenerSupport$class.fireChanged(this);
    }
    
    @Override
    public <T> T check_elapsed_time(final Function0<T> func) {
        return (T)ChangeListenerSupport$class.check_elapsed_time(this, func);
    }
    
    public Class<T> stateClass() {
        return this.stateClass;
    }
    
    public ZooKeeperGroup _group() {
        return this._group;
    }
    
    public void _group_$eq(final ZooKeeperGroup x$1) {
        this._group = x$1;
    }
    
    public ZooKeeperGroup group() {
        return this._group();
    }
    
    public ObjectMapper mapper() {
        return ClusteredSupport$.MODULE$.DEFAULT_MAPPER();
    }
    
    private ChangeListener listener() {
        return this.listener;
    }
    
    public void onConnected() {
    }
    
    public void onDisconnected() {
    }
    
    public synchronized void start(final ZooKeeperGroup group) {
        if (this._group() == null) {
            this._group_$eq(group);
            this._group().add(this.listener());
            return;
        }
        throw new IllegalStateException("Already started.");
    }
    
    public synchronized void stop() {
        if (this._group() == null) {
            throw new IllegalStateException("Not started.");
        }
        this._group().remove(this.listener());
        this._members_$eq((HashMap<String, ListBuffer<Tuple2<String, T>>>)HashMap$.MODULE$.apply((Seq)Nil$.MODULE$));
        this._group_$eq(null);
    }
    
    @Override
    public synchronized boolean connected() {
        return this._group() != null && this._group().connected();
    }
    
    public HashMap<String, ListBuffer<Tuple2<String, T>>> _members() {
        return this._members;
    }
    
    public void _members_$eq(final HashMap<String, ListBuffer<Tuple2<String, T>>> x$1) {
        this._members = x$1;
    }
    
    public synchronized HashMap<String, ListBuffer<Tuple2<String, T>>> members() {
        return this._members();
    }
    
    public void changed_decoded(final LinkedHashMap<String, T> m) {
        synchronized (this) {
            if (this._group() == null) {
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
            else {
                this._members_$eq((HashMap<String, ListBuffer<Tuple2<String, T>>>)HashMap$.MODULE$.apply((Seq)Nil$.MODULE$));
                JavaConversions$.MODULE$.mapAsScalaMap((Map)m).foreach((Function1)new ClusteredSingletonWatcher$$anonfun$changed_decoded.ClusteredSingletonWatcher$$anonfun$changed_decoded$1(this));
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
            // monitorexit(this)
            this.fireChanged();
        }
    }
    
    public synchronized T[] masters() {
        return (T[])((TraversableOnce)Predef$.MODULE$.refArrayOps((Object[])this._members().mapValues((Function1)new ClusteredSingletonWatcher$$anonfun$masters.ClusteredSingletonWatcher$$anonfun$masters$1(this)).toArray(ClassTag$.MODULE$.apply((Class)Tuple2.class))).map((Function1)new ClusteredSingletonWatcher$$anonfun$masters.ClusteredSingletonWatcher$$anonfun$masters$2(this), Array$.MODULE$.fallbackCanBuildFrom(Predef.DummyImplicit$.MODULE$.dummyImplicit()))).toArray((ClassTag)new ClusteredSingletonWatcher$$anon.ClusteredSingletonWatcher$$anon$1(this));
    }
    
    public ClusteredSingletonWatcher(final Class<T> stateClass) {
        this.stateClass = stateClass;
        ChangeListenerSupport$class.$init$(this);
        this.listener = new ChangeListener() {
            @Override
            public void changed() {
                final LinkedHashMap members = ClusteredSingletonWatcher.this._group().members();
                final LinkedHashMap t = new LinkedHashMap();
                JavaConversions$.MODULE$.mapAsScalaMap((Map)members).foreach((Function1)new ClusteredSingletonWatcher$$anon$2$$anonfun$changed.ClusteredSingletonWatcher$$anon$2$$anonfun$changed$1(this, t));
                ClusteredSingletonWatcher.this.changed_decoded(t);
            }
            
            @Override
            public void connected() {
                ClusteredSingletonWatcher.this.onConnected();
                this.changed();
                ClusteredSingletonWatcher.this.fireConnected();
            }
            
            @Override
            public void disconnected() {
                ClusteredSingletonWatcher.this.onDisconnected();
                this.changed();
                ClusteredSingletonWatcher.this.fireDisconnected();
            }
        };
        this._members = (HashMap<String, ListBuffer<Tuple2<String, T>>>)HashMap$.MODULE$.apply((Seq)Nil$.MODULE$);
    }
}
