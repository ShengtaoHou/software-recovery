// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker.jmx;

public interface RecoveredXATransactionViewMBean
{
    @MBeanInfo("The raw xid formatId.")
    int getFormatId();
    
    @MBeanInfo("The raw xid branchQualifier.")
    byte[] getBranchQualifier();
    
    @MBeanInfo("The raw xid globalTransactionId.")
    byte[] getGlobalTransactionId();
    
    @MBeanInfo("force heusistic commit of this transaction")
    void heuristicCommit() throws Exception;
    
    @MBeanInfo("force heusistic rollback of this transaction")
    void heuristicRollback() throws Exception;
}
