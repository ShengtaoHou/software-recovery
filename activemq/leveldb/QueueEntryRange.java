// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Product$class;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u000154A!\u0001\u0002A\u0017\ty\u0011+^3vK\u0016sGO]=SC:<WM\u0003\u0002\u0004\t\u00059A.\u001a<fY\u0012\u0014'BA\u0003\u0007\u0003!\t7\r^5wK6\f(BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001a\u0005\u0003\u0001\u0019I)\u0002CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0005\u0002\u000e'%\u0011AC\u0004\u0002\b!J|G-^2u!\tia#\u0003\u0002\u0018\u001d\ta1+\u001a:jC2L'0\u00192mK\")\u0011\u0004\u0001C\u00015\u00051A(\u001b8jiz\"\u0012a\u0007\t\u00039\u0001i\u0011A\u0001\u0005\b=\u0001\t\t\u0011\"\u0001\u001b\u0003\u0011\u0019w\u000e]=\t\u000f\u0001\u0002\u0011\u0011!C!C\u0005i\u0001O]8ek\u000e$\bK]3gSb,\u0012A\t\t\u0003G!j\u0011\u0001\n\u0006\u0003K\u0019\nA\u0001\\1oO*\tq%\u0001\u0003kCZ\f\u0017BA\u0015%\u0005\u0019\u0019FO]5oO\"91\u0006AA\u0001\n\u0003a\u0013\u0001\u00049s_\u0012,8\r^!sSRLX#A\u0017\u0011\u00055q\u0013BA\u0018\u000f\u0005\rIe\u000e\u001e\u0005\bc\u0001\t\t\u0011\"\u00013\u00039\u0001(o\u001c3vGR,E.Z7f]R$\"a\r\u001c\u0011\u00055!\u0014BA\u001b\u000f\u0005\r\te.\u001f\u0005\boA\n\t\u00111\u0001.\u0003\rAH%\r\u0005\bs\u0001\t\t\u0011\"\u0011;\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014X#A\u001e\u0011\u0007qz4'D\u0001>\u0015\tqd\"\u0001\u0006d_2dWm\u0019;j_:L!\u0001Q\u001f\u0003\u0011%#XM]1u_JDqA\u0011\u0001\u0002\u0002\u0013\u00051)\u0001\u0005dC:,\u0015/^1m)\t!u\t\u0005\u0002\u000e\u000b&\u0011aI\u0004\u0002\b\u0005>|G.Z1o\u0011\u001d9\u0014)!AA\u0002MBq!\u0013\u0001\u0002\u0002\u0013\u0005#*\u0001\u0005iCND7i\u001c3f)\u0005i\u0003b\u0002'\u0001\u0003\u0003%\t%T\u0001\ti>\u001cFO]5oOR\t!\u0005C\u0004P\u0001\u0005\u0005I\u0011\t)\u0002\r\u0015\fX/\u00197t)\t!\u0015\u000bC\u00048\u001d\u0006\u0005\t\u0019A\u001a\b\u000fM\u0013\u0011\u0011!E\u0001)\u0006y\u0011+^3vK\u0016sGO]=SC:<W\r\u0005\u0002\u001d+\u001a9\u0011AAA\u0001\u0012\u000316cA+X+A\u0019\u0001lW\u000e\u000e\u0003eS!A\u0017\b\u0002\u000fI,h\u000e^5nK&\u0011A,\u0017\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u0004\u0004\"B\rV\t\u0003qF#\u0001+\t\u000f1+\u0016\u0011!C#\u001b\"9\u0011-VA\u0001\n\u0003S\u0012!B1qa2L\bbB2V\u0003\u0003%\t\tZ\u0001\bk:\f\u0007\u000f\u001d7z)\t!U\rC\u0004gE\u0006\u0005\t\u0019A\u000e\u0002\u0007a$\u0003\u0007C\u0004i+\u0006\u0005I\u0011B5\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0002UB\u00111e[\u0005\u0003Y\u0012\u0012aa\u00142kK\u000e$\b")
public class QueueEntryRange implements Product, Serializable
{
    public static boolean unapply(final QueueEntryRange x$0) {
        return QueueEntryRange$.MODULE$.unapply(x$0);
    }
    
    public static QueueEntryRange apply() {
        return QueueEntryRange$.MODULE$.apply();
    }
    
    public QueueEntryRange copy() {
        return new QueueEntryRange();
    }
    
    public String productPrefix() {
        return "QueueEntryRange";
    }
    
    public int productArity() {
        return 0;
    }
    
    public Object productElement(final int x$1) {
        throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof QueueEntryRange;
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
        return x$1 instanceof QueueEntryRange && ((QueueEntryRange)x$1).canEqual(this);
    }
    
    public QueueEntryRange() {
        Product$class.$init$((Product)this);
    }
}
