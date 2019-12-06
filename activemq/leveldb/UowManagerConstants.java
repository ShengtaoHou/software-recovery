// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Some;
import scala.Tuple2$mcJJ$sp;
import scala.None$;
import scala.Tuple2;
import scala.Option;
import scala.runtime.AbstractFunction2;
import scala.Product$class;
import scala.runtime.Statics;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.Serializable;
import scala.Product;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005=t!B\u0001\u0003\u0011\u0003Y\u0011aE+po6\u000bg.Y4fe\u000e{gn\u001d;b]R\u001c(BA\u0002\u0005\u0003\u001daWM^3mI\nT!!\u0002\u0004\u0002\u0011\u0005\u001cG/\u001b<f[FT!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u0001\u0001C\u0001\u0007\u000e\u001b\u0005\u0011a!\u0002\b\u0003\u0011\u0003y!aE+po6\u000bg.Y4fe\u000e{gn\u001d;b]R\u001c8CA\u0007\u0011!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fM\")q#\u0004C\u00011\u00051A(\u001b8jiz\"\u0012a\u0003\u0005\b55\u0011\r\u0011\"\u0001\u001c\u0003U\tV+R+F?\u000e{E\nT#D)&{ej\u0018+Z!\u0016+\u0012\u0001\b\t\u0003#uI!A\b\n\u0003\u0007%sG\u000f\u0003\u0004!\u001b\u0001\u0006I\u0001H\u0001\u0017#V+U+R0D\u001f2cUi\u0011+J\u001f:{F+\u0017)FA!9!%\u0004b\u0001\n\u0003Y\u0012!\u0006+P!&\u001bulQ(M\u0019\u0016\u001bE+S(O?RK\u0006+\u0012\u0005\u0007I5\u0001\u000b\u0011\u0002\u000f\u0002-Q{\u0005+S\"`\u0007>cE*R\"U\u0013>su\fV-Q\u000b\u0002BqAJ\u0007C\u0002\u0013\u00051$A\u000eU%\u0006s5+Q\"U\u0013>sulQ(M\u0019\u0016\u001bE+S(O?RK\u0006+\u0012\u0005\u0007Q5\u0001\u000b\u0011\u0002\u000f\u00029Q\u0013\u0016IT*B\u0007RKuJT0D\u001f2cUi\u0011+J\u001f:{F+\u0017)FA!9!&\u0004b\u0001\n\u0003Y\u0012\u0001H*V\u0005N\u001b%+\u0013)U\u0013>sulQ(M\u0019\u0016\u001bE+S(O?RK\u0006+\u0012\u0005\u0007Y5\u0001\u000b\u0011\u0002\u000f\u0002;M+&iU\"S\u0013B#\u0016j\u0014(`\u0007>cE*R\"U\u0013>su\fV-Q\u000b\u00022AAL\u0007A_\ti\u0011+^3vK\u0016sGO]=LKf\u001cB!\f\t1gA\u0011\u0011#M\u0005\u0003eI\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u0012i%\u0011QG\u0005\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\to5\u0012)\u001a!C\u0001q\u0005)\u0011/^3vKV\t\u0011\b\u0005\u0002\u0012u%\u00111H\u0005\u0002\u0005\u0019>tw\r\u0003\u0005>[\tE\t\u0015!\u0003:\u0003\u0019\tX/Z;fA!Aq(\fBK\u0002\u0013\u0005\u0001(A\u0002tKFD\u0001\"Q\u0017\u0003\u0012\u0003\u0006I!O\u0001\u0005g\u0016\f\b\u0005C\u0003\u0018[\u0011\u00051\tF\u0002E\r\u001e\u0003\"!R\u0017\u000e\u00035AQa\u000e\"A\u0002eBQa\u0010\"A\u0002eBq!S\u0017\u0002\u0002\u0013\u0005!*\u0001\u0003d_BLHc\u0001#L\u0019\"9q\u0007\u0013I\u0001\u0002\u0004I\u0004bB I!\u0003\u0005\r!\u000f\u0005\b\u001d6\n\n\u0011\"\u0001P\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012\u0001\u0015\u0016\u0003sE[\u0013A\u0015\t\u0003'bk\u0011\u0001\u0016\u0006\u0003+Z\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005]\u0013\u0012AC1o]>$\u0018\r^5p]&\u0011\u0011\f\u0016\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007bB..#\u0003%\taT\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133\u0011\u001diV&!A\u0005By\u000bQ\u0002\u001d:pIV\u001cG\u000f\u0015:fM&DX#A0\u0011\u0005\u0001,W\"A1\u000b\u0005\t\u001c\u0017\u0001\u00027b]\u001eT\u0011\u0001Z\u0001\u0005U\u00064\u0018-\u0003\u0002gC\n11\u000b\u001e:j]\u001eDq\u0001[\u0017\u0002\u0002\u0013\u00051$\u0001\u0007qe>$Wo\u0019;Be&$\u0018\u0010C\u0004k[\u0005\u0005I\u0011A6\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR\u0011An\u001c\t\u0003#5L!A\u001c\n\u0003\u0007\u0005s\u0017\u0010C\u0004qS\u0006\u0005\t\u0019\u0001\u000f\u0002\u0007a$\u0013\u0007C\u0004s[\u0005\u0005I\u0011I:\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\u0012\u0001\u001e\t\u0004kbdW\"\u0001<\u000b\u0005]\u0014\u0012AC2pY2,7\r^5p]&\u0011\u0011P\u001e\u0002\t\u0013R,'/\u0019;pe\"910LA\u0001\n\u0003a\u0018\u0001C2b]\u0016\u000bX/\u00197\u0015\u0007u\f\t\u0001\u0005\u0002\u0012}&\u0011qP\u0005\u0002\b\u0005>|G.Z1o\u0011\u001d\u0001(0!AA\u00021D\u0011\"!\u0002.\u0003\u0003%\t%a\u0002\u0002\u0011!\f7\u000f[\"pI\u0016$\u0012\u0001\b\u0005\n\u0003\u0017i\u0013\u0011!C!\u0003\u001b\t\u0001\u0002^8TiJLgn\u001a\u000b\u0002?\"I\u0011\u0011C\u0017\u0002\u0002\u0013\u0005\u00131C\u0001\u0007KF,\u0018\r\\:\u0015\u0007u\f)\u0002\u0003\u0005q\u0003\u001f\t\t\u00111\u0001m\u000f%\tI\"DA\u0001\u0012\u0003\tY\"A\u0007Rk\u0016,X-\u00128uef\\U-\u001f\t\u0004\u000b\u0006ua\u0001\u0003\u0018\u000e\u0003\u0003E\t!a\b\u0014\u000b\u0005u\u0011\u0011E\u001a\u0011\u000f\u0005\r\u0012\u0011F\u001d:\t6\u0011\u0011Q\u0005\u0006\u0004\u0003O\u0011\u0012a\u0002:v]RLW.Z\u0005\u0005\u0003W\t)CA\tBEN$(/Y2u\rVt7\r^5p]JBqaFA\u000f\t\u0003\ty\u0003\u0006\u0002\u0002\u001c!Q\u00111BA\u000f\u0003\u0003%)%!\u0004\t\u0015\u0005U\u0012QDA\u0001\n\u0003\u000b9$A\u0003baBd\u0017\u0010F\u0003E\u0003s\tY\u0004\u0003\u00048\u0003g\u0001\r!\u000f\u0005\u0007\u007f\u0005M\u0002\u0019A\u001d\t\u0015\u0005}\u0012QDA\u0001\n\u0003\u000b\t%A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005\r\u0013q\n\t\u0006#\u0005\u0015\u0013\u0011J\u0005\u0004\u0003\u000f\u0012\"AB(qi&|g\u000eE\u0003\u0012\u0003\u0017J\u0014(C\u0002\u0002NI\u0011a\u0001V;qY\u0016\u0014\u0004\"CA)\u0003{\t\t\u00111\u0001E\u0003\rAH\u0005\r\u0005\u000b\u0003+\ni\"!A\u0005\n\u0005]\u0013a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u0017\u0011\u0007\u0001\fY&C\u0002\u0002^\u0005\u0014aa\u00142kK\u000e$\bbBA1\u001b\u0011\u0005\u00111M\u0001\u0004W\u0016LHc\u0001#\u0002f!A\u0011qMA0\u0001\u0004\tI'A\u0001y!\ra\u00111N\u0005\u0004\u0003[\u0012!\u0001E)vKV,WI\u001c;ssJ+7m\u001c:e\u0001")
public final class UowManagerConstants
{
    public static QueueEntryKey key(final QueueEntryRecord x) {
        return UowManagerConstants$.MODULE$.key(x);
    }
    
    public static int SUBSCRIPTION_COLLECTION_TYPE() {
        return UowManagerConstants$.MODULE$.SUBSCRIPTION_COLLECTION_TYPE();
    }
    
    public static int TRANSACTION_COLLECTION_TYPE() {
        return UowManagerConstants$.MODULE$.TRANSACTION_COLLECTION_TYPE();
    }
    
    public static int TOPIC_COLLECTION_TYPE() {
        return UowManagerConstants$.MODULE$.TOPIC_COLLECTION_TYPE();
    }
    
    public static int QUEUE_COLLECTION_TYPE() {
        return UowManagerConstants$.MODULE$.QUEUE_COLLECTION_TYPE();
    }
    
    public static class QueueEntryKey implements Product, Serializable
    {
        private final long queue;
        private final long seq;
        
        public long queue() {
            return this.queue;
        }
        
        public long seq() {
            return this.seq;
        }
        
        public QueueEntryKey copy(final long queue, final long seq) {
            return new QueueEntryKey(queue, seq);
        }
        
        public long copy$default$1() {
            return this.queue();
        }
        
        public long copy$default$2() {
            return this.seq();
        }
        
        public String productPrefix() {
            return "QueueEntryKey";
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
                    n = BoxesRunTime.boxToLong(this.queue());
                    break;
                }
            }
            return n;
        }
        
        public Iterator<Object> productIterator() {
            return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
        }
        
        public boolean canEqual(final Object x$1) {
            return x$1 instanceof QueueEntryKey;
        }
        
        @Override
        public int hashCode() {
            return Statics.finalizeHash(Statics.mix(Statics.mix(-889275714, Statics.longHash(this.queue())), Statics.longHash(this.seq())), 2);
        }
        
        @Override
        public String toString() {
            return ScalaRunTime$.MODULE$._toString((Product)this);
        }
        
        @Override
        public boolean equals(final Object x$1) {
            if (this != x$1) {
                if (x$1 instanceof QueueEntryKey) {
                    final QueueEntryKey queueEntryKey = (QueueEntryKey)x$1;
                    if (this.queue() == queueEntryKey.queue() && this.seq() == queueEntryKey.seq() && queueEntryKey.canEqual(this)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        
        public QueueEntryKey(final long queue, final long seq) {
            this.queue = queue;
            this.seq = seq;
            Product$class.$init$((Product)this);
        }
    }
    
    public static class QueueEntryKey$ extends AbstractFunction2<Object, Object, QueueEntryKey> implements Serializable
    {
        public static final QueueEntryKey$ MODULE$;
        
        static {
            new QueueEntryKey$();
        }
        
        public final String toString() {
            return "QueueEntryKey";
        }
        
        public QueueEntryKey apply(final long queue, final long seq) {
            return new QueueEntryKey(queue, seq);
        }
        
        public Option<Tuple2<Object, Object>> unapply(final QueueEntryKey x$0) {
            return (Option<Tuple2<Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2$mcJJ$sp(x$0.queue(), x$0.seq())));
        }
        
        private Object readResolve() {
            return QueueEntryKey$.MODULE$;
        }
        
        public QueueEntryKey$() {
            MODULE$ = this;
        }
    }
}
