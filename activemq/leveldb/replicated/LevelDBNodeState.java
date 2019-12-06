// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.replicated.groups.NodeState$class;
import org.apache.activemq.leveldb.util.JsonCodec$;
import org.codehaus.jackson.annotate.JsonProperty;
import scala.reflect.ScalaSignature;
import org.apache.activemq.leveldb.replicated.groups.NodeState;

@ScalaSignature(bytes = "\u0006\u0001\u0005Ea\u0001B\u0001\u0003\u00015\u0011\u0001\u0003T3wK2$%IT8eKN#\u0018\r^3\u000b\u0005\r!\u0011A\u0003:fa2L7-\u0019;fI*\u0011QAB\u0001\bY\u00164X\r\u001c3c\u0015\t9\u0001\"\u0001\u0005bGRLg/Z7r\u0015\tI!\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0017\u0005\u0019qN]4\u0004\u0001M\u0019\u0001A\u0004\u000b\u0011\u0005=\u0011R\"\u0001\t\u000b\u0003E\tQa]2bY\u0006L!a\u0005\t\u0003\r\u0005s\u0017PU3g!\t)\u0002$D\u0001\u0017\u0015\t9\"!\u0001\u0004he>,\bo]\u0005\u00033Y\u0011\u0011BT8eKN#\u0018\r^3\t\u000bm\u0001A\u0011\u0001\u000f\u0002\rqJg.\u001b;?)\u0005i\u0002C\u0001\u0010\u0001\u001b\u0005\u0011\u0001\"\u0003\u0011\u0001\u0001\u0004\u0005\r\u0011\"\u0001\"\u0003\tIG-F\u0001#!\t\u0019cE\u0004\u0002\u0010I%\u0011Q\u0005E\u0001\u0007!J,G-\u001a4\n\u0005\u001dB#AB*ue&twM\u0003\u0002&!!I!\u0006\u0001a\u0001\u0002\u0004%\taK\u0001\u0007S\u0012|F%Z9\u0015\u00051z\u0003CA\b.\u0013\tq\u0003C\u0001\u0003V]&$\bb\u0002\u0019*\u0003\u0003\u0005\rAI\u0001\u0004q\u0012\n\u0004B\u0002\u001a\u0001A\u0003&!%A\u0002jI\u0002B#!\r\u001b\u0011\u0005UbT\"\u0001\u001c\u000b\u0005]B\u0014\u0001C1o]>$\u0018\r^3\u000b\u0005eR\u0014a\u00026bG.\u001cxN\u001c\u0006\u0003w)\t\u0001bY8eK\"\fWo]\u0005\u0003{Y\u0012ABS:p]B\u0013x\u000e]3sifD\u0011b\u0010\u0001A\u0002\u0003\u0007I\u0011A\u0011\u0002\u0013\r|g\u000e^1j]\u0016\u0014\b\"C!\u0001\u0001\u0004\u0005\r\u0011\"\u0001C\u00035\u0019wN\u001c;bS:,'o\u0018\u0013fcR\u0011Af\u0011\u0005\ba\u0001\u000b\t\u00111\u0001#\u0011\u0019)\u0005\u0001)Q\u0005E\u0005Q1m\u001c8uC&tWM\u001d\u0011)\u0005\u0011#\u0004\"\u0003%\u0001\u0001\u0004\u0005\r\u0011\"\u0001\"\u0003\u001d\tG\r\u001a:fgND\u0011B\u0013\u0001A\u0002\u0003\u0007I\u0011A&\u0002\u0017\u0005$GM]3tg~#S-\u001d\u000b\u0003Y1Cq\u0001M%\u0002\u0002\u0003\u0007!\u0005\u0003\u0004O\u0001\u0001\u0006KAI\u0001\tC\u0012$'/Z:tA!\u0012Q\n\u000e\u0005\b#\u0002\u0001\r\u0011\"\u0001S\u0003!\u0001xn]5uS>tW#A*\u0011\u0005=!\u0016BA+\u0011\u0005\u0011auN\\4\t\u000f]\u0003\u0001\u0019!C\u00011\u0006a\u0001o\\:ji&|gn\u0018\u0013fcR\u0011A&\u0017\u0005\baY\u000b\t\u00111\u0001T\u0011\u0019Y\u0006\u0001)Q\u0005'\u0006I\u0001o\\:ji&|g\u000e\t\u0015\u00035RBqA\u0018\u0001A\u0002\u0013\u0005q,\u0001\u0004xK&<\u0007\u000e^\u000b\u0002AB\u0011q\"Y\u0005\u0003EB\u00111!\u00138u\u0011\u001d!\u0007\u00011A\u0005\u0002\u0015\f!b^3jO\"$x\fJ3r)\tac\rC\u00041G\u0006\u0005\t\u0019\u00011\t\r!\u0004\u0001\u0015)\u0003a\u0003\u001d9X-[4ii\u0002B#a\u001a\u001b\t\u0013-\u0004\u0001\u0019!a\u0001\n\u0003\t\u0013aB3mK\u000e$X\r\u001a\u0005\n[\u0002\u0001\r\u00111A\u0005\u00029\f1\"\u001a7fGR,Gm\u0018\u0013fcR\u0011Af\u001c\u0005\ba1\f\t\u00111\u0001#\u0011\u0019\t\b\u0001)Q\u0005E\u0005AQ\r\\3di\u0016$\u0007\u0005\u000b\u0002qi!)A\u000f\u0001C!k\u00061Q-];bYN$\"A^=\u0011\u0005=9\u0018B\u0001=\u0011\u0005\u001d\u0011un\u001c7fC:DQA_:A\u0002m\f1a\u001c2k!\tyA0\u0003\u0002~!\t\u0019\u0011I\\=\t\r}\u0004A\u0011IA\u0001\u0003!!xn\u0015;sS:<GCAA\u0002!\u0011\t)!a\u0004\u000e\u0005\u0005\u001d!\u0002BA\u0005\u0003\u0017\tA\u0001\\1oO*\u0011\u0011QB\u0001\u0005U\u00064\u0018-C\u0002(\u0003\u000f\u0001")
public class LevelDBNodeState implements NodeState
{
    @JsonProperty
    private String id;
    @JsonProperty
    private String container;
    @JsonProperty
    private String address;
    @JsonProperty
    private long position;
    @JsonProperty
    private int weight;
    @JsonProperty
    private String elected;
    
    @Override
    public String id() {
        return this.id;
    }
    
    public void id_$eq(final String x$1) {
        this.id = x$1;
    }
    
    public String container() {
        return this.container;
    }
    
    public void container_$eq(final String x$1) {
        this.container = x$1;
    }
    
    public String address() {
        return this.address;
    }
    
    public void address_$eq(final String x$1) {
        this.address = x$1;
    }
    
    public long position() {
        return this.position;
    }
    
    public void position_$eq(final long x$1) {
        this.position = x$1;
    }
    
    public int weight() {
        return this.weight;
    }
    
    public void weight_$eq(final int x$1) {
        this.weight = x$1;
    }
    
    public String elected() {
        return this.elected;
    }
    
    public void elected_$eq(final String x$1) {
        this.elected = x$1;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean b2;
        if (obj instanceof LevelDBNodeState) {
            final LevelDBNodeState levelDBNodeState = (LevelDBNodeState)obj;
            final String id = levelDBNodeState.id();
            final String id2 = this.id();
            boolean b = false;
            Label_0155: {
                Label_0154: {
                    if (id == null) {
                        if (id2 != null) {
                            break Label_0154;
                        }
                    }
                    else if (!id.equals(id2)) {
                        break Label_0154;
                    }
                    final String container = levelDBNodeState.container();
                    final String container2 = this.container();
                    if (container == null) {
                        if (container2 != null) {
                            break Label_0154;
                        }
                    }
                    else if (!container.equals(container2)) {
                        break Label_0154;
                    }
                    final String address = levelDBNodeState.address();
                    final String address2 = this.address();
                    if (address == null) {
                        if (address2 != null) {
                            break Label_0154;
                        }
                    }
                    else if (!address.equals(address2)) {
                        break Label_0154;
                    }
                    if (levelDBNodeState.position() == this.position()) {
                        final String elected = levelDBNodeState.elected();
                        final String elected2 = this.elected();
                        if (elected == null) {
                            if (elected2 != null) {
                                break Label_0154;
                            }
                        }
                        else if (!elected.equals(elected2)) {
                            break Label_0154;
                        }
                        b = true;
                        break Label_0155;
                    }
                }
                b = false;
            }
            b2 = b;
        }
        else {
            b2 = false;
        }
        return b2;
    }
    
    @Override
    public String toString() {
        return JsonCodec$.MODULE$.encode(this).ascii().toString();
    }
    
    public LevelDBNodeState() {
        NodeState$class.$init$(this);
        this.position = -1L;
        this.weight = 0;
    }
}
