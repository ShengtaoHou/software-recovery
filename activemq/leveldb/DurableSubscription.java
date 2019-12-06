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
import scala.Tuple3;
import scala.Option;
import org.apache.activemq.command.SubscriptionInfo;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005\u001de\u0001B\u0001\u0003\u0001.\u00111\u0003R;sC\ndWmU;cg\u000e\u0014\u0018\u000e\u001d;j_:T!a\u0001\u0003\u0002\u000f1,g/\u001a7eE*\u0011QAB\u0001\tC\u000e$\u0018N^3nc*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019B\u0001\u0001\u0007\u0013+A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"!D\n\n\u0005Qq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001bYI!a\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011e\u0001!Q3A\u0005\u0002i\taa];c\u0017\u0016LX#A\u000e\u0011\u00055a\u0012BA\u000f\u000f\u0005\u0011auN\\4\t\u0011}\u0001!\u0011#Q\u0001\nm\tqa];c\u0017\u0016L\b\u0005\u0003\u0005\"\u0001\tU\r\u0011\"\u0001\u001b\u0003!!x\u000e]5d\u0017\u0016L\b\u0002C\u0012\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\u0013Q|\u0007/[2LKf\u0004\u0003\u0002C\u0013\u0001\u0005+\u0007I\u0011\u0001\u0014\u0002\t%tgm\\\u000b\u0002OA\u0011\u0001fK\u0007\u0002S)\u0011!\u0006B\u0001\bG>lW.\u00198e\u0013\ta\u0013F\u0001\tTk\n\u001c8M]5qi&|g.\u00138g_\"Aa\u0006\u0001B\tB\u0003%q%A\u0003j]\u001a|\u0007\u0005C\u00031\u0001\u0011\u0005\u0011'\u0001\u0004=S:LGO\u0010\u000b\u0005eQ*d\u0007\u0005\u00024\u00015\t!\u0001C\u0003\u001a_\u0001\u00071\u0004C\u0003\"_\u0001\u00071\u0004C\u0003&_\u0001\u0007q\u0005C\u00049\u0001\u0001\u0007I\u0011\u0001\u000e\u0002\u0015\u001d\u001c\u0007k\\:ji&|g\u000eC\u0004;\u0001\u0001\u0007I\u0011A\u001e\u0002\u001d\u001d\u001c\u0007k\\:ji&|gn\u0018\u0013fcR\u0011Ah\u0010\t\u0003\u001buJ!A\u0010\b\u0003\tUs\u0017\u000e\u001e\u0005\b\u0001f\n\t\u00111\u0001\u001c\u0003\rAH%\r\u0005\u0007\u0005\u0002\u0001\u000b\u0015B\u000e\u0002\u0017\u001d\u001c\u0007k\\:ji&|g\u000e\t\u0005\b\t\u0002\u0001\r\u0011\"\u0001\u001b\u0003=a\u0017m\u001d;BG.\u0004vn]5uS>t\u0007b\u0002$\u0001\u0001\u0004%\taR\u0001\u0014Y\u0006\u001cH/Q2l!>\u001c\u0018\u000e^5p]~#S-\u001d\u000b\u0003y!Cq\u0001Q#\u0002\u0002\u0003\u00071\u0004\u0003\u0004K\u0001\u0001\u0006KaG\u0001\u0011Y\u0006\u001cH/Q2l!>\u001c\u0018\u000e^5p]\u0002Bq\u0001\u0014\u0001A\u0002\u0013\u0005!$\u0001\bdkJ\u001cxN\u001d)pg&$\u0018n\u001c8\t\u000f9\u0003\u0001\u0019!C\u0001\u001f\u0006\u00112-\u001e:t_J\u0004vn]5uS>tw\fJ3r)\ta\u0004\u000bC\u0004A\u001b\u0006\u0005\t\u0019A\u000e\t\rI\u0003\u0001\u0015)\u0003\u001c\u0003=\u0019WO]:peB{7/\u001b;j_:\u0004\u0003b\u0002+\u0001\u0003\u0003%\t!V\u0001\u0005G>\u0004\u0018\u0010\u0006\u00033-^C\u0006bB\rT!\u0003\u0005\ra\u0007\u0005\bCM\u0003\n\u00111\u0001\u001c\u0011\u001d)3\u000b%AA\u0002\u001dBqA\u0017\u0001\u0012\u0002\u0013\u00051,\u0001\bd_BLH\u0005Z3gCVdG\u000fJ\u0019\u0016\u0003qS#aG/,\u0003y\u0003\"a\u00183\u000e\u0003\u0001T!!\u00192\u0002\u0013Ut7\r[3dW\u0016$'BA2\u000f\u0003)\tgN\\8uCRLwN\\\u0005\u0003K\u0002\u0014\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0011\u001d9\u0007!%A\u0005\u0002m\u000babY8qs\u0012\"WMZ1vYR$#\u0007C\u0004j\u0001E\u0005I\u0011\u00016\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%gU\t1N\u000b\u0002(;\"9Q\u000eAA\u0001\n\u0003r\u0017!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001p!\t\u0001X/D\u0001r\u0015\t\u00118/\u0001\u0003mC:<'\"\u0001;\u0002\t)\fg/Y\u0005\u0003mF\u0014aa\u0015;sS:<\u0007b\u0002=\u0001\u0003\u0003%\t!_\u0001\raJ|G-^2u\u0003JLG/_\u000b\u0002uB\u0011Qb_\u0005\u0003y:\u00111!\u00138u\u0011\u001dq\b!!A\u0005\u0002}\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0003\u0002\u0002\u0005\u001d\u0001cA\u0007\u0002\u0004%\u0019\u0011Q\u0001\b\u0003\u0007\u0005s\u0017\u0010C\u0004A{\u0006\u0005\t\u0019\u0001>\t\u0013\u0005-\u0001!!A\u0005B\u00055\u0011a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0005\u0005=\u0001CBA\t\u0003/\t\t!\u0004\u0002\u0002\u0014)\u0019\u0011Q\u0003\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002\u001a\u0005M!\u0001C%uKJ\fGo\u001c:\t\u0013\u0005u\u0001!!A\u0005\u0002\u0005}\u0011\u0001C2b]\u0016\u000bX/\u00197\u0015\t\u0005\u0005\u0012q\u0005\t\u0004\u001b\u0005\r\u0012bAA\u0013\u001d\t9!i\\8mK\u0006t\u0007\"\u0003!\u0002\u001c\u0005\u0005\t\u0019AA\u0001\u0011%\tY\u0003AA\u0001\n\u0003\ni#\u0001\u0005iCND7i\u001c3f)\u0005Q\b\"CA\u0019\u0001\u0005\u0005I\u0011IA\u001a\u0003!!xn\u0015;sS:<G#A8\t\u0013\u0005]\u0002!!A\u0005B\u0005e\u0012AB3rk\u0006d7\u000f\u0006\u0003\u0002\"\u0005m\u0002\"\u0003!\u00026\u0005\u0005\t\u0019AA\u0001\u000f%\tyDAA\u0001\u0012\u0003\t\t%A\nEkJ\f'\r\\3Tk\n\u001c8M]5qi&|g\u000eE\u00024\u0003\u00072\u0001\"\u0001\u0002\u0002\u0002#\u0005\u0011QI\n\u0006\u0003\u0007\n9%\u0006\t\t\u0003\u0013\nyeG\u000e(e5\u0011\u00111\n\u0006\u0004\u0003\u001br\u0011a\u0002:v]RLW.Z\u0005\u0005\u0003#\nYEA\tBEN$(/Y2u\rVt7\r^5p]NBq\u0001MA\"\t\u0003\t)\u0006\u0006\u0002\u0002B!Q\u0011\u0011GA\"\u0003\u0003%)%a\r\t\u0015\u0005m\u00131IA\u0001\n\u0003\u000bi&A\u0003baBd\u0017\u0010F\u00043\u0003?\n\t'a\u0019\t\re\tI\u00061\u0001\u001c\u0011\u0019\t\u0013\u0011\fa\u00017!1Q%!\u0017A\u0002\u001dB!\"a\u001a\u0002D\u0005\u0005I\u0011QA5\u0003\u001d)h.\u00199qYf$B!a\u001b\u0002xA)Q\"!\u001c\u0002r%\u0019\u0011q\u000e\b\u0003\r=\u0003H/[8o!\u0019i\u00111O\u000e\u001cO%\u0019\u0011Q\u000f\b\u0003\rQ+\b\u000f\\34\u0011%\tI(!\u001a\u0002\u0002\u0003\u0007!'A\u0002yIAB!\"! \u0002D\u0005\u0005I\u0011BA@\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u0005\u0005c\u00019\u0002\u0004&\u0019\u0011QQ9\u0003\r=\u0013'.Z2u\u0001")
public class DurableSubscription implements Product, Serializable
{
    private final long subKey;
    private final long topicKey;
    private final SubscriptionInfo info;
    private long gcPosition;
    private long lastAckPosition;
    private long cursorPosition;
    
    public static Option<Tuple3<Object, Object, SubscriptionInfo>> unapply(final DurableSubscription x$0) {
        return DurableSubscription$.MODULE$.unapply(x$0);
    }
    
    public static DurableSubscription apply(final long subKey, final long topicKey, final SubscriptionInfo info) {
        return DurableSubscription$.MODULE$.apply(subKey, topicKey, info);
    }
    
    public static Function1<Tuple3<Object, Object, SubscriptionInfo>, DurableSubscription> tupled() {
        return (Function1<Tuple3<Object, Object, SubscriptionInfo>, DurableSubscription>)DurableSubscription$.MODULE$.tupled();
    }
    
    public static Function1<Object, Function1<Object, Function1<SubscriptionInfo, DurableSubscription>>> curried() {
        return (Function1<Object, Function1<Object, Function1<SubscriptionInfo, DurableSubscription>>>)DurableSubscription$.MODULE$.curried();
    }
    
    public long subKey() {
        return this.subKey;
    }
    
    public long topicKey() {
        return this.topicKey;
    }
    
    public SubscriptionInfo info() {
        return this.info;
    }
    
    public long gcPosition() {
        return this.gcPosition;
    }
    
    public void gcPosition_$eq(final long x$1) {
        this.gcPosition = x$1;
    }
    
    public long lastAckPosition() {
        return this.lastAckPosition;
    }
    
    public void lastAckPosition_$eq(final long x$1) {
        this.lastAckPosition = x$1;
    }
    
    public long cursorPosition() {
        return this.cursorPosition;
    }
    
    public void cursorPosition_$eq(final long x$1) {
        this.cursorPosition = x$1;
    }
    
    public DurableSubscription copy(final long subKey, final long topicKey, final SubscriptionInfo info) {
        return new DurableSubscription(subKey, topicKey, info);
    }
    
    public long copy$default$1() {
        return this.subKey();
    }
    
    public long copy$default$2() {
        return this.topicKey();
    }
    
    public SubscriptionInfo copy$default$3() {
        return this.info();
    }
    
    public String productPrefix() {
        return "DurableSubscription";
    }
    
    public int productArity() {
        return 3;
    }
    
    public Object productElement(final int x$1) {
        Object o = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 2: {
                o = this.info();
                break;
            }
            case 1: {
                o = BoxesRunTime.boxToLong(this.topicKey());
                break;
            }
            case 0: {
                o = BoxesRunTime.boxToLong(this.subKey());
                break;
            }
        }
        return o;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof DurableSubscription;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.longHash(this.subKey())), Statics.longHash(this.topicKey())), Statics.anyHash((Object)this.info())), 3);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof DurableSubscription) {
                final DurableSubscription durableSubscription = (DurableSubscription)x$1;
                boolean b = false;
                Label_0103: {
                    Label_0102: {
                        if (this.subKey() == durableSubscription.subKey() && this.topicKey() == durableSubscription.topicKey()) {
                            final SubscriptionInfo info = this.info();
                            final SubscriptionInfo info2 = durableSubscription.info();
                            if (info == null) {
                                if (info2 != null) {
                                    break Label_0102;
                                }
                            }
                            else if (!info.equals(info2)) {
                                break Label_0102;
                            }
                            if (durableSubscription.canEqual(this)) {
                                b = true;
                                break Label_0103;
                            }
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
    
    public DurableSubscription(final long subKey, final long topicKey, final SubscriptionInfo info) {
        this.subKey = subKey;
        this.topicKey = topicKey;
        this.info = info;
        Product$class.$init$((Product)this);
        this.gcPosition = 0L;
        this.lastAckPosition = 0L;
        this.cursorPosition = 0L;
    }
}
