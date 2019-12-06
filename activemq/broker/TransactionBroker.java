// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.broker;

import org.slf4j.LoggerFactory;
import javax.transaction.xa.XAException;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.broker.region.MessageReference;
import org.apache.activemq.transaction.LocalTransaction;
import org.apache.activemq.command.LocalTransactionId;
import javax.jms.JMSException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.broker.jmx.ManagedRegionBroker;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.WrappedException;
import org.apache.activemq.command.BaseCommand;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.state.ProducerState;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.transaction.Transaction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.LinkedHashMap;
import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.transaction.XATransaction;
import org.apache.activemq.command.TransactionId;
import java.util.Map;
import org.apache.activemq.store.TransactionStore;
import org.slf4j.Logger;

public class TransactionBroker extends BrokerFilter
{
    private static final Logger LOG;
    private TransactionStore transactionStore;
    private Map<TransactionId, XATransaction> xaTransactions;
    private ActiveMQMessageAudit audit;
    
    public TransactionBroker(final Broker next, final TransactionStore transactionStore) {
        super(next);
        this.xaTransactions = new LinkedHashMap<TransactionId, XATransaction>();
        this.transactionStore = transactionStore;
    }
    
    @Override
    public void start() throws Exception {
        this.transactionStore.start();
        try {
            final ConnectionContext context = new ConnectionContext();
            context.setBroker(this);
            context.setInRecoveryMode(true);
            context.setTransactions(new ConcurrentHashMap<TransactionId, Transaction>());
            context.setProducerFlowControl(false);
            final ProducerBrokerExchange producerExchange = new ProducerBrokerExchange();
            producerExchange.setMutable(true);
            producerExchange.setConnectionContext(context);
            producerExchange.setProducerState(new ProducerState(new ProducerInfo()));
            final ConsumerBrokerExchange consumerExchange = new ConsumerBrokerExchange();
            consumerExchange.setConnectionContext(context);
            this.transactionStore.recover(new TransactionRecoveryListener() {
                @Override
                public void recover(final XATransactionId xid, final Message[] addedMessages, final MessageAck[] aks) {
                    try {
                        TransactionBroker.this.beginTransaction(context, xid);
                        final XATransaction transaction = (XATransaction)TransactionBroker.this.getTransaction(context, xid, false);
                        for (int i = 0; i < addedMessages.length; ++i) {
                            TransactionBroker.this.forceDestinationWakeupOnCompletion(context, transaction, addedMessages[i].getDestination(), addedMessages[i]);
                        }
                        for (int i = 0; i < aks.length; ++i) {
                            TransactionBroker.this.forceDestinationWakeupOnCompletion(context, transaction, aks[i].getDestination(), aks[i]);
                        }
                        transaction.setState((byte)2);
                        TransactionBroker.this.registerMBean(transaction);
                        TransactionBroker.LOG.debug("recovered prepared transaction: {}", transaction.getTransactionId());
                    }
                    catch (Throwable e) {
                        throw new WrappedException(e);
                    }
                }
            });
        }
        catch (WrappedException e) {
            final Throwable cause = e.getCause();
            throw IOExceptionSupport.create("Recovery Failed: " + cause.getMessage(), cause);
        }
        this.next.start();
    }
    
    private void registerMBean(final XATransaction transaction) {
        if (this.getBrokerService().getRegionBroker() instanceof ManagedRegionBroker) {
            final ManagedRegionBroker managedRegionBroker = (ManagedRegionBroker)this.getBrokerService().getRegionBroker();
            managedRegionBroker.registerRecoveredTransactionMBean(transaction);
        }
    }
    
    private void forceDestinationWakeupOnCompletion(final ConnectionContext context, final Transaction transaction, final ActiveMQDestination amqDestination, final BaseCommand ack) throws Exception {
        final Destination destination = this.addDestination(context, amqDestination, false);
        this.registerSync(destination, transaction, ack);
    }
    
    private void registerSync(final Destination destination, final Transaction transaction, final BaseCommand command) {
        final Synchronization sync = new PreparedDestinationCompletion(destination, command.isMessage());
        final Synchronization existing = transaction.findMatching(sync);
        if (existing != null) {
            ((PreparedDestinationCompletion)existing).incrementOpCount();
        }
        else {
            transaction.addSynchronization(sync);
        }
    }
    
    @Override
    public void stop() throws Exception {
        this.transactionStore.stop();
        this.next.stop();
    }
    
    @Override
    public TransactionId[] getPreparedTransactions(final ConnectionContext context) throws Exception {
        final List<TransactionId> txs = new ArrayList<TransactionId>();
        synchronized (this.xaTransactions) {
            for (final Transaction tx : this.xaTransactions.values()) {
                if (tx.isPrepared()) {
                    TransactionBroker.LOG.debug("prepared transaction: {}", tx.getTransactionId());
                    txs.add(tx.getTransactionId());
                }
            }
        }
        final XATransactionId[] rc = new XATransactionId[txs.size()];
        txs.toArray(rc);
        TransactionBroker.LOG.debug("prepared transaction list size: {}", (Object)rc.length);
        return rc;
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        if (xid.isXATransaction()) {
            XATransaction transaction = null;
            synchronized (this.xaTransactions) {
                transaction = this.xaTransactions.get(xid);
                if (transaction != null) {
                    return;
                }
                transaction = new XATransaction(this.transactionStore, (XATransactionId)xid, this, context.getConnectionId());
                this.xaTransactions.put(xid, transaction);
            }
        }
        else {
            final Map<TransactionId, Transaction> transactionMap = context.getTransactions();
            Transaction transaction2 = transactionMap.get(xid);
            if (transaction2 != null) {
                throw new JMSException("Transaction '" + xid + "' has already been started.");
            }
            transaction2 = new LocalTransaction(this.transactionStore, (LocalTransactionId)xid, context);
            transactionMap.put(xid, transaction2);
        }
    }
    
    @Override
    public int prepareTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        final Transaction transaction = this.getTransaction(context, xid, false);
        return transaction.prepare();
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context, final TransactionId xid, final boolean onePhase) throws Exception {
        final Transaction transaction = this.getTransaction(context, xid, true);
        transaction.commit(onePhase);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        final Transaction transaction = this.getTransaction(context, xid, true);
        transaction.rollback();
    }
    
    @Override
    public void forgetTransaction(final ConnectionContext context, final TransactionId xid) throws Exception {
        final Transaction transaction = this.getTransaction(context, xid, true);
        transaction.rollback();
    }
    
    @Override
    public void acknowledge(final ConsumerBrokerExchange consumerExchange, final MessageAck ack) throws Exception {
        final ConnectionContext context = consumerExchange.getConnectionContext();
        final Transaction originalTx = context.getTransaction();
        Transaction transaction = null;
        if (ack.isInTransaction()) {
            transaction = this.getTransaction(context, ack.getTransactionId(), false);
        }
        context.setTransaction(transaction);
        try {
            this.next.acknowledge(consumerExchange, ack);
        }
        finally {
            context.setTransaction(originalTx);
        }
    }
    
    @Override
    public void send(final ProducerBrokerExchange producerExchange, final Message message) throws Exception {
        final ConnectionContext context = producerExchange.getConnectionContext();
        final Transaction originalTx = context.getTransaction();
        Transaction transaction = null;
        Synchronization sync = null;
        if (message.getTransactionId() != null) {
            transaction = this.getTransaction(context, message.getTransactionId(), false);
            if (transaction != null) {
                sync = new Synchronization() {
                    @Override
                    public void afterRollback() {
                        if (TransactionBroker.this.audit != null) {
                            TransactionBroker.this.audit.rollback(message);
                        }
                    }
                };
                transaction.addSynchronization(sync);
            }
        }
        if (this.audit == null || !this.audit.isDuplicate(message)) {
            context.setTransaction(transaction);
            try {
                this.next.send(producerExchange, message);
            }
            finally {
                context.setTransaction(originalTx);
            }
        }
        else {
            if (sync != null && transaction != null) {
                transaction.removeSynchronization(sync);
            }
            TransactionBroker.LOG.debug("IGNORING duplicate message {}", message);
        }
    }
    
    @Override
    public void removeConnection(final ConnectionContext context, final ConnectionInfo info, final Throwable error) throws Exception {
        final Iterator<Transaction> iter = context.getTransactions().values().iterator();
        while (iter.hasNext()) {
            try {
                final Transaction transaction = iter.next();
                transaction.rollback();
            }
            catch (Exception e) {
                TransactionBroker.LOG.warn("ERROR Rolling back disconnected client's transactions: ", e);
            }
            iter.remove();
        }
        synchronized (this.xaTransactions) {
            final ArrayList<XATransaction> txs = new ArrayList<XATransaction>();
            for (final XATransaction tx : this.xaTransactions.values()) {
                if (tx.getConnectionId() != null && tx.getConnectionId().equals(info.getConnectionId()) && !tx.isPrepared()) {
                    txs.add(tx);
                }
            }
            for (final XATransaction tx : txs) {
                try {
                    tx.rollback();
                }
                catch (Exception e2) {
                    TransactionBroker.LOG.warn("ERROR Rolling back disconnected client's xa transactions: ", e2);
                }
            }
        }
        this.next.removeConnection(context, info, error);
    }
    
    public Transaction getTransaction(final ConnectionContext context, final TransactionId xid, final boolean mightBePrepared) throws JMSException, XAException {
        Map transactionMap = null;
        synchronized (this.xaTransactions) {
            transactionMap = (Map)(xid.isXATransaction() ? this.xaTransactions : context.getTransactions());
        }
        final Transaction transaction = transactionMap.get(xid);
        if (transaction != null) {
            return transaction;
        }
        if (xid.isXATransaction()) {
            final XAException e = new XAException("Transaction '" + xid + "' has not been started.");
            e.errorCode = -4;
            throw e;
        }
        throw new JMSException("Transaction '" + xid + "' has not been started.");
    }
    
    public void removeTransaction(final XATransactionId xid) {
        synchronized (this.xaTransactions) {
            this.xaTransactions.remove(xid);
        }
    }
    
    @Override
    public synchronized void brokerServiceStarted() {
        super.brokerServiceStarted();
        if (this.getBrokerService().isSupportFailOver() && this.audit == null) {
            this.audit = new ActiveMQMessageAudit();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransactionBroker.class);
    }
    
    static class PreparedDestinationCompletion extends Synchronization
    {
        final Destination destination;
        final boolean messageSend;
        int opCount;
        
        public PreparedDestinationCompletion(final Destination destination, final boolean messageSend) {
            this.opCount = 1;
            this.destination = destination;
            this.messageSend = messageSend;
        }
        
        public void incrementOpCount() {
            ++this.opCount;
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.destination) + System.identityHashCode(this.messageSend);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof PreparedDestinationCompletion && this.destination.equals(((PreparedDestinationCompletion)other).destination) && this.messageSend == ((PreparedDestinationCompletion)other).messageSend;
        }
        
        @Override
        public void afterRollback() throws Exception {
            if (!this.messageSend) {
                this.destination.clearPendingMessages();
                TransactionBroker.LOG.debug("cleared pending from afterRollback: {}", this.destination);
            }
        }
        
        @Override
        public void afterCommit() throws Exception {
            if (this.messageSend) {
                this.destination.clearPendingMessages();
                this.destination.getDestinationStatistics().getEnqueues().add(this.opCount);
                this.destination.getDestinationStatistics().getMessages().add(this.opCount);
                TransactionBroker.LOG.debug("cleared pending from afterCommit: {}", this.destination);
            }
            else {
                this.destination.getDestinationStatistics().getDequeues().add(this.opCount);
                this.destination.getDestinationStatistics().getMessages().subtract(this.opCount);
            }
        }
    }
}
