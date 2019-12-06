// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transport.mqtt;

import java.io.IOException;
import org.apache.activemq.command.Response;

interface ResponseHandler
{
    void onResponse(final MQTTProtocolConverter p0, final Response p1) throws IOException;
}
