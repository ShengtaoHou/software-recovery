// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Product$class;
import scala.runtime.Statics;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import scala.Tuple4;
import scala.Option;
import org.apache.activemq.command.MessageId;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005=d\u0001B\u0001\u0003\u0001.\u0011\u0001#U;fk\u0016,e\u000e\u001e:z%\u0016\u001cwN\u001d3\u000b\u0005\r!\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000b\u0019\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\t\u0001a!#\u0006\t\u0003\u001bAi\u0011A\u0004\u0006\u0002\u001f\u0005)1oY1mC&\u0011\u0011C\u0004\u0002\u0007\u0003:L(+\u001a4\u0011\u00055\u0019\u0012B\u0001\u000b\u000f\u0005\u001d\u0001&o\u001c3vGR\u0004\"!\u0004\f\n\u0005]q!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002C\r\u0001\u0005+\u0007I\u0011\u0001\u000e\u0002\u0005%$W#A\u000e\u0011\u0005qyR\"A\u000f\u000b\u0005y!\u0011aB2p[6\fg\u000eZ\u0005\u0003Au\u0011\u0011\"T3tg\u0006<W-\u00133\t\u0011\t\u0002!\u0011#Q\u0001\nm\t1!\u001b3!\u0011!!\u0003A!f\u0001\n\u0003)\u0013\u0001C9vKV,7*Z=\u0016\u0003\u0019\u0002\"!D\u0014\n\u0005!r!\u0001\u0002'p]\u001eD\u0001B\u000b\u0001\u0003\u0012\u0003\u0006IAJ\u0001\ncV,W/Z&fs\u0002B\u0001\u0002\f\u0001\u0003\u0016\u0004%\t!J\u0001\tcV,W/Z*fc\"Aa\u0006\u0001B\tB\u0003%a%A\u0005rk\u0016,XmU3rA!A\u0001\u0007\u0001BK\u0002\u0013\u0005\u0011'\u0001\u0006eK2Lg/\u001a:jKN,\u0012A\r\t\u0003\u001bMJ!\u0001\u000e\b\u0003\u0007%sG\u000f\u0003\u00057\u0001\tE\t\u0015!\u00033\u0003-!W\r\\5wKJLWm\u001d\u0011\t\u000ba\u0002A\u0011A\u001d\u0002\rqJg.\u001b;?)\u0015QD(\u0010 @!\tY\u0004!D\u0001\u0003\u0011\u0015Ir\u00071\u0001\u001c\u0011\u0015!s\u00071\u0001'\u0011\u0015as\u00071\u0001'\u0011\u001d\u0001t\u0007%AA\u0002IBq!\u0011\u0001\u0002\u0002\u0013\u0005!)\u0001\u0003d_BLH#\u0002\u001eD\t\u00163\u0005bB\rA!\u0003\u0005\ra\u0007\u0005\bI\u0001\u0003\n\u00111\u0001'\u0011\u001da\u0003\t%AA\u0002\u0019Bq\u0001\r!\u0011\u0002\u0003\u0007!\u0007C\u0004I\u0001E\u0005I\u0011A%\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t!J\u000b\u0002\u001c\u0017.\nA\n\u0005\u0002N%6\taJ\u0003\u0002P!\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003#:\t!\"\u00198o_R\fG/[8o\u0013\t\u0019fJA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq!\u0016\u0001\u0012\u0002\u0013\u0005a+\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003]S#AJ&\t\u000fe\u0003\u0011\u0013!C\u0001-\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001a\u0004bB.\u0001#\u0003%\t\u0001X\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00135+\u0005i&F\u0001\u001aL\u0011\u001dy\u0006!!A\u0005B\u0001\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A1\u0011\u0005\t<W\"A2\u000b\u0005\u0011,\u0017\u0001\u00027b]\u001eT\u0011AZ\u0001\u0005U\u00064\u0018-\u0003\u0002iG\n11\u000b\u001e:j]\u001eDqA\u001b\u0001\u0002\u0002\u0013\u0005\u0011'\u0001\u0007qe>$Wo\u0019;Be&$\u0018\u0010C\u0004m\u0001\u0005\u0005I\u0011A7\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011a.\u001d\t\u0003\u001b=L!\u0001\u001d\b\u0003\u0007\u0005s\u0017\u0010C\u0004sW\u0006\u0005\t\u0019\u0001\u001a\u0002\u0007a$\u0013\u0007C\u0004u\u0001\u0005\u0005I\u0011I;\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012A\u001e\t\u0004ojtW\"\u0001=\u000b\u0005et\u0011AC2pY2,7\r^5p]&\u00111\u0010\u001f\u0002\t\u0013R,'/\u0019;pe\"9Q\u0010AA\u0001\n\u0003q\u0018\u0001C2b]\u0016\u000bX/\u00197\u0015\u0007}\f)\u0001E\u0002\u000e\u0003\u0003I1!a\u0001\u000f\u0005\u001d\u0011un\u001c7fC:DqA\u001d?\u0002\u0002\u0003\u0007a\u000eC\u0005\u0002\n\u0001\t\t\u0011\"\u0011\u0002\f\u0005A\u0001.Y:i\u0007>$W\rF\u00013\u0011%\ty\u0001AA\u0001\n\u0003\n\t\"\u0001\u0005u_N#(/\u001b8h)\u0005\t\u0007\"CA\u000b\u0001\u0005\u0005I\u0011IA\f\u0003\u0019)\u0017/^1mgR\u0019q0!\u0007\t\u0011I\f\u0019\"!AA\u00029<\u0011\"!\b\u0003\u0003\u0003E\t!a\b\u0002!E+X-^3F]R\u0014\u0018PU3d_J$\u0007cA\u001e\u0002\"\u0019A\u0011AAA\u0001\u0012\u0003\t\u0019cE\u0003\u0002\"\u0005\u0015R\u0003E\u0005\u0002(\u000552D\n\u00143u5\u0011\u0011\u0011\u0006\u0006\u0004\u0003Wq\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003_\tICA\tBEN$(/Y2u\rVt7\r^5p]RBq\u0001OA\u0011\t\u0003\t\u0019\u0004\u0006\u0002\u0002 !Q\u0011qBA\u0011\u0003\u0003%)%!\u0005\t\u0015\u0005e\u0012\u0011EA\u0001\n\u0003\u000bY$A\u0003baBd\u0017\u0010F\u0005;\u0003{\ty$!\u0011\u0002D!1\u0011$a\u000eA\u0002mAa\u0001JA\u001c\u0001\u00041\u0003B\u0002\u0017\u00028\u0001\u0007a\u0005\u0003\u00051\u0003o\u0001\n\u00111\u00013\u0011)\t9%!\t\u0002\u0002\u0013\u0005\u0015\u0011J\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\tY%a\u0016\u0011\u000b5\ti%!\u0015\n\u0007\u0005=cB\u0001\u0004PaRLwN\u001c\t\b\u001b\u0005M3D\n\u00143\u0013\r\t)F\u0004\u0002\u0007)V\u0004H.\u001a\u001b\t\u0013\u0005e\u0013QIA\u0001\u0002\u0004Q\u0014a\u0001=%a!I\u0011QLA\u0011#\u0003%\t\u0001X\u0001\u001cI1,7o]5oSR$sM]3bi\u0016\u0014H\u0005Z3gCVdG\u000f\n\u001b\t\u0013\u0005\u0005\u0014\u0011EI\u0001\n\u0003a\u0016aD1qa2LH\u0005Z3gCVdG\u000f\n\u001b\t\u0015\u0005\u0015\u0014\u0011EA\u0001\n\u0013\t9'A\u0006sK\u0006$'+Z:pYZ,GCAA5!\r\u0011\u00171N\u0005\u0004\u0003[\u001a'AB(cU\u0016\u001cG\u000f")
public class QueueEntryRecord implements Product, Serializable
{
    private final MessageId id;
    private final long queueKey;
    private final long queueSeq;
    private final int deliveries;
    
    public static int apply$default$4() {
        return QueueEntryRecord$.MODULE$.apply$default$4();
    }
    
    public static int $lessinit$greater$default$4() {
        return QueueEntryRecord$.MODULE$.$lessinit$greater$default$4();
    }
    
    public static Option<Tuple4<MessageId, Object, Object, Object>> unapply(final QueueEntryRecord x$0) {
        return QueueEntryRecord$.MODULE$.unapply(x$0);
    }
    
    public static QueueEntryRecord apply(final MessageId id, final long queueKey, final long queueSeq, final int deliveries) {
        return QueueEntryRecord$.MODULE$.apply(id, queueKey, queueSeq, deliveries);
    }
    
    public static Function1<Tuple4<MessageId, Object, Object, Object>, QueueEntryRecord> tupled() {
        return (Function1<Tuple4<MessageId, Object, Object, Object>, QueueEntryRecord>)QueueEntryRecord$.MODULE$.tupled();
    }
    
    public static Function1<MessageId, Function1<Object, Function1<Object, Function1<Object, QueueEntryRecord>>>> curried() {
        return (Function1<MessageId, Function1<Object, Function1<Object, Function1<Object, QueueEntryRecord>>>>)QueueEntryRecord$.MODULE$.curried();
    }
    
    public MessageId id() {
        return this.id;
    }
    
    public long queueKey() {
        return this.queueKey;
    }
    
    public long queueSeq() {
        return this.queueSeq;
    }
    
    public int deliveries() {
        return this.deliveries;
    }
    
    public QueueEntryRecord copy(final MessageId id, final long queueKey, final long queueSeq, final int deliveries) {
        return new QueueEntryRecord(id, queueKey, queueSeq, deliveries);
    }
    
    public MessageId copy$default$1() {
        return this.id();
    }
    
    public long copy$default$2() {
        return this.queueKey();
    }
    
    public long copy$default$3() {
        return this.queueSeq();
    }
    
    public int copy$default$4() {
        return this.deliveries();
    }
    
    public String productPrefix() {
        return "QueueEntryRecord";
    }
    
    public int productArity() {
        return 4;
    }
    
    public Object productElement(final int x$1) {
        Object o = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 3: {
                o = BoxesRunTime.boxToInteger(this.deliveries());
                break;
            }
            case 2: {
                o = BoxesRunTime.boxToLong(this.queueSeq());
                break;
            }
            case 1: {
                o = BoxesRunTime.boxToLong(this.queueKey());
                break;
            }
            case 0: {
                o = this.id();
                break;
            }
        }
        return o;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof QueueEntryRecord;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.id())), Statics.longHash(this.queueKey())), Statics.longHash(this.queueSeq())), this.deliveries()), 4);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof QueueEntryRecord) {
                final QueueEntryRecord queueEntryRecord = (QueueEntryRecord)x$1;
                final MessageId id = this.id();
                final MessageId id2 = queueEntryRecord.id();
                boolean b = false;
                Label_0115: {
                    Label_0114: {
                        if (id == null) {
                            if (id2 != null) {
                                break Label_0114;
                            }
                        }
                        else if (!id.equals(id2)) {
                            break Label_0114;
                        }
                        if (this.queueKey() == queueEntryRecord.queueKey() && this.queueSeq() == queueEntryRecord.queueSeq() && this.deliveries() == queueEntryRecord.deliveries() && queueEntryRecord.canEqual(this)) {
                            b = true;
                            break Label_0115;
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
    
    public QueueEntryRecord(final MessageId id, final long queueKey, final long queueSeq, final int deliveries) {
        this.id = id;
        this.queueKey = queueKey;
        this.queueSeq = queueSeq;
        this.deliveries = deliveries;
        Product$class.$init$((Product)this);
    }
}
