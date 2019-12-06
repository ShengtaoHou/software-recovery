// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import java.io.IOException;

public interface ReplayBuffer
{
    void addBuffer(final int p0, final Object p1);
    
    void setReplayBufferListener(final ReplayBufferListener p0);
    
    void replayMessages(final int p0, final int p1, final Replayer p2) throws IOException;
}
