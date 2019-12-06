// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import scala.Tuple2;
import scala.collection.mutable.HashSet$;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.ListBuffer$;
import scala.collection.immutable.Nil$;
import org.apache.activemq.util.ByteSequence;
import org.fusesource.hawtbuf.Buffer;
import scala.collection.Seq;
import scala.Tuple2$mcJJ$sp;
import org.fusesource.hawtdispatch.package$;
import org.apache.activemq.command.Message;
import scala.Option;
import scala.Predef;
import scala.MatchError;
import scala.None$;
import scala.Some;
import scala.Function2;
import scala.runtime.BoxesRunTime;
import scala.Function1;
import scala.Predef$;
import scala.runtime.BoxedUnit;
import scala.Function0;
import scala.collection.mutable.ListBuffer;
import org.apache.activemq.command.MessageId;
import scala.collection.immutable.Map;
import scala.reflect.ScalaSignature;
import org.fusesource.hawtdispatch.BaseRetained;

@ScalaSignature(bytes = "\u0006\u0001\tEh\u0001B\u0001\u0003\u0001-\u0011A\u0002R3mCf\f'\r\\3V\u001f^S!a\u0001\u0003\u0002\u000f1,g/\u001a7eE*\u0011QAB\u0001\tC\u000e$\u0018N^3nc*\u0011q\u0001C\u0001\u0007CB\f7\r[3\u000b\u0003%\t1a\u001c:h\u0007\u0001\u0019\"\u0001\u0001\u0007\u0011\u00055\u0011R\"\u0001\b\u000b\u0005=\u0001\u0012\u0001\u00045boR$\u0017n\u001d9bi\u000eD'BA\t\t\u0003)1Wo]3t_V\u00148-Z\u0005\u0003'9\u0011ABQ1tKJ+G/Y5oK\u0012D\u0001\"\u0006\u0001\u0003\u0006\u0004%\tAF\u0001\b[\u0006t\u0017mZ3s+\u00059\u0002C\u0001\r\u001a\u001b\u0005\u0011\u0011B\u0001\u000e\u0003\u0005%!%)T1oC\u001e,'\u000f\u0003\u0005\u001d\u0001\t\u0005\t\u0015!\u0003\u0018\u0003!i\u0017M\\1hKJ\u0004\u0003\"\u0002\u0010\u0001\t\u0003y\u0012A\u0002\u001fj]&$h\b\u0006\u0002!CA\u0011\u0001\u0004\u0001\u0005\u0006+u\u0001\ra\u0006\u0005\bG\u0001\u0011\r\u0011\"\u0001%\u0003=\u0019w.\u001e8u\t><hNR;ukJ,W#A\u0013\u0011\u0007a1\u0003&\u0003\u0002(\u0005\ty1i\\;oi\u0012{wO\u001c$viV\u0014X\r\u0005\u0002*Y5\t!FC\u0001,\u0003\u0015\u00198-\u00197b\u0013\ti#F\u0001\u0004B]f\u0014VM\u001a\u0005\u0007_\u0001\u0001\u000b\u0011B\u0013\u0002!\r|WO\u001c;E_^tg)\u001e;ve\u0016\u0004\u0003bB\u0019\u0001\u0001\u0004%\tAM\u0001\tG\u0006t7-\u001a7fIV\t1\u0007\u0005\u0002*i%\u0011QG\u000b\u0002\b\u0005>|G.Z1o\u0011\u001d9\u0004\u00011A\u0005\u0002a\nAbY1oG\u0016dW\rZ0%KF$\"!\u000f\u001f\u0011\u0005%R\u0014BA\u001e+\u0005\u0011)f.\u001b;\t\u000fu2\u0014\u0011!a\u0001g\u0005\u0019\u0001\u0010J\u0019\t\r}\u0002\u0001\u0015)\u00034\u0003%\u0019\u0017M\\2fY\u0016$\u0007\u0005C\u0004B\u0001\t\u0007I\u0011\u0001\"\u0002\u000bU|w/\u00133\u0016\u0003\r\u0003\"!\u000b#\n\u0005\u0015S#aA%oi\"1q\t\u0001Q\u0001\n\r\u000ba!^8x\u0013\u0012\u0004\u0003bB%\u0001\u0001\u0004%\tAS\u0001\bC\u000e$\u0018n\u001c8t+\u0005Y\u0005\u0003\u0002'R'fk\u0011!\u0014\u0006\u0003\u001d>\u000b\u0011\"[7nkR\f'\r\\3\u000b\u0005AS\u0013AC2pY2,7\r^5p]&\u0011!+\u0014\u0002\u0004\u001b\u0006\u0004\bC\u0001+X\u001b\u0005)&B\u0001,\u0005\u0003\u001d\u0019w.\\7b]\u0012L!\u0001W+\u0003\u00135+7o]1hK&#\u0007C\u0001.\\\u001b\u0005\u0001a\u0001\u0002/\u0001\u0001u\u0013Q\"T3tg\u0006<W-Q2uS>t7CA.)\u0011\u0015q2\f\"\u0001`)\u0005I\u0006\"C1\\\u0001\u0004\u0005\r\u0011\"\u0001c\u0003\tIG-F\u0001T\u0011%!7\f1AA\u0002\u0013\u0005Q-\u0001\u0004jI~#S-\u001d\u000b\u0003s\u0019Dq!P2\u0002\u0002\u0003\u00071\u000b\u0003\u0004i7\u0002\u0006KaU\u0001\u0004S\u0012\u0004\u0003b\u00026\\\u0001\u0004%\ta[\u0001\u000e[\u0016\u001c8/Y4f%\u0016\u001cwN\u001d3\u0016\u00031\u0004\"\u0001G7\n\u00059\u0014!!D'fgN\fw-\u001a*fG>\u0014H\rC\u0004q7\u0002\u0007I\u0011A9\u0002#5,7o]1hKJ+7m\u001c:e?\u0012*\u0017\u000f\u0006\u0002:e\"9Qh\\A\u0001\u0002\u0004a\u0007B\u0002;\\A\u0003&A.\u0001\bnKN\u001c\u0018mZ3SK\u000e|'\u000f\u001a\u0011\t\u000fY\\\u0006\u0019!C\u0001o\u0006AQM\\9vKV,7/F\u0001y!\rIHP`\u0007\u0002u*\u00111pT\u0001\b[V$\u0018M\u00197f\u0013\ti(P\u0001\u0006MSN$()\u001e4gKJ\u0004\"\u0001G@\n\u0007\u0005\u0005!A\u0001\tRk\u0016,X-\u00128uef\u0014VmY8sI\"I\u0011QA.A\u0002\u0013\u0005\u0011qA\u0001\rK:\fX/Z;fg~#S-\u001d\u000b\u0004s\u0005%\u0001\u0002C\u001f\u0002\u0004\u0005\u0005\t\u0019\u0001=\t\u000f\u000551\f)Q\u0005q\u0006IQM\\9vKV,7\u000f\t\u0005\t\u0003#Y\u0006\u0019!C\u0001o\u0006AA-Z9vKV,7\u000fC\u0005\u0002\u0016m\u0003\r\u0011\"\u0001\u0002\u0018\u0005aA-Z9vKV,7o\u0018\u0013fcR\u0019\u0011(!\u0007\t\u0011u\n\u0019\"!AA\u0002aDq!!\b\\A\u0003&\u00010A\u0005eKF,X-^3tA!I\u0011\u0011E.A\u0002\u0013\u0005\u00111E\u0001\u0007q\u0006\f5m[:\u0016\u0005\u0005\u0015\u0002\u0003B=}\u0003O\u00012\u0001GA\u0015\u0013\r\tYC\u0001\u0002\f1\u0006\f5m\u001b*fG>\u0014H\rC\u0005\u00020m\u0003\r\u0011\"\u0001\u00022\u0005Q\u00010Y!dWN|F%Z9\u0015\u0007e\n\u0019\u0004C\u0005>\u0003[\t\t\u00111\u0001\u0002&!A\u0011qG.!B\u0013\t)#A\u0004yC\u0006\u001b7n\u001d\u0011\t\u000f\u0005m2\f\"\u0001\u0002>\u0005\u0019Qo\\<\u0016\u0003\u0001Bq!!\u0011\\\t\u0003\t\u0019%A\u0004jg\u0016k\u0007\u000f^=\u0015\u0003MBq!a\u0012\\\t\u0003\tI%\u0001\u0004dC:\u001cW\r\u001c\u000b\u0002s!1\u0011QJ.\u0005\u0002I\n!b]=oG:+W\rZ3e\u0011\u001d\t\tf\u0017C\u0001\u0003'\nAa]5{KV\u0011\u0011Q\u000b\t\u0004S\u0005]\u0013bAA-U\t!Aj\u001c8h\u0011\u001d\tif\u0017C\u0001\u0003\u0007\n\u0011#\u00193e)>\u0004VM\u001c3j]\u001e\u001cFo\u001c:f\u0011\u001d\t\tg\u0017C\u0001\u0003G\naC]3n_Z,gI]8n!\u0016tG-\u001b8h'R|'/\u001a\u000b\u0003\u0003K\u00022!KA4\u0013\r\tIG\u000b\u0002\u0004\u0003:L\b\"CA7\u0001\u0001\u0007I\u0011AA8\u0003-\t7\r^5p]N|F%Z9\u0015\u0007e\n\t\b\u0003\u0005>\u0003W\n\t\u00111\u0001L\u0011\u001d\t)\b\u0001Q!\n-\u000b\u0001\"Y2uS>t7\u000f\t\u0005\n\u0003s\u0002\u0001\u0019!C\u0001\u0003w\nqa];c\u0003\u000e\\7/\u0006\u0002\u0002~A!\u0011\u0010`A@!\rA\u0012\u0011Q\u0005\u0004\u0003\u0007\u0013!\u0001D*vE\u0006\u001b7NU3d_J$\u0007\"CAD\u0001\u0001\u0007I\u0011AAE\u0003-\u0019XOY!dWN|F%Z9\u0015\u0007e\nY\tC\u0005>\u0003\u000b\u000b\t\u00111\u0001\u0002~!A\u0011q\u0012\u0001!B\u0013\ti(\u0001\u0005tk\n\f5m[:!\u0011!\t\u0019\n\u0001a\u0001\n\u0003\u0011\u0014!C2p[BdW\r^3e\u0011%\t9\n\u0001a\u0001\n\u0003\tI*A\u0007d_6\u0004H.\u001a;fI~#S-\u001d\u000b\u0004s\u0005m\u0005\u0002C\u001f\u0002\u0016\u0006\u0005\t\u0019A\u001a\t\u000f\u0005}\u0005\u0001)Q\u0005g\u0005Q1m\\7qY\u0016$X\r\u001a\u0011\t\u0011\u0005\r\u0006\u00011A\u0005\u0002I\nA\u0002Z5tC\ndW\rR3mCfD\u0011\"a*\u0001\u0001\u0004%\t!!+\u0002!\u0011L7/\u00192mK\u0012+G.Y=`I\u0015\fHcA\u001d\u0002,\"AQ(!*\u0002\u0002\u0003\u00071\u0007C\u0004\u00020\u0002\u0001\u000b\u0015B\u001a\u0002\u001b\u0011L7/\u00192mK\u0012+G.Y=!\u0011!\t\u0019\f\u0001a\u0001\n\u0003\u0011\u0015\u0001\u00053fY\u0006L\u0018M\u00197f\u0003\u000e$\u0018n\u001c8t\u0011%\t9\f\u0001a\u0001\n\u0003\tI,\u0001\u000beK2\f\u00170\u00192mK\u0006\u001bG/[8og~#S-\u001d\u000b\u0004s\u0005m\u0006\u0002C\u001f\u00026\u0006\u0005\t\u0019A\"\t\u000f\u0005}\u0006\u0001)Q\u0005\u0007\u0006\tB-\u001a7bs\u0006\u0014G.Z!di&|gn\u001d\u0011\t\u0013\u0005\r\u0007\u00011A\u0005\n\u0005\u0015\u0017AB0ti\u0006$X-\u0006\u0002\u0002HB\u0019\u0001$!3\n\u0007\u0005-'A\u0001\u0005V_^\u001cF/\u0019;f\u0011%\ty\r\u0001a\u0001\n\u0013\t\t.\u0001\u0006`gR\fG/Z0%KF$2!OAj\u0011%i\u0014QZA\u0001\u0002\u0004\t9\r\u0003\u0005\u0002X\u0002\u0001\u000b\u0015BAd\u0003\u001dy6\u000f^1uK\u0002Bq!a7\u0001\t\u0003\t)-A\u0003ti\u0006$X\rC\u0004\u0002`\u0002!\t!!9\u0002\u0013M$\u0018\r^3`I\u0015\fHcA\u001d\u0002d\"A\u0011Q]Ao\u0001\u0004\t9-\u0001\u0003oKb$\b\u0002CAu\u0001\u0001\u0007I\u0011\u0001\u001a\u0002\u0011MLhn\u0019$mC\u001eD\u0011\"!<\u0001\u0001\u0004%\t!a<\u0002\u0019MLhn\u0019$mC\u001e|F%Z9\u0015\u0007e\n\t\u0010\u0003\u0005>\u0003W\f\t\u00111\u00014\u0011\u001d\t)\u0010\u0001Q!\nM\n\u0011b]=oG\u001ac\u0017m\u001a\u0011\t\r\u00055\u0003\u0001\"\u00013\u0011\u001d\t\t\u0006\u0001C\u0001\u0003'Bq!!@\u0001\t\u0003\tI%\u0001\u0007d_6\u0004H.\u001a;f\u0003N\f\u0007\u000f\u0003\u0004\u0003\u0002\u0001!\tAM\u0001\nI\u0016d\u0017-_1cY\u0016DqA!\u0002\u0001\t\u0003\u00119!\u0001\u0002s[R\u0019\u0011H!\u0003\t\u000f\t-!1\u0001a\u0001'\u0006\u0019Qn]4\t\u000f\u0005\u001d\u0003\u0001\"\u0001\u0003\u0010U\t\u0011\bC\u0004\u0003\u0014\u0001!\tA!\u0006\u0002\u0013\u001d,G/Q2uS>tGcA-\u0003\u0018!1\u0011M!\u0005A\u0002MCqAa\u0007\u0001\t\u0003\u0011i\"A\tva\u0012\fG/Z!dWB{7/\u001b;j_:$b!! \u0003 \t\r\u0002\u0002\u0003B\u0011\u00053\u0001\r!!\u0016\u0002\u000fM,(mX6fs\"A!Q\u0005B\r\u0001\u0004\t)&A\u0004bG.|6/Z9\t\u000f\t%\u0002\u0001\"\u0001\u0003,\u0005)\u00010Y!dWR\u0019QE!\f\t\u0011\t=\"q\u0005a\u0001\u0003O\taA]3d_J$\u0007b\u0002B\u001a\u0001\u0011\u0005!QG\u0001\bK:\fX/Z;f)%)#q\u0007B\u001e\u0005\u007f\u0011I\u0005\u0003\u0005\u0003:\tE\u0002\u0019AA+\u0003!\tX/Z;f\u0017\u0016L\b\u0002\u0003B\u001f\u0005c\u0001\r!!\u0016\u0002\u0011E,X-^3TKFD\u0001B!\u0011\u00032\u0001\u0007!1I\u0001\b[\u0016\u001c8/Y4f!\r!&QI\u0005\u0004\u0005\u000f*&aB'fgN\fw-\u001a\u0005\b\u0005\u0017\u0012\t\u00041\u00014\u00035!W\r\\1z?\u0016t\u0017/^3vK\"9!q\n\u0001\u0005\u0002\tE\u0013aE5oGJ,W.\u001a8u%\u0016$W\r\\5wKJLH#B\u0013\u0003T\t]\u0003\u0002\u0003B+\u0005\u001b\u0002\r!!\u0016\u0002!\u0015D\b/Z2uK\u0012\fV/Z;f\u0017\u0016L\bBB1\u0003N\u0001\u00071\u000bC\u0004\u0003\\\u0001!\tA!\u0018\u0002\u000f\u0011,\u0017/^3vKR)QEa\u0018\u0003b!A!Q\u000bB-\u0001\u0004\t)\u0006\u0003\u0004b\u00053\u0002\ra\u0015\u0005\b\u0005K\u0002A\u0011\u0001B\b\u00035\u0019w.\u001c9mKR,w,Y:ba\"I!\u0011\u000e\u0001A\u0002\u0013\u0005!1N\u0001\u0013G>l\u0007\u000f\\3uK~c\u0017n\u001d;f]\u0016\u00148/\u0006\u0002\u0003nA!\u0011\u0010 B8!\u0011I#\u0011O\u001d\n\u0007\tM$FA\u0005Gk:\u001cG/[8oa!I!q\u000f\u0001A\u0002\u0013\u0005!\u0011P\u0001\u0017G>l\u0007\u000f\\3uK~c\u0017n\u001d;f]\u0016\u00148o\u0018\u0013fcR\u0019\u0011Ha\u001f\t\u0013u\u0012)(!AA\u0002\t5\u0004\u0002\u0003B@\u0001\u0001\u0006KA!\u001c\u0002'\r|W\u000e\u001d7fi\u0016|F.[:uK:,'o\u001d\u0011\t\u000f\t\r\u0005\u0001\"\u0001\u0003\u0006\u0006\u0019\u0012\r\u001a3D_6\u0004H.\u001a;f\u0019&\u001cH/\u001a8feR\u0019\u0011Ha\"\t\u0013\t%%\u0011\u0011CA\u0002\t-\u0015\u0001\u00024v]\u000e\u0004B!\u000bBGs%\u0019!q\u0012\u0016\u0003\u0011q\u0012\u0017P\\1nKzB\u0011Ba%\u0001\u0001\u0004%\t!a\u0015\u0002#\u0005\u001c\u0018P\\2DCB\f7-\u001b;z+N,G\rC\u0005\u0003\u0018\u0002\u0001\r\u0011\"\u0001\u0003\u001a\u0006)\u0012m]=oG\u000e\u000b\u0007/Y2jif,6/\u001a3`I\u0015\fHcA\u001d\u0003\u001c\"IQH!&\u0002\u0002\u0003\u0007\u0011Q\u000b\u0005\t\u0005?\u0003\u0001\u0015)\u0003\u0002V\u0005\u0011\u0012m]=oG\u000e\u000b\u0007/Y2jif,6/\u001a3!\u0011%\u0011\u0019\u000b\u0001a\u0001\n\u0003\t\u0019&A\u0006eSN\u0004xn]3e?\u0006$\b\"\u0003BT\u0001\u0001\u0007I\u0011\u0001BU\u0003=!\u0017n\u001d9pg\u0016$w,\u0019;`I\u0015\fHcA\u001d\u0003,\"IQH!*\u0002\u0002\u0003\u0007\u0011Q\u000b\u0005\t\u0005_\u0003\u0001\u0015)\u0003\u0002V\u0005aA-[:q_N,GmX1uA!9!1\u0017\u0001\u0005B\u0005%\u0013a\u00023jgB|7/\u001a\u0005\b\u0005o\u0003A\u0011\u0001B]\u0003-ygnQ8na2,G/\u001a3\u0015\u0007e\u0012Y\f\u0003\u0006\u0003>\nU\u0006\u0013!a\u0001\u0005\u007f\u000bQ!\u001a:s_J\u0004BA!1\u0003R:!!1\u0019Bg\u001d\u0011\u0011)Ma3\u000e\u0005\t\u001d'b\u0001Be\u0015\u00051AH]8pizJ\u0011aK\u0005\u0004\u0005\u001fT\u0013a\u00029bG.\fw-Z\u0005\u0005\u0005'\u0014)NA\u0005UQJ|w/\u00192mK*\u0019!q\u001a\u0016\t\u0013\te\u0007!%A\u0005\u0002\tm\u0017!F8o\u0007>l\u0007\u000f\\3uK\u0012$C-\u001a4bk2$H%M\u000b\u0003\u0005;TCAa0\u0003`.\u0012!\u0011\u001d\t\u0005\u0005G\u0014i/\u0004\u0002\u0003f*!!q\u001dBu\u0003%)hn\u00195fG.,GMC\u0002\u0003l*\n!\"\u00198o_R\fG/[8o\u0013\u0011\u0011yO!:\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\r")
public class DelayableUOW extends BaseRetained
{
    private final DBManager manager;
    private final CountDownFuture<Object> countDownFuture;
    private boolean canceled;
    private final int uowId;
    private Map<MessageId, MessageAction> actions;
    private ListBuffer<SubAckRecord> subAcks;
    private boolean completed;
    private boolean disableDelay;
    private int delayableActions;
    private UowState _state;
    private boolean syncFlag;
    private ListBuffer<Function0<BoxedUnit>> complete_listeners;
    private long asyncCapacityUsed;
    private long disposed_at;
    
    public DBManager manager() {
        return this.manager;
    }
    
    public CountDownFuture<Object> countDownFuture() {
        return this.countDownFuture;
    }
    
    public boolean canceled() {
        return this.canceled;
    }
    
    public void canceled_$eq(final boolean x$1) {
        this.canceled = x$1;
    }
    
    public int uowId() {
        return this.uowId;
    }
    
    public Map<MessageId, MessageAction> actions() {
        return this.actions;
    }
    
    public void actions_$eq(final Map<MessageId, MessageAction> x$1) {
        this.actions = x$1;
    }
    
    public ListBuffer<SubAckRecord> subAcks() {
        return this.subAcks;
    }
    
    public void subAcks_$eq(final ListBuffer<SubAckRecord> x$1) {
        this.subAcks = x$1;
    }
    
    public boolean completed() {
        return this.completed;
    }
    
    public void completed_$eq(final boolean x$1) {
        this.completed = x$1;
    }
    
    public boolean disableDelay() {
        return this.disableDelay;
    }
    
    public void disableDelay_$eq(final boolean x$1) {
        this.disableDelay = x$1;
    }
    
    public int delayableActions() {
        return this.delayableActions;
    }
    
    public void delayableActions_$eq(final int x$1) {
        this.delayableActions = x$1;
    }
    
    private UowState _state() {
        return this._state;
    }
    
    private void _state_$eq(final UowState x$1) {
        this._state = x$1;
    }
    
    public UowState state() {
        return this._state();
    }
    
    public void state_$eq(final UowState next) {
        Predef$.MODULE$.assert(this._state().stage() < next.stage());
        this._state_$eq(next);
    }
    
    public boolean syncFlag() {
        return this.syncFlag;
    }
    
    public void syncFlag_$eq(final boolean x$1) {
        this.syncFlag = x$1;
    }
    
    public boolean syncNeeded() {
        return this.syncFlag() || this.actions().find((Function1)new DelayableUOW$$anonfun$syncNeeded.DelayableUOW$$anonfun$syncNeeded$1(this)).isDefined();
    }
    
    public long size() {
        return 100L + BoxesRunTime.unboxToLong(this.actions().foldLeft((Object)BoxesRunTime.boxToLong(0L), (Function2)new DelayableUOW$$anonfun$size.DelayableUOW$$anonfun$size$1(this))) + this.subAcks().size() * 100;
    }
    
    public synchronized void completeAsap() {
        this.disableDelay_$eq(true);
    }
    
    public boolean delayable() {
        return !this.disableDelay() && this.delayableActions() > 0 && this.manager().flushDelay() > 0;
    }
    
    public void rm(final MessageId msg) {
        this.actions_$eq((Map<MessageId, MessageAction>)this.actions().$minus((Object)msg));
        if (this.actions().isEmpty() && this.state().stage() < UowFlushing$.MODULE$.stage()) {
            this.cancel();
        }
    }
    
    public void cancel() {
        this.manager().dispatchQueue().assertExecuting();
        this.manager().uowCanceledCounter_$eq(this.manager().uowCanceledCounter() + 1L);
        this.canceled_$eq(true);
        this.manager().flush_queue().remove(BoxesRunTime.boxToInteger(this.uowId()));
        this.onCompleted(this.onCompleted$default$1());
    }
    
    public MessageAction getAction(final MessageId id) {
        final Option value = this.actions().get((Object)id);
        MessageAction messageAction;
        if (value instanceof Some) {
            final MessageAction x = messageAction = (MessageAction)((Some)value).x();
        }
        else {
            if (!None$.MODULE$.equals(value)) {
                throw new MatchError((Object)value);
            }
            final MessageAction x2 = new MessageAction();
            x2.id_$eq(id);
            this.actions_$eq((Map<MessageId, MessageAction>)this.actions().$plus(Predef.ArrowAssoc$.MODULE$.$minus$greater$extension(Predef$.MODULE$.ArrowAssoc((Object)id), (Object)x2)));
            messageAction = x2;
        }
        return messageAction;
    }
    
    public ListBuffer<SubAckRecord> updateAckPosition(final long sub_key, final long ack_seq) {
        return (ListBuffer<SubAckRecord>)this.subAcks().$plus$eq((Object)new SubAckRecord(sub_key, ack_seq));
    }
    
    public CountDownFuture<Object> xaAck(final XaAckRecord record) {
        synchronized (this) {
            this.getAction(record.ack().getLastMessageId()).xaAcks().$plus$eq((Object)record);
            return this.countDownFuture();
        }
    }
    
    public CountDownFuture<Object> enqueue(final long queueKey, final long queueSeq, final Message message, final boolean delay_enqueue) {
        final boolean delay = delay_enqueue && message.getTransactionId() == null;
        if (delay) {
            this.manager().uowEnqueueDelayReqested_$eq(this.manager().uowEnqueueDelayReqested() + 1L);
        }
        else {
            this.manager().uowEnqueueNodelayReqested_$eq(this.manager().uowEnqueueNodelayReqested() + 1L);
        }
        final MessageId id = message.getMessageId();
        final Object dataLocator = id.getDataLocator();
        Label_0381: {
            MessageRecord create_message_record$1;
            if (dataLocator == null) {
                create_message_record$1 = this.create_message_record$1(message, id);
            }
            else if (dataLocator instanceof MessageRecord) {
                final MessageRecord messageRecord2 = (MessageRecord)dataLocator;
                final LevelDBStore store = messageRecord2.store();
                final LevelDBStore parent = this.manager().parent();
                MessageRecord create_message_record$2 = null;
                Label_0155: {
                    Label_0147: {
                        if (store == null) {
                            if (parent != null) {
                                break Label_0147;
                            }
                        }
                        else if (!store.equals(parent)) {
                            break Label_0147;
                        }
                        create_message_record$2 = messageRecord2;
                        break Label_0155;
                    }
                    create_message_record$2 = this.create_message_record$1(message, id);
                }
                create_message_record$1 = create_message_record$2;
            }
            else {
                if (!(dataLocator instanceof DataLocator)) {
                    break Label_0381;
                }
                final LevelDBStore store2 = ((DataLocator)dataLocator).store();
                final LevelDBStore parent2 = this.manager().parent();
                MessageRecord create_message_record$3 = null;
                Label_0222: {
                    Label_0214: {
                        if (store2 == null) {
                            if (parent2 != null) {
                                break Label_0214;
                            }
                        }
                        else if (!store2.equals(parent2)) {
                            break Label_0214;
                        }
                        create_message_record$3 = null;
                        break Label_0222;
                    }
                    create_message_record$3 = this.create_message_record$1(message, id);
                }
                create_message_record$1 = create_message_record$3;
            }
            final MessageRecord messageRecord = create_message_record$1;
            final QueueEntryRecord entry = new QueueEntryRecord(id, queueKey, queueSeq, QueueEntryRecord$.MODULE$.apply$default$4());
            Predef$.MODULE$.assert(id.getEntryLocator() == null);
            id.setEntryLocator(new EntryLocator(queueKey, queueSeq));
            synchronized (this) {
                if (!delay) {
                    this.disableDelay_$eq(true);
                }
                final MessageAction action = this.getAction(entry.id());
                action.messageRecord_$eq(messageRecord);
                action.enqueues().$plus$eq((Object)entry);
                this.delayableActions_$eq(this.delayableActions() + 1);
                final MessageAction messageAction = action;
                // monitorexit(this)
                final MessageAction a = messageAction;
                package$.MODULE$.DispatchQueueWrapper(this.manager().dispatchQueue()).apply((Function0)new DelayableUOW$$anonfun$enqueue.DelayableUOW$$anonfun$enqueue$1(this, entry, a));
                return this.countDownFuture();
                throw new MatchError(dataLocator);
            }
        }
    }
    
    public CountDownFuture<Object> incrementRedelivery(final long expectedQueueKey, final MessageId id) {
        Label_0197: {
            if (id.getEntryLocator() == null) {
                break Label_0197;
            }
            final EntryLocator entryLocator = (EntryLocator)id.getEntryLocator();
            Label_0202: {
                if (entryLocator == null) {
                    break Label_0202;
                }
                final long queueKey = entryLocator.qid();
                final long queueSeq = entryLocator.seq();
                final Tuple2$mcJJ$sp tuple2$mcJJ$sp = new Tuple2$mcJJ$sp(queueKey, queueSeq);
                final long queueKey2 = ((Tuple2)tuple2$mcJJ$sp)._1$mcJ$sp();
                final long queueSeq2 = ((Tuple2)tuple2$mcJJ$sp)._2$mcJ$sp();
                Predef$.MODULE$.assert(queueKey2 == expectedQueueKey);
                final int counter = this.manager().client().getDeliveryCounter(queueKey2, queueSeq2);
                final QueueEntryRecord entry = new QueueEntryRecord(id, queueKey2, queueSeq2, counter + 1);
                synchronized (this) {
                    final MessageAction action = this.getAction(entry.id());
                    action.enqueues().$plus$eq((Object)entry);
                    this.delayableActions_$eq(this.delayableActions() + 1);
                    final MessageAction messageAction = action;
                    // monitorexit(this)
                    final MessageAction a = messageAction;
                    package$.MODULE$.DispatchQueueWrapper(this.manager().dispatchQueue()).apply((Function0)new DelayableUOW$$anonfun$incrementRedelivery.DelayableUOW$$anonfun$incrementRedelivery$1(this, entry, a));
                    return this.countDownFuture();
                    throw new MatchError((Object)entryLocator);
                }
            }
        }
    }
    
    public CountDownFuture<Object> dequeue(final long expectedQueueKey, final MessageId id) {
        Label_0135: {
            if (id.getEntryLocator() == null) {
                final BoxedUnit unit = BoxedUnit.UNIT;
                break Label_0135;
            }
            final EntryLocator entryLocator = (EntryLocator)id.getEntryLocator();
            Label_0141: {
                if (entryLocator == null) {
                    break Label_0141;
                }
                final long queueKey = entryLocator.qid();
                final long queueSeq = entryLocator.seq();
                final Tuple2$mcJJ$sp tuple2$mcJJ$sp = new Tuple2$mcJJ$sp(queueKey, queueSeq);
                final long queueKey2 = ((Tuple2)tuple2$mcJJ$sp)._1$mcJ$sp();
                final long queueSeq2 = ((Tuple2)tuple2$mcJJ$sp)._2$mcJ$sp();
                Predef$.MODULE$.assert(queueKey2 == expectedQueueKey);
                final QueueEntryRecord entry = new QueueEntryRecord(id, queueKey2, queueSeq2, QueueEntryRecord$.MODULE$.apply$default$4());
                synchronized (this) {
                    this.getAction(id).dequeues().$plus$eq((Object)entry);
                    // monitorexit(this)
                    return this.countDownFuture();
                    throw new MatchError((Object)entryLocator);
                }
            }
        }
    }
    
    public synchronized void complete_asap() {
        this.disableDelay_$eq(true);
        if (this.state() == UowDelayed$.MODULE$) {
            this.manager().enqueueFlush(this);
        }
    }
    
    public ListBuffer<Function0<BoxedUnit>> complete_listeners() {
        return this.complete_listeners;
    }
    
    public void complete_listeners_$eq(final ListBuffer<Function0<BoxedUnit>> x$1) {
        this.complete_listeners = x$1;
    }
    
    public void addCompleteListener(final Function0<BoxedUnit> func) {
        this.complete_listeners().append((Seq)Predef$.MODULE$.wrapRefArray((Object[])new Function0[] { func }));
    }
    
    public long asyncCapacityUsed() {
        return this.asyncCapacityUsed;
    }
    
    public void asyncCapacityUsed_$eq(final long x$1) {
        this.asyncCapacityUsed = x$1;
    }
    
    public long disposed_at() {
        return this.disposed_at;
    }
    
    public void disposed_at_$eq(final long x$1) {
        this.disposed_at = x$1;
    }
    
    public synchronized void dispose() {
        this.state_$eq(UowClosed$.MODULE$);
        this.disposed_at_$eq(System.nanoTime());
        if (this.syncNeeded()) {
            final BoxedUnit boxedUnit = BoxedUnit.UNIT;
        }
        else {
            final long s = this.size();
            if (this.manager().asyncCapacityRemaining().addAndGet(-s) > 0L) {
                this.asyncCapacityUsed_$eq(s);
                this.complete_listeners().foreach((Function1)new DelayableUOW$$anonfun$dispose.DelayableUOW$$anonfun$dispose$2(this));
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
            else {
                BoxesRunTime.boxToLong(this.manager().asyncCapacityRemaining().addAndGet(s));
            }
        }
        package$.MODULE$.DispatchQueueWrapper(this.manager().dispatchQueue()).apply((Function0)new DelayableUOW$$anonfun$dispose.DelayableUOW$$anonfun$dispose$1(this));
    }
    
    public synchronized void onCompleted(final Throwable error) {
        if (this.state().stage() < UowCompleted$.MODULE$.stage()) {
            this.state_$eq(UowCompleted$.MODULE$);
            if (this.asyncCapacityUsed() != 0L) {
                this.manager().asyncCapacityRemaining().addAndGet(this.asyncCapacityUsed());
                this.asyncCapacityUsed_$eq(0L);
            }
            else {
                this.manager().uow_complete_latency().add(System.nanoTime() - this.disposed_at());
                this.complete_listeners().foreach((Function1)new DelayableUOW$$anonfun$onCompleted.DelayableUOW$$anonfun$onCompleted$1(this));
            }
            if (error == null) {
                this.countDownFuture().set(null);
            }
            else {
                this.countDownFuture().failed(error);
            }
            this.actions().withFilter((Function1)new DelayableUOW$$anonfun$onCompleted.DelayableUOW$$anonfun$onCompleted$2(this)).foreach((Function1)new DelayableUOW$$anonfun$onCompleted.DelayableUOW$$anonfun$onCompleted$3(this));
            super.dispose();
        }
    }
    
    public Throwable onCompleted$default$1() {
        return null;
    }
    
    private final MessageRecord create_message_record$1(final Message message$1, final MessageId id$1) {
        message$1.storeContentAndClear();
        final ByteSequence packet = this.manager().parent().wireFormat().marshal(message$1);
        Buffer data = new Buffer(packet.data, packet.offset, packet.length);
        if (this.manager().snappyCompressLogs()) {
            data = org.apache.activemq.leveldb.package$.MODULE$.Snappy().compress(data);
        }
        final MessageRecord record = new MessageRecord(this.manager().parent(), id$1, data, message$1.isResponseRequired());
        id$1.setDataLocator(record);
        return record;
    }
    
    public DelayableUOW(final DBManager manager) {
        this.manager = manager;
        this.countDownFuture = new CountDownFuture<Object>();
        this.canceled = false;
        this.uowId = manager.lastUowId().incrementAndGet();
        this.actions = (Map<MessageId, MessageAction>)Predef$.MODULE$.Map().apply((Seq)Nil$.MODULE$);
        this.subAcks = (ListBuffer<SubAckRecord>)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
        this.completed = false;
        this.disableDelay = false;
        this.delayableActions = 0;
        this._state = UowOpen$.MODULE$;
        this.syncFlag = false;
        this.complete_listeners = (ListBuffer<Function0<BoxedUnit>>)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
        this.asyncCapacityUsed = 0L;
        this.disposed_at = 0L;
    }
    
    public class MessageAction
    {
        private MessageId id;
        private MessageRecord messageRecord;
        private ListBuffer<QueueEntryRecord> enqueues;
        private ListBuffer<QueueEntryRecord> dequeues;
        private ListBuffer<XaAckRecord> xaAcks;
        
        public MessageId id() {
            return this.id;
        }
        
        public void id_$eq(final MessageId x$1) {
            this.id = x$1;
        }
        
        public MessageRecord messageRecord() {
            return this.messageRecord;
        }
        
        public void messageRecord_$eq(final MessageRecord x$1) {
            this.messageRecord = x$1;
        }
        
        public ListBuffer<QueueEntryRecord> enqueues() {
            return this.enqueues;
        }
        
        public void enqueues_$eq(final ListBuffer<QueueEntryRecord> x$1) {
            this.enqueues = x$1;
        }
        
        public ListBuffer<QueueEntryRecord> dequeues() {
            return this.dequeues;
        }
        
        public void dequeues_$eq(final ListBuffer<QueueEntryRecord> x$1) {
            this.dequeues = x$1;
        }
        
        public ListBuffer<XaAckRecord> xaAcks() {
            return this.xaAcks;
        }
        
        public void xaAcks_$eq(final ListBuffer<XaAckRecord> x$1) {
            this.xaAcks = x$1;
        }
        
        public DelayableUOW uow() {
            return this.org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer();
        }
        
        public boolean isEmpty() {
            return this.messageRecord() == null && this.enqueues().isEmpty() && this.dequeues().isEmpty() && this.xaAcks().isEmpty();
        }
        
        public void cancel() {
            this.uow().rm(this.id());
        }
        
        public boolean syncNeeded() {
            return this.messageRecord() != null && this.messageRecord().syncNeeded();
        }
        
        public long size() {
            return ((this.messageRecord() == null) ? 0 : (this.messageRecord().data().length + 20)) + (this.enqueues().size() + this.dequeues().size()) * 50 + BoxesRunTime.unboxToLong(this.xaAcks().foldLeft((Object)BoxesRunTime.boxToLong(0L), (Function2)new DelayableUOW$MessageAction$$anonfun$size.DelayableUOW$MessageAction$$anonfun$size$2(this)));
        }
        
        public boolean addToPendingStore() {
            HashSet set = this.org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer().manager().pendingStores().get(this.id());
            if (set == null) {
                set = (HashSet)HashSet$.MODULE$.apply((Seq)Nil$.MODULE$);
                this.org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer().manager().pendingStores().put(this.id(), (HashSet<MessageAction>)set);
            }
            else {
                final BoxedUnit unit = BoxedUnit.UNIT;
            }
            return set.add((Object)this);
        }
        
        public Object removeFromPendingStore() {
            final HashSet set = this.org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer().manager().pendingStores().get(this.id());
            Object unit;
            if (set == null) {
                unit = BoxedUnit.UNIT;
            }
            else {
                set.remove((Object)this);
                unit = (set.isEmpty() ? this.org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer().manager().pendingStores().remove(this.id()) : BoxedUnit.UNIT);
            }
            return unit;
        }
        
        public /* synthetic */ DelayableUOW org$apache$activemq$leveldb$DelayableUOW$MessageAction$$$outer() {
            return DelayableUOW.this;
        }
        
        public MessageAction() {
            if (DelayableUOW.this == null) {
                throw null;
            }
            this.messageRecord = null;
            this.enqueues = (ListBuffer<QueueEntryRecord>)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
            this.dequeues = (ListBuffer<QueueEntryRecord>)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
            this.xaAcks = (ListBuffer<XaAckRecord>)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
        }
    }
}
