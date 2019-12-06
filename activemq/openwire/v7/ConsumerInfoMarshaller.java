// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v7;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.filter.BooleanExpression;
import org.apache.activemq.command.BrokerId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.DataStructure;

public class ConsumerInfoMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 5;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConsumerInfo();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConsumerInfo info = (ConsumerInfo)o;
        info.setConsumerId((ConsumerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setBrowser(bs.readBoolean());
        info.setDestination((ActiveMQDestination)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setPrefetchSize(dataIn.readInt());
        info.setMaximumPendingMessageLimit(dataIn.readInt());
        info.setDispatchAsync(bs.readBoolean());
        info.setSelector(this.tightUnmarshalString(dataIn, bs));
        info.setSubscriptionName(this.tightUnmarshalString(dataIn, bs));
        info.setNoLocal(bs.readBoolean());
        info.setExclusive(bs.readBoolean());
        info.setRetroactive(bs.readBoolean());
        info.setPriority(dataIn.readByte());
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
        info.setAdditionalPredicate((BooleanExpression)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setNetworkSubscription(bs.readBoolean());
        info.setOptimizedAcknowledge(bs.readBoolean());
        info.setNoRangeAcks(bs.readBoolean());
        if (bs.readBoolean()) {
            final short size = dataIn.readShort();
            final ConsumerId[] value2 = new ConsumerId[size];
            for (int i = 0; i < size; ++i) {
                value2[i] = (ConsumerId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs);
            }
            info.setNetworkConsumerPath(value2);
        }
        else {
            info.setNetworkConsumerPath(null);
        }
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConsumerInfo info = (ConsumerInfo)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConsumerId(), bs);
        bs.writeBoolean(info.isBrowser());
        rc += this.tightMarshalCachedObject1(wireFormat, info.getDestination(), bs);
        bs.writeBoolean(info.isDispatchAsync());
        rc += this.tightMarshalString1(info.getSelector(), bs);
        rc += this.tightMarshalString1(info.getSubscriptionName(), bs);
        bs.writeBoolean(info.isNoLocal());
        bs.writeBoolean(info.isExclusive());
        bs.writeBoolean(info.isRetroactive());
        rc += this.tightMarshalObjectArray1(wireFormat, info.getBrokerPath(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, (DataStructure)info.getAdditionalPredicate(), bs);
        bs.writeBoolean(info.isNetworkSubscription());
        bs.writeBoolean(info.isOptimizedAcknowledge());
        bs.writeBoolean(info.isNoRangeAcks());
        rc += this.tightMarshalObjectArray1(wireFormat, info.getNetworkConsumerPath(), bs);
        return rc + 9;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConsumerInfo info = (ConsumerInfo)o;
        this.tightMarshalCachedObject2(wireFormat, info.getConsumerId(), dataOut, bs);
        bs.readBoolean();
        this.tightMarshalCachedObject2(wireFormat, info.getDestination(), dataOut, bs);
        dataOut.writeInt(info.getPrefetchSize());
        dataOut.writeInt(info.getMaximumPendingMessageLimit());
        bs.readBoolean();
        this.tightMarshalString2(info.getSelector(), dataOut, bs);
        this.tightMarshalString2(info.getSubscriptionName(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        dataOut.writeByte(info.getPriority());
        this.tightMarshalObjectArray2(wireFormat, info.getBrokerPath(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, (DataStructure)info.getAdditionalPredicate(), dataOut, bs);
        bs.readBoolean();
        bs.readBoolean();
        bs.readBoolean();
        this.tightMarshalObjectArray2(wireFormat, info.getNetworkConsumerPath(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConsumerInfo info = (ConsumerInfo)o;
        info.setConsumerId((ConsumerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setBrowser(dataIn.readBoolean());
        info.setDestination((ActiveMQDestination)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setPrefetchSize(dataIn.readInt());
        info.setMaximumPendingMessageLimit(dataIn.readInt());
        info.setDispatchAsync(dataIn.readBoolean());
        info.setSelector(this.looseUnmarshalString(dataIn));
        info.setSubscriptionName(this.looseUnmarshalString(dataIn));
        info.setNoLocal(dataIn.readBoolean());
        info.setExclusive(dataIn.readBoolean());
        info.setRetroactive(dataIn.readBoolean());
        info.setPriority(dataIn.readByte());
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
        info.setAdditionalPredicate((BooleanExpression)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setNetworkSubscription(dataIn.readBoolean());
        info.setOptimizedAcknowledge(dataIn.readBoolean());
        info.setNoRangeAcks(dataIn.readBoolean());
        if (dataIn.readBoolean()) {
            final short size = dataIn.readShort();
            final ConsumerId[] value2 = new ConsumerId[size];
            for (int i = 0; i < size; ++i) {
                value2[i] = (ConsumerId)this.looseUnmarsalNestedObject(wireFormat, dataIn);
            }
            info.setNetworkConsumerPath(value2);
        }
        else {
            info.setNetworkConsumerPath(null);
        }
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConsumerInfo info = (ConsumerInfo)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConsumerId(), dataOut);
        dataOut.writeBoolean(info.isBrowser());
        this.looseMarshalCachedObject(wireFormat, info.getDestination(), dataOut);
        dataOut.writeInt(info.getPrefetchSize());
        dataOut.writeInt(info.getMaximumPendingMessageLimit());
        dataOut.writeBoolean(info.isDispatchAsync());
        this.looseMarshalString(info.getSelector(), dataOut);
        this.looseMarshalString(info.getSubscriptionName(), dataOut);
        dataOut.writeBoolean(info.isNoLocal());
        dataOut.writeBoolean(info.isExclusive());
        dataOut.writeBoolean(info.isRetroactive());
        dataOut.writeByte(info.getPriority());
        this.looseMarshalObjectArray(wireFormat, info.getBrokerPath(), dataOut);
        this.looseMarshalNestedObject(wireFormat, (DataStructure)info.getAdditionalPredicate(), dataOut);
        dataOut.writeBoolean(info.isNetworkSubscription());
        dataOut.writeBoolean(info.isOptimizedAcknowledge());
        dataOut.writeBoolean(info.isNoRangeAcks());
        this.looseMarshalObjectArray(wireFormat, info.getNetworkConsumerPath(), dataOut);
    }
}
