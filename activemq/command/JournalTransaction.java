// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import org.apache.activemq.util.IntrospectionSupport;

public class JournalTransaction implements DataStructure
{
    public static final byte DATA_STRUCTURE_TYPE = 54;
    public static final byte XA_PREPARE = 1;
    public static final byte XA_COMMIT = 2;
    public static final byte XA_ROLLBACK = 3;
    public static final byte LOCAL_COMMIT = 4;
    public static final byte LOCAL_ROLLBACK = 5;
    public byte type;
    public boolean wasPrepared;
    public TransactionId transactionId;
    
    public JournalTransaction(final byte type, final TransactionId transactionId, final boolean wasPrepared) {
        this.type = type;
        this.transactionId = transactionId;
        this.wasPrepared = wasPrepared;
    }
    
    public JournalTransaction() {
    }
    
    @Override
    public byte getDataStructureType() {
        return 54;
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public void setTransactionId(final TransactionId transactionId) {
        this.transactionId = transactionId;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void setType(final byte type) {
        this.type = type;
    }
    
    public boolean getWasPrepared() {
        return this.wasPrepared;
    }
    
    public void setWasPrepared(final boolean wasPrepared) {
        this.wasPrepared = wasPrepared;
    }
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
    
    @Override
    public String toString() {
        return IntrospectionSupport.toString(this, JournalTransaction.class);
    }
}
