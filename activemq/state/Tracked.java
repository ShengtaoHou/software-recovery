// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.Command;
import org.apache.activemq.command.Response;

public class Tracked extends Response
{
    private ResponseHandler handler;
    
    public Tracked(final ResponseHandler runnable) {
        this.handler = runnable;
    }
    
    public void onResponses(final Command command) {
        if (this.handler != null) {
            this.handler.onResponse(command);
            this.handler = null;
        }
    }
    
    public boolean isWaitingForResponse() {
        return this.handler != null;
    }
}
