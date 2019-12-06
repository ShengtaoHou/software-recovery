// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transaction;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.TransactionId;
import java.io.IOException;
import javax.transaction.xa.XAException;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.activemq.store.TransactionStore;
import org.slf4j.Logger;

public class LocalTransaction extends Transaction
{
    private static final Logger LOG;
    private final TransactionStore transactionStore;
    private final LocalTransactionId xid;
    private final ConnectionContext context;
    
    public LocalTransaction(final TransactionStore transactionStore, final LocalTransactionId xid, final ConnectionContext context) {
        this.transactionStore = transactionStore;
        this.xid = xid;
        this.context = context;
    }
    
    @Override
    public void commit(final boolean onePhase) throws XAException, IOException {
        if (LocalTransaction.LOG.isDebugEnabled()) {
            LocalTransaction.LOG.debug("commit: " + this.xid + " syncCount: " + this.size());
        }
        try {
            this.prePrepare();
        }
        catch (XAException e) {
            throw e;
        }
        catch (Throwable e2) {
            LocalTransaction.LOG.warn("COMMIT FAILED: ", e2);
            this.rollback();
            final XAException xae = new XAException("COMMIT FAILED: Transaction rolled back");
            xae.errorCode = 104;
            xae.initCause(e2);
            throw xae;
        }
        this.setState((byte)3);
        this.context.getTransactions().remove(this.xid);
        try {
            this.transactionStore.commit(this.getTransactionId(), false, this.preCommitTask, this.postCommitTask);
            this.waitPostCommitDone(this.postCommitTask);
        }
        catch (Throwable t) {
            LocalTransaction.LOG.warn("Store COMMIT FAILED: ", t);
            this.rollback();
            final XAException xae = new XAException("STORE COMMIT FAILED: Transaction rolled back");
            xae.errorCode = 104;
            xae.initCause(t);
            throw xae;
        }
    }
    
    @Override
    public void rollback() throws XAException, IOException {
        if (LocalTransaction.LOG.isDebugEnabled()) {
            LocalTransaction.LOG.debug("rollback: " + this.xid + " syncCount: " + this.size());
        }
        this.setState((byte)3);
        this.context.getTransactions().remove(this.xid);
        synchronized (this.transactionStore) {
            this.transactionStore.rollback(this.getTransactionId());
            try {
                this.fireAfterRollback();
            }
            catch (Throwable e) {
                LocalTransaction.LOG.warn("POST ROLLBACK FAILED: ", e);
                final XAException xae = new XAException("POST ROLLBACK FAILED");
                xae.errorCode = -3;
                xae.initCause(e);
                throw xae;
            }
        }
    }
    
    @Override
    public int prepare() throws XAException {
        final XAException xae = new XAException("Prepare not implemented on Local Transactions");
        xae.errorCode = -3;
        throw xae;
    }
    
    @Override
    public TransactionId getTransactionId() {
        return this.xid;
    }
    
    @Override
    public Logger getLog() {
        return LocalTransaction.LOG;
    }
    
    static {
        LOG = LoggerFactory.getLogger(LocalTransaction.class);
    }
}
