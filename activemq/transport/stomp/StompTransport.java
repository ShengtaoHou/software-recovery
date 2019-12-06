// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.IOException;
import org.apache.activemq.command.Command;

public interface StompTransport
{
    void sendToActiveMQ(final Command p0);
    
    void sendToStomp(final StompFrame p0) throws IOException;
    
    void onException(final IOException p0);
    
    StompInactivityMonitor getInactivityMonitor();
    
    StompWireFormat getWireFormat();
}
