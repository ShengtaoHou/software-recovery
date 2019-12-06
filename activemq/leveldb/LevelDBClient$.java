// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.List;
import scala.math.Ordering;
import scala.Function1;
import scala.reflect.ClassTag$;
import scala.Array$;
import scala.collection.immutable.TreeMap$;
import scala.collection.immutable.TreeMap;
import scala.collection.immutable.StringOps;
import scala.Predef$;
import org.apache.activemq.leveldb.util.FileSupport$;
import java.io.File;
import scala.Tuple3;
import scala.runtime.BoxesRunTime;
import scala.Tuple2$mcJJ$sp;
import scala.Tuple2$mcJI$sp;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import scala.Tuple2;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.fusesource.hawtbuf.AbstractVarIntSupport;
import org.apache.activemq.leveldb.record.EntryKey;
import org.apache.activemq.leveldb.record.EntryRecord;
import org.apache.activemq.leveldb.record.CollectionKey;
import org.apache.activemq.leveldb.record.CollectionRecord;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.leveldb.util.Log$class;
import scala.collection.Seq;
import scala.Function0;
import org.slf4j.Logger;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.iq80.leveldb.WriteOptions;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.activemq.leveldb.util.Log;

public final class LevelDBClient$ implements Log
{
    public static final LevelDBClient$ MODULE$;
    private final String STORE_SCHEMA_PREFIX;
    private final int STORE_SCHEMA_VERSION;
    private final long THREAD_POOL_STACK_SIZE;
    private final ThreadPoolExecutor THREAD_POOL;
    private final WriteOptions PLIST_WRITE_OPTIONS;
    private final byte[] DIRTY_INDEX_KEY;
    private final byte[] LOG_REF_INDEX_KEY;
    private final byte[] LOGS_INDEX_KEY;
    private final byte[] PRODUCER_IDS_INDEX_KEY;
    private final byte[] COLLECTION_META_KEY;
    private final byte[] TRUE;
    private final byte[] FALSE;
    private final AsciiBuffer ACK_POSITION;
    private final byte COLLECTION_PREFIX;
    private final byte[] COLLECTION_PREFIX_ARRAY;
    private final byte ENTRY_PREFIX;
    private final byte[] ENTRY_PREFIX_ARRAY;
    private final byte LOG_ADD_COLLECTION;
    private final byte LOG_REMOVE_COLLECTION;
    private final byte LOG_ADD_ENTRY;
    private final byte LOG_REMOVE_ENTRY;
    private final byte LOG_DATA;
    private final byte LOG_TRACE;
    private final byte LOG_UPDATE_ENTRY;
    private final String LOG_SUFFIX;
    private final String INDEX_SUFFIX;
    private final Logger log;
    
    static {
        new LevelDBClient$();
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
    
    public final String STORE_SCHEMA_PREFIX() {
        return "activemq_leveldb_store:";
    }
    
    public final int STORE_SCHEMA_VERSION() {
        return 1;
    }
    
    public final long THREAD_POOL_STACK_SIZE() {
        return this.THREAD_POOL_STACK_SIZE;
    }
    
    public final ThreadPoolExecutor THREAD_POOL() {
        return this.THREAD_POOL;
    }
    
    public WriteOptions PLIST_WRITE_OPTIONS() {
        return this.PLIST_WRITE_OPTIONS;
    }
    
    public final byte[] DIRTY_INDEX_KEY() {
        return this.DIRTY_INDEX_KEY;
    }
    
    public final byte[] LOG_REF_INDEX_KEY() {
        return this.LOG_REF_INDEX_KEY;
    }
    
    public final byte[] LOGS_INDEX_KEY() {
        return this.LOGS_INDEX_KEY;
    }
    
    public final byte[] PRODUCER_IDS_INDEX_KEY() {
        return this.PRODUCER_IDS_INDEX_KEY;
    }
    
    public final byte[] COLLECTION_META_KEY() {
        return this.COLLECTION_META_KEY;
    }
    
    public final byte[] TRUE() {
        return this.TRUE;
    }
    
    public final byte[] FALSE() {
        return this.FALSE;
    }
    
    public final AsciiBuffer ACK_POSITION() {
        return this.ACK_POSITION;
    }
    
    public final byte COLLECTION_PREFIX() {
        return this.COLLECTION_PREFIX;
    }
    
    public final byte[] COLLECTION_PREFIX_ARRAY() {
        return this.COLLECTION_PREFIX_ARRAY;
    }
    
    public final byte ENTRY_PREFIX() {
        return this.ENTRY_PREFIX;
    }
    
    public final byte[] ENTRY_PREFIX_ARRAY() {
        return this.ENTRY_PREFIX_ARRAY;
    }
    
    public final byte LOG_ADD_COLLECTION() {
        return this.LOG_ADD_COLLECTION;
    }
    
    public final byte LOG_REMOVE_COLLECTION() {
        return this.LOG_REMOVE_COLLECTION;
    }
    
    public final byte LOG_ADD_ENTRY() {
        return this.LOG_ADD_ENTRY;
    }
    
    public final byte LOG_REMOVE_ENTRY() {
        return this.LOG_REMOVE_ENTRY;
    }
    
    public final byte LOG_DATA() {
        return this.LOG_DATA;
    }
    
    public final byte LOG_TRACE() {
        return this.LOG_TRACE;
    }
    
    public final byte LOG_UPDATE_ENTRY() {
        return this.LOG_UPDATE_ENTRY;
    }
    
    public final String LOG_SUFFIX() {
        return ".log";
    }
    
    public final String INDEX_SUFFIX() {
        return ".index";
    }
    
    public byte[] toByteArray(final Buffer buffer) {
        return buffer.toByteArray();
    }
    
    public Buffer toBuffer(final byte[] buffer) {
        return new Buffer(buffer);
    }
    
    public byte[] encodeCollectionRecord(final CollectionRecord.Buffer v) {
        return v.toUnframedByteArray();
    }
    
    public CollectionRecord.Buffer decodeCollectionRecord(final Buffer data) {
        return CollectionRecord.FACTORY.parseUnframed(data);
    }
    
    public byte[] encodeCollectionKeyRecord(final CollectionKey.Buffer v) {
        return v.toUnframedByteArray();
    }
    
    public CollectionKey.Buffer decodeCollectionKeyRecord(final Buffer data) {
        return CollectionKey.FACTORY.parseUnframed(data);
    }
    
    public Buffer encodeEntryRecord(final EntryRecord.Buffer v) {
        return v.toUnframedBuffer();
    }
    
    public EntryRecord.Buffer decodeEntryRecord(final Buffer data) {
        return EntryRecord.FACTORY.parseUnframed(data);
    }
    
    public byte[] encodeEntryKeyRecord(final EntryKey.Buffer v) {
        return v.toUnframedByteArray();
    }
    
    public EntryKey.Buffer decodeEntryKeyRecord(final Buffer data) {
        return EntryKey.FACTORY.parseUnframed(data);
    }
    
    public byte[] encodeLocator(final long pos, final int len) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(AbstractVarIntSupport.computeVarLongSize(pos) + AbstractVarIntSupport.computeVarIntSize(len));
        out.writeVarLong(pos);
        out.writeVarInt(len);
        return out.getData();
    }
    
    public Tuple2<Object, Object> decodeLocator(final Buffer bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return (Tuple2<Object, Object>)new Tuple2$mcJI$sp(in.readVarLong(), in.readVarInt());
    }
    
    public Tuple2<Object, Object> decodeLocator(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return (Tuple2<Object, Object>)new Tuple2$mcJI$sp(in.readVarLong(), in.readVarInt());
    }
    
    public Buffer encodeLongLong(final long a1, final long a2) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(8);
        out.writeLong(a1);
        out.writeLong(a2);
        return out.toBuffer();
    }
    
    public Tuple2<Object, Object> decodeLongLong(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return (Tuple2<Object, Object>)new Tuple2$mcJJ$sp(in.readLong(), in.readLong());
    }
    
    public Buffer encodeLong(final long a1) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(8);
        out.writeLong(a1);
        return out.toBuffer();
    }
    
    public byte[] encodeVLong(final long a1) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(AbstractVarIntSupport.computeVarLongSize(a1));
        out.writeVarLong(a1);
        return out.getData();
    }
    
    public long decodeVLong(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return in.readVarLong();
    }
    
    public byte[] encodeLongKey(final byte a1, final long a2) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(9);
        out.writeByte(a1);
        out.writeLong(a2);
        return out.getData();
    }
    
    public Tuple2<Object, Object> decodeLongKey(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return (Tuple2<Object, Object>)new Tuple2((Object)BoxesRunTime.boxToByte(in.readByte()), (Object)BoxesRunTime.boxToLong(in.readLong()));
    }
    
    public long decodeLong(final Buffer bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return in.readLong();
    }
    
    public long decodeLong(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return in.readLong();
    }
    
    public byte[] encodeEntryKey(final byte a1, final long a2, final long a3) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(17);
        out.writeByte(a1);
        out.writeLong(a2);
        out.writeLong(a3);
        return out.getData();
    }
    
    public byte[] encodeEntryKey(final byte a1, final long a2, final Buffer a3) {
        final DataByteArrayOutputStream out = new DataByteArrayOutputStream(9 + a3.length);
        out.writeByte(a1);
        out.writeLong(a2);
        out.write(a3);
        return out.getData();
    }
    
    public Tuple3<Object, Object, Buffer> decodeEntryKey(final byte[] bytes) {
        final DataByteArrayInputStream in = new DataByteArrayInputStream(bytes);
        return (Tuple3<Object, Object, Buffer>)new Tuple3((Object)BoxesRunTime.boxToByte(in.readByte()), (Object)BoxesRunTime.boxToLong(in.readLong()), (Object)in.readBuffer(in.available()));
    }
    
    public byte[] bytes(final String value) {
        return value.getBytes("UTF-8");
    }
    
    public File create_sequence_file(final File directory, final long id, final String suffix) {
        return FileSupport$.MODULE$.toRichFile(directory).$div(new StringOps(Predef$.MODULE$.augmentString("%016x%s")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToLong(id), suffix })));
    }
    
    public TreeMap<Object, File> find_sequence_files(final File directory, final String suffix) {
        return (TreeMap<Object, File>)TreeMap$.MODULE$.apply((Seq)Predef$.MODULE$.wrapRefArray((Object[])Predef$.MODULE$.refArrayOps((Object[])FileSupport$.MODULE$.toRichFile(directory).list_files()).flatMap((Function1)new LevelDBClient$$anonfun$find_sequence_files.LevelDBClient$$anonfun$find_sequence_files$1(suffix), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply((Class)Tuple2.class)))), (Ordering)Ordering.Long$.MODULE$);
    }
    
    public void copyIndex(final File from, final File to) {
        Predef$.MODULE$.refArrayOps((Object[])FileSupport$.MODULE$.toRichFile(from).list_files()).foreach((Function1)new LevelDBClient$$anonfun$copyIndex.LevelDBClient$$anonfun$copyIndex$1(to));
    }
    
    private LevelDBClient$() {
        Log$class.$init$(MODULE$ = this);
        this.THREAD_POOL_STACK_SIZE = new StringOps(Predef$.MODULE$.augmentString(System.getProperty("leveldb.thread.stack.size", String.valueOf(BoxesRunTime.boxToInteger(524288))))).toLong();
        this.THREAD_POOL = new ThreadPoolExecutor() {
            @Override
            public void shutdown() {
            }
            
            @Override
            public List<Runnable> shutdownNow() {
                return Collections.emptyList();
            }
        };
        this.PLIST_WRITE_OPTIONS = new WriteOptions().sync(false);
        this.DIRTY_INDEX_KEY = this.bytes(":dirty");
        this.LOG_REF_INDEX_KEY = this.bytes(":log-refs");
        this.LOGS_INDEX_KEY = this.bytes(":logs");
        this.PRODUCER_IDS_INDEX_KEY = this.bytes(":producer_ids");
        this.COLLECTION_META_KEY = this.bytes(":collection-meta");
        this.TRUE = this.bytes("true");
        this.FALSE = this.bytes("false");
        this.ACK_POSITION = new AsciiBuffer("p");
        this.COLLECTION_PREFIX = 99;
        this.COLLECTION_PREFIX_ARRAY = new byte[] { this.COLLECTION_PREFIX() };
        this.ENTRY_PREFIX = 101;
        this.ENTRY_PREFIX_ARRAY = new byte[] { this.ENTRY_PREFIX() };
        this.LOG_ADD_COLLECTION = 1;
        this.LOG_REMOVE_COLLECTION = 2;
        this.LOG_ADD_ENTRY = 3;
        this.LOG_REMOVE_ENTRY = 4;
        this.LOG_DATA = 5;
        this.LOG_TRACE = 6;
        this.LOG_UPDATE_ENTRY = 7;
    }
}
