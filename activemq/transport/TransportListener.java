// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;

public interface TransportListener
{
    void onCommand(final Object p0);
    
    void onException(final IOException p0);
    
    void transportInterupted();
    
    void transportResumed();
}
