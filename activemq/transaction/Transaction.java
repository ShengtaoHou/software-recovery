// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.transaction;

import java.util.concurrent.ExecutionException;
import java.io.InterruptedIOException;
import org.slf4j.Logger;
import org.apache.activemq.command.TransactionId;
import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import javax.transaction.xa.XAException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.ArrayList;

public abstract class Transaction
{
    public static final byte START_STATE = 0;
    public static final byte IN_USE_STATE = 1;
    public static final byte PREPARED_STATE = 2;
    public static final byte FINISHED_STATE = 3;
    boolean committed;
    private final ArrayList<Synchronization> synchronizations;
    private byte state;
    protected FutureTask<?> preCommitTask;
    protected FutureTask<?> postCommitTask;
    
    public Transaction() {
        this.committed = false;
        this.synchronizations = new ArrayList<Synchronization>();
        this.state = 0;
        this.preCommitTask = new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Transaction.this.doPreCommit();
                return null;
            }
        });
        this.postCommitTask = new FutureTask<Object>(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Transaction.this.doPostCommit();
                return null;
            }
        });
    }
    
    public byte getState() {
        return this.state;
    }
    
    public void setState(final byte state) {
        this.state = state;
    }
    
    public boolean isCommitted() {
        return this.committed;
    }
    
    public void setCommitted(final boolean committed) {
        this.committed = committed;
    }
    
    public void addSynchronization(final Synchronization r) {
        this.synchronizations.add(r);
        if (this.state == 0) {
            this.state = 1;
        }
    }
    
    public Synchronization findMatching(final Synchronization r) {
        final int existing = this.synchronizations.indexOf(r);
        if (existing != -1) {
            return this.synchronizations.get(existing);
        }
        return null;
    }
    
    public void removeSynchronization(final Synchronization r) {
        this.synchronizations.remove(r);
    }
    
    public void prePrepare() throws Exception {
        switch (this.state) {
            case 0:
            case 1: {}
            default: {
                final XAException xae = new XAException("Prepare cannot be called now.");
                xae.errorCode = -6;
                throw xae;
            }
        }
    }
    
    protected void fireBeforeCommit() throws Exception {
        for (final Synchronization s : this.synchronizations) {
            s.beforeCommit();
        }
    }
    
    protected void fireAfterCommit() throws Exception {
        for (final Synchronization s : this.synchronizations) {
            s.afterCommit();
        }
    }
    
    public void fireAfterRollback() throws Exception {
        Collections.reverse(this.synchronizations);
        for (final Synchronization s : this.synchronizations) {
            s.afterRollback();
        }
    }
    
    @Override
    public String toString() {
        return "Local-" + this.getTransactionId() + "[synchronizations=" + this.synchronizations + "]";
    }
    
    public abstract void commit(final boolean p0) throws XAException, IOException;
    
    public abstract void rollback() throws XAException, IOException;
    
    public abstract int prepare() throws XAException, IOException;
    
    public abstract TransactionId getTransactionId();
    
    public abstract Logger getLog();
    
    public boolean isPrepared() {
        return this.getState() == 2;
    }
    
    public int size() {
        return this.synchronizations.size();
    }
    
    protected void waitPostCommitDone(final FutureTask<?> postCommitTask) throws XAException, IOException {
        try {
            postCommitTask.get();
        }
        catch (InterruptedException e) {
            throw new InterruptedIOException(e.toString());
        }
        catch (ExecutionException e2) {
            final Throwable t = e2.getCause();
            if (t instanceof XAException) {
                throw (XAException)t;
            }
            if (t instanceof IOException) {
                throw (IOException)t;
            }
            throw new XAException(e2.toString());
        }
    }
    
    protected void doPreCommit() throws XAException {
        try {
            this.fireBeforeCommit();
        }
        catch (Throwable e) {
            this.getLog().warn("PRE COMMIT FAILED: ", e);
            final XAException xae = new XAException("PRE COMMIT FAILED");
            xae.errorCode = -3;
            xae.initCause(e);
            throw xae;
        }
    }
    
    protected void doPostCommit() throws XAException {
        try {
            this.setCommitted(true);
            this.fireAfterCommit();
        }
        catch (Throwable e) {
            this.getLog().warn("POST COMMIT FAILED: ", e);
            final XAException xae = new XAException("POST COMMIT FAILED");
            xae.errorCode = -3;
            xae.initCause(e);
            throw xae;
        }
    }
}
