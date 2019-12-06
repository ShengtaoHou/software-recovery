// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import org.iq80.snappy.Snappy;
import java.nio.ByteBuffer;
import org.fusesource.hawtbuf.Buffer;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001y2A!\u0001\u0002\u0001\u0017\tQ\u0011*\u0015\u001d1':\f\u0007\u000f]=\u000b\u0005\r!\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000b\u0019\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0007\u0001a!\u0003\u0005\u0002\u000e!5\taBC\u0001\u0010\u0003\u0015\u00198-\u00197b\u0013\t\tbB\u0001\u0004B]f\u0014VM\u001a\t\u0003']q!\u0001F\u000b\u000e\u0003\tI!A\u0006\u0002\u0002\u000fA\f7m[1hK&\u0011\u0001$\u0007\u0002\f':\f\u0007\u000f]=Ue\u0006LGO\u0003\u0002\u0017\u0005!)1\u0004\u0001C\u00019\u00051A(\u001b8jiz\"\u0012!\b\t\u0003)\u0001AQa\b\u0001\u0005\u0002\u0001\n1#\u001e8d_6\u0004(/Z:tK\u0012|F.\u001a8hi\"$\"!\t\u0013\u0011\u00055\u0011\u0013BA\u0012\u000f\u0005\rIe\u000e\u001e\u0005\u0006Ky\u0001\rAJ\u0001\u0006S:\u0004X\u000f\u001e\t\u0003O1j\u0011\u0001\u000b\u0006\u0003S)\nq\u0001[1xi\n,hM\u0003\u0002,\u0011\u0005Qa-^:fg>,(oY3\n\u00055B#A\u0002\"vM\u001a,'\u000fC\u00030\u0001\u0011\u0005\u0001'\u0001\u0006v]\u000e|W\u000e\u001d:fgN$2!I\u00193\u0011\u0015)c\u00061\u0001'\u0011\u0015\u0019d\u00061\u0001'\u0003\u0019yW\u000f\u001e9vi\")Q\u0007\u0001C\u0001m\u0005A1m\\7qe\u0016\u001c8\u000fF\u0002\"oaBQ!\n\u001bA\u0002\u0019BQa\r\u001bA\u0002\u0019BQA\u000f\u0001\u0005\u0002m\nQ#\\1y?\u000e|W\u000e\u001d:fgN,Gm\u00187f]\u001e$\b\u000e\u0006\u0002\"y!)Q(\u000fa\u0001C\u00051A.\u001a8hi\"\u0004")
public class IQ80Snappy implements package.SnappyTrait
{
    @Override
    public Buffer compress(final Buffer input) {
        return package.SnappyTrait$class.compress(this, input);
    }
    
    @Override
    public Buffer compress(final String text) {
        return package.SnappyTrait$class.compress(this, text);
    }
    
    @Override
    public Buffer uncompress(final Buffer input) {
        return package.SnappyTrait$class.uncompress(this, input);
    }
    
    @Override
    public int uncompress(final ByteBuffer compressed, final ByteBuffer uncompressed) {
        return package.SnappyTrait$class.uncompress(this, compressed, uncompressed);
    }
    
    @Override
    public int uncompressed_length(final Buffer input) {
        return Snappy.getUncompressedLength(input.data, input.offset);
    }
    
    @Override
    public int uncompress(final Buffer input, final Buffer output) {
        return Snappy.uncompress(input.data, input.offset, input.length, output.data, output.offset);
    }
    
    @Override
    public int compress(final Buffer input, final Buffer output) {
        return Snappy.compress(input.data, input.offset, input.length, output.data, output.offset);
    }
    
    @Override
    public int max_compressed_length(final int length) {
        return Snappy.maxCompressedLength(length);
    }
    
    public IQ80Snappy() {
        package.SnappyTrait$class.$init$(this);
    }
}
