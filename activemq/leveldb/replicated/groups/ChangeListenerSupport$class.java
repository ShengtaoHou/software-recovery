// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.collection.immutable.Nil$;
import java.util.concurrent.TimeUnit;
import scala.Function0;
import scala.runtime.BoxedUnit;
import scala.Function1;
import scala.runtime.BoxesRunTime;
import scala.collection.immutable.List;

public abstract class ChangeListenerSupport$class
{
    public static void add(final ChangeListenerSupport $this, final ChangeListener listener) {
        synchronized ($this) {
            $this.listeners_$eq((List<ChangeListener>)$this.listeners().$colon$colon((Object)listener));
            final Boolean boxToBoolean = BoxesRunTime.boxToBoolean($this.connected());
            // monitorexit($this)
            final boolean connected = BoxesRunTime.unboxToBoolean((Object)boxToBoolean);
            if (connected) {
                listener.connected();
            }
        }
    }
    
    public static void remove(final ChangeListenerSupport $this, final ChangeListener listener) {
        synchronized ($this) {
            $this.listeners_$eq((List<ChangeListener>)$this.listeners().filterNot((Function1)new ChangeListenerSupport$$anonfun$remove.ChangeListenerSupport$$anonfun$remove$1($this, listener)));
            final BoxedUnit unit = BoxedUnit.UNIT;
        }
    }
    
    public static void fireConnected(final ChangeListenerSupport $this) {
        synchronized ($this) {
            final List<ChangeListener> listeners2 = $this.listeners();
            // monitorexit($this)
            final List listeners = listeners2;
            $this.check_elapsed_time((scala.Function0<Object>)new ChangeListenerSupport$$anonfun$fireConnected.ChangeListenerSupport$$anonfun$fireConnected$1($this, listeners));
        }
    }
    
    public static void fireDisconnected(final ChangeListenerSupport $this) {
        synchronized ($this) {
            final List<ChangeListener> listeners2 = $this.listeners();
            // monitorexit($this)
            final List listeners = listeners2;
            $this.check_elapsed_time((scala.Function0<Object>)new ChangeListenerSupport$$anonfun$fireDisconnected.ChangeListenerSupport$$anonfun$fireDisconnected$1($this, listeners));
        }
    }
    
    public static void fireChanged(final ChangeListenerSupport $this) {
        synchronized ($this) {
            final List<ChangeListener> listeners2 = $this.listeners();
            // monitorexit($this)
            final List listeners = listeners2;
            final long start = System.nanoTime();
            $this.check_elapsed_time((scala.Function0<Object>)new ChangeListenerSupport$$anonfun$fireChanged.ChangeListenerSupport$$anonfun$fireChanged$1($this, listeners));
        }
    }
    
    public static Object check_elapsed_time(final ChangeListenerSupport $this, final Function0 func) {
        final long start = System.nanoTime();
        try {
            return func.apply();
        }
        finally {
            final long end = System.nanoTime();
            final long elapsed = TimeUnit.NANOSECONDS.toMillis(end - start);
            if (elapsed > 100L) {
                ChangeListenerSupport$.MODULE$.LOG().warn("listeners are taking too long to process the events");
            }
        }
    }
    
    public static void $init$(final ChangeListenerSupport $this) {
        $this.listeners_$eq((List<ChangeListener>)Nil$.MODULE$);
    }
}
