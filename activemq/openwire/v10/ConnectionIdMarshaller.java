// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v10;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.command.DataStructure;

public class ConnectionIdMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 120;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConnectionId();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConnectionId info = (ConnectionId)o;
        info.setValue(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConnectionId info = (ConnectionId)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getValue(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConnectionId info = (ConnectionId)o;
        this.tightMarshalString2(info.getValue(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConnectionId info = (ConnectionId)o;
        info.setValue(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConnectionId info = (ConnectionId)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getValue(), dataOut);
    }
}
