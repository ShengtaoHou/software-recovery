// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import org.slf4j.Logger;

public interface LogWriter
{
    void initialMessage(final Logger p0);
    
    void logRequest(final Logger p0, final Object p1);
    
    void logResponse(final Logger p0, final Object p1);
    
    void logAsyncRequest(final Logger p0, final Object p1);
    
    void logOneWay(final Logger p0, final Object p1);
    
    void logReceivedCommand(final Logger p0, final Object p1);
    
    void logReceivedException(final Logger p0, final IOException p1);
}
