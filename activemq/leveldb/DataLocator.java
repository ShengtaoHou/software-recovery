// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Product$class;
import scala.runtime.Statics;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.runtime.BoxesRunTime;
import scala.collection.immutable.StringOps;
import scala.Predef$;
import scala.Function1;
import scala.Tuple3;
import scala.Option;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0005uc\u0001B\u0001\u0003\u0001.\u00111\u0002R1uC2{7-\u0019;pe*\u00111\u0001B\u0001\bY\u00164X\r\u001c3c\u0015\t)a!\u0001\u0005bGRLg/Z7r\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0004\u0001M!\u0001\u0001\u0004\n\u0016!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u0011QbE\u0005\u0003)9\u0011q\u0001\u0015:pIV\u001cG\u000f\u0005\u0002\u000e-%\u0011qC\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\u0005\t3\u0001\u0011)\u001a!C\u00015\u0005)1\u000f^8sKV\t1\u0004\u0005\u0002\u001d;5\t!!\u0003\u0002\u001f\u0005\taA*\u001a<fY\u0012\u00135\u000b^8sK\"A\u0001\u0005\u0001B\tB\u0003%1$\u0001\u0004ti>\u0014X\r\t\u0005\tE\u0001\u0011)\u001a!C\u0001G\u0005\u0019\u0001o\\:\u0016\u0003\u0011\u0002\"!D\u0013\n\u0005\u0019r!\u0001\u0002'p]\u001eD\u0001\u0002\u000b\u0001\u0003\u0012\u0003\u0006I\u0001J\u0001\u0005a>\u001c\b\u0005\u0003\u0005+\u0001\tU\r\u0011\"\u0001,\u0003\raWM\\\u000b\u0002YA\u0011Q\"L\u0005\u0003]9\u00111!\u00138u\u0011!\u0001\u0004A!E!\u0002\u0013a\u0013\u0001\u00027f]\u0002BQA\r\u0001\u0005\u0002M\na\u0001P5oSRtD\u0003\u0002\u001b6m]\u0002\"\u0001\b\u0001\t\u000be\t\u0004\u0019A\u000e\t\u000b\t\n\u0004\u0019\u0001\u0013\t\u000b)\n\u0004\u0019\u0001\u0017\t\u000be\u0002A\u0011\t\u001e\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012a\u000f\t\u0003y}r!!D\u001f\n\u0005yr\u0011A\u0002)sK\u0012,g-\u0003\u0002A\u0003\n11\u000b\u001e:j]\u001eT!A\u0010\b\t\u000f\r\u0003\u0011\u0011!C\u0001\t\u0006!1m\u001c9z)\u0011!TIR$\t\u000fe\u0011\u0005\u0013!a\u00017!9!E\u0011I\u0001\u0002\u0004!\u0003b\u0002\u0016C!\u0003\u0005\r\u0001\f\u0005\b\u0013\u0002\t\n\u0011\"\u0001K\u00039\u0019w\u000e]=%I\u00164\u0017-\u001e7uIE*\u0012a\u0013\u0016\u000371[\u0013!\u0014\t\u0003\u001dNk\u0011a\u0014\u0006\u0003!F\u000b\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0005Is\u0011AC1o]>$\u0018\r^5p]&\u0011Ak\u0014\u0002\u0012k:\u001c\u0007.Z2lK\u00124\u0016M]5b]\u000e,\u0007b\u0002,\u0001#\u0003%\taV\u0001\u000fG>\u0004\u0018\u0010\n3fM\u0006,H\u000e\u001e\u00133+\u0005A&F\u0001\u0013M\u0011\u001dQ\u0006!%A\u0005\u0002m\u000babY8qs\u0012\"WMZ1vYR$3'F\u0001]U\taC\nC\u0004_\u0001\u0005\u0005I\u0011I0\u0002\u001bA\u0014x\u000eZ;diB\u0013XMZ5y+\u0005\u0001\u0007CA1g\u001b\u0005\u0011'BA2e\u0003\u0011a\u0017M\\4\u000b\u0003\u0015\fAA[1wC&\u0011\u0001I\u0019\u0005\bQ\u0002\t\t\u0011\"\u0001,\u00031\u0001(o\u001c3vGR\f%/\u001b;z\u0011\u001dQ\u0007!!A\u0005\u0002-\fa\u0002\u001d:pIV\u001cG/\u00127f[\u0016tG\u000f\u0006\u0002m_B\u0011Q\"\\\u0005\u0003]:\u00111!\u00118z\u0011\u001d\u0001\u0018.!AA\u00021\n1\u0001\u001f\u00132\u0011\u001d\u0011\b!!A\u0005BM\fq\u0002\u001d:pIV\u001cG/\u0013;fe\u0006$xN]\u000b\u0002iB\u0019Q\u000f\u001f7\u000e\u0003YT!a\u001e\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002zm\nA\u0011\n^3sCR|'\u000fC\u0004|\u0001\u0005\u0005I\u0011\u0001?\u0002\u0011\r\fg.R9vC2$2!`A\u0001!\tia0\u0003\u0002\u0000\u001d\t9!i\\8mK\u0006t\u0007b\u00029{\u0003\u0003\u0005\r\u0001\u001c\u0005\n\u0003\u000b\u0001\u0011\u0011!C!\u0003\u000f\t\u0001\u0002[1tQ\u000e{G-\u001a\u000b\u0002Y!I\u00111\u0002\u0001\u0002\u0002\u0013\u0005\u0013QB\u0001\u0007KF,\u0018\r\\:\u0015\u0007u\fy\u0001\u0003\u0005q\u0003\u0013\t\t\u00111\u0001m\u000f%\t\u0019BAA\u0001\u0012\u0003\t)\"A\u0006ECR\fGj\\2bi>\u0014\bc\u0001\u000f\u0002\u0018\u0019A\u0011AAA\u0001\u0012\u0003\tIbE\u0003\u0002\u0018\u0005mQ\u0003\u0005\u0005\u0002\u001e\u0005\r2\u0004\n\u00175\u001b\t\tyBC\u0002\u0002\"9\tqA];oi&lW-\u0003\u0003\u0002&\u0005}!!E!cgR\u0014\u0018m\u0019;Gk:\u001cG/[8og!9!'a\u0006\u0005\u0002\u0005%BCAA\u000b\u0011%I\u0014qCA\u0001\n\u000b\ni\u0003F\u0001a\u0011)\t\t$a\u0006\u0002\u0002\u0013\u0005\u00151G\u0001\u0006CB\u0004H.\u001f\u000b\bi\u0005U\u0012qGA\u001d\u0011\u0019I\u0012q\u0006a\u00017!1!%a\fA\u0002\u0011BaAKA\u0018\u0001\u0004a\u0003BCA\u001f\u0003/\t\t\u0011\"!\u0002@\u00059QO\\1qa2LH\u0003BA!\u0003\u001b\u0002R!DA\"\u0003\u000fJ1!!\u0012\u000f\u0005\u0019y\u0005\u000f^5p]B1Q\"!\u0013\u001cI1J1!a\u0013\u000f\u0005\u0019!V\u000f\u001d7fg!I\u0011qJA\u001e\u0003\u0003\u0005\r\u0001N\u0001\u0004q\u0012\u0002\u0004BCA*\u0003/\t\t\u0011\"\u0003\u0002V\u0005Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\t9\u0006E\u0002b\u00033J1!a\u0017c\u0005\u0019y%M[3di\u0002")
public class DataLocator implements Product, Serializable
{
    private final LevelDBStore store;
    private final long pos;
    private final int len;
    
    public static Option<Tuple3<LevelDBStore, Object, Object>> unapply(final DataLocator x$0) {
        return DataLocator$.MODULE$.unapply(x$0);
    }
    
    public static DataLocator apply(final LevelDBStore store, final long pos, final int len) {
        return DataLocator$.MODULE$.apply(store, pos, len);
    }
    
    public static Function1<Tuple3<LevelDBStore, Object, Object>, DataLocator> tupled() {
        return (Function1<Tuple3<LevelDBStore, Object, Object>, DataLocator>)DataLocator$.MODULE$.tupled();
    }
    
    public static Function1<LevelDBStore, Function1<Object, Function1<Object, DataLocator>>> curried() {
        return (Function1<LevelDBStore, Function1<Object, Function1<Object, DataLocator>>>)DataLocator$.MODULE$.curried();
    }
    
    public LevelDBStore store() {
        return this.store;
    }
    
    public long pos() {
        return this.pos;
    }
    
    public int len() {
        return this.len;
    }
    
    @Override
    public String toString() {
        return new StringOps(Predef$.MODULE$.augmentString("DataLocator(%x, %d)")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToLong(this.pos()), BoxesRunTime.boxToInteger(this.len()) }));
    }
    
    public DataLocator copy(final LevelDBStore store, final long pos, final int len) {
        return new DataLocator(store, pos, len);
    }
    
    public LevelDBStore copy$default$1() {
        return this.store();
    }
    
    public long copy$default$2() {
        return this.pos();
    }
    
    public int copy$default$3() {
        return this.len();
    }
    
    public String productPrefix() {
        return "DataLocator";
    }
    
    public int productArity() {
        return 3;
    }
    
    public Object productElement(final int x$1) {
        Object o = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 2: {
                o = BoxesRunTime.boxToInteger(this.len());
                break;
            }
            case 1: {
                o = BoxesRunTime.boxToLong(this.pos());
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
        return x$1 instanceof DataLocator;
    }
    
    @Override
    public int hashCode() {
        return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.store())), Statics.longHash(this.pos())), this.len()), 3);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof DataLocator) {
                final DataLocator dataLocator = (DataLocator)x$1;
                final LevelDBStore store = this.store();
                final LevelDBStore store2 = dataLocator.store();
                boolean b = false;
                Label_0102: {
                    Label_0101: {
                        if (store == null) {
                            if (store2 != null) {
                                break Label_0101;
                            }
                        }
                        else if (!store.equals(store2)) {
                            break Label_0101;
                        }
                        if (this.pos() == dataLocator.pos() && this.len() == dataLocator.len() && dataLocator.canEqual(this)) {
                            b = true;
                            break Label_0102;
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
    
    public DataLocator(final LevelDBStore store, final long pos, final int len) {
        this.store = store;
        this.pos = pos;
        this.len = len;
        Product$class.$init$((Product)this);
    }
}
