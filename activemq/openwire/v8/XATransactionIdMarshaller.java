// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v8;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.command.DataStructure;

public class XATransactionIdMarshaller extends TransactionIdMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 112;
    }
    
    @Override
    public DataStructure createObject() {
        return new XATransactionId();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final XATransactionId info = (XATransactionId)o;
        info.setFormatId(dataIn.readInt());
        info.setGlobalTransactionId(this.tightUnmarshalByteArray(dataIn, bs));
        info.setBranchQualifier(this.tightUnmarshalByteArray(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final XATransactionId info = (XATransactionId)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalByteArray1(info.getGlobalTransactionId(), bs);
        rc += this.tightMarshalByteArray1(info.getBranchQualifier(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final XATransactionId info = (XATransactionId)o;
        dataOut.writeInt(info.getFormatId());
        this.tightMarshalByteArray2(info.getGlobalTransactionId(), dataOut, bs);
        this.tightMarshalByteArray2(info.getBranchQualifier(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final XATransactionId info = (XATransactionId)o;
        info.setFormatId(dataIn.readInt());
        info.setGlobalTransactionId(this.looseUnmarshalByteArray(dataIn));
        info.setBranchQualifier(this.looseUnmarshalByteArray(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final XATransactionId info = (XATransactionId)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getFormatId());
        this.looseMarshalByteArray(wireFormat, info.getGlobalTransactionId(), dataOut);
        this.looseMarshalByteArray(wireFormat, info.getBranchQualifier(), dataOut);
    }
}
