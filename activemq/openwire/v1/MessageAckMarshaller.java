// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v1;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.DataStructure;

public class MessageAckMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 22;
    }
    
    @Override
    public DataStructure createObject() {
        return new MessageAck();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final MessageAck info = (MessageAck)o;
        info.setDestination((ActiveMQDestination)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setTransactionId((TransactionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setConsumerId((ConsumerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setAckType(dataIn.readByte());
        info.setFirstMessageId((MessageId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setLastMessageId((MessageId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setMessageCount(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final MessageAck info = (MessageAck)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getDestination(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getTransactionId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConsumerId(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getFirstMessageId(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getLastMessageId(), bs);
        return rc + 5;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final MessageAck info = (MessageAck)o;
        this.tightMarshalCachedObject2(wireFormat, info.getDestination(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getConsumerId(), dataOut, bs);
        dataOut.writeByte(info.getAckType());
        this.tightMarshalNestedObject2(wireFormat, info.getFirstMessageId(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getLastMessageId(), dataOut, bs);
        dataOut.writeInt(info.getMessageCount());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final MessageAck info = (MessageAck)o;
        info.setDestination((ActiveMQDestination)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setTransactionId((TransactionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setConsumerId((ConsumerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setAckType(dataIn.readByte());
        info.setFirstMessageId((MessageId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setLastMessageId((MessageId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setMessageCount(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final MessageAck info = (MessageAck)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getDestination(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getTransactionId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConsumerId(), dataOut);
        dataOut.writeByte(info.getAckType());
        this.looseMarshalNestedObject(wireFormat, info.getFirstMessageId(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getLastMessageId(), dataOut);
        dataOut.writeInt(info.getMessageCount());
    }
}
