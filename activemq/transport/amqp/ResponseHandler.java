// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.amqp;

import java.io.IOException;
import org.apache.activemq.command.Response;

interface ResponseHandler
{
    void onResponse(final IAmqpProtocolConverter p0, final Response p1) throws IOException;
}
