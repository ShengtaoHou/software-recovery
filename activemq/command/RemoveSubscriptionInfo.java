// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class RemoveSubscriptionInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 9;
    protected ConnectionId connectionId;
    protected String clientId;
    protected String subscriptionName;
    
    @Override
    public byte getDataStructureType() {
        return 9;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getSubcriptionName() {
        return this.subscriptionName;
    }
    
    public void setSubcriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }
    
    public String getSubscriptionName() {
        return this.subscriptionName;
    }
    
    public void setSubscriptionName(final String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }
    
    public String getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processRemoveSubscription(this);
    }
}
