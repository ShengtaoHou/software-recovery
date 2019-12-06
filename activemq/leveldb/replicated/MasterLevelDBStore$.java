// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.util.Log$class;
import scala.collection.Seq;
import scala.Function0;
import org.slf4j.Logger;
import org.apache.activemq.leveldb.util.Log;

public final class MasterLevelDBStore$ implements Log
{
    public static final MasterLevelDBStore$ MODULE$;
    private final int SYNC_TO_DISK;
    private final int SYNC_TO_REMOTE;
    private final int SYNC_TO_REMOTE_MEMORY;
    private final int SYNC_TO_REMOTE_DISK;
    private final Logger log;
    
    static {
        new MasterLevelDBStore$();
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
    
    public int SYNC_TO_DISK() {
        return this.SYNC_TO_DISK;
    }
    
    public int SYNC_TO_REMOTE() {
        return this.SYNC_TO_REMOTE;
    }
    
    public int SYNC_TO_REMOTE_MEMORY() {
        return this.SYNC_TO_REMOTE_MEMORY;
    }
    
    public int SYNC_TO_REMOTE_DISK() {
        return this.SYNC_TO_REMOTE_DISK;
    }
    
    private MasterLevelDBStore$() {
        Log$class.$init$(MODULE$ = this);
        this.SYNC_TO_DISK = 1;
        this.SYNC_TO_REMOTE = 2;
        this.SYNC_TO_REMOTE_MEMORY = (0x4 | this.SYNC_TO_REMOTE());
        this.SYNC_TO_REMOTE_DISK = (0x8 | this.SYNC_TO_REMOTE());
    }
}
