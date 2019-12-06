// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v3;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.command.DataStructure;

public class BrokerInfoMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 2;
    }
    
    @Override
    public DataStructure createObject() {
        return new BrokerInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final BrokerInfo info = (BrokerInfo)o;
        info.setBrokerId((BrokerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setBrokerURL(this.tightUnmarshalString(dataIn, bs));
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerInfo[] value = new BrokerInfo[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerInfo)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setPeerBrokerInfos(value);
        }
        else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(this.tightUnmarshalString(dataIn, bs));
        info.setSlaveBroker(bs.readBoolean());
        info.setMasterBroker(bs.readBoolean());
        info.setFaultTolerantConfiguration(bs.readBoolean());
        info.setDuplexConnection(bs.readBoolean());
        info.setNetworkConnection(bs.readBoolean());
        info.setConnectionId(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerUploadUrl(this.tightUnmarshalString(dataIn, bs));
        info.setNetworkProperties(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final BrokerInfo info = (BrokerInfo)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getBrokerId(), bs);
        rc += this.tightMarshalString1(info.getBrokerURL(), bs);
        rc += this.tightMarshalObjectArray1(wireFormat, info.getPeerBrokerInfos(), bs);
        rc += this.tightMarshalString1(info.getBrokerName(), bs);
        bs.writeBoolean(info.isSlaveBroker());
        bs.writeBoolean(info.isMasterBroker());
        bs.writeBoolean(info.isFaultTolerantConfiguration());
        bs.writeBoolean(info.isDuplexConnection());
        bs.writeBoolean(info.isNetworkConnection());
        rc += this.tightMarshalLong1(wireFormat, info.getConnectionId(), bs);
        rc += this.tightMarshalString1(info.getBrokerUploadUrl(), bs);
        rc += this.tightMarshalString1(info.getNetworkProperties(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final BrokerInfo info = (BrokerInfo)o;
        this.tightMarshalCachedObject2(wireFormat, info.getBrokerId(), dataOut, bs);
        this.tightMarshalString2(info.getBrokerURL(), dataOut, bs);
        this.tightMarshalObjectArray2(wireFormat, info.getPeerBrokerInfos(), dataOut, bs);
        this.tightMarshalString2(info.getBrokerName(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        this.tightMarshalLong2(wireFormat, info.getConnectionId(), dataOut, bs);
        this.tightMarshalString2(info.getBrokerUploadUrl(), dataOut, bs);
        this.tightMarshalString2(info.getNetworkProperties(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final BrokerInfo info = (BrokerInfo)o;
        info.setBrokerId((BrokerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setBrokerURL(this.looseUnmarshalString(dataIn));
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerInfo[] value = new BrokerInfo[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerInfo)this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setPeerBrokerInfos(value);
        }
        else {
            info.setPeerBrokerInfos(null);
        }
        info.setBrokerName(this.looseUnmarshalString(dataIn));
        info.setSlaveBroker(dataIn.readBoolean());
        info.setMasterBroker(dataIn.readBoolean());
        info.setFaultTolerantConfiguration(dataIn.readBoolean());
        info.setDuplexConnection(dataIn.readBoolean());
        info.setNetworkConnection(dataIn.readBoolean());
        info.setConnectionId(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerUploadUrl(this.looseUnmarshalString(dataIn));
        info.setNetworkProperties(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final BrokerInfo info = (BrokerInfo)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getBrokerId(), dataOut);
        this.looseMarshalString(info.getBrokerURL(), dataOut);
        this.looseMarshalObjectArray(wireFormat, info.getPeerBrokerInfos(), dataOut);
        this.looseMarshalString(info.getBrokerName(), dataOut);
        dataOut.writeBoolean(info.isSlaveBroker());
        dataOut.writeBoolean(info.isMasterBroker());
        dataOut.writeBoolean(info.isFaultTolerantConfiguration());
        dataOut.writeBoolean(info.isDuplexConnection());
        dataOut.writeBoolean(info.isNetworkConnection());
        this.looseMarshalLong(wireFormat, info.getConnectionId(), dataOut);
        this.looseMarshalString(info.getBrokerUploadUrl(), dataOut);
        this.looseMarshalString(info.getNetworkProperties(), dataOut);
    }
}
