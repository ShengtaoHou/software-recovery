// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.LevelDBStore$;
import scala.runtime.LongRef;
import scala.runtime.BoxesRunTime;
import org.apache.activemq.broker.LockableServiceSupport;
import org.apache.activemq.leveldb.LevelDBStore;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.broker.jmx.BrokerMBeanSupport;
import scala.collection.mutable.StringBuilder;
import javax.management.ObjectName;
import org.fusesource.hawtdispatch.package$;
import scala.runtime.BoxedUnit;
import scala.Function1;
import org.apache.activemq.broker.Locker;
import scala.collection.immutable.StringOps;
import scala.Predef$;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import scala.collection.Seq;
import scala.Function0;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.leveldb.replicated.groups.ZooKeeperGroup;
import org.apache.activemq.leveldb.replicated.groups.ZKClient;
import java.io.File;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0015}v!B\u0001\u0003\u0011\u0003i\u0011\u0001F#mK\u000e$\u0018N\\4MKZ,G\u000e\u0012\"Ti>\u0014XM\u0003\u0002\u0004\t\u0005Q!/\u001a9mS\u000e\fG/\u001a3\u000b\u0005\u00151\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000f!\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xm\u0001\u0001\u0011\u00059yQ\"\u0001\u0002\u0007\u000bA\u0011\u0001\u0012A\t\u0003)\u0015cWm\u0019;j]\u001edUM^3m\t\n\u001bFo\u001c:f'\ry!\u0003\u0007\t\u0003'Yi\u0011\u0001\u0006\u0006\u0002+\u0005)1oY1mC&\u0011q\u0003\u0006\u0002\u0007\u0003:L(+\u001a4\u0011\u0005eaR\"\u0001\u000e\u000b\u0005m!\u0011\u0001B;uS2L!!\b\u000e\u0003\u00071{w\rC\u0003 \u001f\u0011\u0005\u0001%\u0001\u0004=S:LGO\u0010\u000b\u0002\u001b!)!e\u0004C\u0001G\u0005\u0001R.Y2iS:,w\f[8ti:\fW.Z\u000b\u0002IA\u0011Q\u0005\u000b\b\u0003'\u0019J!a\n\u000b\u0002\rA\u0013X\rZ3g\u0013\tI#F\u0001\u0004TiJLgn\u001a\u0006\u0003OQ1A\u0001\u0005\u0002\u0001YM\u00111&\f\t\u0003\u001d9J!a\f\u0002\u0003#A\u0013x\u000e_=MKZ,G\u000e\u0012\"Ti>\u0014X\rC\u0003 W\u0011\u0005\u0011\u0007F\u00013!\tq1\u0006C\u00035W\u0011\u0005Q'\u0001\u0007qe>D\u0018p\u0018;be\u001e,G/F\u00017!\tqq'\u0003\u00029\u0005\t\u0011R*Y:uKJdUM^3m\t\n\u001bFo\u001c:f\u0011\u001dQ4\u00061A\u0005\u0002m\n\u0011B_6BI\u0012\u0014Xm]:\u0016\u0003q\u0002\"!\u0010\"\u000e\u0003yR!a\u0010!\u0002\t1\fgn\u001a\u0006\u0002\u0003\u0006!!.\u0019<b\u0013\tIc\bC\u0004EW\u0001\u0007I\u0011A#\u0002\u001bi\\\u0017\t\u001a3sKN\u001cx\fJ3r)\t1\u0015\n\u0005\u0002\u0014\u000f&\u0011\u0001\n\u0006\u0002\u0005+:LG\u000fC\u0004K\u0007\u0006\u0005\t\u0019\u0001\u001f\u0002\u0007a$\u0013\u0007\u0003\u0004MW\u0001\u0006K\u0001P\u0001\u000bu.\fE\r\u001a:fgN\u0004\u0003FA&O!\ty%+D\u0001Q\u0015\t\tF#A\u0003cK\u0006t7/\u0003\u0002T!\na!)Z1o!J|\u0007/\u001a:us\")Qk\u000bC\u0001-\u0006aq-\u001a;[W\u0006#GM]3tgR\tA\bC\u0003YW\u0011\u0005\u0011,\u0001\u0007tKRT6.\u00113ee\u0016\u001c8\u000f\u0006\u0002G5\"9!jVA\u0001\u0002\u0004a\u0004\"\u0003/,\u0001\u0004\u0005\r\u0011\"\u0001$\u0003)Q8\u000eU1tg^|'\u000f\u001a\u0005\n=.\u0002\r\u00111A\u0005\u0002}\u000baB_6QCN\u001cxo\u001c:e?\u0012*\u0017\u000f\u0006\u0002GA\"9!*XA\u0001\u0002\u0004!\u0003B\u00022,A\u0003&A%A\u0006{WB\u000b7o]<pe\u0012\u0004\u0003FA1O\u0011\u0015)7\u0006\"\u0001g\u000359W\r\u001e.l!\u0006\u001c8o^8sIR\tA\u0005C\u0003iW\u0011\u0005\u0011.A\u0007tKRT6\u000eU1tg^|'\u000f\u001a\u000b\u0003\r*DqAS4\u0002\u0002\u0003\u0007A\u0005C\u0004mW\u0001\u0007I\u0011A\u001e\u0002\ri\\\u0007+\u0019;i\u0011\u001dq7\u00061A\u0005\u0002=\f!B_6QCRDw\fJ3r)\t1\u0005\u000fC\u0004K[\u0006\u0005\t\u0019\u0001\u001f\t\rI\\\u0003\u0015)\u0003=\u0003\u001dQ8\u000eU1uQ\u0002B#!\u001d(\t\u000bU\\C\u0011\u0001,\u0002\u0013\u001d,GOW6QCRD\u0007\"B<,\t\u0003A\u0018!C:fij[\u0007+\u0019;i)\t1\u0015\u0010C\u0004Km\u0006\u0005\t\u0019\u0001\u001f\t\u000fm\\\u0003\u0019!C\u0001w\u0005y!p[*fgNLwN\u001c+nK>,H\u000fC\u0004~W\u0001\u0007I\u0011\u0001@\u0002'i\\7+Z:tS>tG+\\3pkR|F%Z9\u0015\u0005\u0019{\bb\u0002&}\u0003\u0003\u0005\r\u0001\u0010\u0005\b\u0003\u0007Y\u0003\u0015)\u0003=\u0003AQ8nU3tg&|g\u000eV7f_V$\b\u0005K\u0002\u0002\u00029Ca!!\u0003,\t\u00031\u0016AE4fij[7+Z:tS>tG+\\3pkRDq!!\u0004,\t\u0003\ty!\u0001\ntKRT6nU3tg&|g\u000eV7f_V$Hc\u0001$\u0002\u0012!A!*a\u0003\u0002\u0002\u0003\u0007A\b\u0003\u0006\u0002\u0016-\u0002\r\u00111A\u0005\u0002\r\n!B\u0019:pW\u0016\u0014h*Y7f\u0011-\tIb\u000ba\u0001\u0002\u0004%\t!a\u0007\u0002\u001d\t\u0014xn[3s\u001d\u0006lWm\u0018\u0013fcR\u0019a)!\b\t\u0011)\u000b9\"!AA\u0002\u0011Bq!!\t,A\u0003&A%A\u0006ce>\\WM\u001d(b[\u0016\u0004\u0003BCA\u0013W\u0001\u0007\t\u0019!C\u0001G\u0005I1m\u001c8uC&tWM\u001d\u0005\f\u0003SY\u0003\u0019!a\u0001\n\u0003\tY#A\u0007d_:$\u0018-\u001b8fe~#S-\u001d\u000b\u0004\r\u00065\u0002\u0002\u0003&\u0002(\u0005\u0005\t\u0019\u0001\u0013\t\u000f\u0005E2\u0006)Q\u0005I\u0005Q1m\u001c8uC&tWM\u001d\u0011)\u0007\u0005=b\n\u0003\u0004\u00028-\"\tAZ\u0001\rO\u0016$8i\u001c8uC&tWM\u001d\u0005\b\u0003wYC\u0011AA\u001f\u00031\u0019X\r^\"p]R\f\u0017N\\3s)\r1\u0015q\b\u0005\t\u0015\u0006e\u0012\u0011!a\u0001I!Q\u00111I\u0016A\u0002\u0003\u0007I\u0011A\u0012\u0002\u0011!|7\u000f\u001e8b[\u0016D1\"a\u0012,\u0001\u0004\u0005\r\u0011\"\u0001\u0002J\u0005a\u0001n\\:u]\u0006lWm\u0018\u0013fcR\u0019a)a\u0013\t\u0011)\u000b)%!AA\u0002\u0011Bq!a\u0014,A\u0003&A%A\u0005i_N$h.Y7fA!\u001a\u0011Q\n(\t\r\u0005U3\u0006\"\u0001g\u0003-9W\r\u001e%pgRt\u0017-\\3\t\u000f\u0005e3\u0006\"\u0001\u0002\\\u0005Y1/\u001a;I_N$h.Y7f)\r1\u0015Q\f\u0005\t\u0015\u0006]\u0013\u0011!a\u0001I!A\u0011\u0011M\u0016A\u0002\u0013\u00051(\u0001\u0003cS:$\u0007\"CA3W\u0001\u0007I\u0011AA4\u0003!\u0011\u0017N\u001c3`I\u0015\fHc\u0001$\u0002j!A!*a\u0019\u0002\u0002\u0003\u0007A\bC\u0004\u0002n-\u0002\u000b\u0015\u0002\u001f\u0002\u000b\tLg\u000e\u001a\u0011)\u0007\u0005-d\n\u0003\u0004\u0002t-\"\tAV\u0001\bO\u0016$()\u001b8e\u0011\u001d\t9h\u000bC\u0001\u0003s\nqa]3u\u0005&tG\rF\u0002G\u0003wB\u0001BSA;\u0003\u0003\u0005\r\u0001\u0010\u0005\n\u0003\u007fZ\u0003\u0019!C\u0001\u0003\u0003\u000baa^3jO\"$XCAAB!\r\u0019\u0012QQ\u0005\u0004\u0003\u000f#\"aA%oi\"I\u00111R\u0016A\u0002\u0013\u0005\u0011QR\u0001\u000bo\u0016Lw\r\u001b;`I\u0015\fHc\u0001$\u0002\u0010\"I!*!#\u0002\u0002\u0003\u0007\u00111\u0011\u0005\t\u0003'[\u0003\u0015)\u0003\u0002\u0004\u00069q/Z5hQR\u0004\u0003fAAI\u001d\"9\u0011\u0011T\u0016\u0005\u0002\u0005m\u0015!C4fi^+\u0017n\u001a5u)\t\t\u0019\tC\u0004\u0002 .\"\t!!)\u0002\u0013M,GoV3jO\"$Hc\u0001$\u0002$\"I!*!(\u0002\u0002\u0003\u0007\u00111\u0011\u0005\n\u0003O[\u0003\u0019!C\u0001\u0003\u0003\u000b\u0001B]3qY&\u001c\u0017m\u001d\u0005\n\u0003W[\u0003\u0019!C\u0001\u0003[\u000bAB]3qY&\u001c\u0017m]0%KF$2ARAX\u0011%Q\u0015\u0011VA\u0001\u0002\u0004\t\u0019\t\u0003\u0005\u00024.\u0002\u000b\u0015BAB\u0003%\u0011X\r\u001d7jG\u0006\u001c\b\u0005K\u0002\u00022:Cq!!/,\t\u0003\tY*A\u0006hKR\u0014V\r\u001d7jG\u0006\u001c\bbBA_W\u0011\u0005\u0011qX\u0001\fg\u0016$(+\u001a9mS\u000e\f7\u000fF\u0002G\u0003\u0003D\u0011BSA^\u0003\u0003\u0005\r!a!\t\u0011\u0005\u00157\u00061A\u0005\u0002m\nAa]=oG\"I\u0011\u0011Z\u0016A\u0002\u0013\u0005\u00111Z\u0001\tgft7m\u0018\u0013fcR\u0019a)!4\t\u0011)\u000b9-!AA\u0002qBq!!5,A\u0003&A(A\u0003ts:\u001c\u0007\u0005K\u0002\u0002P:Ca!a6,\t\u00031\u0016aB4fiNKhn\u0019\u0005\b\u00037\\C\u0011AAo\u0003\u001d\u0019X\r^*z]\u000e$2ARAp\u0011!Q\u0015\u0011\\A\u0001\u0002\u0004a\u0004bBArW\u0011\u0005\u0011\u0011Q\u0001\u0012G2,8\u000f^3s'&TX-U;peVl\u0007\u0002CAtW\u0001\u0007I\u0011A\u001e\u0002\u001bM,7-\u001e:jif$vn[3o\u0011%\tYo\u000ba\u0001\n\u0003\ti/A\ttK\u000e,(/\u001b;z)>\\WM\\0%KF$2ARAx\u0011!Q\u0015\u0011^A\u0001\u0002\u0004a\u0004bBAzW\u0001\u0006K\u0001P\u0001\u000fg\u0016\u001cWO]5usR{7.\u001a8!Q\r\t\tP\u0014\u0005\u0007\u0003s\\C\u0011\u0001,\u0002!\u001d,GoU3dkJLG/\u001f+pW\u0016t\u0007bBA\u007fW\u0011\u0005\u0011q`\u0001\u0011g\u0016$8+Z2ve&$\u0018\u0010V8lK:$2A\u0012B\u0001\u0011!Q\u00151`A\u0001\u0002\u0004a\u0004\"\u0003B\u0003W\u0001\u0007I\u0011\u0001B\u0004\u0003%!\u0017N]3di>\u0014\u00180\u0006\u0002\u0003\nA!!1\u0002B\t\u001b\t\u0011iAC\u0002\u0003\u0010\u0001\u000b!![8\n\t\tM!Q\u0002\u0002\u0005\r&dW\rC\u0005\u0003\u0018-\u0002\r\u0011\"\u0001\u0003\u001a\u0005iA-\u001b:fGR|'/_0%KF$2A\u0012B\u000e\u0011%Q%QCA\u0001\u0002\u0004\u0011I\u0001\u0003\u0005\u0003 -\u0002\u000b\u0015\u0002B\u0005\u0003)!\u0017N]3di>\u0014\u0018\u0010\t\u0005\b\u0005GYC\u0011\tB\u0013\u00031\u0019X\r\u001e#je\u0016\u001cGo\u001c:z)\r1%q\u0005\u0005\t\u0005S\u0011\t\u00031\u0001\u0003\n\u0005\u0019A-\u001b:\t\u000f\t52\u0006\"\u0011\u00030\u0005aq-\u001a;ESJ,7\r^8ssR\u0011!\u0011\u0002\u0005\n\u0005gY\u0003\u0019!C\u0001\u0005k\tq\u0001\\8h'&TX-\u0006\u0002\u00038A\u00191C!\u000f\n\u0007\tmBC\u0001\u0003M_:<\u0007\"\u0003B W\u0001\u0007I\u0011\u0001B!\u0003-awnZ*ju\u0016|F%Z9\u0015\u0007\u0019\u0013\u0019\u0005C\u0005K\u0005{\t\t\u00111\u0001\u00038!A!qI\u0016!B\u0013\u00119$\u0001\u0005m_\u001e\u001c\u0016N_3!Q\r\u0011)E\u0014\u0005\b\u0005\u001bZC\u0011\u0001B(\u0003)9W\r\u001e'pONK'0\u001a\u000b\u0003\u0005oAqAa\u0015,\t\u0003\u0011)&\u0001\u0006tKRdunZ*ju\u0016$2A\u0012B,\u0011%Q%\u0011KA\u0001\u0002\u0004\u00119\u0004\u0003\u0005\u0003\\-\u0002\r\u0011\"\u0001$\u00031Ig\u000eZ3y\r\u0006\u001cGo\u001c:z\u0011%\u0011yf\u000ba\u0001\n\u0003\u0011\t'\u0001\tj]\u0012,\u0007PR1di>\u0014\u0018p\u0018\u0013fcR\u0019aIa\u0019\t\u0011)\u0013i&!AA\u0002\u0011BqAa\u001a,A\u0003&A%A\u0007j]\u0012,\u0007PR1di>\u0014\u0018\u0010\t\u0015\u0004\u0005Kr\u0005B\u0002B7W\u0011\u0005a-A\bhKRLe\u000eZ3y\r\u0006\u001cGo\u001c:z\u0011\u001d\u0011\th\u000bC\u0001\u0005g\nqb]3u\u0013:$W\r\u001f$bGR|'/\u001f\u000b\u0004\r\nU\u0004\u0002\u0003&\u0003p\u0005\u0005\t\u0019\u0001\u0013\t\u0013\te4\u00061A\u0005\u0002\tm\u0014a\u0004<fe&4\u0017p\u00115fG.\u001cX/\\:\u0016\u0005\tu\u0004cA\n\u0003\u0000%\u0019!\u0011\u0011\u000b\u0003\u000f\t{w\u000e\\3b]\"I!QQ\u0016A\u0002\u0013\u0005!qQ\u0001\u0014m\u0016\u0014\u0018NZ=DQ\u0016\u001c7n];ng~#S-\u001d\u000b\u0004\r\n%\u0005\"\u0003&\u0003\u0004\u0006\u0005\t\u0019\u0001B?\u0011!\u0011ii\u000bQ!\n\tu\u0014\u0001\u0005<fe&4\u0017p\u00115fG.\u001cX/\\:!Q\r\u0011YI\u0014\u0005\b\u0005'[C\u0011\u0001BK\u0003I9W\r\u001e,fe&4\u0017p\u00115fG.\u001cX/\\:\u0015\u0005\tu\u0004b\u0002BMW\u0011\u0005!1T\u0001\u0013g\u0016$h+\u001a:jMf\u001c\u0005.Z2lgVl7\u000fF\u0002G\u0005;C\u0011B\u0013BL\u0003\u0003\u0005\rA! \t\u0013\t\u00056\u00061A\u0005\u0002\u0005\u0005\u0015!E5oI\u0016DX*\u0019=Pa\u0016tg)\u001b7fg\"I!QU\u0016A\u0002\u0013\u0005!qU\u0001\u0016S:$W\r_'bq>\u0003XM\u001c$jY\u0016\u001cx\fJ3r)\r1%\u0011\u0016\u0005\n\u0015\n\r\u0016\u0011!a\u0001\u0003\u0007C\u0001B!,,A\u0003&\u00111Q\u0001\u0013S:$W\r_'bq>\u0003XM\u001c$jY\u0016\u001c\b\u0005K\u0002\u0003,:CqAa-,\t\u0003\tY*\u0001\u000bhKRLe\u000eZ3y\u001b\u0006Dx\n]3o\r&dWm\u001d\u0005\b\u0005o[C\u0011\u0001B]\u0003Q\u0019X\r^%oI\u0016DX*\u0019=Pa\u0016tg)\u001b7fgR\u0019aIa/\t\u0013)\u0013),!AA\u0002\u0005\r\u0005\"\u0003B`W\u0001\u0007I\u0011AAA\u0003eIg\u000eZ3y\u00052|7m\u001b*fgR\f'\u000f^%oi\u0016\u0014h/\u00197\t\u0013\t\r7\u00061A\u0005\u0002\t\u0015\u0017!H5oI\u0016D(\t\\8dWJ+7\u000f^1si&sG/\u001a:wC2|F%Z9\u0015\u0007\u0019\u00139\rC\u0005K\u0005\u0003\f\t\u00111\u0001\u0002\u0004\"A!1Z\u0016!B\u0013\t\u0019)\u0001\u000ej]\u0012,\u0007P\u00117pG.\u0014Vm\u001d;beRLe\u000e^3sm\u0006d\u0007\u0005K\u0002\u0003J:CqA!5,\t\u0003\tY*\u0001\u000fhKRLe\u000eZ3y\u00052|7m\u001b*fgR\f'\u000f^%oi\u0016\u0014h/\u00197\t\u000f\tU7\u0006\"\u0001\u0003X\u0006a2/\u001a;J]\u0012,\u0007P\u00117pG.\u0014Vm\u001d;beRLe\u000e^3sm\u0006dGc\u0001$\u0003Z\"I!Ja5\u0002\u0002\u0003\u0007\u00111\u0011\u0005\n\u0005;\\\u0003\u0019!C\u0001\u0005w\na\u0002]1sC:|\u0017\u000eZ\"iK\u000e\\7\u000fC\u0005\u0003b.\u0002\r\u0011\"\u0001\u0003d\u0006\u0011\u0002/\u0019:b]>LGm\u00115fG.\u001cx\fJ3r)\r1%Q\u001d\u0005\n\u0015\n}\u0017\u0011!a\u0001\u0005{B\u0001B!;,A\u0003&!QP\u0001\u0010a\u0006\u0014\u0018M\\8jI\u000eCWmY6tA!\u001a!q\u001d(\t\u000f\t=8\u0006\"\u0001\u0003\u0016\u0006\tr-\u001a;QCJ\fgn\\5e\u0007\",7m[:\t\u000f\tM8\u0006\"\u0001\u0003v\u0006\t2/\u001a;QCJ\fgn\\5e\u0007\",7m[:\u0015\u0007\u0019\u00139\u0010C\u0005K\u0005c\f\t\u00111\u0001\u0003~!I!1`\u0016A\u0002\u0013\u0005\u0011\u0011Q\u0001\u0015S:$W\r_,sSR,')\u001e4gKJ\u001c\u0016N_3\t\u0013\t}8\u00061A\u0005\u0002\r\u0005\u0011\u0001G5oI\u0016DxK]5uK\n+hMZ3s'&TXm\u0018\u0013fcR\u0019aia\u0001\t\u0013)\u0013i0!AA\u0002\u0005\r\u0005\u0002CB\u0004W\u0001\u0006K!a!\u0002+%tG-\u001a=Xe&$XMQ;gM\u0016\u00148+\u001b>fA!\u001a1Q\u0001(\t\u000f\r51\u0006\"\u0001\u0002\u001c\u00069r-\u001a;J]\u0012,\u0007p\u0016:ji\u0016\u0014UO\u001a4feNK'0\u001a\u0005\b\u0007#YC\u0011AB\n\u0003]\u0019X\r^%oI\u0016DxK]5uK\n+hMZ3s'&TX\rF\u0002G\u0007+A\u0011BSB\b\u0003\u0003\u0005\r!a!\t\u0013\re1\u00061A\u0005\u0002\u0005\u0005\u0015AD5oI\u0016D(\t\\8dWNK'0\u001a\u0005\n\u0007;Y\u0003\u0019!C\u0001\u0007?\t!#\u001b8eKb\u0014En\\2l'&TXm\u0018\u0013fcR\u0019ai!\t\t\u0013)\u001bY\"!AA\u0002\u0005\r\u0005\u0002CB\u0013W\u0001\u0006K!a!\u0002\u001f%tG-\u001a=CY>\u001c7nU5{K\u0002B3aa\tO\u0011\u001d\u0019Yc\u000bC\u0001\u00037\u000b\u0011cZ3u\u0013:$W\r\u001f\"m_\u000e\\7+\u001b>f\u0011\u001d\u0019yc\u000bC\u0001\u0007c\t\u0011c]3u\u0013:$W\r\u001f\"m_\u000e\\7+\u001b>f)\r151\u0007\u0005\n\u0015\u000e5\u0012\u0011!a\u0001\u0003\u0007C\u0001ba\u000e,\u0001\u0004%\taI\u0001\u0011S:$W\r_\"p[B\u0014Xm]:j_:D\u0011ba\u000f,\u0001\u0004%\ta!\u0010\u0002)%tG-\u001a=D_6\u0004(/Z:tS>tw\fJ3r)\r15q\b\u0005\t\u0015\u000ee\u0012\u0011!a\u0001I!911I\u0016!B\u0013!\u0013!E5oI\u0016D8i\\7qe\u0016\u001c8/[8oA!\u001a1\u0011\t(\t\r\r%3\u0006\"\u0001g\u0003M9W\r^%oI\u0016D8i\\7qe\u0016\u001c8/[8o\u0011\u001d\u0019ie\u000bC\u0001\u0007\u001f\n1c]3u\u0013:$W\r_\"p[B\u0014Xm]:j_:$2ARB)\u0011!Q51JA\u0001\u0002\u0004!\u0003\u0002CB+W\u0001\u0007I\u0011A\u0012\u0002\u001d1|wmQ8naJ,7o]5p]\"I1\u0011L\u0016A\u0002\u0013\u000511L\u0001\u0013Y><7i\\7qe\u0016\u001c8/[8o?\u0012*\u0017\u000fF\u0002G\u0007;B\u0001BSB,\u0003\u0003\u0005\r\u0001\n\u0005\b\u0007CZ\u0003\u0015)\u0003%\u0003=awnZ\"p[B\u0014Xm]:j_:\u0004\u0003fAB0\u001d\"11qM\u0016\u0005\u0002\u0019\f\u0011cZ3u\u0019><7i\\7qe\u0016\u001c8/[8o\u0011\u001d\u0019Yg\u000bC\u0001\u0007[\n\u0011c]3u\u0019><7i\\7qe\u0016\u001c8/[8o)\r15q\u000e\u0005\t\u0015\u000e%\u0014\u0011!a\u0001I!I11O\u0016A\u0002\u0013\u0005!QG\u0001\u000fS:$W\r_\"bG\",7+\u001b>f\u0011%\u00199h\u000ba\u0001\n\u0003\u0019I(\u0001\nj]\u0012,\u0007pQ1dQ\u0016\u001c\u0016N_3`I\u0015\fHc\u0001$\u0004|!I!j!\u001e\u0002\u0002\u0003\u0007!q\u0007\u0005\t\u0007\u007fZ\u0003\u0015)\u0003\u00038\u0005y\u0011N\u001c3fq\u000e\u000b7\r[3TSj,\u0007\u0005K\u0002\u0004~9Cqa!\",\t\u0003\u0011y%A\thKRLe\u000eZ3y\u0007\u0006\u001c\u0007.Z*ju\u0016Dqa!#,\t\u0003\u0019Y)A\ttKRLe\u000eZ3y\u0007\u0006\u001c\u0007.Z*ju\u0016$2ARBG\u0011%Q5qQA\u0001\u0002\u0004\u00119\u0004C\u0005\u0004\u0012.\u0002\r\u0011\"\u0001\u0002\u0002\u0006Qa\r\\;tQ\u0012+G.Y=\t\u0013\rU5\u00061A\u0005\u0002\r]\u0015A\u00044mkNDG)\u001a7bs~#S-\u001d\u000b\u0004\r\u000ee\u0005\"\u0003&\u0004\u0014\u0006\u0005\t\u0019AAB\u0011!\u0019ij\u000bQ!\n\u0005\r\u0015a\u00034mkNDG)\u001a7bs\u0002B3aa'O\u0011\u001d\u0019\u0019k\u000bC\u0001\u00037\u000bQbZ3u\r2,8\u000f\u001b#fY\u0006L\bbBBTW\u0011\u00051\u0011V\u0001\u000eg\u0016$h\t\\;tQ\u0012+G.Y=\u0015\u0007\u0019\u001bY\u000bC\u0005K\u0007K\u000b\t\u00111\u0001\u0002\u0004\"I1qV\u0016A\u0002\u0013\u0005\u0011\u0011Q\u0001\u0010CNLhn\u0019\"vM\u001a,'oU5{K\"I11W\u0016A\u0002\u0013\u00051QW\u0001\u0014CNLhn\u0019\"vM\u001a,'oU5{K~#S-\u001d\u000b\u0004\r\u000e]\u0006\"\u0003&\u00042\u0006\u0005\t\u0019AAB\u0011!\u0019Yl\u000bQ!\n\u0005\r\u0015\u0001E1ts:\u001c')\u001e4gKJ\u001c\u0016N_3!Q\r\u0019IL\u0014\u0005\b\u0007\u0003\\C\u0011AAN\u0003I9W\r^!ts:\u001c')\u001e4gKJ\u001c\u0016N_3\t\u000f\r\u00157\u0006\"\u0001\u0004H\u0006\u00112/\u001a;Bgft7MQ;gM\u0016\u00148+\u001b>f)\r15\u0011\u001a\u0005\n\u0015\u000e\r\u0017\u0011!a\u0001\u0003\u0007C\u0011b!4,\u0001\u0004%\tAa\u001f\u0002\u00195|g.\u001b;peN#\u0018\r^:\t\u0013\rE7\u00061A\u0005\u0002\rM\u0017\u0001E7p]&$xN]*uCR\u001cx\fJ3r)\r15Q\u001b\u0005\n\u0015\u000e=\u0017\u0011!a\u0001\u0005{B\u0001b!7,A\u0003&!QP\u0001\u000e[>t\u0017\u000e^8s'R\fGo\u001d\u0011)\u0007\r]g\nC\u0004\u0004`.\"\tA!&\u0002\u001f\u001d,G/T8oSR|'o\u0015;biNDqaa9,\t\u0003\u0019)/A\btKRluN\\5u_J\u001cF/\u0019;t)\r15q\u001d\u0005\n\u0015\u000e\u0005\u0018\u0011!a\u0001\u0005{B\u0011ba;,\u0001\u0004%\t!!!\u00027\u0019\f\u0017\u000e\\8wKJ\u0004&o\u001c3vG\u0016\u00148/Q;eSR$U\r\u001d;i\u0011%\u0019yo\u000ba\u0001\n\u0003\u0019\t0A\u0010gC&dwN^3s!J|G-^2feN\fU\u000fZ5u\t\u0016\u0004H\u000f[0%KF$2ARBz\u0011%Q5Q^A\u0001\u0002\u0004\t\u0019\t\u0003\u0005\u0004x.\u0002\u000b\u0015BAB\u0003q1\u0017-\u001b7pm\u0016\u0014\bK]8ek\u000e,'o]!vI&$H)\u001a9uQ\u0002B3a!>O\u0011\u001d\u0019ip\u000bC\u0001\u00037\u000badZ3u\r\u0006LGn\u001c<feB\u0013x\u000eZ;dKJ\u001c\u0018)\u001e3ji\u0012+\u0007\u000f\u001e5\t\u000f\u0011\u00051\u0006\"\u0001\u0005\u0004\u0005q2/\u001a;GC&dwN^3s!J|G-^2feN\fU\u000fZ5u\t\u0016\u0004H\u000f\u001b\u000b\u0004\r\u0012\u0015\u0001\"\u0003&\u0004\u0000\u0006\u0005\t\u0019AAB\u0011%!Ia\u000ba\u0001\n\u0003\t\t)A\u000enCb4\u0015-\u001b7pm\u0016\u0014\bK]8ek\u000e,'o\u001d+p)J\f7m\u001b\u0005\n\t\u001bY\u0003\u0019!C\u0001\t\u001f\tq$\\1y\r\u0006LGn\u001c<feB\u0013x\u000eZ;dKJ\u001cHk\u001c+sC\u000e\\w\fJ3r)\r1E\u0011\u0003\u0005\n\u0015\u0012-\u0011\u0011!a\u0001\u0003\u0007C\u0001\u0002\"\u0006,A\u0003&\u00111Q\u0001\u001d[\u0006Dh)Y5m_Z,'\u000f\u0015:pIV\u001cWM]:U_R\u0013\u0018mY6!Q\r!\u0019B\u0014\u0005\b\t7YC\u0011AAN\u0003y9W\r^'bq\u001a\u000b\u0017\u000e\\8wKJ\u0004&o\u001c3vG\u0016\u00148\u000fV8Ue\u0006\u001c7\u000eC\u0004\u0005 -\"\t\u0001\"\t\u0002=M,G/T1y\r\u0006LGn\u001c<feB\u0013x\u000eZ;dKJ\u001cHk\u001c+sC\u000e\\Gc\u0001$\u0005$!I!\n\"\b\u0002\u0002\u0003\u0007\u00111\u0011\u0005\u000b\tOY\u0003\u0019!a\u0001\n\u0003)\u0014AB7bgR,'\u000fC\u0006\u0005,-\u0002\r\u00111A\u0005\u0002\u00115\u0012AC7bgR,'o\u0018\u0013fcR\u0019a\tb\f\t\u0011)#I#!AA\u0002YBq\u0001b\r,A\u0003&a'A\u0004nCN$XM\u001d\u0011\t\u0017\u0011]2\u00061AA\u0002\u0013\u0005A\u0011H\u0001\u0006g2\fg/Z\u000b\u0003\tw\u00012A\u0004C\u001f\u0013\r!yD\u0001\u0002\u0012'2\fg/\u001a'fm\u0016dGIQ*u_J,\u0007b\u0003C\"W\u0001\u0007\t\u0019!C\u0001\t\u000b\n\u0011b\u001d7bm\u0016|F%Z9\u0015\u0007\u0019#9\u0005C\u0005K\t\u0003\n\t\u00111\u0001\u0005<!AA1J\u0016!B\u0013!Y$\u0001\u0004tY\u00064X\r\t\u0005\f\t\u001fZ\u0003\u0019!a\u0001\n\u0003!\t&A\u0005{W~\u001bG.[3oiV\u0011A1\u000b\t\u0005\t+\"Y&\u0004\u0002\u0005X)\u0019A\u0011\f\u0002\u0002\r\u001d\u0014x.\u001e9t\u0013\u0011!i\u0006b\u0016\u0003\u0011i[5\t\\5f]RD1\u0002\"\u0019,\u0001\u0004\u0005\r\u0011\"\u0001\u0005d\u0005i!p[0dY&,g\u000e^0%KF$2A\u0012C3\u0011%QEqLA\u0001\u0002\u0004!\u0019\u0006\u0003\u0005\u0005j-\u0002\u000b\u0015\u0002C*\u0003)Q8nX2mS\u0016tG\u000f\t\u0005\f\t[Z\u0003\u0019!a\u0001\n\u0003!y'\u0001\u0005{W~;'o\\;q+\t!\t\b\u0005\u0003\u0005V\u0011M\u0014\u0002\u0002C;\t/\u0012aBW8p\u0017\u0016,\u0007/\u001a:He>,\b\u000fC\u0006\u0005z-\u0002\r\u00111A\u0005\u0002\u0011m\u0014\u0001\u0004>l?\u001e\u0014x.\u001e9`I\u0015\fHc\u0001$\u0005~!I!\nb\u001e\u0002\u0002\u0003\u0007A\u0011\u000f\u0005\t\t\u0003[\u0003\u0015)\u0003\u0005r\u0005I!p[0he>,\b\u000f\t\u0005\n\t\u000b[\u0003\u0019!C\u0001\u0005k\t\u0001\u0002]8tSRLwN\u001c\u0005\n\t\u0013[\u0003\u0019!C\u0001\t\u0017\u000bA\u0002]8tSRLwN\\0%KF$2A\u0012CG\u0011%QEqQA\u0001\u0002\u0004\u00119\u0004\u0003\u0005\u0005\u0012.\u0002\u000b\u0015\u0002B\u001c\u0003%\u0001xn]5uS>t\u0007\u0005\u0003\u0004\u0005\u0016.\"\tEZ\u0001\ti>\u001cFO]5oO\"YA\u0011T\u0016A\u0002\u0003\u0007I\u0011\u0001CN\u00031)8/Y4f\u001b\u0006t\u0017mZ3s+\t!i\n\u0005\u0003\u0005 \u0012\u0015VB\u0001CQ\u0015\r!\u0019KB\u0001\u0006kN\fw-Z\u0005\u0005\tO#\tKA\u0006TsN$X-\\+tC\u001e,\u0007b\u0003CVW\u0001\u0007\t\u0019!C\u0001\t[\u000b\u0001#^:bO\u0016l\u0015M\\1hKJ|F%Z9\u0015\u0007\u0019#y\u000bC\u0005K\tS\u000b\t\u00111\u0001\u0005\u001e\"AA1W\u0016!B\u0013!i*A\u0007vg\u0006<W-T1oC\u001e,'\u000f\t\u0005\b\to[C\u0011\tC]\u0003=\u0019X\r^+tC\u001e,W*\u00198bO\u0016\u0014Hc\u0001$\u0005<\"AA\u0011\u0014C[\u0001\u0004!i\n\u0003\u0004\u0005@.\"\taI\u0001\b]>$WmX5e\u0011\u001d!\u0019m\u000bC\u0001\t\u000b\fA!\u001b8jiR\ta\tC\u0004\u0005J.\"\t\u0001b3\u0002'\r\u0014X-\u0019;f\t\u00164\u0017-\u001e7u\u0019>\u001c7.\u001a:\u0015\u0005\u00115\u0007\u0003\u0002Ch\t+l!\u0001\"5\u000b\u0007\u0011Mg!\u0001\u0004ce>\\WM]\u0005\u0005\t/$\tN\u0001\u0004M_\u000e\\WM\u001d\u0005\n\t7\\#\u0019!C\u0001\t;\fA#\\1ti\u0016\u0014xl\u001d;beR,Gm\u00187bi\u000eDWC\u0001Cp!\u0011!\t\u000f\";\u000e\u0005\u0011\r(\u0002\u0002Cs\tO\f!bY8oGV\u0014(/\u001a8u\u0015\tY\u0002)\u0003\u0003\u0005l\u0012\r(AD\"pk:$Hi\\<o\u0019\u0006$8\r\u001b\u0005\t\t_\\\u0003\u0015!\u0003\u0005`\u0006)R.Y:uKJ|6\u000f^1si\u0016$w\f\\1uG\"\u0004\u0003\"\u0003CzW\t\u0007I\u0011\u0001C{\u00039i\u0017m\u001d;fe~\u001bH/\u0019:uK\u0012,\"\u0001b>\u0011\t\u0011eHq`\u0007\u0003\twTA\u0001\"@\u0005d\u00061\u0011\r^8nS\u000eLA!\"\u0001\u0005|\ni\u0011\t^8nS\u000e\u0014un\u001c7fC:D\u0001\"\"\u0002,A\u0003%Aq_\u0001\u0010[\u0006\u001cH/\u001a:`gR\f'\u000f^3eA!9Q\u0011B\u0016\u0005\u0002\u0015-\u0011\u0001D:uCJ$x,\\1ti\u0016\u0014Hc\u0001$\u0006\u000e!AQqBC\u0004\u0001\u0004)\t\"\u0001\u0003gk:\u001c\u0007CB\n\u0006\u0014\u0005\re)C\u0002\u0006\u0016Q\u0011\u0011BR;oGRLwN\\\u0019\t\u000f\u0015e1\u0006\"\u0001\u0003|\u0005A\u0011n]'bgR,'\u000fC\u0005\u0006\u001e-\u0012\r\u0011\"\u0001\u0005^\u0006i1\u000f^8qa\u0016$w\f\\1uG\"D\u0001\"\"\t,A\u0003%Aq\\\u0001\u000fgR|\u0007\u000f]3e?2\fGo\u00195!\u0011%))c\u000bb\u0001\n\u0003!)0\u0001\bnCN$XM]0ti>\u0004\b/\u001a3\t\u0011\u0015%2\u0006)A\u0005\to\fq\"\\1ti\u0016\u0014xl\u001d;paB,G\r\t\u0005\b\u000b[YC\u0011AC\u0018\u0003-\u0019Ho\u001c9`[\u0006\u001cH/\u001a:\u0015\u0007\u0019+\t\u0004C\u0005\u0006\u0010\u0015-B\u00111\u0001\u00064A!1#\"\u000eG\u0013\r)9\u0004\u0006\u0002\ty\tLh.Y7f}!9Q1H\u0016\u0005\u0002\u0015u\u0012AC8cU\u0016\u001cGOT1nKV\u0011Qq\b\t\u0005\u000b\u0003*Y%\u0004\u0002\u0006D)!QQIC$\u0003)i\u0017M\\1hK6,g\u000e\u001e\u0006\u0003\u000b\u0013\nQA[1wCbLA!\"\u0014\u0006D\tQqJ\u00196fGRt\u0015-\\3\t\u000f\u0015E3\u0006\"\u0005\u0005F\u00069Am\\*uCJ$\bbBC+W\u0011EQqK\u0001\u0007I>\u001cFo\u001c9\u0015\u0007\u0019+I\u0006\u0003\u0005\u0006\\\u0015M\u0003\u0019AC/\u0003\u001d\u0019Ho\u001c9qKJ\u0004B!b\u0018\u0006d5\u0011Q\u0011\r\u0006\u00037\u0019IA!\"\u001a\u0006b\tq1+\u001a:wS\u000e,7\u000b^8qa\u0016\u0014\bbBC5W\u0011\u0005Q1N\u0001\fgR\f'\u000f^0tY\u00064X\r\u0006\u0003\u0006n\u0015EDc\u0001$\u0006p!IQqBC4\t\u0003\u0007Q1\u0007\u0005\b\u000bg*9\u00071\u0001%\u0003\u001d\tG\r\u001a:fgNDq!b\u001e,\t\u0003)I(\u0001\u0006ti>\u0004xl\u001d7bm\u0016$2ARC>\u0011%)y!\"\u001e\u0005\u0002\u0004)\u0019\u0004C\u0004\u0006\u0000-\"\t!\"!\u0002\u0019\r\u0014X-\u0019;f?Nd\u0017M^3\u0015\u0005\u0011m\u0002bBCCW\u0011\u0005QqQ\u0001\u000eGJ,\u0017\r^3`[\u0006\u001cH/\u001a:\u0015\u0003YBq!b#,\t\u0003*i)A\u0007tKR\u0014%o\\6fe:\u000bW.\u001a\u000b\u0004\r\u0016=\u0005bBA\u000b\u000b\u0013\u0003\r\u0001\n\u0005\b\u000b'[C\u0011\tCc\u0003E!W\r\\3uK\u0006cG.T3tg\u0006<Wm\u001d\u0005\b\u000b/[C\u0011ACM\u0003%\u0019wN\u001c4jOV\u0014X\rF\u0002G\u000b7C\u0001\"\"(\u0006\u0016\u0002\u0007QqT\u0001\u0006gR|'/\u001a\t\u0004\u001d\u0015\u0005\u0016bACR\u0005\tY\"+\u001a9mS\u000e\fG/\u001a3MKZ,G\u000e\u0012\"Ti>\u0014X\r\u0016:bSRDq!b\u001d,\t\u0003)9\u000bF\u0002=\u000bSC\u0001\"b+\u0006&\u0002\u0007\u00111Q\u0001\u0005a>\u0014H\u000fC\u0004\u00060.\"\tEa\u0014\u0002\tML'0\u001a\u0005\r\u000bg[\u0003\u0013!A\u0001\u0002\u0013\u0005QQW\u0001\u0018aJ|G/Z2uK\u0012$#M]8lKJ\u001cVM\u001d<jG\u0016$B!b.\u0006>B!AqZC]\u0013\u0011)Y\f\"5\u0003\u001b\t\u0013xn[3s'\u0016\u0014h/[2f\u0011!QU\u0011WA\u0001\u0002\u0004\u0011\u0004")
public class ElectingLevelDBStore extends ProxyLevelDBStore
{
    private String zkAddress;
    private String zkPassword;
    private String zkPath;
    private String zkSessionTmeout;
    private String brokerName;
    private String container;
    private String hostname;
    private String bind;
    private int weight;
    private int replicas;
    private String sync;
    private String securityToken;
    private File directory;
    private long logSize;
    private String indexFactory;
    private boolean verifyChecksums;
    private int indexMaxOpenFiles;
    private int indexBlockRestartInterval;
    private boolean paranoidChecks;
    private int indexWriteBufferSize;
    private int indexBlockSize;
    private String indexCompression;
    private String logCompression;
    private long indexCacheSize;
    private int flushDelay;
    private int asyncBufferSize;
    private boolean monitorStats;
    private int failoverProducersAuditDepth;
    private int maxFailoverProducersToTrack;
    private MasterLevelDBStore master;
    private SlaveLevelDBStore slave;
    private ZKClient zk_client;
    private ZooKeeperGroup zk_group;
    private long position;
    private SystemUsage usageManager;
    private final CountDownLatch master_started_latch;
    private final AtomicBoolean master_started;
    private final CountDownLatch stopped_latch;
    private final AtomicBoolean master_stopped;
    
    public static void trace(final Throwable e) {
        ElectingLevelDBStore$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        ElectingLevelDBStore$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        ElectingLevelDBStore$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        ElectingLevelDBStore$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        ElectingLevelDBStore$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        ElectingLevelDBStore$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        ElectingLevelDBStore$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static Logger log() {
        return ElectingLevelDBStore$.MODULE$.log();
    }
    
    public static String machine_hostname() {
        return ElectingLevelDBStore$.MODULE$.machine_hostname();
    }
    
    @Override
    public MasterLevelDBStore proxy_target() {
        return this.master();
    }
    
    public String zkAddress() {
        return this.zkAddress;
    }
    
    public void zkAddress_$eq(final String x$1) {
        this.zkAddress = x$1;
    }
    
    public void setZkAddress(final String x$1) {
        this.zkAddress = x$1;
    }
    
    public String zkPassword() {
        return this.zkPassword;
    }
    
    public void zkPassword_$eq(final String x$1) {
        this.zkPassword = x$1;
    }
    
    public void setZkPassword(final String x$1) {
        this.zkPassword = x$1;
    }
    
    public String zkPath() {
        return this.zkPath;
    }
    
    public void zkPath_$eq(final String x$1) {
        this.zkPath = x$1;
    }
    
    public void setZkPath(final String x$1) {
        this.zkPath = x$1;
    }
    
    public String zkSessionTmeout() {
        return this.zkSessionTmeout;
    }
    
    public void zkSessionTmeout_$eq(final String x$1) {
        this.zkSessionTmeout = x$1;
    }
    
    public void setZkSessionTmeout(final String x$1) {
        this.zkSessionTmeout = x$1;
    }
    
    public String brokerName() {
        return this.brokerName;
    }
    
    public void brokerName_$eq(final String x$1) {
        this.brokerName = x$1;
    }
    
    public String container() {
        return this.container;
    }
    
    public void container_$eq(final String x$1) {
        this.container = x$1;
    }
    
    public void setContainer(final String x$1) {
        this.container = x$1;
    }
    
    public String hostname() {
        return this.hostname;
    }
    
    public void hostname_$eq(final String x$1) {
        this.hostname = x$1;
    }
    
    public void setHostname(final String x$1) {
        this.hostname = x$1;
    }
    
    public String bind() {
        return this.bind;
    }
    
    public void bind_$eq(final String x$1) {
        this.bind = x$1;
    }
    
    public void setBind(final String x$1) {
        this.bind = x$1;
    }
    
    public int weight() {
        return this.weight;
    }
    
    public void weight_$eq(final int x$1) {
        this.weight = x$1;
    }
    
    public void setWeight(final int x$1) {
        this.weight = x$1;
    }
    
    public int replicas() {
        return this.replicas;
    }
    
    public void replicas_$eq(final int x$1) {
        this.replicas = x$1;
    }
    
    public void setReplicas(final int x$1) {
        this.replicas = x$1;
    }
    
    public String sync() {
        return this.sync;
    }
    
    public void sync_$eq(final String x$1) {
        this.sync = x$1;
    }
    
    public void setSync(final String x$1) {
        this.sync = x$1;
    }
    
    public int clusterSizeQuorum() {
        return this.replicas() / 2 + 1;
    }
    
    public String securityToken() {
        return this.securityToken;
    }
    
    public void securityToken_$eq(final String x$1) {
        this.securityToken = x$1;
    }
    
    public void setSecurityToken(final String x$1) {
        this.securityToken = x$1;
    }
    
    public File directory() {
        return this.directory;
    }
    
    public void directory_$eq(final File x$1) {
        this.directory = x$1;
    }
    
    @Override
    public void setDirectory(final File dir) {
        this.directory_$eq(dir);
    }
    
    @Override
    public File getDirectory() {
        return this.directory();
    }
    
    public long logSize() {
        return this.logSize;
    }
    
    public void logSize_$eq(final long x$1) {
        this.logSize = x$1;
    }
    
    public void setLogSize(final long x$1) {
        this.logSize = x$1;
    }
    
    public String indexFactory() {
        return this.indexFactory;
    }
    
    public void indexFactory_$eq(final String x$1) {
        this.indexFactory = x$1;
    }
    
    public void setIndexFactory(final String x$1) {
        this.indexFactory = x$1;
    }
    
    public boolean verifyChecksums() {
        return this.verifyChecksums;
    }
    
    public void verifyChecksums_$eq(final boolean x$1) {
        this.verifyChecksums = x$1;
    }
    
    public void setVerifyChecksums(final boolean x$1) {
        this.verifyChecksums = x$1;
    }
    
    public int indexMaxOpenFiles() {
        return this.indexMaxOpenFiles;
    }
    
    public void indexMaxOpenFiles_$eq(final int x$1) {
        this.indexMaxOpenFiles = x$1;
    }
    
    public void setIndexMaxOpenFiles(final int x$1) {
        this.indexMaxOpenFiles = x$1;
    }
    
    public int indexBlockRestartInterval() {
        return this.indexBlockRestartInterval;
    }
    
    public void indexBlockRestartInterval_$eq(final int x$1) {
        this.indexBlockRestartInterval = x$1;
    }
    
    public void setIndexBlockRestartInterval(final int x$1) {
        this.indexBlockRestartInterval = x$1;
    }
    
    public boolean paranoidChecks() {
        return this.paranoidChecks;
    }
    
    public void paranoidChecks_$eq(final boolean x$1) {
        this.paranoidChecks = x$1;
    }
    
    public void setParanoidChecks(final boolean x$1) {
        this.paranoidChecks = x$1;
    }
    
    public int indexWriteBufferSize() {
        return this.indexWriteBufferSize;
    }
    
    public void indexWriteBufferSize_$eq(final int x$1) {
        this.indexWriteBufferSize = x$1;
    }
    
    public void setIndexWriteBufferSize(final int x$1) {
        this.indexWriteBufferSize = x$1;
    }
    
    public int indexBlockSize() {
        return this.indexBlockSize;
    }
    
    public void indexBlockSize_$eq(final int x$1) {
        this.indexBlockSize = x$1;
    }
    
    public void setIndexBlockSize(final int x$1) {
        this.indexBlockSize = x$1;
    }
    
    public String indexCompression() {
        return this.indexCompression;
    }
    
    public void indexCompression_$eq(final String x$1) {
        this.indexCompression = x$1;
    }
    
    public void setIndexCompression(final String x$1) {
        this.indexCompression = x$1;
    }
    
    public String logCompression() {
        return this.logCompression;
    }
    
    public void logCompression_$eq(final String x$1) {
        this.logCompression = x$1;
    }
    
    public void setLogCompression(final String x$1) {
        this.logCompression = x$1;
    }
    
    public long indexCacheSize() {
        return this.indexCacheSize;
    }
    
    public void indexCacheSize_$eq(final long x$1) {
        this.indexCacheSize = x$1;
    }
    
    public void setIndexCacheSize(final long x$1) {
        this.indexCacheSize = x$1;
    }
    
    public int flushDelay() {
        return this.flushDelay;
    }
    
    public void flushDelay_$eq(final int x$1) {
        this.flushDelay = x$1;
    }
    
    public void setFlushDelay(final int x$1) {
        this.flushDelay = x$1;
    }
    
    public int asyncBufferSize() {
        return this.asyncBufferSize;
    }
    
    public void asyncBufferSize_$eq(final int x$1) {
        this.asyncBufferSize = x$1;
    }
    
    public void setAsyncBufferSize(final int x$1) {
        this.asyncBufferSize = x$1;
    }
    
    public boolean monitorStats() {
        return this.monitorStats;
    }
    
    public void monitorStats_$eq(final boolean x$1) {
        this.monitorStats = x$1;
    }
    
    public void setMonitorStats(final boolean x$1) {
        this.monitorStats = x$1;
    }
    
    public int failoverProducersAuditDepth() {
        return this.failoverProducersAuditDepth;
    }
    
    public void failoverProducersAuditDepth_$eq(final int x$1) {
        this.failoverProducersAuditDepth = x$1;
    }
    
    public void setFailoverProducersAuditDepth(final int x$1) {
        this.failoverProducersAuditDepth = x$1;
    }
    
    public int maxFailoverProducersToTrack() {
        return this.maxFailoverProducersToTrack;
    }
    
    public void maxFailoverProducersToTrack_$eq(final int x$1) {
        this.maxFailoverProducersToTrack = x$1;
    }
    
    public void setMaxFailoverProducersToTrack(final int x$1) {
        this.maxFailoverProducersToTrack = x$1;
    }
    
    public MasterLevelDBStore master() {
        return this.master;
    }
    
    public void master_$eq(final MasterLevelDBStore x$1) {
        this.master = x$1;
    }
    
    public SlaveLevelDBStore slave() {
        return this.slave;
    }
    
    public void slave_$eq(final SlaveLevelDBStore x$1) {
        this.slave = x$1;
    }
    
    public ZKClient zk_client() {
        return this.zk_client;
    }
    
    public void zk_client_$eq(final ZKClient x$1) {
        this.zk_client = x$1;
    }
    
    public ZooKeeperGroup zk_group() {
        return this.zk_group;
    }
    
    public void zk_group_$eq(final ZooKeeperGroup x$1) {
        this.zk_group = x$1;
    }
    
    public long position() {
        return this.position;
    }
    
    public void position_$eq(final long x$1) {
        this.position = x$1;
    }
    
    @Override
    public String toString() {
        return new StringOps(Predef$.MODULE$.augmentString("Replicated LevelDB[%s, %s/%s]")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { this.directory().getAbsolutePath(), this.zkAddress(), this.zkPath() }));
    }
    
    public SystemUsage usageManager() {
        return this.usageManager;
    }
    
    public void usageManager_$eq(final SystemUsage x$1) {
        this.usageManager = x$1;
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        this.usageManager_$eq(usageManager);
    }
    
    public String node_id() {
        return ReplicatedLevelDBStoreTrait$.MODULE$.node_id(this.directory());
    }
    
    @Override
    public void init() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: getfield        org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.brokerService:Lorg/apache/activemq/broker/BrokerService;
        //     4: ifnull          69
        //     7: aload_0         /* this */
        //     8: getfield        org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.brokerService:Lorg/apache/activemq/broker/BrokerService;
        //    11: invokevirtual   org/apache/activemq/broker/BrokerService.isUseJmx:()Z
        //    14: ifeq            69
        //    17: aload_0         /* this */
        //    18: getfield        org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.brokerService:Lorg/apache/activemq/broker/BrokerService;
        //    21: invokevirtual   org/apache/activemq/broker/BrokerService.getManagementContext:()Lorg/apache/activemq/broker/jmx/ManagementContext;
        //    24: new             Lorg/apache/activemq/leveldb/replicated/ReplicatedLevelDBStoreView;
        //    27: dup            
        //    28: aload_0         /* this */
        //    29: invokespecial   org/apache/activemq/leveldb/replicated/ReplicatedLevelDBStoreView.<init>:(Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore;)V
        //    32: aload_0         /* this */
        //    33: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.objectName:()Ljavax/management/ObjectName;
        //    36: invokestatic    org/apache/activemq/broker/jmx/AnnotatedMBean.registerMBean:(Lorg/apache/activemq/broker/jmx/ManagementContext;Ljava/lang/Object;Ljavax/management/ObjectName;)V
        //    39: goto            69
        //    42: astore_1       
        //    43: getstatic       org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.MODULE$:Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$;
        //    46: aload_1        
        //    47: new             Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$1;
        //    50: dup            
        //    51: aload_0         /* this */
        //    52: aload_1        
        //    53: invokespecial   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$1.<init>:(Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore;Ljava/lang/Throwable;)V
        //    56: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    59: iconst_0       
        //    60: anewarray       Ljava/lang/Object;
        //    63: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //    66: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.warn:(Ljava/lang/Throwable;Lscala/Function0;Lscala/collection/Seq;)V
        //    69: aload_0         /* this */
        //    70: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.directory:()Ljava/io/File;
        //    73: invokevirtual   java/io/File.mkdirs:()Z
        //    76: pop            
        //    77: new             Lorg/apache/activemq/leveldb/RecordLog;
        //    80: dup            
        //    81: aload_0         /* this */
        //    82: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.directory:()Ljava/io/File;
        //    85: ldc_w           ".log"
        //    88: invokespecial   org/apache/activemq/leveldb/RecordLog.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //    91: astore_2        /* log */
        //    92: aload_2         /* log */
        //    93: aload_0         /* this */
        //    94: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.logSize:()J
        //    97: invokevirtual   org/apache/activemq/leveldb/RecordLog.logSize_$eq:(J)V
        //   100: aload_2         /* log */
        //   101: aload_2         /* log */
        //   102: invokevirtual   org/apache/activemq/leveldb/RecordLog.open$default$1:()J
        //   105: invokevirtual   org/apache/activemq/leveldb/RecordLog.open:(J)Ljava/lang/Object;
        //   108: pop            
        //   109: aload_0         /* this */
        //   110: aload_2         /* log */
        //   111: invokevirtual   org/apache/activemq/leveldb/RecordLog.current_appender:()Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   114: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogAppender.append_position:()J
        //   117: aload_2         /* log */
        //   118: invokevirtual   org/apache/activemq/leveldb/RecordLog.close:()V
        //   121: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.position_$eq:(J)V
        //   124: aload_0         /* this */
        //   125: new             Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;
        //   128: dup            
        //   129: aload_0         /* this */
        //   130: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zkAddress:()Ljava/lang/String;
        //   133: aload_0         /* this */
        //   134: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zkSessionTmeout:()Ljava/lang/String;
        //   137: invokestatic    org/linkedin/util/clock/Timespan.parse:(Ljava/lang/String;)Lorg/linkedin/util/clock/Timespan;
        //   140: aconst_null    
        //   141: invokespecial   org/apache/activemq/leveldb/replicated/groups/ZKClient.<init>:(Ljava/lang/String;Lorg/linkedin/util/clock/Timespan;Lorg/apache/zookeeper/Watcher;)V
        //   144: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_client_$eq:(Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;)V
        //   147: aload_0         /* this */
        //   148: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zkPassword:()Ljava/lang/String;
        //   151: ifnull          165
        //   154: aload_0         /* this */
        //   155: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_client:()Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;
        //   158: aload_0         /* this */
        //   159: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zkPassword:()Ljava/lang/String;
        //   162: invokevirtual   org/apache/activemq/leveldb/replicated/groups/ZKClient.setPassword:(Ljava/lang/String;)V
        //   165: aload_0         /* this */
        //   166: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_client:()Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;
        //   169: invokevirtual   org/apache/activemq/leveldb/replicated/groups/ZKClient.start:()V
        //   172: aload_0         /* this */
        //   173: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_client:()Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;
        //   176: ldc_w           "30s"
        //   179: invokestatic    org/linkedin/util/clock/Timespan.parse:(Ljava/lang/String;)Lorg/linkedin/util/clock/Timespan;
        //   182: invokevirtual   org/apache/activemq/leveldb/replicated/groups/ZKClient.waitForConnected:(Lorg/linkedin/util/clock/Timespan;)V
        //   185: aload_0         /* this */
        //   186: getstatic       org/apache/activemq/leveldb/replicated/groups/ZooKeeperGroupFactory$.MODULE$:Lorg/apache/activemq/leveldb/replicated/groups/ZooKeeperGroupFactory$;
        //   189: aload_0         /* this */
        //   190: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_client:()Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;
        //   193: aload_0         /* this */
        //   194: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zkPath:()Ljava/lang/String;
        //   197: invokevirtual   org/apache/activemq/leveldb/replicated/groups/ZooKeeperGroupFactory$.create:(Lorg/apache/activemq/leveldb/replicated/groups/ZKClient;Ljava/lang/String;)Lorg/apache/activemq/leveldb/replicated/groups/ZooKeeperGroup;
        //   200: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_group_$eq:(Lorg/apache/activemq/leveldb/replicated/groups/ZooKeeperGroup;)V
        //   203: new             Lorg/apache/activemq/leveldb/replicated/MasterElector;
        //   206: dup            
        //   207: aload_0         /* this */
        //   208: invokespecial   org/apache/activemq/leveldb/replicated/MasterElector.<init>:(Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore;)V
        //   211: astore          master_elector
        //   213: getstatic       org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.MODULE$:Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$;
        //   216: new             Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$2;
        //   219: dup            
        //   220: aload_0         /* this */
        //   221: invokespecial   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$2.<init>:(Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore;)V
        //   224: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //   227: iconst_0       
        //   228: anewarray       Ljava/lang/Object;
        //   231: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //   234: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.debug:(Lscala/Function0;Lscala/collection/Seq;)V
        //   237: aload           master_elector
        //   239: aload_0         /* this */
        //   240: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.zk_group:()Lorg/apache/activemq/leveldb/replicated/groups/ZooKeeperGroup;
        //   243: invokevirtual   org/apache/activemq/leveldb/replicated/MasterElector.start:(Lorg/apache/activemq/leveldb/replicated/groups/ZooKeeperGroup;)V
        //   246: getstatic       org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.MODULE$:Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$;
        //   249: new             Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$3;
        //   252: dup            
        //   253: aload_0         /* this */
        //   254: invokespecial   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$$anonfun$init$3.<init>:(Lorg/apache/activemq/leveldb/replicated/ElectingLevelDBStore;)V
        //   257: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //   260: iconst_0       
        //   261: anewarray       Ljava/lang/Object;
        //   264: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //   267: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore$.debug:(Lscala/Function0;Lscala/collection/Seq;)V
        //   270: aload           master_elector
        //   272: invokevirtual   org/apache/activemq/leveldb/replicated/MasterElector.join:()V
        //   275: aload_0         /* this */
        //   276: iconst_1       
        //   277: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.setUseLock:(Z)V
        //   280: aload_0         /* this */
        //   281: aload_0         /* this */
        //   282: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.createDefaultLocker:()Lorg/apache/activemq/broker/Locker;
        //   285: invokevirtual   org/apache/activemq/leveldb/replicated/ElectingLevelDBStore.setLocker:(Lorg/apache/activemq/broker/Locker;)V
        //   288: return         
        //   289: astore_3       
        //   290: aload_2        
        //   291: invokevirtual   org/apache/activemq/leveldb/RecordLog.close:()V
        //   294: aload_3        
        //   295: athrow         
        //    StackMapTable: 00 04 6A 07 01 DC 1A FD 00 5F 00 07 01 74 F7 00 7B 07 01 DC
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  110    117    289    296    Any
        //  17     42     42     69     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.ast.AstBuilder.convertLocalVariables(AstBuilder.java:2895)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2445)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public Locker createDefaultLocker() {
        return (Locker)new ElectingLevelDBStore$$anon.ElectingLevelDBStore$$anon$1(this);
    }
    
    public CountDownLatch master_started_latch() {
        return this.master_started_latch;
    }
    
    public AtomicBoolean master_started() {
        return this.master_started;
    }
    
    public void start_master(final Function1<Object, BoxedUnit> func) {
        Predef$.MODULE$.assert(this.master() == null);
        this.master_$eq(this.create_master());
        this.master_started().set(true);
        this.master().blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$start_master.ElectingLevelDBStore$$anonfun$start_master$1(this)));
        this.master().blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$start_master.ElectingLevelDBStore$$anonfun$start_master$2(this, (Function1)func)));
    }
    
    public boolean isMaster() {
        return this.master_started().get() && !this.master_stopped().get();
    }
    
    public CountDownLatch stopped_latch() {
        return this.stopped_latch;
    }
    
    public AtomicBoolean master_stopped() {
        return this.master_stopped;
    }
    
    public void stop_master(final Function0<BoxedUnit> func) {
        Predef$.MODULE$.assert(this.master() != null);
        this.master().blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$stop_master.ElectingLevelDBStore$$anonfun$stop_master$1(this, (Function0)func)));
        this.master().blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$stop_master.ElectingLevelDBStore$$anonfun$stop_master$2(this)));
    }
    
    public ObjectName objectName() {
        String objectNameStr = BrokerMBeanSupport.createPersistenceAdapterName(this.brokerService.getBrokerObjectName().toString(), new StringBuilder().append((Object)"LevelDB[").append((Object)this.directory().getAbsolutePath()).append((Object)"]").toString()).toString();
        objectNameStr = new StringBuilder().append((Object)objectNameStr).append((Object)",view=Replication").toString();
        return new ObjectName(objectNameStr);
    }
    
    public void doStart() {
        this.master_started_latch().await();
    }
    
    public void doStop(final ServiceStopper stopper) {
        if (this.brokerService != null && this.brokerService.isUseJmx()) {
            this.brokerService.getManagementContext().unregisterMBean(this.objectName());
        }
        this.zk_group().close();
        this.zk_client().close();
        this.zk_client_$eq(null);
        if (this.master() != null) {
            final CountDownLatch latch = new CountDownLatch(1);
            this.stop_master((Function0<BoxedUnit>)new ElectingLevelDBStore$$anonfun$doStop.ElectingLevelDBStore$$anonfun$doStop$1(this, latch));
            latch.await();
        }
        if (this.slave() != null) {
            final CountDownLatch latch2 = new CountDownLatch(1);
            this.stop_slave((Function0<BoxedUnit>)new ElectingLevelDBStore$$anonfun$doStop.ElectingLevelDBStore$$anonfun$doStop$2(this, latch2));
            latch2.await();
        }
        if (this.master_started().get()) {
            this.stopped_latch().countDown();
        }
    }
    
    public void start_slave(final String address, final Function0<BoxedUnit> func) {
        Predef$.MODULE$.assert(this.master() == null);
        this.slave_$eq(this.create_slave());
        this.slave().connect_$eq(address);
        this.slave().blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$start_slave.ElectingLevelDBStore$$anonfun$start_slave$1(this, (Function0)func)));
    }
    
    public void stop_slave(final Function0<BoxedUnit> func) {
        if (this.slave() != null) {
            final SlaveLevelDBStore s = this.slave();
            this.slave_$eq(null);
            s.blocking_executor().execute((Runnable)package$.MODULE$.$up((Function0)new ElectingLevelDBStore$$anonfun$stop_slave.ElectingLevelDBStore$$anonfun$stop_slave$1(this, (Function0)func, s)));
        }
    }
    
    public SlaveLevelDBStore create_slave() {
        final SlaveLevelDBStore slave = new SlaveLevelDBStore();
        this.configure(slave);
        return slave;
    }
    
    public MasterLevelDBStore create_master() {
        final MasterLevelDBStore master = new MasterLevelDBStore();
        this.configure(master);
        master.replicas_$eq(this.replicas());
        master.bind_$eq(this.bind());
        master.syncTo_$eq(this.sync());
        return master;
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
        this.brokerName_$eq(brokerName);
    }
    
    @Override
    public void deleteAllMessages() {
        if (this.proxy_target() == null) {
            ElectingLevelDBStore$.MODULE$.info((Function0<String>)new ElectingLevelDBStore$$anonfun$deleteAllMessages.ElectingLevelDBStore$$anonfun$deleteAllMessages$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
        }
        else {
            this.proxy_target().deleteAllMessages();
        }
    }
    
    public void configure(final ReplicatedLevelDBStoreTrait store) {
        ((LevelDBStore)store).directory_$eq(this.directory());
        ((LevelDBStore)store).indexFactory_$eq(this.indexFactory());
        ((LevelDBStore)store).verifyChecksums_$eq(this.verifyChecksums());
        ((LevelDBStore)store).indexMaxOpenFiles_$eq(this.indexMaxOpenFiles());
        ((LevelDBStore)store).indexBlockRestartInterval_$eq(this.indexBlockRestartInterval());
        ((LevelDBStore)store).paranoidChecks_$eq(this.paranoidChecks());
        ((LevelDBStore)store).indexWriteBufferSize_$eq(this.indexWriteBufferSize());
        ((LevelDBStore)store).indexBlockSize_$eq(this.indexBlockSize());
        ((LevelDBStore)store).indexCompression_$eq(this.indexCompression());
        ((LevelDBStore)store).logCompression_$eq(this.logCompression());
        ((LevelDBStore)store).indexCacheSize_$eq(this.indexCacheSize());
        ((LevelDBStore)store).flushDelay_$eq(this.flushDelay());
        ((LevelDBStore)store).asyncBufferSize_$eq(this.asyncBufferSize());
        ((LevelDBStore)store).monitorStats_$eq(this.monitorStats());
        store.securityToken_$eq(this.securityToken());
        ((LevelDBStore)store).setFailoverProducersAuditDepth(this.failoverProducersAuditDepth());
        ((LevelDBStore)store).setMaxFailoverProducersToTrack(this.maxFailoverProducersToTrack());
        ((LevelDBStore)store).setBrokerName(this.brokerName());
        ((LockableServiceSupport)store).setBrokerService(this.brokerService);
        ((LevelDBStore)store).setUsageManager(this.usageManager());
    }
    
    public String address(final int port) {
        if (this.hostname() == null) {
            this.hostname_$eq(ElectingLevelDBStore$.MODULE$.machine_hostname());
        }
        return new StringBuilder().append((Object)"tcp://").append((Object)this.hostname()).append((Object)":").append((Object)BoxesRunTime.boxToInteger(port)).toString();
    }
    
    @Override
    public long size() {
        long n;
        if (this.master() == null) {
            if (this.slave() == null) {
                final LongRef rc = LongRef.create(0L);
                if (this.directory().exists()) {
                    Predef$.MODULE$.refArrayOps((Object[])this.directory().list()).foreach((Function1)new ElectingLevelDBStore$$anonfun$size.ElectingLevelDBStore$$anonfun$size$1(this, rc));
                }
                n = rc.elem;
            }
            else {
                n = this.slave().size();
            }
        }
        else {
            n = this.master().size();
        }
        return n;
    }
    
    public String getZkAddress() {
        return this.zkAddress();
    }
    
    public String getZkPassword() {
        return this.zkPassword();
    }
    
    public String getZkPath() {
        return this.zkPath();
    }
    
    public String getZkSessionTmeout() {
        return this.zkSessionTmeout();
    }
    
    public String getContainer() {
        return this.container();
    }
    
    public String getHostname() {
        return this.hostname();
    }
    
    public String getBind() {
        return this.bind();
    }
    
    public int getWeight() {
        return this.weight();
    }
    
    public int getReplicas() {
        return this.replicas();
    }
    
    public String getSync() {
        return this.sync();
    }
    
    public String getSecurityToken() {
        return this.securityToken();
    }
    
    public long getLogSize() {
        return this.logSize();
    }
    
    public String getIndexFactory() {
        return this.indexFactory();
    }
    
    public boolean getVerifyChecksums() {
        return this.verifyChecksums();
    }
    
    public int getIndexMaxOpenFiles() {
        return this.indexMaxOpenFiles();
    }
    
    public int getIndexBlockRestartInterval() {
        return this.indexBlockRestartInterval();
    }
    
    public boolean getParanoidChecks() {
        return this.paranoidChecks();
    }
    
    public int getIndexWriteBufferSize() {
        return this.indexWriteBufferSize();
    }
    
    public int getIndexBlockSize() {
        return this.indexBlockSize();
    }
    
    public String getIndexCompression() {
        return this.indexCompression();
    }
    
    public String getLogCompression() {
        return this.logCompression();
    }
    
    public long getIndexCacheSize() {
        return this.indexCacheSize();
    }
    
    public int getFlushDelay() {
        return this.flushDelay();
    }
    
    public int getAsyncBufferSize() {
        return this.asyncBufferSize();
    }
    
    public boolean getMonitorStats() {
        return this.monitorStats();
    }
    
    public int getFailoverProducersAuditDepth() {
        return this.failoverProducersAuditDepth();
    }
    
    public int getMaxFailoverProducersToTrack() {
        return this.maxFailoverProducersToTrack();
    }
    
    public ElectingLevelDBStore() {
        this.zkAddress = "127.0.0.1:2181";
        this.zkPath = "/default";
        this.zkSessionTmeout = "2s";
        this.bind = "tcp://0.0.0.0:61619";
        this.weight = 1;
        this.replicas = 3;
        this.sync = "quorum_mem";
        this.securityToken = "";
        this.directory = LevelDBStore$.MODULE$.DEFAULT_DIRECTORY();
        this.logSize = 104857600L;
        this.indexFactory = "org.fusesource.leveldbjni.JniDBFactory, org.iq80.leveldb.impl.Iq80DBFactory";
        this.verifyChecksums = false;
        this.indexMaxOpenFiles = 1000;
        this.indexBlockRestartInterval = 16;
        this.paranoidChecks = false;
        this.indexWriteBufferSize = 6291456;
        this.indexBlockSize = 4096;
        this.indexCompression = "snappy";
        this.logCompression = "none";
        this.indexCacheSize = 268435456L;
        this.flushDelay = 0;
        this.asyncBufferSize = 4194304;
        this.monitorStats = false;
        this.failoverProducersAuditDepth = 2048;
        this.maxFailoverProducersToTrack = 64;
        this.position = -1L;
        this.master_started_latch = new CountDownLatch(1);
        this.master_started = new AtomicBoolean(false);
        this.stopped_latch = new CountDownLatch(1);
        this.master_stopped = new AtomicBoolean(false);
    }
}
