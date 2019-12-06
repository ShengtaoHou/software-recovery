// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.Predef$;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtdispatch.transport.ProtocolCodec;
import scala.MatchError;
import scala.runtime.BoxedUnit;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.fusesource.hawtbuf.Buffer;
import java.nio.MappedByteBuffer;
import java.util.LinkedList;
import scala.reflect.ScalaSignature;
import org.fusesource.hawtdispatch.transport.AbstractProtocolCodec;

@ScalaSignature(bytes = "\u0006\u0001\u0005]c\u0001B\u0001\u0003\u00015\u0011\u0001DU3qY&\u001c\u0017\r^5p]B\u0013x\u000e^8d_2\u001cu\u000eZ3d\u0015\t\u0019A!\u0001\u0006sKBd\u0017nY1uK\u0012T!!\u0002\u0004\u0002\u000f1,g/\u001a7eE*\u0011q\u0001C\u0001\tC\u000e$\u0018N^3nc*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\b\u0011\u0005=1R\"\u0001\t\u000b\u0005E\u0011\u0012!\u0003;sC:\u001c\bo\u001c:u\u0015\t\u0019B#\u0001\u0007iC^$H-[:qCR\u001c\u0007N\u0003\u0002\u0016\u0015\u0005Qa-^:fg>,(oY3\n\u0005]\u0001\"!F!cgR\u0014\u0018m\u0019;Qe>$xnY8m\u0007>$Wm\u0019\u0005\u00063\u0001!\tAG\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0003m\u0001\"\u0001\b\u0001\u000e\u0003\tAqA\b\u0001C\u0002\u0013\u0005q$A\u0005ue\u0006t7OZ3sgV\t\u0001\u0005E\u0002\"M!j\u0011A\t\u0006\u0003G\u0011\nA!\u001e;jY*\tQ%\u0001\u0003kCZ\f\u0017BA\u0014#\u0005)a\u0015N\\6fI2K7\u000f\u001e\t\u0003S1j\u0011A\u000b\u0006\u0003W\u0011\n1A\\5p\u0013\ti#F\u0001\tNCB\u0004X\r\u001a\"zi\u0016\u0014UO\u001a4fe\"1q\u0006\u0001Q\u0001\n\u0001\n!\u0002\u001e:b]N4WM]:!\u0011\u0015\t\u0004\u0001\"\u00013\u0003\u0019)gnY8eKR\u00111'\u000f\t\u0003i]j\u0011!\u000e\u0006\u0002m\u0005)1oY1mC&\u0011\u0001(\u000e\u0002\u0005+:LG\u000fC\u0003;a\u0001\u00071(A\u0003wC2,X\r\u0005\u00025y%\u0011Q(\u000e\u0002\u0004\u0003:L\b\"B \u0001\t\u0003\u0002\u0015!\u00024mkNDG#A!\u0011\u0005\t\u0003fBA\"O\u001d\t!UJ\u0004\u0002F\u0019:\u0011ai\u0013\b\u0003\u000f*k\u0011\u0001\u0013\u0006\u0003\u00132\ta\u0001\u0010:p_Rt\u0014\"A\u0006\n\u0005UQ\u0011BA\n\u0015\u0013\t\t\"#\u0003\u0002P!\u0005i\u0001K]8u_\u000e|GnQ8eK\u000eL!!\u0015*\u0003\u0017\t+hMZ3s'R\fG/\u001a\u0006\u0003\u001fBAQ\u0001\u0016\u0001\u0005\u0002U\u000b1#\u001b8ji&\fG\u000eR3d_\u0012,\u0017i\u0019;j_:$\u0012A\u0016\n\u0004/n\u000bg\u0001\u0002-Z\u0001Y\u0013A\u0002\u0010:fM&tW-\\3oizBaA\u0017\u0001!\u0002\u00131\u0016a\u0003:fC\u0012DU-\u00193fe\u0002\u0002\"\u0001X0\u000e\u0003uS!A\u0018\u0013\u0002\t1\fgnZ\u0005\u0003Av\u0013aa\u00142kK\u000e$\bC\u00012f\u001d\t\u00195-\u0003\u0002e!\u0005)\u0012IY:ue\u0006\u001cG\u000f\u0015:pi>\u001cw\u000e\\\"pI\u0016\u001c\u0017B\u00014h\u0005\u0019\t5\r^5p]*\u0011A\r\u0005\u0005\bS\u0002\u0011\r\u0011\"\u0001k\u0003)\u0011X-\u00193IK\u0006$WM]\u000b\u0002-\")A\u000e\u0001C\u0001[\u0006!\"/Z1e%\u0016\u0004H.[2bi&|gN\u0012:b[\u0016$\"!\u00198\t\u000b=\\\u0007\u0019\u00019\u0002\r\u0005\u001cG/[8o!\t\tH/D\u0001s\u0015\t\u0019H#A\u0004iC^$(-\u001e4\n\u0005U\u0014(aC!tG&L')\u001e4gKJDQa\u001e\u0001\u0005\u0002a\f\u0001B]3bI\u0012\u000bG/\u0019\u000b\u0004get\b\"\u0002>w\u0001\u0004Y\u0018a\u00033bi\u0006|F/\u0019:hKR\u0004\"!\u000b?\n\u0005uT#A\u0003\"zi\u0016\u0014UO\u001a4fe\"1qP\u001ea\u0001\u0003\u0003\t!a\u00192\u0011\t\u0005\r\u0011QA\u0007\u0002%%\u0019\u0011q\u0001\n\u0003\tQ\u000b7o\u001b\u0005\r\u0003\u0017\u0001\u0001\u0013!A\u0001\u0002\u0013\u0005\u0011QB\u0001\u0014aJ|G/Z2uK\u0012$#/Z1e+:$\u0018\u000e\u001c\u000b\u0005\u0003\u001f\tY\u0003\u0006\u0004\u0002\u0012\u0005]\u0011\u0011\u0005\t\u0004c\u0006M\u0011bAA\u000be\n1!)\u001e4gKJD!\"!\u0007\u0002\n\u0005\u0005\t\u0019AA\u000e\u0003\rAH%\r\t\u00049\u0006u\u0011bAA\u0010;\n!!)\u001f;f\u0011)\t\u0019#!\u0003\u0002\u0002\u0003\u0007\u0011QE\u0001\u0004q\u0012\u0012\u0004c\u0001\u001b\u0002(%\u0019\u0011\u0011F\u001b\u0003\u0007%sG\u000fC\u0005\u0002\u001a\u0005%\u0011\u0011!a\u00017!a\u0011q\u0006\u0001\u0011\u0002\u0003\u0005\t\u0011\"\u0001\u00022\u0005i\u0002O]8uK\u000e$X\r\u001a\u0013tKRtW\r\u001f;EK\u000e|G-Z!di&|g\u000eF\u00034\u0003g\t)\u0004C\u0005\u0002\u001a\u00055\u0012\u0011!a\u00017!Q\u00111EA\u0017\u0003\u0003\u0005\r!a\u000e\u0011\u0007\u0005eR-D\u0001h\u00111\ti\u0004\u0001I\u0001\u0002\u0003\u0005I\u0011AA \u0003i\u0001(o\u001c;fGR,G\r\n8fqR$UmY8eK\u0006\u001bG/[8o)\u0011\t9$!\u0011\t\u0015\u0005e\u00111HA\u0001\u0002\u0004\t\u0019%D\u0001\u0001\u00111\t9\u0005\u0001I\u0001\u0002\u0003\u0005I\u0011AA%\u0003Q\u0001(o\u001c;fGR,G\r\n:fC\u0012$\u0015N]3diR!\u00111JA+)\u0011\ti%a\u0015\u0011\u0007q\u000by%C\u0002\u0002Ru\u0013qAQ8pY\u0016\fg\u000eC\u0005\u0002\u001a\u0005\u0015\u0013\u0011!a\u0001w\"I\u0011\u0011DA#\u0003\u0003\u0005\ra\u0007")
public class ReplicationProtocolCodec extends AbstractProtocolCodec
{
    private final LinkedList<MappedByteBuffer> transfers;
    private final AbstractProtocolCodec.Action readHeader;
    
    public /* synthetic */ Buffer protected$readUntil(final ReplicationProtocolCodec x$1, final Byte x$1, final int x$2) {
        return x$1.readUntil(x$1, x$2);
    }
    
    public /* synthetic */ void protected$setnextDecodeAction(final ReplicationProtocolCodec x$1, final AbstractProtocolCodec.Action x$2) {
        x$1.nextDecodeAction = x$2;
    }
    
    public /* synthetic */ AbstractProtocolCodec.Action protected$nextDecodeAction(final ReplicationProtocolCodec x$1) {
        return x$1.nextDecodeAction;
    }
    
    public LinkedList<MappedByteBuffer> transfers() {
        return this.transfers;
    }
    
    public void encode(final Object value) {
        if (value instanceof ReplicationFrame) {
            final ReplicationFrame replicationFrame = (ReplicationFrame)value;
            replicationFrame.action().writeTo((OutputStream)this.nextWriteBuffer);
            this.nextWriteBuffer.write(10);
            if (replicationFrame.body() != null) {
                replicationFrame.body().writeTo((OutputStream)this.nextWriteBuffer);
            }
            this.nextWriteBuffer.write(0);
            final BoxedUnit unit = BoxedUnit.UNIT;
        }
        else if (value instanceof FileTransferFrame) {
            final FileTransferFrame fileTransferFrame = (FileTransferFrame)value;
            if (fileTransferFrame.length() > 0L) {
                final MappedByteBuffer buffer = ReplicationSupport$.MODULE$.map(fileTransferFrame.file(), fileTransferFrame.offset(), fileTransferFrame.length(), true);
                this.writeDirect((ByteBuffer)buffer);
                if (buffer.hasRemaining()) {
                    this.transfers().addLast(buffer);
                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                }
                else {
                    ReplicationSupport$.MODULE$.unmap(buffer);
                    final BoxedUnit boxedUnit = BoxedUnit.UNIT;
                }
            }
            else {
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
        }
        else {
            if (!(value instanceof Buffer)) {
                throw new MatchError(value);
            }
            ((Buffer)value).writeTo((OutputStream)this.nextWriteBuffer);
            final BoxedUnit unit2 = BoxedUnit.UNIT;
        }
    }
    
    public ProtocolCodec.BufferState flush() {
        final ProtocolCodec.BufferState rc = super.flush();
        while (!this.transfers().isEmpty() && !this.transfers().peekFirst().hasRemaining()) {
            ReplicationSupport$.MODULE$.unmap(this.transfers().removeFirst());
        }
        return rc;
    }
    
    public AbstractProtocolCodec.Action initialDecodeAction() {
        return this.readHeader();
    }
    
    public AbstractProtocolCodec.Action readHeader() {
        return this.readHeader;
    }
    
    public AbstractProtocolCodec.Action readReplicationFrame(final AsciiBuffer action) {
        return (AbstractProtocolCodec.Action)new ReplicationProtocolCodec$$anon.ReplicationProtocolCodec$$anon$2(this, action);
    }
    
    public void readData(final ByteBuffer data_target, final Task cb) {
        super.nextDecodeAction = (AbstractProtocolCodec.Action)new ReplicationProtocolCodec$$anon.ReplicationProtocolCodec$$anon$3(this, data_target, cb);
    }
    
    public ReplicationProtocolCodec() {
        this.transfers = new LinkedList<MappedByteBuffer>();
        this.readHeader = (AbstractProtocolCodec.Action)new AbstractProtocolCodec.Action() {
            public Object apply() {
                final Buffer action_line = ReplicationProtocolCodec.this.protected$readUntil(ReplicationProtocolCodec.this, Predef$.MODULE$.byte2Byte((byte)10), 80);
                Object apply;
                if (action_line == null) {
                    apply = null;
                }
                else {
                    action_line.moveTail(-1);
                    ReplicationProtocolCodec.this.protected$setnextDecodeAction(ReplicationProtocolCodec.this, ReplicationProtocolCodec.this.readReplicationFrame(action_line.ascii()));
                    apply = ReplicationProtocolCodec.this.protected$nextDecodeAction(ReplicationProtocolCodec.this).apply();
                }
                return apply;
            }
        };
    }
}
