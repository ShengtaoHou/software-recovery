// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v4;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.FlushCommand;
import org.apache.activemq.command.DataStructure;

public class FlushCommandMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 15;
    }
    
    @Override
    public DataStructure createObject() {
        return new FlushCommand();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        super.looseMarshal(wireFormat, o, dataOut);
    }
}
