// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire.v9;

import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.openwire.BooleanStream;
import java.io.DataInput;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.command.ExceptionResponse;
import org.apache.activemq.command.DataStructure;

public class ExceptionResponseMarshaller extends ResponseMarshaller
{
    @Override
    public byte getDataStructureType() {
        return 31;
    }
    
    @Override
    public DataStructure createObject() {
        return new ExceptionResponse();
    }
    
    @Override
    public void tightUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn, final BooleanStream bs) throws IOException {
        super.tightUnmarshal(wireFormat, o, dataIn, bs);
        final ExceptionResponse info = (ExceptionResponse)o;
        info.setException(this.tightUnmarsalThrowable(wireFormat, dataIn, bs));
    }
    
    @Override
    public int tightMarshal1(final OpenWireFormat wireFormat, final Object o, final BooleanStream bs) throws IOException {
        final ExceptionResponse info = (ExceptionResponse)o;
        int rc = super.tightMarshal1(wireFormat, o, bs);
        rc += this.tightMarshalThrowable1(wireFormat, info.getException(), bs);
        return rc + 0;
    }
    
    @Override
    public void tightMarshal2(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut, final BooleanStream bs) throws IOException {
        super.tightMarshal2(wireFormat, o, dataOut, bs);
        final ExceptionResponse info = (ExceptionResponse)o;
        this.tightMarshalThrowable2(wireFormat, info.getException(), dataOut, bs);
    }
    
    @Override
    public void looseUnmarshal(final OpenWireFormat wireFormat, final Object o, final DataInput dataIn) throws IOException {
        super.looseUnmarshal(wireFormat, o, dataIn);
        final ExceptionResponse info = (ExceptionResponse)o;
        info.setException(this.looseUnmarsalThrowable(wireFormat, dataIn));
    }
    
    @Override
    public void looseMarshal(final OpenWireFormat wireFormat, final Object o, final DataOutput dataOut) throws IOException {
        final ExceptionResponse info = (ExceptionResponse)o;
        super.looseMarshal(wireFormat, o, dataOut);
        this.looseMarshalThrowable(wireFormat, info.getException(), dataOut);
    }
}
