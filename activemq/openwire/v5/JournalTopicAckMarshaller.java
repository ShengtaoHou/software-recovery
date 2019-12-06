// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v5;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.JournalTopicAck;
import org.apache.activemq.command.DataStructure;

public class JournalTopicAckMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 50;
    }
    
    @Override
    public DataStructure createObject() {
        return new JournalTopicAck();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final JournalTopicAck info = (JournalTopicAck)o;
        info.setDestination((ActiveMQDestination)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setMessageId((MessageId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setMessageSequenceId(this.tightUnmarshalLong(wireFormat, dataIn, bs));
        info.setSubscritionName(this.tightUnmarshalString(dataIn, bs));
        info.setClientId(this.tightUnmarshalString(dataIn, bs));
        info.setTransactionId((TransactionId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final JournalTopicAck info = (JournalTopicAck)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getDestination(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getMessageId(), bs);
        rc += this.tightMarshalLong1(wireFormat, info.getMessageSequenceId(), bs);
        rc += this.tightMarshalString1(info.getSubscritionName(), bs);
        rc += this.tightMarshalString1(info.getClientId(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getTransactionId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final JournalTopicAck info = (JournalTopicAck)o;
        this.tightMarshalNestedObject2(wireFormat, info.getDestination(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getMessageId(), dataOut, bs);
        this.tightMarshalLong2(wireFormat, info.getMessageSequenceId(), dataOut, bs);
        this.tightMarshalString2(info.getSubscritionName(), dataOut, bs);
        this.tightMarshalString2(info.getClientId(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getTransactionId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final JournalTopicAck info = (JournalTopicAck)o;
        info.setDestination((ActiveMQDestination)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setMessageId((MessageId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setMessageSequenceId(this.looseUnmarshalLong(wireFormat, dataIn));
        info.setSubscritionName(this.looseUnmarshalString(dataIn));
        info.setClientId(this.looseUnmarshalString(dataIn));
        info.setTransactionId((TransactionId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final JournalTopicAck info = (JournalTopicAck)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getDestination(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getMessageId(), dataOut);
        this.looseMarshalLong(wireFormat, info.getMessageSequenceId(), dataOut);
        this.looseMarshalString(info.getSubscritionName(), dataOut);
        this.looseMarshalString(info.getClientId(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getTransactionId(), dataOut);
    }
}
