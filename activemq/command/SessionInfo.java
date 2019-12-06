// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.state.CommandVisitor;

public class SessionInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 4;
    protected SessionId sessionId;
    
    public SessionInfo() {
        this.sessionId = new SessionId();
    }
    
    public SessionInfo(final ConnectionInfo connectionInfo, final long sessionId) {
        this.sessionId = new SessionId(connectionInfo.getConnectionId(), sessionId);
    }
    
    public SessionInfo(final SessionId sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public byte getDataStructureType() {
        return 4;
    }
    
    public SessionId getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final SessionId sessionId) {
        this.sessionId = sessionId;
    }
    
    public RemoveInfo createRemoveCommand() {
        final RemoveInfo command = new RemoveInfo(this.getSessionId());
        command.setResponseRequired(this.isResponseRequired());
        return command;
    }
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        return visitor.processAddSession(this);
    }
}
