// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.transaction.XATransaction;

public class RecoveredXATransactionView implements RecoveredXATransactionViewMBean
{
    private final XATransaction transaction;
    
    public RecoveredXATransactionView(final ManagedRegionBroker managedRegionBroker, final XATransaction transaction) {
        (this.transaction = transaction).addSynchronization(new Synchronization() {
            @Override
            public void afterCommit() throws Exception {
                managedRegionBroker.unregister(transaction);
            }
            
            @Override
            public void afterRollback() throws Exception {
                managedRegionBroker.unregister(transaction);
            }
        });
    }
    
    @Override
    public int getFormatId() {
        return this.transaction.getXid().getFormatId();
    }
    
    @Override
    public byte[] getBranchQualifier() {
        return this.transaction.getXid().getBranchQualifier();
    }
    
    @Override
    public byte[] getGlobalTransactionId() {
        return this.transaction.getXid().getGlobalTransactionId();
    }
    
    @Override
    public void heuristicCommit() throws Exception {
        this.transaction.commit(false);
    }
    
    @Override
    public void heuristicRollback() throws Exception {
        this.transaction.rollback();
    }
}
