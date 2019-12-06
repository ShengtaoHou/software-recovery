// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.activemq.network.jms;

import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.jms.QueueConnection;
import org.apache.activemq.broker.BrokerService;
import java.util.concurrent.ExecutorService;
import org.apache.activemq.util.ThreadPoolUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import javax.jms.Destination;
import org.apache.activemq.util.LRUCache;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.apache.activemq.Service;

public abstract class JmsConnector implements Service
{
    private static int nextId;
    private static final Logger LOG;
    protected boolean preferJndiDestinationLookup;
    protected JndiLookupFactory jndiLocalTemplate;
    protected JndiLookupFactory jndiOutboundTemplate;
    protected JmsMesageConvertor inboundMessageConvertor;
    protected JmsMesageConvertor outboundMessageConvertor;
    protected AtomicBoolean initialized;
    protected AtomicBoolean localSideInitialized;
    protected AtomicBoolean foreignSideInitialized;
    protected AtomicBoolean started;
    protected AtomicBoolean failed;
    protected AtomicReference<Connection> foreignConnection;
    protected AtomicReference<Connection> localConnection;
    protected ActiveMQConnectionFactory embeddedConnectionFactory;
    protected int replyToDestinationCacheSize;
    protected String outboundUsername;
    protected String outboundPassword;
    protected String localUsername;
    protected String localPassword;
    protected String outboundClientId;
    protected String localClientId;
    protected LRUCache<Destination, DestinationBridge> replyToBridges;
    private ReconnectionPolicy policy;
    protected ThreadPoolExecutor connectionSerivce;
    private List<DestinationBridge> inboundBridges;
    private List<DestinationBridge> outboundBridges;
    private String name;
    private ThreadFactory factory;
    
    public JmsConnector() {
        this.preferJndiDestinationLookup = false;
        this.initialized = new AtomicBoolean(false);
        this.localSideInitialized = new AtomicBoolean(false);
        this.foreignSideInitialized = new AtomicBoolean(false);
        this.started = new AtomicBoolean(false);
        this.failed = new AtomicBoolean();
        this.foreignConnection = new AtomicReference<Connection>();
        this.localConnection = new AtomicReference<Connection>();
        this.replyToDestinationCacheSize = 10000;
        this.replyToBridges = createLRUCache();
        this.policy = new ReconnectionPolicy();
        this.inboundBridges = new CopyOnWriteArrayList<DestinationBridge>();
        this.outboundBridges = new CopyOnWriteArrayList<DestinationBridge>();
        this.factory = new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = new Thread(runnable, "JmsConnector Async Connection Task: ");
                thread.setDaemon(true);
                return thread;
            }
        };
    }
    
    private static LRUCache<Destination, DestinationBridge> createLRUCache() {
        return new LRUCache<Destination, DestinationBridge>() {
            private static final long serialVersionUID = -7446792754185879286L;
            
            @Override
            protected boolean removeEldestEntry(final Map.Entry<Destination, DestinationBridge> enty) {
                if (this.size() > this.maxCacheSize) {
                    final Iterator<Map.Entry<Destination, DestinationBridge>> iter = this.entrySet().iterator();
                    final Map.Entry<Destination, DestinationBridge> lru = iter.next();
                    this.remove(lru.getKey());
                    final DestinationBridge bridge = lru.getValue();
                    try {
                        bridge.stop();
                        JmsConnector.LOG.info("Expired bridge: {}", bridge);
                    }
                    catch (Exception e) {
                        JmsConnector.LOG.warn("Stopping expired bridge {} caused an exception", bridge, e);
                    }
                }
                return false;
            }
        };
    }
    
    public boolean init() {
        boolean result = this.initialized.compareAndSet(false, true);
        if (result) {
            if (this.jndiLocalTemplate == null) {
                this.jndiLocalTemplate = new JndiLookupFactory();
            }
            if (this.jndiOutboundTemplate == null) {
                this.jndiOutboundTemplate = new JndiLookupFactory();
            }
            if (this.inboundMessageConvertor == null) {
                this.inboundMessageConvertor = new SimpleJmsMessageConvertor();
            }
            if (this.outboundMessageConvertor == null) {
                this.outboundMessageConvertor = new SimpleJmsMessageConvertor();
            }
            this.replyToBridges.setMaxCacheSize(this.getReplyToDestinationCacheSize());
            this.connectionSerivce = this.createExecutor();
            result = this.doConnectorInit();
        }
        return result;
    }
    
    protected boolean doConnectorInit() {
        try {
            this.initializeLocalConnection();
            this.localSideInitialized.set(true);
        }
        catch (Exception e) {
            this.scheduleAsyncLocalConnectionReconnect();
        }
        try {
            this.initializeForeignConnection();
            this.foreignSideInitialized.set(true);
        }
        catch (Exception e) {
            this.scheduleAsyncForeignConnectionReconnect();
        }
        return true;
    }
    
    @Override
    public void start() throws Exception {
        if (this.started.compareAndSet(false, true)) {
            this.init();
            for (final DestinationBridge bridge : this.inboundBridges) {
                bridge.start();
            }
            for (final DestinationBridge bridge : this.outboundBridges) {
                bridge.start();
            }
            JmsConnector.LOG.info("JMS Connector {} started", this.getName());
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (this.started.compareAndSet(true, false)) {
            ThreadPoolUtils.shutdown(this.connectionSerivce);
            this.connectionSerivce = null;
            for (final DestinationBridge bridge : this.inboundBridges) {
                bridge.stop();
            }
            for (final DestinationBridge bridge : this.outboundBridges) {
                bridge.stop();
            }
            JmsConnector.LOG.info("JMS Connector {} stopped", this.getName());
        }
    }
    
    public void clearBridges() {
        this.inboundBridges.clear();
        this.outboundBridges.clear();
        this.replyToBridges.clear();
    }
    
    protected abstract Destination createReplyToBridge(final Destination p0, final Connection p1, final Connection p2);
    
    public void setBrokerService(final BrokerService service) {
        this.embeddedConnectionFactory = new ActiveMQConnectionFactory(service.getVmConnectorURI());
    }
    
    public Connection getLocalConnection() {
        return this.localConnection.get();
    }
    
    public Connection getForeignConnection() {
        return this.foreignConnection.get();
    }
    
    public JndiLookupFactory getJndiLocalTemplate() {
        return this.jndiLocalTemplate;
    }
    
    public void setJndiLocalTemplate(final JndiLookupFactory jndiTemplate) {
        this.jndiLocalTemplate = jndiTemplate;
    }
    
    public JndiLookupFactory getJndiOutboundTemplate() {
        return this.jndiOutboundTemplate;
    }
    
    public void setJndiOutboundTemplate(final JndiLookupFactory jndiOutboundTemplate) {
        this.jndiOutboundTemplate = jndiOutboundTemplate;
    }
    
    public JmsMesageConvertor getInboundMessageConvertor() {
        return this.inboundMessageConvertor;
    }
    
    public void setInboundMessageConvertor(final JmsMesageConvertor jmsMessageConvertor) {
        this.inboundMessageConvertor = jmsMessageConvertor;
    }
    
    public JmsMesageConvertor getOutboundMessageConvertor() {
        return this.outboundMessageConvertor;
    }
    
    public void setOutboundMessageConvertor(final JmsMesageConvertor outboundMessageConvertor) {
        this.outboundMessageConvertor = outboundMessageConvertor;
    }
    
    public int getReplyToDestinationCacheSize() {
        return this.replyToDestinationCacheSize;
    }
    
    public void setReplyToDestinationCacheSize(final int replyToDestinationCacheSize) {
        this.replyToDestinationCacheSize = replyToDestinationCacheSize;
    }
    
    public String getLocalPassword() {
        return this.localPassword;
    }
    
    public void setLocalPassword(final String localPassword) {
        this.localPassword = localPassword;
    }
    
    public String getLocalUsername() {
        return this.localUsername;
    }
    
    public void setLocalUsername(final String localUsername) {
        this.localUsername = localUsername;
    }
    
    public String getOutboundPassword() {
        return this.outboundPassword;
    }
    
    public void setOutboundPassword(final String outboundPassword) {
        this.outboundPassword = outboundPassword;
    }
    
    public String getOutboundUsername() {
        return this.outboundUsername;
    }
    
    public void setOutboundUsername(final String outboundUsername) {
        this.outboundUsername = outboundUsername;
    }
    
    public String getOutboundClientId() {
        return this.outboundClientId;
    }
    
    public void setOutboundClientId(final String outboundClientId) {
        this.outboundClientId = outboundClientId;
    }
    
    public String getLocalClientId() {
        return this.localClientId;
    }
    
    public void setLocalClientId(final String localClientId) {
        this.localClientId = localClientId;
    }
    
    public ReconnectionPolicy getReconnectionPolicy() {
        return this.policy;
    }
    
    public void setReconnectionPolicy(final ReconnectionPolicy policy) {
        this.policy = policy;
    }
    
    public boolean isPreferJndiDestinationLookup() {
        return this.preferJndiDestinationLookup;
    }
    
    public void setPreferJndiDestinationLookup(final boolean preferJndiDestinationLookup) {
        this.preferJndiDestinationLookup = preferJndiDestinationLookup;
    }
    
    public boolean isConnected() {
        return this.localConnection.get() != null && this.foreignConnection.get() != null;
    }
    
    protected void addInboundBridge(final DestinationBridge bridge) {
        if (!this.inboundBridges.contains(bridge)) {
            this.inboundBridges.add(bridge);
        }
    }
    
    protected void addOutboundBridge(final DestinationBridge bridge) {
        if (!this.outboundBridges.contains(bridge)) {
            this.outboundBridges.add(bridge);
        }
    }
    
    protected void removeInboundBridge(final DestinationBridge bridge) {
        this.inboundBridges.remove(bridge);
    }
    
    protected void removeOutboundBridge(final DestinationBridge bridge) {
        this.outboundBridges.remove(bridge);
    }
    
    public String getName() {
        if (this.name == null) {
            this.name = "Connector:" + getNextId();
        }
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    private static synchronized int getNextId() {
        return JmsConnector.nextId++;
    }
    
    public boolean isFailed() {
        return this.failed.get();
    }
    
    protected abstract void initializeLocalConnection() throws Exception;
    
    protected abstract void initializeForeignConnection() throws Exception;
    
    void handleConnectionFailure(final Connection connection) {
        if (connection == null || !this.started.get()) {
            return;
        }
        JmsConnector.LOG.info("JmsConnector handling loss of connection [{}]", connection.toString());
        this.replyToBridges.clear();
        if (this.foreignConnection.compareAndSet(connection, null)) {
            for (final DestinationBridge bridge : this.inboundBridges) {
                try {
                    bridge.stop();
                }
                catch (Exception ex) {}
            }
            this.connectionSerivce.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JmsConnector.this.doInitializeConnection(false);
                    }
                    catch (Exception e) {
                        JmsConnector.LOG.error("Failed to initialize foreign connection for the JMSConnector", e);
                    }
                }
            });
        }
        else if (this.localConnection.compareAndSet(connection, null)) {
            for (final DestinationBridge bridge : this.outboundBridges) {
                try {
                    bridge.stop();
                }
                catch (Exception ex2) {}
            }
            this.connectionSerivce.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JmsConnector.this.doInitializeConnection(true);
                    }
                    catch (Exception e) {
                        JmsConnector.LOG.error("Failed to initialize local connection for the JMSConnector", e);
                    }
                }
            });
        }
    }
    
    private void scheduleAsyncLocalConnectionReconnect() {
        this.connectionSerivce.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JmsConnector.this.doInitializeConnection(true);
                }
                catch (Exception e) {
                    JmsConnector.LOG.error("Failed to initialize local connection for the JMSConnector", e);
                }
            }
        });
    }
    
    private void scheduleAsyncForeignConnectionReconnect() {
        this.connectionSerivce.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JmsConnector.this.doInitializeConnection(false);
                }
                catch (Exception e) {
                    JmsConnector.LOG.error("Failed to initialize foreign connection for the JMSConnector", e);
                }
            }
        });
    }
    
    private void doInitializeConnection(final boolean local) throws Exception {
        int attempt = 0;
        int maxRetries;
        if (local) {
            maxRetries = (this.localSideInitialized.get() ? this.policy.getMaxReconnectAttempts() : this.policy.getMaxInitialConnectAttempts());
        }
        else {
            maxRetries = (this.foreignSideInitialized.get() ? this.policy.getMaxReconnectAttempts() : this.policy.getMaxInitialConnectAttempts());
        }
        while (true) {
            if (attempt > 0) {
                try {
                    Thread.sleep(this.policy.getNextDelay(attempt));
                }
                catch (InterruptedException ex) {}
            }
            if (this.connectionSerivce.isTerminating()) {
                break;
            }
            try {
                if (local) {
                    this.initializeLocalConnection();
                    this.localSideInitialized.set(true);
                }
                else {
                    this.initializeForeignConnection();
                    this.foreignSideInitialized.set(true);
                }
                if (this.localConnection.get() != null && this.foreignConnection.get() != null) {
                    for (final DestinationBridge bridge : this.inboundBridges) {
                        bridge.start();
                    }
                    for (final DestinationBridge bridge : this.outboundBridges) {
                        bridge.start();
                    }
                }
            }
            catch (Exception e) {
                JmsConnector.LOG.debug("Failed to establish initial {} connection for JmsConnector [{}]", (Object)new Object[] { local ? "local" : "foreign", attempt }, e);
                if (maxRetries >= ++attempt || this.connectionSerivce.isTerminating()) {
                    this.failed.set(true);
                    return;
                }
                continue;
            }
        }
    }
    
    private ThreadPoolExecutor createExecutor() {
        final ThreadPoolExecutor exec = new ThreadPoolExecutor(0, 2, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), this.factory);
        exec.allowCoreThreadTimeOut(true);
        return exec;
    }
    
    static {
        LOG = LoggerFactory.getLogger(JmsConnector.class);
    }
}
