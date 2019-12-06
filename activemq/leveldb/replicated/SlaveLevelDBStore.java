// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.apache.activemq.leveldb.replicated.dto.Login;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.LinkedList;
import java.io.File;
import org.apache.activemq.leveldb.replicated.dto.SyncResponse;
import java.util.concurrent.Executor;
import org.apache.activemq.leveldb.replicated.dto.WalAck;
import org.fusesource.hawtdispatch.Task;
import java.net.URI;
import org.fusesource.hawtdispatch.transport.TcpTransport;
import scala.runtime.BoxedUnit;
import scala.Function1;
import org.fusesource.hawtdispatch.transport.Transport;
import org.fusesource.hawtdispatch.package$;
import java.util.concurrent.CountDownLatch;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.leveldb.util.FileSupport$;
import scala.Predef$;
import scala.collection.mutable.StringBuilder;
import org.apache.activemq.leveldb.LevelDBClient;
import scala.runtime.TraitSetter;
import org.slf4j.Logger;
import scala.collection.Seq;
import scala.Function0;
import java.util.ArrayList;
import org.fusesource.hawtdispatch.DispatchQueue;
import scala.reflect.ScalaSignature;
import org.apache.activemq.leveldb.LevelDBStore;

@ScalaSignature(bytes = "\u0006\u0001\t\u001dw!B\u0001\u0003\u0011\u0003i\u0011!E*mCZ,G*\u001a<fY\u0012\u00135\u000b^8sK*\u00111\u0001B\u0001\u000be\u0016\u0004H.[2bi\u0016$'BA\u0003\u0007\u0003\u001daWM^3mI\nT!a\u0002\u0005\u0002\u0011\u0005\u001cG/\u001b<f[FT!!\u0003\u0006\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005Y\u0011aA8sO\u000e\u0001\u0001C\u0001\b\u0010\u001b\u0005\u0011a!\u0002\t\u0003\u0011\u0003\t\"!E*mCZ,G*\u001a<fY\u0012\u00135\u000b^8sKN\u0019qB\u0005\r\u0011\u0005M1R\"\u0001\u000b\u000b\u0003U\tQa]2bY\u0006L!a\u0006\u000b\u0003\r\u0005s\u0017PU3g!\tIB$D\u0001\u001b\u0015\tYB!\u0001\u0003vi&d\u0017BA\u000f\u001b\u0005\raun\u001a\u0005\u0006?=!\t\u0001I\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000351A\u0001\u0005\u0002\u0001EM\u0019\u0011eI\u0014\u0011\u0005\u0011*S\"\u0001\u0003\n\u0005\u0019\"!\u0001\u0004'fm\u0016dGIQ*u_J,\u0007C\u0001\b)\u0013\tI#AA\u000eSKBd\u0017nY1uK\u0012dUM^3m\t\n\u001bFo\u001c:f)J\f\u0017\u000e\u001e\u0005\u0006?\u0005\"\ta\u000b\u000b\u0002YA\u0011a\"\t\u0005\b]\u0005\u0002\r\u0011\"\u00010\u0003\u001d\u0019wN\u001c8fGR,\u0012\u0001\r\t\u0003cYj\u0011A\r\u0006\u0003gQ\nA\u0001\\1oO*\tQ'\u0001\u0003kCZ\f\u0017BA\u001c3\u0005\u0019\u0019FO]5oO\"9\u0011(\ta\u0001\n\u0003Q\u0014aC2p]:,7\r^0%KF$\"a\u000f \u0011\u0005Ma\u0014BA\u001f\u0015\u0005\u0011)f.\u001b;\t\u000f}B\u0014\u0011!a\u0001a\u0005\u0019\u0001\u0010J\u0019\t\r\u0005\u000b\u0003\u0015)\u00031\u0003!\u0019wN\u001c8fGR\u0004\u0003F\u0001!D!\t!u)D\u0001F\u0015\t1E#A\u0003cK\u0006t7/\u0003\u0002I\u000b\na!)Z1o!J|\u0007/\u001a:us\")!*\tC\u0001\u0017\u0006Qq-\u001a;D_:tWm\u0019;\u0015\u0003ABQ!T\u0011\u0005\u00029\u000b!b]3u\u0007>tg.Z2u)\tYt\nC\u0004@\u0019\u0006\u0005\t\u0019\u0001\u0019\t\u000fE\u000b#\u0019!C\u0001%\u0006)\u0011/^3vKV\t1\u000b\u0005\u0002U36\tQK\u0003\u0002W/\u0006a\u0001.Y<uI&\u001c\b/\u0019;dQ*\u0011\u0001LC\u0001\u000bMV\u001cXm]8ve\u000e,\u0017B\u0001.V\u00055!\u0015n\u001d9bi\u000eD\u0017+^3vK\"1A,\tQ\u0001\nM\u000ba!];fk\u0016\u0004\u0003b\u00020\"\u0001\u0004%\taX\u0001\fe\u0016\u0004H.Y=`MJ|W.F\u0001a!\t\u0019\u0012-\u0003\u0002c)\t!Aj\u001c8h\u0011\u001d!\u0017\u00051A\u0005\u0002\u0015\fqB]3qY\u0006LxL\u001a:p[~#S-\u001d\u000b\u0003w\u0019DqaP2\u0002\u0002\u0003\u0007\u0001\r\u0003\u0004iC\u0001\u0006K\u0001Y\u0001\re\u0016\u0004H.Y=`MJ|W\u000e\t\u0005\bU\u0006\u0002\r\u0011\"\u0001l\u0003!\u0019\u0017-^4iiV\u0003X#\u00017\u0011\u0005Mi\u0017B\u00018\u0015\u0005\u001d\u0011un\u001c7fC:Dq\u0001]\u0011A\u0002\u0013\u0005\u0011/\u0001\u0007dCV<\u0007\u000e^+q?\u0012*\u0017\u000f\u0006\u0002<e\"9qh\\A\u0001\u0002\u0004a\u0007B\u0002;\"A\u0003&A.A\u0005dCV<\u0007\u000e^+qA!Ia/\ta\u0001\u0002\u0004%\ta^\u0001\fo\u0006dwl]3tg&|g.F\u0001y!\tI(0D\u0001\"\r\u0011Y\u0018\u0005\u0001?\u0003\u000fM+7o]5p]N\u0011!0 \t\u0003\u001dyL!a \u0002\u0003!Q\u0013\u0018M\\:q_J$\b*\u00198eY\u0016\u0014\b\u0002DA\u0002u\n\u0005\t\u0015!\u0003\u0002\u0006\u0005=\u0011!\u0003;sC:\u001c\bo\u001c:u!\u0011\t9!a\u0003\u000e\u0005\u0005%!bAA\u0002+&!\u0011QBA\u0005\u0005%!&/\u00198ta>\u0014H/C\u0002\u0002\u0004yD!\"a\u0005{\u0005\u0003\u0005\u000b\u0011BA\u000b\u0003!ygn\u00187pO&t\u0007#B\n\u0002\u0018a\\\u0014bAA\r)\tIa)\u001e8di&|g.\r\u0005\u0007?i$\t!!\b\u0015\u000ba\fy\"!\t\t\u0011\u0005\r\u00111\u0004a\u0001\u0003\u000bA\u0001\"a\u0005\u0002\u001c\u0001\u0007\u0011Q\u0003\u0005\n\u0003KQ(\u0019!C\u0001\u0003O\t!C]3ta>t7/Z0dC2d'-Y2lgV\u0011\u0011\u0011\u0006\t\u0007\u0003W\ty#a\r\u000e\u0005\u00055\"BA\u000e5\u0013\u0011\t\t$!\f\u0003\u00151Kgn[3e\u0019&\u001cH\u000f\u0005\u0004\u0014\u0003/\t)d\u000f\t\u0004\u001d\u0005]\u0012bAA\u001d\u0005\t\u0001\"+\u001a9mS\u000e\fG/[8o\rJ\fW.\u001a\u0005\t\u0003{Q\b\u0015!\u0003\u0002*\u0005\u0019\"/Z:q_:\u001cXmX2bY2\u0014\u0017mY6tA!9\u0011\u0011\t>\u0005B\u0005\r\u0013AE8o)J\fgn\u001d9peR4\u0015-\u001b7ve\u0016$2aOA#\u0011!\t9%a\u0010A\u0002\u0005%\u0013!B3se>\u0014\b\u0003BA&\u0003#j!!!\u0014\u000b\u0007\u0005=C'\u0001\u0002j_&!\u00111KA'\u0005-Iu*\u0012=dKB$\u0018n\u001c8\t\u000f\u0005]#\u0010\"\u0011\u0002Z\u0005!rN\u001c+sC:\u001c\bo\u001c:u\u0007>tg.Z2uK\u0012$\u0012a\u000f\u0005\b\u0003;RH\u0011AA0\u0003)!\u0017n]2p]:,7\r\u001e\u000b\u0004w\u0005\u0005\u0004\u0002CA2\u00037\u0002\r!!\u001a\u0002\u0005\r\u0014\u0007c\u0001+\u0002h%\u0019\u0011\u0011N+\u0003\tQ\u000b7o\u001b\u0005\b\u0003[RH\u0011AA8\u0003\u00111\u0017-\u001b7\u0015\u0007m\n\t\b\u0003\u0005\u0002t\u0005-\u0004\u0019AA;\u0003\ri7o\u001a\t\u0005\u0003o\niHD\u0002\u0014\u0003sJ1!a\u001f\u0015\u0003\u0019\u0001&/\u001a3fM&\u0019q'a \u000b\u0007\u0005mD\u0003C\u0005\u0002\u0004j\u0004\r\u0011\"\u0001\u0002\u0006\u00069\u0001.\u00198eY\u0016\u0014XCAAD!\u0015\u0019\u0012q\u0003\n<\u0011%\tYI\u001fa\u0001\n\u0003\ti)A\u0006iC:$G.\u001a:`I\u0015\fHcA\u001e\u0002\u0010\"Iq(!#\u0002\u0002\u0003\u0007\u0011q\u0011\u0005\t\u0003'S\b\u0015)\u0003\u0002\b\u0006A\u0001.\u00198eY\u0016\u0014\b\u0005C\u0004\u0002\u0018j$\t!!'\u0002%=tGK]1ogB|'\u000f^\"p[6\fg\u000e\u001a\u000b\u0004w\u0005m\u0005bBAO\u0003+\u0003\rAE\u0001\bG>lW.\u00198e\u0011\u001d\t\tK\u001fC\u0001\u0003G\u000bAB]3rk\u0016\u001cHo\u0018;iK:$b!!*\u00028\u0006\u0005GcA\u001e\u0002(\"A\u00111MAP\u0001\u0004\tI\u000b\u0005\u0004\u0014\u0003/\tYk\u000f\t\u0005\u0003[\u000b\u0019,\u0004\u0002\u00020*\u0019\u0011\u0011W,\u0002\u000f!\fw\u000f\u001e2vM&!\u0011QWAX\u0005\u0019\u0011UO\u001a4fe\"A\u0011\u0011XAP\u0001\u0004\tY,\u0001\u0004bGRLwN\u001c\t\u0005\u0003[\u000bi,\u0003\u0003\u0002@\u0006=&aC!tG&L')\u001e4gKJDq!a1\u0002 \u0002\u0007!#\u0001\u0003c_\u0012L\bbBAdu\u0012\u0005\u0011\u0011Z\u0001\be\u0016\fX/Z:u)\u0019\tY-a4\u0002RR\u00191(!4\t\u0011\u0005\r\u0014Q\u0019a\u0001\u0003gA\u0001\"!/\u0002F\u0002\u0007\u00111\u0018\u0005\b\u0003\u0007\f)\r1\u0001\u0013\u0011\u001d\t)N\u001fC\u0001\u0003\u000b\u000b\u0001C]3ta>t7/Z0iC:$G.\u001a:\t\u001d\u0005e'\u0010%A\u0002\u0002\u0003%I!a7\u0002\u0010\u0005y1/\u001e9fe\u0012\"(/\u00198ta>\u0014H/\u0006\u0002\u0002\u0006!Y\u0011q\\\u0011A\u0002\u0003\u0007I\u0011AAq\u0003=9\u0018\r\\0tKN\u001c\u0018n\u001c8`I\u0015\fHcA\u001e\u0002d\"Aq(!8\u0002\u0002\u0003\u0007\u0001\u0010C\u0004\u0002h\u0006\u0002\u000b\u0015\u0002=\u0002\u0019]\fGnX:fgNLwN\u001c\u0011\t\u0015\u0005-\u0018\u00051AA\u0002\u0013\u0005q/\u0001\tue\u0006t7OZ3s?N,7o]5p]\"Y\u0011q^\u0011A\u0002\u0003\u0007I\u0011AAy\u0003Q!(/\u00198tM\u0016\u0014xl]3tg&|gn\u0018\u0013fcR\u00191(a=\t\u0011}\ni/!AA\u0002aDq!a>\"A\u0003&\u00010A\tue\u0006t7OZ3s?N,7o]5p]\u0002B\u0001\"a?\"\u0001\u0004%\taL\u0001\u0007gR\fG/^:\t\u0013\u0005}\u0018\u00051A\u0005\u0002\t\u0005\u0011AC:uCR,8o\u0018\u0013fcR\u00191Ha\u0001\t\u0011}\ni0!AA\u0002ABqAa\u0002\"A\u0003&\u0001'A\u0004ti\u0006$Xo\u001d\u0011\t\u000f\t-\u0011\u0005\"\u0011\u0003\u000e\u0005a1M]3bi\u0016\u001cE.[3oiV\u0011!q\u0002\t\u0004I\tE\u0011b\u0001B\n\t\tiA*\u001a<fY\u0012\u00135\t\\5f]RDqAa\u0006\"\t\u0003\nI&A\u0004e_N#\u0018M\u001d;\t\u0011\tm\u0011\u00051A\u0005\u0002-\fqa\u001d;paB,G\rC\u0005\u0003 \u0005\u0002\r\u0011\"\u0001\u0003\"\u0005Y1\u000f^8qa\u0016$w\fJ3r)\rY$1\u0005\u0005\t\u007f\tu\u0011\u0011!a\u0001Y\"9!qE\u0011!B\u0013a\u0017\u0001C:u_B\u0004X\r\u001a\u0011\t\u000f\t-\u0012\u0005\"\u0011\u0003.\u00051Am\\*u_B$2a\u000fB\u0018\u0011!\u0011\tD!\u000bA\u0002\tM\u0012aB:u_B\u0004XM\u001d\t\u0005\u0005k\u0011I$\u0004\u0002\u00038)\u00111DB\u0005\u0005\u0005w\u00119D\u0001\bTKJ4\u0018nY3Ti>\u0004\b/\u001a:\t\u000f\t}\u0012\u0005\"\u0001\u0003B\u0005I\"/Z:uCJ$xl\u001d7bm\u0016|6m\u001c8oK\u000e$\u0018n\u001c8t+\u0005Y\u0004b\u0002B#C\u0011\u0005!\u0011I\u0001\u0018gR\f'\u000f^0tY\u00064XmX2p]:,7\r^5p]NDqA!\u0013\"\t\u0003\u0011Y%\u0001\tde\u0016\fG/Z0ue\u0006t7\u000f]8siV\u0011!Q\n\t\u0005\u0003\u000f\u0011y%\u0003\u0003\u0003R\u0005%!\u0001\u0004+daR\u0013\u0018M\\:q_J$\bb\u0002B+C\u0011\u0005!qK\u0001\u0011gR|\u0007oX2p]:,7\r^5p]N$2a\u000fB-\u0011!\t\u0019Ga\u0015A\u0002\u0005\u0015\u0004\u0002\u0003B/C\u0001\u0007I\u0011A0\u0002']\fGnX1qa\u0016tGm\u00189pg&$\u0018n\u001c8\t\u0013\t\u0005\u0014\u00051A\u0005\u0002\t\r\u0014aF<bY~\u000b\u0007\u000f]3oI~\u0003xn]5uS>tw\fJ3r)\rY$Q\r\u0005\t\u007f\t}\u0013\u0011!a\u0001A\"9!\u0011N\u0011!B\u0013\u0001\u0017\u0001F<bY~\u000b\u0007\u000f]3oI~\u0003xn]5uS>t\u0007\u0005\u0003\u0005\u0003n\u0005\u0002\r\u0011\"\u0001`\u0003E9\u0018\r\\0baB,g\u000eZ0pM\u001a\u001cX\r\u001e\u0005\n\u0005c\n\u0003\u0019!C\u0001\u0005g\nQc^1m?\u0006\u0004\b/\u001a8e?>4gm]3u?\u0012*\u0017\u000fF\u0002<\u0005kB\u0001b\u0010B8\u0003\u0003\u0005\r\u0001\u0019\u0005\b\u0005s\n\u0003\u0015)\u0003a\u0003I9\u0018\r\\0baB,g\u000eZ0pM\u001a\u001cX\r\u001e\u0011\t\u0011\tu\u0014\u00051A\u0005\u0002}\u000b\u0001b^1m?\u0012\fG/\u001a\u0005\n\u0005\u0003\u000b\u0003\u0019!C\u0001\u0005\u0007\u000bAb^1m?\u0012\fG/Z0%KF$2a\u000fBC\u0011!y$qPA\u0001\u0002\u0004\u0001\u0007b\u0002BEC\u0001\u0006K\u0001Y\u0001\no\u0006dw\fZ1uK\u0002BCAa\"\u0003\u000eB\u00191Ca$\n\u0007\tEEC\u0001\u0005w_2\fG/\u001b7f\u0011\u001d\u0011)*\tC\u0001\u0005\u0003\nAb]3oI~;\u0018\r\\0bG.D\u0011B!'\"\u0005\u0004%\tAa'\u0002'A,g\u000eZ5oO~cwnZ0sK6|g/Z:\u0016\u0005\tu\u0005#BA\u0016\u0005?\u0003\u0017\u0002\u0002BQ\u0003[\u0011\u0011\"\u0011:sCfd\u0015n\u001d;\t\u0011\t\u0015\u0016\u0005)A\u0005\u0005;\u000bA\u0003]3oI&twm\u00187pO~\u0013X-\\8wKN\u0004\u0003b\u0002BUC\u0011\u0005!1V\u0001\fo\u0006dw\f[1oI2,'\u000f\u0006\u0003\u0002\b\n5\u0006b\u0002BX\u0005O\u0003\r\u0001_\u0001\bg\u0016\u001c8/[8o\u0011\u001d\u0011\u0019,\tC\u0001\u0005k\u000b\u0001\u0003\u001e:b]N4WM]0nSN\u001c\u0018N\\4\u0015\u0007\u0001\u00149\f\u0003\u0005\u0003:\nE\u0006\u0019\u0001B^\u0003\u0015\u0019H/\u0019;f!\u0011\u0011iLa1\u000e\u0005\t}&b\u0001Ba\u0005\u0005\u0019A\r^8\n\t\t\u0015'q\u0018\u0002\r'ft7MU3ta>t7/\u001a")
public class SlaveLevelDBStore extends LevelDBStore implements ReplicatedLevelDBStoreTrait
{
    private String connect;
    private final DispatchQueue queue;
    private long replay_from;
    private boolean caughtUp;
    private Session wal_session;
    private Session transfer_session;
    private String status;
    private boolean stopped;
    private long wal_append_position;
    private long wal_append_offset;
    private volatile long wal_date;
    private final ArrayList<Object> pending_log_removes;
    private String securityToken;
    
    public static void trace(final Throwable e) {
        SlaveLevelDBStore$.MODULE$.trace(e);
    }
    
    public static void trace(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.trace(e, m, args);
    }
    
    public static void trace(final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.trace(m, args);
    }
    
    public static void debug(final Throwable e) {
        SlaveLevelDBStore$.MODULE$.debug(e);
    }
    
    public static void debug(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.debug(e, m, args);
    }
    
    public static void debug(final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.debug(m, args);
    }
    
    public static void info(final Throwable e) {
        SlaveLevelDBStore$.MODULE$.info(e);
    }
    
    public static void info(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.info(e, m, args);
    }
    
    public static void info(final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.info(m, args);
    }
    
    public static void warn(final Throwable e) {
        SlaveLevelDBStore$.MODULE$.warn(e);
    }
    
    public static void warn(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.warn(e, m, args);
    }
    
    public static void warn(final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.warn(m, args);
    }
    
    public static void error(final Throwable e) {
        SlaveLevelDBStore$.MODULE$.error(e);
    }
    
    public static void error(final Throwable e, final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.error(e, m, args);
    }
    
    public static void error(final Function0<String> m, final Seq<Object> args) {
        SlaveLevelDBStore$.MODULE$.error(m, args);
    }
    
    public static void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger x$1) {
        SlaveLevelDBStore$.MODULE$.org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(x$1);
    }
    
    public static Logger log() {
        return SlaveLevelDBStore$.MODULE$.log();
    }
    
    @Override
    public String securityToken() {
        return this.securityToken;
    }
    
    @TraitSetter
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
    
    public String connect() {
        return this.connect;
    }
    
    public void connect_$eq(final String x$1) {
        this.connect = x$1;
    }
    
    public void setConnect(final String x$1) {
        this.connect = x$1;
    }
    
    public DispatchQueue queue() {
        return this.queue;
    }
    
    public long replay_from() {
        return this.replay_from;
    }
    
    public void replay_from_$eq(final long x$1) {
        this.replay_from = x$1;
    }
    
    public boolean caughtUp() {
        return this.caughtUp;
    }
    
    public void caughtUp_$eq(final boolean x$1) {
        this.caughtUp = x$1;
    }
    
    public Session wal_session() {
        return this.wal_session;
    }
    
    public void wal_session_$eq(final Session x$1) {
        this.wal_session = x$1;
    }
    
    public Session transfer_session() {
        return this.transfer_session;
    }
    
    public void transfer_session_$eq(final Session x$1) {
        this.transfer_session = x$1;
    }
    
    public String status() {
        return this.status;
    }
    
    public void status_$eq(final String x$1) {
        this.status = x$1;
    }
    
    @Override
    public LevelDBClient createClient() {
        return (LevelDBClient)new SlaveLevelDBStore$$anon.SlaveLevelDBStore$$anon$1(this);
    }
    
    @Override
    public void doStart() {
        this.queue().setLabel(new StringBuilder().append((Object)"slave: ").append((Object)this.node_id()).toString());
        this.client().init();
        if (this.purgeOnStatup()) {
            this.purgeOnStatup_$eq(false);
            this.db().client().locked_purge();
            SlaveLevelDBStore$.MODULE$.info((Function0<String>)new SlaveLevelDBStore$$anonfun$doStart.SlaveLevelDBStore$$anonfun$doStart$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
        }
        FileSupport$.MODULE$.toRichFile(this.db().client().dirtyIndexFile()).recursiveDelete();
        FileSupport$.MODULE$.toRichFile(this.db().client().plistIndexFile()).recursiveDelete();
        this.start_slave_connections();
    }
    
    public boolean stopped() {
        return this.stopped;
    }
    
    public void stopped_$eq(final boolean x$1) {
        this.stopped = x$1;
    }
    
    @Override
    public void doStop(final ServiceStopper stopper) {
        final CountDownLatch latch = new CountDownLatch(1);
        this.stop_connections(package$.MODULE$.$up((Function0)new SlaveLevelDBStore$$anonfun$doStop.SlaveLevelDBStore$$anonfun$doStop$1(this, latch)));
        latch.await();
        this.client().stop();
    }
    
    public void restart_slave_connections() {
        this.stop_connections(package$.MODULE$.$up((Function0)new SlaveLevelDBStore$$anonfun$restart_slave_connections.SlaveLevelDBStore$$anonfun$restart_slave_connections$1(this)));
    }
    
    public void start_slave_connections() {
        final TcpTransport transport = this.create_transport();
        this.status_$eq(new StringBuilder().append((Object)"Attaching to master: ").append((Object)this.connect()).toString());
        SlaveLevelDBStore$.MODULE$.info((Function0<String>)new SlaveLevelDBStore$$anonfun$start_slave_connections.SlaveLevelDBStore$$anonfun$start_slave_connections$1(this), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
        this.wal_session_$eq(new Session((Transport)transport, (Function1<Session, BoxedUnit>)new SlaveLevelDBStore$$anonfun$start_slave_connections.SlaveLevelDBStore$$anonfun$start_slave_connections$2(this)));
        this.wal_session().start();
    }
    
    public TcpTransport create_transport() {
        final TcpTransport transport = new TcpTransport();
        transport.setBlockingExecutor(this.blocking_executor());
        transport.setDispatchQueue(this.queue());
        transport.connecting(new URI(this.connect()), (URI)null);
        return transport;
    }
    
    public void stop_connections(final Task cb) {
        Task then = package$.MODULE$.$up((Function0)new SlaveLevelDBStore$$anonfun.SlaveLevelDBStore$$anonfun$1(this, cb));
        final Session wal_session_copy = this.wal_session();
        if (wal_session_copy != null) {
            this.wal_session_$eq(null);
            final Task next = then;
            then = package$.MODULE$.$up((Function0)new SlaveLevelDBStore$$anonfun$stop_connections.SlaveLevelDBStore$$anonfun$stop_connections$1(this, wal_session_copy, next));
        }
        final Session transfer_session_copy = this.transfer_session();
        if (transfer_session_copy != null) {
            this.transfer_session_$eq(null);
            final Task next2 = then;
            then = package$.MODULE$.$up((Function0)new SlaveLevelDBStore$$anonfun$stop_connections.SlaveLevelDBStore$$anonfun$stop_connections$2(this, transfer_session_copy, next2));
        }
        then.run();
    }
    
    public long wal_append_position() {
        return this.wal_append_position;
    }
    
    public void wal_append_position_$eq(final long x$1) {
        this.wal_append_position = x$1;
    }
    
    public long wal_append_offset() {
        return this.wal_append_offset;
    }
    
    public void wal_append_offset_$eq(final long x$1) {
        this.wal_append_offset = x$1;
    }
    
    public long wal_date() {
        return this.wal_date;
    }
    
    public void wal_date_$eq(final long x$1) {
        this.wal_date = x$1;
    }
    
    public void send_wal_ack() {
        this.queue().assertExecuting();
        if (this.caughtUp() && !this.stopped() && this.wal_session() != null) {
            final WalAck ack = new WalAck();
            ack.position = this.wal_append_position();
            this.wal_session().send_replication_frame(ReplicationSupport$.MODULE$.ACK_ACTION(), ack);
            if (this.replay_from() != ack.position) {
                final long old_replay_from = this.replay_from();
                this.replay_from_$eq(ack.position);
                package$.MODULE$.ExecutorWrapper((Executor)this.client().writeExecutor()).apply((Function0)new SlaveLevelDBStore$$anonfun$send_wal_ack.SlaveLevelDBStore$$anonfun$send_wal_ack$1(this, ack, old_replay_from));
            }
        }
    }
    
    public ArrayList<Object> pending_log_removes() {
        return this.pending_log_removes;
    }
    
    public Function1<Object, BoxedUnit> wal_handler(final Session session) {
        return (Function1<Object, BoxedUnit>)new SlaveLevelDBStore$$anonfun$wal_handler.SlaveLevelDBStore$$anonfun$wal_handler$1(this, session);
    }
    
    public long transfer_missing(final SyncResponse state) {
        final File dirty_index = this.client().dirtyIndexFile();
        FileSupport$.MODULE$.toRichFile(dirty_index).recursiveDelete();
        final File snapshot_index = this.client().snapshotIndexFile(state.snapshot_position);
        final TcpTransport transport = new TcpTransport();
        transport.setBlockingExecutor(this.blocking_executor());
        transport.setDispatchQueue(this.queue());
        transport.connecting(new URI(this.connect()), (URI)null);
        SlaveLevelDBStore$.MODULE$.debug((Function0<String>)new SlaveLevelDBStore$$anonfun$transfer_missing.SlaveLevelDBStore$$anonfun$transfer_missing$1(this, state), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
        this.transfer_session_$eq(new Session((Transport)transport, (Function1<Session, BoxedUnit>)new SlaveLevelDBStore$$anonfun$transfer_missing.SlaveLevelDBStore$$anonfun$transfer_missing$2(this, state, dirty_index, snapshot_index, transport)));
        this.transfer_session().start();
        return state.snapshot_position;
    }
    
    public String getConnect() {
        return this.connect();
    }
    
    public SlaveLevelDBStore() {
        ReplicatedLevelDBStoreTrait$class.$init$(this);
        this.connect = "tcp://0.0.0.0:61619";
        this.queue = package$.MODULE$.createQueue("leveldb replication slave");
        this.replay_from = 0L;
        this.caughtUp = false;
        this.status = "initialized";
        this.stopped = false;
        this.wal_append_position = 0L;
        this.wal_append_offset = 0L;
        this.wal_date = 0L;
        this.pending_log_removes = new ArrayList<Object>();
    }
    
    public class Session extends TransportHandler
    {
        public final Function1<Session, BoxedUnit> org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$on_login;
        private final LinkedList<Function1<ReplicationFrame, BoxedUnit>> response_callbacks;
        private Function1<Object, BoxedUnit> handler;
        
        public LinkedList<Function1<ReplicationFrame, BoxedUnit>> response_callbacks() {
            return this.response_callbacks;
        }
        
        @Override
        public void onTransportFailure(final IOException error) {
            if (this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer().isStarted()) {
                SlaveLevelDBStore$.MODULE$.warn((Function0<String>)new SlaveLevelDBStore$Session$$anonfun$onTransportFailure.SlaveLevelDBStore$Session$$anonfun$onTransportFailure$2(this, error), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
                package$.MODULE$.DispatchQueueWrapper(this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer().queue()).after(1L, TimeUnit.SECONDS, (Function0)new SlaveLevelDBStore$Session$$anonfun$onTransportFailure.SlaveLevelDBStore$Session$$anonfun$onTransportFailure$1(this));
            }
            super.onTransportFailure(error);
        }
        
        @Override
        public void onTransportConnected() {
            super.onTransportConnected();
            final Login login = new Login();
            login.security_token = this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer().securityToken();
            login.node_id = this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer().node_id();
            this.request_then(ReplicationSupport$.MODULE$.LOGIN_ACTION(), login, (Function1<Buffer, BoxedUnit>)new SlaveLevelDBStore$Session$$anonfun$onTransportConnected.SlaveLevelDBStore$Session$$anonfun$onTransportConnected$1(this));
        }
        
        public void disconnect(final Task cb) {
            package$.MODULE$.DispatchQueueWrapper(this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer().queue()).apply((Function0)new SlaveLevelDBStore$Session$$anonfun$disconnect.SlaveLevelDBStore$Session$$anonfun$disconnect$1(this, cb));
        }
        
        public void fail(final String msg) {
            SlaveLevelDBStore$.MODULE$.error((Function0<String>)new SlaveLevelDBStore$Session$$anonfun$fail.SlaveLevelDBStore$Session$$anonfun$fail$1(this, msg), (Seq<Object>)Predef$.MODULE$.genericWrapArray((Object)new Object[0]));
            super.transport().stop(package$.MODULE$.NOOP());
        }
        
        public Function1<Object, BoxedUnit> handler() {
            return this.handler;
        }
        
        public void handler_$eq(final Function1<Object, BoxedUnit> x$1) {
            this.handler = x$1;
        }
        
        public void onTransportCommand(final Object command) {
            this.handler().apply(command);
        }
        
        public void request_then(final AsciiBuffer action, final Object body, final Function1<Buffer, BoxedUnit> cb) {
            this.request(action, body, (Function1<ReplicationFrame, BoxedUnit>)new SlaveLevelDBStore$Session$$anonfun$request_then.SlaveLevelDBStore$Session$$anonfun$request_then$1(this, action, (Function1)cb));
        }
        
        public void request(final AsciiBuffer action, final Object body, final Function1<ReplicationFrame, BoxedUnit> cb) {
            this.response_callbacks().addLast(cb);
            this.send_replication_frame(action, body);
        }
        
        public Function1<Object, BoxedUnit> response_handler() {
            return (Function1<Object, BoxedUnit>)new SlaveLevelDBStore$Session$$anonfun$response_handler.SlaveLevelDBStore$Session$$anonfun$response_handler$1(this);
        }
        
        public /* synthetic */ SlaveLevelDBStore org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$$outer() {
            return SlaveLevelDBStore.this;
        }
        
        public Session(final Transport transport, final Function1<Session, BoxedUnit> on_login) {
            this.org$apache$activemq$leveldb$replicated$SlaveLevelDBStore$Session$$on_login = on_login;
            if (SlaveLevelDBStore.this == null) {
                throw null;
            }
            super(transport);
            this.response_callbacks = new LinkedList<Function1<ReplicationFrame, BoxedUnit>>();
            this.handler = this.response_handler();
        }
    }
}
