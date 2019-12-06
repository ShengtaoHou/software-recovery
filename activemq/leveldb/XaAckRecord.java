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
import org.apache.activemq.command.MessageAck;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005-d\u0001B\u0001\u0003\u0001.\u00111\u0002W1BG.\u0014VmY8sI*\u00111\u0001B\u0001\bY\u00164X\r\u001c3c\u0015\t)a!\u0001\u0005bGRLg/Z7r\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\n\u0016!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u0011QbE\u0005\u0003)9\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e-%\u0011qC\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00015\u0005I1m\u001c8uC&tWM]\u000b\u00027A\u0011Q\u0002H\u0005\u0003;9\u0011A\u0001T8oO\"Aq\u0004\u0001B\tB\u0003%1$\u0001\u0006d_:$\u0018-\u001b8fe\u0002B\u0001\"\t\u0001\u0003\u0016\u0004%\tAG\u0001\u0004g\u0016\f\b\u0002C\u0012\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\tM,\u0017\u000f\t\u0005\tK\u0001\u0011)\u001a!C\u0001M\u0005\u0019\u0011mY6\u0016\u0003\u001d\u0002\"\u0001K\u0016\u000e\u0003%R!A\u000b\u0003\u0002\u000f\r|W.\\1oI&\u0011A&\u000b\u0002\u000b\u001b\u0016\u001c8/Y4f\u0003\u000e\\\u0007\u0002\u0003\u0018\u0001\u0005#\u0005\u000b\u0011B\u0014\u0002\t\u0005\u001c7\u000e\t\u0005\ta\u0001\u0011)\u001a!C\u00015\u0005\u00191/\u001e2\t\u0011I\u0002!\u0011#Q\u0001\nm\tAa];cA!)A\u0007\u0001C\u0001k\u00051A(\u001b8jiz\"RA\u000e\u001d:um\u0002\"a\u000e\u0001\u000e\u0003\tAQ!G\u001aA\u0002mAQ!I\u001aA\u0002mAQ!J\u001aA\u0002\u001dBq\u0001M\u001a\u0011\u0002\u0003\u00071\u0004C\u0004>\u0001\u0005\u0005I\u0011\u0001 \u0002\t\r|\u0007/\u001f\u000b\u0006m}\u0002\u0015I\u0011\u0005\b3q\u0002\n\u00111\u0001\u001c\u0011\u001d\tC\b%AA\u0002mAq!\n\u001f\u0011\u0002\u0003\u0007q\u0005C\u00041yA\u0005\t\u0019A\u000e\t\u000f\u0011\u0003\u0011\u0013!C\u0001\u000b\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001$+\u0005m95&\u0001%\u0011\u0005%sU\"\u0001&\u000b\u0005-c\u0015!C;oG\",7m[3e\u0015\tie\"\u0001\u0006b]:|G/\u0019;j_:L!a\u0014&\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004R\u0001E\u0005I\u0011A#\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%e!91\u000bAI\u0001\n\u0003!\u0016AD2paf$C-\u001a4bk2$HeM\u000b\u0002+*\u0012qe\u0012\u0005\b/\u0002\t\n\u0011\"\u0001F\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIQBq!\u0017\u0001\u0002\u0002\u0013\u0005#,A\u0007qe>$Wo\u0019;Qe\u00164\u0017\u000e_\u000b\u00027B\u0011A,Y\u0007\u0002;*\u0011alX\u0001\u0005Y\u0006twMC\u0001a\u0003\u0011Q\u0017M^1\n\u0005\tl&AB*ue&tw\rC\u0004e\u0001\u0005\u0005I\u0011A3\u0002\u0019A\u0014x\u000eZ;di\u0006\u0013\u0018\u000e^=\u0016\u0003\u0019\u0004\"!D4\n\u0005!t!aA%oi\"9!\u000eAA\u0001\n\u0003Y\u0017A\u00049s_\u0012,8\r^#mK6,g\u000e\u001e\u000b\u0003Y>\u0004\"!D7\n\u00059t!aA!os\"9\u0001/[A\u0001\u0002\u00041\u0017a\u0001=%c!9!\u000fAA\u0001\n\u0003\u001a\u0018a\u00049s_\u0012,8\r^%uKJ\fGo\u001c:\u0016\u0003Q\u00042!\u001e=m\u001b\u00051(BA<\u000f\u0003)\u0019w\u000e\u001c7fGRLwN\\\u0005\u0003sZ\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\bw\u0002\t\t\u0011\"\u0001}\u0003!\u0019\u0017M\\#rk\u0006dGcA?\u0002\u0002A\u0011QB`\u0005\u0003\u007f:\u0011qAQ8pY\u0016\fg\u000eC\u0004qu\u0006\u0005\t\u0019\u00017\t\u0013\u0005\u0015\u0001!!A\u0005B\u0005\u001d\u0011\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u0003\u0019D\u0011\"a\u0003\u0001\u0003\u0003%\t%!\u0004\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012a\u0017\u0005\n\u0003#\u0001\u0011\u0011!C!\u0003'\ta!Z9vC2\u001cHcA?\u0002\u0016!A\u0001/a\u0004\u0002\u0002\u0003\u0007AnB\u0005\u0002\u001a\t\t\t\u0011#\u0001\u0002\u001c\u0005Y\u0001,Y!dWJ+7m\u001c:e!\r9\u0014Q\u0004\u0004\t\u0003\t\t\t\u0011#\u0001\u0002 M)\u0011QDA\u0011+AI\u00111EA\u00157m93DN\u0007\u0003\u0003KQ1!a\n\u000f\u0003\u001d\u0011XO\u001c;j[\u0016LA!a\u000b\u0002&\t\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001b\t\u000fQ\ni\u0002\"\u0001\u00020Q\u0011\u00111\u0004\u0005\u000b\u0003\u0017\ti\"!A\u0005F\u00055\u0001BCA\u001b\u0003;\t\t\u0011\"!\u00028\u0005)\u0011\r\u001d9msRIa'!\u000f\u0002<\u0005u\u0012q\b\u0005\u00073\u0005M\u0002\u0019A\u000e\t\r\u0005\n\u0019\u00041\u0001\u001c\u0011\u0019)\u00131\u0007a\u0001O!A\u0001'a\r\u0011\u0002\u0003\u00071\u0004\u0003\u0006\u0002D\u0005u\u0011\u0011!CA\u0003\u000b\nq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002H\u0005M\u0003#B\u0007\u0002J\u00055\u0013bAA&\u001d\t1q\n\u001d;j_:\u0004r!DA(7m93$C\u0002\u0002R9\u0011a\u0001V;qY\u0016$\u0004\"CA+\u0003\u0003\n\t\u00111\u00017\u0003\rAH\u0005\r\u0005\n\u00033\ni\"%A\u0005\u0002\u0015\u000b1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\"\u0004\"CA/\u0003;\t\n\u0011\"\u0001F\u0003=\t\u0007\u000f\u001d7zI\u0011,g-Y;mi\u0012\"\u0004BCA1\u0003;\t\t\u0011\"\u0003\u0002d\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t)\u0007E\u0002]\u0003OJ1!!\u001b^\u0005\u0019y%M[3di\u0002")
public class XaAckRecord implements Product, Serializable
{
    private final long container;
    private final long seq;
    private final MessageAck ack;
    private final long sub;
    
    public static long apply$default$4() {
        return XaAckRecord$.MODULE$.apply$default$4();
    }
    
    public static long $lessinit$greater$default$4() {
        return XaAckRecord$.MODULE$.$lessinit$greater$default$4();
    }
    
    public static Option<Tuple4<Object, Object, MessageAck, Object>> unapply(final XaAckRecord x$0) {
        return XaAckRecord$.MODULE$.unapply(x$0);
    }
    
    public static XaAckRecord apply(final long container, final long seq, final MessageAck ack, final long sub) {
        return XaAckRecord$.MODULE$.apply(container, seq, ack, sub);
    }
    
    public static Function1<Tuple4<Object, Object, MessageAck, Object>, XaAckRecord> tupled() {
        return (Function1<Tuple4<Object, Object, MessageAck, Object>, XaAckRecord>)XaAckRecord$.MODULE$.tupled();
    }
    
    public static Function1<Object, Function1<Object, Function1<MessageAck, Function1<Object, XaAckRecord>>>> curried() {
        return (Function1<Object, Function1<Object, Function1<MessageAck, Function1<Object, XaAckRecord>>>>)XaAckRecord$.MODULE$.curried();
    }
    
    public long container() {
        return this.container;
    }
    
    public long seq() {
        return this.seq;
    }
    
    public MessageAck ack() {
        return this.ack;
    }
    
    public long sub() {
        return this.sub;
    }
    
    public XaAckRecord copy(final long container, final long seq, final MessageAck ack, final long sub) {
        return new XaAckRecord(container, seq, ack, sub);
    }
    
    public long copy$default$1() {
        return this.container();
    }
    
    public long copy$default$2() {
        return this.seq();
    }
    
    public MessageAck copy$default$3() {
        return this.ack();
    }
    
    public long copy$default$4() {
        return this.sub();
    }
    
    public String productPrefix() {
        return "XaAckRecord";
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
                o = BoxesRunTime.boxToLong(this.sub());
                break;
            }
            case 2: {
                o = this.ack();
                break;
            }
            case 1: {
                o = BoxesRunTime.boxToLong(this.seq());
                break;
            }
            case 0: {
                o = BoxesRunTime.boxToLong(this.container());
                break;
            }
        }
        return o;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof XaAckRecord;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.longHash(this.container())), Statics.longHash(this.seq())), Statics.anyHash((Object)this.ack())), Statics.longHash(this.sub())), 4);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof XaAckRecord) {
                final XaAckRecord xaAckRecord = (XaAckRecord)x$1;
                boolean b = false;
                Label_0116: {
                    Label_0115: {
                        if (this.container() == xaAckRecord.container() && this.seq() == xaAckRecord.seq()) {
                            final MessageAck ack = this.ack();
                            final MessageAck ack2 = xaAckRecord.ack();
                            if (ack == null) {
                                if (ack2 != null) {
                                    break Label_0115;
                                }
                            }
                            else if (!ack.equals(ack2)) {
                                break Label_0115;
                            }
                            if (this.sub() == xaAckRecord.sub() && xaAckRecord.canEqual(this)) {
                                b = true;
                                break Label_0116;
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
    
    public XaAckRecord(final long container, final long seq, final MessageAck ack, final long sub) {
        this.container = container;
        this.seq = seq;
        this.ack = ack;
        this.sub = sub;
        Product$class.$init$((Product)this);
    }
}
