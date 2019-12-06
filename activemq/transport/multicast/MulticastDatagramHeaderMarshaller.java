// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.multicast;

import org.apache.activemq.command.Command;
import org.apache.activemq.transport.udp.DatagramEndpoint;
import org.apache.activemq.command.Endpoint;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.apache.activemq.transport.udp.DatagramHeaderMarshaller;

public class MulticastDatagramHeaderMarshaller extends DatagramHeaderMarshaller
{
    private final byte[] localUriAsBytes;
    
    public MulticastDatagramHeaderMarshaller(final String localUri) {
        this.localUriAsBytes = localUri.getBytes();
    }
    
    @Override
    public Endpoint createEndpoint(final ByteBuffer readBuffer, final SocketAddress address) {
        final int size = readBuffer.getInt();
        final byte[] data = new byte[size];
        readBuffer.get(data);
        return new DatagramEndpoint(new String(data), address);
    }
    
    @Override
    public void writeHeader(final Command command, final ByteBuffer writeBuffer) {
        writeBuffer.putInt(this.localUriAsBytes.length);
        writeBuffer.put(this.localUriAsBytes);
        super.writeHeader(command, writeBuffer);
    }
}
