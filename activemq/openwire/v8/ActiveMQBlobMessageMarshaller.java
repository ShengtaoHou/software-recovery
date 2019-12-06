// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v8;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.DataStructure;

public class ActiveMQBlobMessageMarshaller extends ActiveMQMessageMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 29;
    }
    
    @Override
    public DataStructure createObject() {
        return new ActiveMQBlobMessage();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ActiveMQBlobMessage info = (ActiveMQBlobMessage)o;
        info.setRemoteBlobUrl(this.tightUnmarshalString(dataIn, bs));
        info.setMimeType(this.tightUnmarshalString(dataIn, bs));
        info.setDeletedByBroker(bs.readBoolean());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ActiveMQBlobMessage info = (ActiveMQBlobMessage)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalString1(info.getRemoteBlobUrl(), bs);
        rc += this.tightMarshalString1(info.getMimeType(), bs);
        bs.writeBoolean(info.isDeletedByBroker());
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ActiveMQBlobMessage info = (ActiveMQBlobMessage)o;
        this.tightMarshalString2(info.getRemoteBlobUrl(), dataOut, bs);
        this.tightMarshalString2(info.getMimeType(), dataOut, bs);
        bs.readBoolean();
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ActiveMQBlobMessage info = (ActiveMQBlobMessage)o;
        info.setRemoteBlobUrl(this.looseUnmarshalString(dataIn));
        info.setMimeType(this.looseUnmarshalString(dataIn));
        info.setDeletedByBroker(dataIn.readBoolean());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ActiveMQBlobMessage info = (ActiveMQBlobMessage)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalString(info.getRemoteBlobUrl(), dataOut);
        this.looseMarshalString(info.getMimeType(), dataOut);
        dataOut.writeBoolean(info.isDeletedByBroker());
    }
}
