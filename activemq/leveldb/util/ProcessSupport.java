// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.Function1;
import scala.Some;
import scala.None$;
import scala.Option;
import scala.runtime.AbstractFunction1;
import scala.Product$class;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.Function0;
import java.util.concurrent.Executor;
import org.apache.activemq.leveldb.LevelDBClient$;
import org.fusesource.hawtdispatch.package$;
import java.io.OutputStream;
import scala.Serializable;
import scala.Product;
import scala.runtime.BoxedUnit;
import scala.Function3;
import scala.collection.Seq;
import scala.Tuple3;
import java.io.InputStream;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\t5t!B\u0001\u0003\u0011\u0003i\u0011A\u0004)s_\u000e,7o]*vaB|'\u000f\u001e\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\bY\u00164X\r\u001c3c\u0015\t9\u0001\"\u0001\u0005bGRLg/Z7r\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001A\u0011abD\u0007\u0002\u0005\u0019)\u0001C\u0001E\u0001#\tq\u0001K]8dKN\u001c8+\u001e9q_J$8CA\b\u0013!\t\u0019b#D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005\u0019\te.\u001f*fM\")\u0011d\u0004C\u00015\u00051A(\u001b8jiz\"\u0012!\u0004\u0005\u00069=!\u0019!H\u0001\u0015i>\u0014\u0016n\u00195Qe>\u001cWm]:Ck&dG-\u001a:\u0015\u0007y\ty\u0003\u0005\u0002 A5\tqB\u0002\u0003\"\u001f\u0001\u0013#A\u0005*jG\"\u0004&o\\2fgN\u0014U/\u001b7eKJ\u001cB\u0001\t\n$MA\u00111\u0003J\u0005\u0003KQ\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0014O%\u0011\u0001\u0006\u0006\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\tU\u0001\u0012)\u001a!C\u0001W\u0005!1/\u001a7g+\u0005a\u0003CA\u00173\u001b\u0005q#BA\u00181\u0003\u0011a\u0017M\\4\u000b\u0003E\nAA[1wC&\u00111G\f\u0002\u000f!J|7-Z:t\u0005VLG\u000eZ3s\u0011!)\u0004E!E!\u0002\u0013a\u0013!B:fY\u001a\u0004\u0003\"B\r!\t\u00039DC\u0001\u00109\u0011\u0015Qc\u00071\u0001-\u0011\u0015Q\u0004\u0005\"\u0001<\u0003\u0015\u0019H/\u0019:u)\u0011athR%\u0011\u00055j\u0014B\u0001 /\u0005\u001d\u0001&o\\2fgNDq\u0001Q\u001d\u0011\u0002\u0003\u0007\u0011)A\u0002pkR\u0004\"AQ#\u000e\u0003\rS!\u0001\u0012\u0019\u0002\u0005%|\u0017B\u0001$D\u00051yU\u000f\u001e9viN#(/Z1n\u0011\u001dA\u0015\b%AA\u0002\u0005\u000b1!\u001a:s\u0011\u001dQ\u0015\b%AA\u0002-\u000b!!\u001b8\u0011\u0005\tc\u0015BA'D\u0005-Ie\u000e];u'R\u0014X-Y7\t\u000f=\u0003\u0013\u0011!C\u0001!\u0006!1m\u001c9z)\tq\u0012\u000bC\u0004+\u001dB\u0005\t\u0019\u0001\u0017\t\u000fM\u0003\u0013\u0013!C\u0001)\u0006y1\u000f^1si\u0012\"WMZ1vYR$\u0013'F\u0001VU\t\tekK\u0001X!\tAV,D\u0001Z\u0015\tQ6,A\u0005v]\u000eDWmY6fI*\u0011A\fF\u0001\u000bC:tw\u000e^1uS>t\u0017B\u00010Z\u0005E)hn\u00195fG.,GMV1sS\u0006t7-\u001a\u0005\bA\u0002\n\n\u0011\"\u0001U\u0003=\u0019H/\u0019:uI\u0011,g-Y;mi\u0012\u0012\u0004b\u00022!#\u0003%\taY\u0001\u0010gR\f'\u000f\u001e\u0013eK\u001a\fW\u000f\u001c;%gU\tAM\u000b\u0002L-\"9a\rII\u0001\n\u00039\u0017AD2paf$C-\u001a4bk2$H%M\u000b\u0002Q*\u0012AF\u0016\u0005\bU\u0002\n\t\u0011\"\u0011l\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\tA\u000e\u0005\u0002.[&\u0011aN\f\u0002\u0007'R\u0014\u0018N\\4\t\u000fA\u0004\u0013\u0011!C\u0001c\u0006a\u0001O]8ek\u000e$\u0018I]5usV\t!\u000f\u0005\u0002\u0014g&\u0011A\u000f\u0006\u0002\u0004\u0013:$\bb\u0002<!\u0003\u0003%\ta^\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\tA8\u0010\u0005\u0002\u0014s&\u0011!\u0010\u0006\u0002\u0004\u0003:L\bb\u0002?v\u0003\u0003\u0005\rA]\u0001\u0004q\u0012\n\u0004b\u0002@!\u0003\u0003%\te`\u0001\u0010aJ|G-^2u\u0013R,'/\u0019;peV\u0011\u0011\u0011\u0001\t\u0006\u0003\u0007\tI\u0001_\u0007\u0003\u0003\u000bQ1!a\u0002\u0015\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0005\u0003\u0017\t)A\u0001\u0005Ji\u0016\u0014\u0018\r^8s\u0011%\ty\u0001IA\u0001\n\u0003\t\t\"\u0001\u0005dC:,\u0015/^1m)\u0011\t\u0019\"!\u0007\u0011\u0007M\t)\"C\u0002\u0002\u0018Q\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005}\u0003\u001b\t\t\u00111\u0001y\u0011%\ti\u0002IA\u0001\n\u0003\ny\"\u0001\u0005iCND7i\u001c3f)\u0005\u0011\b\"CA\u0012A\u0005\u0005I\u0011IA\u0013\u0003!!xn\u0015;sS:<G#\u00017\t\u0013\u0005%\u0002%!A\u0005B\u0005-\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002\u0014\u00055\u0002\u0002\u0003?\u0002(\u0005\u0005\t\u0019\u0001=\t\u000b)Z\u0002\u0019\u0001\u0017\b\u0013\u0005Mr\"!A\t\u0002\u0005U\u0012A\u0005*jG\"\u0004&o\\2fgN\u0014U/\u001b7eKJ\u00042aHA\u001c\r!\ts\"!A\t\u0002\u0005e2#BA\u001c\u0003w1\u0003CBA\u001f\u0003\u0007bc$\u0004\u0002\u0002@)\u0019\u0011\u0011\t\u000b\u0002\u000fI,h\u000e^5nK&!\u0011QIA \u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\b3\u0005]B\u0011AA%)\t\t)\u0004\u0003\u0006\u0002$\u0005]\u0012\u0011!C#\u0003KA!\"a\u0014\u00028\u0005\u0005I\u0011QA)\u0003\u0015\t\u0007\u000f\u001d7z)\rq\u00121\u000b\u0005\u0007U\u00055\u0003\u0019\u0001\u0017\t\u0015\u0005]\u0013qGA\u0001\n\u0003\u000bI&A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005m\u0013\u0011\r\t\u0005'\u0005uC&C\u0002\u0002`Q\u0011aa\u00149uS>t\u0007\"CA2\u0003+\n\t\u00111\u0001\u001f\u0003\rAH\u0005\r\u0005\u000b\u0003O\n9$!A\u0005\n\u0005%\u0014a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!a\u001b\u0011\u00075\ni'C\u0002\u0002p9\u0012aa\u00142kK\u000e$\bbBA:\u001f\u0011\r\u0011QO\u0001\u000ei>\u0014\u0016n\u00195Qe>\u001cWm]:\u0015\t\u0005]\u0014\u0011\u001a\t\u0004?\u0005edABA>\u001f\u0001\u000biHA\u0006SS\u000eD\u0007K]8dKN\u001c8#BA=%\r2\u0003B\u0003\u0016\u0002z\tU\r\u0011\"\u0001\u0002\u0002V\tA\bC\u00056\u0003s\u0012\t\u0012)A\u0005y!9\u0011$!\u001f\u0005\u0002\u0005\u001dE\u0003BA<\u0003\u0013CaAKAC\u0001\u0004a\u0004\u0002CAG\u0003s\"\t!a$\u0002\r=tW\t_5u)\u0011\t\t*a&\u0011\u0007M\t\u0019*C\u0002\u0002\u0016R\u0011A!\u00168ji\"A\u0011\u0011TAF\u0001\u0004\tY*\u0001\u0003gk:\u001c\u0007CB\n\u0002\u001eJ\f\t*C\u0002\u0002 R\u0011\u0011BR;oGRLwN\\\u0019\t\u0013=\u000bI(!A\u0005\u0002\u0005\rF\u0003BA<\u0003KC\u0001BKAQ!\u0003\u0005\r\u0001\u0010\u0005\nM\u0006e\u0014\u0013!C\u0001\u0003S+\"!a++\u0005q2\u0006\u0002\u00036\u0002z\u0005\u0005I\u0011I6\t\u0011A\fI(!A\u0005\u0002ED\u0011B^A=\u0003\u0003%\t!a-\u0015\u0007a\f)\f\u0003\u0005}\u0003c\u000b\t\u00111\u0001s\u0011!q\u0018\u0011PA\u0001\n\u0003z\bBCA\b\u0003s\n\t\u0011\"\u0001\u0002<R!\u00111CA_\u0011!a\u0018\u0011XA\u0001\u0002\u0004A\bBCA\u000f\u0003s\n\t\u0011\"\u0011\u0002 !Q\u00111EA=\u0003\u0003%\t%!\n\t\u0015\u0005%\u0012\u0011PA\u0001\n\u0003\n)\r\u0006\u0003\u0002\u0014\u0005\u001d\u0007\u0002\u0003?\u0002D\u0006\u0005\t\u0019\u0001=\t\r)\n\t\b1\u0001=\u000f%\timDA\u0001\u0012\u0003\ty-A\u0006SS\u000eD\u0007K]8dKN\u001c\bcA\u0010\u0002R\u001aI\u00111P\b\u0002\u0002#\u0005\u00111[\n\u0006\u0003#\f)N\n\t\b\u0003{\t\u0019\u0005PA<\u0011\u001dI\u0012\u0011\u001bC\u0001\u00033$\"!a4\t\u0015\u0005\r\u0012\u0011[A\u0001\n\u000b\n)\u0003\u0003\u0006\u0002P\u0005E\u0017\u0011!CA\u0003?$B!a\u001e\u0002b\"1!&!8A\u0002qB!\"a\u0016\u0002R\u0006\u0005I\u0011QAs)\u0011\t9/!;\u0011\tM\ti\u0006\u0010\u0005\u000b\u0003G\n\u0019/!AA\u0002\u0005]\u0004BCA4\u0003#\f\t\u0011\"\u0003\u0002j!9\u0011q^\b\u0005\u0004\u0005E\u0018\u0001\u0005;p!J|7-Z:t\u0005VLG\u000eZ3s)\ra\u00131\u001f\u0005\t\u0003k\fi\u000f1\u0001\u0002x\u0006!\u0011M]4t!\u0019\tIP!\u0003\u0003\u00109!\u00111 B\u0003\u001d\u0011\tiPa\u0001\u000e\u0005\u0005}(b\u0001B\u0001\u0019\u00051AH]8pizJ\u0011!F\u0005\u0004\u0005\u000f!\u0012a\u00029bG.\fw-Z\u0005\u0005\u0005\u0017\u0011iAA\u0002TKFT1Aa\u0002\u0015!\u0011\u0011\tBa\u0006\u000f\u0007M\u0011\u0019\"C\u0002\u0003\u0016Q\ta\u0001\u0015:fI\u00164\u0017b\u00018\u0003\u001a)\u0019!Q\u0003\u000b\t\u000f\tuq\u0002\"\u0001\u0003 \u00051A.Y;oG\"$BA!\t\u00038Q!\u0011\u0011\u0013B\u0012\u0011!\tIJa\u0007A\u0002\t\u0015\u0002CC\n\u0003(I\u0014YCa\u000b\u0002\u0012&\u0019!\u0011\u0006\u000b\u0003\u0013\u0019+hn\u0019;j_:\u001c\u0004#B\n\u0003.\tE\u0012b\u0001B\u0018)\t)\u0011I\u001d:bsB\u00191Ca\r\n\u0007\tUBC\u0001\u0003CsR,\u0007\u0002\u0003B\u001d\u00057\u0001\rAa\u000f\u0002\u000f\r|W.\\1oIB)1C!\u0010\u0003\u0010%\u0019!q\b\u000b\u0003\u0015q\u0012X\r]3bi\u0016$g\bC\u0004\u0003\u001e=!\tAa\u0011\u0015\r\t\u0015#\u0011\nB')\u0011\t\tJa\u0012\t\u0011\u0005e%\u0011\ta\u0001\u0005KAqAa\u0013\u0003B\u0001\u0007A&A\u0001q\u0011!Q%\u0011\tI\u0001\u0002\u0004Y\u0005b\u0002B)\u001f\u0011\u0005!1K\u0001\u0007gf\u001cH/Z7\u0015\t\tU#1\f\t\t'\t]#Oa\u000b\u0003,%\u0019!\u0011\f\u000b\u0003\rQ+\b\u000f\\34\u0011!\u0011IDa\u0014A\u0002\tm\u0002b\u0002B)\u001f\u0011\u0005!q\f\u000b\u0007\u0005+\u0012\tGa\u0019\t\u000f\t-#Q\fa\u0001Y!A!J!\u0018\u0011\u0002\u0003\u00071\n\u0003\u0005\u0003h=\t\n\u0011\"\u0001d\u0003A\u0019\u0018p\u001d;f[\u0012\"WMZ1vYR$#\u0007\u0003\u0005\u0003l=\t\n\u0011\"\u0001d\u0003Aa\u0017-\u001e8dQ\u0012\"WMZ1vYR$#\u0007")
public final class ProcessSupport
{
    public static InputStream launch$default$2() {
        return ProcessSupport$.MODULE$.launch$default$2();
    }
    
    public static InputStream system$default$2() {
        return ProcessSupport$.MODULE$.system$default$2();
    }
    
    public static Tuple3<Object, byte[], byte[]> system(final ProcessBuilder p2, final InputStream in) {
        return ProcessSupport$.MODULE$.system(p2, in);
    }
    
    public static Tuple3<Object, byte[], byte[]> system(final Seq<String> command) {
        return ProcessSupport$.MODULE$.system(command);
    }
    
    public static void launch(final ProcessBuilder p3, final InputStream in, final Function3<Object, byte[], byte[], BoxedUnit> func) {
        ProcessSupport$.MODULE$.launch(p3, in, func);
    }
    
    public static void launch(final Seq<String> command, final Function3<Object, byte[], byte[], BoxedUnit> func) {
        ProcessSupport$.MODULE$.launch(command, func);
    }
    
    public static ProcessBuilder toProcessBuilder(final Seq<String> args) {
        return ProcessSupport$.MODULE$.toProcessBuilder(args);
    }
    
    public static RichProcess toRichProcess(final Process self) {
        return ProcessSupport$.MODULE$.toRichProcess(self);
    }
    
    public static RichProcessBuilder toRichProcessBuilder(final ProcessBuilder self) {
        return ProcessSupport$.MODULE$.toRichProcessBuilder(self);
    }
    
    public static class RichProcessBuilder implements Product, Serializable
    {
        private final ProcessBuilder self;
        
        public ProcessBuilder self() {
            return this.self;
        }
        
        public Process start(final OutputStream out, final OutputStream err, final InputStream in) {
            final ProcessBuilder self = this.self();
            boolean redirectErrorStream = false;
            Label_0034: {
                Label_0033: {
                    if (out == null) {
                        if (err != null) {
                            break Label_0033;
                        }
                    }
                    else if (!out.equals(err)) {
                        break Label_0033;
                    }
                    redirectErrorStream = true;
                    break Label_0034;
                }
                redirectErrorStream = false;
            }
            self.redirectErrorStream(redirectErrorStream);
            final Process process = this.self().start();
            if (in == null) {
                process.getOutputStream().close();
            }
            else {
                package$.MODULE$.ExecutorWrapper((Executor)LevelDBClient$.MODULE$.THREAD_POOL()).apply((Function0)new ProcessSupport$RichProcessBuilder$$anonfun$start.ProcessSupport$RichProcessBuilder$$anonfun$start$1(this, in, process));
            }
            if (out == null) {
                process.getInputStream().close();
            }
            else {
                package$.MODULE$.ExecutorWrapper((Executor)LevelDBClient$.MODULE$.THREAD_POOL()).apply((Function0)new ProcessSupport$RichProcessBuilder$$anonfun$start.ProcessSupport$RichProcessBuilder$$anonfun$start$2(this, out, process));
            }
            Label_0158: {
                if (err != null) {
                    if (err == null) {
                        if (out == null) {
                            break Label_0158;
                        }
                    }
                    else if (err.equals(out)) {
                        break Label_0158;
                    }
                    package$.MODULE$.ExecutorWrapper((Executor)LevelDBClient$.MODULE$.THREAD_POOL()).apply((Function0)new ProcessSupport$RichProcessBuilder$$anonfun$start.ProcessSupport$RichProcessBuilder$$anonfun$start$3(this, err, process));
                    return process;
                }
            }
            process.getErrorStream().close();
            return process;
        }
        
        public OutputStream start$default$1() {
            return null;
        }
        
        public OutputStream start$default$2() {
            return null;
        }
        
        public InputStream start$default$3() {
            return null;
        }
        
        public RichProcessBuilder copy(final ProcessBuilder self) {
            return new RichProcessBuilder(self);
        }
        
        public ProcessBuilder copy$default$1() {
            return this.self();
        }
        
        public String productPrefix() {
            return "RichProcessBuilder";
        }
        
        public int productArity() {
            return 1;
        }
        
        public Object productElement(final int x$1) {
            switch (x$1) {
                default: {
                    throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
                }
                case 0: {
                    return this.self();
                }
            }
        }
        
        public Iterator<Object> productIterator() {
            return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
        }
        
        public boolean canEqual(final Object x$1) {
            return x$1 instanceof RichProcessBuilder;
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
            if (this != x$1) {
                if (x$1 instanceof RichProcessBuilder) {
                    final RichProcessBuilder richProcessBuilder = (RichProcessBuilder)x$1;
                    final ProcessBuilder self = this.self();
                    final ProcessBuilder self2 = richProcessBuilder.self();
                    boolean b = false;
                    Label_0077: {
                        Label_0076: {
                            if (self == null) {
                                if (self2 != null) {
                                    break Label_0076;
                                }
                            }
                            else if (!self.equals(self2)) {
                                break Label_0076;
                            }
                            if (richProcessBuilder.canEqual(this)) {
                                b = true;
                                break Label_0077;
                            }
                        }
                        b = false;
                    }
                    if (b) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public RichProcessBuilder(final ProcessBuilder self) {
            this.self = self;
            Product$class.$init$((Product)this);
        }
    }
    
    public static class RichProcessBuilder$ extends AbstractFunction1<ProcessBuilder, RichProcessBuilder> implements Serializable
    {
        public static final RichProcessBuilder$ MODULE$;
        
        static {
            new RichProcessBuilder$();
        }
        
        public final String toString() {
            return "RichProcessBuilder";
        }
        
        public RichProcessBuilder apply(final ProcessBuilder self) {
            return new RichProcessBuilder(self);
        }
        
        public Option<ProcessBuilder> unapply(final RichProcessBuilder x$0) {
            return (Option<ProcessBuilder>)((x$0 == null) ? None$.MODULE$ : new Some((Object)x$0.self()));
        }
        
        private Object readResolve() {
            return RichProcessBuilder$.MODULE$;
        }
        
        public RichProcessBuilder$() {
            MODULE$ = this;
        }
    }
    
    public static class RichProcess implements Product, Serializable
    {
        private final Process self;
        
        public Process self() {
            return this.self;
        }
        
        public void onExit(final Function1<Object, BoxedUnit> func) {
            package$.MODULE$.ExecutorWrapper((Executor)LevelDBClient$.MODULE$.THREAD_POOL()).apply((Function0)new ProcessSupport$RichProcess$$anonfun$onExit.ProcessSupport$RichProcess$$anonfun$onExit$1(this, (Function1)func));
        }
        
        public RichProcess copy(final Process self) {
            return new RichProcess(self);
        }
        
        public Process copy$default$1() {
            return this.self();
        }
        
        public String productPrefix() {
            return "RichProcess";
        }
        
        public int productArity() {
            return 1;
        }
        
        public Object productElement(final int x$1) {
            switch (x$1) {
                default: {
                    throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
                }
                case 0: {
                    return this.self();
                }
            }
        }
        
        public Iterator<Object> productIterator() {
            return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
        }
        
        public boolean canEqual(final Object x$1) {
            return x$1 instanceof RichProcess;
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
            if (this != x$1) {
                if (x$1 instanceof RichProcess) {
                    final RichProcess richProcess = (RichProcess)x$1;
                    final Process self = this.self();
                    final Process self2 = richProcess.self();
                    boolean b = false;
                    Label_0077: {
                        Label_0076: {
                            if (self == null) {
                                if (self2 != null) {
                                    break Label_0076;
                                }
                            }
                            else if (!self.equals(self2)) {
                                break Label_0076;
                            }
                            if (richProcess.canEqual(this)) {
                                b = true;
                                break Label_0077;
                            }
                        }
                        b = false;
                    }
                    if (b) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public RichProcess(final Process self) {
            this.self = self;
            Product$class.$init$((Product)this);
        }
    }
    
    public static class RichProcess$ extends AbstractFunction1<Process, RichProcess> implements Serializable
    {
        public static final RichProcess$ MODULE$;
        
        static {
            new RichProcess$();
        }
        
        public final String toString() {
            return "RichProcess";
        }
        
        public RichProcess apply(final Process self) {
            return new RichProcess(self);
        }
        
        public Option<Process> unapply(final RichProcess x$0) {
            return (Option<Process>)((x$0 == null) ? None$.MODULE$ : new Some((Object)x$0.self()));
        }
        
        private Object readResolve() {
            return RichProcess$.MODULE$;
        }
        
        public RichProcess$() {
            MODULE$ = this;
        }
    }
}
