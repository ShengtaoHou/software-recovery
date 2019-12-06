// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.Product$class;
import scala.runtime.Statics;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import scala.Tuple4;
import scala.Option;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u00055d\u0001B\u0001\u0003\u00016\u00111b\u00157bm\u0016\u001cF/\u0019;vg*\u00111\u0001B\u0001\u000be\u0016\u0004H.[2bi\u0016$'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\u000f)]\u0001\"a\u0004\n\u000e\u0003AQ\u0011!E\u0001\u0006g\u000e\fG.Y\u0005\u0003'A\u0011a!\u00118z%\u00164\u0007CA\b\u0016\u0013\t1\u0002CA\u0004Qe>$Wo\u0019;\u0011\u0005=A\u0012BA\r\u0011\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!Y\u0002A!f\u0001\n\u0003a\u0012A\u00028pI\u0016LE-F\u0001\u001e!\tq\u0012E\u0004\u0002\u0010?%\u0011\u0001\u0005E\u0001\u0007!J,G-\u001a4\n\u0005\t\u001a#AB*ue&twM\u0003\u0002!!!AQ\u0005\u0001B\tB\u0003%Q$A\u0004o_\u0012,\u0017\n\u001a\u0011\t\u0011\u001d\u0002!Q3A\u0005\u0002q\tQB]3n_R,\u0017\t\u001a3sKN\u001c\b\u0002C\u0015\u0001\u0005#\u0005\u000b\u0011B\u000f\u0002\u001dI,Wn\u001c;f\u0003\u0012$'/Z:tA!A1\u0006\u0001BK\u0002\u0013\u0005A&\u0001\u0005biR\f7\r[3e+\u0005i\u0003CA\b/\u0013\ty\u0003CA\u0004C_>dW-\u00198\t\u0011E\u0002!\u0011#Q\u0001\n5\n\u0011\"\u0019;uC\u000eDW\r\u001a\u0011\t\u0011M\u0002!Q3A\u0005\u0002Q\n\u0001\u0002]8tSRLwN\\\u000b\u0002kA\u0011qBN\u0005\u0003oA\u0011A\u0001T8oO\"A\u0011\b\u0001B\tB\u0003%Q'A\u0005q_NLG/[8oA!)1\b\u0001C\u0001y\u00051A(\u001b8jiz\"R!P A\u0003\n\u0003\"A\u0010\u0001\u000e\u0003\tAQa\u0007\u001eA\u0002uAQa\n\u001eA\u0002uAQa\u000b\u001eA\u00025BQa\r\u001eA\u0002UBq\u0001\u0012\u0001\u0002\u0002\u0013\u0005Q)\u0001\u0003d_BLH#B\u001fG\u000f\"K\u0005bB\u000eD!\u0003\u0005\r!\b\u0005\bO\r\u0003\n\u00111\u0001\u001e\u0011\u001dY3\t%AA\u00025BqaM\"\u0011\u0002\u0003\u0007Q\u0007C\u0004L\u0001E\u0005I\u0011\u0001'\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tQJ\u000b\u0002\u001e\u001d.\nq\n\u0005\u0002Q+6\t\u0011K\u0003\u0002S'\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003)B\t!\"\u00198o_R\fG/[8o\u0013\t1\u0016KA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016Dq\u0001\u0017\u0001\u0012\u0002\u0013\u0005A*\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\t\u000fi\u0003\u0011\u0013!C\u00017\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001aT#\u0001/+\u00055r\u0005b\u00020\u0001#\u0003%\taX\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00135+\u0005\u0001'FA\u001bO\u0011\u001d\u0011\u0007!!A\u0005B\r\fQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#\u00013\u0011\u0005\u0015TW\"\u00014\u000b\u0005\u001dD\u0017\u0001\u00027b]\u001eT\u0011![\u0001\u0005U\u00064\u0018-\u0003\u0002#M\"9A\u000eAA\u0001\n\u0003i\u0017\u0001\u00049s_\u0012,8\r^!sSRLX#\u00018\u0011\u0005=y\u0017B\u00019\u0011\u0005\rIe\u000e\u001e\u0005\be\u0002\t\t\u0011\"\u0001t\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"\u0001^<\u0011\u0005=)\u0018B\u0001<\u0011\u0005\r\te.\u001f\u0005\bqF\f\t\u00111\u0001o\u0003\rAH%\r\u0005\bu\u0002\t\t\u0011\"\u0011|\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#\u0001?\u0011\tu\f\t\u0001^\u0007\u0002}*\u0011q\u0010E\u0001\u000bG>dG.Z2uS>t\u0017bAA\u0002}\nA\u0011\n^3sCR|'\u000fC\u0005\u0002\b\u0001\t\t\u0011\"\u0001\u0002\n\u0005A1-\u00198FcV\fG\u000eF\u0002.\u0003\u0017A\u0001\u0002_A\u0003\u0003\u0003\u0005\r\u0001\u001e\u0005\n\u0003\u001f\u0001\u0011\u0011!C!\u0003#\t\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002]\"I\u0011Q\u0003\u0001\u0002\u0002\u0013\u0005\u0013qC\u0001\ti>\u001cFO]5oOR\tA\rC\u0005\u0002\u001c\u0001\t\t\u0011\"\u0011\u0002\u001e\u00051Q-];bYN$2!LA\u0010\u0011!A\u0018\u0011DA\u0001\u0002\u0004!x!CA\u0012\u0005\u0005\u0005\t\u0012AA\u0013\u0003-\u0019F.\u0019<f'R\fG/^:\u0011\u0007y\n9C\u0002\u0005\u0002\u0005\u0005\u0005\t\u0012AA\u0015'\u0015\t9#a\u000b\u0018!%\ti#a\r\u001e;5*T(\u0004\u0002\u00020)\u0019\u0011\u0011\u0007\t\u0002\u000fI,h\u000e^5nK&!\u0011QGA\u0018\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g\u000e\u000e\u0005\bw\u0005\u001dB\u0011AA\u001d)\t\t)\u0003\u0003\u0006\u0002\u0016\u0005\u001d\u0012\u0011!C#\u0003/A!\"a\u0010\u0002(\u0005\u0005I\u0011QA!\u0003\u0015\t\u0007\u000f\u001d7z)%i\u00141IA#\u0003\u000f\nI\u0005\u0003\u0004\u001c\u0003{\u0001\r!\b\u0005\u0007O\u0005u\u0002\u0019A\u000f\t\r-\ni\u00041\u0001.\u0011\u0019\u0019\u0014Q\ba\u0001k!Q\u0011QJA\u0014\u0003\u0003%\t)a\u0014\u0002\u000fUt\u0017\r\u001d9msR!\u0011\u0011KA/!\u0015y\u00111KA,\u0013\r\t)\u0006\u0005\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u000f=\tI&H\u000f.k%\u0019\u00111\f\t\u0003\rQ+\b\u000f\\35\u0011%\ty&a\u0013\u0002\u0002\u0003\u0007Q(A\u0002yIAB!\"a\u0019\u0002(\u0005\u0005I\u0011BA3\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u001d\u0004cA3\u0002j%\u0019\u00111\u000e4\u0003\r=\u0013'.Z2u\u0001")
public class SlaveStatus implements Product, Serializable
{
    private final String nodeId;
    private final String remoteAddress;
    private final boolean attached;
    private final long position;
    
    public static Option<Tuple4<String, String, Object, Object>> unapply(final SlaveStatus x$0) {
        return SlaveStatus$.MODULE$.unapply(x$0);
    }
    
    public static SlaveStatus apply(final String nodeId, final String remoteAddress, final boolean attached, final long position) {
        return SlaveStatus$.MODULE$.apply(nodeId, remoteAddress, attached, position);
    }
    
    public static Function1<Tuple4<String, String, Object, Object>, SlaveStatus> tupled() {
        return (Function1<Tuple4<String, String, Object, Object>, SlaveStatus>)SlaveStatus$.MODULE$.tupled();
    }
    
    public static Function1<String, Function1<String, Function1<Object, Function1<Object, SlaveStatus>>>> curried() {
        return (Function1<String, Function1<String, Function1<Object, Function1<Object, SlaveStatus>>>>)SlaveStatus$.MODULE$.curried();
    }
    
    public String nodeId() {
        return this.nodeId;
    }
    
    public String remoteAddress() {
        return this.remoteAddress;
    }
    
    public boolean attached() {
        return this.attached;
    }
    
    public long position() {
        return this.position;
    }
    
    public SlaveStatus copy(final String nodeId, final String remoteAddress, final boolean attached, final long position) {
        return new SlaveStatus(nodeId, remoteAddress, attached, position);
    }
    
    public String copy$default$1() {
        return this.nodeId();
    }
    
    public String copy$default$2() {
        return this.remoteAddress();
    }
    
    public boolean copy$default$3() {
        return this.attached();
    }
    
    public long copy$default$4() {
        return this.position();
    }
    
    public String productPrefix() {
        return "SlaveStatus";
    }
    
    public int productArity() {
        return 4;
    }
    
    public Object productElement(final int x$1) {
        java.io.Serializable s = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 3: {
                s = BoxesRunTime.boxToLong(this.position());
                break;
            }
            case 2: {
                s = BoxesRunTime.boxToBoolean(this.attached());
                break;
            }
            case 1: {
                s = this.remoteAddress();
                break;
            }
            case 0: {
                s = this.nodeId();
                break;
            }
        }
        return s;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof SlaveStatus;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.nodeId())), Statics.anyHash((Object)this.remoteAddress())), this.attached() ? 1231 : 1237), Statics.longHash(this.position())), 4);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof SlaveStatus) {
                final SlaveStatus slaveStatus = (SlaveStatus)x$1;
                final String nodeId = this.nodeId();
                final String nodeId2 = slaveStatus.nodeId();
                boolean b = false;
                Label_0134: {
                    Label_0133: {
                        if (nodeId == null) {
                            if (nodeId2 != null) {
                                break Label_0133;
                            }
                        }
                        else if (!nodeId.equals(nodeId2)) {
                            break Label_0133;
                        }
                        final String remoteAddress = this.remoteAddress();
                        final String remoteAddress2 = slaveStatus.remoteAddress();
                        if (remoteAddress == null) {
                            if (remoteAddress2 != null) {
                                break Label_0133;
                            }
                        }
                        else if (!remoteAddress.equals(remoteAddress2)) {
                            break Label_0133;
                        }
                        if (this.attached() == slaveStatus.attached() && this.position() == slaveStatus.position() && slaveStatus.canEqual(this)) {
                            b = true;
                            break Label_0134;
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
    
    public SlaveStatus(final String nodeId, final String remoteAddress, final boolean attached, final long position) {
        this.nodeId = nodeId;
        this.remoteAddress = remoteAddress;
        this.attached = attached;
        this.position = position;
        Product$class.$init$((Product)this);
    }
}
