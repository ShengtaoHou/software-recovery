// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v2;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.DataStructure;

public class ResponseMarshaller extends BaseCommandMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 30;
    }
    
    @Override
    public DataStructure createObject() {
        return new Response();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final Response info = (Response)o;
        info.setCorrelationId(dataIn.readInt());
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final Response info = (Response)o;
        final int rc = super.tightMarshal1(wireFormat, o, bs);
        return rc + 4;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final Response info = (Response)o;
        dataOut.writeInt(info.getCorrelationId());
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final Response info = (Response)o;
        info.setCorrelationId(dataIn.readInt());
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final Response info = (Response)o;
        super.looseMarshal(wireFormat, o, dataOut);
        dataOut.writeInt(info.getCorrelationId());
    }
}
