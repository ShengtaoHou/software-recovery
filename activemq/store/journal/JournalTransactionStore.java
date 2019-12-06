// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.journal;

import java.util.ArrayList;
import org.apache.activemq.command.JournalTopicAck;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import java.util.Iterator;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activeio.journal.RecordLocation;
import java.io.IOException;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.JournalTransaction;
import java.util.LinkedHashMap;
import org.apache.activemq.command.TransactionId;
import java.util.Map;
import org.apache.activemq.store.TransactionStore;

public class JournalTransactionStore implements TransactionStore
{
    private final JournalPersistenceAdapter peristenceAdapter;
    private final Map<Object, Tx> inflightTransactions;
    private final Map<TransactionId, Tx> preparedTransactions;
    private boolean doingRecover;
    
    public JournalTransactionStore(final JournalPersistenceAdapter adapter) {
        this.inflightTransactions = new LinkedHashMap<Object, Tx>();
        this.preparedTransactions = new LinkedHashMap<TransactionId, Tx>();
        this.peristenceAdapter = adapter;
    }
    
    @Override
    public void prepare(final TransactionId txid) throws IOException {
        Tx tx = null;
        synchronized (this.inflightTransactions) {
            tx = this.inflightTransactions.remove(txid);
        }
        if (tx == null) {
            return;
        }
        this.peristenceAdapter.writeCommand(new JournalTransaction((byte)1, txid, false), true);
        synchronized (this.preparedTransactions) {
            this.preparedTransactions.put(txid, tx);
        }
    }
    
    public void replayPrepare(final TransactionId txid) throws IOException {
        Tx tx = null;
        synchronized (this.inflightTransactions) {
            tx = this.inflightTransactions.remove(txid);
        }
        if (tx == null) {
            return;
        }
        synchronized (this.preparedTransactions) {
            this.preparedTransactions.put(txid, tx);
        }
    }
    
    public Tx getTx(final Object txid, final RecordLocation location) {
        Tx tx = null;
        synchronized (this.inflightTransactions) {
            tx = this.inflightTransactions.get(txid);
        }
        if (tx == null) {
            tx = new Tx(location);
            this.inflightTransactions.put(txid, tx);
        }
        return tx;
    }
    
    @Override
    public void commit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) throws IOException {
        if (preCommit != null) {
            preCommit.run();
        }
        Tx tx;
        if (wasPrepared) {
            synchronized (this.preparedTransactions) {
                tx = this.preparedTransactions.remove(txid);
            }
        }
        else {
            synchronized (this.inflightTransactions) {
                tx = this.inflightTransactions.remove(txid);
            }
        }
        if (tx == null) {
            if (postCommit != null) {
                postCommit.run();
            }
            return;
        }
        if (txid.isXATransaction()) {
            this.peristenceAdapter.writeCommand(new JournalTransaction((byte)2, txid, wasPrepared), true);
        }
        else {
            this.peristenceAdapter.writeCommand(new JournalTransaction((byte)4, txid, wasPrepared), true);
        }
        if (postCommit != null) {
            postCommit.run();
        }
    }
    
    public Tx replayCommit(final TransactionId txid, final boolean wasPrepared) throws IOException {
        if (wasPrepared) {
            synchronized (this.preparedTransactions) {
                return this.preparedTransactions.remove(txid);
            }
        }
        synchronized (this.inflightTransactions) {
            return this.inflightTransactions.remove(txid);
        }
    }
    
    @Override
    public void rollback(final TransactionId txid) throws IOException {
        Tx tx = null;
        synchronized (this.inflightTransactions) {
            tx = this.inflightTransactions.remove(txid);
        }
        if (tx != null) {
            synchronized (this.preparedTransactions) {
                tx = this.preparedTransactions.remove(txid);
            }
        }
        if (tx != null) {
            if (txid.isXATransaction()) {
                this.peristenceAdapter.writeCommand(new JournalTransaction((byte)3, txid, false), true);
            }
            else {
                this.peristenceAdapter.writeCommand(new JournalTransaction((byte)5, txid, false), true);
            }
        }
    }
    
    public void replayRollback(final TransactionId txid) throws IOException {
        boolean inflight = false;
        synchronized (this.inflightTransactions) {
            inflight = (this.inflightTransactions.remove(txid) != null);
        }
        if (inflight) {
            synchronized (this.preparedTransactions) {
                this.preparedTransactions.remove(txid);
            }
        }
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public synchronized void recover(final TransactionRecoveryListener listener) throws IOException {
        synchronized (this.inflightTransactions) {
            this.inflightTransactions.clear();
        }
        this.doingRecover = true;
        try {
            Map<TransactionId, Tx> txs = null;
            synchronized (this.preparedTransactions) {
                txs = new LinkedHashMap<TransactionId, Tx>(this.preparedTransactions);
            }
            for (final Object txid : txs.keySet()) {
                final Tx tx = txs.get(txid);
                listener.recover((XATransactionId)txid, tx.getMessages(), tx.getAcks());
            }
        }
        finally {
            this.doingRecover = false;
        }
    }
    
    void addMessage(final JournalMessageStore store, final Message message, final RecordLocation location) throws IOException {
        final Tx tx = this.getTx(message.getTransactionId(), location);
        tx.add(store, message);
    }
    
    public void removeMessage(final JournalMessageStore store, final MessageAck ack, final RecordLocation location) throws IOException {
        final Tx tx = this.getTx(ack.getTransactionId(), location);
        tx.add(store, ack);
    }
    
    public void acknowledge(final JournalTopicMessageStore store, final JournalTopicAck ack, final RecordLocation location) {
        final Tx tx = this.getTx(ack.getTransactionId(), location);
        tx.add(store, ack);
    }
    
    public RecordLocation checkpoint() throws IOException {
        RecordLocation rc = null;
        synchronized (this.inflightTransactions) {
            for (final Tx tx : this.inflightTransactions.values()) {
                final RecordLocation location = tx.location;
                if (rc == null || rc.compareTo((Object)location) < 0) {
                    rc = location;
                }
            }
        }
        synchronized (this.preparedTransactions) {
            for (final Tx tx : this.preparedTransactions.values()) {
                final RecordLocation location = tx.location;
                if (rc == null || rc.compareTo((Object)location) < 0) {
                    rc = location;
                }
            }
            return rc;
        }
    }
    
    public boolean isDoingRecover() {
        return this.doingRecover;
    }
    
    public static class TxOperation
    {
        static final byte ADD_OPERATION_TYPE = 0;
        static final byte REMOVE_OPERATION_TYPE = 1;
        static final byte ACK_OPERATION_TYPE = 3;
        public byte operationType;
        public JournalMessageStore store;
        public Object data;
        
        public TxOperation(final byte operationType, final JournalMessageStore store, final Object data) {
            this.operationType = operationType;
            this.store = store;
            this.data = data;
        }
    }
    
    public static class Tx
    {
        private final RecordLocation location;
        private final ArrayList<TxOperation> operations;
        
        public Tx(final RecordLocation location) {
            this.operations = new ArrayList<TxOperation>();
            this.location = location;
        }
        
        public void add(final JournalMessageStore store, final Message msg) {
            this.operations.add(new TxOperation((byte)0, store, msg));
        }
        
        public void add(final JournalMessageStore store, final MessageAck ack) {
            this.operations.add(new TxOperation((byte)1, store, ack));
        }
        
        public void add(final JournalTopicMessageStore store, final JournalTopicAck ack) {
            this.operations.add(new TxOperation((byte)3, store, ack));
        }
        
        public Message[] getMessages() {
            final ArrayList<Object> list = new ArrayList<Object>();
            for (final TxOperation op : this.operations) {
                if (op.operationType == 0) {
                    list.add(op.data);
                }
            }
            final Message[] rc = new Message[list.size()];
            list.toArray(rc);
            return rc;
        }
        
        public MessageAck[] getAcks() {
            final ArrayList<Object> list = new ArrayList<Object>();
            for (final TxOperation op : this.operations) {
                if (op.operationType == 1) {
                    list.add(op.data);
                }
            }
            final MessageAck[] rc = new MessageAck[list.size()];
            list.toArray(rc);
            return rc;
        }
        
        public ArrayList<TxOperation> getOperations() {
            return this.operations;
        }
    }
}
