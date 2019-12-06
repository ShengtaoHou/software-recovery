// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;
import java.util.Map;

public abstract class BaseCommand implements Command
{
    protected int commandId;
    protected boolean responseRequired;
    private transient Endpoint from;
    private transient Endpoint to;
    
    public void copy(final BaseCommand copy) {
        copy.commandId = this.commandId;
        copy.responseRequired = this.responseRequired;
    }
    
    @Override
    public int getCommandId() {
        return this.commandId;
    }
    
    @Override
    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }
    
    @Override
    public boolean isResponseRequired() {
        return this.responseRequired;
    }
    
    @Override
    public void setResponseRequired(final boolean responseRequired) {
        this.responseRequired = responseRequired;
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(final Map<String, Object> overrideFields) {
        return IntrospectionSupport.toString(this, BaseCommand.class, overrideFields);
    }
    
    @Override
    public boolean isWireFormatInfo() {
        return false;
    }
    
    @Override
    public boolean isBrokerInfo() {
        return false;
    }
    
    @Override
    public boolean isResponse() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatch() {
        return false;
    }
    
    @Override
    public boolean isMessage() {
        return false;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public boolean isMessageAck() {
        return false;
    }
    
    @Override
    public boolean isMessageDispatchNotification() {
        return false;
    }
    
    @Override
    public boolean isShutdownInfo() {
        return false;
    }
    
    @Override
    public boolean isConnectionControl() {
        return false;
    }
    
    @Override
    public Endpoint getFrom() {
        return this.from;
    }
    
    @Override
    public void setFrom(final Endpoint from) {
        this.from = from;
    }
    
    @Override
    public Endpoint getTo() {
        return this.to;
    }
    
    @Override
    public void setTo(final Endpoint to) {
        this.to = to;
    }
}
