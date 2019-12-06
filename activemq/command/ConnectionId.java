// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class ConnectionId implements DataStructure, Comparable<ConnectionId>
{
    public static final byte DATA_STRUCTURE_TYPE = 120;
    protected String value;
    
    public ConnectionId() {
    }
    
    public ConnectionId(final String connectionId) {
        this.value = connectionId;
    }
    
    public ConnectionId(final ConnectionId id) {
        this.value = id.getValue();
    }
    
    public ConnectionId(final SessionId id) {
        this.value = id.getConnectionId();
    }
    
    public ConnectionId(final ProducerId id) {
        this.value = id.getConnectionId();
    }
    
    public ConnectionId(final ConsumerId id) {
        this.value = id.getConnectionId();
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != ConnectionId.class) {
            return false;
        }
        final ConnectionId id = (ConnectionId)o;
        return this.value.equals(id.value);
    }
    
    @Override
    public byte getDataStructureType() {
        return 120;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String connectionId) {
        this.value = connectionId;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public int compareTo(final ConnectionId o) {
        return this.value.compareTo(o.value);
    }
}
