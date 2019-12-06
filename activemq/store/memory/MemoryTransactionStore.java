// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.memory;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.ProxyTopicMessageStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.store.InlineListenableFuture;
import org.apache.activemq.store.ListenableFuture;
import java.io.IOException;
import org.apache.activemq.command.Message;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.store.MessageStore;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.command.TransactionId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.activemq.store.TransactionStore;

public class MemoryTransactionStore implements TransactionStore
{
    protected ConcurrentHashMap<Object, Tx> inflightTransactions;
    protected Map<TransactionId, Tx> preparedTransactions;
    protected final PersistenceAdapter persistenceAdapter;
    private boolean doingRecover;
    
    public MemoryTransactionStore(final PersistenceAdapter persistenceAdapter) {
        this.inflightTransactions = new ConcurrentHashMap<Object, Tx>();
        this.preparedTransactions = Collections.synchronizedMap(new LinkedHashMap<TransactionId, Tx>());
        this.persistenceAdapter = persistenceAdapter;
    }
    
    public MessageStore proxy(final MessageStore messageStore) {
        final ProxyMessageStore proxyMessageStore = new ProxyMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimize) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), message);
                return new InlineListenableFuture();
            }
            
            @Override
            public ListenableFuture<Object> asyncAddQueueMessage(final ConnectionContext context, final Message message, final boolean canoptimize) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), message);
                return new InlineListenableFuture();
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MemoryTransactionStore.this.removeMessage(this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MemoryTransactionStore.this.removeMessage(this.getDelegate(), ack);
            }
        };
        this.onProxyQueueStore(proxyMessageStore);
        return proxyMessageStore;
    }
    
    protected void onProxyQueueStore(final ProxyMessageStore proxyMessageStore) {
    }
    
    public TopicMessageStore proxy(final TopicMessageStore messageStore) {
        final ProxyTopicMessageStore proxyTopicMessageStore = new ProxyTopicMessageStore(messageStore) {
            @Override
            public void addMessage(final ConnectionContext context, final Message send) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), send);
            }
            
            @Override
            public void addMessage(final ConnectionContext context, final Message send, final boolean canOptimize) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), send);
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), message);
                return new InlineListenableFuture();
            }
            
            @Override
            public ListenableFuture<Object> asyncAddTopicMessage(final ConnectionContext context, final Message message, final boolean canOptimize) throws IOException {
                MemoryTransactionStore.this.addMessage(this.getDelegate(), message);
                return new InlineListenableFuture();
            }
            
            @Override
            public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MemoryTransactionStore.this.removeMessage(this.getDelegate(), ack);
            }
            
            @Override
            public void removeAsyncMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
                MemoryTransactionStore.this.removeMessage(this.getDelegate(), ack);
            }
            
            @Override
            public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
                MemoryTransactionStore.this.acknowledge((TopicMessageStore)this.getDelegate(), clientId, subscriptionName, messageId, ack);
            }
        };
        this.onProxyTopicStore(proxyTopicMessageStore);
        return proxyTopicMessageStore;
    }
    
    protected void onProxyTopicStore(final ProxyTopicMessageStore proxyTopicMessageStore) {
    }
    
    @Override
    public void prepare(final TransactionId txid) throws IOException {
        final Tx tx = this.inflightTransactions.remove(txid);
        if (tx == null) {
            return;
        }
        this.preparedTransactions.put(txid, tx);
    }
    
    public Tx getTx(final Object txid) {
        Tx tx = this.inflightTransactions.get(txid);
        if (tx == null) {
            tx = new Tx();
            this.inflightTransactions.put(txid, tx);
        }
        return tx;
    }
    
    public Tx getPreparedTx(final TransactionId txid) {
        Tx tx = this.preparedTransactions.get(txid);
        if (tx == null) {
            tx = new Tx();
            this.preparedTransactions.put(txid, tx);
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
            tx = this.preparedTransactions.remove(txid);
        }
        else {
            tx = this.inflightTransactions.remove(txid);
        }
        if (tx != null) {
            tx.commit();
        }
        if (postCommit != null) {
            postCommit.run();
        }
    }
    
    @Override
    public void rollback(final TransactionId txid) throws IOException {
        this.preparedTransactions.remove(txid);
        this.inflightTransactions.remove(txid);
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void stop() throws Exception {
    }
    
    @Override
    public synchronized void recover(final TransactionRecoveryListener listener) throws IOException {
        this.inflightTransactions.clear();
        this.doingRecover = true;
        try {
            for (final Object txid : this.preparedTransactions.keySet()) {
                final Tx tx = this.preparedTransactions.get(txid);
                listener.recover((XATransactionId)txid, tx.getMessages(), tx.getAcks());
                this.onRecovered(tx);
            }
        }
        finally {
            this.doingRecover = false;
        }
    }
    
    protected void onRecovered(final Tx tx) {
    }
    
    void addMessage(final MessageStore destination, final Message message) throws IOException {
        if (this.doingRecover) {
            return;
        }
        if (message.getTransactionId() != null) {
            final Tx tx = this.getTx(message.getTransactionId());
            tx.add(new AddMessageCommand() {
                MessageStore messageStore = destination;
                
                @Override
                public Message getMessage() {
                    return message;
                }
                
                @Override
                public MessageStore getMessageStore() {
                    return destination;
                }
                
                @Override
                public void run(final ConnectionContext ctx) throws IOException {
                    destination.addMessage(ctx, message);
                }
                
                @Override
                public void setMessageStore(final MessageStore messageStore) {
                    this.messageStore = messageStore;
                }
            });
        }
        else {
            destination.addMessage(null, message);
        }
    }
    
    final void removeMessage(final MessageStore destination, final MessageAck ack) throws IOException {
        if (this.doingRecover) {
            return;
        }
        if (ack.isInTransaction()) {
            final Tx tx = this.getTx(ack.getTransactionId());
            tx.add(new RemoveMessageCommand() {
                @Override
                public MessageAck getMessageAck() {
                    return ack;
                }
                
                @Override
                public void run(final ConnectionContext ctx) throws IOException {
                    destination.removeMessage(ctx, ack);
                }
                
                @Override
                public MessageStore getMessageStore() {
                    return destination;
                }
            });
        }
        else {
            destination.removeMessage(null, ack);
        }
    }
    
    public void acknowledge(final TopicMessageStore destination, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        if (this.doingRecover) {
            return;
        }
        if (ack.isInTransaction()) {
            final Tx tx = this.getTx(ack.getTransactionId());
            tx.add(new RemoveMessageCommand() {
                @Override
                public MessageAck getMessageAck() {
                    return ack;
                }
                
                @Override
                public void run(final ConnectionContext ctx) throws IOException {
                    destination.acknowledge(ctx, clientId, subscriptionName, messageId, ack);
                }
                
                @Override
                public MessageStore getMessageStore() {
                    return destination;
                }
            });
        }
        else {
            destination.acknowledge(null, clientId, subscriptionName, messageId, ack);
        }
    }
    
    public void delete() {
        this.inflightTransactions.clear();
        this.preparedTransactions.clear();
        this.doingRecover = false;
    }
    
    public class Tx
    {
        public ArrayList<AddMessageCommand> messages;
        public final ArrayList<RemoveMessageCommand> acks;
        
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
        
        public void commit() throws IOException {
            final ConnectionContext ctx = new ConnectionContext();
            MemoryTransactionStore.this.persistenceAdapter.beginTransaction(ctx);
            try {
                for (final AddMessageCommand cmd : this.messages) {
                    cmd.run(ctx);
                }
                for (final RemoveMessageCommand cmd2 : this.acks) {
                    cmd2.run(ctx);
                }
            }
            catch (IOException e) {
                MemoryTransactionStore.this.persistenceAdapter.rollbackTransaction(ctx);
                throw e;
            }
            MemoryTransactionStore.this.persistenceAdapter.commitTransaction(ctx);
        }
    }
    
    public interface RemoveMessageCommand
    {
        MessageAck getMessageAck();
        
        void run(final ConnectionContext p0) throws IOException;
        
        MessageStore getMessageStore();
    }
    
    public interface AddMessageCommand
    {
        Message getMessage();
        
        MessageStore getMessageStore();
        
        void run(final ConnectionContext p0) throws IOException;
        
        void setMessageStore(final MessageStore p0);
    }
}
