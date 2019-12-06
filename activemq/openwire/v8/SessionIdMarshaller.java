// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v8;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.SessionId;
import org.apache.activemq.command.DataStructure;

public class SessionIdMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 121;
    }
    
    @Override
    public DataStructure createObject() {
        return new SessionId();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final SessionId info = (SessionId)o;
        info.setConnectionId(this.tightUnmarshalString(dataIn, bs));
        info.setValue(this.tightUnmarshalLong(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final SessionId info = (SessionId)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getConnectionId(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getValue(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final SessionId info = (SessionId)o;
        this.tightMarshalString2(info.getConnectionId(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getValue(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final SessionId info = (SessionId)o;
        info.setConnectionId(this.looseUnmarshalString(dataIn));
        info.setValue(this.looseUnmarshalLong(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final SessionId info = (SessionId)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getConnectionId(), dataOut);
        this.looseMarshalLong(wireFormat, info.getValue(), dataOut);
    }
}
