// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import org.slf4j.LoggerFactory;
import scala.Function0;
import scala.Function1;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.Predef$;
import scala.collection.Seq;

public abstract class Log$class
{
    private static String format(final Log $this, final String message, final Seq args) {
        return args.isEmpty() ? message : new StringOps(Predef$.MODULE$.augmentString(message)).format((Seq)args.map((Function1)new Log$$anonfun$format.Log$$anonfun$format$1($this), Seq$.MODULE$.canBuildFrom()));
    }
    
    public static void error(final Log $this, final Function0 m, final Seq args) {
        if ($this.log().isErrorEnabled()) {
            $this.log().error(format($this, (String)m.apply(), args.toSeq()));
        }
    }
    
    public static void error(final Log $this, final Throwable e, final Function0 m, final Seq args) {
        if ($this.log().isErrorEnabled()) {
            $this.log().error(format($this, (String)m.apply(), args.toSeq()), e);
        }
    }
    
    public static void error(final Log $this, final Throwable e) {
        if ($this.log().isErrorEnabled()) {
            $this.log().error(e.getMessage(), e);
        }
    }
    
    public static void warn(final Log $this, final Function0 m, final Seq args) {
        if ($this.log().isWarnEnabled()) {
            $this.log().warn(format($this, (String)m.apply(), args.toSeq()));
        }
    }
    
    public static void warn(final Log $this, final Throwable e, final Function0 m, final Seq args) {
        if ($this.log().isWarnEnabled()) {
            $this.log().warn(format($this, (String)m.apply(), args.toSeq()), e);
        }
    }
    
    public static void warn(final Log $this, final Throwable e) {
        if ($this.log().isWarnEnabled()) {
            $this.log().warn(e.toString(), e);
        }
    }
    
    public static void info(final Log $this, final Function0 m, final Seq args) {
        if ($this.log().isInfoEnabled()) {
            $this.log().info(format($this, (String)m.apply(), args.toSeq()));
        }
    }
    
    public static void info(final Log $this, final Throwable e, final Function0 m, final Seq args) {
        if ($this.log().isInfoEnabled()) {
            $this.log().info(format($this, (String)m.apply(), args.toSeq()), e);
        }
    }
    
    public static void info(final Log $this, final Throwable e) {
        if ($this.log().isInfoEnabled()) {
            $this.log().info(e.toString(), e);
        }
    }
    
    public static void debug(final Log $this, final Function0 m, final Seq args) {
        if ($this.log().isDebugEnabled()) {
            $this.log().debug(format($this, (String)m.apply(), args.toSeq()));
        }
    }
    
    public static void debug(final Log $this, final Throwable e, final Function0 m, final Seq args) {
        if ($this.log().isDebugEnabled()) {
            $this.log().debug(format($this, (String)m.apply(), args.toSeq()), e);
        }
    }
    
    public static void debug(final Log $this, final Throwable e) {
        if ($this.log().isDebugEnabled()) {
            $this.log().debug(e.toString(), e);
        }
    }
    
    public static void trace(final Log $this, final Function0 m, final Seq args) {
        if ($this.log().isTraceEnabled()) {
            $this.log().trace(format($this, (String)m.apply(), args.toSeq()));
        }
    }
    
    public static void trace(final Log $this, final Throwable e, final Function0 m, final Seq args) {
        if ($this.log().isTraceEnabled()) {
            $this.log().trace(format($this, (String)m.apply(), args.toSeq()), e);
        }
    }
    
    public static void trace(final Log $this, final Throwable e) {
        if ($this.log().isTraceEnabled()) {
            $this.log().trace(e.toString(), e);
        }
    }
    
    public static void $init$(final Log $this) {
        $this.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(LoggerFactory.getLogger(new StringOps(Predef$.MODULE$.augmentString($this.getClass().getName())).stripSuffix("$")));
    }
}
