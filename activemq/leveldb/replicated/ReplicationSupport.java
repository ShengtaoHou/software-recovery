// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.fusesource.hawtbuf.AsciiBuffer;
import java.nio.MappedByteBuffer;
import java.io.File;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001\u0005=q!B\u0001\u0003\u0011\u0003i\u0011A\u0005*fa2L7-\u0019;j_:\u001cV\u000f\u001d9peRT!a\u0001\u0003\u0002\u0015I,\u0007\u000f\\5dCR,GM\u0003\u0002\u0006\r\u00059A.\u001a<fY\u0012\u0014'BA\u0004\t\u0003!\t7\r^5wK6\f(BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u000f\u001f5\t!AB\u0003\u0011\u0005!\u0005\u0011C\u0001\nSKBd\u0017nY1uS>t7+\u001e9q_J$8CA\b\u0013!\t\u0019b#D\u0001\u0015\u0015\u0005)\u0012!B:dC2\f\u0017BA\f\u0015\u0005\u0019\te.\u001f*fM\")\u0011d\u0004C\u00015\u00051A(\u001b8jiz\"\u0012!\u0004\u0005\b9=\u0011\r\u0011\"\u0001\u001e\u0003)9\u0016\tT0B\u0007RKuJT\u000b\u0002=A\u0011q\u0004J\u0007\u0002A)\u0011\u0011EI\u0001\bQ\u0006<HOY;g\u0015\t\u0019#\"\u0001\u0006gkN,7o\\;sG\u0016L!!\n\u0011\u0003\u0017\u0005\u001b8-[5Ck\u001a4WM\u001d\u0005\u0007O=\u0001\u000b\u0011\u0002\u0010\u0002\u0017]\u000bEjX!D)&{e\n\t\u0005\bS=\u0011\r\u0011\"\u0001\u001e\u00031aujR%O?\u0006\u001bE+S(O\u0011\u0019Ys\u0002)A\u0005=\u0005iAjT$J\u001d~\u000b5\tV%P\u001d\u0002Bq!L\bC\u0002\u0013\u0005Q$A\u0006T3:\u001bu,Q\"U\u0013>s\u0005BB\u0018\u0010A\u0003%a$\u0001\u0007T3:\u001bu,Q\"U\u0013>s\u0005\u0005C\u00042\u001f\t\u0007I\u0011A\u000f\u0002\u0015\u001d+EkX!D)&{e\n\u0003\u00044\u001f\u0001\u0006IAH\u0001\f\u000f\u0016#v,Q\"U\u0013>s\u0005\u0005C\u00046\u001f\t\u0007I\u0011A\u000f\u0002\u0015\u0005\u001b5jX!D)&{e\n\u0003\u00048\u001f\u0001\u0006IAH\u0001\f\u0003\u000e[u,Q\"U\u0013>s\u0005\u0005C\u0004:\u001f\t\u0007I\u0011A\u000f\u0002\u0013=[u,Q\"U\u0013>s\u0005BB\u001e\u0010A\u0003%a$\u0001\u0006P\u0017~\u000b5\tV%P\u001d\u0002Bq!P\bC\u0002\u0013\u0005Q$A\tE\u0013N\u001buJ\u0014(F\u0007R{\u0016i\u0011+J\u001f:CaaP\b!\u0002\u0013q\u0012A\u0005#J'\u000e{eJT#D)~\u000b5\tV%P\u001d\u0002Bq!Q\bC\u0002\u0013\u0005Q$\u0001\u0007F%J{%kX!D)&{e\n\u0003\u0004D\u001f\u0001\u0006IAH\u0001\u000e\u000bJ\u0013vJU0B\u0007RKuJ\u0014\u0011\t\u000f\u0015{!\u0019!C\u0001;\u0005\tBjT$`\t\u0016cU\tV#`\u0003\u000e#\u0016j\u0014(\t\r\u001d{\u0001\u0015!\u0003\u001f\u0003IaujR0E\u000b2+E+R0B\u0007RKuJ\u0014\u0011\t\u000b%{A\u0011\u0001&\u0002\u000bUtW.\u00199\u0015\u0005-s\u0005CA\nM\u0013\tiEC\u0001\u0003V]&$\b\"B(I\u0001\u0004\u0001\u0016A\u00022vM\u001a,'\u000f\u0005\u0002R-6\t!K\u0003\u0002T)\u0006\u0019a.[8\u000b\u0003U\u000bAA[1wC&\u0011qK\u0015\u0002\u0011\u001b\u0006\u0004\b/\u001a3CsR,')\u001e4gKJDQ!W\b\u0005\u0002i\u000b1!\\1q)\u0015\u00016l\u00195k\u0011\u0015a\u0006\f1\u0001^\u0003\u00111\u0017\u000e\\3\u0011\u0005y\u000bW\"A0\u000b\u0005\u0001$\u0016AA5p\u0013\t\u0011wL\u0001\u0003GS2,\u0007\"\u00023Y\u0001\u0004)\u0017AB8gMN,G\u000f\u0005\u0002\u0014M&\u0011q\r\u0006\u0002\u0005\u0019>tw\rC\u0003j1\u0002\u0007Q-\u0001\u0004mK:<G\u000f\u001b\u0005\u0006Wb\u0003\r\u0001\\\u0001\te\u0016\fGm\u00148msB\u00111#\\\u0005\u0003]R\u0011qAQ8pY\u0016\fg\u000eC\u0003q\u001f\u0011\u0005\u0011/A\u0003ti\u0006\u001c\b\u000e\u0006\u0002Le\")1o\u001ca\u0001;\u0006IA-\u001b:fGR|'/\u001f\u0005\u0006k>!\tA^\u0001\u000fG>\u0004\u0018pX:u_J,w\fZ5s)\rYu/\u001f\u0005\u0006qR\u0004\r!X\u0001\u0005MJ|W\u000eC\u0003{i\u0002\u0007Q,\u0001\u0002u_\")Ap\u0004C\u0001{\u0006Y1\u000f^1tQ~\u001bG.Z1s)\tYe\u0010C\u0003tw\u0002\u0007Q\fC\u0004\u0002\u0002=!\t!a\u0001\u0002\u000fUt7\u000f^1tQR\u00191*!\u0002\t\u000bM|\b\u0019A/\t\u000f\u0005%q\u0002\"\u0001\u0002\f\u0005aA-\u001a7fi\u0016|6\u000f^8sKR\u00191*!\u0004\t\rM\f9\u00011\u0001^\u0001")
public final class ReplicationSupport
{
    public static void delete_store(final File directory) {
        ReplicationSupport$.MODULE$.delete_store(directory);
    }
    
    public static void unstash(final File directory) {
        ReplicationSupport$.MODULE$.unstash(directory);
    }
    
    public static void stash_clear(final File directory) {
        ReplicationSupport$.MODULE$.stash_clear(directory);
    }
    
    public static void copy_store_dir(final File from, final File to) {
        ReplicationSupport$.MODULE$.copy_store_dir(from, to);
    }
    
    public static void stash(final File directory) {
        ReplicationSupport$.MODULE$.stash(directory);
    }
    
    public static MappedByteBuffer map(final File file, final long offset, final long length, final boolean readOnly) {
        return ReplicationSupport$.MODULE$.map(file, offset, length, readOnly);
    }
    
    public static void unmap(final MappedByteBuffer buffer) {
        ReplicationSupport$.MODULE$.unmap(buffer);
    }
    
    public static AsciiBuffer LOG_DELETE_ACTION() {
        return ReplicationSupport$.MODULE$.LOG_DELETE_ACTION();
    }
    
    public static AsciiBuffer ERROR_ACTION() {
        return ReplicationSupport$.MODULE$.ERROR_ACTION();
    }
    
    public static AsciiBuffer DISCONNECT_ACTION() {
        return ReplicationSupport$.MODULE$.DISCONNECT_ACTION();
    }
    
    public static AsciiBuffer OK_ACTION() {
        return ReplicationSupport$.MODULE$.OK_ACTION();
    }
    
    public static AsciiBuffer ACK_ACTION() {
        return ReplicationSupport$.MODULE$.ACK_ACTION();
    }
    
    public static AsciiBuffer GET_ACTION() {
        return ReplicationSupport$.MODULE$.GET_ACTION();
    }
    
    public static AsciiBuffer SYNC_ACTION() {
        return ReplicationSupport$.MODULE$.SYNC_ACTION();
    }
    
    public static AsciiBuffer LOGIN_ACTION() {
        return ReplicationSupport$.MODULE$.LOGIN_ACTION();
    }
    
    public static AsciiBuffer WAL_ACTION() {
        return ReplicationSupport$.MODULE$.WAL_ACTION();
    }
}
