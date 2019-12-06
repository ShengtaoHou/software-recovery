// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v3;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.JournalTransaction;
import org.apache.activemq.command.DataStructure;

public class JournalTransactionMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 54;
    }
    
    @Override
    public DataStructure createObject() {
        return new JournalTransaction();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final JournalTransaction info = (JournalTransaction)o;
        info.setTransactionId((TransactionId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setType(dataIn.readByte());
        info.setWasPrepared(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final JournalTransaction info = (JournalTransaction)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getTransactionId(), bs);
        bs.writeBoolean(info.getWasPrepared());
        return rc + 1;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final JournalTransaction info = (JournalTransaction)o;
        this.tightMarshalNestedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
        dataOut.writeByte(info.getType());
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final JournalTransaction info = (JournalTransaction)o;
        info.setTransactionId((TransactionId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setType(dataIn.readByte());
        info.setWasPrepared(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final JournalTransaction info = (JournalTransaction)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getTransactionId(), dataOut);
        dataOut.writeByte(info.getType());
        dataOut.writeBoolean(info.getWasPrepared());
    }
}
