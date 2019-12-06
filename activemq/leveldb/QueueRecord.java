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
import org.apache.activemq.command.ActiveMQDestination;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005}b\u0001B\u0001\u0003\u0001.\u00111\"U;fk\u0016\u0014VmY8sI*\u00111\u0001B\u0001\bY\u00164X\r\u001c3c\u0015\t)a!\u0001\u0005bGRLg/Z7r\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\n\u0016!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u0011QbE\u0005\u0003)9\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e-%\u0011qC\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00015\u0005\u0011\u0011\u000eZ\u000b\u00027A\u0011AdH\u0007\u0002;)\u0011a\u0004B\u0001\bG>lW.\u00198e\u0013\t\u0001SDA\nBGRLg/Z'R\t\u0016\u001cH/\u001b8bi&|g\u000e\u0003\u0005#\u0001\tE\t\u0015!\u0003\u001c\u0003\rIG\r\t\u0005\tI\u0001\u0011)\u001a!C\u0001K\u0005I\u0011/^3vK~[W-_\u000b\u0002MA\u0011QbJ\u0005\u0003Q9\u0011A\u0001T8oO\"A!\u0006\u0001B\tB\u0003%a%\u0001\u0006rk\u0016,XmX6fs\u0002BQ\u0001\f\u0001\u0005\u00025\na\u0001P5oSRtDc\u0001\u00181cA\u0011q\u0006A\u0007\u0002\u0005!)\u0011d\u000ba\u00017!)Ae\u000ba\u0001M!91\u0007AA\u0001\n\u0003!\u0014\u0001B2paf$2AL\u001b7\u0011\u001dI\"\u0007%AA\u0002mAq\u0001\n\u001a\u0011\u0002\u0003\u0007a\u0005C\u00049\u0001E\u0005I\u0011A\u001d\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\t!H\u000b\u0002\u001cw-\nA\b\u0005\u0002>\u00056\taH\u0003\u0002@\u0001\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003\u0003:\t!\"\u00198o_R\fG/[8o\u0013\t\u0019eHA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq!\u0012\u0001\u0012\u0002\u0013\u0005a)\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003\u001dS#AJ\u001e\t\u000f%\u0003\u0011\u0011!C!\u0015\u0006i\u0001O]8ek\u000e$\bK]3gSb,\u0012a\u0013\t\u0003\u0019Fk\u0011!\u0014\u0006\u0003\u001d>\u000bA\u0001\\1oO*\t\u0001+\u0001\u0003kCZ\f\u0017B\u0001*N\u0005\u0019\u0019FO]5oO\"9A\u000bAA\u0001\n\u0003)\u0016\u0001\u00049s_\u0012,8\r^!sSRLX#\u0001,\u0011\u000559\u0016B\u0001-\u000f\u0005\rIe\u000e\u001e\u0005\b5\u0002\t\t\u0011\"\u0001\\\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001X0\u0011\u00055i\u0016B\u00010\u000f\u0005\r\te.\u001f\u0005\bAf\u000b\t\u00111\u0001W\u0003\rAH%\r\u0005\bE\u0002\t\t\u0011\"\u0011d\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u00013\u0011\u0007\u0015DG,D\u0001g\u0015\t9g\"\u0001\u0006d_2dWm\u0019;j_:L!!\u001b4\u0003\u0011%#XM]1u_JDqa\u001b\u0001\u0002\u0002\u0013\u0005A.\u0001\u0005dC:,\u0015/^1m)\ti\u0007\u000f\u0005\u0002\u000e]&\u0011qN\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d\u0001'.!AA\u0002qCqA\u001d\u0001\u0002\u0002\u0013\u00053/\u0001\u0005iCND7i\u001c3f)\u00051\u0006bB;\u0001\u0003\u0003%\tE^\u0001\ti>\u001cFO]5oOR\t1\nC\u0004y\u0001\u0005\u0005I\u0011I=\u0002\r\u0015\fX/\u00197t)\ti'\u0010C\u0004ao\u0006\u0005\t\u0019\u0001/\b\u000fq\u0014\u0011\u0011!E\u0001{\u0006Y\u0011+^3vKJ+7m\u001c:e!\tycPB\u0004\u0002\u0005\u0005\u0005\t\u0012A@\u0014\ty\f\t!\u0006\t\b\u0003\u0007\tIa\u0007\u0014/\u001b\t\t)AC\u0002\u0002\b9\tqA];oi&lW-\u0003\u0003\u0002\f\u0005\u0015!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!1AF C\u0001\u0003\u001f!\u0012! \u0005\bkz\f\t\u0011\"\u0012w\u0011%\t)B`A\u0001\n\u0003\u000b9\"A\u0003baBd\u0017\u0010F\u0003/\u00033\tY\u0002\u0003\u0004\u001a\u0003'\u0001\ra\u0007\u0005\u0007I\u0005M\u0001\u0019\u0001\u0014\t\u0013\u0005}a0!A\u0005\u0002\u0006\u0005\u0012aB;oCB\u0004H.\u001f\u000b\u0005\u0003G\ty\u0003E\u0003\u000e\u0003K\tI#C\u0002\u0002(9\u0011aa\u00149uS>t\u0007#B\u0007\u0002,m1\u0013bAA\u0017\u001d\t1A+\u001e9mKJB\u0011\"!\r\u0002\u001e\u0005\u0005\t\u0019\u0001\u0018\u0002\u0007a$\u0003\u0007C\u0005\u00026y\f\t\u0011\"\u0003\u00028\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\tI\u0004E\u0002M\u0003wI1!!\u0010N\u0005\u0019y%M[3di\u0002")
public class QueueRecord implements Product, Serializable
{
    private final ActiveMQDestination id;
    private final long queue_key;
    
    public static Option<Tuple2<ActiveMQDestination, Object>> unapply(final QueueRecord x$0) {
        return QueueRecord$.MODULE$.unapply(x$0);
    }
    
    public static QueueRecord apply(final ActiveMQDestination id, final long queue_key) {
        return QueueRecord$.MODULE$.apply(id, queue_key);
    }
    
    public static Function1<Tuple2<ActiveMQDestination, Object>, QueueRecord> tupled() {
        return (Function1<Tuple2<ActiveMQDestination, Object>, QueueRecord>)QueueRecord$.MODULE$.tupled();
    }
    
    public static Function1<ActiveMQDestination, Function1<Object, QueueRecord>> curried() {
        return (Function1<ActiveMQDestination, Function1<Object, QueueRecord>>)QueueRecord$.MODULE$.curried();
    }
    
    public ActiveMQDestination id() {
        return this.id;
    }
    
    public long queue_key() {
        return this.queue_key;
    }
    
    public QueueRecord copy(final ActiveMQDestination id, final long queue_key) {
        return new QueueRecord(id, queue_key);
    }
    
    public ActiveMQDestination copy$default$1() {
        return this.id();
    }
    
    public long copy$default$2() {
        return this.queue_key();
    }
    
    public String productPrefix() {
        return "QueueRecord";
    }
    
    public int productArity() {
        return 2;
    }
    
    public Object productElement(final int x$1) {
        java.io.Serializable s = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 1: {
                s = BoxesRunTime.boxToLong(this.queue_key());
                break;
            }
            case 0: {
                s = this.id();
                break;
            }
        }
        return s;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof QueueRecord;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.id())), Statics.longHash(this.queue_key())), 2);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof QueueRecord) {
                final QueueRecord queueRecord = (QueueRecord)x$1;
                final ActiveMQDestination id = this.id();
                final ActiveMQDestination id2 = queueRecord.id();
                boolean b = false;
                Label_0090: {
                    Label_0089: {
                        if (id == null) {
                            if (id2 != null) {
                                break Label_0089;
                            }
                        }
                        else if (!id.equals(id2)) {
                            break Label_0089;
                        }
                        if (this.queue_key() == queueRecord.queue_key() && queueRecord.canEqual(this)) {
                            b = true;
                            break Label_0090;
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
    
    public QueueRecord(final ActiveMQDestination id, final long queue_key) {
        this.id = id;
        this.queue_key = queue_key;
        Product$class.$init$((Product)this);
    }
}
