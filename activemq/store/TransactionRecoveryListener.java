// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store;

import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.XATransactionId;

public interface TransactionRecoveryListener
{
    void recover(final XATransactionId p0, final Message[] p1, final MessageAck[] p2);
}
