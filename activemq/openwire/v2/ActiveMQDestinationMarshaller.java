// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;

public abstract class ActiveMQDestinationMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ActiveMQDestination info = (ActiveMQDestination)o;
        info.setPhysicalName(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ActiveMQDestination info = (ActiveMQDestination)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getPhysicalName(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ActiveMQDestination info = (ActiveMQDestination)o;
        this.tightMarshalString2(info.getPhysicalName(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ActiveMQDestination info = (ActiveMQDestination)o;
        info.setPhysicalName(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ActiveMQDestination info = (ActiveMQDestination)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getPhysicalName(), dataOut);
    }
}
