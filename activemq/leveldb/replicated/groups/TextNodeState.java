// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import org.codehaus.jackson.annotate.JsonProperty;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u000113A!\u0001\u0002\u0001\u001f\tiA+\u001a=u\u001d>$Wm\u0015;bi\u0016T!a\u0001\u0003\u0002\r\u001d\u0014x.\u001e9t\u0015\t)a!\u0001\u0006sKBd\u0017nY1uK\u0012T!a\u0002\u0005\u0002\u000f1,g/\u001a7eE*\u0011\u0011BC\u0001\tC\u000e$\u0018N^3nc*\u00111\u0002D\u0001\u0007CB\f7\r[3\u000b\u00035\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\t\u0017!\t\tB#D\u0001\u0013\u0015\u0005\u0019\u0012!B:dC2\f\u0017BA\u000b\u0013\u0005\u0019\te.\u001f*fMB\u0011q\u0003G\u0007\u0002\u0005%\u0011\u0011D\u0001\u0002\n\u001d>$Wm\u0015;bi\u0016DQa\u0007\u0001\u0005\u0002q\ta\u0001P5oSRtD#A\u000f\u0011\u0005]\u0001\u0001\"C\u0010\u0001\u0001\u0004\u0005\r\u0011\"\u0001!\u0003\tIG-F\u0001\"!\t\u0011s%D\u0001$\u0015\t!S%\u0001\u0003mC:<'\"\u0001\u0014\u0002\t)\fg/Y\u0005\u0003Q\r\u0012aa\u0015;sS:<\u0007\"\u0003\u0016\u0001\u0001\u0004\u0005\r\u0011\"\u0001,\u0003\u0019IGm\u0018\u0013fcR\u0011Af\f\t\u0003#5J!A\f\n\u0003\tUs\u0017\u000e\u001e\u0005\ba%\n\t\u00111\u0001\"\u0003\rAH%\r\u0005\u0007e\u0001\u0001\u000b\u0015B\u0011\u0002\u0007%$\u0007\u0005\u000b\u00022iA\u0011Q\u0007P\u0007\u0002m)\u0011q\u0007O\u0001\tC:tw\u000e^1uK*\u0011\u0011HO\u0001\bU\u0006\u001c7n]8o\u0015\tYD\"\u0001\u0005d_\u0012,\u0007.Y;t\u0013\tidG\u0001\u0007Kg>t\u0007K]8qKJ$\u0018\u0010\u000b\u00022\u007fA\u0011\u0001iQ\u0007\u0002\u0003*\u0011!IE\u0001\u0006E\u0016\fgn]\u0005\u0003\t\u0006\u0013ABQ3b]B\u0013x\u000e]3sifDQA\u0012\u0001\u0005\u0002\u001d\u000bQaZ3u\u0013\u0012$\u0012!\t\u0005\u0006\u0013\u0002!\tAS\u0001\u0006g\u0016$\u0018\n\u001a\u000b\u0003Y-Cq\u0001\r%\u0002\u0002\u0003\u0007\u0011\u0005")
public class TextNodeState implements NodeState
{
    @JsonProperty
    private String id;
    
    @Override
    public String toString() {
        return NodeState$class.toString(this);
    }
    
    @Override
    public String id() {
        return this.id;
    }
    
    public void id_$eq(final String x$1) {
        this.id = x$1;
    }
    
    public void setId(final String x$1) {
        this.id = x$1;
    }
    
    public String getId() {
        return this.id();
    }
    
    public TextNodeState() {
        NodeState$class.$init$(this);
    }
}
