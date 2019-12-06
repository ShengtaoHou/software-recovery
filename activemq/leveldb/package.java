// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import java.nio.ByteBuffer;
import org.fusesource.hawtbuf.Buffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001!<Q!\u0001\u0002\t\u0002-\tq\u0001]1dW\u0006<WM\u0003\u0002\u0004\t\u00059A.\u001a<fY\u0012\u0014'BA\u0003\u0007\u0003!\t7\r^5wK6\f(BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\r\u001b5\t!AB\u0003\u000f\u0005!\u0005qBA\u0004qC\u000e\\\u0017mZ3\u0014\u00055\u0001\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\rC\u0003\u0018\u001b\u0011\u0005\u0001$\u0001\u0004=S:LGO\u0010\u000b\u0002\u0017!9!$\u0004b\u0001\n\u000bY\u0012AB*oCB\u0004\u00180F\u0001\u001d!\tib$D\u0001\u000e\r\u001dyR\u0002%A\u0002\u0002\u0001\u00121b\u00158baBLHK]1jiN\u0011a\u0004\u0005\u0005\u0006Ey!\taI\u0001\u0007I%t\u0017\u000e\u001e\u0013\u0015\u0003\u0011\u0002\"!E\u0013\n\u0005\u0019\u0012\"\u0001B+oSRDQ\u0001\u000b\u0010\u0007\u0002%\n1#\u001e8d_6\u0004(/Z:tK\u0012|F.\u001a8hi\"$\"AK\u0017\u0011\u0005EY\u0013B\u0001\u0017\u0013\u0005\rIe\u000e\u001e\u0005\u0006]\u001d\u0002\raL\u0001\u0006S:\u0004X\u000f\u001e\t\u0003aUj\u0011!\r\u0006\u0003eM\nq\u0001[1xi\n,hM\u0003\u00025\u0011\u0005Qa-^:fg>,(oY3\n\u0005Y\n$A\u0002\"vM\u001a,'\u000fC\u00039=\u0019\u0005\u0011(\u0001\u0006v]\u000e|W\u000e\u001d:fgN$2A\u000b\u001e<\u0011\u0015qs\u00071\u00010\u0011\u0015at\u00071\u00010\u0003\u0019yW\u000f\u001e9vi\")aH\bD\u0001\u007f\u0005)R.\u0019=`G>l\u0007O]3tg\u0016$w\f\\3oORDGC\u0001\u0016A\u0011\u0015\tU\b1\u0001+\u0003\u0019aWM\\4uQ\")1I\bD\u0001\t\u0006A1m\\7qe\u0016\u001c8\u000fF\u0002+\u000b\u001aCQA\f\"A\u0002=BQ\u0001\u0010\"A\u0002=BQa\u0011\u0010\u0005\u0002!#\"aL%\t\u000b9:\u0005\u0019A\u0018\t\u000b\rsB\u0011A&\u0015\u0005=b\u0005\"B'K\u0001\u0004q\u0015\u0001\u0002;fqR\u0004\"a\u0014*\u000f\u0005E\u0001\u0016BA)\u0013\u0003\u0019\u0001&/\u001a3fM&\u00111\u000b\u0016\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005E\u0013\u0002\"\u0002\u001d\u001f\t\u00031FCA\u0018X\u0011\u0015qS\u000b1\u00010\u0011\u0015Ad\u0004\"\u0001Z)\rQ#\f\u001a\u0005\u00067b\u0003\r\u0001X\u0001\u000bG>l\u0007O]3tg\u0016$\u0007CA/c\u001b\u0005q&BA0a\u0003\rq\u0017n\u001c\u0006\u0002C\u0006!!.\u0019<b\u0013\t\u0019gL\u0001\u0006CsR,')\u001e4gKJDQ!\u001a-A\u0002q\u000bA\"\u001e8d_6\u0004(/Z:tK\u0012DaaZ\u0007!\u0002\u001ba\u0012aB*oCB\u0004\u0018\u0010\t")
public final class package
{
    public static SnappyTrait Snappy() {
        return package$.MODULE$.Snappy();
    }
    
    public abstract static class SnappyTrait$class
    {
        public static Buffer compress(final SnappyTrait $this, final Buffer input) {
            final Buffer compressed = new Buffer($this.max_compressed_length(input.length));
            compressed.length = $this.compress(input, compressed);
            return compressed;
        }
        
        public static Buffer compress(final SnappyTrait $this, final String text) {
            final Buffer uncompressed = new Buffer(text.getBytes("UTF-8"));
            final Buffer compressed = new Buffer($this.max_compressed_length(uncompressed.length));
            compressed.length = $this.compress(uncompressed, compressed);
            return compressed;
        }
        
        public static Buffer uncompress(final SnappyTrait $this, final Buffer input) {
            final Buffer uncompressed = new Buffer($this.uncompressed_length(input));
            uncompressed.length = $this.uncompress(input, uncompressed);
            return uncompressed;
        }
        
        public static int uncompress(final SnappyTrait $this, final ByteBuffer compressed, final ByteBuffer uncompressed) {
            Buffer buffer;
            if (compressed.hasArray()) {
                buffer = new Buffer(compressed.array(), compressed.arrayOffset() + compressed.position(), compressed.remaining());
            }
            else {
                final Buffer t = new Buffer(compressed.remaining());
                compressed.mark();
                compressed.get(t.data);
                compressed.reset();
                buffer = t;
            }
            final Buffer input = buffer;
            final Buffer output = uncompressed.hasArray() ? new Buffer(uncompressed.array(), uncompressed.arrayOffset() + uncompressed.position(), uncompressed.capacity() - uncompressed.position()) : new Buffer($this.uncompressed_length(input));
            output.length = $this.uncompress(input, output);
            if (uncompressed.hasArray()) {
                uncompressed.limit(uncompressed.position() + output.length);
            }
            else {
                final int p = uncompressed.position();
                uncompressed.limit(uncompressed.capacity());
                uncompressed.put(output.data, output.offset, output.length);
                uncompressed.flip().position(p);
            }
            return output.length;
        }
        
        public static void $init$(final SnappyTrait $this) {
        }
    }
    
    public interface SnappyTrait
    {
        int uncompressed_length(final Buffer p0);
        
        int uncompress(final Buffer p0, final Buffer p1);
        
        int max_compressed_length(final int p0);
        
        int compress(final Buffer p0, final Buffer p1);
        
        Buffer compress(final Buffer p0);
        
        Buffer compress(final String p0);
        
        Buffer uncompress(final Buffer p0);
        
        int uncompress(final ByteBuffer p0, final ByteBuffer p1);
    }
}
