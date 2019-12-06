// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.InputStream;
import org.fusesource.hawtbuf.Buffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\t<Q!\u0001\u0002\t\u00025\t\u0011BS:p]\u000e{G-Z2\u000b\u0005\r!\u0011\u0001B;uS2T!!\u0002\u0004\u0002\u000f1,g/\u001a7eE*\u0011q\u0001C\u0001\tC\u000e$\u0018N^3nc*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u0001\"AD\b\u000e\u0003\t1Q\u0001\u0005\u0002\t\u0002E\u0011\u0011BS:p]\u000e{G-Z2\u0014\u0005=\u0011\u0002CA\n\u0017\u001b\u0005!\"\"A\u000b\u0002\u000bM\u001c\u0017\r\\1\n\u0005]!\"AB!osJ+g\rC\u0003\u001a\u001f\u0011\u0005!$\u0001\u0004=S:LGO\u0010\u000b\u0002\u001b!9Ad\u0004b\u0001\n\u000bi\u0012AB7baB,'/F\u0001\u001f!\tyb%D\u0001!\u0015\t\t#%A\u0002nCBT!a\t\u0013\u0002\u000f)\f7m[:p]*\u0011QEC\u0001\tG>$W\r[1vg&\u0011q\u0005\t\u0002\r\u001f\nTWm\u0019;NCB\u0004XM\u001d\u0005\u0007S=\u0001\u000bQ\u0002\u0010\u0002\u000f5\f\u0007\u000f]3sA!)1f\u0004C\u0001Y\u00051A-Z2pI\u0016,\"!\f\u0019\u0015\u00079J4\t\u0005\u00020a1\u0001A!B\u0019+\u0005\u0004\u0011$!\u0001+\u0012\u0005M2\u0004CA\n5\u0013\t)DCA\u0004O_RD\u0017N\\4\u0011\u0005M9\u0014B\u0001\u001d\u0015\u0005\r\te.\u001f\u0005\u0006u)\u0002\raO\u0001\u0007EV4g-\u001a:\u0011\u0005q\nU\"A\u001f\u000b\u0005yz\u0014a\u00025boR\u0014WO\u001a\u0006\u0003\u0001*\t!BZ;tKN|WO]2f\u0013\t\u0011UH\u0001\u0004Ck\u001a4WM\u001d\u0005\u0006\t*\u0002\r!R\u0001\u0006G2\f'P\u001f\t\u0004\r&scBA\nH\u0013\tAE#\u0001\u0004Qe\u0016$WMZ\u0005\u0003\u0015.\u0013Qa\u00117bgNT!\u0001\u0013\u000b\t\u000b-zA\u0011A'\u0016\u00059\u0003FcA(R7B\u0011q\u0006\u0015\u0003\u0006c1\u0013\rA\r\u0005\u0006%2\u0003\raU\u0001\u0003SN\u0004\"\u0001V-\u000e\u0003US!AV,\u0002\u0005%|'\"\u0001-\u0002\t)\fg/Y\u0005\u00035V\u00131\"\u00138qkR\u001cFO]3b[\")A\t\u0014a\u00019B\u0019a)S(\t\u000by{A\u0011A0\u0002\r\u0015t7m\u001c3f)\tY\u0004\rC\u0003b;\u0002\u0007!#A\u0003wC2,X\r")
public final class JsonCodec
{
    public static Buffer encode(final Object value) {
        return JsonCodec$.MODULE$.encode(value);
    }
    
    public static <T> T decode(final InputStream is, final Class<T> clazz) {
        return JsonCodec$.MODULE$.decode(is, clazz);
    }
    
    public static <T> T decode(final Buffer buffer, final Class<T> clazz) {
        return JsonCodec$.MODULE$.decode(buffer, clazz);
    }
    
    public static ObjectMapper mapper() {
        return JsonCodec$.MODULE$.mapper();
    }
}
