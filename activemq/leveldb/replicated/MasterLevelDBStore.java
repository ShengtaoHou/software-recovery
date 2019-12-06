// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.replicated.dto.LogWrite;
import org.fusesource.hawtbuf.Buffer;
import scala.None$;
import org.apache.activemq.leveldb.replicated.dto.SyncResponse;
import scala.Option$;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import scala.Option;
import org.fusesource.hawtdispatch.transport.Transport;
import org.apache.activemq.leveldb.util.FileSupport$;
import org.fusesource.hawtbuf.AsciiBuffer;
import scala.MatchError;
import org.apache.activemq.leveldb.replicated.dto.WalAck;
import org.apache.activemq.leveldb.replicated.dto.Transfer;
import scala.runtime.BoxedUnit;
import org.apache.activemq.leveldb.util.JsonCodec$;
import java.io.IOException;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.apache.activemq.leveldb.replicated.dto.Login;
import org.apache.activemq.leveldb.LevelDBClient;
import org.apache.activemq.leveldb.replicated.dto.LogDelete;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.net.InetSocketAddress;
import org.fusesource.hawtdispatch.transport.TransportServerListener;
import org.fusesource.hawtdispatch.package$;
import org.fusesource.hawtdispatch.transport.TcpTransportServer;
import java.net.URI;
import org.apache.activemq.util.ServiceStopper;
import scala.runtime.BoxesRunTime;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.runtime.IntRef;
import scala.collection.Iterable$;
import java.util.Collection;
import scala.collection.JavaConversions$;
import scala.collection.Iterable;
import scala.Function1;
import scala.reflect.ClassTag$;
import scala.Array$;
import scala.Predef$;
import org.slf4j.Logger;
import scala.collection.Seq;
import scala.Function0;
import java.util.concurrent.CountDownLatch;
import org.fusesource.hawtdispatch.transport.TransportServer;
import java.util.concurrent.ConcurrentHashMap;
import scala.reflect.ScalaSignature;
import org.apache.activemq.leveldb.LevelDBStore;

@ScalaSignature(bytes = "\u0006\u0001\u0011Ut!B\u0001\u0003\u0011\u0003i\u0011AE'bgR,'\u000fT3wK2$%i\u0015;pe\u0016T!a\u0001\u0003\u0002\u0015I,\u0007\u000f\\5dCR,GM\u0003\u0002\u0006\r\u00059A.\u001a<fY\u0012\u0014'BA\u0004\t\u0003!\t7\r^5wK6\f(BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u000f\u001f5\t!AB\u0003\u0011\u0005!\u0005\u0011C\u0001\nNCN$XM\u001d'fm\u0016dGIQ*u_J,7cA\b\u00131A\u00111CF\u0007\u0002))\tQ#A\u0003tG\u0006d\u0017-\u0003\u0002\u0018)\t1\u0011I\\=SK\u001a\u0004\"!\u0007\u000f\u000e\u0003iQ!a\u0007\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003;i\u00111\u0001T8h\u0011\u0015yr\u0002\"\u0001!\u0003\u0019a\u0014N\\5u}Q\tQ\u0002C\u0004#\u001f\t\u0007I\u0011A\u0012\u0002\u0019MKfjQ0U\u001f~#\u0015jU&\u0016\u0003\u0011\u0002\"aE\u0013\n\u0005\u0019\"\"aA%oi\"1\u0001f\u0004Q\u0001\n\u0011\nQbU-O\u0007~#vj\u0018#J'.\u0003\u0003b\u0002\u0016\u0010\u0005\u0004%\taI\u0001\u000f'fs5i\u0018+P?J+Uj\u0014+F\u0011\u0019as\u0002)A\u0005I\u0005y1+\u0017(D?R{uLU#N\u001fR+\u0005\u0005C\u0004/\u001f\t\u0007I\u0011A\u0012\u0002+MKfjQ0U\u001f~\u0013V)T(U\u000b~kU)T(S3\"1\u0001g\u0004Q\u0001\n\u0011\nacU-O\u0007~#vj\u0018*F\u001b>#ViX'F\u001b>\u0013\u0016\f\t\u0005\be=\u0011\r\u0011\"\u0001$\u0003M\u0019\u0016LT\"`)>{&+R'P)\u0016{F)S*L\u0011\u0019!t\u0002)A\u0005I\u0005!2+\u0017(D?R{uLU#N\u001fR+u\fR%T\u0017\u00022A\u0001\u0005\u0002\u0001mM\u0019QgN\u001e\u0011\u0005aJT\"\u0001\u0003\n\u0005i\"!\u0001\u0004'fm\u0016dGIQ*u_J,\u0007C\u0001\b=\u0013\ti$AA\u000eSKBd\u0017nY1uK\u0012dUM^3m\t\n\u001bFo\u001c:f)J\f\u0017\u000e\u001e\u0005\u0006?U\"\ta\u0010\u000b\u0002\u0001B\u0011a\"\u000e\u0005\b\u0005V\u0002\r\u0011\"\u0001D\u0003\u0011\u0011\u0017N\u001c3\u0016\u0003\u0011\u0003\"!\u0012&\u000e\u0003\u0019S!a\u0012%\u0002\t1\fgn\u001a\u0006\u0002\u0013\u0006!!.\u0019<b\u0013\tYeI\u0001\u0004TiJLgn\u001a\u0005\b\u001bV\u0002\r\u0011\"\u0001O\u0003!\u0011\u0017N\u001c3`I\u0015\fHCA(S!\t\u0019\u0002+\u0003\u0002R)\t!QK\\5u\u0011\u001d\u0019F*!AA\u0002\u0011\u000b1\u0001\u001f\u00132\u0011\u0019)V\u0007)Q\u0005\t\u0006)!-\u001b8eA!\u0012Ak\u0016\t\u00031nk\u0011!\u0017\u0006\u00035R\tQAY3b]NL!\u0001X-\u0003\u0019\t+\u0017M\u001c)s_B,'\u000f^=\t\u000by+D\u0011A0\u0002\u000f\u001d,GOQ5oIR\tA\tC\u0003bk\u0011\u0005!-A\u0004tKR\u0014\u0015N\u001c3\u0015\u0005=\u001b\u0007bB*a\u0003\u0003\u0005\r\u0001\u0012\u0005\bKV\u0002\r\u0011\"\u0001$\u0003!\u0011X\r\u001d7jG\u0006\u001c\bbB46\u0001\u0004%\t\u0001[\u0001\re\u0016\u0004H.[2bg~#S-\u001d\u000b\u0003\u001f&Dqa\u00154\u0002\u0002\u0003\u0007A\u0005\u0003\u0004lk\u0001\u0006K\u0001J\u0001\ne\u0016\u0004H.[2bg\u0002B#A[,\t\u000b9,D\u0011A8\u0002\u0017\u001d,GOU3qY&\u001c\u0017m\u001d\u000b\u0002I!)\u0011/\u000eC\u0001e\u0006Y1/\u001a;SKBd\u0017nY1t)\ty5\u000fC\u0004Ta\u0006\u0005\t\u0019\u0001\u0013\t\u000bU,D\u0011A\u0012\u0002\u00195Lgn\u00157bm\u0016\f5m[:\t\u000f],\u0004\u0019!C\u0001\u0007\u00069ql]=oGR{\u0007bB=6\u0001\u0004%\tA_\u0001\f?NLhn\u0019+p?\u0012*\u0017\u000f\u0006\u0002Pw\"91\u000b_A\u0001\u0002\u0004!\u0005BB?6A\u0003&A)\u0001\u0005`gft7\rV8!\u0011\u001dyX\u00071A\u0005\u0002\r\n!b]=oGR{W*Y:l\u0011%\t\u0019!\u000ea\u0001\n\u0003\t)!\u0001\bts:\u001cGk\\'bg.|F%Z9\u0015\u0007=\u000b9\u0001\u0003\u0005T\u0003\u0003\t\t\u00111\u0001%\u0011\u001d\tY!\u000eQ!\n\u0011\n1b]=oGR{W*Y:lA!1\u0011qB\u001b\u0005\u0002\r\u000baa]=oGR{\u0007fAA\u0007/\"9\u0011QC\u001b\u0005\u0002\u0005]\u0011AC:z]\u000e$vn\u0018\u0013fcR\u0019q*!\u0007\t\u0011\u0005m\u00111\u0003a\u0001\u0003;\tQA^1mk\u0016\u0004B!a\b\u0002&9\u00191#!\t\n\u0007\u0005\rB#\u0001\u0004Qe\u0016$WMZ\u0005\u0004\u0017\u0006\u001d\"bAA\u0012)!\u001a\u00111C,\t\u0013\u00055RG1A\u0005\u0002\u0005=\u0012AB:mCZ,7/\u0006\u0002\u00022AA\u00111GA\u001e\u0003;\ty$\u0004\u0002\u00026)!\u0011qGA\u001d\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u00037!KA!!\u0010\u00026\t\t2i\u001c8dkJ\u0014XM\u001c;ICNDW*\u00199\u0011\t\u0005\u0005\u00131I\u0007\u0002k\u00191\u0011QI\u001b\u0001\u0003\u000f\u0012!b\u00157bm\u0016\u001cF/\u0019;f'\r\t\u0019E\u0005\u0005\f\u0003\u0017\n\u0019E!b\u0001\n\u0003\ti%\u0001\u0005tY\u00064XmX5e+\t\ti\u0002C\u0006\u0002R\u0005\r#\u0011!Q\u0001\n\u0005u\u0011!C:mCZ,w,\u001b3!\u0011\u001dy\u00121\tC\u0001\u0003+\"B!a\u0010\u0002X!A\u00111JA*\u0001\u0004\ti\u0002\u0003\u0006\u0002\\\u0005\r\u0003\u0019!C\u0001\u0003;\nQ\u0002[3mI~\u001bh.\u00199tQ>$XCAA0!\u0015\u0019\u0012\u0011MA3\u0013\r\t\u0019\u0007\u0006\u0002\u0007\u001fB$\u0018n\u001c8\u0011\u0007M\t9'C\u0002\u0002jQ\u0011A\u0001T8oO\"Q\u0011QNA\"\u0001\u0004%\t!a\u001c\u0002#!,G\u000eZ0t]\u0006\u00048\u000f[8u?\u0012*\u0017\u000fF\u0002P\u0003cB\u0011bUA6\u0003\u0003\u0005\r!a\u0018\t\u0013\u0005U\u00141\tQ!\n\u0005}\u0013A\u00045fY\u0012|6O\\1qg\"|G\u000f\t\u0005\r\u0003s\n\u0019\u00051AA\u0002\u0013\u0005\u00111P\u0001\bg\u0016\u001c8/[8o+\t\ti\b\u0005\u0003\u0002B\u0005}dABAAk\u0001\t\u0019IA\u0004TKN\u001c\u0018n\u001c8\u0014\t\u0005}\u0014Q\u0011\t\u0004\u001d\u0005\u001d\u0015bAAE\u0005\t\u0001BK]1ogB|'\u000f\u001e%b]\u0012dWM\u001d\u0005\u000e\u0003\u001b\u000byH!A!\u0002\u0013\ty)!)\u0002\u0013Q\u0014\u0018M\\:q_J$\b\u0003BAI\u0003;k!!a%\u000b\t\u00055\u0015Q\u0013\u0006\u0005\u0003/\u000bI*\u0001\u0007iC^$H-[:qCR\u001c\u0007NC\u0002\u0002\u001c*\t!BZ;tKN|WO]2f\u0013\u0011\ty*a%\u0003\u0013Q\u0013\u0018M\\:q_J$\u0018\u0002BAG\u0003\u000fCqaHA@\t\u0003\t)\u000b\u0006\u0003\u0002~\u0005\u001d\u0006\u0002CAG\u0003G\u0003\r!a$\t\u0019\u0005-\u0016q\u0010a\u0001\u0002\u0004%\t!!,\u0002\u000b1|w-\u001b8\u0016\u0005\u0005=\u0006\u0003BAY\u0003ok!!a-\u000b\u0007\u0005U&!A\u0002ei>LA!!/\u00024\n)Aj\\4j]\"a\u0011QXA@\u0001\u0004\u0005\r\u0011\"\u0001\u0002@\u0006IAn\\4j]~#S-\u001d\u000b\u0004\u001f\u0006\u0005\u0007\"C*\u0002<\u0006\u0005\t\u0019AAX\u0011%\t)-a !B\u0013\ty+\u0001\u0004m_\u001eLg\u000e\t\u0005\r\u0003\u0013\fy\b1AA\u0002\u0013\u0005\u00111Z\u0001\fg2\fg/Z0ti\u0006$X-\u0006\u0002\u0002@!a\u0011qZA@\u0001\u0004\u0005\r\u0011\"\u0001\u0002R\u0006y1\u000f\\1wK~\u001bH/\u0019;f?\u0012*\u0017\u000fF\u0002P\u0003'D\u0011bUAg\u0003\u0003\u0005\r!a\u0010\t\u0013\u0005]\u0017q\u0010Q!\n\u0005}\u0012\u0001D:mCZ,wl\u001d;bi\u0016\u0004\u0003BCAn\u0003\u007f\u0002\r\u0011\"\u0001\u0002^\u0006aA-[:d_:tWm\u0019;fIV\u0011\u0011q\u001c\t\u0004'\u0005\u0005\u0018bAAr)\t9!i\\8mK\u0006t\u0007BCAt\u0003\u007f\u0002\r\u0011\"\u0001\u0002j\u0006\u0001B-[:d_:tWm\u0019;fI~#S-\u001d\u000b\u0004\u001f\u0006-\b\"C*\u0002f\u0006\u0005\t\u0019AAp\u0011%\ty/a !B\u0013\ty.A\u0007eSN\u001cwN\u001c8fGR,G\r\t\u0005\t\u0003g\fy\b\"\u0001\u0002v\u0006)\u0011/^3vKV\u0011\u0011q\u001f\t\u0005\u0003s\fY0\u0004\u0002\u0002\u0016&!\u0011Q`AK\u00055!\u0015n\u001d9bi\u000eD\u0017+^3vK\"A!\u0011AA@\t\u0003\u0012\u0019!\u0001\np]R\u0013\u0018M\\:q_J$h)Y5mkJ,GcA(\u0003\u0006!A!qAA\u0000\u0001\u0004\u0011I!A\u0003feJ|'\u000f\u0005\u0003\u0003\f\tEQB\u0001B\u0007\u0015\r\u0011y\u0001S\u0001\u0003S>LAAa\u0005\u0003\u000e\tY\u0011jT#yG\u0016\u0004H/[8o\u0011!\u00119\"a \u0005\u0002\te\u0011AE8o)J\fgn\u001d9peR\u001cu.\\7b]\u0012$2a\u0014B\u000e\u0011!\u0011iB!\u0006A\u0002\t}\u0011aB2p[6\fg\u000e\u001a\t\u0004'\t\u0005\u0012b\u0001B\u0012)\t\u0019\u0011I\\=\t\u0011\t\u001d\u0012q\u0010C\u0001\u0005S\tA\u0002[1oI2,w\f\\8hS:$2a\u0014B\u0016\u0011!\u0011iC!\nA\u0002\u0005=\u0016a\u0002:fcV,7\u000f\u001e\u0005\t\u0005c\ty\b\"\u0011\u00034\u00059rN\u001c+sC:\u001c\bo\u001c:u\t&\u001c8m\u001c8oK\u000e$X\r\u001a\u000b\u0002\u001f\"A!qGA@\t\u0003\u0011\u0019$A\tiC:$G.Z0eSN\u001cwN\u001c8fGRD\u0001Ba\u000f\u0002\u0000\u0011\u0005!1G\u0001\fQ\u0006tG\r\\3`gft7\r\u0003\u0005\u0003@\u0005}D\u0011\u0001B!\u0003)A\u0017M\u001c3mK~\u000b7m\u001b\u000b\u0004\u001f\n\r\u0003\u0002\u0003B#\u0005{\u0001\rAa\u0012\u0002\u0007I,\u0017\u000f\u0005\u0003\u00022\n%\u0013\u0002\u0002B&\u0003g\u0013aaV1m\u0003\u000e\\\u0007\u0002\u0003B(\u0003\u007f\"\tA!\u0015\u0002\u0015!\fg\u000e\u001a7f?\u001e,G\u000fF\u0002P\u0005'B\u0001B!\u0012\u0003N\u0001\u0007!Q\u000b\t\u0005\u0003c\u00139&\u0003\u0003\u0003Z\u0005M&\u0001\u0003+sC:\u001ch-\u001a:\t\u0019\tu\u00131\ta\u0001\u0002\u0004%\tAa\u0018\u0002\u0017M,7o]5p]~#S-\u001d\u000b\u0004\u001f\n\u0005\u0004\"C*\u0003\\\u0005\u0005\t\u0019AA?\u0011%\u0011)'a\u0011!B\u0013\ti(\u0001\u0005tKN\u001c\u0018n\u001c8!\u0011)\u0011I'a\u0011A\u0002\u0013\u0005!1N\u0001\ta>\u001c\u0018\u000e^5p]V\u0011!Q\u000e\t\u0005\u0005_\u0012)(\u0004\u0002\u0003r)!!1OA\u001b\u0003\u0019\tGo\\7jG&!!q\u000fB9\u0005)\tEo\\7jG2{gn\u001a\u0005\u000b\u0005w\n\u0019\u00051A\u0005\u0002\tu\u0014\u0001\u00049pg&$\u0018n\u001c8`I\u0015\fHcA(\u0003\u0000!I1K!\u001f\u0002\u0002\u0003\u0007!Q\u000e\u0005\n\u0005\u0007\u000b\u0019\u0005)Q\u0005\u0005[\n\u0011\u0002]8tSRLwN\u001c\u0011\t\u0015\t\u001d\u00151\ta\u0001\n\u0003\u0011I)\u0001\u0005dCV<\u0007\u000e^+q+\t\u0011Y\t\u0005\u0003\u0003p\t5\u0015\u0002\u0002BH\u0005c\u0012Q\"\u0011;p[&\u001c'i\\8mK\u0006t\u0007B\u0003BJ\u0003\u0007\u0002\r\u0011\"\u0001\u0003\u0016\u0006a1-Y;hQR,\u0006o\u0018\u0013fcR\u0019qJa&\t\u0013M\u0013\t*!AA\u0002\t-\u0005\"\u0003BN\u0003\u0007\u0002\u000b\u0015\u0002BF\u0003%\u0019\u0017-^4iiV\u0003\b\u0005\u0003\u0007\u0003 \u0006\r\u0003\u0019!a\u0001\n\u0003\u0011\t+A\u0007t_\u000e\\W\r^!eIJ,7o]\u000b\u0003\u0005G\u0003BA!*\u0003,6\u0011!q\u0015\u0006\u0004\u0005SC\u0015a\u00018fi&!!Q\u0016BT\u00055\u0019vnY6fi\u0006#GM]3tg\"a!\u0011WA\"\u0001\u0004\u0005\r\u0011\"\u0001\u00034\u0006\t2o\\2lKR\fE\r\u001a:fgN|F%Z9\u0015\u0007=\u0013)\fC\u0005T\u0005_\u000b\t\u00111\u0001\u0003$\"I!\u0011XA\"A\u0003&!1U\u0001\u000fg>\u001c7.\u001a;BI\u0012\u0014Xm]:!\u0011!\u0011i,a\u0011\u0005\u0002\t}\u0016!B:uCJ$HcA(\u0003B\"A\u0011\u0011\u0010B^\u0001\u0004\ti\b\u0003\u0005\u0003F\u0006\rC\u0011\u0001Bd\u0003\u0011\u0019Ho\u001c9\u0015\t\u0005}'\u0011\u001a\u0005\t\u0003s\u0012\u0019\r1\u0001\u0002~!A\u00111_A\"\t\u0003\u0011i\rF\u0002P\u0005\u001fD\u0001B!5\u0003L\u0002\u0007!1[\u0001\u0005MVt7\r\u0005\u0004\u0014\u0005+\fihT\u0005\u0004\u0005/$\"!\u0003$v]\u000e$\u0018n\u001c82\u0011!\u0011Y.a\u0011\u0005\u0002\tu\u0017!\u0003:fa2L7-\u0019;f)\ry%q\u001c\u0005\t\u00037\u0011I\u000e1\u0001\u0003bB!\u0011\u0011\u0017Br\u0013\u0011\u0011)/a-\u0003\u00131{w\rR3mKR,\u0007B\u0003Bu\u0003\u0007\u0002\r\u0011\"\u0001\u0003l\u0006YRO\u001c4mkNDW\rZ0sKBd\u0017nY1uS>twL\u001a:b[\u0016,\"A!<\u0011\t\t=(\u0011_\u0007\u0003\u0003\u00072qAa=\u0002D\u0001\u0011)P\u0001\rEK\u001a,'O]3e%\u0016\u0004H.[2bi&|gN\u0012:b[\u0016\u001cBA!=\u0003xB\u0019aB!?\n\u0007\tm(A\u0001\tSKBd\u0017nY1uS>tgI]1nK\"Y!q By\u0005\u0003\u0005\u000b\u0011BB\u0001\u0003\u00111\u0017\u000e\\3\u0011\t\t-11A\u0005\u0005\u0007\u000b\u0011iA\u0001\u0003GS2,\u0007b\u0003B5\u0005c\u0014)\u0019!C\u0001\u0007\u0013)\"!!\u001a\t\u0017\t\r%\u0011\u001fB\u0001B\u0003%\u0011Q\r\u0005\f\u0007\u001f\u0011\tP!A!\u0002\u0013\t)'A\u0004`_\u001a47/\u001a;\t\u0017\rM!\u0011\u001fB\u0001B\u0003%\u0011QM\u0001\u000eS:LG/[1m\u0019\u0016tw\r\u001e5\t\u000f}\u0011\t\u0010\"\u0001\u0004\u0018QQ!Q^B\r\u00077\u0019iba\b\t\u0011\t}8Q\u0003a\u0001\u0007\u0003A\u0001B!\u001b\u0004\u0016\u0001\u0007\u0011Q\r\u0005\t\u0007\u001f\u0019)\u00021\u0001\u0002f!A11CB\u000b\u0001\u0004\t)\u0007\u0003\u0006\u0004$\tE(\u0019!C\u0001\u0007K\t\u0011CZ5mKR\u0013\u0018M\\:gKJ4%/Y7f+\t\u00199\u0003E\u0002\u000f\u0007SI1aa\u000b\u0003\u0005E1\u0015\u000e\\3Ue\u0006t7OZ3s\rJ\fW.\u001a\u0005\n\u0007_\u0011\t\u0010)A\u0005\u0007O\t!CZ5mKR\u0013\u0018M\\:gKJ4%/Y7fA!Q11\u0007By\u0001\u0004%\ta!\u000e\u0002\u000f\u0015t7m\u001c3fIV\u00111q\u0007\t\u0005\u0007s\u0019y$\u0004\u0002\u0004<)!1QHAM\u0003\u001dA\u0017m\u001e;ck\u001aLAa!\u0011\u0004<\t1!)\u001e4gKJD!b!\u0012\u0003r\u0002\u0007I\u0011AB$\u0003-)gnY8eK\u0012|F%Z9\u0015\u0007=\u001bI\u0005C\u0005T\u0007\u0007\n\t\u00111\u0001\u00048!I1Q\nByA\u0003&1qG\u0001\tK:\u001cw\u000eZ3eA!A1\u0011\u000bBy\t\u0003\u0019I!\u0001\u0004pM\u001a\u001cX\r\u001e\u0005\t\u0007+\u0012\t\u0010\"\u0001\u0004\n\u00051A.\u001a8hi\"D\u0001b!\u0017\u0003r\u0012\u00053QG\u0001\u0005E>$\u0017\u0010\u0003\u0006\u0004^\u0005\r\u0003\u0019!C\u0001\u0007?\nq$\u001e8gYV\u001c\b.\u001a3`e\u0016\u0004H.[2bi&|gn\u00184sC6,w\fJ3r)\ry5\u0011\r\u0005\n'\u000em\u0013\u0011!a\u0001\u0005[D\u0011b!\u001a\u0002D\u0001\u0006KA!<\u00029Utg\r\\;tQ\u0016$wL]3qY&\u001c\u0017\r^5p]~3'/Y7fA!A!1\\A\"\t\u0003\u0019I\u0007F\u0005P\u0007W\u001aiga\u001c\u0004r!A!q`B4\u0001\u0004\u0019\t\u0001\u0003\u0005\u0003j\r\u001d\u0004\u0019AA3\u0011!\u0019\tfa\u001aA\u0002\u0005\u0015\u0004\u0002CB+\u0007O\u0002\r!!\u001a\t\u0011\rU\u00141\tC\u0001\u0007o\nq\u0002]8tSRLwN\\0va\u0012\fG/\u001a\u000b\u0004\u001f\u000ee\u0004\u0002\u0003B5\u0007g\u0002\r!!\u001a\t\u0015\ru\u00141\ta\u0001\n\u0003\u0019y(\u0001\nmCN$x\f]8tSRLwN\\0ts:\u001cWCABA!\rq11Q\u0005\u0004\u0007\u000b\u0013!\u0001\u0004)pg&$\u0018n\u001c8Ts:\u001c\u0007BCBE\u0003\u0007\u0002\r\u0011\"\u0001\u0004\f\u00061B.Y:u?B|7/\u001b;j_:|6/\u001f8d?\u0012*\u0017\u000fF\u0002P\u0007\u001bC\u0011bUBD\u0003\u0003\u0005\ra!!\t\u0013\rE\u00151\tQ!\n\r\u0005\u0015a\u00057bgR|\u0006o\\:ji&|gnX:z]\u000e\u0004\u0003\u0006BBH\u0007+\u00032aEBL\u0013\r\u0019I\n\u0006\u0002\tm>d\u0017\r^5mK\"A1QTA\"\t\u0003\u0019y*A\ndQ\u0016\u001c7n\u00189pg&$\u0018n\u001c8`gft7-F\u0001P\u0011!\u0019\u0019+a\u0011\u0005\u0002\u0005u\u0017AC5t\u0007\u0006,x\r\u001b;Va\"A1qUA\"\t\u0003\u0019I+\u0001\u0004ti\u0006$Xo]\u000b\u0003\u0007W\u00032ADBW\u0013\r\u0019yK\u0001\u0002\f'2\fg/Z*uCR,8\u000f\u0003\u0005\u00044V\u0002\u000b\u0011BA\u0019\u0003\u001d\u0019H.\u0019<fg\u0002Bqaa.6\t\u0003\u0019I,A\u0007tY\u00064Xm]0ti\u0006$Xo]\u000b\u0003\u0007w\u0003ba!0\u0004D\u000e-VBAB`\u0015\r\u0019\t\rF\u0001\u000bG>dG.Z2uS>t\u0017\u0002BBc\u0007\u007f\u0013\u0001\"\u0013;fe\u0006\u0014G.\u001a\u0005\u0007\u0007O+D\u0011A\"\t\u000f\r-W\u0007\"\u0011\u00034\u00059Am\\*uCJ$\bbBBhk\u0011\u00053\u0011[\u0001\u0007I>\u001cFo\u001c9\u0015\u0007=\u001b\u0019\u000e\u0003\u0005\u0004V\u000e5\u0007\u0019ABl\u0003\u001d\u0019Ho\u001c9qKJ\u0004Ba!7\u0004^6\u001111\u001c\u0006\u00037\u0019IAaa8\u0004\\\nq1+\u001a:wS\u000e,7\u000b^8qa\u0016\u0014\bbBBrk\u0011\u00053Q]\u0001\rGJ,\u0017\r^3DY&,g\u000e^\u000b\u0003\u0007O\u00042ADBu\u0013\r\u0019YO\u0001\u0002\u0014\u001b\u0006\u001cH/\u001a:MKZ,G\u000e\u0012\"DY&,g\u000e\u001e\u0005\b\u0007_,D\u0011ABs\u00035i\u0017m\u001d;fe~\u001bG.[3oi\"Y11_\u001bA\u0002\u0003\u0007I\u0011AB{\u0003A!(/\u00198ta>\u0014HoX:feZ,'/\u0006\u0002\u0004xB!\u0011\u0011SB}\u0013\u0011\u0019Y0a%\u0003\u001fQ\u0013\u0018M\\:q_J$8+\u001a:wKJD1ba@6\u0001\u0004\u0005\r\u0011\"\u0001\u0005\u0002\u0005!BO]1ogB|'\u000f^0tKJ4XM]0%KF$2a\u0014C\u0002\u0011%\u00196Q`A\u0001\u0002\u0004\u00199\u0010\u0003\u0005\u0005\bU\u0002\u000b\u0015BB|\u0003E!(/\u00198ta>\u0014HoX:feZ,'\u000f\t\u0005\n\t\u0017)$\u0019!C\u0001\t\u001b\t1b\u001d;beR|F.\u0019;dQV\u0011Aq\u0002\t\u0005\u0003g!\t\"\u0003\u0003\u0005\u0014\u0005U\"AD\"pk:$Hi\\<o\u0019\u0006$8\r\u001b\u0005\t\t/)\u0004\u0015!\u0003\u0005\u0010\u0005a1\u000f^1si~c\u0017\r^2iA!9A1D\u001b\u0005\u0002\r}\u0015!F:uCJ$x\f\u001d:pi>\u001cw\u000e\\0tKJ4XM\u001d\u0005\u0007\t?)D\u0011A\u0012\u0002\u000f\u001d,G\u000fU8si\"9A1E\u001b\u0005\u0002\r}\u0015\u0001F:u_B|\u0006O]8u_\u000e|GnX:feZ,'\u000fC\u0005\u0005(U\u0002\r\u0011\"\u0001\u0004\u0000\u0005i\u0001o\\:ji&|gnX:z]\u000eD\u0011\u0002b\u000b6\u0001\u0004%\t\u0001\"\f\u0002#A|7/\u001b;j_:|6/\u001f8d?\u0012*\u0017\u000fF\u0002P\t_A\u0011b\u0015C\u0015\u0003\u0003\u0005\ra!!\t\u0011\u0011MR\u0007)Q\u0005\u0007\u0003\u000ba\u0002]8tSRLwN\\0ts:\u001c\u0007\u0005\u000b\u0003\u00052\rU\u0005b\u0002C\u001dk\u0011\u0005A1H\u0001\fo\u0006dwl]=oG~#x\u000eF\u0002P\t{A\u0001B!\u001b\u00058\u0001\u0007\u0011Q\r\u0005\b\t\u0003*D\u0011AAo\u0003MI7o\u0015;paB,Gm\u0014:Ti>\u0004\b/\u001b8h\u0011\u001d!)%\u000eC\u0001\u0007\u0013\tA\u0001Z1uK\"9A\u0011J\u001b\u0005\u0002\u0011-\u0013!\u0004:fa2L7-\u0019;f?^\fG\u000eF\u0005P\t\u001b\"y\u0005\"\u0015\u0005T!A!q C$\u0001\u0004\u0019\t\u0001\u0003\u0005\u0003j\u0011\u001d\u0003\u0019AA3\u0011!\u0019\t\u0006b\u0012A\u0002\u0005\u0015\u0004\u0002CB+\t\u000f\u0002\r!!\u001a\t\u000f\u0011]S\u0007\"\u0001\u0005Z\u0005!\"/\u001a9mS\u000e\fG/Z0m_\u001e|F-\u001a7fi\u0016$2a\u0014C.\u0011!!i\u0006\"\u0016A\u0002\u0005\u0015\u0014a\u00017pO\"9A\u0011M\u001b\u0005\u0002\r%\u0011aE<bY~\u000b\u0007\u000f]3oI~\u0003xn]5uS>t\u0007\"\u0003C3k\u0001\u0007I\u0011AB\u0005\u0003!9\u0018\r\\0eCR,\u0007\"\u0003C5k\u0001\u0007I\u0011\u0001C6\u000319\u0018\r\\0eCR,w\fJ3r)\ryEQ\u000e\u0005\n'\u0012\u001d\u0014\u0011!a\u0001\u0003KB\u0001\u0002\"\u001d6A\u0003&\u0011QM\u0001\no\u0006dw\fZ1uK\u0002BC\u0001b\u001c\u0004\u0016\u0002")
public class MasterLevelDBStore extends LevelDBStore implements ReplicatedLevelDBStoreTrait
{
    private String bind;
    private int replicas;
    private String _syncTo;
    private int syncToMask;
    private final ConcurrentHashMap<String, SlaveState> slaves;
    private TransportServer transport_server;
    private final CountDownLatch start_latch;
    private volatile PositionSync position_sync;
    private volatile long wal_date;
    private String securityToken;
    
    public static void trace(final Throwable e) {
        MasterLevelDBStore$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        MasterLevelDBStore$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        MasterLevelDBStore$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        MasterLevelDBStore$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        MasterLevelDBStore$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        MasterLevelDBStore$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        MasterLevelDBStore$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static Logger log() {
        return MasterLevelDBStore$.MODULE$.log();
    }
    
    public static int SYNC_TO_REMOTE_DISK() {
        return MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE_DISK();
    }
    
    public static int SYNC_TO_REMOTE_MEMORY() {
        return MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE_MEMORY();
    }
    
    public static int SYNC_TO_REMOTE() {
        return MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE();
    }
    
    public static int SYNC_TO_DISK() {
        return MasterLevelDBStore$.MODULE$.SYNC_TO_DISK();
    }
    
    @Override
    public String securityToken() {
        return this.securityToken;
    }
    
    @Override
    public void securityToken_$eq(final String x$1) {
        this.securityToken = x$1;
    }
    
    @Override
    public String getSecurityToken() {
        return ReplicatedLevelDBStoreTrait$class.getSecurityToken(this);
    }
    
    @Override
    public void setSecurityToken(final String x$1) {
        ReplicatedLevelDBStoreTrait$class.setSecurityToken(this, x$1);
    }
    
    @Override
    public String node_id() {
        return ReplicatedLevelDBStoreTrait$class.node_id(this);
    }
    
    @Override
    public String storeId() {
        return ReplicatedLevelDBStoreTrait$class.storeId(this);
    }
    
    @Override
    public void storeId_$eq(final String value) {
        ReplicatedLevelDBStoreTrait$class.storeId_$eq(this, value);
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
    
    public int replicas() {
        return this.replicas;
    }
    
    public void replicas_$eq(final int x$1) {
        this.replicas = x$1;
    }
    
    public void setReplicas(final int x$1) {
        this.replicas = x$1;
    }
    
    public int minSlaveAcks() {
        return this.replicas() / 2;
    }
    
    public String _syncTo() {
        return this._syncTo;
    }
    
    public void _syncTo_$eq(final String x$1) {
        this._syncTo = x$1;
    }
    
    public int syncToMask() {
        return this.syncToMask;
    }
    
    public void syncToMask_$eq(final int x$1) {
        this.syncToMask = x$1;
    }
    
    public String syncTo() {
        return this._syncTo();
    }
    
    public void syncTo_$eq(final String value) {
        this._syncTo_$eq(value);
        this.syncToMask_$eq(0);
        Predef$.MODULE$.refArrayOps((Object[])Predef$.MODULE$.refArrayOps((Object[])value.split(",")).map((Function1)new MasterLevelDBStore$$anonfun$syncTo_$eq.MasterLevelDBStore$$anonfun$syncTo_$eq$1(this), Array$.MODULE$.canBuildFrom(ClassTag$.MODULE$.apply((Class)String.class)))).foreach((Function1)new MasterLevelDBStore$$anonfun$syncTo_$eq.MasterLevelDBStore$$anonfun$syncTo_$eq$2(this));
    }
    
    public ConcurrentHashMap<String, SlaveState> slaves() {
        return this.slaves;
    }
    
    public Iterable<SlaveStatus> slaves_status() {
        return (Iterable<SlaveStatus>)JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.slaves().values()).map((Function1)new MasterLevelDBStore$$anonfun$slaves_status.MasterLevelDBStore$$anonfun$slaves_status$1(this), Iterable$.MODULE$.canBuildFrom());
    }
    
    public String status() {
        final IntRef caughtUpCounter = IntRef.create(0);
        final IntRef notCaughtUpCounter = IntRef.create(0);
        JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.slaves().values()).foreach((Function1)new MasterLevelDBStore$$anonfun$status.MasterLevelDBStore$$anonfun$status$1(this, caughtUpCounter, notCaughtUpCounter));
        String rc = "";
        if (notCaughtUpCounter.elem > 0) {
            rc = new StringBuilder().append((Object)rc).append((Object)new StringOps(Predef$.MODULE$.augmentString("%d slave nodes attaching. ")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToInteger(notCaughtUpCounter.elem) }))).toString();
        }
        if (caughtUpCounter.elem > 0) {
            rc = new StringBuilder().append((Object)rc).append((Object)new StringOps(Predef$.MODULE$.augmentString("%d slave nodes attached. ")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToInteger(caughtUpCounter.elem) }))).toString();
        }
        return rc;
    }
    
    @Override
    public void doStart() {
        ReplicationSupport$.MODULE$.unstash(this.directory());
        super.doStart();
        this.start_protocol_server();
        this.wal_sync_to(this.wal_append_position());
    }
    
    @Override
    public void doStop(final ServiceStopper stopper) {
        if (this.transport_server() != null) {
            this.stop_protocol_server();
            this.transport_server_$eq(null);
        }
        super.doStop(stopper);
    }
    
    @Override
    public MasterLevelDBClient createClient() {
        return new MasterLevelDBClient(this);
    }
    
    public MasterLevelDBClient master_client() {
        return (MasterLevelDBClient)this.client();
    }
    
    public TransportServer transport_server() {
        return this.transport_server;
    }
    
    public void transport_server_$eq(final TransportServer x$1) {
        this.transport_server = x$1;
    }
    
    public CountDownLatch start_latch() {
        return this.start_latch;
    }
    
    public void start_protocol_server() {
        this.transport_server_$eq((TransportServer)new TcpTransportServer(new URI(this.bind())));
        this.transport_server().setBlockingExecutor(this.blocking_executor());
        this.transport_server().setDispatchQueue(package$.MODULE$.createQueue(new StringBuilder().append((Object)"master: ").append((Object)this.node_id()).toString()));
        this.transport_server().setTransportServerListener((TransportServerListener)new MasterLevelDBStore$$anon.MasterLevelDBStore$$anon$1(this));
        this.transport_server().start(package$.MODULE$.$up((Function0)new MasterLevelDBStore$$anonfun$start_protocol_server.MasterLevelDBStore$$anonfun$start_protocol_server$1(this)));
        this.start_latch().await();
    }
    
    public int getPort() {
        this.start_latch().await();
        return ((InetSocketAddress)this.transport_server().getSocketAddress()).getPort();
    }
    
    public void stop_protocol_server() {
        this.transport_server().stop(package$.MODULE$.NOOP());
    }
    
    public PositionSync position_sync() {
        return this.position_sync;
    }
    
    public void position_sync_$eq(final PositionSync x$1) {
        this.position_sync = x$1;
    }
    
    public void wal_sync_to(final long position) {
        if (this.minSlaveAcks() < 1 || (this.syncToMask() & MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE()) == 0x0) {
            return;
        }
        if (this.isStoppedOrStopping()) {
            throw new IllegalStateException("Store replication stopped");
        }
        final PositionSync position_sync = new PositionSync(position, this.minSlaveAcks());
        this.position_sync_$eq(position_sync);
        JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.slaves().values()).foreach((Function1)new MasterLevelDBStore$$anonfun$wal_sync_to.MasterLevelDBStore$$anonfun$wal_sync_to$1(this));
        while (!position_sync.await(1L, TimeUnit.SECONDS)) {
            if (this.isStoppedOrStopping()) {
                throw new IllegalStateException("Store replication stopped");
            }
            MasterLevelDBStore$.MODULE$.warn((Function0<String>)new MasterLevelDBStore$$anonfun$wal_sync_to.MasterLevelDBStore$$anonfun$wal_sync_to$2(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToInteger(this.minSlaveAcks()), BoxesRunTime.boxToLong(position), this.status() }));
        }
    }
    
    public boolean isStoppedOrStopping() {
        return this.isStopped() || this.isStopping() || (this.broker_service() != null && this.broker_service().isStopping());
    }
    
    public long date() {
        return System.currentTimeMillis();
    }
    
    public void replicate_wal(final File file, final long position, final long offset, final long length) {
        if (length > 0L) {
            JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.slaves().values()).foreach((Function1)new MasterLevelDBStore$$anonfun$replicate_wal.MasterLevelDBStore$$anonfun$replicate_wal$1(this, file, position, offset, length));
        }
    }
    
    public void replicate_log_delete(final long log) {
        final LogDelete value = new LogDelete();
        value.log = log;
        JavaConversions$.MODULE$.collectionAsScalaIterable((Collection)this.slaves().values()).foreach((Function1)new MasterLevelDBStore$$anonfun$replicate_log_delete.MasterLevelDBStore$$anonfun$replicate_log_delete$1(this, value));
    }
    
    public long wal_append_position() {
        return this.client().wal_append_position();
    }
    
    public long wal_date() {
        return this.wal_date;
    }
    
    public void wal_date_$eq(final long x$1) {
        this.wal_date = x$1;
    }
    
    public String getBind() {
        return this.bind();
    }
    
    public int getReplicas() {
        return this.replicas();
    }
    
    public MasterLevelDBStore() {
        ReplicatedLevelDBStoreTrait$class.$init$(this);
        this.bind = "tcp://0.0.0.0:61619";
        this.replicas = 3;
        this._syncTo = "quorum_mem";
        this.syncToMask = MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE_MEMORY();
        this.slaves = new ConcurrentHashMap<String, SlaveState>();
        this.start_latch = new CountDownLatch(1);
        this.position_sync = new PositionSync(0L, 0);
        this.wal_date = 0L;
    }
    
    public class Session extends TransportHandler
    {
        private Login login;
        private SlaveState slave_state;
        private boolean disconnected;
        
        public Login login() {
            return this.login;
        }
        
        public void login_$eq(final Login x$1) {
            this.login = x$1;
        }
        
        public SlaveState slave_state() {
            return this.slave_state;
        }
        
        public void slave_state_$eq(final SlaveState x$1) {
            this.slave_state = x$1;
        }
        
        public boolean disconnected() {
            return this.disconnected;
        }
        
        public void disconnected_$eq(final boolean x$1) {
            this.disconnected = x$1;
        }
        
        public DispatchQueue queue() {
            return super.transport().getDispatchQueue();
        }
        
        @Override
        public void onTransportFailure(final IOException error) {
            if (!this.disconnected()) {
                MasterLevelDBStore$.MODULE$.warn((Function0<String>)new MasterLevelDBStore$Session$$anonfun$onTransportFailure.MasterLevelDBStore$Session$$anonfun$onTransportFailure$1(this, error), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            }
            super.onTransportFailure(error);
        }
        
        public void onTransportCommand(final Object command) {
            if (command instanceof ReplicationFrame) {
                final ReplicationFrame replicationFrame = (ReplicationFrame)command;
                final AsciiBuffer action = replicationFrame.action();
                final AsciiBuffer login_ACTION = ReplicationSupport$.MODULE$.LOGIN_ACTION();
                final AsciiBuffer obj = action;
                Label_0311: {
                    Label_0078: {
                        if (login_ACTION == null) {
                            if (obj != null) {
                                break Label_0078;
                            }
                        }
                        else if (!login_ACTION.equals(obj)) {
                            break Label_0078;
                        }
                        this.handle_login(JsonCodec$.MODULE$.decode(replicationFrame.body(), Login.class));
                        final BoxedUnit unit = BoxedUnit.UNIT;
                        break Label_0311;
                    }
                    final AsciiBuffer sync_ACTION = ReplicationSupport$.MODULE$.SYNC_ACTION();
                    final AsciiBuffer obj2 = action;
                    Label_0121: {
                        if (sync_ACTION == null) {
                            if (obj2 != null) {
                                break Label_0121;
                            }
                        }
                        else if (!sync_ACTION.equals(obj2)) {
                            break Label_0121;
                        }
                        this.handle_sync();
                        final BoxedUnit unit2 = BoxedUnit.UNIT;
                        break Label_0311;
                    }
                    final AsciiBuffer get_ACTION = ReplicationSupport$.MODULE$.GET_ACTION();
                    final AsciiBuffer obj3 = action;
                    Label_0179: {
                        if (get_ACTION == null) {
                            if (obj3 != null) {
                                break Label_0179;
                            }
                        }
                        else if (!get_ACTION.equals(obj3)) {
                            break Label_0179;
                        }
                        this.handle_get(JsonCodec$.MODULE$.decode(replicationFrame.body(), Transfer.class));
                        final BoxedUnit unit3 = BoxedUnit.UNIT;
                        break Label_0311;
                    }
                    final AsciiBuffer ack_ACTION = ReplicationSupport$.MODULE$.ACK_ACTION();
                    final AsciiBuffer obj4 = action;
                    Label_0237: {
                        if (ack_ACTION == null) {
                            if (obj4 != null) {
                                break Label_0237;
                            }
                        }
                        else if (!ack_ACTION.equals(obj4)) {
                            break Label_0237;
                        }
                        this.handle_ack(JsonCodec$.MODULE$.decode(replicationFrame.body(), WalAck.class));
                        final BoxedUnit unit4 = BoxedUnit.UNIT;
                        break Label_0311;
                    }
                    final AsciiBuffer disconnect_ACTION = ReplicationSupport$.MODULE$.DISCONNECT_ACTION();
                    final AsciiBuffer obj5 = action;
                    Label_0280: {
                        if (disconnect_ACTION == null) {
                            if (obj5 != null) {
                                break Label_0280;
                            }
                        }
                        else if (!disconnect_ACTION.equals(obj5)) {
                            break Label_0280;
                        }
                        this.handle_disconnect();
                        final BoxedUnit unit5 = BoxedUnit.UNIT;
                        break Label_0311;
                    }
                    this.sendError(new StringBuilder().append((Object)"Unknown frame action: ").append((Object)replicationFrame.action()).toString());
                    final BoxedUnit unit6 = BoxedUnit.UNIT;
                }
                final BoxedUnit unit7 = BoxedUnit.UNIT;
                return;
            }
            throw new MatchError(command);
        }
        
        public void handle_login(final Login request) {
            final String security_token = request.security_token;
            final String securityToken = this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().securityToken();
            Label_0044: {
                if (security_token == null) {
                    if (securityToken != null) {
                        break Label_0044;
                    }
                }
                else if (!security_token.equals(securityToken)) {
                    break Label_0044;
                }
                this.login_$eq(request);
                this.sendOk(null);
                return;
            }
            this.sendError("Invalid security_token");
        }
        
        @Override
        public void onTransportDisconnected() {
            final SlaveState slave_state = this.slave_state();
            if (slave_state != null) {
                this.slave_state_$eq(null);
                if (slave_state.stop(this) && this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().isStarted()) {
                    this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().slaves().remove(slave_state.slave_id(), slave_state);
                }
            }
        }
        
        public void handle_disconnect() {
            this.disconnected_$eq(true);
            this.sendOk(null);
        }
        
        public void handle_sync() {
            if (this.login() == null) {
                this.sendError("Not logged in");
                return;
            }
            MasterLevelDBStore$.MODULE$.debug((Function0<String>)new MasterLevelDBStore$Session$$anonfun$handle_sync.MasterLevelDBStore$Session$$anonfun$handle_sync$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            this.slave_state_$eq(this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().slaves().get(this.login().node_id));
            if (this.slave_state() == null) {
                this.slave_state_$eq(this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().new SlaveState(this.login().node_id));
                this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().slaves().put(this.login().node_id, this.slave_state());
            }
            else {
                final BoxedUnit unit = BoxedUnit.UNIT;
            }
            this.slave_state().start(this);
        }
        
        public void handle_ack(final WalAck req) {
            if (this.login() == null || this.slave_state() == null) {
                return;
            }
            MasterLevelDBStore$.MODULE$.trace((Function0<String>)new MasterLevelDBStore$Session$$anonfun$handle_ack.MasterLevelDBStore$Session$$anonfun$handle_ack$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[] { this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().directory(), BoxesRunTime.boxToLong(req.position), this.slave_state().slave_id() }));
            this.slave_state().position_update(req.position);
        }
        
        public void handle_get(final Transfer req) {
            if (this.login() == null) {
                this.sendError("Not logged in");
                return;
            }
            final File file = req.file.startsWith("log/") ? FileSupport$.MODULE$.toRichFile(this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().client().logDirectory()).$div(new StringOps(Predef$.MODULE$.augmentString(req.file)).stripPrefix("log/")) : FileSupport$.MODULE$.toRichFile(this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer().client().directory()).$div(req.file);
            if (!file.exists()) {
                this.sendError("file does not exist");
                return;
            }
            final long length = file.length();
            if (req.offset > length) {
                this.sendError("Invalid offset");
                return;
            }
            if (req.offset + req.length > length) {
                this.sendError("Invalid length");
            }
            this.sendOk(null);
            this.send(new FileTransferFrame(file, req.offset, req.length));
        }
        
        public /* synthetic */ MasterLevelDBStore org$apache$activemq$leveldb$replicated$MasterLevelDBStore$Session$$$outer() {
            return MasterLevelDBStore.this;
        }
        
        public Session(final Transport transport) {
            if (MasterLevelDBStore.this == null) {
                throw null;
            }
            super(transport);
            this.disconnected = false;
        }
    }
    
    public class SlaveState
    {
        private final String slave_id;
        private Option<Object> held_snapshot;
        private Session session;
        private AtomicLong position;
        private AtomicBoolean caughtUp;
        private SocketAddress socketAddress;
        private DeferredReplicationFrame unflushed_replication_frame;
        private volatile PositionSync last_position_sync;
        
        public String slave_id() {
            return this.slave_id;
        }
        
        public Option<Object> held_snapshot() {
            return this.held_snapshot;
        }
        
        public void held_snapshot_$eq(final Option<Object> x$1) {
            this.held_snapshot = x$1;
        }
        
        public Session session() {
            return this.session;
        }
        
        public void session_$eq(final Session x$1) {
            this.session = x$1;
        }
        
        public AtomicLong position() {
            return this.position;
        }
        
        public void position_$eq(final AtomicLong x$1) {
            this.position = x$1;
        }
        
        public AtomicBoolean caughtUp() {
            return this.caughtUp;
        }
        
        public void caughtUp_$eq(final AtomicBoolean x$1) {
            this.caughtUp = x$1;
        }
        
        public SocketAddress socketAddress() {
            return this.socketAddress;
        }
        
        public void socketAddress_$eq(final SocketAddress x$1) {
            this.socketAddress = x$1;
        }
        
        public void start(final Session session) {
            MasterLevelDBStore$.MODULE$.debug((Function0<String>)new MasterLevelDBStore$SlaveState$$anonfun$start.MasterLevelDBStore$SlaveState$$anonfun$start$2(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            this.socketAddress_$eq(session.transport().getRemoteAddress());
            session.queue().setLabel(new StringBuilder().append((Object)this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().transport_server().getDispatchQueue().getLabel()).append((Object)" -> ").append((Object)this.slave_id()).toString());
            synchronized (this) {
                if (this.session() != null) {
                    this.session().transport().stop(package$.MODULE$.NOOP());
                }
                this.session_$eq(session);
                final long snapshot_id = this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().client().lastIndexSnapshotPos();
                this.held_snapshot_$eq((Option<Object>)Option$.MODULE$.apply((Object)BoxesRunTime.boxToLong(snapshot_id)));
                this.position().set(0L);
                final SyncResponse snapshot_state = this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().master_client().snapshot_state(snapshot_id);
                // monitorexit(this)
                final SyncResponse resp = snapshot_state;
                MasterLevelDBStore$.MODULE$.info((Function0<String>)new MasterLevelDBStore$SlaveState$$anonfun$start.MasterLevelDBStore$SlaveState$$anonfun$start$3(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                package$.MODULE$.DispatchQueueWrapper(session.queue()).apply((Function0)new MasterLevelDBStore$SlaveState$$anonfun$start.MasterLevelDBStore$SlaveState$$anonfun$start$1(this, session, resp));
            }
        }
        
        public synchronized boolean stop(final Session session) {
            final Session session2 = this.session();
            if (session2 == null) {
                if (session != null) {
                    return false;
                }
            }
            else if (!session2.equals(session)) {
                return false;
            }
            MasterLevelDBStore$.MODULE$.info((Function0<String>)new MasterLevelDBStore$SlaveState$$anonfun$stop.MasterLevelDBStore$SlaveState$$anonfun$stop$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            return true;
            b = false;
            return b;
        }
        
        public void queue(final Function1<Session, BoxedUnit> func) {
            synchronized (this) {
                final Session session = this.session();
                // monitorexit(this)
                final Session h = session;
                if (h != null) {
                    package$.MODULE$.DispatchQueueWrapper(h.queue()).apply((Function0)new MasterLevelDBStore$SlaveState$$anonfun$queue.MasterLevelDBStore$SlaveState$$anonfun$queue$1(this, (Function1)func));
                }
            }
        }
        
        public void replicate(final LogDelete value) {
            final ReplicationFrame frame = new ReplicationFrame(ReplicationSupport$.MODULE$.LOG_DELETE_ACTION(), JsonCodec$.MODULE$.encode(value));
            this.queue((Function1<Session, BoxedUnit>)new MasterLevelDBStore$SlaveState$$anonfun$replicate.MasterLevelDBStore$SlaveState$$anonfun$replicate$1(this, frame));
        }
        
        public DeferredReplicationFrame unflushed_replication_frame() {
            return this.unflushed_replication_frame;
        }
        
        public void unflushed_replication_frame_$eq(final DeferredReplicationFrame x$1) {
            this.unflushed_replication_frame = x$1;
        }
        
        public void replicate(final File file, final long position, final long offset, final long length) {
            this.queue((Function1<Session, BoxedUnit>)new MasterLevelDBStore$SlaveState$$anonfun$replicate.MasterLevelDBStore$SlaveState$$anonfun$replicate$2(this, file, position, offset, length));
        }
        
        public void position_update(final long position) {
            this.position().getAndSet(position);
            this.check_position_sync();
        }
        
        public PositionSync last_position_sync() {
            return this.last_position_sync;
        }
        
        public void last_position_sync_$eq(final PositionSync x$1) {
            this.last_position_sync = x$1;
        }
        
        public void check_position_sync() {
            final PositionSync p = this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().position_sync();
            final PositionSync last_position_sync = this.last_position_sync();
            final PositionSync obj = p;
            if (last_position_sync == null) {
                if (obj == null) {
                    return;
                }
            }
            else if (last_position_sync.equals(obj)) {
                return;
            }
            if (this.position().get() < p.position()) {
                return;
            }
            Label_0107: {
                if (!this.caughtUp().compareAndSet(false, true)) {
                    break Label_0107;
                }
                MasterLevelDBStore$.MODULE$.info((Function0<String>)new MasterLevelDBStore$SlaveState$$anonfun$check_position_sync.MasterLevelDBStore$SlaveState$$anonfun$check_position_sync$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                synchronized (this) {
                    this.held_snapshot_$eq((Option<Object>)None$.MODULE$);
                    final BoxedUnit unit = BoxedUnit.UNIT;
                    // monitorexit(this)
                    while (true) {
                        p.countDown();
                        this.last_position_sync_$eq(p);
                        return;
                        final BoxedUnit unit2 = BoxedUnit.UNIT;
                        continue;
                    }
                }
            }
        }
        
        public boolean isCaughtUp() {
            return this.caughtUp().get();
        }
        
        public SlaveStatus status() {
            return new SlaveStatus(this.slave_id(), this.socketAddress().toString(), this.isCaughtUp(), this.position().get());
        }
        
        public /* synthetic */ MasterLevelDBStore org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer() {
            return MasterLevelDBStore.this;
        }
        
        public SlaveState(final String slave_id) {
            this.slave_id = slave_id;
            if (MasterLevelDBStore.this == null) {
                throw null;
            }
            this.held_snapshot = (Option<Object>)None$.MODULE$;
            this.position = new AtomicLong(0L);
            this.caughtUp = new AtomicBoolean(false);
            this.unflushed_replication_frame = null;
            this.last_position_sync = null;
        }
        
        public class DeferredReplicationFrame extends ReplicationFrame
        {
            private final long position;
            private final FileTransferFrame fileTransferFrame;
            private Buffer encoded;
            
            public long position() {
                return this.position;
            }
            
            public FileTransferFrame fileTransferFrame() {
                return this.fileTransferFrame;
            }
            
            public Buffer encoded() {
                return this.encoded;
            }
            
            public void encoded_$eq(final Buffer x$1) {
                this.encoded = x$1;
            }
            
            public long offset() {
                return this.fileTransferFrame().offset();
            }
            
            public long length() {
                return this.fileTransferFrame().length();
            }
            
            @Override
            public Buffer body() {
                if (this.encoded() == null) {
                    final LogWrite value = new LogWrite();
                    value.file = this.position();
                    value.offset = this.offset();
                    value.sync = ((this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$DeferredReplicationFrame$$$outer().org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().syncToMask() & MasterLevelDBStore$.MODULE$.SYNC_TO_REMOTE_DISK()) != 0x0);
                    value.length = this.fileTransferFrame().length();
                    value.date = this.org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$DeferredReplicationFrame$$$outer().org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$$$outer().date();
                    this.encoded_$eq(JsonCodec$.MODULE$.encode(value));
                }
                return this.encoded();
            }
            
            public /* synthetic */ SlaveState org$apache$activemq$leveldb$replicated$MasterLevelDBStore$SlaveState$DeferredReplicationFrame$$$outer() {
                return SlaveState.this;
            }
            
            public DeferredReplicationFrame(final File file, final long position, final long _offset, final long initialLength) {
                this.position = position;
                if (SlaveState.this == null) {
                    throw null;
                }
                super(ReplicationSupport$.MODULE$.WAL_ACTION(), null);
                this.fileTransferFrame = new FileTransferFrame(file, _offset, initialLength);
                this.encoded = null;
            }
        }
    }
}
