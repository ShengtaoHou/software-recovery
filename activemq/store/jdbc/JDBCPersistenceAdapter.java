// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.store.jdbc;

import org.slf4j.LoggerFactory;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.command.MessageAck;
import java.sql.Connection;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.ConnectionContext;
import java.util.Locale;
import org.apache.activemq.store.jdbc.adapter.DefaultJDBCAdapter;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.Locker;
import java.util.concurrent.ThreadFactory;
import org.apache.activemq.util.ServiceStopper;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.command.Message;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.MessageId;
import java.util.Collections;
import java.sql.SQLException;
import java.io.IOException;
import org.apache.activemq.command.ActiveMQDestination;
import java.util.Set;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.ActiveMQMessageAudit;
import java.io.File;
import javax.sql.DataSource;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.activemq.store.memory.MemoryTransactionStore;
import org.apache.activemq.wireformat.WireFormat;
import org.apache.activemq.util.FactoryFinder;
import org.slf4j.Logger;
import org.apache.activemq.store.PersistenceAdapter;

public class JDBCPersistenceAdapter extends DataSourceServiceSupport implements PersistenceAdapter
{
    private static final Logger LOG;
    private static FactoryFinder adapterFactoryFinder;
    private static FactoryFinder lockFactoryFinder;
    public static final long DEFAULT_LOCK_KEEP_ALIVE_PERIOD = 30000L;
    private WireFormat wireFormat;
    private Statements statements;
    private JDBCAdapter adapter;
    private MemoryTransactionStore transactionStore;
    private ScheduledThreadPoolExecutor clockDaemon;
    private ScheduledFuture<?> cleanupTicket;
    private int cleanupPeriod;
    private boolean useExternalMessageReferences;
    private boolean createTablesOnStartup;
    private DataSource lockDataSource;
    private int transactionIsolation;
    private File directory;
    private boolean changeAutoCommitAllowed;
    protected int maxProducersToAudit;
    protected int maxAuditDepth;
    protected boolean enableAudit;
    protected int auditRecoveryDepth;
    protected ActiveMQMessageAudit audit;
    protected LongSequenceGenerator sequenceGenerator;
    protected int maxRows;
    
    public JDBCPersistenceAdapter() {
        this.wireFormat = new OpenWireFormat();
        this.cleanupPeriod = 300000;
        this.createTablesOnStartup = true;
        this.changeAutoCommitAllowed = true;
        this.maxProducersToAudit = 1024;
        this.maxAuditDepth = 1000;
        this.enableAudit = false;
        this.auditRecoveryDepth = 1024;
        this.sequenceGenerator = new LongSequenceGenerator();
        this.maxRows = 32767;
        this.setLockKeepAlivePeriod(30000L);
    }
    
    public JDBCPersistenceAdapter(final DataSource ds, final WireFormat wireFormat) {
        super(ds);
        this.wireFormat = new OpenWireFormat();
        this.cleanupPeriod = 300000;
        this.createTablesOnStartup = true;
        this.changeAutoCommitAllowed = true;
        this.maxProducersToAudit = 1024;
        this.maxAuditDepth = 1000;
        this.enableAudit = false;
        this.auditRecoveryDepth = 1024;
        this.sequenceGenerator = new LongSequenceGenerator();
        this.maxRows = 32767;
        this.setLockKeepAlivePeriod(30000L);
        this.wireFormat = wireFormat;
    }
    
    @Override
    public Set<ActiveMQDestination> getDestinations() {
        TransactionContext c = null;
        try {
            c = this.getTransactionContext();
            return this.getAdapter().doGetDestinations(c);
        }
        catch (IOException e2) {
            return this.emptyDestinationSet();
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            return this.emptyDestinationSet();
        }
        finally {
            if (c != null) {
                try {
                    c.close();
                }
                catch (Throwable t) {}
            }
        }
    }
    
    private Set<ActiveMQDestination> emptyDestinationSet() {
        return (Set<ActiveMQDestination>)Collections.EMPTY_SET;
    }
    
    protected void createMessageAudit() {
        if (this.enableAudit && this.audit == null) {
            this.audit = new ActiveMQMessageAudit(this.maxAuditDepth, this.maxProducersToAudit);
            TransactionContext c = null;
            try {
                c = this.getTransactionContext();
                this.getAdapter().doMessageIdScan(c, this.auditRecoveryDepth, new JDBCMessageIdScanListener() {
                    @Override
                    public void messageId(final MessageId id) {
                        JDBCPersistenceAdapter.this.audit.isDuplicate(id);
                    }
                });
            }
            catch (Exception e) {
                JDBCPersistenceAdapter.LOG.error("Failed to reload store message audit for JDBC persistence adapter", e);
            }
            finally {
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (Throwable t) {}
                }
            }
        }
    }
    
    public void initSequenceIdGenerator() {
        TransactionContext c = null;
        try {
            c = this.getTransactionContext();
            this.getAdapter().doMessageIdScan(c, this.auditRecoveryDepth, new JDBCMessageIdScanListener() {
                @Override
                public void messageId(final MessageId id) {
                    JDBCPersistenceAdapter.this.audit.isDuplicate(id);
                }
            });
        }
        catch (Exception e) {
            JDBCPersistenceAdapter.LOG.error("Failed to reload store message audit for JDBC persistence adapter", e);
        }
        finally {
            if (c != null) {
                try {
                    c.close();
                }
                catch (Throwable t) {}
            }
        }
    }
    
    @Override
    public MessageStore createQueueMessageStore(final ActiveMQQueue destination) throws IOException {
        MessageStore rc = new JDBCMessageStore(this, this.getAdapter(), this.wireFormat, destination, this.audit);
        if (this.transactionStore != null) {
            rc = this.transactionStore.proxy(rc);
        }
        return rc;
    }
    
    @Override
    public TopicMessageStore createTopicMessageStore(final ActiveMQTopic destination) throws IOException {
        TopicMessageStore rc = new JDBCTopicMessageStore(this, this.getAdapter(), this.wireFormat, destination, this.audit);
        if (this.transactionStore != null) {
            rc = this.transactionStore.proxy(rc);
        }
        return rc;
    }
    
    @Override
    public void removeQueueMessageStore(final ActiveMQQueue destination) {
        if (destination.isQueue() && this.getBrokerService().shouldRecordVirtualDestination(destination)) {
            try {
                this.removeConsumerDestination(destination);
            }
            catch (IOException ioe) {
                JDBCPersistenceAdapter.LOG.error("Failed to remove consumer destination: " + destination, ioe);
            }
        }
    }
    
    private void removeConsumerDestination(final ActiveMQQueue destination) throws IOException {
        final TransactionContext c = this.getTransactionContext();
        try {
            final String id = destination.getQualifiedName();
            this.getAdapter().doDeleteSubscription(c, destination, id, id);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to remove consumer destination: " + destination, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public void removeTopicMessageStore(final ActiveMQTopic destination) {
    }
    
    @Override
    public TransactionStore createTransactionStore() throws IOException {
        if (this.transactionStore == null) {
            this.transactionStore = new JdbcMemoryTransactionStore(this);
        }
        return this.transactionStore;
    }
    
    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        final TransactionContext c = this.getTransactionContext();
        try {
            final long seq = this.getAdapter().doGetLastMessageStoreSequenceId(c);
            this.sequenceGenerator.setLastSequenceId(seq);
            long brokerSeq = 0L;
            if (seq != 0L) {
                final byte[] msg = this.getAdapter().doGetMessageById(c, seq);
                if (msg != null) {
                    final Message last = (Message)this.wireFormat.unmarshal(new ByteSequence(msg));
                    brokerSeq = last.getMessageId().getBrokerSequenceId();
                }
                else {
                    JDBCPersistenceAdapter.LOG.warn("Broker sequence id wasn't recovered properly, possible duplicates!");
                }
            }
            return brokerSeq;
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to get last broker message id: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public long getLastProducerSequenceId(final ProducerId id) throws IOException {
        final TransactionContext c = this.getTransactionContext();
        try {
            return this.getAdapter().doGetLastProducerSequenceId(c, id);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to get last broker message id: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    @Override
    public void init() throws Exception {
        this.getAdapter().setUseExternalMessageReferences(this.isUseExternalMessageReferences());
        if (this.isCreateTablesOnStartup()) {
            final TransactionContext transactionContext = this.getTransactionContext();
            transactionContext.begin();
            try {
                this.getAdapter().doCreateTables(transactionContext);
            }
            catch (SQLException e) {
                JDBCPersistenceAdapter.LOG.warn("Cannot create tables due to: " + e);
                log("Failure Details: ", e);
            }
            finally {
                transactionContext.commit();
            }
        }
    }
    
    public void doStart() throws Exception {
        if (this.brokerService != null) {
            this.wireFormat.setVersion(this.brokerService.getStoreOpenWireVersion());
        }
        if (this.cleanupPeriod > 0) {
            this.cleanupTicket = this.getScheduledThreadPoolExecutor().scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    JDBCPersistenceAdapter.this.cleanup();
                }
            }, 0L, this.cleanupPeriod, TimeUnit.MILLISECONDS);
        }
        this.createMessageAudit();
    }
    
    public synchronized void doStop(final ServiceStopper stopper) throws Exception {
        if (this.cleanupTicket != null) {
            this.cleanupTicket.cancel(true);
            this.cleanupTicket = null;
        }
    }
    
    public void cleanup() {
        TransactionContext c = null;
        try {
            JDBCPersistenceAdapter.LOG.debug("Cleaning up old messages.");
            c = this.getTransactionContext();
            this.getAdapter().doDeleteOldMessages(c);
        }
        catch (IOException e) {
            JDBCPersistenceAdapter.LOG.warn("Old message cleanup failed due to: " + e, e);
        }
        catch (SQLException e2) {
            JDBCPersistenceAdapter.LOG.warn("Old message cleanup failed due to: " + e2);
            log("Failure Details: ", e2);
        }
        finally {
            if (c != null) {
                try {
                    c.close();
                }
                catch (Throwable t) {}
            }
            JDBCPersistenceAdapter.LOG.debug("Cleanup done.");
        }
    }
    
    public void setScheduledThreadPoolExecutor(final ScheduledThreadPoolExecutor clockDaemon) {
        this.clockDaemon = clockDaemon;
    }
    
    @Override
    public ScheduledThreadPoolExecutor getScheduledThreadPoolExecutor() {
        if (this.clockDaemon == null) {
            this.clockDaemon = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
                @Override
                public Thread newThread(final Runnable runnable) {
                    final Thread thread = new Thread(runnable, "ActiveMQ JDBC PA Scheduled Task");
                    thread.setDaemon(true);
                    return thread;
                }
            });
        }
        return this.clockDaemon;
    }
    
    public JDBCAdapter getAdapter() throws IOException {
        if (this.adapter == null) {
            this.setAdapter(this.createAdapter());
        }
        return this.adapter;
    }
    
    @Deprecated
    public Locker getDatabaseLocker() throws IOException {
        return this.getLocker();
    }
    
    @Deprecated
    public void setDatabaseLocker(final Locker locker) throws IOException {
        this.setLocker(locker);
    }
    
    public DataSource getLockDataSource() throws IOException {
        if (this.lockDataSource == null) {
            this.lockDataSource = this.getDataSource();
            if (this.lockDataSource == null) {
                throw new IllegalArgumentException("No dataSource property has been configured");
            }
        }
        else {
            JDBCPersistenceAdapter.LOG.info("Using a separate dataSource for locking: " + this.lockDataSource);
        }
        return this.lockDataSource;
    }
    
    public void setLockDataSource(final DataSource dataSource) {
        this.lockDataSource = dataSource;
    }
    
    @Override
    public BrokerService getBrokerService() {
        return this.brokerService;
    }
    
    protected JDBCAdapter createAdapter() throws IOException {
        this.adapter = (JDBCAdapter)this.loadAdapter(JDBCPersistenceAdapter.adapterFactoryFinder, "adapter");
        if (this.adapter == null) {
            this.adapter = new DefaultJDBCAdapter();
            JDBCPersistenceAdapter.LOG.debug("Using default JDBC Adapter: " + this.adapter);
        }
        return this.adapter;
    }
    
    private Object loadAdapter(final FactoryFinder finder, final String kind) throws IOException {
        Object adapter = null;
        final TransactionContext c = this.getTransactionContext();
        try {
            String dirverName = c.getConnection().getMetaData().getDriverName();
            dirverName = dirverName.replaceAll("[^a-zA-Z0-9\\-]", "_").toLowerCase(Locale.ENGLISH);
            try {
                adapter = finder.newInstance(dirverName);
                JDBCPersistenceAdapter.LOG.info("Database " + kind + " driver override recognized for : [" + dirverName + "] - adapter: " + adapter.getClass());
            }
            catch (Throwable e2) {
                JDBCPersistenceAdapter.LOG.info("Database " + kind + " driver override not found for : [" + dirverName + "].  Will use default implementation.");
            }
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.LOG.warn("JDBC error occurred while trying to detect database type for overrides. Will use default implementations: " + e.getMessage());
            log("Failure Details: ", e);
        }
        finally {
            c.close();
        }
        return adapter;
    }
    
    public void setAdapter(final JDBCAdapter adapter) {
        (this.adapter = adapter).setStatements(this.getStatements());
        this.adapter.setMaxRows(this.getMaxRows());
    }
    
    public WireFormat getWireFormat() {
        return this.wireFormat;
    }
    
    public void setWireFormat(final WireFormat wireFormat) {
        this.wireFormat = wireFormat;
    }
    
    public TransactionContext getTransactionContext(final ConnectionContext context) throws IOException {
        if (context == null) {
            return this.getTransactionContext();
        }
        TransactionContext answer = (TransactionContext)context.getLongTermStoreContext();
        if (answer == null) {
            answer = this.getTransactionContext();
            context.setLongTermStoreContext(answer);
        }
        return answer;
    }
    
    public TransactionContext getTransactionContext() throws IOException {
        final TransactionContext answer = new TransactionContext(this);
        if (this.transactionIsolation > 0) {
            answer.setTransactionIsolation(this.transactionIsolation);
        }
        return answer;
    }
    
    @Override
    public void beginTransaction(final ConnectionContext context) throws IOException {
        final TransactionContext transactionContext = this.getTransactionContext(context);
        transactionContext.begin();
    }
    
    @Override
    public void commitTransaction(final ConnectionContext context) throws IOException {
        final TransactionContext transactionContext = this.getTransactionContext(context);
        transactionContext.commit();
    }
    
    @Override
    public void rollbackTransaction(final ConnectionContext context) throws IOException {
        final TransactionContext transactionContext = this.getTransactionContext(context);
        transactionContext.rollback();
    }
    
    public int getCleanupPeriod() {
        return this.cleanupPeriod;
    }
    
    public void setCleanupPeriod(final int cleanupPeriod) {
        this.cleanupPeriod = cleanupPeriod;
    }
    
    public boolean isChangeAutoCommitAllowed() {
        return this.changeAutoCommitAllowed;
    }
    
    public void setChangeAutoCommitAllowed(final boolean changeAutoCommitAllowed) {
        this.changeAutoCommitAllowed = changeAutoCommitAllowed;
    }
    
    @Override
    public void deleteAllMessages() throws IOException {
        final TransactionContext c = this.getTransactionContext();
        try {
            this.getAdapter().doDropTables(c);
            this.getAdapter().setUseExternalMessageReferences(this.isUseExternalMessageReferences());
            this.getAdapter().doCreateTables(c);
            JDBCPersistenceAdapter.LOG.info("Persistence store purged.");
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create(e);
        }
        finally {
            c.close();
        }
    }
    
    public boolean isUseExternalMessageReferences() {
        return this.useExternalMessageReferences;
    }
    
    public void setUseExternalMessageReferences(final boolean useExternalMessageReferences) {
        this.useExternalMessageReferences = useExternalMessageReferences;
    }
    
    public boolean isCreateTablesOnStartup() {
        return this.createTablesOnStartup;
    }
    
    public void setCreateTablesOnStartup(final boolean createTablesOnStartup) {
        this.createTablesOnStartup = createTablesOnStartup;
    }
    
    @Deprecated
    public void setUseDatabaseLock(final boolean useDatabaseLock) {
        this.setUseLock(useDatabaseLock);
    }
    
    public static void log(final String msg, SQLException e) {
        String s;
        for (s = msg + e.getMessage(); e.getNextException() != null; e = e.getNextException(), s = s + ", due to: " + e.getMessage()) {}
        JDBCPersistenceAdapter.LOG.warn(s, e);
    }
    
    public Statements getStatements() {
        if (this.statements == null) {
            this.statements = new Statements();
        }
        return this.statements;
    }
    
    public void setStatements(final Statements statements) {
        this.statements = statements;
        if (this.adapter != null) {
            this.adapter.setStatements(this.getStatements());
        }
    }
    
    @Override
    public void setUsageManager(final SystemUsage usageManager) {
    }
    
    @Override
    public Locker createDefaultLocker() throws IOException {
        Locker locker = (Locker)this.loadAdapter(JDBCPersistenceAdapter.lockFactoryFinder, "lock");
        if (locker == null) {
            locker = new DefaultDatabaseLocker();
            JDBCPersistenceAdapter.LOG.debug("Using default JDBC Locker: " + locker);
        }
        locker.configure(this);
        return locker;
    }
    
    @Override
    public void setBrokerName(final String brokerName) {
    }
    
    @Override
    public String toString() {
        return "JDBCPersistenceAdapter(" + super.toString() + ")";
    }
    
    @Override
    public void setDirectory(final File dir) {
        this.directory = dir;
    }
    
    @Override
    public File getDirectory() {
        if (this.directory == null && this.brokerService != null) {
            this.directory = this.brokerService.getBrokerDataDirectory();
        }
        return this.directory;
    }
    
    @Override
    public void checkpoint(final boolean sync) throws IOException {
        Connection connection = null;
        try {
            connection = this.getDataSource().getConnection();
            if (!connection.isValid(10)) {
                throw new IOException("isValid(10) failed for: " + connection);
            }
        }
        catch (SQLException e) {
            JDBCPersistenceAdapter.LOG.debug("Could not get JDBC connection for checkpoint: " + e);
            throw IOExceptionSupport.create(e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Throwable t) {}
            }
        }
    }
    
    @Override
    public long size() {
        return 0L;
    }
    
    @Deprecated
    public void setLockAcquireSleepInterval(final long lockAcquireSleepInterval) throws IOException {
        this.getLocker().setLockAcquireSleepInterval(lockAcquireSleepInterval);
    }
    
    public void setTransactionIsolation(final int transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
    
    public int getMaxProducersToAudit() {
        return this.maxProducersToAudit;
    }
    
    public void setMaxProducersToAudit(final int maxProducersToAudit) {
        this.maxProducersToAudit = maxProducersToAudit;
    }
    
    public int getMaxAuditDepth() {
        return this.maxAuditDepth;
    }
    
    public void setMaxAuditDepth(final int maxAuditDepth) {
        this.maxAuditDepth = maxAuditDepth;
    }
    
    public boolean isEnableAudit() {
        return this.enableAudit;
    }
    
    public void setEnableAudit(final boolean enableAudit) {
        this.enableAudit = enableAudit;
    }
    
    public int getAuditRecoveryDepth() {
        return this.auditRecoveryDepth;
    }
    
    public void setAuditRecoveryDepth(final int auditRecoveryDepth) {
        this.auditRecoveryDepth = auditRecoveryDepth;
    }
    
    public long getNextSequenceId() {
        synchronized (this.sequenceGenerator) {
            return this.sequenceGenerator.getNextSequenceId();
        }
    }
    
    public int getMaxRows() {
        return this.maxRows;
    }
    
    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }
    
    public void recover(final JdbcMemoryTransactionStore jdbcMemoryTransactionStore) throws IOException {
        final TransactionContext c = this.getTransactionContext();
        try {
            this.getAdapter().doRecoverPreparedOps(c, jdbcMemoryTransactionStore);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to recover from: " + jdbcMemoryTransactionStore + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public void commitAdd(final ConnectionContext context, final MessageId messageId) throws IOException {
        final TransactionContext c = this.getTransactionContext(context);
        try {
            final long sequence = (long)messageId.getEntryLocator();
            this.getAdapter().doCommitAddOp(c, sequence);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to commit add: " + messageId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public void commitRemove(final ConnectionContext context, final MessageAck ack) throws IOException {
        final TransactionContext c = this.getTransactionContext(context);
        try {
            this.getAdapter().doRemoveMessage(c, (long)ack.getLastMessageId().getEntryLocator(), null);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to commit last ack: " + ack + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public void commitLastAck(final ConnectionContext context, final long xidLastAck, final long priority, final ActiveMQDestination destination, final String subName, final String clientId) throws IOException {
        final TransactionContext c = this.getTransactionContext(context);
        try {
            this.getAdapter().doSetLastAck(c, destination, null, clientId, subName, xidLastAck, priority);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to commit last ack with priority: " + priority + " on " + destination + " for " + subName + ":" + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public void rollbackLastAck(final ConnectionContext context, final JDBCTopicMessageStore store, final MessageAck ack, final String subName, final String clientId) throws IOException {
        final TransactionContext c = this.getTransactionContext(context);
        try {
            final byte priority = (byte)store.getCachedStoreSequenceId(c, store.getDestination(), ack.getLastMessageId())[1];
            this.getAdapter().doClearLastAck(c, store.getDestination(), priority, clientId, subName);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to rollback last ack: " + ack + " on " + store.getDestination() + " for " + subName + ":" + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    public void rollbackLastAck(final ConnectionContext context, final byte priority, final ActiveMQDestination destination, final String subName, final String clientId) throws IOException {
        final TransactionContext c = this.getTransactionContext(context);
        try {
            this.getAdapter().doClearLastAck(c, destination, priority, clientId, subName);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to rollback last ack with priority: " + priority + " on " + destination + " for " + subName + ":" + clientId + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
    }
    
    long[] getStoreSequenceIdForMessageId(final MessageId messageId, final ActiveMQDestination destination) throws IOException {
        long[] result = { -1L, 126L };
        final TransactionContext c = this.getTransactionContext();
        try {
            result = this.adapter.getStoreSequenceId(c, destination, messageId);
        }
        catch (SQLException e) {
            log("JDBC Failure: ", e);
            throw IOExceptionSupport.create("Failed to get store sequenceId for messageId: " + messageId + ", on: " + destination + ". Reason: " + e, e);
        }
        finally {
            c.close();
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JDBCPersistenceAdapter.class);
        JDBCPersistenceAdapter.adapterFactoryFinder = new FactoryFinder("META-INF/services/org/apache/activemq/store/jdbc/");
        JDBCPersistenceAdapter.lockFactoryFinder = new FactoryFinder("META-INF/services/org/apache/activemq/store/jdbc/lock/");
    }
}
