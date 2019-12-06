// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Product$class;
import scala.runtime.Statics;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import scala.Tuple4;
import scala.Option;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.command.MessageId;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005ue\u0001B\u0001\u0003\u0001.\u0011Q\"T3tg\u0006<WMU3d_J$'BA\u0002\u0005\u0003\u001daWM^3mI\nT!!\u0002\u0004\u0002\u0011\u0005\u001cG/\u001b<f[FT!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sO\u000e\u00011\u0003\u0002\u0001\r%U\u0001\"!\u0004\t\u000e\u00039Q\u0011aD\u0001\u0006g\u000e\fG.Y\u0005\u0003#9\u0011a!\u00118z%\u00164\u0007CA\u0007\u0014\u0013\t!bBA\u0004Qe>$Wo\u0019;\u0011\u000551\u0012BA\f\u000f\u00051\u0019VM]5bY&T\u0018M\u00197f\u0011!I\u0002A!f\u0001\n\u0003Q\u0012!B:u_J,W#A\u000e\u0011\u0005qiR\"\u0001\u0002\n\u0005y\u0011!\u0001\u0004'fm\u0016dGIQ*u_J,\u0007\u0002\u0003\u0011\u0001\u0005#\u0005\u000b\u0011B\u000e\u0002\rM$xN]3!\u0011!\u0011\u0003A!f\u0001\n\u0003\u0019\u0013AA5e+\u0005!\u0003CA\u0013)\u001b\u00051#BA\u0014\u0005\u0003\u001d\u0019w.\\7b]\u0012L!!\u000b\u0014\u0003\u00135+7o]1hK&#\u0007\u0002C\u0016\u0001\u0005#\u0005\u000b\u0011\u0002\u0013\u0002\u0007%$\u0007\u0005\u0003\u0005.\u0001\tU\r\u0011\"\u0001/\u0003\u0011!\u0017\r^1\u0016\u0003=\u0002\"\u0001M\u001b\u000e\u0003ER!AM\u001a\u0002\u000f!\fw\u000f\u001e2vM*\u0011A\u0007C\u0001\u000bMV\u001cXm]8ve\u000e,\u0017B\u0001\u001c2\u0005\u0019\u0011UO\u001a4fe\"A\u0001\b\u0001B\tB\u0003%q&A\u0003eCR\f\u0007\u0005\u0003\u0005;\u0001\tU\r\u0011\"\u0001<\u0003)\u0019\u0018P\\2OK\u0016$W\rZ\u000b\u0002yA\u0011Q\"P\u0005\u0003}9\u0011qAQ8pY\u0016\fg\u000e\u0003\u0005A\u0001\tE\t\u0015!\u0003=\u0003-\u0019\u0018P\\2OK\u0016$W\r\u001a\u0011\t\u000b\t\u0003A\u0011A\"\u0002\rqJg.\u001b;?)\u0015!UIR$I!\ta\u0002\u0001C\u0003\u001a\u0003\u0002\u00071\u0004C\u0003#\u0003\u0002\u0007A\u0005C\u0003.\u0003\u0002\u0007q\u0006C\u0003;\u0003\u0002\u0007A\bC\u0005K\u0001\u0001\u0007\t\u0019!C\u0001\u0017\u00069An\\2bi>\u0014X#\u0001'\u0011\u0005qi\u0015B\u0001(\u0003\u0005-!\u0015\r^1M_\u000e\fGo\u001c:\t\u0013A\u0003\u0001\u0019!a\u0001\n\u0003\t\u0016a\u00037pG\u0006$xN]0%KF$\"AU+\u0011\u00055\u0019\u0016B\u0001+\u000f\u0005\u0011)f.\u001b;\t\u000fY{\u0015\u0011!a\u0001\u0019\u0006\u0019\u0001\u0010J\u0019\t\ra\u0003\u0001\u0015)\u0003M\u0003!awnY1u_J\u0004\u0003b\u0002.\u0001\u0003\u0003%\taW\u0001\u0005G>\u0004\u0018\u0010F\u0003E9vsv\fC\u0004\u001a3B\u0005\t\u0019A\u000e\t\u000f\tJ\u0006\u0013!a\u0001I!9Q&\u0017I\u0001\u0002\u0004y\u0003b\u0002\u001eZ!\u0003\u0005\r\u0001\u0010\u0005\bC\u0002\t\n\u0011\"\u0001c\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\u0019\u0016\u00037\u0011\\\u0013!\u001a\t\u0003M.l\u0011a\u001a\u0006\u0003Q&\f\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005)t\u0011AC1o]>$\u0018\r^5p]&\u0011An\u001a\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u00028\u0001#\u0003%\ta\\\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005\u0001(F\u0001\u0013e\u0011\u001d\u0011\b!%A\u0005\u0002M\fabY8qs\u0012\"WMZ1vYR$3'F\u0001uU\tyC\rC\u0004w\u0001E\u0005I\u0011A<\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%iU\t\u0001P\u000b\u0002=I\"9!\u0010AA\u0001\n\u0003Z\u0018!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001}!\ri\u0018QA\u0007\u0002}*\u0019q0!\u0001\u0002\t1\fgn\u001a\u0006\u0003\u0003\u0007\tAA[1wC&\u0019\u0011q\u0001@\u0003\rM#(/\u001b8h\u0011%\tY\u0001AA\u0001\n\u0003\ti!\u0001\u0007qe>$Wo\u0019;Be&$\u00180\u0006\u0002\u0002\u0010A\u0019Q\"!\u0005\n\u0007\u0005MaBA\u0002J]RD\u0011\"a\u0006\u0001\u0003\u0003%\t!!\u0007\u0002\u001dA\u0014x\u000eZ;di\u0016cW-\\3oiR!\u00111DA\u0011!\ri\u0011QD\u0005\u0004\u0003?q!aA!os\"Ia+!\u0006\u0002\u0002\u0003\u0007\u0011q\u0002\u0005\n\u0003K\u0001\u0011\u0011!C!\u0003O\tq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0003\u0003S\u0001b!a\u000b\u00022\u0005mQBAA\u0017\u0015\r\tyCD\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\u001a\u0003[\u0011\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003o\u0001\u0011\u0011!C\u0001\u0003s\t\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0004y\u0005m\u0002\"\u0003,\u00026\u0005\u0005\t\u0019AA\u000e\u0011%\ty\u0004AA\u0001\n\u0003\n\t%\u0001\u0005iCND7i\u001c3f)\t\ty\u0001C\u0005\u0002F\u0001\t\t\u0011\"\u0011\u0002H\u0005AAo\\*ue&tw\rF\u0001}\u0011%\tY\u0005AA\u0001\n\u0003\ni%\u0001\u0004fcV\fGn\u001d\u000b\u0004y\u0005=\u0003\"\u0003,\u0002J\u0005\u0005\t\u0019AA\u000e\u000f%\t\u0019FAA\u0001\u0012\u0003\t)&A\u0007NKN\u001c\u0018mZ3SK\u000e|'\u000f\u001a\t\u00049\u0005]c\u0001C\u0001\u0003\u0003\u0003E\t!!\u0017\u0014\u000b\u0005]\u00131L\u000b\u0011\u0013\u0005u\u00131M\u000e%_q\"UBAA0\u0015\r\t\tGD\u0001\beVtG/[7f\u0013\u0011\t)'a\u0018\u0003#\u0005\u00137\u000f\u001e:bGR4UO\\2uS>tG\u0007C\u0004C\u0003/\"\t!!\u001b\u0015\u0005\u0005U\u0003BCA#\u0003/\n\t\u0011\"\u0012\u0002H!Q\u0011qNA,\u0003\u0003%\t)!\u001d\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u0013\u0011\u000b\u0019(!\u001e\u0002x\u0005e\u0004BB\r\u0002n\u0001\u00071\u0004\u0003\u0004#\u0003[\u0002\r\u0001\n\u0005\u0007[\u00055\u0004\u0019A\u0018\t\ri\ni\u00071\u0001=\u0011)\ti(a\u0016\u0002\u0002\u0013\u0005\u0015qP\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\t\t)!$\u0011\u000b5\t\u0019)a\"\n\u0007\u0005\u0015eB\u0001\u0004PaRLwN\u001c\t\b\u001b\u0005%5\u0004J\u0018=\u0013\r\tYI\u0004\u0002\u0007)V\u0004H.\u001a\u001b\t\u0013\u0005=\u00151PA\u0001\u0002\u0004!\u0015a\u0001=%a!Q\u00111SA,\u0003\u0003%I!!&\u0002\u0017I,\u0017\r\u001a*fg>dg/\u001a\u000b\u0003\u0003/\u00032!`AM\u0013\r\tYJ \u0002\u0007\u001f\nTWm\u0019;")
public class MessageRecord implements Product, Serializable
{
    private final LevelDBStore store;
    private final MessageId id;
    private final Buffer data;
    private final boolean syncNeeded;
    private DataLocator locator;
    
    public static Option<Tuple4<LevelDBStore, MessageId, Buffer, Object>> unapply(final MessageRecord x$0) {
        return MessageRecord$.MODULE$.unapply(x$0);
    }
    
    public static MessageRecord apply(final LevelDBStore store, final MessageId id, final Buffer data, final boolean syncNeeded) {
        return MessageRecord$.MODULE$.apply(store, id, data, syncNeeded);
    }
    
    public static Function1<Tuple4<LevelDBStore, MessageId, Buffer, Object>, MessageRecord> tupled() {
        return (Function1<Tuple4<LevelDBStore, MessageId, Buffer, Object>, MessageRecord>)MessageRecord$.MODULE$.tupled();
    }
    
    public static Function1<LevelDBStore, Function1<MessageId, Function1<Buffer, Function1<Object, MessageRecord>>>> curried() {
        return (Function1<LevelDBStore, Function1<MessageId, Function1<Buffer, Function1<Object, MessageRecord>>>>)MessageRecord$.MODULE$.curried();
    }
    
    public LevelDBStore store() {
        return this.store;
    }
    
    public MessageId id() {
        return this.id;
    }
    
    public Buffer data() {
        return this.data;
    }
    
    public boolean syncNeeded() {
        return this.syncNeeded;
    }
    
    public DataLocator locator() {
        return this.locator;
    }
    
    public void locator_$eq(final DataLocator x$1) {
        this.locator = x$1;
    }
    
    public MessageRecord copy(final LevelDBStore store, final MessageId id, final Buffer data, final boolean syncNeeded) {
        return new MessageRecord(store, id, data, syncNeeded);
    }
    
    public LevelDBStore copy$default$1() {
        return this.store();
    }
    
    public MessageId copy$default$2() {
        return this.id();
    }
    
    public Buffer copy$default$3() {
        return this.data();
    }
    
    public boolean copy$default$4() {
        return this.syncNeeded();
    }
    
    public String productPrefix() {
        return "MessageRecord";
    }
    
    public int productArity() {
        return 4;
    }
    
    public Object productElement(final int x$1) {
        Object o = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 3: {
                o = BoxesRunTime.boxToBoolean(this.syncNeeded());
                break;
            }
            case 2: {
                o = this.data();
                break;
            }
            case 1: {
                o = this.id();
                break;
            }
            case 0: {
                o = this.store();
                break;
            }
        }
        return o;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof MessageRecord;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.store())), Statics.anyHash((Object)this.id())), Statics.anyHash((Object)this.data())), this.syncNeeded() ? 1231 : 1237), 4);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof MessageRecord) {
                final MessageRecord messageRecord = (MessageRecord)x$1;
                final LevelDBStore store = this.store();
                final LevelDBStore store2 = messageRecord.store();
                boolean b = false;
                Label_0153: {
                    Label_0152: {
                        if (store == null) {
                            if (store2 != null) {
                                break Label_0152;
                            }
                        }
                        else if (!store.equals(store2)) {
                            break Label_0152;
                        }
                        final MessageId id = this.id();
                        final MessageId id2 = messageRecord.id();
                        if (id == null) {
                            if (id2 != null) {
                                break Label_0152;
                            }
                        }
                        else if (!id.equals(id2)) {
                            break Label_0152;
                        }
                        final Buffer data = this.data();
                        final Buffer data2 = messageRecord.data();
                        if (data == null) {
                            if (data2 != null) {
                                break Label_0152;
                            }
                        }
                        else if (!data.equals(data2)) {
                            break Label_0152;
                        }
                        if (this.syncNeeded() == messageRecord.syncNeeded() && messageRecord.canEqual(this)) {
                            b = true;
                            break Label_0153;
                        }
                    }
                    b = false;
                }
                if (b) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public MessageRecord(final LevelDBStore store, final MessageId id, final Buffer data, final boolean syncNeeded) {
        this.store = store;
        this.id = id;
        this.data = data;
        this.syncNeeded = syncNeeded;
        Product$class.$init$((Product)this);
    }
}
