// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.collection.Seq;
import scala.Function0;
import scala.Predef$;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import scala.reflect.ScalaSignature;
import org.apache.activemq.store.ListenableFuture;

@ScalaSignature(bytes = "\u0006\u0001\u0005%c\u0001B\u0001\u0003\u0001-\u0011qbQ8v]R$un\u001e8GkR,(/\u001a\u0006\u0003\u0007\u0011\tq\u0001\\3wK2$'M\u0003\u0002\u0006\r\u0005A\u0011m\u0019;jm\u0016l\u0017O\u0003\u0002\b\u0011\u00051\u0011\r]1dQ\u0016T\u0011!C\u0001\u0004_J<7\u0001A\u000b\u0003\u0019u\u00192\u0001A\u0007\u0016!\tq1#D\u0001\u0010\u0015\t\u0001\u0012#\u0001\u0003mC:<'\"\u0001\n\u0002\t)\fg/Y\u0005\u0003)=\u0011aa\u00142kK\u000e$\bc\u0001\f\u001a75\tqC\u0003\u0002\u0019\t\u0005)1\u000f^8sK&\u0011!d\u0006\u0002\u0011\u0019&\u001cH/\u001a8bE2,g)\u001e;ve\u0016\u0004\"\u0001H\u000f\r\u0001\u0011)a\u0004\u0001b\u0001?\t\tA+\u0005\u0002!MA\u0011\u0011\u0005J\u0007\u0002E)\t1%A\u0003tG\u0006d\u0017-\u0003\u0002&E\t9aj\u001c;iS:<\u0007CA\u0011(\u0013\tA#E\u0001\u0004B]f\u0014VM\u001a\u0005\u0006U\u0001!\taK\u0001\u0007y%t\u0017\u000e\u001e \u0015\u00031\u00022!\f\u0001\u001c\u001b\u0005\u0011\u0001bB\u0018\u0001\u0005\u0004%I\u0001M\u0001\u0006Y\u0006$8\r[\u000b\u0002cA\u0011!gN\u0007\u0002g)\u0011A'N\u0001\u000bG>t7-\u001e:sK:$(B\u0001\u001c\u0012\u0003\u0011)H/\u001b7\n\u0005a\u001a$AD\"pk:$Hi\\<o\u0019\u0006$8\r\u001b\u0005\u0007u\u0001\u0001\u000b\u0011B\u0019\u0002\r1\fGo\u00195!\u0011%a\u0004\u00011AA\u0002\u0013\u0005Q(A\u0003wC2,X-F\u0001\u001c\u0011%y\u0004\u00011AA\u0002\u0013\u0005\u0001)A\u0005wC2,Xm\u0018\u0013fcR\u0011\u0011\t\u0012\t\u0003C\tK!a\u0011\u0012\u0003\tUs\u0017\u000e\u001e\u0005\b\u000bz\n\t\u00111\u0001\u001c\u0003\rAH%\r\u0005\u0007\u000f\u0002\u0001\u000b\u0015B\u000e\u0002\rY\fG.^3!Q\t1\u0015\n\u0005\u0002\"\u0015&\u00111J\t\u0002\tm>d\u0017\r^5mK\"IQ\n\u0001a\u0001\u0002\u0004%\tAT\u0001\u0006KJ\u0014xN]\u000b\u0002\u001fB\u0011\u0001\u000b\u0017\b\u0003#Zs!AU+\u000e\u0003MS!\u0001\u0016\u0006\u0002\rq\u0012xn\u001c;?\u0013\u0005\u0019\u0013BA,#\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u0017.\u0003\u0013QC'o\\<bE2,'BA,#\u0011%a\u0006\u00011AA\u0002\u0013\u0005Q,A\u0005feJ|'o\u0018\u0013fcR\u0011\u0011I\u0018\u0005\b\u000bn\u000b\t\u00111\u0001P\u0011\u0019\u0001\u0007\u0001)Q\u0005\u001f\u00061QM\u001d:pe\u0002B\u0011B\u0019\u0001A\u0002\u0003\u0007I\u0011A2\u0002\u00111L7\u000f^3oKJ,\u0012\u0001\u001a\t\u0003\u001d\u0015L!AZ\b\u0003\u0011I+hN\\1cY\u0016D\u0011\u0002\u001b\u0001A\u0002\u0003\u0007I\u0011A5\u0002\u00191L7\u000f^3oKJ|F%Z9\u0015\u0005\u0005S\u0007bB#h\u0003\u0003\u0005\r\u0001\u001a\u0005\u0007Y\u0002\u0001\u000b\u0015\u00023\u0002\u00131L7\u000f^3oKJ\u0004\u0003\"\u00028\u0001\t\u0003y\u0017AB2b]\u000e,G\u000e\u0006\u0002qgB\u0011\u0011%]\u0005\u0003e\n\u0012qAQ8pY\u0016\fg\u000eC\u0003u[\u0002\u0007\u0001/A\u000bnCfLe\u000e^3seV\u0004H/\u00134Sk:t\u0017N\\4\t\u000bY\u0004A\u0011A<\u0002\u0017%\u001c8)\u00198dK2dW\r\u001a\u000b\u0002a\")\u0011\u0010\u0001C\u0001u\u0006I1m\\7qY\u0016$X\rZ\u000b\u0002a\")A\u0010\u0001C\u0001{\u0006)\u0011m^1jiR\t\u0011\tC\u0003}\u0001\u0011\u0005q\u0010F\u0003q\u0003\u0003\tY\u0001C\u0004\u0002\u0004y\u0004\r!!\u0002\u0002\u0005A\f\u0004cA\u0011\u0002\b%\u0019\u0011\u0011\u0002\u0012\u0003\t1{gn\u001a\u0005\b\u0003\u001bq\b\u0019AA\b\u0003\t\u0001(\u0007E\u00023\u0003#I1!a\u00054\u0005!!\u0016.\\3V]&$\bbBA\f\u0001\u0011\u0005\u0011\u0011D\u0001\u0004g\u0016$HcA!\u0002\u001c!9\u0011QDA\u000b\u0001\u0004Y\u0012!\u0001<\t\u000f\u0005\u0005\u0002\u0001\"\u0001\u0002$\u00051a-Y5mK\u0012$2!QA\u0013\u0011\u001d\ti\"a\bA\u0002=Cq!!\u000b\u0001\t\u0003\tY#A\u0002hKR$\u0012a\u0007\u0005\b\u0003S\u0001A\u0011AA\u0018)\u0015Y\u0012\u0011GA\u001a\u0011!\t\u0019!!\fA\u0002\u0005\u0015\u0001\u0002CA\u0007\u0003[\u0001\r!a\u0004\t\r\u0005]\u0002\u0001\"\u0001x\u0003\u0019I7\u000fR8oK\"9\u00111\b\u0001\u0005\u0002\u0005u\u0012\u0001\u00044je\u0016d\u0015n\u001d;f]\u0016\u0014X#A!\t\u000f\u0005\u0005\u0003\u0001\"\u0001\u0002D\u0005Y\u0011\r\u001a3MSN$XM\\3s)\r\t\u0015Q\t\u0005\b\u0003\u000f\ny\u00041\u0001e\u0003\u0005a\u0007")
public class CountDownFuture<T> implements ListenableFuture<T>
{
    private final CountDownLatch latch;
    private volatile T value;
    private Throwable error;
    private Runnable listener;
    
    private CountDownLatch latch() {
        return this.latch;
    }
    
    public T value() {
        return this.value;
    }
    
    public void value_$eq(final T x$1) {
        this.value = x$1;
    }
    
    public Throwable error() {
        return this.error;
    }
    
    public void error_$eq(final Throwable x$1) {
        this.error = x$1;
    }
    
    public Runnable listener() {
        return this.listener;
    }
    
    public void listener_$eq(final Runnable x$1) {
        this.listener = x$1;
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    public boolean completed() {
        return this.latch().getCount() == 0L;
    }
    
    public void await() {
        this.latch().await();
    }
    
    public boolean await(final long p1, final TimeUnit p2) {
        return this.latch().await(p1, p2);
    }
    
    public void set(final T v) {
        this.value_$eq(v);
        this.latch().countDown();
        this.fireListener();
    }
    
    public void failed(final Throwable v) {
        this.error_$eq(v);
        this.latch().countDown();
        this.fireListener();
    }
    
    @Override
    public T get() {
        this.latch().await();
        if (this.error() == null) {
            return this.value();
        }
        throw this.error();
    }
    
    @Override
    public T get(final long p1, final TimeUnit p2) {
        if (!this.latch().await(p1, p2)) {
            throw new TimeoutException();
        }
        if (this.error() == null) {
            return this.value();
        }
        throw this.error();
    }
    
    @Override
    public boolean isDone() {
        return this.latch().await(0L, TimeUnit.SECONDS);
    }
    
    public void fireListener() {
        if (this.listener() != null) {
            try {
                this.listener().run();
            }
            finally {
                final Throwable e;
                LevelDBStore$.MODULE$.warn(e, (Function0<String>)new CountDownFuture$$anonfun$fireListener.CountDownFuture$$anonfun$fireListener$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            }
        }
    }
    
    @Override
    public void addListener(final Runnable l) {
        this.listener_$eq(l);
        if (this.isDone()) {
            this.fireListener();
        }
    }
    
    public CountDownFuture() {
        this.latch = new CountDownLatch(1);
    }
}
