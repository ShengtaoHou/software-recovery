// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v5;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.WireFormatInfo;
import org.apache.activemq.command.DataStructure;

public class WireFormatInfoMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 1;
    }
    
    @Override
    public DataStructure createObject() {
        return new WireFormatInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final WireFormatInfo info = (WireFormatInfo)o;
        info.beforeUnmarshall(wireFormat);
        info.setMagic(this.tightUnmarshalConstByteArray(dataIn, bs, 8));
        info.setVersion(dataIn.readInt());
        info.setMarshalledProperties(this.tightUnmarshalByteSequence(dataIn, bs));
        info.afterUnmarshall(wireFormat);
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final WireFormatInfo info = (WireFormatInfo)o;
        info.beforeMarshall(wireFormat);
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalConstByteArray1(info.getMagic(), bs, 8);
        rc += this.tightMarshalByteSequence1(info.getMarshalledProperties(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final WireFormatInfo info = (WireFormatInfo)o;
        this.tightMarshalConstByteArray2(info.getMagic(), dataOut, bs, 8);
        dataOut.writeInt(info.getVersion());
        this.tightMarshalByteSequence2(info.getMarshalledProperties(), dataOut, bs);
        info.afterMarshall(wireFormat);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final WireFormatInfo info = (WireFormatInfo)o;
        info.beforeUnmarshall(wireFormat);
        info.setMagic(this.looseUnmarshalConstByteArray(dataIn, 8));
        info.setVersion(dataIn.readInt());
        info.setMarshalledProperties(this.looseUnmarshalByteSequence(dataIn));
        info.afterUnmarshall(wireFormat);
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final WireFormatInfo info = (WireFormatInfo)o;
        info.beforeMarshall(wireFormat);
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalConstByteArray(wireFormat, info.getMagic(), dataOut, 8);
        dataOut.writeInt(info.getVersion());
        this.looseMarshalByteSequence(wireFormat, info.getMarshalledProperties(), dataOut);
    }
}
