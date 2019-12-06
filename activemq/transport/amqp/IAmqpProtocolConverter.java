// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.apache.activemq.command.Command;
import java.io.IOException;

public interface IAmqpProtocolConverter
{
    void onAMQPData(final Object p0) throws Exception;
    
    void onAMQPException(final IOException p0);
    
    void onActiveMQCommand(final Command p0) throws Exception;
    
    void updateTracer();
}
