// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import org.apache.activemq.TransportLoggerSupport;

public class TransportLoggerFactorySPI implements TransportLoggerSupport.SPI
{
    @Override
    public Transport createTransportLogger(final Transport transport) throws IOException {
        return TransportLoggerFactory.getInstance().createTransportLogger(transport);
    }
    
    @Override
    public Transport createTransportLogger(final Transport transport, final String logWriterName, final boolean dynamicManagement, final boolean startLogging, final int jmxPort) throws IOException {
        return TransportLoggerFactory.getInstance().createTransportLogger(transport, logWriterName, dynamicManagement, startLogging, jmxPort);
    }
}
