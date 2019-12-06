// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.multicast;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.wireformat.WireFormat;
import java.net.URI;
import org.apache.activemq.transport.udp.UdpTransportFactory;

public class MulticastTransportFactory extends UdpTransportFactory
{
    @Override
    protected Transport createTransport(final URI location, final WireFormat wf) throws UnknownHostException, IOException {
        final OpenWireFormat wireFormat = this.asOpenWireFormat(wf);
        return new MulticastTransport(wireFormat, location);
    }
}
