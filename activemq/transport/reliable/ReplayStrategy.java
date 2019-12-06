// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import java.io.IOException;

public interface ReplayStrategy
{
    boolean onDroppedPackets(final ReliableTransport p0, final int p1, final int p2, final int p3) throws IOException;
    
    void onReceivedPacket(final ReliableTransport p0, final long p1);
}
