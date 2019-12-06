// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.wireformat.WireFormatFactory;

public class MQTTWireFormatFactory implements WireFormatFactory
{
    @Override
    public WireFormat createWireFormat() {
        return new MQTTWireFormat();
    }
}
