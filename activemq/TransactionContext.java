// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq;

import org.slf4j.LoggerFactory;
import java.io.InterruptedIOException;
import org.apache.activemq.command.Response;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.DataArrayResponse;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.apache.activemq.command.IntegerResponse;
import org.apache.activemq.command.XATransactionId;
import java.util.Arrays;
import org.apache.activemq.command.ConnectionId;
import javax.transaction.xa.XAException;
import javax.jms.TransactionRolledBackException;
import org.apache.activemq.command.Command;
import org.apache.activemq.command.TransactionInfo;
import org.apache.activemq.command.LocalTransactionId;
import javax.jms.TransactionInProgressException;
import javax.jms.JMSException;
import org.apache.activemq.util.JMSExceptionSupport;
import java.util.ArrayList;
import java.util.Iterator;
import javax.transaction.xa.Xid;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.util.LongSequenceGenerator;
import java.util.List;
import org.apache.activemq.command.TransactionId;
import java.util.HashMap;
import org.slf4j.Logger;
import javax.transaction.xa.XAResource;

public class TransactionContext implements XAResource
{
    private static final Logger LOG;
    private static final HashMap<TransactionId, List<TransactionContext>> ENDED_XA_TRANSACTION_CONTEXTS;
    private ActiveMQConnection connection;
    private final LongSequenceGenerator localTransactionIdGenerator;
    private List<Synchronization> synchronizations;
    private Xid associatedXid;
    private TransactionId transactionId;
    private LocalTransactionEventListener localTransactionEventListener;
    private int beforeEndIndex;
    
    public TransactionContext() {
        this.localTransactionIdGenerator = null;
    }
    
    public TransactionContext(final ActiveMQConnection connection) {
        this.connection = connection;
        this.localTransactionIdGenerator = connection.getLocalTransactionIdGenerator();
    }
    
    public boolean isInXATransaction() {
        if (this.transactionId != null && this.transactionId.isXATransaction()) {
            return true;
        }
        if (!TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.isEmpty()) {
            synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                for (final List<TransactionContext> transactions : TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.values()) {
                    if (transactions.contains(this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isInLocalTransaction() {
        return this.transactionId != null && this.transactionId.isLocalTransaction();
    }
    
    public boolean isInTransaction() {
        return this.transactionId != null;
    }
    
    public LocalTransactionEventListener getLocalTransactionEventListener() {
        return this.localTransactionEventListener;
    }
    
    public void setLocalTransactionEventListener(final LocalTransactionEventListener localTransactionEventListener) {
        this.localTransactionEventListener = localTransactionEventListener;
    }
    
    public void addSynchronization(final Synchronization s) {
        if (this.synchronizations == null) {
            this.synchronizations = new ArrayList<Synchronization>(10);
        }
        this.synchronizations.add(s);
    }
    
    private void afterRollback() throws JMSException {
        if (this.synchronizations == null) {
            return;
        }
        Throwable firstException = null;
        for (int size = this.synchronizations.size(), i = 0; i < size; ++i) {
            try {
                this.synchronizations.get(i).afterRollback();
            }
            catch (Throwable t) {
                TransactionContext.LOG.debug("Exception from afterRollback on " + this.synchronizations.get(i), t);
                if (firstException == null) {
                    firstException = t;
                }
            }
        }
        this.synchronizations = null;
        if (firstException != null) {
            throw JMSExceptionSupport.create(firstException);
        }
    }
    
    private void afterCommit() throws JMSException {
        if (this.synchronizations == null) {
            return;
        }
        Throwable firstException = null;
        for (int size = this.synchronizations.size(), i = 0; i < size; ++i) {
            try {
                this.synchronizations.get(i).afterCommit();
            }
            catch (Throwable t) {
                TransactionContext.LOG.debug("Exception from afterCommit on " + this.synchronizations.get(i), t);
                if (firstException == null) {
                    firstException = t;
                }
            }
        }
        this.synchronizations = null;
        if (firstException != null) {
            throw JMSExceptionSupport.create(firstException);
        }
    }
    
    private void beforeEnd() throws JMSException {
        if (this.synchronizations == null) {
            return;
        }
        final int size = this.synchronizations.size();
        try {
            while (this.beforeEndIndex < size) {
                this.synchronizations.get(this.beforeEndIndex++).beforeEnd();
            }
        }
        catch (JMSException e) {
            throw e;
        }
        catch (Throwable e2) {
            throw JMSExceptionSupport.create(e2);
        }
    }
    
    public TransactionId getTransactionId() {
        return this.transactionId;
    }
    
    public void begin() throws JMSException {
        if (this.isInXATransaction()) {
            throw new TransactionInProgressException("Cannot start local transaction.  XA transaction is already in progress.");
        }
        if (this.transactionId == null) {
            this.synchronizations = null;
            this.beforeEndIndex = 0;
            this.transactionId = new LocalTransactionId(this.getConnectionId(), this.localTransactionIdGenerator.getNextSequenceId());
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), this.transactionId, (byte)0);
            this.connection.ensureConnectionInfoSent();
            this.connection.asyncSendPacket(info);
            if (this.localTransactionEventListener != null) {
                this.localTransactionEventListener.beginEvent();
            }
            if (TransactionContext.LOG.isDebugEnabled()) {
                TransactionContext.LOG.debug("Begin:" + this.transactionId);
            }
        }
    }
    
    public void rollback() throws JMSException {
        if (this.isInXATransaction()) {
            throw new TransactionInProgressException("Cannot rollback() if an XA transaction is already in progress ");
        }
        try {
            this.beforeEnd();
        }
        catch (TransactionRolledBackException canOcurrOnFailover) {
            TransactionContext.LOG.warn("rollback processing error", canOcurrOnFailover);
        }
        if (this.transactionId != null) {
            if (TransactionContext.LOG.isDebugEnabled()) {
                TransactionContext.LOG.debug("Rollback: " + this.transactionId + " syncCount: " + ((this.synchronizations != null) ? this.synchronizations.size() : 0));
            }
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), this.transactionId, (byte)4);
            this.transactionId = null;
            this.connection.syncSendPacket(info);
            if (this.localTransactionEventListener != null) {
                this.localTransactionEventListener.rollbackEvent();
            }
        }
        this.afterRollback();
    }
    
    public void commit() throws JMSException {
        if (this.isInXATransaction()) {
            throw new TransactionInProgressException("Cannot commit() if an XA transaction is already in progress ");
        }
        try {
            this.beforeEnd();
        }
        catch (JMSException e) {
            this.rollback();
            throw e;
        }
        if (this.transactionId != null) {
            if (TransactionContext.LOG.isDebugEnabled()) {
                TransactionContext.LOG.debug("Commit: " + this.transactionId + " syncCount: " + ((this.synchronizations != null) ? this.synchronizations.size() : 0));
            }
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), this.transactionId, (byte)2);
            this.transactionId = null;
            try {
                this.syncSendPacketWithInterruptionHandling(info);
                if (this.localTransactionEventListener != null) {
                    this.localTransactionEventListener.commitEvent();
                }
                this.afterCommit();
            }
            catch (JMSException cause) {
                TransactionContext.LOG.info("commit failed for transaction " + info.getTransactionId(), cause);
                if (this.localTransactionEventListener != null) {
                    this.localTransactionEventListener.rollbackEvent();
                }
                this.afterRollback();
                throw cause;
            }
        }
    }
    
    @Override
    public void start(final Xid xid, final int flags) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Start: " + xid + ", flags:" + flags);
        }
        if (this.isInLocalTransaction()) {
            throw new XAException(-6);
        }
        if (this.associatedXid != null) {
            throw new XAException(-6);
        }
        this.synchronizations = null;
        this.beforeEndIndex = 0;
        this.setXid(xid);
    }
    
    private ConnectionId getConnectionId() {
        return this.connection.getConnectionInfo().getConnectionId();
    }
    
    @Override
    public void end(final Xid xid, final int flags) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("End: " + xid + ", flags:" + flags);
        }
        if (this.isInLocalTransaction()) {
            throw new XAException(-6);
        }
        if ((flags & 0x22000000) != 0x0) {
            if (!this.equals(this.associatedXid, xid)) {
                throw new XAException(-6);
            }
            try {
                this.beforeEnd();
            }
            catch (JMSException e) {
                throw this.toXAException(e);
            }
            finally {
                this.setXid(null);
            }
        }
        else {
            if ((flags & 0x4000000) != 0x4000000) {
                throw new XAException(-5);
            }
            if (this.equals(this.associatedXid, xid)) {
                try {
                    this.beforeEnd();
                }
                catch (JMSException e) {
                    throw this.toXAException(e);
                }
                finally {
                    this.setXid(null);
                }
            }
        }
    }
    
    private boolean equals(final Xid xid1, final Xid xid2) {
        return xid1 == xid2 || (!(xid1 == null ^ xid2 == null) && xid1.getFormatId() == xid2.getFormatId() && Arrays.equals(xid1.getBranchQualifier(), xid2.getBranchQualifier()) && Arrays.equals(xid1.getGlobalTransactionId(), xid2.getGlobalTransactionId()));
    }
    
    @Override
    public int prepare(final Xid xid) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Prepare: " + xid);
        }
        if (xid == null || this.equals(this.associatedXid, xid)) {
            throw new XAException(-6);
        }
        final XATransactionId x = new XATransactionId(xid);
        try {
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), x, (byte)1);
            final IntegerResponse response = (IntegerResponse)this.syncSendPacketWithInterruptionHandling(info);
            if (3 == response.getResult()) {
                synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                    final List<TransactionContext> l = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
                    if (l != null && !l.isEmpty()) {
                        if (TransactionContext.LOG.isDebugEnabled()) {
                            TransactionContext.LOG.debug("firing afterCommit callbacks on XA_RDONLY from prepare: " + xid);
                        }
                        for (final TransactionContext ctx : l) {
                            ctx.afterCommit();
                        }
                    }
                }
            }
            return response.getResult();
        }
        catch (JMSException e) {
            TransactionContext.LOG.warn("prepare of: " + x + " failed with: " + e, e);
            synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                final List<TransactionContext> i = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
                if (i != null && !i.isEmpty()) {
                    for (final TransactionContext ctx2 : i) {
                        try {
                            ctx2.afterRollback();
                        }
                        catch (Throwable ignored) {
                            if (!TransactionContext.LOG.isDebugEnabled()) {
                                continue;
                            }
                            TransactionContext.LOG.debug("failed to firing afterRollback callbacks on prepare failure, txid: " + x + ", context: " + ctx2, ignored);
                        }
                    }
                }
            }
            throw this.toXAException(e);
        }
    }
    
    @Override
    public void rollback(final Xid xid) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Rollback: " + xid);
        }
        if (xid == null) {
            throw new XAException(-6);
        }
        XATransactionId x;
        if (this.equals(this.associatedXid, xid)) {
            x = (XATransactionId)this.transactionId;
        }
        else {
            x = new XATransactionId(xid);
        }
        try {
            this.connection.checkClosedOrFailed();
            this.connection.ensureConnectionInfoSent();
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), x, (byte)4);
            this.syncSendPacketWithInterruptionHandling(info);
            synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                final List<TransactionContext> l = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
                if (l != null && !l.isEmpty()) {
                    for (final TransactionContext ctx : l) {
                        ctx.afterRollback();
                    }
                }
            }
        }
        catch (JMSException e) {
            throw this.toXAException(e);
        }
    }
    
    @Override
    public void commit(final Xid xid, final boolean onePhase) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Commit: " + xid + ", onePhase=" + onePhase);
        }
        if (xid == null || this.equals(this.associatedXid, xid)) {
            throw new XAException(-6);
        }
        final XATransactionId x = new XATransactionId(xid);
        try {
            this.connection.checkClosedOrFailed();
            this.connection.ensureConnectionInfoSent();
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), x, (byte)(onePhase ? 2 : 3));
            this.syncSendPacketWithInterruptionHandling(info);
            synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                final List<TransactionContext> l = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
                if (l != null && !l.isEmpty()) {
                    for (final TransactionContext ctx : l) {
                        try {
                            ctx.afterCommit();
                        }
                        catch (Exception ignored) {
                            TransactionContext.LOG.debug("ignoring exception from after completion on ended transaction: " + ignored, ignored);
                        }
                    }
                }
            }
        }
        catch (JMSException e) {
            TransactionContext.LOG.warn("commit of: " + x + " failed with: " + e, e);
            if (onePhase) {
                synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                    final List<TransactionContext> l = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
                    if (l != null && !l.isEmpty()) {
                        for (final TransactionContext ctx : l) {
                            try {
                                ctx.afterRollback();
                            }
                            catch (Throwable ignored2) {
                                if (!TransactionContext.LOG.isDebugEnabled()) {
                                    continue;
                                }
                                TransactionContext.LOG.debug("failed to firing afterRollback callbacks commit failure, txid: " + x + ", context: " + ctx, ignored2);
                            }
                        }
                    }
                }
            }
            throw this.toXAException(e);
        }
    }
    
    @Override
    public void forget(final Xid xid) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Forget: " + xid);
        }
        if (xid == null) {
            throw new XAException(-6);
        }
        XATransactionId x;
        if (this.equals(this.associatedXid, xid)) {
            x = (XATransactionId)this.transactionId;
        }
        else {
            x = new XATransactionId(xid);
        }
        final TransactionInfo info = new TransactionInfo(this.getConnectionId(), x, (byte)6);
        try {
            this.syncSendPacketWithInterruptionHandling(info);
        }
        catch (JMSException e) {
            throw this.toXAException(e);
        }
        synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
            TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.remove(x);
        }
    }
    
    @Override
    public boolean isSameRM(final XAResource xaResource) throws XAException {
        if (xaResource == null) {
            return false;
        }
        if (!(xaResource instanceof TransactionContext)) {
            return false;
        }
        final TransactionContext xar = (TransactionContext)xaResource;
        try {
            return this.getResourceManagerId().equals(xar.getResourceManagerId());
        }
        catch (Throwable e) {
            throw (XAException)new XAException("Could not get resource manager id.").initCause(e);
        }
    }
    
    @Override
    public Xid[] recover(final int flag) throws XAException {
        if (TransactionContext.LOG.isDebugEnabled()) {
            TransactionContext.LOG.debug("Recover: " + flag);
        }
        final TransactionInfo info = new TransactionInfo(this.getConnectionId(), null, (byte)5);
        try {
            this.connection.checkClosedOrFailed();
            final FailoverTransport failoverTransport = this.connection.getTransport().narrow(FailoverTransport.class);
            if (failoverTransport != null && !failoverTransport.isConnected()) {
                final XAException xaException = new XAException("Failover transport not connected: " + this.getConnection().getTransport());
                xaException.errorCode = -3;
                throw xaException;
            }
            this.connection.ensureConnectionInfoSent();
            final DataArrayResponse receipt = (DataArrayResponse)this.connection.syncSendPacket(info);
            final DataStructure[] data = receipt.getData();
            XATransactionId[] answer;
            if (data instanceof XATransactionId[]) {
                answer = (XATransactionId[])data;
            }
            else {
                answer = new XATransactionId[data.length];
                System.arraycopy(data, 0, answer, 0, data.length);
            }
            TransactionContext.LOG.trace("recover({})={}", (Object)flag, answer);
            return answer;
        }
        catch (JMSException e) {
            throw this.toXAException(e);
        }
    }
    
    @Override
    public int getTransactionTimeout() throws XAException {
        return 0;
    }
    
    @Override
    public boolean setTransactionTimeout(final int seconds) throws XAException {
        return false;
    }
    
    protected String getResourceManagerId() throws JMSException {
        return this.connection.getResourceManagerId();
    }
    
    private void setXid(final Xid xid) throws XAException {
        try {
            this.connection.checkClosedOrFailed();
            this.connection.ensureConnectionInfoSent();
        }
        catch (JMSException e) {
            this.disassociate();
            throw this.toXAException(e);
        }
        if (xid != null) {
            this.associatedXid = xid;
            this.transactionId = new XATransactionId(xid);
            final TransactionInfo info = new TransactionInfo(this.getConnectionId(), this.transactionId, (byte)0);
            try {
                this.connection.asyncSendPacket(info);
                if (TransactionContext.LOG.isDebugEnabled()) {
                    TransactionContext.LOG.debug("{} started XA transaction {} ", this, this.transactionId);
                }
            }
            catch (JMSException e2) {
                this.disassociate();
                throw this.toXAException(e2);
            }
        }
        else {
            if (this.transactionId != null) {
                final TransactionInfo info = new TransactionInfo(this.getConnectionId(), this.transactionId, (byte)7);
                try {
                    this.syncSendPacketWithInterruptionHandling(info);
                    if (TransactionContext.LOG.isDebugEnabled()) {
                        TransactionContext.LOG.debug("{} ended XA transaction {}", this, this.transactionId);
                    }
                }
                catch (JMSException e2) {
                    this.disassociate();
                    throw this.toXAException(e2);
                }
                synchronized (TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS) {
                    List<TransactionContext> l = TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.get(this.transactionId);
                    if (l == null) {
                        l = new ArrayList<TransactionContext>(3);
                        TransactionContext.ENDED_XA_TRANSACTION_CONTEXTS.put(this.transactionId, l);
                        l.add(this);
                    }
                    else if (!l.contains(this)) {
                        l.add(this);
                    }
                }
            }
            this.disassociate();
        }
    }
    
    private void disassociate() {
        this.associatedXid = null;
        this.transactionId = null;
    }
    
    private Response syncSendPacketWithInterruptionHandling(final Command command) throws JMSException {
        try {
            return this.connection.syncSendPacket(command);
        }
        catch (JMSException e) {
            if (e.getLinkedException() instanceof InterruptedIOException) {
                try {
                    Thread.interrupted();
                    return this.connection.syncSendPacket(command);
                }
                finally {
                    Thread.currentThread().interrupt();
                }
            }
            throw e;
        }
    }
    
    private XAException toXAException(final JMSException e) {
        if (e.getCause() != null && e.getCause() instanceof XAException) {
            final XAException original = (XAException)e.getCause();
            final XAException xae = new XAException(original.getMessage());
            xae.errorCode = original.errorCode;
            if (xae.errorCode == 0) {
                xae.errorCode = -3;
            }
            xae.initCause(original);
            return xae;
        }
        final XAException xae2 = new XAException(e.getMessage());
        xae2.errorCode = -7;
        xae2.initCause(e);
        return xae2;
    }
    
    public ActiveMQConnection getConnection() {
        return this.connection;
    }
    
    public ActiveMQConnection setConnection(final ActiveMQConnection connection) {
        final ActiveMQConnection existing = this.connection;
        this.connection = connection;
        return existing;
    }
    
    public void cleanup() {
        this.associatedXid = null;
        this.transactionId = null;
    }
    
    @Override
    public String toString() {
        return "TransactionContext{transactionId=" + this.transactionId + '}';
    }
    
    static {
        LOG = LoggerFactory.getLogger(TransactionContext.class);
        ENDED_XA_TRANSACTION_CONTEXTS = new HashMap<TransactionId, List<TransactionContext>>();
    }
}
