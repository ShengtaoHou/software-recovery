// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.scheduler;

import org.apache.activemq.util.ByteSequence;

public interface JobListener
{
    void scheduledJob(final String p0, final ByteSequence p1);
}
