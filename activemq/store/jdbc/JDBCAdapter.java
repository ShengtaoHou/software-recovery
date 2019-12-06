// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.apache.activemq.command.ProducerId;
import java.util.Set;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageId;
import java.io.IOException;
import java.sql.SQLException;

public interface JDBCAdapter
{
    void setStatements(final Statements p0);
    
    void doCreateTables(final TransactionContext p0) throws SQLException, IOException;
    
    void doDropTables(final TransactionContext p0) throws SQLException, IOException;
    
    void doAddMessage(final TransactionContext p0, final long p1, final MessageId p2, final ActiveMQDestination p3, final byte[] p4, final long p5, final byte p6, final XATransactionId p7) throws SQLException, IOException;
    
    void doAddMessageReference(final TransactionContext p0, final long p1, final MessageId p2, final ActiveMQDestination p3, final long p4, final String p5) throws SQLException, IOException;
    
    byte[] doGetMessage(final TransactionContext p0, final MessageId p1) throws SQLException, IOException;
    
    byte[] doGetMessageById(final TransactionContext p0, final long p1) throws SQLException, IOException;
    
    String doGetMessageReference(final TransactionContext p0, final long p1) throws SQLException, IOException;
    
    void doRemoveMessage(final TransactionContext p0, final long p1, final XATransactionId p2) throws SQLException, IOException;
    
    void doRecover(final TransactionContext p0, final ActiveMQDestination p1, final JDBCMessageRecoveryListener p2) throws Exception;
    
    void doSetLastAck(final TransactionContext p0, final ActiveMQDestination p1, final XATransactionId p2, final String p3, final String p4, final long p5, final long p6) throws SQLException, IOException;
    
    void doRecoverSubscription(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3, final JDBCMessageRecoveryListener p4) throws Exception;
    
    void doRecoverNextMessages(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3, final long p4, final long p5, final int p6, final JDBCMessageRecoveryListener p7) throws Exception;
    
    void doRecoverNextMessagesWithPriority(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3, final long p4, final long p5, final int p6, final JDBCMessageRecoveryListener p7) throws Exception;
    
    void doSetSubscriberEntry(final TransactionContext p0, final SubscriptionInfo p1, final boolean p2, final boolean p3) throws SQLException, IOException;
    
    SubscriptionInfo doGetSubscriberEntry(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3) throws SQLException, IOException;
    
    long[] getStoreSequenceId(final TransactionContext p0, final ActiveMQDestination p1, final MessageId p2) throws SQLException, IOException;
    
    void doRemoveAllMessages(final TransactionContext p0, final ActiveMQDestination p1) throws SQLException, IOException;
    
    void doDeleteSubscription(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3) throws SQLException, IOException;
    
    void doDeleteOldMessages(final TransactionContext p0) throws SQLException, IOException;
    
    long doGetLastMessageStoreSequenceId(final TransactionContext p0) throws SQLException, IOException;
    
    Set<ActiveMQDestination> doGetDestinations(final TransactionContext p0) throws SQLException, IOException;
    
    void setUseExternalMessageReferences(final boolean p0);
    
    SubscriptionInfo[] doGetAllSubscriptions(final TransactionContext p0, final ActiveMQDestination p1) throws SQLException, IOException;
    
    int doGetDurableSubscriberMessageCount(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3, final boolean p4) throws SQLException, IOException;
    
    int doGetMessageCount(final TransactionContext p0, final ActiveMQDestination p1) throws SQLException, IOException;
    
    void doRecoverNextMessages(final TransactionContext p0, final ActiveMQDestination p1, final long p2, final long p3, final int p4, final boolean p5, final JDBCMessageRecoveryListener p6) throws Exception;
    
    long doGetLastAckedDurableSubscriberMessageId(final TransactionContext p0, final ActiveMQDestination p1, final String p2, final String p3) throws SQLException, IOException;
    
    void doMessageIdScan(final TransactionContext p0, final int p1, final JDBCMessageIdScanListener p2) throws SQLException, IOException;
    
    long doGetLastProducerSequenceId(final TransactionContext p0, final ProducerId p1) throws SQLException, IOException;
    
    void doSetLastAckWithPriority(final TransactionContext p0, final ActiveMQDestination p1, final XATransactionId p2, final String p3, final String p4, final long p5, final long p6) throws SQLException, IOException;
    
    int getMaxRows();
    
    void setMaxRows(final int p0);
    
    void doRecordDestination(final TransactionContext p0, final ActiveMQDestination p1) throws SQLException, IOException;
    
    void doRecoverPreparedOps(final TransactionContext p0, final JdbcMemoryTransactionStore p1) throws SQLException, IOException;
    
    void doCommitAddOp(final TransactionContext p0, final long p1) throws SQLException, IOException;
    
    void doClearLastAck(final TransactionContext p0, final ActiveMQDestination p1, final byte p2, final String p3, final String p4) throws SQLException, IOException;
    
    void doUpdateMessage(final TransactionContext p0, final ActiveMQDestination p1, final MessageId p2, final byte[] p3) throws SQLException, IOException;
}
