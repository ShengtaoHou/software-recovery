// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConnectionControl;
import org.apache.activemq.command.DataStructure;

public class ConnectionControlMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 18;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConnectionControl();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConnectionControl info = (ConnectionControl)o;
        info.setClose(bs.readBoolean());
        info.setExit(bs.readBoolean());
        info.setFaultTolerant(bs.readBoolean());
        info.setResume(bs.readBoolean());
        info.setSuspend(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConnectionControl info = (ConnectionControl)o;
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        bs.writeBoolean(info.isClose());
        bs.writeBoolean(info.isExit());
        bs.writeBoolean(info.isFaultTolerant());
        bs.writeBoolean(info.isResume());
        bs.writeBoolean(info.isSuspend());
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConnectionControl info = (ConnectionControl)o;
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConnectionControl info = (ConnectionControl)o;
        info.setClose(dataIn.readBoolean());
        info.setExit(dataIn.readBoolean());
        info.setFaultTolerant(dataIn.readBoolean());
        info.setResume(dataIn.readBoolean());
        info.setSuspend(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConnectionControl info = (ConnectionControl)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeBoolean(info.isClose());
        dataOut.writeBoolean(info.isExit());
        dataOut.writeBoolean(info.isFaultTolerant());
        dataOut.writeBoolean(info.isResume());
        dataOut.writeBoolean(info.isSuspend());
    }
}
