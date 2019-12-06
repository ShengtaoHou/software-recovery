// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class ProducerId implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 123;
    protected String connectionId;
    protected long sessionId;
    protected long value;
    protected transient int hashCode;
    protected transient String key;
    protected transient SessionId parentId;
    
    public ProducerId() {
    }
    
    public ProducerId(final SessionId sessionId, final long producerId) {
        this.connectionId = sessionId.getConnectionId();
        this.sessionId = sessionId.getValue();
        this.value = producerId;
    }
    
    public ProducerId(final ProducerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId = id.getSessionId();
        this.value = id.getValue();
    }
    
    public ProducerId(String producerKey) {
        final int p = producerKey.lastIndexOf(":");
        if (p >= 0) {
            this.value = Long.parseLong(producerKey.substring(p + 1));
            producerKey = producerKey.substring(0, p);
        }
        this.setProducerSessionKey(producerKey);
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
        if (o == null || o.getClass() != ProducerId.class) {
            return false;
        }
        final ProducerId id = (ProducerId)o;
        return this.sessionId == id.sessionId && this.value == id.value && this.connectionId.equals(id.connectionId);
    }
    
    private void setProducerSessionKey(String sessionKey) {
        final int p = sessionKey.lastIndexOf(":");
        if (p >= 0) {
            this.sessionId = Long.parseLong(sessionKey.substring(p + 1));
            sessionKey = sessionKey.substring(0, p);
        }
        this.connectionId = sessionKey;
    }
    
    @Override
    public String toString() {
        if (this.key == null) {
            this.key = this.connectionId + ":" + this.sessionId + ":" + this.value;
        }
        return this.key;
    }
    
    @Override
    public byte getDataStructureType() {
        return 123;
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
    
    public void setValue(final long producerId) {
        this.value = producerId;
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final long sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
