// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import scala.runtime.BoxesRunTime;
import java.util.Map;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import org.apache.activemq.broker.jmx.OpenTypeSupport;
import scala.Function1;
import scala.collection.Iterable$;
import scala.collection.TraversableOnce;
import scala.collection.Seq;
import scala.reflect.ClassTag$;
import scala.collection.immutable.Nil$;
import scala.Array$;
import javax.management.openmbean.CompositeData;
import scala.runtime.BoxedUnit;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005\u001db\u0001B\u0001\u0003\u00015\u0011!DU3qY&\u001c\u0017\r^3e\u0019\u00164X\r\u001c#C'R|'/\u001a,jK^T!a\u0001\u0003\u0002\u0015I,\u0007\u000f\\5dCR,GM\u0003\u0002\u0006\r\u00059A.\u001a<fY\u0012\u0014'BA\u0004\t\u0003!\t7\r^5wK6\f(BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001aE\u0002\u0001\u001dY\u0001\"a\u0004\u000b\u000e\u0003AQ!!\u0005\n\u0002\t1\fgn\u001a\u0006\u0002'\u0005!!.\u0019<b\u0013\t)\u0002C\u0001\u0004PE*,7\r\u001e\t\u0003/ai\u0011AA\u0005\u00033\t\u0011qDU3qY&\u001c\u0017\r^3e\u0019\u00164X\r\u001c#C'R|'/\u001a,jK^l%)Z1o\u0011!Y\u0002A!b\u0001\n\u0003a\u0012!B:u_J,W#A\u000f\u0011\u0005]q\u0012BA\u0010\u0003\u0005Q)E.Z2uS:<G*\u001a<fY\u0012\u00135\u000b^8sK\"A\u0011\u0005\u0001B\u0001B\u0003%Q$\u0001\u0004ti>\u0014X\r\t\u0005\u0006G\u0001!\t\u0001J\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005\u00152\u0003CA\f\u0001\u0011\u0015Y\"\u00051\u0001\u001e\u0011\u0015A\u0003\u0001\"\u0001*\u000319W\r\u001e.l\u0003\u0012$'/Z:t)\u0005Q\u0003CA\b,\u0013\ta\u0003C\u0001\u0004TiJLgn\u001a\u0005\u0006]\u0001!\t!K\u0001\nO\u0016$(l\u001b)bi\"DQ\u0001\r\u0001\u0005\u0002%\n!cZ3u5.\u001cVm]:j_:$V.Z8vi\")!\u0007\u0001C\u0001S\u00059q-\u001a;CS:$\u0007\"\u0002\u001b\u0001\t\u0003)\u0014aC4fiJ+\u0007\u000f\\5dCN$\u0012A\u000e\t\u0003oij\u0011\u0001\u000f\u0006\u0002s\u0005)1oY1mC&\u00111\b\u000f\u0002\u0004\u0013:$\b\"B\u001f\u0001\t\u0003q\u0014aC4fi:{G-\u001a*pY\u0016$\u0012a\u0010\t\u0003\u0001\u000es!aN!\n\u0005\tC\u0014A\u0002)sK\u0012,g-\u0003\u0002-\t*\u0011!\t\u000f\u0005\u0006\r\u0002!\tAP\u0001\nO\u0016$8\u000b^1ukN<Q\u0001\u0013\u0001\t\u0002%\u000bab\u00157bm\u0016\u001cF/\u0019;vg>#f\t\u0005\u0002K\u00176\t\u0001AB\u0003M\u0001!\u0005QJ\u0001\bTY\u00064Xm\u0015;biV\u001cx\n\u0016$\u0014\u0005-s\u0005CA(X\u001d\t\u0001V+D\u0001R\u0015\t\u00116+A\u0002k[bT!\u0001\u0016\u0004\u0002\r\t\u0014xn[3s\u0013\t1\u0016+A\bPa\u0016tG+\u001f9f'V\u0004\bo\u001c:u\u0013\tA\u0016LA\fBEN$(/Y2u\u001fB,g\u000eV=qK\u001a\u000b7\r^8ss*\u0011a+\u0015\u0005\u0006G-#\ta\u0017\u000b\u0002\u0013\")Ql\u0013C\t}\u0005Yq-\u001a;UsB,g*Y7f\u0011\u0015y6\n\"\u0015a\u0003\u0011Ig.\u001b;\u0015\u0003\u0005\u0004\"a\u000e2\n\u0005\rD$\u0001B+oSRDQ!Z&\u0005B\u0019\f\u0011bZ3u\r&,G\u000eZ:\u0015\u0005\u001d\u0004\b\u0003\u00025l\u007f5l\u0011!\u001b\u0006\u0003UJ\tA!\u001e;jY&\u0011A.\u001b\u0002\u0004\u001b\u0006\u0004\bCA\u001co\u0013\ty\u0007H\u0001\u0004B]f\u0014VM\u001a\u0005\u0006c\u0012\u0004\rA]\u0001\u0002_B\u0011qg]\u0005\u0003ib\u00121!\u00118z\u0011\u00151\b\u0001\"\u0001x\u0003%9W\r^*mCZ,7\u000fF\u0001y!\r9\u0014p_\u0005\u0003ub\u0012Q!\u0011:sCf\u00042\u0001`A\u0004\u001b\u0005i(B\u0001@\u0000\u0003%y\u0007/\u001a8nE\u0016\fgN\u0003\u0003\u0002\u0002\u0005\r\u0011AC7b]\u0006<W-\\3oi*\u0011\u0011QA\u0001\u0006U\u00064\u0018\r_\u0005\u0004\u0003\u0013i(!D\"p[B|7/\u001b;f\t\u0006$\u0018\rC\u0004\u0002\u000e\u0001!\t!a\u0004\u0002\u0017\u001d,G\u000fU8tSRLwN\u001c\u000b\u0003\u0003#\u00012aDA\n\u0013\r\t)\u0002\u0005\u0002\u0005\u0019>tw\rC\u0004\u0002\u001a\u0001!\t!a\u0004\u0002\u001f\u001d,G\u000fU8tSRLwN\u001c#bi\u0016Da!!\b\u0001\t\u0003I\u0013\u0001D4fi\u0012K'/Z2u_JL\bBBA\u0011\u0001\u0011\u0005\u0011&A\u0004hKR\u001c\u0016P\\2\t\r\u0005\u0015\u0002\u0001\"\u0001?\u0003%9W\r\u001e(pI\u0016LE\r")
public class ReplicatedLevelDBStoreView implements ReplicatedLevelDBStoreViewMBean
{
    private final ElectingLevelDBStore store;
    private volatile SlaveStatusOTF$ SlaveStatusOTF$module;
    
    private SlaveStatusOTF$ SlaveStatusOTF$lzycompute() {
        synchronized (this) {
            if (this.SlaveStatusOTF$module == null) {
                this.SlaveStatusOTF$module = new SlaveStatusOTF$();
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.SlaveStatusOTF$module;
        }
    }
    
    public ElectingLevelDBStore store() {
        return this.store;
    }
    
    @Override
    public String getZkAddress() {
        return this.store().zkAddress();
    }
    
    @Override
    public String getZkPath() {
        return this.store().zkPath();
    }
    
    @Override
    public String getZkSessionTmeout() {
        return this.store().zkSessionTmeout();
    }
    
    @Override
    public String getBind() {
        return this.store().bind();
    }
    
    @Override
    public int getReplicas() {
        return this.store().replicas();
    }
    
    @Override
    public String getNodeRole() {
        if (this.store().slave() != null) {
            return "slave";
        }
        if (this.store().master() == null) {
            return "electing";
        }
        return "master";
    }
    
    @Override
    public String getStatus() {
        if (this.store().slave() != null) {
            return this.store().slave().status();
        }
        if (this.store().master() == null) {
            return "";
        }
        return this.store().master().status();
    }
    
    public SlaveStatusOTF$ SlaveStatusOTF() {
        return (this.SlaveStatusOTF$module == null) ? this.SlaveStatusOTF$lzycompute() : this.SlaveStatusOTF$module;
    }
    
    @Override
    public CompositeData[] getSlaves() {
        return (CompositeData[])((this.store().master() == null) ? Array$.MODULE$.apply((Seq)Nil$.MODULE$, ClassTag$.MODULE$.apply((Class)CompositeData.class)) : ((CompositeData[])((TraversableOnce)this.store().master().slaves_status().map((Function1)new ReplicatedLevelDBStoreView$$anonfun$getSlaves.ReplicatedLevelDBStoreView$$anonfun$getSlaves$1(this), Iterable$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.apply((Class)CompositeData.class))));
    }
    
    @Override
    public Long getPosition() {
        if (this.store().slave() != null) {
            return new Long(this.store().slave().wal_append_position());
        }
        if (this.store().master() == null) {
            return null;
        }
        return new Long(this.store().master().wal_append_position());
    }
    
    @Override
    public Long getPositionDate() {
        final long rc = (this.store().slave() == null) ? ((this.store().master() == null) ? 0L : this.store().master().wal_date()) : this.store().slave().wal_date();
        if (rc != 0L) {
            return new Long(rc);
        }
        return null;
    }
    
    @Override
    public String getDirectory() {
        return this.store().directory().getCanonicalPath();
    }
    
    @Override
    public String getSync() {
        return this.store().sync();
    }
    
    @Override
    public String getNodeId() {
        return this.store().node_id();
    }
    
    public ReplicatedLevelDBStoreView(final ElectingLevelDBStore store) {
        this.store = store;
    }
    
    public class SlaveStatusOTF$ extends OpenTypeSupport.AbstractOpenTypeFactory
    {
        public String getTypeName() {
            return SlaveStatus.class.getName();
        }
        
        public void init() {
            super.init();
            this.addItem("nodeId", "nodeId", SimpleType.STRING);
            this.addItem("remoteAddress", "remoteAddress", SimpleType.STRING);
            this.addItem("attached", "attached", SimpleType.BOOLEAN);
            this.addItem("position", "position", SimpleType.LONG);
        }
        
        @Override
        public Map<String, Object> getFields(final Object o) {
            final SlaveStatus status = (SlaveStatus)o;
            final Map rc = super.getFields(o);
            rc.put("nodeId", status.nodeId());
            rc.put("remoteAddress", status.remoteAddress());
            rc.put("attached", BoxesRunTime.boxToBoolean(status.attached()));
            rc.put("position", BoxesRunTime.boxToLong(status.position()));
            return (Map<String, Object>)rc;
        }
        
        public SlaveStatusOTF$(final ReplicatedLevelDBStoreView $outer) {
        }
    }
}
