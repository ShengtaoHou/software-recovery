// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.jackson.map.ObjectMapper;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005\u001dq!B\u0001\u0003\u0011\u0003y\u0011\u0001E\"mkN$XM]3e'V\u0004\bo\u001c:u\u0015\t\u0019A!\u0001\u0004he>,\bo\u001d\u0006\u0003\u000b\u0019\t!B]3qY&\u001c\u0017\r^3e\u0015\t9\u0001\"A\u0004mKZ,G\u000e\u001a2\u000b\u0005%Q\u0011\u0001C1di&4X-\\9\u000b\u0005-a\u0011AB1qC\u000eDWMC\u0001\u000e\u0003\ry'oZ\u0002\u0001!\t\u0001\u0012#D\u0001\u0003\r\u0015\u0011\"\u0001#\u0001\u0014\u0005A\u0019E.^:uKJ,GmU;qa>\u0014Ho\u0005\u0002\u0012)A\u0011Q\u0003G\u0007\u0002-)\tq#A\u0003tG\u0006d\u0017-\u0003\u0002\u001a-\t1\u0011I\\=SK\u001aDQaG\t\u0005\u0002q\ta\u0001P5oSRtD#A\b\t\u000fy\t\"\u0019!C\u0001?\u0005qA)\u0012$B+2#v,T!Q!\u0016\u0013V#\u0001\u0011\u0011\u0005\u0005BS\"\u0001\u0012\u000b\u0005\r\"\u0013aA7ba*\u0011QEJ\u0001\bU\u0006\u001c7n]8o\u0015\t9C\"\u0001\u0005d_\u0012,\u0007.Y;t\u0013\tI#E\u0001\u0007PE*,7\r^'baB,'\u000f\u0003\u0004,#\u0001\u0006I\u0001I\u0001\u0010\t\u00163\u0015)\u0016'U?6\u000b\u0005\u000bU#SA!)Q&\u0005C\u0001]\u00051A-Z2pI\u0016,\"a\f\u001a\u0015\tAZD\t\u0014\t\u0003cIb\u0001\u0001B\u00034Y\t\u0007AGA\u0001U#\t)\u0004\b\u0005\u0002\u0016m%\u0011qG\u0006\u0002\b\u001d>$\b.\u001b8h!\t)\u0012(\u0003\u0002;-\t\u0019\u0011I\\=\t\u000bqb\u0003\u0019A\u001f\u0002\u0003Q\u00042AP!1\u001d\t)r(\u0003\u0002A-\u00051\u0001K]3eK\u001aL!AQ\"\u0003\u000b\rc\u0017m]:\u000b\u0005\u00013\u0002\"B#-\u0001\u00041\u0015A\u00022vM\u001a,'\u000fE\u0002\u0016\u000f&K!\u0001\u0013\f\u0003\u000b\u0005\u0013(/Y=\u0011\u0005UQ\u0015BA&\u0017\u0005\u0011\u0011\u0015\u0010^3\t\u000f5c\u0003\u0013!a\u0001A\u00051Q.\u00199qKJDQ!L\t\u0005\u0002=+\"\u0001\u0015*\u0015\tE\u001bVk\u0018\t\u0003cI#Qa\r(C\u0002QBQ\u0001\u0010(A\u0002Q\u00032AP!R\u0011\u00151f\n1\u0001X\u0003\tIg\u000e\u0005\u0002Y;6\t\u0011L\u0003\u0002[7\u0006\u0011\u0011n\u001c\u0006\u00029\u0006!!.\u0019<b\u0013\tq\u0016LA\u0006J]B,Ho\u0015;sK\u0006l\u0007\"B'O\u0001\u0004\u0001\u0003\"B1\u0012\t\u0003\u0011\u0017AB3oG>$W\rF\u0002GG\u0016DQ\u0001\u001a1A\u0002Q\tQA^1mk\u0016Dq!\u00141\u0011\u0002\u0003\u0007\u0001\u0005C\u0003b#\u0011\u0005q\r\u0006\u0003iW2\f\bCA\u000bj\u0013\tQgC\u0001\u0003V]&$\b\"\u00023g\u0001\u0004!\u0002\"B7g\u0001\u0004q\u0017aA8viB\u0011\u0001l\\\u0005\u0003af\u0013AbT;uaV$8\u000b\u001e:fC6DQ!\u00144A\u0002\u0001Bqa]\t\u0012\u0002\u0013\u0005A/\u0001\teK\u000e|G-\u001a\u0013eK\u001a\fW\u000f\u001c;%gU\u0019Q/!\u0001\u0016\u0003YT#\u0001I<,\u0003a\u0004\"!\u001f@\u000e\u0003iT!a\u001f?\u0002\u0013Ut7\r[3dW\u0016$'BA?\u0017\u0003)\tgN\\8uCRLwN\\\u0005\u0003\u007fj\u0014\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\t\u0015\u0019$O1\u00015\u0011!\t)!EI\u0001\n\u0003)\u0018\u0001E3oG>$W\r\n3fM\u0006,H\u000e\u001e\u00133\u0001")
public final class ClusteredSupport
{
    public static ObjectMapper encode$default$2() {
        return ClusteredSupport$.MODULE$.encode$default$2();
    }
    
    public static <T> ObjectMapper decode$default$3() {
        return ClusteredSupport$.MODULE$.decode$default$3();
    }
    
    public static void encode(final Object value, final OutputStream out, final ObjectMapper mapper) {
        ClusteredSupport$.MODULE$.encode(value, out, mapper);
    }
    
    public static byte[] encode(final Object value, final ObjectMapper mapper) {
        return ClusteredSupport$.MODULE$.encode(value, mapper);
    }
    
    public static <T> T decode(final Class<T> t, final InputStream in, final ObjectMapper mapper) {
        return ClusteredSupport$.MODULE$.decode(t, in, mapper);
    }
    
    public static <T> T decode(final Class<T> t, final byte[] buffer, final ObjectMapper mapper) {
        return ClusteredSupport$.MODULE$.decode(t, buffer, mapper);
    }
    
    public static ObjectMapper DEFAULT_MAPPER() {
        return ClusteredSupport$.MODULE$.DEFAULT_MAPPER();
    }
}
