// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.Some;
import scala.None$;
import scala.Option;
import scala.runtime.AbstractFunction1;
import scala.Product$class;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.immutable.List;
import scala.Function0;
import scala.Option$;
import scala.collection.immutable.StringOps;
import scala.Predef$;
import scala.collection.mutable.StringBuilder;
import java.io.FileInputStream;
import scala.runtime.LongRef;
import java.util.zip.CRC32;
import scala.runtime.BoxesRunTime;
import java.io.FileOutputStream;
import scala.Serializable;
import scala.Product;
import java.io.File;
import java.io.Closeable;
import scala.Function1;
import java.io.InputStream;
import java.io.OutputStream;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\t=v!B\u0001\u0003\u0011\u0003i\u0011a\u0003$jY\u0016\u001cV\u000f\u001d9peRT!a\u0001\u0003\u0002\tU$\u0018\u000e\u001c\u0006\u0003\u000b\u0019\tq\u0001\\3wK2$'M\u0003\u0002\b\u0011\u0005A\u0011m\u0019;jm\u0016l\u0017O\u0003\u0002\n\u0015\u00051\u0011\r]1dQ\u0016T\u0011aC\u0001\u0004_J<7\u0001\u0001\t\u0003\u001d=i\u0011A\u0001\u0004\u0006!\tA\t!\u0005\u0002\f\r&dWmU;qa>\u0014Ho\u0005\u0002\u0010%A\u00111CF\u0007\u0002))\tQ#A\u0003tG\u0006d\u0017-\u0003\u0002\u0018)\t1\u0011I\\=SK\u001aDQ!G\b\u0005\u0002i\ta\u0001P5oSRtD#A\u0007\t\u000bqyA1A\u000f\u0002\u0015Q|'+[2i\r&dW\rF\u0002\u001f\u0003g\u0003\"a\b\u0011\u000e\u0003=1A!I\bAE\tA!+[2i\r&dWm\u0005\u0003!%\r2\u0003CA\n%\u0013\t)CCA\u0004Qe>$Wo\u0019;\u0011\u0005M9\u0013B\u0001\u0015\u0015\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!Q\u0003E!f\u0001\n\u0003Y\u0013\u0001B:fY\u001a,\u0012\u0001\f\t\u0003[Ij\u0011A\f\u0006\u0003_A\n!![8\u000b\u0003E\nAA[1wC&\u00111G\f\u0002\u0005\r&dW\r\u0003\u00056A\tE\t\u0015!\u0003-\u0003\u0015\u0019X\r\u001c4!\u0011\u0015I\u0002\u0005\"\u00018)\tq\u0002\bC\u0003+m\u0001\u0007A\u0006C\u0003;A\u0011\u00051(\u0001\u0003%I&4HC\u0001\u0017=\u0011\u0015i\u0014\b1\u0001?\u0003\u0011\u0001\u0018\r\u001e5\u0011\u0005}\u0012eBA\nA\u0013\t\tE#\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0007\u0012\u0013aa\u0015;sS:<'BA!\u0015\u0011\u00151\u0005\u0005\"\u0001H\u0003\u0019a\u0017N\\6U_R\u0011\u0001j\u0013\t\u0003'%K!A\u0013\u000b\u0003\tUs\u0017\u000e\u001e\u0005\u0006\u0019\u0016\u0003\r\u0001L\u0001\u0007i\u0006\u0014x-\u001a;\t\u000b9\u0003C\u0011A(\u0002\r\r|\u0007/\u001f+p)\t\u00016\u000b\u0005\u0002\u0014#&\u0011!\u000b\u0006\u0002\u0005\u0019>tw\rC\u0003M\u001b\u0002\u0007A\u0006C\u0003VA\u0011\u0005a+A\u0003de\u000e\u001c$\u0007\u0006\u0002Q/\"9\u0001\f\u0016I\u0001\u0002\u0004\u0001\u0016!\u00027j[&$\b\"\u0002.!\t\u0003Y\u0016\u0001D2bG\",GmX2sGN\u0012T#\u0001)\t\u000bu\u0003C\u0011\u00010\u0002\u00151L7\u000f^0gS2,7/F\u0001`!\r\u0019\u0002\rL\u0005\u0003CR\u0011Q!\u0011:sCfDQa\u0019\u0011\u0005\u0002\u0011\fQB]3dkJ\u001c\u0018N^3MSN$X#A3\u0011\u0007\u0019tGF\u0004\u0002hY:\u0011\u0001n[\u0007\u0002S*\u0011!\u000eD\u0001\u0007yI|w\u000e\u001e \n\u0003UI!!\u001c\u000b\u0002\u000fA\f7m[1hK&\u0011q\u000e\u001d\u0002\u0005\u0019&\u001cHO\u0003\u0002n)!)!\u000f\tC\u0001g\u0006y!/Z2veNLg/\u001a#fY\u0016$X-F\u0001I\u0011\u0015)\b\u0005\"\u0001w\u0003=\u0011XmY;sg&4XmQ8qsR{GC\u0001%x\u0011\u0015aE\u000f1\u0001-\u0011\u0015I\b\u0005\"\u0001{\u0003!\u0011X-\u00193UKb$HC\u0001 |\u0011\u001da\b\u0010%AA\u0002y\nqa\u00195beN,G\u000fC\u0003\u007fA\u0011\u0005q0A\u0005sK\u0006$')\u001f;fgV\u0011\u0011\u0011\u0001\t\u0005'\u0001\f\u0019\u0001E\u0002\u0014\u0003\u000bI1!a\u0002\u0015\u0005\u0011\u0011\u0015\u0010^3\t\u000f\u0005-\u0001\u0005\"\u0001\u0002\u000e\u0005QqO]5uK\nKH/Z:\u0015\u0007!\u000by\u0001\u0003\u0005\u0002\u0012\u0005%\u0001\u0019AA\u0001\u0003\u0011!\u0017\r^1\t\u000f\u0005U\u0001\u0005\"\u0001\u0002\u0018\u0005IqO]5uKR+\u0007\u0010\u001e\u000b\u0006\u0011\u0006e\u00111\u0004\u0005\b\u0003#\t\u0019\u00021\u0001?\u0011!a\u00181\u0003I\u0001\u0002\u0004q\u0004\"CA\u0010A\u0005\u0005I\u0011AA\u0011\u0003\u0011\u0019w\u000e]=\u0015\u0007y\t\u0019\u0003\u0003\u0005+\u0003;\u0001\n\u00111\u0001-\u0011%\t9\u0003II\u0001\n\u0003\tI#\u0001\nsK\u0006$G+\u001a=uI\u0011,g-Y;mi\u0012\nTCAA\u0016U\rq\u0014QF\u0016\u0003\u0003_\u0001B!!\r\u0002<5\u0011\u00111\u0007\u0006\u0005\u0003k\t9$A\u0005v]\u000eDWmY6fI*\u0019\u0011\u0011\b\u000b\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0003\u0002>\u0005M\"!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"I\u0011\u0011\t\u0011\u0012\u0002\u0013\u0005\u0011\u0011F\u0001\u0014oJLG/\u001a+fqR$C-\u001a4bk2$HE\r\u0005\n\u0003\u000b\u0002\u0013\u0013!C\u0001\u0003\u000f\nqb\u0019:dgI\"C-\u001a4bk2$H%M\u000b\u0003\u0003\u0013R3\u0001UA\u0017\u0011%\ti\u0005II\u0001\n\u0003\ty%\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0005\u0005E#f\u0001\u0017\u0002.!I\u0011Q\u000b\u0011\u0002\u0002\u0013\u0005\u0013qK\u0001\u000eaJ|G-^2u!J,g-\u001b=\u0016\u0005\u0005e\u0003\u0003BA.\u0003Cj!!!\u0018\u000b\u0007\u0005}\u0003'\u0001\u0003mC:<\u0017bA\"\u0002^!I\u0011Q\r\u0011\u0002\u0002\u0013\u0005\u0011qM\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0003\u0003S\u00022aEA6\u0013\r\ti\u0007\u0006\u0002\u0004\u0013:$\b\"CA9A\u0005\u0005I\u0011AA:\u00039\u0001(o\u001c3vGR,E.Z7f]R$B!!\u001e\u0002|A\u00191#a\u001e\n\u0007\u0005eDCA\u0002B]fD!\"! \u0002p\u0005\u0005\t\u0019AA5\u0003\rAH%\r\u0005\n\u0003\u0003\u0003\u0013\u0011!C!\u0003\u0007\u000bq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003\u000b\u0003b!a\"\u0002\u000e\u0006UTBAAE\u0015\r\tY\tF\u0001\u000bG>dG.Z2uS>t\u0017\u0002BAH\u0003\u0013\u0013\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003'\u0003\u0013\u0011!C\u0001\u0003+\u000b\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0005\u0003/\u000bi\nE\u0002\u0014\u00033K1!a'\u0015\u0005\u001d\u0011un\u001c7fC:D!\"! \u0002\u0012\u0006\u0005\t\u0019AA;\u0011%\t\t\u000bIA\u0001\n\u0003\n\u0019+\u0001\u0005iCND7i\u001c3f)\t\tI\u0007C\u0005\u0002(\u0002\n\t\u0011\"\u0011\u0002*\u0006AAo\\*ue&tw\r\u0006\u0002\u0002Z!I\u0011Q\u0016\u0011\u0002\u0002\u0013\u0005\u0013qV\u0001\u0007KF,\u0018\r\\:\u0015\t\u0005]\u0015\u0011\u0017\u0005\u000b\u0003{\nY+!AA\u0002\u0005U\u0004BBA[7\u0001\u0007A&\u0001\u0003gS2,\u0007\"CA]\u001f\t\u0007I\u0011AA^\u0003%ygnV5oI><8/\u0006\u0002\u0002\u0018\"A\u0011qX\b!\u0002\u0013\t9*\u0001\u0006p]^Kg\u000eZ8xg\u0002B\u0011\"a1\u0010\u0001\u0004%I!a\u001a\u0002\u00191Lgn[*ue\u0006$XmZ=\t\u0013\u0005\u001dw\u00021A\u0005\n\u0005%\u0017\u0001\u00057j].\u001cFO]1uK\u001eLx\fJ3r)\rA\u00151\u001a\u0005\u000b\u0003{\n)-!AA\u0002\u0005%\u0004\u0002CAh\u001f\u0001\u0006K!!\u001b\u0002\u001b1Lgn[*ue\u0006$XmZ=!\u0011%\t\u0019n\u0004b\u0001\n\u0013\t).A\u0002M\u001f\u001e+\"!a6\u0011\u00079\tI.C\u0002\u0002\\\n\u00111\u0001T8h\u0011!\tyn\u0004Q\u0001\n\u0005]\u0017\u0001\u0002'P\u000f\u0002Bq!a9\u0010\t\u0003\t)/\u0001\u0003mS:\\G#\u0002%\u0002h\u0006-\bbBAu\u0003C\u0004\r\u0001L\u0001\u0007g>,(oY3\t\r1\u000b\t\u000f1\u0001-\u0011\u001d\tyo\u0004C\u0001\u0003c\f\u0011b]=ti\u0016lG)\u001b:\u0015\u00071\n\u0019\u0010C\u0004\u0002v\u00065\b\u0019\u0001 \u0002\t9\fW.Z\u0004\n\u0003s|\u0011\u0011!E\u0001\u0003w\f\u0001BU5dQ\u001aKG.\u001a\t\u0004?\u0005uh\u0001C\u0011\u0010\u0003\u0003E\t!a@\u0014\u000b\u0005u(\u0011\u0001\u0014\u0011\r\t\r!\u0011\u0002\u0017\u001f\u001b\t\u0011)AC\u0002\u0003\bQ\tqA];oi&lW-\u0003\u0003\u0003\f\t\u0015!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oc!9\u0011$!@\u0005\u0002\t=ACAA~\u0011)\t9+!@\u0002\u0002\u0013\u0015\u0013\u0011\u0016\u0005\u000b\u0005+\ti0!A\u0005\u0002\n]\u0011!B1qa2LHc\u0001\u0010\u0003\u001a!1!Fa\u0005A\u00021B!B!\b\u0002~\u0006\u0005I\u0011\u0011B\u0010\u0003\u001d)h.\u00199qYf$BA!\t\u0003(A!1Ca\t-\u0013\r\u0011)\u0003\u0006\u0002\u0007\u001fB$\u0018n\u001c8\t\u0013\t%\"1DA\u0001\u0002\u0004q\u0012a\u0001=%a!Q!QFA\u007f\u0003\u0003%IAa\f\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0005c\u0001B!a\u0017\u00034%!!QGA/\u0005\u0019y%M[3di\"9\u0011qD\b\u0005\u0002\teB#\u0002)\u0003<\t\u0015\u0003\u0002\u0003B\u001f\u0005o\u0001\rAa\u0010\u0002\u0005%t\u0007cA\u0017\u0003B%\u0019!1\t\u0018\u0003\u0017%s\u0007/\u001e;TiJ,\u0017-\u001c\u0005\t\u0005\u000f\u00129\u00041\u0001\u0003J\u0005\u0019q.\u001e;\u0011\u00075\u0012Y%C\u0002\u0003N9\u0012AbT;uaV$8\u000b\u001e:fC6DqA!\u0015\u0010\t\u0003\u0011\u0019&A\u0003vg&tw-\u0006\u0004\u0003V\tu#Q\u000f\u000b\u0005\u0005/\u0012\t\t\u0006\u0003\u0003Z\t%\u0004\u0003\u0002B.\u0005;b\u0001\u0001\u0002\u0005\u0003`\t=#\u0019\u0001B1\u0005\u0005\u0011\u0016\u0003\u0002B2\u0003k\u00022a\u0005B3\u0013\r\u00119\u0007\u0006\u0002\b\u001d>$\b.\u001b8h\u0011!\u0011YGa\u0014A\u0002\t5\u0014\u0001\u00029s_\u000e\u0004ra\u0005B8\u0005g\u0012I&C\u0002\u0003rQ\u0011\u0011BR;oGRLwN\\\u0019\u0011\t\tm#Q\u000f\u0003\t\u0005o\u0012yE1\u0001\u0003z\t\t1)\u0005\u0003\u0003d\tm\u0004cA\u0017\u0003~%\u0019!q\u0010\u0018\u0003\u0013\rcwn]3bE2,\u0007\u0002\u0003BB\u0005\u001f\u0002\rAa\u001d\u0002\u0011\rdwn]1cY\u0016Da!_\b\u0005\u0002\t\u001dE#\u0002 \u0003\n\n-\u0005\u0002\u0003B\u001f\u0005\u000b\u0003\rAa\u0010\t\u0011q\u0014)\t%AA\u0002yBaA`\b\u0005\u0002\t=E\u0003BA\u0001\u0005#C\u0001B!\u0010\u0003\u000e\u0002\u0007!q\b\u0005\b\u0003+yA\u0011\u0001BK)\u001dA%q\u0013BM\u0005;C\u0001Ba\u0012\u0003\u0014\u0002\u0007!\u0011\n\u0005\b\u00057\u0013\u0019\n1\u0001?\u0003\u00151\u0018\r\\;f\u0011!a(1\u0013I\u0001\u0002\u0004q\u0004bBA\u0006\u001f\u0011\u0005!\u0011\u0015\u000b\u0006\u0011\n\r&Q\u0015\u0005\t\u0005\u000f\u0012y\n1\u0001\u0003J!A\u0011\u0011\u0003BP\u0001\u0004\t\t\u0001C\u0005\u0003*>\t\n\u0011\"\u0001\u0002*\u0005\u0011\"/Z1e)\u0016DH\u000f\n3fM\u0006,H\u000e\u001e\u00133\u0011%\u0011ikDI\u0001\n\u0003\tI#A\nxe&$X\rV3yi\u0012\"WMZ1vYR$3\u0007")
public final class FileSupport
{
    public static String writeText$default$3() {
        return FileSupport$.MODULE$.writeText$default$3();
    }
    
    public static String readText$default$2() {
        return FileSupport$.MODULE$.readText$default$2();
    }
    
    public static void writeBytes(final OutputStream out, final byte[] data) {
        FileSupport$.MODULE$.writeBytes(out, data);
    }
    
    public static void writeText(final OutputStream out, final String value, final String charset) {
        FileSupport$.MODULE$.writeText(out, value, charset);
    }
    
    public static byte[] readBytes(final InputStream in) {
        return FileSupport$.MODULE$.readBytes(in);
    }
    
    public static String readText(final InputStream in, final String charset) {
        return FileSupport$.MODULE$.readText(in, charset);
    }
    
    public static <R, C extends Closeable> R using(final C closable, final Function1<C, R> proc) {
        return FileSupport$.MODULE$.using(closable, proc);
    }
    
    public static long copy(final InputStream in, final OutputStream out) {
        return FileSupport$.MODULE$.copy(in, out);
    }
    
    public static File systemDir(final String name) {
        return FileSupport$.MODULE$.systemDir(name);
    }
    
    public static void link(final File source, final File target) {
        FileSupport$.MODULE$.link(source, target);
    }
    
    public static boolean onWindows() {
        return FileSupport$.MODULE$.onWindows();
    }
    
    public static RichFile toRichFile(final File file) {
        return FileSupport$.MODULE$.toRichFile(file);
    }
    
    public static class RichFile implements Product, Serializable
    {
        private final File self;
        
        public File self() {
            return this.self;
        }
        
        public File $div(final String path) {
            return new File(this.self(), path);
        }
        
        public void linkTo(final File target) {
            FileSupport$.MODULE$.link(this.self(), target);
        }
        
        public long copyTo(final File target) {
            return BoxesRunTime.unboxToLong(FileSupport$.MODULE$.using(new FileOutputStream(target), (scala.Function1<FileOutputStream, Object>)new FileSupport$RichFile$$anonfun$copyTo.FileSupport$RichFile$$anonfun$copyTo$1(this)));
        }
        
        public long crc32(final long limit) {
            final CRC32 checksum = new CRC32();
            final LongRef remaining = LongRef.create(limit);
            FileSupport$.MODULE$.using(new FileInputStream(this.self()), (scala.Function1<FileInputStream, Object>)new FileSupport$RichFile$$anonfun$crc32.FileSupport$RichFile$$anonfun$crc32$1(this, checksum, remaining));
            return checksum.getValue();
        }
        
        public long crc32$default$1() {
            return Long.MAX_VALUE;
        }
        
        public long cached_crc32() {
            final File crc32_file = new File(this.self().getParentFile(), new StringBuilder().append((Object)this.self().getName()).append((Object)".crc32").toString());
            long long1;
            if (crc32_file.exists() && crc32_file.lastModified() > this.self().lastModified()) {
                final Predef$ module$ = Predef$.MODULE$;
                final RichFile qual$1 = FileSupport$.MODULE$.toRichFile(crc32_file);
                final String x$5 = qual$1.readText$default$1();
                long1 = new StringOps(module$.augmentString(qual$1.readText(x$5).trim())).toLong();
            }
            else {
                final long rc = this.crc32(this.crc32$default$1());
                final RichFile qual$2 = FileSupport$.MODULE$.toRichFile(crc32_file);
                final String x$6 = BoxesRunTime.boxToLong(rc).toString();
                final String x$7 = qual$2.writeText$default$2();
                qual$2.writeText(x$6, x$7);
                long1 = rc;
            }
            return long1;
        }
        
        public File[] list_files() {
            return (File[])Option$.MODULE$.apply((Object)this.self().listFiles()).getOrElse((Function0)new FileSupport$RichFile$$anonfun$list_files.FileSupport$RichFile$$anonfun$list_files$1(this));
        }
        
        public List<File> recursiveList() {
            return (List<File>)(this.self().isDirectory() ? ((List)Predef$.MODULE$.refArrayOps((Object[])this.self().listFiles()).toList().flatten((Function1)new FileSupport$RichFile$$anonfun$recursiveList.FileSupport$RichFile$$anonfun$recursiveList$1(this))).$colon$colon((Object)this.self()) : Nil$.MODULE$.$colon$colon((Object)this.self()));
        }
        
        public void recursiveDelete() {
            if (this.self().exists()) {
                if (this.self().isDirectory()) {
                    Predef$.MODULE$.refArrayOps((Object[])this.self().listFiles()).foreach((Function1)new FileSupport$RichFile$$anonfun$recursiveDelete.FileSupport$RichFile$$anonfun$recursiveDelete$1(this));
                }
                this.self().delete();
            }
        }
        
        public void recursiveCopyTo(final File target) {
            if (this.self().isDirectory()) {
                target.mkdirs();
                Predef$.MODULE$.refArrayOps((Object[])this.self().listFiles()).foreach((Function1)new FileSupport$RichFile$$anonfun$recursiveCopyTo.FileSupport$RichFile$$anonfun$recursiveCopyTo$1(this, target));
            }
            else {
                FileSupport$.MODULE$.toRichFile(this.self()).copyTo(target);
            }
        }
        
        public String readText(final String charset) {
            return FileSupport$.MODULE$.using(new FileInputStream(this.self()), (scala.Function1<FileInputStream, String>)new FileSupport$RichFile$$anonfun$readText.FileSupport$RichFile$$anonfun$readText$1(this, charset));
        }
        
        public String readText$default$1() {
            return "UTF-8";
        }
        
        public byte[] readBytes() {
            return FileSupport$.MODULE$.using(new FileInputStream(this.self()), (scala.Function1<FileInputStream, byte[]>)new FileSupport$RichFile$$anonfun$readBytes.FileSupport$RichFile$$anonfun$readBytes$1(this));
        }
        
        public void writeBytes(final byte[] data) {
            FileSupport$.MODULE$.using(new FileOutputStream(this.self()), (scala.Function1<FileOutputStream, Object>)new FileSupport$RichFile$$anonfun$writeBytes.FileSupport$RichFile$$anonfun$writeBytes$1(this, data));
        }
        
        public void writeText(final String data, final String charset) {
            FileSupport$.MODULE$.using(new FileOutputStream(this.self()), (scala.Function1<FileOutputStream, Object>)new FileSupport$RichFile$$anonfun$writeText.FileSupport$RichFile$$anonfun$writeText$1(this, data, charset));
        }
        
        public String writeText$default$2() {
            return "UTF-8";
        }
        
        public RichFile copy(final File self) {
            return new RichFile(self);
        }
        
        public File copy$default$1() {
            return this.self();
        }
        
        public String productPrefix() {
            return "RichFile";
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
            return x$1 instanceof RichFile;
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
                if (x$1 instanceof RichFile) {
                    final RichFile richFile = (RichFile)x$1;
                    final File self = this.self();
                    final File self2 = richFile.self();
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
                            if (richFile.canEqual(this)) {
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
        
        public RichFile(final File self) {
            this.self = self;
            Product$class.$init$((Product)this);
        }
    }
    
    public static class RichFile$ extends AbstractFunction1<File, RichFile> implements Serializable
    {
        public static final RichFile$ MODULE$;
        
        static {
            new RichFile$();
        }
        
        public final String toString() {
            return "RichFile";
        }
        
        public RichFile apply(final File self) {
            return new RichFile(self);
        }
        
        public Option<File> unapply(final RichFile x$0) {
            return (Option<File>)((x$0 == null) ? None$.MODULE$ : new Some((Object)x$0.self()));
        }
        
        private Object readResolve() {
            return RichFile$.MODULE$;
        }
        
        public RichFile$() {
            MODULE$ = this;
        }
    }
}
