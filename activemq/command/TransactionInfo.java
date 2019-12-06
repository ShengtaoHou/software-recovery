// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

import java.io.IOException;
import org.apache.activemq.state.CommandVisitor;

public class TransactionInfo extends BaseCommand
{
    public static final byte DATA_STRUCTURE_TYPE = 7;
    public static final byte BEGIN = 0;
    public static final byte PREPARE = 1;
    public static final byte COMMIT_ONE_PHASE = 2;
    public static final byte COMMIT_TWO_PHASE = 3;
    public static final byte ROLLBACK = 4;
    public static final byte RECOVER = 5;
    public static final byte FORGET = 6;
    public static final byte END = 7;
    protected byte type;
    protected ConnectionId connectionId;
    protected TransactionId transactionId;
    
    public TransactionInfo() {
    }
    
    public TransactionInfo(final ConnectionId connectionId, final TransactionId transactionId, final byte type) {
        this.connectionId = connectionId;
        this.transactionId = transactionId;
        this.type = type;
    }
    
    @Override
    public byte getDataStructureType() {
        return 7;
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    public void setConnectionId(final ConnectionId connectionId) {
        this.connectionId = connectionId;
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
    
    @Override
    public Response visit(final CommandVisitor visitor) throws Exception {
        switch (this.type) {
            case 0: {
                return visitor.processBeginTransaction(this);
            }
            case 7: {
                return visitor.processEndTransaction(this);
            }
            case 1: {
                return visitor.processPrepareTransaction(this);
            }
            case 2: {
                return visitor.processCommitTransactionOnePhase(this);
            }
            case 3: {
                return visitor.processCommitTransactionTwoPhase(this);
            }
            case 4: {
                return visitor.processRollbackTransaction(this);
            }
            case 5: {
                return visitor.processRecoverTransactions(this);
            }
            case 6: {
                return visitor.processForgetTransaction(this);
            }
            default: {
                throw new IOException("Transaction info type unknown: " + this.type);
            }
        }
    }
}
