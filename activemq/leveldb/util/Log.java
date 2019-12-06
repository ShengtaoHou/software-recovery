// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.util;

import scala.collection.Seq;
import scala.Function0;
import org.slf4j.Logger;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u00055t!B\u0001\u0003\u0011\u0003i\u0011a\u0001'pO*\u00111\u0001B\u0001\u0005kRLGN\u0003\u0002\u0006\r\u00059A.\u001a<fY\u0012\u0014'BA\u0004\t\u0003!\t7\r^5wK6\f(BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u000f\u001f5\t!AB\u0003\u0011\u0005!\u0005\u0011CA\u0002M_\u001e\u001c\"a\u0004\n\u0011\u0005M1R\"\u0001\u000b\u000b\u0003U\tQa]2bY\u0006L!a\u0006\u000b\u0003\r\u0005s\u0017PU3g\u0011\u0015Ir\u0002\"\u0001\u001b\u0003\u0019a\u0014N\\5u}Q\tQ\u0002C\u0003\u001d\u001f\u0011\u0005Q$A\u0003baBd\u0017\u0010F\u0002\u001f\u0003s\u0001\"AD\u0010\u0007\u000fA\u0011\u0001\u0013aA\u0001AM\u0011qD\u0005\u0005\u0006E}!\taI\u0001\u0007I%t\u0017\u000e\u001e\u0013\u0015\u0003\u0011\u0002\"aE\u0013\n\u0005\u0019\"\"\u0001B+oSRDq\u0001K\u0010C\u0002\u0013\u0005\u0011&A\u0002m_\u001e,\u0012A\u000b\t\u0003W9j\u0011\u0001\f\u0006\u0003[)\tQa\u001d7gi)L!a\f\u0017\u0003\r1{wmZ3s\u0011\u0019\tt\u0004)A\u0005U\u0005!An\\4!\u0011\u0015\u0019t\u0004\"\u00035\u0003\u00191wN]7biR\u0019Q'P \u0011\u0005YZT\"A\u001c\u000b\u0005aJ\u0014\u0001\u00027b]\u001eT\u0011AO\u0001\u0005U\u00064\u0018-\u0003\u0002=o\t11\u000b\u001e:j]\u001eDQA\u0010\u001aA\u0002U\nq!\\3tg\u0006<W\rC\u0003Ae\u0001\u0007\u0011)\u0001\u0003be\u001e\u001c\bc\u0001\"K\u001b:\u00111\t\u0013\b\u0003\t\u001ek\u0011!\u0012\u0006\u0003\r2\ta\u0001\u0010:p_Rt\u0014\"A\u000b\n\u0005%#\u0012a\u00029bG.\fw-Z\u0005\u0003\u00172\u00131aU3r\u0015\tIE\u0003\u0005\u0002\u0014\u001d&\u0011q\n\u0006\u0002\u0004\u0003:L\b\"B) \t\u0003\u0011\u0016!B3se>\u0014Hc\u0001\u0013T1\"1A\u000b\u0015CA\u0002U\u000b\u0011!\u001c\t\u0004'Y+\u0014BA,\u0015\u0005!a$-\u001f8b[\u0016t\u0004\"\u0002!Q\u0001\u0004I\u0006cA\n[\u001b&\u00111\f\u0006\u0002\u000byI,\u0007/Z1uK\u0012t\u0004\"B) \t\u0003iF\u0003\u0002\u0013_G\u0012DQa\u0018/A\u0002\u0001\f\u0011!\u001a\t\u0003m\u0005L!AY\u001c\u0003\u0013QC'o\\<bE2,\u0007B\u0002+]\t\u0003\u0007Q\u000bC\u0003A9\u0002\u0007\u0011\fC\u0003R?\u0011\u0005a\r\u0006\u0002%O\")q,\u001aa\u0001A\")\u0011n\bC\u0001U\u0006!q/\u0019:o)\r!3\u000e\u001c\u0005\u0007)\"$\t\u0019A+\t\u000b\u0001C\u0007\u0019A-\t\u000b%|B\u0011\u00018\u0015\t\u0011z\u0007/\u001d\u0005\u0006?6\u0004\r\u0001\u0019\u0005\u0007)6$\t\u0019A+\t\u000b\u0001k\u0007\u0019A-\t\u000b%|B\u0011A:\u0015\u0005\u0011\"\b\"B0s\u0001\u0004\u0001\u0007\"\u0002< \t\u00039\u0018\u0001B5oM>$2\u0001\n=z\u0011\u0019!V\u000f\"a\u0001+\")\u0001)\u001ea\u00013\")ao\bC\u0001wR!A\u0005`?\u007f\u0011\u0015y&\u00101\u0001a\u0011\u0019!&\u0010\"a\u0001+\")\u0001I\u001fa\u00013\"1ao\bC\u0001\u0003\u0003!2\u0001JA\u0002\u0011\u0015yv\u00101\u0001a\u0011\u001d\t9a\bC\u0001\u0003\u0013\tQ\u0001Z3ck\u001e$R\u0001JA\u0006\u0003\u001bAq\u0001VA\u0003\t\u0003\u0007Q\u000b\u0003\u0004A\u0003\u000b\u0001\r!\u0017\u0005\b\u0003\u000fyB\u0011AA\t)\u001d!\u00131CA\u000b\u0003/AaaXA\b\u0001\u0004\u0001\u0007b\u0002+\u0002\u0010\u0011\u0005\r!\u0016\u0005\u0007\u0001\u0006=\u0001\u0019A-\t\u000f\u0005\u001dq\u0004\"\u0001\u0002\u001cQ\u0019A%!\b\t\r}\u000bI\u00021\u0001a\u0011\u001d\t\tc\bC\u0001\u0003G\tQ\u0001\u001e:bG\u0016$R\u0001JA\u0013\u0003OAq\u0001VA\u0010\t\u0003\u0007Q\u000b\u0003\u0004A\u0003?\u0001\r!\u0017\u0005\b\u0003CyB\u0011AA\u0016)\u001d!\u0013QFA\u0018\u0003cAaaXA\u0015\u0001\u0004\u0001\u0007b\u0002+\u0002*\u0011\u0005\r!\u0016\u0005\u0007\u0001\u0006%\u0002\u0019A-\t\u000f\u0005\u0005r\u0004\"\u0001\u00026Q\u0019A%a\u000e\t\r}\u000b\u0019\u00041\u0001a\u0011\u001d\tYd\u0007a\u0001\u0003{\tQa\u00197buj\u0004D!a\u0010\u0002RA1\u0011\u0011IA$\u0003\u001br1aEA\"\u0013\r\t)\u0005F\u0001\u0007!J,G-\u001a4\n\t\u0005%\u00131\n\u0002\u0006\u00072\f7o\u001d\u0006\u0004\u0003\u000b\"\u0002\u0003BA(\u0003#b\u0001\u0001\u0002\u0007\u0002T\u0005e\u0012\u0011!A\u0001\u0006\u0003\t)FA\u0002`IE\n2!a\u0016N!\r\u0019\u0012\u0011L\u0005\u0004\u00037\"\"a\u0002(pi\"Lgn\u001a\u0005\u00079=!\t!a\u0018\u0015\u0007y\t\t\u0007C\u0004\u0002d\u0005u\u0003\u0019A\u001b\u0002\t9\fW.\u001a\u0005\u00079=!\t!a\u001a\u0015\u0007y\tI\u0007C\u0004\u0002l\u0005\u0015\u0004\u0019\u0001\u0016\u0002\u000bY\fG.^3")
public interface Log
{
    void org$apache$activemq$leveldb$util$Log$_setter_$log_$eq(final Logger p0);
    
    Logger log();
    
    void error(final Function0<String> p0, final Seq<Object> p1);
    
    void error(final Throwable p0, final Function0<String> p1, final Seq<Object> p2);
    
    void error(final Throwable p0);
    
    void warn(final Function0<String> p0, final Seq<Object> p1);
    
    void warn(final Throwable p0, final Function0<String> p1, final Seq<Object> p2);
    
    void warn(final Throwable p0);
    
    void info(final Function0<String> p0, final Seq<Object> p1);
    
    void info(final Throwable p0, final Function0<String> p1, final Seq<Object> p2);
    
    void info(final Throwable p0);
    
    void debug(final Function0<String> p0, final Seq<Object> p1);
    
    void debug(final Throwable p0, final Function0<String> p1, final Seq<Object> p2);
    
    void debug(final Throwable p0);
    
    void trace(final Function0<String> p0, final Seq<Object> p1);
    
    void trace(final Throwable p0, final Function0<String> p1, final Seq<Object> p2);
    
    void trace(final Throwable p0);
}
