// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v10;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ActiveMQDestination;
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
        info.setDestination((ActiveMQDestination)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setClose(bs.readBoolean());
        info.setConsumerId((ConsumerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setPrefetch(dataIn.readInt());
        info.setFlush(bs.readBoolean());
        info.setStart(bs.readBoolean());
        info.setStop(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConsumerControl info = (ConsumerControl)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getDestination(), bs);
        bs.writeBoolean(info.isClose());
        rc += this.tightMarshalNestedObject1(wireFormat, info.getConsumerId(), bs);
        bs.writeBoolean(info.isFlush());
        bs.writeBoolean(info.isStart());
        bs.writeBoolean(info.isStop());
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConsumerControl info = (ConsumerControl)o;
        this.tightMarshalNestedObject2(wireFormat, info.getDestination(), dataOut, bs);
        bs.readBoolean();
        this.tightMarshalNestedObject2(wireFormat, info.getConsumerId(), dataOut, bs);
        dataOut.writeInt(info.getPrefetch());
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConsumerControl info = (ConsumerControl)o;
        info.setDestination((ActiveMQDestination)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setClose(dataIn.readBoolean());
        info.setConsumerId((ConsumerId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setPrefetch(dataIn.readInt());
        info.setFlush(dataIn.readBoolean());
        info.setStart(dataIn.readBoolean());
        info.setStop(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConsumerControl info = (ConsumerControl)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getDestination(), dataOut);
        dataOut.writeBoolean(info.isClose());
        this.looseMarshalNestedObject(wireFormat, info.getConsumerId(), dataOut);
        dataOut.writeInt(info.getPrefetch());
        dataOut.writeBoolean(info.isFlush());
        dataOut.writeBoolean(info.isStart());
        dataOut.writeBoolean(info.isStop());
    }
}
