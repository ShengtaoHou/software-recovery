// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v8;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.DiscoveryEvent;
import org.apache.activemq.command.DataStructure;

public class DiscoveryEventMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 40;
    }
    
    @Override
    public DataStructure createObject() {
        return new DiscoveryEvent();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final DiscoveryEvent info = (DiscoveryEvent)o;
        info.setServiceName(this.tightUnmarshalString(dataIn, bs));
        info.setBrokerName(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final DiscoveryEvent info = (DiscoveryEvent)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getServiceName(), bs);
        rc += this.tightMarshalString1(info.getBrokerName(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final DiscoveryEvent info = (DiscoveryEvent)o;
        this.tightMarshalString2(info.getServiceName(), dataOut, bs);
        this.tightMarshalString2(info.getBrokerName(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final DiscoveryEvent info = (DiscoveryEvent)o;
        info.setServiceName(this.looseUnmarshalString(dataIn));
        info.setBrokerName(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final DiscoveryEvent info = (DiscoveryEvent)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getServiceName(), dataOut);
        this.looseMarshalString(info.getBrokerName(), dataOut);
    }
}
