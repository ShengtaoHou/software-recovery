// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.security.cert.X509Certificate;
import java.io.IOException;
import org.apache.activemq.command.Command;

public interface AmqpTransport
{
    void sendToActiveMQ(final Command p0);
    
    void sendToActiveMQ(final IOException p0);
    
    void sendToAmqp(final Object p0) throws IOException;
    
    X509Certificate[] getPeerCertificates();
    
    void onException(final IOException p0);
    
    AmqpWireFormat getWireFormat();
    
    void stop() throws Exception;
    
    String getTransformer();
    
    String getRemoteAddress();
    
    boolean isTrace();
    
    IAmqpProtocolConverter getProtocolConverter();
    
    void setProtocolConverter(final IAmqpProtocolConverter p0);
}
