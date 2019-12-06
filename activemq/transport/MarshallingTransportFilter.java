// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport;

import java.io.IOException;
import org.apache.activemq.command.Command;
import org.apache.activemq.wireformat.WireFormat;

public class MarshallingTransportFilter extends TransportFilter
{
    private final WireFormat localWireFormat;
    private final WireFormat remoteWireFormat;
    
    public MarshallingTransportFilter(final Transport next, final WireFormat localWireFormat, final WireFormat remoteWireFormat) {
        super(next);
        this.localWireFormat = localWireFormat;
        this.remoteWireFormat = remoteWireFormat;
    }
    
    @Override
    public void oneway(final Object command) throws IOException {
        this.next.oneway(this.remoteWireFormat.unmarshal(this.localWireFormat.marshal(command)));
    }
    
    @Override
    public void onCommand(final Object command) {
        try {
            this.getTransportListener().onCommand(this.localWireFormat.unmarshal(this.remoteWireFormat.marshal(command)));
        }
        catch (IOException e) {
            this.getTransportListener().onException(e);
        }
    }
}
