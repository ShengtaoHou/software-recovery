// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.kahadb;

import org.apache.activemq.store.TransactionIdTransformer;
import org.apache.activemq.store.SharedFileLocker;
import org.apache.activemq.broker.Locker;
import org.apache.activemq.protobuf.Buffer;
import org.apache.activemq.store.kahadb.data.KahaXATransactionId;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.kahadb.data.KahaLocalTransactionId;
import org.apache.activemq.command.LocalTransactionId;
import org.apache.activemq.store.kahadb.data.KahaTransactionInfo;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.broker.BrokerService;
import java.io.File;
import javax.management.ObjectName;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.broker.jmx.AnnotatedMBean;
import org.apache.activemq.broker.jmx.BrokerMBeanSupport;
import java.util.concurrent.Callable;
import org.apache.activemq.broker.jmx.PersistenceAdapterView;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import java.io.IOException;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.store.TransactionIdTransformerAware;
import org.apache.activemq.store.JournaledStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.broker.LockableServiceSupport;

public class KahaDBPersistenceAdapter extends LockableServiceSupport implements PersistenceAdapter, JournaledStore, TransactionIdTransformerAware
{
    private final KahaDBStore letter;
    
    public KahaDBPersistenceAdapter() {
        this.letter = new KahaDBStore();
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        this.letter.beginTransaction(context);
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
        this.letter.checkpoint(sync);
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        this.letter.commitTransaction(context);
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        return this.letter.createQueueMessageStore(destination);
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        return this.letter.createTopicMessageStore(destination);
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        return this.letter.createTransactionStore();
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        this.letter.deleteAllMessages();
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        return this.letter.getDestinations();
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return this.letter.getLastMessageBrokerSequenceId();
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) throws IOException {
        return this.letter.getLastProducerSequenceId(id);
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
        this.letter.removeQueueMessageStore(destination);
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
        this.letter.removeTopicMessageStore(destination);
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        this.letter.rollbackTransaction(context);
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
        this.letter.setBrokerName(brokerName);
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
        this.letter.setUsageManager(usageManager);
    }
    
    @Override
    public long size() {
        return this.letter.size();
    }
    
    public void doStart() throws Exception {
        this.letter.start();
        if (this.brokerService != null && this.brokerService.isUseJmx()) {
            final PersistenceAdapterView view = new PersistenceAdapterView(this);
            view.setInflightTransactionViewCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return KahaDBPersistenceAdapter.this.letter.getTransactions();
                }
            });
            view.setDataViewCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return KahaDBPersistenceAdapter.this.letter.getJournal().getFileMap().keySet().toString();
                }
            });
            AnnotatedMBean.registerMBean(this.brokerService.getManagementContext(), view, BrokerMBeanSupport.createPersistenceAdapterName(this.brokerService.getBrokerObjectName().toString(), this.toString()));
        }
    }
    
    public void doStop(final ServiceStopper stopper) throws Exception {
        this.letter.stop();
        if (this.brokerService != null && this.brokerService.isUseJmx()) {
            final ObjectName brokerObjectName = this.brokerService.getBrokerObjectName();
            this.brokerService.getManagementContext().unregisterMBean(BrokerMBeanSupport.createPersistenceAdapterName(brokerObjectName.toString(), this.toString()));
        }
    }
    
    @Override
    public int getJournalMaxFileLength() {
        return this.letter.getJournalMaxFileLength();
    }
    
    public void setJournalMaxFileLength(final int journalMaxFileLength) {
        this.letter.setJournalMaxFileLength(journalMaxFileLength);
    }
    
    public void setMaxFailoverProducersToTrack(final int maxFailoverProducersToTrack) {
        this.letter.setMaxFailoverProducersToTrack(maxFailoverProducersToTrack);
    }
    
    public int getMaxFailoverProducersToTrack() {
        return this.letter.getMaxFailoverProducersToTrack();
    }
    
    public void setFailoverProducersAuditDepth(final int failoverProducersAuditDepth) {
        this.letter.setFailoverProducersAuditDepth(failoverProducersAuditDepth);
    }
    
    public int getFailoverProducersAuditDepth() {
        return this.letter.getFailoverProducersAuditDepth();
    }
    
    public long getCheckpointInterval() {
        return this.letter.getCheckpointInterval();
    }
    
    public void setCheckpointInterval(final long checkpointInterval) {
        this.letter.setCheckpointInterval(checkpointInterval);
    }
    
    public long getCleanupInterval() {
        return this.letter.getCleanupInterval();
    }
    
    public void setCleanupInterval(final long cleanupInterval) {
        this.letter.setCleanupInterval(cleanupInterval);
    }
    
    public int getIndexWriteBatchSize() {
        return this.letter.getIndexWriteBatchSize();
    }
    
    public void setIndexWriteBatchSize(final int indexWriteBatchSize) {
        this.letter.setIndexWriteBatchSize(indexWriteBatchSize);
    }
    
    public int getJournalMaxWriteBatchSize() {
        return this.letter.getJournalMaxWriteBatchSize();
    }
    
    public void setJournalMaxWriteBatchSize(final int journalMaxWriteBatchSize) {
        this.letter.setJournalMaxWriteBatchSize(journalMaxWriteBatchSize);
    }
    
    public boolean isEnableIndexWriteAsync() {
        return this.letter.isEnableIndexWriteAsync();
    }
    
    public void setEnableIndexWriteAsync(final boolean enableIndexWriteAsync) {
        this.letter.setEnableIndexWriteAsync(enableIndexWriteAsync);
    }
    
    @Override
    public File getDirectory() {
        return this.letter.getDirectory();
    }
    
    @Override
    public void setDirectory(final File dir) {
        this.letter.setDirectory(dir);
    }
    
    public boolean isEnableJournalDiskSyncs() {
        return this.letter.isEnableJournalDiskSyncs();
    }
    
    public void setEnableJournalDiskSyncs(final boolean enableJournalDiskSyncs) {
        this.letter.setEnableJournalDiskSyncs(enableJournalDiskSyncs);
    }
    
    public int getIndexCacheSize() {
        return this.letter.getIndexCacheSize();
    }
    
    public void setIndexCacheSize(final int indexCacheSize) {
        this.letter.setIndexCacheSize(indexCacheSize);
    }
    
    public boolean isIgnoreMissingJournalfiles() {
        return this.letter.isIgnoreMissingJournalfiles();
    }
    
    public void setIgnoreMissingJournalfiles(final boolean ignoreMissingJournalfiles) {
        this.letter.setIgnoreMissingJournalfiles(ignoreMissingJournalfiles);
    }
    
    public boolean isChecksumJournalFiles() {
        return this.letter.isChecksumJournalFiles();
    }
    
    public boolean isCheckForCorruptJournalFiles() {
        return this.letter.isCheckForCorruptJournalFiles();
    }
    
    public void setChecksumJournalFiles(final boolean checksumJournalFiles) {
        this.letter.setChecksumJournalFiles(checksumJournalFiles);
    }
    
    public void setCheckForCorruptJournalFiles(final boolean checkForCorruptJournalFiles) {
        this.letter.setCheckForCorruptJournalFiles(checkForCorruptJournalFiles);
    }
    
    @Override
    public void setBrokerService(final BrokerService brokerService) {
        super.setBrokerService(brokerService);
        this.letter.setBrokerService(brokerService);
    }
    
    public boolean isArchiveDataLogs() {
        return this.letter.isArchiveDataLogs();
    }
    
    public void setArchiveDataLogs(final boolean archiveDataLogs) {
        this.letter.setArchiveDataLogs(archiveDataLogs);
    }
    
    public File getDirectoryArchive() {
        return this.letter.getDirectoryArchive();
    }
    
    public void setDirectoryArchive(final File directoryArchive) {
        this.letter.setDirectoryArchive(directoryArchive);
    }
    
    public boolean isConcurrentStoreAndDispatchQueues() {
        return this.letter.isConcurrentStoreAndDispatchQueues();
    }
    
    public void setConcurrentStoreAndDispatchQueues(final boolean concurrentStoreAndDispatch) {
        this.letter.setConcurrentStoreAndDispatchQueues(concurrentStoreAndDispatch);
    }
    
    public boolean isConcurrentStoreAndDispatchTopics() {
        return this.letter.isConcurrentStoreAndDispatchTopics();
    }
    
    public void setConcurrentStoreAndDispatchTopics(final boolean concurrentStoreAndDispatch) {
        this.letter.setConcurrentStoreAndDispatchTopics(concurrentStoreAndDispatch);
    }
    
    public int getMaxAsyncJobs() {
        return this.letter.getMaxAsyncJobs();
    }
    
    public void setMaxAsyncJobs(final int maxAsyncJobs) {
        this.letter.setMaxAsyncJobs(maxAsyncJobs);
    }
    
    @Deprecated
    public void setDatabaseLockedWaitDelay(final int databaseLockedWaitDelay) throws IOException {
        this.getLocker().setLockAcquireSleepInterval(databaseLockedWaitDelay);
    }
    
    public boolean getForceRecoverIndex() {
        return this.letter.getForceRecoverIndex();
    }
    
    public void setForceRecoverIndex(final boolean forceRecoverIndex) {
        this.letter.setForceRecoverIndex(forceRecoverIndex);
    }
    
    public boolean isArchiveCorruptedIndex() {
        return this.letter.isArchiveCorruptedIndex();
    }
    
    public void setArchiveCorruptedIndex(final boolean archiveCorruptedIndex) {
        this.letter.setArchiveCorruptedIndex(archiveCorruptedIndex);
    }
    
    public float getIndexLFUEvictionFactor() {
        return this.letter.getIndexLFUEvictionFactor();
    }
    
    public void setIndexLFUEvictionFactor(final float indexLFUEvictionFactor) {
        this.letter.setIndexLFUEvictionFactor(indexLFUEvictionFactor);
    }
    
    public boolean isUseIndexLFRUEviction() {
        return this.letter.isUseIndexLFRUEviction();
    }
    
    public void setUseIndexLFRUEviction(final boolean useIndexLFRUEviction) {
        this.letter.setUseIndexLFRUEviction(useIndexLFRUEviction);
    }
    
    public void setEnableIndexDiskSyncs(final boolean diskSyncs) {
        this.letter.setEnableIndexDiskSyncs(diskSyncs);
    }
    
    public boolean isEnableIndexDiskSyncs() {
        return this.letter.isEnableIndexDiskSyncs();
    }
    
    public void setEnableIndexRecoveryFile(final boolean enable) {
        this.letter.setEnableIndexRecoveryFile(enable);
    }
    
    public boolean isEnableIndexRecoveryFile() {
        return this.letter.isEnableIndexRecoveryFile();
    }
    
    public void setEnableIndexPageCaching(final boolean enable) {
        this.letter.setEnableIndexPageCaching(enable);
    }
    
    public boolean isEnableIndexPageCaching() {
        return this.letter.isEnableIndexPageCaching();
    }
    
    public KahaDBStore getStore() {
        return this.letter;
    }
    
    public KahaTransactionInfo createTransactionInfo(final TransactionId txid) {
        if (txid == null) {
            return null;
        }
        final KahaTransactionInfo rc = new KahaTransactionInfo();
        if (txid.isLocalTransaction()) {
            final LocalTransactionId t = (LocalTransactionId)txid;
            final KahaLocalTransactionId kahaTxId = new KahaLocalTransactionId();
            kahaTxId.setConnectionId(t.getConnectionId().getValue());
            kahaTxId.setTransactionId(t.getValue());
            rc.setLocalTransactionId(kahaTxId);
        }
        else {
            final XATransactionId t2 = (XATransactionId)txid;
            final KahaXATransactionId kahaTxId2 = new KahaXATransactionId();
            kahaTxId2.setBranchQualifier(new Buffer(t2.getBranchQualifier()));
            kahaTxId2.setGlobalTransactionId(new Buffer(t2.getGlobalTransactionId()));
            kahaTxId2.setFormatId(t2.getFormatId());
            rc.setXaTransactionId(kahaTxId2);
        }
        return rc;
    }
    
    @Override
    public Locker createDefaultLocker() throws IOException {
        final SharedFileLocker locker = new SharedFileLocker();
        locker.configure(this);
        return locker;
    }
    
    @Override
    public void init() throws Exception {
    }
    
    @Override
    public String toString() {
        final String path = (this.getDirectory() != null) ? this.getDirectory().getAbsolutePath() : "DIRECTORY_NOT_SET";
        return "KahaDBPersistenceAdapter[" + path + "]";
    }
    
    @Override
    public void setTransactionIdTransformer(final TransactionIdTransformer transactionIdTransformer) {
        this.getStore().setTransactionIdTransformer(transactionIdTransformer);
    }
}
