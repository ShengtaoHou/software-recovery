// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ConnectionControl extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 18;
    protected boolean suspend;
    protected boolean resume;
    protected boolean close;
    protected boolean exit;
    protected boolean faultTolerant;
    protected String connectedBrokers;
    protected String reconnectTo;
    protected byte[] token;
    protected boolean rebalanceConnection;
    
    public ConnectionControl() {
        this.connectedBrokers = "";
        this.reconnectTo = "";
    }
    
    @Override
    public byte getDataStructureType() {
        return 18;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processConnectionControl(this);
    }
    
    @Override
    public boolean isConnectionControl() {
        return true;
    }
    
    public boolean isClose() {
        return this.close;
    }
    
    public void setClose(final boolean close) {
        this.close = close;
    }
    
    public boolean isExit() {
        return this.exit;
    }
    
    public void setExit(final boolean exit) {
        this.exit = exit;
    }
    
    public boolean isFaultTolerant() {
        return this.faultTolerant;
    }
    
    public void setFaultTolerant(final boolean faultTolerant) {
        this.faultTolerant = faultTolerant;
    }
    
    public boolean isResume() {
        return this.resume;
    }
    
    public void setResume(final boolean resume) {
        this.resume = resume;
    }
    
    public boolean isSuspend() {
        return this.suspend;
    }
    
    public void setSuspend(final boolean suspend) {
        this.suspend = suspend;
    }
    
    public String getConnectedBrokers() {
        return this.connectedBrokers;
    }
    
    public void setConnectedBrokers(final String connectedBrokers) {
        this.connectedBrokers = connectedBrokers;
    }
    
    public String getReconnectTo() {
        return this.reconnectTo;
    }
    
    public void setReconnectTo(final String reconnectTo) {
        this.reconnectTo = reconnectTo;
    }
    
    public boolean isRebalanceConnection() {
        return this.rebalanceConnection;
    }
    
    public void setRebalanceConnection(final boolean rebalanceConnection) {
        this.rebalanceConnection = rebalanceConnection;
    }
    
    public byte[] getToken() {
        return this.token;
    }
    
    public void setToken(final byte[] token) {
        this.token = token;
    }
}
