// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.stomp;

import java.io.IOException;
import org.apache.activemq.command.Response;

interface ResponseHandler
{
    void onResponse(final ProtocolConverter p0, final Response p1) throws IOException;
}
