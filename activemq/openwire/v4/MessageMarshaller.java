// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v4;

import java.io.DataOutput;
import org.apache.activemq.command.DataStructure;
import java.io.IOException;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.command.Message;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;

public abstract class MessageMarshaller extends BaseCommandMarshaller
{
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final Message info = (Message)o;
        info.beforeUnmarshall(wireFormat);
        info.setProducerId((ProducerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((ActiveMQDestination)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setTransactionId((TransactionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setOriginalDestination((ActiveMQDestination)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setMessageId((MessageId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setOriginalTransactionId((TransactionId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setGroupID(this.tightUnmarshalString(dataIn, bs));
        info.setGroupSequence(dataIn.readInt());
        info.setCorrelationId(this.tightUnmarshalString(dataIn, bs));
        info.setPersistent(bs.readBoolean());
        info.setExpiration(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setPriority(dataIn.readByte());
        info.setReplyTo((ActiveMQDestination)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setTimestamp(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setType(this.tightUnmarshalString(dataIn, bs));
        info.setContent(this.tightUnmarshalByteSequence(dataIn, bs));
        info.setMarshalledProperties(this.tightUnmarshalByteSequence(dataIn, bs));
        info.setDataStructure(this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setTargetConsumerId((ConsumerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setCompressed(bs.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }
        info.setArrival(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setUserID(this.tightUnmarshalString(dataIn, bs));
        info.setRecievedByDFBridge(bs.readBoolean());
        info.setDroppable(bs.readBoolean());
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setCluster(value);
        }
        else {
            info.setCluster(null);
        }
        info.setBrokerInTime(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setBrokerOutTime(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.afterUnmarshall(wireFormat);
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final Message info = (Message)o;
        info.beforeMarshall(wireFormat);
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getProducerId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getDestination(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getTransactionId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getOriginalDestination(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getMessageId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getOriginalTransactionId(), bs);
        rc += this.tightMarshalString1(info.getGroupID(), bs);
        rc += this.tightMarshalString1(info.getCorrelationId(), bs);
        bs.writeBoolean(info.isPersistent());
        rc += this.tightMarshalLong1(wireFormat, info.getExpiration(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getReplyTo(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getTimestamp(), bs);
        rc += this.tightMarshalString1(info.getType(), bs);
        rc += this.tightMarshalByteSequence1(info.getContent(), bs);
        rc += this.tightMarshalByteSequence1(info.getMarshalledProperties(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getDataStructure(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getTargetConsumerId(), bs);
        bs.writeBoolean(info.isCompressed());
        rc += this.tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getArrival(), bs);
        rc += this.tightMarshalString1(info.getUserID(), bs);
        bs.writeBoolean(info.isRecievedByDFBridge());
        bs.writeBoolean(info.isDroppable());
        rc += this.tightMarshalObjectArray1(wireFormat, info.getCluster(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getBrokerInTime(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getBrokerOutTime(), bs);
        return rc + 9;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final Message info = (Message)o;
        this.tightMarshalCachedObject2(wireFormat, info.getProducerId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getDestination(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getOriginalDestination(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getMessageId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getOriginalTransactionId(), dataOut, bs);
        this.tightMarshalString2(info.getGroupID(), dataOut, bs);
        dataOut.writeInt(info.getGroupSequence());
        this.tightMarshalString2(info.getCorrelationId(), dataOut, bs);
        bs.readBoolean();
        this.tightMarshalLong2(wireFormat, info.getExpiration(), dataOut, bs);
        dataOut.writeByte(info.getPriority());
        this.tightMarshalNestedObject2(wireFormat, info.getReplyTo(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getTimestamp(), dataOut, bs);
        this.tightMarshalString2(info.getType(), dataOut, bs);
        this.tightMarshalByteSequence2(info.getContent(), dataOut, bs);
        this.tightMarshalByteSequence2(info.getMarshalledProperties(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getDataStructure(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getTargetConsumerId(), dataOut, bs);
        bs.readBoolean();
        dataOut.writeInt(info.getRedeliveryCounter());
        this.tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getArrival(), dataOut, bs);
        this.tightMarshalString2(info.getUserID(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        this.tightMarshalObjectArray2(wireFormat, info.getCluster(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getBrokerInTime(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getBrokerOutTime(), dataOut, bs);
        info.afterMarshall(wireFormat);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final Message info = (Message)o;
        info.beforeUnmarshall(wireFormat);
        info.setProducerId((ProducerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDestination((ActiveMQDestination)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setTransactionId((TransactionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setOriginalDestination((ActiveMQDestination)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setMessageId((MessageId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setOriginalTransactionId((TransactionId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setGroupID(this.looseUnmarshalString(dataIn));
        info.setGroupSequence(dataIn.readInt());
        info.setCorrelationId(this.looseUnmarshalString(dataIn));
        info.setPersistent(dataIn.readBoolean());
        info.setExpiration(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setPriority(dataIn.readByte());
        info.setReplyTo((ActiveMQDestination)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setTimestamp(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setType(this.looseUnmarshalString(dataIn));
        info.setContent(this.looseUnmarshalByteSequence(dataIn));
        info.setMarshalledProperties(this.looseUnmarshalByteSequence(dataIn));
        info.setDataStructure(this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setTargetConsumerId((ConsumerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setCompressed(dataIn.readBoolean());
        info.setRedeliveryCounter(dataIn.readInt());
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setBrokerPath(value);
        }
        else {
            info.setBrokerPath(null);
        }
        info.setArrival(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setUserID(this.looseUnmarshalString(dataIn));
        info.setRecievedByDFBridge(dataIn.readBoolean());
        info.setDroppable(dataIn.readBoolean());
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final BrokerId[] value = new BrokerId[size];
            for (int i = 0; i < size; ++i) {
                value[i] = (BrokerId)this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setCluster(value);
        }
        else {
            info.setCluster(null);
        }
        info.setBrokerInTime(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setBrokerOutTime(this.looseUnmarshalLong(wireFormat, dataIn));
        info.afterUnmarshall(wireFormat);
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final Message info = (Message)o;
        info.beforeMarshall(wireFormat);
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getProducerId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getDestination(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getTransactionId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getOriginalDestination(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getMessageId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getOriginalTransactionId(), dataOut);
        this.looseMarshalString(info.getGroupID(), dataOut);
        dataOut.writeInt(info.getGroupSequence());
        this.looseMarshalString(info.getCorrelationId(), dataOut);
        dataOut.writeBoolean(info.isPersistent());
        this.looseMarshalLong(wireFormat, info.getExpiration(), dataOut);
        dataOut.writeByte(info.getPriority());
        this.looseMarshalNestedObject(wireFormat, info.getReplyTo(), dataOut);
        this.looseMarshalLong(wireFormat, info.getTimestamp(), dataOut);
        this.looseMarshalString(info.getType(), dataOut);
        this.looseMarshalByteSequence(wireFormat, info.getContent(), dataOut);
        this.looseMarshalByteSequence(wireFormat, info.getMarshalledProperties(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getDataStructure(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getTargetConsumerId(), dataOut);
        dataOut.writeBoolean(info.isCompressed());
        dataOut.writeInt(info.getRedeliveryCounter());
        this.looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        this.looseMarshalLong(wireFormat, info.getArrival(), dataOut);
        this.looseMarshalString(info.getUserID(), dataOut);
        dataOut.writeBoolean(info.isRecievedByDFBridge());
        dataOut.writeBoolean(info.isDroppable());
        this.looseMarshalObjectArray(wireFormat, info.getCluster(), dataOut);
        this.looseMarshalLong(wireFormat, info.getBrokerInTime(), dataOut);
        this.looseMarshalLong(wireFormat, info.getBrokerOutTime(), dataOut);
    }
}
