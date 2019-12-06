// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.openwire;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.command.DataStructure;

public interface DataStreamMarshaller
{
    byte getDataStructureType();
    
    DataStructure createObject();
    
    int tightMarshal1(final OpenWireFormat p0, final Object p1, final BooleanStream p2) throws IOException;
    
    void tightMarshal2(final OpenWireFormat p0, final Object p1, final DataOutput p2, final BooleanStream p3) throws IOException;
    
    void tightUnmarshal(final OpenWireFormat p0, final Object p1, final DataInput p2, final BooleanStream p3) throws IOException;
    
    void looseMarshal(final OpenWireFormat p0, final Object p1, final DataOutput p2) throws IOException;
    
    void looseUnmarshal(final OpenWireFormat p0, final Object p1, final DataInput p2) throws IOException;
}
