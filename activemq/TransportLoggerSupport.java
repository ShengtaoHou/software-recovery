// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import java.io.IOException;
import org.apache.activemq.transport.Transport;

public class TransportLoggerSupport
{
    public static String defaultLogWriterName;
    public static final SPI spi;
    
    public static Transport createTransportLogger(final Transport transport) throws IOException {
        if (TransportLoggerSupport.spi != null) {
            return TransportLoggerSupport.spi.createTransportLogger(transport);
        }
        return transport;
    }
    
    public static Transport createTransportLogger(final Transport transport, final String logWriterName, final boolean dynamicManagement, final boolean startLogging, final int jmxPort) throws IOException {
        if (TransportLoggerSupport.spi != null) {
            return TransportLoggerSupport.spi.createTransportLogger(transport, logWriterName, dynamicManagement, startLogging, jmxPort);
        }
        return transport;
    }
    
    static {
        TransportLoggerSupport.defaultLogWriterName = "default";
        SPI temp;
        try {
            temp = (SPI)TransportLoggerSupport.class.getClassLoader().loadClass("org.apache.activemq.transport.TransportLoggerFactorySPI").newInstance();
        }
        catch (Throwable e) {
            temp = null;
        }
        spi = temp;
    }
    
    public interface SPI
    {
        Transport createTransportLogger(final Transport p0) throws IOException;
        
        Transport createTransportLogger(final Transport p0, final String p1, final boolean p2, final boolean p3, final int p4) throws IOException;
    }
}
