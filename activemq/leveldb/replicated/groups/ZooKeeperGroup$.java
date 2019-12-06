// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import scala.Function1;
import scala.Function2;
import scala.collection.JavaConversions$;
import scala.collection.IterableLike;
import scala.runtime.ObjectRef;
import java.util.LinkedHashMap;

public final class ZooKeeperGroup$
{
    public static final ZooKeeperGroup$ MODULE$;
    
    static {
        new ZooKeeperGroup$();
    }
    
    public LinkedHashMap<String, byte[]> members(final ZKClient zk, final String path) {
        final ObjectRef rc = ObjectRef.create((Object)new LinkedHashMap());
        ((IterableLike)JavaConversions$.MODULE$.asScalaBuffer(zk.getAllChildren(path)).sortWith((Function2)new ZooKeeperGroup$$anonfun$members.ZooKeeperGroup$$anonfun$members$1())).foreach((Function1)new ZooKeeperGroup$$anonfun$members.ZooKeeperGroup$$anonfun$members$2(zk, path, rc));
        return (LinkedHashMap<String, byte[]>)rc.elem;
    }
    
    private ZooKeeperGroup$() {
        MODULE$ = this;
    }
}
