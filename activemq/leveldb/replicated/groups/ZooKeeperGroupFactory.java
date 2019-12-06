// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import java.util.LinkedHashMap;
import scala.reflect.ScalaSignature;

@ScalaSignature(bytes = "\u0006\u0001%;Q!\u0001\u0002\t\u0002=\tQCW8p\u0017\u0016,\u0007/\u001a:He>,\bOR1di>\u0014\u0018P\u0003\u0002\u0004\t\u00051qM]8vaNT!!\u0002\u0004\u0002\u0015I,\u0007\u000f\\5dCR,GM\u0003\u0002\b\u0011\u00059A.\u001a<fY\u0012\u0014'BA\u0005\u000b\u0003!\t7\r^5wK6\f(BA\u0006\r\u0003\u0019\t\u0007/Y2iK*\tQ\"A\u0002pe\u001e\u001c\u0001\u0001\u0005\u0002\u0011#5\t!AB\u0003\u0013\u0005!\u00051CA\u000b[_>\\U-\u001a9fe\u001e\u0013x.\u001e9GC\u000e$xN]=\u0014\u0005E!\u0002CA\u000b\u0019\u001b\u00051\"\"A\f\u0002\u000bM\u001c\u0017\r\\1\n\u0005e1\"AB!osJ+g\rC\u0003\u001c#\u0011\u0005A$\u0001\u0004=S:LGO\u0010\u000b\u0002\u001f!)a$\u0005C\u0001?\u000511M]3bi\u0016$2\u0001I\u0012)!\t\u0001\u0012%\u0003\u0002#\u0005\tq!l\\8LK\u0016\u0004XM]$s_V\u0004\b\"\u0002\u0013\u001e\u0001\u0004)\u0013A\u0001>l!\t\u0001b%\u0003\u0002(\u0005\tA!lS\"mS\u0016tG\u000fC\u0003*;\u0001\u0007!&\u0001\u0003qCRD\u0007CA\u00164\u001d\ta\u0013G\u0004\u0002.a5\taF\u0003\u00020\u001d\u00051AH]8pizJ\u0011aF\u0005\u0003eY\ta\u0001\u0015:fI\u00164\u0017B\u0001\u001b6\u0005\u0019\u0019FO]5oO*\u0011!G\u0006\u0005\u0006oE!\t\u0001O\u0001\b[\u0016l'-\u001a:t)\rIt\t\u0013\t\u0005u}R\u0013)D\u0001<\u0015\taT(\u0001\u0003vi&d'\"\u0001 \u0002\t)\fg/Y\u0005\u0003\u0001n\u0012Q\u0002T5oW\u0016$\u0007*Y:i\u001b\u0006\u0004\bcA\u000bC\t&\u00111I\u0006\u0002\u0006\u0003J\u0014\u0018-\u001f\t\u0003+\u0015K!A\u0012\f\u0003\t\tKH/\u001a\u0005\u0006IY\u0002\r!\n\u0005\u0006SY\u0002\rA\u000b")
public final class ZooKeeperGroupFactory
{
    public static LinkedHashMap<String, byte[]> members(final ZKClient zk, final String path) {
        return ZooKeeperGroupFactory$.MODULE$.members(zk, path);
    }
    
    public static ZooKeeperGroup create(final ZKClient zk, final String path) {
        return ZooKeeperGroupFactory$.MODULE$.create(zk, path);
    }
}
