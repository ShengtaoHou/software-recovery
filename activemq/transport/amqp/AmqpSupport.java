// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import org.fusesource.hawtbuf.Buffer;
import java.nio.ByteBuffer;

public class AmqpSupport
{
    public static Buffer toBuffer(final ByteBuffer data) {
        if (data == null) {
            return null;
        }
        Buffer rc;
        if (data.isDirect()) {
            rc = new Buffer(data.remaining());
            data.get(rc.data);
        }
        else {
            rc = new Buffer(data);
            data.position(data.position() + data.remaining());
        }
        return rc;
    }
}
