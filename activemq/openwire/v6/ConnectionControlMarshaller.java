// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v6;

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
        info.setConnectedBrokers(this.tightUnmarshalString(dataIn, bs));
        info.setReconnectTo(this.tightUnmarshalString(dataIn, bs));
        info.setRebalanceConnection(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConnectionControl info = (ConnectionControl)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        bs.writeBoolean(info.isClose());
        bs.writeBoolean(info.isExit());
        bs.writeBoolean(info.isFaultTolerant());
        bs.writeBoolean(info.isResume());
        bs.writeBoolean(info.isSuspend());
        rc += this.tightMarshalString1(info.getConnectedBrokers(), bs);
        rc += this.tightMarshalString1(info.getReconnectTo(), bs);
        bs.writeBoolean(info.isRebalanceConnection());
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
        this.tightMarshalString2(info.getConnectedBrokers(), dataOut, bs);
        this.tightMarshalString2(info.getReconnectTo(), dataOut, bs);
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
        info.setConnectedBrokers(this.looseUnmarshalString(dataIn));
        info.setReconnectTo(this.looseUnmarshalString(dataIn));
        info.setRebalanceConnection(dataIn.readBoolean());
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
        this.looseMarshalString(info.getConnectedBrokers(), dataOut);
        this.looseMarshalString(info.getReconnectTo(), dataOut);
        dataOut.writeBoolean(info.isRebalanceConnection());
    }
}
