// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ConnectionError;
import org.apache.activemq.command.DataStructure;

public class ConnectionErrorMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 16;
    }
    
    @Override
    public DataStructure createObject() {
        return new ConnectionError();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ConnectionError info = (ConnectionError)o;
        info.setException(this.tightUnmarsalThrowable(wireFormat, dataIn, bs));
        info.setConnectionId((ConnectionId)this.tightUnmarsalNestedObject(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ConnectionError info = (ConnectionError)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalThrowable1(wireFormat, info.getException(), bs);
        rc += this.tightMarshalNestedObject1(wireFormat, info.getConnectionId(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ConnectionError info = (ConnectionError)o;
        this.tightMarshalThrowable2(wireFormat, info.getException(), dataOut, bs);
        this.tightMarshalNestedObject2(wireFormat, info.getConnectionId(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ConnectionError info = (ConnectionError)o;
        info.setException(this.looseUnmarsalThrowable(wireFormat, dataIn));
        info.setConnectionId((ConnectionId)this.looseUnmarsalNestedObject(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ConnectionError info = (ConnectionError)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalThrowable(wireFormat, info.getException(), dataOut);
        this.looseMarshalNestedObject(wireFormat, info.getConnectionId(), dataOut);
    }
}
