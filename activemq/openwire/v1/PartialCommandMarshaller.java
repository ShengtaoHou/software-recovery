// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.PartialCommand;
import org.apache.activemq.command.DataStructure;

public class PartialCommandMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 60;
    }
    
    @Override
    public DataStructure createObject() {
        return new PartialCommand();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final PartialCommand info = (PartialCommand)o;
        info.setCommandId(dataIn.readInt());
        info.setData(this.tightUnmarshalByteArray(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final PartialCommand info = (PartialCommand)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalByteArray1(info.getData(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final PartialCommand info = (PartialCommand)o;
        dataOut.writeInt(info.getCommandId());
        this.tightMarshalByteArray2(info.getData(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final PartialCommand info = (PartialCommand)o;
        info.setCommandId(dataIn.readInt());
        info.setData(this.looseUnmarshalByteArray(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final PartialCommand info = (PartialCommand)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getCommandId());
        this.looseMarshalByteArray(wireFormat, info.getData(), dataOut);
    }
}
