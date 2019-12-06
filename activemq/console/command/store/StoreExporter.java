// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.console.command.store;

import java.io.DataOutput;
import org.fusesource.hawtbuf.DataByteArrayOutputStream;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.store.MessageStore;
import java.util.Iterator;
import org.apache.activemq.command.ActiveMQTopic;
import java.io.IOException;
import org.apache.activemq.console.command.store.proto.QueueEntryPB;
import org.apache.activemq.console.command.store.proto.MessagePB;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.store.MessageRecoveryListener;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import java.util.HashMap;
import org.apache.activemq.console.command.store.proto.QueuePB;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.broker.BrokerService;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.activemq.broker.BrokerFactory;
import java.net.URISyntaxException;
import org.apache.activemq.openwire.OpenWireFormat;
import org.fusesource.hawtbuf.AsciiBuffer;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.File;
import java.net.URI;

public class StoreExporter
{
    static final int OPENWIRE_VERSION = 8;
    static final boolean TIGHT_ENCODING = false;
    URI config;
    File file;
    private final ObjectMapper mapper;
    private final AsciiBuffer ds_kind;
    private final AsciiBuffer ptp_kind;
    private final AsciiBuffer codec_id;
    private final OpenWireFormat wireformat;
    
    public StoreExporter() throws URISyntaxException {
        this.mapper = new ObjectMapper();
        this.ds_kind = new AsciiBuffer("ds");
        this.ptp_kind = new AsciiBuffer("ptp");
        this.codec_id = new AsciiBuffer("openwire");
        this.wireformat = new OpenWireFormat();
        this.config = new URI("xbean:activemq.xml");
        this.wireformat.setCacheEnabled(false);
        this.wireformat.setTightEncodingEnabled(false);
        this.wireformat.setVersion(8);
    }
    
    public void execute() throws Exception {
        if (this.config == null) {
            throw new Exception("required --config option missing");
        }
        if (this.file == null) {
            throw new Exception("required --file option missing");
        }
        System.out.println("Loading: " + this.config);
        BrokerFactory.setStartDefault(false);
        final BrokerService broker = BrokerFactory.createBroker(this.config);
        BrokerFactory.resetStartDefault();
        final PersistenceAdapter store = broker.getPersistenceAdapter();
        System.out.println("Starting: " + store);
        store.start();
        try {
            final BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(this.file));
            try {
                this.export(store, fos);
            }
            finally {
                fos.close();
            }
        }
        finally {
            store.stop();
        }
    }
    
    void export(final PersistenceAdapter store, final BufferedOutputStream fos) throws Exception {
        final long[] messageKeyCounter = { 0L };
        final long[] containerKeyCounter = { 0L };
        final ExportStreamManager manager = new ExportStreamManager(fos, 1);
        final int[] preparedTxs = { 0 };
        store.createTransactionStore().recover(new TransactionRecoveryListener() {
            @Override
            public void recover(final XATransactionId xid, final Message[] addedMessages, final MessageAck[] aks) {
                final int[] val$preparedTxs = preparedTxs;
                final int n = 0;
                ++val$preparedTxs[n];
            }
        });
        if (preparedTxs[0] > 0) {
            throw new Exception("Cannot export a store with prepared XA transactions.  Please commit or rollback those transactions before attempting to export.");
        }
        for (final ActiveMQDestination odest : store.getDestinations()) {
            final long[] array = containerKeyCounter;
            final int n = 0;
            ++array[n];
            if (odest instanceof ActiveMQQueue) {
                final ActiveMQQueue dest = (ActiveMQQueue)odest;
                final MessageStore queue = store.createQueueMessageStore(dest);
                final QueuePB.Bean destRecord = new QueuePB.Bean();
                destRecord.setKey(containerKeyCounter[0]);
                destRecord.setBindingKind(this.ptp_kind);
                final long[] seqKeyCounter = { 0L };
                final HashMap<String, Object> jsonMap = new HashMap<String, Object>();
                jsonMap.put("@class", "queue_destination");
                jsonMap.put("name", dest.getQueueName());
                final String json = this.mapper.writeValueAsString((Object)jsonMap);
                System.out.println(json);
                destRecord.setBindingData(new UTF8Buffer(json));
                manager.store_queue(destRecord);
                queue.recover(new MessageRecoveryListener() {
                    @Override
                    public boolean hasSpace() {
                        return true;
                    }
                    
                    @Override
                    public boolean recoverMessageReference(final MessageId ref) throws Exception {
                        return true;
                    }
                    
                    @Override
                    public boolean isDuplicate(final MessageId ref) {
                        return false;
                    }
                    
                    @Override
                    public boolean recoverMessage(final Message message) throws IOException {
                        final long[] val$messageKeyCounter = messageKeyCounter;
                        final int n = 0;
                        ++val$messageKeyCounter[n];
                        final long[] val$seqKeyCounter = seqKeyCounter;
                        final int n2 = 0;
                        ++val$seqKeyCounter[n2];
                        final MessagePB.Bean messageRecord = StoreExporter.this.createMessagePB(message, messageKeyCounter[0]);
                        manager.store_message(messageRecord);
                        final QueueEntryPB.Bean entryRecord = StoreExporter.this.createQueueEntryPB(message, containerKeyCounter[0], seqKeyCounter[0], messageKeyCounter[0]);
                        manager.store_queue_entry(entryRecord);
                        return true;
                    }
                });
            }
            else {
                if (!(odest instanceof ActiveMQTopic)) {
                    continue;
                }
                final ActiveMQTopic dest2 = (ActiveMQTopic)odest;
                final TopicMessageStore topic = store.createTopicMessageStore(dest2);
                for (final SubscriptionInfo sub : topic.getAllSubscriptions()) {
                    final QueuePB.Bean destRecord2 = new QueuePB.Bean();
                    destRecord2.setKey(containerKeyCounter[0]);
                    destRecord2.setBindingKind(this.ds_kind);
                    final HashMap<String, Object> jsonMap2 = new HashMap<String, Object>();
                    jsonMap2.put("@class", "dsub_destination");
                    jsonMap2.put("name", sub.getClientId() + ":" + sub.getSubscriptionName());
                    final HashMap<String, Object> jsonTopic = new HashMap<String, Object>();
                    jsonTopic.put("name", dest2.getTopicName());
                    jsonMap2.put("topics", new Object[] { jsonTopic });
                    if (sub.getSelector() != null) {
                        jsonMap2.put("selector", sub.getSelector());
                    }
                    final String json2 = this.mapper.writeValueAsString((Object)jsonMap2);
                    System.out.println(json2);
                    destRecord2.setBindingData(new UTF8Buffer(json2));
                    manager.store_queue(destRecord2);
                    final long[] seqKeyCounter2 = { 0L };
                    topic.recoverSubscription(sub.getClientId(), sub.getSubscriptionName(), new MessageRecoveryListener() {
                        @Override
                        public boolean hasSpace() {
                            return true;
                        }
                        
                        @Override
                        public boolean recoverMessageReference(final MessageId ref) throws Exception {
                            return true;
                        }
                        
                        @Override
                        public boolean isDuplicate(final MessageId ref) {
                            return false;
                        }
                        
                        @Override
                        public boolean recoverMessage(final Message message) throws IOException {
                            final long[] val$messageKeyCounter = messageKeyCounter;
                            final int n = 0;
                            ++val$messageKeyCounter[n];
                            final long[] val$seqKeyCounter = seqKeyCounter2;
                            final int n2 = 0;
                            ++val$seqKeyCounter[n2];
                            final MessagePB.Bean messageRecord = StoreExporter.this.createMessagePB(message, messageKeyCounter[0]);
                            manager.store_message(messageRecord);
                            final QueueEntryPB.Bean entryRecord = StoreExporter.this.createQueueEntryPB(message, containerKeyCounter[0], seqKeyCounter2[0], messageKeyCounter[0]);
                            manager.store_queue_entry(entryRecord);
                            return true;
                        }
                    });
                }
            }
        }
        manager.finish();
    }
    
    private QueueEntryPB.Bean createQueueEntryPB(final Message message, final long queueKey, final long queueSeq, final long messageKey) {
        final QueueEntryPB.Bean entryRecord = new QueueEntryPB.Bean();
        entryRecord.setQueueKey(queueKey);
        entryRecord.setQueueSeq(queueSeq);
        entryRecord.setMessageKey(messageKey);
        entryRecord.setSize(message.getSize());
        if (message.getExpiration() != 0L) {
            entryRecord.setExpiration(message.getExpiration());
        }
        if (message.getRedeliveryCounter() != 0) {
            entryRecord.setRedeliveries(message.getRedeliveryCounter());
        }
        return entryRecord;
    }
    
    private MessagePB.Bean createMessagePB(final Message message, final long messageKey) throws IOException {
        final DataByteArrayOutputStream mos = new DataByteArrayOutputStream();
        mos.writeBoolean(false);
        mos.writeVarInt(8);
        this.wireformat.marshal(message, mos);
        final MessagePB.Bean messageRecord = new MessagePB.Bean();
        messageRecord.setCodec(this.codec_id);
        messageRecord.setMessageKey(messageKey);
        messageRecord.setSize(message.getSize());
        messageRecord.setValue(mos.toBuffer());
        return messageRecord;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final String file) {
        this.setFile(new File(file));
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public URI getConfig() {
        return this.config;
    }
    
    public void setConfig(final URI config) {
        this.config = config;
    }
}
