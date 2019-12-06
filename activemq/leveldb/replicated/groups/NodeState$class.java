// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.leveldb.replicated.groups;

public abstract class NodeState$class
{
    public static String toString(final NodeState $this) {
        return new String(ClusteredSupport$.MODULE$.encode($this, ClusteredSupport$.MODULE$.encode$default$2()), "UTF-8");
    }
    
    public static void $init$(final NodeState $this) {
    }
}
