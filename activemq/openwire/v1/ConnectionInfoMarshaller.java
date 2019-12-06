// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.DataStructure;

public class ConnectionInfoMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 3;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConnectionInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConnectionInfo info = (ConnectionInfo)o;
        info.setConnectionId((ConnectionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setClientId(this.tightUnmarshalString(dataIn, bs));
        info.setPassword(this.tightUnmarshalString(dataIn, bs));
        info.setUserName(this.tightUnmarshalString(dataIn, bs));
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }
        info.setBrokerMasterConnector(bs.readBoolean());
        info.setManageable(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConnectionInfo info = (ConnectionInfo)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConnectionId(), bs);
        rc += this.tightMarshalString1(info.getClientId(), bs);
        rc += this.tightMarshalString1(info.getPassword(), bs);
        rc += this.tightMarshalString1(info.getUserName(), bs);
        rc += this.tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        bs.writeBoolean(info.isBrokerMasterConnector());
        bs.writeBoolean(info.isManageable());
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConnectionInfo info = (ConnectionInfo)o;
        this.tightMarshalCachedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
        this.tightMarshalString2(info.getClientId(), dataOut, bs);
        this.tightMarshalString2(info.getPassword(), dataOut, bs);
        this.tightMarshalString2(info.getUserName(), dataOut, bs);
        this.tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConnectionInfo info = (ConnectionInfo)o;
        info.setConnectionId((ConnectionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setClientId(this.looseUnmarshalString(dataIn));
        info.setPassword(this.looseUnmarshalString(dataIn));
        info.setUserName(this.looseUnmarshalString(dataIn));
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }
        info.setBrokerMasterConnector(dataIn.readBoolean());
        info.setManageable(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConnectionInfo info = (ConnectionInfo)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConnectionId(), dataOut);
        this.looseMarshalString(info.getClientId(), dataOut);
        this.looseMarshalString(info.getPassword(), dataOut);
        this.looseMarshalString(info.getUserName(), dataOut);
        this.looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        dataOut.writeBoolean(info.isBrokerMasterConnector());
        dataOut.writeBoolean(info.isManageable());
    }
}
