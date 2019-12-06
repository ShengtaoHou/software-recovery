// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.xstream;

import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.wireformat.WireFormatFactory;

public class XStreamWireFormatFactory implements WireFormatFactory
{
    String host;
    
    @Override
    public WireFormat createWireFormat() {
        return new XStreamWireFormat();
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
}
