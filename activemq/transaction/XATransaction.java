// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transaction;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.TransactionId;
import java.io.IOException;
import javax.transaction.xa.XAException;
import org.apache.activemq.command.ConnectionId;
import org.apache.activemq.broker.TransactionBroker;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.TransactionStore;
import org.slf4j.Logger;

public class XATransaction extends Transaction
{
    private static final Logger LOG;
    private final TransactionStore transactionStore;
    private final XATransactionId xid;
    private final TransactionBroker broker;
    private final ConnectionId connectionId;
    
    public XATransaction(final TransactionStore transactionStore, final XATransactionId xid, final TransactionBroker broker, final ConnectionId connectionId) {
        this.transactionStore = transactionStore;
        this.xid = xid;
        this.broker = broker;
        this.connectionId = connectionId;
        if (XATransaction.LOG.isDebugEnabled()) {
            XATransaction.LOG.debug("XA Transaction new/begin : " + xid);
        }
    }
    
    @Override
    public void commit(final boolean onePhase) throws XAException, IOException {
        if (XATransaction.LOG.isDebugEnabled()) {
            XATransaction.LOG.debug("XA Transaction commit onePhase:" + onePhase + ", xid: " + this.xid);
        }
        switch (this.getState()) {
            case 0: {
                this.checkForPreparedState(onePhase);
                this.setStateFinished();
                break;
            }
            case 1: {
                this.checkForPreparedState(onePhase);
                this.doPrePrepare();
                this.setStateFinished();
                this.storeCommit(this.getTransactionId(), false, this.preCommitTask, this.postCommitTask);
                break;
            }
            case 2: {
                this.setStateFinished();
                this.storeCommit(this.getTransactionId(), true, this.preCommitTask, this.postCommitTask);
                break;
            }
            default: {
                this.illegalStateTransition("commit");
                break;
            }
        }
    }
    
    private void storeCommit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) throws XAException, IOException {
        try {
            this.transactionStore.commit(this.getTransactionId(), wasPrepared, this.preCommitTask, this.postCommitTask);
            this.waitPostCommitDone(this.postCommitTask);
        }
        catch (XAException xae) {
            throw xae;
        }
        catch (Throwable t) {
            XATransaction.LOG.warn("Store COMMIT FAILED: ", t);
            this.rollback();
            final XAException xae2 = new XAException("STORE COMMIT FAILED: Transaction rolled back");
            xae2.errorCode = 104;
            xae2.initCause(t);
            throw xae2;
        }
    }
    
    private void illegalStateTransition(final String callName) throws XAException {
        final XAException xae = new XAException("Cannot call " + callName + " now.");
        xae.errorCode = -6;
        throw xae;
    }
    
    private void checkForPreparedState(final boolean onePhase) throws XAException {
        if (!onePhase) {
            final XAException xae = new XAException("Cannot do 2 phase commit if the transaction has not been prepared");
            xae.errorCode = -6;
            throw xae;
        }
    }
    
    private void doPrePrepare() throws XAException, IOException {
        try {
            this.prePrepare();
        }
        catch (XAException e) {
            throw e;
        }
        catch (Throwable e2) {
            XATransaction.LOG.warn("PRE-PREPARE FAILED: ", e2);
            this.rollback();
            final XAException xae = new XAException("PRE-PREPARE FAILED: Transaction rolled back");
            xae.errorCode = 104;
            xae.initCause(e2);
            throw xae;
        }
    }
    
    @Override
    public void rollback() throws XAException, IOException {
        if (XATransaction.LOG.isDebugEnabled()) {
            XATransaction.LOG.debug("XA Transaction rollback: " + this.xid);
        }
        switch (this.getState()) {
            case 0: {
                this.setStateFinished();
                break;
            }
            case 1: {
                this.setStateFinished();
                this.transactionStore.rollback(this.getTransactionId());
                this.doPostRollback();
                break;
            }
            case 2: {
                this.setStateFinished();
                this.transactionStore.rollback(this.getTransactionId());
                this.doPostRollback();
                break;
            }
            case 3: {
                this.transactionStore.rollback(this.getTransactionId());
                this.doPostRollback();
                break;
            }
            default: {
                throw new XAException("Invalid state");
            }
        }
    }
    
    private void doPostRollback() throws XAException {
        try {
            this.fireAfterRollback();
        }
        catch (Throwable e) {
            XATransaction.LOG.warn("POST ROLLBACK FAILED: ", e);
            final XAException xae = new XAException("POST ROLLBACK FAILED");
            xae.errorCode = -3;
            xae.initCause(e);
            throw xae;
        }
    }
    
    @Override
    public int prepare() throws XAException, IOException {
        if (XATransaction.LOG.isDebugEnabled()) {
            XATransaction.LOG.debug("XA Transaction prepare: " + this.xid);
        }
        switch (this.getState()) {
            case 0: {
                this.setStateFinished();
                return 3;
            }
            case 1: {
                this.doPrePrepare();
                this.setState((byte)2);
                this.transactionStore.prepare(this.getTransactionId());
                return 0;
            }
            default: {
                this.illegalStateTransition("prepare");
                return 3;
            }
        }
    }
    
    private void setStateFinished() {
        this.setState((byte)3);
        this.broker.removeTransaction(this.xid);
    }
    
    public ConnectionId getConnectionId() {
        return this.connectionId;
    }
    
    @Override
    public TransactionId getTransactionId() {
        return this.xid;
    }
    
    @Override
    public Logger getLog() {
        return XATransaction.LOG;
    }
    
    public XATransactionId getXid() {
        return this.xid;
    }
    
    static {
        LOG = LoggerFactory.getLogger(XATransaction.class);
    }
}
