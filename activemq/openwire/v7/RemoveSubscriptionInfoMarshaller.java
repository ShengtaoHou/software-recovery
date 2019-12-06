// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v7;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.RemoveSubscriptionInfo;
import org.apache.activemq.command.DataStructure;

public class RemoveSubscriptionInfoMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 9;
    }
    
    @Override
    public DataStructure createObject() {
        return new RemoveSubscriptionInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final RemoveSubscriptionInfo info = (RemoveSubscriptionInfo)o;
        info.setConnectionId((ConnectionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setSubcriptionName(this.tightUnmarshalString(dataIn, bs));
        info.setClientId(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final RemoveSubscriptionInfo info = (RemoveSubscriptionInfo)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConnectionId(), bs);
        rc += this.tightMarshalString1(info.getSubcriptionName(), bs);
        rc += this.tightMarshalString1(info.getClientId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final RemoveSubscriptionInfo info = (RemoveSubscriptionInfo)o;
        this.tightMarshalCachedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
        this.tightMarshalString2(info.getSubcriptionName(), dataOut, bs);
        this.tightMarshalString2(info.getClientId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final RemoveSubscriptionInfo info = (RemoveSubscriptionInfo)o;
        info.setConnectionId((ConnectionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setSubcriptionName(this.looseUnmarshalString(dataIn));
        info.setClientId(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final RemoveSubscriptionInfo info = (RemoveSubscriptionInfo)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConnectionId(), dataOut);
        this.looseMarshalString(info.getSubcriptionName(), dataOut);
        this.looseMarshalString(info.getClientId(), dataOut);
    }
}
