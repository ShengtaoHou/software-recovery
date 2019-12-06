// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ReplayCommand;
import org.apache.activemq.command.DataStructure;

public class ReplayCommandMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 65;
    }
    
    @Override
    public DataStructure createObject() {
        return new ReplayCommand();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ReplayCommand info = (ReplayCommand)o;
        info.setFirstNakNumber(dataIn.readInt());
        info.setLastNakNumber(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ReplayCommand info = (ReplayCommand)o;
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        return rc + 8;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ReplayCommand info = (ReplayCommand)o;
        dataOut.writeInt(info.getFirstNakNumber());
        dataOut.writeInt(info.getLastNakNumber());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ReplayCommand info = (ReplayCommand)o;
        info.setFirstNakNumber(dataIn.readInt());
        info.setLastNakNumber(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ReplayCommand info = (ReplayCommand)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getFirstNakNumber());
        dataOut.writeInt(info.getLastNakNumber());
    }
}
