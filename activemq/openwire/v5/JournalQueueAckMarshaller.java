// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v5;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.JournalQueueAck;
import org.apache.activemq.command.DataStructure;

public class JournalQueueAckMarshaller extends BaseDataStreamMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 52;
    }
    
    @Override
    public DataStructure createObject() {
        return new JournalQueueAck();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final JournalQueueAck info = (JournalQueueAck)o;
        info.setDestination((ActiveMQDestination)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
        info.setMessageAck((MessageAck)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final JournalQueueAck info = (JournalQueueAck)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getDestination(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getMessageAck(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final JournalQueueAck info = (JournalQueueAck)o;
        this.tightMarshalNestedObject2(wireFormat, info.getDestination(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getMessageAck(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final JournalQueueAck info = (JournalQueueAck)o;
        info.setDestination((ActiveMQDestination)this.looseUnmarsalNestedObject(wireFormat, dataIn));
        info.setMessageAck((MessageAck)this.looseUnmarsalNestedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final JournalQueueAck info = (JournalQueueAck)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getDestination(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getMessageAck(), dataOut);
    }
}
