// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.util.FileSupport;
import java.io.File;
import org.apache.activemq.leveldb.util.FileSupport$;
import org.apache.activemq.leveldb.LevelDBStore;

public abstract class ReplicatedLevelDBStoreTrait$class
{
    public static void setSecurityToken(final ReplicatedLevelDBStoreTrait $this, final String x$1) {
        $this.securityToken_$eq(x$1);
    }
    
    public static String node_id(final ReplicatedLevelDBStoreTrait $this) {
        return ReplicatedLevelDBStoreTrait$.MODULE$.node_id(((LevelDBStore)$this).directory());
    }
    
    public static String storeId(final ReplicatedLevelDBStoreTrait $this) {
        final File storeid_file = FileSupport$.MODULE$.toRichFile(((LevelDBStore)$this).directory()).$div("storeid.txt");
        String text;
        if (storeid_file.exists()) {
            final FileSupport.RichFile qual$3 = FileSupport$.MODULE$.toRichFile(storeid_file);
            final String x$4 = qual$3.readText$default$1();
            text = qual$3.readText(x$4);
        }
        else {
            text = null;
        }
        return text;
    }
    
    public static void storeId_$eq(final ReplicatedLevelDBStoreTrait $this, final String value) {
        final File storeid_file = FileSupport$.MODULE$.toRichFile(((LevelDBStore)$this).directory()).$div("storeid.txt");
        final FileSupport.RichFile qual$4 = FileSupport$.MODULE$.toRichFile(storeid_file);
        final String x$5 = value;
        final String x$6 = qual$4.writeText$default$2();
        qual$4.writeText(x$5, x$6);
    }
    
    public static String getSecurityToken(final ReplicatedLevelDBStoreTrait $this) {
        return $this.securityToken();
    }
    
    public static void $init$(final ReplicatedLevelDBStoreTrait $this) {
        $this.securityToken_$eq("");
    }
}
