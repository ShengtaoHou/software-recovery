// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.Command;

public interface ResponseHandler
{
    void onResponse(final Command p0);
}
