// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v9;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ControlCommand;
import org.apache.activemq.command.DataStructure;

public class ControlCommandMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 14;
    }
    
    @Override
    public DataStructure createObject() {
        return new ControlCommand();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ControlCommand info = (ControlCommand)o;
        info.setCommand(this.tightUnmarshalString(dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ControlCommand info = (ControlCommand)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getCommand(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ControlCommand info = (ControlCommand)o;
        this.tightMarshalString2(info.getCommand(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ControlCommand info = (ControlCommand)o;
        info.setCommand(this.looseUnmarshalString(dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ControlCommand info = (ControlCommand)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getCommand(), dataOut);
    }
}
