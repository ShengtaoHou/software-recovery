// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Some;
import scala.None$;
import scala.Tuple2;
import scala.Option;
import java.io.File;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.leveldb.util.Log$class;
import scala.collection.Seq;
import scala.Function0;
import org.slf4j.Logger;
import scala.Serializable;
import org.apache.activemq.leveldb.util.Log;

public final class RecordLog$ implements Log, Serializable
{
    public static final RecordLog$ MODULE$;
    private final byte LOG_HEADER_PREFIX;
    private final byte UOW_END_RECORD;
    private final int LOG_HEADER_SIZE;
    private final int BUFFER_SIZE;
    private final int BYPASS_BUFFER_SIZE;
    private final Logger log;
    
    static {
        new RecordLog$();
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
    
    public byte LOG_HEADER_PREFIX() {
        return this.LOG_HEADER_PREFIX;
    }
    
    public byte UOW_END_RECORD() {
        return this.UOW_END_RECORD;
    }
    
    public int LOG_HEADER_SIZE() {
        return this.LOG_HEADER_SIZE;
    }
    
    public int BUFFER_SIZE() {
        return this.BUFFER_SIZE;
    }
    
    public int BYPASS_BUFFER_SIZE() {
        return this.BYPASS_BUFFER_SIZE;
    }
    
    public Buffer encode_long(final long a1) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(8);
        out.writeLong(a1);
        return out.toBuffer();
    }
    
    public long decode_long(final Buffer value) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(value);
        return in.readLong();
    }
    
    public RecordLog apply(final File directory, final String logSuffix) {
        return new RecordLog(directory, logSuffix);
    }
    
    public Option<Tuple2<File, String>> unapply(final RecordLog x$0) {
        return (Option<Tuple2<File, String>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2((Object)x$0.directory(), (Object)x$0.logSuffix())));
    }
    
    private Object readResolve() {
        return RecordLog$.MODULE$;
    }
    
    private RecordLog$() {
        Log$class.$init$(MODULE$ = this);
        this.LOG_HEADER_PREFIX = 42;
        this.UOW_END_RECORD = -1;
        this.LOG_HEADER_SIZE = 10;
        this.BUFFER_SIZE = 524288;
        this.BYPASS_BUFFER_SIZE = 16384;
    }
}
