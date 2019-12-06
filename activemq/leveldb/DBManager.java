// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import org.fusesource.hawtdispatch.EventAggregators;
import scala.Option$;
import org.iq80.leveldb.DBIterator;
import scala.Option;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.leveldb.record.CollectionRecord;
import scala.runtime.BoxedUnit;
import org.apache.activemq.leveldb.record.SubscriptionRecord;
import org.apache.activemq.command.SubscriptionInfo;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.command.ActiveMQQueue;
import scala.collection.Seq;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.ListBuffer$;
import scala.collection.mutable.ListBuffer;
import scala.Tuple2;
import org.apache.activemq.command.Message;
import scala.runtime.LongRef;
import scala.runtime.ObjectRef;
import org.apache.activemq.store.MessageRecoveryListener;
import java.util.concurrent.Executor;
import scala.Array$;
import scala.reflect.ClassTag$;
import java.util.Collection;
import scala.collection.JavaConversions$;
import scala.Predef$;
import java.util.concurrent.TimeUnit;
import java.lang.ref.WeakReference;
import scala.Function1;
import scala.runtime.BoxesRunTime;
import scala.Function0;
import org.fusesource.hawtdispatch.package$;
import org.apache.activemq.command.ProducerId;
import java.util.concurrent.ExecutorService;
import org.fusesource.hawtdispatch.CustomDispatchSource;
import java.util.LinkedHashMap;
import org.apache.activemq.ActiveMQMessageAuditNoSync;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import scala.collection.mutable.HashSet;
import org.apache.activemq.command.MessageId;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.leveldb.util.TimeMetric;
import org.fusesource.hawtdispatch.DispatchQueue;
import java.util.concurrent.atomic.AtomicLong;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0011\rb\u0001B\u0001\u0003\u0001-\u0011\u0011\u0002\u0012\"NC:\fw-\u001a:\u000b\u0005\r!\u0011a\u00027fm\u0016dGM\u0019\u0006\u0003\u000b\u0019\t\u0001\"Y2uSZ,W.\u001d\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0001\u0001\u0014\u0005\u0001a\u0001CA\u0007\u0011\u001b\u0005q!\"A\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Eq!AB!osJ+g\r\u0003\u0005\u0014\u0001\t\u0015\r\u0011\"\u0001\u0015\u0003\u0019\u0001\u0018M]3oiV\tQ\u0003\u0005\u0002\u0017/5\t!!\u0003\u0002\u0019\u0005\taA*\u001a<fY\u0012\u00135\u000b^8sK\"A!\u0004\u0001B\u0001B\u0003%Q#A\u0004qCJ,g\u000e\u001e\u0011\t\u000bq\u0001A\u0011A\u000f\u0002\rqJg.\u001b;?)\tqr\u0004\u0005\u0002\u0017\u0001!)1c\u0007a\u0001+!9\u0011\u0005\u0001a\u0001\n\u0003\u0011\u0013!\u00057bgR\u001cu\u000e\u001c7fGRLwN\\&fsV\t1\u0005\u0005\u0002%[5\tQE\u0003\u0002'O\u00051\u0011\r^8nS\u000eT!\u0001K\u0015\u0002\u0015\r|gnY;se\u0016tGO\u0003\u0002+W\u0005!Q\u000f^5m\u0015\u0005a\u0013\u0001\u00026bm\u0006L!AL\u0013\u0003\u0015\u0005#x.\\5d\u0019>tw\rC\u00041\u0001\u0001\u0007I\u0011A\u0019\u0002+1\f7\u000f^\"pY2,7\r^5p].+\u0017p\u0018\u0013fcR\u0011!'\u000e\t\u0003\u001bMJ!\u0001\u000e\b\u0003\tUs\u0017\u000e\u001e\u0005\bm=\n\t\u00111\u0001$\u0003\rAH%\r\u0005\u0007q\u0001\u0001\u000b\u0015B\u0012\u0002%1\f7\u000f^\"pY2,7\r^5p].+\u0017\u0010\t\u0005\bu\u0001\u0001\r\u0011\"\u0001#\u00031a\u0017m\u001d;Q\u0019&\u001cHoS3z\u0011\u001da\u0004\u00011A\u0005\u0002u\n\u0001\u0003\\1tiBc\u0015n\u001d;LKf|F%Z9\u0015\u0005Ir\u0004b\u0002\u001c<\u0003\u0003\u0005\ra\t\u0005\u0007\u0001\u0002\u0001\u000b\u0015B\u0012\u0002\u001b1\f7\u000f\u001e)MSN$8*Z=!\u0011\u0015\u0011\u0005\u0001\"\u0001D\u0003\u0019\u0019G.[3oiV\tA\t\u0005\u0002\u0017\u000b&\u0011aI\u0001\u0002\u000e\u0019\u00164X\r\u001c#C\u00072LWM\u001c;\t\u000b!\u0003A\u0011A%\u0002\u001b]\u0014\u0018\u000e^3Fq\u0016\u001cW\u000f^8s+\u0005Q\u0005CA&M\u001b\u00059\u0013BA'(\u0005=)\u00050Z2vi>\u00148+\u001a:wS\u000e,\u0007\"B(\u0001\t\u0003\u0001\u0016A\u00034mkNDG)\u001a7bsV\t\u0011\u000b\u0005\u0002\u000e%&\u00111K\u0004\u0002\u0004\u0013:$\bbB+\u0001\u0005\u0004%\tAV\u0001\u000eI&\u001c\b/\u0019;dQF+X-^3\u0016\u0003]\u0003\"\u0001W/\u000e\u0003eS!AW.\u0002\u0019!\fw\u000f\u001e3jgB\fGo\u00195\u000b\u0005qC\u0011A\u00034vg\u0016\u001cx.\u001e:dK&\u0011a,\u0017\u0002\u000e\t&\u001c\b/\u0019;dQF+X-^3\t\r\u0001\u0004\u0001\u0015!\u0003X\u00039!\u0017n\u001d9bi\u000eD\u0017+^3vK\u0002BqA\u0019\u0001C\u0002\u0013\u0005!%\u0001\fbgft7mQ1qC\u000eLG/\u001f*f[\u0006Lg.\u001b8h\u0011\u0019!\u0007\u0001)A\u0005G\u00059\u0012m]=oG\u000e\u000b\u0007/Y2jif\u0014V-\\1j]&tw\r\t\u0005\u0006M\u0002!\taZ\u0001\nGJ,\u0017\r^3V_^$\u0012\u0001\u001b\t\u0003-%L!A\u001b\u0002\u0003\u0019\u0011+G.Y=bE2,WkT,\t\u000f1\u0004\u0001\u0019!C\u0001[\u00069Ro\\<F]F,X-^3EK2\f\u0017PU3rKN$X\rZ\u000b\u0002]B\u0011Qb\\\u0005\u0003a:\u0011A\u0001T8oO\"9!\u000f\u0001a\u0001\n\u0003\u0019\u0018aG;po\u0016s\u0017/^3vK\u0012+G.Y=SKF,7\u000f^3e?\u0012*\u0017\u000f\u0006\u00023i\"9a']A\u0001\u0002\u0004q\u0007B\u0002<\u0001A\u0003&a.\u0001\rv_^,e.];fk\u0016$U\r\\1z%\u0016\fXm\u001d;fI\u0002Bq\u0001\u001f\u0001A\u0002\u0013\u0005Q.A\rv_^,e.];fk\u0016tu\u000eZ3mCf\u0014V-]3ti\u0016$\u0007b\u0002>\u0001\u0001\u0004%\ta_\u0001\u001ek><XI\\9vKV,gj\u001c3fY\u0006L(+Z9fgR,Gm\u0018\u0013fcR\u0011!\u0007 \u0005\bme\f\t\u00111\u0001o\u0011\u0019q\b\u0001)Q\u0005]\u0006QRo\\<F]F,X-^3O_\u0012,G.Y=SKF,7\u000f^3eA!A\u0011\u0011\u0001\u0001A\u0002\u0013\u0005Q.\u0001\tv_^\u001cEn\\:fI\u000e{WO\u001c;fe\"I\u0011Q\u0001\u0001A\u0002\u0013\u0005\u0011qA\u0001\u0015k><8\t\\8tK\u0012\u001cu.\u001e8uKJ|F%Z9\u0015\u0007I\nI\u0001\u0003\u00057\u0003\u0007\t\t\u00111\u0001o\u0011\u001d\ti\u0001\u0001Q!\n9\f\u0011#^8x\u00072|7/\u001a3D_VtG/\u001a:!\u0011!\t\t\u0002\u0001a\u0001\n\u0003i\u0017AE;po\u000e\u000bgnY3mK\u0012\u001cu.\u001e8uKJD\u0011\"!\u0006\u0001\u0001\u0004%\t!a\u0006\u0002-U|woQ1oG\u0016dW\rZ\"pk:$XM]0%KF$2AMA\r\u0011!1\u00141CA\u0001\u0002\u0004q\u0007bBA\u000f\u0001\u0001\u0006KA\\\u0001\u0014k><8)\u00198dK2,GmQ8v]R,'\u000f\t\u0005\t\u0003C\u0001\u0001\u0019!C\u0001[\u0006\tRo\\<Ti>\u0014\u0018N\\4D_VtG/\u001a:\t\u0013\u0005\u0015\u0002\u00011A\u0005\u0002\u0005\u001d\u0012!F;poN#xN]5oO\u000e{WO\u001c;fe~#S-\u001d\u000b\u0004e\u0005%\u0002\u0002\u0003\u001c\u0002$\u0005\u0005\t\u0019\u00018\t\u000f\u00055\u0002\u0001)Q\u0005]\u0006\u0011Ro\\<Ti>\u0014\u0018N\\4D_VtG/\u001a:!\u0011!\t\t\u0004\u0001a\u0001\n\u0003i\u0017\u0001E;poN#xN]3e\u0007>,h\u000e^3s\u0011%\t)\u0004\u0001a\u0001\n\u0003\t9$\u0001\u000bv_^\u001cFo\u001c:fI\u000e{WO\u001c;fe~#S-\u001d\u000b\u0004e\u0005e\u0002\u0002\u0003\u001c\u00024\u0005\u0005\t\u0019\u00018\t\u000f\u0005u\u0002\u0001)Q\u0005]\u0006\tRo\\<Ti>\u0014X\rZ\"pk:$XM\u001d\u0011\t\u0013\u0005\u0005\u0003A1A\u0005\u0002\u0005\r\u0013\u0001F;po~\u001bw.\u001c9mKR,w\f\\1uK:\u001c\u00170\u0006\u0002\u0002FA!\u0011qIA&\u001b\t\tIE\u0003\u0002+\u0005%!\u0011QJA%\u0005)!\u0016.\\3NKR\u0014\u0018n\u0019\u0005\t\u0003#\u0002\u0001\u0015!\u0003\u0002F\u0005)Ro\\<`G>l\u0007\u000f\\3uK~c\u0017\r^3oGf\u0004\u0003\"CA+\u0001\u0001\u0007I\u0011AA,\u00035\u0001XM\u001c3j]\u001e\u001cFo\u001c:fgV\u0011\u0011\u0011\f\t\b\u0017\u0006m\u0013qLA6\u0013\r\tif\n\u0002\u0012\u0007>t7-\u001e:sK:$\b*Y:i\u001b\u0006\u0004\b\u0003BA1\u0003Oj!!a\u0019\u000b\u0007\u0005\u0015D!A\u0004d_6l\u0017M\u001c3\n\t\u0005%\u00141\r\u0002\n\u001b\u0016\u001c8/Y4f\u0013\u0012\u0004b!!\u001c\u0002x\u0005mTBAA8\u0015\u0011\t\t(a\u001d\u0002\u000f5,H/\u00192mK*\u0019\u0011Q\u000f\b\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0003\u0002z\u0005=$a\u0002%bg\"\u001cV\r\u001e\t\u0004Q\u0006u\u0014bAA@S\niQ*Z:tC\u001e,\u0017i\u0019;j_:D\u0011\"a!\u0001\u0001\u0004%\t!!\"\u0002#A,g\u000eZ5oON#xN]3t?\u0012*\u0017\u000fF\u00023\u0003\u000fC\u0011BNAA\u0003\u0003\u0005\r!!\u0017\t\u0011\u0005-\u0005\u0001)Q\u0005\u00033\na\u0002]3oI&twm\u0015;pe\u0016\u001c\b\u0005C\u0005\u0002\u0010\u0002\u0001\r\u0011\"\u0001\u0002\u0012\u0006Q2-\u00198dK2\f'\r\\3`K:\fX/Z;f?\u0006\u001cG/[8ogV\u0011\u00111\u0013\t\t\u0003+\u000b9*a'\u0002|5\t\u0011&C\u0002\u0002\u001a&\u0012q\u0001S1tQ6\u000b\u0007\u000f\u0005\u0003\u0002\u001e\u0006\rfb\u0001\f\u0002 &\u0019\u0011\u0011\u0015\u0002\u0002'U{w/T1oC\u001e,'oQ8ogR\fg\u000e^:\n\t\u0005\u0015\u0016q\u0015\u0002\u000e#V,W/Z#oiJL8*Z=\u000b\u0007\u0005\u0005&\u0001C\u0005\u0002,\u0002\u0001\r\u0011\"\u0001\u0002.\u0006q2-\u00198dK2\f'\r\\3`K:\fX/Z;f?\u0006\u001cG/[8og~#S-\u001d\u000b\u0004e\u0005=\u0006\"\u0003\u001c\u0002*\u0006\u0005\t\u0019AAJ\u0011!\t\u0019\f\u0001Q!\n\u0005M\u0015aG2b]\u000e,G.\u00192mK~+g.];fk\u0016|\u0016m\u0019;j_:\u001c\b\u0005C\u0005\u00028\u0002\u0011\r\u0011\"\u0001\u0002:\u0006IA.Y:u+><\u0018\nZ\u000b\u0003\u0003w\u00032\u0001JA_\u0013\r\ty,\n\u0002\u000e\u0003R|W.[2J]R,w-\u001a:\t\u0011\u0005\r\u0007\u0001)A\u0005\u0003w\u000b!\u0002\\1tiV{w/\u00133!\u0011%\t9\r\u0001a\u0001\n\u0003\tI-A\rqe>$WoY3s'\u0016\fX/\u001a8dK&#GK]1dW\u0016\u0014XCAAf!\u0011\ti-a4\u000e\u0003\u0011I1!!5\u0005\u0005i\t5\r^5wK6\u000bV*Z:tC\u001e,\u0017)\u001e3ji:{7+\u001f8d\u0011%\t)\u000e\u0001a\u0001\n\u0003\t9.A\u000fqe>$WoY3s'\u0016\fX/\u001a8dK&#GK]1dW\u0016\u0014x\fJ3r)\r\u0011\u0014\u0011\u001c\u0005\nm\u0005M\u0017\u0011!a\u0001\u0003\u0017D\u0001\"!8\u0001A\u0003&\u00111Z\u0001\u001baJ|G-^2feN+\u0017/^3oG\u0016LE\r\u0016:bG.,'\u000f\t\u0005\b\u0003C\u0004A\u0011AAr\u0003e9W\r\u001e'bgR\u0004&o\u001c3vG\u0016\u00148+Z9vK:\u001cW-\u00133\u0015\u00079\f)\u000f\u0003\u0005\u0002h\u0006}\u0007\u0019AAu\u0003\tIG\r\u0005\u0003\u0002b\u0005-\u0018\u0002BAw\u0003G\u0012!\u0002\u0015:pIV\u001cWM]%e\u0011\u001d\t\t\u0010\u0001C\u0001\u0003g\fQ\u0002\u001d:pG\u0016\u001c8o\u00117pg\u0016$Gc\u0001\u001a\u0002v\"9\u0011q_Ax\u0001\u0004A\u0017aA;po\"9\u00111 \u0001\u0005\n\u0005u\u0018!D:dQ\u0016$W\u000f\\3GYV\u001c\b\u000eF\u00023\u0003\u007fD\u0001B!\u0001\u0002z\u0002\u0007!1A\u0001\u0004e\u00164\u0007#\u0002B\u0003\u0005\u001bAWB\u0001B\u0004\u0015\u0011\u0011\tA!\u0003\u000b\u0007\t-1&\u0001\u0003mC:<\u0017\u0002\u0002B\b\u0005\u000f\u0011QbV3bWJ+g-\u001a:f]\u000e,\u0007\"\u0003B\n\u0001\t\u0007I\u0011\u0001B\u000b\u0003-1G.^:i?F,X-^3\u0016\u0005\t]\u0001CBAK\u00053q\u0007.C\u0002\u0003\u001c%\u0012Q\u0002T5oW\u0016$\u0007*Y:i\u001b\u0006\u0004\b\u0002\u0003B\u0010\u0001\u0001\u0006IAa\u0006\u0002\u0019\u0019dWo\u001d5`cV,W/\u001a\u0011\t\u000f\t\r\u0002\u0001\"\u0001\u0003&\u0005aQM\\9vKV,g\t\\;tQR\u0019!Ga\n\t\u000f\u0005](\u0011\u0005a\u0001Q\"I!1\u0006\u0001C\u0002\u0013\u0005!QF\u0001\fM2,8\u000f[*pkJ\u001cW-\u0006\u0002\u00030A9\u0001L!\r\u00036\tU\u0012b\u0001B\u001a3\n!2)^:u_6$\u0015n\u001d9bi\u000eD7k\\;sG\u0016\u0004BAa\u000e\u0003:5\u0011!\u0011B\u0005\u0005\u0005w\u0011IAA\u0004J]R,w-\u001a:\t\u0011\t}\u0002\u0001)A\u0005\u0005_\tAB\u001a7vg\"\u001cv.\u001e:dK\u0002BqAa\u0011\u0001\t\u0003\u0011)%\u0001\u0007ee\u0006LgN\u00127vg\",7/F\u00013\u0011%\u0011I\u0005\u0001a\u0001\n\u0003\u0011Y%A\u0004ti\u0006\u0014H/\u001a3\u0016\u0005\t5\u0003cA\u0007\u0003P%\u0019!\u0011\u000b\b\u0003\u000f\t{w\u000e\\3b]\"I!Q\u000b\u0001A\u0002\u0013\u0005!qK\u0001\fgR\f'\u000f^3e?\u0012*\u0017\u000fF\u00023\u00053B\u0011B\u000eB*\u0003\u0003\u0005\rA!\u0014\t\u0011\tu\u0003\u0001)Q\u0005\u0005\u001b\n\u0001b\u001d;beR,G\r\t\u0005\b\u0005C\u0002A\u0011\u0001B&\u0003I\u0019h.\u00199qs\u000e{W\u000e\u001d:fgNdunZ:\t\u000f\t\u0015\u0004\u0001\"\u0001\u0003F\u0005)1\u000f^1si\"9!\u0011\u000e\u0001\u0005\u0002\t-\u0014\u0001B:u_B$\u0012A\r\u0005\b\u0005_\u0002A\u0011\u0001B#\u0003\u0019\u0001x\u000e\u001c7HG\"9!1\u000f\u0001\u0005\u0002\t\u0015\u0013\u0001D7p]&$xN]*uCR\u001c\bb\u0002B<\u0001\u0011\u0005!\u0011P\u0001\u000bG\",7m\u001b9pS:$Hc\u0001\u001a\u0003|!A!Q\u0010B;\u0001\u0004\u0011i%\u0001\u0003ts:\u001c\u0007b\u0002BA\u0001\u0011\u0005!QI\u0001\u0006aV\u0014x-\u001a\u0005\b\u0005\u000b\u0003A\u0011\u0001BD\u0003Q9W\r\u001e'bgR\fV/Z;f\u000b:$(/_*fcR\u0019aN!#\t\u000f\t-%1\u0011a\u0001]\u0006\u00191.Z=\t\u000f\t=\u0005\u0001\"\u0001\u0003\u0012\u0006y1m\u001c7mK\u000e$\u0018n\u001c8F[B$\u0018\u0010F\u00023\u0005'CqAa#\u0003\u000e\u0002\u0007a\u000eC\u0004\u0003\u0018\u0002!\tA!'\u0002\u001d\r|G\u000e\\3di&|gnU5{KR\u0019aNa'\t\u000f\t-%Q\u0013a\u0001]\"9!q\u0014\u0001\u0005\u0002\t\u0005\u0016!E2pY2,7\r^5p]&\u001bX)\u001c9usR!!Q\nBR\u0011\u001d\u0011YI!(A\u00029DqAa*\u0001\t\u0003\u0011I+\u0001\bdkJ\u001cxN]'fgN\fw-Z:\u0015\u00179\u0014YKa-\u00036\n\u0015'\u0011\u001a\u0005\t\u0005[\u0013)\u000b1\u0001\u00030\u0006a\u0001O]3qCJ,G-Q2lgB1\u0011Q\u0013BY\u0003?J1!!\u001f*\u0011\u001d\u0011YI!*A\u00029D\u0001Ba.\u0003&\u0002\u0007!\u0011X\u0001\tY&\u001cH/\u001a8feB!!1\u0018Ba\u001b\t\u0011iLC\u0002\u0003@\u0012\tQa\u001d;pe\u0016LAAa1\u0003>\n9R*Z:tC\u001e,'+Z2pm\u0016\u0014\u0018\u0010T5ti\u0016tWM\u001d\u0005\b\u0005\u000f\u0014)\u000b1\u0001o\u0003!\u0019H/\u0019:u!>\u001c\b\"\u0003Bf\u0005K\u0003\n\u00111\u0001o\u0003\ri\u0017\r\u001f\u0005\b\u0005\u001f\u0004A\u0011\u0001Bi\u000319W\r\u001e-B\u0003\u000e$\u0018n\u001c8t)\u0011\u0011\u0019N!<\u0011\u000f5\u0011)N!7\u0003f&\u0019!q\u001b\b\u0003\rQ+\b\u000f\\33!\u0019\tiGa7\u0003`&!!Q\\A8\u0005)a\u0015n\u001d;Ck\u001a4WM\u001d\t\u0005\u0003C\u0012\t/\u0003\u0003\u0003d\u0006\r$aB'fgN\fw-\u001a\t\u0007\u0003[\u0012YNa:\u0011\u0007Y\u0011I/C\u0002\u0003l\n\u00111\u0002W1BG.\u0014VmY8sI\"9!1\u0012Bg\u0001\u0004q\u0007b\u0002By\u0001\u0011\u0005!1_\u0001\u000ecV,W/\u001a)pg&$\u0018n\u001c8\u0015\u00079\u0014)\u0010\u0003\u0005\u0002h\n=\b\u0019AA0\u0011\u001d\u0011I\u0010\u0001C\u0001\u0005w\f\u0001c\u0019:fCR,\u0017+^3vKN#xN]3\u0015\t\tu81\u0001\t\u0004+\t}\u0018bAB\u0001/\t\u0019B*\u001a<fY\u0012\u0013U*Z:tC\u001e,7\u000b^8sK\"A1Q\u0001B|\u0001\u0004\u00199!\u0001\u0003eKN$\b\u0003BA1\u0007\u0013IAaa\u0003\u0002d\ti\u0011i\u0019;jm\u0016l\u0015+U;fk\u0016Dqaa\u0004\u0001\t\u0003\u0019\t\"A\teKN$(o\\=Rk\u0016,Xm\u0015;pe\u0016$2AMB\n\u0011\u001d\u0011Yi!\u0004A\u00029Daaa\u0006\u0001\t\u0003i\u0017\u0001F4fi2{w-\u00119qK:$\u0007k\\:ji&|g\u000eC\u0004\u0004\u001c\u0001!\ta!\b\u0002\u001f\u0005$GmU;cg\u000e\u0014\u0018\u000e\u001d;j_:$baa\b\u0004&\r%\u0002c\u0001\f\u0004\"%\u001911\u0005\u0002\u0003'\u0011+(/\u00192mKN+(m]2sSB$\u0018n\u001c8\t\u000f\r\u001d2\u0011\u0004a\u0001]\u0006IAo\u001c9jG~[W-\u001f\u0005\t\u0007W\u0019I\u00021\u0001\u0004.\u0005!\u0011N\u001c4p!\u0011\t\tga\f\n\t\rE\u00121\r\u0002\u0011'V\u00147o\u0019:jaRLwN\\%oM>Dqa!\u000e\u0001\t\u0003\u00199$\u0001\nsK6|g/Z*vEN\u001c'/\u001b9uS>tGc\u0001\u001a\u0004:!A11HB\u001a\u0001\u0004\u0019y\"A\u0002tk\nDqaa\u0010\u0001\t\u0003\u0019\t%\u0001\tde\u0016\fG/\u001a+pa&\u001c7\u000b^8sKR!11IB%!\r)2QI\u0005\u0004\u0007\u000f:\"\u0001\u0007'fm\u0016dGI\u0011+pa&\u001cW*Z:tC\u001e,7\u000b^8sK\"A1QAB\u001f\u0001\u0004\u0019Y\u0005\u0005\u0003\u0002b\r5\u0013\u0002BB(\u0003G\u0012Q\"Q2uSZ,W*\u0015+pa&\u001c\u0007bBB*\u0001\u0011\u00051QK\u0001\u0011GJ,\u0017\r^3D_2dWm\u0019;j_:$RA\\B,\u0007OB\u0001b!\u0017\u0004R\u0001\u000711L\u0001\u0005]\u0006lW\r\u0005\u0003\u0004^\r\rTBAB0\u0015\r\u0019\tgW\u0001\bQ\u0006<HOY;g\u0013\u0011\u0019)ga\u0018\u0003\r\t+hMZ3s\u0011\u001d\u0019Ig!\u0015A\u0002E\u000babY8mY\u0016\u001cG/[8o)f\u0004X\rC\u0004\u0004n\u0001!\taa\u001c\u0002\r\t,hMZ3s)\u0011\u0019Yf!\u001d\t\u0011\rM41\u000ea\u0001\u0007k\na\u0001]1dW\u0016$\b\u0003BB<\u0007wj!a!\u001f\u000b\u0005)\"\u0011\u0002BB?\u0007s\u0012ABQ=uKN+\u0017/^3oG\u0016Dqa!!\u0001\t\u0003\u0019\u0019)\u0001\u000ede\u0016\fG/\u001a+sC:\u001c\u0018m\u0019;j_:\u001cuN\u001c;bS:,'\u000fF\u0002o\u0007\u000bC\u0001\"a:\u0004\u0000\u0001\u00071q\u0011\t\u0005\u0003C\u001aI)\u0003\u0003\u0004\f\u0006\r$a\u0004-B)J\fgn]1di&|g.\u00133\t\u000f\r=\u0005\u0001\"\u0001\u0004\u0012\u0006Q\"/Z7pm\u0016$&/\u00198tC\u000e$\u0018n\u001c8D_:$\u0018-\u001b8feR\u0019!ga%\t\u000f\t-5Q\u0012a\u0001]\"91q\u0013\u0001\u0005\u0002\t\u0015\u0013a\u00047pC\u0012\u001cu\u000e\u001c7fGRLwN\\:\t\u000f\rm\u0005\u0001\"\u0001\u0004\u001e\u0006Y1M]3bi\u0016\u0004F*[:u)\u0011\u0019yj!*\u0011\u0007U\u0019\t+C\u0002\u0004$^\u0011A\u0002T3wK2$%\t\u0015'jgRD\u0001b!\u0017\u0004\u001a\u0002\u00071q\u0015\t\u0005\u0007S\u001byKD\u0002\u000e\u0007WK1a!,\u000f\u0003\u0019\u0001&/\u001a3fM&!1\u0011WBZ\u0005\u0019\u0019FO]5oO*\u00191Q\u0016\b\t\u000f\r]\u0006\u0001\"\u0001\u0004:\u0006aA-Z:ue>L\b\u000bT5tiR\u0019!ga/\t\u000f\t-5Q\u0017a\u0001]\"91q\u0018\u0001\u0005\u0002\r\u0005\u0017\u0001\u00039mSN$\b+\u001e;\u0015\u000bI\u001a\u0019m!5\t\u0011\t-5Q\u0018a\u0001\u0007\u000b\u0004R!DBd\u0007\u0017L1a!3\u000f\u0005\u0015\t%O]1z!\ri1QZ\u0005\u0004\u0007\u001ft!\u0001\u0002\"zi\u0016D\u0001ba5\u0004>\u0002\u00071QY\u0001\u0006m\u0006dW/\u001a\u0005\b\u0007/\u0004A\u0011ABm\u0003!\u0001H.[:u\u000f\u0016$H\u0003BBn\u0007C\u0004R!DBo\u0007\u000bL1aa8\u000f\u0005\u0019y\u0005\u000f^5p]\"A!1RBk\u0001\u0004\u0019)\rC\u0004\u0004f\u0002!\taa:\u0002\u0017Ad\u0017n\u001d;EK2,G/\u001a\u000b\u0004e\r%\b\u0002\u0003BF\u0007G\u0004\ra!2\t\u000f\r5\b\u0001\"\u0001\u0004p\u0006i\u0001\u000f\\5ti&#XM]1u_J,\"a!=\u0011\t\rM81`\u0007\u0003\u0007kT1aAB|\u0015\r\u0019I\u0010C\u0001\u0005SFD\u0004'\u0003\u0003\u0004~\u000eU(A\u0003#C\u0013R,'/\u0019;pe\"9A\u0011\u0001\u0001\u0005\u0002\u0011\r\u0011AC4fi6+7o]1hKR!!q\u001cC\u0003\u0011!!9aa@A\u0002\u0005}\u0013!\u0001=\t\u0013\u0011-\u0001!%A\u0005\u0002\u00115\u0011\u0001G2veN|'/T3tg\u0006<Wm\u001d\u0013eK\u001a\fW\u000f\u001c;%kU\u0011Aq\u0002\u0016\u0004]\u0012E1F\u0001C\n!\u0011!)\u0002b\b\u000e\u0005\u0011]!\u0002\u0002C\r\t7\t\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0011ua\"\u0001\u0006b]:|G/\u0019;j_:LA\u0001\"\t\u0005\u0018\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3")
public class DBManager
{
    private final LevelDBStore parent;
    private AtomicLong lastCollectionKey;
    private AtomicLong lastPListKey;
    private final DispatchQueue dispatchQueue;
    private final AtomicLong asyncCapacityRemaining;
    private long uowEnqueueDelayReqested;
    private long uowEnqueueNodelayReqested;
    private long uowClosedCounter;
    private long uowCanceledCounter;
    private long uowStoringCounter;
    private long uowStoredCounter;
    private final TimeMetric uow_complete_latency;
    private ConcurrentHashMap<MessageId, HashSet<DelayableUOW.MessageAction>> pendingStores;
    private HashMap<UowManagerConstants.QueueEntryKey, DelayableUOW.MessageAction> cancelable_enqueue_actions;
    private final AtomicInteger lastUowId;
    private ActiveMQMessageAuditNoSync producerSequenceIdTracker;
    private final LinkedHashMap<Object, DelayableUOW> flush_queue;
    private final CustomDispatchSource<Integer, Integer> flushSource;
    private boolean started;
    
    public LevelDBStore parent() {
        return this.parent;
    }
    
    public AtomicLong lastCollectionKey() {
        return this.lastCollectionKey;
    }
    
    public void lastCollectionKey_$eq(final AtomicLong x$1) {
        this.lastCollectionKey = x$1;
    }
    
    public AtomicLong lastPListKey() {
        return this.lastPListKey;
    }
    
    public void lastPListKey_$eq(final AtomicLong x$1) {
        this.lastPListKey = x$1;
    }
    
    public LevelDBClient client() {
        return this.parent().client();
    }
    
    public ExecutorService writeExecutor() {
        return this.client().writeExecutor();
    }
    
    public int flushDelay() {
        return this.parent().flushDelay();
    }
    
    public DispatchQueue dispatchQueue() {
        return this.dispatchQueue;
    }
    
    public AtomicLong asyncCapacityRemaining() {
        return this.asyncCapacityRemaining;
    }
    
    public DelayableUOW createUow() {
        return new DelayableUOW(this);
    }
    
    public long uowEnqueueDelayReqested() {
        return this.uowEnqueueDelayReqested;
    }
    
    public void uowEnqueueDelayReqested_$eq(final long x$1) {
        this.uowEnqueueDelayReqested = x$1;
    }
    
    public long uowEnqueueNodelayReqested() {
        return this.uowEnqueueNodelayReqested;
    }
    
    public void uowEnqueueNodelayReqested_$eq(final long x$1) {
        this.uowEnqueueNodelayReqested = x$1;
    }
    
    public long uowClosedCounter() {
        return this.uowClosedCounter;
    }
    
    public void uowClosedCounter_$eq(final long x$1) {
        this.uowClosedCounter = x$1;
    }
    
    public long uowCanceledCounter() {
        return this.uowCanceledCounter;
    }
    
    public void uowCanceledCounter_$eq(final long x$1) {
        this.uowCanceledCounter = x$1;
    }
    
    public long uowStoringCounter() {
        return this.uowStoringCounter;
    }
    
    public void uowStoringCounter_$eq(final long x$1) {
        this.uowStoringCounter = x$1;
    }
    
    public long uowStoredCounter() {
        return this.uowStoredCounter;
    }
    
    public void uowStoredCounter_$eq(final long x$1) {
        this.uowStoredCounter = x$1;
    }
    
    public TimeMetric uow_complete_latency() {
        return this.uow_complete_latency;
    }
    
    public ConcurrentHashMap<MessageId, HashSet<DelayableUOW.MessageAction>> pendingStores() {
        return this.pendingStores;
    }
    
    public void pendingStores_$eq(final ConcurrentHashMap<MessageId, HashSet<DelayableUOW.MessageAction>> x$1) {
        this.pendingStores = x$1;
    }
    
    public HashMap<UowManagerConstants.QueueEntryKey, DelayableUOW.MessageAction> cancelable_enqueue_actions() {
        return this.cancelable_enqueue_actions;
    }
    
    public void cancelable_enqueue_actions_$eq(final HashMap<UowManagerConstants.QueueEntryKey, DelayableUOW.MessageAction> x$1) {
        this.cancelable_enqueue_actions = x$1;
    }
    
    public AtomicInteger lastUowId() {
        return this.lastUowId;
    }
    
    public ActiveMQMessageAuditNoSync producerSequenceIdTracker() {
        return this.producerSequenceIdTracker;
    }
    
    public void producerSequenceIdTracker_$eq(final ActiveMQMessageAuditNoSync x$1) {
        this.producerSequenceIdTracker = x$1;
    }
    
    public long getLastProducerSequenceId(final ProducerId id) {
        return BoxesRunTime.unboxToLong(package$.MODULE$.DispatchQueueWrapper(this.dispatchQueue()).sync((Function0)new DBManager$$anonfun$getLastProducerSequenceId.DBManager$$anonfun$getLastProducerSequenceId$1(this, id)));
    }
    
    public void processClosed(final DelayableUOW uow) {
        this.dispatchQueue().assertExecuting();
        this.uowClosedCounter_$eq(this.uowClosedCounter() + 1L);
        if (uow.state().stage() < UowDelayed$.MODULE$.stage()) {
            uow.state_$eq(UowDelayed$.MODULE$);
        }
        if (uow.state().stage() < UowFlushing$.MODULE$.stage()) {
            uow.actions().foreach((Function1)new DBManager$$anonfun$processClosed.DBManager$$anonfun$processClosed$1(this, uow));
        }
        if (!uow.canceled() && uow.state().stage() < UowFlushQueued$.MODULE$.stage()) {
            if (uow.delayable()) {
                final WeakReference ref = new WeakReference((T)uow);
                this.scheduleFlush(ref);
            }
            else {
                this.enqueueFlush(uow);
            }
        }
    }
    
    private void scheduleFlush(final WeakReference<DelayableUOW> ref) {
        this.dispatchQueue().executeAfter((long)this.flushDelay(), TimeUnit.MILLISECONDS, package$.MODULE$.$up((Function0)new DBManager$$anonfun$scheduleFlush.DBManager$$anonfun$scheduleFlush$1(this, (WeakReference)ref)));
    }
    
    public LinkedHashMap<Object, DelayableUOW> flush_queue() {
        return this.flush_queue;
    }
    
    public void enqueueFlush(final DelayableUOW uow) {
        this.dispatchQueue().assertExecuting();
        if (uow != null && !uow.canceled() && uow.state().stage() < UowFlushQueued$.MODULE$.stage()) {
            uow.state_$eq(UowFlushQueued$.MODULE$);
            this.flush_queue().put(BoxesRunTime.boxToLong((long)uow.uowId()), uow);
            this.flushSource().merge((Object)Predef$.MODULE$.int2Integer(1));
        }
    }
    
    public CustomDispatchSource<Integer, Integer> flushSource() {
        return this.flushSource;
    }
    
    public void drainFlushes() {
        this.dispatchQueue().assertExecuting();
        final DelayableUOW[] values = (DelayableUOW[])JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.flush_queue().values()).toSeq().toArray(ClassTag$.MODULE$.apply((Class)DelayableUOW.class));
        this.flush_queue().clear();
        final DelayableUOW[] uows = (DelayableUOW[])Predef$.MODULE$.refArrayOps((Object[])values).flatMap((Function1)new DBManager$$anonfun.DBManager$$anonfun$2(this), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply((Class)DelayableUOW.class)));
        if (!Predef$.MODULE$.refArrayOps((Object[])uows).isEmpty()) {
            this.uowStoringCounter_$eq(this.uowStoringCounter() + Predef$.MODULE$.refArrayOps((Object[])uows).size());
            this.flushSource().suspend();
            package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).apply((Function0)new DBManager$$anonfun$drainFlushes.DBManager$$anonfun$drainFlushes$1(this, uows));
        }
    }
    
    public boolean started() {
        return this.started;
    }
    
    public void started_$eq(final boolean x$1) {
        this.started = x$1;
    }
    
    public boolean snappyCompressLogs() {
        return this.parent().snappyCompressLogs();
    }
    
    public void start() {
        this.asyncCapacityRemaining().set(this.parent().asyncBufferSize());
        this.client().start();
        package$.MODULE$.DispatchQueueWrapper(this.dispatchQueue()).sync((Function0)new DBManager$$anonfun$start.DBManager$$anonfun$start$1(this));
    }
    
    public void stop() {
        package$.MODULE$.DispatchQueueWrapper(this.dispatchQueue()).sync((Function0)new DBManager$$anonfun$stop.DBManager$$anonfun$stop$1(this));
        this.client().stop();
    }
    
    public void pollGc() {
        package$.MODULE$.DispatchQueueWrapper(this.dispatchQueue()).after(10L, TimeUnit.SECONDS, (Function0)new DBManager$$anonfun$pollGc.DBManager$$anonfun$pollGc$1(this));
    }
    
    public void monitorStats() {
        package$.MODULE$.DispatchQueueWrapper(this.dispatchQueue()).after(1L, TimeUnit.SECONDS, (Function0)new DBManager$$anonfun$monitorStats.DBManager$$anonfun$monitorStats$1(this));
    }
    
    public void checkpoint(final boolean sync) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$checkpoint.DBManager$$anonfun$checkpoint$1(this, sync));
    }
    
    public void purge() {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$purge.DBManager$$anonfun$purge$1(this));
    }
    
    public long getLastQueueEntrySeq(final long key) {
        return this.client().getLastQueueEntrySeq(key);
    }
    
    public void collectionEmpty(final long key) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$collectionEmpty.DBManager$$anonfun$collectionEmpty$1(this, key));
    }
    
    public long collectionSize(final long key) {
        return this.client().collectionSize(key);
    }
    
    public boolean collectionIsEmpty(final long key) {
        return this.client().collectionIsEmpty(key);
    }
    
    public long cursorMessages(final java.util.HashSet<MessageId> preparedAcks, final long key, final MessageRecoveryListener listener, final long startPos, final long max) {
        final ObjectRef lastmsgid = ObjectRef.create((Object)null);
        final LongRef count = LongRef.create(0L);
        this.client().queueCursor(key, startPos, (Function1<Message, Object>)new DBManager$$anonfun$cursorMessages.DBManager$$anonfun$cursorMessages$1(this, (java.util.HashSet)preparedAcks, listener, max, lastmsgid, count));
        return (lastmsgid.elem == null) ? startPos : (((EntryLocator)((MessageId)lastmsgid.elem).getEntryLocator()).seq() + 1L);
    }
    
    public long cursorMessages$default$5() {
        return Long.MAX_VALUE;
    }
    
    public Tuple2<ListBuffer<Message>, ListBuffer<XaAckRecord>> getXAActions(final long key) {
        final ListBuffer msgs = (ListBuffer)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
        final ListBuffer acks = (ListBuffer)ListBuffer$.MODULE$.apply((Seq)Nil$.MODULE$);
        this.client().transactionCursor(key, (Function1<Object, Object>)new DBManager$$anonfun$getXAActions.DBManager$$anonfun$getXAActions$1(this, msgs, acks));
        return (Tuple2<ListBuffer<Message>, ListBuffer<XaAckRecord>>)new Tuple2((Object)msgs, (Object)acks);
    }
    
    public long queuePosition(final MessageId id) {
        return ((EntryLocator)id.getEntryLocator()).seq();
    }
    
    public LevelDBStore.LevelDBMessageStore createQueueStore(final ActiveMQQueue dest) {
        return this.parent().createQueueMessageStore(dest, this.createCollection(Buffer.utf8(dest.getQualifiedName()), UowManagerConstants$.MODULE$.QUEUE_COLLECTION_TYPE()));
    }
    
    public void destroyQueueStore(final long key) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$destroyQueueStore.DBManager$$anonfun$destroyQueueStore$1(this, key));
    }
    
    public long getLogAppendPosition() {
        return BoxesRunTime.unboxToLong(package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$getLogAppendPosition.DBManager$$anonfun$getLogAppendPosition$1(this)));
    }
    
    public DurableSubscription addSubscription(final long topic_key, final SubscriptionInfo info) {
        final SubscriptionRecord.Bean record = new SubscriptionRecord.Bean();
        record.setTopicKey(topic_key);
        record.setClientId(info.getClientId());
        record.setSubscriptionName(info.getSubscriptionName());
        if (info.getSelector() == null) {
            final BoxedUnit unit = BoxedUnit.UNIT;
        }
        else {
            record.setSelector(info.getSelector());
        }
        if (info.getDestination() == null) {
            final BoxedUnit unit2 = BoxedUnit.UNIT;
        }
        else {
            record.setDestinationName(info.getDestination().getQualifiedName());
        }
        final CollectionRecord.Bean collection = new CollectionRecord.Bean();
        collection.setType(UowManagerConstants$.MODULE$.SUBSCRIPTION_COLLECTION_TYPE());
        collection.setKey(this.lastCollectionKey().incrementAndGet());
        collection.setMeta(record.freeze().toUnframedBuffer());
        final CollectionRecord.Buffer buffer = collection.freeze();
        buffer.toFramedBuffer();
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$addSubscription.DBManager$$anonfun$addSubscription$1(this, buffer));
        return new DurableSubscription(collection.getKey(), topic_key, info);
    }
    
    public void removeSubscription(final DurableSubscription sub) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$removeSubscription.DBManager$$anonfun$removeSubscription$1(this, sub));
    }
    
    public LevelDBStore.LevelDBTopicMessageStore createTopicStore(final ActiveMQTopic dest) {
        final long key = this.createCollection(Buffer.utf8(dest.getQualifiedName()), UowManagerConstants$.MODULE$.TOPIC_COLLECTION_TYPE());
        return this.parent().createTopicMessageStore(dest, key);
    }
    
    public long createCollection(final Buffer name, final int collectionType) {
        final CollectionRecord.Bean collection = new CollectionRecord.Bean();
        collection.setType(collectionType);
        collection.setMeta(name);
        collection.setKey(this.lastCollectionKey().incrementAndGet());
        final CollectionRecord.Buffer buffer = collection.freeze();
        buffer.toFramedBuffer();
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$createCollection.DBManager$$anonfun$createCollection$1(this, buffer));
        return collection.getKey();
    }
    
    public Buffer buffer(final ByteSequence packet) {
        return new Buffer(packet.data, packet.offset, packet.length);
    }
    
    public long createTransactionContainer(final XATransactionId id) {
        return this.createCollection(this.buffer(this.parent().wireFormat().marshal(id)), UowManagerConstants$.MODULE$.TRANSACTION_COLLECTION_TYPE());
    }
    
    public void removeTransactionContainer(final long key) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$removeTransactionContainer.DBManager$$anonfun$removeTransactionContainer$1(this, key));
    }
    
    public void loadCollections() {
        final Seq collections = (Seq)package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun.DBManager$$anonfun$3(this));
        final LongRef last = LongRef.create(0L);
        collections.foreach((Function1)new DBManager$$anonfun$loadCollections.DBManager$$anonfun$loadCollections$1(this, last));
        this.lastCollectionKey().set(last.elem);
    }
    
    public LevelDBStore.LevelDBPList createPList(final String name) {
        return this.parent().createPList(name, this.lastPListKey().incrementAndGet());
    }
    
    public void destroyPList(final long key) {
        package$.MODULE$.ExecutorWrapper((Executor)this.writeExecutor()).sync((Function0)new DBManager$$anonfun$destroyPList.DBManager$$anonfun$destroyPList$1(this, key));
    }
    
    public void plistPut(final byte[] key, final byte[] value) {
        this.client().plistPut(key, value);
    }
    
    public Option<byte[]> plistGet(final byte[] key) {
        return this.client().plistGet(key);
    }
    
    public void plistDelete(final byte[] key) {
        this.client().plistDelete(key);
    }
    
    public DBIterator plistIterator() {
        return this.client().plistIterator();
    }
    
    public Message getMessage(final MessageId x) {
        final MessageId id = (MessageId)Option$.MODULE$.apply((Object)this.pendingStores().get(x)).flatMap((Function1)new DBManager$$anonfun.DBManager$$anonfun$4(this)).map((Function1)new DBManager$$anonfun.DBManager$$anonfun$5(this)).getOrElse((Function0)new DBManager$$anonfun.DBManager$$anonfun$6(this, x));
        final Object locator = id.getDataLocator();
        final Message msg = this.client().getMessage(locator);
        if (msg == null) {
            LevelDBStore$.MODULE$.warn((Function0<String>)new DBManager$$anonfun$getMessage.DBManager$$anonfun$getMessage$1(this, x, locator), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
        }
        else {
            msg.setMessageId(id);
        }
        return msg;
    }
    
    public DBManager(final LevelDBStore parent) {
        this.parent = parent;
        this.lastCollectionKey = new AtomicLong(0L);
        this.lastPListKey = new AtomicLong(0L);
        this.dispatchQueue = package$.MODULE$.createQueue(this.toString());
        this.asyncCapacityRemaining = new AtomicLong(0L);
        this.uowEnqueueDelayReqested = 0L;
        this.uowEnqueueNodelayReqested = 0L;
        this.uowClosedCounter = 0L;
        this.uowCanceledCounter = 0L;
        this.uowStoringCounter = 0L;
        this.uowStoredCounter = 0L;
        this.uow_complete_latency = new TimeMetric();
        this.pendingStores = new ConcurrentHashMap<MessageId, HashSet<DelayableUOW.MessageAction>>();
        this.cancelable_enqueue_actions = new HashMap<UowManagerConstants.QueueEntryKey, DelayableUOW.MessageAction>();
        this.lastUowId = new AtomicInteger(1);
        this.producerSequenceIdTracker = new ActiveMQMessageAuditNoSync();
        this.flush_queue = new LinkedHashMap<Object, DelayableUOW>();
        this.flushSource = (CustomDispatchSource<Integer, Integer>)package$.MODULE$.createSource(EventAggregators.INTEGER_ADD, this.dispatchQueue());
        this.flushSource().setEventHandler(package$.MODULE$.$up((Function0)new DBManager$$anonfun.DBManager$$anonfun$1(this)));
        this.flushSource().resume();
        this.started = false;
    }
}
