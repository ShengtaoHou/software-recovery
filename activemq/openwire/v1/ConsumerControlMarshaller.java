// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConsumerControl;
import org.apache.activemq.command.DataStructure;

public class ConsumerControlMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 17;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConsumerControl();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConsumerControl info = (ConsumerControl)o;
        info.setClose(bs.readBoolean());
        info.setConsumerId((ConsumerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setPrefetch(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConsumerControl info = (ConsumerControl)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        bs.writeBoolean(info.isClose());
        rc += this.tightMarshalNestedObject1(wireFormat, info.getConsumerId(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConsumerControl info = (ConsumerControl)o;
        bs.readBoolean();
        this.tightMarshalNestedObject2(wireFormat, info.getConsumerId(), dataOut, bs);
        dataOut.writeInt(info.getPrefetch());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConsumerControl info = (ConsumerControl)o;
        info.setClose(dataIn.readBoolean());
        info.setConsumerId((ConsumerId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setPrefetch(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConsumerControl info = (ConsumerControl)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeBoolean(info.isClose());
        this.looseMarshalNestedObject(wireFormat, info.getConsumerId(), dataOut);
        dataOut.writeInt(info.getPrefetch());
    }
}
