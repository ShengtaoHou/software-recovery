// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v7;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.NetworkBridgeFilter;
import org.apache.activemq.command.DataStructure;

public class NetworkBridgeFilterMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 91;
    }
    
    @Override
    public DataStructure createObject() {
        return new NetworkBridgeFilter();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final NetworkBridgeFilter info = (NetworkBridgeFilter)o;
        info.setNetworkTTL(dataIn.readInt());
        info.setNetworkBrokerId((BrokerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final NetworkBridgeFilter info = (NetworkBridgeFilter)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getNetworkBrokerId(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final NetworkBridgeFilter info = (NetworkBridgeFilter)o;
        dataOut.writeInt(info.getNetworkTTL());
        this.tightMarshalCachedObject2(wireFormat, info.getNetworkBrokerId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final NetworkBridgeFilter info = (NetworkBridgeFilter)o;
        info.setNetworkTTL(dataIn.readInt());
        info.setNetworkBrokerId((BrokerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final NetworkBridgeFilter info = (NetworkBridgeFilter)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getNetworkTTL());
        this.looseMarshalCachedObject(wireFormat, info.getNetworkBrokerId(), dataOut);
    }
}
