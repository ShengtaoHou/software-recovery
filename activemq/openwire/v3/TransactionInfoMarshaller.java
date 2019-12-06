// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v3;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.DataStructure;

public class TransactionInfoMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 7;
    }
    
    @Override
    public DataStructure createObject() {
        return new TransactionInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final TransactionInfo info = (TransactionInfo)o;
        info.setConnectionId((ConnectionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setTransactionId((TransactionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setType(dataIn.readByte());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final TransactionInfo info = (TransactionInfo)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConnectionId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getTransactionId(), bs);
        return rc + 1;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final TransactionInfo info = (TransactionInfo)o;
        this.tightMarshalCachedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
        dataOut.writeByte(info.getType());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final TransactionInfo info = (TransactionInfo)o;
        info.setConnectionId((ConnectionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setTransactionId((TransactionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setType(dataIn.readByte());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final TransactionInfo info = (TransactionInfo)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConnectionId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getTransactionId(), dataOut);
        dataOut.writeByte(info.getType());
    }
}
