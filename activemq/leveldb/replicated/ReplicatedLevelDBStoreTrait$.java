// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated;

import org.apache.activemq.leveldb.util.FileSupport;
import org.apache.activemq.leveldb.util.FileSupport$;
import java.io.File;
import java.util.UUID;

public final class ReplicatedLevelDBStoreTrait$
{
    public static final ReplicatedLevelDBStoreTrait$ MODULE$;
    
    static {
        new ReplicatedLevelDBStoreTrait$();
    }
    
    public String create_uuid() {
        return UUID.randomUUID().toString();
    }
    
    public String node_id(final File directory) {
        final File nodeid_file = FileSupport$.MODULE$.toRichFile(directory).$div("nodeid.txt");
        String text;
        if (nodeid_file.exists()) {
            final FileSupport.RichFile qual$1 = FileSupport$.MODULE$.toRichFile(nodeid_file);
            final String x$1 = qual$1.readText$default$1();
            text = qual$1.readText(x$1);
        }
        else {
            final String rc = this.create_uuid();
            nodeid_file.getParentFile().mkdirs();
            final FileSupport.RichFile qual$2 = FileSupport$.MODULE$.toRichFile(nodeid_file);
            final String x$2 = rc;
            final String x$3 = qual$2.writeText$default$2();
            qual$2.writeText(x$2, x$3);
            text = rc;
        }
        return text;
    }
    
    private ReplicatedLevelDBStoreTrait$() {
        MODULE$ = this;
    }
}
