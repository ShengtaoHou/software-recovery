// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.store.PList;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.usage.SystemUsage;
import java.io.File;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.leveldb.LevelDBStore;
import scala.reflect.ScalaSignature;
import org.apache.activemq.store.PListStore;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.broker.LockableServiceSupport;

@ScalaSignature(bytes = "\u0006\u0001\u00055g!B\u0001\u0003\u0003\u0003i!!\u0005)s_bLH*\u001a<fY\u0012\u00135\u000b^8sK*\u00111\u0001B\u0001\u000be\u0016\u0004H.[2bi\u0016$'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u00011C\u0002\u0001\u000f)]i\u0002\u0005\u0005\u0002\u0010%5\t\u0001C\u0003\u0002\u0012\r\u00051!M]8lKJL!a\u0005\t\u0003-1{7m[1cY\u0016\u001cVM\u001d<jG\u0016\u001cV\u000f\u001d9peR\u0004\"aD\u000b\n\u0005Y\u0001\"A\u0005\"s_.,'oU3sm&\u001cW-Q<be\u0016\u0004\"\u0001G\u000e\u000e\u0003eQ!A\u0007\u0004\u0002\u000bM$xN]3\n\u0005qI\"A\u0005)feNL7\u000f^3oG\u0016\fE-\u00199uKJ\u0004\"\u0001\u0007\u0010\n\u0005}I\"\u0001\u0005+sC:\u001c\u0018m\u0019;j_:\u001cFo\u001c:f!\tA\u0012%\u0003\u0002#3\tQ\u0001\u000bT5tiN#xN]3\t\u000b\u0011\u0002A\u0011A\u0013\u0002\rqJg.\u001b;?)\u00051\u0003CA\u0014\u0001\u001b\u0005\u0011\u0001\"B\u0015\u0001\r\u0003Q\u0013\u0001\u00049s_bLx\f^1sO\u0016$X#A\u0016\u0011\u00051jS\"\u0001\u0003\n\u00059\"!\u0001\u0004'fm\u0016dGIQ*u_J,\u0007\"\u0002\u0019\u0001\t\u0003\t\u0014\u0001\u00052fO&tGK]1og\u0006\u001cG/[8o)\t\u0011\u0004\b\u0005\u00024m5\tAGC\u00016\u0003\u0015\u00198-\u00197b\u0013\t9DG\u0001\u0003V]&$\b\"B\u001d0\u0001\u0004Q\u0014aB2p]R,\u0007\u0010\u001e\t\u0003\u001fmJ!\u0001\u0010\t\u0003#\r{gN\\3di&|gnQ8oi\u0016DH\u000fC\u0003?\u0001\u0011\u0005q(A\rhKRd\u0015m\u001d;Qe>$WoY3s'\u0016\fX/\u001a8dK&#GC\u0001!D!\t\u0019\u0014)\u0003\u0002Ci\t!Aj\u001c8h\u0011\u0015!U\b1\u0001F\u0003\tIG\r\u0005\u0002G\u00136\tqI\u0003\u0002I\r\u000591m\\7nC:$\u0017B\u0001&H\u0005)\u0001&o\u001c3vG\u0016\u0014\u0018\n\u001a\u0005\u0006\u0019\u0002!\t!T\u0001\u0018GJ,\u0017\r^3U_BL7-T3tg\u0006<Wm\u0015;pe\u0016$\"AT)\u0011\u0005ay\u0015B\u0001)\u001a\u0005E!v\u000e]5d\u001b\u0016\u001c8/Y4f'R|'/\u001a\u0005\u0006%.\u0003\raU\u0001\fI\u0016\u001cH/\u001b8bi&|g\u000e\u0005\u0002G)&\u0011Qk\u0012\u0002\u000e\u0003\u000e$\u0018N^3N#R{\u0007/[2\t\u000b]\u0003A\u0011\u0001-\u0002\u0019M,G\u000fR5sK\u000e$xN]=\u0015\u0005IJ\u0006\"\u0002.W\u0001\u0004Y\u0016a\u00013jeB\u0011A,Y\u0007\u0002;*\u0011alX\u0001\u0003S>T\u0011\u0001Y\u0001\u0005U\u00064\u0018-\u0003\u0002c;\n!a)\u001b7f\u0011\u0015!\u0007\u0001\"\u0001f\u0003)\u0019\u0007.Z2la>Lg\u000e\u001e\u000b\u0003e\u0019DQaZ2A\u0002!\fAa]=oGB\u00111'[\u0005\u0003UR\u0012qAQ8pY\u0016\fg\u000eC\u0003m\u0001\u0011\u0005Q.\u0001\fde\u0016\fG/\u001a+sC:\u001c\u0018m\u0019;j_:\u001cFo\u001c:f)\u0005i\u0002\"B8\u0001\t\u0003\u0001\u0018aD:fiV\u001b\u0018mZ3NC:\fw-\u001a:\u0015\u0005I\n\b\"\u0002:o\u0001\u0004\u0019\u0018\u0001D;tC\u001e,W*\u00198bO\u0016\u0014\bC\u0001;x\u001b\u0005)(B\u0001<\u0007\u0003\u0015)8/Y4f\u0013\tAXOA\u0006TsN$X-\\+tC\u001e,\u0007\"\u0002>\u0001\t\u0003Y\u0018!E2p[6LG\u000f\u0016:b]N\f7\r^5p]R\u0011!\u0007 \u0005\u0006se\u0004\rA\u000f\u0005\u0006}\u0002!\ta`\u0001\u001fO\u0016$H*Y:u\u001b\u0016\u001c8/Y4f\u0005J|7.\u001a:TKF,XM\\2f\u0013\u0012$\u0012\u0001\u0011\u0005\b\u0003\u0007\u0001A\u0011AA\u0003\u00035\u0019X\r\u001e\"s_.,'OT1nKR\u0019!'a\u0002\t\u0011\u0005%\u0011\u0011\u0001a\u0001\u0003\u0017\t!B\u0019:pW\u0016\u0014h*Y7f!\u0011\ti!a\u0005\u000f\u0007M\ny!C\u0002\u0002\u0012Q\na\u0001\u0015:fI\u00164\u0017\u0002BA\u000b\u0003/\u0011aa\u0015;sS:<'bAA\ti!9\u00111\u0004\u0001\u0005\u0002\u0005u\u0011a\u0005:pY2\u0014\u0017mY6Ue\u0006t7/Y2uS>tGc\u0001\u001a\u0002 !1\u0011(!\u0007A\u0002iBq!a\t\u0001\t\u0003\t)#A\fsK6|g/\u001a+pa&\u001cW*Z:tC\u001e,7\u000b^8sKR\u0019!'a\n\t\rI\u000b\t\u00031\u0001T\u0011\u001d\tY\u0003\u0001C\u0001\u0003[\tAbZ3u\t&\u0014Xm\u0019;pef$\u0012a\u0017\u0005\u0007\u0003c\u0001A\u0011A@\u0002\tML'0\u001a\u0005\b\u0003k\u0001A\u0011AA\u001c\u0003]\u0011X-\\8wKF+X-^3NKN\u001c\u0018mZ3Ti>\u0014X\rF\u00023\u0003sAqAUA\u001a\u0001\u0004\tY\u0004E\u0002G\u0003{I1!a\u0010H\u00055\t5\r^5wK6\u000b\u0016+^3vK\"9\u00111\t\u0001\u0005\u0002\u0005\u0015\u0013aF2sK\u0006$X-U;fk\u0016lUm]:bO\u0016\u001cFo\u001c:f)\u0011\t9%!\u0014\u0011\u0007a\tI%C\u0002\u0002Le\u0011A\"T3tg\u0006<Wm\u0015;pe\u0016DqAUA!\u0001\u0004\tY\u0004C\u0004\u0002R\u0001!\t!a\u0015\u0002#\u0011,G.\u001a;f\u00032dW*Z:tC\u001e,7\u000fF\u00013\u0011\u001d\t9\u0006\u0001C\u0001\u00033\nqbZ3u\t\u0016\u001cH/\u001b8bi&|gn\u001d\u000b\u0003\u00037\u0002b!!\u0018\u0002d\u0005\u001dTBAA0\u0015\r\t\tgX\u0001\u0005kRLG.\u0003\u0003\u0002f\u0005}#aA*fiB\u0019a)!\u001b\n\u0007\u0005-tIA\nBGRLg/Z'R\t\u0016\u001cH/\u001b8bi&|g\u000eC\u0004\u0002p\u0001!\t!!\u001d\u0002\u0011I|G\u000e\u001c2bG.$2AMA:\u0011!\t)(!\u001cA\u0002\u0005]\u0014\u0001\u0002;yS\u0012\u00042ARA=\u0013\r\tYh\u0012\u0002\u000e)J\fgn]1di&|g.\u00133\t\u000f\u0005}\u0004\u0001\"\u0001\u0002\u0002\u00069!/Z2pm\u0016\u0014Hc\u0001\u001a\u0002\u0004\"A\u0011QQA?\u0001\u0004\t9)\u0001\u0005mSN$XM\\3s!\rA\u0012\u0011R\u0005\u0004\u0003\u0017K\"a\u0007+sC:\u001c\u0018m\u0019;j_:\u0014VmY8wKJLH*[:uK:,'\u000fC\u0004\u0002\u0010\u0002!\t!!%\u0002\u000fA\u0014X\r]1sKR\u0019!'a%\t\u0011\u0005U\u0014Q\u0012a\u0001\u0003oBq!a&\u0001\t\u0003\tI*\u0001\u0004d_6l\u0017\u000e\u001e\u000b\ne\u0005m\u0015QTAQ\u0003cC\u0001\"!\u001e\u0002\u0016\u0002\u0007\u0011q\u000f\u0005\b\u0003?\u000b)\n1\u0001i\u0003-9\u0018m\u001d)sKB\f'/\u001a3\t\u0011\u0005\r\u0016Q\u0013a\u0001\u0003K\u000b\u0011\u0002\u001d:f\u0007>lW.\u001b;\u0011\t\u0005\u001d\u0016QV\u0007\u0003\u0003SS1!a+`\u0003\u0011a\u0017M\\4\n\t\u0005=\u0016\u0011\u0016\u0002\t%Vtg.\u00192mK\"A\u00111WAK\u0001\u0004\t)+\u0001\u0006q_N$8i\\7nSRDq!a.\u0001\t\u0003\tI,\u0001\u0005hKR\u0004F*[:u)\u0011\tY,!1\u0011\u0007a\ti,C\u0002\u0002@f\u0011Q\u0001\u0015'jgRD\u0001\"a1\u00026\u0002\u0007\u00111B\u0001\u0005]\u0006lW\rC\u0004\u0002H\u0002!\t!!3\u0002\u0017I,Wn\u001c<f!2K7\u000f\u001e\u000b\u0004Q\u0006-\u0007\u0002CAb\u0003\u000b\u0004\r!a\u0003")
public abstract class ProxyLevelDBStore extends LockableServiceSupport implements PersistenceAdapter, TransactionStore, PListStore
{
    public abstract LevelDBStore proxy_target();
    
    @Override
    public void beginTransaction(final ConnectionContext context) {
        this.proxy_target().beginTransaction(context);
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) {
        return this.proxy_target().getLastProducerSequenceId(id);
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) {
        return this.proxy_target().createTopicMessageStore(destination);
    }
    
    @Override
    public void setDirectory(final File dir) {
        this.proxy_target().setDirectory(dir);
    }
    
    @Override
    public void checkpoint(final boolean sync) {
        this.proxy_target().checkpoint(sync);
    }
    
    @Override
    public TransactionStore createTransactionStore() {
        return this.proxy_target().createTransactionStore();
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        this.proxy_target().setUsageManager(usageManager);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) {
        this.proxy_target().commitTransaction(context);
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() {
        return this.proxy_target().getLastMessageBrokerSequenceId();
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
        this.proxy_target().setBrokerName(brokerName);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) {
        this.proxy_target().rollbackTransaction(context);
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
        this.proxy_target().removeTopicMessageStore(destination);
    }
    
    @Override
    public File getDirectory() {
        return this.proxy_target().getDirectory();
    }
    
    @Override
    public long size() {
        return this.proxy_target().size();
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
        this.proxy_target().removeQueueMessageStore(destination);
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) {
        return this.proxy_target().createQueueMessageStore(destination);
    }
    
    @Override
    public void deleteAllMessages() {
        this.proxy_target().deleteAllMessages();
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        return this.proxy_target().getDestinations();
    }
    
    @Override
    public void rollback(final TransactionId txid) {
        this.proxy_target().rollback(txid);
    }
    
    @Override
    public void recover(final TransactionRecoveryListener listener) {
        this.proxy_target().recover(listener);
    }
    
    @Override
    public void prepare(final TransactionId txid) {
        this.proxy_target().prepare(txid);
    }
    
    @Override
    public void commit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) {
        this.proxy_target().commit(txid, wasPrepared, preCommit, postCommit);
    }
    
    @Override
    public PList getPList(final String name) {
        return this.proxy_target().getPList(name);
    }
    
    @Override
    public boolean removePList(final String name) {
        return this.proxy_target().removePList(name);
    }
}
