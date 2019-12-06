// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.fusesource.hawtdispatch.transport.ProtocolCodec;
import org.apache.activemq.leveldb.util.JsonCodec$;
import org.fusesource.hawtbuf.AsciiBuffer;
import scala.MatchError;
import java.io.IOException;
import org.fusesource.hawtdispatch.package$;
import scala.runtime.BoxedUnit;
import scala.Function0;
import scala.Tuple2;
import java.util.LinkedList;
import org.fusesource.hawtdispatch.transport.Transport;
import scala.reflect.ScalaSignature;
import org.fusesource.hawtdispatch.transport.TransportListener;

@ScalaSignature(bytes = "\u0006\u0001\u0005\rb!B\u0001\u0003\u0003\u0003i!\u0001\u0005+sC:\u001c\bo\u001c:u\u0011\u0006tG\r\\3s\u0015\t\u0019A!\u0001\u0006sKBd\u0017nY1uK\u0012T!!\u0002\u0004\u0002\u000f1,g/\u001a7eE*\u0011q\u0001C\u0001\tC\u000e$\u0018N^3nc*\u0011\u0011BC\u0001\u0007CB\f7\r[3\u000b\u0003-\t1a\u001c:h\u0007\u0001\u00192\u0001\u0001\b\u0017!\tyA#D\u0001\u0011\u0015\t\t\"#\u0001\u0003mC:<'\"A\n\u0002\t)\fg/Y\u0005\u0003+A\u0011aa\u00142kK\u000e$\bCA\f\u001f\u001b\u0005A\"BA\r\u001b\u0003%!(/\u00198ta>\u0014HO\u0003\u0002\u001c9\u0005a\u0001.Y<uI&\u001c\b/\u0019;dQ*\u0011QDC\u0001\u000bMV\u001cXm]8ve\u000e,\u0017BA\u0010\u0019\u0005E!&/\u00198ta>\u0014H\u000fT5ti\u0016tWM\u001d\u0005\t3\u0001\u0011)\u0019!C\u0001CU\t!\u0005\u0005\u0002\u0018G%\u0011A\u0005\u0007\u0002\n)J\fgn\u001d9peRD\u0001B\n\u0001\u0003\u0002\u0003\u0006IAI\u0001\u000biJ\fgn\u001d9peR\u0004\u0003\"\u0002\u0015\u0001\t\u0003I\u0013A\u0002\u001fj]&$h\b\u0006\u0002+YA\u00111\u0006A\u0007\u0002\u0005!)\u0011d\na\u0001E!9a\u0006\u0001a\u0001\n\u0003y\u0013\u0001C8vi\n|WO\u001c3\u0016\u0003A\u00022!\r\u001b7\u001b\u0005\u0011$BA\u001a\u0013\u0003\u0011)H/\u001b7\n\u0005U\u0012$A\u0003'j].,G\rT5tiB!qG\u000f\u001f@\u001b\u0005A$\"A\u001d\u0002\u000bM\u001c\u0017\r\\1\n\u0005mB$A\u0002+va2,'\u0007\u0005\u00028{%\u0011a\b\u000f\u0002\u0007\u0003:L(+\u001a4\u0011\u0007]\u0002%)\u0003\u0002Bq\tIa)\u001e8di&|g\u000e\r\t\u0003o\rK!\u0001\u0012\u001d\u0003\tUs\u0017\u000e\u001e\u0005\b\r\u0002\u0001\r\u0011\"\u0001H\u00031yW\u000f\u001e2pk:$w\fJ3r)\t\u0011\u0005\nC\u0004J\u000b\u0006\u0005\t\u0019\u0001\u0019\u0002\u0007a$\u0013\u0007\u0003\u0004L\u0001\u0001\u0006K\u0001M\u0001\n_V$(m\\;oI\u0002Bq!\u0014\u0001C\u0002\u0013\u0005a*A\u0003d_\u0012,7-F\u0001P!\tY\u0003+\u0003\u0002R\u0005\tA\"+\u001a9mS\u000e\fG/[8o!J|Go\\2pY\u000e{G-Z2\t\rM\u0003\u0001\u0015!\u0003P\u0003\u0019\u0019w\u000eZ3dA!)Q\u000b\u0001C\u0001-\u0006)1\u000f^1siV\t!\tC\u0003Y\u0001\u0011\u0005\u0011,\u0001\u000bp]R\u0013\u0018M\\:q_J$8i\u001c8oK\u000e$X\r\u001a\u000b\u0002\u0005\")1\f\u0001C\u00013\u00069rN\u001c+sC:\u001c\bo\u001c:u\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u0005\u0006;\u0002!\t!W\u0001\t_:\u0014VMZ5mY\")q\f\u0001C\u0001A\u0006\u0011rN\u001c+sC:\u001c\bo\u001c:u\r\u0006LG.\u001e:f)\t\u0011\u0015\rC\u0003c=\u0002\u00071-A\u0003feJ|'\u000f\u0005\u0002eO6\tQM\u0003\u0002g%\u0005\u0011\u0011n\\\u0005\u0003Q\u0016\u00141\"S(Fq\u000e,\u0007\u000f^5p]\")!\u000e\u0001C\u0001-\u0006)AM]1j]\")A\u000e\u0001C\u0001[\u0006!1/\u001a8e)\t\u0011e\u000eC\u0003pW\u0002\u0007A(A\u0003wC2,X\rC\u0003m\u0001\u0011\u0005\u0011\u000fF\u0002CeNDQa\u001c9A\u0002qBQ\u0001\u001e9A\u0002}\nqa\u001c8`g\u0016tG\rC\u0003w\u0001\u0011\u0005q/\u0001\ftK:$wL]3qY&\u001c\u0017\r^5p]~3'/Y7f)\u0011\u0011\u00050!\u0001\t\u000be,\b\u0019\u0001>\u0002\r\u0005\u001cG/[8o!\tYh0D\u0001}\u0015\tiH$A\u0004iC^$(-\u001e4\n\u0005}d(aC!tG&L')\u001e4gKJDa!a\u0001v\u0001\u0004a\u0014\u0001\u00022pIfDq!a\u0002\u0001\t\u0003\tI!A\u0005tK:$WI\u001d:peR\u0019!)a\u0003\t\u000f\t\f)\u00011\u0001\u0002\u000eA!\u0011qBA\u000b\u001d\r9\u0014\u0011C\u0005\u0004\u0003'A\u0014A\u0002)sK\u0012,g-\u0003\u0003\u0002\u0018\u0005e!AB*ue&twMC\u0002\u0002\u0014aBq!!\b\u0001\t\u0003\ty\"\u0001\u0004tK:$wj\u001b\u000b\u0004\u0005\u0006\u0005\u0002bBA\u0002\u00037\u0001\r\u0001\u0010")
public abstract class TransportHandler implements TransportListener
{
    private final Transport transport;
    private LinkedList<Tuple2<Object, Function0<BoxedUnit>>> outbound;
    private final ReplicationProtocolCodec codec;
    
    public Transport transport() {
        return this.transport;
    }
    
    public LinkedList<Tuple2<Object, Function0<BoxedUnit>>> outbound() {
        return this.outbound;
    }
    
    public void outbound_$eq(final LinkedList<Tuple2<Object, Function0<BoxedUnit>>> x$1) {
        this.outbound = x$1;
    }
    
    public ReplicationProtocolCodec codec() {
        return this.codec;
    }
    
    public void start() {
        this.transport().start(package$.MODULE$.NOOP());
    }
    
    public void onTransportConnected() {
        this.transport().resumeRead();
    }
    
    public void onTransportDisconnected() {
    }
    
    public void onRefill() {
        this.drain();
    }
    
    public void onTransportFailure(final IOException error) {
        this.transport().stop(package$.MODULE$.NOOP());
    }
    
    public void drain() {
        while (!this.outbound().isEmpty()) {
            final Tuple2<Object, Function0<BoxedUnit>> tuple2 = this.outbound().peekFirst();
            if (tuple2 == null) {
                throw new MatchError((Object)tuple2);
            }
            final Object value = tuple2._1();
            final Function0 on_send = (Function0)tuple2._2();
            final Tuple2 tuple3 = new Tuple2(value, (Object)on_send);
            final Object value2 = tuple3._1();
            final Function0 on_send2 = (Function0)tuple3._2();
            if (!this.transport().offer(value2)) {
                return;
            }
            this.outbound().removeFirst();
            if (on_send2 == null) {
                continue;
            }
            on_send2.apply$mcV$sp();
        }
    }
    
    public void send(final Object value) {
        this.send(value, null);
    }
    
    public void send(final Object value, final Function0<BoxedUnit> on_send) {
        this.transport().getDispatchQueue().assertExecuting();
        this.outbound().add((Tuple2<Object, Function0<BoxedUnit>>)new Tuple2(value, (Object)on_send));
        this.drain();
    }
    
    public void send_replication_frame(final AsciiBuffer action, final Object body) {
        this.send(new ReplicationFrame(action, (body == null) ? null : JsonCodec$.MODULE$.encode(body)));
    }
    
    public void sendError(final String error) {
        this.send_replication_frame(ReplicationSupport$.MODULE$.ERROR_ACTION(), error);
    }
    
    public void sendOk(final Object body) {
        this.send_replication_frame(ReplicationSupport$.MODULE$.OK_ACTION(), body);
    }
    
    public TransportHandler(final Transport transport) {
        this.transport = transport;
        this.outbound = new LinkedList<Tuple2<Object, Function0<BoxedUnit>>>();
        this.codec = new ReplicationProtocolCodec();
        transport.setProtocolCodec((ProtocolCodec)this.codec());
        transport.setTransportListener((TransportListener)this);
    }
}
