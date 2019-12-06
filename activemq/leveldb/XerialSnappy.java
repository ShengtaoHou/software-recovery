// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import org.xerial.snappy.Snappy;
import java.nio.ByteBuffer;
import org.fusesource.hawtbuf.Buffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001]3A!\u0001\u0002\u0001\u0017\ta\u0001,\u001a:jC2\u001cf.\u00199qs*\u00111\u0001B\u0001\bY\u00164X\r\u001c3c\u0015\t)a!\u0001\u0005bGRLg/Z7r\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M\u0019\u0001\u0001\u0004\n\u0011\u00055\u0001R\"\u0001\b\u000b\u0003=\tQa]2bY\u0006L!!\u0005\b\u0003\r\u0005s\u0017PU3g!\t\u0019rC\u0004\u0002\u0015+5\t!!\u0003\u0002\u0017\u0005\u00059\u0001/Y2lC\u001e,\u0017B\u0001\r\u001a\u0005-\u0019f.\u00199qsR\u0013\u0018-\u001b;\u000b\u0005Y\u0011\u0001\"B\u000e\u0001\t\u0003a\u0012A\u0002\u001fj]&$h\bF\u0001\u001e!\t!\u0002\u0001C\u0003 \u0001\u0011\u0005\u0003%\u0001\u0006v]\u000e|W\u000e\u001d:fgN$2!\t\u0013/!\ti!%\u0003\u0002$\u001d\t\u0019\u0011J\u001c;\t\u000b\u0015r\u0002\u0019\u0001\u0014\u0002\u0015\r|W\u000e\u001d:fgN,G\r\u0005\u0002(Y5\t\u0001F\u0003\u0002*U\u0005\u0019a.[8\u000b\u0003-\nAA[1wC&\u0011Q\u0006\u000b\u0002\u000b\u0005f$XMQ;gM\u0016\u0014\b\"B\u0018\u001f\u0001\u00041\u0013\u0001D;oG>l\u0007O]3tg\u0016$\u0007\"B\u0019\u0001\t\u0003\u0011\u0014aE;oG>l\u0007O]3tg\u0016$w\f\\3oORDGCA\u00114\u0011\u0015!\u0004\u00071\u00016\u0003\u0015Ig\u000e];u!\t14(D\u00018\u0015\tA\u0014(A\u0004iC^$(-\u001e4\u000b\u0005iB\u0011A\u00034vg\u0016\u001cx.\u001e:dK&\u0011Ah\u000e\u0002\u0007\u0005V4g-\u001a:\t\u000b}\u0001A\u0011\u0001 \u0015\u0007\u0005z\u0004\tC\u00035{\u0001\u0007Q\u0007C\u0003B{\u0001\u0007Q'\u0001\u0004pkR\u0004X\u000f\u001e\u0005\u0006\u0007\u0002!\t\u0001R\u0001\u0016[\u0006DxlY8naJ,7o]3e?2,gn\u001a;i)\t\tS\tC\u0003G\u0005\u0002\u0007\u0011%\u0001\u0004mK:<G\u000f\u001b\u0005\u0006\u0011\u0002!\t!S\u0001\tG>l\u0007O]3tgR\u0019\u0011ES&\t\u000bQ:\u0005\u0019A\u001b\t\u000b\u0005;\u0005\u0019A\u001b\t\u000b!\u0003A\u0011I'\u0015\u0005Ur\u0005\"B(M\u0001\u0004\u0001\u0016\u0001\u0002;fqR\u0004\"!\u0015+\u000f\u00055\u0011\u0016BA*\u000f\u0003\u0019\u0001&/\u001a3fM&\u0011QK\u0016\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005Ms\u0001")
public class XerialSnappy implements package.SnappyTrait
{
    @Override
    public Buffer compress(final Buffer input) {
        return package.SnappyTrait$class.compress(this, input);
    }
    
    @Override
    public Buffer uncompress(final Buffer input) {
        return package.SnappyTrait$class.uncompress(this, input);
    }
    
    @Override
    public int uncompress(final ByteBuffer compressed, final ByteBuffer uncompressed) {
        return Snappy.uncompress(compressed, uncompressed);
    }
    
    @Override
    public int uncompressed_length(final Buffer input) {
        return Snappy.uncompressedLength(input.data, input.offset, input.length);
    }
    
    @Override
    public int uncompress(final Buffer input, final Buffer output) {
        return Snappy.uncompress(input.data, input.offset, input.length, output.data, output.offset);
    }
    
    @Override
    public int max_compressed_length(final int length) {
        return Snappy.maxCompressedLength(length);
    }
    
    @Override
    public int compress(final Buffer input, final Buffer output) {
        return Snappy.compress(input.data, input.offset, input.length, output.data, output.offset);
    }
    
    @Override
    public Buffer compress(final String text) {
        return new Buffer(Snappy.compress(text));
    }
    
    public XerialSnappy() {
        package.SnappyTrait$class.$init$(this);
    }
}
