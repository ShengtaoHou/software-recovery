// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.IntegerResponse;
import org.apache.activemq.command.DataStructure;

public class IntegerResponseMarshaller extends ResponseMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 34;
    }
    
    @Override
    public DataStructure createObject() {
        return new IntegerResponse();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final IntegerResponse info = (IntegerResponse)o;
        info.setResult(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final IntegerResponse info = (IntegerResponse)o;
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final IntegerResponse info = (IntegerResponse)o;
        dataOut.writeInt(info.getResult());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final IntegerResponse info = (IntegerResponse)o;
        info.setResult(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final IntegerResponse info = (IntegerResponse)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getResult());
    }
}
