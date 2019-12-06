// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class PartialCommand implements Command
{
    public static final byte DATA_STRUCTURE_TYPE = 60;
    private int commandId;
    private byte[] data;
    private transient Endpoint from;
    private transient Endpoint to;
    
    @Override
    public byte getDataStructureType() {
        return 60;
    }
    
    @Override
    public int getCommandId() {
        return this.commandId;
    }
    
    @Override
    public void setCommandId(final int commandId) {
        this.commandId = commandId;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] data) {
        this.data = data;
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
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        throw new IllegalStateException("The transport layer should filter out PartialCommand instances but received: " + this);
    }
    
    @Override
    public boolean isResponseRequired() {
        return false;
    }
    
    @Override
    public boolean isResponse() {
        return false;
    }
    
    @Override
    public boolean isBrokerInfo() {
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
    public void setResponseRequired(final boolean responseRequired) {
    }
    
    @Override
    public boolean isWireFormatInfo() {
        return false;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        int size = 0;
        if (this.data != null) {
            size = this.data.length;
        }
        return "PartialCommand[id: " + this.commandId + " data: " + size + " byte(s)]";
    }
}
