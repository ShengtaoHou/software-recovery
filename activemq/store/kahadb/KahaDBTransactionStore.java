// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.slf4j.LoggerFactory;
import org.apache.activemq.store.AbstractMessageStore;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import java.util.ArrayList;
import java.util.Map;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.kahadb.data.KahaRollbackCommand;
import java.util.Iterator;
import java.util.List;
import org.apache.activemq.store.kahadb.data.KahaCommitCommand;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.activemq.store.kahadb.data.KahaTransactionInfo;
import org.apache.activemq.store.kahadb.data.KahaPrepareCommand;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.ProxyTopicMessageStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.store.ListenableFuture;
import java.io.IOException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.wireformat.WireFormat;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.apache.activemq.store.TransactionStore;

public class KahaDBTransactionStore implements TransactionStore
{
    static final Logger LOG;
    ConcurrentHashMap<Object, Tx> inflightTransactions;
    private final KahaDBStore theStore;
    
    public KahaDBTransactionStore(final KahaDBStore theStore) {
        this.inflightTransactions = new ConcurrentHashMap<Object, Tx>();
        this.theStore = theStore;
    }
    
    private WireFormat wireFormat() {
        return this.theStore.wireFormat;
    }
    
    public MessageStore proxy(final MessageStore messageStore) {
        return new ProxyMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                KahaDBTransactionStore.this.addMessage(context, this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimize) throws IOException {
                KahaDBTransactionStore.this.addMessage(context, this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
                return KahaDBTransactionStore.this.asyncAddQueueMessage(context, this.getDelegate(), message);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message, final boolean canOptimize) throws IOException {
                return KahaDBTransactionStore.this.asyncAddQueueMessage(context, this.getDelegate(), message);
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                KahaDBTransactionStore.this.removeMessage(context, this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                KahaDBTransactionStore.this.removeAsyncMessage(context, this.getDelegate(), ack);
            }
        };
    }
    
    public TopicMessageStore proxy(final TopicMessageStore messageStore) {
        return new ProxyTopicMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                KahaDBTransactionStore.this.addMessage(context, this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimize) throws IOException {
                KahaDBTransactionStore.this.addMessage(context, this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
                return KahaDBTransactionStore.this.asyncAddTopicMessage(context, this.getDelegate(), message);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message, final boolean canOptimize) throws IOException {
                return KahaDBTransactionStore.this.asyncAddTopicMessage(context, this.getDelegate(), message);
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                KahaDBTransactionStore.this.removeMessage(context, this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                KahaDBTransactionStore.this.removeAsyncMessage(context, this.getDelegate(), ack);
            }
            
            @Override
            public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
                KahaDBTransactionStore.this.acknowledge(context, (TopicMessageStore)this.getDelegate(), clientId, subscriptionName, messageId, ack);
            }
        };
    }
    
    @Override
    public void prepare(final TransactionId txid) throws IOException {
        final KahaTransactionInfo info = this.getTransactionInfo(txid);
        if (txid.isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
            this.theStore.store(((KahaPrepareCommandBase<JournalCommand<?>>)new KahaPrepareCommand()).setTransactionInfo(info), true, null, null);
        }
        else {
            final Tx tx = this.inflightTransactions.remove(txid);
            if (tx != null) {
                this.theStore.store(((KahaPrepareCommandBase<JournalCommand<?>>)new KahaPrepareCommand()).setTransactionInfo(info), true, null, null);
            }
        }
    }
    
    public Tx getTx(final Object txid) {
        Tx tx = this.inflightTransactions.get(txid);
        if (tx == null) {
            tx = new Tx();
            this.inflightTransactions.put(txid, tx);
        }
        return tx;
    }
    
    @Override
    public void commit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) throws IOException {
        if (txid != null) {
            if (!txid.isXATransaction() && this.theStore.isConcurrentStoreAndDispatchTransactions()) {
                if (preCommit != null) {
                    preCommit.run();
                }
                final Tx tx = this.inflightTransactions.remove(txid);
                if (tx != null) {
                    final List<Future<Object>> results = tx.commit();
                    boolean doneSomething = false;
                    for (final Future<Object> result : results) {
                        try {
                            result.get();
                        }
                        catch (InterruptedException e) {
                            this.theStore.brokerService.handleIOException(new IOException(e.getMessage()));
                        }
                        catch (ExecutionException e2) {
                            this.theStore.brokerService.handleIOException(new IOException(e2.getMessage()));
                        }
                        catch (CancellationException ex) {}
                        if (!result.isCancelled()) {
                            doneSomething = true;
                        }
                    }
                    if (postCommit != null) {
                        postCommit.run();
                    }
                    if (doneSomething) {
                        final KahaTransactionInfo info = this.getTransactionInfo(txid);
                        this.theStore.store(((KahaCommitCommandBase<JournalCommand<?>>)new KahaCommitCommand()).setTransactionInfo(info), this.theStore.isEnableJournalDiskSyncs(), null, null);
                    }
                }
                else if (postCommit != null) {
                    postCommit.run();
                }
            }
            else {
                final KahaTransactionInfo info2 = this.getTransactionInfo(txid);
                this.theStore.store(((KahaCommitCommandBase<JournalCommand<?>>)new KahaCommitCommand()).setTransactionInfo(info2), this.theStore.isEnableJournalDiskSyncs(), preCommit, postCommit);
                this.forgetRecoveredAcks(txid, false);
            }
        }
        else {
            KahaDBTransactionStore.LOG.error("Null transaction passed on commit");
        }
    }
    
    @Override
    public void rollback(final TransactionId txid) throws IOException {
        if (txid.isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
            final KahaTransactionInfo info = this.getTransactionInfo(txid);
            this.theStore.store(((KahaRollbackCommandBase<JournalCommand<?>>)new KahaRollbackCommand()).setTransactionInfo(info), this.theStore.isEnableJournalDiskSyncs(), null, null);
            this.forgetRecoveredAcks(txid, true);
        }
        else {
            this.inflightTransactions.remove(txid);
        }
    }
    
    protected void forgetRecoveredAcks(final TransactionId txid, final boolean isRollback) throws IOException {
        if (txid.isXATransaction()) {
            final XATransactionId xaTid = (XATransactionId)txid;
            this.theStore.forgetRecoveredAcks(xaTid.getPreparedAcks(), isRollback);
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
        for (final Map.Entry<TransactionId, List<MessageDatabase.Operation>> entry : this.theStore.preparedTransactions.entrySet()) {
            final XATransactionId xid = entry.getKey();
            final ArrayList<Message> messageList = new ArrayList<Message>();
            final ArrayList<MessageAck> ackList = new ArrayList<MessageAck>();
            for (final MessageDatabase.Operation op : entry.getValue()) {
                if (op.getClass() == MessageDatabase.AddOpperation.class) {
                    final MessageDatabase.AddOpperation addOp = (MessageDatabase.AddOpperation)op;
                    final Message msg = (Message)this.wireFormat().unmarshal(new DataInputStream(addOp.getCommand().getMessage().newInput()));
                    messageList.add(msg);
                }
                else {
                    final MessageDatabase.RemoveOpperation rmOp = (MessageDatabase.RemoveOpperation)op;
                    final Buffer ackb = rmOp.getCommand().getAck();
                    final MessageAck ack = (MessageAck)this.wireFormat().unmarshal(new DataInputStream(ackb.newInput()));
                    ackList.add(ack);
                }
            }
            final Message[] addedMessages = new Message[messageList.size()];
            final MessageAck[] acks = new MessageAck[ackList.size()];
            messageList.toArray(addedMessages);
            ackList.toArray(acks);
            xid.setPreparedAcks(ackList);
            this.theStore.trackRecoveredAcks(ackList);
            listener.recover(xid, addedMessages, acks);
        }
    }
    
    void addMessage(final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() != null) {
            if (message.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
                destination.addMessage(context, message);
            }
            else {
                final Tx tx = this.getTx(message.getTransactionId());
                tx.add(new AddMessageCommand(context) {
                    public Message getMessage() {
                        return message;
                    }
                    
                    public Future<Object> run(final ConnectionContext ctx) throws IOException {
                        destination.addMessage(ctx, message);
                        return AbstractMessageStore.FUTURE;
                    }
                });
            }
        }
        else {
            destination.addMessage(context, message);
        }
    }
    
    ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() == null) {
            return destination.asyncAddQueueMessage(context, message);
        }
        if (message.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
            destination.addMessage(context, message);
            return AbstractMessageStore.FUTURE;
        }
        final Tx tx = this.getTx(message.getTransactionId());
        tx.add(new AddMessageCommand(context) {
            public Message getMessage() {
                return message;
            }
            
            public Future<Object> run(final ConnectionContext ctx) throws IOException {
                return destination.asyncAddQueueMessage(ctx, message);
            }
        });
        return AbstractMessageStore.FUTURE;
    }
    
    ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final MessageStore destination, final Message message) throws IOException {
        if (message.getTransactionId() == null) {
            return destination.asyncAddTopicMessage(context, message);
        }
        if (message.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
            destination.addMessage(context, message);
            return AbstractMessageStore.FUTURE;
        }
        final Tx tx = this.getTx(message.getTransactionId());
        tx.add(new AddMessageCommand(context) {
            public Message getMessage() {
                return message;
            }
            
            public Future<Object> run(final ConnectionContext ctx) throws IOException {
                return destination.asyncAddTopicMessage(ctx, message);
            }
        });
        return AbstractMessageStore.FUTURE;
    }
    
    final void removeMessage(final ConnectionContext context, final MessageStore destination, final MessageAck ack) throws IOException {
        if (ack.isInTransaction()) {
            if (ack.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
                destination.removeMessage(context, ack);
            }
            else {
                final Tx tx = this.getTx(ack.getTransactionId());
                tx.add(new RemoveMessageCommand(context) {
                    public MessageAck getMessageAck() {
                        return ack;
                    }
                    
                    public Future<Object> run(final ConnectionContext ctx) throws IOException {
                        destination.removeMessage(ctx, ack);
                        return AbstractMessageStore.FUTURE;
                    }
                });
            }
        }
        else {
            destination.removeMessage(context, ack);
        }
    }
    
    final void removeAsyncMessage(final ConnectionContext context, final MessageStore destination, final MessageAck ack) throws IOException {
        if (ack.isInTransaction()) {
            if (ack.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
                destination.removeAsyncMessage(context, ack);
            }
            else {
                final Tx tx = this.getTx(ack.getTransactionId());
                tx.add(new RemoveMessageCommand(context) {
                    public MessageAck getMessageAck() {
                        return ack;
                    }
                    
                    public Future<Object> run(final ConnectionContext ctx) throws IOException {
                        destination.removeMessage(ctx, ack);
                        return AbstractMessageStore.FUTURE;
                    }
                });
            }
        }
        else {
            destination.removeAsyncMessage(context, ack);
        }
    }
    
    final void acknowledge(final ConnectionContext context, final TopicMessageStore destination, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        if (ack.isInTransaction()) {
            if (ack.getTransactionId().isXATransaction() || !this.theStore.isConcurrentStoreAndDispatchTransactions()) {
                destination.acknowledge(context, clientId, subscriptionName, messageId, ack);
            }
            else {
                final Tx tx = this.getTx(ack.getTransactionId());
                tx.add(new RemoveMessageCommand(context) {
                    public MessageAck getMessageAck() {
                        return ack;
                    }
                    
                    public Future<Object> run(final ConnectionContext ctx) throws IOException {
                        destination.acknowledge(ctx, clientId, subscriptionName, messageId, ack);
                        return AbstractMessageStore.FUTURE;
                    }
                });
            }
        }
        else {
            destination.acknowledge(context, clientId, subscriptionName, messageId, ack);
        }
    }
    
    private KahaTransactionInfo getTransactionInfo(final TransactionId txid) {
        return TransactionIdConversion.convert(this.theStore.getTransactionIdTransformer().transform(txid));
    }
    
    static {
        LOG = LoggerFactory.getLogger(KahaDBTransactionStore.class);
    }
    
    public class Tx
    {
        private final ArrayList<AddMessageCommand> messages;
        private final ArrayList<RemoveMessageCommand> acks;
        
        public Tx() {
            this.messages = new ArrayList<AddMessageCommand>();
            this.acks = new ArrayList<RemoveMessageCommand>();
        }
        
        public void add(final AddMessageCommand msg) {
            this.messages.add(msg);
        }
        
        public void add(final RemoveMessageCommand ack) {
            this.acks.add(ack);
        }
        
        public Message[] getMessages() {
            final Message[] rc = new Message[this.messages.size()];
            int count = 0;
            for (final AddMessageCommand cmd : this.messages) {
                rc[count++] = cmd.getMessage();
            }
            return rc;
        }
        
        public MessageAck[] getAcks() {
            final MessageAck[] rc = new MessageAck[this.acks.size()];
            int count = 0;
            for (final RemoveMessageCommand cmd : this.acks) {
                rc[count++] = cmd.getMessageAck();
            }
            return rc;
        }
        
        public List<Future<Object>> commit() throws IOException {
            final List<Future<Object>> results = new ArrayList<Future<Object>>();
            for (final AddMessageCommand cmd : this.messages) {
                results.add(cmd.run());
            }
            for (final RemoveMessageCommand cmd2 : this.acks) {
                cmd2.run();
                results.add(cmd2.run());
            }
            return results;
        }
    }
    
    public abstract class AddMessageCommand
    {
        private final ConnectionContext ctx;
        
        AddMessageCommand(final ConnectionContext ctx) {
            this.ctx = ctx;
        }
        
        abstract Message getMessage();
        
        Future<Object> run() throws IOException {
            return this.run(this.ctx);
        }
        
        abstract Future<Object> run(final ConnectionContext p0) throws IOException;
    }
    
    public abstract class RemoveMessageCommand
    {
        private final ConnectionContext ctx;
        
        RemoveMessageCommand(final ConnectionContext ctx) {
            this.ctx = ctx;
        }
        
        abstract MessageAck getMessageAck();
        
        Future<Object> run() throws IOException {
            return this.run(this.ctx);
        }
        
        abstract Future<Object> run(final ConnectionContext p0) throws IOException;
    }
}
