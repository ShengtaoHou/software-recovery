// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.BaseCommand;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;

public abstract class BaseCommandMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final BaseCommand info = (BaseCommand)o;
        info.setCommandId(dataIn.readInt());
        info.setResponseRequired(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final BaseCommand info = (BaseCommand)o;
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        bs.writeBoolean(info.isResponseRequired());
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final BaseCommand info = (BaseCommand)o;
        dataOut.writeInt(info.getCommandId());
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final BaseCommand info = (BaseCommand)o;
        info.setCommandId(dataIn.readInt());
        info.setResponseRequired(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final BaseCommand info = (BaseCommand)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getCommandId());
        dataOut.writeBoolean(info.isResponseRequired());
    }
}
