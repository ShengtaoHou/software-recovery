// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.activemq.command.DataStructure;

public class LocalTransactionIdMarshaller extends TransactionIdMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 111;
    }
    
    @Override
    public DataStructure createObject() {
        return new LocalTransactionId();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final LocalTransactionId info = (LocalTransactionId)o;
        info.setValue(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setConnectionId((ConnectionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final LocalTransactionId info = (LocalTransactionId)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalLong1(wireFormat, info.getValue(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConnectionId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final LocalTransactionId info = (LocalTransactionId)o;
        this.tightMarshalLong2(wireFormat, info.getValue(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final LocalTransactionId info = (LocalTransactionId)o;
        info.setValue(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setConnectionId((ConnectionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final LocalTransactionId info = (LocalTransactionId)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalLong(wireFormat, info.getValue(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConnectionId(), dataOut);
    }
}
