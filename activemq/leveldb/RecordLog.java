// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb;

import org.apache.activemq.leveldb.util.FileSupport$;
import scala.runtime.NonLocalReturnControl;
import scala.MatchError;
import scala.runtime.LongRef;
import scala.runtime.RichInt$;
import org.fusesource.hawtbuf.BufferEditor;
import java.io.EOFException;
import scala.collection.immutable.StringOps;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import scala.collection.mutable.StringBuilder;
import scala.Predef$;
import java.nio.channels.FileChannel;
import org.fusesource.hawtdispatch.BaseRetained;
import scala.runtime.AbstractFunction2;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicLong;
import scala.Some;
import scala.None$;
import scala.runtime.AbstractFunction3;
import scala.runtime.Statics;
import scala.runtime.AbstractFunction0$mcV$sp;
import scala.Product$class;
import scala.runtime.ScalaRunTime$;
import scala.collection.Iterator;
import scala.Tuple3;
import scala.reflect.ClassTag$;
import scala.collection.mutable.Iterable$;
import java.util.Map;
import scala.collection.JavaConversions$;
import scala.collection.TraversableOnce;
import java.util.zip.CRC32;
import scala.Function1;
import scala.runtime.BoxesRunTime;
import scala.Option$;
import org.fusesource.hawtbuf.Buffer;
import scala.Tuple2;
import scala.Option;
import org.slf4j.Logger;
import scala.collection.Seq;
import org.apache.activemq.util.LRUCache;
import scala.runtime.BoxedUnit;
import scala.Function0;
import org.apache.activemq.leveldb.util.TimeMetric;
import java.util.TreeMap;
import java.io.File;
import scala.reflect.ScalaSignature;
import scala.Serializable;
import scala.Product;

@ScalaSignature(bytes = "\u0006\u0001\u0015\u001dr!B\u0001\u0003\u0011\u0003Y\u0011!\u0003*fG>\u0014H\rT8h\u0015\t\u0019A!A\u0004mKZ,G\u000e\u001a2\u000b\u0005\u00151\u0011\u0001C1di&4X-\\9\u000b\u0005\u001dA\u0011AB1qC\u000eDWMC\u0001\n\u0003\ry'oZ\u0002\u0001!\taQ\"D\u0001\u0003\r\u0015q!\u0001#\u0001\u0010\u0005%\u0011VmY8sI2{wm\u0005\u0003\u000e!Ya\u0002CA\t\u0015\u001b\u0005\u0011\"\"A\n\u0002\u000bM\u001c\u0017\r\\1\n\u0005U\u0011\"AB!osJ+g\r\u0005\u0002\u001855\t\u0001D\u0003\u0002\u001a\u0005\u0005!Q\u000f^5m\u0013\tY\u0002DA\u0002M_\u001e\u0004\"!E\u000f\n\u0005y\u0011\"\u0001D*fe&\fG.\u001b>bE2,\u0007\"\u0002\u0011\u000e\t\u0003\t\u0013A\u0002\u001fj]&$h\bF\u0001\f\u0011\u001d\u0019SB1A\u0005\u0002\u0011\n\u0011\u0003T(H?\"+\u0015\tR#S?B\u0013VIR%Y+\u0005)\u0003CA\t'\u0013\t9#C\u0001\u0003CsR,\u0007BB\u0015\u000eA\u0003%Q%\u0001\nM\u001f\u001e{\u0006*R!E\u000bJ{\u0006KU#G\u0013b\u0003\u0003bB\u0016\u000e\u0005\u0004%\t\u0001J\u0001\u000f+>;v,\u0012(E?J+5i\u0014*E\u0011\u0019iS\u0002)A\u0005K\u0005yQkT,`\u000b:#uLU#D\u001fJ#\u0005\u0005C\u00040\u001b\t\u0007I\u0011\u0001\u0019\u0002\u001f1{ui\u0018%F\u0003\u0012+%kX*J5\u0016+\u0012!\r\t\u0003#IJ!a\r\n\u0003\u0007%sG\u000f\u0003\u00046\u001b\u0001\u0006I!M\u0001\u0011\u0019>;u\fS#B\t\u0016\u0013vlU%[\u000b\u0002BqaN\u0007C\u0002\u0013\u0005\u0001'A\u0006C+\u001a3UIU0T\u0013j+\u0005BB\u001d\u000eA\u0003%\u0011'\u0001\u0007C+\u001a3UIU0T\u0013j+\u0005\u0005C\u0004<\u001b\t\u0007I\u0011\u0001\u0019\u0002%\tK\u0006+Q*T?\n+fIR#S?NK%,\u0012\u0005\u0007{5\u0001\u000b\u0011B\u0019\u0002'\tK\u0006+Q*T?\n+fIR#S?NK%,\u0012\u0011\u0007\t}j\u0001\t\u0011\u0002\b\u0019><\u0017J\u001c4p'\u0011q\u0004#\u0011\u000f\u0011\u0005E\u0011\u0015BA\"\u0013\u0005\u001d\u0001&o\u001c3vGRD\u0001\"\u0012 \u0003\u0016\u0004%\tAR\u0001\u0005M&dW-F\u0001H!\tAU*D\u0001J\u0015\tQ5*\u0001\u0002j_*\tA*\u0001\u0003kCZ\f\u0017B\u0001(J\u0005\u00111\u0015\u000e\\3\t\u0011As$\u0011#Q\u0001\n\u001d\u000bQAZ5mK\u0002B\u0001B\u0015 \u0003\u0016\u0004%\taU\u0001\ta>\u001c\u0018\u000e^5p]V\tA\u000b\u0005\u0002\u0012+&\u0011aK\u0005\u0002\u0005\u0019>tw\r\u0003\u0005Y}\tE\t\u0015!\u0003U\u0003%\u0001xn]5uS>t\u0007\u0005\u0003\u0005[}\tU\r\u0011\"\u0001T\u0003\u0019aWM\\4uQ\"AAL\u0010B\tB\u0003%A+A\u0004mK:<G\u000f\u001b\u0011\t\u000b\u0001rD\u0011\u00010\u0015\t}\u000b'm\u0019\t\u0003Azj\u0011!\u0004\u0005\u0006\u000bv\u0003\ra\u0012\u0005\u0006%v\u0003\r\u0001\u0016\u0005\u00065v\u0003\r\u0001\u0016\u0005\u0006Kz\"\taU\u0001\u0006Y&l\u0017\u000e\u001e\u0005\bOz\n\t\u0011\"\u0001i\u0003\u0011\u0019w\u000e]=\u0015\t}K'n\u001b\u0005\b\u000b\u001a\u0004\n\u00111\u0001H\u0011\u001d\u0011f\r%AA\u0002QCqA\u00174\u0011\u0002\u0003\u0007A\u000bC\u0004n}E\u0005I\u0011\u00018\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\tqN\u000b\u0002Ha.\n\u0011\u000f\u0005\u0002so6\t1O\u0003\u0002uk\u0006IQO\\2iK\u000e\\W\r\u001a\u0006\u0003mJ\t!\"\u00198o_R\fG/[8o\u0013\tA8OA\tv]\u000eDWmY6fIZ\u000b'/[1oG\u0016DqA\u001f \u0012\u0002\u0013\u000510\u0001\bd_BLH\u0005Z3gCVdG\u000f\n\u001a\u0016\u0003qT#\u0001\u00169\t\u000fyt\u0014\u0013!C\u0001w\u0006q1m\u001c9zI\u0011,g-Y;mi\u0012\u001a\u0004\"CA\u0001}\u0005\u0005I\u0011IA\u0002\u00035\u0001(o\u001c3vGR\u0004&/\u001a4jqV\u0011\u0011Q\u0001\t\u0005\u0003\u000f\ti!\u0004\u0002\u0002\n)\u0019\u00111B&\u0002\t1\fgnZ\u0005\u0005\u0003\u001f\tIA\u0001\u0004TiJLgn\u001a\u0005\t\u0003'q\u0014\u0011!C\u0001a\u0005a\u0001O]8ek\u000e$\u0018I]5us\"I\u0011q\u0003 \u0002\u0002\u0013\u0005\u0011\u0011D\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\tY\"!\t\u0011\u0007E\ti\"C\u0002\u0002 I\u00111!\u00118z\u0011%\t\u0019#!\u0006\u0002\u0002\u0003\u0007\u0011'A\u0002yIEB\u0011\"a\n?\u0003\u0003%\t%!\u000b\u0002\u001fA\u0014x\u000eZ;di&#XM]1u_J,\"!a\u000b\u0011\r\u00055\u00121GA\u000e\u001b\t\tyCC\u0002\u00022I\t!bY8mY\u0016\u001cG/[8o\u0013\u0011\t)$a\f\u0003\u0011%#XM]1u_JD\u0011\"!\u000f?\u0003\u0003%\t!a\u000f\u0002\u0011\r\fg.R9vC2$B!!\u0010\u0002DA\u0019\u0011#a\u0010\n\u0007\u0005\u0005#CA\u0004C_>dW-\u00198\t\u0015\u0005\r\u0012qGA\u0001\u0002\u0004\tY\u0002C\u0005\u0002Hy\n\t\u0011\"\u0011\u0002J\u0005A\u0001.Y:i\u0007>$W\rF\u00012\u0011%\tiEPA\u0001\n\u0003\ny%\u0001\u0005u_N#(/\u001b8h)\t\t)\u0001C\u0005\u0002Ty\n\t\u0011\"\u0011\u0002V\u00051Q-];bYN$B!!\u0010\u0002X!Q\u00111EA)\u0003\u0003\u0005\r!a\u0007\b\u0013\u0005mS\"!A\t\u0002\u0005u\u0013a\u0002'pO&sgm\u001c\t\u0004A\u0006}c\u0001C \u000e\u0003\u0003E\t!!\u0019\u0014\u000b\u0005}\u00131\r\u000f\u0011\u0011\u0005\u0015\u00141N$U)~k!!a\u001a\u000b\u0007\u0005%$#A\u0004sk:$\u0018.\\3\n\t\u00055\u0014q\r\u0002\u0012\u0003\n\u001cHO]1di\u001a+hn\u0019;j_:\u001c\u0004b\u0002\u0011\u0002`\u0011\u0005\u0011\u0011\u000f\u000b\u0003\u0003;B!\"!\u0014\u0002`\u0005\u0005IQIA(\u0011)\t9(a\u0018\u0002\u0002\u0013\u0005\u0015\u0011P\u0001\u0006CB\u0004H.\u001f\u000b\b?\u0006m\u0014QPA@\u0011\u0019)\u0015Q\u000fa\u0001\u000f\"1!+!\u001eA\u0002QCaAWA;\u0001\u0004!\u0006BCAB\u0003?\n\t\u0011\"!\u0002\u0006\u00069QO\\1qa2LH\u0003BAD\u0003'\u0003R!EAE\u0003\u001bK1!a#\u0013\u0005\u0019y\u0005\u000f^5p]B1\u0011#a$H)RK1!!%\u0013\u0005\u0019!V\u000f\u001d7fg!I\u0011QSAA\u0003\u0003\u0005\raX\u0001\u0004q\u0012\u0002\u0004BCAM\u0003?\n\t\u0011\"\u0003\u0002\u001c\u0006Y!/Z1e%\u0016\u001cx\u000e\u001c<f)\t\ti\n\u0005\u0003\u0002\b\u0005}\u0015\u0002BAQ\u0003\u0013\u0011aa\u00142kK\u000e$\bbBAS\u001b\u0011\u0005\u0011qU\u0001\fK:\u001cw\u000eZ3`Y>tw\r\u0006\u0003\u0002*\u0006e\u0006\u0003BAV\u0003kk!!!,\u000b\t\u0005=\u0016\u0011W\u0001\bQ\u0006<HOY;g\u0015\r\t\u0019\fC\u0001\u000bMV\u001cXm]8ve\u000e,\u0017\u0002BA\\\u0003[\u0013aAQ;gM\u0016\u0014\bbBA^\u0003G\u0003\r\u0001V\u0001\u0003CFBq!a0\u000e\t\u0003\t\t-A\u0006eK\u000e|G-Z0m_:<Gc\u0001+\u0002D\"A\u0011QYA_\u0001\u0004\tI+A\u0003wC2,X\rC\u0005\u0002x5\t\t\u0011\"!\u0002JR1\u00111ZC\f\u000b3\u00012\u0001DAg\r\u0015q!\u0001QAh'\u0015\ti\rE!\u001d\u0011)\t\u0019.!4\u0003\u0016\u0004%\tAR\u0001\nI&\u0014Xm\u0019;pefD!\"a6\u0002N\nE\t\u0015!\u0003H\u0003)!\u0017N]3di>\u0014\u0018\u0010\t\u0005\f\u00037\fiM!f\u0001\n\u0003\ti.A\u0005m_\u001e\u001cVO\u001a4jqV\u0011\u0011q\u001c\t\u0005\u0003C\f9OD\u0002\u0012\u0003GL1!!:\u0013\u0003\u0019\u0001&/\u001a3fM&!\u0011qBAu\u0015\r\t)O\u0005\u0005\f\u0003[\fiM!E!\u0002\u0013\ty.\u0001\u0006m_\u001e\u001cVO\u001a4jq\u0002Bq\u0001IAg\t\u0003\t\t\u0010\u0006\u0004\u0002L\u0006M\u0018Q\u001f\u0005\b\u0003'\fy\u000f1\u0001H\u0011!\tY.a<A\u0002\u0005}\u0007\"CA}\u0003\u001b\u0004\r\u0011\"\u0001T\u0003\u001dawnZ*ju\u0016D!\"!@\u0002N\u0002\u0007I\u0011AA\u0000\u0003-awnZ*ju\u0016|F%Z9\u0015\t\t\u0005!q\u0001\t\u0004#\t\r\u0011b\u0001B\u0003%\t!QK\\5u\u0011%\t\u0019#a?\u0002\u0002\u0003\u0007A\u000b\u0003\u0005\u0003\f\u00055\u0007\u0015)\u0003U\u0003!awnZ*ju\u0016\u0004\u0003\u0002\u0004B\b\u0003\u001b\u0004\r\u00111A\u0005\u0002\tE\u0011\u0001E2veJ,g\u000e^0baB,g\u000eZ3s+\t\u0011\u0019\u0002\u0005\u0003\u0003\u0016\t]QBAAg\r\u001d\u0011I\"!4\u0001\u00057\u00111\u0002T8h\u0003B\u0004XM\u001c3feN!!q\u0003B\u000f!\u0011\u0011)Ba\b\u0007\u000f\t\u0005\u0012Q\u001a!\u0003$\tIAj\\4SK\u0006$WM]\n\u0007\u0005?\u0011)#\u0011\u000f\u0011\t\t\u001d\"QF\u0007\u0003\u0005SQAAa\u000b\u00022\u0006a\u0001.Y<uI&\u001c\b/\u0019;dQ&!!q\u0006B\u0015\u00051\u0011\u0015m]3SKR\f\u0017N\\3e\u0011%)%q\u0004BK\u0002\u0013\u0005a\tC\u0005Q\u0005?\u0011\t\u0012)A\u0005\u000f\"I!Ka\b\u0003\u0016\u0004%\ta\u0015\u0005\n1\n}!\u0011#Q\u0001\nQCq\u0001\tB\u0010\t\u0003\u0011Y\u0004\u0006\u0004\u0003\u001e\tu\"q\b\u0005\u0007\u000b\ne\u0002\u0019A$\t\rI\u0013I\u00041\u0001U\u0011!\u0011\u0019Ea\b\u0005\u0002\t\u0015\u0013\u0001B8qK:,\"Aa\u0012\u0011\u0007!\u0013I%C\u0002\u0003L%\u0013\u0001CU1oI>l\u0017iY2fgN4\u0015\u000e\\3\t\u0015\t=#q\u0004b\u0001\n\u0003\u0011)%\u0001\u0002gI\"I!1\u000bB\u0010A\u0003%!qI\u0001\u0004M\u0012\u0004\u0003B\u0003B,\u0005?\u0011\r\u0011\"\u0001\u0003Z\u000591\r[1o]\u0016dWC\u0001B.!\u0011\u0011iFa\u001a\u000e\u0005\t}#\u0002\u0002B1\u0005G\n\u0001b\u00195b]:,Gn\u001d\u0006\u0004\u0005KZ\u0015a\u00018j_&!!\u0011\u000eB0\u0005-1\u0015\u000e\\3DQ\u0006tg.\u001a7\t\u0013\t5$q\u0004Q\u0001\n\tm\u0013\u0001C2iC:tW\r\u001c\u0011\t\u0011\tE$q\u0004C!\u0005g\nq\u0001Z5ta>\u001cX\r\u0006\u0002\u0003\u0002!A!q\u000fB\u0010\t\u0003\u0011I(\u0001\u0005p]~\u001bGn\\:f+\t\u0011\t\u0001\u0003\u0005\u0003~\t}A\u0011\u0001B@\u0003A\u0019\u0007.Z2l?J,\u0017\rZ0gYV\u001c\b\u000e\u0006\u0003\u0003\u0002\t\u0005\u0005b\u0002BB\u0005w\u0002\r\u0001V\u0001\u000bK:$wl\u001c4gg\u0016$\b\u0002\u0003BD\u0005?!\tA!#\u0002\tI,\u0017\r\u001a\u000b\u0007\u0003S\u0013YIa$\t\u000f\t5%Q\u0011a\u0001)\u0006y!/Z2pe\u0012|\u0006o\\:ji&|g\u000e\u0003\u0004[\u0005\u000b\u0003\r!\r\u0005\t\u0005\u000f\u0013y\u0002\"\u0001\u0003\u0014R!!Q\u0013BL!\u001d\t\u0012qR\u0013\u0002*RCqA!$\u0003\u0012\u0002\u0007A\u000b\u0003\u0005\u0003\u001c\n}A\u0011\u0001BO\u0003\u0015\u0019\u0007.Z2l)\u0011\u0011yJ!+\u0011\u000bE\tII!)\u0011\rE\u0011\u0019\u000b\u0016BT\u0013\r\u0011)K\u0005\u0002\u0007)V\u0004H.\u001a\u001a\u0011\tE\tI\t\u0016\u0005\b\u0005\u001b\u0013I\n1\u0001U\u0011\u001d\u0011iKa\b\u0005\u0002M\u000bQC^3sS\u001aL\u0018I\u001c3HKR,e\u000eZ(gMN,G\u000fC\u0005h\u0005?\t\t\u0011\"\u0001\u00032R1!Q\u0004BZ\u0005kC\u0001\"\u0012BX!\u0003\u0005\ra\u0012\u0005\t%\n=\u0006\u0013!a\u0001)\"AQNa\b\u0012\u0002\u0013\u0005a\u000e\u0003\u0005{\u0005?\t\n\u0011\"\u0001|\u0011)\t\tAa\b\u0002\u0002\u0013\u0005\u00131\u0001\u0005\n\u0003'\u0011y\"!A\u0005\u0002AB!\"a\u0006\u0003 \u0005\u0005I\u0011\u0001Ba)\u0011\tYBa1\t\u0013\u0005\r\"qXA\u0001\u0002\u0004\t\u0004BCA\u0014\u0005?\t\t\u0011\"\u0011\u0002*!Q\u0011\u0011\bB\u0010\u0003\u0003%\tA!3\u0015\t\u0005u\"1\u001a\u0005\u000b\u0003G\u00119-!AA\u0002\u0005m\u0001BCA$\u0005?\t\t\u0011\"\u0011\u0002J!Q\u0011Q\nB\u0010\u0003\u0003%\t%a\u0014\t\u0015\u0005M#qDA\u0001\n\u0003\u0012\u0019\u000e\u0006\u0003\u0002>\tU\u0007BCA\u0012\u0005#\f\t\u00111\u0001\u0002\u001c!YQIa\u0006\u0003\u0002\u0003\u0006Ia\u0012B\u0019\u0011-\u0011&q\u0003B\u0001B\u0003%AK!\u000e\t\u0015\tu'q\u0003BA\u0002\u0013\u00051+A\u0007baB,g\u000eZ0pM\u001a\u001cX\r\u001e\u0005\f\u0005C\u00149B!a\u0001\n\u0003\u0011\u0019/A\tbaB,g\u000eZ0pM\u001a\u001cX\r^0%KF$BA!\u0001\u0003f\"I\u00111\u0005Bp\u0003\u0003\u0005\r\u0001\u0016\u0005\u000b\u0005S\u00149B!A!B\u0013!\u0016AD1qa\u0016tGmX8gMN,G\u000f\t\u0005\bA\t]A\u0011\u0001Bw)!\u0011\u0019Ba<\u0003r\nM\bBB#\u0003l\u0002\u0007q\t\u0003\u0004S\u0005W\u0004\r\u0001\u0016\u0005\n\u0005;\u0014Y\u000f%AA\u0002QC!Ba>\u0003\u0018\t\u0007I\u0011\u0001B}\u0003\u0011IgNZ8\u0016\u0005\tm\bc\u0001B\u007f}9\u0011A\u0002\u0001\u0005\n\u0007\u0003\u00119\u0002)A\u0005\u0005w\fQ!\u001b8g_\u0002B\u0001Ba\u0011\u0003\u0018\u0011\u0005#Q\t\u0005\t\u0005o\u00129\u0002\"\u0011\u0003z!Q1\u0011\u0002B\f\u0005\u0004%\taa\u0003\u0002\u001d\u0019dWo\u001d5fI~{gMZ:fiV\u00111Q\u0002\t\u0005\u0007\u001f\u0019Y\"\u0004\u0002\u0004\u0012)!11CB\u000b\u0003\u0019\tGo\\7jG*!1qCB\r\u0003)\u0019wN\\2veJ,g\u000e\u001e\u0006\u00033-KAa!\b\u0004\u0012\tQ\u0011\t^8nS\u000eduN\\4\t\u0013\r\u0005\"q\u0003Q\u0001\n\r5\u0011a\u00044mkNDW\rZ0pM\u001a\u001cX\r\u001e\u0011\t\u000f\r\u0015\"q\u0003C\u0001'\u0006y\u0011\r\u001d9f]\u0012|\u0006o\\:ji&|g\u000e\u0003\u0006\u0004*\t]!\u0019!C\u0001\u0007W\tAb\u001e:ji\u0016|&-\u001e4gKJ,\"a!\f\u0011\t\u0005-6qF\u0005\u0005\u0007c\tiKA\rECR\f')\u001f;f\u0003J\u0014\u0018-_(viB,Ho\u0015;sK\u0006l\u0007\"CB\u001b\u0005/\u0001\u000b\u0011BB\u0017\u000359(/\u001b;f?\n,hMZ3sA!A1\u0011\bB\f\t\u0003\u0011I(A\u0003g_J\u001cW\r\u0003\u0005\u0004>\t]A\u0011AB \u0003\u0011\u00198.\u001b9\u0015\u0007Q\u001b\t\u0005\u0003\u0004[\u0007w\u0001\r\u0001\u0016\u0005\t\u0007\u000b\u00129\u0002\"\u0001\u0004H\u00051\u0011\r\u001d9f]\u0012$ba!\u0013\u0004L\r=\u0003CB\t\u0003$R\u0013Y\u0010C\u0004\u0004N\r\r\u0003\u0019A\u0013\u0002\u0005%$\u0007\u0002CB)\u0007\u0007\u0002\r!!+\u0002\t\u0011\fG/\u0019\u0005\t\u0007+\u00129\u0002\"\u0001\u0003z\u0005)a\r\\;tQ\"A!Q\u0010B\f\t\u0003\u001aI\u0006\u0006\u0003\u0003\u0002\rm\u0003b\u0002BB\u0007/\u0002\r\u0001\u0016\u0005\r\u0007?\ni\r1AA\u0002\u0013\u00051\u0011M\u0001\u0015GV\u0014(/\u001a8u?\u0006\u0004\b/\u001a8eKJ|F%Z9\u0015\t\t\u000511\r\u0005\u000b\u0003G\u0019i&!AA\u0002\tM\u0001\"CB4\u0003\u001b\u0004\u000b\u0015\u0002B\n\u0003E\u0019WO\u001d:f]R|\u0016\r\u001d9f]\u0012,'\u000f\t\u0005\u000b\u0007W\ni\r1A\u0005\u0002\r5\u0014\u0001\u0005<fe&4\u0017pX2iK\u000e\\7/^7t+\t\ti\u0004\u0003\u0006\u0004r\u00055\u0007\u0019!C\u0001\u0007g\nAC^3sS\u001aLxl\u00195fG.\u001cX/\\:`I\u0015\fH\u0003\u0002B\u0001\u0007kB!\"a\t\u0004p\u0005\u0005\t\u0019AA\u001f\u0011%\u0019I(!4!B\u0013\ti$A\twKJLg-_0dQ\u0016\u001c7n];ng\u0002B!b! \u0002N\n\u0007I\u0011AB@\u0003%awnZ0j]\u001a|7/\u0006\u0002\u0004\u0002B911QBC)\nmXBAB\r\u0013\u0011\u00199i!\u0007\u0003\u000fQ\u0013X-Z'ba\"I11RAgA\u0003%1\u0011Q\u0001\u000bY><w,\u001b8g_N\u0004s\u0001CBH\u0003\u001bD\ta!%\u0002\u00131|wmX7vi\u0016D\b\u0003\u0002B\u000b\u0007'3\u0001b!&\u0002N\"\u00051q\u0013\u0002\nY><w,\\;uKb\u001c2aa%\u0011\u0011\u001d\u000131\u0013C\u0001\u00077#\"a!%\t\u0011\r}\u0015Q\u001aC\u0001\u0007C\u000ba\u0001Z3mKR,G\u0003\u0002B\u0001\u0007GCqa!\u0014\u0004\u001e\u0002\u0007A\u000b\u0003\u0005\u0004(\u00065G\u0011CBU\u0003!yg\u000eR3mKR,G\u0003\u0002B\u0001\u0007WCa!RBS\u0001\u0004!\u0006\u0002CBT\u0003\u001b$\tba,\u0015\t\u0005u2\u0011\u0017\u0005\u0007\u000b\u000e5\u0006\u0019A$\t\u0011\rU\u0016Q\u001aC\u0001\u0007o\u000b\u0001b\u00195fG.\u001cX/\u001c\u000b\u0004c\re\u0006\u0002CB)\u0007g\u0003\r!!+\b\u0015\ru\u0016QZA\u0001\u0012\u0003\u0019y,A\u0006M_\u001e\f\u0005\u000f]3oI\u0016\u0014\b\u0003\u0002B\u000b\u0007\u00034!B!\u0007\u0002N\u0006\u0005\t\u0012ABb'\u0011\u0019\t\r\u0005\u000f\t\u000f\u0001\u001a\t\r\"\u0001\u0004HR\u00111q\u0018\u0005\n\u0007\u0017\u001c\t-%A\u0005\u0002m\f1\u0004\n7fgNLg.\u001b;%OJ,\u0017\r^3sI\u0011,g-Y;mi\u0012\u001a\u0004BCAM\u0007\u0003\f\t\u0011\"\u0003\u0002\u001c\u001eQ1\u0011[Ag\u0003\u0003E\taa5\u0002\u00131{wMU3bI\u0016\u0014\b\u0003\u0002B\u000b\u0007+4!B!\t\u0002N\u0006\u0005\t\u0012ABl'\u0015\u0019)n!7\u001d!!\t)ga7H)\nu\u0011\u0002BBo\u0003O\u0012\u0011#\u00112tiJ\f7\r\u001e$v]\u000e$\u0018n\u001c83\u0011\u001d\u00013Q\u001bC\u0001\u0007C$\"aa5\t\u0015\u000553Q[A\u0001\n\u000b\ny\u0005\u0003\u0006\u0002x\rU\u0017\u0011!CA\u0007O$bA!\b\u0004j\u000e-\bBB#\u0004f\u0002\u0007q\t\u0003\u0004S\u0007K\u0004\r\u0001\u0016\u0005\u000b\u0003\u0007\u001b).!A\u0005\u0002\u000e=H\u0003BBy\u0007k\u0004R!EAE\u0007g\u0004R!\u0005BR\u000fRC!\"!&\u0004n\u0006\u0005\t\u0019\u0001B\u000f\u0011)\tIj!6\u0002\u0002\u0013%\u00111\u0014\u0005\t\u0007w\fi\r\"\u0001\u0004~\u0006\u00192M]3bi\u0016|Fn\\4`CB\u0004XM\u001c3feR1!1CB\u0000\t\u0003AaAUB}\u0001\u0004!\u0006b\u0002C\u0002\u0007s\u0004\r\u0001V\u0001\u0007_\u001a47/\u001a;\t\u0011\u0011\u001d\u0011Q\u001aC\u0001\t\u0013\tqb\u0019:fCR,w,\u00199qK:$WM\u001d\u000b\u0007\u00037!Y\u0001\"\u0004\t\rI#)\u00011\u0001U\u0011\u001d!\u0019\u0001\"\u0002A\u0002QC!\u0002\"\u0005\u0002N\n\u0007I\u0011\u0001C\n\u0003Ui\u0017\r_0m_\u001e|vO]5uK~c\u0017\r^3oGf,\"\u0001\"\u0006\u0011\u0007]!9\"C\u0002\u0005\u001aa\u0011!\u0002V5nK6+GO]5d\u0011%!i\"!4!\u0002\u0013!)\"\u0001\fnCb|Fn\\4`oJLG/Z0mCR,gnY=!\u0011)!\t#!4C\u0002\u0013\u0005A1C\u0001\u0016[\u0006Dx\f\\8h?\u001adWo\u001d5`Y\u0006$XM\\2z\u0011%!)#!4!\u0002\u0013!)\"\u0001\fnCb|Fn\\4`M2,8\u000f[0mCR,gnY=!\u0011)!I#!4C\u0002\u0013\u0005A1C\u0001\u0017[\u0006Dx\f\\8h?J|G/\u0019;f?2\fG/\u001a8ds\"IAQFAgA\u0003%AQC\u0001\u0018[\u0006Dx\f\\8h?J|G/\u0019;f?2\fG/\u001a8ds\u0002B\u0001Ba\u0011\u0002N\u0012\u0005A\u0011\u0007\u000b\u0005\u00037!\u0019\u0004C\u0005\u00056\u0011=\u0002\u0013!a\u0001)\u0006i\u0011\r\u001d9f]\u0012,'oX:ju\u0016D\u0001\u0002\"\u000f\u0002N\u0012\u00051QN\u0001\u0007SN|\u0005/\u001a8\t\u0011\u0011u\u0012Q\u001aC\u0001\u0005s\nQa\u00197pg\u0016Dq\u0001\"\u0011\u0002N\u0012\u00051+\u0001\bbaB,g\u000eZ3s?2LW.\u001b;\t\u000f\u0011\u0015\u0013Q\u001aC\u0001'\u0006q\u0011\r\u001d9f]\u0012,'oX:uCJ$\b\u0002\u0003C%\u0003\u001b$\t\u0001b\u0013\u0002\u00119,\u0007\u0010^0m_\u001e$2a\u0012C'\u0011\u0019\u0011Fq\ta\u0001)\"AA\u0011KAg\t\u0003!\u0019&\u0001\u0005baB,g\u000eZ3s+\u0011!)\u0006b\u0017\u0015\t\u0011]Cq\r\t\u0005\t3\"Y\u0006\u0004\u0001\u0005\u0011\u0011uCq\nb\u0001\t?\u0012\u0011\u0001V\t\u0005\tC\nY\u0002E\u0002\u0012\tGJ1\u0001\"\u001a\u0013\u0005\u001dqu\u000e\u001e5j]\u001eD\u0001\u0002\"\u001b\u0005P\u0001\u0007A1N\u0001\u0005MVt7\rE\u0004\u0012\t[\u0012\u0019\u0002b\u0016\n\u0007\u0011=$CA\u0005Gk:\u001cG/[8oc!AA1OAg\t\u0003!)(\u0001\u0004s_R\fG/Z\u000b\u0005\to\"I(\u0006\u0002\u0002\u001c\u0011AAQ\fC9\u0005\u0004!y\u0006\u0003\u0006\u0005~\u00055\u0007\u0019!C\u0001\t\u007f\nQb\u001c8`Y><wL]8uCR,WC\u0001CA!\u0015\tB1\u0011B\u0001\u0013\r!)I\u0005\u0002\n\rVt7\r^5p]BB!\u0002\"#\u0002N\u0002\u0007I\u0011\u0001CF\u0003Eygn\u00187pO~\u0013x\u000e^1uK~#S-\u001d\u000b\u0005\u0005\u0003!i\t\u0003\u0006\u0002$\u0011\u001d\u0015\u0011!a\u0001\t\u0003C\u0011\u0002\"%\u0002N\u0002\u0006K\u0001\"!\u0002\u001d=tw\f\\8h?J|G/\u0019;fA!QAQSAg\u0005\u0004%I\u0001b&\u0002\u0019I,\u0017\rZ3s?\u000e\f7\r[3\u0016\u0005\u0011e\u0005c\u0002CN\t?;%QD\u0007\u0003\t;S!!\u0007\u0003\n\t\u0011\u0005FQ\u0014\u0002\t\u0019J+6)Y2iK\"IAQUAgA\u0003%A\u0011T\u0001\u000ee\u0016\fG-\u001a:`G\u0006\u001c\u0007.\u001a\u0011\t\u0011\u0011%\u0016Q\u001aC\u0001\tW\u000b\u0001\u0002\\8h?&tgm\u001c\u000b\u0005\t[#y\u000bE\u0003\u0012\u0003\u0013\u0013Y\u0010C\u0004\u00052\u0012\u001d\u0006\u0019\u0001+\u0002\u0007A|7\u000f\u0003\u0005\u00056\u00065G\u0011\u0001C\\\u0003IawnZ0gS2,w\f]8tSRLwN\\:\u0016\u0005\u0011e\u0006\u0003B\t\u0005<RK1\u0001\"0\u0013\u0005\u0015\t%O]1z\u0011!!\t-!4\u0005\n\u0011\r\u0017AC4fi~\u0013X-\u00193feV!AQ\u0019Cg)\u0011!9\rb5\u0015\t\u0011%Gq\u001a\t\u0006#\u0005%E1\u001a\t\u0005\t3\"i\r\u0002\u0005\u0005^\u0011}&\u0019\u0001C0\u0011!!I\u0007b0A\u0002\u0011E\u0007cB\t\u0005n\tuA1\u001a\u0005\b\u0005\u001b#y\f1\u0001U\u0011!\u00119)!4\u0005\u0002\u0011]G\u0003\u0002Cm\t7\u0004R!EAE\u0005+Cq\u0001\"-\u0005V\u0002\u0007A\u000b\u0003\u0005\u0003\b\u00065G\u0011\u0001Cp)\u0019!\t\u000fb9\u0005fB)\u0011#!#\u0002*\"9A\u0011\u0017Co\u0001\u0004!\u0006B\u0002.\u0005^\u0002\u0007\u0011\u0007C\u0005h\u0003\u001b\f\t\u0011\"\u0001\u0005jR1\u00111\u001aCv\t[D\u0011\"a5\u0005hB\u0005\t\u0019A$\t\u0015\u0005mGq\u001dI\u0001\u0002\u0004\ty\u000eC\u0005\u0005r\u00065\u0017\u0013!C\u0001w\u0006qq\u000e]3oI\u0011,g-Y;mi\u0012\n\u0004\u0002C7\u0002NF\u0005I\u0011\u00018\t\u0013i\fi-%A\u0005\u0002\u0011]XC\u0001C}U\r\ty\u000e\u001d\u0005\u000b\u0003\u0003\ti-!A\u0005B\u0005\r\u0001\"CA\n\u0003\u001b\f\t\u0011\"\u00011\u0011)\t9\"!4\u0002\u0002\u0013\u0005Q\u0011\u0001\u000b\u0005\u00037)\u0019\u0001C\u0005\u0002$\u0011}\u0018\u0011!a\u0001c!Q\u0011qEAg\u0003\u0003%\t%!\u000b\t\u0015\u0005e\u0012QZA\u0001\n\u0003)I\u0001\u0006\u0003\u0002>\u0015-\u0001BCA\u0012\u000b\u000f\t\t\u00111\u0001\u0002\u001c!Q\u0011qIAg\u0003\u0003%\t%!\u0013\t\u0015\u00055\u0013QZA\u0001\n\u0003\ny\u0005\u0003\u0006\u0002T\u00055\u0017\u0011!C!\u000b'!B!!\u0010\u0006\u0016!Q\u00111EC\t\u0003\u0003\u0005\r!a\u0007\t\u000f\u0005M\u0017q\u0019a\u0001\u000f\"A\u00111\\Ad\u0001\u0004\ty\u000eC\u0005\u0002\u00046\t\t\u0011\"!\u0006\u001eQ!QqDC\u0012!\u0015\t\u0012\u0011RC\u0011!\u0019\t\"1U$\u0002`\"Q\u0011QSC\u000e\u0003\u0003\u0005\r!a3\t\u0013\u0005eU\"!A\u0005\n\u0005m\u0005")
public class RecordLog implements Product, Serializable
{
    private final File directory;
    private final String logSuffix;
    private long logSize;
    private LogAppender current_appender;
    private boolean verify_checksums;
    private final TreeMap<Object, LogInfo> log_infos;
    private final TimeMetric max_log_write_latency;
    private final TimeMetric max_log_flush_latency;
    private final TimeMetric max_log_rotate_latency;
    private Function0<BoxedUnit> on_log_rotate;
    private final LRUCache<File, LogReader> reader_cache;
    private volatile log_mutex$ log_mutex$module;
    private volatile LogAppender$ LogAppender$module;
    private volatile LogReader$ LogReader$module;
    
    public static void trace(final Throwable e) {
        RecordLog$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        RecordLog$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        RecordLog$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        RecordLog$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        RecordLog$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        RecordLog$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        RecordLog$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static Logger log() {
        return RecordLog$.MODULE$.log();
    }
    
    public static Option<Tuple2<File, String>> unapply(final RecordLog x$0) {
        return RecordLog$.MODULE$.unapply(x$0);
    }
    
    public static RecordLog apply(final File directory, final String logSuffix) {
        return RecordLog$.MODULE$.apply(directory, logSuffix);
    }
    
    public static long decode_long(final Buffer value) {
        return RecordLog$.MODULE$.decode_long(value);
    }
    
    public static Buffer encode_long(final long a1) {
        return RecordLog$.MODULE$.encode_long(a1);
    }
    
    public static int BYPASS_BUFFER_SIZE() {
        return RecordLog$.MODULE$.BYPASS_BUFFER_SIZE();
    }
    
    public static int BUFFER_SIZE() {
        return RecordLog$.MODULE$.BUFFER_SIZE();
    }
    
    public static int LOG_HEADER_SIZE() {
        return RecordLog$.MODULE$.LOG_HEADER_SIZE();
    }
    
    public static byte UOW_END_RECORD() {
        return RecordLog$.MODULE$.UOW_END_RECORD();
    }
    
    public static byte LOG_HEADER_PREFIX() {
        return RecordLog$.MODULE$.LOG_HEADER_PREFIX();
    }
    
    private log_mutex$ log_mutex$lzycompute() {
        synchronized (this) {
            if (this.log_mutex$module == null) {
                this.log_mutex$module = new log_mutex$();
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.log_mutex$module;
        }
    }
    
    private LogAppender$ LogAppender$lzycompute() {
        synchronized (this) {
            if (this.LogAppender$module == null) {
                this.LogAppender$module = new LogAppender$();
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.LogAppender$module;
        }
    }
    
    private LogReader$ LogReader$lzycompute() {
        synchronized (this) {
            if (this.LogReader$module == null) {
                this.LogReader$module = new LogReader$();
            }
            final BoxedUnit unit = BoxedUnit.UNIT;
            return this.LogReader$module;
        }
    }
    
    public File directory() {
        return this.directory;
    }
    
    public String logSuffix() {
        return this.logSuffix;
    }
    
    public long logSize() {
        return this.logSize;
    }
    
    public void logSize_$eq(final long x$1) {
        this.logSize = x$1;
    }
    
    public LogAppender current_appender() {
        return this.current_appender;
    }
    
    public void current_appender_$eq(final LogAppender x$1) {
        this.current_appender = x$1;
    }
    
    public boolean verify_checksums() {
        return this.verify_checksums;
    }
    
    public void verify_checksums_$eq(final boolean x$1) {
        this.verify_checksums = x$1;
    }
    
    public TreeMap<Object, LogInfo> log_infos() {
        return this.log_infos;
    }
    
    public log_mutex$ log_mutex() {
        return (this.log_mutex$module == null) ? this.log_mutex$lzycompute() : this.log_mutex$module;
    }
    
    public void delete(final long id) {
        synchronized (this.log_mutex()) {
            if (this.current_appender().position() != id) {
                Option$.MODULE$.apply((Object)this.log_infos().get(BoxesRunTime.boxToLong(id))).foreach((Function1)new RecordLog$$anonfun$delete.RecordLog$$anonfun$delete$1(this, id));
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
            else {
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
        }
        // monitorexit(this.log_mutex())
    }
    
    public void onDelete(final long file) {
    }
    
    public boolean onDelete(final File file) {
        return file.delete();
    }
    
    public int checksum(final Buffer data) {
        final CRC32 checksum = new CRC32();
        checksum.update(data.data, data.offset, data.length);
        return (int)(checksum.getValue() & -1L);
    }
    
    public LogAppender$ LogAppender() {
        return (this.LogAppender$module == null) ? this.LogAppender$lzycompute() : this.LogAppender$module;
    }
    
    public LogReader$ LogReader() {
        return (this.LogReader$module == null) ? this.LogReader$lzycompute() : this.LogReader$module;
    }
    
    public LogAppender create_log_appender(final long position, final long offset) {
        return new LogAppender(this.next_log(position), position, offset);
    }
    
    public Object create_appender(final long position, final long offset) {
        synchronized (this.log_mutex()) {
            if (this.current_appender() == null) {
                final BoxedUnit unit = BoxedUnit.UNIT;
            }
            else {
                this.log_infos().put(BoxesRunTime.boxToLong(position), new LogInfo(this.current_appender().file(), this.current_appender().position(), this.current_appender().append_offset()));
            }
            this.current_appender_$eq(this.create_log_appender(position, offset));
            // monitorexit(this.log_mutex())
            return this.log_infos().put(BoxesRunTime.boxToLong(position), new LogInfo(this.current_appender().file(), position, 0L));
        }
    }
    
    public TimeMetric max_log_write_latency() {
        return this.max_log_write_latency;
    }
    
    public TimeMetric max_log_flush_latency() {
        return this.max_log_flush_latency;
    }
    
    public TimeMetric max_log_rotate_latency() {
        return this.max_log_rotate_latency;
    }
    
    public Object open(final long appender_size) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_mutex:()Lorg/apache/activemq/leveldb/RecordLog$log_mutex$;
        //     4: dup            
        //     5: astore_3       
        //     6: monitorenter   
        //     7: aload_0         /* this */
        //     8: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_infos:()Ljava/util/TreeMap;
        //    11: invokevirtual   java/util/TreeMap.clear:()V
        //    14: getstatic       org/apache/activemq/leveldb/LevelDBClient$.MODULE$:Lorg/apache/activemq/leveldb/LevelDBClient$;
        //    17: aload_0         /* this */
        //    18: invokevirtual   org/apache/activemq/leveldb/RecordLog.directory:()Ljava/io/File;
        //    21: aload_0         /* this */
        //    22: invokevirtual   org/apache/activemq/leveldb/RecordLog.logSuffix:()Ljava/lang/String;
        //    25: invokevirtual   org/apache/activemq/leveldb/LevelDBClient$.find_sequence_files:(Ljava/io/File;Ljava/lang/String;)Lscala/collection/immutable/TreeMap;
        //    28: new             Lorg/apache/activemq/leveldb/RecordLog$$anonfun$open$1;
        //    31: dup            
        //    32: aload_0         /* this */
        //    33: invokespecial   org/apache/activemq/leveldb/RecordLog$$anonfun$open$1.<init>:(Lorg/apache/activemq/leveldb/RecordLog;)V
        //    36: invokevirtual   scala/collection/immutable/TreeMap.foreach:(Lscala/Function1;)V
        //    39: aload_0         /* this */
        //    40: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_infos:()Ljava/util/TreeMap;
        //    43: invokevirtual   java/util/TreeMap.isEmpty:()Z
        //    46: ifeq            58
        //    49: aload_0         /* this */
        //    50: lconst_0       
        //    51: lconst_0       
        //    52: invokevirtual   org/apache/activemq/leveldb/RecordLog.create_appender:(JJ)Ljava/lang/Object;
        //    55: goto            171
        //    58: aload_0         /* this */
        //    59: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_infos:()Ljava/util/TreeMap;
        //    62: invokevirtual   java/util/TreeMap.lastEntry:()Ljava/util/Map$Entry;
        //    65: invokeinterface java/util/Map$Entry.getValue:()Ljava/lang/Object;
        //    70: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogInfo;
        //    73: astore          file
        //    75: lload_1         /* appender_size */
        //    76: ldc2_w          -1
        //    79: lcmp           
        //    80: ifne            161
        //    83: new             Lorg/apache/activemq/leveldb/RecordLog$LogReader;
        //    86: dup            
        //    87: aload_0         /* this */
        //    88: aload           file
        //    90: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.file:()Ljava/io/File;
        //    93: aload           file
        //    95: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.position:()J
        //    98: invokespecial   org/apache/activemq/leveldb/RecordLog$LogReader.<init>:(Lorg/apache/activemq/leveldb/RecordLog;Ljava/io/File;J)V
        //   101: astore          r
        //   103: aload           r
        //   105: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.verifyAndGetEndOffset:()J
        //   108: lstore          endOffset
        //   110: getstatic       org/apache/activemq/leveldb/util/FileSupport$.MODULE$:Lorg/apache/activemq/leveldb/util/FileSupport$;
        //   113: new             Ljava/io/RandomAccessFile;
        //   116: dup            
        //   117: aload           file
        //   119: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.file:()Ljava/io/File;
        //   122: ldc_w           "rw"
        //   125: invokespecial   java/io/RandomAccessFile.<init>:(Ljava/io/File;Ljava/lang/String;)V
        //   128: new             Lorg/apache/activemq/leveldb/RecordLog$$anonfun$open$2;
        //   131: dup            
        //   132: aload_0         /* this */
        //   133: lload           endOffset
        //   135: invokespecial   org/apache/activemq/leveldb/RecordLog$$anonfun$open$2.<init>:(Lorg/apache/activemq/leveldb/RecordLog;J)V
        //   138: invokevirtual   org/apache/activemq/leveldb/util/FileSupport$.using:(Ljava/io/Closeable;Lscala/Function1;)Ljava/lang/Object;
        //   141: pop            
        //   142: aload_0         /* this */
        //   143: aload           file
        //   145: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.position:()J
        //   148: lload           endOffset
        //   150: invokevirtual   org/apache/activemq/leveldb/RecordLog.create_appender:(JJ)Ljava/lang/Object;
        //   153: aload           r
        //   155: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.release:()V
        //   158: goto            171
        //   161: aload_0         /* this */
        //   162: aload           file
        //   164: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.position:()J
        //   167: lload_1         /* appender_size */
        //   168: invokevirtual   org/apache/activemq/leveldb/RecordLog.create_appender:(JJ)Ljava/lang/Object;
        //   171: astore          4
        //   173: aload_3        
        //   174: monitorexit    
        //   175: aload           4
        //   177: areturn        
        //   178: astore          7
        //   180: aload           6
        //   182: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.release:()V
        //   185: aload           7
        //   187: athrow         
        //   188: aload_3        
        //   189: monitorexit    
        //   190: athrow         
        //    StackMapTable: 00 05 FC 00 3A 07 00 84 FD 00 66 00 07 01 21 FF 00 09 00 03 07 00 02 04 07 00 84 00 01 07 00 04 FF 00 06 00 06 07 00 02 04 07 00 84 00 07 01 21 07 01 60 00 01 07 00 92 FF 00 09 00 03 07 00 02 04 07 00 84 00 01 07 00 92
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  103    153    178    188    Any
        //  178    188    188    191    Any
        //  7      175    188    191    Any
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
    
    public long open$default$1() {
        return -1L;
    }
    
    public boolean isOpen() {
        synchronized (this.log_mutex()) {
            final Boolean boxToBoolean = BoxesRunTime.boxToBoolean(this.current_appender() != null);
            // monitorexit(this.log_mutex())
            return BoxesRunTime.unboxToBoolean((Object)boxToBoolean);
        }
    }
    
    public void close() {
        synchronized (this.log_mutex()) {
            if (this.current_appender() == null) {
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
            else {
                this.current_appender().release();
                final BoxedUnit boxedUnit = BoxedUnit.UNIT;
            }
        }
        // monitorexit(this.log_mutex())
    }
    
    public long appender_limit() {
        return this.current_appender().append_position();
    }
    
    public long appender_start() {
        return this.current_appender().position();
    }
    
    public File next_log(final long position) {
        return LevelDBClient$.MODULE$.create_sequence_file(this.directory(), position, this.logSuffix());
    }
    
    public <T> T appender(final Function1<LogAppender, T> func) {
        final long intial_position = this.current_appender().append_position();
        try {
            return this.max_log_write_latency().apply((scala.Function0<T>)new RecordLog$$anonfun$appender.RecordLog$$anonfun$appender$1(this, (Function1)func, intial_position));
        }
        finally {
            this.current_appender().flush();
            this.max_log_rotate_latency().apply((scala.Function0<Object>)new RecordLog$$anonfun$appender.RecordLog$$anonfun$appender$2(this));
        }
    }
    
    public <T> Object rotate() {
        synchronized (this.log_mutex()) {
            this.current_appender().release();
            this.on_log_rotate().apply$mcV$sp();
            // monitorexit(this.log_mutex())
            return this.create_appender(this.current_appender().append_position(), 0L);
        }
    }
    
    public Function0<BoxedUnit> on_log_rotate() {
        return this.on_log_rotate;
    }
    
    public void on_log_rotate_$eq(final Function0<BoxedUnit> x$1) {
        this.on_log_rotate = x$1;
    }
    
    private LRUCache<File, LogReader> reader_cache() {
        return this.reader_cache;
    }
    
    public Option<LogInfo> log_info(final long pos) {
        synchronized (this.log_mutex()) {
            final Option map = Option$.MODULE$.apply((Object)this.log_infos().floorEntry(BoxesRunTime.boxToLong(pos))).map((Function1)new RecordLog$$anonfun$log_info.RecordLog$$anonfun$log_info$1(this));
            // monitorexit(this.log_mutex())
            return (Option<LogInfo>)map;
        }
    }
    
    public long[] log_file_positions() {
        synchronized (this.log_mutex()) {
            final Object array = ((TraversableOnce)JavaConversions$.MODULE$.mapAsScalaMap((Map)this.log_infos()).map((Function1)new RecordLog$$anonfun$log_file_positions.RecordLog$$anonfun$log_file_positions$1(this), Iterable$.MODULE$.canBuildFrom())).toArray(ClassTag$.MODULE$.Long());
            // monitorexit(this.log_mutex())
            return (long[])array;
        }
    }
    
    private <T> Option<T> get_reader(final long record_position, final Function1<LogReader, T> func) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_mutex:()Lorg/apache/activemq/leveldb/RecordLog$log_mutex$;
        //     4: dup            
        //     5: astore          6
        //     7: monitorenter   
        //     8: aload_0         /* this */
        //     9: lload_1         /* record_position */
        //    10: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_info:(J)Lscala/Option;
        //    13: astore          8
        //    15: getstatic       scala/None$.MODULE$:Lscala/None$;
        //    18: aload           8
        //    20: invokevirtual   java/lang/Object.equals:(Ljava/lang/Object;)Z
        //    23: ifeq            71
        //    26: getstatic       org/apache/activemq/leveldb/RecordLog$.MODULE$:Lorg/apache/activemq/leveldb/RecordLog$;
        //    29: new             Lorg/apache/activemq/leveldb/RecordLog$$anonfun$2;
        //    32: dup            
        //    33: aload_0         /* this */
        //    34: invokespecial   org/apache/activemq/leveldb/RecordLog$$anonfun$2.<init>:(Lorg/apache/activemq/leveldb/RecordLog;)V
        //    37: getstatic       scala/Predef$.MODULE$:Lscala/Predef$;
        //    40: iconst_2       
        //    41: anewarray       Ljava/lang/Object;
        //    44: dup            
        //    45: iconst_0       
        //    46: lload_1         /* record_position */
        //    47: invokestatic    scala/runtime/BoxesRunTime.boxToLong:(J)Ljava/lang/Long;
        //    50: aastore        
        //    51: dup            
        //    52: iconst_1       
        //    53: aload_0         /* this */
        //    54: invokevirtual   org/apache/activemq/leveldb/RecordLog.log_infos:()Ljava/util/TreeMap;
        //    57: aastore        
        //    58: invokevirtual   scala/Predef$.genericWrapArray:(Ljava/lang/Object;)Lscala/collection/mutable/WrappedArray;
        //    61: invokevirtual   org/apache/activemq/leveldb/RecordLog$.warn:(Lscala/Function0;Lscala/collection/Seq;)V
        //    64: getstatic       scala/None$.MODULE$:Lscala/None$;
        //    67: aload           6
        //    69: monitorexit    
        //    70: areturn        
        //    71: aload           8
        //    73: instanceof      Lscala/Some;
        //    76: ifeq            354
        //    79: aload           8
        //    81: checkcast       Lscala/Some;
        //    84: astore          10
        //    86: aload           10
        //    88: invokevirtual   scala/Some.x:()Ljava/lang/Object;
        //    91: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogInfo;
        //    94: astore          info
        //    96: aload           info
        //    98: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.position:()J
        //   101: aload_0         /* this */
        //   102: invokevirtual   org/apache/activemq/leveldb/RecordLog.current_appender:()Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   105: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogAppender.position:()J
        //   108: lcmp           
        //   109: ifne            135
        //   112: aload_0         /* this */
        //   113: invokevirtual   org/apache/activemq/leveldb/RecordLog.current_appender:()Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   116: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogAppender.retain:()V
        //   119: new             Lscala/Tuple2;
        //   122: dup            
        //   123: aload           info
        //   125: aload_0         /* this */
        //   126: invokevirtual   org/apache/activemq/leveldb/RecordLog.current_appender:()Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   129: invokespecial   scala/Tuple2.<init>:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   132: goto            145
        //   135: new             Lscala/Tuple2;
        //   138: dup            
        //   139: aload           info
        //   141: aconst_null    
        //   142: invokespecial   scala/Tuple2.<init>:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   145: astore          9
        //   147: aload           9
        //   149: astore          7
        //   151: aload           6
        //   153: monitorexit    
        //   154: aload           7
        //   156: checkcast       Lscala/Tuple2;
        //   159: astore          5
        //   161: aload           5
        //   163: ifnull          344
        //   166: aload           5
        //   168: invokevirtual   scala/Tuple2._1:()Ljava/lang/Object;
        //   171: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogInfo;
        //   174: astore          info
        //   176: aload           5
        //   178: invokevirtual   scala/Tuple2._2:()Ljava/lang/Object;
        //   181: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   184: astore          appender
        //   186: new             Lscala/Tuple2;
        //   189: dup            
        //   190: aload           info
        //   192: aload           appender
        //   194: invokespecial   scala/Tuple2.<init>:(Ljava/lang/Object;Ljava/lang/Object;)V
        //   197: astore          14
        //   199: aload           14
        //   201: astore          4
        //   203: aload           4
        //   205: invokevirtual   scala/Tuple2._1:()Ljava/lang/Object;
        //   208: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogInfo;
        //   211: astore          info
        //   213: aload           4
        //   215: invokevirtual   scala/Tuple2._2:()Ljava/lang/Object;
        //   218: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogAppender;
        //   221: astore          appender
        //   223: aload           appender
        //   225: ifnonnull       319
        //   228: aload_0         /* this */
        //   229: invokespecial   org/apache/activemq/leveldb/RecordLog.reader_cache:()Lorg/apache/activemq/util/LRUCache;
        //   232: dup            
        //   233: astore          18
        //   235: monitorenter   
        //   236: aload_0         /* this */
        //   237: invokespecial   org/apache/activemq/leveldb/RecordLog.reader_cache:()Lorg/apache/activemq/util/LRUCache;
        //   240: aload           info
        //   242: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.file:()Ljava/io/File;
        //   245: invokevirtual   org/apache/activemq/util/LRUCache.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   248: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogReader;
        //   251: astore          reader
        //   253: aload           reader
        //   255: ifnonnull       295
        //   258: new             Lorg/apache/activemq/leveldb/RecordLog$LogReader;
        //   261: dup            
        //   262: aload_0         /* this */
        //   263: aload           info
        //   265: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.file:()Ljava/io/File;
        //   268: aload           info
        //   270: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.position:()J
        //   273: invokespecial   org/apache/activemq/leveldb/RecordLog$LogReader.<init>:(Lorg/apache/activemq/leveldb/RecordLog;Ljava/io/File;J)V
        //   276: astore          reader
        //   278: aload_0         /* this */
        //   279: invokespecial   org/apache/activemq/leveldb/RecordLog.reader_cache:()Lorg/apache/activemq/util/LRUCache;
        //   282: aload           info
        //   284: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogInfo.file:()Ljava/io/File;
        //   287: aload           reader
        //   289: invokevirtual   org/apache/activemq/util/LRUCache.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   292: goto            298
        //   295: getstatic       scala/runtime/BoxedUnit.UNIT:Lscala/runtime/BoxedUnit;
        //   298: pop            
        //   299: aload           reader
        //   301: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.retain:()V
        //   304: aload           reader
        //   306: astore          19
        //   308: aload           18
        //   310: monitorexit    
        //   311: aload           19
        //   313: checkcast       Lorg/apache/activemq/leveldb/RecordLog$LogReader;
        //   316: goto            321
        //   319: aload           appender
        //   321: astore          reader
        //   323: new             Lscala/Some;
        //   326: dup            
        //   327: aload_3         /* func */
        //   328: aload           reader
        //   330: invokeinterface scala/Function1.apply:(Ljava/lang/Object;)Ljava/lang/Object;
        //   335: invokespecial   scala/Some.<init>:(Ljava/lang/Object;)V
        //   338: aload           reader
        //   340: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.release:()V
        //   343: areturn        
        //   344: new             Lscala/MatchError;
        //   347: dup            
        //   348: aload           5
        //   350: invokespecial   scala/MatchError.<init>:(Ljava/lang/Object;)V
        //   353: athrow         
        //   354: new             Lscala/MatchError;
        //   357: dup            
        //   358: aload           8
        //   360: invokespecial   scala/MatchError.<init>:(Ljava/lang/Object;)V
        //   363: athrow         
        //   364: aload           6
        //   366: monitorexit    
        //   367: athrow         
        //   368: aload           18
        //   370: monitorexit    
        //   371: athrow         
        //   372: astore          21
        //   374: aload           17
        //   376: invokevirtual   org/apache/activemq/leveldb/RecordLog$LogReader.release:()V
        //   379: aload           21
        //   381: athrow         
        //    Signature:
        //  <T:Ljava/lang/Object;>(JLscala/Function1<Lorg/apache/activemq/leveldb/RecordLog$LogReader;TT;>;)Lscala/Option<TT;>;
        //    StackMapTable: 00 0C FF 00 47 00 08 07 00 02 04 07 01 B3 00 00 07 00 84 00 07 00 E3 00 00 FE 00 3F 00 07 02 18 07 01 21 49 07 02 20 FF 00 95 00 14 07 00 02 04 07 01 B3 07 02 20 07 02 20 07 00 84 07 02 20 07 00 E3 07 02 20 07 02 18 07 01 21 07 01 21 07 00 C3 07 02 20 07 01 21 07 00 C3 00 07 02 2D 00 07 01 60 00 00 42 07 00 04 FF 00 14 00 10 07 00 02 04 07 01 B3 07 02 20 07 02 20 07 00 84 07 02 20 07 00 E3 07 02 20 07 02 18 07 01 21 07 01 21 07 00 C3 07 02 20 07 01 21 07 00 C3 00 00 41 07 01 60 FF 00 16 00 0B 07 00 02 04 07 01 B3 00 07 02 20 07 00 84 07 02 20 07 00 E3 07 02 20 07 02 18 07 01 21 00 00 FF 00 09 00 08 07 00 02 04 07 01 B3 00 00 07 00 84 00 07 00 E3 00 00 FF 00 09 00 06 07 00 02 04 07 01 B3 00 00 07 00 84 00 01 07 00 92 FF 00 03 00 12 07 00 02 04 07 01 B3 07 02 20 07 02 20 07 00 84 07 02 20 07 00 E3 07 02 20 07 02 18 07 01 21 07 01 21 07 00 C3 07 02 20 07 01 21 07 00 C3 00 07 02 2D 00 01 07 00 92 FF 00 03 00 11 07 00 02 04 07 01 B3 07 02 20 07 02 20 07 00 84 07 02 20 07 00 E3 07 02 20 07 02 18 07 01 21 07 01 21 07 00 C3 07 02 20 07 01 21 07 00 C3 07 01 60 00 01 07 00 92
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  323    338    372    382    Any
        //  236    311    368    372    Any
        //  354    364    364    368    Any
        //  8      154    364    368    Any
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
    
    public Option<Tuple3<Object, Buffer, Object>> read(final long pos) {
        return this.get_reader(pos, (scala.Function1<LogReader, Tuple3<Object, Buffer, Object>>)new RecordLog$$anonfun$read.RecordLog$$anonfun$read$1(this, pos));
    }
    
    public Option<Buffer> read(final long pos, final int length) {
        return this.get_reader(pos, (scala.Function1<LogReader, Buffer>)new RecordLog$$anonfun$read.RecordLog$$anonfun$read$2(this, pos, length));
    }
    
    public RecordLog copy(final File directory, final String logSuffix) {
        return new RecordLog(directory, logSuffix);
    }
    
    public File copy$default$1() {
        return this.directory();
    }
    
    public String copy$default$2() {
        return this.logSuffix();
    }
    
    public String productPrefix() {
        return "RecordLog";
    }
    
    public int productArity() {
        return 2;
    }
    
    public Object productElement(final int x$1) {
        java.io.Serializable s = null;
        switch (x$1) {
            default: {
                throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
            }
            case 1: {
                s = this.logSuffix();
                break;
            }
            case 0: {
                s = this.directory();
                break;
            }
        }
        return s;
    }
    
    public Iterator<Object> productIterator() {
        return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
    }
    
    public boolean canEqual(final Object x$1) {
        return x$1 instanceof RecordLog;
    }
    
    @Override
    public int hashCode() {
        return ScalaRunTime$.MODULE$._hashCode((Product)this);
    }
    
    @Override
    public String toString() {
        return ScalaRunTime$.MODULE$._toString((Product)this);
    }
    
    @Override
    public boolean equals(final Object x$1) {
        if (this != x$1) {
            if (x$1 instanceof RecordLog) {
                final RecordLog recordLog = (RecordLog)x$1;
                final File directory = this.directory();
                final File directory2 = recordLog.directory();
                boolean b = false;
                Label_0109: {
                    Label_0108: {
                        if (directory == null) {
                            if (directory2 != null) {
                                break Label_0108;
                            }
                        }
                        else if (!directory.equals(directory2)) {
                            break Label_0108;
                        }
                        final String logSuffix = this.logSuffix();
                        final String logSuffix2 = recordLog.logSuffix();
                        if (logSuffix == null) {
                            if (logSuffix2 != null) {
                                break Label_0108;
                            }
                        }
                        else if (!logSuffix.equals(logSuffix2)) {
                            break Label_0108;
                        }
                        if (recordLog.canEqual(this)) {
                            b = true;
                            break Label_0109;
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
    
    public RecordLog(final File directory, final String logSuffix) {
        this.directory = directory;
        this.logSuffix = logSuffix;
        Product$class.$init$((Product)this);
        directory.mkdirs();
        this.logSize = 104857600L;
        this.verify_checksums = false;
        this.log_infos = new TreeMap<Object, LogInfo>();
        this.max_log_write_latency = new TimeMetric();
        this.max_log_flush_latency = new TimeMetric();
        this.max_log_rotate_latency = new TimeMetric();
        this.on_log_rotate = (Function0<BoxedUnit>)new Serializable(this) {
            public static final long serialVersionUID = 0L;
            
            public final void apply() {
                this.apply$mcV$sp();
            }
            
            public void apply$mcV$sp() {
            }
        };
        this.reader_cache = new LRUCache<File, LogReader>(this) {
            public void onCacheEviction(final Map.Entry<File, LogReader> entry) {
                entry.getValue().release();
            }
        };
    }
    
    public static class LogInfo implements Product, Serializable
    {
        private final File file;
        private final long position;
        private final long length;
        
        public File file() {
            return this.file;
        }
        
        public long position() {
            return this.position;
        }
        
        public long length() {
            return this.length;
        }
        
        public long limit() {
            return this.position() + this.length();
        }
        
        public LogInfo copy(final File file, final long position, final long length) {
            return new LogInfo(file, position, length);
        }
        
        public File copy$default$1() {
            return this.file();
        }
        
        public long copy$default$2() {
            return this.position();
        }
        
        public long copy$default$3() {
            return this.length();
        }
        
        public String productPrefix() {
            return "LogInfo";
        }
        
        public int productArity() {
            return 3;
        }
        
        public Object productElement(final int x$1) {
            java.io.Serializable s = null;
            switch (x$1) {
                default: {
                    throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
                }
                case 2: {
                    s = BoxesRunTime.boxToLong(this.length());
                    break;
                }
                case 1: {
                    s = BoxesRunTime.boxToLong(this.position());
                    break;
                }
                case 0: {
                    s = this.file();
                    break;
                }
            }
            return s;
        }
        
        public Iterator<Object> productIterator() {
            return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
        }
        
        public boolean canEqual(final Object x$1) {
            return x$1 instanceof LogInfo;
        }
        
        @Override
        public int hashCode() {
            return Statics.finalizeHash(Statics.mix(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.file())), Statics.longHash(this.position())), Statics.longHash(this.length())), 3);
        }
        
        @Override
        public String toString() {
            return ScalaRunTime$.MODULE$._toString((Product)this);
        }
        
        @Override
        public boolean equals(final Object x$1) {
            if (this != x$1) {
                if (x$1 instanceof LogInfo) {
                    final LogInfo logInfo = (LogInfo)x$1;
                    final File file = this.file();
                    final File file2 = logInfo.file();
                    boolean b = false;
                    Label_0103: {
                        Label_0102: {
                            if (file == null) {
                                if (file2 != null) {
                                    break Label_0102;
                                }
                            }
                            else if (!file.equals(file2)) {
                                break Label_0102;
                            }
                            if (this.position() == logInfo.position() && this.length() == logInfo.length() && logInfo.canEqual(this)) {
                                b = true;
                                break Label_0103;
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
        
        public LogInfo(final File file, final long position, final long length) {
            this.file = file;
            this.position = position;
            this.length = length;
            Product$class.$init$((Product)this);
        }
    }
    
    public static class LogInfo$ extends AbstractFunction3<File, Object, Object, LogInfo> implements Serializable
    {
        public static final LogInfo$ MODULE$;
        
        static {
            new LogInfo$();
        }
        
        public final String toString() {
            return "LogInfo";
        }
        
        public LogInfo apply(final File file, final long position, final long length) {
            return new LogInfo(file, position, length);
        }
        
        public Option<Tuple3<File, Object, Object>> unapply(final LogInfo x$0) {
            return (Option<Tuple3<File, Object, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple3((Object)x$0.file(), (Object)BoxesRunTime.boxToLong(x$0.position()), (Object)BoxesRunTime.boxToLong(x$0.length()))));
        }
        
        private Object readResolve() {
            return LogInfo$.MODULE$;
        }
        
        public LogInfo$() {
            MODULE$ = this;
        }
    }
    
    public class log_mutex$
    {
        public log_mutex$(final RecordLog $outer) {
        }
    }
    
    public class LogAppender extends LogReader
    {
        private long append_offset;
        private final LogInfo info;
        private final AtomicLong flushed_offset;
        private final DataByteArrayOutputStream write_buffer;
        
        public long append_offset() {
            return this.append_offset;
        }
        
        public void append_offset_$eq(final long x$1) {
            this.append_offset = x$1;
        }
        
        public LogInfo info() {
            return this.info;
        }
        
        @Override
        public RandomAccessFile open() {
            return new RandomAccessFile(super.file(), "rw");
        }
        
        @Override
        public void on_close() {
            this.force();
        }
        
        public AtomicLong flushed_offset() {
            return this.flushed_offset;
        }
        
        public long append_position() {
            return super.position() + this.append_offset();
        }
        
        public DataByteArrayOutputStream write_buffer() {
            return this.write_buffer;
        }
        
        public void force() {
            this.flush();
            this.org$apache$activemq$leveldb$RecordLog$LogAppender$$$outer().max_log_flush_latency().apply((scala.Function0<Object>)new RecordLog$LogAppender$$anonfun$force.RecordLog$LogAppender$$anonfun$force$1(this));
        }
        
        public synchronized long skip(final long length) {
            this.flush();
            this.append_offset_$eq(this.append_offset() + length);
            return this.flushed_offset().addAndGet(length);
        }
        
        public synchronized Tuple2<Object, LogInfo> append(final byte id, final Buffer data) {
            final long record_position = this.append_position();
            final int data_length = data.length;
            final int total_length = RecordLog$.MODULE$.LOG_HEADER_SIZE() + data_length;
            if (this.write_buffer().position() + total_length > RecordLog$.MODULE$.BUFFER_SIZE()) {
                this.flush();
            }
            final int cs = this.org$apache$activemq$leveldb$RecordLog$LogAppender$$$outer().checksum(data);
            if (false && total_length > RecordLog$.MODULE$.BYPASS_BUFFER_SIZE()) {
                this.write_buffer().writeByte(RecordLog$.MODULE$.LOG_HEADER_PREFIX());
                this.write_buffer().writeByte(id);
                this.write_buffer().writeInt(cs);
                this.write_buffer().writeInt(data_length);
                this.append_offset_$eq(this.append_offset() + RecordLog$.MODULE$.LOG_HEADER_SIZE());
                this.flush();
                final ByteBuffer buffer = data.toByteBuffer();
                final long pos = this.append_offset() + RecordLog$.MODULE$.LOG_HEADER_SIZE();
                final int remaining = buffer.remaining();
                this.channel().write(buffer, pos);
                this.flushed_offset().addAndGet(remaining);
                if (buffer.hasRemaining()) {
                    throw new IOException("Short write");
                }
                this.append_offset_$eq(this.append_offset() + data_length);
            }
            else {
                this.write_buffer().writeByte(RecordLog$.MODULE$.LOG_HEADER_PREFIX());
                this.write_buffer().writeByte(id);
                this.write_buffer().writeInt(cs);
                this.write_buffer().writeInt(data_length);
                this.write_buffer().write(data.data, data.offset, data_length);
                this.append_offset_$eq(this.append_offset() + total_length);
            }
            return (Tuple2<Object, LogInfo>)new Tuple2((Object)BoxesRunTime.boxToLong(record_position), (Object)this.info());
        }
        
        public void flush() {
            this.org$apache$activemq$leveldb$RecordLog$LogAppender$$$outer().max_log_flush_latency().apply((scala.Function0<Object>)new RecordLog$LogAppender$$anonfun$flush.RecordLog$LogAppender$$anonfun$flush$1(this));
        }
        
        @Override
        public void check_read_flush(final long end_offset) {
            if (this.flushed_offset().get() < end_offset) {
                this.flush();
            }
        }
        
        public /* synthetic */ RecordLog org$apache$activemq$leveldb$RecordLog$LogAppender$$$outer() {
            return RecordLog.this;
        }
        
        public LogAppender(final RecordLog $outer, final File file, final long position, final long append_offset) {
            this.append_offset = append_offset;
            $outer.super(file, position);
            this.info = new LogInfo(super.file(), super.position(), 0L);
            this.flushed_offset = new AtomicLong(this.append_offset());
            if (this.append_offset() == 0L) {
                this.channel().position($outer.logSize() - 1L);
                this.channel().write(new Buffer(1).toByteBuffer());
                this.channel().force(true);
                this.channel().position(0L);
            }
            else {
                final BoxedUnit unit = BoxedUnit.UNIT;
            }
            this.write_buffer = new DataByteArrayOutputStream(RecordLog$.MODULE$.BUFFER_SIZE() + RecordLog$.MODULE$.LOG_HEADER_SIZE());
        }
    }
    
    public class LogAppender$ implements Serializable
    {
        public long $lessinit$greater$default$3() {
            return 0L;
        }
        
        private Object readResolve() {
            return RecordLog.this.LogAppender();
        }
        
        public LogAppender$() {
            if (RecordLog.this == null) {
                throw null;
            }
        }
    }
    
    public class LogReader$ extends AbstractFunction2<File, Object, LogReader> implements Serializable
    {
        public final String toString() {
            return "LogReader";
        }
        
        public LogReader apply(final File file, final long position) {
            return new LogReader(file, position);
        }
        
        public Option<Tuple2<File, Object>> unapply(final LogReader x$0) {
            return (Option<Tuple2<File, Object>>)((x$0 == null) ? None$.MODULE$ : new Some((Object)new Tuple2((Object)x$0.file(), (Object)BoxesRunTime.boxToLong(x$0.position()))));
        }
        
        private Object readResolve() {
            return RecordLog.this.LogReader();
        }
        
        public LogReader$() {
            if (RecordLog.this == null) {
                throw null;
            }
        }
    }
    
    public class LogReader extends BaseRetained implements Product, Serializable
    {
        private final File file;
        private final long position;
        private final RandomAccessFile fd;
        private final FileChannel channel;
        
        public File file() {
            return this.file;
        }
        
        public long position() {
            return this.position;
        }
        
        public RandomAccessFile open() {
            return new RandomAccessFile(this.file(), "r");
        }
        
        public RandomAccessFile fd() {
            return this.fd;
        }
        
        public FileChannel channel() {
            return this.channel;
        }
        
        public void dispose() {
            this.on_close();
            this.fd().close();
        }
        
        public void on_close() {
        }
        
        public void check_read_flush(final long end_offset) {
        }
        
        public Buffer read(final long record_position, final int length) {
            final long offset = record_position - this.position();
            Predef$.MODULE$.assert(offset >= 0L);
            this.check_read_flush(offset + RecordLog$.MODULE$.LOG_HEADER_SIZE() + length);
            Buffer buffer;
            if (this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer().verify_checksums()) {
                final Buffer record = new Buffer(RecordLog$.MODULE$.LOG_HEADER_SIZE() + length);
                if (this.channel().read(record.toByteBuffer(), offset) != record.length) {
                    Predef$.MODULE$.assert(this.record_is_not_changing$1(length, offset, record));
                    throw new IOException(new StringBuilder().append((Object)"short record at position: ").append((Object)BoxesRunTime.boxToLong(record_position)).append((Object)" in file: ").append((Object)this.file()).append((Object)", offset: ").append((Object)BoxesRunTime.boxToLong(offset)).toString());
                }
                final DataByteArrayInputStream is = new DataByteArrayInputStream(record);
                final byte prefix = is.readByte();
                if (prefix != RecordLog$.MODULE$.LOG_HEADER_PREFIX()) {
                    Predef$.MODULE$.assert(this.record_is_not_changing$1(length, offset, record));
                    throw new IOException(new StringBuilder().append((Object)"invalid record at position: ").append((Object)BoxesRunTime.boxToLong(record_position)).append((Object)" in file: ").append((Object)this.file()).append((Object)", offset: ").append((Object)BoxesRunTime.boxToLong(offset)).toString());
                }
                final byte id = is.readByte();
                final int expectedChecksum = is.readInt();
                final int expectedLength = is.readInt();
                final Buffer data = is.readBuffer(length);
                if (expectedLength == length && expectedChecksum != this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer().checksum(data)) {
                    Predef$.MODULE$.assert(this.record_is_not_changing$1(length, offset, record));
                    throw new IOException(new StringBuilder().append((Object)"checksum does not match at position: ").append((Object)BoxesRunTime.boxToLong(record_position)).append((Object)" in file: ").append((Object)this.file()).append((Object)", offset: ").append((Object)BoxesRunTime.boxToLong(offset)).toString());
                }
                buffer = data;
            }
            else {
                final Buffer data2 = new Buffer(length);
                final ByteBuffer bb = data2.toByteBuffer();
                long position = offset + RecordLog$.MODULE$.LOG_HEADER_SIZE();
                while (bb.hasRemaining()) {
                    final int count = this.channel().read(bb, position);
                    if (count == 0) {
                        throw new IOException(new StringOps(Predef$.MODULE$.augmentString("zero read at file '%s' offset: %d")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { this.file(), BoxesRunTime.boxToLong(position) })));
                    }
                    if (count < 0) {
                        throw new EOFException(new StringOps(Predef$.MODULE$.augmentString("File '%s' offset: %d")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { this.file(), BoxesRunTime.boxToLong(position) })));
                    }
                    position += count;
                }
                buffer = data2;
            }
            return buffer;
        }
        
        public Tuple3<Object, Buffer, Object> read(final long record_position) {
            final long offset = record_position - this.position();
            final Buffer header = new Buffer(RecordLog$.MODULE$.LOG_HEADER_SIZE());
            this.check_read_flush(offset + RecordLog$.MODULE$.LOG_HEADER_SIZE());
            this.channel().read(header.toByteBuffer(), offset);
            final BufferEditor is = header.bigEndianEditor();
            final byte prefix = is.readByte();
            if (prefix != RecordLog$.MODULE$.LOG_HEADER_PREFIX()) {
                throw new IOException(new StringOps(Predef$.MODULE$.augmentString("invalid record position %d (file: %s, offset: %d)")).format((Seq)Predef$.MODULE$.genericWrapArray((Object)new Object[] { BoxesRunTime.boxToLong(record_position), this.file().getAbsolutePath(), BoxesRunTime.boxToLong(offset) })));
            }
            final byte id = is.readByte();
            final int expectedChecksum = is.readInt();
            final int length = is.readInt();
            final Buffer data = new Buffer(length);
            this.check_read_flush(offset + RecordLog$.MODULE$.LOG_HEADER_SIZE() + length);
            if (this.channel().read(data.toByteBuffer(), offset + RecordLog$.MODULE$.LOG_HEADER_SIZE()) != length) {
                throw new IOException("short record");
            }
            if (this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer().verify_checksums() && expectedChecksum != this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer().checksum(data)) {
                throw new IOException("checksum does not match");
            }
            return (Tuple3<Object, Buffer, Object>)new Tuple3((Object)BoxesRunTime.boxToByte(id), (Object)data, (Object)BoxesRunTime.boxToLong(record_position + RecordLog$.MODULE$.LOG_HEADER_SIZE() + length));
        }
        
        public Option<Tuple2<Object, Option<Object>>> check(final long record_position) {
            long offset = record_position - this.position();
            final Buffer header = new Buffer(RecordLog$.MODULE$.LOG_HEADER_SIZE());
            this.channel().read(header.toByteBuffer(), offset);
            final BufferEditor is = header.bigEndianEditor();
            final byte prefix = is.readByte();
            if (prefix != RecordLog$.MODULE$.LOG_HEADER_PREFIX()) {
                return (Option<Tuple2<Object, Option<Object>>>)None$.MODULE$;
            }
            final byte kind = is.readByte();
            final int expectedChecksum = is.readInt();
            final int length = is.readInt();
            final Buffer chunk = new Buffer(4096);
            final ByteBuffer chunkbb = chunk.toByteBuffer();
            offset += RecordLog$.MODULE$.LOG_HEADER_SIZE();
            final CRC32 checksumer = new CRC32();
            int chunkSize;
            for (int remaining = length; remaining > 0; remaining -= chunkSize) {
                chunkSize = RichInt$.MODULE$.min$extension(Predef$.MODULE$.intWrapper(remaining), 4096);
                chunkbb.position(0);
                chunkbb.limit(chunkSize);
                this.channel().read(chunkbb, offset);
                if (chunkbb.hasRemaining()) {
                    return (Option<Tuple2<Object, Option<Object>>>)None$.MODULE$;
                }
                checksumer.update(chunk.data, 0, chunkSize);
                offset += chunkSize;
            }
            final int checksum = (int)(checksumer.getValue() & -1L);
            if (expectedChecksum != checksum) {
                return (Option<Tuple2<Object, Option<Object>>>)None$.MODULE$;
            }
            final Option uow_start_pos = (Option)((kind == RecordLog$.MODULE$.UOW_END_RECORD() && length == 8) ? new Some((Object)BoxesRunTime.boxToLong(RecordLog$.MODULE$.decode_long(chunk))) : None$.MODULE$);
            return (Option<Tuple2<Object, Option<Object>>>)new Some((Object)new Tuple2((Object)BoxesRunTime.boxToLong(record_position + RecordLog$.MODULE$.LOG_HEADER_SIZE() + length), (Object)uow_start_pos));
        }
        
        public long verifyAndGetEndOffset() {
            final Object o = new Object();
            try {
                long pos = this.position();
                final LongRef current_uow_start = LongRef.create(pos);
                final long limit = this.position() + this.channel().size();
                while (pos < limit) {
                    final Option<Tuple2<Object, Option<Object>>> check = this.check(pos);
                    if (check instanceof Some) {
                        final Tuple2 tuple2 = (Tuple2)((Some)check).x();
                        if (tuple2 != null) {
                            final long next = tuple2._1$mcJ$sp();
                            final Option uow_start_pos = (Option)tuple2._2();
                            uow_start_pos.foreach((Function1)new RecordLog$LogReader$$anonfun$verifyAndGetEndOffset.RecordLog$LogReader$$anonfun$verifyAndGetEndOffset$1(this, current_uow_start, next, o));
                            pos = next;
                            final BoxedUnit unit = BoxedUnit.UNIT;
                            continue;
                        }
                    }
                    if (None$.MODULE$.equals(check)) {
                        return current_uow_start.elem - this.position();
                    }
                    throw new MatchError((Object)check);
                }
                return current_uow_start.elem - this.position();
            }
            catch (NonLocalReturnControl nonLocalReturnControl) {
                if (nonLocalReturnControl.key() == o) {
                    return nonLocalReturnControl.value$mcJ$sp();
                }
                throw nonLocalReturnControl;
            }
        }
        
        public LogReader copy(final File file, final long position) {
            return this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer().new LogReader(file, position);
        }
        
        public File copy$default$1() {
            return this.file();
        }
        
        public long copy$default$2() {
            return this.position();
        }
        
        public String productPrefix() {
            return "LogReader";
        }
        
        public int productArity() {
            return 2;
        }
        
        public Object productElement(final int x$1) {
            java.io.Serializable s = null;
            switch (x$1) {
                default: {
                    throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
                }
                case 1: {
                    s = BoxesRunTime.boxToLong(this.position());
                    break;
                }
                case 0: {
                    s = this.file();
                    break;
                }
            }
            return s;
        }
        
        public Iterator<Object> productIterator() {
            return (Iterator<Object>)ScalaRunTime$.MODULE$.typedProductIterator((Product)this);
        }
        
        public boolean canEqual(final Object x$1) {
            return x$1 instanceof LogReader;
        }
        
        public int hashCode() {
            return Statics.finalizeHash(Statics.mix(Statics.mix(-889275714, Statics.anyHash((Object)this.file())), Statics.longHash(this.position())), 2);
        }
        
        public String toString() {
            return ScalaRunTime$.MODULE$._toString((Product)this);
        }
        
        public boolean equals(final Object x$1) {
            if (this != x$1) {
                if (x$1 instanceof LogReader && ((LogReader)x$1).org$apache$activemq$leveldb$RecordLog$LogReader$$$outer() == this.org$apache$activemq$leveldb$RecordLog$LogReader$$$outer()) {
                    final LogReader logReader = (LogReader)x$1;
                    final File file = this.file();
                    final File file2 = logReader.file();
                    boolean b = false;
                    Label_0104: {
                        Label_0103: {
                            if (file == null) {
                                if (file2 != null) {
                                    break Label_0103;
                                }
                            }
                            else if (!file.equals(file2)) {
                                break Label_0103;
                            }
                            if (this.position() == logReader.position() && logReader.canEqual(this)) {
                                b = true;
                                break Label_0104;
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
        
        public /* synthetic */ RecordLog org$apache$activemq$leveldb$RecordLog$LogReader$$$outer() {
            return RecordLog.this;
        }
        
        private final boolean record_is_not_changing$1(final int length$2, final long offset$1, final Buffer record$1) {
            return BoxesRunTime.unboxToBoolean(FileSupport$.MODULE$.using(this.open(), (scala.Function1<RandomAccessFile, Object>)new RecordLog$LogReader$$anonfun$record_is_not_changing$1.RecordLog$LogReader$$anonfun$record_is_not_changing$1$1(this, length$2, offset$1, record$1)));
        }
        
        public LogReader(final File file, final long position) {
            this.file = file;
            this.position = position;
            if (RecordLog.this == null) {
                throw null;
            }
            Product$class.$init$((Product)this);
            this.fd = this.open();
            this.channel = this.fd().getChannel();
        }
    }
}
