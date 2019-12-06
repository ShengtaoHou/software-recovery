// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public class LocalTransactionId extends TransactionId implements Comparable<LocalTransactionId>
{
    public static final byte DATA_STRUCTURE_TYPE = 111;
    protected ConnectionId connectionId;
    protected long value;
    private transient String transactionKey;
    private transient int hashCode;
    
    public LocalTransactionId() {
    }
    
    public LocalTransactionId(final ConnectionId connectionId, final long transactionId) {
        this.connectionId = connectionId;
        this.value = transactionId;
    }
    
    @Override
    public byte getDataStructureType() {
        return 111;
    }
    
    @Override
    public boolean isXATransaction() {
        return false;
    }
    
    @Override
    public boolean isLocalTransaction() {
        return true;
    }
    
    @Override
    public String getTransactionKey() {
        if (this.transactionKey == null) {
            this.transactionKey = "TX:" + this.connectionId + ":" + this.value;
        }
        return this.transactionKey;
    }
    
    @Override
    public String toString() {
        return this.getTransactionKey();
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
        if (o == null || o.getClass() != LocalTransactionId.class) {
            return false;
        }
        final LocalTransactionId tx = (LocalTransactionId)o;
        return this.value == tx.value && this.connectionId.equals(tx.connectionId);
    }
    
    @Override
    public int compareTo(final LocalTransactionId o) {
        int result = this.connectionId.compareTo(o.connectionId);
        if (result == 0) {
            result = (int)(this.value - o.value);
        }
        return result;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public void setValue(final long transactionId) {
        this.value = transactionId;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
    }
}
