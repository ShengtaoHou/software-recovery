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
import scala.Tuple2;
import scala.Option;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u00055b\u0001B\u0001\u0003\u0001.\u0011AbU;c\u0003\u000e\\'+Z2pe\u0012T!a\u0001\u0003\u0002\u000f1,g/\u001a7eE*\u0011QAB\u0001\tC\u000e$\u0018N^3nc*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019B\u0001\u0001\u0007\u0013+A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"!D\n\n\u0005Qq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001bYI!a\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011e\u0001!Q3A\u0005\u0002i\taa];c\u0017\u0016LX#A\u000e\u0011\u00055a\u0012BA\u000f\u000f\u0005\u0011auN\\4\t\u0011}\u0001!\u0011#Q\u0001\nm\tqa];c\u0017\u0016L\b\u0005\u0003\u0005\"\u0001\tU\r\u0011\"\u0001\u001b\u0003-\t7m\u001b)pg&$\u0018n\u001c8\t\u0011\r\u0002!\u0011#Q\u0001\nm\tA\"Y2l!>\u001c\u0018\u000e^5p]\u0002BQ!\n\u0001\u0005\u0002\u0019\na\u0001P5oSRtDcA\u0014*UA\u0011\u0001\u0006A\u0007\u0002\u0005!)\u0011\u0004\na\u00017!)\u0011\u0005\na\u00017!9A\u0006AA\u0001\n\u0003i\u0013\u0001B2paf$2a\n\u00180\u0011\u001dI2\u0006%AA\u0002mAq!I\u0016\u0011\u0002\u0003\u00071\u0004C\u00042\u0001E\u0005I\u0011\u0001\u001a\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t1G\u000b\u0002\u001ci-\nQ\u0007\u0005\u00027w5\tqG\u0003\u00029s\u0005IQO\\2iK\u000e\\W\r\u001a\u0006\u0003u9\t!\"\u00198o_R\fG/[8o\u0013\tatGA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016DqA\u0010\u0001\u0012\u0002\u0013\u0005!'\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000f\u0001\u0003\u0011\u0011!C!\u0003\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012A\u0011\t\u0003\u0007\"k\u0011\u0001\u0012\u0006\u0003\u000b\u001a\u000bA\u0001\\1oO*\tq)\u0001\u0003kCZ\f\u0017BA%E\u0005\u0019\u0019FO]5oO\"91\nAA\u0001\n\u0003a\u0015\u0001\u00049s_\u0012,8\r^!sSRLX#A'\u0011\u00055q\u0015BA(\u000f\u0005\rIe\u000e\u001e\u0005\b#\u0002\t\t\u0011\"\u0001S\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\u0015,\u0011\u00055!\u0016BA+\u000f\u0005\r\te.\u001f\u0005\b/B\u000b\t\u00111\u0001N\u0003\rAH%\r\u0005\b3\u0002\t\t\u0011\"\u0011[\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A.\u0011\u0007q{6+D\u0001^\u0015\tqf\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001Y/\u0003\u0011%#XM]1u_JDqA\u0019\u0001\u0002\u0002\u0013\u00051-\u0001\u0005dC:,\u0015/^1m)\t!w\r\u0005\u0002\u000eK&\u0011aM\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d9\u0016-!AA\u0002MCq!\u001b\u0001\u0002\u0002\u0013\u0005#.\u0001\u0005iCND7i\u001c3f)\u0005i\u0005b\u00027\u0001\u0003\u0003%\t%\\\u0001\ti>\u001cFO]5oOR\t!\tC\u0004p\u0001\u0005\u0005I\u0011\t9\u0002\r\u0015\fX/\u00197t)\t!\u0017\u000fC\u0004X]\u0006\u0005\t\u0019A*\b\u000fM\u0014\u0011\u0011!E\u0001i\u0006a1+\u001e2BG.\u0014VmY8sIB\u0011\u0001&\u001e\u0004\b\u0003\t\t\t\u0011#\u0001w'\r)x/\u0006\t\u0006qn\\2dJ\u0007\u0002s*\u0011!PD\u0001\beVtG/[7f\u0013\ta\u0018PA\tBEN$(/Y2u\rVt7\r^5p]JBQ!J;\u0005\u0002y$\u0012\u0001\u001e\u0005\bYV\f\t\u0011\"\u0012n\u0011%\t\u0019!^A\u0001\n\u0003\u000b)!A\u0003baBd\u0017\u0010F\u0003(\u0003\u000f\tI\u0001\u0003\u0004\u001a\u0003\u0003\u0001\ra\u0007\u0005\u0007C\u0005\u0005\u0001\u0019A\u000e\t\u0013\u00055Q/!A\u0005\u0002\u0006=\u0011aB;oCB\u0004H.\u001f\u000b\u0005\u0003#\ti\u0002E\u0003\u000e\u0003'\t9\"C\u0002\u0002\u00169\u0011aa\u00149uS>t\u0007#B\u0007\u0002\u001amY\u0012bAA\u000e\u001d\t1A+\u001e9mKJB\u0011\"a\b\u0002\f\u0005\u0005\t\u0019A\u0014\u0002\u0007a$\u0003\u0007C\u0005\u0002$U\f\t\u0011\"\u0003\u0002&\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t9\u0003E\u0002D\u0003SI1!a\u000bE\u0005\u0019y%M[3di\u0002")
public class SubAckRecord implements Product, Serializable
{
    private final long subKey;
    private final long ackPosition;
    
    public static Option<Tuple2<Object, Object>> unapply(final SubAckRecord x$0) {
        return SubAckRecord$.MODULE$.unapply(x$0);
    }
    
    public static SubAckRecord apply(final long subKey, final long ackPosition) {
        return SubAckRecord$.MODULE$.apply(subKey, ackPosition);
    }
    
    public static Function1<Tuple2<Object, Object>, SubAckRecord> tupled() {
        return (Function1<Tuple2<Object, Object>, SubAckRecord>)SubAckRecord$.MODULE$.tupled();
    }
    
    public static Function1<Object, Function1<Object, SubAckRecord>> curried() {
        return (Function1<Object, Function1<Object, SubAckRecord>>)SubAckRecord$.MODULE$.curried();
    }
    
    public long subKey() {
        return this.subKey;
    }
    
    public long ackPosition() {
        return this.ackPosition;
    }
    
    public SubAckRecord copy(final long subKey, final long ackPosition) {
        return new SubAckRecord(subKey, ackPosition);
    }
    
    public long copy$default$1() {
        return this.subKey();
    }
    
    public long copy$default$2() {
        return this.ackPosition();
    }
    
    public String productPrefix() {
        return "SubAckRecord";
    }
    
    public int productArity() {
        return 2;
    }
    
    public Object productElement(final int x$1) {
        Long n = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 1: {
                n = BoxesRunTime.boxToLong(this.ackPosition());
                break;
            }
            case 0: {
                n = BoxesRunTime.boxToLong(this.subKey());
                break;
            }
        }
        return n;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof SubAckRecord;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(-889275714, Statics.longHash(this.subKey())), Statics.longHash(this.ackPosition())), 2);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof SubAckRecord) {
                final SubAckRecord subAckRecord = (SubAckRecord)x$1;
                if (this.subKey() == subAckRecord.subKey() && this.ackPosition() == subAckRecord.ackPosition() && subAckRecord.canEqual(this)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public SubAckRecord(final long subKey, final long ackPosition) {
        this.subKey = subKey;
        this.ackPosition = ackPosition;
        Product$class.$init$((Product)this);
    }
}
