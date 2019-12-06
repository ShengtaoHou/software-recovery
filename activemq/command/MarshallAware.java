// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.io.IOException;
import org.apache.activemq.wireformat.WireFormat;

public interface MarshallAware
{
    void beforeMarshall(final WireFormat p0) throws IOException;
    
    void afterMarshall(final WireFormat p0) throws IOException;
    
    void beforeUnmarshall(final WireFormat p0) throws IOException;
    
    void afterUnmarshall(final WireFormat p0) throws IOException;
}
