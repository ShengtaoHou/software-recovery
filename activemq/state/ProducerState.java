// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.state;

import org.apache.activemq.command.ProducerInfo;

public class ProducerState
{
    final ProducerInfo info;
    private TransactionState transactionState;
    
    public ProducerState(final ProducerInfo info) {
        this.info = info;
    }
    
    @Override
    public String toString() {
        return this.info.toString();
    }
    
    public ProducerInfo getInfo() {
        return this.info;
    }
    
    public void setTransactionState(final TransactionState transactionState) {
        this.transactionState = transactionState;
    }
    
    public TransactionState getTransactionState() {
        return this.transactionState;
    }
}
