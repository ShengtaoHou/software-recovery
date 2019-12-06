// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.MessageDispatchNotification;
import org.apache.activemq.command.DataStructure;

public class MessageDispatchNotificationMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 90;
    }
    
    @Override
    public DataStructure createObject() {
        return new MessageDispatchNotification();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final MessageDispatchNotification info = (MessageDispatchNotification)o;
        info.setConsumerId((ConsumerId)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDestination((ActiveMQDestination)this.tightUnmarsalCachedObject(wireFormat, dataIn, bs));
        info.setDeliverySequenceId(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setMessageId((MessageId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final MessageDispatchNotification info = (MessageDispatchNotification)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getConsumerId(), bs);
        rc += this.tightMarshalCachedObject1(wireFormat, info.getDestination(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getDeliverySequenceId(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getMessageId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final MessageDispatchNotification info = (MessageDispatchNotification)o;
        this.tightMarshalCachedObject2(wireFormat, info.getConsumerId(), dataOut, bs);
        this.tightMarshalCachedObject2(wireFormat, info.getDestination(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getDeliverySequenceId(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getMessageId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final MessageDispatchNotification info = (MessageDispatchNotification)o;
        info.setConsumerId((ConsumerId)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDestination((ActiveMQDestination)this.looseUnmarsalCachedObject(wireFormat, dataIn));
        info.setDeliverySequenceId(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setMessageId((MessageId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final MessageDispatchNotification info = (MessageDispatchNotification)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getConsumerId(), dataOut);
        this.looseMarshalCachedObject(wireFormat, info.getDestination(), dataOut);
        this.looseMarshalLong(wireFormat, info.getDeliverySequenceId(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getMessageId(), dataOut);
    }
}
