// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.filter.DestinationMap;
import org.apache.activemq.filter.AnyDestination;
import org.slf4j.LoggerFactory;
import org.apache.activemq.store.SharedFileLocker;
import org.apache.activemq.broker.Locker;
import org.apache.activemq.Service;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.util.IOExceptionSupport;
import java.util.Map;
import org.apache.activemq.util.IntrospectionSupport;
import java.util.HashMap;
import org.apache.activemq.broker.Lockable;
import org.apache.activemq.store.TransactionIdTransformerAware;
import java.io.FileFilter;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.ProducerId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import java.io.IOException;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Iterator;
import org.apache.activemq.filter.DestinationMapEntry;
import org.apache.activemq.command.XATransactionId;
import java.nio.charset.Charset;
import javax.transaction.xa.Xid;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.util.IOHelper;
import java.util.LinkedList;
import org.apache.activemq.store.TransactionIdTransformer;
import java.io.File;
import java.util.List;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.slf4j.Logger;
import org.apache.activemq.broker.BrokerServiceAware;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.broker.LockableServiceSupport;

public class MultiKahaDBPersistenceAdapter extends LockableServiceSupport implements PersistenceAdapter, BrokerServiceAware
{
    static final Logger LOG;
    static final ActiveMQDestination matchAll;
    final int LOCAL_FORMAT_ID_MAGIC;
    final DelegateDestinationMap destinationMap;
    BrokerService brokerService;
    List<PersistenceAdapter> adapters;
    private File directory;
    MultiKahaDBTransactionStore transactionStore;
    TransactionIdTransformer transactionIdTransformer;
    
    public MultiKahaDBPersistenceAdapter() {
        this.LOCAL_FORMAT_ID_MAGIC = Integer.valueOf(System.getProperty("org.apache.activemq.store.kahadb.MultiKahaDBTransactionStore.localXaFormatId", "61616"));
        this.destinationMap = new DelegateDestinationMap();
        this.adapters = new LinkedList<PersistenceAdapter>();
        this.directory = new File(IOHelper.getDefaultDataDirectory() + File.separator + "mKahaDB");
        this.transactionStore = new MultiKahaDBTransactionStore(this);
        this.transactionIdTransformer = new TransactionIdTransformer() {
            @Override
            public TransactionId transform(final TransactionId txid) {
                if (txid == null) {
                    return null;
                }
                if (txid.isLocalTransaction()) {
                    final LocalTransactionId t = (LocalTransactionId)txid;
                    return new XATransactionId(new Xid() {
                        @Override
                        public int getFormatId() {
                            return MultiKahaDBPersistenceAdapter.this.LOCAL_FORMAT_ID_MAGIC;
                        }
                        
                        @Override
                        public byte[] getGlobalTransactionId() {
                            return t.getConnectionId().getValue().getBytes(Charset.forName("utf-8"));
                        }
                        
                        @Override
                        public byte[] getBranchQualifier() {
                            return Long.toString(t.getValue()).getBytes(Charset.forName("utf-8"));
                        }
                    });
                }
                return txid;
            }
        };
    }
    
    public void setFilteredPersistenceAdapters(final List entries) {
        for (final Object entry : entries) {
            final FilteredKahaDBPersistenceAdapter filteredAdapter = (FilteredKahaDBPersistenceAdapter)entry;
            final PersistenceAdapter adapter = filteredAdapter.getPersistenceAdapter();
            if (filteredAdapter.getDestination() == null) {
                filteredAdapter.setDestination(MultiKahaDBPersistenceAdapter.matchAll);
            }
            if (filteredAdapter.isPerDestination()) {
                this.configureDirectory(adapter, null);
            }
            else {
                this.configureDirectory(adapter, this.nameFromDestinationFilter(filteredAdapter.getDestination()));
                this.configureAdapter(adapter);
                this.adapters.add(adapter);
            }
        }
        this.destinationMap.setEntries(entries);
    }
    
    private String nameFromDestinationFilter(final ActiveMQDestination destination) {
        if (destination.getQualifiedName().length() > IOHelper.getMaxFileNameLength()) {
            MultiKahaDBPersistenceAdapter.LOG.warn("Destination name is longer than 'MaximumFileNameLength' system property, potential problem with recovery can result from name truncation.");
        }
        return IOHelper.toFileSystemSafeName(destination.getQualifiedName());
    }
    
    public boolean isLocalXid(final TransactionId xid) {
        return xid instanceof XATransactionId && ((XATransactionId)xid).getFormatId() == this.LOCAL_FORMAT_ID_MAGIC;
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        throw new IllegalStateException();
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            persistenceAdapter.checkpoint(sync);
        }
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        throw new IllegalStateException();
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        final PersistenceAdapter persistenceAdapter = this.getMatchingPersistenceAdapter(destination);
        return this.transactionStore.proxy(persistenceAdapter.createTransactionStore(), persistenceAdapter.createQueueMessageStore(destination));
    }
    
    private PersistenceAdapter getMatchingPersistenceAdapter(final ActiveMQDestination destination) throws IOException {
        final Object result = this.destinationMap.chooseValue(destination);
        if (result == null) {
            throw new RuntimeException("No matching persistence adapter configured for destination: " + destination + ", options:" + this.adapters);
        }
        FilteredKahaDBPersistenceAdapter filteredAdapter = (FilteredKahaDBPersistenceAdapter)result;
        if (filteredAdapter.getDestination() == MultiKahaDBPersistenceAdapter.matchAll && filteredAdapter.isPerDestination()) {
            filteredAdapter = this.addAdapter(filteredAdapter, destination);
            if (MultiKahaDBPersistenceAdapter.LOG.isTraceEnabled()) {
                MultiKahaDBPersistenceAdapter.LOG.info("created per destination adapter for: " + destination + ", " + result);
            }
        }
        this.startAdapter(filteredAdapter.getPersistenceAdapter(), destination.getQualifiedName());
        MultiKahaDBPersistenceAdapter.LOG.debug("destination {} matched persistence adapter {}", new Object[] { destination.getQualifiedName(), filteredAdapter.getPersistenceAdapter() });
        return filteredAdapter.getPersistenceAdapter();
    }
    
    private void startAdapter(final PersistenceAdapter kahaDBPersistenceAdapter, final String destination) {
        try {
            kahaDBPersistenceAdapter.start();
        }
        catch (Exception e) {
            final RuntimeException detail = new RuntimeException("Failed to start per destination persistence adapter for destination: " + destination + ", options:" + this.adapters, e);
            MultiKahaDBPersistenceAdapter.LOG.error(detail.toString(), e);
            throw detail;
        }
    }
    
    private void stopAdapter(final PersistenceAdapter kahaDBPersistenceAdapter, final String destination) {
        try {
            kahaDBPersistenceAdapter.stop();
        }
        catch (Exception e) {
            final RuntimeException detail = new RuntimeException("Failed to stop per destination persistence adapter for destination: " + destination + ", options:" + this.adapters, e);
            MultiKahaDBPersistenceAdapter.LOG.error(detail.toString(), e);
            throw detail;
        }
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        final PersistenceAdapter persistenceAdapter = this.getMatchingPersistenceAdapter(destination);
        return this.transactionStore.proxy(persistenceAdapter.createTransactionStore(), persistenceAdapter.createTopicMessageStore(destination));
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        return this.transactionStore;
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            persistenceAdapter.deleteAllMessages();
        }
        this.transactionStore.deleteAllMessages();
        IOHelper.deleteChildren(this.getDirectory());
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        final Set<ActiveMQDestination> results = new HashSet<ActiveMQDestination>();
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            results.addAll(persistenceAdapter.getDestinations());
        }
        return results;
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        long maxId = -1L;
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            maxId = Math.max(maxId, persistenceAdapter.getLastMessageBrokerSequenceId());
        }
        return maxId;
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) throws IOException {
        long maxId = -1L;
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            maxId = Math.max(maxId, persistenceAdapter.getLastProducerSequenceId(id));
        }
        return maxId;
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
        PersistenceAdapter adapter = null;
        try {
            adapter = this.getMatchingPersistenceAdapter(destination);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (adapter instanceof PersistenceAdapter) {
            adapter.removeQueueMessageStore(destination);
            this.removeMessageStore(adapter, destination);
            this.destinationMap.removeAll(destination);
        }
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
        PersistenceAdapter adapter = null;
        try {
            adapter = this.getMatchingPersistenceAdapter(destination);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (adapter instanceof PersistenceAdapter) {
            adapter.removeTopicMessageStore(destination);
            this.removeMessageStore(adapter, destination);
            this.destinationMap.removeAll(destination);
        }
    }
    
    private void removeMessageStore(final PersistenceAdapter adapter, final ActiveMQDestination destination) {
        if (adapter.getDestinations().isEmpty()) {
            this.stopAdapter(adapter, destination.toString());
            final File adapterDir = adapter.getDirectory();
            if (adapterDir != null) {
                if (IOHelper.deleteFile(adapterDir)) {
                    if (MultiKahaDBPersistenceAdapter.LOG.isTraceEnabled()) {
                        MultiKahaDBPersistenceAdapter.LOG.info("deleted per destination adapter directory for: " + destination);
                    }
                }
                else if (MultiKahaDBPersistenceAdapter.LOG.isTraceEnabled()) {
                    MultiKahaDBPersistenceAdapter.LOG.info("failed to deleted per destination adapter directory for: " + destination);
                }
            }
        }
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        throw new IllegalStateException();
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            persistenceAdapter.setBrokerName(brokerName);
        }
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            persistenceAdapter.setUsageManager(usageManager);
        }
    }
    
    @Override
    public long size() {
        long size = 0L;
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            size += persistenceAdapter.size();
        }
        return size;
    }
    
    public void doStart() throws Exception {
        final Object result = this.destinationMap.chooseValue(MultiKahaDBPersistenceAdapter.matchAll);
        if (result != null) {
            final FilteredKahaDBPersistenceAdapter filteredAdapter = (FilteredKahaDBPersistenceAdapter)result;
            if (filteredAdapter.getDestination() == MultiKahaDBPersistenceAdapter.matchAll && filteredAdapter.isPerDestination()) {
                this.findAndRegisterExistingAdapters(filteredAdapter);
            }
        }
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            persistenceAdapter.start();
        }
    }
    
    private void findAndRegisterExistingAdapters(final FilteredKahaDBPersistenceAdapter template) throws IOException {
        final FileFilter destinationNames = new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.getName().startsWith("queue#") || file.getName().startsWith("topic#");
            }
        };
        final File[] candidates = template.getPersistenceAdapter().getDirectory().listFiles(destinationNames);
        if (candidates != null) {
            for (final File candidate : candidates) {
                this.registerExistingAdapter(template, candidate);
            }
        }
    }
    
    private void registerExistingAdapter(final FilteredKahaDBPersistenceAdapter filteredAdapter, final File candidate) throws IOException {
        final PersistenceAdapter adapter = this.adapterFromTemplate(filteredAdapter.getPersistenceAdapter(), candidate.getName());
        this.startAdapter(adapter, candidate.getName());
        final Set<ActiveMQDestination> destinations = adapter.getDestinations();
        if (destinations.size() != 0) {
            this.registerAdapter(adapter, destinations.toArray(new ActiveMQDestination[0])[0]);
        }
        else {
            this.stopAdapter(adapter, candidate.getName());
        }
    }
    
    private FilteredKahaDBPersistenceAdapter addAdapter(final FilteredKahaDBPersistenceAdapter filteredAdapter, final ActiveMQDestination destination) throws IOException {
        final PersistenceAdapter adapter = this.adapterFromTemplate(filteredAdapter.getPersistenceAdapter(), this.nameFromDestinationFilter(destination));
        return this.registerAdapter(adapter, destination);
    }
    
    private PersistenceAdapter adapterFromTemplate(final PersistenceAdapter template, final String destinationName) throws IOException {
        final PersistenceAdapter adapter = this.kahaDBFromTemplate(template);
        this.configureAdapter(adapter);
        this.configureDirectory(adapter, destinationName);
        return adapter;
    }
    
    private void configureDirectory(final PersistenceAdapter adapter, final String fileName) {
        File directory = null;
        File defaultDir = MessageDatabase.DEFAULT_DIRECTORY;
        try {
            defaultDir = ((PersistenceAdapter)adapter.getClass().newInstance()).getDirectory();
        }
        catch (Exception ex) {}
        if (defaultDir.equals(adapter.getDirectory())) {
            directory = this.getDirectory();
        }
        else {
            directory = adapter.getDirectory();
        }
        if (fileName != null) {
            directory = new File(directory, fileName);
        }
        adapter.setDirectory(directory);
    }
    
    private FilteredKahaDBPersistenceAdapter registerAdapter(final PersistenceAdapter adapter, final ActiveMQDestination destination) {
        this.adapters.add(adapter);
        final FilteredKahaDBPersistenceAdapter result = new FilteredKahaDBPersistenceAdapter(destination, adapter);
        this.destinationMap.put(destination, result);
        return result;
    }
    
    private void configureAdapter(final PersistenceAdapter adapter) {
        ((TransactionIdTransformerAware)adapter).setTransactionIdTransformer(this.transactionIdTransformer);
        if (this.isUseLock() && adapter instanceof Lockable) {
            ((Lockable)adapter).setUseLock(false);
        }
        if (adapter instanceof BrokerServiceAware) {
            ((BrokerServiceAware)adapter).setBrokerService(this.getBrokerService());
        }
    }
    
    private PersistenceAdapter kahaDBFromTemplate(final PersistenceAdapter template) throws IOException {
        try {
            final Map<String, Object> configuration = new HashMap<String, Object>();
            IntrospectionSupport.getProperties(template, configuration, null);
            final PersistenceAdapter adapter = (PersistenceAdapter)template.getClass().newInstance();
            IntrospectionSupport.setProperties(adapter, configuration);
            return adapter;
        }
        catch (Exception e) {
            throw IOExceptionSupport.create(e);
        }
    }
    
    @Override
    protected void doStop(final ServiceStopper stopper) throws Exception {
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            stopper.stop(persistenceAdapter);
        }
    }
    
    @Override
    public File getDirectory() {
        return this.directory;
    }
    
    @Override
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    @Override
    public void init() throws Exception {
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        this.brokerService = brokerService;
        for (final PersistenceAdapter persistenceAdapter : this.adapters) {
            if (persistenceAdapter instanceof BrokerServiceAware) {
                ((BrokerServiceAware)persistenceAdapter).setBrokerService(this.getBrokerService());
            }
        }
    }
    
    @Override
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    public void setTransactionStore(final MultiKahaDBTransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }
    
    public void setJournalMaxFileLength(final int maxFileLength) {
        this.transactionStore.setJournalMaxFileLength(maxFileLength);
    }
    
    public int getJournalMaxFileLength() {
        return this.transactionStore.getJournalMaxFileLength();
    }
    
    public void setJournalWriteBatchSize(final int journalWriteBatchSize) {
        this.transactionStore.setJournalMaxWriteBatchSize(journalWriteBatchSize);
    }
    
    public int getJournalWriteBatchSize() {
        return this.transactionStore.getJournalMaxWriteBatchSize();
    }
    
    @Override
    public String toString() {
        final String path = (this.getDirectory() != null) ? this.getDirectory().getAbsolutePath() : "DIRECTORY_NOT_SET";
        return "MultiKahaDBPersistenceAdapter[" + path + "]" + this.adapters;
    }
    
    @Override
    public Locker createDefaultLocker() throws IOException {
        final SharedFileLocker locker = new SharedFileLocker();
        locker.configure(this);
        return locker;
    }
    
    static {
        LOG = LoggerFactory.getLogger(MultiKahaDBPersistenceAdapter.class);
        matchAll = new AnyDestination(new ActiveMQDestination[] { new ActiveMQQueue(">"), new ActiveMQTopic(">") });
    }
    
    final class DelegateDestinationMap extends DestinationMap
    {
        public void setEntries(final List<DestinationMapEntry> entries) {
            super.setEntries(entries);
        }
    }
}
