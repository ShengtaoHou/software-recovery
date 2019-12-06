// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import java.security.cert.X509Certificate;
import java.io.IOException;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.apache.activemq.command.Command;

public interface MQTTTransport
{
    void sendToActiveMQ(final Command p0);
    
    void sendToMQTT(final MQTTFrame p0) throws IOException;
    
    X509Certificate[] getPeerCertificates();
    
    void onException(final IOException p0);
    
    MQTTInactivityMonitor getInactivityMonitor();
    
    MQTTWireFormat getWireFormat();
    
    void stop() throws Exception;
}
