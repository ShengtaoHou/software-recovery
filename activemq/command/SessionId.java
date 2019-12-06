// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class SessionId implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 121;
    protected String connectionId;
    protected long value;
    protected transient int hashCode;
    protected transient String key;
    protected transient ConnectionId parentId;
    
    public SessionId() {
    }
    
    public SessionId(final ConnectionId connectionId, final long sessionId) {
        this.connectionId = connectionId.getValue();
        this.value = sessionId;
    }
    
    public SessionId(final SessionId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getValue();
    }
    
    public SessionId(final ProducerId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getSessionId();
    }
    
    public SessionId(final ConsumerId id) {
        this.connectionId = id.getConnectionId();
        this.value = id.getSessionId();
    }
    
    public ConnectionId getParentId() {
        if (this.parentId == null) {
            this.parentId = new ConnectionId(this);
        }
        return this.parentId;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (this.connectionId.hashCode() ^ (int)this.value);
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != SessionId.class) {
            return false;
        }
        final SessionId id = (SessionId)o;
        return this.value == id.value && this.connectionId.equals(id.connectionId);
    }
    
    @Override
    public byte getDataStructureType() {
        return 121;
    }
    
    public String getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final String connectionId) {
        this.connectionId = connectionId;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public void setValue(final long sessionId) {
        this.value = sessionId;
    }
    
    @Override
    public String toString() {
        if (this.key == null) {
            this.key = this.connectionId + ":" + this.value;
        }
        return this.key;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
