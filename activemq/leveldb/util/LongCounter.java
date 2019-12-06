// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.runtime.BoxesRunTime;
import scala.reflect.ScalaSignature;
import scala.Serializable;

@ScalaSignature(bytes = "\u0006\u000154A!\u0001\u0002\u0001\u001b\tYAj\u001c8h\u0007>,h\u000e^3s\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011c\u0001\u0001\u000f)A\u0011qBE\u0007\u0002!)\t\u0011#A\u0003tG\u0006d\u0017-\u0003\u0002\u0014!\t1\u0011I\\=SK\u001a\u0004\"aD\u000b\n\u0005Y\u0001\"\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003\r\u0001\u0005\u0003\u0007I\u0011B\r\u0002\u000bY\fG.^3\u0016\u0003i\u0001\"aD\u000e\n\u0005q\u0001\"\u0001\u0002'p]\u001eD\u0001B\b\u0001\u0003\u0002\u0004%IaH\u0001\nm\u0006dW/Z0%KF$\"\u0001I\u0012\u0011\u0005=\t\u0013B\u0001\u0012\u0011\u0005\u0011)f.\u001b;\t\u000f\u0011j\u0012\u0011!a\u00015\u0005\u0019\u0001\u0010J\u0019\t\u0011\u0019\u0002!\u0011!Q!\ni\taA^1mk\u0016\u0004\u0003\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\b\u0006\u0002+YA\u00111\u0006A\u0007\u0002\u0005!9\u0001d\nI\u0001\u0002\u0004Q\u0002\"\u0002\u0018\u0001\t\u0003y\u0013!B2mK\u0006\u0014H#\u0001\u0011\t\u000bE\u0002A\u0011\u0001\u001a\u0002\u0007\u001d,G\u000fF\u0001\u001b\u0011\u0015!\u0004\u0001\"\u00016\u0003\r\u0019X\r\u001e\u000b\u0003AYBQ\u0001G\u001aA\u0002iAQ\u0001\u000f\u0001\u0005\u0002I\nq\"\u001b8de\u0016lWM\u001c;B]\u0012<U\r\u001e\u0005\u0006u\u0001!\tAM\u0001\u0010I\u0016\u001c'/Z7f]R\fe\u000eZ$fi\")A\b\u0001C\u0001{\u0005I\u0011\r\u001a3B]\u0012<U\r\u001e\u000b\u00035yBQaP\u001eA\u0002i\ta!Y7pk:$\b\"B!\u0001\t\u0003\u0011\u0014aD4fi\u0006sG-\u00138de\u0016lWM\u001c;\t\u000b\r\u0003A\u0011\u0001\u001a\u0002\u001f\u001d,G/\u00118e\t\u0016\u001c'/Z7f]RDQ!\u0012\u0001\u0005\u0002\u0019\u000b\u0011bZ3u\u0003:$\u0017\t\u001a3\u0015\u0005i9\u0005\"B E\u0001\u0004Q\u0002\"B%\u0001\t\u0003R\u0015\u0001\u0003;p'R\u0014\u0018N\\4\u0015\u0003-\u0003\"\u0001T)\u000e\u00035S!AT(\u0002\t1\fgn\u001a\u0006\u0002!\u0006!!.\u0019<b\u0013\t\u0011VJ\u0001\u0004TiJLgnZ\u0004\b)\n\t\t\u0011#\u0001V\u0003-auN\\4D_VtG/\u001a:\u0011\u0005-2faB\u0001\u0003\u0003\u0003E\taV\n\u0004-:!\u0002\"\u0002\u0015W\t\u0003IF#A+\t\u000fm3\u0016\u0013!C\u00019\u0006YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIE*\u0012!\u0018\u0016\u00035y[\u0013a\u0018\t\u0003A\u0016l\u0011!\u0019\u0006\u0003E\u000e\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005\u0011\u0004\u0012AC1o]>$\u0018\r^5p]&\u0011a-\u0019\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u00025W\u0003\u0003%I![\u0001\fe\u0016\fGMU3t_24X\rF\u0001k!\ta5.\u0003\u0002m\u001b\n1qJ\u00196fGR\u0004")
public class LongCounter implements Serializable
{
    private long value;
    
    public static long $lessinit$greater$default$1() {
        return LongCounter$.MODULE$.$lessinit$greater$default$1();
    }
    
    private long value() {
        return this.value;
    }
    
    private void value_$eq(final long x$1) {
        this.value = x$1;
    }
    
    public void clear() {
        this.value_$eq(0L);
    }
    
    public long get() {
        return this.value();
    }
    
    public void set(final long value) {
        this.value_$eq(value);
    }
    
    public long incrementAndGet() {
        return this.addAndGet(1L);
    }
    
    public long decrementAndGet() {
        return this.addAndGet(-1L);
    }
    
    public long addAndGet(final long amount) {
        this.value_$eq(this.value() + amount);
        return this.value();
    }
    
    public long getAndIncrement() {
        return this.getAndAdd(1L);
    }
    
    public long getAndDecrement() {
        return this.getAndAdd(-11L);
    }
    
    public long getAndAdd(final long amount) {
        final long rc = this.value();
        this.value_$eq(this.value() + amount);
        return rc;
    }
    
    @Override
    public String toString() {
        return BoxesRunTime.boxToLong(this.get()).toString();
    }
    
    public LongCounter(final long value) {
        this.value = value;
    }
}
