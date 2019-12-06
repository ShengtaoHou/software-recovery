// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class ConnectionInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 3;
    protected ConnectionId connectionId;
    protected String clientId;
    protected String clientIp;
    protected String userName;
    protected String password;
    protected BrokerId[] brokerPath;
    protected boolean brokerMasterConnector;
    protected boolean manageable;
    protected boolean clientMaster;
    protected boolean faultTolerant;
    protected boolean failoverReconnect;
    protected transient Object transportContext;
    
    public ConnectionInfo() {
        this.clientMaster = true;
        this.faultTolerant = false;
    }
    
    public ConnectionInfo(final ConnectionId connectionId) {
        this.clientMaster = true;
        this.faultTolerant = false;
        this.connectionId = connectionId;
    }
    
    @Override
    public byte getDataStructureType() {
        return 3;
    }
    
    public ConnectionInfo copy() {
        final ConnectionInfo copy = new ConnectionInfo();
        this.copy(copy);
        return copy;
    }
    
    private void copy(final ConnectionInfo copy) {
        super.copy(copy);
        copy.connectionId = this.connectionId;
        copy.clientId = this.clientId;
        copy.userName = this.userName;
        copy.password = this.password;
        copy.brokerPath = this.brokerPath;
        copy.brokerMasterConnector = this.brokerMasterConnector;
        copy.manageable = this.manageable;
        copy.clientMaster = this.clientMaster;
        copy.transportContext = this.transportContext;
        copy.faultTolerant = this.faultTolerant;
        copy.clientIp = this.clientIp;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    public RemoveInfo createRemoveCommand() {
        final RemoveInfo command = new RemoveInfo(this.getConnectionId());
        command.setResponseRequired(this.isResponseRequired());
        return command;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public BrokerId[] getBrokerPath() {
        return this.brokerPath;
    }
    
    public void setBrokerPath(final BrokerId[] brokerPath) {
        this.brokerPath = brokerPath;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processAddConnection(this);
    }
    
    public boolean isBrokerMasterConnector() {
        return this.brokerMasterConnector;
    }
    
    public void setBrokerMasterConnector(final boolean slaveBroker) {
        this.brokerMasterConnector = slaveBroker;
    }
    
    public boolean isManageable() {
        return this.manageable;
    }
    
    public void setManageable(final boolean manageable) {
        this.manageable = manageable;
    }
    
    public Object getTransportContext() {
        return this.transportContext;
    }
    
    public void setTransportContext(final Object transportContext) {
        this.transportContext = transportContext;
    }
    
    public boolean isClientMaster() {
        return this.clientMaster;
    }
    
    public void setClientMaster(final boolean clientMaster) {
        this.clientMaster = clientMaster;
    }
    
    public boolean isFaultTolerant() {
        return this.faultTolerant;
    }
    
    public void setFaultTolerant(final boolean faultTolerant) {
        this.faultTolerant = faultTolerant;
    }
    
    public boolean isFailoverReconnect() {
        return this.failoverReconnect;
    }
    
    public void setFailoverReconnect(final boolean failoverReconnect) {
        this.failoverReconnect = failoverReconnect;
    }
    
    public String getClientIp() {
        return this.clientIp;
    }
    
    public void setClientIp(final String clientIp) {
        this.clientIp = clientIp;
    }
}
