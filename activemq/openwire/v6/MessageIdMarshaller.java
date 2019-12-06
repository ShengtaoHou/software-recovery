// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v6;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.DataStructure;

public class MessageIdMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 110;
    }
    
    @Override
    public DataStructure createObject() {
        return new MessageId();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final MessageId info = (MessageId)o;
        info.setProducerId((ProducerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setProducerSequenceId(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerSequenceId(this.tightUnmarshalLong(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final MessageId info = (MessageId)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getProducerId(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getProducerSequenceId(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getBrokerSequenceId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final MessageId info = (MessageId)o;
        this.tightMarshalCachedObject2(wireFormat, info.getProducerId(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getProducerSequenceId(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getBrokerSequenceId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final MessageId info = (MessageId)o;
        info.setProducerId((ProducerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setProducerSequenceId(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerSequenceId(this.looseUnmarshalLong(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final MessageId info = (MessageId)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getProducerId(), dataOut);
        this.looseMarshalLong(wireFormat, info.getProducerSequenceId(), dataOut);
        this.looseMarshalLong(wireFormat, info.getBrokerSequenceId(), dataOut);
    }
}
