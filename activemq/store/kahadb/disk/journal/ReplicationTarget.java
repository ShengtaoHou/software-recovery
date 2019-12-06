// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb.disk.journal;

import org.apache.activemq.util.ByteSequence;

public interface ReplicationTarget
{
    void replicate(final Location p0, final ByteSequence p1, final boolean p2);
}
