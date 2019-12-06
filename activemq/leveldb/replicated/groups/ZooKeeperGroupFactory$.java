// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

import java.util.LinkedHashMap;

public final class ZooKeeperGroupFactory$
{
    public static final ZooKeeperGroupFactory$ MODULE$;
    
    static {
        new ZooKeeperGroupFactory$();
    }
    
    public ZooKeeperGroup create(final ZKClient zk, final String path) {
        return new ZooKeeperGroup(zk, path);
    }
    
    public LinkedHashMap<String, byte[]> members(final ZKClient zk, final String path) {
        return ZooKeeperGroup$.MODULE$.members(zk, path);
    }
    
    private ZooKeeperGroupFactory$() {
        MODULE$ = this;
    }
}
