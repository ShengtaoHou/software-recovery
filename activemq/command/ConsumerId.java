// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class ConsumerId implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 122;
    protected String connectionId;
    protected long sessionId;
    protected long value;
    protected transient int hashCode;
    protected transient String key;
    protected transient SessionId parentId;
    
    public ConsumerId() {
    }
    
    public ConsumerId(final String str) {
        if (str != null) {
            final String[] splits = str.split(":");
            if (splits != null && splits.length >= 3) {
                this.connectionId = splits[0];
                this.sessionId = Long.parseLong(splits[1]);
                this.value = Long.parseLong(splits[2]);
            }
        }
    }
    
    public ConsumerId(final SessionId sessionId, final long consumerId) {
        this.connectionId = sessionId.getConnectionId();
        this.sessionId = sessionId.getValue();
        this.value = consumerId;
    }
    
    public ConsumerId(final ConsumerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId = id.getSessionId();
        this.value = id.getValue();
    }
    
    public SessionId getParentId() {
        if (this.parentId == null) {
            this.parentId = new SessionId(this);
        }
        return this.parentId;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = (this.connectionId.hashCode() ^ (int)this.sessionId ^ (int)this.value);
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != ConsumerId.class) {
            return false;
        }
        final ConsumerId id = (ConsumerId)o;
        return this.sessionId == id.sessionId && this.value == id.value && this.connectionId.equals(id.connectionId);
    }
    
    @Override
    public byte getDataStructureType() {
        return 122;
    }
    
    @Override
    public String toString() {
        if (this.key == null) {
            this.key = this.connectionId + ":" + this.sessionId + ":" + this.value;
        }
        return this.key;
    }
    
    public String getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final String connectionId) {
        this.connectionId = connectionId;
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final long sessionId) {
        this.sessionId = sessionId;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public void setValue(final long consumerId) {
        this.value = consumerId;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
