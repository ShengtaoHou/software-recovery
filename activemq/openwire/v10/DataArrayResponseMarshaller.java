// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v10;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.DataArrayResponse;
import org.apache.activemq.command.DataStructure;

public class DataArrayResponseMarshaller extends ResponseMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 33;
    }
    
    @Override
    public DataStructure createObject() {
        return new DataArrayResponse();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final DataArrayResponse info = (DataArrayResponse)o;
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final DataStructure[] value = new DataStructure[size];
            for (int i = 0; i < size; ++i) {
                value[i] = this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setData(value);
        }
        else {
            info.setData(null);
        }
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final DataArrayResponse info = (DataArrayResponse)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalObjectArray1(wireFormat, info.getData(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final DataArrayResponse info = (DataArrayResponse)o;
        this.tightMarshalObjectArray2(wireFormat, info.getData(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final DataArrayResponse info = (DataArrayResponse)o;
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final DataStructure[] value = new DataStructure[size];
            for (int i = 0; i < size; ++i) {
                value[i] = this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setData(value);
        }
        else {
            info.setData(null);
        }
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final DataArrayResponse info = (DataArrayResponse)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalObjectArray(wireFormat, info.getData(), dataOut);
    }
}
