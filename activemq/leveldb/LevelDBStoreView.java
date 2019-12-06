// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.collection.Seq;
import java.util.concurrent.Executor;
import org.fusesource.hawtdispatch.package$;
import scala.runtime.ObjectRef;
import java.util.concurrent.CountDownLatch;
import scala.Function0;
import scala.Option$;
import java.io.File;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005\u001da\u0001B\u0001\u0003\u0001-\u0011\u0001\u0003T3wK2$%i\u0015;pe\u00164\u0016.Z<\u000b\u0005\r!\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000b\u0019\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001aA\u0003\u0005\u0002\u000e%5\taB\u0003\u0002\u0010!\u0005!A.\u00198h\u0015\u0005\t\u0012\u0001\u00026bm\u0006L!a\u0005\b\u0003\r=\u0013'.Z2u!\t)b#D\u0001\u0003\u0013\t9\"AA\u000bMKZ,G\u000e\u0012\"Ti>\u0014XMV5fo6\u0013U-\u00198\t\u0011e\u0001!Q1A\u0005\u0002i\tQa\u001d;pe\u0016,\u0012a\u0007\t\u0003+qI!!\b\u0002\u0003\u00191+g/\u001a7E\u0005N#xN]3\t\u0011}\u0001!\u0011!Q\u0001\nm\taa\u001d;pe\u0016\u0004\u0003\"B\u0011\u0001\t\u0003\u0011\u0013A\u0002\u001fj]&$h\b\u0006\u0002$IA\u0011Q\u0003\u0001\u0005\u00063\u0001\u0002\ra\u0007\u0005\u0006M\u0001!\taJ\u0001\u0013O\u0016$\u0018i]=oG\n+hMZ3s'&TX\rF\u0001)!\tIC&D\u0001+\u0015\u0005Y\u0013!B:dC2\f\u0017BA\u0017+\u0005\rIe\u000e\u001e\u0005\u0006_\u0001!\t\u0001M\u0001\u0012O\u0016$\u0018J\u001c3fq\u0012K'/Z2u_JLH#A\u0019\u0011\u00055\u0011\u0014BA\u001a\u000f\u0005\u0019\u0019FO]5oO\")Q\u0007\u0001C\u0001a\u0005yq-\u001a;M_\u001e$\u0015N]3di>\u0014\u0018\u0010C\u00038\u0001\u0011\u0005q%\u0001\u000fhKRLe\u000eZ3y\u00052|7m\u001b*fgR\f'\u000f^%oi\u0016\u0014h/\u00197\t\u000be\u0002A\u0011A\u0014\u0002#\u001d,G/\u00138eKb\u0014En\\2l'&TX\rC\u0003<\u0001\u0011\u0005A(A\thKRLe\u000eZ3y\u0007\u0006\u001c\u0007.Z*ju\u0016$\u0012!\u0010\t\u0003SyJ!a\u0010\u0016\u0003\t1{gn\u001a\u0005\u0006\u0003\u0002!\tAQ\u0001\u0014O\u0016$\u0018J\u001c3fq\u000e{W\u000e\u001d:fgNLwN\u001c\u000b\u0002\u0007B\u0011Ai\u0012\b\u0003S\u0015K!A\u0012\u0016\u0002\rA\u0013X\rZ3g\u0013\t\u0019\u0004J\u0003\u0002GU!)!\n\u0001C\u0001a\u0005yq-\u001a;J]\u0012,\u0007PR1di>\u0014\u0018\u0010C\u0003M\u0001\u0011\u0005q%\u0001\u000bhKRLe\u000eZ3y\u001b\u0006Dx\n]3o\r&dWm\u001d\u0005\u0006\u001d\u0002!\taJ\u0001\u0018O\u0016$\u0018J\u001c3fq^\u0013\u0018\u000e^3Ck\u001a4WM]*ju\u0016DQ\u0001\u0015\u0001\u0005\u0002q\n!bZ3u\u0019><7+\u001b>f\u0011\u0015\u0011\u0006\u0001\"\u0001T\u0003E9W\r\u001e)be\u0006tw.\u001b3DQ\u0016\u001c7n\u001d\u000b\u0002)B\u0011\u0011&V\u0005\u0003-*\u0012qAQ8pY\u0016\fg\u000eC\u0003Y\u0001\u0011\u00051+A\u0004hKR\u001c\u0016P\\2\t\u000bi\u0003A\u0011A*\u0002%\u001d,GOV3sS\u001aL8\t[3dWN,Xn\u001d\u0005\u00069\u0002!\t\u0001P\u0001\u0014O\u0016$Xk\\<DY>\u001cX\rZ\"pk:$XM\u001d\u0005\u0006=\u0002!\t\u0001P\u0001\u0016O\u0016$Xk\\<DC:\u001cW\r\\3e\u0007>,h\u000e^3s\u0011\u0015\u0001\u0007\u0001\"\u0001=\u0003Q9W\r^+poN#xN]5oO\u000e{WO\u001c;fe\")!\r\u0001C\u0001y\u0005\u0019r-\u001a;V_^\u001cFo\u001c:fI\u000e{WO\u001c;fe\")A\r\u0001C\u0001K\u0006Ar-\u001a;V_^l\u0015\r_\"p[BdW\r^3MCR,gnY=\u0015\u0003\u0019\u0004\"!K4\n\u0005!T#A\u0002#pk\ndW\rC\u0003k\u0001\u0011\u0005Q-A\fhKRl\u0015\r_%oI\u0016DxK]5uK2\u000bG/\u001a8ds\")A\u000e\u0001C\u0001K\u0006)r-\u001a;NCbdunZ,sSR,G*\u0019;f]\u000eL\b\"\u00028\u0001\t\u0003)\u0017!F4fi6\u000b\u0007\u0010T8h\r2,8\u000f\u001b'bi\u0016t7-\u001f\u0005\u0006a\u0002!\t!Z\u0001\u0017O\u0016$X*\u0019=M_\u001e\u0014v\u000e^1uK2\u000bG/\u001a8ds\")!\u000f\u0001C\u0001K\u0006Q\"/Z:fiV{w/T1y\u0007>l\u0007\u000f\\3uK2\u000bG/\u001a8ds\")A\u000f\u0001C\u0001K\u0006I\"/Z:fi6\u000b\u00070\u00138eKb<&/\u001b;f\u0019\u0006$XM\\2z\u0011\u00151\b\u0001\"\u0001f\u0003]\u0011Xm]3u\u001b\u0006DHj\\4Xe&$X\rT1uK:\u001c\u0017\u0010C\u0003y\u0001\u0011\u0005Q-A\fsKN,G/T1y\u0019><g\t\\;tQ2\u000bG/\u001a8ds\")!\u0010\u0001C\u0001K\u0006A\"/Z:fi6\u000b\u0007\u0010T8h%>$\u0018\r^3MCR,gnY=\t\u000bq\u0004A\u0011\u0001\u0019\u0002\u001b\u001d,G/\u00138eKb\u001cF/\u0019;t\u0011\u0015q\b\u0001\"\u0001\u0000\u0003\u001d\u0019w.\u001c9bGR$\"!!\u0001\u0011\u0007%\n\u0019!C\u0002\u0002\u0006)\u0012A!\u00168ji\u0002")
public class LevelDBStoreView implements LevelDBStoreViewMBean
{
    private final LevelDBStore store;
    
    public LevelDBStore store() {
        return this.store;
    }
    
    @Override
    public int getAsyncBufferSize() {
        return this.store().asyncBufferSize();
    }
    
    @Override
    public String getIndexDirectory() {
        return this.store().directory().getCanonicalPath();
    }
    
    @Override
    public String getLogDirectory() {
        return ((File)Option$.MODULE$.apply((Object)this.store().logDirectory()).getOrElse((Function0)new LevelDBStoreView$$anonfun$getLogDirectory.LevelDBStoreView$$anonfun$getLogDirectory$1(this))).getCanonicalPath();
    }
    
    @Override
    public int getIndexBlockRestartInterval() {
        return this.store().indexBlockRestartInterval();
    }
    
    @Override
    public int getIndexBlockSize() {
        return this.store().indexBlockSize();
    }
    
    @Override
    public long getIndexCacheSize() {
        return this.store().indexCacheSize();
    }
    
    @Override
    public String getIndexCompression() {
        return this.store().indexCompression();
    }
    
    @Override
    public String getIndexFactory() {
        return this.store().db().client().factory().getClass().getName();
    }
    
    @Override
    public int getIndexMaxOpenFiles() {
        return this.store().indexMaxOpenFiles();
    }
    
    @Override
    public int getIndexWriteBufferSize() {
        return this.store().indexWriteBufferSize();
    }
    
    @Override
    public long getLogSize() {
        return this.store().logSize();
    }
    
    @Override
    public boolean getParanoidChecks() {
        return this.store().paranoidChecks();
    }
    
    @Override
    public boolean getSync() {
        return this.store().sync();
    }
    
    @Override
    public boolean getVerifyChecksums() {
        return this.store().verifyChecksums();
    }
    
    @Override
    public long getUowClosedCounter() {
        return this.store().db().uowClosedCounter();
    }
    
    @Override
    public long getUowCanceledCounter() {
        return this.store().db().uowCanceledCounter();
    }
    
    @Override
    public long getUowStoringCounter() {
        return this.store().db().uowStoringCounter();
    }
    
    @Override
    public long getUowStoredCounter() {
        return this.store().db().uowStoredCounter();
    }
    
    @Override
    public double getUowMaxCompleteLatency() {
        return this.store().db().uow_complete_latency().get();
    }
    
    @Override
    public double getMaxIndexWriteLatency() {
        return this.store().db().client().max_index_write_latency().get();
    }
    
    @Override
    public double getMaxLogWriteLatency() {
        return this.store().db().client().log().max_log_write_latency().get();
    }
    
    @Override
    public double getMaxLogFlushLatency() {
        return this.store().db().client().log().max_log_flush_latency().get();
    }
    
    @Override
    public double getMaxLogRotateLatency() {
        return this.store().db().client().log().max_log_rotate_latency().get();
    }
    
    @Override
    public double resetUowMaxCompleteLatency() {
        return this.store().db().uow_complete_latency().reset();
    }
    
    @Override
    public double resetMaxIndexWriteLatency() {
        return this.store().db().client().max_index_write_latency().reset();
    }
    
    @Override
    public double resetMaxLogWriteLatency() {
        return this.store().db().client().log().max_log_write_latency().reset();
    }
    
    @Override
    public double resetMaxLogFlushLatency() {
        return this.store().db().client().log().max_log_flush_latency().reset();
    }
    
    @Override
    public double resetMaxLogRotateLatency() {
        return this.store().db().client().log().max_log_rotate_latency().reset();
    }
    
    @Override
    public String getIndexStats() {
        return this.store().db().client().index().getProperty("leveldb.stats");
    }
    
    @Override
    public void compact() {
        final ObjectRef done = ObjectRef.create((Object)new CountDownLatch(1));
        final Seq positions = this.store().getTopicGCPositions();
        package$.MODULE$.ExecutorWrapper((Executor)this.store().client().writeExecutor()).apply((Function0)new LevelDBStoreView$$anonfun$compact.LevelDBStoreView$$anonfun$compact$1(this, done, positions));
        ((CountDownLatch)done.elem).await();
    }
    
    public LevelDBStoreView(final LevelDBStore store) {
        this.store = store;
    }
}
