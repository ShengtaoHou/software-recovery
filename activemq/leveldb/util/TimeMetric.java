// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.Product$class;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.Function0;
import scala.runtime.BoxesRunTime;
import scala.Predef$;
import scala.runtime.RichLong$;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005]b\u0001B\u0001\u0003\u00016\u0011!\u0002V5nK6+GO]5d\u0015\t\u0019A!\u0001\u0003vi&d'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\u000f)]\u0001\"a\u0004\n\u000e\u0003AQ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'A\u0011a!\u00118z%\u00164\u0007CA\b\u0016\u0013\t1\u0002CA\u0004Qe>$Wo\u0019;\u0011\u0005=A\u0012BA\r\u0011\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011\u0015Y\u0002\u0001\"\u0001\u001d\u0003\u0019a\u0014N\\5u}Q\tQ\u0004\u0005\u0002\u001f\u00015\t!\u0001C\u0004!\u0001\u0001\u0007I\u0011A\u0011\u0002\u00075\f\u00070F\u0001#!\ty1%\u0003\u0002%!\t!Aj\u001c8h\u0011\u001d1\u0003\u00011A\u0005\u0002\u001d\nq!\\1y?\u0012*\u0017\u000f\u0006\u0002)WA\u0011q\"K\u0005\u0003UA\u0011A!\u00168ji\"9A&JA\u0001\u0002\u0004\u0011\u0013a\u0001=%c!1a\u0006\u0001Q!\n\t\nA!\\1yA!)\u0001\u0007\u0001C\u0001c\u0005\u0019\u0011\r\u001a3\u0015\u0005!\u0012\u0004\"B\u001a0\u0001\u0004\u0011\u0013\u0001\u00033ve\u0006$\u0018n\u001c8\t\u000bU\u0002A\u0011\u0001\u001c\u0002\u0007\u001d,G/F\u00018!\ty\u0001(\u0003\u0002:!\t1Ai\\;cY\u0016DQa\u000f\u0001\u0005\u0002Y\nQA]3tKRDQ!\u0010\u0001\u0005\u0002y\nQ!\u00199qYf,\"a\u0010\"\u0015\u0005\u0001[\u0005CA!C\u0019\u0001!Qa\u0011\u001fC\u0002\u0011\u0013\u0011\u0001V\t\u0003\u000b\"\u0003\"a\u0004$\n\u0005\u001d\u0003\"a\u0002(pi\"Lgn\u001a\t\u0003\u001f%K!A\u0013\t\u0003\u0007\u0005s\u0017\u0010\u0003\u0004My\u0011\u0005\r!T\u0001\u0005MVt7\rE\u0002\u0010\u001d\u0002K!a\u0014\t\u0003\u0011q\u0012\u0017P\\1nKzBq!\u0015\u0001\u0002\u0002\u0013\u0005A$\u0001\u0003d_BL\bbB*\u0001\u0003\u0003%\t\u0005V\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0003U\u0003\"AV.\u000e\u0003]S!\u0001W-\u0002\t1\fgn\u001a\u0006\u00025\u0006!!.\u0019<b\u0013\tavK\u0001\u0004TiJLgn\u001a\u0005\b=\u0002\t\t\u0011\"\u0001`\u00031\u0001(o\u001c3vGR\f%/\u001b;z+\u0005\u0001\u0007CA\bb\u0013\t\u0011\u0007CA\u0002J]RDq\u0001\u001a\u0001\u0002\u0002\u0013\u0005Q-\u0001\bqe>$Wo\u0019;FY\u0016lWM\u001c;\u0015\u0005!3\u0007b\u0002\u0017d\u0003\u0003\u0005\r\u0001\u0019\u0005\bQ\u0002\t\t\u0011\"\u0011j\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00016\u0011\u0007-t\u0007*D\u0001m\u0015\ti\u0007#\u0001\u0006d_2dWm\u0019;j_:L!a\u001c7\u0003\u0011%#XM]1u_JDq!\u001d\u0001\u0002\u0002\u0013\u0005!/\u0001\u0005dC:,\u0015/^1m)\t\u0019h\u000f\u0005\u0002\u0010i&\u0011Q\u000f\u0005\u0002\b\u0005>|G.Z1o\u0011\u001da\u0003/!AA\u0002!Cq\u0001\u001f\u0001\u0002\u0002\u0013\u0005\u00130\u0001\u0005iCND7i\u001c3f)\u0005\u0001\u0007bB>\u0001\u0003\u0003%\t\u0005`\u0001\ti>\u001cFO]5oOR\tQ\u000bC\u0004\u007f\u0001\u0005\u0005I\u0011I@\u0002\r\u0015\fX/\u00197t)\r\u0019\u0018\u0011\u0001\u0005\bYu\f\t\u00111\u0001I\u000f%\t)AAA\u0001\u0012\u0003\t9!\u0001\u0006US6,W*\u001a;sS\u000e\u00042AHA\u0005\r!\t!!!A\t\u0002\u0005-1#BA\u0005\u0003\u001b9\u0002#BA\b\u0003+iRBAA\t\u0015\r\t\u0019\u0002E\u0001\beVtG/[7f\u0013\u0011\t9\"!\u0005\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>t\u0007\u0007C\u0004\u001c\u0003\u0013!\t!a\u0007\u0015\u0005\u0005\u001d\u0001\u0002C>\u0002\n\u0005\u0005IQ\t?\t\u0011u\nI!!A\u0005\u0002rA!\"a\t\u0002\n\u0005\u0005I\u0011QA\u0013\u0003\u001d)h.\u00199qYf$2a]A\u0014\u0011%\tI#!\t\u0002\u0002\u0003\u0007Q$A\u0002yIAB!\"!\f\u0002\n\u0005\u0005I\u0011BA\u0018\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005E\u0002c\u0001,\u00024%\u0019\u0011QG,\u0003\r=\u0013'.Z2u\u0001")
public class TimeMetric implements Product, Serializable
{
    private long max;
    
    public static boolean unapply(final TimeMetric x$0) {
        return TimeMetric$.MODULE$.unapply(x$0);
    }
    
    public long max() {
        return this.max;
    }
    
    public void max_$eq(final long x$1) {
        this.max = x$1;
    }
    
    public synchronized void add(final long duration) {
        this.max_$eq(RichLong$.MODULE$.max$extension(Predef$.MODULE$.longWrapper(this.max()), duration));
    }
    
    public double get() {
        synchronized (this) {
            return BoxesRunTime.unboxToLong((Object)BoxesRunTime.boxToLong(this.max())) / 1000000.0;
        }
    }
    
    public double reset() {
        synchronized (this) {
            final long rc = this.max();
            this.max_$eq(0L);
            return BoxesRunTime.unboxToLong((Object)BoxesRunTime.boxToLong(rc)) / 1000000.0;
        }
    }
    
    public <T> T apply(final Function0<T> func) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: lstore_2        /* start */
        //     4: aload_1         /* func */
        //     5: invokeinterface scala/Function0.apply:()Ljava/lang/Object;
        //    10: aload_0         /* this */
        //    11: invokestatic    java/lang/System.nanoTime:()J
        //    14: lload_2         /* start */
        //    15: lsub           
        //    16: invokevirtual   org/apache/activemq/leveldb/util/TimeMetric.add:(J)V
        //    19: areturn        
        //    20: astore          4
        //    22: aload_0         /* this */
        //    23: invokestatic    java/lang/System.nanoTime:()J
        //    26: lload_2        
        //    27: lsub           
        //    28: invokevirtual   org/apache/activemq/leveldb/util/TimeMetric.add:(J)V
        //    31: aload           4
        //    33: athrow         
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lscala/Function0<TT;>;)TT;
        //    StackMapTable: 00 01 FF 00 14 00 03 07 00 02 07 00 53 04 00 01 07 00 48
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  4      10     20     34     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public TimeMetric copy() {
        return new TimeMetric();
    }
    
    public String productPrefix() {
        return "TimeMetric";
    }
    
    public int productArity() {
        return 0;
    }
    
    public Object productElement(final int x$1) {
        throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof TimeMetric;
    }
    
    @Override
    public int hashCode() {
        return ScalaRunTime$.MODULE$._hashCode((Product)this);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        return x$1 instanceof TimeMetric && ((TimeMetric)x$1).canEqual(this);
    }
    
    public TimeMetric() {
        Product$class.$init$((Product)this);
        this.max = 0L;
    }
}
