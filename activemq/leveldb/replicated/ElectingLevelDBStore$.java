// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.runtime.NonLocalReturnControl;
import java.net.InetAddress;
import scala.Function1;
import java.util.Enumeration;
import java.net.NetworkInterface;
import scala.collection.JavaConversions$;
import org.apache.activemq.leveldb.util.Log$class;
import scala.collection.Seq;
import scala.Function0;
import org.slf4j.Logger;
import org.apache.activemq.leveldb.util.Log;

public final class ElectingLevelDBStore$ implements Log
{
    public static final ElectingLevelDBStore$ MODULE$;
    private final Logger log;
    
    static {
        new ElectingLevelDBStore$();
    }
    
    @Override
    public Logger log() {
        return this.log;
    }
    
    @Override
    public void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        this.log = x$1;
    }
    
    @Override
    public void error(final Function0<String> m, final Seq<Object> args) {
        Log$class.error(this, m, args);
    }
    
    @Override
    public void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        Log$class.error(this, e, m, args);
    }
    
    @Override
    public void error(final Throwable e) {
        Log$class.error(this, e);
    }
    
    @Override
    public void warn(final Function0<String> m, final Seq<Object> args) {
        Log$class.warn(this, m, args);
    }
    
    @Override
    public void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        Log$class.warn(this, e, m, args);
    }
    
    @Override
    public void warn(final Throwable e) {
        Log$class.warn(this, e);
    }
    
    @Override
    public void info(final Function0<String> m, final Seq<Object> args) {
        Log$class.info(this, m, args);
    }
    
    @Override
    public void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        Log$class.info(this, e, m, args);
    }
    
    @Override
    public void info(final Throwable e) {
        Log$class.info(this, e);
    }
    
    @Override
    public void debug(final Function0<String> m, final Seq<Object> args) {
        Log$class.debug(this, m, args);
    }
    
    @Override
    public void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        Log$class.debug(this, e, m, args);
    }
    
    @Override
    public void debug(final Throwable e) {
        Log$class.debug(this, e);
    }
    
    @Override
    public void trace(final Function0<String> m, final Seq<Object> args) {
        Log$class.trace(this, m, args);
    }
    
    @Override
    public void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        Log$class.trace(this, e, m, args);
    }
    
    @Override
    public void trace(final Throwable e) {
        Log$class.trace(this, e);
    }
    
    public String machine_hostname() {
        final Object o = new Object();
        try {
            JavaConversions$.MODULE$.enumerationAsScalaIterator((Enumeration)NetworkInterface.getNetworkInterfaces()).withFilter((Function1)new ElectingLevelDBStore$$anonfun$machine_hostname.ElectingLevelDBStore$$anonfun$machine_hostname$1()).foreach((Function1)new ElectingLevelDBStore$$anonfun$machine_hostname.ElectingLevelDBStore$$anonfun$machine_hostname$2(o));
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (NonLocalReturnControl nonLocalReturnControl) {
            if (nonLocalReturnControl.key() == o) {
                return (String)nonLocalReturnControl.value();
            }
            throw nonLocalReturnControl;
        }
    }
    
    private ElectingLevelDBStore$() {
        Log$class.$init$(MODULE$ = this);
    }
}
