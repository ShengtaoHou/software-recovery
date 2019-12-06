// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.reliable;

import java.io.IOException;

public interface Replayer
{
    void sendBuffer(final int p0, final Object p1) throws IOException;
}
