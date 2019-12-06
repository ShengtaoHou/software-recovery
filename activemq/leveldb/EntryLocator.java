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

@ScalaSignature(bytes = "\u0006\u0001\u00055b\u0001B\u0001\u0003\u0001.\u0011A\"\u00128uefdunY1u_JT!a\u0001\u0003\u0002\u000f1,g/\u001a7eE*\u0011QAB\u0001\tC\u000e$\u0018N^3nc*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019B\u0001\u0001\u0007\u0013+A\u0011Q\u0002E\u0007\u0002\u001d)\tq\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0012\u001d\t1\u0011I\\=SK\u001a\u0004\"!D\n\n\u0005Qq!a\u0002)s_\u0012,8\r\u001e\t\u0003\u001bYI!a\u0006\b\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011e\u0001!Q3A\u0005\u0002i\t1!]5e+\u0005Y\u0002CA\u0007\u001d\u0013\tibB\u0001\u0003M_:<\u0007\u0002C\u0010\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\tELG\r\t\u0005\tC\u0001\u0011)\u001a!C\u00015\u0005\u00191/Z9\t\u0011\r\u0002!\u0011#Q\u0001\nm\tAa]3rA!)Q\u0005\u0001C\u0001M\u00051A(\u001b8jiz\"2aJ\u0015+!\tA\u0003!D\u0001\u0003\u0011\u0015IB\u00051\u0001\u001c\u0011\u0015\tC\u00051\u0001\u001c\u0011\u001da\u0003!!A\u0005\u00025\nAaY8qsR\u0019qEL\u0018\t\u000feY\u0003\u0013!a\u00017!9\u0011e\u000bI\u0001\u0002\u0004Y\u0002bB\u0019\u0001#\u0003%\tAM\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00132+\u0005\u0019$FA\u000e5W\u0005)\u0004C\u0001\u001c<\u001b\u00059$B\u0001\u001d:\u0003%)hn\u00195fG.,GM\u0003\u0002;\u001d\u0005Q\u0011M\u001c8pi\u0006$\u0018n\u001c8\n\u0005q:$!E;oG\",7m[3e-\u0006\u0014\u0018.\u00198dK\"9a\bAI\u0001\n\u0003\u0011\u0014AD2paf$C-\u001a4bk2$HE\r\u0005\b\u0001\u0002\t\t\u0011\"\u0011B\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\t!\t\u0005\u0002D\u00116\tAI\u0003\u0002F\r\u0006!A.\u00198h\u0015\u00059\u0015\u0001\u00026bm\u0006L!!\u0013#\u0003\rM#(/\u001b8h\u0011\u001dY\u0005!!A\u0005\u00021\u000bA\u0002\u001d:pIV\u001cG/\u0011:jif,\u0012!\u0014\t\u0003\u001b9K!a\u0014\b\u0003\u0007%sG\u000fC\u0004R\u0001\u0005\u0005I\u0011\u0001*\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u00111K\u0016\t\u0003\u001bQK!!\u0016\b\u0003\u0007\u0005s\u0017\u0010C\u0004X!\u0006\u0005\t\u0019A'\u0002\u0007a$\u0013\u0007C\u0004Z\u0001\u0005\u0005I\u0011\t.\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012a\u0017\t\u00049~\u001bV\"A/\u000b\u0005ys\u0011AC2pY2,7\r^5p]&\u0011\u0001-\u0018\u0002\t\u0013R,'/\u0019;pe\"9!\rAA\u0001\n\u0003\u0019\u0017\u0001C2b]\u0016\u000bX/\u00197\u0015\u0005\u0011<\u0007CA\u0007f\u0013\t1gBA\u0004C_>dW-\u00198\t\u000f]\u000b\u0017\u0011!a\u0001'\"9\u0011\u000eAA\u0001\n\u0003R\u0017\u0001\u00035bg\"\u001cu\u000eZ3\u0015\u00035Cq\u0001\u001c\u0001\u0002\u0002\u0013\u0005S.\u0001\u0005u_N#(/\u001b8h)\u0005\u0011\u0005bB8\u0001\u0003\u0003%\t\u0005]\u0001\u0007KF,\u0018\r\\:\u0015\u0005\u0011\f\bbB,o\u0003\u0003\u0005\raU\u0004\bg\n\t\t\u0011#\u0001u\u00031)e\u000e\u001e:z\u0019>\u001c\u0017\r^8s!\tASOB\u0004\u0002\u0005\u0005\u0005\t\u0012\u0001<\u0014\u0007U<X\u0003E\u0003ywnYr%D\u0001z\u0015\tQh\"A\u0004sk:$\u0018.\\3\n\u0005qL(!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8oe!)Q%\u001eC\u0001}R\tA\u000fC\u0004mk\u0006\u0005IQI7\t\u0013\u0005\rQ/!A\u0005\u0002\u0006\u0015\u0011!B1qa2LH#B\u0014\u0002\b\u0005%\u0001BB\r\u0002\u0002\u0001\u00071\u0004\u0003\u0004\"\u0003\u0003\u0001\ra\u0007\u0005\n\u0003\u001b)\u0018\u0011!CA\u0003\u001f\tq!\u001e8baBd\u0017\u0010\u0006\u0003\u0002\u0012\u0005u\u0001#B\u0007\u0002\u0014\u0005]\u0011bAA\u000b\u001d\t1q\n\u001d;j_:\u0004R!DA\r7mI1!a\u0007\u000f\u0005\u0019!V\u000f\u001d7fe!I\u0011qDA\u0006\u0003\u0003\u0005\raJ\u0001\u0004q\u0012\u0002\u0004\"CA\u0012k\u0006\u0005I\u0011BA\u0013\u0003-\u0011X-\u00193SKN|GN^3\u0015\u0005\u0005\u001d\u0002cA\"\u0002*%\u0019\u00111\u0006#\u0003\r=\u0013'.Z2u\u0001")
public class EntryLocator implements Product, Serializable
{
    private final long qid;
    private final long seq;
    
    public static Option<Tuple2<Object, Object>> unapply(final EntryLocator x$0) {
        return EntryLocator$.MODULE$.unapply(x$0);
    }
    
    public static EntryLocator apply(final long qid, final long seq) {
        return EntryLocator$.MODULE$.apply(qid, seq);
    }
    
    public static Function1<Tuple2<Object, Object>, EntryLocator> tupled() {
        return (Function1<Tuple2<Object, Object>, EntryLocator>)EntryLocator$.MODULE$.tupled();
    }
    
    public static Function1<Object, Function1<Object, EntryLocator>> curried() {
        return (Function1<Object, Function1<Object, EntryLocator>>)EntryLocator$.MODULE$.curried();
    }
    
    public long qid() {
        return this.qid;
    }
    
    public long seq() {
        return this.seq;
    }
    
    public EntryLocator copy(final long qid, final long seq) {
        return new EntryLocator(qid, seq);
    }
    
    public long copy$default$1() {
        return this.qid();
    }
    
    public long copy$default$2() {
        return this.seq();
    }
    
    public String productPrefix() {
        return "EntryLocator";
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
                n = BoxesRunTime.boxToLong(this.seq());
                break;
            }
            case 0: {
                n = BoxesRunTime.boxToLong(this.qid());
                break;
            }
        }
        return n;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof EntryLocator;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(-889275714, Statics.longHash(this.qid())), Statics.longHash(this.seq())), 2);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof EntryLocator) {
                final EntryLocator entryLocator = (EntryLocator)x$1;
                if (this.qid() == entryLocator.qid() && this.seq() == entryLocator.seq() && entryLocator.canEqual(this)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public EntryLocator(final long qid, final long seq) {
        this.qid = qid;
        this.seq = seq;
        Product$class.$init$((Product)this);
    }
}
