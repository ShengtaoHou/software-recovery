// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.wireformat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.activemq.util.ByteSequence;

public interface WireFormat
{
    ByteSequence marshal(final Object p0) throws IOException;
    
    Object unmarshal(final ByteSequence p0) throws IOException;
    
    void marshal(final Object p0, final DataOutput p1) throws IOException;
    
    Object unmarshal(final DataInput p0) throws IOException;
    
    void setVersion(final int p0);
    
    int getVersion();
}
