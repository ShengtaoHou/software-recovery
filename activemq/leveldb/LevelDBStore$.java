// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import java.util.concurrent.Future;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import org.apache.activemq.leveldb.util.Log$class;
import scala.collection.Seq;
import scala.Function0;
import scala.runtime.BoxedUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.apache.activemq.store.InlineListenableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.File;
import org.apache.activemq.leveldb.util.Log;

public final class LevelDBStore$ implements Log
{
    public static final LevelDBStore$ MODULE$;
    private final File DEFAULT_DIRECTORY;
    private ThreadPoolExecutor BLOCKING_EXECUTOR;
    private final InlineListenableFuture DONE;
    private final Logger log;
    private volatile boolean bitmap$0;
    
    static {
        new LevelDBStore$();
    }
    
    private ThreadPoolExecutor BLOCKING_EXECUTOR$lzycompute() {
        synchronized (this) {
            if (!this.bitmap$0) {
                this.BLOCKING_EXECUTOR = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
                    @Override
                    public Thread newThread(final Runnable r) {
                        final Thread rc = new Thread(null, r, "ActiveMQ Task");
                        rc.setDaemon(true);
                        return rc;
                    }
                });
                this.bitmap$0 = true;
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.BLOCKING_EXECUTOR;
        }
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
    
    public File DEFAULT_DIRECTORY() {
        return this.DEFAULT_DIRECTORY;
    }
    
    public ThreadPoolExecutor BLOCKING_EXECUTOR() {
        return this.bitmap$0 ? this.BLOCKING_EXECUTOR : this.BLOCKING_EXECUTOR$lzycompute();
    }
    
    public InlineListenableFuture DONE() {
        return this.DONE;
    }
    
    public IOException toIOException(final Throwable e) {
        if (e instanceof ExecutionException) {
            final Throwable cause = ((ExecutionException)e).getCause();
            if (cause instanceof IOException) {
                return (IOException)cause;
            }
        }
        if (e instanceof IOException) {
            return (IOException)e;
        }
        return IOExceptionSupport.create(e);
    }
    
    public void waitOn(final Future<Object> future) {
        try {
            future.get();
        }
        finally {
            final Throwable e;
            throw this.toIOException(e);
        }
    }
    
    private LevelDBStore$() {
        Log$class.$init$(MODULE$ = this);
        this.DEFAULT_DIRECTORY = new File("LevelDB");
        this.DONE = new InlineListenableFuture();
    }
}
