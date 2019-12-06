// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.store.ProxyTopicMessageStore;
import org.apache.activemq.util.DataByteArrayInputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.command.MessageAck;
import java.util.Iterator;
import org.apache.activemq.command.Message;
import java.util.ArrayList;
import java.io.IOException;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.HashMap;
import org.apache.activemq.store.memory.MemoryTransactionStore;

public class JdbcMemoryTransactionStore extends MemoryTransactionStore
{
    private HashMap<ActiveMQDestination, MessageStore> topicStores;
    private HashMap<ActiveMQDestination, MessageStore> queueStores;
    
    public JdbcMemoryTransactionStore(final JDBCPersistenceAdapter jdbcPersistenceAdapter) {
        super(jdbcPersistenceAdapter);
        this.topicStores = new HashMap<ActiveMQDestination, MessageStore>();
        this.queueStores = new HashMap<ActiveMQDestination, MessageStore>();
    }
    
    @Override
    public void prepare(final TransactionId txid) throws IOException {
        final Tx tx = this.inflightTransactions.remove(txid);
        if (tx == null) {
            return;
        }
        final ConnectionContext ctx = new ConnectionContext();
        ctx.setXid((XATransactionId)txid);
        this.persistenceAdapter.beginTransaction(ctx);
        try {
            for (final AddMessageCommand cmd : tx.messages) {
                cmd.run(ctx);
            }
            for (final RemoveMessageCommand cmd2 : tx.acks) {
                cmd2.run(ctx);
            }
        }
        catch (IOException e) {
            this.persistenceAdapter.rollbackTransaction(ctx);
            throw e;
        }
        this.persistenceAdapter.commitTransaction(ctx);
        ctx.setXid(null);
        final ArrayList<AddMessageCommand> updateFromPreparedStateCommands = new ArrayList<AddMessageCommand>();
        for (final AddMessageCommand addMessageCommand : tx.messages) {
            updateFromPreparedStateCommands.add(new AddMessageCommand() {
                @Override
                public Message getMessage() {
                    return addMessageCommand.getMessage();
                }
                
                @Override
                public MessageStore getMessageStore() {
                    return addMessageCommand.getMessageStore();
                }
                
                @Override
                public void run(final ConnectionContext context) throws IOException {
                    final JDBCPersistenceAdapter jdbcPersistenceAdapter = (JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter;
                    final Message message = addMessageCommand.getMessage();
                    jdbcPersistenceAdapter.commitAdd(context, message.getMessageId());
                    ((JDBCMessageStore)addMessageCommand.getMessageStore()).onAdd(message.getMessageId(), (long)message.getMessageId().getEntryLocator(), message.getPriority());
                }
                
                @Override
                public void setMessageStore(final MessageStore messageStore) {
                    throw new RuntimeException("MessageStore already known");
                }
            });
        }
        tx.messages = updateFromPreparedStateCommands;
        this.preparedTransactions.put(txid, tx);
    }
    
    @Override
    public void rollback(final TransactionId txid) throws IOException {
        Tx tx = this.inflightTransactions.remove(txid);
        if (tx == null) {
            tx = this.preparedTransactions.remove(txid);
            if (tx != null) {
                final ConnectionContext ctx = new ConnectionContext();
                this.persistenceAdapter.beginTransaction(ctx);
                try {
                    final Iterator<AddMessageCommand> iter = tx.messages.iterator();
                    while (iter.hasNext()) {
                        final Message message = iter.next().getMessage();
                        ((JDBCPersistenceAdapter)this.persistenceAdapter).commitRemove(ctx, new MessageAck(message, (byte)2, 1));
                    }
                    for (final RemoveMessageCommand removeMessageCommand : tx.acks) {
                        if (removeMessageCommand instanceof LastAckCommand) {
                            ((LastAckCommand)removeMessageCommand).rollback(ctx);
                        }
                        else {
                            ((JDBCPersistenceAdapter)this.persistenceAdapter).commitAdd(ctx, removeMessageCommand.getMessageAck().getLastMessageId());
                        }
                    }
                }
                catch (IOException e) {
                    this.persistenceAdapter.rollbackTransaction(ctx);
                    throw e;
                }
                this.persistenceAdapter.commitTransaction(ctx);
            }
        }
    }
    
    @Override
    public void recover(final TransactionRecoveryListener listener) throws IOException {
        ((JDBCPersistenceAdapter)this.persistenceAdapter).recover(this);
        super.recover(listener);
    }
    
    public void recoverAdd(final long id, final byte[] messageBytes) throws IOException {
        final Message message = (Message)((JDBCPersistenceAdapter)this.persistenceAdapter).getWireFormat().unmarshal(new ByteSequence(messageBytes));
        message.getMessageId().setEntryLocator(id);
        final Tx tx = this.getPreparedTx(message.getTransactionId());
        tx.add(new AddMessageCommand() {
            MessageStore messageStore;
            
            @Override
            public Message getMessage() {
                return message;
            }
            
            @Override
            public MessageStore getMessageStore() {
                return this.messageStore;
            }
            
            @Override
            public void run(final ConnectionContext context) throws IOException {
                ((JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter).commitAdd(null, message.getMessageId());
                ((JDBCMessageStore)this.messageStore).onAdd(message.getMessageId(), (long)message.getMessageId().getEntryLocator(), message.getPriority());
            }
            
            @Override
            public void setMessageStore(final MessageStore messageStore) {
                this.messageStore = messageStore;
            }
        });
    }
    
    public void recoverAck(final long id, final byte[] xid, final byte[] message) throws IOException {
        final Message msg = (Message)((JDBCPersistenceAdapter)this.persistenceAdapter).getWireFormat().unmarshal(new ByteSequence(message));
        msg.getMessageId().setEntryLocator(id);
        final Tx tx = this.getPreparedTx(new XATransactionId(xid));
        final MessageAck ack = new MessageAck(msg, (byte)2, 1);
        tx.add(new RemoveMessageCommand() {
            @Override
            public MessageAck getMessageAck() {
                return ack;
            }
            
            @Override
            public void run(final ConnectionContext context) throws IOException {
                ((JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter).commitRemove(context, ack);
            }
            
            @Override
            public MessageStore getMessageStore() {
                return null;
            }
        });
    }
    
    public void recoverLastAck(final byte[] encodedXid, final ActiveMQDestination destination, final String subName, final String clientId) throws IOException {
        final Tx tx = this.getPreparedTx(new XATransactionId(encodedXid));
        final DataByteArrayInputStream inputStream = new DataByteArrayInputStream(encodedXid);
        inputStream.skipBytes(1);
        final long lastAck = inputStream.readLong();
        final byte priority = inputStream.readByte();
        final MessageAck ack = new MessageAck();
        ack.setDestination(destination);
        tx.add(new LastAckCommand() {
            JDBCTopicMessageStore jdbcTopicMessageStore;
            
            @Override
            public MessageAck getMessageAck() {
                return ack;
            }
            
            @Override
            public MessageStore getMessageStore() {
                return this.jdbcTopicMessageStore;
            }
            
            @Override
            public void run(final ConnectionContext context) throws IOException {
                ((JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter).commitLastAck(context, lastAck, priority, destination, subName, clientId);
                this.jdbcTopicMessageStore.complete(clientId, subName);
            }
            
            @Override
            public void rollback(final ConnectionContext context) throws IOException {
                ((JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter).rollbackLastAck(context, priority, this.jdbcTopicMessageStore.getDestination(), subName, clientId);
                this.jdbcTopicMessageStore.complete(clientId, subName);
            }
            
            @Override
            public String getClientId() {
                return clientId;
            }
            
            @Override
            public String getSubName() {
                return subName;
            }
            
            @Override
            public long getSequence() {
                return lastAck;
            }
            
            @Override
            public byte getPriority() {
                return priority;
            }
            
            @Override
            public void setMessageStore(final JDBCTopicMessageStore jdbcTopicMessageStore) {
                this.jdbcTopicMessageStore = jdbcTopicMessageStore;
            }
        });
    }
    
    @Override
    protected void onProxyTopicStore(final ProxyTopicMessageStore proxyTopicMessageStore) {
        this.topicStores.put(proxyTopicMessageStore.getDestination(), proxyTopicMessageStore.getDelegate());
    }
    
    @Override
    protected void onProxyQueueStore(final ProxyMessageStore proxyQueueMessageStore) {
        this.queueStores.put(proxyQueueMessageStore.getDestination(), proxyQueueMessageStore.getDelegate());
    }
    
    @Override
    protected void onRecovered(final Tx tx) {
        for (final RemoveMessageCommand removeMessageCommand : tx.acks) {
            if (removeMessageCommand instanceof LastAckCommand) {
                final LastAckCommand lastAckCommand = (LastAckCommand)removeMessageCommand;
                final JDBCTopicMessageStore jdbcTopicMessageStore = this.topicStores.get(lastAckCommand.getMessageAck().getDestination());
                jdbcTopicMessageStore.pendingCompletion(lastAckCommand.getClientId(), lastAckCommand.getSubName(), lastAckCommand.getSequence(), lastAckCommand.getPriority());
                lastAckCommand.setMessageStore(jdbcTopicMessageStore);
            }
            else {
                ((JDBCPersistenceAdapter)this.persistenceAdapter).getBrokerService().getRegionBroker().getDestinationMap().get(removeMessageCommand.getMessageAck().getDestination()).getDestinationStatistics().getMessages().increment();
            }
        }
        for (final AddMessageCommand addMessageCommand : tx.messages) {
            final ActiveMQDestination destination = addMessageCommand.getMessage().getDestination();
            addMessageCommand.setMessageStore(destination.isQueue() ? this.queueStores.get(destination) : this.topicStores.get(destination));
        }
    }
    
    @Override
    public void acknowledge(final TopicMessageStore topicMessageStore, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
        if (ack.isInTransaction()) {
            final Tx tx = this.getTx(ack.getTransactionId());
            tx.add(new LastAckCommand() {
                @Override
                public MessageAck getMessageAck() {
                    return ack;
                }
                
                @Override
                public void run(final ConnectionContext ctx) throws IOException {
                    topicMessageStore.acknowledge(ctx, clientId, subscriptionName, messageId, ack);
                }
                
                @Override
                public MessageStore getMessageStore() {
                    return topicMessageStore;
                }
                
                @Override
                public void rollback(final ConnectionContext context) throws IOException {
                    final JDBCTopicMessageStore jdbcTopicMessageStore = (JDBCTopicMessageStore)topicMessageStore;
                    ((JDBCPersistenceAdapter)JdbcMemoryTransactionStore.this.persistenceAdapter).rollbackLastAck(context, jdbcTopicMessageStore, ack, subscriptionName, clientId);
                    jdbcTopicMessageStore.complete(clientId, subscriptionName);
                }
                
                @Override
                public String getClientId() {
                    return clientId;
                }
                
                @Override
                public String getSubName() {
                    return subscriptionName;
                }
                
                @Override
                public long getSequence() {
                    throw new IllegalStateException("Sequence id must be inferred from ack");
                }
                
                @Override
                public byte getPriority() {
                    throw new IllegalStateException("Priority must be inferred from ack or row");
                }
                
                @Override
                public void setMessageStore(final JDBCTopicMessageStore jdbcTopicMessageStore) {
                    throw new IllegalStateException("message store already known!");
                }
            });
        }
        else {
            topicMessageStore.acknowledge(null, clientId, subscriptionName, messageId, ack);
        }
    }
    
    interface LastAckCommand extends RemoveMessageCommand
    {
        void rollback(final ConnectionContext p0) throws IOException;
        
        String getClientId();
        
        String getSubName();
        
        long getSequence();
        
        byte getPriority();
        
        void setMessageStore(final JDBCTopicMessageStore p0);
    }
}
