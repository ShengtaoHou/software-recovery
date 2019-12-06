// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.store.kahadb.data.KahaSubscriptionCommand;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.kahadb.data.KahaRemoveDestinationCommand;
import org.apache.activemq.store.kahadb.data.KahaRemoveMessageCommand;
import org.apache.activemq.store.kahadb.data.KahaAddMessageCommand;
import org.apache.activemq.store.AbstractMessageStore;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.store.kahadb.data.KahaDestination;
import org.apache.activemq.store.kahadb.data.KahaLocation;
import org.apache.activemq.store.kahadb.disk.journal.Location;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.kahadb.disk.page.Transaction;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import java.util.Iterator;
import java.io.DataInput;
import java.io.InputStream;
import java.io.DataInputStream;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.XATransactionId;
import java.util.ArrayList;
import java.util.Map;
import org.apache.activemq.store.TransactionRecoveryListener;
import java.io.IOException;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.store.PersistenceAdapter;

public class TempKahaDBStore extends TempMessageDatabase implements PersistenceAdapter, BrokerServiceAware
{
    private final WireFormat wireFormat;
    private BrokerService brokerService;
    
    public TempKahaDBStore() {
        this.wireFormat = new OpenWireFormat();
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        return new TransactionStore() {
            @Override
            public void commit(final TransactionId txid, final boolean wasPrepared, final Runnable preCommit, final Runnable postCommit) throws IOException {
                if (preCommit != null) {
                    preCommit.run();
                }
                TempKahaDBStore.this.processCommit(txid);
                if (postCommit != null) {
                    postCommit.run();
                }
            }
            
            @Override
            public void prepare(final TransactionId txid) throws IOException {
                TempKahaDBStore.this.processPrepare(txid);
            }
            
            @Override
            public void rollback(final TransactionId txid) throws IOException {
                TempKahaDBStore.this.processRollback(txid);
            }
            
            @Override
            public void recover(final TransactionRecoveryListener listener) throws IOException {
                for (final Map.Entry<TransactionId, ArrayList<Operation>> entry : TempKahaDBStore.this.preparedTransactions.entrySet()) {
                    final XATransactionId xid = entry.getKey();
                    final ArrayList<Message> messageList = new ArrayList<Message>();
                    final ArrayList<MessageAck> ackList = new ArrayList<MessageAck>();
                    for (final Operation op : entry.getValue()) {
                        if (op.getClass() == AddOpperation.class) {
                            final AddOpperation addOp = (AddOpperation)op;
                            final Message msg = (Message)TempKahaDBStore.this.wireFormat.unmarshal(new DataInputStream(addOp.getCommand().getMessage().newInput()));
                            messageList.add(msg);
                        }
                        else {
                            final RemoveOpperation rmOp = (RemoveOpperation)op;
                            final MessageAck ack = (MessageAck)TempKahaDBStore.this.wireFormat.unmarshal(new DataInputStream(rmOp.getCommand().getAck().newInput()));
                            ackList.add(ack);
                        }
                    }
                    final Message[] addedMessages = new Message[messageList.size()];
                    final MessageAck[] acks = new MessageAck[ackList.size()];
                    messageList.toArray(addedMessages);
                    ackList.toArray(acks);
                    listener.recover(xid, addedMessages, acks);
                }
            }
            
            @Override
            public void start() throws Exception {
            }
            
            @Override
            public void stop() throws Exception {
            }
        };
    }
    
    String subscriptionKey(final String clientId, final String subscriptionName) {
        return clientId + ":" + subscriptionName;
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        return new KahaDBMessageStore(destination);
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        return new KahaDBTopicMessageStore(destination);
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        try {
            final HashSet<ActiveMQDestination> rc = new HashSet<ActiveMQDestination>();
            synchronized (this.indexMutex) {
                this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        final Iterator<Map.Entry<String, StoredDestination>> iterator = TempKahaDBStore.this.destinations.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<String, StoredDestination> entry = iterator.next();
                            rc.add(TempKahaDBStore.this.convert(entry.getKey()));
                        }
                    }
                });
            }
            return rc;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return 0L;
    }
    
    @Override
    public long size() {
        if (!this.started.get()) {
            return 0L;
        }
        try {
            return this.pageFile.getDiskSize();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        throw new IOException("Not yet implemented.");
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
    }
    
    KahaLocation convert(final Location location) {
        final KahaLocation rc = new KahaLocation();
        rc.setLogId(location.getDataFileId());
        rc.setOffset(location.getOffset());
        return rc;
    }
    
    KahaDestination convert(final ActiveMQDestination dest) {
        final KahaDestination rc = new KahaDestination();
        rc.setName(dest.getPhysicalName());
        switch (dest.getDestinationType()) {
            case 1: {
                rc.setType(KahaDestination.DestinationType.QUEUE);
                return rc;
            }
            case 2: {
                rc.setType(KahaDestination.DestinationType.TOPIC);
                return rc;
            }
            case 5: {
                rc.setType(KahaDestination.DestinationType.TEMP_QUEUE);
                return rc;
            }
            case 6: {
                rc.setType(KahaDestination.DestinationType.TEMP_TOPIC);
                return rc;
            }
            default: {
                return null;
            }
        }
    }
    
    ActiveMQDestination convert(final String dest) {
        final int p = dest.indexOf(":");
        if (p < 0) {
            throw new IllegalArgumentException("Not in the valid destination format");
        }
        final int type = Integer.parseInt(dest.substring(0, p));
        final String name = dest.substring(p + 1);
        switch (KahaDestination.DestinationType.valueOf(type)) {
            case QUEUE: {
                return new ActiveMQQueue(name);
            }
            case TOPIC: {
                return new ActiveMQTopic(name);
            }
            case TEMP_QUEUE: {
                return new ActiveMQTempQueue(name);
            }
            case TEMP_TOPIC: {
                return new ActiveMQTempTopic(name);
            }
            default: {
                throw new IllegalArgumentException("Not in the valid destination format");
            }
        }
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) {
        return -1L;
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
    }
    
    @Override
    public void load() throws IOException {
        if (this.brokerService != null) {
            this.wireFormat.setVersion(this.brokerService.getStoreOpenWireVersion());
        }
        super.load();
    }
    
    public class KahaDBMessageStore extends AbstractMessageStore
    {
        protected KahaDestination dest;
        long cursorPos;
        
        public KahaDBMessageStore(final ActiveMQDestination destination) {
            super(destination);
            this.cursorPos = 0L;
            this.dest = TempKahaDBStore.this.convert(destination);
        }
        
        @Override
        public ActiveMQDestination getDestination() {
            return this.destination;
        }
        
        @Override
        public void addMessage(final ConnectionContext context, final Message message) throws IOException {
            final KahaAddMessageCommand command = new KahaAddMessageCommand();
            command.setDestination(this.dest);
            command.setMessageId(message.getMessageId().toProducerKey());
            TempKahaDBStore.this.processAdd(command, message.getTransactionId(), TempKahaDBStore.this.wireFormat.marshal(message));
        }
        
        @Override
        public void removeMessage(final ConnectionContext context, final MessageAck ack) throws IOException {
            final KahaRemoveMessageCommand command = new KahaRemoveMessageCommand();
            command.setDestination(this.dest);
            command.setMessageId(ack.getLastMessageId().toProducerKey());
            TempKahaDBStore.this.processRemove(command, ack.getTransactionId());
        }
        
        @Override
        public void removeAllMessages(final ConnectionContext context) throws IOException {
            final KahaRemoveDestinationCommand command = new KahaRemoveDestinationCommand();
            command.setDestination(this.dest);
            TempKahaDBStore.this.process(command);
        }
        
        @Override
        public Message getMessage(final MessageId identity) throws IOException {
            final String key = identity.toProducerKey();
            final ByteSequence data;
            synchronized (TempKahaDBStore.this.indexMutex) {
                data = TempKahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<ByteSequence, Throwable>)new Transaction.CallableClosure<ByteSequence, IOException>() {
                    @Override
                    public ByteSequence execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        final Long sequence = sd.messageIdIndex.get(tx, key);
                        if (sequence == null) {
                            return null;
                        }
                        return sd.orderIndex.get(tx, sequence).data;
                    }
                });
            }
            if (data == null) {
                return null;
            }
            final Message msg = (Message)TempKahaDBStore.this.wireFormat.unmarshal(data);
            return msg;
        }
        
        @Override
        public int getMessageCount() throws IOException {
            synchronized (TempKahaDBStore.this.indexMutex) {
                return TempKahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Integer, Throwable>)new Transaction.CallableClosure<Integer, IOException>() {
                    @Override
                    public Integer execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        int rc = 0;
                        final Iterator<Map.Entry<String, Long>> iterator = sd.messageIdIndex.iterator(tx);
                        while (iterator.hasNext()) {
                            iterator.next();
                            ++rc;
                        }
                        return rc;
                    }
                });
            }
        }
        
        @Override
        public void recover(final MessageRecoveryListener listener) throws Exception {
            synchronized (TempKahaDBStore.this.indexMutex) {
                TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<Long, MessageRecord> entry = iterator.next();
                            listener.recoverMessage((Message)TempKahaDBStore.this.wireFormat.unmarshal(entry.getValue().data));
                        }
                    }
                });
            }
        }
        
        @Override
        public void recoverNextMessages(final int maxReturned, final MessageRecoveryListener listener) throws Exception {
            synchronized (TempKahaDBStore.this.indexMutex) {
                TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        Map.Entry<Long, MessageRecord> entry = null;
                        int counter = 0;
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx, KahaDBMessageStore.this.cursorPos);
                        while (iterator.hasNext()) {
                            entry = iterator.next();
                            listener.recoverMessage((Message)TempKahaDBStore.this.wireFormat.unmarshal(entry.getValue().data));
                            if (++counter >= maxReturned) {
                                break;
                            }
                        }
                        if (entry != null) {
                            KahaDBMessageStore.this.cursorPos = entry.getKey() + 1L;
                        }
                    }
                });
            }
        }
        
        @Override
        public void resetBatching() {
            this.cursorPos = 0L;
        }
        
        @Override
        public void setBatch(final MessageId identity) throws IOException {
            final String key = identity.toProducerKey();
            final Long location;
            synchronized (TempKahaDBStore.this.indexMutex) {
                location = TempKahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Long, Throwable>)new Transaction.CallableClosure<Long, IOException>() {
                    @Override
                    public Long execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBMessageStore.this.dest, tx);
                        return sd.messageIdIndex.get(tx, key);
                    }
                });
            }
            if (location != null) {
                this.cursorPos = location + 1L;
            }
        }
        
        @Override
        public void setMemoryUsage(final MemoryUsage memoryUsage) {
        }
        
        @Override
        public void start() throws Exception {
        }
        
        @Override
        public void stop() throws Exception {
        }
    }
    
    class KahaDBTopicMessageStore extends KahaDBMessageStore implements TopicMessageStore
    {
        public KahaDBTopicMessageStore(final ActiveMQTopic destination) {
            super(destination);
        }
        
        @Override
        public void acknowledge(final ConnectionContext context, final String clientId, final String subscriptionName, final MessageId messageId, final MessageAck ack) throws IOException {
            final KahaRemoveMessageCommand command = new KahaRemoveMessageCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName));
            command.setMessageId(messageId.toProducerKey());
            TempKahaDBStore.this.processRemove(command, null);
        }
        
        @Override
        public void addSubscription(final SubscriptionInfo subscriptionInfo, final boolean retroactive) throws IOException {
            final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(subscriptionInfo.getClientId(), subscriptionInfo.getSubscriptionName());
            final KahaSubscriptionCommand command = new KahaSubscriptionCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(subscriptionKey);
            command.setRetroactive(retroactive);
            final ByteSequence packet = TempKahaDBStore.this.wireFormat.marshal(subscriptionInfo);
            command.setSubscriptionInfo(new Buffer(packet.getData(), packet.getOffset(), packet.getLength()));
            TempKahaDBStore.this.process(command);
        }
        
        @Override
        public void deleteSubscription(final String clientId, final String subscriptionName) throws IOException {
            final KahaSubscriptionCommand command = new KahaSubscriptionCommand();
            command.setDestination(this.dest);
            command.setSubscriptionKey(TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName));
            TempKahaDBStore.this.process(command);
        }
        
        @Override
        public SubscriptionInfo[] getAllSubscriptions() throws IOException {
            final ArrayList<SubscriptionInfo> subscriptions = new ArrayList<SubscriptionInfo>();
            synchronized (TempKahaDBStore.this.indexMutex) {
                TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                    @Override
                    public void execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final Iterator<Map.Entry<String, KahaSubscriptionCommand>> iterator = sd.subscriptions.iterator(tx);
                        while (iterator.hasNext()) {
                            final Map.Entry<String, KahaSubscriptionCommand> entry = iterator.next();
                            final SubscriptionInfo info = (SubscriptionInfo)TempKahaDBStore.this.wireFormat.unmarshal(new DataInputStream(entry.getValue().getSubscriptionInfo().newInput()));
                            subscriptions.add(info);
                        }
                    }
                });
            }
            final SubscriptionInfo[] rc = new SubscriptionInfo[subscriptions.size()];
            subscriptions.toArray(rc);
            return rc;
        }
        
        @Override
        public SubscriptionInfo lookupSubscription(final String clientId, final String subscriptionName) throws IOException {
            final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            synchronized (TempKahaDBStore.this.indexMutex) {
                return TempKahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<SubscriptionInfo, Throwable>)new Transaction.CallableClosure<SubscriptionInfo, IOException>() {
                    @Override
                    public SubscriptionInfo execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        final KahaSubscriptionCommand command = sd.subscriptions.get(tx, subscriptionKey);
                        if (command == null) {
                            return null;
                        }
                        return (SubscriptionInfo)TempKahaDBStore.this.wireFormat.unmarshal(new DataInputStream(command.getSubscriptionInfo().newInput()));
                    }
                });
            }
        }
        
        @Override
        public int getMessageCount(final String clientId, final String subscriptionName) throws IOException {
            final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            synchronized (TempKahaDBStore.this.indexMutex) {
                return TempKahaDBStore.this.pageFile.tx().execute((Transaction.CallableClosure<Integer, Throwable>)new Transaction.CallableClosure<Integer, IOException>() {
                    @Override
                    public Integer execute(final Transaction tx) throws IOException {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        Long cursorPos = sd.subscriptionAcks.get(tx, subscriptionKey);
                        if (cursorPos == null) {
                            return 0;
                        }
                        ++cursorPos;
                        int counter = 0;
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx, cursorPos);
                        while (iterator.hasNext()) {
                            iterator.next();
                            ++counter;
                        }
                        return counter;
                    }
                });
            }
        }
        
        @Override
        public void recoverSubscription(final String clientId, final String subscriptionName, final MessageRecoveryListener listener) throws Exception {
            final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            synchronized (TempKahaDBStore.this.indexMutex) {
                TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        Long cursorPos = sd.subscriptionAcks.get(tx, subscriptionKey);
                        ++cursorPos;
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx, cursorPos);
                        while (iterator.hasNext()) {
                            final Map.Entry<Long, MessageRecord> entry = iterator.next();
                            listener.recoverMessage((Message)TempKahaDBStore.this.wireFormat.unmarshal(entry.getValue().data));
                        }
                    }
                });
            }
        }
        
        @Override
        public void recoverNextMessages(final String clientId, final String subscriptionName, final int maxReturned, final MessageRecoveryListener listener) throws Exception {
            final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName);
            synchronized (TempKahaDBStore.this.indexMutex) {
                TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<Exception>() {
                    @Override
                    public void execute(final Transaction tx) throws Exception {
                        final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                        Long cursorPos = sd.subscriptionCursors.get(subscriptionKey);
                        if (cursorPos == null) {
                            cursorPos = sd.subscriptionAcks.get(tx, subscriptionKey);
                            ++cursorPos;
                        }
                        Map.Entry<Long, MessageRecord> entry = null;
                        int counter = 0;
                        final Iterator<Map.Entry<Long, MessageRecord>> iterator = sd.orderIndex.iterator(tx, cursorPos);
                        while (iterator.hasNext()) {
                            entry = iterator.next();
                            listener.recoverMessage((Message)TempKahaDBStore.this.wireFormat.unmarshal(entry.getValue().data));
                            if (++counter >= maxReturned) {
                                break;
                            }
                        }
                        if (entry != null) {
                            sd.subscriptionCursors.put(subscriptionKey, entry.getKey() + 1L);
                        }
                    }
                });
            }
        }
        
        @Override
        public void resetBatching(final String clientId, final String subscriptionName) {
            try {
                final String subscriptionKey = TempKahaDBStore.this.subscriptionKey(clientId, subscriptionName);
                synchronized (TempKahaDBStore.this.indexMutex) {
                    TempKahaDBStore.this.pageFile.tx().execute((Transaction.Closure<Throwable>)new Transaction.Closure<IOException>() {
                        @Override
                        public void execute(final Transaction tx) throws IOException {
                            final StoredDestination sd = TempKahaDBStore.this.getStoredDestination(KahaDBTopicMessageStore.this.dest, tx);
                            sd.subscriptionCursors.remove(subscriptionKey);
                        }
                    });
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
