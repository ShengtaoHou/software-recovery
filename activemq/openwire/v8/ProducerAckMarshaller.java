// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v8;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ProducerAck;
import org.apache.activemq.command.DataStructure;

public class ProducerAckMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 19;
    }
    
    @Override
    public DataStructure createObject() {
        return new ProducerAck();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ProducerAck info = (ProducerAck)o;
        info.setProducerId((ProducerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setSize(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ProducerAck info = (ProducerAck)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getProducerId(), bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ProducerAck info = (ProducerAck)o;
        this.tightMarshalNestedObject2(wireFormat, info.getProducerId(), dataOut, bs);
        dataOut.writeInt(info.getSize());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ProducerAck info = (ProducerAck)o;
        info.setProducerId((ProducerId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setSize(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ProducerAck info = (ProducerAck)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getProducerId(), dataOut);
        dataOut.writeInt(info.getSize());
    }
}
