// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.command;

public abstract class TransactionId implements DataStructure
{
    public abstract boolean isXATransaction();
    
    public abstract boolean isLocalTransaction();
    
    public abstract String getTransactionKey();
    
    @Override
    public boolean isMarshallAware() {
        return false;
    }
}
